package com.yihu.ehr.resource.controller.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MCdaTransformDto;
import com.yihu.ehr.resource.service.query.ResourcesQueryDao;
import com.yihu.ehr.resource.service.query.ResourcesQueryService;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "query", description = "资源查询服务接口")
public class ResourcesQueryEndPoint {
    @Autowired
    ResourcesQueryService resourcesQueryService;

    @Autowired
    ResourcesQueryDao resourcesQueryDao;

    @Autowired
    ResourcesTransformService resourcesTransformService;

    @Autowired
    private ObjectMapper objectMapper;

    /*********************************** 公开接口 ************************************************************/
    /**
     * 获取资源数据
     */
    @ApiOperation("获取资源数据")
    @RequestMapping(value = ServiceApi.Resources.ResourcesQuery, method = RequestMethod.POST)
    public Envelop getResources(@ApiParam("资源代码")
                                @RequestParam String resourcesCode,
                                @ApiParam("appId应用ID")
                                @RequestParam String appId,
                                @ApiParam("orgCode机构代码")
                                @RequestParam(required = false) String orgCode,
                                @ApiParam("json查询条件，{\"q\":\"*:*\"}")
                                @RequestParam(required = false) String queryParams,
                                @ApiParam("第几页")
                                @RequestParam(required = false) Integer page,
                                @ApiParam("每页几条")
                                @RequestParam(required = false) Integer size) throws Exception {

        return resourcesQueryService.getResources(resourcesCode, appId, orgCode, queryParams, page, size);
    }

    /**
     * 获取资源数据(转译)
     */
    @ApiOperation("获取资源数据(转译)")
    @RequestMapping(value = ServiceApi.Resources.ResourcesQueryTransform, method = RequestMethod.POST)
    public Envelop getResourcesTransform(@ApiParam("资源代码")
                                          @RequestParam String resourcesCode,
                                          @ApiParam(value = "appId应用ID",defaultValue = "JKZL")
                                          @RequestParam String appId,
                                          @ApiParam("orgCode机构代码")
                                          @RequestParam(required = false) String orgCode,
                                          @ApiParam("json查询条件，{\"q\":\"*:*\"}")
                                          @RequestParam(required = false) String queryParams,
                                          @ApiParam("第几页")
                                          @RequestParam(required = false) Integer page,
                                          @ApiParam("每页几条")
                                          @RequestParam(required = false) Integer size,
                                          @ApiParam("版本号")
                                          @RequestParam(required = false) String version) throws Exception {


        Envelop re = resourcesQueryService.getResources(resourcesCode, appId, orgCode, queryParams, page, size);
        if(version!=null && version.length()>0)
        {
            List<Map<String,Object>> list = re.getDetailModelList();
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(list,version,null));
        }

