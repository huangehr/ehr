package com.yihu.ehr.medicalRecords.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Base64;

/**
 * Created by hzp on 2016/8/1.
 * 文件上传类
 */
@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 上传图片
     */
    public String uploadPicture(String fileData) throws Exception {

        if(fileData == null){
            return null;
        }

        String data = URLDecoder.decode(fileData,"UTF-8");

        String[] fileStreams = data.split(",");
        String is = URLDecoder.decode(fileStreams[0],"UTF-8").replace(" ","+");
        byte[] in = Base64.getDecoder().decode(is);

        String pictureName = fileStreams[1].substring(0,fileStreams[1].length()-1);
        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;

        if ((pictureName != null) && (pictureName.length() > 0)) {
            int dot = pictureName.lastIndexOf('.');
            if ((dot > -1) && (dot < (pictureName.length()))) {
                description = pictureName.substring(0, dot);
            }
        }

        String path = null;

        try {
            InputStream inputStream = new ByteArrayInputStream(in);

            ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();

            path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);

        } catch (Exception e) {

            LogService.getLogger(FileService.class).error("图片上传失败；错误代码："+e);
        }


        //返回文件路径
        return path;
    }


}
