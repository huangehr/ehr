package com.yihu.ehr.report.service;

import com.yihu.ehr.entity.geography.Geography;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.geography.service.GeographyService;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.org.service.OrgService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created by janseny on 2017/5/16.
 */
@Service
public class QuotaStatisticsService {

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

    public static String quotaId1Name = "档案完整性";
    public static String quotaId2Name = "数据集完整性";
    public static String quotaId3Name = "数据元完整性";
    public static String quotaId4Name = "数据元准确性";
    public static String quotaId5Name = "档案及时性";
    public static String quotaId6Name = "门诊及时性";
    public static String quotaId7Name = "住院及时性";

    /**
     *
     * @param quotaId  指标ID  1 档案完整性 2 数据集完整性 3 数据元完整性 4 数据元准确性 5 档案及时性 6 门诊及时性 7 住院及时性
     * @param orgCode  机构编码
     * @param quotaDateStr 指标统计时间  今天统计昨天
     */
    public void statisticQuotaData( String quotaId,String orgCode ,String quotaDateStr ) throws ParseException {
        Date quotaDate = DateUtil.formatCharDateYMD(quotaDateStr);
        switch (quotaId){
            case "1":
                statisQuotaBy1(quotaId,quotaId1Name, orgCode , quotaDate);
                break;
            case "2":
                statisDatasetsQuotaBy2(quotaId,quotaId2Name, orgCode, quotaDate);
                break;
            case "3":
                statisMetaddataQuotaBy3(quotaId,quotaId3Name, orgCode, quotaDate);
                break;
            case "4":
                statisMetaddataQuotaBy4(quotaId,quotaId4Name, orgCode, quotaDate);
                break;
            case "5":
                statisTimelyQuotaByQuotaId(quotaId,quotaId5Name,null, orgCode, quotaDate);
                break;
            case "6":
                statisTimelyQuotaByQuotaId(quotaId,quotaId6Name,"outpatient", orgCode, quotaDate);
                break;
            case "7":
                statisTimelyQuotaByQuotaId(quotaId, quotaId7Name, "hospital", orgCode, quotaDate);
                break;
        }
    }

    //1 档案完整性 统计
    public void statisQuotaBy1(String quotaId ,String quotaName,String orgCode ,Date quotaDate) throws ParseException {
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
        List<Object> objectList = qcDailyReportService.getOrgData(orgCode,quotaDate);
        if(objectList != null && objectList.size() > 0){
            for(Object object :objectList){
                Map<Integer,Object> mapVal  = converMapObject(object);
                todayTolNum = todayTolNum + Integer.valueOf(mapVal.get(2).toString()) + Integer.valueOf(mapVal.get(4).toString());
                String storageFlag = "3";//解析成功入库
                String reportId = mapVal.get(6).toString();
                //查询已入库的的数量
                List todaylist = qcDailyReportDetailService.getQcDailyReportDetailData(reportId,quotaDate,storageFlag);
                int num = todaylist == null ? 0 : todaylist.size();
                todayRelNum = todayRelNum + num;
            }
            yesterdayRelNum = getRealCount(orgCode,parseYesterday(quotaDate));
            lasetYearTodayRelNum = getRealCount(orgCode,parseLastYearToday(quotaDate));
            String val = todayTolNum==0?"0%":df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum==0?"0%":df.format(((float)todayRelNum-(float)yesterdayRelNum)/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum==0?"0%":df.format(((float)todayRelNum-(float)lasetYearTodayRelNum)/(float)lasetYearTodayRelNum*100)+"%";
            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayRelNum,timelyNum);
            qcQuotaResultService.save(qcQuotaResult);
        }
    }


