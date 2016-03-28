package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
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
import java.util.Map;

/**
 * Created by wq on 2016/3/2.
 */

@FeignClient(name=MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DictClient {

    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    ResponseEntity<Collection<MStdDict>> searchDict(
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
            @RequestParam(value = "version") String version)throws Exception;

    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.POST)
    @ApiOperation(value = "新增字典")
    MStdDict addDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String stdVersion,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典")
    MStdDict updateDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    boolean deleteDict(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = RestApi.Standards.Dictionaries, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典")
    boolean deleteDicts(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = RestApi.Standards.Dictionary, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典详细信息")
    MStdDict getCdaDictInfo(
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.MetaDataWithDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典map集")
    Map getDictMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "data_set_id") Long dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "meta_data_id") Long metaDataId);


    @RequestMapping(value = RestApi.Standards.DictCodeIsExist,method = RequestMethod.GET)
    boolean isExistDictCode(
            @RequestParam(value = "dict_code")String dictCode,
            @RequestParam("version_code")String versionCode);



    //以下是字典项
    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    MStdDictEntry addDictEntry(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "model") String model);


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项")
    MStdDictEntry updateDictEntry(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "id") long id,
            @RequestParam(value = "model") String model) ;


    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    ResponseEntity<Collection<MStdDictEntry>> searchDictEntry(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    MStdDictEntry getDictEntry(
            @PathVariable(value = "id") long id,
            @RequestParam(value = "version") String version);


    @RequestMapping(value = RestApi.Standards.Entry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    boolean deleteDictEntry(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "id") long id);


    @RequestMapping(value = RestApi.Standards.Entries, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    boolean deleteDictEntrys(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = RestApi.Standards.EntriesWithDictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    boolean deleteDictEntryList(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "dict_id") long dictId);

    @RequestMapping(value = RestApi.Standards.EntryCodeIsExist,method = RequestMethod.GET)
    boolean isExistEntryCode(
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode);
}
