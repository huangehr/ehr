package com.yihu.ehr.redis.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.redis.feign.XResourceClient;
import com.yihu.ehr.redis.feign.XStandardClient;
import com.yihu.ehr.redis.service.RedisInitService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @author hzp add at 20170425
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/redisInit")
@Api(value = "Redis初始化服务", description = "Redis初始化服务")
public class RedisInitEndPoint extends EnvelopRestEndPoint {

    @Autowired
    RedisInitService redisInitService;
    @Autowired
    XResourceClient resourceClient;
    @Autowired
    XStandardClient standardClient;
    @Autowired
    RedisClient redis;

    @ApiOperation("清除Redis缓存")
    @RequestMapping(value = ServiceApi.Redis.DeleteRedis, method = RequestMethod.POST)
    public String deleteRedis(@RequestParam("key") String key) throws Exception {
        redis.delete(key);
        return "Redis缓存清除成功！";
    }

    @ApiOperation("Redis缓存行政地址")
    @RequestMapping(value = ServiceApi.Redis.AddressRedis, method = RequestMethod.POST)
    public String cacheAddressDict() throws Exception {
        redisInitService.cacheAddressDict();
        return "Redis缓存行政地址完成！";
    }

    @ApiOperation("Redis缓存健康问题名称")
    @RequestMapping(value = ServiceApi.Redis.HealthProblemRedis, method = RequestMethod.POST)
    public String cacheHpName() throws Exception {
        redisInitService.cacheHpName();
        return "Redis缓存健康问题名称完成！";
    }

    @ApiOperation("Redis缓存ICD10")
    @RequestMapping(value = ServiceApi.Redis.Icd10NameRedis, method = RequestMethod.POST)
    public String cacheIcd10() throws Exception {
        redisInitService.cacheIcd10();
        return "Redis缓存ICD10完成！";
    }

    @ApiOperation("Redis缓存机构名称")
    @RequestMapping(value = ServiceApi.Redis.OrgRedis, method = RequestMethod.POST)
    public String cacheOrgName() throws Exception {
        redisInitService.cacheOrgName();
        return "Redis缓存机构名称完成！";
    }

    @ApiOperation("Redis缓存机构区域")
    @RequestMapping(value = ServiceApi.Redis.OrgAreaRedis, method = RequestMethod.POST)
    public String cacheOrgArea() throws Exception {
        redisInitService.cacheOrgArea();
        return "Redis缓存机构区域完成！";
    }

    @ApiOperation("Redis缓存机构Saas区域")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasAreaRedis, method = RequestMethod.POST)
    public String cacheOrgSaasArea() throws Exception {
        redisInitService.cacheOrgSaasArea();
        return "Redis缓存机构Saas区域完成！";
    }

    @ApiOperation("Redis缓存机构Saas机构")
    @RequestMapping(value = ServiceApi.Redis.OrgSaasOrgRedis, method = RequestMethod.POST)
    public String cacheOrgSaasOrg() throws Exception {
        redisInitService.cacheOrgSaasOrg();
        return "Redis缓存机构Saas机构完成！";
    }

    /************************************ 标准Redis *******************************************************************/
    @ApiOperation("Redis缓存标准")
    @RequestMapping(value = ServiceApi.Redis.Versions, method = RequestMethod.POST)
    public String versions(@ApiParam(value = "版本列表，使用逗号分隔", defaultValue = "000000000000,568ce002559f")
                         @RequestParam("versions") String versions,
                         @ApiParam(value = "强制清除再缓存", defaultValue = "true")
                         @RequestParam("force") boolean force) throws Exception {
        standardClient.versions(versions,force);
        return "Redis缓存标准完成！";
    }


    /************************************ 资源化Redis *******************************************************************/
    @RequestMapping(value= ServiceApi.Adaptions.Cache,method = RequestMethod.POST)
    @ApiOperation("缓存适配数据")
    public boolean cacheData(
            @ApiParam(name="id",value="schema_id",defaultValue = "")
            @PathVariable(value = "id")String id) throws Exception {
        return resourceClient.cacheData(id);
    }
}
