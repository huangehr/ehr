package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.patient.ArApplyModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.patient.MArApply;
import com.yihu.ehr.model.patient.MArRelation;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.patient.service.ArApplyClient;
import com.yihu.ehr.patient.service.ArRelationClient;
import com.yihu.ehr.patient.service.XResourceClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.utils.FeignExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.1
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(protocols = "https", value = "archive_apply", description = "档案申请" , tags = {"档案申请"})
public class ArApplyController extends ExtendController<ArApplyModel> {
    String appId = "ag-admin";
    @Autowired
    ArApplyClient arApplyClient;
    @Autowired
    ArRelationClient arRelationClient;
    @Autowired
    SystemDictClient systemDictClient;
    @Autowired
    OrganizationClient organizationClient;
    @Autowired
    XResourceClient resource; //资源服务

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联申请列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MArApply>> responseEntity = arApplyClient.search(fields, filters, sorts, size, page);
        return getResult(convertModels(responseEntity.getBody()), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.POST)
    @ApiOperation(value = "新增档案关联申请")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {

        try {
            return success(convertModel(arApplyClient.add(model)) );
        }catch (Exception e){
            e.printStackTrace();
            return failed("新增出错！");
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.PUT)
    @ApiOperation(value = "修改档案关联申请")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {

        try {
            ArApplyModel arApplyModel = toEntity(model, ArApplyModel.class);
            MArApply arApply = convertToMModel(arApplyModel, MArApply.class);
            if(arApply.getId()==0)
                return failed("编号不能为空");
            return success(convertModel(arApplyClient.update(arApply)));
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    public boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        arApplyClient.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        return arApplyClient.batchDelete(ids.split(","));
    }

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联申请信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "档案关联申请编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            MArApply arApply = arApplyClient.getInfo(id);
            if(arApply==null)
                return failed("查无数据！");
            return success(convertModel(arApply));
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }


    @RequestMapping(value = "/archive/applications/{id}/audit", method = RequestMethod.GET)
    @ApiOperation(value = "档案申请审核")
    public Envelop audit(
            @ApiParam(name = "id", value = "档案关联申请编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            MArApply arApply = arApplyClient.getInfo(id);
            Envelop envelop = success(getModel(arApply));
            String f = "";
            if(!StringUtils.isEmpty(arApply.getIdCard()))
                f += "demographic_id:" + arApply.getIdCard();

            if(!StringUtils.isEmpty(arApply.getVisDate()))
                f += ("".equals(f)? "" : "+AND+") + "event_date:["+ arApply.getVisDate().replace("~", "+TO+") +"]" ;

            if(!StringUtils.isEmpty(arApply.getVisOrg()))
                f += ("".equals(f)? "" : "+AND+") + "org_code:"+ arApply.getVisOrg() +"" ;

            envelop.setDetailModelList(
                    getArchiveInfos(resource.getResources("RS_PATIENT_INFO", appId, "{\"q\":\"" + f + "\"}", null, null))
            );
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }

    @RequestMapping(value = "/archive/applications/{id}/archive_info", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案申请信息以及档案信息")
    public Envelop getApplyInfoWithArchiveInfo(
            @ApiParam(name = "id", value = "档案关联申请编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            MArApply arApply = arApplyClient.getInfo(id);
            Envelop envelop = success(getModel(arApply));
            List<MArRelation> relations = arRelationClient.search("", "arApplyId="+ arApply.getId() +";", "", 1, 1).getBody();
            if(relations==null || relations.size()==0)
                failed("不存在档案关联信息");
            MArRelation relation = relations.get(0);
            if(StringUtils.isEmpty(relation.getArchiveId()))
                failed("档案关联信息丢失");
            String f = "rowkey:" + relation.getArchiveId().replace(" ","+");
            envelop.setDetailModelList(
                    getArchiveInfos(resource.getResources("RS_PATIENT_INFO", appId, "{\"q\":\"" + f + "\"}", null, null))
            );
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }

    private List getArchiveInfos(Envelop main) throws Exception
    {
        String re = "";
        Map<String, ArApplyModel> arApplyModels = new HashMap<>();
        List<ArApplyModel> ls = new ArrayList<>();
        if(main.getDetailModelList() != null && main.getDetailModelList().size() > 0)
        {
            //主表rowkey条件
            StringBuilder rowkeys = new StringBuilder();
            ArApplyModel arApplyModel;
            for(Map<String,Object> map : (List<Map<String,Object>>)main.getDetailModelList())
            {
                arApplyModel = new ArApplyModel();
                arApplyModel.setVisOrg(String.valueOf(map.get("org_code")));
                arApplyModel.setVisOrgName(String.valueOf(map.get("org_name")));
                arApplyModel.setVisDate(String.valueOf(map.get("event_date")));
                arApplyModel.setCardNo(String.valueOf(map.get("card_no")));
                arApplyModels.put(map.get("rowkey").toString(), arApplyModel);
                ls.add(arApplyModel);
                if(rowkeys.length() > 0)
                {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + map.get("rowkey").toString());
            }

            re = "(" + rowkeys.toString() +")";
            re =  "{\"q\":\""+ re.replace(" ","+") +"\"}" ;
            if(re!=null){
                //门诊诊断  无数据
                Envelop tmp = resource.getResources("RS_OUTPATIENT_DIAGNOSIS", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setDiagnosedResult(
                                    (StringUtils.isEmpty(arApplyModel.getDiagnosedResult()) ? "" : ",") + map.get("EHR_000112") );
                            arApplyModel.setVisDoctor(String.valueOf(map.get("EHR_000106")));
                        }
                    }
                }
                //住院诊断
                tmp = resource.getResources("RS_HOSPITALIZED_DIAGNOSIS", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setDiagnosedResult(
                                    (StringUtils.isEmpty(arApplyModel.getDiagnosedResult()) ? "" : ",") + map.get("EHR_000295") );
                            arApplyModel.setVisDoctor(String.valueOf(map.get("EHR_000290")));
                        }
                    }
                }
                //检验报告单
                tmp = resource.getResources("RS_LABORATORY_REPORT", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setDiagnosedResult(
                                    (StringUtils.isEmpty(arApplyModel.getDiagnosedResult()) ? "" : ",") + map.get("EHR_000352") );
                        }
                    }
                }
                //检查报告单
                tmp = resource.getResources("RS_EXAMINATION_REPORT", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setDiagnosedResult(
                                    (StringUtils.isEmpty(arApplyModel.getDiagnosedResult()) ? "" : ",") + map.get("EHR_000317") );
                        }
                    }
                }
                //诊断开药： 西药
                tmp = resource.getResources("medicationWestern", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setMedicines(
                                    (StringUtils.isEmpty(arApplyModel.getMedicines()) ? "" : ",") + map.get("EHR_000100") );
                        }
                    }
                }

                //诊断开药： 中药
                tmp = resource.getResources("RS_MEDICATION_CHINESE", appId, re, null, null);
                if(tmp.getDetailModelList()!=null && tmp.getDetailModelList().size()>0){
                    for(Map<String,Object> map : (List<Map<String,Object>>)tmp.getDetailModelList()) {
                        if((arApplyModel = arApplyModels.get(map.get("profile_id"))) != null){
                            arApplyModel.setMedicines(
                                    (StringUtils.isEmpty(arApplyModel.getMedicines()) ? "" : ",") + map.get("EHR_000131"));
                        }
                    }
                }
            }
        }
        return ls;
    }

    private ArApplyModel convertModel(MArApply arApply){
        ArApplyModel model = getModel(arApply);
        if(!StringUtils.isEmpty(model.getStatus())){
            MDictionaryEntry statusDicts = systemDictClient.getDictEntry(36, model.getStatus());
            if(statusDicts!=null)
                model.setStatusName(statusDicts.getValue());
        }

        if(!StringUtils.isEmpty(model.getVisOrg())){
            MOrganization org = organizationClient.getOrg(model.getVisOrg());
            if(org!=null)
                model.setStatusName(org.getFullName());
        }

        model.setApplyDate(dt2Str(arApply.getApplyDate()));
        model.setAuditDate(dt2Str(arApply.getAuditDate()));

        return model;
    }

    private List convertModels(List<MArApply> arApplies){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=36", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }
        List ls = new ArrayList<>();
        ArApplyModel model;
        for(MArApply arApply : arApplies){
            model = getModel(arApply);
            model.setStatusName(statusMap.get(arApply.getStatus()));
            model.setApplyDate(dt2Str(arApply.getApplyDate()));
            model.setAuditDate(dt2Str(arApply.getAuditDate()));
            ls.add(model);
        }
        return ls;
    }
}
