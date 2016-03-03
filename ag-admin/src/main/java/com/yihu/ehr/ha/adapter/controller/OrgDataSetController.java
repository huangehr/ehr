package com.yihu.ehr.ha.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.adapter.service.OrgDataSetClient;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
public class OrgDataSetController extends BaseController {

    @Autowired
    private OrgDataSetClient orgDataSetClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Envelop getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathParam(value = "id") long id) throws Exception{

        MOrgDataSet mOrgDataSet = orgDataSetClient.getOrgDataSet(id);
        OrgDataSetModel dataSetModel = convertToModel(mOrgDataSet,OrgDataSetModel.class);
        if(dataSetModel==null)
        {
            return failed("数据集信息获取失败!");
        }

        return success(dataSetModel);
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public Envelop saveOrgDataSet(
            @ApiParam(name = "json_data", value = "json_data", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception{

        OrgDataSetDetailModel detailModel = objectMapper.readValue(jsonData,OrgDataSetDetailModel.class);

        String errorMsg = "";
        if(StringUtils.isEmpty(detailModel.getOrganization()))
        {
            errorMsg+="机构代码不能为空!";
        }

        if(StringUtils.isEmpty(detailModel.getCode()))
        {
            errorMsg+="数据集代码不能为空!";
        }

        if(StringUtils.isEmpty(detailModel.getName()))
        {
            errorMsg+="数据集名称不能为空!";
        }

        if(StringUtils.isNotEmpty(errorMsg))
        {
            return failed(errorMsg);
        }

        if (orgDataSetClient.isExistOrgDataSet(detailModel.getOrganization(), detailModel.getCode(), detailModel.getName()))
        {
            return failed("代码、名称不能重复!");
        }
        MOrgDataSet mOrgDataSet = convertToModel(detailModel,MOrgDataSet.class);
        if (detailModel.getId()==0) {
            mOrgDataSet = orgDataSetClient.createOrgDataSet(objectMapper.writeValueAsString(mOrgDataSet));
        }else
        {
            mOrgDataSet = orgDataSetClient.updateOrgDataSet(objectMapper.writeValueAsString(mOrgDataSet));
        }

        detailModel = convertToModel(mOrgDataSet,OrgDataSetDetailModel.class);
        if(detailModel==null)
        {
            return failed("保存失败!");
        }
        return success(detailModel);
    }


    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public Envelop deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathParam(value = "id") long id) throws Exception{

        boolean result = orgDataSetClient.deleteOrgDataSet(id);
        if(!result)
        {
            return failed("删除失败!");
        }

        return success(null);
    }


    @RequestMapping(value = "/data_sets", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Envelop searchAdapterOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        List<MOrgDataSet> mOrgDataSets = (List<MOrgDataSet>)orgDataSetClient.searchAdapterOrg(fields,filters,sorts,size,page);
        List<OrgDataSetDetailModel> detailModels = (List<OrgDataSetDetailModel>)convertToModels(mOrgDataSets,new ArrayList<OrgDataSetDetailModel>(mOrgDataSets.size()),OrgDataSetDetailModel.class,null);

        return getResult(detailModels,1,page,size);
    }
}
