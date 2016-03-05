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

    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public MOrgDataSet getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") Long id) throws Exception{

        return getModel(orgDataSetService.retrieve(id));
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public MOrgDataSet createOrgDataSet(
            @ApiParam(name = "model", value = "适配字典数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDataSet orgDataSet = jsonToObj(model, OrgDataSet.class);
        orgDataSet.setCreateDate(new Date());
        return getModel(orgDataSetService.createOrgDataSet(orgDataSet));
    }


    @RequestMapping(value = "/data_set", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") long id) throws Exception{

            orgDataSetService.deleteOrgDataSet(id);
            return true;
    }


    @RequestMapping(value = "/data_set", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    public MOrgDataSet updateOrgDataSet(
            @ApiParam(name = "model", value = "适配字典数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDataSet dataModel = jsonToObj(model, OrgDataSet.class);
        OrgDataSet orgDataSet = orgDataSetService.retrieve(dataModel.getId());
        if (orgDataSet == null)
            throw errNotFound();
        if (orgDataSet.getCode().equals(dataModel.getCode())
                || !orgDataSetService.isExistOrgDataSet(dataModel.getOrganization(), dataModel.getCode(), dataModel.getName())) {

            dataModel.setUpdateDate(new Date());
            return getModel(orgDataSetService.save(dataModel));
        } else
            throw new ApiException(ErrorCode.RepeatOrgDataSet, "该数据集已存在!");
    }



    @RequestMapping(value = "/data_sets", method = RequestMethod.GET)
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

    @RequestMapping(value = "/is_exist", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public boolean isExistOrgDataSet(
            @ApiParam(name = "org_code",value = "机构代码")
            @RequestParam(value = "org_code",required = false)String orgCode,
            @ApiParam(name="code",value="数据集代码")
            @RequestParam(value = "code",required = false)String code,
            @ApiParam(name="name",value="数据集名称")
            @RequestParam(value = "name",required = false)String name){
       return orgDataSetService.isExistOrgDataSet(orgCode, code, name);
    }

}
