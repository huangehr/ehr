package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "Redis服务", description = "Redis服务")
public class RedisEndPoint extends EnvelopRestEndPoint {

    @Autowired
    RedisService redisService;


    @ApiOperation("获取地址redis")
    @RequestMapping(value = ServiceApi.Redis.AddressRedis, method = RequestMethod.GET)
    public String getAddressRedis(@ApiParam(value = "key", defaultValue = "")
                                      @RequestParam("key") String key)
    {
        return redisService.getAddressRedis(key);
    }


    @ApiOperation("获取健康问题redis")
    @RequestMapping(value = ServiceApi.Redis.HealthProblemRedis, method = RequestMethod.GET)
    public String getHealthProblemRedis(@ApiParam(value = "key", defaultValue = "")
                                            @RequestParam("key") String key)
    {
        return redisService.getHealthProblemRedis(key);
    }


    @ApiOperation("获取ICD10健康问题 redis")
    @RequestMapping(value = ServiceApi.Redis.Icd10HpRelationRedis, method = RequestMethod.GET)
    public String getIcd10HpRelationRedis(@ApiParam(value = "key", defaultValue = "")
                                              @RequestParam("key") String key)
    {
        return redisService.getIcd10HpRelationRedis(key);
    }


    @ApiOperation("获取指标 redis")
    @RequestMapping(value = ServiceApi.Redis.IndicatorsRedis, method = RequestMethod.GET)
    public String getIndicatorsRedis(@ApiParam(value = "key", defaultValue = "")
                                         @RequestParam("key") String key)
    {
        return redisService.getIndicatorsRedis(key);
    }


    @ApiOperation("获取机构redis")
    @RequestMapping(value = ServiceApi.Redis.OrgRedis, method = RequestMethod.GET)
    public String getOrgRedis(@ApiParam(value = "key", defaultValue = "")
                                  @RequestParam("key") String key)
    {
        return redisService.getOrgRedis(key);
    }


    /******************************************* 资源化相关Redis *******************************************************************/

    @ApiOperation("获取资源化字典映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdaptionDict, method = RequestMethod.GET)
    public String getRsAdaptionDict(@ApiParam(value = "cdaVersion", defaultValue = "")
                                    @RequestParam("cdaVersion") String cdaVersion,
                                    @ApiParam(value = "dictCode", defaultValue = "")
                                    @RequestParam("dictCode") String dictCode,
                                    @ApiParam(value = "srcDictEntryCode", defaultValue = "")
                                    @RequestParam("srcDictEntryCode") String srcDictEntryCode)
    {
        return redisService.getRsAdaptionDict(cdaVersion,dictCode,srcDictEntryCode);
    }


    @ApiOperation("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsAdaptionMetaData, method = RequestMethod.GET)
    public String getRsAdaptionMetaData(@ApiParam(value = "cdaVersion", defaultValue = "")
                                            @RequestParam("cdaVersion") String cdaVersion,
                                        @ApiParam(value = "dictCode", defaultValue = "")
                                            @RequestParam("dictCode") String dictCode,
                                        @ApiParam(value = "srcDictEntryCode", defaultValue = "")
                                            @RequestParam("srcDictEntryCode") String srcDictEntryCode)
    {
        return redisService.getRsAdaptionMetaData(cdaVersion,dictCode,srcDictEntryCode);
    }


    @ApiOperation("获取资源化数据元映射 redis")
    @RequestMapping(value = ServiceApi.Redis.RsMetaData, method = RequestMethod.GET)
    public String getRsMetaData(@ApiParam(value = "key", defaultValue = "")
                                    @RequestParam("key") String key)
    {
        return redisService.getRsMetaData(key);
    }
    /******************************************* 标准相关Redis *******************************************************************/

    @ApiOperation("获取标准版本 redis")
    @RequestMapping(value = ServiceApi.Redis.StdVersion, method = RequestMethod.GET)
    public String getStdVersion(@ApiParam(value = "key", defaultValue = "")
                                    @RequestParam("key") String key)
    {
        return redisService.getStdVersion(key);
    }


    @ApiOperation("获取标准数据集代码 redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetCode, method = RequestMethod.GET)
    public String getDataSetCode(@ApiParam(value = "version", defaultValue = "")
                                 @RequestParam("version") String version,
                                 @ApiParam(value = "id", defaultValue = "")
                                 @RequestParam("id") String id){
        return redisService.getDataSetCode(version, id);
    }


    @ApiOperation("获取标准数据集名称通过ID redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetName, method = RequestMethod.GET)
    public String getDataSetName(@ApiParam(value = "version", defaultValue = "")
                                     @RequestParam("version") String version,
                                 @ApiParam(value = "id", defaultValue = "")
                                 @RequestParam("id") String id){
        return redisService.getDataSetName(version, id);
    }


    @ApiOperation("获取标准数据集名称通过Code redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetNameByCode, method = RequestMethod.GET)
    public String getDataSetNameByCode(@ApiParam(value = "version", defaultValue = "")
                                           @RequestParam("version") String version,
                                       @ApiParam(value = "code", defaultValue = "")
                                           @RequestParam("code") String code){
        return redisService.getDataSetNameByCode(version, code);
    }


    @ApiOperation("获取标准数据集--主从表 redis")
    @RequestMapping(value = ServiceApi.Redis.DataSetMultiRecord, method = RequestMethod.GET)
    public String getDataSetMultiRecord(@ApiParam(value = "version", defaultValue = "")
                                            @RequestParam("version") String version,
                                        @ApiParam(value = "code", defaultValue = "")
                                            @RequestParam("code") String code){
        return redisService.getDataSetMultiRecord(version, code);
    }


    @ApiOperation("获取标准数据元对应类型 redis")
    @RequestMapping(value = ServiceApi.Redis.MetaDataType, method = RequestMethod.GET)
    public String getMetaDataType(@ApiParam(value = "version", defaultValue = "")
                                      @RequestParam("version") String version,
                                  @ApiParam(value = "dataSetCode", defaultValue = "")
                                  @RequestParam("dataSetCode") String dataSetCode,
                                  @ApiParam(value = "innerCode", defaultValue = "")
                                  @RequestParam("innerCode") String innerCode) {

        return redisService.getMetaDataType( version, dataSetCode , innerCode);
    }

    @ApiOperation("获取标准数据元对应字典 redis")
    @RequestMapping(value = ServiceApi.Redis.MetaDataDict, method = RequestMethod.GET)
    public String getMetaDataDict(@ApiParam(value = "version", defaultValue = "")
                                      @RequestParam("version") String version,
                                  @ApiParam(value = "dataSetCode", defaultValue = "")
                                      @RequestParam("dataSetCode") String dataSetCode,
                                  @ApiParam(value = "innerCode", defaultValue = "")
                                      @RequestParam("innerCode") String innerCode) {
        return redisService.getMetaDataDict(version, dataSetCode,innerCode);
    }


    @ApiOperation("获取标准数据字典对应值 redis")
    @RequestMapping(value = ServiceApi.Redis.DictEntryValue, method = RequestMethod.GET)
    public String getDictEntryValue(@ApiParam(value = "version", defaultValue = "")
                                        @RequestParam("version") String version,
                                    @ApiParam(value = "dictId", defaultValue = "")
                                    @RequestParam("dictId") String dictId,
                                    @ApiParam(value = "entryCode", defaultValue = "")
                                    @RequestParam("entryCode") String entryCode) {

        return redisService.getDictEntryValue( version, dictId , entryCode);
    }


}
