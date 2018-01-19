package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.AppApiRepository;
import com.yihu.ehr.basic.apps.model.AppApi;
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
 * @created 2016年7月7日21:05:04
 */
@Service
@Transactional
public class AppApiService extends BaseJpaService<AppApi, AppApiRepository> {
    @Autowired
    private AppApiRepository xAppApiRepository;
    public AppApiService() {

    }

    public Page<AppApi> getAppApiList(String sorts, int page, int size){
        AppApiRepository repo = (AppApiRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApi createAppApi(AppApi appApi) {
        if ("".equals(appApi.getMethodName())){
            appApi.setMethodName(null);
        }
        xAppApiRepository.save(appApi);
        return appApi;
    }

    public AppApi updateAppApi(AppApi appApi){
        xAppApiRepository.save(appApi);
        return appApi;
    }

    public void  deleteAppApi(String id){
        xAppApiRepository.delete(id);
    }
}