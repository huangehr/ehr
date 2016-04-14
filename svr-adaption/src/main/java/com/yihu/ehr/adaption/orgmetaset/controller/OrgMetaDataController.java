package com.yihu.ehr.adaption.orgmetaset.controller;

import com.yihu.ehr.adaption.commons.ExtendController;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaData;
import com.yihu.ehr.adaption.orgmetaset.service.OrgMetaDataService;
import com.yihu.ehr.constants.ApiVersion;
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
@Api(value = "orgmetadata", description = "机构数据元", tags = {"机构数据元"})
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
    public MOrgMetaData createOrgMetaData(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        OrgMetaData orgMetaData = jsonToObj(model, OrgMetaData.class);
        orgMetaData.setColumnLength(orgMetaData.getColumnLength()==null? 0 : orgMetaData.getColumnLength());
        orgMetaData.setCreateDate(new Date());
        return getModel(orgMetaDataService.createOrgMetaData(orgMetaData));
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
            orgMetaDataService.delete(strToLongArr(ids));
        return true;
    }

    @RequestMapping(value = "/meta", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据元")
    public MOrgMetaData updateOrgMetaData(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        OrgMetaData dataModel = jsonToObj(model, OrgMetaData.class);
        dataModel.setUpdateDate(new Date());
        return getModel(orgMetaDataService.save(dataModel));
    }


    @RequestMapping(value = "/page", method = RequestMethod.GET)
    @ApiOperation(value = "分页查询")
    public Collection<MOrgMetaData> searchOrgMetaDatas(
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

    @RequestMapping(value = "/meta/is_exist", method = RequestMethod.GET)
    public boolean isExistMetaData(
            @RequestParam(value = "data_set_id") int dataSetId,
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "meta_data_code") String metaDataCode) {
        return orgMetaDataService.isExistOrgMetaData(dataSetId, orgCode, metaDataCode);//重复验证
    }

    @RequestMapping(value = "/meta_data",method = RequestMethod.GET)
    public MOrgMetaData getMetaDataBySequence(
            @RequestParam(value = "org_code") String orgCode,
            @RequestParam(value = "sequence") int sequence) {
        return convertToModel(orgMetaDataService.getMetaDataBySequence(orgCode, sequence), MOrgMetaData.class);
    }
}
