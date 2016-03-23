package com.yihu.ehr.adapter.service;

import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.model.adaption.MAdapterPlan;
import org.springframework.stereotype.Service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/19
 */
@Service
public class AdapterDataSetService extends ExtendService<MAdapterDataSet> {

    public AdapterDataSetService() {
        init(
                "/adapter/plan/data_set/{plan_id}",        //searchUrl
                "/adapter//metadata/{id}",   //modelUrl
                "/adapter/metadata",        //addUrl
                "/adapter/metadata",        //modifyUrl
                "/adapter/meta_data"        //deleteUrl
        );
    }

}
