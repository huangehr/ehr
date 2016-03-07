package com.yihu.ehr.adaption.adapterorg.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrg;
import com.yihu.ehr.adaption.adapterorg.service.AdapterOrgService;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/adapter")
@Api(protocols = "https", value = "adapterorg", description = "第三方标准管理接口", tags = {"第三方标准"})
public class AdapterOrgController extends ExtendController<MAdapterOrg> {
    @Autowired
    private AdapterOrgService adapterOrgService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/orgs", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
    public Collection<MAdapterOrg> searchAdapterOrg(
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
            HttpServletResponse response) throws Exception{

        List appList = adapterOrgService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, adapterOrgService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MAdapterOrg.class, fields);
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配采集标准")
    public MAdapterOrg getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) throws Exception{

        if (StringUtils.isEmpty(code))
            throw errMissCode();
        return getModel(adapterOrgService.retrieve(code));
    }


    @RequestMapping(value = "/org", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public MAdapterOrg addAdapterOrg(
            @ApiParam(name = "json_data", value = "采集机构模型", defaultValue = "")
            @RequestParam(value = "json_data", required = false) String jsonData) throws Exception{

        AdapterOrg adapterOrg = objectMapper.readValue(jsonData,AdapterOrg.class);//jsonToObj(jsonData, AdapterOrg.class);

        return getModel(adapterOrgService.addAdapterOrg(adapterOrg));
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public MAdapterOrg updateAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception{

        AdapterOrg adapterOrg = adapterOrgService.retrieve(code);
        if (adapterOrg == null)
            throw errNotFound();
        adapterOrg.setName(name);
        adapterOrg.setDescription(description);
        return getModel(adapterOrgService.save(adapterOrg));
    }


    @RequestMapping(value = "/orgs", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public boolean delAdapterOrg(
            @ApiParam(name = "codes", value = "代码", defaultValue = "")
            @RequestParam(value = "codes") String codes) throws Exception{

        if (StringUtils.isEmpty(codes))
            throw errMissCode();
        adapterOrgService.deleteAdapterOrg(codes.split(","));
        return true;
    }


    @RequestMapping(value = "/isExistAdapterData/{org}", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    public boolean orgIsExistData(
            @ApiParam(name = "org", value = "机构", defaultValue = "")
            @PathVariable(value = "org") String org) throws Exception{

        return adapterOrgService.isExistData(org);
    }

    @RequestMapping(value = "/isExistAdapterOrg/{org}", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    public boolean isExistAdapterOrg(@ApiParam(name = "org", value = "机构", defaultValue = "")
                                     @PathVariable(value = "org") String org){

        return adapterOrgService.retrieve(org) != null;
    }
}
