package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.specialdict.model.HealthProblemDict;
import com.yihu.ehr.specialdict.service.Icd10HpRelationCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 16:10
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Icd10HpRelationCache", description = "健康问题和ICD10缓存管理接口")
public class Icd10HpRelationCacheEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private Icd10HpRelationCacheService icd10HpRelationCacheService;

    /**
     * 缓存单个
     * @param icd10Id
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/one", method = RequestMethod.POST)
    @ApiOperation(value = "缓存单个")
    public void cacheOne(
            @ApiParam(name = "icd10_id", value = "icd10_id")
            @RequestParam(value = "icd10_id") long icd10Id) {
        icd10HpRelationCacheService.cacheOne(icd10Id);
    }

    /**
     * 缓存所有
     * @param force
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/all", method = RequestMethod.POST)
    @ApiOperation(value = "缓存所有")
    public void cacheAll(boolean force) {
        icd10HpRelationCacheService.cacheAll(force);
    }


    /**
     * 获取单个缓存
     * @param icd10Id
     * @return
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/one", method = RequestMethod.GET)
    @ApiOperation(value = "获取单个缓存")
    public HealthProblemDict healthProblemDict(
            @ApiParam(name = "icd10_id", value = "icd10_id")
            @RequestParam(value = "icd10_id") String icd10Id) {
        return icd10HpRelationCacheService.healthProblemDict(icd10Id);
    }

    @RequestMapping(value = "/hp_icd10_relation_cache/list", method = RequestMethod.POST)
    @ApiOperation(value = "获取缓存列表")
    public List<HealthProblemDict> healthProblemDict(
            @ApiParam(name = "icd10_id", value = "icd10_idList")
            @RequestParam(value = "icd10_idList",required = true) List<String> icd10IdList) {
        List<HealthProblemDict> list=new ArrayList<>();
        for(int i=0;i<icd10IdList.size();i++){
            list.add(icd10HpRelationCacheService.healthProblemDict(icd10IdList.get(i)));
        }
        return list;
    }

    /**
     * 获取所有缓存
     * @return
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/all", method = RequestMethod.GET)
    @ApiOperation(value = "获取所有缓存")
    public List<HealthProblemDict> healthProblemDictList(){
        //icd10HpRelationCacheService.cacheAll(true);
        return icd10HpRelationCacheService.healthProblemDictList();
    }

    /**
     * 清楚所有缓存
     */
    @RequestMapping(value = "/hp_icd10_relation_cache/all", method = RequestMethod.DELETE)
    @ApiOperation(value = "清除所有缓存")
    public void clean() {
        icd10HpRelationCacheService.clean();
    }

}
