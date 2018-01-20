package com.yihu.ehr.redis.cache.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.cache.dao.RedisCacheKeyRuleDao;
import com.yihu.ehr.redis.cache.entity.RedisCacheKeyRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 缓存Key规则 Service
 *
 * @author 张进军
 * @date 2017/11/23 11:28
 */
@Service
@Transactional
public class RedisCacheKeyRuleService extends BaseJpaService<RedisCacheKeyRule, RedisCacheKeyRuleDao> {

    @Autowired
    RedisCacheKeyRuleDao redisCacheKeyRuleDao;

    public RedisCacheKeyRule getById(Integer id) {
        return redisCacheKeyRuleDao.findOne(id);
    }

    public RedisCacheKeyRule findByCode(String code) {
        return redisCacheKeyRuleDao.findByCode(code);
    }

    @Transactional(readOnly = false)
    public RedisCacheKeyRule save(RedisCacheKeyRule redisCacheKeyRule) {
        return redisCacheKeyRuleDao.save(redisCacheKeyRule);
    }

    @Transactional(readOnly = false)
    public void delete(Integer id) {
        redisCacheKeyRuleDao.delete(id);
    }

    public Boolean isUniqueName(Integer id, String name) {
        RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleDao.isUniqueName(id, name);
        if (redisCacheKeyRule == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueCode(Integer id, String code) {
        RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleDao.isUniqueCode(id, code);
        if (redisCacheKeyRule == null) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean isUniqueExpression(Integer id, String categoryCode, String expression) {
        // todo 存在问题：模糊匹配不准确
//        String similarExpression = CacheCommonBiz.replaceParams(expression, "%");
        RedisCacheKeyRule redisCacheKeyRule = redisCacheKeyRuleDao.isUniqueExpression(id, categoryCode, expression);
        if (redisCacheKeyRule == null) {
            return true;
        } else {
            return false;
        }
    }

}