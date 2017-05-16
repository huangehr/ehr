package com.yihu.ehr.report.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;

import java.text.DecimalFormat;

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

}
