package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MRRsResources;
import com.yihu.ehr.medicalRecord.service.MRResourcesService;
import com.yihu.ehr.medicalRecord.service.UserInfoService;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Guo Yanshan on 2016/7/12.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "medivalRecords", description = "病例夹服务接口")
public class MedicalRecords extends EnvelopRestEndPoint {
    @Autowired
    private UserInfoService uiService;

    @Autowired
    private MRResourcesService rsService;

    @RequestMapping(value = ServiceApi.MedivalRecords.MedivalRecords,method = RequestMethod.GET)
    @ApiOperation("根据LoginCode获取用户信息")
    public MUser getUserInfoByCode(
            @ApiParam(name="loginCode",value="loginCode",defaultValue = "")
            @RequestParam(value="loginCode",required = false)String loginCode,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        return convertToModel(uiService.getInfoByLoginCode(loginCode),MUser.class);
    }

    @ApiOperation("创建资源")
    @RequestMapping(value = ServiceApi.MedivalRecords.MedivalRecords,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public MRsResources createResource(
            @ApiParam(name="resource",value="资源",defaultValue = "")
            @RequestBody String resource) throws Exception
    {
        MRRsResources rs = toEntity(resource,MRRsResources.class);
        rs.setId(getObjectId(BizObject.Resources));
        rsService.saveResource(rs);
        return convertToModel(rs,MRsResources.class);
    }

    @ApiOperation("查询资源")
    @RequestMapping(value = ServiceApi.MedivalRecords.MedivalRecordsId,method = RequestMethod.GET)
    public MRsResources getInfoById(
            @ApiParam(name="id",value="资源ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        return convertToModel(rsService.getInfoById(id),MRsResources.class);
    }

    @ApiOperation("删除资源")
    @RequestMapping(value = ServiceApi.MedivalRecords.MedivalRecordsId,method = RequestMethod.DELETE)
    public boolean deleteInfo(
            @ApiParam(name="id",value="资源ID",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        rsService.delete(id);
        return true;
    }
}