package com.yihu.ehr.dfs.fastdfs.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.client.SystemDictClient;
import com.yihu.ehr.dfs.client.SystemDictEntryClient;
import com.yihu.ehr.dfs.es.service.ElasticSearchService;
import com.yihu.ehr.dfs.fastdfs.service.FastDFSService;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EndPoint - 文件服务
 * Created by progr1mmer on 2017/12/1.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "FastDFSEndPoint", description = "FastDFS服务", tags = {"分布式综合服务-FastDFS服务"})
public class FastDFSEndPoint extends EnvelopRestEndPoint {

    private static String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<String> publicServer;

    @Value("${fast-dfs.index.name}")
    private String indexName;
    @Value("${fast-dfs.index.type}")
    private String indexType;
    @Value("${fast-dfs.server.dict-code}")
    private String dictCode;

    @Autowired
    private FastDFSService fastDFSService;
    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private SystemDictClient systemDictClient;
    @Autowired
    private SystemDictEntryClient systemDictEntryClient;

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接
     * @param file
     * @param creator
     * @param objectId
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Upload, method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接")
    public Envelop upload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name = "creator", value = "创建者", required = true)
            @RequestParam(value = "creator") String creator,
            @ApiParam(name = "objectId", value = "创建者标识", required = true, defaultValue = "EHR")
            @RequestParam(value = "objectId") String objectId) {
        Envelop envelop = new Envelop();
        ObjectNode objectNode;
        //上传
        try {
            objectNode = fastDFSService.upload(file);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        //保存索引到ES库中
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("fileName", file.getOriginalFilename());
        paramMap.put("creator", creator);
        paramMap.put("objectId", objectId);
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        Map<String, Object> newSource;
        try {
            newSource = elasticSearchService.index(indexName, indexType, source);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                fastDFSService.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                e1.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e1.getMessage());
                return envelop;
            }
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        if(null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        newSource.put("httpUrl", publicUrl + "/" + path);
        envelop.setSuccessFlg(true);
        envelop.setObj(newSource);
        return envelop;
    }

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.OldUpload, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    public Envelop upload(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        ObjectNode objectNode;
        try {
            objectNode = fastDFSService.upload(paramMap);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        //保存索引到ES库中
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        Map<String, Object> newSource;
        try {
            newSource = elasticSearchService.index(indexName, indexType, source);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                fastDFSService.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                e1.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e1.getMessage());
                return envelop;
            }
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        if(null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        newSource.put("httpUrl", publicUrl + "/" + path);
        envelop.setSuccessFlg(true);
        envelop.setObj(newSource);
        return envelop;
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteById, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteById(
            @ApiParam(name = "id", value = "id", required = true)
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        Map<String, Object> source = elasticSearchService.findById(indexName, indexType, id);
        if(null == source) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关文件资源");
            return envelop;
        }
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        // 删除文件
        try {
            fastDFSService.delete(groupName, remoteFileName);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        // 删除索引
        elasticSearchService.delete(indexName, indexType, id.split(","));
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteByPath, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        //String path = java.net.URLDecoder.decode(storagePath, "UTF-8");
        // 删除文件
        try {
            fastDFSService.delete(path.split(":")[0], path.split(":")[1]);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        List<Map<String, Object>> resultList = elasticSearchService.findByField(indexName, indexType,"path", path);
        StringBuilder ids = new StringBuilder();
        for(Map<String, Object> resultMap : resultList) {
            String id = resultMap.get("_id").toString();
            ids.append(id + ",");
            // 删除索引
            elasticSearchService.delete(indexName, indexType, ids.toString().split(","));
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DeleteByObjectId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList = elasticSearchService.findByField(indexName, indexType, "objectId", objectId);
        StringBuilder ids = new StringBuilder();
        for(Map<String, Object> resultMap : resultList) {
            String id = resultMap.get("_id").toString();
            ids.append(id + ",");
            String storagePath = resultMap.get("path").toString();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            // 删除文件
            try {
                fastDFSService.delete(groupName, remoteFileName);
            }catch (Exception e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }
        // 删除索引
        elasticSearchService.delete(indexName, indexType, ids.toString().split(","));
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 修改文件 - 返回相关索引信息,以及HttpUrl下载连接
     * @param file
     * @param path
     * @param _id
     * @param modifier
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Modify, method = RequestMethod.POST)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接")
    public Envelop modify(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name = "path", value = "旧文件路径", required = true)
            @RequestParam(value = "path") String path,
            @ApiParam(name = "_id", value = "旧文件唯一索引ID", required = true)
            @RequestParam(value = "_id") String _id,
            @ApiParam(name = "modifier", value = "修改者", required = true)
            @RequestParam(value = "modifier") String modifier) {
        Envelop envelop = new Envelop();
        Map<String, Object> source = elasticSearchService.findById(indexName, indexType, _id);
        if (null == source) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关文件资源");
            return envelop;
        }
        // 删除旧文件
        try {
            fastDFSService.delete(path.split(":")[0], path.split(":")[1]);
        }catch (MyException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        // 上传新文件
        ObjectNode objectNode;
        try {
            objectNode = fastDFSService.upload(file);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        // 更新索引
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        String name = file.getOriginalFilename();
        if (!StringUtils.isEmpty(name)) {
            source.put("name", name);
        }
        source.put("modifier", modifier);
        source.put("path", groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("modifyDate", nowStr);
        Map<String, Object> resultMap = elasticSearchService.update(indexName, indexType, _id, source);
        String newPath = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if(null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        resultMap.put("httpUrl", publicUrl + "/" + newPath);
        envelop.setSuccessFlg(true);
        envelop.setObj(resultMap);
        return envelop;
    }

    /**
     * 修改文件 - 返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.OldModify, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改文件", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    public Envelop modify(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        String _id = paramMap.get("_id");
        Map<String, Object> source = elasticSearchService.findById(indexName, indexType, _id);
        if (null == source) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关文件资源");
            return envelop;
        }
        // 删除旧文件
        String oldPath = source.get("path").toString();
        try {
            fastDFSService.delete(oldPath.split(":")[0], oldPath.split(":")[1]);
        }catch (MyException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        // 上传文件
        ObjectNode objectNode;
        try {
            objectNode = fastDFSService.upload(paramMap);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        // 更新索引
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        String modifier = paramMap.get("modifier");
        String name = paramMap.get("fileName");
        if (!StringUtils.isEmpty(name)) {
            source.put("name", name);
        }
        source.put("modifier", modifier);
        source.put("path", groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("modifyDate", nowStr);
        Map<String, Object> resultMap = elasticSearchService.update(indexName, indexType, _id, source);
        String newPath = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if(null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        resultMap.put("httpUrl", publicUrl + "/" + newPath);
        envelop.setSuccessFlg(true);
        envelop.setObj(resultMap);
        return envelop;
    }

    /**
     * 获取文件信息
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.FileInfo, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件信息")
    public Envelop getInfo(
            @ApiParam(name = "path", value = "路径", required = true)
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        try {
            FileInfo fileInfo = fastDFSService.getFileInfo(path.split(":")[0], path.split(":")[1]);
            envelop.setObj(fileInfo);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 下载文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadById, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byId)")
    public Envelop downloadById (
            @ApiParam(name = "id", value = "id", required = true)
            @RequestParam(value = "id") String id) {
        Envelop envelop = new Envelop();
        Map<String, Object> source = elasticSearchService.findById(indexName, indexType, id);
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        byte[] bytes;
        try {
            bytes = fastDFSService.download(groupName, remoteFileName);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        if(!StringUtils.isEmpty(fileStream)) {
            envelop.setSuccessFlg(true);
            envelop.setObj(fileStream);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg("FileStream Is Empty");
        return envelop;
    }

    /**
     * 下载文件
     * @param path
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadByPath, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    public Envelop downloadByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        //String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        String groupName = path.split(":")[0];
        String remoteFileName = path.split(":")[1];
        byte[] bytes;
        try {
            bytes = fastDFSService.download(groupName, remoteFileName);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        if(!StringUtils.isEmpty(fileStream)) {
            envelop.setSuccessFlg(true);
            envelop.setObj(fileStream);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        envelop.setErrorMsg("FileStream Is Empty");
        return envelop;
    }

    /**
     * 下载文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadByObjectId, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    public Envelop downloadByObjectId(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> indexList = elasticSearchService.findByField(indexName, indexType, "objectId", objectId);
        List<String> resultList = new ArrayList<String>(indexList.size());
        for(Map<String, Object> resultMap : indexList) {
            String storagePath = resultMap.get("path").toString();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            try {
                byte [] bytes = fastDFSService.download(groupName, remoteFileName);
                String fileStream = new String(Base64.getEncoder().encode(bytes));
                resultList.add(fileStream);
            }catch (Exception e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 下载文件至本地路径
     * @param remotePath
     * @param localPath
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadToLocal, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    public Envelop downloadToLocal(
            @ApiParam(name = "remotePath", value = "远程文件路径", required = true)
            @RequestParam(value = "remotePath") String remotePath,
            @ApiParam(name = "localPath", value = "本地文件路径", required = true)
            @RequestParam(value = "localPath") String localPath) {
        Envelop envelop = new Envelop();
        //String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        String groupName = remotePath.split(":")[0];
        String remoteFileName = remotePath.split(":")[1];
        String localFileName = localPath + remoteFileName.replaceAll("/", "_");
        byte[] bytes;
        try {
            bytes = fastDFSService.download(groupName, remoteFileName);
            FileOutputStream fileOutputStream = new FileOutputStream(localFileName);
            fileOutputStream.write(bytes);
            fileOutputStream.close();
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj("文件已下载至:" + localFileName);
        return envelop;
    }

    /**
     * 获取文件下载路径
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetFilePath, method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径(byObjectId)")
    public Envelop getPath(
            @ApiParam(name = "objectId", value = "对象ID", required = true)
            @RequestParam(value = "objectId") String objectId) {
        Envelop envelop = new Envelop();
        //List<FastDFS> fileResources = fastDFSService.findByObjectId(objectId);
        List<Map<String, Object>> resultList = elasticSearchService.findByField(indexName, indexType, "objectId", objectId);
        List<String> fileStrList = new ArrayList<>();
        for (Map<String, Object> resultMap : resultList) {
            String storagePath = resultMap.get("path").toString();
            String path = storagePath.replaceAll(":", "/");
            /**
            try {
                storagePath = URLEncoder.encode(storagePath,"ISO8859-1");
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
            */
            fileStrList.add(path);
        }
        envelop.setDetailModelList(fileStrList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取分页
     * @param filter
     * @param page
     * @param size
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.Page, method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "page", value = "页码", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        List<Map<String, String>> filterMap;
        if(!StringUtils.isEmpty(filter)) {
            try {
                filterMap = objectMapper.readValue(filter, List.class);
            } catch (IOException e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }else {
            filterMap = new ArrayList<Map<String, String>>(0);
        }
        List<Map<String, Object>> resultList = elasticSearchService.page(indexName, indexType, filterMap, page, size);
        int count = (int)elasticSearchService.count(indexName, indexType, filterMap);
        envelop = getPageResult(resultList, count, page, size);
        return envelop;
    }

    /**
     * 获取服务器状态信息
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Status, method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    public Envelop status() {
        Envelop envelop = new Envelop();
        Map<String, Object> resultMap;
        try {
            resultMap = fastDFSService.status();
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setDetailModelList((List) resultMap.get("space"));
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetPublicUrl, method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    public Envelop getPublicUrl() {
        Envelop envelop = new Envelop();
        if (null == publicServer || publicServer.isEmpty()) {
            MSystemDict systemDict = systemDictClient.getDictionaryByPhoneticCode(dictCode);
            Envelop envelop1 = systemDictEntryClient.listByDictId(systemDict.getId());
            if(envelop1.isSuccessFlg()) {
                List<Map<String, Object>> list = envelop1.getDetailModelList();
                publicServer = new ArrayList<String>(list.size());
                for(Map dictEntry : list) {
                    publicServer.add(String.valueOf(dictEntry.get("value")));
                }
            }
        }
        envelop.setDetailModelList(publicServer);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 设置外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.SetPublicUrl, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "设置外链地址")
    public Envelop setPublicUrl(
            @ApiParam(name = "jsonData", value = "字典项JSON结构")
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        MDictionaryEntry mDictionaryEntry;
        synchronized (FastDFSEndPoint.class) {
            mDictionaryEntry = systemDictEntryClient.updateDictEntry(jsonData);
            publicServer = null;
            publicServer = getPublicUrl().getDetailModelList();
        }
        envelop.setObj(mDictionaryEntry);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    private Map<String, Object> getIndexSource(ObjectNode objectNode, Map<String, String> paramMap) {
        Map<String, Object> source = new HashMap<String, Object>();
        String fileName = paramMap.get("fileName");
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String creator = paramMap.get("creator");
        String objectId = paramMap.get("objectId");
        int fileSize = objectNode.get(FastDFSUtil.FILE_SIZE).asInt();
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
        char prefix = CHARS.charAt((int)(Math.random() * 26));
        source.put("sn", prefix + "" + new Date().getTime());
        source.put("name", fileName);
        source.put("path", path);
        source.put("objectId", objectId);
        source.put("size", fileSize);
        if(!fileType.equals(fileName)) {
            source.put("type", fileType);
        }else {
            source.put("type", "file");
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("createDate", nowStr);
        source.put("creator", creator);
        source.put("modifyDate", nowStr);
        source.put("modifier", creator);
        return source;
    }

}