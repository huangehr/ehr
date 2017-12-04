package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import com.yihu.ehr.redis.cache.service.RedisCacheCategoryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 缓存统计
 *
 * @author 张进军
 * @date 2017/11/30 17:07
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "缓存统计接口", tags = {"缓存服务管理--缓存统计接口"})
public class RedisCacheStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RedisCacheCategoryService redisCacheCategoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("统计缓存分类的缓存数量")
    @RequestMapping(value = ServiceApi.Redis.CacheStatistics.GetCategoryKeys, method = RequestMethod.GET)
    public Envelop getCategoryKeys() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            List<String> categoryNameList = new ArrayList<>();
            List<Integer> categoryNumList = new ArrayList<>();
            List<RedisCacheCategory> categoryList = redisCacheCategoryService.search("");
            for(RedisCacheCategory category : categoryList) {
                categoryNameList.add(category.getName());
                String keysPattern = "[" + category.getCode() + "]*";
                categoryNumList.add(redisTemplate.keys(keysPattern).size());
            }
            result.put("categoryNameList", categoryNameList);
            result.put("categoryNumList", categoryNumList);

            envelop.setObj(result);
            envelop.setSuccessFlg(true);
            envelop.setErrorMsg("成功获取数据。");
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg("发生异常：" + e.getMessage());
        }
        return envelop;
    }

}
