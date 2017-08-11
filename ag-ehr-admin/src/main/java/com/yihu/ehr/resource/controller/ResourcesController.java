package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.ResourceQuotaModel;
import com.yihu.ehr.agModel.resource.RsResourcesModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.resource.MRsCategory;
import com.yihu.ehr.model.resource.MRsInterface;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.TjQuotaChartClient;
import com.yihu.ehr.quota.service.TjQuotaClient;
import com.yihu.ehr.resource.client.ResourceQuotaClient;
import com.yihu.ehr.resource.client.ResourcesCategoryClient;
import com.yihu.ehr.resource.client.ResourcesClient;
import com.yihu.ehr.resource.client.RsInterfaceClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resources", description = "资源服务接口", tags = {"资源管理-资源服务接口"})
public class ResourcesController extends BaseController {
    @Autowired
    private ResourcesClient resourcesClient;
    @Autowired
    private RsInterfaceClient rsInterfaceClient;
    @Autowired
    private ResourcesCategoryClient rsCategoryClient;
    @Autowired
    private TjQuotaClient tjQuotaClient;
    @Autowired
    private ResourceQuotaClient resourceQuotaClient;
    @Autowired
    private TjQuotaChartClient tjQuotaChartClient;

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
    @RequestMapping(value = "/resources/isExistCode/{code}" , method = RequestMethod.GET)
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

    @ApiOperation("资源名称是否已存在")
    @RequestMapping(value = "/resources/isExistName",method = RequestMethod.GET)
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
    @RequestMapping(value = "/resources/getQuotaList",method = RequestMethod.GET)
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
            for (int i=0; i<mainModelList.size(); i++) {
                if (list.contains(mainModelList.get(i).getQuotaId())) {
                    mainModelList.get(i).setFlag(true);
                    // 根据quotaId查询resourceQuota表，获取已选择的图表值
                    String quotaChart = resourceQuotaClient.getQuotaChartByQuotaId(mainModelList.get(i).getQuotaId());
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
}