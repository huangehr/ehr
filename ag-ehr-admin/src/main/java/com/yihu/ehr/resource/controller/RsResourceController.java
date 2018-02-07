package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.resource.*;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.TjQuotaChartClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.quota.service.TjQuotaJobClient;
import com.yihu.ehr.quota.service.TjQuotaSynthesizeQueryClient;
import com.yihu.ehr.resource.client.*;
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
    private TjQuotaSynthesizeQueryClient tjQuotaSynthesizeQueryClient;

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.POST)
    public Envelop createResource(
            @ApiParam(name = "resource", value = "资源")
            @RequestParam(value = "resource") String resource){
        return resourcesClient.createResource(resource);
    }

    @ApiOperation("更新资源")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.PUT)
    public Envelop updateResources(
            @ApiParam(name = "resource", value = "资源")
            @RequestParam(value = "resource") String resource) throws Exception {
        return resourcesClient.updateResources(resource);
    }

    @ApiOperation("资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.DELETE)
    public Envelop deleteResources(
            @ApiParam(name = "id", value = "资源ID")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        resourcesClient.deleteResources(id);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @ApiOperation("批量资源删除")
    @RequestMapping(value = ServiceApi.Resources.Resources, method = RequestMethod.DELETE)
    public Envelop deleteResourcesBatch(
            @ApiParam(name = "ids", value = "资源ID")
            @RequestParam(value = "ids") String ids) {
        Envelop envelop = new Envelop();
        resourcesClient.deleteResourcesBatch(ids);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.Resource, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源")
    public Envelop getResourceById(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") String id) throws Exception {
        return resourcesClient.getResourceById(id);
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceByCode, method = RequestMethod.GET)
    @ApiOperation("根据code获取资源")
    public Envelop getResourceByCode(
            @ApiParam(name = "code", value = "编码" )
            @RequestParam(value = "code" ) String code) {
        return resourcesClient.getResourceByCode(code);
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceTree, method = RequestMethod.GET)
    @ApiOperation("获取资源列表树")
    public Envelop getResourceTree(
            @ApiParam(name = "dataSource", value = "资源类型")
            @RequestParam(value = "dataSource") Integer dataSource,
            @ApiParam(name = "userResource", value = "授权资源")
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "filters", value = "过条件(name)")
            @RequestParam(value = "filters", required = false) String filters) {
        return resourcesClient.getResourceTree(dataSource, userResource, filters);
    }

    @RequestMapping(value = ServiceApi.Resources.ResourcePage, method = RequestMethod.GET)
    @ApiOperation("获取资源列表分页（政府服务平台）")
    public Envelop getResourcePage(
            @ApiParam(name = "userResource", value = "授权资源")
            @RequestParam(value = "userResource") String userResource,
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size") int size) {
        return resourcesClient.getResourcePage(userResource, userId, page, size);
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
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "rolesId", value = "角色组Id", defaultValue = "")
            @RequestParam(value = "rolesId", required = false) String rolesId,
            @ApiParam(name = "appId", value = "应用Id", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId) throws Exception {
        try {
            ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources(fields,filters,sorts,page,size, rolesId, appId);
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
        ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources("","code="+code,"",1,999, null, null);
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
        ResponseEntity<List<MRsResources>> responseEntity = resourcesClient.queryResources("","name="+name,"",1,999, null, null);
        List<MRsResources> mRsResources = responseEntity.getBody();
        if(mRsResources.size() != 0){
            envelop.setSuccessFlg(true);
        }
        return envelop;
    }

    @ApiOperation("指标资源配置")
    @RequestMapping(value = ServiceApi.Resources.GetQuotaList,method = RequestMethod.GET)
    public Envelop getQuotaList(
            @ApiParam(name = "quotaNameOrCode", value = "指标名称或编码")
            @RequestParam(value = "quotaNameOrCode", required = false) String quotaNameOrCode,
            @ApiParam(name = "filters", value = "过滤条件")
            @RequestParam(value = "filters") String filters,
            @ApiParam(name = "page", value = "页码",defaultValue = "1")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "pageSize", value = "每页大小",defaultValue = "15")
            @RequestParam(value = "pageSize") Integer pageSize) {
        ListResult listResult = tjQuotaClient.quotaConfigInfo(quotaNameOrCode, page, pageSize);

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
    @ApiOperation(value = "根据资源Id获取资源视图关联指标列表预览单个图表支持 柱状，线型，饼状，雷达，旭日,支持多个指标放在一个图形上展示")
    public Envelop getRsQuotaPreview(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "quotaFilter" ,value = "指标过滤条件 如：town=361002;quotaDate >= '2018-01-01';quotaDate <= '2018-12-31'" , defaultValue = "" )
            @RequestParam(value = "quotaFilter" , required = false) String quotaFilter,
            @ApiParam(name = "userOrgList" ,value = "用户拥有机构权限", defaultValue = "null" )
            @RequestParam(value = "userOrgList" , required = false) List<String> userOrgList,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension) throws IOException {

        //-----------------用户数据权限 start
        String org = "";
        if( userOrgList != null ){
            if( !(userOrgList.size()==1 && (userOrgList.get(0).equals("null") || userOrgList.get(0).equals("[]")) ) ) {
                org = StringUtils.strip(String.join(",", userOrgList), "[]");
                String [] orgs = org.split(",");
                for(int i = 0;i < orgs.length; i++){
                    if(i==0){
                        org = " org = '" + orgs[i] + "' ";
                    }else {
                        org = org + " or "  +   " org = '" + orgs[i]  + "' ";
                    }
                }
            }
        }
        String filter = quotaFilter;
        //-----------------用户数据权限 end
        Envelop envelop = new Envelop();
        MChartInfoModel chartInfoModel = new MChartInfoModel();;
        envelop.setObj(chartInfoModel);
        envelop.setSuccessFlg(false);
        Envelop resourceResult =  resourcesClient.getResourceById(resourceId);
        if(!resourceResult.isSuccessFlg()){
            envelop.setErrorMsg("视图不存在，请确认！");
            return envelop;
        }else {
            RsResourcesModel rsResourcesModel = toEntity(toJson(resourceResult.getObj()), RsResourcesModel.class);
            List<ResourceQuotaModel> list = resourceQuotaClient.getByResourceId(resourceId);
            if(list != null && list.size() > 0){
                String quotaCodestr  = "";
                String quotaIdstr  = "";
                String charstr = "";
                int charTypeNum = 0;
                boolean lineOrBarFlag = true;
                boolean pieFlag = true;
                for (ResourceQuotaModel m : list) {
                    quotaCodestr = quotaCodestr + m.getQuotaCode() +",";
                    quotaIdstr = quotaIdstr + m.getQuotaId() +",";
                    charstr = charstr + m.getQuotaChart() +",";
                    if(lineOrBarFlag && (m.getQuotaChart() == 1 || m.getQuotaChart() == 2)){
                        charTypeNum ++;
                        lineOrBarFlag = false;
                    }else if(pieFlag && m.getQuotaChart() == 3) {
                        charTypeNum ++;
                        pieFlag = false;
                    }
                }

                List<Map<String, String>> synthesiseDimensionMap = tjQuotaSynthesizeQueryClient.getTjQuotaSynthesiseDimension(quotaCodestr);
                Map<String, String> dimensionMap = new LinkedHashMap<>();
                String firstDimension = "";
                boolean firstFlag = true;
                for(Map<String, String> map :synthesiseDimensionMap){
                    String name = "";
                    String code = "";
                    for(String key :map.keySet()){
                        if(key.equals("name")){
                            name = map.get(key);
                        }else{
                            code = map.get(key);
                        }
                    }
                    dimensionMap.put(code,name);
                    if(firstFlag){
                        firstDimension = code;
                        firstFlag =  false;
                    }
                }
                if(StringUtils.isEmpty(dimension) || dimension.equals(" ")){
                    String defaultDimension = rsResourcesModel.getDimension();
                    if (!StringUtils.isEmpty(defaultDimension)) {
                        dimension = rsResourcesModel.getDimension();
                    } else {
                        dimension = firstDimension;
                    }
                }

                if(org.length() > 0 && dimensionMap.containsKey("org")){
                    if(filter.length() > 0){
                        filter = filter + " and (" + org + " ) ";
                    }else {
                        filter =  org;
                    }
                }
                if(charTypeNum > 1){
                    envelop.setObj(chartInfoModel);
                    envelop.setErrorMsg("视图由多个指标组成时，预览图形支持 多指标都属于同一类型，混合型目前支持‘柱状+柱状’,请确认图表展示类型！");
                }else {
                    MRsResources mRsResources = objectMapper.convertValue(resourceResult.getObj(), MRsResources.class);
                    if(StringUtils.isNotEmpty(mRsResources.getEchartType()) && mRsResources.getEchartType().equals("radar")){
                        chartInfoModel = tjQuotaJobClient.getQuotaRadarGraphicReports(quotaIdstr, filter, dimension, mRsResources.getName());
                    }else if(StringUtils.isNotEmpty(mRsResources.getEchartType()) && mRsResources.getEchartType().equals("nestedPie")){
                        chartInfoModel = tjQuotaJobClient.getQuotaNestedPieGraphicReports(resourceId, quotaIdstr, filter, dimension, mRsResources.getName());
                    }else {
                        //修改前
//                        chartInfoModel = tjQuotaJobClient.getMoreQuotaGraphicReportPreviews(quotaIdstr, charstr, filter, dimension, mRsResources.getName());
                        //修改后
                        String chart = "";
                        if(StringUtils.isNotEmpty(rsResourcesModel.getEchartType())){
                            chart = rsResourcesModel.getEchartType();
                            if(chart.equals("bar")){
                                chart ="1";
                            }else if(chart.equals("line")){
                                chart ="2";
                            }else if(chart.equals("pie")){
                                chart ="3";
                            }
                        }else{
                            chart = charstr;
                        }
                        if(StringUtils.isNotEmpty(rsResourcesModel.getDimension())){
                            dimension = rsResourcesModel.getDimension();
                        }else {
                            dimension =  firstDimension;
                        }
                        chartInfoModel = tjQuotaJobClient.getMoreQuotaGraphicReportPreviews(quotaIdstr, chart, filter, dimension , mRsResources.getName());
                    }
                    chartInfoModel.setResourceId(resourceId);
                    chartInfoModel.setDimensionMap(dimensionMap);
                    chartInfoModel.setFirstDimension(firstDimension);
                    envelop.setObj(chartInfoModel);
                    envelop.setSuccessFlg(true);
                }
            }else{
                envelop.setErrorMsg("视图中无指标，请确认！");
            }
        }
        return envelop;
    }

    @ApiOperation(value = "获取指标统计结果echart radar雷达图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaRadarGraphicReportPreviews, method = RequestMethod.GET)
    public Envelop getQuotaRadarGraphicReports(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "quotaFilter" ,value = "指标过滤条件" , defaultValue = "" )
            @RequestParam(value = "quotaFilter" , required = false) String quotaFilter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title) {
        String filter = filterHandle(quotaFilter);

        Envelop envelop = new Envelop();
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        chartInfoModel.setResourceId(resourceId);
        envelop.setObj(chartInfoModel);
        envelop.setSuccessFlg(false);
        try {
            Envelop resourceResult =  resourcesClient.getResourceById(resourceId);
            if(!resourceResult.isSuccessFlg()){
                envelop.setErrorMsg("视图不存在，请确认！");
                return envelop;
            }
            List<ResourceQuotaModel> list = resourceQuotaClient.getByResourceId(resourceId);
            String quotaIdStr = "";
            if (null != list && list.size() > 0) {
                for (ResourceQuotaModel ResourceQuota : list) {
                    quotaIdStr += ResourceQuota.getQuotaId() + ",";
                }
            }
            chartInfoModel = tjQuotaJobClient.getQuotaRadarGraphicReports(quotaIdStr, filter, dimension, title);
            chartInfoModel.setFirstDimension(dimension);
            chartInfoModel.setResourceId(resourceId);
            envelop.setObj(chartInfoModel);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            envelop.setErrorMsg("获取图表出错！");
        }
        return envelop;
    }

    @ApiOperation(value = "获取指标统计结果echart NestedPie图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaNestedPieReportPreviews, method = RequestMethod.GET)
    public Envelop getQuotaNestedPieGraphicReports(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceId") String resourceId,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title) {
        Envelop envelop = new Envelop();
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        chartInfoModel.setResourceId(resourceId);
        envelop.setObj(chartInfoModel);
        envelop.setSuccessFlg(false);
        String filters = filterHandle(filter);
        try {
            Envelop resourceResult =  resourcesClient.getResourceById(resourceId);
            if(!resourceResult.isSuccessFlg()){
                envelop.setErrorMsg("视图不存在，请确认！");
                return envelop;
            }
            List<ResourceQuotaModel> list = resourceQuotaClient.getByResourceId(resourceId);
            String quotaIdStr = "";
            if (null != list && list.size() > 0) {
                for (ResourceQuotaModel ResourceQuota : list) {
                    quotaIdStr += ResourceQuota.getQuotaId() + ",";
                }
            }
            chartInfoModel = tjQuotaJobClient.getQuotaNestedPieGraphicReports(resourceId, quotaIdStr, filters, dimension, title);
            chartInfoModel.setFirstDimension(dimension);
            chartInfoModel.setResourceId(resourceId);
            envelop.setObj(chartInfoModel);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            envelop.setErrorMsg("获取图表出错！");
        }
        return envelop;
    }

    private String filterHandle(String quotaFilter) {
        String filter = "";
        if(StringUtils.isNotEmpty(quotaFilter)){
            String [] quotaFilters = quotaFilter.split(";");
            for(int i = 0;i < quotaFilters.length; i++){
                String [] keyVal = quotaFilters[i].split("=");
                if(keyVal[i].length()>1){
                    if(i==0){
                        filter = keyVal[0] + "='" + keyVal[1] +"' ";
                    }else {
                        filter = filter + " and "  + keyVal[0] + "='" + keyVal[1] +"' ";
                    }
                }
            }
        }
        return filter;
    }
}