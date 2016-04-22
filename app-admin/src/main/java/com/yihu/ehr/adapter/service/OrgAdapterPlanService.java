package com.yihu.ehr.adapter.service;

import com.yihu.ehr.model.adaption.MAdapterPlan;
import org.springframework.stereotype.Service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/19
 */
@Service
public class OrgAdapterPlanService extends ExtendService<MAdapterPlan> {

    public OrgAdapterPlanService() {
        init(
                "/adapter/plans",        //searchUrl
                "/adapter/plan/{id}",   //modelUrl
                "/adapter/plan",        //addUrl
                "/adapter/plan",        //modifyUrl
                "/adapter/plans"        //deleteUrl
        );
    }

}
