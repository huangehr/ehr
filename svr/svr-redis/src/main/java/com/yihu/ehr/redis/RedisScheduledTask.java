package com.yihu.ehr.redis;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.redis.cache.RedisCacheKeyMemorySetMapper;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyMemoryService;
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
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @Value("${ehr-redis.cache.memory.rdbToolPath}")
    private String rdbToolPath;
    @Value("${ehr-redis.cache.memory.rdbFilePath}")
    private String rdbFilePath;
    @Value("${ehr-redis.cache.memory.outFilePath}")
    private String outFilePath;

    @Autowired
    RedisCacheKeyMemoryService redisCacheKeyMemoryService;

    /**
     * 导出 redis 内存分析报告，并导入到数据库
     */
    @RequestMapping(value = "/redis/cache/statistics/exportAndImportRedisMemoryData", method = RequestMethod.GET)
//    @Scheduled(cron = "* */2 * * * *")
    public void exportAndImportRedisMemoryData() {
        try {
            // 导出内存分析报告CSV文件
            String command = "python " + rdbToolPath + " -c memory " + rdbFilePath + " -f " + outFilePath;
            Process pr = Runtime.getRuntime().exec(command);
            pr.waitFor(); // 这个线程结束后才会往下执行。

            // 导入CSV格式的内存分析数据
            FlatFileItemReader<RedisCacheKeyMemory> itemReader = new FlatFileItemReader<RedisCacheKeyMemory>();
            itemReader.setResource(new FileSystemResource(outFilePath));
            itemReader.setLinesToSkip(1);
            DefaultLineMapper<RedisCacheKeyMemory> lineMapper = new DefaultLineMapper<RedisCacheKeyMemory>();
            lineMapper.setLineTokenizer(new DelimitedLineTokenizer());
            lineMapper.setFieldSetMapper(new RedisCacheKeyMemorySetMapper());
            itemReader.setLineMapper(lineMapper);
            itemReader.open(new ExecutionContext());

            redisCacheKeyMemoryService.deleteAll();
            RedisCacheKeyMemory redisCacheKeyMemory = null;
            while ((redisCacheKeyMemory = itemReader.read()) != null) {
                redisCacheKeyMemoryService.save(redisCacheKeyMemory);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
