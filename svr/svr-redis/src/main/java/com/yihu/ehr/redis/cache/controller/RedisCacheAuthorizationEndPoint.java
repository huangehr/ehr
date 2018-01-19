package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheAuthorization;
import com.yihu.ehr.redis.cache.entity.RedisCacheAuthorization;
import com.yihu.ehr.redis.cache.service.RedisCacheAuthorizationService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存授权 接口
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存授权接口", tags = {"缓存服务管理--缓存授权接口"})
public class RedisCacheAuthorizationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheAuthorizationService redisCacheAuthorizationService;

    @ApiOperation("根据ID获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisCacheAuthorization mRedisCacheAuthorization = convertToModel(redisCacheAuthorizationService.getById(id), MRedisCacheAuthorization.class);
            envelop.setObj(mRedisCacheAuthorization);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存授权。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存授权发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Search, method = RequestMethod.GET)
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空则返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "筛选条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            List<RedisCacheAuthorization> redisCacheAuthorization = redisCacheAuthorizationService.search(fields, filters, sorts, page, size);
            int count = (int) redisCacheAuthorizationService.getCount(filters);
            List<MRedisCacheAuthorization> mRedisCacheAuthorization = (List<MRedisCacheAuthorization>) convertToModels(redisCacheAuthorization, new ArrayList<MRedisCacheAuthorization>(), MRedisCacheAuthorization.class, fields);
            envelop = getPageResult(mRedisCacheAuthorization, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存授权列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存授权发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(value = "缓存授权JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheAuthorization newEntity = objectMapper.readValue(entityJson, RedisCacheAuthorization.class);
            newEntity = redisCacheAuthorizationService.save(newEntity);

            MRedisCacheAuthorization mRedisCacheAuthorization = convertToModel(newEntity, MRedisCacheAuthorization.class);
            envelop.setObj(mRedisCacheAuthorization);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增缓存授权。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增缓存授权发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(value = "缓存授权JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheAuthorization updateEntity = objectMapper.readValue(entityJson, RedisCacheAuthorization.class);
            updateEntity = redisCacheAuthorizationService.save(updateEntity);

            MRedisCacheAuthorization mRedisCacheAuthorization = convertToModel(updateEntity, MRedisCacheAuthorization.class);
            envelop.setObj(mRedisCacheAuthorization);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新缓存授权。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新缓存授权发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除缓存授权")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            redisCacheAuthorizationService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除缓存授权。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除缓存授权发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证指定缓存分类下应用ID是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheAuthorization.IsUniqueAppId, method = RequestMethod.GET)
    public Envelop isUniqueAppId(
            @ApiParam(name = "id", value = "缓存授权ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "categoryCode", value = "缓存分类编码", required = true)
            @RequestParam(value = "categoryCode") String categoryCode,
            @ApiParam(name = "appId", value = "应用ID", required = true)
            @RequestParam(value = "appId") String appId) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheAuthorizationService.isUniqueAppId(id, categoryCode, appId);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("当前缓存分类下的当前应用ID已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
