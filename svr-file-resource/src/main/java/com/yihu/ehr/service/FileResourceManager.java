package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Service
@Transactional
public class FileResourceManager extends BaseJpaService<FileResource, XFileResourceRepository> {

    @Autowired
    private XFileResourceRepository resourceRepository;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    public String saveFileResource(String fileStr, String fileName, FileResource fileResource) throws Exception {

        byte[] bytes = fileStr.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        ////   保存到resource表中
        fileResource.setStoragePath(path);
        resourceRepository.save(fileResource);
        return path;
    }

    public List<FileResource> findByObjectId(String objectId) {
        return resourceRepository.findByObjectId(objectId);
    }

    public boolean deleteFileResource(List<FileResource> fileResources) throws Exception {
        for(FileResource fileResource : fileResources){
            //删除表数据
            resourceRepository.delete(fileResource.getId());
            //删除fastdfs上的文件
            String storagePath = fileResource.getStoragePath();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            fastDFSUtil.delete(groupName,remoteFileName);
        }
        return true;
    }
}