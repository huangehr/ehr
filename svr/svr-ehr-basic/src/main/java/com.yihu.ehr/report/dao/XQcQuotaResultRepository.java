package com.yihu.ehr.report.dao;

import com.yihu.ehr.entity.report.QcQuotaResult;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/5/08
 */
public interface XQcQuotaResultRepository extends PagingAndSortingRepository<QcQuotaResult, Integer> {

    //按机构查询统计结果集
    @Query("select qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName,sum(qc.totalNum),sum(qc.realNum),sum(qc.errorNum),sum(qc.timelyNum) from QcQuotaResult qc where qc.orgCode = :orgCode and qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName ")
    List findListByOrg(@Param("orgCode")  String orgCode,@Param("startTime") Date startTime,@Param("endTime")  Date endTime);

    //按区域查询统计结果集 - 按机构区分
    @Query("select qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName,sum(qc.totalNum) as totalNum,sum(qc.realNum) as realNum,sum(qc.errorNum) as errorNum,sum(qc.timelyNum) as timelyNum  from QcQuotaResult qc where (qc.city = :location or qc.town = :location ) and qc.quotaId = :quotaId and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName")
    List<Object> findListByLocationByOrg(@Param("location")  String location, @Param("quotaId")  long quotaId, @Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

    //按区域查询统计结果集
    @Query("select qc.quotaId,qc.quotaName,sum(qc.totalNum),sum(qc.realNum),sum(qc.errorNum),sum(qc.timelyNum) from QcQuotaResult qc where (qc.city = :location or qc.town = :location ) and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.quotaId,qc.quotaName")
    List findListByLocation(@Param("location")  String location,@Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

}
