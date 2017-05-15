package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.report.dao.XQcQuotaResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
@Service
@Transactional
public class QcQuotaResultService extends BaseJpaService<QcQuotaResult, XQcQuotaResultRepository> {

    @Autowired
    XQcQuotaResultRepository qcQuotaResultRepository;

    /**
     * 按机构查询统计结果集
     * @param orgCode
     * @param startTime
     * @param endTime
     * @return
     */
    public List getQuotaListByOrgCode(String orgCode,Date startTime, Date endTime) {
        List quotaList = qcQuotaResultRepository.findListByOrg(orgCode, startTime, endTime);
        if(quotaList.size() > 0)
        {
            return quotaList;
        }else{
            return null;
        }
    }

     /**
     * 按区域查询统计结果集 - 按机构区分
     * @param location
     * @param quotaId
     * @param startTime
     * @param endTime
     * @return
     */
    public List getQuotaListByLocationGBOrg(String location,long quotaId,Date startTime, Date endTime) {
        List quotaList = qcQuotaResultRepository.findListByLocationByOrg(location,quotaId, startTime, endTime);
        if(quotaList.size() > 0)
        {
            return quotaList;
        }else{
            return null;
        }
    }

    /**
     * 按区域查询统计结果集
     * @param location
     * @param startTime
     * @param endTime
     * @return
     */
    public List getQuotaListByLocation(String location,Date startTime, Date endTime) {
        List quotaList = qcQuotaResultRepository.findListByLocation(location, startTime, endTime);
        if(quotaList.size() > 0)
        {
            return quotaList;
        }else{
            return null;
        }
    }



}
