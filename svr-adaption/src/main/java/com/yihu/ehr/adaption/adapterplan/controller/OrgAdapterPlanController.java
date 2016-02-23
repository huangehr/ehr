package com.yihu.ehr.adaption.adapterplan.controller;

import com.yihu.ehr.adaption.adapterplan.service.AdapterCustomize;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlan;
import com.yihu.ehr.adaption.adapterplan.service.OrgAdapterPlanManager;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSet;
import com.yihu.ehr.adaption.dataset.service.AdapterDataSetManager;
import com.yihu.ehr.adaption.feignclient.DataSetClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.parm.PageModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersion.CommonVersion + "/adapter")
@Api(protocols = "https", value = "adapter", description = "适配器管理接口", tags = {"适配器管理"})
public class OrgAdapterPlanController extends ExtendController<MAdapterPlan> {
    @Autowired
    private OrgAdapterPlanManager orgAdapterPlanManager;
    @Autowired
    private AdapterDataSetManager adapterDataSetManager;
    @Autowired
    private DataSetClient dataSetClient;



    @RequestMapping(value = "/plans/page", method = RequestMethod.GET)
    @ApiOperation(value = "适配方案搜索")
    public Envelop searchAdapterPlan(
            @ApiParam(name = "parmJson", value = "分页模型", defaultValue = "")
            @RequestParam(value = "parmJson", required = false) String parmJson) {

        try {
            return orgAdapterPlanManager.pagesToResult(jsonToObj(parmJson, PageModel.class));
        } catch (IOException e){
            throw errParm();
        } catch (Exception ex) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案信息")
    public MAdapterPlan getAdapterPlanById(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) {

        try {
            if(id==null)
                throw errMissId();
            return getModel(orgAdapterPlanManager.findOne(id));
        } catch (Exception es) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plan", method = RequestMethod.POST)
    @ApiOperation(value = "保存适配方案")
    public boolean saveAdapterPlan(
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson,
            @ApiParam(name = "isCover", value = "是否覆盖", defaultValue = "")
            @RequestParam(value = "isCover") String isCover) {
        try {
            return saveModel(parmJson, isCover, 0l);
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plan/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新适配方案")
    public boolean updateAdapterPlan(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id,
            @ApiParam(name = "parmJson", value = "数据模型", defaultValue = "")
            @RequestParam(value = "parmJson") String parmJson) {

        try {
            return saveModel(parmJson, "", id);
        } catch (IOException e) {
            throw errParm();
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plans", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除适配方案")
    public boolean delAdapterPlan(
            @ApiParam(name = "ids", value = "编号列表", defaultValue = "")
            @RequestParam("ids") String ids) {
        try {
            if(StringUtils.isEmpty(ids))
                errMissId();
            orgAdapterPlanManager.deleteOrgAdapterPlan(ids.split(","));
            return true;
        } catch (Exception e) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plans/list", method = RequestMethod.GET)
    @ApiOperation(value = "根据类型跟版本号获取适配方案列表")
    public List getAdapterPlanList(
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam("type") String type,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @PathVariable(value = "version") String version) {
        try {
            List<OrgAdapterPlan> orgAdapterPlans = orgAdapterPlanManager.findList(type, version);
            List<Map> adapterPlan = new ArrayList<>();
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
        } catch (Exception ex) {
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plan/{planId}/adapterCustomizes", method = RequestMethod.GET)
    @ApiOperation(value = "获取定制信息")
    public Map getAdapterCustomize(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam("version") String version) {

        try {
            //获取所有定制数据集
            List<AdapterCustomize> adapterCustomizeList = findAdapterCustomize(apiVersion, planId, version);
            //获取所有标准数据集
            List<AdapterCustomize> stdCustomizeList = findStdCustomize(apiVersion, version, adapterCustomizeList);
            Map map = new HashMap<>();
            map.put("stdDataSet", stdCustomizeList);
            map.put("adapterDataSet", adapterCustomizeList);
            return map;
        }catch (Exception e){
            throw errSystem();
        }
    }


    @RequestMapping(value = "/plan/{planId}/adapterDataSet", method = RequestMethod.POST)
    @ApiOperation(value = "定制数据集")
    public boolean adapterDataSet(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "planId", value = "编号", defaultValue = "")
            @PathVariable("planId") Long planId,
            @ApiParam(name = "customizeData", value = "customizeData", defaultValue = "")
            @RequestParam("customizeData") String customizeData) {

        try {
            customizeData = customizeData.replace("DataSet", "").replace("MetaData", "");
            List<AdapterCustomize> adapterDataSetList = Arrays.asList(jsonToObj(customizeData, AdapterCustomize[].class));
            orgAdapterPlanManager.adapterDataSet(apiVersion, planId, adapterDataSetList);
            return true;
        } catch (IOException ex) {
            throw errParm();
        } catch (Exception ex) {
            throw errSystem();
        }
    }


    /**************************************************************************************************/
    /***********************  private  ***************************************************************/
    /**************************************************************************************************/

    //获取所有标准数据集
    private List<AdapterCustomize> findStdCustomize(String apiVersion, String version, List<AdapterCustomize> adapterCustomizeList) {
        String id;
        boolean std = false;
        boolean check = false;
        long childCheckCount;
        List<AdapterCustomize> stdCustomizeList = new ArrayList<>();

        Map<String, String> map = (Map<String, String>) dataSetClient.getDataSetMapByIds(apiVersion, version, "");
        Map<String, Map> metaDatas = (Map<String, Map>) dataSetClient.getMetaDataMapByIds(apiVersion, version, "", "");
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
    private List<AdapterCustomize> findAdapterCustomize(String apiVersion, Long planId, String version) {
        boolean adapter = false;  //定制是否添加根节点
        List<AdapterCustomize> adapterCustomizeList = new ArrayList<>();
        //获取所有定制数据集
        List<Long> adapterDataSetList = adapterDataSetManager.getAdapterDataSet(planId);
        List<AdapterDataSet> adapterMetaDataList = adapterDataSetManager.getAdapterMetaData(planId);
        //数据集
        String ids = adapterDataSetList.toString();
        ids = ids.substring(1, ids.length() - 1);
        Map<Integer, String> map = (Map<Integer, String>) dataSetClient.getDataSetMapByIds(apiVersion, version, ids);
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
        Map metaDatas = (Map) dataSetClient.getMetaDataMapByIds(apiVersion, version, ids, metaDataIds.substring(1));
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

    private boolean saveModel(String parmJson, String isCover, Long id) throws IOException {
        OrgAdapterPlan plan = jsonToObj(parmJson, OrgAdapterPlan.class);
        OrgAdapterPlan orgAdapterPlan = orgAdapterPlanManager.findOne(id);
        boolean checkCode = true;
        if (plan.getId() != null && plan.getCode().equals(orgAdapterPlan.getCode()))
            checkCode = false;
        if (checkCode && orgAdapterPlanManager.isAdapterCodeExist(plan.getCode())) {
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
            orgAdapterPlanManager.addOrgAdapterPlan(orgAdapterPlan, isCover);
        } else {
            orgAdapterPlanManager.save(orgAdapterPlan);
        }
        return true;
    }
}
