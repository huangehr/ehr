package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MCdaTransformDto;
import com.yihu.ehr.model.resource.MRsColumnsModel;
import com.yihu.ehr.resource.service.ResourceBrowseService;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "ResourceBrowseEndPoint", description = "数据浏览查询", tags = {"资源服务-数据浏览查询"})
public class ResourceBrowseEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceBrowseService resourceBrowseService;
    @Autowired
    private ResourcesTransformService resourcesTransformService;


    // ---------------------------------------------- 基础信息管理 Start---------------------------------

    @ApiOperation("资源数据源结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewMetadata, method = RequestMethod.GET)
    public List<MRsColumnsModel> getResourceMetadata(
            @ApiParam(name = "resourcesCode", value = "资源编码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "roleId", value = "角色id")
            @RequestParam(value = "roleId") String roleId) throws Exception {
        return resourceBrowseService.getResourceMetadata(resourcesCode, roleId);
    }

    @ApiOperation("资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewData, method = RequestMethod.GET)
    public Envelop getResourceData(
            @ApiParam(name = "resourcesCode", value = "资源编码")
            @RequestParam(value = "resourcesCode", defaultValue = "HDSA00_01") String resourcesCode,
            @ApiParam(name = "roleId", value = "角色id")
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name= "areaCode", value = "地区编码")
            @RequestParam(value = "areaCode") String areaCode,
            @ApiParam(name = "queryCondition", value = "查询条件")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几条")
            @RequestParam(value = "size", required = false) Integer size) throws Exception{
        Page<Map<String, Object>> result = resourceBrowseService.getResourceData(resourcesCode, roleId, orgCode, areaCode, queryCondition, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    @ApiOperation("档案资源浏览细表数据")
    @RequestMapping(value = ServiceApi.Resources.ResourceViewSubData, method = RequestMethod.GET)
    public Envelop findSubDateByRowKey(
            @ApiParam(name = "rowKey", value = "主表rowKey", required = true)
            @RequestParam(value = "rowKey") String rowKey,
            @ApiParam(name = "version", value = "版本", required = true)
            @RequestParam(value = "version") String version) throws Exception {
        List<Object> resultList = resourceBrowseService.getSubDateByRowkey(rowKey, version);
        return success(resultList);
    }

    // ---------------------------------------------- 基础信息管理 End---------------------------------

    /**
     * 获取资源数据
     */
    @ApiOperation("获取资源数据")
    @RequestMapping(value = ServiceApi.Resources.ResourceQuery, method = RequestMethod.POST)
    public Envelop getResources(
            @ApiParam(name = "resourcesCode", value = "资源编码")
            @RequestParam(value = "resourcesCode", defaultValue = "HDSA00_01") String resourcesCode,
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name= "areaCode", value = "地区编码")
            @RequestParam(value = "areaCode") String areaCode,
            @ApiParam(name = "queryParams", value = "json查询条件，{\"q\":\"*:*\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几条")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getResultData(resourcesCode, "*", orgCode, areaCode, queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    /**
     * （档案浏览器主要健康问题诊断详情）
     * @param resourcesCode
     * @param orgCode
     * @param areaCode
     * @param queryParams
     * @param page
     * @param size
     * @return
     * @throws Exception

    @ApiOperation("获取资源数据（档案浏览器主要健康问题诊断详情）")
    @RequestMapping(value = ServiceApi.Resources.ResourceSubQuery, method = RequestMethod.POST)
    public Envelop getResourcesSub(
            @ApiParam(name = "resourcesCode", value = "资源编码")
            @RequestParam(value = "resourcesCode", defaultValue = "HDSD00_13") String resourcesCode,
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name= "areaCode", value = "地区编码")
            @RequestParam(value = "areaCode") String areaCode,
            @ApiParam(name = "queryParams",value = "json查询条件，{\"q\":\"*:*\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几条")
            @RequestParam(value = "size", required = false) Integer size) {
        Envelop envelop = new Envelop();
        try {
            envelop = resourceBrowseService.getResourcesSub(resourcesCode, "*", orgCode, areaCode, queryParams, page, size);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }
     */


    /**
     * 获取资源数据(转译)
     */
    @ApiOperation("获取资源数据(转译)")
    @RequestMapping(value = ServiceApi.Resources.ResourceQueryTransform, method = RequestMethod.POST)
    public Envelop getResourcesTransform(
            @ApiParam(name = "resourcesCode", value = "资源代码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "roleId", value = "角色ID")
            @RequestParam(value = "roleId") String roleId,
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "areaCode", value = "地区编码")
            @RequestParam(value = "areaCode") String areaCode,
            @ApiParam(name = "queryParams", value = "json查询条件，{\"q\":\"*:*\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几条")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getResultData(resourcesCode, roleId , orgCode, areaCode, queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        if (version != null && version.length() > 0) {
            envelop.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(), version, resourcesCode));
        } else {
            envelop.setDetailModelList(result.getContent());
        }
        return envelop;
    }

    /**
     * 获取非结构化数据
     */
    @ApiOperation("获取非结构化数据")
    @RequestMapping(value = ServiceApi.Resources.ResourceRawFiles, method = RequestMethod.GET)
    public Envelop getRawFiles(
            @ApiParam(name = "profileId", value = "主表rowkey")
            @RequestParam(value = "profileId") String profileId,
            @ApiParam(name = "cdaDocumentId", value = "cda档案ID")
            @RequestParam(value = "cdaDocumentId", required = false) String cdaDocumentId,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几条")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getRawFiles(profileId, cdaDocumentId, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    @ApiOperation("获取非结构化数据list")
    @RequestMapping(value = ServiceApi.Resources.ResourceRawFilesList, method = RequestMethod.GET)
    public Map<String, Envelop> getRawFilesList(
            @ApiParam(name = "profileId", value = "主表rowkey")
            @RequestParam(value = "profileId") String profileId,
            @ApiParam(name = "cdaDocumentId", value = "cda模板ID")
            @RequestParam(value = "cdaDocumentId") String[] cdaDocumentIdList) throws Exception {

        Map<String, Envelop> map = new HashMap<>();
        for (int i = 0 ; i < cdaDocumentIdList.length; i++) {
            Page<Map<String, Object>> result = resourceBrowseService.getRawFiles(profileId, cdaDocumentIdList[i], null, null);
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setCurrPage(result.getNumber());
            envelop.setPageSize(result.getSize());
            envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
            envelop.setDetailModelList(result.getContent());
            map.put(cdaDocumentIdList[i], envelop);
        }
        return map;
    }

    @ApiOperation("Hbase主表")
    @RequestMapping(value = ServiceApi.Resources.ResourceMasterData, method = RequestMethod.GET)
    public Envelop getEhrCenter(
            @ApiParam(name = "queryParams", value = "检索条件", defaultValue = "{\"q\":\"*:*\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getEhrCenter(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        if (version != null && version.length() > 0) {
            envelop.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(), version,null));
        } else {
            envelop.setDetailModelList(result.getContent());
        }
        return envelop;
    }

    @ApiOperation("Hbase从表")
    @RequestMapping(value = ServiceApi.Resources.ResourceSubData, method = RequestMethod.GET)
    public Envelop getEhrCenterSub(
            @ApiParam(name = "queryParams", value = "检索条件", defaultValue = "{\"table\":\"HDSC02_17\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getEhrCenterSub(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        if (version != null && version.length() > 0) {
            envelop.setDetailModelList(resourcesTransformService.displayCodeConvert(result.getContent(), version,null));
        } else {
            envelop.setDetailModelList(result.getContent());
        }
        return envelop;
    }

    @ApiOperation("获取Cda Data")
    @RequestMapping(value = ServiceApi.Resources.getCDAData, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, Object> getCDAData(
            @ApiParam(name = "cdaTransformDtoJson", value = "资源数据模型", required = true)
            @RequestBody String cdaTransformDtoJson) throws Exception {
        MCdaTransformDto cdaTransformDto =  objectMapper.readValue(cdaTransformDtoJson, MCdaTransformDto.class);
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> event = cdaTransformDto.getMasterJson();
        if (null == event) {
            return result;
        }
        Map<String, List<String>> masterDataSetCodeMap = cdaTransformDto.getMasterDatasetCodeList();
        Map<String, List<String>> multiDataSetCodeMap = cdaTransformDto.getMultiDatasetCodeList();
        String profileId = event.get("rowkey").toString();
        String cdaVersion = event.get("cda_version").toString();
        for(String cdaId : masterDataSetCodeMap.keySet()) {
            Map<String, Object> data = new HashMap<>();
            List<String> masterDataSetList = masterDataSetCodeMap.get(cdaId);
            List<String> multiDataSetList = multiDataSetCodeMap.get(cdaId);
            for (int i = 0; i < masterDataSetList.size(); i++) {
                List<Map<String, Object>> dataList = new ArrayList<>();
                String dataSet = masterDataSetList.get(i);
                dataList.add(resourcesTransformService.stdMasterTransform(event, dataSet, cdaVersion));
                data.put(dataSet, dataList);
            }
            for (int i = 0; i < multiDataSetList.size(); i++) {
                String dataSet = multiDataSetList.get(i);
                String q = "{\"q\":\"rowkey:" + profileId + "$" + dataSet + "$*\"}";
                //String q = "{\"q\":\"profile_id:" + profileId + "\"}";
                Page<Map<String, Object>> page = resourceBrowseService.getEhrCenterSub(q, 1, 500);
                if (cdaVersion != null && cdaVersion.length() > 0) {
                    data.put(dataSet, resourcesTransformService.displayCodeConvert(page.getContent(), cdaVersion, dataSet));
                } else {
                    data.put(dataSet, page.getContent());
                }
            }
            result.put(cdaId, data);
        }
        return result;
    }


    @ApiOperation("Hbase主表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourceMasterStat, method = RequestMethod.GET)
    public Envelop countEhrCenter(
            @ApiParam(name = "queryParams", value = "检索条件", defaultValue = "{\"groupFields\":\"org_code\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.countEhrCenter(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    @ApiOperation("Hbase从表统计")
    @RequestMapping(value = ServiceApi.Resources.ResourceSubStat, method = RequestMethod.GET)
    public Envelop countEhrCenterSub(
            @ApiParam(name = "queryParams", value = "检索条件")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.countEhrCenterSub(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    @ApiOperation("Mysql查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMysql, method = RequestMethod.GET)
    public Envelop getMysqlData(
            @ApiParam(name = "queryParams", value = "检索条件")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        Page<Map<String, Object>> result = resourceBrowseService.getMysqlData(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

    @ApiOperation("获取solr索引列表")
    @RequestMapping(value = ServiceApi.Resources.SolrIndexData, method = RequestMethod.GET)
    public Envelop getSolrIndex(
            @ApiParam(name = "queryParams", value = "检索条件", defaultValue = "{\"q\":\"*:*\"}")
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @ApiParam(name = "page", value = "页数")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "分页大小")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "version", value = "版本")
            @RequestParam(value = "version", required = false) String version) throws Exception {
        Page<String> result = resourceBrowseService.getSolrIndexs(queryParams, page, size);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(result.getNumber());
        envelop.setPageSize(result.getSize());
        envelop.setTotalCount(new Long(result.getTotalElements()).intValue());
        envelop.setDetailModelList(result.getContent());
        return envelop;
    }

}
