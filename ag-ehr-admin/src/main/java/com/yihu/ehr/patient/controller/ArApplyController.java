package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.patient.ArApplyModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.patient.MArApply;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.patient.service.ArApplyClient;
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

    @Autowired
    ArApplyClient arApplyClient;
    @Autowired
    SystemDictClient systemDictClient;
    @Autowired
    OrganizationClient organizationClient;

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

            //todo 通过申请的信息检索档案列表， 将检索出的信息放在DetailModelList中
//            envelop.setDetailModelList();
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
            Envelop envelop = success(convertModel(arApply));

            //todo 通过申请id获取关联的档案编号， 取得档案信息， 将信息放到DetailModelList中
//            envelop.setDetailModelList();
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
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
