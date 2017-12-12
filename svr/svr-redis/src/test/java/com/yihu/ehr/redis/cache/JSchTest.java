package com.yihu.ehr.redis.cache;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author 张进军
 * @date 2017/12/11 10:32
 */
public class JSchTest {

    Logger logger = LoggerFactory.getLogger(JSchTest.class);

    private String host = "172.19.103.47";
    private String username = "root";
    private String password = "jkzl";
    private String default_ssh_port = "22";

    @Test
    public void commandTest() {
        long start = System.currentTimeMillis();

        FileOutputStream fileOut = null;
        BufferedOutputStream bufOut = null;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, host);
            session.setPassword(password);
            session.setUserInfo(new MyUserInfo());
            session.connect();

            long conEnd = System.currentTimeMillis();
            logger.info("JSch 连接耗时：" + (conEnd - start) + " 毫秒");

//            String command = "ls /usr/local/bin/";
            String rdbFilePath = "/usr/local/bin/dump.rdb";
            String command = "rdb -c memory " + rdbFilePath;
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();

//            File outFile = new File("/usr/local/ehr/redisReport/memory.csv");
            File outFile = new File("E:\\临时文件\\memory.csv");
            if (!outFile.exists()) outFile.createNewFile();

            fileOut = new FileOutputStream(outFile);
            bufOut = new BufferedOutputStream(fileOut);
            byte[] tmp = new byte[1024];
            int count = 0;
            while ((count = in.read(tmp, 0, 1024)) != -1) {
                bufOut.write(tmp, 0, count);
            }
            bufOut.flush();

            long streamEnd = System.currentTimeMillis();
            logger.info("文件流输出耗时：" + (streamEnd - conEnd) + " 毫秒");

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
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

        logger.info("总耗时：" + (System.currentTimeMillis() - start) + " 毫秒");
    }

    private class MyUserInfo implements UserInfo {
        @Override
        public String getPassphrase() {
            System.out.println("getPassphrase");
            return null;
        }

        @Override
        public String getPassword() {
            System.out.println("getPassword");
            return null;
        }

        @Override
        public boolean promptPassword(String s) {
            System.out.println("promptPassword:" + s);
            return false;
        }

        @Override
        public boolean promptPassphrase(String s) {
            System.out.println("promptPassphrase:" + s);
            return false;
        }

        @Override
        public boolean promptYesNo(String s) {
            System.out.println("promptYesNo:" + s);
            return true; // notice here!
        }

        @Override
        public void showMessage(String s) {
            System.out.println("showMessage:" + s);
        }
    }

}
