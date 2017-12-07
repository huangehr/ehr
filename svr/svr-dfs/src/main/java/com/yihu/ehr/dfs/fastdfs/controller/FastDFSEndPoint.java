package com.yihu.ehr.dfs.fastdfs.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ApiVersion;
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
import org.csource.common.NameValuePair;
import org.csource.fastdfs.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
@Api(value = "FastDFSEndPoint", description = "文件管理服务", tags = {"FastDFS-Service"})
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
     * 上传文件，返回索引ID
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnId", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回索引id")
    public Envelop fileUploadReturnId(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
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
        //String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
        //保存到ES库中
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
                envelop.setObj("groupName:" + groupName + "-remoteFileName" + remoteFileName);
                return envelop;
            }
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        String _id = newSource.get("_id").toString();
        envelop.setSuccessFlg(true);
        envelop.setObj(_id);
        return envelop;
    }

    /**
     * 上传文件，返回URL
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回url")
    public Envelop fileUploadReturnUrl(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
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
        String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
        //保存到ES库中
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        try {
            elasticSearchService.index(indexName, indexType, source);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                fastDFSService.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                e1.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e1.getMessage());
                envelop.setObj("groupName:" + groupName + "-remoteFileName" + remoteFileName);
                return envelop;
            }
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(path);
        return envelop;
    }

    /**
     * 上传文件，返回整个HttpUrl
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnHttpUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "上传文件", notes = "返回httpUrl")
    public Envelop fileUploadReturnHttpUrl(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
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
        String path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);
        //保存到ES库中
        Map<String, Object> source = getIndexSource(objectNode, paramMap);
        try {
            elasticSearchService.index(indexName, indexType, source);
        }catch (Exception e) {
            e.printStackTrace();
            try {
                fastDFSService.delete(groupName, remoteFileName);
            }catch (Exception e1) {
                e1.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e1.getMessage());
                envelop.setObj("groupName:" + groupName + "-remoteFileName" + remoteFileName);
                return envelop;
            }
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        if(null == publicServer || publicServer.isEmpty()) {
            publicServer = getPublicUrl().getDetailModelList();
        }
        path = path.replace(":","/");
        String publicUrl = publicServer.get((int)(Math.random() * publicServer.size()));
        envelop.setSuccessFlg(true);
        envelop.setObj(publicUrl + "/" + path);
        return envelop;
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/deleteById", method = RequestMethod.DELETE)
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
    @RequestMapping(value = "/fastDfs/deleteByPath", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByPath(
            @ApiParam(name = "path", value = "文件路径", required = true)
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        //String path = java.net.URLDecoder.decode(storagePath, "UTF-8");
        List<Map<String, Object>> resultList = elasticSearchService.findByField(indexName, indexType,"path", path);
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
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/deleteByObjectId", method = RequestMethod.DELETE)
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
     * 修改文件信息
     * @param jsonData
     * @return
     * @throws Exception
    @RequestMapping(value = "/fastDfs/modify", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "上传文件返回url")
    public Envelop modify(
            @ApiParam(name = "jsonData", value = "文件资源")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
        //FastDFS fileResource = toEntity(jsonData, FastDFS.class);
        //fileResource.setId(getObjectId(BizObject.FileResource));
        int size = fastDFSService.modify(paramMap);
        envelop.setSuccessFlg(true);
        envelop.setObj(size);
        return envelop;
    }
     */

    /**
     * 修改文件信息
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/modify", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改文件")
    public Envelop modify(
            @ApiParam(name = "jsonData", value = "文件资源", required = true)
            @RequestBody String jsonData) {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData.substring(9), Map.class);
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
        String name = paramMap.get("name");
        if (!StringUtils.isEmpty(name)) {
            source.put("name", name);
        }
        source.put("modifier", modifier);
        source.put("path", groupName + ":" + remoteFileName);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        source.put("modifyDate", nowStr);
        Map<String, Object> resultMap = elasticSearchService.update(indexName, indexType, _id, source);
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
    @RequestMapping(value = "/fastDfs/fileInfo", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件信息")
    public Envelop getFileInfo(
            @ApiParam(name = "path", value = "路径", required = true)
            @RequestParam(value = "path") String path) {
        Envelop envelop = new Envelop();
        try {
            List<NameValuePair> resultList = fastDFSService.getMetadata(path.split(":")[0], path.split(":")[1]);
            envelop.setDetailModelList(resultList);
            FileInfo fileInfo = fastDFSService.getFileInfo(path.split(":")[0], path.split(":")[1]);
            envelop.setObj(fileInfo);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 下载文件
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/downloadById", method = RequestMethod.GET)
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
    @RequestMapping(value = "/fastDfs/downloadByPath", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byPath)")
    public Envelop downLoadByPath(
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
    @RequestMapping(value = "/fastDfs/downloadByObjectId", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件(byObjectId)")
    public Envelop downLoadByObjectId(
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
     * 获取文件下载路径
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/getFilePath", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件路径(byObjectId)")
    public Envelop getFilePath(
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
    @RequestMapping(value = "/fastDfs/page", method = RequestMethod.GET)
    @ApiOperation(value = "获取结果集")
    public Envelop page(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter") String filter,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true)
            @RequestParam(value = "size") int size) {
        Envelop envelop = new Envelop();
        List<Map<String, String>> filterMap;
        try {
            filterMap = objectMapper.readValue(filter, List.class);
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setDetailModelList(elasticSearchService.page(indexName, indexType, filterMap, page, size));
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取服务器状态信息
     * @return
     */
    @RequestMapping(value = "/fastDfs/status", method = RequestMethod.GET)
    @ApiOperation(value = "获取服务器状态信息")
    public Envelop status() {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> resultList;
        try {
            resultList = fastDFSService.status();
        }catch (IOException e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setDetailModelList(resultList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取外链地址
     * @return
     */
    @RequestMapping(value = "/fastDfs/getPublicUrl", method = RequestMethod.GET)
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
    @RequestMapping(value = "/fastDfs/setPublicUrl", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
        source.put("type", fileType);
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