package com.yihu.ehr.standard.stdsrc.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.stdsrc.service.StandardSource;
import com.yihu.ehr.standard.stdsrc.service.StdSourceService;
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
 * @created 2016.1.23
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "source", description = "标准来源", tags = {"标准来源"})
public class StandardSourceController extends ExtendController<MStdSource> {

    @Autowired
    private StdSourceService stdSourceService;

    @RequestMapping(value = RestApi.Standards.Sources, method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索")
    public Collection<MStdSource> searchSources(
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

        List appList = stdSourceService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, stdSourceService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<>(appList.size()), MStdSource.class, fields);
    }

    //标准 // TODO: 2016/3/29 bu fenye  
//    @RequestMapping(value = "", method = RequestMethod.GET)
//    @ApiOperation(value = "标准来源分页搜索不分页")
//    public Collection<MStdSource> ss(
//            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
//            @RequestParam(value = "fields", required = false) String fields,
//            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
//            @RequestParam(value = "filters", required = false) String filters,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
//            @RequestParam(value = "sorts", required = false) String sorts) throws Exception {
//
//        List appList = stdSourceService.search(fields, filters, sorts, "", "");
//        return convertToModels(appList, new ArrayList<>(appList.size()), MStdSource.class, fields);
//    }

    


    @RequestMapping(value = RestApi.Standards.Source, method = RequestMethod.GET)
    @ApiOperation(value = "获取标准来源信息")
    public MStdSource getStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) {

        return getModel(stdSourceService.retrieve(id));
    }


    @RequestMapping(value = RestApi.Standards.Source, method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准来源")
    public MStdSource updateStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        StandardSource standardSourceModel = jsonToObj(model, StandardSource.class);
        StandardSource standardSource = stdSourceService.retrieve(id);
        if (standardSource == null)
            throw errNotFound("标准来源", standardSourceModel.getId());

        standardSourceModel.setId(id);
        standardSourceModel.setUpdateDate(new Date());
        return getModel(stdSourceService.save(standardSourceModel));
    }


    @RequestMapping(value = RestApi.Standards.Sources, method = RequestMethod.POST)
    @ApiOperation(value = "新增标准来源")
    public MStdSource addStdSource(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception {

        StandardSource standardSource = jsonToObj(model, StandardSource.class);
        if (stdSourceService.isSourceCodeExist(standardSource.getCode()))
            throw errRepeatCode();
        standardSource.setCreateDate(new Date());
        return getModel(stdSourceService.save(standardSource));
    }


    @RequestMapping(value = RestApi.Standards.Sources, method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public boolean delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {

        stdSourceService.deleteSource(ids.split(","));
        return true;
    }

    @RequestMapping(value = RestApi.Standards.Source, method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    public boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {

        stdSourceService.delete(id);
        return true;
    }

    @RequestMapping(value = RestApi.Standards.IsSourceCodeExist,method = RequestMethod.GET)
    public boolean isCodeExist(@RequestParam(value="code")String code){

        return stdSourceService.isSourceCodeExist(code);
    }
}
