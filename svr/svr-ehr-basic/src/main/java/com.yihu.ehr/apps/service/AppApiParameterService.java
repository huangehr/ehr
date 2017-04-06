package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.AppApiParameter;
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
 * @created 2016年7月7日21:04:41
 */
@Service
@Transactional
public class AppApiParameterService extends BaseJpaService<AppApiParameter, XAppApiParameterRepository> {

    @Autowired
    private XAppApiParameterRepository appApiParRepo;
    public AppApiParameterService() {

    }

    public Page<AppApiParameter> getAppApiParameterList(String sorts, int page, int size){
        XAppApiParameterRepository repo = (XAppApiParameterRepository)getJpaRepository();
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return repo.findAll(pageable);
    }

    public AppApiParameter createAppApiParameter(AppApiParameter appApiParameter) {
        appApiParRepo.save(appApiParameter);
        return appApiParameter;
    }

    public AppApiParameter  updateAppApiParameter(AppApiParameter appApiParameter){
        appApiParRepo.save(appApiParameter);
        return appApiParameter;
    }

    public void  deleteAppApiParameter(String id){
        appApiParRepo.delete(id);
    }
}