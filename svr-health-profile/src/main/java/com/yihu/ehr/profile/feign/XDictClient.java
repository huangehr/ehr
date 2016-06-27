package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @version 1.0
 * @created 2016.06.27 14:58
 * 字典服务
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Dictionary)
public interface XDictClient {

    @RequestMapping(value = "/dictionaries/record_data_sources", method = RequestMethod.GET)
    public List<MConventionalDict> getRecordDataSourceList();
}
