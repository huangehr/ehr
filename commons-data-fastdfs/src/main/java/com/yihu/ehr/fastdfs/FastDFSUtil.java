package com.yihu.ehr.fastdfs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.util.log.LogService;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

/**
 * FastDFS 客户端工具.
 *
 * 作为Bean方式来调用。
 *
 * @author szx
 * @author Sand
 */
public class FastDFSUtil {
    public final static String GroupField = "groupName";
    public final static String RemoteFileField = "remoteFileName";
    public final static String FileIdField = "fid";
    public final static String FileUrlField = "fileUrl";
    
    @Autowired
    FastDFSClientPool clientPool;
    /**
     * 以输入流的方式上传文件
     * InputStream in = new FileInputStream("C://Desert.jpg");
     * ObjectNode msg = FileUtil.upload(in,"jpg", "沙漠");
     * in.close();
     *
     * @param in            输入流
     * @param fileExtension 文件扩展名，不要带“.”
     * @param description   文件名称（中文）
     * @return 返回值的格式如下:
     * {
     * "groupName": "healthArchiveGroup",
     * "remoteFileName": "/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg",
     * "fid": "group1/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg",
     * "fileURL": "http://172.19.103.13/healthArchiveGroup/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg"
     * }
     * <p>
     * groupName 及 remoteFileName 可以用于查询在 fastDFS 中文件的信息，如果只是图片显示，可以忽略这两个值。
     * fid 保存了在 fastDFS 上的完整路径，为了避免将来服务器域名发生变更，最好使用本值.服务器的域名另外配置。
     * fileURL 保存了完整的 web 访问路径，为了避免将来服务器域名发生变更，最好不要直接使用本值。
     * 如果需要在下载时，可以显示原始文件名，请在访问file_url时，增加 attname 参数，如：
     * <p>
     * http://host/healthArchiveGroup/M00/00/00/rBFuH1XdIseAUTZZAA1rIuRd3Es062.jpg?attname=a.jpg
     * @throws Exception
     */
    public ObjectNode upload(InputStream in, String fileExtension,
                                    String description) throws Exception {
        StorageClient client = clientPool.getStorageClient();
        try {
            NameValuePair[] fileMetaData;
            fileMetaData = new NameValuePair[1];
            fileMetaData[0] = new NameValuePair("description", description == null ? "" : description);

//            ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
            ObjectNode message = new ObjectMapper().createObjectNode();

            byte fileBuffer[] = new byte[in.available()];
            int len = 0;
            int temp = 0;                             //所有读取的内容都使用temp接收
            while ((temp = in.read()) != -1) {            //当没有读取完时，继续读取
                fileBuffer[len] = (byte) temp;
                len++;
            }
            in.close();

            TrackerServer trackerServer = clientPool.getTrackerServer();

            String[] results = client.upload_file(fileBuffer, fileExtension, fileMetaData);
            if (results != null) {
                String fileId;
                int ts;
                String token;
                String fileURl;
                InetSocketAddress socketAddress;

                String groupName = results[0];
                String remoteFile = results[1];
                message.put(GroupField, groupName);
                message.put(RemoteFileField, remoteFile);

                fileId = groupName + StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR + remoteFile;
                message.put(FileIdField, fileId);

                socketAddress = trackerServer.getInetSocketAddress();
                fileURl = "http://" + socketAddress.getAddress().getHostAddress();
                if (ClientGlobal.g_tracker_http_port != 80) {
                    fileURl += ":" + ClientGlobal.g_tracker_http_port;
                }

                fileURl += "/" + fileId;
                if (ClientGlobal.g_anti_steal_token) {
                    ts = (int) (System.currentTimeMillis() / 1000);
                    token = ProtoCommon.getToken(fileId, ts, ClientGlobal.g_secret_key);
                    fileURl += "?token=" + token + "&ts=" + ts;
                }

                message.put(FileUrlField, fileURl);
                LogService.getLogger(FastDFSUtil.class).info(client.get_file_info(groupName, remoteFile).toString());

                return message;
            } else {
                return null;
            }
        } finally {
            clientPool.releaseStorageClient(client);
        }
    }

