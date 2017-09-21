package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.agModel.resource.RsBrowseModel;
import com.yihu.ehr.agModel.resource.RsCategoryTypeTreeModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.quota.service.TjQuotaSynthesizeQueryClient;
import com.yihu.ehr.resource.client.*;
import com.yihu.ehr.users.service.GetInfoClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by wq on 2016/5/30.
 */

@RestController
@Api(value = "ResourceBrowse", description = "资源浏览", tags = {"资源管理-资源浏览"})
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
public class ResourceBrowseController extends BaseController {

    @Autowired
    private RsResourceClient resourcesClient;
    @Autowired
    private RsResourceCategoryClient resourcesCategoryClient;
    @Autowired
    private ResourceBrowseClient resourceBrowseClient;
    @Autowired
    private RsResourceQuotaClient resourceQuotaClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private TjQuotaJobClient tjQuotaJobClient;
    @Autowired
    private TjQuotaSynthesizeQueryClient tjQuotaSynthesizeQueryClient;
    @Autowired
    private RsResourceDefaultQueryClient rsResourceDefaultQueryClient;
    @Autowired
    private GetInfoClient getInfoClient;
    @Autowired
    private AddressClient addressClient;
    @Autowired
    OrganizationClient organizationClient;

    @ApiOperation("获取档案资源分类")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseCategories, method = RequestMethod.GET)
    public Envelop getCategories(
            @ApiParam(name = "id", value = "返回字段", defaultValue = "")
            @RequestParam(value = "id", required = false) String id) throws Exception {
        Envelop envelop = new Envelop();
        List<RsCategoryTypeTreeModel> rsCategoryTypeTreeModelList = new ArrayList<>();
        List<MRsResources> rsResources = new ArrayList<>();
        try {
            List<MRsCategory> rsCategories = new ArrayList<>();
            //查询资源分类
            List<MRsCategory> resources = resourcesCategoryClient.getAllCategories("");
            for (MRsCategory mRsCategory:resources){

                RsCategoryTypeTreeModel rsCategoryModel = new RsCategoryTypeTreeModel();
                rsCategoryModel.setId(mRsCategory.getId());
                rsCategoryModel.setPid(mRsCategory.getPid());
                rsCategoryModel.setName(mRsCategory.getName());
                rsCategoryTypeTreeModelList.add(rsCategoryModel);

                ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", "categoryId=" + mRsCategory.getId(), "", 1, 999);// TODO: 2016/5/30 测试数据15（无不分页查询）
                rsResources = categoryResponseEntity.getBody();
                //rsResources = resourcesClient.queryNoPageResources("categoryId=" + mRsCategory.getId());
                if (rsResources.size() > 0) {
                    List<RsResourcesModel> resourcesModelList = (List<RsResourcesModel>) convertToModels(rsResources, new ArrayList<RsResourcesModel>(rsResources.size()), RsResourcesModel.class, null);
                    for (RsResourcesModel resourcesModel : resourcesModelList) {
                        RsCategoryTypeTreeModel rsCategoryTypeModel = new RsCategoryTypeTreeModel();
                        rsCategoryTypeModel.setId(resourcesModel.getId());
                        rsCategoryTypeModel.setPid(mRsCategory.getId());
                        rsCategoryTypeModel.setResourceIds(resourcesModel.getId());
                        rsCategoryTypeModel.setName(resourcesModel.getName());
                        rsCategoryTypeModel.setResourceCode(resourcesModel.getCode());
                        rsCategoryTypeTreeModelList.add(rsCategoryTypeModel);
                    }
                }
            }

            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(rsCategoryTypeTreeModelList);

        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取档案资源分类失败");
        }
        return envelop;
    }

    @ApiOperation("档案资源数据元结构")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseResourceMetadata, method = RequestMethod.GET)
    public Envelop getResourceData(
            @ApiParam("resourcesCode")
            @RequestParam(value = "resourcesCode", required = true) String resourcesCode) {
        Envelop envelop = new Envelop();
        List<RsBrowseModel> rsBrowseModelList = new ArrayList<>();
        String resourceMetadata = resourceBrowseClient.getResourceMetadata(resourcesCode);
        RsBrowseModel resourceMetadataModel = toEntity(resourceMetadata, RsBrowseModel.class);
        List<String> code = resourceMetadataModel.getColunmCode();
        List<String> value = resourceMetadataModel.getColunmName();
        List<String> type = resourceMetadataModel.getColunmType();
        List<String> dict = resourceMetadataModel.getColunmDict();
        for (int i = 0; i < code.size(); i++) {
            RsBrowseModel rsBrowseModel = new RsBrowseModel();
            rsBrowseModel.setCode(code.get(i));
            rsBrowseModel.setValue(value.get(i));
            rsBrowseModel.setType(type.get(i));
            rsBrowseModel.setDict(dict.get(i));
            rsBrowseModelList.add(rsBrowseModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rsBrowseModelList);
        return envelop;
    }

    @ApiOperation("档案资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseResourceData, method = RequestMethod.GET)
    public Envelop getResourceData(
            @ApiParam("资源代码")
            @RequestParam String resourcesCode,
            @ApiParam("机构代码")
            @RequestParam String orgCode,
            @ApiParam("查询条件")
            @RequestParam(required = false) String queryCondition,
            @ApiParam("第几页")
            @RequestParam(required = false) Integer page,
            @ApiParam("每页几行")
            @RequestParam(required = false) Integer size) throws Exception {

        Envelop categoryResponseEntity = resourceBrowseClient.getResourceData(resourcesCode, orgCode, queryCondition, page, size);
        return categoryResponseEntity;
    }

    @ApiOperation("指标资源浏览")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseQuotaResourceData, method = RequestMethod.GET)
    public Envelop getQuotaResourceData(
            @ApiParam("资源Id")
            @RequestParam String resourcesId,
            @ApiParam("机构代码(预留参数)")
            @RequestParam(required = false) String orgCode,
            @ApiParam("查询条件")
            @RequestParam(required = false) String queryCondition,
            @ApiParam(name = "userId" ,value = "用户ID" )
            @RequestParam(value = "userId" , required = false) String userId) throws Exception {
        Envelop envelop = new Envelop();
        String [] quotaCodeArr = null;
        try {
            //获取资源关联指标
            List<ResourceQuotaModel> rqmList = resourceQuotaClient.getByResourceId(resourcesId);
            if(rqmList == null || rqmList.size() <= 0) {
                envelop.setErrorMsg("关联指标为空");
                return envelop;
            }
            //获取资源默认查询条件
            String query = rsResourceDefaultQueryClient.getByResourceId(resourcesId);
            //拼接指标code字符串作为维度交集查询参数
            String quotaCodes = "";
            quotaCodeArr = new String [rqmList.size()];
            List<Map<String, String>> objList = new ArrayList<Map<String, String>>();
            for (int i = 0; i< rqmList.size(); i ++) {
                ResourceQuotaModel resourceQuotaModel = rqmList.get(i);
                MTjQuotaModel tjQuotaModel = tjQuotaClient.getById((long) resourceQuotaModel.getQuotaId());
                quotaCodeArr[i] = tjQuotaModel.getCode();
                quotaCodes += tjQuotaModel.getCode() + ",";
            }
            //拼接交集维度字符串作为查询参数
            String dimension = "";
            if(StringUtils.isEmpty(quotaCodes)) {
                envelop.setErrorMsg("指标编码有误");
                return envelop;
            }
            List<Map<String, String>> qsdList = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimension(quotaCodes.substring(0, quotaCodes.length() - 1));
            if(qsdList == null || qsdList.size() <= 0) {
                envelop.setSuccessFlg(true);
                return envelop;
            }
            for (Map<String, String> temp : qsdList) {
                for (String codeStr : temp.keySet()) {
                    if (quotaCodes.contains(codeStr)) {
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
            for (int i = 0; i< rqmList.size(); i ++) {
                ResourceQuotaModel resourceQuotaModel = rqmList.get(i);
                MTjQuotaModel tjQuotaModel = tjQuotaClient.getById((long) resourceQuotaModel.getQuotaId());
                Map<String, String> objMap = new HashMap<String, String>();
                objMap.put("key", tjQuotaModel.getCode());
                objMap.put("name", tjQuotaModel.getName());
                objList.add(objMap);
            }
            //依次获取指标统计不同维度结果总量
            List<Envelop> envelopList = new ArrayList<Envelop>();
            for (ResourceQuotaModel resourceQuotaModel : rqmList) {
                Envelop envelop1;
                //-----------------用户数据权限 start
                String org = "";
                Map<String, String> orgMap = new HashMap<>();
                //获取用户所拥有的  带saaa权限
                List<String> orgList = getInfoClient.getOrgCode(userId);
                if(orgList != null && orgList.size() > 0){
                    for(String orgcode : orgList){
                        orgMap.put(orgcode,orgcode);
                    }
                }
                //获取用户所拥有的区域   带saaa权限
                Map<String,String> param = new HashMap<>();
                List<String> districtList = getInfoClient.getUserDistrictCode(userId);
                if(districtList != null && districtList.size() > 0){
                    for(String code : districtList){
                        MGeographyDict mGeographyDict = addressClient.getAddressDictById(code);
                        if(mGeographyDict != null){
                            String province = "";
                            String city = "";
                            String district = "";
                            if(mGeographyDict.getLevel() == 1){
                                province =  mGeographyDict.getName();
                            }else if(mGeographyDict.getLevel() == 2){
                                city =  mGeographyDict.getName();
                            }else if(mGeographyDict.getLevel() == 3){
                                district =  mGeographyDict.getName();
                            }
                            Collection<MOrganization> organizations = organizationClient.getOrgsByAddress(province,city ,district );
                            if(organizations !=null ){
                                java.util.Iterator it = organizations.iterator();
                                while(it.hasNext()){
                                    MOrganization mOrganization = (MOrganization)it.next();
                                    orgMap.put(mOrganization.getCode(),mOrganization.getCode());
                                }
                            }
                        }
                    }
                }
                if(orgMap != null){
                    for(String key :orgMap.keySet()){
                        if(!StringUtils.isEmpty(key))
                            org =   org + key + ",";
                    }
                }
                //-----------------用户数据权限 end
                //判断是否启用默认查询条件
                if (queryCondition == null || queryCondition.equals("{}")) {
                    if(org.length()>0){
                        Map<String, Object> params  = objectMapper.readValue(query, new TypeReference<Map>() {});
                        params.put("org",org.substring(0,org.length()-1));
                        query = objectMapper.writeValueAsString(params);
                    }
                    envelop1 = tjQuotaJobClient.getQuotaTotalCount(resourceQuotaModel.getQuotaId(), query, dimension.substring(0, dimension.length() - 1));
                    envelopList.add(envelop1);
                } else {
                    if(org.length()>0){
                        Map<String, Object> params  = objectMapper.readValue(queryCondition, new TypeReference<Map>() {});
                        params.put("org",org.substring(0,org.length()-1));
                        queryCondition = objectMapper.writeValueAsString(params);
                    }
                    envelop1 = tjQuotaJobClient.getQuotaTotalCount(resourceQuotaModel.getQuotaId(), queryCondition, dimension.substring(0, dimension.length() - 1));
                    envelopList.add(envelop1);
                }
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
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("指标资源浏览数据检索条件获取")
    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseQuotaResourceParam, method = RequestMethod.GET)
    public Envelop getStatisticsParam(
            @ApiParam("资源Id")
            @RequestParam String resourcesId){
        Envelop envelop = new Envelop();
        String [] quotaCodeArr = null;
        //获取资源关联指标
        List<ResourceQuotaModel> rqmList = resourceQuotaClient.getByResourceId(resourcesId);
        if(rqmList == null || rqmList.size() <= 0) {
            envelop.setErrorMsg("关联指标为空");
            return envelop;
        }
        //拼接指标code字符串作为维度交集查询参数
        String quotaCodes = "";
        quotaCodeArr = new String [rqmList.size()];
        List<Map<String, String>> objList = new ArrayList<Map<String, String>>();
        for (int i = 0; i< rqmList.size(); i ++) {
            ResourceQuotaModel resourceQuotaModel = rqmList.get(i);
            MTjQuotaModel tjQuotaModel = tjQuotaClient.getById((long) resourceQuotaModel.getQuotaId());
            quotaCodeArr[i] = tjQuotaModel.getCode();
            quotaCodes += tjQuotaModel.getCode() + ",";
        }
        //拼接交集维度字符串作为查询参数
        String dimensions = "";
        if(StringUtils.isEmpty(quotaCodes)) {
            envelop.setErrorMsg("指标编码有误");
            return envelop;
        }
        List<Map<String, String>> qsdList = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimension(quotaCodes.substring(0, quotaCodes.length() - 1));
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
        Map<String, Map<String, Object>> dataMap = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimensionKeyVal(quotaCodeArr[0], dimensions);
        if (dataMap != null) {
            envelop.setSuccessFlg(true);
            envelop.setObj(dataMap);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseTree, method = RequestMethod.GET)
    @ApiOperation(value = "获取视图类别-数据集列表树")
    public Envelop getResourceBrowseTree() {
        Envelop envelop = new Envelop();
        List<MRsResources> rsResources = new ArrayList<>();
        List<RsCategoryTypeTreeModel> rsCategoryTypeTreeModelList=new ArrayList<>();
        Map<String,String> map=new HashedMap();
        //限定五大库
        map.put("0dae002159535497b3865e129433e933","0dae002159535497b3865e129433e933");
        map.put("0dae0021595354a8b3865e129433e934","0dae0021595354a8b3865e129433e934");
        map.put("0dae0021595354c4b3865e129433e935","0dae0021595354c4b3865e129433e935");
        map.put("0dae0021595354cfb3865e129433e936","0dae0021595354cfb3865e129433e936");
        map.put("0dae0021595354d6b3865e129433e937","0dae0021595354d6b3865e129433e937");
        //查询资源分类
        List<MRsCategory> resources = resourcesCategoryClient.getAllCategories("");
        RsCategoryTypeTreeModel rsCategoryModel;
        for (MRsCategory mRsCategory:resources){
            if(null!=map.get(mRsCategory.getId().toString())){
                rsCategoryModel = new RsCategoryTypeTreeModel();
                rsCategoryModel.setId(mRsCategory.getId());
                rsCategoryModel.setPid(mRsCategory.getPid());
                rsCategoryModel.setName(mRsCategory.getName());
                //查询资源-数据集
                ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", "categoryId=" + mRsCategory.getId(), "", 1, 999);// TODO: 2016/5/30 测试数据15（无不分页查询）
                rsResources = categoryResponseEntity.getBody();
                if (rsResources.size() > 0) {
                    List<RsResourcesModel> resourcesModelList = (List<RsResourcesModel>) convertToModels(rsResources, new ArrayList<RsResourcesModel>(rsResources.size()), RsResourcesModel.class, null);
                    rsCategoryModel.setRsResourceslist(resourcesModelList);
                }
                rsCategoryTypeTreeModelList.add(rsCategoryModel);
            }
        }
        //平台应用-角色组对象模型列表
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rsCategoryTypeTreeModelList);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceBrowseGetRsByCategoryId, method = RequestMethod.GET)
    @ApiOperation(value = "根据视图分类的CategoryId获取数据集")
    public Envelop getResourceByCategoryId(
            @ApiParam("categoryId")
            @RequestParam(value = "categoryId", required = true) String categoryId) {
        //查询资源-数据集
        String filters="";
        if(null!=categoryId&&!"".equals(categoryId)){
             filters="categoryId=" + categoryId;
        }

        ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", filters, "", 1, 999);
        List<MRsResources>  rsResources = categoryResponseEntity.getBody();
        Integer totalCount = getTotalCount(categoryResponseEntity);
        return getResult(rsResources, totalCount, 1, 999);
    }

    /**
     @ApiOperation("资源浏览详细信息")
     @RequestMapping(value = "/resources/ResourceBrowses/getResourceDataSub", method = RequestMethod.GET)
     public Envelop getResourceDataSub(
            @ApiParam("资源代码") @RequestParam String resourcesCode,
            @ApiParam("最后一个版本记录") @RequestParam String rowKey) throws Exception {
        Envelop categoryResponseEntity = resourceBrowseClient.getResourceDataSub(resourcesCode, rowKey);
        return categoryResponseEntity;
     }
     */

    /**
     @ApiOperation("获取资源数据元信息，column信息")
     @RequestMapping(value = "/resources/ResourceBrowses", method = RequestMethod.GET)
     public Envelop queryResources(
            @ApiParam(name = "category_id", value = "返回字段", defaultValue = "")
            @RequestParam(value = "category_id") String categoryId) throws Exception {
         Envelop envelop = new Envelop();
         List<RsResourceMetadataModel> rsMetadataModels = new ArrayList<>();
         try {
             //查询资源注册信息
             ResponseEntity<List<MRsResources>> categoryResponseEntity = resourcesClient.queryResources("", "id=" + categoryId, "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
             List<MRsResources> rsResources = categoryResponseEntity.getBody();
             //查询资源数据元信息
             ResponseEntity<List<MRsResourceMetadata>> resourceMetadataResponseEntity = resourceMetadataClient.queryDimensions("", "resourcesId=" + rsResources.get(0).getId(), "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
             List<MRsResourceMetadata> rsMetadatas = resourceMetadataResponseEntity.getBody();
             //查询资源数据元详情
             for (MRsResourceMetadata mrm : rsMetadatas) {
                 RsResourceMetadataModel rsMetadataModel = convertToModel(mrm, RsResourceMetadataModel.class);
                 ResponseEntity<List<MRsMetadata>> RsresponseEntity = metadataClient.getMetadata("", "id=" + rsMetadataModel.getMetadataId(), "", 1, 15);// TODO: 2016/5/30 测试数据15（无不分页查询）
                 List<MRsMetadata> mRsMetadataList = RsresponseEntity.getBody();
                 if (mRsMetadataList.size() > 0) {
                     rsMetadataModel.setStdCode(mRsMetadataList.get(0).getStdCode());
                     rsMetadataModel.setName(mRsMetadataList.get(0).getName());
                     rsMetadataModel.setColumnType(mRsMetadataList.get(0).getColumnType());
                     rsMetadataModel.setDictId(mRsMetadataList.get(0).getDictCode());
                     rsMetadataModels.add(rsMetadataModel);
                 }
             }
             envelop.setSuccessFlg(true);
             envelop.setDetailModelList(rsMetadataModels);
         } catch (Exception e) {
            envelop.setSuccessFlg(false);
         }
         return envelop;
     }
     */
}
