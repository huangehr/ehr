package com.yihu.ehr.profile.feign;

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
public interface XRedisServiceClient {


    //("获取地址redis")
    @RequestMapping(value = ServiceApi.Redis.Address, method = RequestMethod.GET)
    public String getAddressRedis(@RequestParam("key") String key);


    //("获取健康问题名称redis")
    @RequestMapping(value = ServiceApi.Redis.HealthProblem, method = RequestMethod.GET)
    public String getHealthProblemRedis(@RequestParam("key") String key);


    //("获取ICD10健康问题 redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpR, method = RequestMethod.GET)
    public String getIcd10HpRelationRedis(@RequestParam("key") String key);


    //("获取指标 redis")
    @RequestMapping(value = ServiceApi.Redis.IndicatorsDict, method = RequestMethod.GET)
    public String getIndicatorsRedis(@RequestParam("key") String key);


    //("获取机构redis")
    @RequestMapping(value = ServiceApi.Redis.OrgName, method = RequestMethod.GET)
    public String getOrgRedis(@RequestParam("key") String key);

    //("获取机构区域redis")
    @RequestMapping(value = ServiceApi.Redis.OrgArea, method = RequestMethod.GET)
    public String getOrgAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasArea, method = RequestMethod.GET)
    public String getOrgSaasAreaRedis(@RequestParam("key") String key);

    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrg, method = RequestMethod.GET)
    public String getOrgSaasOrgRedis(@RequestParam("key") String key);

    /******************************************* 资源化相关Redis *******************************************************************/
    //("获取资源化字典映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterDict, method = RequestMethod.GET)
    public String getRsAdaptionDict(@RequestParam("cdaVersion") String cdaVersion,
                                    @RequestParam("srcDictCode") String srcDictCode,
                                    @RequestParam("srcDictEntryCode") String srcDictEntryCode);

    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterMetadata, method = RequestMethod.GET)
    public String getRsAdaptionMetaData(@RequestParam("cdaVersion") String cdaVersion,
                                        @RequestParam("srcDataSetCode") String srcDataSetCode,
                                        @RequestParam("srcMetadataCode") String srcMetadataCode);


    //("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsMetadata, method = RequestMethod.GET)
    public String getRsMetaData(@RequestParam("key") String key);

}
