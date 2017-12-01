package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.redis.MRedisCacheCategory;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import com.yihu.ehr.redis.cache.service.RedisCacheCategoryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存分类 接口
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存分类接口", tags = {"缓存服务管理--缓存分类接口"})
public class RedisCacheCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheCategoryService redisCacheCategoryService;

    @ApiOperation("根据ID获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.GetById, method = RequestMethod.GET)
    public Envelop getById(
            @ApiParam(name = "id", value = "主键", required = true)
            @PathVariable(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            MRedisCacheCategory mRedisCacheCategory = convertToModel(redisCacheCategoryService.getById(id), MRedisCacheCategory.class);
            envelop.setObj(mRedisCacheCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据条件获取缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Search, method = RequestMethod.GET)
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
            List<RedisCacheCategory> redisCacheCategoryList = redisCacheCategoryService.search(fields, filters, sorts, page, size);
            int count = (int) redisCacheCategoryService.getCount(filters);
            List<MRedisCacheCategory> mRedisCacheCategoryList = (List<MRedisCacheCategory>) convertToModels(redisCacheCategoryList, new ArrayList<MRedisCacheCategory>(), MRedisCacheCategory.class, fields);
            envelop = getPageResult(mRedisCacheCategoryList, count, page, size);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取缓存分类列表。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("获取缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("新增缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.POST)
    public Envelop add(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestParam(value = "entityJson") String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheCategory newEntity = objectMapper.readValue(entityJson, RedisCacheCategory.class);
            newEntity = redisCacheCategoryService.save(newEntity);

            MRedisCacheCategory mRedisCacheCategory = convertToModel(newEntity, MRedisCacheCategory.class);
            envelop.setObj(mRedisCacheCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功新增缓存分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("新增缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("更新缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Save, method = RequestMethod.PUT)
    public Envelop update(
            @ApiParam(name = "entityJson", value = "缓存分类JSON", required = true)
            @RequestBody String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            RedisCacheCategory updateEntity = objectMapper.readValue(entityJson, RedisCacheCategory.class);
            updateEntity = redisCacheCategoryService.save(updateEntity);

            MRedisCacheCategory mRedisCacheCategory = convertToModel(updateEntity, MRedisCacheCategory.class);
            envelop.setObj(mRedisCacheCategory);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功更新缓存分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("更新缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("删除缓存分类")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.Delete, method = RequestMethod.DELETE)
    public Envelop delete(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            redisCacheCategoryService.delete(id);

            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功删除缓存分类。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证缓存分类名称是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueName, method = RequestMethod.GET)
    public Envelop isUniqueName(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "name", value = "缓存分类名称", required = true)
            @RequestParam(value = "name") String name) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheCategoryService.isUniqueName(id, name);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该缓存分类名称已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("验证缓存分类编码是否唯一")
    @RequestMapping(value = ServiceApi.Redis.CacheCategory.IsUniqueCode, method = RequestMethod.GET)
    public Envelop isUniqueCode(
            @ApiParam(name = "id", value = "缓存分类ID", required = true)
            @RequestParam(value = "id") Integer id,
            @ApiParam(name = "code", value = "缓存分类编码", required = true)
            @RequestParam(value = "code") String code) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            boolean result = redisCacheCategoryService.isUniqueCode(id, code);
            envelop.setSuccessFlg(result);
            if (!result) {
                envelop.setErrorMsg("该缓存分类编码已被使用，请重新填写！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("删除缓存分类发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
