package com.yihu.ehr.util.ftp;

/**
 * Created by Administrator on 2018/5/5.
 */
import com.yihu.ehr.util.file.FileUtil;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * ftp 操作工具类
 * Created by HZY on 2015/8/12.
 */
public class FtpUtils {
    static final private Logger logger = LoggerFactory.getLogger(FtpUtils.class);

    private int port = 21;      // 端口号
    private String username;    //ftp  登录名
    private String password;    //ftp  登录密码
    private String ftpHostName; //ftp  主机名（IP）
    private FTPClient ftpClient = new FTPClient();
    private FileOutputStream fos = null;

    public FTPClient getFtpClient() {
        return ftpClient;
    }

    public void setFtpClient(FTPClient ftpClient) {
        this.ftpClient = ftpClient;
    }

    public FtpUtils(String username, String password, String ftpHostName, int port) {
        this.username = username;
        this.password = password;
        this.ftpHostName = ftpHostName;
        this.port = port;
    }

    /**
     * 建立ftp连接
     */
    public void connect() {
        try {
            // 连接
            ftpClient.connect(ftpHostName, port);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
            }
            // 登录
            ftpClient.login(username, password);
            ftpClient.setBufferSize(1024 * 1024 * 1);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setControlEncoding("GBK");
            ftpClient.setDefaultTimeout(60 * 1000);
            ftpClient.setConnectTimeout(60 * 1000);
            ftpClient.setDataTimeout(60 * 1000);
            ftpClient.setRemoteVerificationEnabled(false);
        } catch (SocketException e) {
            logger.error("连接Ftp服务器异常：", e);
        } catch (IOException e) {
            logger.error("连接Ftp服务器异常：", e);
        }
    }

    /**
     * 关闭输入输出流
     */
    public void closeConnect() {
        try {
            if (fos != null) {
                fos.close();
            }
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            logger.error("关闭Ftp连接失败：", e);
        }
    }

    /**
     * 下载文件
     *
     * @param ftpFileName ftp文件路径
     * @param localDir    本地保存路径
     */
    public boolean down(String ftpFileName, String localDir) {
        connect();
        boolean flag = downFile(ftpFileName, localDir);
        closeConnect();
        return flag;
    }

    /**
     * 上传文件
     *
     * @param localDir    本地文件
     * @param ftpFileName ftp上传路径
     * @return 是否上传成功
     * @throws Exception
     */
    public boolean upload(String localDir, String ftpFileName) throws Exception {
        connect();
        boolean flag = uploadFile(localDir, ftpFileName);
        closeConnect();
        return flag;
    }


    /**
     * 下载单个ftp上文件
     *
     * @param ftpFileName ftp文件路径
     * @param localDir    本地保存路径
     * @return 是否下载成功
     */
    public boolean downFile(String ftpFileName, String localDir) {
        boolean success = false;
        try {
            File file = new File(ftpFileName);
            File temp = new File(localDir);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            File localfile = new File(localDir + File.separator + file.getName());
            if (!localfile.exists()) {
                fos = new FileOutputStream(localfile);
                success = ftpClient.retrieveFile(ftpFileName, fos);
            }
            ftpClient.changeToParentDirectory();
        } catch (SocketException e) {
            logger.error("Ftp服务器连接失败！", e);
        } catch (IOException e) {
            logger.error("Ftp文件下载失败！", e);
        }
        return success;
    }


    /**
     * 写入文件至ftp
     *
     * @param local  写入到FTP服务器上的文件
     * @param remote FTP服务器保存目录
     * @return 是否上传成功
     * @throws Exception
     */
    public boolean uploadFile(String local, String remote) {
        boolean success = false;
        File localFile = new File(local);
        InputStream in = null;
        try {
            if (!localFile.exists()) {
                return success;
            } else {
                in = new BufferedInputStream(new FileInputStream(localFile));
            }

            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return success;
            }
            createDir(remote);
            ftpClient.changeWorkingDirectory(remote);
            success = ftpClient.storeFile(localFile.getName(), in);
            in.close();
        } catch (IOException e) {
            logger.error("Ftp文件上传失败！", e);
        }
        return success;
    }


    /**
     * 判断是否是目录
     *
     * @param fileName ftp文件路径
     * @return true 是目录 false 不是
     */
    public boolean isDir(String fileName) {
        try {
            //切换目录，若当前是目录则返回true,否则返回false。
            return ftpClient.changeWorkingDirectory(fileName);
        } catch (Exception e) {
            logger.error("Ftp判断是否是目录失败！", e);
        }
        return false;
    }


    /**
     * 递归创建远程服务器目录
     *
     * @param remote 远程服务器文件绝对路径
     * @return 目录创建是否成功
     * @throws IOException
     */
    public boolean createDir(String remote) throws IOException {
        boolean success = true;
        String directory = remote.substring(0, remote.lastIndexOf("/") + 1);
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/")
                && !ftpClient.changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            end = directory.indexOf("/", start);
            while (true) {
                String subDirectory = new String(remote.substring(start, end));
                if (!ftpClient.changeWorkingDirectory(subDirectory)) {
                    if (ftpClient.makeDirectory(subDirectory)) {
                        ftpClient.changeWorkingDirectory(subDirectory);
                    } else {
                        logger.debug("创建Ftp目录失败");
                        return false;
                    }
                }
                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }


    /**
     * 删除文件-FTP方式
     *
     * @param path     FTP服务器上传地址
     * @param fileName FTP服务器上要删除的文件名
     * @return
     */
    public boolean deleteFile(String path, String fileName) {
        boolean success = false;
        try {
            ftpClient.changeWorkingDirectory(path);//转移到指定FTP服务器目录
            ftpClient.deleteFile(fileName);
            ftpClient.logout();
            success = true;
        } catch (Exception e) {
            logger.error("Ftp文件删除失败！", e);
        }
        return success;
    }

    /**
     * 上传文件夹或文件至ftp
     *
     * @param filename   要上传的文件
     * @param uploadpath ftp文件路径
     * @return
     * @throws Exception
     */
    public boolean uploadDir(String filename, String uploadpath) {
        boolean success = false;
        File file = new File(filename);
        try {


            // 要上传的是否存在
            if (!file.exists()) {
                return success;
            }
            // 要上传的是否是文件夹
            if (!file.isDirectory()) {
                return uploadFile(filename, uploadpath);
            }
            File[] flles = file.listFiles();
            if (flles != null) {
                for (File files : flles) {
                    if (files.exists()) {
                        if (files.isDirectory()) {
                            uploadDir(files.getAbsoluteFile().toString(),
                                    uploadpath);
                        } else {
                            String local = files.getCanonicalPath().replaceAll("\\\\", "/");
                            String remote = uploadpath + local.substring(local.indexOf("/") + 1, local.lastIndexOf("/") + 1);
                            ftpClient.changeWorkingDirectory("/");
                            uploadFile(local, remote);
                            ftpClient.changeWorkingDirectory("/");
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Ftp文件上传失败！", e);
        }
        return true;
    }

    /**
     * 读取病人信息并封装成List<String>
     *
     * @param path 远程ftp文件路径
     * @return List<String>病人信息集合
     */
    public List<String> readFileData(String path) {
        connect();
        List<String> list = new ArrayList<>();
        InputStream inputStream = null;

        try {
            ftpClient.changeWorkingDirectory(path);
            String[] filenames = ftpClient.listNames();
            if (filenames != null && filenames.length > 0) {
                for (String filenanme : filenames) {
                    inputStream = ftpClient.retrieveFileStream(path + "/" + filenanme);
                    if (inputStream == null) {
                        logger.info("读取Ftp文件路径错误：" + (path + "/" + filenanme));
                        return list;
                    }//流为null，文件读取失败
                    String json = FileUtil.streamToBase64String(inputStream);
                    list.add(json);
                    inputStream.close();
                    ftpClient.completePendingCommand();
                }
            }
        } catch (SocketException e) {
            logger.error("Ftp服务器异常！", e);
        } catch (IOException e) {
            logger.error("Ftp文件读取失败！", e);
        } finally {
            closeConnect();
        }
        return list;
    }

    /**
     * 获取病人列表信息
     *
     * @param basePath 根目录下病人数据文件夹 eg：/home/test/patient/
     * @param agencyNo 机构编码
     * @return 病人信息map集合
     */
    public List<Map<String, String>> getPatientList(String basePath, String agencyNo) {
        connect();
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map = null;
        String base = basePath + agencyNo;
        try {
            ftpClient.changeWorkingDirectory(base);//病人ID集合
            int reply = ftpClient.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                String[] patientnames = ftpClient.listNames();
                if (patientnames != null && patientnames.length > 0) {
                    for (String patientname : patientnames) {
                        String jsonPath = base + "/" + patientname;//json文件夹
                        ftpClient.changeWorkingDirectory(jsonPath);
                        String[] eventNames = ftpClient.listNames();//获取json文件遍历
                        for (String event : eventNames) {
                            map = new HashMap<>();
                            map.put("agency_code", agencyNo);
                            map.put("patient_id", patientname);
                            map.put("event_no", event);
                            list.add(map);
                        }
//                    ftpClient.changeWorkingDirectory("..");
                    }
                }
            } else {
                logger.debug("agency编码：" + agencyNo + " 对应的数据不存在，获取病人列表信息失败");
            }
        } catch (SocketException e) {
            logger.error("Ftp-服务器连接失败！", e);
        } catch (IOException e) {
            logger.error("Ftp-获取病人数据失败！", e);
        } finally {
            closeConnect();
        }
        return list;
    }

    /**
     * 清空文件夹中文件
     *
     * @param pathname ftp文件夹路径
     * @return
     */
    public boolean removeData(String pathname) {
        try {
            FTPFile[] files = ftpClient.listFiles(pathname);
            for (FTPFile f : files) {
                if (f.isDirectory()) {
                    removeData(pathname + "/" + f.getName());
                    ftpClient.removeDirectory(pathname);
                }
                if (f.isFile()) {
                    ftpClient.deleteFile(pathname + "/" + f.getName());
                }
            }
        } catch (IOException e) {
            logger.error("Ftp-清空文件夹失败！", e);
            return false;
        }
        return true;
    }

    /**
     * 下载ftp文件夹
     *
     * @param ftpFileName ftp文件或文件夹名
     * @param localDir    本地保存路径
     */
    private boolean downDir(String ftpFileName, String localDir) {
        boolean success = false;
        try {
            File file = new File(ftpFileName);
            File temp = new File(localDir);
            if (!temp.exists()) {
                temp.mkdirs();
            }
            // 判断是否是目录
            if (isDir(ftpFileName)) {
                String dirPath = localDir + "/" + ftpFileName;
                File dirFile = new File(dirPath);
                if (!dirFile.exists()) {
                    dirFile.mkdirs();
                }
                String[] names = ftpClient.listNames();
                for (int i = 0; i < names.length; i++) {
                    if (isDir(names[i])) {
                        downDir(ftpFileName + '/' + names[i], dirPath
                                + File.separator + names[i]);
                        ftpClient.changeToParentDirectory();
                    } else {
                        File localfile = new File(dirPath + File.separator
                                + names[i]);
                        if (!localfile.exists()) {
                            fos = new FileOutputStream(localfile);
                            ftpClient.retrieveFile(names[i], fos);
                        }
                    }
                }
            } else {

                File localfile = new File(localDir + File.separator + file.getName());
                if (!localfile.exists()) {
                    fos = new FileOutputStream(localfile);
                    success = ftpClient.retrieveFile(ftpFileName, fos);

                }
                ftpClient.changeToParentDirectory();
            }
        } catch (SocketException e) {
            logger.error("Ftp服务器连接异常！", e);
        } catch (IOException e) {
            logger.error("Ftp文件下载失败！", e);
        }
        return success;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        InputStream inputStream = ftpClient.retrieveFileStream(fileName);
        return inputStream;
    }

}
