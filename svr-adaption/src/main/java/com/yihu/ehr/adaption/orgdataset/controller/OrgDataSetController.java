package com.yihu.ehr.adaption.orgdataset.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSet;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSetService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(ApiVersion.Version1_0 + "/adapter/org")
@Api(protocols = "https", value = "orgdataset", description = "机构数据集管理接口", tags = {"机构数据集"})

public class OrgDataSetController extends ExtendController<MOrgDataSet> {

    @Autowired
    private OrgDataSetService orgDataSetService;

    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public MOrgDataSet getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{

        return getModel(orgDataSetService.retrieve(id));
    }

    @RequestMapping(value = "/dataset", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public boolean createOrgDataSet(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{

        if (orgDataSetService.isExistOrgDataSet(orgCode, code, name))
            throw new ApiException(ErrorCode.RepeatOrgDataSet, "该数据集已存在!");
        OrgDataSet orgDataSet = new OrgDataSet();
        orgDataSet.setCode(code);
        orgDataSet.setName(name);
        orgDataSet.setDescription(description);
        orgDataSet.setOrganization(orgCode);
        orgDataSet.setCreateDate(new Date());
        orgDataSet.setCreateUser(userId);
        orgDataSetService.createOrgDataSet(orgDataSet);
        return true;
    }


    @RequestMapping(value = "/dataset", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id) throws Exception{

            orgDataSetService.deleteOrgDataSet(id);
            return true;
    }


    @RequestMapping(value = "/dataset", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    public boolean updateOrgDataSet(
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
            @RequestParam(value = "userId") String userId) throws Exception{

        OrgDataSet orgDataSet = orgDataSetService.retrieve(id);
        if (orgDataSet == null)
            throw errNotFound();
        if (orgDataSet.getCode().equals(code)
                || !orgDataSetService.isExistOrgDataSet(orgCode, code, name)) {
            orgDataSet.setCode(code);
            orgDataSet.setName(name);
            orgDataSet.setDescription(description);
            orgDataSet.setUpdateDate(new Date());
            orgDataSet.setUpdateUser(userId);
            orgDataSetService.save(orgDataSet);
            return true;
        } else
            throw new ApiException(ErrorCode.RepeatOrgDataSet, "该数据集已存在!");

    }



    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Collection<MOrgDataSet> searchAdapterOrg(
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

        List appList = orgDataSetService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgDataSetService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MOrgDataSet.class, fields);
    }
}
