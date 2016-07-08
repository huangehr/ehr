package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApi;
import com.yihu.ehr.apps.model.AppApiResponse;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:05:04
 */
@Service
@Transactional
public class AppApiService extends BaseJpaService<AppApi, XAppApiRepository> {
    @Autowired
    private XAppApiRepository xAppApiRepository;
    public AppApiService() {

    }

    public Page<AppApi> getAppApiList(String sorts, int page, int size){
        XAppApiRepository repo = (XAppApiRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApi createAppApi(AppApi appApi) {
        xAppApiRepository.save(appApi);
        return appApi;
    }

    public AppApi  updateAppApi(AppApi appApi){
        xAppApiRepository.save(appApi);
        return appApi;
    }

    public void  deleteAppApi(String id){
        xAppApiRepository.delete(id);
    }
}