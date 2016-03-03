package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.agModel.standard.datasset.MetaDataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDataSet;
import com.yihu.ehr.model.standard.MStdMetaData;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;
//import com.yihu.ehr.standard.datasets.service.IMetaData;

import java.util.Collection;

/**
 * Created by wq on 2016/2/29.
 */

@FeignClient(MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DataSetClient {

    @RequestMapping(value = "/std/datasets", method = RequestMethod.GET)
    @ApiOperation("查询数据集的方法")
    Collection<MStdDataSet> searchDataSets(
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

    @RequestMapping(value = "/std/dataset/{id}", method = RequestMethod.DELETE)
    @ApiOperation("删除数据集信息")
    boolean deleteDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/datasets", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据集信息")
    boolean deleteDataSet(
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/dataset/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集信息")
    public MStdDataSet getDataSet(
            @ApiParam(name = "id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/dataset", method = RequestMethod.POST)
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
            @RequestParam(value = "version") String version);


    @RequestMapping(value = "/std/dataset/{id}", method = RequestMethod.PUT)
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
            @RequestParam(value = "version") String version);

    //以下是数据元部分
    @RequestMapping(value = "/std/metadata", method = RequestMethod.POST)
    @ApiOperation(value = "新增数据元")
    boolean saveMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String metadataJsonData);

    @RequestMapping(value = "/std/metadatas", method = RequestMethod.GET)
    @ApiOperation(value = "查询数据元")
    Collection<MStdMetaData> searchMetaDatas(
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

    @RequestMapping(value = "/std/metadata/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元")
    MStdMetaData getMetaData(
            @ApiParam(name = "id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "metaDataId", value = "数据元ID")
            @RequestParam(value = "metaDataId") long metaDataId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/metadata", method = RequestMethod.PUT)
    @ApiOperation(value = "更新数据元")
    boolean updataMetaSet(
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "数据源模型", defaultValue = "")
            @RequestParam(value = "model", required = false) String model);

    @RequestMapping(value = "/std/metadata/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据元")
    boolean deleteMetaData(
            @ApiParam(name = "id", value = "编号集", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/dataset/{dataSetId}/metadata", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据集关联的数据元")
    boolean deleteMetaDataByDataSet(
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "dataSetId") long dataSetId,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/metadatas", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除数据元")
    public boolean deleteMetaDatas(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") String ids,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/metadata/validate/code", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元代码是否重复")
    public boolean validateCode(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "code", value = "查询代码", defaultValue = "")
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/std/metadata/validate/name", method = RequestMethod.GET)
    @ApiOperation(value = "验证数据元名称是否重复")
    public boolean validatorName(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") long dataSetId,
            @ApiParam(name = "name", value = "查询名称", defaultValue = "")
            @RequestParam(value = "name") String name);
}
