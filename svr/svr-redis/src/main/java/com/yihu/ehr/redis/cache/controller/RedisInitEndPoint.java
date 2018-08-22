package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.service.RedisInitService;
import com.yihu.ehr.redis.client.RedisClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RedisInitEndPoint", description = "Redis初始化服务", tags = {"缓存服务-数据初始化接口"})
public class RedisInitEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisInitService redisInitService;
    @Autowired
    private RedisClient redis;

    @ApiOperation("清除Redis缓存")
    @RequestMapping(value = ServiceApi.Redis.Delete, method = RequestMethod.POST)
    @ApiIgnore
    public String deleteRedis(
            @ApiParam(name = "key", value = "机构编码、ICD10编码、健康问题编码等")
            @RequestParam(value = "key") String key) throws Exception {
        redis.delete(key);
        return "缓存清除成功！";
    }

    @ApiOperation("Redis缓存HP健康问题名称")
    @RequestMapping(value = ServiceApi.Redis.InitHealthProblem, method = RequestMethod.POST)
    public int cacheHpName() throws Exception {
        return redisInitService.cacheHpName();
    }

    @ApiOperation("1.Redis缓存ICD10名称 2.对应HP健康问题编码 3.慢病信息")
    @RequestMapping(value = ServiceApi.Redis.InitIcd10, method = RequestMethod.POST)
    public int cacheIcd10() throws Exception {
        return redisInitService.cacheIcd10();
    }

    @ApiOperation("Redis缓存机构名称")
    @RequestMapping(value = ServiceApi.Redis.InitOrgName, method = RequestMethod.POST)
    public int cacheOrgName() throws Exception {
        return redisInitService.cacheOrgName();
    }

    @ApiOperation("Redis缓存机构区域")
    @RequestMapping(value = ServiceApi.Redis.InitOrgArea, method = RequestMethod.POST)
    public int cacheOrgArea() throws Exception {
        return redisInitService.cacheOrgArea();
    }

    @ApiOperation("Redis缓存机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.InitOrgSaasArea, method = RequestMethod.POST)
    public int cacheOrgSaasArea() throws Exception {
        return redisInitService.cacheOrgSaasArea();
    }

    @ApiOperation("Redis缓存机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.InitOrgSaasOrg, method = RequestMethod.POST)
    public int cacheOrgSaasOrg() throws Exception {
        return redisInitService.cacheOrgSaasOrg();
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterMeta, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据元数据")
    public int cacheAdapterMetadata(
            @ApiParam(name = "id", value = "rs_adapter_scheme.id")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "deleteAll", defaultValue = "false", required = true)
            @RequestParam(value = "deleteAll") boolean deleteAll) throws Exception {
       return redisInitService.cacheAdapterMetadata(id, deleteAll);
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsMetadataDict, method = RequestMethod.POST)
    @ApiOperation("Redis缓存资源化数据元字典（dict_code不为空）")
    public int cacheMetadataDict() throws Exception {
        return redisInitService.cacheMetadataDict();
    }

    @ApiOperation("Redis缓存行政区域")
    @RequestMapping(value = ServiceApi.Redis.InitAreaName, method = RequestMethod.POST)
    public int cacheAreaName() throws Exception {
        return redisInitService.cacheAreaName();
    }

    //TODO ------------------- 未知用途 --------------------------

    /*@ApiOperation("Redis缓存指标(-1)")
    @RequestMapping(value = ServiceApi.Redis.InitIndicatorsDict, method = RequestMethod.POST)
    public String cacheIndicatorsDict() throws Exception {
        redisInitService.cacheIndicatorsDict();
        return "Redis缓存指标完成！";
    }*/

}
