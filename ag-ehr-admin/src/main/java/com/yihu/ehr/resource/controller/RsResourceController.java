package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.model.resource.MRsInterface;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.quota.service.TjQuotaChartClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.resource.client.RsResourceQuotaClient;
import com.yihu.ehr.resource.client.RsResourceCategoryClient;
import com.yihu.ehr.resource.client.RsResourceClient;
import com.yihu.ehr.resource.client.RsInterfaceClient;
import com.yihu.ehr.users.service.GetInfoClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resources", description = "资源服务接口", tags = {"资源管理-资源服务接口"})
public class RsResourceController extends BaseController {
    @Autowired
    private RsResourceClient resourcesClient;
    @Autowired
    private RsInterfaceClient rsInterfaceClient;
    @Autowired
    private RsResourceCategoryClient rsCategoryClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private RsResourceQuotaClient resourceQuotaClient;
    @Autowired
    private TjQuotaChartClient tjQuotaChartClient;
    @Autowired
    private TjQuotaJobClient tjQuotaJobClient;
    @Autowired
    private GetInfoClient getInfoClient;
    @Autowired
    private AddressClient addressClient;
    @Autowired
    OrganizationClient organizationClient;

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST)
    public Envelop createResource(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        Envelop envelop = new Envelop();
        try{
            RsResourcesModel rsResourcesModel = objectMapper.readValue(resource,RsResourcesModel.class);
            MRsResources mRsResources = convertToMModel(rsResourcesModel,MRsResources.class);
            MRsResources rsResources = resourcesClient.createResource(objectMapper.writeValueAsString(mRsResources));
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT)
    public Envelop updateResources(
            @ApiParam(name = "resource", value = "资源", defaultValue = "")
            @RequestParam(value = "resource") String resource) throws Exception {
        Envelop envelop = new Envelop();
        try{
            RsResourcesModel rsResourcesModel = objectMapper.readValue(resource,RsResourcesModel.class);
            MRsResources mRsResources = convertToMModel(rsResourcesModel,MRsResources.class);
            MRsResources rsResources = resourcesClient.updateResources(objectMapper.writeValueAsString(mRsResources));
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    public Envelop deleteResources(
            @ApiParam(name = "id", value = "资源ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesClient.deleteResources(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("批量资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    public Envelop deleteResourcesBatch(
            @ApiParam(name = "ids", value = "资源ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesClient.deleteResourcesBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Resource,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源")
    public Envelop getResourceById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsResources rsResources = resourcesClient.getResourceById(id);
            envelop.setObj(rsResources);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceTree, method = RequestMethod.GET)
    @ApiOperation("获取资源列表树")
    public Envelop getResourceTree(
            @ApiParam(name = "dataSource", value = "数据源")
            @RequestParam(value = "dataSource") Integer dataSource,
            @ApiParam(name = "filters", value = "过条件(name)")
            @RequestParam(value = "filters", required = false) String filters) {
        return resourcesClient.getResourceTree(dataSource, filters);
    }

    @ApiOperation("资源查询")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.GET)
    public Envelop queryResources(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources(fields,filters,sorts,page,size);
            List<MRsResources> mRsResources = responseEntity.getBody();
            List<RsResourcesModel> rsResources = new ArrayList<>(mRsResources.size());
            for(MRsResources m:mRsResources){
                RsResourcesModel rsResourcesModel = convertToModel(m,RsResourcesModel.class);
                String categoryId =  rsResourcesModel.getCategoryId();
                if (!StringUtils.isEmpty(categoryId)){
                    MRsCategory category = rsCategoryClient.getRsCategoryById(categoryId);
                    rsResourcesModel.setCategoryName(category==null?"":category.getName());
                }
                String rsInterfaceCode = m.getRsInterface();
                if (!StringUtils.isEmpty(rsInterfaceCode)){
                    MRsInterface rsInterface = rsInterfaceClient.findByResourceInterface(rsInterfaceCode);
                    rsResourcesModel.setRsInterfaceName(rsInterface==null?"":rsInterface.getName());
                }
                rsResources.add(rsResourcesModel);
            }
            Envelop envelop = getResult(rsResources, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @ApiOperation("资源编码是否已存在")
    @RequestMapping(value = ServiceApi.Resources.IsExistCode , method = RequestMethod.GET)
    public Envelop isExistCode(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources("","code="+code,"",1,999);
        List<MRsResources> mRsResources = responseEntity.getBody();
        if(mRsResources.size() != 0){
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }

    @ApiOperation(value = "资源查询_不分页")
    @RequestMapping(value = ServiceApi.Resources.NoPageResources, method = RequestMethod.GET)
    public Envelop queryNoPageResources(
            @ApiParam(name = "filters", value = "过滤条件")
            @RequestParam(value = "filters", required = false) String filters) {
        Envelop envelop = new Envelop();
        try{
            List<MRsResources> list = resourcesClient.queryNoPageResources(filters);
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源名称是否已存在")
    @RequestMapping(value = ServiceApi.Resources.IsExistName,method = RequestMethod.GET)
    public Object isExistName(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources("","name="+name,"",1,999);
        List<MRsResources> mRsResources = responseEntity.getBody();
        if(mRsResources.size() != 0){
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }

    @ApiOperation("指标资源配置")
    @RequestMapping(value = ServiceApi.Resources.GetQuotaList,method = RequestMethod.GET)
    public Envelop getQuotaList(
            @ApiParam(name = "quotaName", value = "指标名称",defaultValue = "")
            @RequestParam(value = "quotaName", required = false) String quotaName,
            @ApiParam(name = "filters", value = "过滤条件")
            @RequestParam(value = "filters") String filters,
            @ApiParam(name = "page", value = "页码",defaultValue = "1")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "pageSize", value = "每页大小",defaultValue = "15")
            @RequestParam(value = "pageSize") Integer pageSize) {
        ListResult listResult = tjQuotaClient.quotaConfigInfo(quotaName, page, pageSize);

        List<MQuotaConfigModel> mainModelList  = new ArrayList<>();
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> modelList = listResult.getDetailModelList();
            for(Map<String,Object> map : modelList){
                MQuotaConfigModel quotaConfigModel = objectMapper.convertValue(map,MQuotaConfigModel.class);
                mainModelList.add(quotaConfigModel);
            }
            ListResult listResult2 = resourceQuotaClient.search("", filters, "", pageSize, page);
            List<ResourceQuotaModel> mainModelList2  = new ArrayList<>();
            if(listResult2.getTotalCount() != 0) {
                List<Map<String, Object>> modelList2 = listResult2.getDetailModelList();
                for (Map<String, Object> map : modelList2) {
                    ResourceQuotaModel resourceQuotaModel = objectMapper.convertValue(map, ResourceQuotaModel.class);
                    MTjQuotaModel mTjQuotaModel = tjQuotaClient.getById(Long.valueOf(resourceQuotaModel.getQuotaId()));
                    if (mTjQuotaModel != null) {
                        resourceQuotaModel.setQuotaName(mTjQuotaModel.getName());
                        //根据指标编码查询tjQuotaChart表，获取chartId
                        List<Integer> chartTypeList = tjQuotaChartClient.getChartTypeByQuotaCode(mTjQuotaModel.getCode());
                        StringBuilder chartType = new StringBuilder();
                        for (Integer i : chartTypeList) {
                            chartType.append(i).append(",");
                        }
                        resourceQuotaModel.setChartType(chartType.length() > 0 ? chartType.substring(0, chartType.length() -1) : "");
                    }
                    mainModelList2.add(resourceQuotaModel);
                }
            }
            List<Integer> list = resourceQuotaClient.getRQNameByResourceId(filters);//根据资源ID查询
            String[] split = filters.split("=");    //获取resourceId
            String resourceId = split[split.length-1];
            for (int i=0; i<mainModelList.size(); i++) {
                if (list.contains(mainModelList.get(i).getQuotaId())) {
                    mainModelList.get(i).setFlag(true);
                    // 根据quotaId及resourceId查询resourceQuota表，获取已选择的图表值
                    String quotaChart = resourceQuotaClient.getQuotaChartByQuotaId(mainModelList.get(i).getQuotaId(), resourceId);
                    mainModelList.get(i).setQuotaChart(quotaChart);
                } else {
                    mainModelList.get(i).setFlag(false);
                }
                //根据指标编码查询tjQuotaChart表，获取chartId
                List<Integer> chartTypeList = tjQuotaChartClient.getChartTypeByQuotaCode(mainModelList.get(i).getQuotaCode());
                StringBuilder chartType = new StringBuilder();
                for (Integer c : chartTypeList) {
                    chartType.append(c).append(",");
                }
                mainModelList.get(i).setChartType(chartType.length() > 0 ? chartType.substring(0, chartType.length() -1) : "");
            }
            Envelop result = getResult(mainModelList, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
            result.setObj(mainModelList2);
            return result;
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Resources.GetRsQuotaPreview, method = RequestMethod.GET)
    @ApiOperation(value = "根据资源Id获取资源视图关联指标列表预览")
    public List<MChartInfoModel> getRsQuotaPreview(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "quotaId", value = "上卷下钻的指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "userOrgList" ,value = "用户拥有机构权限" )
            @RequestParam(value = "userOrgList" , required = false) List<String> userOrgList,
            @ApiParam(name = "quotaFilter", value = "指标查询过滤条件", defaultValue = "")
            @RequestParam(value = "quotaFilter", required = false) String quotaFilter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension) throws IOException {
        List<ResourceQuotaModel> list = resourceQuotaClient.getByResourceId(resourceId);
        List<MChartInfoModel> chartInfoModels = new ArrayList<>();
        if(list!=null && list.size() > 0){
            for (ResourceQuotaModel m : list) {
                if(StringUtils.isEmpty(quotaId) || m.getQuotaId() == Integer.valueOf(quotaId)){
                    //-----------------用户数据权限 start
                    String org = "";
                    if( userOrgList != null ){
                        org = StringUtils.strip(String.join(",", userOrgList), "[]");
                    }
                    //-----------------用户数据权限 end
                    Map<String, Object> params  = new HashMap<>();
                    if(org.length()>0){
                        if(StringUtils.isNotEmpty(quotaFilter)){
                            params  = objectMapper.readValue(quotaFilter, new TypeReference<Map>() {});
                        }
                        params.put("org",org);
                        quotaFilter = objectMapper.writeValueAsString(params);
                    }
                    MChartInfoModel chartInfoModel = tjQuotaJobClient.getQuotaGraphicReport(m.getQuotaId(), m.getQuotaChart(), quotaFilter,dimension);
                    chartInfoModels.add(chartInfoModel);
                }
            }
        }
        return chartInfoModels;
    }

}