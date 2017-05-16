package com.yihu.ehr.report.controller;

import com.yihu.ehr.entity.geography.Geography;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.report.service.QcDailyReportDetailService;
import com.yihu.ehr.report.service.QcDailyReportService;
import com.yihu.ehr.report.service.QcQuotaResultService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by janseny on 2017/5/16.
 */
@RestController
public class QuotaStatisticsEndPoint {

    @Autowired
    QcDailyReportService qcDailyReportService;
    @Autowired
    QcDailyReportDetailService qcDailyReportDetailService;
    @Autowired
    QcQuotaResultService qcQuotaResultService;
    @Autowired
    private OrgService orgService;
    @Autowired
    private GeographyService geographyService;

    /**
     *
     * @param quotaId  指标ID  1 档案完整性 2 数据集完整性 3 数据元完整性 4 数据元准确性 5 档案及时性 6 门诊及时性 7 住院及时性
     * @param orgCode  机构编码
     * @param quotaDate 指标统计时间  今天统计昨天 2017-05-16
     */
    public void statisticQuotaData( String quotaId,String orgCode ,Date quotaDate ){
        DecimalFormat df = new DecimalFormat("0");
        List<Map<Integer,Object>> listVal = null;
        Map<Integer,Object> mapVal = null;
        switch (quotaId){
            case "1":
                statisQuotaBy1( df,listVal,mapVal, orgCode , quotaDate);
                break;
            case "2":
//                statisQuotaBy2(df, listVal, mapVal, orgCode, quotaDate);
                break;
            case "3":

                break;
            case "4":

                break;
            case "5":

                break;
            case "6":

                break;
            case "7":

                break;
        }
    }

    //1 档案完整性 统计
    public void statisQuotaBy1(DecimalFormat df,List<Map<Integer,Object>> listVal,Map<Integer,Object> mapVal,String orgCode ,Date quotaDate){
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        int todayTolNum = 0;
        int todayRelNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        Object object = qcDailyReportService.getOrgData(orgCode,quotaDate);
        if(object == null)
            return;
        mapVal = converMapObject(object);
        if(mapVal.get(0) != null){
            Organization org = orgService.getOrg(mapVal.get(0).toString());
            if(org != null && StringUtils.isNotEmpty(org.getLocation())){
                Geography geography = geographyService.getAddressById(org.getLocation());
                qcQuotaResult.setOrgName(org.getShortName());
                qcQuotaResult.setCity(geography.getCity());
                qcQuotaResult.setCityName(geography.getCity());
                qcQuotaResult.setTown(geography.getTown());
                qcQuotaResult.setTownName(geography.getTown());
            }
        }
        qcQuotaResult.setQuotaId(Long.valueOf("1"));
        qcQuotaResult.setOrgCode(mapVal.get(1).toString());
        qcQuotaResult.setReceiveTime(DateUtil.formatCharDateYMDHMS(mapVal.get(1).toString()));
        todayTolNum = Integer.valueOf(mapVal.get(2).toString()) + Integer.valueOf(mapVal.get(4).toString());
        todayRelNum = Integer.valueOf(mapVal.get(3).toString()) + Integer.valueOf(mapVal.get(5).toString());
        qcQuotaResult.setTotalNum(todayTolNum);
        qcQuotaResult.setRealNum(todayRelNum);
        List<Object> objects = qcDailyReportService.getYesterdayAndLastYearTodayData(orgCode, quotaDate);
       if(objects!= null){
           listVal = converListObject(objects);
           for(Map<Integer,Object> map :listVal){
               Date evenTime = DateUtil.formatCharDateYMD(map.get(0).toString());
               quotaDate = DateUtil.formatCharDateYMD(DateUtil.formatDate(quotaDate,"yyyy-MM-dd"));
               int days = DateUtil.getDifferenceOfDays(evenTime,quotaDate);
               if(days <= 1){
                   yesterdayRelNum = Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
               }else if(days == 365 || days == 366){
                   lasetYearTodayRelNum = Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
               }
           }
       }
        String an = yesterdayRelNum==0?"0":df.format((float)todayRelNum/(float)yesterdayRelNum*100)+"%";
        String mom = lasetYearTodayRelNum==0?"0":df.format((float)todayRelNum/(float)lasetYearTodayRelNum*100)+"%";
        String val = todayRelNum==0?"0":df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
        qcQuotaResult.setAn(an);
        qcQuotaResult.setMom(mom);
        qcQuotaResult.setQuotaName("档案完整性");
        qcQuotaResult.setValue(val);
        qcQuotaResult.setErrorNum(0);
        qcQuotaResult.setTimelyNum(0);
        qcQuotaResult.setQuotaDate(new Date());
        qcQuotaResultService.save(qcQuotaResult);
    }




    public List<Map<Integer,Object>> converListObject(List<Object> objects){
        List<Map<Integer,Object>> list = new ArrayList<>();
        if(objects != null && objects.size() > 0) {
            for (int i = 0; i < objects.size(); i++) {
                Map<Integer,Object> map = new HashMap<>();
                Object[] obj = (Object[]) objects.get(i);
                for (int j = 0; j < obj.length; j++) {
                    map.put(j, obj[j].toString());
                }
                list.add(map);
            }
        }
        return list;
    }

    public Map<Integer,Object> converMapObject(Object object){
        Map<Integer,Object> map = new HashMap<>();
        Object[] obj = (Object[]) object;
        for (int i = 0; i < obj.length; i++) {
            map.put(i, obj[i].toString());
        }
        return map;
    }

    public Date parseYesterday(Date now){
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
        Date yesterday = calendar.getTime();
        return  yesterday;
    }

    public Date parseLastYearToday(Date now){
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(now);//把当前时间赋给日历
        calendar.add(Calendar.YEAR, -1);  //设置为前一年
        Date lastyearToday = calendar.getTime();
        return  lastyearToday;
    }

    public static void main(String args[]){
        QuotaStatisticsEndPoint quotaStatisticsEndPoint = new QuotaStatisticsEndPoint();
//        quotaStatisticsEndPoint.statisticQuotaData("1","org-1", DateUtil.strToDate("2017-05-09"));
        quotaStatisticsEndPoint.parseYesterday(DateUtil.strToDate("2017-05-09"));
    }


}
