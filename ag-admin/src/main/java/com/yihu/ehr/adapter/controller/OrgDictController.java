package com.yihu.ehr.adapter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.service.OrgDictClient;
import com.yihu.ehr.agModel.thirdpartystandard.OrgDictDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.adaption.MOrgDict;
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
 * Created by AndyCai on 2016/1/27.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/adapter/org")
@RestController
@Api(protocols = "https", value = "adapter", description = "机构字典", tags = {"机构字典"})
public class OrgDictController extends BaseController {

    @Autowired
    private OrgDictClient orgDictClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/dict", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询实体")
    public Envelop getOrgDict(
            @ApiParam(name = "id", value = "查询条件", defaultValue = "")
            @RequestParam(value = "id", required = false) long id) throws Exception {

        MOrgDict mOrgDict = orgDictClient.getOrgDict(id);
        if (mOrgDict == null) {
            return failed("字典明细获取失败!");
        }
        OrgDictDetailModel detailModel = convertToModel(mOrgDict, OrgDictDetailModel.class);

        return success(detailModel);
    }

    @RequestMapping(value = "/dict", method = RequestMethod.POST)
    @ApiOperation(value = "创建机构字典")
    public Envelop saveOrgDict(
            @ApiParam(name = "json_data", value = "字典信息", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {

        OrgDictDetailModel detailModel = objectMapper.readValue(jsonData, OrgDictDetailModel.class);
        String errorMsg = "";
        if (StringUtils.isEmpty(detailModel.getCode())) {
            errorMsg += "代码不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getName())) {
            errorMsg += "值不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getOrganization())) {
            errorMsg += "请先选择对应的机构!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }

        boolean isExist = orgDictClient.isExistDict(detailModel.getOrganization(), detailModel.getCode());
        MOrgDict mOrgDict = convertToMOrgDict(detailModel);
        if (mOrgDict.getId() == 0) {
            if (isExist) {
                return failed("字典已存在!");
            }
            mOrgDict = orgDictClient.createOrgDict(objectMapper.writeValueAsString(mOrgDict));
        } else {
            MOrgDict orgDict = orgDictClient.getOrgDict(mOrgDict.getId());
            if (!orgDict.getCode().equals(mOrgDict.getCode())
                    && isExist) {
                return failed("字典已存在");
            }
            BeanUtils.copyProperties(mOrgDict, orgDict, "id", "createDate", "createUser", "sequence");
            mOrgDict = orgDictClient.updateOrgDict(objectMapper.writeValueAsString(orgDict));
        }

        if (mOrgDict == null) {
            return failed("保存失败");
        }
        detailModel = convertToOrgDictDetailModel(mOrgDict);

        return success(detailModel);
    }


    @RequestMapping(value = "/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除机构字典")
    public Envelop deleteOrgDict(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) {

        boolean result = orgDictClient.deleteOrgDict(id);
        if (!result) {
            return failed("删除失败!");
        }
        return success(null);
    }


    @RequestMapping(value = "/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "条件查询")
    public Envelop searchOrgDicts(
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

        ResponseEntity<Collection<MOrgDict>> responseEntity = orgDictClient.searchOrgDicts(fields, filters, sorts, size, page);
        List<MOrgDict> dicts = (List<MOrgDict>) responseEntity.getBody();
        List<OrgDictDetailModel> detailModels =new ArrayList<> ();
        for(MOrgDict mOrgDict : dicts)
        {
            OrgDictDetailModel detailModel = convertToOrgDictDetailModel(mOrgDict);
            detailModels.add(detailModel);
        }

        return getResult(detailModels, getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = "/dict/combo", method = RequestMethod.GET)
    @ApiOperation(value = "机构字典下拉")
    public List<String> getOrgDict(
            @ApiParam(name = "orgCode", value = "机构代码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode) throws Exception {

        return orgDictClient.getOrgDict(orgCode);
    }

    @RequestMapping(value = "/dict/org_dict", method = RequestMethod.GET)
    public Envelop getOrgDictBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence) {
        try {
            MOrgDict mOrgDict = orgDictClient.getOrgDictBySequence(orgCode, sequence);
            OrgDictDetailModel detailModel = convertToOrgDictDetailModel(mOrgDict);

            return success(detailModel);
        } catch (Exception ex) {
            return failedSystem();
        }
    }

    public OrgDictDetailModel convertToOrgDictDetailModel(MOrgDict mOrgDict)
    {
        if(mOrgDict==null)
        {
            return null;
        }
        OrgDictDetailModel detailModel = convertToModel(mOrgDict,OrgDictDetailModel.class);
        detailModel.setCreateDate(DateToString(mOrgDict.getCreateDate(), AgAdminConstants.DateTimeFormat));
        detailModel.setUpdateDate(DateToString(mOrgDict.getUpdateDate(),AgAdminConstants.DateTimeFormat));
        return detailModel;
    }

    public MOrgDict convertToMOrgDict(OrgDictDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MOrgDict mOrgDict = convertToModel(detailModel,MOrgDict.class);
        mOrgDict.setCreateDate(StringToDate(detailModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        mOrgDict.setUpdateDate(StringToDate(detailModel.getUpdateDate(),AgAdminConstants.DateTimeFormat));
        return mOrgDict;
    }
}
