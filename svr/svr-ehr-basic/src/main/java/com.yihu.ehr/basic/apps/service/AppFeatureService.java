package com.yihu.ehr.basic.apps.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.apps.dao.AppApiFeatureRepository;
import com.yihu.ehr.basic.apps.model.AppFeature;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:33
 */
@Service
@Transactional
public class AppFeatureService extends BaseJpaService<AppFeature, AppApiFeatureRepository> {

    @Autowired
    private AppApiFeatureRepository xAppFeatureRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    ObjectMapper objectMapper;

    public Page<AppFeature> getAppFeatureList(String sorts, int page, int size){
        AppApiFeatureRepository repo = (AppApiFeatureRepository)getJpaRepository();
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
        contentMap.put("code", appFeature.getCode());
        contentMap.put("level", appFeature.getLevel());
        contentMap.put("text", appFeature.getName());
        contentMap.put("type", appFeature.getType());
        contentMap.put("iconUrl", appFeature.getIconUrl());
        if (appFeature.getLevel() != 1) {
            contentMap.put("pid", appFeature.getParentId());
        }
        if (appFeature.getUrl().startsWith("/")) {
            contentMap.put("url", appFeature.getPrefixUrl() + appFeature.getUrl());
        }
        appFeature.setContent(objectMapper.writeValueAsString(contentMap));
        return appFeature;
    }

    /**
     * 根据权限，获取应用菜单
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 菜单JSON字符串集合
     */
    public List<Map<String, Object>> findAppMenus(String appId, String userId, int parentId) throws IOException {
        List<Map<String, Object>> menuList = new ArrayList<>();

        String sql = " SELECT DISTINCT af.id AS id, af.content AS content FROM apps_feature af " +
                " JOIN role_user ru ON ru.user_id = '" + userId + "' " +
                " JOIN role_feature_relation rfr ON rfr.feature_id = af.id AND rfr.role_id = ru.role_id " +
                " WHERE af.app_id = '" + appId + "' AND af.parent_id = " + parentId + " AND af.type <> 3 " +
                " ORDER BY af.sort ";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> item : list) {
            Map<String, Object> conMap = objectMapper.readValue(item.get("content").toString(), Map.class);
            menuList.add(conMap);
            menuList.addAll(findAppMenus(appId, userId, (int) item.get("id")));
        }

        return menuList;
    }

}