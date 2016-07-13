package com.yihu.ehr.archivrsecurity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.ScAuthorizeDoctorApply;
import com.yihu.ehr.archivrsecurity.service.DoctorApplyService;
import com.yihu.ehr.model.archivesecurity.MScAuthorizeDoctorApply;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by lyr on 2016/7/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "/authorize_doctor_apply",description = "医生授权申请")
public class DoctorApplyEndPoint extends BaseRestEndPoint {

    @Autowired
    DoctorApplyService doctorApplyService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctors,method = RequestMethod.POST)
    @ApiOperation("医生授权申请新增")
    public MScAuthorizeDoctorApply saveDoctorApply(
            @ApiParam(value="jsonData")
            @RequestParam(value = "jsonData",required = true)
                    String jsonData) throws Exception {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        ScAuthorizeDoctorApply doctorApply = objectMapper.readValue(jsonData,ScAuthorizeDoctorApply.class);
        return convertToModel(doctorApplyService.save(doctorApply),MScAuthorizeDoctorApply.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctors,method = RequestMethod.PUT)
    @ApiOperation("医生授权申请更新")
    public MScAuthorizeDoctorApply updateDoctorApply(
            @ApiParam(value="jsonData")
            @RequestParam(value = "jsonData",required = true)
                    String jsonData) throws Exception {
        objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.simpleDatePattern));
        ScAuthorizeDoctorApply doctorApply = objectMapper.readValue(jsonData,ScAuthorizeDoctorApply.class);
        return convertToModel(doctorApplyService.save(doctorApply),MScAuthorizeDoctorApply.class);
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctorsId,method = RequestMethod.DELETE)
    @ApiOperation("医生授权申请删除")
    public boolean deleteDoctorApply(
            @ApiParam(value="id")
            @RequestParam(value = "id",required = true) String id)
    {
        doctorApplyService.deleteDoctorApply(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctorsIdAlteration,method = RequestMethod.PUT)
    @ApiOperation("医生授权变更")
    public boolean modifyDoctorApply(
            @ApiParam(value="id")
            @PathVariable(value = "id")String id,
            @ApiParam(value="authorizeType")
            @RequestParam(value = "authorizeType",required = false) int authorizeType,
            @ApiParam(value="authorizeScope")
            @RequestParam(value = "authorizeScope",required = false) int authorizeScope,
            @ApiParam(value="startTime")
            @RequestParam(value = "startTime",required = false) String startTime,
            @ApiParam(value="endTime")
            @RequestParam(value = "endTime",required = false) String endTime) throws Exception
    {
        Date start  = DateTimeUtil.simpleDateTimeParse(startTime);
        Date end = DateTimeUtil.simpleDateTimeParse(endTime);
        doctorApplyService.modifyDoctorAuthorize(id,authorizeType,authorizeScope,start,end);
        return true;
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctorsIdAuthorization,method = RequestMethod.PUT)
    @ApiOperation("医生授权变更")
    public boolean authorizeDoctorApply(
            @ApiParam(value="id")
            @PathVariable(value = "id")String id,
            @ApiParam(value="authorizeStatus")
            @RequestParam(value = "authorizeStatus",required = true) int authorizeStatus,
            @ApiParam(value="authorizeMode")
            @RequestParam(value = "authorizeMode",required = false) int authorizeMode) throws Exception
    {
        doctorApplyService.authorizeDoctorApply(id,authorizeStatus,authorizeMode);
        return true;
    }


    @ApiOperation("医生授权申请查询")
    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeDoctors,method = RequestMethod.GET)
    public Collection<MScAuthorizeDoctorApply> getDoctorAuthorize(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(value="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(value="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MScAuthorizeDoctorApply> rsList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<ScAuthorizeDoctorApply> doctorApplies = doctorApplyService.getDoctorApply(sorts,reducePage(page),size);
            total = doctorApplies.getTotalElements();
            rsList = convertToModels(doctorApplies.getContent(),new ArrayList<>(doctorApplies.getNumber()),MScAuthorizeDoctorApply.class,fields);
        }
        else
        {
            List<ScAuthorizeDoctorApply> doctorApplies = doctorApplyService.search(fields,filters,sorts,page,size);
            total = doctorApplyService.getCount(filters);
            rsList = convertToModels(doctorApplies,new ArrayList<>(doctorApplies.size()),MScAuthorizeDoctorApply.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return rsList;
    }
}
