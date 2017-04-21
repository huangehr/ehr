package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.standard.MStdDictEntry;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * @author linaz
 * @created 2016.07.08 15:33
 */
@ApiIgnore
@FeignClient(name = MicroServices.Standard)
public interface StdDictEntryClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Standards.Entries, method = RequestMethod.GET)
    @ApiOperation(value = "查询字典项")
    Collection<MStdDictEntry> searchDictEntry(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "version") String version);
}
