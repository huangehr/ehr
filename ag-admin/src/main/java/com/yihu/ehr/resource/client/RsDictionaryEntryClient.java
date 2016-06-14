package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsDictionaryEntry;
import io.swagger.annotations.ApiOperation;
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
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsDictionaryEntryClient {

    @RequestMapping(value = ServiceApi.Resources.DictEntries, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取标准字典项列表", notes = "根据查询条件获取标准字典项列表")
    ResponseEntity<List<MRsDictionaryEntry>> searchRsDictionaryEntries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);



    @RequestMapping(value = ServiceApi.Resources.DictEntries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建标准字典项", notes = "创建标准字典项")
    MRsDictionaryEntry createRsDictionaryEntry(
            @RequestBody String jsonData);

    @RequestMapping(value =ServiceApi.Resources.DictEntries, method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准字典项", notes = "修改标准字典项")
    MRsDictionaryEntry updateRsDictionaryEntry(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.DictEntry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准字典项", notes = "删除标准字典项")
    boolean deleteRsDictionaryEntry(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.DictEntry, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    MRsDictionaryEntry getRsDictionaryEntryById(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = ServiceApi.Resources.DictEntriesExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    boolean isExistence(
            @RequestParam(value="filters") String filters) ;
}
