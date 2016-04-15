package com.yihu.ehr.service;

import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:10:56
 */

@Service
@Transactional
public class FamiliesService extends BaseJpaService<Families, XFamiliesRepository> {

    @Autowired
    private XFamiliesRepository familiesRepository;

}