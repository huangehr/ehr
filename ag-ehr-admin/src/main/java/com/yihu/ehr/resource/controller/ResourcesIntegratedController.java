package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.resource.client.ResourcesIntegratedClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.ls.LSInput;

import java.util.*;


/**
 * @author Sxy
 * @created 2016.08.01 17:46
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceIntegrated", description = "资源综合查询数据服务接口", tags = {"资源管理-资源综合查询数据服务接口"})
public class ResourcesIntegratedController extends BaseController {

    @Autowired
    private ResourcesIntegratedClient resourcesIntegratedClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private TjQuotaJobClient tjQuotaJobClient;

    @RequestMapping(value = ServiceApi.Resources.IntMetadataList, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据列表树")
    public Envelop getMetadataList(
            @ApiParam(name="filters",value="过滤条件",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesIntegratedClient.getMetadataList(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
            if(resources != null) {
                envelop.setTotalCount(resources.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntMetadataData, method = RequestMethod.GET)
    @ApiOperation("综合查询档案数据检索")
    public Envelop searchMetadataData(
            @ApiParam(name = "resourcesCode", value = "资源代码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metaData", value = "数据元")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "queryCondition", value = "查询条件")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> resources = resourcesIntegratedClient.searchMetadataData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resources);
            if(resources != null) {
                envelop.setTotalCount(resources.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntQuotaList, method = RequestMethod.GET)
    @ApiOperation("综合查询指标统计列表树")
    public Envelop getStatisticsList(
            @ApiParam(name="filters",value="过滤条件",defaultValue = "")
            @RequestParam(value="filters",required = false) String filters) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<Map<String,Object>> quotas = resourcesIntegratedClient.getQuotaList(filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(quotas);
            if(quotas != null) {
                envelop.setTotalCount(quotas.size());
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntQuotaData, method = RequestMethod.GET)
    @ApiOperation("综合查询指标统计数据检索")
    public Envelop getStatisticsData(
            @ApiParam(name="quotaIds", value = "指标ID列表", defaultValue = "")
            @RequestParam(name = "quotaIds") String quotaIds,
            @ApiParam(name="quotaCodes", value = "指标code列表", defaultValue = "")
            @RequestParam(name = "quotaCodes") String quotaCodes,
            @ApiParam(name="queryCondition", value="搜索条件",defaultValue = "")
            @RequestParam(value="queryCondition", required = false) String queryCondition) throws  Exception {
        Envelop envelop = new Envelop();
        String [] quotaIdArr = quotaIds.split(",");
        String [] quotaCodeArr = quotaCodes.split(",");
        try {
            String dimension = "";
            List<Map<String, String>> qsdList = tjQuotaClient.getTjQuotaSynthesiseDimension(quotaCodes);
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
            List<String> emptyList = new ArrayList<String>();
            //遍历数据集，拼装结果集
            for(int i = 0; i < envelopList.size(); i ++ ) {
                Envelop envelop1 = envelopList.get(i);
                if(envelop1.getDetailModelList().size() > 0) {
                    //遍历当前数据集内的数据
                    for (Map<String, Object> tempMap1 : (List<Map<String, Object>>) envelop1.getDetailModelList()) {
                        Map<String, Object> newMap = new HashMap<String, Object>();
                        //当记录为最后一个数据集的时候
                        if ((envelopList.size() - 1) == i) {
                            boolean isInResult = false;
                            for (Map<String, Object> tempMap4 : resultList) {
                                if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) tempMap4.get("cloumns")).toArray())) {
                                    isInResult = true;
                                }
                            }
                            if (!isInResult) {
                                newMap.put("cloumns", tempMap1.get("cloumns"));
                                for (int j = 0; j < quotaCodeArr.length; j++) {
                                    if (i == j) {
                                        newMap.put(quotaCodeArr[j], tempMap1.get("value"));
                                    } else {
                                        newMap.put(quotaCodeArr[j], 0);
                                    }
                                }
                            }
                        } else {
                            for (int j = i + 1; j < envelopList.size(); j++) {
                                //是否匹配
                                boolean isMatch = false;
                                //时候已记录数据
                                boolean isRecode = false;
                                //与其他结果集进行对比
                                Envelop envelop2 = envelopList.get(j);
                                if(envelop2.getDetailModelList().size() > 0) {
                                    for (Map<String, Object> tempMap2 : (List<Map<String, Object>>) envelop2.getDetailModelList()) {
                                        if (resultList.size() > 0) {
                                            for (Map<String, Object> tempMap3 : resultList) {
                                                if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) tempMap3.get("cloumns")).toArray())) {
                                                    isRecode = true;
                                                }
                                            }
                                            if (!isRecode) {
                                                if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) tempMap2.get("cloumns")).toArray())) {
                                                    newMap.put("cloumns", tempMap1.get("cloumns"));
                                                    newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                                                    newMap.put(quotaCodeArr[j], tempMap2.get("value"));
                                                    for(int p = 0; p < i; p ++) {
                                                        newMap.put(quotaCodeArr[p], 0);
                                                    }
                                                    isMatch = true;
                                                }
                                            }
                                        } else {
                                            if (Arrays.equals(((List<String>) tempMap1.get("cloumns")).toArray(), ((List<String>) tempMap2.get("cloumns")).toArray())) {
                                                newMap.put("cloumns", tempMap1.get("cloumns"));
                                                    newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                                                    newMap.put(quotaCodeArr[j], tempMap2.get("value"));
                                                for(String tempCode : emptyList) {
                                                    newMap.put(tempCode, 0);
                                                }
                                                isMatch = true;
                                            }
                                        }
                                    }
                                    if (!isRecode && !isMatch) {
                                        newMap.put("cloumns", tempMap1.get("cloumns"));
                                        newMap.put(quotaCodeArr[i], tempMap1.get("value"));
                                        newMap.put(quotaCodeArr[j], 0);
                                        for(int p = 0; p < i; p ++) {
                                            newMap.put(quotaCodeArr[p], 0);
                                        }
                                    }
                                }else {
                                    if(!emptyList.contains(quotaCodeArr[j])) {
                                        emptyList.add(quotaCodeArr[j]);
                                    }
                                    if(emptyList.size() > 0) {
                                        for (Map<String, Object> tempMap : resultList) {
                                            for (String tempCode : emptyList) {
                                                tempMap.put(tempCode, 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (newMap.size() > 0) {
                            resultList.add(newMap);
                        }
                    }
                }else {
                    if(!emptyList.contains(quotaCodeArr[i])) {
                        emptyList.add(quotaCodeArr[i]);
                    }
                    if(emptyList.size() > 0) {
                        for (Map<String, Object> tempMap : resultList) {
                            for (String tempCode : emptyList) {
                                tempMap.put(tempCode, 0);
                            }
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
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceUpdate, method = RequestMethod.POST)
    @ApiOperation("综合查询视图保存")
    public Envelop updateResource(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            envelop = resourcesIntegratedClient.updateResource(dataJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.IntResourceQueryUpdate, method = RequestMethod.PUT)
    @ApiOperation("综合查询搜索条件更新")
    public Envelop updateResourceQuery(
            @ApiParam(name="dataJson",value="JSON对象参数")
            @RequestParam(value="dataJson") String dataJson) throws  Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            envelop = resourcesIntegratedClient.updateResourceQuery(dataJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return envelop;
    }



}
