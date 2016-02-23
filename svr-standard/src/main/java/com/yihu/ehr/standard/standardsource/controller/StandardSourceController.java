package com.yihu.ehr.standard.standardsource.controller;

import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MStdSource;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.standardsource.service.StandardSource;
import com.yihu.ehr.standard.standardsource.service.StdSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.1.23
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0 + "/std")
@Api(protocols = "https", value = "source", description = "标准来源", tags = {"标准来源"})
public class StandardSourceController extends ExtendController<MStdSource>{

    @Autowired
    private StdSourceService stdSourceService;

    private StandardSource setValues(StandardSource standardSource, String code, String name, String type, String description){
        standardSource.setCode(code);
        standardSource.setName(name);
        standardSource.setSourceType(type);
        standardSource.setDescription(description);
        standardSource.setUpdateDate(new Date());
        return standardSource;
    }

    @RequestMapping(value = "/sources", method = RequestMethod.GET)
    @ApiOperation(value = "标准来源分页搜索")
    public Collection searchAdapterOrg(
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
            HttpServletResponse response) throws Exception{

        List appList = stdSourceService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, stdSourceService.getCount(filters), page, size);
        return convertToModels(appList, new ArrayList<MStdSource>(appList.size()), MStdSource.class, fields.split(","));
    }


    @RequestMapping(value = "/source", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取标准来源信息")
    public MStdSource getStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "id") String id) {

        return getModel(stdSourceService.retrieve(id));
    }


    @RequestMapping(value = "/source/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准来源，通过id取数据，取不到数据时新增，否则修改")
    public Object updateStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "编码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description) throws Exception{

        StandardSource standardSource = stdSourceService.retrieve(id);
        if(standardSource==null)
            throw errNotFound();

        if (!standardSource.getCode().equals(code)
                && stdSourceService.isSourceCodeExist(code))
            throw new ApiException(ErrorCode.RepeatCode, "代码重复！");

        stdSourceService.save(
                setValues(standardSource, code, name, type, description));
        return true;
    }

    @RequestMapping(value = "/source", method = RequestMethod.POST)
    @ApiOperation(value = "新增标准来源")
    public Object updateStdSource(
            @ApiParam(name = "code", value = "编码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type", value = "类型", defaultValue = "")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description) throws Exception{

        if (stdSourceService.isSourceCodeExist(code))
            throw errRepeatCode();

        stdSourceService.save(
                setValues(new StandardSource(), code, name, type, description));
        return true;
    }


    @RequestMapping(value = "/sources", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id组删除标准来源，多个id以,分隔")
    public boolean delStdSources(
            @ApiParam(name = "ids", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        String idArr[] = ids.split(",");
        return stdSourceService.deleteSource(Arrays.asList(idArr)) != -1;
    }

    @RequestMapping(value = "/source", method = RequestMethod.DELETE)
    @ApiOperation(value = "通过id删除标准来源")
    public boolean delStdSource(
            @ApiParam(name = "id", value = "标准来源编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{

        stdSourceService.delete(id);
        return true;
    }

}
