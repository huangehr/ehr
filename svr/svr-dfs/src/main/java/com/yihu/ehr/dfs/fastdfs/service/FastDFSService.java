package com.yihu.ehr.dfs.fastdfs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.dfs.fastdfs.dao.FastDFSDao;
import org.csource.common.MyException;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private FastDFSDao fastDFSDao;
    @Autowired
    private ObjectMapper objectMapper;

    public ObjectNode upload(Map<String, String> paramMap) throws Exception{
        //String creator = paramMap.get("creator");
        //String objectId = paramMap.get("objectId");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String description = paramMap.get("description");
        byte [] descriptions = description.getBytes();
        List<Byte> list = new ArrayList<Byte>(descriptions.length);
        for(byte temp : descriptions) {
            list.add(temp);
        }
        String newDescription = objectMapper.writeValueAsString(list);
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return fastDFSDao.upload(inputStream, fileExtension, newDescription);
    }

    /**
    public String fileUploadReturnId(Map<String, String> paramMap) throws JsonProcessingException {
        //byte[] bytes = fileStr.getBytes();
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        String description = paramMap.get("description");
        byte [] descriptions = description.getBytes();
        List<Byte> list = new ArrayList<Byte>(descriptions.length);
        for(byte temp : descriptions) {
            list.add(temp);
        }
        String newDescription = objectMapper.writeValueAsString(list);
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode;
        try {
            objectNode = fastDFSDao.upload(inputStream, fileExtension, newDescription);
        }catch (Exception e) {
            e.printStackTrace();
            return "ERROR:" + e.getMessage();
        }
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
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
        Map<String, Object> newSource;
        try {
            newSource = elasticSearchDao.index(index, source);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                fastDFSDao.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                e1.printStackTrace();
                return "ERROR:" + e1.getMessage();
            }
            return "ERROR:" + e.getMessage();
        }
        if(null != source) {
            return newSource.get("_id").toString();
        }else {
            return null;
        }
    }

    public String fileUploadReturnUrl(Map<String, String> paramMap) throws IOException, MyException, NoSuchAlgorithmException{
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        String description = paramMap.get("description");
        byte [] descriptions = description.getBytes();
        List<Byte> list = new ArrayList<Byte>(descriptions.length);
        for(byte temp : descriptions) {
            list.add(temp);
        }
        String newDescription = objectMapper.writeValueAsString(list);
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSDao.upload(inputStream, fileExtension, newDescription);
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
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
        Map<String, Object> newSource =  elasticSearchDao.index(index, source);
        if(null != source) {
            return newSource.get("path").toString();
        }else {
            return null;
        }
    }

    public String fileUploadReturnHttpUrl(Map<String, String> paramMap) throws IOException, MyException, NoSuchAlgorithmException {
        String creator = paramMap.get("creator");
        String fileStr = paramMap.get("fileStr");
        String fileName = paramMap.get("fileName");
        String objectId = paramMap.get("objectId");
        String description = paramMap.get("description");
        byte [] descriptions = description.getBytes();
        List<Byte> list = new ArrayList<Byte>(descriptions.length);
        for(byte temp : descriptions) {
            list.add(temp);
        }
        String newDescription = objectMapper.writeValueAsString(list);
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        ObjectNode objectNode = fastDFSDao.upload(inputStream, fileExtension, newDescription);
        String groupName = objectNode.get("groupName").toString();
        String remoteFileName = objectNode.get("remoteFileName").toString();
        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
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
        elasticSearchDao.index(index, source);
        path = path.replace(":","/");
        publicServer = getPublicUrl();
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        return publicUrl + "/" + path;
    }

    public boolean deleteFile(Map<String, Object> source, String id) throws IOException, MyException {
        //删除fastDfs上的文件
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        fastDFSDao.delete(groupName, remoteFileName);
        //删除Es数据
        elasticSearchDao.delete(index, id.split(","));
        return true;
    }
    */

    public void delete(String groupName, String remoteFileName) throws IOException, MyException {
        fastDFSDao.delete(groupName, remoteFileName);
    }

    /**
    public int modify(Map<String, String> paramMap) throws IOException, MyException {
        String _id = paramMap.get("_id");
        String modifier = paramMap.get("modifier");
        String fileStr = paramMap.get("fileStr");
        String path = paramMap.get("path");
        //String objectId = paramMap.get("objectId");
        byte[] bytes = Base64.getDecoder().decode(fileStr);
        int size = fastDFSDao.modify(path.split(":")[0], path.split(":")[1], 23356, bytes);
        Map<String, Object> source = elasticSearchDao.findById(INDEX, _id);
        source.remove("_id");
        source.put("modifier", modifier);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("modifyDate", nowStr);
        elasticSearchDao.update(INDEX, _id, source);
        return size;
    }
    */

    public FileInfo getFileInfo(String groupName, String remoteFileName) throws IOException, MyException{
        return fastDFSDao.getFileInfo(groupName, remoteFileName);
    }

    public List<NameValuePair> getMetadata(String groupName, String remoteFileName) throws IOException, MyException{
        NameValuePair[] nameValuePairs = fastDFSDao.getMetadata(groupName, remoteFileName);
        List<NameValuePair> resultList = null;
        if(null != nameValuePairs) {
            resultList = new ArrayList<NameValuePair>(nameValuePairs.length);
            for (int i = 0; i < nameValuePairs.length; i++) {
                NameValuePair nameValuePair = nameValuePairs[i];
                String value = nameValuePair.getValue();
                List<Integer> list = objectMapper.readValue(value, List.class);
                byte [] byteV = new byte[list.size()];
                for(int j = 0; j < list.size(); j ++) {
                    byteV[j] = list.get(j).byteValue();
                }
                String newValue = new String(byteV, "UTF-8");
                nameValuePair.setValue(newValue);
                resultList.add(nameValuePair);
            }
        }
        return resultList;
    }

    public byte[] download(String groupName, String remoteFileName) throws IOException, MyException{
        return fastDFSDao.download(groupName, remoteFileName);
    }

    public List<Map<String, Object>> status() throws IOException {
        return fastDFSDao.status();
    }

}