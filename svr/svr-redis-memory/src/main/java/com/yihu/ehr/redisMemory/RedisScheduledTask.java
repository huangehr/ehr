package com.yihu.ehr.redisMemory;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.redisMemory.cache.RedisCacheKeyMemorySetMapper;
import com.yihu.ehr.redisMemory.cache.entity.RedisCacheKeyMemory;
import com.yihu.ehr.redisMemory.cache.service.RedisCacheKeyMemoryService;
import com.yihu.ehr.util.id.UuidUtil;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 定时任务
 *
 * @author 张进军
 * @date 2017/12/6 09:22
 */
@Component
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "定时任务", tags = {"缓存服务管理--定时任务"})
public class RedisScheduledTask {

    Logger logger = LoggerFactory.getLogger(RedisScheduledTask.class);

    @Value("${ehr-redis.cache.memory.rdbFilePath}")
    private String rdbFilePath;
    @Value("${ehr-redis.cache.memory.outFilePath}")
    private String outFilePath;

    @Autowired
    private RedisCacheKeyMemoryService redisCacheKeyMemoryService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成 Redis 快照
     */
    @RequestMapping(value = "/redis-memory/cache/statistics/backupRedis", method = RequestMethod.GET)
    @Scheduled(cron = "0 0 0 15 * ?")
    public void backupRedis() {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bgSave();
                return null;
            }
        });
        logger.info("已执行Redis快照命令。");
    }

    /**
     * 导出 redis 内存分析报告，并导入到数据库
     */
    @RequestMapping(value = "/redis-memory/cache/statistics/exportAndImportRedisMemoryData", method = RequestMethod.GET)
    @Scheduled(cron = "0 30 0 15 * ?")
    public void exportAndImportRedisMemoryData() {
        long start = System.currentTimeMillis();
        try {
            // 导出内存分析报告CSV文件（得到的内存值是近似值，比实际略小）
            String command = "rdb -c memory " + rdbFilePath + " -f " + outFilePath;
            Process pr = Runtime.getRuntime().exec(command);
            pr.waitFor();

            long export = System.currentTimeMillis();
            logger.info("成功导出Redis内存分析报告，耗时：" + (export - start) + " 毫秒");

            // 导入CSV格式的内存分析数据
            FlatFileItemReader<RedisCacheKeyMemory> itemReader = new FlatFileItemReader<>();
            itemReader.setResource(new FileSystemResource(outFilePath));
            itemReader.setLinesToSkip(1);
            DefaultLineMapper<RedisCacheKeyMemory> lineMapper = new DefaultLineMapper<>();
            lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
            lineMapper.setFieldSetMapper(new RedisCacheKeyMemorySetMapper());
            itemReader.setLineMapper(lineMapper);
            itemReader.open(new ExecutionContext());

            long deleteBe = System.currentTimeMillis();
            redisCacheKeyMemoryService.deleteAllInBatch();
            logger.info("清空旧的内存分析表数据，耗时：" + (System.currentTimeMillis() - deleteBe) + " 毫秒");

            List<RedisCacheKeyMemory> redisCacheKeyMemoryList = new ArrayList<>();
            RedisCacheKeyMemory redisCacheKeyMemory = null;
            int i = 0;
            while ((redisCacheKeyMemory = itemReader.read()) != null) {
                redisCacheKeyMemory.setId(UuidUtil.randomUUID());
                redisCacheKeyMemoryList.add(redisCacheKeyMemory);
                if (redisCacheKeyMemoryList.size() == 1000) {
                    redisCacheKeyMemoryService.save(redisCacheKeyMemoryList);
                    redisCacheKeyMemoryList.clear();
                    i += 1000;
                    logger.info("已导入 " + i + " 条数据。");
                }
            }
            redisCacheKeyMemoryService.save(redisCacheKeyMemoryList);
            logger.info("总共导入 " + (i + redisCacheKeyMemoryList.size()) + " 条数据。");

            logger.info("成功导入Redis内存分析数据到数据库，耗时：" + (System.currentTimeMillis() - export) + " 毫秒");
            logger.info("导出Redis内存分析报告，并导入到数据库，总耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常：" + e.getMessage());
        }
    }

}
