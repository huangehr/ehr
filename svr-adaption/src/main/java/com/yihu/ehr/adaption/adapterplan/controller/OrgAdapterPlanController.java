package com.yihu.ehr.adaption.adapterplan.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.adapterplan.service.AdapterCustomize;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanService;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetService;
import com.yihu.ehr.adaption.dispatch.service.AdapterInfoSendService;
import com.yihu.ehr.adaption.feignclient.DataSetClient;
import com.yihu.ehr.adaption.feignclient.DispatchLogClient;
import com.yihu.ehr.adaption.log.LogService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter")
@Api(protocols = "https", value = "plan", description = "适配器管理接口", tags = {"适配器管理"})
public class OrgAdapterPlanController extends ExtendController<MAdapterPlan> {
    @Autowired
    private OrgAdapterPlanService orgAdapterPlanService;
    @Autowired
    private AdapterDataSetService adapterDataSetService;
    @Autowired
    private DataSetClient dataSetClient;
    @Autowired
    AdapterInfoSendService adapterInfoSendService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    DispatchLogClient dispatchLogClient;
    @RequestMapping(value = "/plans", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    public Collection<MAdapterPlan> searchAdapterPlan(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        List appList = orgAdapterPlanService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgAdapterPlanService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MAdapterPlan.class, fields);
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public MAdapterPlan getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) throws Exception {

        if (id == null)
            throw errMissId();
        return getModel(orgAdapterPlanService.retrieve(id));
    }


    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    @ApiOperation(value = "保存适配方案")
    public MAdapterPlan saveAdapterPlan(
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson,
            @ApiParam(name = "isCover", value = "是否覆盖", defaultValue = "")
            @RequestParam(value = "isCover") String isCover) throws Exception {

        return getModel(saveModel(parmJson, isCover, 0l));
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新适配方案")
    public MAdapterPlan updateAdapterPlan(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson) throws Exception {

        return getModel(saveModel(parmJson, "", id));
    }


    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案")
    public boolean delAdapterPlan(
            @ApiParam(name = "ids", value = "编号列表", defaultValue = "")
            @RequestParam("ids") String ids) throws Exception {

        if (StringUtils.isEmpty(ids))
            errMissId();
        orgAdapterPlanService.deleteOrgAdapterPlan(strToLongArr(ids));
        return true;
    }


    @RequestMapping(value = "/plans/list", method = RequestMethod.GET)
    @ApiOperation(value = "根据类型跟版本号获取适配方案列表")
    public List<Map<String, String>> getAdapterPlanList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {

        List<OrgAdapterPlan> orgAdapterPlans = orgAdapterPlanService.findList(type, version);
        List<Map<String, String>> adapterPlan = new ArrayList<>();
        if (!orgAdapterPlans.isEmpty()) {
            Map<String, String> map = null;
            for (OrgAdapterPlan plan : orgAdapterPlans) {
                map = new HashMap<>();
                map.put("code", plan.getId().toString());
                map.put("value", plan.getName());
                adapterPlan.add(map);
            }
        }
        return adapterPlan;
    }


    @RequestMapping(value = "/plan/{planId}/adapterCustomizes", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    public Map getAdapterCustomize(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) throws Exception {

        //获取所有定制数据集
        List<AdapterCustomize> adapterCustomizeList = findAdapterCustomize(planId, version);
        //获取所有标准数据集
        List<AdapterCustomize> stdCustomizeList = findStdCustomize(version, adapterCustomizeList);
        Map map = new HashMap<>();
        map.put("stdDataSet", stdCustomizeList);
        map.put("adapterDataSet", adapterCustomizeList);
        return map;
    }


    @RequestMapping(value = "/plan/{planId}/adapterDataSet", method = RequestMethod.POST)
    @ApiOperation(value = "定制数据集")
    public boolean adapterDataSet(
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) throws Exception {

        try {
            customizeData = customizeData.replace("DataSet", "").replace("MetaData", "");
            List<AdapterCustomize> adapterDataSetList = Arrays.asList(jsonToObj(customizeData, AdapterCustomize[].class));
            orgAdapterPlanService.adapterDataSet(planId, adapterDataSetList);
            return true;
        } catch (IOException ex) {
            throw errParm();
        }
    }

    /**
     * 适配版本发布
     * 1.生成适配版本文件并记录文件位置；2.修改适配方案状态
     */
    @RequestMapping(value = "/plan/{planId}/dispatch", method = RequestMethod.POST)
    @ApiOperation(value = "适配版本发布")
    public boolean adapterDispatch(
            @ApiParam(name = "planId", value = "方案编号", defaultValue = "")
            @PathVariable("planId") Long planId) throws Exception{

        try {
            OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(planId);
            String versionCode = orgAdapterPlan.getVersion();
            String orgCode = orgAdapterPlan.getOrg();

            Map<String, Object> resultMap = adapterInfoSendService.createStandardAndMappingInfo(versionCode, orgCode);
            if (resultMap == null) {
                return false;
            }

            orgAdapterPlan.setStatus(1);
            orgAdapterPlanService.save(orgAdapterPlan);
            return true;
        } catch (Exception ex) {
            LogService.getLogger(OrgAdapterPlanController.class).error(ex.getMessage());
            throw ex;
        }
    }


    /**************************************************************************************************/
    /***********************  private  ***************************************************************/
    /**************************************************************************************************/

    //获取所有标准数据集
    private List<AdapterCustomize> findStdCustomize(String version, List<AdapterCustomize> adapterCustomizeList) {
        String id;
        boolean std = false;
        boolean check = false;
        long childCheckCount;
        List<AdapterCustomize> stdCustomizeList = new ArrayList<>();

        Map<String, String> map = (Map<String, String>) dataSetClient.getDataSetMapByIds(version, "");
        Map<String, Map> metaDatas = (Map<String, Map>) dataSetClient.getMetaDataMapByIds(version, "");
        for (String dataSetId : map.keySet()) {
            AdapterCustomize parent = new AdapterCustomize();
            parent.setId("stdDataSet" + dataSetId);
            parent.setPid("std0");
            parent.setText(map.get(dataSetId));
            std = true;
            childCheckCount = 0;
            Map<String, String> metaDataList = metaDatas.get(dataSetId);
            if (metaDataList == null)
                continue;
            for (String k : metaDataList.keySet()) {
                check = false;
                for (AdapterCustomize adapterCustomize : adapterCustomizeList) {
                    //已适配的要勾选
                    if (("adapterMetaData" + k).equals(adapterCustomize.getId())) {
                        check = true;
                        childCheckCount++;
                        break;
                    } else {
                        check = false;
                    }
                }
                AdapterCustomize child = new AdapterCustomize();
                child.setId("stdMetaData" + k);
                child.setPid("stdDataSet" + dataSetId);
                child.setText(metaDataList.get(k));
                stdCustomizeList.add(child);
                child.setIschecked(check);
                std = true;
            }
            if (metaDataList.size() == childCheckCount && childCheckCount > 0) {
                parent.setIschecked(true);//子节点全选
            }
            stdCustomizeList.add(parent);
        }
        //根节点
        if (std) {
            AdapterCustomize stdRoot = new AdapterCustomize();
            stdRoot.setId("std0");
            stdRoot.setPid("-1");
            stdRoot.setText("数据集");
            stdCustomizeList.add(stdRoot);
        }
        return stdCustomizeList;
    }

    //获取所有定制数据集
    private List<AdapterCustomize> findAdapterCustomize(Long planId, String version) {
        boolean adapter = false;  //定制是否添加根节点
        List<AdapterCustomize> adapterCustomizeList = new ArrayList<>();
        //获取所有定制数据集
        List<Long> adapterDataSetList = adapterDataSetService.getAdapterDataSet(planId);
        List<AdapterDataSet> adapterMetaDataList = adapterDataSetService.getAdapterMetaData(planId);
        //数据集
        String ids = adapterDataSetList.toString();
        ids = ids.substring(1, ids.length() - 1);
        Map<Integer, String> map = (Map<Integer, String>) dataSetClient.getDataSetMapByIds(version, ids);
        for (Long adapterDataSet : adapterDataSetList) {
            AdapterCustomize parent = new AdapterCustomize();
            parent.setId("adapterDataSet" + adapterDataSet);
            parent.setPid("adapter0");
            parent.setText(map.get(adapterDataSet.intValue()));
            parent.setIschecked(true);
            adapterCustomizeList.add(parent);
            adapter = true;
        }
        //数据元
        String metaDataIds = "";
        int i = 0;
        for (AdapterDataSet adapterDataSet : adapterMetaDataList) {
            metaDataIds += "," + adapterDataSet.getMetaDataId();
        }
        Map metaDatas = dataSetClient.getMetaDataMapByIds(version, metaDataIds.substring(1));
        Map tmp;
        for (AdapterDataSet adapterDataSet : adapterMetaDataList) {
            AdapterCustomize child = new AdapterCustomize();
            child.setId("adapterMetaData" + adapterDataSet.getMetaDataId());
            child.setPid("adapterDataSet" + adapterDataSet.getDataSetId());
            if ((tmp = (Map) metaDatas.get(adapterDataSet.getDataSetId().intValue())) != null)
                child.setText(((String) tmp.get(adapterDataSet.getMetaDataId().intValue())));
            child.setIschecked(true);
            adapterCustomizeList.add(child);
            adapter = true;
        }
        //根节点
        if (adapter) {
            AdapterCustomize adapterRoot = new AdapterCustomize();
            adapterRoot.setId("adapter0");
            adapterRoot.setPid("-1");
            adapterRoot.setText("数据集");
            adapterRoot.setIschecked(true);
            adapterCustomizeList.add(adapterRoot);
        }
        return adapterCustomizeList;
    }

    private OrgAdapterPlan saveModel(String parmJson, String isCover, Long id) {
        OrgAdapterPlan plan = null;
        try {
            plan = jsonToObj(parmJson, OrgAdapterPlan.class);
        } catch (IOException e) {
            throw errParm();
        }
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanService.retrieve(id);
        orgAdapterPlan = orgAdapterPlan==null? new OrgAdapterPlan():orgAdapterPlan;
        boolean checkCode = true;
        if (plan.getId() != null && plan.getCode().equals(orgAdapterPlan.getCode()))
            checkCode = false;
        if (checkCode && orgAdapterPlanService.isAdapterCodeExist(plan.getCode())) {
            throw errRepeatCode();
        }
        orgAdapterPlan.setCode(plan.getCode());
        orgAdapterPlan.setName(plan.getName());
        orgAdapterPlan.setDescription(plan.getDescription());
        orgAdapterPlan.setVersion(plan.getVersion());
        orgAdapterPlan.setType(plan.getType());
        orgAdapterPlan.setOrg(plan.getOrg());
        orgAdapterPlan.setParentId(plan.getParentId());
        if (plan.getId() == null) {
            return orgAdapterPlanService.addOrgAdapterPlan(orgAdapterPlan, isCover);
        } else {
            return orgAdapterPlanService.save(orgAdapterPlan);
        }
    }
}
