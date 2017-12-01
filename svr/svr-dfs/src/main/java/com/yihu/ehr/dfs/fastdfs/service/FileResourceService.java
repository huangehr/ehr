package com.yihu.ehr.dfs.fastdfs.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dfs.fastdfs.dao.FileResourceDao;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

/**
 * @Service - 文件服务
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Service
@Transactional
public class FileResourceService extends BaseJpaService<FileResource, FileResourceDao> {

    @Autowired
    private FileResourceDao resourceDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;


    public String saveFileResource(String fileStr, String fileName, FileResource fileResource) throws Exception {
//        byte[] bytes = fileStr.getBytes();
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        //   保存到resource表中
        fileResource.setStoragePath(path);
        return resourceDao.save(fileResource).getId();
    }

    public String saveFileResourceReturnUrl(String fileStr, String fileName, FileResource fileResource) throws Exception {

        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        //   保存到resource表中
        fileResource.setStoragePath(path);
        resourceDao.save(fileResource).getId();
        return path ;
    }

    public String saveFileResourceReturnHttpUrl(String fileStr, String fileName, FileResource fileResource) throws Exception {

        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        //   保存到resource表中
        fileResource.setStoragePath(path);
        resourceDao.save(fileResource).getId();
        path = path.replace(":","/");
        return fastDfsPublicServers.substring(0,fastDfsPublicServers.lastIndexOf(":"))+"/"+path ;
    }

    public List<FileResource> findByObjectId(String objectId) {
        return resourceDao.findByObjectId(objectId);
    }

    public List<FileResource> findByObjectIdAndMime(String objectId, String mime) {
        return resourceDao.findByObjectIdAndMime(objectId, mime);
    }

    public List<FileResource> findByStoragePath(String storagePath) {
        return resourceDao.findByStoragePath(storagePath);
    }

    public boolean deleteFileResource(List<FileResource> fileResources) throws Exception {
        for(FileResource fileResource : fileResources){
            //删除表数据
            resourceDao.delete(fileResource.getId());
            //删除fastdfs上的文件
            String storagePath = fileResource.getStoragePath();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            fastDFSUtil.delete(groupName,remoteFileName);
        }
        return true;
    }
}