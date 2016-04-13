package com.yihu.ehr.std.service;

import com.yihu.ehr.agModel.standard.datasset.DataSetModel;
import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;


/**
 * Created by wq on 2016/2/29.
 */

@FeignClient(name=MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DataSetClient {

    @RequestMapping(value = RestApi.Standards.DataSets, method = RequestMethod.GET)
    @ApiOperation("查询数据集的方法")
    ResponseEntity<Collection<MStdDataSet>> searchDataSets(
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
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.DataSet, method = RequestMethod.DELETE)
    @ApiOperation("删除数据集信息")
    boolean deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.DataSets, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据集信息")
    boolean deleteDataSet(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.DataSet, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    MStdDataSet getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.DataSets, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据集信息")
    MStdDataSet saveDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);



    @RequestMapping(value = RestApi.Standards.DataSet, method = RequestMethod.PUT)
    @ApiOperation(value = "修改数据集信息")
    MStdDataSet updateDataSet(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);

    @RequestMapping(value = RestApi.Standards.DataSetCodeIsExist,method = RequestMethod.GET)
    boolean isExistCode(
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode);

    //以下是数据元部分
    @RequestMapping(value = RestApi.Standards.MetaDatas, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    MStdMetaData saveMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String metadataJsonData);


    @RequestMapping(value = RestApi.Standards.MetaDatas, method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    ResponseEntity<Collection<MStdMetaData>> searchMetaDatas(
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
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.MetaData, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    MStdMetaData getMetaData(
            @ApiParam(name = "id", value = "数据元ID", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.MetaData, method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据元")
    MStdMetaData updataMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String model);


    @RequestMapping(value = RestApi.Standards.MetaData, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    boolean deleteMetaData(
            @ApiParam(name = "id", value = "编号集", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.MetaDatasWithDataSet, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集关联的数据元")
    boolean deleteMetaDataByDataSet(
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "data_set_id") long dataSetId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.MetaDatas, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    boolean deleteMetaDatas(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.MetaDataCodeExistence, method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元代码是否重复")
    boolean validateInnerCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "data_set_id") long dataSetId,
            @ApiParam(name = "inner_code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "inner_code") String innerCode);

    @RequestMapping(value = RestApi.Standards.MetaDataNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元名称是否重复")
    public boolean validatorName(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "data_set_id") long dataSetId,
            @ApiParam(name = "name", value = "查询名称", defaultValue = "")
            @RequestParam(value = "name") String name);

    @RequestMapping(value = RestApi.Standards.DataSetsIds, method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集ids(用逗号隔开)获取数据集信息")
    List<MStdDataSet> getDataSets(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.MetaDatasWithDataSet, method = RequestMethod.GET)
    @ApiOperation(value = "根据数据集id获取数据元")
    List<MStdMetaData> getMetaDataByDataSetId(
            @ApiParam(name = "data_set_id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "data_set_id") long dataSetIs,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = RestApi.Standards.NoPageDataSets, method = RequestMethod.GET)
    List<MStdDataSet> search(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);
}  
