package com.yihu.ehr.api.esb.dao;

import com.yihu.ehr.api.esb.model.HosEsbMiniInstallLog;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by chenweida on 2016/3/2.
 */
public interface IHosEsbMiniInstallLogDao extends JpaRepository<HosEsbMiniInstallLog, String> {
}