    /**
     * 上传本地文件
     * ObjectNode  a = FileUtil.upload("C://Desert.jpg", "沙漠");
     * System.out.println(a.toString());
     *
     * @param fileName    本地文件的绝对路径，如 C://Desert.jpg
     * @param description 文件备注, 可以为空
     * @return {"groupName":"group1","remoteFileName":"/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg"
     * {
     * "groupName": "healthArchiveGroup",
     * "remoteFileName": "/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg",
     * "fid": "group1/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg",
     * "fileURL": "http://172.19.103.13/healthArchiveGroup/M00/00/24/rBFuH1XdQC6AP3CDAAzodQCbVVc052.jpg"
     * }
     * <p>
     * groupName 及 remoteFileName 可以用于查询在 fastDFS 中文件的信息，如果只是图片显示，可以忽略这两个值。
     * fid 保存了在 fastDFS 上的完整路径，为了避免将来服务器域名发生变更，最好使用本值.服务器的域名另外配置。
     * fileURL 保存了完整的 web 访问路径，为了避免将来服务器域名发生变更，最好不要直接使用本值。
     * 如果需要在下载时，可以显示原始文件名，请在访问file_url时，增加 attname 参数，如：
     * <p>
     * http://host/healthArchiveGroup/M00/00/00/rBFuH1XdIseAUTZZAA1rIuRd3Es062.jpg?attname=a.jpg
     * @throws Exception
     */
    public ObjectNode upload(String fileName, String description) throws Exception {
        StorageClient client = clientPool.getStorageClient();
        try {
            NameValuePair[] fileMetaData;
            fileMetaData = new NameValuePair[1];
            fileMetaData[0] = new NameValuePair("description", description == null ? "" : description);

//            ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
            ObjectNode message = new ObjectMapper().createObjectNode();
            String fileExtension = "";
            if (fileName.contains(".")) {
                fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
            } else {
                throw new RuntimeException("上传失败, 文件缺失扩展名.");
            }

            TrackerServer trackerServer = clientPool.getTrackerServer();
            String[] results = client.upload_file(fileName, fileExtension, fileMetaData);
            if (results != null) {
                String fileId;
                int ts;
                String token;
                String fileUrl;
                InetSocketAddress inetSockAddr;

                String groupName = results[0];
                String remoteFileName = results[1];
                message.put(GroupField, groupName);
                message.put(RemoteFileField, remoteFileName);

                fileId = groupName + StorageClient1.SPLIT_GROUP_NAME_AND_FILENAME_SEPERATOR + remoteFileName;
                message.put(FileIdField, fileId);

                inetSockAddr = trackerServer.getInetSocketAddress();
                fileUrl = "http://" + inetSockAddr.getAddress().getHostAddress();
                if (ClientGlobal.g_tracker_http_port != 80) {
                    fileUrl += ":" + ClientGlobal.g_tracker_http_port;
                }
                fileUrl += "/" + fileId;
                if (ClientGlobal.g_anti_steal_token) {
                    ts = (int) (System.currentTimeMillis() / 1000);
                    token = ProtoCommon.getToken(fileId, ts, ClientGlobal.g_secret_key);
                    fileUrl += "?token=" + token + "&ts=" + ts;
                }

                message.put(FileUrlField, fileUrl);
                LogService.getLogger(FastDFSUtil.class).info(client.get_file_info(groupName, remoteFileName).toString());

                return message;
            } else {
                return null;
            }
        } finally {
            clientPool.releaseStorageClient(client);
        }
    }

    /**
     * 下载文件, 返回文件字节数组.
     *
     * @param groupName      在fastdfs上的卷名
     * @param remoteFileName 在fastdfs上的路径
     * @return 文件的字节码
     * @throws Exception
     */
    public byte[] download(String groupName, String remoteFileName) throws Exception {
        StorageClient client = clientPool.getStorageClient();
        try {
            byte[] b = client.download_file(groupName, remoteFileName);

            return b;
        } finally {
            clientPool.releaseStorageClient(client);
        }
    }

    /**
     * 下载文件到本地路径上.
     *
     * @param groupName      在 fastDFS 上的卷名
     * @param remoteFileName 在 fastDFS 上的路径
     * @param localPath      本地路径
     * @return 是否下载成功
     */
    public String download(String groupName, String remoteFileName, String localPath) throws IOException, MyException {
        StorageClient client = clientPool.getStorageClient();
        try {
            String localFileName = localPath + "\\" + remoteFileName.replaceAll("/", "_");
            client.download_file(groupName, remoteFileName, 0, 0, localFileName);

            return localFileName;
        } finally {
            clientPool.releaseStorageClient(client);
        }
    }

    /**
     * 删除文件。
     *
     * @param groupName
     * @param remoteFileName
     */
    public void delete(String groupName, String remoteFileName) throws IOException, MyException {
        StorageClient client = clientPool.getStorageClient();
        try {
            client.delete_file(groupName, remoteFileName);
        } finally {
            clientPool.releaseStorageClient(client);
        }
    }
}
