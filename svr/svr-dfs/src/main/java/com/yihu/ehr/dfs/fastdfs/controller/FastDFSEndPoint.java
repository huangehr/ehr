package com.yihu.ehr.dfs.fastdfs.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.fastdfs.service.FastDFSService;
import com.yihu.ehr.dfs.fastdfs.service.SystemDictEntryService;
import com.yihu.ehr.dfs.fastdfs.service.SystemDictService;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.dict.SystemDict;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.csource.fastdfs.FileInfo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    @Autowired
    private FastDFSService fastDFSService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private SystemDictService systemDictService;
    @Autowired
    private SystemDictEntryService systemDictEntryService;

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
            @RequestParam(value = "objectId") String objectId) throws Exception {
        ObjectNode objectNode;
        //上传
        objectNode = fastDFSService.upload(file);
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
            newSource = elasticSearchUtil.index(indexName, indexType, source);
        } catch (Exception e) {
            try {
                fastDFSUtil.delete(groupName, remoteFileName);
            } catch (Exception e1) {
                throw e1;
            }
            throw e;
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            try {
                publicServer = getPublicUrl().getDetailModelList();
                if (null != publicServer && !publicServer.isEmpty()) {
                    String publicUrl = publicServer.get((int) (Math.random() * publicServer.size()));
                    newSource.put("httpUrl", publicUrl + "/" + path);
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(FastDFSEndPoint.class).error(e.getMessage());
            }
        } else {
            String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
            newSource.put("httpUrl", publicUrl + "/" + path);
        }
        return success(newSource);
    }

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接 开放接口
     * @param file
     * @param creator
     * @param objectId
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.OpenUpload, method = RequestMethod.POST)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接")
    public Envelop openUpload(
            @ApiParam(name = "file", value = "文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @ApiParam(name = "creator", value = "创建者", required = true)
            @RequestParam(value = "creator") String creator,
            @ApiParam(name = "objectId", value = "创建者标识", required = true, defaultValue = "EHR")
            @RequestParam(value = "objectId") String objectId) throws Exception {
        ObjectNode objectNode;
        //上传
        objectNode = fastDFSService.upload(file);
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
            newSource = elasticSearchUtil.index(indexName, indexType, source);
        } catch (Exception e) {
            try {
                fastDFSUtil.delete(groupName, remoteFileName);
            } catch (Exception e1) {
                throw e1;
            }
            throw e;
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            try {
                publicServer = getPublicUrl().getDetailModelList();
                if (null != publicServer && !publicServer.isEmpty()) {
                    String publicUrl = publicServer.get((int) (Math.random() * publicServer.size()));
                    newSource.put("httpUrl", publicUrl + "/" + path);
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(FastDFSEndPoint.class).error(e.getMessage());
            }
        } else {
            String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
            newSource.put("httpUrl", publicUrl + "/" + path);
        }
        return success(newSource);
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
            @RequestBody String jsonData) throws Exception {
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        ObjectNode objectNode;
        objectNode = fastDFSService.upload(paramMap);
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        //保存索引到ES库中
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        Map<String, Object> newSource;
        try {
            newSource = elasticSearchUtil.index(indexName, indexType, source);
        } catch (Exception e) {
            try {
                fastDFSUtil.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                throw e1;
            }
            throw e;
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            try {
                publicServer = getPublicUrl().getDetailModelList();
                if (null != publicServer && !publicServer.isEmpty()) {
                    String publicUrl = publicServer.get((int) (Math.random() * publicServer.size()));
                    newSource.put("httpUrl", publicUrl + "/" + path);
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(FastDFSEndPoint.class).error(e.getMessage());
            }
        } else {
            String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
            newSource.put("httpUrl", publicUrl + "/" + path);
        }
        return success(newSource);
    }

    /**
     * 文件上传 - 返回相关索引信息,以及HttpUrl下载连接(兼容旧接口) 开放接口
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.OpenOldUpload, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "文件上传", notes = "返回相关索引信息,以及HttpUrl下载连接(兼容旧接口)")
    public Envelop openUpload(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) throws Exception {
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        ObjectNode objectNode;
        objectNode = fastDFSService.upload(paramMap);
        String groupName = objectNode.get(FastDFSUtil.GROUP_NAME).toString();
        String remoteFileName = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).toString();
        //保存索引到ES库中
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        Map<String, Object> newSource;
        try {
            newSource = elasticSearchUtil.index(indexName, indexType, source);
        } catch (Exception e) {
            try {
                fastDFSUtil.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                throw e1;
            }
            throw e;
        }
        String path = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            try {
                publicServer = getPublicUrl().getDetailModelList();
                if (null != publicServer && !publicServer.isEmpty()) {
                    String publicUrl = publicServer.get((int) (Math.random() * publicServer.size()));
                    newSource.put("httpUrl", publicUrl + "/" + path);
                }
            } catch (Exception e) {
                LoggerFactory.getLogger(FastDFSEndPoint.class).error(e.getMessage());
            }
        } else {
            String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
            newSource.put("httpUrl", publicUrl + "/" + path);
        }
        return success(newSource);
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
            @RequestParam(value = "id") String id) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(indexName, indexType, id);
        if (null == source) {
            return failed("无相关文件资源");
        }
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        // 删除文件
        fastDFSUtil.delete(groupName, remoteFileName);
        // 删除索引
        elasticSearchUtil.delete(indexName, indexType, id);
        return success(true);
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
            @RequestParam(value = "path") String path) throws Exception {
        //String path = java.net.URLDecoder.decode(storagePath, "UTF-8");
        if (path.split(":").length < 2) {
            return failed("参数有误");
        }
        // 删除文件
        fastDFSUtil.delete(path.split(":")[0], path.split(":")[1]);
        List<Map<String, Object>> resultList = elasticSearchUtil.findByField(indexName, indexType,"path", path);
        StringBuilder ids = new StringBuilder();
        for (Map<String, Object> resultMap : resultList) {
            String id = resultMap.get("_id").toString();
            ids.append(id + ",");
        }
        // 删除索引
        elasticSearchUtil.bulkDelete(indexName, indexType, ids.toString().split(","));
        return success(true);
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
            @RequestParam(value = "objectId") String objectId) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.findByField(indexName, indexType, "objectId", objectId);
        StringBuilder ids = new StringBuilder();
        for(Map<String, Object> resultMap : resultList) {
            String id = resultMap.get("_id").toString();
            ids.append(id + ",");
            String storagePath = resultMap.get("path").toString();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            // 删除文件
            fastDFSUtil.delete(groupName, remoteFileName);
        }
        // 删除索引
        elasticSearchUtil.bulkDelete(indexName, indexType, ids.toString().split(","));
        return success(true);
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
            @RequestParam(value = "modifier") String modifier) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(indexName, indexType, _id);
        if (null == source) {
            return failed("无相关文件资源");
        }
        if (path.split(":").length < 2) {
            return failed("参数有误");
        }
        // 删除旧文件
        fastDFSUtil.delete(path.split(":")[0], path.split(":")[1]);
        // 上传新文件
        ObjectNode objectNode = fastDFSService.upload(file);
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
        Map<String, Object> resultMap = elasticSearchUtil.update(indexName, indexType, _id, source);
        String newPath = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        resultMap.put("httpUrl", publicUrl + "/" + newPath);
        return success(resultMap);
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
            @RequestBody String jsonData) throws Exception {
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        String _id = paramMap.get("_id");
        Map<String, Object> source = elasticSearchUtil.findById(indexName, indexType, _id);
        if (null == source) {
            return failed("无相关文件资源");
        }
        // 删除旧文件
        String oldPath = source.get("path").toString();
        fastDFSUtil.delete(oldPath.split(":")[0], oldPath.split(":")[1]);
        // 上传文件
        ObjectNode objectNode = fastDFSService.upload(paramMap);
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
        Map<String, Object> resultMap = elasticSearchUtil.update(indexName, indexType, _id, source);
        String newPath = groupName.substring(1, groupName.length() - 1) + "/" + remoteFileName.substring(1, remoteFileName.length() - 1);
        if (null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        resultMap.put("httpUrl", publicUrl + "/" + newPath);
        return success(resultMap);
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
            @RequestParam(value = "path") String path) throws Exception {
        if (path.split(":").length < 2) {
            return failed("参数有误");
        }
        FileInfo fileInfo = fastDFSUtil.getFileInfo(path.split(":")[0], path.split(":")[1]);
        return success(fileInfo);
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
            @RequestParam(value = "id") String id) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(indexName, indexType, id);
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        if (!StringUtils.isEmpty(fileStream)) {
            return success(fileStream);
        }
        return failed("FileStream Is Empty");
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
            @RequestParam(value = "path") String path) throws Exception {
        //String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        if (path.split(":").length < 2) {
            return failed("参数有误");
        }
        String groupName = path.split(":")[0];
        String remoteFileName = path.split(":")[1];
        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        if (!StringUtils.isEmpty(fileStream)) {
            return success(fileStream);
        }
        return failed("FileStream Is Empty");
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
            @RequestParam(value = "objectId") String objectId) throws Exception {
        List<Map<String, Object>> indexList = elasticSearchUtil.findByField(indexName, indexType, "objectId", objectId);
        List<String> resultList = new ArrayList<String>(indexList.size());
        for (Map<String, Object> resultMap : indexList) {
            String storagePath = resultMap.get("path").toString();
            String groupName = storagePath.split(":")[0];
            String remoteFileName = storagePath.split(":")[1];
            byte [] bytes = fastDFSUtil.download(groupName, remoteFileName);
            String fileStream = new String(Base64.getEncoder().encode(bytes));
            resultList.add(fileStream);
        }
        return success(resultList);
    }

    /**
     * 下载文件至本地路径
     * @param remotePath
     * @param localPath
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.DownloadToLocal, method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath) 本地测试")
    public Envelop downloadToLocal(
            @ApiParam(name = "remotePath", value = "远程文件路径", required = true)
            @RequestParam(value = "remotePath") String remotePath,
            @ApiParam(name = "localPath", value = "本地文件路径", required = true)
            @RequestParam(value = "localPath") String localPath) throws Exception {
        //String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        if (remotePath.split(":").length < 2) {
            return failed("参数有误");
        }
        String groupName = remotePath.split(":")[0];
        String remoteFileName = remotePath.split(":")[1];
        String localFileName = localPath + remoteFileName.replaceAll("/", "_");
        byte[] bytes;
        bytes = fastDFSUtil.download(groupName, remoteFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(localFileName);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        return success("文件已下载至:" + localFileName);
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
        //List<FastDFS> fileResources = fastDFSService.findByObjectId(objectId);
        List<Map<String, Object>> resultList = elasticSearchUtil.findByField(indexName, indexType, "objectId", objectId);
        List<String> fileStrList = new ArrayList<>();
        for (Map<String, Object> resultMap : resultList) {
            String storagePath = resultMap.get("path").toString();
            String path = storagePath.replaceAll(":", "/");
            fileStrList.add(path);
        }
        return success(fileStrList);
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
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(indexName, indexType, filter, page, size);
        int count = (int)elasticSearchUtil.count(indexName, indexType, filter);
        Envelop envelop = getPageResult(resultList, count, page, size);
        return envelop;
    }

    /**
     * 获取服务器状态信息
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.Status, method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    public Envelop status() throws Exception {
        List<Map<String, Object>> status = fastDFSUtil.status();
        return success(status);
    }

    /**
     * 获取外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetPublicUrl, method = RequestMethod.GET)
    @ApiOperation(value = "获取外链地址")
    public Envelop getPublicUrl() {
        if (null == publicServer || publicServer.isEmpty()) {
            SystemDict systemDict = systemDictService.findByPhoneticCode(dictCode);
            Page<SystemDictEntry> page =  systemDictEntryService.findByDictId(systemDict.getId(), 0, 100);
            if (page != null) {
                List<SystemDictEntry> systemDictEntryList = page.getContent();
                publicServer = new ArrayList<>(systemDictEntryList.size());
                for (SystemDictEntry dictEntry : systemDictEntryList) {
                    publicServer.add(String.valueOf(dictEntry.getValue()));
                }
            }
        }
        return success(publicServer);
    }

    /**
     * 设置外链地址
     * @return
     */
    @RequestMapping(value = ServiceApi.FastDFS.SetPublicUrl, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "设置外链地址")
    public Envelop setPublicUrl(
            @ApiParam(name = "jsonData", value = "字典项JSON结构")
            @RequestBody String jsonData) throws IOException {
        SystemDictEntry entry;
        synchronized (FastDFSEndPoint.class) {
            entry = toEntity(jsonData, SystemDictEntry.class);
            systemDictEntryService.createDictEntry(entry);
            publicServer = null;
            publicServer = getPublicUrl().getDetailModelList();
        }
        return success(entry);
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
        if (!fileType.equals(fileName)) {
            source.put("type", fileType);
        } else {
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


    /**
     * 根据系统字典项获取Logo
     * @param dictId
     * @param code
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.FastDFS.GetFileByDictEntry, method = RequestMethod.GET)
    @ApiOperation(value = "根据系统字典id及系统字典项值获取结果集（logo）")
    public Envelop getFileByDictEntry(
            @ApiParam(name = "dictId", value = "字典ID", required = true)
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "code", value = "字典项代码", required = true)
            @RequestParam(value = "code") String code) throws Exception {
        Envelop envelop;
        StringBuffer dictEntryFilter = new StringBuffer();
        dictEntryFilter.append("dictId=" + dictId + ";");
        if (!org.apache.commons.lang3.StringUtils.isEmpty(code)) {
            dictEntryFilter.append("code=" + code + ";");
        }
        List<SystemDictEntry> page =  systemDictEntryService.search(dictEntryFilter.toString());
        if(page.size()>0){
            SystemDictEntry systemDictEntry=page.get(0);
            String filter="sn="+systemDictEntry.getValue()+";";
            List<Map<String, Object>> resultList = elasticSearchUtil.page(indexName, indexType, filter, 1, 1);
            Map<String, Object> map = new HashMap<>();
            if(null != resultList && resultList.size()>0){
                map = resultList.get(0);
                String path =fastDfsPublicServers+"/"+ map.get("path").toString().replace(":","/");
                map.put("path",path);
                resultList.remove(0);
                resultList.add(map);
            }
            int count = (int)elasticSearchUtil.count(indexName, indexType, filter);
            envelop = getPageResult(resultList, count, 1, 5);
        }else{
            envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("未设置字典项值！");
        }
        return envelop;
    }

}