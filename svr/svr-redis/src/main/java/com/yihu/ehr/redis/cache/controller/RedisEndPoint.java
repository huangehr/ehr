package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author hzp add at 20170425
 * @modified Airhead 2018.01.21
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RedisEndPoint", description = "Redis数据缓存服务", tags = {"缓存服务-数据获取接口"})
public class RedisEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisService redisService;

    @ApiOperation("通过HP编码取健康问题名称")
    @RequestMapping(value = ServiceApi.Redis.HealthProblem, method = RequestMethod.GET)
    public String getHealthProblem(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getHealthProblem(key);
    }

    @ApiOperation("通过ICD10编码获取ICD10名称")
    @RequestMapping(value = ServiceApi.Redis.Icd10Name, method = RequestMethod.GET)
    public String getIcd10Name(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getIcd10Name(key);
    }

    @ApiOperation("通过ICD10编码获取HP健康问题编码")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpCode, method = RequestMethod.GET)
    public String getHpCodeByIcd10(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getHpCodeByIcd10(key);
    }

    @ApiOperation("通过ICD10编码获取ICD10慢病信息")
    @RequestMapping(value = ServiceApi.Redis.Icd10ChronicInfo, method = RequestMethod.GET)
    public String cacheIcd10ChronicInfo(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) throws Exception {
        return redisService.getChronicInfo(key);
    }

    @ApiOperation("通过机构编码获取机构名称")
    @RequestMapping(value = ServiceApi.Redis.OrgName, method = RequestMethod.GET)
    public String getOrgName(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getOrgName(key);
    }

    @ApiOperation("通过机构编码获取机构区域")
    @RequestMapping(value = ServiceApi.Redis.OrgArea, method = RequestMethod.GET)
    public String getOrgArea(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getOrgArea(key);
    }

    @ApiOperation("通过机构编码获取机构SAAS区域权限范围")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasArea, method = RequestMethod.GET)
    public String getOrgSaasArea(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getOrgSaasArea(key);
    }

    @ApiOperation("通过机构编码获取机构SAAS机构权限范围")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrg, method = RequestMethod.GET)
    public String getOrgSaasOrg(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getOrgSaasOrg(key);
    }

    //------------------------------------ 资源化相关 START -------------------------------------------------------

    @ApiOperation("通过StdDataSet版本、编码和标准数据元内部编码获取资源化数据元ID(rs_adapter_metadata)")
    @RequestMapping(value = ServiceApi.Redis.RsAdapterMetadata, method = RequestMethod.GET)
    public String getRsAdapterMetaData(
            @ApiParam(value = "cdaVersion")
            @RequestParam(value = "cdaVersion") String cdaVersion,
            @ApiParam(value = "srcDataSetCode")
            @RequestParam(value = "srcDataSetCode") String srcDataSetCode,
            @ApiParam(value = "srcMetadataCode")
            @RequestParam(value = "srcMetadataCode") String srcMetadataCode) {
        return redisService.getRsAdapterMetaData(cdaVersion, srcDataSetCode, srcMetadataCode);
    }

    @ApiOperation("通过资源化数据元ID获取标准数据元字典编码")
    @RequestMapping(value = ServiceApi.Redis.RsMetadataDict, method = RequestMethod.GET)
    public String getRsMetaData(
            @ApiParam(value = "key", required = true)
            @RequestParam(value = "key") String key) {
        return redisService.getRsMetadataDict(key);
    }

    //------------------------------------ 资源化相关 END -------------------------------------------------------

    //------------------------------------ 标准相关 START -------------------------------------------------------
    @ApiOperation("通过version值获取版本名")
    @RequestMapping(value = ServiceApi.Redis.StdVersion, method = RequestMethod.GET)
    public String getStdVersion(
            @ApiParam(value = "key", defaultValue = "59083976eebd")
            @RequestParam(value = "key") String key) {
        return redisService.getStdVersion(key);
    }

    @ApiOperation("通过StdDataSet版本和id获取标准数据集编码")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetCode, method = RequestMethod.GET)
    public String getDataSetCode(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
             @ApiParam(value = "id")
             @RequestParam(value = "id") String id) {
        return redisService.getDataSetCode(version, id);
    }

    @ApiOperation("通过StdDataSet版本和id获取标准数据集名称")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetName, method = RequestMethod.GET)
    public String getDataSetName(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "id")
            @RequestParam(value = "id") String id) {
        return redisService.getDataSetName(version, id);
    }


    @ApiOperation("通过StdDataSet版本和编码获取标准数据集名称")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetNameByCode, method = RequestMethod.GET)
    public String getDataSetNameByCode(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "code")
            @RequestParam(value = "code") String code) {
        return redisService.getDataSetNameByCode(version, code);
    }


    @ApiOperation("通过StdDataSet版本和编码获取标准数据集主从表信息")
    @RequestMapping(value = ServiceApi.Redis.StdDataSetMultiRecord, method = RequestMethod.GET)
    public Boolean getDataSetMultiRecord(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "code")
            @RequestParam(value = "code") String code) {
        return redisService.getDataSetMultiRecord(version, code);
    }


    @ApiOperation("通过StdDataSet版本、编码以及标准数据元的内部编码获取标准数据元对应类型")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataType, method = RequestMethod.GET)
    public String getMetaDataType(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dataSetCode")
            @RequestParam(value = "dataSetCode") String dataSetCode,
            @ApiParam(value = "innerCode")
            @RequestParam(value = "innerCode") String innerCode) {

        return redisService.getMetaDataType(version, dataSetCode, innerCode);
    }

    @ApiOperation("通过StdDataSet版本、编码以及标准数据元的内部编码获取标准数据元的格式")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataFormat, method = RequestMethod.GET)
    public String getMetaDataFormat(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dataSetCode")
            @RequestParam(value = "dataSetCode") String dataSetCode,
            @ApiParam(value = "innerCode")
            @RequestParam(value = "innerCode") String innerCode) {
        return redisService.getMetaDataFormat(version, dataSetCode, innerCode);
    }

    @ApiOperation("通过StdDataSet版本、编码以及标准数据元的内部编码获取标准数据元的是否可为空")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataNullable, method = RequestMethod.GET)
    public Boolean isMetaDataNullable(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dataSetCode")
            @RequestParam(value = "dataSetCode") String dataSetCode,
            @ApiParam(value = "innerCode")
            @RequestParam(value = "innerCode") String innerCode) {
        return redisService.isMetaDataNullable(version, dataSetCode, innerCode);
    }

    @ApiOperation("通过StdDataSet版本、编码以及标准数据元的内部编码获取标准数据元字典ID")
    @RequestMapping(value = ServiceApi.Redis.StdMetadataDict, method = RequestMethod.GET)
    public String getMetaDataDict(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dataSetCode")
            @RequestParam(value = "dataSetCode") String dataSetCode,
            @ApiParam(value = "innerCode")
            @RequestParam(value = "innerCode") String innerCode) {
        return redisService.getMetaDataDict(version, dataSetCode, innerCode);
    }


    @ApiOperation("通过StdDataSet版本、标准数据元字典ID以及标准数据元字典项编码获取标准数据元字典项值")
    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValue, method = RequestMethod.GET)
    public String getDictEntryValue(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dictId")
            @RequestParam(value = "dictId") String dictId,
            @ApiParam(value = "entryCode")
            @RequestParam(value = "entryCode") String entryCode) {
        return redisService.getDictEntryValue(version, dictId, entryCode);
    }


    @ApiOperation("通过StdDataSet版本、标准数据元字典ID以及标准数据元字典项编码判断值是否存在")
    @RequestMapping(value = ServiceApi.Redis.StdDictEntryValueExist, method = RequestMethod.GET)
    public Boolean isDictValueExist(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dictId")
            @RequestParam(value = "dictId") String dictId,
            @ApiParam(value = "entryValue")
            @RequestParam(value = "entryValue") String entryValue) {
        return redisService.isDictValueExist(version, dictId, entryValue);
    }

    @ApiOperation("通过StdDataSet版本、标准数据元字典ID以及标准数据元字典项编码判断编码是否存在")
    @RequestMapping(value = ServiceApi.Redis.StdDictEntryCodeExist, method = RequestMethod.GET)
    public Boolean isDictCodeExist(
            @ApiParam(value = "version")
            @RequestParam(value = "version") String version,
            @ApiParam(value = "dictId")
            @RequestParam(value = "dictId") String dictId,
            @ApiParam(value = "entryCode")
            @RequestParam(value = "entryCode") String entryCode) {
        return redisService.isDictCodeExist(version, dictId, entryCode);
    }
    //------------------------------------ 标准相关 END -------------------------------------------------------

    /*@ApiOperation("通过指标编码获取指标相关数据")
    @RequestMapping(value = ServiceApi.Redis.IndicatorsDict, method = RequestMethod.GET)
    public String getIndicators(
            @ApiParam(value = "key")
            @RequestParam(value = "key") String key) {
        return redisService.getIndicators(key);
    }*/

}
