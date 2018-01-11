package com.yihu.ehr.standard.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.standard.common.ExtendEndPoint;
import com.yihu.ehr.standard.model.BaseDataSet;
import com.yihu.ehr.standard.service.DataSetService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "DataSetEndPoint", description = "数据集", tags = {"标准服务-数据集"})
public class DataSetEndPoint extends ExtendEndPoint<MStdDataSet> {

    @Autowired
    private DataSetService dataSetService;

    private Class getServiceEntity(String version){
        return dataSetService.getServiceEntity(version);
    }

    @RequestMapping(value = ServiceApi.Standards.DataSets, method = RequestMethod.GET)
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


    @RequestMapping(value = ServiceApi.Standards.NoPageDataSets, method = RequestMethod.GET)
    @ApiOperation(value = "标准数据集不分页搜索")
    public Collection<MStdDataSet> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception {
        Class entityClass = getServiceEntity(version);
        List dataSetList = dataSetService.search(entityClass,filters);
        return convertToModels(dataSetList, new ArrayList<>(dataSetList.size()), MStdDataSet.class, "");
    }


    @RequestMapping(value = ServiceApi.Standards.DataSet, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public boolean deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return dataSetService.removeDataSet(new Long[]{id}, version) > 0;
    }


    @RequestMapping(value = ServiceApi.Standards.DataSets, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集信息")
    public boolean deleteDataSet(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception{

        return dataSetService.removeDataSet(strToLongArr(ids), version) > 0;
    }


    @RequestMapping(value = ServiceApi.Standards.DataSet, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public MStdDataSet getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {

        return getModel(dataSetService.retrieve(id, getServiceEntity(version)));
    }

    @RequestMapping(value = ServiceApi.Standards.DataSetsIds, method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ids(用逗号隔开)获取数据集信息")
    public Collection<MStdDataSet> getDataSets(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) {
        Class entityClass = getServiceEntity(version);
        List<BaseDataSet> list = dataSetService.search(entityClass,"", "id="+ids, "+id");
        return convertToModels(list, new ArrayList<>(list.size()), MStdDataSet.class, "");
    }

    @RequestMapping(value = ServiceApi.Standards.DataSets, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据集信息")
    public MStdDataSet saveDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        BaseDataSet dataSet = (BaseDataSet) jsonToObj(model, entityClass);
//        if (dataSetService.isExistByField("code", dataSet.getCode(), entityClass))
//            throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");

        return getModel(dataSetService.insert(dataSet));
    }


    @RequestMapping(value = ServiceApi.Standards.DataSet, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改数据集信息")
    public MStdDataSet updateDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{

        Class entityClass = getServiceEntity(version);
        BaseDataSet dataSetModel = (BaseDataSet) jsonToObj(model, entityClass);
//        BaseDataSet dataSet = dataSetService.retrieve(id, entityClass);
//        if(!dataSet.getCode().equals(dataSetModel.getCode())){
//            if(dataSetService.isExistByField("code", dataSetModel.getCode(), entityClass))
//                throw new ApiException(ErrorCode.RapeatDataSetCode, "代码重复！");
//        }
        dataSetModel.setId(id);
        dataSetService.save(dataSetModel);
        return getModel(dataSetModel);
    }


    @RequestMapping(value = ServiceApi.Standards.DataSetsName, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.Standards.DataSetCodeIsExist,method = RequestMethod.GET)
    public boolean isExistCode(
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode)
    {
        Class entityClass = getServiceEntity(versionCode);
        return dataSetService.isExistByField("code", code, entityClass);
    }

    @RequestMapping(value = ServiceApi.Standards.DataSetsBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建标准数据集以及数据元", notes = "批量创建标准数据集以及数据元")
    public boolean createDictAndEntries(
            @RequestParam(value = "version") String version,
            @RequestBody String jsonData) throws Exception {

        List models = objectMapper.readValue(jsonData, new TypeReference<List>() {});
        dataSetService.batchInsertDictsAndEntry(models, version);
        return true;
    }

    @RequestMapping(value = ServiceApi.Standards.GetDataSetByCode, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据Code获取数据集", notes = "根据Code获取数据集")
    public Map<String,Object> getDataSetByCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "code", value = "数据集code", defaultValue = "")
            @RequestParam(value = "code") String code) throws Exception {
        return dataSetService.getDataSetByCode(code,version);
    }
}