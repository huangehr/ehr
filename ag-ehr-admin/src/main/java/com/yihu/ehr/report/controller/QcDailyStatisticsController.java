package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.QcDailyStorageDetailModel;
import com.yihu.ehr.agModel.report.QcDailyStorageModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.report.service.QcDailyStatisticsClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "StatisticsProfile", description = "档案统计", tags = {"档案统计-档案统计"})
public class QcDailyStatisticsController extends ExtendController<QcDailyReport> {

    @Autowired
    QcDailyStatisticsClient qcDailyStatisticsClient;


    @RequestMapping(value = ServiceApi.DailyStatistics.StatisticsProfileCreateDate, method = RequestMethod.GET)
    @ApiOperation(value = "按入库时间统计-solr")
    public Map<String, Map<String, Long>> search(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(value = "startDate")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(value = "endDate")
            @RequestParam(value = "endDate") String endDate) throws Exception {

         Map<String, Map<String, Long>> map = qcDailyStatisticsClient.search(orgCode, startDate, endDate );
        return map;
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyStatisticsStorage, method = RequestMethod.GET)
    @ApiOperation(value = "单个机构总入库统计")
    public Envelop getQcDailyStatisticsStorage(
            @ApiParam(value = "orgCode")
            @RequestParam(value = "orgCode", required = false) String orgCode
        ) throws Exception {

        Map<String,Integer> map = qcDailyStatisticsClient.getQcDailyStatisticsStorage(orgCode);
       Envelop envelop = new Envelop();
        envelop.setObj(map);
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyStatisticsStorageByDate, method = RequestMethod.GET)
    @ApiOperation(value = "单个机构入库按时间统计")
    public Envelop getQcDailyStatisticsStorageByDate(
            @ApiParam(name = "orgCode", value = "机构编码")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "startDate", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "截止日期", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate) throws Exception {

        List<QcDailyStorageModel> qcDailyStorageModelList = new ArrayList<>();
        Map<String,Integer> storResultMap = qcDailyStatisticsClient.getQcDailyStatisticsStorage(orgCode);
        Map<String,String> idenResultMap = qcDailyStatisticsClient.getQcDailyStatisticsIdentify(orgCode);

        Map<String,List<QcDailyStorageDetailModel>> instalMap = new HashMap<>();
        Date star = DateUtil.formatCharDateYMD(startDate);
        Date end = DateUtil.formatCharDateYMD(endDate);
        while (DateUtil.compareDate(end,star) >= 0){
            List<QcDailyStorageDetailModel> instalStorageModels = new ArrayList<>();
            for(int i=0 ;i < 3 ; i++){
                QcDailyStorageDetailModel qcDailyStorageModel = new QcDailyStorageDetailModel();
                String eventTypeName = "";
                int totalStorageNum = 0;
                int totalIdenNum = 0;
                int totalNoIdenNum = 0;
                if( i == 0 ){
                    eventTypeName = "门诊";
                    totalStorageNum = storResultMap.get("0")==null ? 0 :storResultMap.get("0");
                    totalIdenNum = idenResultMap.get("0")==null ? 0 : Integer.valueOf( idenResultMap.get("0").split(";")[0]);
                    totalNoIdenNum = idenResultMap.get("0")==null ? 0 : Integer.valueOf( idenResultMap.get("0").split(";")[1]);
                }else  if(i == 1){
                    eventTypeName = "住院";
                    totalStorageNum = storResultMap.get("1")==null ? 0 :storResultMap.get("1");
                    totalIdenNum = idenResultMap.get("1")==null ? 0 : Integer.valueOf( idenResultMap.get("1").split(";")[0]);
                    totalNoIdenNum = idenResultMap.get("1")==null ? 0 : Integer.valueOf( idenResultMap.get("1").split(";")[1]);
                }else  if(i == 2){
                    eventTypeName = "体检";
                    totalStorageNum = storResultMap.get("2")==null ? 0 :storResultMap.get("2");;
                    totalIdenNum = idenResultMap.get("2")==null ? 0 : Integer.valueOf( idenResultMap.get("2").split(";")[0]);
                    totalNoIdenNum = idenResultMap.get("2")==null ? 0 : Integer.valueOf( idenResultMap.get("2").split(";")[1]);
                }
                qcDailyStorageModel.setDataType(eventTypeName);
                qcDailyStorageModel.setTotalStorageNum(totalStorageNum);
                qcDailyStorageModel.setTotalIdentifyNum(totalIdenNum);
                qcDailyStorageModel.setTotalNoIdentifyNum(totalNoIdenNum);
                qcDailyStorageModel.setTodayStorageNum(0);
                qcDailyStorageModel.setTodayIdentifyNum(0);
                qcDailyStorageModel.setTodayNoIdentifyNum(0);
                instalStorageModels.add(qcDailyStorageModel);
            }
            instalMap.put(DateUtil.formatDate(star,"yyyy-MM-dd"),instalStorageModels);
            star =  DateUtil.addDate(1,star);
        }

        List list = qcDailyStatisticsClient.getQcDailyStatisticsStorageByDate(orgCode, startDate, endDate);

        for(int i = 0 ;i < list.size() ; i++){
            List<Object> objectList =  (List<Object>)list.get(i);
            int num = Integer.valueOf(objectList.get(0).toString());//数量
            String date = objectList.get(1).toString();//统计的日期
            String eventType = objectList.get(2).toString();//事件类型
            int identifyNum = Integer.valueOf(objectList.get(3).toString());//可数量
            int noIdentifyNum = Integer.valueOf(objectList.get(4).toString());//不可数量
            if(eventType.equals("0")){
                eventType = "门诊";
            }else if(eventType.equals("1")){
                eventType = "住院";
            }else if(eventType.equals("2")){
                eventType = "体检";
            }

            List<QcDailyStorageDetailModel> oneStorageModels = new ArrayList<>();
            List<QcDailyStorageDetailModel> instalStorageModels = instalMap.get(date) ;
            for(QcDailyStorageDetailModel detailModel : instalStorageModels){
                if(eventType.equals(detailModel.getDataType())){
                    detailModel.setTodayStorageNum(num);
                    detailModel.setTodayIdentifyNum(identifyNum);
                    detailModel.setTodayNoIdentifyNum(noIdentifyNum);
                }
                oneStorageModels.add(detailModel);
            }
            instalMap.put(date, oneStorageModels);
        }
        for(String key : instalMap.keySet()){
            List<QcDailyStorageDetailModel> qcDailyStorageDetailModelList = instalMap.get(key);
            int todayStoreTotalNum = 0;
            int todayIdenTotalNum = 0;
            int todayNoIdenTotalNum = 0;
            for(QcDailyStorageDetailModel qcDailyStorageDetailModel : qcDailyStorageDetailModelList){
                todayStoreTotalNum = todayStoreTotalNum + qcDailyStorageDetailModel.getTodayStorageNum();
                todayIdenTotalNum = todayIdenTotalNum + qcDailyStorageDetailModel.getTodayIdentifyNum();
                todayNoIdenTotalNum = todayNoIdenTotalNum + qcDailyStorageDetailModel.getTodayNoIdentifyNum();
            }
            QcDailyStorageDetailModel totalQcDailyStorageModel = new QcDailyStorageDetailModel();
            totalQcDailyStorageModel.setTotalStorageNum(storResultMap.get("total"));
            totalQcDailyStorageModel.setTotalIdentifyNum(Integer.valueOf(idenResultMap.get("total").split(";")[0]));
            totalQcDailyStorageModel.setTotalNoIdentifyNum(Integer.valueOf(idenResultMap.get("total").split(";")[1]));
            totalQcDailyStorageModel.setTodayIdentifyNum(todayIdenTotalNum);
            totalQcDailyStorageModel.setTodayNoIdentifyNum(todayNoIdenTotalNum);
            totalQcDailyStorageModel.setDataType("HealthArchives");
            totalQcDailyStorageModel.setTodayStorageNum(todayStoreTotalNum);
            qcDailyStorageDetailModelList.add(totalQcDailyStorageModel);

            QcDailyStorageModel qcDailyStorageModel = new QcDailyStorageModel();
            qcDailyStorageModel.setEventDate(key);
            qcDailyStorageModel.setQcDailyStorageDetailModelList(qcDailyStorageDetailModelList);
            qcDailyStorageModelList.add(qcDailyStorageModel);
        }
        Envelop envelop = new Envelop();
        envelop.setDetailModelList(qcDailyStorageModelList);
        return envelop;
    }

}
