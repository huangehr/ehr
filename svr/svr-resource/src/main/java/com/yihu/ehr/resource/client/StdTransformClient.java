package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by progr1mmer on 2018/1/24.
 */
@ApiIgnore
@FeignClient(name = MicroServices.StdRedis)
@RequestMapping(value = ApiVersion.Version1_0)
public interface StdTransformClient {

    @RequestMapping(value = "/redis/stdMetadataCodes", method = RequestMethod.GET)
    String stdMetadataCodes(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "datasetCode") String datasetCode);

    @RequestMapping(value = "/redis/adapterMetadataCode", method = RequestMethod.GET)
    String adapterMetadataCode(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "stdDatasetCode") String datasetCode,
            @RequestParam(value = "stdMetadataCode") String stdMetadataCode);

    @RequestMapping(value = "/redis/stdMetadataName", method = RequestMethod.GET)
    String stdMetadataName(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "datasetCode") String datasetCode,
            @RequestParam(value = "metadataCode") String metadataCode);

}
