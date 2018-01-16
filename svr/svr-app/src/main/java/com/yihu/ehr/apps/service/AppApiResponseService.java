package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApiResponse;
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
 * @created 2016年7月7日21:04:54
 */
@Service
@Transactional
public class AppApiResponseService extends BaseJpaService<AppApiResponse, XAppApiResponseRepository> {
    @Autowired
    private XAppApiResponseRepository xAppApiResponseRepository;
    public AppApiResponseService() {

    }

    public Page<AppApiResponse> getAppApiResponseList(String sorts, int page, int size){
        XAppApiResponseRepository repo = (XAppApiResponseRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApiResponse createAppApiResponse(AppApiResponse appApiResponse) {
        xAppApiResponseRepository.save(appApiResponse);
        return appApiResponse;
    }

    public AppApiResponse  updateAppApiResponse(AppApiResponse appApiResponse){
        xAppApiResponseRepository.save(appApiResponse);
        return appApiResponse;
    }

    public void  deleteAppApiResponse(String id){
        xAppApiResponseRepository.delete(id);
    }

}