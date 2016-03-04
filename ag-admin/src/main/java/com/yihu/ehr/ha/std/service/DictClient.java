package com.yihu.ehr.ha.std.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDict;
import com.yihu.ehr.model.standard.MStdDictEntry;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.Map;

/**
 * Created by wq on 2016/3/2.
 */

@FeignClient(MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DictClient {

    @RequestMapping(value = "/std/dicts", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典")
    Collection<MStdDict> searchDict(
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

    @RequestMapping(value = "/std/dict", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典")
    MStdDict addDict(
            @ApiParam(name = "stdVersion", value = "标准版本", defaultValue = "")
            @RequestParam(value = "stdVersion") String stdVersion,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);


    @RequestMapping(value = "/std/dict", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典")
    MStdDict updateDict(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);


    @RequestMapping(value = "/std/dict/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典")
    boolean deleteDict(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") long id);


    @RequestMapping(value = "/std/dicts", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典")
    boolean deleteDicts(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = "/std/dict/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据CdaVersion及字典ID查询相应版本的字典详细信息")
    MStdDict getCdaDictInfo(
            @ApiParam(name = "id", value = "字典编号", defaultValue = "")
            @RequestParam(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/dict/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典map集")
    Map getDictMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "metaDataId", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "metaDataId") Long metaDataId);






    //以下是字典项
    @RequestMapping(value = "/std/dict/entry", method = RequestMethod.POST)
    @ApiOperation(value = "新增字典项")
    MStdDictEntry addDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);

    @RequestMapping(value = "/std/dict/entry", method = RequestMethod.PUT)
    @ApiOperation(value = "修改字典项")
    MStdDictEntry updateDictEntry(
            @ApiParam(name = "version", value = "标准版本", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam(value = "model") String model);

    @RequestMapping(value = "/std/dict/entrys", method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    Collection<MStdDictEntry> searchDictEntry(
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

    @RequestMapping(value = "/std/dict/entry/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典项")
    MStdDictEntry getDictEntry(
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @PathVariable(value = "id") long id,
            @ApiParam(name = "version", value = "版本编号", defaultValue = "")
            @RequestParam(value = "version") String version);

    @RequestMapping(value = "/std/dict/entry", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典项")
    boolean deleteDictEntry(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "id", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "id") long id);

    @RequestMapping(value = "/std/dict/entrys", method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除字典项")
    boolean deleteDictEntrys(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @RequestParam(value = "dictId") long dictId,
            @ApiParam(name = "ids", value = "字典项编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = "/std/dict/{dictId}/entry", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除字典关联的所有字典项")
    boolean deleteDictEntryList(
            @ApiParam(name = "version", value = "cda版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dictId", value = "字典编号", defaultValue = "")
            @PathVariable(value = "dictId") long dictId);



}
