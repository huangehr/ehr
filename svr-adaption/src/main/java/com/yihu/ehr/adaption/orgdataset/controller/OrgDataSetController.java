package com.yihu.ehr.adaption.orgdataset.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSet;
import com.yihu.ehr.adaption.orgdataset.service.OrgDataSetService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MOrgDataSet;
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
@RequestMapping(ApiVersion.Version1_0 + "/adapter/org")
@Api(value = "orgdataset", description = "机构数据集管理接口", tags = {"机构数据集"})

public class OrgDataSetController extends ExtendController<MOrgDataSet> {

    @Autowired
    private OrgDataSetService orgDataSetService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public MOrgDataSet getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") Long id) throws Exception{

        return getModel(orgDataSetService.retrieve(id));
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public MOrgDataSet createOrgDataSet(
            @ApiParam(name = "model", value = "适配字典数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDataSet orgDataSet = objectMapper.readValue(model, OrgDataSet.class);
        orgDataSet.setCreateDate(new Date());
        return getModel(orgDataSetService.createOrgDataSet(orgDataSet));
    }


    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public boolean deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception{

            orgDataSetService.deleteOrgDataSet(id);
            return true;
    }


    @RequestMapping(value = "/data_set", method = RequestMethod.PUT)
    @ApiOperation(value = "修改机构数据集")
    public MOrgDataSet updateOrgDataSet(
            @ApiParam(name = "model", value = "适配字典数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        OrgDataSet dataModel = objectMapper.readValue(model, OrgDataSet.class);
        dataModel.setUpdateDate(new Date());
        return getModel(orgDataSetService.save(dataModel));

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
    public boolean dataSetIsExist(
            @ApiParam(name = "org_code",value = "机构代码",defaultValue = "")
            @RequestParam(value = "org_code")String orgCode,
            @ApiParam(name="code",value="数据集代码",defaultValue = "")
            @RequestParam(value = "code")String code){

       return orgDataSetService.isExistOrgDataSet(orgCode, code);
    }

    @RequestMapping(value = "/data_set",method = RequestMethod.GET)
    public MOrgDataSet getDataSetBySequence(
            @RequestParam(value="org_code")String orgCode,
            @RequestParam(value = "sequence")int sequence){
        return convertToModel(orgDataSetService.getDataSetBySequence(orgCode,sequence),MOrgDataSet.class);
    }
}
