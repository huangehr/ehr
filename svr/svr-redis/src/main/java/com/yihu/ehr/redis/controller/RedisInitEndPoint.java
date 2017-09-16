package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.redis.service.RedisInitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RedisInit", description = "Redis初始化服务（使用原生SQL提高效率）")
public class RedisInitEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisInitService redisInitService;
    @Autowired
    private RedisClient redis;

    @ApiOperation("清除Redis缓存")
    @RequestMapping(value = ServiceApi.Redis.Delete, method = RequestMethod.POST)
    public String deleteRedis(
            @ApiParam(name = "key", value = "机构编码、ICD10编码、健康问题编码等")
            @RequestParam(value = "key") String key) throws Exception {
        redis.delete(key);
        return "Redis缓存清除成功！";
    }

    @ApiOperation("Redis缓存行政地址")
    @RequestMapping(value = ServiceApi.Redis.InitAddress, method = RequestMethod.POST)
    public String cacheAddressDict() throws Exception {
        redisInitService.cacheAddressDict();
        return "Redis缓存行政地址完成！";
    }

    @ApiOperation("Redis缓存HP健康问题名称")
    @RequestMapping(value = ServiceApi.Redis.InitHealthProblem, method = RequestMethod.POST)
    public String cacheHpName() throws Exception {
        redisInitService.cacheHpName();
        return "Redis缓存HP健康问题名称！";
    }

    @ApiOperation("Redis缓存ICD10对应HP健康问题的编码和名称组合值")
    @RequestMapping(value = ServiceApi.Redis.InitIcd10HpR, method = RequestMethod.POST)
    public String cacheIcd10HpRelation(
            @ApiParam(name = "force", value = "强制清除再缓存", defaultValue = "true")
            @RequestParam("force") boolean force) throws Exception {
        redisInitService.cacheIcd10HpRelation(force);
        return "Redis缓存ICD10对应HP健康问题的编码和名称组合值！";
    }

    @ApiOperation("Redis缓存ICD10名称和对应HP健康问题编码")
    @RequestMapping(value = ServiceApi.Redis.InitIcd10, method = RequestMethod.POST)
    public String cacheIcd10() throws Exception {
        redisInitService.cacheIcd10();
        return "Redis缓存ICD10名称和对应HP健康问题编码！";
    }

    @ApiOperation("Redis缓存指标")
    @RequestMapping(value = ServiceApi.Redis.InitIndicatorsDict, method = RequestMethod.POST)
    public String cacheIndicatorsDict() throws Exception {
        redisInitService.cacheIndicatorsDict();
        return "Redis缓存指标完成！";
    }

    @ApiOperation("Redis缓存机构名称")
    @RequestMapping(value = ServiceApi.Redis.InitOrgName, method = RequestMethod.POST)
    public String cacheOrgName() throws Exception {
        redisInitService.cacheOrgName();
        return "Redis缓存机构名称完成！";
    }

    @ApiOperation("Redis缓存机构区域")
    @RequestMapping(value = ServiceApi.Redis.InitOrgArea, method = RequestMethod.POST)
    public String cacheOrgArea() throws Exception {
        redisInitService.cacheOrgArea();
        return "Redis缓存机构区域完成！";
    }

    @ApiOperation("Redis缓存机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.InitOrgSaasArea, method = RequestMethod.POST)
    public String cacheOrgSaasArea() throws Exception {
        redisInitService.cacheOrgSaasArea();
        return "Redis缓存机构Saas区域完成！";
    }

    @ApiOperation("Redis缓存机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.InitOrgSaasOrg, method = RequestMethod.POST)
    public String cacheOrgSaasOrg() throws Exception {
        redisInitService.cacheOrgSaasOrg();
        return "Redis缓存机构Saas机构完成！";
    }

    @ApiOperation("Redis缓存标准版本")
    @RequestMapping(value = ServiceApi.Redis.InitVersions, method = RequestMethod.POST)
    public String cacheVersions(
            @ApiParam(name = "versions", value = "版本列表，使用逗号分隔", defaultValue = "59083976eebd")
            @RequestParam("versions") String versions,
            @ApiParam(name = "force", value = "强制清除再缓存", defaultValue = "true")
            @RequestParam("force") boolean force) throws Exception {
        redisInitService.cacheVersions(versions, force);
        return "Redis缓存标准版本完成！";
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterDict, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据字典数据")
    public String cacheAdapterDict(
            @ApiParam(name="id",value="rs_adapter_scheme.id")
            @PathVariable(value = "id") String id) throws Exception {
        redisInitService.cacheAdapterDict(id);
        return "Redis缓存适配数据字典数据完成";
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsAdapterMeta, method = RequestMethod.POST)
    @ApiOperation("Redis缓存适配数据元数据")
    public String cacheAdapterMetadata(
            @ApiParam(name="id",value="rs_adapter_scheme.id")
            @PathVariable(value = "id") String id) throws Exception {
       redisInitService.cacheAdapterMetadata(id);
       return "Redis缓存适配数据元数据完成";
    }

    @RequestMapping(value= ServiceApi.Redis.InitRsMetadata, method = RequestMethod.POST)
    @ApiOperation("Redis缓存资源化数据元字典（dict_code不为空）")
    public String cacheMetadata() throws Exception {
        redisInitService.cacheMetadata();
        return "Redis缓存资源化数据元字典完成";
    }

}
