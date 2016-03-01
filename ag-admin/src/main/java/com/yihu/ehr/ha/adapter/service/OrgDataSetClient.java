package com.yihu.ehr.ha.adapter.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by AndyCai on 2016/2/29.
 */
@FeignClient(MicroServices.AdaptionMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface OrgDataSetClient {

    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    MOrgDataSet getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) ;

    @RequestMapping(value = "/dataset", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    boolean createOrgDataSet(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);


    @RequestMapping(value = "/dataset", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id);


    @RequestMapping(value = "/dataset", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    boolean updateOrgDataSet(
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id") Long id,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId);



    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    Collection<MOrgDataSet> searchAdapterOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) ;
}
