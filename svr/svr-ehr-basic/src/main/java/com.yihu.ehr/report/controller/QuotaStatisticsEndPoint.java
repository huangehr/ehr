package com.yihu.ehr.report.controller;

import com.yihu.ehr.entity.geography.Geography;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.report.service.*;
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
    @Autowired
    QcDailyReportDatasetsService qcDailyReportDatasetsService;
    @Autowired
    QcDailyReportDatasetService qcDailyReportDatasetService;
    @Autowired
    QcDailyReportMetadataService qcDailyReportMetaDataService;

    /**
     *
     * @param quotaId  指标ID  1 档案完整性 2 数据集完整性 3 数据元完整性 4 数据元准确性 5 档案及时性 6 门诊及时性 7 住院及时性
     * @param orgCode  机构编码
     * @param quotaDate 指标统计时间  今天统计昨天 2017-05-16
     */
    public void statisticQuotaData( String quotaId,String orgCode ,Date quotaDate ){
        DecimalFormat df = new DecimalFormat("0");
        switch (quotaId){
            case "1":
                statisQuotaBy1(quotaId,"档案完整性", orgCode , quotaDate);
                break;
            case "2":
                statisDatasetsQuotaBy2(quotaId,"数据集完整性", orgCode, quotaDate);
                break;
            case "3":
                statisMetaddataQuotaBy3(quotaId,"数据元完整性", orgCode, quotaDate);
                break;
            case "4":
//                statisMetaddataQuotaBy4(quotaId,"数据元准确性", orgCode, quotaDate);
                break;
            case "5":
                statisTimelyQuotaByQuotaId(quotaId,"档案及时性",null, orgCode, quotaDate);
                break;
            case "6":
                statisTimelyQuotaByQuotaId(quotaId,"门诊及时性","outpatient", orgCode, quotaDate);
                break;
            case "7":
                statisTimelyQuotaByQuotaId(quotaId, "住院及时性", "hospital", orgCode, quotaDate);
                break;
        }
    }

    //1 档案完整性 统计
    public void statisQuotaBy1(String quotaId ,String quotaName,String orgCode ,Date quotaDate){
        DecimalFormat df = new DecimalFormat("0");
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        qcQuotaResult = setBaseInfo(qcQuotaResult,quotaId,quotaName,quotaDate);
        qcQuotaResult = setOrgInfo(orgCode,qcQuotaResult);
        int todayTolNum = 0;
        int todayRelNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        int errorNum = 0;
        int timelyNum = 0;
        Object object = qcDailyReportService.getOrgData(orgCode,quotaDate);
        if(object != null){
            Map<Integer,Object> mapVal  = converMapObject(object);
            todayTolNum = Integer.valueOf(mapVal.get(2).toString()) + Integer.valueOf(mapVal.get(4).toString());
            todayRelNum = Integer.valueOf(mapVal.get(3).toString()) + Integer.valueOf(mapVal.get(5).toString());
            List<Object> objects = qcDailyReportService.getYesterdayAndLastYearTodayData(orgCode, quotaDate);
            yesterdayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,1);
            lasetYearTodayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,2);

            String val = todayTolNum==0?"0%":df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum==0?"0%":df.format((float)todayRelNum/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum==0?"0%":df.format((float)todayRelNum/(float)lasetYearTodayRelNum*100)+"%";

            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayRelNum,timelyNum);
            qcQuotaResultService.save(qcQuotaResult);
        }
    }


    //2 数据集完整性  统计
    public void statisDatasetsQuotaBy2(String quotaId ,String quotaName,String orgCode ,Date quotaDate){
        DecimalFormat df = new DecimalFormat("0");
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        qcQuotaResult = setBaseInfo(qcQuotaResult,quotaId,quotaName,quotaDate);
        qcQuotaResult = setOrgInfo(orgCode,qcQuotaResult);
        int todayTolNum = 0;
        int todayRelNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        int errorNum = 0;
        int timelyNum = 0;
        Object object = qcDailyReportDatasetsService.getOrgDatasetsData(orgCode, quotaDate);
        if(object != null){
            Map<Integer,Object> mapVal  = converMapObject(object);
            todayTolNum = Integer.valueOf(mapVal.get(2).toString());
            todayRelNum = Integer.valueOf(mapVal.get(3).toString());
            List<Object> objects = qcDailyReportDatasetsService.getDatasetsYesdayLastYearData(orgCode, quotaDate);
            yesterdayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,1);
            lasetYearTodayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,2);

            String val = todayTolNum == 0 ? "0%" : df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum == 0 ? "0%" : df.format((float)todayRelNum/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum == 0 ? "0%" :df.format((float)todayRelNum/(float)lasetYearTodayRelNum*100)+"%";

            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayRelNum,timelyNum);
            qcQuotaResultService.save(qcQuotaResult);
        }
    }

    // 3 数据元完整性 统计
    public void statisMetaddataQuotaBy3(String quotaId ,String quotaName,String orgCode ,Date quotaDate){
        DecimalFormat df = new DecimalFormat("0");
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        qcQuotaResult = setBaseInfo(qcQuotaResult,quotaId,quotaName,quotaDate);
        qcQuotaResult = setOrgInfo(orgCode,qcQuotaResult);
        int todayErrorNum = 0;
        int todayRelNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        List<Object> objectList  = qcDailyReportMetaDataService.getOrgMeataData(orgCode, quotaDate);
        String errrCodeStr = "";
        if(objectList != null){
            for(Object object : objectList){
                List<Map<Integer,Object>> list  = converListObject(objectList);
                for(Map<Integer,Object> map : list){
                    todayRelNum = todayRelNum + Integer.valueOf(map.get(2).toString());
                    todayErrorNum = todayErrorNum + Integer.valueOf(map.get(3).toString());
                    errrCodeStr = errrCodeStr + map.get(4).toString();
                }
            }
        }else {
            return;
        }


        List<Object> objects = qcDailyReportMetaDataService.getDataMeataYesdayLastYearData(orgCode, quotaDate);
        yesterdayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,1);
        lasetYearTodayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,2);
        String val = todayErrorNum == 0 ? "0%" : df.format((float)todayRelNum/(float)todayErrorNum*100)+"%";
        String an = yesterdayRelNum == 0 ? "0%" : df.format((float)todayRelNum/(float)yesterdayRelNum*100)+"%";
        String mom = lasetYearTodayRelNum == 0 ? "0%" :df.format((float)todayRelNum/(float)lasetYearTodayRelNum*100)+"%";
        int errorNum = 0;
        int timelyNum = 0;
        timelyNum = todayRelNum;
