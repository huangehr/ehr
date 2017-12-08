package com.yihu.ehr.dfs.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MSystemDict;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Client - 系统字典
 * Created by progr1mmer on 2017/12/6.
 */
@ApiIgnore
@FeignClient(name = MicroServices.Basic)
@RequestMapping(ApiVersion.Version1_0)
public interface SystemDictClient {

    @ApiOperation(value = "获取字典")
    @RequestMapping(value = "/dictionary/{phoneticCode}", method = RequestMethod.GET)
    MSystemDict getDictionaryByPhoneticCode(
            @PathVariable(value = "phoneticCode") String phoneticCode);

}