        return re;
    }

    /**
     * 资源数据源结构
     */
    @ApiOperation("资源数据源结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewMetadata, method = RequestMethod.GET)
    public String getResourceMetadata(@ApiParam("resourcesCode") @RequestParam(value = "resourcesCode", required = true) String resourcesCode) throws Exception {
        return resourcesQueryService.getResourceMetadata(resourcesCode);
    }

    /**
     * 资源浏览
     */
    @ApiOperation("资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    public Envelop getResourceData(@ApiParam(name = "resourcesCode", defaultValue = "RS_HOSPITALIZED_DIAGNOSIS") @RequestParam String resourcesCode,
                                   @ApiParam(name = "机构代码") @RequestParam String orgCode,
                                   @ApiParam("查询条件") @RequestParam(value = "queryCondition", required = false) String queryCondition,
                                   @ApiParam("第几页") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("每页几条") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesQueryService.getResourceData(resourcesCode,orgCode, queryCondition, page, size);
    }

    /**
     * 获取非结构化数据
     */
    @ApiOperation("获取非结构化数据")
    @RequestMapping(value = ServiceApi.Resources.ResourcesRawFiles, method = RequestMethod.GET)
    public Envelop getRawFiles(@ApiParam("profileId") @RequestParam(value = "profileId", required = true) String profileId,
                               @ApiParam("cdaDocumentId") @RequestParam(value = "cdaDocumentId", required = false) String cdaDocumentId,
                                @ApiParam("第几页") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("每页几条") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return resourcesQueryService.getRawFiles(profileId,cdaDocumentId,page,size);
    }


    @ApiOperation("获取非结构化数据list")
    @RequestMapping(value = ServiceApi.Resources.ResourcesRawFilesList, method = RequestMethod.GET)
    public Map<String,Envelop> getRawFilesList(@RequestParam(value = "profileId", required = true) String profileId,
                               @RequestParam(value = "cdaDocumentId", required = true) String[] cdaDocumentIdList) throws Exception {

        Map<String,Envelop> map=new HashMap<>();
        for(int i=0;i<cdaDocumentIdList.length;i++)
             map.put(cdaDocumentIdList[i],resourcesQueryService.getRawFiles(profileId,cdaDocumentIdList[i],null,null));
        return map;
    }

    @ApiOperation("Hbase主表")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMasterData, method = RequestMethod.GET)
    public Envelop getEhrCenter(@ApiParam(name = "queryParams", defaultValue = "{\"q\":\"*:*\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
                                @ApiParam("version") @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenter(queryParams, page, size);
        Envelop re = new Envelop();
        re.setSuccessFlg(true);
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        if(version!=null && version.length()>0)
        {
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(),version,null));
        }
        else{
            re.setDetailModelList(result.getContent());
        }
        return re;
    }

    @ApiOperation("Hbase从表")
    @RequestMapping(value = ServiceApi.Resources.ResourcesSubData, method = RequestMethod.GET)
    public Envelop getEhrCenterSub(
            @ApiParam(name = "queryParams", defaultValue = "{\"table\":\"HDSC02_17\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size,
            @ApiParam("version") @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenterSub(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        if(version!=null && version.length()>0)
        {
            re.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(),version,null));
        }
        else{
            re.setDetailModelList(result.getContent());
        }
        return re;
    }

    @ApiOperation("获取Cda Data")
    @RequestMapping(value = ServiceApi.Resources.getCDAData, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String,Object> getCDAData( @ApiParam(name="cdaTransformDtoJson",value="资源数据模型",required = true)
                                           @RequestBody String cdaTransformDtoJson) throws Exception {
        MCdaTransformDto cdaTransformDto =  objectMapper.readValue(cdaTransformDtoJson,MCdaTransformDto.class);

        Map<String,Object> re = new HashMap<>();
        Map<String, Object> obj = cdaTransformDto.getMasterJson();
        Map<String, List<String>> masterDatasetCodeMap = cdaTransformDto.getMasterDatasetCodeList();
        Map<String, List<String>> multiDatasetCodeMap = cdaTransformDto.getMultiDatasetCodeList();
        String profileId = obj.get("rowkey").toString();
        String cdaVersion = obj.get("cda_version").toString();
        for(String key:masterDatasetCodeMap.keySet()) {
            Map<String,Object> map=new HashMap<>();
            List<String> masterDatasetList = masterDatasetCodeMap.get(key);
            List<String> multiDatasetList = multiDatasetCodeMap.get(key);

            for (int i = 0; i < masterDatasetList.size(); i++) {
                List<Map<String, Object>> dataList = new ArrayList<>();
                String dataset = masterDatasetList.get(i);
                dataList.add(resourcesTransformService.stdMasterTransform(obj, dataset, cdaVersion));
                map.put(dataset, dataList);
            }
            for (int i = 0; i < multiDatasetList.size(); i++) {
                String dataset = multiDatasetList.get(i);
                String q = "{\"table\":\"" + dataset + "\",\"q\":\"profile_id:" + profileId + "\"}";
                Page<Map<String, Object>> result = resourcesQueryDao.getEhrCenterSub(q, null, null);

                if (cdaVersion != null && cdaVersion.length() > 0) {
                    map.put(dataset, resourcesTransformService.displayCodeConvert(result.getContent(), cdaVersion, null));
                } else {
                    map.put(dataset, result.getContent());
                }
            }
            re.put(key,map);
        }
        return re;
    }


    @ApiOperation("Hbase主表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMasterStat, method = RequestMethod.GET)
    public Envelop countEhrCenter(@ApiParam(value = "queryParams", defaultValue = "{\"groupFields\":\"org_code,HDSD00_01_003_VALUE_S,HDSA00_01_015_VALUE_S\"}") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.countEhrCenter(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }

    @ApiOperation("Hbase从表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourcesSubStat, method = RequestMethod.GET)
    public Envelop countEhrCenterSub(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                       @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                       @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.countEhrCenterSub(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }

    @ApiOperation("Mysql查询")
    @RequestMapping(value = ServiceApi.Resources.ResourcesMysql, method = RequestMethod.GET)
    public Envelop getMysqlData(@ApiParam("queryParams") @RequestParam(value = "queryParams", required = false) String queryParams,
                                                  @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                                  @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourcesQueryDao.getMysqlData(queryParams, page, size);
        Envelop re = new Envelop();
        re.setCurrPage(result.getNumber());
        re.setPageSize(result.getSize());
        re.setTotalCount(new Long(result.getTotalElements()).intValue());
        re.setDetailModelList(result.getContent());
        return re;
    }


}