//        qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,todayErrorNum,todayTolNum,todayRelNum,timelyNum);
        qcQuotaResult.setTotalNum(todayRelNum);
        qcQuotaResult.setErrorNum(todayErrorNum);
        qcQuotaResultService.save(qcQuotaResult);
    }


    //5 6 7  及时性 统计
    public void statisTimelyQuotaByQuotaId(String quotaId ,String quotaName,String archiveType ,String orgCode ,Date quotaDate){
        DecimalFormat df = new DecimalFormat("0");
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        qcQuotaResult = setBaseInfo(qcQuotaResult,quotaId,quotaName,quotaDate);
        qcQuotaResult = setOrgInfo(orgCode,qcQuotaResult);
        int todayTolNum = 0;
        int todayRelNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        Object object = qcDailyReportService.getOrgData(orgCode, quotaDate);
        if(object != null) {
            Map<Integer,Object> mapVal  = converMapObject(object);
            todayTolNum = qcDailyReportService.getOrgDailyReportDetailCount(mapVal.get(6).toString(), null,archiveType);
            todayRelNum = qcDailyReportService.getOrgDailyReportDetailCount(mapVal.get(6).toString(), "1",archiveType);
        }
        List<Object> objects = qcDailyReportService.getYesterdayAndLastYearTodayData(orgCode, quotaDate);
        if(objects != null && objects.size() > 0) {
            List<Map<Integer,Object>> mapList = converListObject(objects);
            for(Map<Integer,Object> map : mapList){
                if(map.get(0) !=null ){
                    Date createTime = DateUtil.formatCharDateYMD(map.get(0).toString());
                    quotaDate = DateUtil.formatCharDateYMD(DateUtil.formatDate(quotaDate,"yyyy-MM-dd"));
                    int days = DateUtil.getDifferenceOfDays(createTime,quotaDate);
                    if(days == 1){
                        yesterdayRelNum = qcDailyReportService.getOrgDailyReportDetailCount(map.get(3).toString(), "1",archiveType);
                    }else if(days == 365 || days == 366){
                        lasetYearTodayRelNum = qcDailyReportService.getOrgDailyReportDetailCount(map.get(3).toString(), "1",archiveType);
                    }
                }
            }
        }

        String val = todayTolNum==0?"0%":df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
        String an = yesterdayRelNum==0?"0%":df.format((float)todayRelNum/(float)yesterdayRelNum*100)+"%";
        String mom = lasetYearTodayRelNum==0?"0%":df.format((float)todayRelNum/(float)lasetYearTodayRelNum*100)+"%";
        int errorNum = 0;
        int timelyNum = 0;
        timelyNum = todayRelNum;
        qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayRelNum,timelyNum);
        qcQuotaResultService.save(qcQuotaResult);
    }

    //设置机构信息
    public QcQuotaResult setOrgInfo(String orgCode,QcQuotaResult qcQuotaResult){
        Organization org = orgService.getOrg(orgCode);
        if(org != null && StringUtils.isNotEmpty(org.getLocation())){
            Geography geography = geographyService.getAddressById(org.getLocation());
            if(geography!=null){
                qcQuotaResult.setOrgCode(orgCode);
                qcQuotaResult.setOrgName(org.getShortName());
                qcQuotaResult.setCity(geography.getCity());
                qcQuotaResult.setCityName(geography.getCity());
                qcQuotaResult.setTown(geography.getTown());
                qcQuotaResult.setTownName(geography.getTown());
            }
        }
        return qcQuotaResult;
    }

    //设置QcQuotaResult信息
    public QcQuotaResult setBaseInfo(QcQuotaResult qcQuotaResult,String quotaId,String quotaName ,Date quotaDate){
        qcQuotaResult.setQuotaId(Long.valueOf(quotaId));
        qcQuotaResult.setQuotaName(quotaName);
        qcQuotaResult.setQuotaDate(new Date());
        qcQuotaResult.setReceiveTime(quotaDate);
        qcQuotaResult.setEventTime(quotaDate);
        return qcQuotaResult;
    }

    //设置QcQuotaResult  统计数据信息
    public QcQuotaResult setDataInfo(QcQuotaResult qcQuotaResult,String val,String an ,String mom,int errorNum,int todayTolNum,int todayRelNum,int timeLyNum){
        qcQuotaResult.setValue(val);
        qcQuotaResult.setAn(an);
        qcQuotaResult.setMom(mom);
        qcQuotaResult.setErrorNum(errorNum);
        qcQuotaResult.setTotalNum(todayTolNum);
        qcQuotaResult.setRealNum(todayRelNum);
        qcQuotaResult.setTimelyNum(timeLyNum);
        return qcQuotaResult;
    }

    // type 1获取昨天的实收数据, 2 去年今天的实收数据
    public int getYesterdayOrLastYearData( List<Object> objects,Date quotaDate,int type ){
        List<Map<Integer,Object>> listVal = null;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        if(objects != null){
            listVal = converListObject(objects);
            for(Map<Integer,Object> map :listVal){
                Date createTime = DateUtil.formatCharDateYMD(map.get(0).toString());
                quotaDate = DateUtil.formatCharDateYMD(DateUtil.formatDate(quotaDate,"yyyy-MM-dd"));
                int days = DateUtil.getDifferenceOfDays(createTime,quotaDate);
                if(days == 1){
                    yesterdayRelNum = Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
                }else if(days == 365 || days == 366){
                    lasetYearTodayRelNum = Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
                }
            }
        }
        if(type==1){
            return  yesterdayRelNum;
        }else {
            return  lasetYearTodayRelNum;
        }
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
