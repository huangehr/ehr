package com.yihu.ehr.resolve.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.resolve.service.profile.ArchiveRelationService;
import com.yihu.ehr.resolve.service.profile.PrescriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ProfileManagerEndPoint", description = "档案编辑管理", tags = {"档案解析服务-档案编辑管理"})
public class ProfileManagerEndPoint {

    @Autowired
    private PrescriptionService prescriptionService;
    @Autowired
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "处方笺信息维护")
    @RequestMapping(value = ServiceApi.Packages.Prescription, method = RequestMethod.POST)
    @ResponseBody
    public String prescription(
            @ApiParam(value = "档案ID", defaultValue = "")
            @RequestParam("profileId") String profileId,
            @ApiParam(value = "处方笺列表json", defaultValue = "")
            @RequestBody String data,
            @ApiParam(value = "返回档案数据")
            @RequestParam("existed") Integer existed) throws Throwable
    {
        List<Map<String, String>> list = objectMapper.readValue(data,List.class);
        List<Map<String,Object>> re = prescriptionService.savePrescription(profileId,list,existed);

        return objectMapper.writeValueAsString(re);
    }

    @ApiOperation(value = "档案关联（单条）")
    @RequestMapping(value = ServiceApi.Packages.ArchiveRelation, method = RequestMethod.POST)
    @ResponseBody
    public Result archiveRelation(
            @ApiParam(value = "档案ID", defaultValue = "")
            @RequestParam("profileId") String profileId,
            @ApiParam(value = "身份证号码", defaultValue = "")
            @RequestParam("idCardNo") String idCardNo) throws Throwable
    {
        archiveRelationService.archiveRelation(profileId,idCardNo);

        return Result.success("档案关联成功！");
    }

}
