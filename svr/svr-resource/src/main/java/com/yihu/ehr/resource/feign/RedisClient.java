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
@FeignClient(name = MicroServices.Redis)
@RequestMapping(ApiVersion.Version1_0)
public interface RedisClient {


    //("获取地址redis")
    @RequestMapping(value = ServiceApi.Redis.AddressRedis, method = RequestMethod.GET)
    public String getAddressRedis(@RequestParam("key") String key);


    //("获取健康问题名称redis")
    @RequestMapping(value = ServiceApi.Redis.HealthProblemRedis, method = RequestMethod.GET)
    public String getHealthProblemRedis(@RequestParam("key") String key);


    //("获取ICD10健康问题 redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpRelationRedis, method = RequestMethod.GET)
    public String getIcd10HpRelationRedis(@RequestParam("key") String key);


    //("获取指标 redis")
    @RequestMapping(value = ServiceApi.Redis.IndicatorsRedis, method = RequestMethod.GET)
    public String getIndicatorsRedis(@RequestParam("key") String key);


    //("获取机构redis")
    @RequestMapping(value = ServiceApi.Redis.OrgRedis, method = RequestMethod.GET)
    public String getOrgRedis(@RequestParam("key") String key);

    //("获取机构区域redis")
    @RequestMapping(value = ServiceApi.Redis.OrgAreaRedis, method = RequestMethod.GET)
    public String getOrgAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasAreaRedis, method = RequestMethod.GET)
    public String getOrgSaasAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrgRedis, method = RequestMethod.GET)
    public String getOrgSaasOrgRedis(@RequestParam("key") String key);

    /******************************************* 资源化相关Redis *******************************************************************/
    //("获取资源化字典映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdaptionDict, method = RequestMethod.GET)
    public String getRsAdaptionDict(@RequestParam("cdaVersion") String cdaVersion,
                                    @RequestParam("dictCode") String dictCode,
                                    @RequestParam("srcDictEntryCode") String srcDictEntryCode);

    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdaptionMetaData, method = RequestMethod.GET)
    public String getRsAdaptionMetaData(@RequestParam("cdaVersion") String cdaVersion,
                                        @RequestParam("dictCode") String dictCode,
                                        @RequestParam("srcDictEntryCode") String srcDictEntryCode);


    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsMetaData, method = RequestMethod.GET)
    public String getRsMetaData(@RequestParam("key") String key);
    /******************************************* 标准相关Redis *******************************************************************/

    //("获取标准版本 redis")
    @RequestMapping(value = ServiceApi.Redis.StdVersion, method = RequestMethod.GET)
    public String getStdVersion(@RequestParam("key") String key);


    //("获取标准数据集代码 redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetCode, method = RequestMethod.GET)
    public String getDataSetCode(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过ID redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetName, method = RequestMethod.GET)
    public String getDataSetName(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过Code redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetNameByCode, method = RequestMethod.GET)
    public String getDataSetNameByCode(@RequestParam("version") String version,
                                       @RequestParam("code") String code);


    //("获取标准数据集--主从表 redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetMultiRecord, method = RequestMethod.GET)
    public String getDataSetMultiRecord(@RequestParam("version") String version,
                                        @RequestParam("code") String code);


    //("获取标准数据元对应类型 redis")
    @RequestMapping(value = ServiceApi.Redis.MetaDataType, method = RequestMethod.GET)
    public String getMetaDataType(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);

    //("获取标准数据元对应字典 redis")
    @RequestMapping(value = ServiceApi.Redis.MetaDataDict, method = RequestMethod.GET)
    public String getMetaDataDict(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);


    //("获取标准数据字典对应值 redis")
    @RequestMapping(value = ServiceApi.Redis.DictEntryValue, method = RequestMethod.GET)
    public String getDictEntryValue(@RequestParam("version") String version,
                                    @RequestParam("dictId") String dictId,
                                    @RequestParam("entryCode") String entryCode);
}
