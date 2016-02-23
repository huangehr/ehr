package com.yihu.ehr.adaption.orgmetaset.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaData;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.adaption.MOrgMetaData;
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
@Api(protocols = "https", value = "orgmetadata", description = "机构数据元", tags = {"机构数据元"})
public class OrgMetaDataController extends ExtendController<MOrgMetaData> {

    @Autowired
    private OrgMetaDataService orgMetaDataService;


    @RequestMapping(value = "/meta/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取机构数据元")
    public MOrgMetaData getOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception {

        return getModel(orgMetaDataService.retrieve(id));
    }


    @RequestMapping(value = "/meta", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    public boolean createOrgMetaData(
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") int orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception {

        boolean isExist = orgMetaDataService.isExistOrgMetaData(orgDataSetSeq, orgCode, code);//重复验证
        if (isExist)
            throw new ApiException(ErrorCode.RepeatOrgMetaData, "该数据元已存在!");

        OrgMetaData orgMetaData = new OrgMetaData();
        orgMetaData.setCode(code);
        orgMetaData.setName(name);
        orgMetaData.setOrgDataSet(orgDataSetSeq);
        orgMetaData.setCreateDate(new Date());
        orgMetaData.setCreateUser(userId);
        orgMetaData.setOrganization(orgCode);
        orgMetaData.setDescription(description);
        orgMetaDataService.createOrgMetaData(orgMetaData);
        return true;
    }


    @RequestMapping(value = "/meta/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    public boolean deleteOrgMetaData(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id) throws Exception {

        orgMetaDataService.delete(id);
        return true;
    }

    @RequestMapping(value = "/metas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    public boolean deleteOrgMetaDataList(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        if (ids != null && ids.length() > 0)
            orgMetaDataService.delete(ids.split(","));
        return true;
    }

    @RequestMapping(value = "/meta/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元")
    public boolean updateOrgMetaData(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "orgDataSetSeq", value = "orgDataSetSeq", defaultValue = "")
            @RequestParam(value = "orgDataSetSeq") Integer orgDataSetSeq,
            @ApiParam(name = "orgCode", value = "orgCode", defaultValue = "")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "description", value = "description", defaultValue = "")
            @RequestParam(value = "description", required = false) String description,
            @ApiParam(name = "userId", value = "userId", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception {

        OrgMetaData orgMetaData = orgMetaDataService.retrieve(id);
        if (orgMetaData == null) {
            throw errNotFound();
        } else {
            //重复验证
            boolean updateFlg = orgMetaData.getCode().equals(code) || !orgMetaDataService.isExistOrgMetaData(orgDataSetSeq, orgCode, code);
            if (updateFlg) {
                orgMetaData.setCode(code);
                orgMetaData.setName(name);
                orgMetaData.setDescription(description);
                orgMetaData.setUpdateDate(new Date());
                orgMetaData.setUpdateUser(userId);
                orgMetaData.setOrganization(orgCode);
                orgMetaDataService.save(orgMetaData);
                return true;
            } else
                throw new ApiException(ErrorCode.RepeatOrgMetaData, "数据元代码重复！");
        }
    }


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    public Collection searchOrgMetaDatas(
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
            HttpServletResponse response) throws Exception {

        List appList = orgMetaDataService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, orgMetaDataService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MOrgMetaData.class, fields);
    }

}
