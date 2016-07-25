package com.yihu.ehr.medicalRecord.controller;


import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorDraftEntity;
import com.yihu.ehr.medicalRecord.service.DoctorDraftService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "DoctorDraft", description = "医生文本草稿接口")
public class DoctorDraftController extends EnvelopRestEndPoint {

    @Autowired
    DoctorDraftService dDService;

    /**
     * 保存草稿
     *
     * @param doctorDraft { "content": "string",
                            "doctorId": "string",
                            "type": "string"
                            }
     * @return MrDoctorDraftEntity
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存草稿")
    public boolean saveDoctorDraft(
            @ApiParam(name="doctorDraft",value="草稿JSON",defaultValue = "")
            @RequestBody String doctorDraft) throws Exception
    {
        MrDoctorDraftEntity mrDoctorDraft = toEntity(doctorDraft,MrDoctorDraftEntity.class);

        mrDoctorDraft.setUsageCount(1); //设置默认使用次数
        dDService.saveDoctorDraft(mrDoctorDraft);
        return true;
    }

    /**
     * 获取文本草稿
     *
     * @param fields  id,doctorId,type,usageCount,content
     * @param filters doctorId,type
     * @param sorts   usageCount
     * @param page    1
     * @param size    10
     * @param request
     * @param response
     * @return List<MrDoctorDraftEntity>
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.GET)
    @ApiOperation("根据医生ID和草稿类型获取文本草稿")
    public List<MrDoctorDraftEntity> getDoctorDrafts(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "10")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MrDoctorDraftEntity> metaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<MrDoctorDraftEntity> metadataPage = dDService.getDraft(sorts,reducePage(page),size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(),new ArrayList<>(metadataPage.getNumber()),MrDoctorDraftEntity.class,fields);
        }
        else
        {
            List<MrDoctorDraftEntity> metadata = dDService.search(fields,filters,sorts,page,size);
            total = dDService.getCount(filters);
            metaList = convertToModels(metadata,new ArrayList<>(metadata.size()),MrDoctorDraftEntity.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return (List<MrDoctorDraftEntity>)metaList;
    }

    /**
     * check草稿是否存在
     * 存在：   使用次数+1
     * 不存在： 新增草稿
     *
     * @param doctorId 医生Id
     * @param type     草稿类型
     * @param content  草稿内容
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.CheckDoctorDraft,method = RequestMethod.GET)
    @ApiOperation("检查文本草稿是否存在")
    public boolean checkDoctorDraft(
            @ApiParam(name="doctorId",value="医生Id",defaultValue = "")
            @RequestParam(value="doctorId") String doctorId,
            @ApiParam(name="type",value="草稿类型",defaultValue = "")
            @RequestParam(value="type") String type,
            @ApiParam(name="content",value="草稿内容",defaultValue = "")
            @RequestParam(value="content") String content) throws Exception
    {
        dDService.checkDoctorDraft(doctorId, type, content);

        return true;
    }


    /**
     * 根据草稿Id批量删除草稿
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorDraft,method = RequestMethod.DELETE)
    @ApiOperation("批量删除草稿")
    public boolean deleteDraft(
            @ApiParam(name="id",value="草稿Id",defaultValue = "")
            @RequestParam(value="id") String ids) throws Exception
    {
        dDService.deleteDraft(ids);

        return true;
    }
}