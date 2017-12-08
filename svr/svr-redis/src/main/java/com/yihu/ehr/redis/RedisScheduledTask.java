package com.yihu.ehr.redis;

import com.yihu.ehr.redis.cache.RedisCacheKeyMemorySetMapper;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyMemoryService;
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

import javax.annotation.Resource;

/**
 * 定时任务
 *
 * @author 张进军
 * @date 2017/12/6 09:22
 */
@Component
//@RestController
//@RequestMapping(value = ApiVersion.Version1_0)
//@Api(description = "定时任务", tags = {"缓存服务管理--定时任务"})
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
//    @RequestMapping(value = "/redis/cache/statistics/backupRedis", method = RequestMethod.GET)
    @Scheduled(cron = "0 30 0 15 * ?")
    public void backupRedis() {
        long start = System.currentTimeMillis();
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.bgSave();
                return null;
            }
        });
        long end = System.currentTimeMillis();
        logger.info("成功生成Redis快照。" + (end - start));
    }

    /**
     * 导出 redis 内存分析报告，并导入到数据库
     */
//    @RequestMapping(value = "/redis/cache/statistics/exportAndImportRedisMemoryData", method = RequestMethod.GET)
    @Scheduled(cron = "0 0 0 15 * ?")
    public void exportAndImportRedisMemoryData() {
        try {
            long start = System.currentTimeMillis();

            // 导出内存分析报告CSV文件（得到的内存值是近似值，比实际略小）
            String command = "rdb -c memory " + rdbFilePath + " -f " + outFilePath;
            Process pr = Runtime.getRuntime().exec(command);
            pr.waitFor(); // 这个线程结束后才会往下执行。

            long export = System.currentTimeMillis();
            logger.info("成功导出Redis内存分析报告，耗时：" + (export - start) + "毫秒");

            // 导入CSV格式的内存分析数据
            FlatFileItemReader<RedisCacheKeyMemory> itemReader = new FlatFileItemReader<>();
            itemReader.setResource(new FileSystemResource(outFilePath));
            itemReader.setLinesToSkip(1);
            DefaultLineMapper<RedisCacheKeyMemory> lineMapper = new DefaultLineMapper<>();
            lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
            lineMapper.setFieldSetMapper(new RedisCacheKeyMemorySetMapper());
            itemReader.setLineMapper(lineMapper);
            itemReader.open(new ExecutionContext());

            redisCacheKeyMemoryService.deleteAll();
            RedisCacheKeyMemory redisCacheKeyMemory = null;
            while ((redisCacheKeyMemory = itemReader.read()) != null) {
                redisCacheKeyMemoryService.save(redisCacheKeyMemory);
            }

            long imports = System.currentTimeMillis();
            logger.info("成功导入Redis内存分析数据到数据库，耗时：" + (imports - export) + "毫秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