    public int getRealCount(String orgCode,Date quotaDate){
        int todayRelNum = 0;
        List<Object> objectList = qcDailyReportService.getOrgData(orgCode,quotaDate);
        if(objectList != null && objectList.size() > 0){
            for (Object object : objectList) {
                Map<Integer, Object> mapVal = converMapObject(object);
                String storageFlag = "3";//解析成功入库
                String reportId = mapVal.get(6).toString();
                List todaylist = qcDailyReportDetailService.getQcDailyReportDetailData(reportId, quotaDate, storageFlag);
                int num = todaylist == null ? 0 : todaylist.size();
                todayRelNum = todayRelNum + num;
            }
        }
        return todayRelNum;
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
        List<Object> objectList  = qcDailyReportDatasetsService.getOrgDatasetsData(orgCode, quotaDate);
        if(objectList != null && objectList.size() > 0){
            for (Object object : objectList) {
                Map<Integer,Object> mapVal  = converMapObject(object);
                todayTolNum = todayTolNum + Integer.valueOf(mapVal.get(2).toString());
                todayRelNum = todayRelNum + Integer.valueOf(mapVal.get(3).toString());
            }
            List<Object> objects = qcDailyReportDatasetsService.getDatasetsYesdayLastYearData(orgCode, quotaDate, parseLastYearToday(quotaDate));
            yesterdayRelNum =  getYesterdayOrLastYearData(objects,quotaDate,1);
            lasetYearTodayRelNum =  getYesterdayOrLastYearData(objects,parseLastYearToday(quotaDate),2);

            String val = todayTolNum == 0 ? "0%" : df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum == 0 ? "0%" : df.format(((float)todayRelNum-(float)yesterdayRelNum)/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum == 0 ? "0%" :df.format(((float)todayRelNum-(float)lasetYearTodayRelNum)/(float)lasetYearTodayRelNum*100)+"%";

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
        int todayTolNum = 0;
        int nullErrorNum = 0;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        List<Object> objectList  = qcDailyReportMetaDataService.getOrgMeataData(orgCode, quotaDate);
        if(objectList != null && objectList.size() > 0){
            List<Map<Integer,Object>> list  = converListObject(objectList);
            for(Map<Integer,Object> map : list){
                todayTolNum = todayTolNum + Integer.valueOf(map.get(0).toString());
                String errorCode = map.get(2).toString();
                nullErrorNum = nullErrorNum + errorCode.split(";").length;
            }
            List<Object> objects = qcDailyReportMetaDataService.getDataMeataYesdayLastYearData(orgCode, quotaDate, parseLastYearToday(quotaDate));
            if(objects != null && objects.size() > 0) {
                List<Map<Integer,Object>> mapList = converListObject(objects);
                for(Map<Integer,Object> map : mapList){
                    if(map.get(0) !=null ){
                        Date createTime = DateUtil.formatCharDateYMD(map.get(0).toString());
                        quotaDate = DateUtil.formatCharDateYMD(DateUtil.formatDate(quotaDate,"yyyy-MM-dd"));
                        int days = DateUtil.getDifferenceOfDays(createTime,quotaDate);
                        int days2 = DateUtil.getDifferenceOfDays(createTime,parseLastYearToday(quotaDate));
                        if(days == 1){
                            yesterdayRelNum = yesterdayRelNum + map.get(3).toString().split(";").length;
                        }else if(days2 == 0){
                            lasetYearTodayRelNum = lasetYearTodayRelNum + map.get(3).toString().split(";").length;
                        }
                    }
                }
            }
            String val = todayTolNum == 0 ? "0%" : df.format((float)nullErrorNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum == 0 ? "0%" : df.format(((float)nullErrorNum-(float)yesterdayRelNum)/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum == 0 ? "0%" :df.format(((float)nullErrorNum-(float)lasetYearTodayRelNum)/(float)lasetYearTodayRelNum*100)+"%";
            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,nullErrorNum,todayTolNum,todayTolNum-nullErrorNum,0);
            qcQuotaResultService.save(qcQuotaResult);
        }
    }


    // 4 数据元准确性 统计
    public void statisMetaddataQuotaBy4(String quotaId ,String quotaName,String orgCode ,Date quotaDate){
        DecimalFormat df = new DecimalFormat("0");
        QcQuotaResult qcQuotaResult = new QcQuotaResult();
        qcQuotaResult = setBaseInfo(qcQuotaResult,quotaId,quotaName,quotaDate);
        qcQuotaResult = setOrgInfo(orgCode,qcQuotaResult);
        int todayErrorNum = 0;
        int todayTolNum = 0;
        int yesterdayErrorNum = 0;
        int lasetYearTodayErrorNum = 0;
        int errorNum = 0;
        List<Object> objectList  = qcDailyReportMetaDataService.getOrgMeataData(orgCode, quotaDate);
        String errrCodeStr = "";
        if(objectList != null && objectList.size() > 0){
            List<Map<Integer,Object>> listVal = converListObject(objectList);
            for(Map<Integer,Object> map :listVal){
                todayTolNum = todayTolNum + Integer.valueOf(map.get(0).toString());
                todayErrorNum = todayErrorNum + Integer.valueOf(map.get(1).toString());
                errrCodeStr = errrCodeStr + map.get(2).toString();
            }
            List<Object> objects = qcDailyReportMetaDataService.getDataMeataYesdayLastYearData(orgCode,quotaDate, parseLastYearToday(quotaDate));
            yesterdayErrorNum =  getYesterdayOrLastYearData(objects,quotaDate,1);
            lasetYearTodayErrorNum =  getYesterdayOrLastYearData(objects,parseLastYearToday(quotaDate),2);
            String val = todayTolNum == 0 ? "0%" : df.format((float)todayErrorNum/(float)todayTolNum*100)+"%";
            String an = yesterdayErrorNum == 0 ? "0%" : df.format(((float)todayErrorNum-(float)yesterdayErrorNum)/(float)yesterdayErrorNum*100)+"%";
            String mom = lasetYearTodayErrorNum == 0 ? "0%" :df.format(((float)todayErrorNum-(float)lasetYearTodayErrorNum)/(float)lasetYearTodayErrorNum*100)+"%";

            errorNum = todayErrorNum;
            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayTolNum-errorNum,0);
            qcQuotaResultService.save(qcQuotaResult);
        }
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
        int errorNum = 0;
        int timelyNum = 0;
        String storageFlag = "3";
        List<Object> objectList = qcDailyReportService.getOrgData(orgCode, quotaDate);

        if(objectList != null && objectList.size() > 0){
            for (Object object : objectList) {
                Map<Integer,Object> mapVal  = converMapObject(object);
                todayTolNum = todayTolNum + qcDailyReportService.getOrgDailyReportDetailCount(mapVal.get(6).toString(), null,archiveType,storageFlag);
                todayRelNum = todayRelNum + qcDailyReportService.getOrgDailyReportDetailCount(mapVal.get(6).toString(), "1",archiveType,storageFlag);
            }
            List<Object> yesterObjects = qcDailyReportService.getYesterdayData(orgCode, quotaDate);
            if(yesterObjects != null && yesterObjects.size() > 0) {
                List<Map<Integer,Object>> mapList = converListObject(yesterObjects);
                for(Map<Integer,Object> map : mapList){
                    yesterdayRelNum = yesterdayRelNum + qcDailyReportService.getOrgDailyReportDetailCount(map.get(3).toString(), "1",archiveType,storageFlag);
                }
            }
            List<Object> lassYearObjects = qcDailyReportService.getYesterdayData(orgCode, parseLastYearToday(quotaDate));
            if(lassYearObjects != null && lassYearObjects.size() > 0) {
                List<Map<Integer,Object>> mapList = converListObject(lassYearObjects);
                for(Map<Integer,Object> map : mapList){
                    lasetYearTodayRelNum  = lasetYearTodayRelNum + qcDailyReportService.getOrgDailyReportDetailCount(map.get(3).toString(), "1",archiveType,storageFlag);
                }
            }
            String val = todayTolNum==0?"0%":df.format((float)todayRelNum/(float)todayTolNum*100)+"%";
            String an = yesterdayRelNum==0?"0%":df.format(((float)todayRelNum-(float)yesterdayRelNum)/(float)yesterdayRelNum*100)+"%";
            String mom = lasetYearTodayRelNum==0?"0%":df.format(((float)todayRelNum-(float)lasetYearTodayRelNum)/(float)lasetYearTodayRelNum*100)+"%";
            timelyNum = todayRelNum;
            qcQuotaResult = setDataInfo(qcQuotaResult,val,an,mom,errorNum,todayTolNum,todayRelNum,timelyNum);
            qcQuotaResultService.save(qcQuotaResult);
        }
    }

    //设置机构信息
    public QcQuotaResult setOrgInfo(String orgCode,QcQuotaResult qcQuotaResult){
        Organization org = orgService.getOrg(orgCode);
        if(org != null && StringUtils.isNotEmpty(org.getLocation())){
            qcQuotaResult.setOrgCode(orgCode);
            Geography geography = geographyService.getAddressById(org.getLocation());
            if(geography!=null){
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
    public int getYesterdayOrLastYearData( List<Object> objects,Date date,int type ){
        List<Map<Integer,Object>> listVal = null;
        int yesterdayRelNum = 0;
        int lasetYearTodayRelNum = 0;
        if(objects != null){
            listVal = converListObject(objects);
            for(Map<Integer,Object> map :listVal){
                Date createTime = DateUtil.formatCharDateYMD(map.get(0).toString());
                date = DateUtil.formatCharDateYMD(DateUtil.formatDate(date,"yyyy-MM-dd"));
                int days = DateUtil.getDifferenceOfDays(createTime,date);
                if(days == 1 && type == 1){
                    yesterdayRelNum = yesterdayRelNum + Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
                }else if(days == 0 && type == 2){
                    lasetYearTodayRelNum = lasetYearTodayRelNum + Integer.valueOf(map.get(1).toString())+Integer.valueOf(map.get(2).toString());
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


}
