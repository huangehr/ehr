package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubjectResource;
import com.yihu.ehr.archivrsecurity.service.AuthorizeSubjectResourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyr on 2016/7/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "authorize_subject_resource",description = "授权主题资源映射")
public class AuthorizeSubjectResourceEndPoint extends BaseRestEndPoint {

    @Autowired
    AuthorizeSubjectResourceService subjectResourceService;

    @RequestMapping(value = "/authorizesubjects/{subjectId}/resources",method = RequestMethod.POST)
    @ApiOperation("授权主题资源新增")
    public List<RsAuthorizeSubjectResource> saveSubjectResource(
            @ApiParam(value = "主题标识")
            @PathVariable(value = "subjectId")String subjectId,
            @ApiParam(value = "资源标识")
            @RequestParam(value = "resourceId",required = true)String resourceId)
    {
        String[] resourceIds = resourceId.split(",");
        List<RsAuthorizeSubjectResource> subjectResources =  new ArrayList<RsAuthorizeSubjectResource>();

        for(String resource : resourceIds)
        {
            RsAuthorizeSubjectResource  subjectResource = new RsAuthorizeSubjectResource();

            subjectResource.setSubjectId(subjectId);
            subjectResource.setResourceId(resource);

            subjectResources.add(subjectResource);
        }

        return subjectResourceService.saveSubjectResource(subjectResources);
    }

    @RequestMapping(value = "/authorizesubjects/{subjectId}/resources",method = RequestMethod.DELETE)
    @ApiOperation("授权主题资源删除")
    public boolean deleteSubjectResource(
            @ApiParam(value = "主题标识")
            @PathVariable(value = "subjectId")String subjectId,
            @ApiParam(value = "资源标识")
            @RequestParam(value = "resourceId",required = false)String resourceId)
    {
        subjectResourceService.deleteSubjectResouce(subjectId,resourceId);
        return true;
    }

    @RequestMapping(value = "/authorizesubjects/{subjectId}/resources",method = RequestMethod.GET)
    @ApiOperation("授权主题资源查询")
    public List<RsAuthorizeSubjectResource> getSubjectResources(
            @ApiParam(value = "主题标识")
            @PathVariable(value = "subjectId")String subjectId)
    {
        return subjectResourceService.getSubjectResources(subjectId);
    }
}
