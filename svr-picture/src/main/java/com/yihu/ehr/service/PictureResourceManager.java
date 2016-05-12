package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Service
@Transactional
public class PictureResourceManager extends BaseJpaService<PictureResource, XPictureResourceRepository> {

    @Autowired
    private XPictureResourceRepository resourceRepository;

    @Autowired
    private FastDFSUtil fastDFSUtil;


    public String saveResource(String fileStr, String fileName, PictureResource pictureResource) throws Exception {

        byte[] bytes = fileStr.getBytes();
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        ////   保存到resource表中
        pictureResource.setStoragePath(path);
        resourceRepository.save(pictureResource);
        return path;
    }
}