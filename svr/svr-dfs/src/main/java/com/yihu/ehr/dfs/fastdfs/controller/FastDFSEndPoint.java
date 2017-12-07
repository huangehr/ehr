package com.yihu.ehr.dfs.fastdfs.controller;


import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dfs.es.service.ElasticSearchService;
import com.yihu.ehr.dfs.fastdfs.service.FastDFSService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/12/1.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "FastDFSEndPoint", description = "文件管理服务", tags = {"FastDFS-Service"})
public class FastDFSEndPoint extends EnvelopRestEndPoint {

    private static String INDEX = "dfs";

    @Autowired
    private FastDFSService fastDFSService;
    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
     * 上传文件，返回索引ID
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnId", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "图片上传")
    public Envelop fileUploadReturnId(
            @ApiParam(name = "jsonData", value = "文件资源")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        //FastDFS fileResource = toEntity(jsonData, FastDFS.class);
        //fileResource.setId(getObjectId(BizObject.FileResource));
        String _id = fastDFSService.fileUploadReturnId(paramMap);
        if(StringUtils.isEmpty(_id)) {
            envelop.setSuccessFlg(false);
            return envelop;
        }
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
    @RequestMapping(value = "/fastDfs/uploadReturnUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "上传文件返回url")
    public Envelop fileUploadReturnUrl(
            @ApiParam(name = "jsonData", value = "文件资源")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        //FastDFS fileResource = toEntity(jsonData, FastDFS.class);
        //fileResource.setId(getObjectId(BizObject.FileResource));
        String url =  fastDFSService.fileUploadReturnUrl(paramMap);
        if(StringUtils.isEmpty(url)) {
            envelop.setSuccessFlg(false);
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(url);
        return envelop;
    }

    /**
     * 上传文件，返回整个HttpUrl
     * @param jsonData
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/uploadReturnHttpUrl", method = RequestMethod.POST)
    @ApiOperation(value = "上传文件", notes = "上传文件返回url")
    public Envelop fileUploadReturnHttpUrl(
            @ApiParam(name = "json_data", value = "文件资源")
            @RequestBody String jsonData) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, String> paramMap = toEntity(jsonData, Map.class);
        //FastDFS fileResource = toEntity(jsonData, FastDFS.class);
        //fileResource.setId(getObjectId(BizObject.FileResource));
        String httpUrl =  fastDFSService.fileUploadReturnHttpUrl(paramMap);
        if(StringUtils.isEmpty(httpUrl)) {
            envelop.setSuccessFlg(false);
            return envelop;
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(httpUrl);
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
            @ApiParam(name = "id", value = "文件字符串")
            @RequestParam(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, Object> source = elasticSearchService.findById(INDEX, id);
        //List<FastDFS> fileResources = fastDFSService.findByObjectId(objectId);
        if(null == source) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("无相关文件资源");
            return envelop;
        }
        boolean success = fastDFSService.deleteFile(source, id);
        envelop.setSuccessFlg(success);
        return envelop;
    }

    /**
     * 删除资源表对应关系，并且删除fastDfs相对应当文件
     * @param storagePath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/deleteByPath", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByPath(
            @ApiParam(name = "storagePath", value = "文件路径")
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        Envelop envelop = new Envelop();
        String path = java.net.URLDecoder.decode(storagePath, "UTF-8");
        List<Map<String, Object>> resultList = elasticSearchService.findByField(INDEX, "path", path);
        for(Map<String, Object> resultMap : resultList) {
            fastDFSService.deleteFile(resultMap, resultMap.get("_id").toString());
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
    @RequestMapping(value = "/fastDfs/deleteByObjectId", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源表对应关系，并且删除fastDfs相对应文件")
    public Envelop deleteByObjectId(
            @ApiParam(name = "objectId", value = "对象ID")
            @RequestParam(value = "objectId") String objectId) throws Exception {
        Envelop envelop = new Envelop();
        //String path = java.net.URLDecoder.decode(storagePath, "UTF-8");
        List<Map<String, Object>> resultList = elasticSearchService.findByField(INDEX, "objectId", objectId);
        for(Map<String, Object> resultMap : resultList) {
            fastDFSService.deleteFile(resultMap, resultMap.get("_id").toString());
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
    @ApiOperation(value = "下载文件")
    public Envelop downloadById(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, Object> source = elasticSearchService.findById("dfs", id);
        String storagePath = source.get("path").toString();
        String groupName = storagePath.split(":")[0];
        String remoteFileName = storagePath.split(":")[1];
        byte[] bytes = fastDFSService.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        if(!StringUtils.isEmpty(fileStream)) {
            envelop.setSuccessFlg(true);
            envelop.setObj(fileStream);
        }
        return envelop;
    }

    /**
     * 下载文件
     * @param objectId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/getFilePath", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件路径")
    public Envelop getFilePath(
            @ApiParam(name = "objectId", value = "对象ID")
            @RequestParam(value = "objectId") String objectId) throws Exception {
        Envelop envelop = new Envelop();
        //List<FastDFS> fileResources = fastDFSService.findByObjectId(objectId);
        List<Map<String, Object>> resultList = elasticSearchService.findByField(INDEX, "objectId", objectId);
        List<String> fileStrList = new ArrayList<>();
        for (Map<String, Object> resultMap : resultList) {
            String storagePath = resultMap.get("path").toString();
            storagePath = URLEncoder.encode(storagePath,"ISO8859-1");
            fileStrList.add(storagePath);
        }
        envelop.setDetailModelList(fileStrList);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 下载文件
     * @param storagePath
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/fastDfs/downloadByPath", method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public Envelop downLoadByPath(
            @ApiParam(name = "storagePath", value = "文件路径")
            @RequestParam(value = "storagePath") String storagePath) throws Exception {
        Envelop envelop = new Envelop();
        String s = java.net.URLDecoder.decode(storagePath, "UTF-8");
        String groupName = s.split(":")[0];
        String remoteFileName = s.split(":")[1];
        byte[] bytes = fastDFSService.download(groupName, remoteFileName);
        String fileStream = new String(Base64.getEncoder().encode(bytes));
        envelop.setSuccessFlg(true);
        envelop.setObj(fileStream);
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
            @ApiParam(name = "page", value = "页码")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size") int size) throws Exception{
        Envelop envelop = new Envelop();
        List<Map<String, String>> filterMap;
        try {
            filterMap = objectMapper.readValue(filter, List.class);
        }catch (IOException e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        envelop.setDetailModelList(elasticSearchService.page(INDEX, filterMap, page, size));
        envelop.setSuccessFlg(true);
        return envelop;
    }

}