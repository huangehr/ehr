package com.yihu.ehr.api.esb.service;

import com.yihu.ehr.api.esb.model.HosLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author linaz
 * @created 2016.05.12 17:58
 */
public interface XHosLogRepository extends JpaRepository<HosLog, String> {

}