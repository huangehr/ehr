package com.yihu.ehr.dfs.fastdfs.dao;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * Dao - 文件服务
 * @author Progr1mmer
 * @created 2017.12.04 8:53
 */
@Repository
public class FastDFSDao {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    public ObjectNode upload(InputStream in, String fileExtension, String description) throws IOException, MyException, NoSuchAlgorithmException {
        return fastDFSUtil.upload(in, fileExtension, description);
    }

    public int modify(String groupName, String remoteFilename, NameValuePair[] metaList, byte opFlag) throws IOException, MyException{
        return fastDFSUtil.modify(groupName, remoteFilename, metaList, opFlag);
    }

    public FileInfo getFileInfo(String groupName, String remoteFileName) throws IOException, MyException{
        return fastDFSUtil.getFileInfo(groupName, remoteFileName);
    }

    public NameValuePair[] getMetadata(String groupName, String remoteFileName) throws IOException, MyException{
        return fastDFSUtil.getMetadata(groupName, remoteFileName);
    }

    public void delete(String groupName, String remoteFileName) throws IOException, MyException {
        fastDFSUtil.delete(groupName, remoteFileName);
    }

    public byte[] download(String groupName, String remoteFileName) throws IOException, MyException {
        return fastDFSUtil.download(groupName, remoteFileName);
    }

    public Map<String, Object> status() throws IOException {
        return fastDFSUtil.status();
    }

}
