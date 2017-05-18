package com.yihu.ehr.report.controller;

import com.yihu.ehr.model.report.MQcDailyReportResultDetail;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by janseny on 2017/5/16.
 */
public class calculatePointUtil {


    /**
     * 计算各指标的数据占比
     * @param quotaId  指标ID
     * @param realNum  实收数量
     * @param totalNum  应收数量
     * @param errorNum  错误数量
     * @param timelyNum 及时采集数量
     * @return
     */
    public String calculatePoint(String quotaId,int realNum,int totalNum ,int errorNum ,int timelyNum){
        DecimalFormat df = new DecimalFormat("0.00");
        String point ="0.00";
        if(StringUtils.isNotEmpty(quotaId)){
            //指标ID
            if( quotaId.equals("2")||quotaId.equals("1")||quotaId.equals("3")){
                //数据集完整性-2，档案完整性-1，数据元完整性-3
                point = df.format(((float)realNum/(float)totalNum)*100);
            }
            if( (quotaId.equals("7")||quotaId.equals("5")||quotaId.equals("6"))){
                //住院及时性-7，档案及时性-5，门诊及时性-6
                point = df.format(((float)timelyNum/(float)totalNum)*100);
            }
            if( quotaId.equals("4") ){
                //数据元准确性-4
                point = df.format(((float)(totalNum-errorNum)/(float)totalNum*100));
            }
        }
        return  point;
    }

    /**
     * 分析列表赋值
     * @param scaleType //总体-1，同比-2，环比-3
     * @return
     */
    public Map<String,String> reportDetailData(String scaleType,Object[] obj){
        Map<String,String> map=new TreeMap();
        String reportDetail="";
        //指标IDobj[3].toString()
        String quotaId=obj[3].toString();
        //总比
        String value=obj[5].toString();
        //实收数量
        String realNum=obj[7].toString();
        //应收数量
        String totalNum=obj[6].toString();
        //错误数量
        String errorNum=obj[8].toString();
        //及时采集数量
        String timelyNum=obj[9].toString();
        //同比：与去年的当天&周&月相比
        String an=obj[10].toString();
        //环比：与前一天&周&月环比
        String mom=obj[11].toString();
            if(StringUtils.isNotEmpty(quotaId)){
                //返回总比+实收输/应收数 档案完整性-1
                map=detailValue(scaleType,quotaId,value,realNum,totalNum,errorNum,timelyNum,an,mom);
            }
        return  map;
    }

    /**
     * 计算各指标的数据占比
     * @param scaleType  比例类型
     * @param quotaId  指标ID
     * @param value  总比
     * @param realNum  实收数量
     * @param totalNum  应收数量
     * @param errorNum  错误数量
     * @param timelyNum 及时采集数量
     * @param an    //同比：与去年的当天&周&月相比
     * @param mom    //环比：与前一天&周&月环比
     * @return
     */
    public Map<String,String> detailValue(String scaleType,String quotaId,String value,String realNum,String totalNum,String errorNum,String timelyNum,String an,String mom){
        Map<String,String> map=new TreeMap();
        //根据不同的指标返回不同的比例
        String reportDetail="";
            //返回总比+实收输/应收数 档案完整性-1
            if(null!=scaleType&&scaleType.equals("1")){
                //指标ID
                if( quotaId.equals("2")||quotaId.equals("1")||quotaId.equals("3")){
                    //数据集完整性-2，档案完整性-1，数据元完整性-3
                    reportDetail =value+"% <br/>"+ realNum+"/"+totalNum;
                }
                if( (quotaId.equals("7")||quotaId.equals("5")||quotaId.equals("6"))){
                    //住院及时性-6，档案及时性-5，门诊及时性-7
                    reportDetail =value+"% <br/>"+ timelyNum+"/"+totalNum;
                }
                if( quotaId.equals("4") ){
                    //数据元准确性-4
                    reportDetail =value+"% <br/>"+String.valueOf(Integer.valueOf(totalNum)-Integer.valueOf(errorNum))+"/"+totalNum;
                }
                map.put(scaleType,reportDetail);
            }else if(null!=scaleType&&scaleType.equals("2")){
                //返回同比
                map.put(scaleType,an+"%");
            }else if(null!=scaleType&&scaleType.equals("3")){
                //返回环比
                map.put(scaleType,mom+"%");
            }
        return  map;
    }
    /**
     * 计算各指标的数据占比
     * @param detailMapKey  key
     * @param eventTime  事件时间
     * @param orgCode  机构code
     * @param orgName  机构名称
     * @param scaleType  比值类型
     * @param value  比值
     * @return
     */
    public Map<String,MQcDailyReportResultDetail> detailValue(String resultQuotaId,int j,Map<String,MQcDailyReportResultDetail> detailMap ,String detailMapKey,String eventTime,String orgCode,String orgName,String scaleType,String value){
         MQcDailyReportResultDetail qrd=null;
        if(null==detailMap.get(detailMapKey)){
            qrd=new MQcDailyReportResultDetail();
            qrd=new MQcDailyReportResultDetail();
            qrd.setEventTime(eventTime);
            qrd.setOrgCode(orgCode);
            qrd.setOrgName(orgName);
            qrd.setScaleType(scaleType);
        }else {
            qrd = detailMap.get(detailMapKey);
        }
        if (resultQuotaId.equals("1")) {
            //档案完整性-1
            qrd.setArIntegrity(value);
        }else if (resultQuotaId.equals("2")) {
            //数据集完整性-2
            qrd.setDsIntegrity(value);
        } else if (resultQuotaId.equals("3")) {
            //数据元完整性-3
            qrd.setMdIntegrity(value);
        } else if (resultQuotaId.equals("4")) {
            //数据元准确性-4
            qrd.setMdAccuracy(value);
        } else if (resultQuotaId.equals("5")) {
            //档案及时性-5
            qrd.setArTimely(value);
        } else if (resultQuotaId.equals("6")) {
            //住院及时性-6
            qrd.setHpTimely(value);
        } else {
            //门诊及时性-7
            qrd.setOpTimely(value);
        }
        detailMap.put(detailMapKey, qrd);
        return  detailMap;
    }

}
