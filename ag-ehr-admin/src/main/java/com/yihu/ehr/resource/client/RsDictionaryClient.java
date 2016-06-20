package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsDictionary;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0 + "/dictionaries")
@ApiIgnore
public interface RsDictionaryClient {

    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典列表", notes = "根据查询条件获取标准字典列表")
    ResponseEntity<List<MRsDictionary>> searchRsDictionaries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准字典", notes = "创建标准字典")
    MRsDictionary createRsDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.DictList, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改标准字典", notes = "修改标准字典")
    MRsDictionary updateRsDictionary(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典", notes = "删除标准字典")
    boolean deleteRsDictionary(
            @PathVariable(value = "id") int id);

    @RequestMapping(value = ServiceApi.Resources.Dict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    MRsDictionary getRsDictionaryById(
            @PathVariable(value = "id") int id);

    @RequestMapping(value = ServiceApi.Resources.DictBatch, method = RequestMethod.POST , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建标准字典", notes = "批量创建标准字典")
    boolean createRsDictionaries(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.DictExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters);

    @RequestMapping(value = ServiceApi.Resources.DictEntryBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量创建标准字典以及字典项", notes = "批量创建标准字典以及字典项")
    boolean createDictAndEntries(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.DictCodesExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在字典编码")
    List codeExistence(
            @RequestBody String codes);
}
