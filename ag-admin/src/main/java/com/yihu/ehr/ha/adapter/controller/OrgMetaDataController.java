package com.yihu.ehr.ha.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgMetaDataDetailModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.adapter.service.OrgMetaDataClient;
import com.yihu.ehr.model.adaption.MOrgMetaData;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
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
 * Created by AndyCai on 2016/3/1.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
public class OrgMetaDataController extends BaseController {

    @Autowired
    private OrgMetaDataClient orgMetaDataClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/meta_data/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取机构数据元")
    public Envelop getOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception {

        MOrgMetaData mOrgMetaData = orgMetaDataClient.getOrgMetaData(id);
        OrgMetaDataDetailModel detailModel = convertToModel(mOrgMetaData, OrgMetaDataDetailModel.class);
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
        MOrgMetaData mOrgMetaData = convertToModel(detailModel, MOrgMetaData.class);
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
           // mOrgMetaData.setUpdateDate(new Date());
            mOrgMetaData = orgMetaDataClient.updateOrgMetaData(objectMapper.writeValueAsString(mOrgMetaData));
        }
        detailModel = convertToModel(mOrgMetaData, OrgMetaDataDetailModel.class);
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
        List<OrgMetaDataDetailModel> detailModels = (List<OrgMetaDataDetailModel>)convertToModels(mOrgMetaDatas,new ArrayList<OrgMetaDataDetailModel>(mOrgMetaDatas.size()),OrgMetaDataDetailModel.class,null);

        return getResult(detailModels,getTotalCount(responseEntity),page,size);
    }

}
