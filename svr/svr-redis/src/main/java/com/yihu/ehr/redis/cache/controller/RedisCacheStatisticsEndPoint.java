package com.yihu.ehr.redis.cache.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.redis.cache.CacheCommonBiz;
import com.yihu.ehr.redis.cache.entity.RedisCacheCategory;
import com.yihu.ehr.redis.cache.service.RedisCacheCategoryService;
import com.yihu.ehr.redis.cache.service.RedisCacheKeyMemoryService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.DecimalFormat;
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
    private RedisCacheKeyMemoryService redisCacheKeyMemoryService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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
            for (RedisCacheCategory category : categoryList) {
                categoryNameList.add(category.getName());
                String keysPattern = CacheCommonBiz.makeKeyPrefix(category.getCode()) + "*";
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

    @ApiOperation("统计缓存分类的缓存内存比率（近似值，比实际略小）")
    @RequestMapping(value = ServiceApi.Redis.CacheStatistics.GetCategoryMemory, method = RequestMethod.GET)
    public Envelop getCategoryMemory() {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> categoryMemoryRateList = new ArrayList<>();

            // Redis使用的总内存
            double redisUsedMemory = (double) redisTemplate.execute(new RedisCallback() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    return Double.parseDouble(connection.info("memory").get("used_memory").toString());
                }
            });

            DecimalFormat df = new DecimalFormat("#.####%");
            List<RedisCacheCategory> categoryList = redisCacheCategoryService.search("");
            double categoryMemoryTotal = 0;
            for (RedisCacheCategory category : categoryList) {
                String keyPrefix = CacheCommonBiz.makeKeyPrefix(category.getCode());
                Long categoryMemoryObj = redisCacheKeyMemoryService.sumCategoryMemory(keyPrefix);
                double categoryMemory = categoryMemoryObj == null ? 0 : (double) categoryMemoryObj;
                String categoryMemoryRate = df.format(categoryMemory / redisUsedMemory);
                Map<String, Object> rateMap = new HashMap<>();
                rateMap.put("value", categoryMemoryRate);
                rateMap.put("name", category.getName());
                categoryMemoryRateList.add(rateMap);
                categoryMemoryTotal += categoryMemory;
            }
            Map<String, Object> rateMap = new HashMap<>();
            String otherRate = df.format((redisUsedMemory - categoryMemoryTotal) / redisUsedMemory);
            rateMap.put("value", otherRate);
            rateMap.put("name", "未分类");
//            Map<String, Object> idleRateMap = new HashMap<>();
//            idleRateMap.put("name", "闲置内存");
//            idleRateMap.put("value", );
            categoryMemoryRateList.add(rateMap);

            result.put("categoryMemoryRateList", categoryMemoryRateList);

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
