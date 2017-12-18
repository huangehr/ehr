package com.yihu.ehr.redis;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.yihu.ehr.redis.cache.RedisCacheKeyMemorySetMapper;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyMemory;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyMemoryService;
import com.yihu.ehr.util.id.UuidUtil;
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
import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    // 生产环境服务器不允许代码直接通过SSH访问服务器，故暂时注释，另寻方法。
//    @Value("${ehr-redis.server.host}")
//    private String redisServerHost;
//    @Value("${ehr-redis.server.username}")
//    private String redisServerUsername;
//    @Value("${ehr-redis.server.password}")
//    private String redisServerPwd;
//    @Value("${ehr-redis.server.ssh-port}")
//    private int redisServerSshPort;
    private String redisServerHost = "";
    private String redisServerUsername = "";
    private String redisServerPwd = "";
    private int redisServerSshPort = 22;

    @Autowired
    private RedisCacheKeyMemoryService redisCacheKeyMemoryService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成 Redis 快照
     */
//    @RequestMapping(value = "/redis/cache/statistics/backupRedis", method = RequestMethod.GET)
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
//    @RequestMapping(value = "/redis/cache/statistics/exportAndImportRedisMemoryData", method = RequestMethod.GET)
    @Scheduled(cron = "0 30 0 15 * ?")
    public void exportAndImportRedisMemoryData() {
        long start = System.currentTimeMillis();
        try {
            // 导出内存分析报告CSV文件（得到的内存值是近似值，比实际略小）
            // 生产环境服务器不允许代码直接通过SSH访问服务器，故暂时注释，另寻方法。
//            exportRedisMemoryReport();

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

    /**
     * 导出 Redis 的内存分析报告为CSV文件
     */
    private void exportRedisMemoryReport() throws Exception {
        long start = System.currentTimeMillis();

        FileOutputStream fileOut = null;
        BufferedOutputStream bufOut = null;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(redisServerUsername, redisServerHost);
            session.setPassword(redisServerPwd);
            session.setPort(redisServerSshPort);
            session.setUserInfo(new DefaultJSchUserInfo());
            session.connect();

            long conEnd = System.currentTimeMillis();
            logger.info("JSch 连接耗时：" + (conEnd - start) + " 毫秒");

            String command = "rdb -c memory " + rdbFilePath;
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();

            File outFile = new File(outFilePath);
            if (!outFile.exists()) outFile.createNewFile();

            fileOut = new FileOutputStream(outFile);
            bufOut = new BufferedOutputStream(fileOut);
            byte[] tmp = new byte[1024];
            int count = 0;
            while ((count = in.read(tmp, 0, 1024)) != -1) {
                bufOut.write(tmp, 0, count);
            }
            bufOut.flush();

            logger.info("生成Redis内存分析报告文件，耗时：" + (System.currentTimeMillis() - conEnd) + " 毫秒");

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            logger.error("生成Redis内存分析报告发生异常：" + e.getMessage());
            throw new Exception();
        } finally {
            try {
                if (bufOut != null) {
                    bufOut.close();
                }
                if (fileOut != null) {
                    fileOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
