package com.yihu.ehr.standard.datasets.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.standard.commons.ExtendController;
import com.yihu.ehr.standard.datasets.service.DataSetService;
import com.yihu.ehr.standard.datasets.service.IDataSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.15
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/std")
@Api(protocols = "https", value = "std/dataset", description = "标准数据集", tags = {"标准数据集", "标准数据元"})
public class DataSetsController extends ExtendController<MStdDataSet> {

    @Autowired
    private DataSetService dataSetService;

    private Class getServiceEntity(String version){
        return dataSetService.getServiceEntity(version);
    }

    private IDataSet setValues(IDataSet dataSet, String code, String name, String refStandard, String summary, String version){
        dataSet.setStdVersion(version);
        dataSet.setName(name);
        dataSet.setReference(refStandard);
        dataSet.setSummary(summary);
        dataSet.setCode(code);
        return dataSet;
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集的方法")
    public Collection searchDataSets(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        Class entityClass = getServiceEntity(version);
        List ls = dataSetService.search(entityClass, fields, filters, sorts, page, size);
        pagedResponse(request, response, dataSetService.getCount(entityClass, filters), page, size);
        return convertToModels(ls, new ArrayList<>(ls.size()), MStdDataSet.class, fields);
    }


    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public boolean deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return dataSetService.removeDataSet(new Long[]{id}, version) > 0;
    }

    @RequestMapping(value = "/datasets", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public boolean deleteDataSet(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return dataSetService.removeDataSet(strToLongArr(ids), version) > 0;
    }

    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public MStdDataSet getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        return getModel(dataSetService.retrieve(id, getServiceEntity(version)));
    }


    @RequestMapping(value = "/dataset", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集信息")
    public boolean saveDataSet(
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "refStandard", value = "标准来源", defaultValue = "")
            @RequestParam(value = "refStandard") String refStandard,
            @ApiParam(name = "summary", value = "描述", defaultValue = "")
            @RequestParam(value = "summary") String summary,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDataSet dataSet = (IDataSet)entityClass.newInstance();
        setValues(dataSet, code, name, refStandard, summary, version);
        if (dataSetService.isExistByField("code", code, entityClass))
            throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");
        dataSetService.add(dataSet);
        return true;
    }

    @RequestMapping(value = "/dataset/{id}", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集信息")
    public boolean updateDataSet(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "code", value = "代码", defaultValue = "")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "refStandard", value = "标准来源", defaultValue = "")
            @RequestParam(value = "refStandard") String refStandard,
            @ApiParam(name = "summary", value = "描述", defaultValue = "")
            @RequestParam(value = "summary") String summary,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDataSet dataSet = dataSetService.retrieve(id, entityClass);
        if(!dataSet.getCode().equals(code)){
            dataSet.setCode(code);
            if(dataSetService.isExistByField("code", code, entityClass))
                throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");
        }
        setValues(dataSet, code, name, refStandard, summary, version);
        dataSetService.save(dataSet);
        return true;
    }

}