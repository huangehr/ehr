package com.yihu.ehr.dfs.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 系统字典项
 * Created by progr1mmer on 2017/12/6.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface SystemDictEntryClient {

    @ApiOperation(value = "获取字典项")
    @RequestMapping(value = "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.GET)
    MDictionaryEntry getDictEntry(
            @PathVariable(value = "dict_id") long dictId,
            @PathVariable(value = "code") String code);

    @ApiOperation(value = "修改字典项")
    @RequestMapping(value = "/dictionaries/entries", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MDictionaryEntry updateDictEntry(
            @RequestBody String entryJson);

    @RequestMapping(value ="/dictionary/entryList/{dictId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据dictId获取所有字典项")
    Envelop listByDictId(
            @PathVariable(value = "dictId") long dictId);

}
