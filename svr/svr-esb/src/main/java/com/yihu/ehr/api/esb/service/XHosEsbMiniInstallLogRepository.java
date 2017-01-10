package com.yihu.ehr.api.esb.service;

import com.yihu.ehr.api.esb.model.HosEsbMiniInstallLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author linaz
 * @created 2016.05.13 9:43
 */
public interface XHosEsbMiniInstallLogRepository extends JpaRepository<HosEsbMiniInstallLog, String> {

}