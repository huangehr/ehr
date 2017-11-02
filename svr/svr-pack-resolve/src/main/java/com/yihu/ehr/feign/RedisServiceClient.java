package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
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
    /******************************************* 标准相关Redis *******************************************************************/

    //("获取标准版本 redis")
    @RequestMapping(value = ServiceApi.Redis.StdVersion, method = RequestMethod.GET)
    String getStdVersion(@RequestParam("key") String key);


    //("获取标准数据集代码 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetCode, method = RequestMethod.GET)
    String getDataSetCode(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过ID redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetName, method = RequestMethod.GET)
    String getDataSetName(@RequestParam("version") String version,
                                 @RequestParam("id") String id);


    //("获取标准数据集名称通过Code redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetNameByCode, method = RequestMethod.GET)
    String getDataSetNameByCode(@RequestParam("version") String version,
                                       @RequestParam("code") String code);


    //("获取标准数据集--主从表 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetMultiRecord, method = RequestMethod.GET)
    String getDataSetMultiRecord(@RequestParam("version") String version,
                                        @RequestParam("code") String code);


    //("获取标准数据元对应类型 redis")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataType, method = RequestMethod.GET)
    String getMetaDataType(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);

    //("获取标准数据元对应字典 redis")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataDict, method = RequestMethod.GET)
    String getMetaDataDict(@RequestParam("version") String version,
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @RequestParam("innerCode") String innerCode);


    //("获取标准数据字典对应值 redis")
    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValue, method = RequestMethod.GET)
    String getDictEntryValue(@RequestParam("version") String version,
                                    @RequestParam("dictId") String dictId,
                                    @RequestParam("entryCode") String entryCode);
}
