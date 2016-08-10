package com.yihu.ehr.medicalRecords.comom;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.image.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * Created by hzp on 2016/8/10.
 * 文件上传类
 */
@Service
public class FileService {

    @Autowired
    FastDFSUtil fastDFSUtil;


    private Integer scaleWidth = 200;  //缩略图宽度
    private Integer scaleHeight = 200;  //缩略图高度


    /**
     * 上传图片，同时生成缩略图
     */
    public String uploadImage(String data,String extension) throws Exception
    {
        byte[] bytes = Base64.getDecoder().decode(data);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectNode objectNode = fastDFSUtil.upload(inputStream, extension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);

        //保存缩略图
        byte[] bytesScale = ImageUtil.scale(bytes,scaleWidth,scaleHeight);
        InputStream inputStreamScale = new ByteArrayInputStream(bytesScale);
        ObjectNode objectNodeScale = fastDFSUtil.upload(inputStreamScale, extension, "");

        return path;
    }

}
