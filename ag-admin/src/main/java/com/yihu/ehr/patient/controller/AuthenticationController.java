package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.patient.AuthenticationModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.patient.MAuthentication;
import com.yihu.ehr.patient.service.AuthenticationClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.util.FeignExceptionUtils;
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
@Api(protocols = "https", value = "Authentication", description = "人口身份认证", tags = {"人口管理-人口身份认证"})
public class AuthenticationController extends ExtendController<AuthenticationModel> {

    @Autowired
    AuthenticationClient authenticationClient;
    @Autowired
    SystemDictClient systemDictClient;

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.GET)
    @ApiOperation(value = "人口身份认证申请列表")
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
            @RequestParam(value = "page", required = false) int page){

        ResponseEntity<List<MAuthentication>> responseEntity = authenticationClient.search(fields, filters, sorts, size, page);
        return getResult(convertModels(responseEntity.getBody()), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.POST)
    @ApiOperation(value = "新增认证申请")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {

        try {
            return success(convertModel(authenticationClient.add(model)) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.PUT)
    @ApiOperation(value = "修改认证申请")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            AuthenticationModel authenticationModel = toEntity(model,AuthenticationModel.class);
            MAuthentication authentication = convertToMModel(authenticationModel, MAuthentication.class);
            if(authentication.getId()==0)
                return failed("编号不能为空");

            return success(convertModel(authenticationClient.update(authentication)) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除认证申请")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            authenticationClient.delete(id);
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除认证申请")
    public boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        return authenticationClient.batchDelete(ids.split(","));
    }

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.GET)
    @ApiOperation(value = "获取认证申请信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "档案关联编号", defaultValue = "")
            @PathVariable(value = "id") int id) {

        try {
            MAuthentication authentication = authenticationClient.getInfo(id);
            if(authentication == null)
                return failed("没有找到该认证申请信息！");
            return success(convertModel(authentication));
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }

    private AuthenticationModel convertModel(MAuthentication authentication){
        AuthenticationModel model = getModel(authentication);
        if(!StringUtils.isEmpty(model.getStatus())){
            MDictionaryEntry statusDicts = systemDictClient.getDictEntry(36, model.getStatus());
            if(statusDicts!=null)
                model.setStatusName(statusDicts.getValue());
        }

        if(!StringUtils.isEmpty(model.getMedicalCardType())){
            MDictionaryEntry statusDicts = systemDictClient.getDictEntry(10, model.getMedicalCardType());
            if(statusDicts!=null)
                model.setMedicalCardTypeName(statusDicts.getValue());
        }
        model.setApplyDate(dt2Str(authentication.getApplyDate()));
        model.setAuditDate(dt2Str(authentication.getAuditDate()));
        return model;
    }

    private List convertModels(List<MAuthentication> authentications){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=36", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }

        List ls = new ArrayList<>();
        AuthenticationModel model;
        for(MAuthentication authentication : authentications){
            model = getModel(authentication);
            model.setStatusName(statusMap.get(authentication.getStatus()));
            model.setApplyDate(dt2Str(authentication.getApplyDate()));
            model.setAuditDate(dt2Str(authentication.getAuditDate()));
            ls.add(model);
        }
        return ls;
    }

}
