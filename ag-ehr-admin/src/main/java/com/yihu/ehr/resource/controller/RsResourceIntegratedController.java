package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.quota.service.TjQuotaSynthesizeQueryClient;
import com.yihu.ehr.resource.client.RsResourceIntegratedClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;


/**
 * @Created by Sxy on 2017/08/01.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "ResourceIntegrated", description = "资源综合查询数据服务接口", tags = {"资源管理-资源综合查询数据服务接口"})
public class RsResourceIntegratedController extends BaseController {

    @Autowired
    private RsResourceIntegratedClient resourcesIntegratedClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private TjQuotaSynthesizeQueryClient tjQuotaSynthesizeQueryClient;
    @Autowired
    private TjQuotaJobClient tjQuotaJobClient;

    @ApiOperation("综合查询档案数据列表树")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    public Envelop getMetadataList(
            @ApiParam(name="filters",value="过滤条件(name)",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) {
        return resourcesIntegratedClient.getMetadataList(filters);
    }

    @ApiOperation("综合查询档案数据检索")
    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    public Envelop searchMetadataData(
            @ApiParam(name = "resourcesCode", value = "资源代码([\"code\"])")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metaData", value = "数据元([\"metadataId\"])")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构代码(orgCode)")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "appId", value = "应用ID(appId)")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "queryCondition", value = "查询条件([{\"andOr\":\"(AND)(OR)\",\"condition\":\"(<)(=)(>)\",\"field\":\"fieldName\",\"value\":\"value\"}])")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页(>0)")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行(>0)")
            @RequestParam(value = "size", required = false) Integer size) throws  Exception {
        return resourcesIntegratedClient.searchMetadataData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
    }

    @ApiOperation("综合查询指标统计列表树")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    public Envelop getStatisticsList(
            @ApiParam(name="filters",value="过滤条件(name)",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        return resourcesIntegratedClient.getQuotaList(filters);
    }

    @ApiOperation("综合查询指标统计数据检索")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaData, method = RequestMethod.GET)
    public Envelop getStatisticsData(
            @ApiParam(name="quotaIds", value = "指标ID列表(id1,id2,id3,...)", defaultValue = "")
            @RequestParam(name = "quotaIds") String quotaIds,
            @ApiParam(name="quotaCodes", value = "指标code列表(code1,code2,code3,...)", defaultValue = "")
            @RequestParam(name = "quotaCodes") String quotaCodes,
            @ApiParam(name="queryCondition", value="搜索条件({})",defaultValue = "")
            @RequestParam(value="queryCondition", required = false) String queryCondition) {
        Envelop envelop = new Envelop();
        String [] quotaIdArr = quotaIds.split(",");
        String [] quotaCodeArr = quotaCodes.split(",");
        String dimension = "";
        List<Map<String, String>> qsdList = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimension(quotaCodes);
        if(qsdList == null || qsdList.size() <= 0) {
            envelop.setSuccessFlg(true);
            return envelop;
        }
        List<Map<String, String>> objList = new ArrayList<Map<String, String>>();
        for(Map<String, String> temp : qsdList) {
            for(String codeStr : temp.keySet()){
                if(quotaCodes.contains(codeStr)) {
                    //添加键值对应列表
                    Map<String, String> objMap = new HashMap<String, String>();
                    objMap.put("key", temp.get(codeStr));
                    objMap.put("name", temp.get("name"));
                    objList.add(objMap);
                    //结果总量参数
                    dimension += temp.get(codeStr) + ";";
                    break;
                }
            }
        }
        //依次获取指标统计不同维度结果总量
        List<Envelop>  envelopList = new ArrayList<Envelop>();
        for(String id : quotaIdArr) {
            //添加键值对应列表
            MTjQuotaModel tjQuotaModel = tjQuotaClient.getById(new Long(id));
            Map<String, String> objMap = new HashMap<String, String>();
            objMap.put("key", tjQuotaModel.getCode());
            objMap.put("name", tjQuotaModel.getName());
            objList.add(objMap);
            Envelop tempEnvelop = tjQuotaJobClient.getQuotaTotalCount(new Integer(id), queryCondition, dimension.substring(0, dimension.length() - 1));
            envelopList.add(tempEnvelop);
        }
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        //遍历数据集，拼装结果集
        for(int i = 0; i < envelopList.size(); i ++ ) {
            Envelop envelop1 = envelopList.get(i);
            if(envelop1.getDetailModelList() != null) {
                //遍历当前数据
                for (Map<String, Object> tempMap1 : (List<Map<String, Object>>) envelop1.getDetailModelList()) {
                    //判断是否已记录数据
                    boolean isRecode = false;
                    for (Map<String, Object> resultMap : resultList) {
                        if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) resultMap.get("cloumns")).toArray())) {
                            isRecode = true;
                        }
                    }
                    //未记录的数据
                    if (!isRecode) {
                        Map<String, Object> newMap = new HashMap<String, Object>();
                        //初始化基本列名
                        newMap.put("cloumns", tempMap1.get("cloumns"));
                        //初始化为空数据
                        for (int p = 0; p < i; p++) {
                            newMap.put(quotaCodeArr[p], 0);
                        }
                        //当数据为最后一个数据集中的一个时
                        if ((envelopList.size() - 1) == i) {
                            newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                        } else {
                            //与其他数据集进行对比
                            for (int j = i + 1; j < envelopList.size(); j++) {
                                //判断是否匹配
                                boolean isMatch = false;
                                Envelop envelop2 = envelopList.get(j);
                                for (Map<String, Object> tempMap2 : (List<Map<String, Object>>) envelop2.getDetailModelList()) {
                                    if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) tempMap2.get("cloumns")).toArray())) {
                                        newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                                        newMap.put(quotaCodeArr[j], tempMap2.get("value"));
                                        isMatch = true;
                                    }
                                }
                                //未匹配到数据
                                if (!isMatch) {
                                    newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                                    newMap.put(quotaCodeArr[j], 0);
                                }
                            }
                        }
                        resultList.add(newMap);
                    }
                }
            }
        }
        List<Map<String, Object>> finalList = new ArrayList<Map<String, Object>>();
        String [] dimensionArr = dimension.split(";");
        for(Map<String, Object> tempMap : resultList) {
            List<String> colList = (List<String>)tempMap.get("cloumns");
            Map<String, Object> finalMap = new HashMap<String, Object>();
            for(int i = 0; i < colList.size(); i++) {
                finalMap.put(dimensionArr[i], colList.get(i));
            }
            for(String key : tempMap.keySet()) {
                if(!key.equals("cloumns")) {
                    finalMap.put(key, tempMap.get(key));
                }
            }
            finalList.add(finalMap);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(finalList);
        envelop.setObj(objList);
        if(resultList != null) {
            envelop.setTotalCount(resultList.size());
        }
        return envelop;
    }

    @ApiOperation("综合查询指标统计数据检索条件获取")
    @RequestMapping(value = ServiceApi.Resources.IntQuotaParam, method = RequestMethod.GET)
    public Envelop getStatisticsParam(
            @ApiParam(name = "quotaCodes", value = "指标code列表(code1,code2,code3,...)")
            @RequestParam(value = "quotaCodes") String quotaCodes){
        Envelop envelop = new Envelop();
        String dimensions = "";
        List<Map<String, String>> qsdList = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimension(quotaCodes);
        if(qsdList == null || qsdList.size() <= 0) {
            envelop.setSuccessFlg(true);
            return envelop;
        }
        for(Map<String, String> temp : qsdList) {
            for(String codeStr : temp.keySet()){
                if(quotaCodes.contains(codeStr)) {
                    //交集维度参数
                    dimensions += temp.get(codeStr) + ",";
                    break;
                }
            }
        }
        String quotaCode = quotaCodes.split(",")[0];
        Map<String, Map<String, Object>> dataMap = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimensionKeyVal(quotaCode, dimensions);
        if (dataMap != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(dataMap);
        }
        return envelop;
    }

    @ApiOperation("综合查询视图保存")
    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    public Envelop updateResource(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resource\":\"objStr\",\"(metadatas)(quotas)\":\"[objStr]\",\"queryCondition\":\"([])({})\"})")
            @RequestParam(value="dataJson") String dataJson) {
        return resourcesIntegratedClient.updateResource(dataJson);
    }

    @ApiOperation("综合查询搜索条件更新")
    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    public Envelop updateResourceQuery(
            @ApiParam(name="dataJson",value="JSON对象参数({\"resourceId\":\"resourceId\",\"queryCondition\":\"([])({})\"})")
            @RequestParam(value="dataJson") String dataJson) {
        return resourcesIntegratedClient.updateResourceQuery(dataJson);
    }
}
