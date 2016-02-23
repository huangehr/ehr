package com.yihu.ehr.adaption.orgdict.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgdict.service.OrgDict;
import com.yihu.ehr.adaption.orgdict.service.OrgDictService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MOrgDict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/adapter/org")
@Api(protocols = "https", value = "orgdict", description = "机构字典管理接口", tags = {"机构字典"})
public class OrgDictController extends ExtendController<MOrgDict> {

    @Autowired
    private OrgDictService orgDictService;

    @RequestMapping(value = "/dict", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public MOrgDict getOrgDict(
            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id) throws Exception{

       return getModel(orgDictService.retrieve(id));
    }

    @RequestMapping(value = "/dict", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    public boolean createOrgDict(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "orgCode", value = "name", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        if (orgDictService.isExistOrgDict(orgCode, code))
            throw new ApiException(ErrorCode.RepeatOrgDict, "该字典已存在！");
        OrgDict orgDict = new OrgDict();
        orgDict.setCode(code);
        orgDict.setName(name);
        orgDict.setDescription(description);
        orgDict.setOrganization(orgCode);
        orgDict.setCreateDate(new Date());
        orgDict.setCreateUser(userId);
        orgDictService.createOrgDict(orgDict);
        return true;
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    public boolean deleteOrgDict(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) {

        orgDictService.deleteOrgDict(id);
        return true;
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构字典")
    public boolean updateOrgDict(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) {

        OrgDict orgDict = orgDictService.retrieve(id);
        if (orgDict == null)
            throw errNotFound();
        if (orgDict.getCode().equals(code) || !orgDictService.isExistOrgDict(orgCode, code)) {
            orgDict.setCode(code);
            orgDict.setName(name);
            orgDict.setDescription(description);
            orgDict.setUpdateDate(new Date());
            orgDict.setUpdateUser(userId);
            orgDictService.save(orgDict);
            return true;
        }
        else
            throw new ApiException(ErrorCode.RepeatOrgDict, "该字典已存在！");
    }



    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Collection searchOrgDicts(
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

        List appList = orgDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgDictService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MOrgDict.class, fields);
    }


    @RequestMapping(value = "/dict/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典下拉")
    public List getOrgDict(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode) throws Exception{

        List<OrgDict> orgDictList = orgDictService.findByField(orgCode, "organization");
        List<String> orgDicts = new ArrayList<>();
        for (OrgDict orgDict : orgDictList) {
            orgDicts.add(String.valueOf(orgDict.getSequence())+','+orgDict.getName());
        }
        return orgDicts;
    }

}
