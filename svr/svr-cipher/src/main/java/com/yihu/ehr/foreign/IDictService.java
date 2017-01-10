package com.yihu.ehr.foreign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 系统字典调用
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
@FeignClient(name = MicroServices.Dictionary)
@ApiIgnore
public interface IDictService {

    @RequestMapping(value = ApiVersion.Version1_0 + "/dictionaries/{dict_id}/entries/{code}", method = RequestMethod.GET)
    MDictionaryEntry getDictEntry(
            @PathVariable(value = "dict_id") long dictId,
            @PathVariable(value = "code") String code);

    @RequestMapping(value = ApiVersion.Version1_0 + "/dictionaries/entries", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MDictionaryEntry updateDictEntry(
            @RequestBody String entryJson);
}
