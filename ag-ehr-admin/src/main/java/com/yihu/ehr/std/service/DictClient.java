package com.yihu.ehr.std.service;

import com.yihu.ehr.agModel.standard.dict.DictModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by wq on 2016/3/2.
 */

@FeignClient(name=MicroServices.Standard)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DictClient {

    @RequestMapping(value = ServiceApi.Standards.Dictionaries, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.Standards.Dictionaries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增字典")
    MStdDict addDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String stdVersion,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);


    @RequestMapping(value = ServiceApi.Standards.Dictionary, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改字典")
    MStdDict updateDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);


    @RequestMapping(value = ServiceApi.Standards.Dictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    boolean deleteDict(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = ServiceApi.Standards.Dictionaries, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典")
    boolean deleteDicts(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Standards.Dictionary, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典详细信息")
    MStdDict getCdaDictInfo(
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version);


    @RequestMapping(value = ServiceApi.Standards.DictionaryCode, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典详细信息")
     MStdDict getDictByCode(
            @RequestParam(value = "code") String code,
            @RequestParam(value = "version") String version) ;

    @RequestMapping(value = ServiceApi.Standards.MetaDataWithDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典map集")
    Map getDictMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "data_set_id", value = "数据集编号", defaultValue = "")
            @PathVariable(value = "data_set_id") Long dataSetId,
            @ApiParam(name = "meta_data_id", value = "数据元编号", defaultValue = "")
            @PathVariable(value = "meta_data_id") Long metaDataId);


    @RequestMapping(value = ServiceApi.Standards.DictCodeIsExist,method = RequestMethod.GET)
    boolean isExistDictCode(
            @RequestParam(value = "dict_code")String dictCode,
            @RequestParam("version_code")String versionCode);



    //以下是字典项
    @RequestMapping(value = ServiceApi.Standards.Entries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增字典项")
    MStdDictEntry addDictEntry(
            @RequestParam(value = "version") String version,
            @RequestBody String model);


    @RequestMapping(value = ServiceApi.Standards.Entry, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改字典项")
    MStdDictEntry updateDictEntry(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "id") long id,
            @RequestBody String model) ;


    @RequestMapping(value = ServiceApi.Standards.Entries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    ResponseEntity<Collection<MStdDictEntry>> searchDictEntry(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "version") String version);


    @RequestMapping(value = ServiceApi.Standards.Entry, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    MStdDictEntry getDictEntry(
            @PathVariable(value = "id") long id,
            @RequestParam(value = "version") String version);


    @RequestMapping(value = ServiceApi.Standards.Entry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    boolean deleteDictEntry(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "id") long id);


    @RequestMapping(value = ServiceApi.Standards.Entries, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    boolean deleteDictEntrys(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Standards.EntriesWithDictionary, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    boolean deleteDictEntryList(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "dict_id") long dictId);

    @RequestMapping(value = ServiceApi.Standards.EntryCodeIsExist,method = RequestMethod.GET)
    boolean isExistEntryCode(
            @RequestParam(value = "dict_id")long dictId,
            @RequestParam(value = "code")String code,
            @RequestParam(value = "version_code")String versionCode);


    @RequestMapping(value = ServiceApi.Standards.NoPageDictionaries, method = RequestMethod.GET)
    ResponseEntity<Collection<DictModel>> search(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "version") String version);

    @RequestMapping(value = ServiceApi.Standards.DictCodesExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在字典编码")
    List codeExistence(
            @RequestBody String codes,@RequestParam(value = "version")String version);

    @RequestMapping(value = ServiceApi.Standards.DictEntryBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建标准字典以及字典项", notes = "批量创建标准字典以及字典项")
    boolean createDictAndEntries(
            @RequestBody String jsonData,@RequestParam(value = "version")String version);
}
