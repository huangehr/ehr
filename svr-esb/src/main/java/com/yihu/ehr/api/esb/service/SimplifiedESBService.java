package com.yihu.ehr.api.esb.service;


import com.yihu.ehr.api.esb.dao.*;
import com.yihu.ehr.api.esb.model.HosAcqTask;
import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.api.esb.model.HosLog;
import com.yihu.ehr.api.esb.model.HosSqlTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by chenweida on 2016/3/1.
 */
@Service("simplifiedESBService")
public class SimplifiedESBService {
    @Autowired
    private IHosAcqTackDao hosAcqTackDao;
    @Autowired
    private IHosEsbMiniInstallLogDao hosEsbMiniInstallLogDao;
    @Autowired
    private IHosEsbMiniReleaseDao hosEsbMiniReleaseDao;
    @Autowired
    private IHosLogDao hosLogDao;
    @Autowired
    private IHosSqlTaskDao hosSqlTaskDao;


    public boolean getUpdateFlag(String versionCode, String systemCode, String orgCode) throws Exception {
        HosEsbMiniRelease hemr = hosEsbMiniReleaseDao.findBySystemCode(systemCode).get(0);
        //判断版本是否需要更新
        if (Integer.valueOf(versionCode) < hemr.getVersionCode()) {
            //判断该机构是否包含在更新的里面
            if (hemr.getOrgCode().contains(orgCode)) {
                return true;
            }
        }
        return false;
    }

    public HosEsbMiniRelease getSimplifiedESBBySystemCodes(String systemCode, String orgCode) throws Exception {
        List<HosEsbMiniRelease> hoss = hosEsbMiniReleaseDao.findBySystemCode(systemCode);
        if (hoss != null && hoss.size() > 0) {
            if (hoss.get(0).getOrgCode().contains(orgCode)) {
                return hoss.get(0);
            } else {
                throw new Exception("orgCode error");
            }
        } else {
            throw new Exception("systemCode error");
        }
    }

    @Transactional
    public void changeFillMiningStatus(String id, String message, String result) {
        HosAcqTask r = hosAcqTackDao.getOne(id);
        r.setMessage(message);
        r.setStatus(result);
    }

    public List<HosAcqTask> fillMining(String systemCode, String orgCode) {
        return hosAcqTackDao.findBySystemCodeAndOrgCode(systemCode, orgCode);
    }

    public HosSqlTask hisPenetration(String systemCode, String orgCode) {
        List<HosSqlTask> hosSqlTasks = hosSqlTaskDao.findBySystemCodeAndOrgCode(systemCode, orgCode);
        if (hosSqlTasks != null && hosSqlTasks.size() > 0) {
            return hosSqlTasks.get(0);
        }
        return null;
    }

    @Transactional
    public void changeHisPenetrationStatus(String id, String message, String result) {
        HosSqlTask hqt = hosSqlTaskDao.getOne(id);
        hqt.setMessage(message);
        hqt.setResult(result);
        hqt.setStatus("1");
    }

    @Transactional
    public void saveHosLog(HosLog lh) {
        hosLogDao.save(lh);
    }
}
