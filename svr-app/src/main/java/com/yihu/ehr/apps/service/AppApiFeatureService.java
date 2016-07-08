package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApiFeature;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:33
 */
@Service
@Transactional
public class AppApiFeatureService extends BaseJpaService<AppApiFeature, XAppApiFeatureRepository> {
    @Autowired
    private XAppApiFeatureRepository xAppFeatureRepository;
    public AppApiFeatureService() {

    }

    public Page<AppApiFeature> getAppApiFeatureList(String sorts, int page, int size){
        XAppApiFeatureRepository repo = (XAppApiFeatureRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApiFeature createAppApiFeature(AppApiFeature appFeature) {
        xAppFeatureRepository.save(appFeature);
        return appFeature;
    }

    public AppApiFeature  updateAppApiFeature(AppApiFeature appApiFeature){
        xAppFeatureRepository.save(appApiFeature);
        return appApiFeature;
    }

    public void  deleteAppApiFeature(String id){
        xAppFeatureRepository.delete(id);
    }
}