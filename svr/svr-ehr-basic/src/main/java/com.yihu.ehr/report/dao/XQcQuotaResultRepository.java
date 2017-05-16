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
    //分析明细-2
    @Query("select qc.eventTime,qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName,qc.value,qc.totalNum,qc.realNum,qc.errorNum,qc.timelyNum,qc.an,qc.mom from QcQuotaResultDetail qc where (qc.city = :location or qc.town = :location ) and  qc.eventTime >= :startTime and qc.eventTime< :endTime order by qc.eventTime,qc.quotaId,qc.quotaName")
    List findQcDetailListByLocationAndTime(@Param("location")  String location,@Param("startTime")  Date startTime,@Param("endTime")  Date endTime);
    //分析明细-1
    @Query("select qc.cityName,qc.townName,qc.eventTime from QcQuotaResultAnalyse qc where (qc.city = :location or qc.town = :location ) and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.eventTime,qc.cityName,qc.townName")
    List findQcListByLocationAndTime(@Param("location")  String location,@Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

    //按区域查询统计结果集(完整性，及时性，准确性)
    @Query("select qc.quotaId,qc.quotaName,qc.cityName,qc.townName,sum(qc.totalNum),sum(qc.realNum),sum(qc.errorNum),sum(qc.timelyNum) from QcQuotaResult qc where (qc.city = :location or qc.town = :location ) and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.quotaId,qc.quotaName,qc.cityName,qc.townName")
    List findListByLocationAndTime(@Param("location")  String location,@Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

    //趋势分析 按机构查询统计结果集 - 按机构区分
    @Query("select qc.quotaId,qc.quotaName,qc.eventTime,sum(qc.totalNum) as totalNum,sum(qc.realNum) as realNum,sum(qc.errorNum) as errorNum,sum(qc.timelyNum) as timelyNum  from QcQuotaResult qc where qc.orgCode = :orgCode and qc.quotaId = :quotaId and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.eventTime,qc.quotaId,qc.quotaName")
    List<Object> findListByOrg(@Param("orgCode")  String orgCode, @Param("quotaId")  long quotaId, @Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

    //趋势分析 按区域查询统计结果集 - 按机构区分
    @Query("select qc.quotaId,qc.quotaName,qc.eventTime,sum(qc.totalNum) as totalNum,sum(qc.realNum) as realNum,sum(qc.errorNum) as errorNum,sum(qc.timelyNum) as timelyNum  from QcQuotaResult qc where (qc.city = :location or qc.town = :location ) and qc.quotaId = :quotaId and  qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.eventTime,qc.quotaId,qc.quotaName")
    List<Object> findListByLocation(@Param("location")  String location, @Param("quotaId")  long quotaId, @Param("startTime")  Date startTime,@Param("endTime")  Date endTime);

}
