package com.yihu.ehr.redis.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by progr1mmer on 2017/9/11.
 */
@FeignClient(value = MicroServices.Redis)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RedisClient {

    @ApiOperation("获取资源化数据元映射")
    @RequestMapping(value = ServiceApi.Redis.RsAdaptionMetaData, method = RequestMethod.GET)
    @ResponseBody
    String getRsAdaptionMetaData(
            @RequestParam("cdaVersion") String cdaVersion,
            @RequestParam("dictCode") String dictCode,
            @RequestParam("srcDictEntryCode") String srcDictEntryCode);

}
