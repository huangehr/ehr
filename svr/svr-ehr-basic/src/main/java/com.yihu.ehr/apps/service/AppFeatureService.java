package com.yihu.ehr.apps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.apps.model.AppFeature;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:33
 */
@Service
@Transactional
public class AppFeatureService extends BaseJpaService<AppFeature, XAppApiFeatureRepository> {
    @Autowired
    private XAppApiFeatureRepository xAppFeatureRepository;
    public AppFeatureService() {

    }

    public Page<AppFeature> getAppFeatureList(String sorts, int page, int size){
        XAppApiFeatureRepository repo = (XAppApiFeatureRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppFeature createAppFeature(AppFeature appFeature) {
        xAppFeatureRepository.save(appFeature);
        return appFeature;
    }

    public AppFeature updateAppFeature(AppFeature appFeature){
        xAppFeatureRepository.save(appFeature);
        return appFeature;
    }

    public void  deleteAppFeature(String id){
        xAppFeatureRepository.delete(id);
    }

    /**
     * 生成菜单JSON对象字符串
     * @return AppFeature
     */
    public AppFeature joinMenuItemJsonStr(AppFeature appFeature) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("id", appFeature.getId());
        contentMap.put("level", appFeature.getLevel());
        contentMap.put("text", appFeature.getName());
        if (!"1".equals(appFeature.getLevel())) {
            contentMap.put("pid", appFeature.getParentId());
        }
        if (appFeature.getUrl().startsWith("/")) {
            contentMap.put("url", "${contextRoot}" + appFeature.getUrl());
        }
        appFeature.setContent(objectMapper.writeValueAsString(contentMap));
        return appFeature;
    }

}