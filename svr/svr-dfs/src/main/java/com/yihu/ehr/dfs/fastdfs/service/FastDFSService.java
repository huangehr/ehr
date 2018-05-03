package com.yihu.ehr.dfs.fastdfs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dfs.fastdfs.dao.FastDFSDao;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Service - 文件服务
 * @author Progr1mmer
 * @created 2017.12.04 8:53
 */
@Service
public class FastDFSService {

    @Autowired
    private FastDFSUtil fastDFSUtil;


    public ObjectNode upload(Map<String, String> paramMap) throws Exception{
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fastDFSUtil.upload(inputStream, fileExtension, "svr-dfs");
    }

    public ObjectNode upload(MultipartFile file) throws Exception{
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fastDFSUtil.upload(file.getInputStream(), fileExtension, "svr-dfs");
    }

}