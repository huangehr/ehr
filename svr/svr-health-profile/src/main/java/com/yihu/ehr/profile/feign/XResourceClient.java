package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author hzp
 * @version 1.0
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Resource)
public interface XResourceClient {


    @RequestMapping(value = ServiceApi.Resources.ResourceQuery, method = RequestMethod.POST)
    Envelop getResources(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "orgCode") String orgCode,
            @RequestParam(value = "areaCode") String areaCode,
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    @RequestMapping(value = ServiceApi.Resources.ResourceQueryTransform, method = RequestMethod.POST)
    Envelop ResourcesQueryTransform(
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @RequestParam(value = "roleId") String roleId,
            @RequestParam(value = "orgCode") String orgCode,
            @RequestParam(value = "areaCode", required = false) String areaCode,
            @RequestParam(value = "queryParams", required = false) String queryParams,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "version", required = true) String version);

    //cda数据
    @RequestMapping(value = ServiceApi.Resources.getCDAData, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    Map<String,Object> getCDAData(@RequestBody String cdaTransformDtoJson);


    //查询主表数据
    @RequestMapping(value = ServiceApi.Resources.ResourceMasterData, method = GET)
    Envelop getMasterData(@RequestParam(value = "queryParams", required = true) String queryParams,
                          @RequestParam(value = "page", required = false) Integer page,
                          @RequestParam(value = "size", required = false) Integer size,
                          @RequestParam(value = "version", required = true) String version);

    //查询细表数据
    @RequestMapping(value = ServiceApi.Resources.ResourceSubData, method = GET)
    Envelop getSubData(@RequestParam(value = "queryParams", required = true) String queryParams,
                       @RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "size", required = false) Integer size,
                       @RequestParam(value = "version", required = true) String version);

    //查询主表统计数据
    @RequestMapping(value = ServiceApi.Resources.ResourceMasterStat, method = GET)
    Envelop getMasterStat(@RequestParam(value = "queryParams", required = true) String queryParams,
                         @RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "size", required = false) Integer size);

    //查询细表统计数据
    @RequestMapping(value = ServiceApi.Resources.ResourceSubStat, method = GET)
    Envelop getSubStat(@RequestParam(value = "queryParams", required = true) String queryParams,
                       @RequestParam(value = "page", required = false) Integer page,
                       @RequestParam(value = "size", required = false) Integer size);

    @RequestMapping(value = ServiceApi.Resources.ResourceRawFiles, method = GET)
    Envelop getRawFiles(@RequestParam(value = "profileId", required = false) String profileId,
                        @RequestParam(value = "cdaDocumentId", required = false) String cdaDocumentId,
                        @RequestParam(value = "page", required = false) Integer page,
                        @RequestParam(value = "size", required = false) Integer size);

    @RequestMapping(value = ServiceApi.Resources.ResourceRawFilesList, method = GET)
    Map<String,Envelop> getRawFilesList(@RequestParam(value = "profileId", required = true) String profileId,
                        @RequestParam(value = "cdaDocumentId", required = true) String[] cdaDocumentIdList);
}
