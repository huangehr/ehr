package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgMetaDataDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.adapter.service.OrgDataSetClient;
import com.yihu.ehr.adapter.service.OrgMetaDataClient;
import com.yihu.ehr.model.adaption.MOrgDataSet;
import com.yihu.ehr.model.adaption.MOrgMetaData;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
@Api(protocols = "https", value = "adapter", description = "机构字典项", tags = {"机构字典项"})
public class OrgMetaDataController extends BaseController {

    @Autowired
    private OrgMetaDataClient orgMetaDataClient;

    @Autowired
    private OrgDataSetClient orgDataSetClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/meta_data/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取机构数据元")
    public Envelop getOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception {

        MOrgMetaData mOrgMetaData = orgMetaDataClient.getOrgMetaData(id);
        OrgMetaDataDetailModel detailModel = convertToOrgMetaDataDetailModel(mOrgMetaData);
        if (detailModel == null) {
            return failed("数据元信息获取失败!");
        }

        return success(detailModel);
    }


    @RequestMapping(value = "/meta_data", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    public Envelop saveOrgMetaData(
            @ApiParam(name = "json_data", value = "json_data", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        OrgMetaDataDetailModel detailModel = objectMapper.readValue(jsonData, OrgMetaDataDetailModel.class);
        String errorMsg = "";
        if (StringUtils.isEmpty(detailModel.getCode())) {
            errorMsg += "代码不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "名称不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getOrganization())) {
            errorMsg += "机构不能为空!";
        }

        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        boolean isExist = orgMetaDataClient.isExistMetaData(detailModel.getOrgDataSet(),detailModel.getOrganization(),detailModel.getCode());
        MOrgMetaData mOrgMetaData = convertToMOrgMetaData(detailModel);
        if (detailModel.getId() == 0) {
            if(isExist)
            {
                return failed("数据元已存在!");
            }
            mOrgMetaData = orgMetaDataClient.createOrgMetaData(objectMapper.writeValueAsString(mOrgMetaData));
        } else {
            MOrgMetaData orgMetaData = orgMetaDataClient.getOrgMetaData(detailModel.getId());
            if(!orgMetaData.getCode().equals(mOrgMetaData.getCode())
                    && isExist){
                return failed("数据元已存在!");
            }
            BeanUtils.copyProperties(mOrgMetaData, orgMetaData, "id", "createDate", "createUser", "columnType", "columnLength", "sequence");
            mOrgMetaData = orgMetaDataClient.updateOrgMetaData(objectMapper.writeValueAsString(orgMetaData));
        }
        detailModel = convertToOrgMetaDataDetailModel(mOrgMetaData);
        if (detailModel == null) {

            return failed("保存失败!");
        }
        return success(detailModel);
    }

    @RequestMapping(value = "/meta_data", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    public Envelop deleteOrgMetaDataList(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        ids = trimEnd(ids,",");
        if(StringUtils.isEmpty(ids))
        {
            return failed("请选择需要删除的数据元!");
        }
        boolean result = orgMetaDataClient.deleteOrgMetaDataList(ids);
        if(!result)
        {
            return failed("删除失败!");
        }

        return success(null);
    }

    @RequestMapping(value = "/meta_datas", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    public Envelop searchOrgMetaData(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        ResponseEntity<Collection<MOrgMetaData>> responseEntity = orgMetaDataClient.searchOrgMetaDatas(fields,filters,sorts,size,page);

        List<MOrgMetaData> mOrgMetaDatas = (List<MOrgMetaData>)responseEntity.getBody();
        List<OrgMetaDataDetailModel> detailModels = new ArrayList<> ();//List<OrgMetaDataDetailModel>)convertToModels(mOrgMetaDatas,new ArrayList<OrgMetaDataDetailModel>(mOrgMetaDatas.size()),OrgMetaDataDetailModel.class,null);
        for(MOrgMetaData mOrgMetaData:mOrgMetaDatas)
        {
            OrgMetaDataDetailModel detailModel = convertToOrgMetaDataDetailModel(mOrgMetaData);
            detailModels.add(detailModel);
        }
        return getResult(detailModels,getTotalCount(responseEntity),page,size);
    }

    @RequestMapping(value = "/meta_data", method = RequestMethod.GET)
    public Envelop getMetaDataBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence) {

        try {
            MOrgMetaData mOrgMetaData = orgMetaDataClient.getMetaDataBySequence(orgCode, sequence);
            OrgMetaDataDetailModel metaDataDetailModel = convertToOrgMetaDataDetailModel(mOrgMetaData);

            return success(metaDataDetailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    public OrgMetaDataDetailModel convertToOrgMetaDataDetailModel(MOrgMetaData mOrgMetaData)
    {
        if(mOrgMetaData==null)
        {
            return null;
        }
        OrgMetaDataDetailModel metaDataDetailModel = convertToModel(mOrgMetaData, OrgMetaDataDetailModel.class);
        metaDataDetailModel.setCreateDate(DateToString(mOrgMetaData.getCreateDate(), AgAdminConstants.DateTimeFormat));
        metaDataDetailModel.setUpdateDate(DateToString(mOrgMetaData.getUpdateDate(),AgAdminConstants.DateTimeFormat));

        int dataSetSeq = metaDataDetailModel.getOrgDataSet();
        if (dataSetSeq!=0)
        {
            MOrgDataSet mOrgDataSet = orgDataSetClient.getDataSetBySequence(metaDataDetailModel.getOrganization(),dataSetSeq);
            metaDataDetailModel.setDataSetCode(mOrgDataSet==null?"":mOrgDataSet.getCode());
            metaDataDetailModel.setDataSetName(mOrgDataSet==null?"":mOrgDataSet.getName());
        }

        return metaDataDetailModel;
    }

    public MOrgMetaData convertToMOrgMetaData(OrgMetaDataDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrgMetaData mOrgMetaData = convertToModel(detailModel,MOrgMetaData.class);
        mOrgMetaData.setCreateDate(StringToDate(detailModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        mOrgMetaData.setUpdateDate(StringToDate(detailModel.getUpdateDate(),AgAdminConstants.DateTimeFormat));

        return mOrgMetaData;
    }
}
