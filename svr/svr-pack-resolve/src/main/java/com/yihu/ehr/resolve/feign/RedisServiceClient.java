package com.yihu.ehr.resolve.feign;

import com.yihu.ehr.constants.ServiceApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author hzp
 * @created 2017.04.28
 */
//@ApiIgnore
//@FeignClient(name = MicroServices.Redis)
//@RequestMapping(ApiVersion.Version1_0)
@Deprecated
public interface RedisServiceClient {


    //("获取地址redis")
    @RequestMapping(value = ServiceApi.Redis.Address, method = RequestMethod.GET)
    String getAddressRedis(@RequestParam("key") String key);


    //("获取健康问题名称redis")
    @RequestMapping(value = ServiceApi.Redis.HealthProblem, method = RequestMethod.GET)
    String getHealthProblemRedis(@RequestParam("key") String key);


    //("获取ICD10健康问题 redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpR, method = RequestMethod.GET)
    String getIcd10HpRelationRedis(@RequestParam("key") String key);

    //("获取健康问题名称redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10Name, method = RequestMethod.GET)
    String getIcd10NameRedis(@RequestParam("key") String key);

    //("获取健康问题名称redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpCode, method = RequestMethod.GET)
    String getIcd10HpCodeRedis(@RequestParam("key") String key);

    //("获取指标 redis")
    @RequestMapping(value = ServiceApi.Redis.IndicatorsDict, method = RequestMethod.GET)
    String getIndicatorsRedis(@RequestParam("key") String key);


    //("获取机构redis")
    @RequestMapping(value = ServiceApi.Redis.OrgName, method = RequestMethod.GET)
    String getOrgRedis(@RequestParam("key") String key);

    //("获取机构区域redis")
    @RequestMapping(value = ServiceApi.Redis.OrgArea, method = RequestMethod.GET)
    String getOrgAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasArea, method = RequestMethod.GET)
    String getOrgSaasAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrg, method = RequestMethod.GET)
    String getOrgSaasOrgRedis(@RequestParam("key") String key);

    /******************************************* 资源化相关Redis *******************************************************************/
    //("获取资源化字典映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterDict, method = RequestMethod.GET)
    String getRsAdaptionDict(@RequestParam("cdaVersion") String cdaVersion,
                             @RequestParam("srcDictCode") String srcDictCode,
                             @RequestParam("srcDictEntryCode") String srcDictEntryCode);

    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterMetadata, method = RequestMethod.GET)
    String getRsAdaptionMetaData(@RequestParam("cdaVersion") String cdaVersion,
                                 @RequestParam("srcDataSetCode") String srcDataSetCode,
                                 @RequestParam("srcMetadataCode") String srcMetadataCode);

    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsMetadata, method = RequestMethod.GET)
    String getRsMetaData(@RequestParam("key") String key);

}
