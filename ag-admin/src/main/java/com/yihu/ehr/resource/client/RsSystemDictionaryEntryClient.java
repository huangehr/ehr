package com.yihu.ehr.resource.client;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.resource.MRsSystemDictionaryEntry;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
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
public interface RsSystemDictionaryEntryClient {

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取系统字典项列表", notes = "根据查询条件获取系统字典项列表")
    List<MRsSystemDictionaryEntry> searchRsSystemDictionaryEntries(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建系统字典项", notes = "创建系统字典项")
    MRsSystemDictionaryEntry createRsSystemDictionaryEntry(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntries, method = RequestMethod.PUT)
    @ApiOperation(value = "修改系统字典项", notes = "修改系统字典项")
    MRsSystemDictionaryEntry updateRsSystemDictionaryEntry(
            @RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Resources.SystemDictEntry, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除系统字典项", notes = "删除系统字典项")
    boolean deleteRsSystemDictionaryEntry(
            @PathVariable(value = "id") String id);
}
