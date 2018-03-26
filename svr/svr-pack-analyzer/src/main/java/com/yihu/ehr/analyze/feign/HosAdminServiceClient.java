package com.yihu.ehr.analyze.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author zjj
 * @created 2018.03.20
 */
@ApiIgnore
@FeignClient(name = MicroServices.StdRedis)
@RequestMapping(ApiVersion.Version1_0)
public interface HosAdminServiceClient {

    @RequestMapping(value = ServiceApi.Redis.StdMetadataType, method = RequestMethod.GET)
    String getMetaDataType(
            @RequestParam("version") String version,
            @RequestParam("dataSetCode") String dataSetCode,
            @RequestParam("innerCode") String innerCode);

    @RequestMapping(value = ServiceApi.Redis.StdMetadataFormat, method = RequestMethod.GET)
    String getMetaDataFormat(
            @RequestParam("version") String version,
            @RequestParam("dataSetCode") String dataSetCode,
            @RequestParam("innerCode") String innerCode);

    @RequestMapping(value = ServiceApi.Redis.StdMetadataNullable, method = RequestMethod.GET)
    Boolean isMetaDataNullable(
            @RequestParam("version") String version,
            @RequestParam("datasetCode") String datasetCode,
            @RequestParam("metadataCode") String metadataCode);

    @RequestMapping(value = ServiceApi.Redis.StdMetadataDict, method = RequestMethod.GET)
    String getMetaDataDict(
            @RequestParam("version") String version,
            @RequestParam("dataSetCode") String dataSetCode,
            @RequestParam("innerCode") String innerCode);


    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValue, method = RequestMethod.GET)
    String getDictEntryValue(
            @RequestParam("version") String version,
            @RequestParam("dictId") String dictId,
            @RequestParam("entryCode") String entryCode);


    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValueExist, method = RequestMethod.GET)
    Boolean isDictValueExist(
            @RequestParam("version") String version,
            @RequestParam("dictId") String dictId,
            @RequestParam("entryValue") String entryValue);

    @RequestMapping(value = ServiceApi.Redis.StdDictEntryCodeExist, method = RequestMethod.GET)
    Boolean isDictCodeExist(
            @RequestParam("version") String version,
            @RequestParam("dictId") String dictId,
            @RequestParam("entryCode") String entryCode);
}
