package com.yihu.ehr.analyze.feign;

import com.yihu.ehr.analyze.model.AdapterDatasetModel;
import com.yihu.ehr.analyze.model.AdapterMetadataModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.hos.model.standard.MStdMetaData;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * @author zjj
 * @created 2018.03.20
 */
@ApiIgnore
@FeignClient(name = MicroServices.StdRedis)
@RequestMapping(ApiVersion.Version1_0)
public interface HosAdminServiceClient {

    @RequestMapping(value = ServiceApi.Redis.StdMetadataCodes, method = RequestMethod.GET)
    public String getMetadataCodes(@RequestParam("version") String version,
                                   @RequestParam("datasetCode") String datasetCode);

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

    @RequestMapping(value = "/adapterCenter/dataset/pageList", method = RequestMethod.GET)
    ResponseEntity<Collection<AdapterDatasetModel>> adapterDatasetList(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String version,
            @ApiParam(name = "fields", value = "字段")
            @RequestParam(value = "fields", required = true) String fields,
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size);

    @RequestMapping(value = "/adapterCenter/metadata/pageList", method = RequestMethod.GET)
    ResponseEntity<Collection<AdapterMetadataModel>> adapterMetadataList(
            @ApiParam(name = "version", value = "版本号")
            @RequestParam(value = "version", required = true) String version,
            @ApiParam(name = "fields", value = "字段")
            @RequestParam(value = "fields", required = true) String fields,
            @ApiParam(name = "filters", value = "过滤")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size);
}
