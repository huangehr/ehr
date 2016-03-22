package com.yihu.ehr.adapter.service;

import com.yihu.ehr.model.adaption.MAdapterDataSet;
import com.yihu.ehr.model.adaption.MAdapterDict;
import org.springframework.stereotype.Service;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/19
 */
@Service
public class AdapterDictService extends ExtendService<MAdapterDict> {

    public AdapterDictService() {
        init(
                "",        //searchUrl
                "/adapter/dict/entry/{id}",   //modelUrl
                "/adapter/dict/entry",        //addUrl
                "/adapter/dict/entry",        //modifyUrl
                "/adapter/dict/entrys"        //deleteUrl
        );
    }

}
