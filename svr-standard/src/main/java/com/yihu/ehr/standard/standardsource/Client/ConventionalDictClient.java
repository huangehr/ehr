package com.yihu.ehr.standard.standardsource.Client;

import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient("svr-dict")
@RequestMapping("/rest")
public interface ConventionalDictClient {

    @RequestMapping(value = "/{api_version}/conventional_dict/stdSourceType", method = GET,consumes = "application/json")
    MConventionalDict getStdSourceType(
            @PathVariable(value="api_version")String version,
            @RequestParam(value = "code") String code);

}
