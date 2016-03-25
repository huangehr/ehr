package com.yihu.ehr.user.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name = MicroServices.Dictionary)
@ApiIgnore
public interface ConventionalDictClient {

    @RequestMapping(value = ApiVersion.Version1_0+"/dictionaries/user_type", method = GET )
    MConventionalDict getUserType(@RequestParam(value = "code") String code);

}

