package com.yihu.quota.service.dict;

import com.yihu.quota.dao.jpa.dict.SystemDictDao;
import com.yihu.quota.dao.jpa.dict.SystemDictListDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by chenweida on 2017/6/1.
 */
@Service
public class DictService {
    @Autowired
    private SystemDictDao systemDictDao;
    @Autowired
    private SystemDictListDao systemDictListDao;
}
