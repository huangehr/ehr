package com.yihu.ehr.resource.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author hzp
 * @created 2017.04.28
 */
@ApiIgnore
@FeignClient(name = MicroServices.StdRedis)
@RequestMapping(ApiVersion.Version1_0)
public interface StdRedisClient {

    //("获取标准版本 redis")
    @RequestMapping(value = ServiceApi.Redis.StdVersion, method = RequestMethod.GET)
    public String getStdVersion(@RequestParam("key") String key);


    //("获取标准数据集代码 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetCode, method = RequestMethod.GET)
    public String getDataSetCode(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过ID redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetName, method = RequestMethod.GET)
    public String getDataSetName(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过Code redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetNameByCode, method = RequestMethod.GET)
    public String getDataSetNameByCode(@RequestParam("version") String version,
                                       @RequestParam("code") String code);


    //("获取标准数据集--主从表 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetMultiRecord, method = RequestMethod.GET)
    public String getDataSetMultiRecord(@RequestParam("version") String version,
                                        @RequestParam("code") String code);


    //("获取标准数据元对应类型 redis")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataType, method = RequestMethod.GET)
    public String getMetaDataType(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);

    //("获取标准数据元对应字典 redis")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataDict, method = RequestMethod.GET)
    public String getMetaDataDict(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);


    //("获取标准数据字典对应值 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValue, method = RequestMethod.GET)
    public String getDictEntryValue(@RequestParam("version") String version,
                                    @RequestParam("dictId") String dictId,
                                    @RequestParam("entryCode") String entryCode);
}
