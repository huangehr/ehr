package com.yihu.ehr.standard.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.standard.dao.DispatchLogRepository;
import com.yihu.ehr.standard.model.DispatchLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
@Transactional
@Service
public class DispatchLogService extends BaseJpaService<DispatchLog, DispatchLogRepository> {

    public boolean delete(String versionCode, String orgCode){
        String sql = "delete from com.yihu.ehr.standard.dispatch.service.DispatchLog where orgId=:orgId and stdVersionId=:versionCode";
        return currentSession()
                    .createQuery(sql)
                    .setParameter("orgId", orgCode)
                    .setParameter("versionCode", versionCode)
                    .executeUpdate()>0;
    }

}
