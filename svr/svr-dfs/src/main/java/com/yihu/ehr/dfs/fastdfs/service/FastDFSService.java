package com.yihu.ehr.dfs.fastdfs.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dfs.es.dao.ElasticSearchDao;
import com.yihu.ehr.dfs.fastdfs.dao.FastDFSDao;
import com.yihu.ehr.dfs.fastdfs.entity.FastDFS;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Service - 文件服务
 * @author linaz
 * @created 2016.05.12 8:53
 */
@Service
@Transactional
public class FastDFSService {

    private static String INDEX = "dfs";
    private static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServer;

    @Autowired
    private FastDFSDao fastDFSDao;
    @Autowired
    private ElasticSearchDao elasticSearchDao;

    public String fileUploadReturnId(Map<String, String> paramMap) throws Exception {
        //byte[] bytes = fileStr.getBytes();
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSDao.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length()-1);
        // 保存到resource表中
        // fileResource.setStoragePath(path);
        //保存到ES库中
        Map<String, Object> source = new HashMap<String, Object>();
        char prefix = CHARS.charAt((int)(Math.random() * 26));
        source.put("sn", prefix + "" + new Date().getTime());
        source.put("name", fileName);
        source.put("path", path);
        source.put("objectId", objectId);
        source.put("size", bytes.length);
        source.put("type", fileExtension);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("createDate", nowStr);
        source.put("creator", creator);
        source.put("modifyDate", nowStr);
        source.put("modifier", creator);
        Map<String, Object> newSource =  elasticSearchDao.index(INDEX, source);
        if(null != source) {
            return newSource.get("_id").toString();
        }else {
            return null;
        }
    }

    public String fileUploadReturnUrl(Map<String, String> paramMap) throws Exception {
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSDao.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        //保存到ES库中
        Map<String, Object> source = new HashMap<String, Object>();
        char prefix = CHARS.charAt((int)(Math.random() * 26));
        source.put("sn", prefix + "" + new Date().getTime());
        source.put("name", fileName);
        source.put("path", path);
        source.put("objectId", objectId);
        source.put("size", bytes.length);
        source.put("type", fileExtension);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("createDate", nowStr);
        source.put("creator", creator);
        source.put("modifyDate", nowStr);
        source.put("modifier", creator);
        Map<String, Object> newSource =  elasticSearchDao.index(INDEX, source);
        if(null != source) {
            return newSource.get("path").toString();
        }else {
            return null;
        }
    }

    public String fileUploadReturnHttpUrl(Map<String, String> paramMap) throws Exception {
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSDao.upload(inputStream, fileExtension, "");
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
        // 保存到ES库中
        Map<String, Object> source = new HashMap<String, Object>();
        char prefix = CHARS.charAt((int)(Math.random() * 26));
        source.put("sn", prefix + "" + new Date().getTime());
        source.put("name", fileName);
        source.put("path", path);
        source.put("objectId", objectId);
        source.put("size", bytes.length);
        source.put("type", fileExtension);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("createDate", nowStr);
        source.put("creator", creator);
        source.put("modifyDate", nowStr);
        source.put("modifier", creator);
        Map<String, Object> newSource =  elasticSearchDao.index(INDEX, source);
        // 保存到resource表中
        // fileResource.setStoragePath(path);
        // resourceDao.save(fileResource).getId();
        path = path.replace(":","/");
        if(null != source) {
            return fastDfsPublicServer.substring(0, fastDfsPublicServer.lastIndexOf(":")) + "/" + newSource.get("path").toString() ;
        }else {
            return null;
        }
    }

    public boolean deleteFile(Map<String, Object> source, String id) throws Exception {
        //删除fastDfs上的文件
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        fastDFSDao.delete(groupName, remoteFileName);
        //删除Es数据
        elasticSearchDao.delete(INDEX, id.split(","));
        return true;
    }

    public byte[] download(String groupName, String remoteFileName) throws Exception {
        return fastDFSDao.download(groupName, remoteFileName);
    }

}