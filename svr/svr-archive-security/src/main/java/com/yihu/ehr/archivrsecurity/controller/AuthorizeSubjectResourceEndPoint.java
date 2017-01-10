package com.yihu.ehr.archivrsecurity.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubjectResource;
import com.yihu.ehr.archivrsecurity.service.AuthorizeSubjectResourceService;
import com.yihu.ehr.model.archivesecurity.MRsAuthorizeSubjectResource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
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

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjectsResources,method = RequestMethod.POST)
    @ApiOperation("授权主题资源新增")
    public Collection<MRsAuthorizeSubjectResource> saveSubjectResource(
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
        subjectResourceService.saveSubjectResource(subjectResources);
        return convertToModels(subjectResources,new ArrayList<MRsAuthorizeSubjectResource>(subjectResources.size())
                ,MRsAuthorizeSubjectResource.class,"");
    }

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjectsResources,method = RequestMethod.DELETE)
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

    @RequestMapping(value = ServiceApi.ArchiveSecurity.AuthorizeSubjectsResources,method = RequestMethod.GET)
    @ApiOperation("授权主题资源查询")
    public Collection<MRsAuthorizeSubjectResource> getSubjectResources(
            @ApiParam(value = "主题标识")
            @PathVariable(value = "subjectId")String subjectId)
    {
        List<RsAuthorizeSubjectResource>  subjectResources = subjectResourceService.getSubjectResources(subjectId);
        return convertToModels(subjectResources,new ArrayList<MRsAuthorizeSubjectResource>(subjectResources.size())
                ,MRsAuthorizeSubjectResource.class,"");
    }
}
