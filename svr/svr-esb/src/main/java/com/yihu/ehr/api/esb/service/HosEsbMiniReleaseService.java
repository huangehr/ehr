package com.yihu.ehr.api.esb.service;

import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author linaz
 * @created 2016.05.12 17:56
 */
@Service
@Transactional
public class HosEsbMiniReleaseService extends BaseJpaService<HosEsbMiniRelease, XHosEsbMiniReleaseRepository> {

}