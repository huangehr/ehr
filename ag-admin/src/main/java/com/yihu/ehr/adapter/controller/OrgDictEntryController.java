package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictEntryDetailModel;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictEntryModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.adapter.service.OrgDictEntryClient;
import com.yihu.ehr.model.adaption.MOrgDictItem;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/3/2.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
@Api(protocols = "https", value = "adapter", description = "机构字典项", tags = {"机构字典项"})
public class OrgDictEntryController extends BaseController {

    @Autowired
    private OrgDictEntryClient orgDictEntryClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/item/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项信息")
    public Envelop getOrgDictItem(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception {

        MOrgDictItem mOrgDictItem = orgDictEntryClient.getOrgDictItem(id);
        if (mOrgDictItem == null) {
            return failed("字典明细获取失败!");
        }
        OrgDictEntryDetailModel detailModel = convertToOrgDictEntryDetailModel(mOrgDictItem);

        return success(detailModel);
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    public Envelop saveOrgDictItem(
            @ApiParam(name = "json_data", value = "字典项信息", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        OrgDictEntryDetailModel detailModel = objectMapper.readValue(jsonData, OrgDictEntryDetailModel.class);
        String errorMsg = "";
        if (StringUtils.isEmpty(detailModel.getCode())) {
            errorMsg += "代码不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "值不能为空!";
        }
        if (detailModel.getOrgDict() == 0) {
            errorMsg += "请先选择对应的字典!";
        }
        if (StringUtils.isEmpty(detailModel.getOrganization())) {
            errorMsg += "请先选择对应的机构!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }

        boolean isExist = orgDictEntryClient.isExistDictItem(detailModel.getOrgDict(),detailModel.getOrganization(),detailModel.getCode());

        MOrgDictItem mOrgDictItem = convertToMOrgDictItem(detailModel);
        if (mOrgDictItem.getId() == 0) {
            if(isExist)
            {
                return failed("字典项已存在!");
            }
            mOrgDictItem = orgDictEntryClient.createOrgDictItem(objectMapper.writeValueAsString(mOrgDictItem));
        } else {
            MOrgDictItem dictItem = orgDictEntryClient.getOrgDictItem(detailModel.getId());
            if(!dictItem.getCode().equals(detailModel.getCode())
                    && isExist)
            {
                return failed("字典项已存在!");
            }
            BeanUtils.copyProperties(mOrgDictItem, dictItem, "id", "createDate", "createUser", "sequence");
            mOrgDictItem = orgDictEntryClient.updateDictItem(objectMapper.writeValueAsString(dictItem));
        }
        if (mOrgDictItem == null) {
            return failed("保存失败!");
        }
        detailModel = convertToOrgDictEntryDetailModel(mOrgDictItem);

        return success(detailModel);
    }


    @RequestMapping(value = "/item", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    public Envelop deleteOrgDictItem(
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids", required = false) String ids) throws Exception {

        ids = trimEnd(ids,",");
        boolean result = orgDictEntryClient.deleteOrgDictItemList(ids);
        if(!result)
        {
            return failed("删除失败!");
        }
        return success(null);
    }


    @RequestMapping(value = "/items", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    public Envelop searchOrgDictItems(
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

        ResponseEntity<Collection<MOrgDictItem>> responseEntity = orgDictEntryClient.searchOrgDictItems(fields, filters, sorts, size, page);
        List<MOrgDictItem> dictItems = (List<MOrgDictItem>) responseEntity.getBody();
        List<OrgDictEntryModel> detailModels = (List<OrgDictEntryModel>) convertToModels(dictItems,
                                                                                                    new ArrayList<OrgDictEntryModel>(dictItems.size()),
                                                                                                    OrgDictEntryModel.class,
                                                                                                    null);

        return getResult(detailModels,getTotalCount(responseEntity),page,size);
    }

    @RequestMapping(value = "/items/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典项下拉")
    public List<String> getOrgDictEntry(
            @ApiParam(name = "orgDictSeq", value = "字典seq", defaultValue = "")
            @RequestParam(value = "orgDictSeq") long orgDictSeq,
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode) throws Exception {


        return (List<String>)orgDictEntryClient.getOrgDictEntry(orgDictSeq,orgCode);
    }

    @RequestMapping(value = "/dict/dict_entry", method = RequestMethod.GET)
    public Envelop getOrgDictEntryBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence) {
        try {
            MOrgDictItem mOrgDictItem = orgDictEntryClient.getOrgDicEntryBySequence(orgCode, sequence);
            OrgDictEntryDetailModel detailModel = convertToOrgDictEntryDetailModel(mOrgDictItem);

            return success(detailModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }

    public OrgDictEntryDetailModel convertToOrgDictEntryDetailModel(MOrgDictItem mOrgDictItem)
    {
        if(mOrgDictItem==null)
        {
            return null;
        }
        OrgDictEntryDetailModel detailModel = convertToModel(mOrgDictItem,OrgDictEntryDetailModel.class);
        detailModel.setCreateDate(DateToString(mOrgDictItem.getCreateDate(), AgAdminConstants.DateTimeFormat));
        detailModel.setUpdateDate(DateToString(mOrgDictItem.getUpdateDate(),AgAdminConstants.DateTimeFormat));
        return detailModel;
    }

    public MOrgDictItem convertToMOrgDictItem(OrgDictEntryDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrgDictItem mOrgDictItem = convertToModel(detailModel,MOrgDictItem.class);
        mOrgDictItem.setCreateDate(StringToDate(detailModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        mOrgDictItem.setUpdateDate(StringToDate(detailModel.getUpdateDate(),AgAdminConstants.DateTimeFormat));

        return mOrgDictItem;
    }
}
