package com.yihu.ehr.redis.cache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;

/**
 * @author 张进军
 * @date 2017/12/5 15:55
 */
@RunWith(JUnit4.class)
public class RedisMemoryCsvTest {

    /**
     * 通过 redis-rdb-tools 导出 redis 内存分析报告
     */
    @Test
    public void exportRedisMemoryCsv() {
//        BufferedReader in = null;
//        FileWriter fw = null;
//        BufferedWriter bw = null;
        try {
            // 这两个文件路径在同一主机上。
            String rdbFilePath = "C:\\Zjj_Programs\\Redis-x64-3.2.100\\dump.rdb";
            String outFilePath = "E:\\临时文件\\memory.csv";

            // 该写法没效果，原因不详。
//            String[] cmdArgs = {"rdb", "-c memory", rdbFilePath};
//            Process pr = Runtime.getRuntime().exec(cmdArgs);
            // 获取内存分析文件流，手动导出数据到CSV文件
//            String command = "rdb -c memory " + rdbFilePath;
            // 命令中指定CSV文件进行导出
            String command = "rdb -c memory " + rdbFilePath + " -f " + outFilePath;
            Process pr = Runtime.getRuntime().exec(command);

            /*// 手动导出CSV文件
            File outFile = new File(outFilePath);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }

            in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            StringBuffer content = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null) {
                content.append(line).append(System.lineSeparator());
            }
            fw = new FileWriter(outFile);
            bw = new BufferedWriter(fw);
            bw.write(content.toString());
            bw.flush();*/

            // 该线程执行完之后，才会往下执行。
            // 注意，执行比较大的命令（比如写大文件），该线程极可能会被阻塞，
            // 这时需要特殊处理，比如不断读取输出流，防止缓冲区被填满。
            pr.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } /* finally {
            try {
                if (bw != null) {
                    in.close();
                }
                if (fw != null) {
                    in.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

}
