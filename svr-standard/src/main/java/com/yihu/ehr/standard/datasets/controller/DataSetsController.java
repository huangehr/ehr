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
import java.util.Map;

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


    @RequestMapping(value = "/datasets", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据集的方法")
    public Collection<MStdDataSet> searchDataSets(
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
    public MStdDataSet saveDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDataSet dataSet = (IDataSet) jsonToObj(model, entityClass);
        if (dataSetService.isExistByField("code", dataSet.getCode(), entityClass))
            throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");
        if(dataSetService.add(dataSet))
            return getModel(dataSet);
        return null;
    }

    @RequestMapping(value = "/dataset", method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集信息")
    public MStdDataSet updateDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        IDataSet dataSetModel = (IDataSet) jsonToObj(model, entityClass);
        IDataSet dataSet = dataSetService.retrieve(dataSetModel.getId(), entityClass);
        if(!dataSet.getCode().equals(dataSetModel.getCode())){
            if(dataSetService.isExistByField("code", dataSetModel.getCode(), entityClass))
                throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");
        }
        dataSetService.save(dataSetModel);
        return getModel(dataSetModel);
    }


    @RequestMapping(value = "/datasets/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception{

        return dataSetService.getDataSetMapByIds(
                ids==null || ids.trim().length()==0? new String[]{} : ids.split(","),
                version);
    }

}