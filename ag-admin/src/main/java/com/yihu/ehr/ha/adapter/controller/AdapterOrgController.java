package com.yihu.ehr.ha.adapter.controller;

import com.yihu.ehr.agModel.thirdpartystandard.AdapterOrgModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.adapter.service.AdapterOrgClient;
import com.yihu.ehr.model.adaption.MAdapterOrg;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/26.
 */
@RequestMapping(ApiVersion.Version1_0 + "/adapterOrg")
@RestController
public class AdapterOrgController extends BaseController {

    @Autowired
    private AdapterOrgClient adapterOrgClient;

    @RequestMapping(value = "/orgs", method = RequestMethod.GET)
    @ApiOperation(value = "适配采集标准")
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

        List<MAdapterOrg> mAdapterOrgs = (List<MAdapterOrg>)adapterOrgClient.searchAdapterOrg(fields,filters,sorts,size,page);

        List<AdapterOrgModel> adapterOrgModels = (List<AdapterOrgModel>)convertToModels(mAdapterOrgs, new ArrayList<AdapterOrgModel>(mAdapterOrgs.size()), AdapterOrgModel.class,null);

        return getResult(adapterOrgModels,1,page,size);
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配采集标准")
    public Envelop getAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code) throws Exception{

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);

        MAdapterOrg mAdapterOrg = adapterOrgClient.getAdapterOrg(code);

        AdapterOrgModel adapterOrgModel = convertToModel(mAdapterOrg,AdapterOrgModel.class);
        if(adapterOrgModel==null)
        {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("适配机构信息获取失败!");
        }

        envelop.setObj(mAdapterOrg);
        return envelop;
    }


    @RequestMapping(value = "/org", method = RequestMethod.POST)
    @ApiOperation(value = "新增采集标准")
    public boolean addAdapterOrg(
            @ApiParam(name = "adapterOrg", value = "采集机构模型", defaultValue = "")
            @RequestParam(value = "adapterOrg", required = false) String adapterOrg) throws Exception{


        return true;
    }


    @RequestMapping(value = "/org/{code}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新采集标准")
    public boolean updateAdapterOrg(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @PathVariable(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception{


        return true;
    }


    @RequestMapping(value = "/orgs", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除采集标准")
    public boolean delAdapterOrg(
            @ApiParam(name = "codes", value = "代码", defaultValue = "")
            @PathVariable(value = "codes") String codes) throws Exception{


        return true;
    }


    @RequestMapping(value = "/{org}/isExistAdapterData", method = RequestMethod.GET)
    @ApiOperation(value = "判断采集机构是否存在采集数据")
    public boolean orgIsExistData(
            @ApiParam(name = "org_code", value = "机构", defaultValue = "")
            @PathVariable(value = "org_code") String orgCode) throws Exception{

        return adapterOrgClient.orgIsExistData(orgCode);
    }
}
