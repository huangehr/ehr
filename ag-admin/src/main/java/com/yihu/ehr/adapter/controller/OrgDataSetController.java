package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDataSetDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.adapter.service.OrgDataSetClient;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
@Api(protocols = "https", value = "adapter", description = "数据集", tags = {"数据集"})
public class OrgDataSetController extends BaseController {

    @Autowired
    private OrgDataSetClient orgDataSetClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Envelop getOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) {

        try {
            MOrgDataSet mOrgDataSet = orgDataSetClient.getOrgDataSet(id);
            OrgDataSetDetailModel dataSetModel = convertToOrgDataSetDetailModel(mOrgDataSet);
            if (dataSetModel == null) {
                return failed("数据集信息获取失败!");
            }
            return success(dataSetModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }

    }

    @RequestMapping(value = "/data_set", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构数据集")
    public Envelop saveOrgDataSet(
            @ApiParam(name = "json_data", value = "json_data", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) {

        try {
            OrgDataSetDetailModel detailModel = objectMapper.readValue(jsonData, OrgDataSetDetailModel.class);

            String errorMsg = "";
            if (StringUtils.isEmpty(detailModel.getOrganization())) {
                errorMsg += "机构代码不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getCode())) {
                errorMsg += "数据集代码不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "数据集名称不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            boolean isExist = orgDataSetClient.dataSetIsExist(detailModel.getOrganization(), detailModel.getCode());

            MOrgDataSet mOrgDataSet = convertToMOrgDataSet(detailModel);
            if (detailModel.getId() == 0) {
                if (isExist) {
                    return failed("数据集已存在!");
                }
                mOrgDataSet = orgDataSetClient.createOrgDataSet(objectMapper.writeValueAsString(mOrgDataSet));
            } else {
                MOrgDataSet orgDataSet = orgDataSetClient.getOrgDataSet(detailModel.getId());
                if (!orgDataSet.getCode().equals(detailModel.getCode())
                        && isExist) {
                    return failed("数据集已存在!");
                }
                //mOrgDataSet.setUpdateDate(new Date());
                mOrgDataSet = orgDataSetClient.updateOrgDataSet(objectMapper.writeValueAsString(mOrgDataSet));
            }

            detailModel = convertToOrgDataSetDetailModel(mOrgDataSet);
            if (detailModel == null) {
                return failed("保存失败!");
            }
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/data_set/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构数据集")
    public Envelop deleteOrgDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) {

        try {
            if (id == 0) {
                return failed("请选择需要删除数据集!");
            }

            boolean result = orgDataSetClient.deleteOrgDataSet(id);
            if (!result) {
                return failed("删除失败!");
            }

            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
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
            @RequestParam(value = "page", required = false) int page) {

        try {
            ResponseEntity<Collection<MOrgDataSet>> responseEntity = orgDataSetClient.searchAdapterOrg(fields, filters, sorts, size, page);
            List<MOrgDataSet> mOrgDataSets = (List<MOrgDataSet>) responseEntity.getBody();
            List<OrgDataSetDetailModel> detailModels =new ArrayList<> ();
            //List<OrgDataSetDetailModel>) convertToModels(mOrgDataSets, new ArrayList<OrgDataSetDetailModel>(mOrgDataSets.size()), OrgDataSetDetailModel.class, null);
            for(MOrgDataSet mOrgDataSet : mOrgDataSets)
            {
                OrgDataSetDetailModel detailModel = convertToOrgDataSetDetailModel(mOrgDataSet);
                detailModels.add(detailModel);
            }
            return getResult(detailModels, getTotalCount(responseEntity), page, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/data_set", method = RequestMethod.GET)
    public Envelop getDataSetBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") long sequence) {

        try {
            MOrgDataSet mOrgDataSet = orgDataSetClient.getDataSetBySequence(orgCode, sequence);
            OrgDataSetDetailModel dataSetModel = convertToOrgDataSetDetailModel(mOrgDataSet);

            return success(dataSetModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    public MOrgDataSet convertToMOrgDataSet(OrgDataSetDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrgDataSet mOrgDataSet = convertToModel(detailModel,MOrgDataSet.class);
        mOrgDataSet.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
        mOrgDataSet.setUpdateDate(StringToDate(detailModel.getUpdateDate(),AgAdminConstants.DateTimeFormat));
        return mOrgDataSet;
    }

    public OrgDataSetDetailModel convertToOrgDataSetDetailModel(MOrgDataSet mOrgDataSet)
    {
        if(mOrgDataSet==null)
        {
            return null;
        }
        OrgDataSetDetailModel detailModel = convertToModel(mOrgDataSet, OrgDataSetDetailModel.class);
        detailModel.setCreateDate(DateToString(mOrgDataSet.getCreateDate(),AgAdminConstants.DateTimeFormat));
        detailModel.setUpdateDate(DateToString(mOrgDataSet.getUpdateDate(),AgAdminConstants.DateTimeFormat));
        return detailModel;
    }
}
