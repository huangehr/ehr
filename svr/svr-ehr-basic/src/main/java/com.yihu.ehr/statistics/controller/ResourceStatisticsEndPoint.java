package com.yihu.ehr.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.model.tj.MapDataModel;
import com.yihu.ehr.patient.service.arapply.UserCardsService;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.patient.service.arapply.ArchiveRelationService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ResourceStatistics", description = "资源中心首页报表", tags = {"资源中心首页报表-入口"})
public class ResourceStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private DemographicService demographicService;
    @Autowired
    UserCardsService userCardsService;
    @Autowired
    private ArchiveRelationService archiveRelationService;

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsElectronicMedicalCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历-最近七天采集总数统计，门诊住院数 - 柱状")
    public Envelop getStatisticsElectronicMedical( ) throws Exception {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        List<String> dateList = new ArrayList<>();
        List<Object> list = archiveRelationService.getCollectTocalCount();
        if( list != null && list.size() > 0){
            for(int i=0 ; i < list.size(); i++){
                Map<Integer,Object> mapVal  = converMapObject(list.get(i));
                dateList.add(mapVal.get(1).toString());
            }
            EchartReportModel echartReportModel = setEchartReportModel(list,"采集总数",dateList);
            echartReportModels.add(echartReportModel);
        }
        List<Object> list0 = archiveRelationService.getCollectEventTypeCount(0);
        if( list0 != null && list0.size() > 0){
            EchartReportModel echartReportModel = setEchartReportModel(list0,"outpatient",dateList);
            echartReportModels.add(echartReportModel);
        }
        List<Object> list1 = archiveRelationService.getCollectEventTypeCount(1);
        if( list1 != null && list1.size() > 0){
            EchartReportModel echartReportModel = setEchartReportModel(list1,"hospital",dateList);
            echartReportModels.add(echartReportModel);
        }
        envelop.setDetailModelList(echartReportModels);
        envelop.setSuccessFlg(true);
        return envelop;
    }


    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsMedicalEventTypeCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历 - 今天 门诊住院数统计 - 饼状")
    public Envelop getStatisticsElectronicMedicalEventType( ) throws Exception {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        List<Object> list = archiveRelationService.getCollectTodayEventTypeCount();
        List<MapDataModel> dataList = new ArrayList<>();
        if( list != null && list.size() > 0){
            EchartReportModel echartReportModel = new EchartReportModel();
            for(int i=0 ; i < list.size(); i++){
                MapDataModel mapDataModel = new MapDataModel();
                Map<Integer,Object> mapVal  = converMapObject(list.get(i));
                if(mapVal.get(1).toString().equals("0")){
                    mapDataModel.setName("outpatient");
                }else  if(mapVal.get(1).toString().equals("1")){
                    mapDataModel.setName("hospital");
                }
                mapDataModel.setValue(mapVal.get(0).toString());
                dataList.add(mapDataModel);
            }
            echartReportModel.setDataModels(dataList);
            echartReportModels.add(echartReportModel);
        }
        envelop.setDetailModelList(echartReportModels);
        Map<String,Object> mapObj = new HashMap<>();
        for(MapDataModel dataModel:dataList){
            if(dataModel.getName().equals("outpatient")){
                mapObj.put("outpatient",dataModel.getValue());
            }
            if(dataModel.getName().equals("hospital")){
                mapObj.put("hospital",dataModel.getValue());
            }
        }
        int total = 0;
        if (mapObj != null && mapObj.size() > 0 ){
            for(String key :mapObj.keySet()){
                total = total + Integer.valueOf(mapObj.get(key).toString());
            }
        }
        mapObj.put("hospital/outpatient",total);
        envelop.setObj(mapObj);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计 -柱状")
    public Envelop getStatisticsDemographicsAgeCount( ) throws Exception {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        List<Object> list = demographicService.getStatisticsDemographicsAgeCount();
        if( list != null && list.size() > 0){
            EchartReportModel echartReportModel = new EchartReportModel();
            String [] xdata = new String[list.size()];
            int [] ydata = new int[list.size()];
            for(int i=0 ; i < list.size(); i++){
                Map<Integer,Object> mapVal  = converMapObject(list.get(i));
                ydata[i] = Integer.valueOf(mapVal.get(0).toString());
                xdata[i] = mapVal.get(1).toString();
            }
            echartReportModel.setxData(xdata);
            echartReportModel.setyData(ydata);
            echartReportModels.add(echartReportModel);
        }
        envelop.setDetailModelList(echartReportModels);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    public EchartReportModel setEchartReportModel(List<Object> objectList,String name,List<String> dateList){
        EchartReportModel echartReportModel = new EchartReportModel();
        if( objectList != null && objectList.size() > 0){
            echartReportModel.setName(name);
            if(dateList != null){
                int num = dateList.size();
                int[] ydata = new int[num];
                String[] xdata = new String[num];
                echartReportModel.setxData(xdata);
                for(int k=0 ; k < dateList.size() ; k++){
                    int val = 0;
                    for(int i=0 ; i < objectList.size() ; i++){
                        Map<Integer,Object> mapVal  = converMapObject(objectList.get(i));
                        if(mapVal.get(1).toString().equals(dateList.get(k))){
                            val = Integer.valueOf(mapVal.get(0).toString());
                            break;
                        }
                    }
                    ydata[k] = val;
                    xdata[k] = dateList.get(k).toString().substring(5);
                }
                echartReportModel.setyData(ydata);
            }
        }
        return echartReportModel;
    }

    public Map<Integer,Object> converMapObject(Object object){
        Map<Integer,Object> map = new HashMap<>();
        Object[] obj = (Object[]) object;
        for (int i = 0; i < obj.length; i++) {
            map.put(i, obj[i].toString());
        }
        return map;
    }


//    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
//    @ApiOperation(value = "全员人口个案-健康卡绑定量")
//    public ListResult getTjDataSaveList(
//            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
//            @RequestParam(value = "fields", required = false) String fields,
//            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
//            @RequestParam(value = "filters", required = false) String filters
//    ) throws Exception {
//
//        ListResult listResult = new ListResult();
//
//        return listResult;
//    }

    @RequestMapping(value = "/getStatisticsUserCards", method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public List<EchartReportModel> getStatisticsUserCards() {
        //获取居民总数
        Map<String, Object> map=new HashedMap();
        map.put("search",null);
        map.put("province",null);
        map.put("city",null);
        map.put("district",null);
        map.put("gender",null);
        map.put("startDate",null);
        map.put("endDate",null);
       int totalDemographicsNum=demographicService.searchPatientByParamsTotalCount(map);
        //获取绑卡量 userCardsNum
        int userCardsNum= 0;
        try {
            String filters="";
             userCardsNum=(int)userCardsService.getCount(filters);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 计算未绑卡量 nonBindingCardNum、
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        int nonBindingCardNum=totalDemographicsNum - userCardsNum;
        ////获取绑卡率
        String userCardsScale = df.format((float)userCardsNum/totalDemographicsNum);//返回的是String类型
        String userCardsN=String.valueOf(userCardsNum)+"("+userCardsScale+"%)";


        // 计算未绑卡率
        String nonBindingCardScale = df.format((float)nonBindingCardNum/totalDemographicsNum);//返回的是String类型
        String nonBindingCardN=String.valueOf(nonBindingCardNum)+"("+nonBindingCardScale+"%)";
        EchartReportModel echartReportModel=new EchartReportModel();
        Map<String, String> mapData=new HashedMap();
        mapData.put("totalDemographicsNum",String.valueOf(totalDemographicsNum));
        mapData.put("userCardsNum",userCardsN);
        mapData.put("nonBindingCardNum",nonBindingCardN);
//        echartReportModel.setMapData(mapData);
        List<EchartReportModel> quotaCategories = new ArrayList<>();
        quotaCategories.add(echartReportModel);
        return (List<EchartReportModel>) convertToModels(quotaCategories, new ArrayList<EchartReportModel>(quotaCategories.size()), EchartReportModel.class, null);
    }

    @RequestMapping(value = "archive/getArchiveIdentifyReportInfo", method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--档案识别")
    public Envelop getArchiveIdentifyReportInfo() throws Exception {
        Envelop envelop = new Envelop();
        int archiveCount = archiveRelationService.getArchiveCount();
        int identifyCount = archiveRelationService.getIdentifyCount();
        int unIdentifyCount = archiveRelationService.getUnIdentifyCount();
        List<EchartReportModel> echartReportModelList = new ArrayList<>();
        EchartReportModel echartReportModel = new EchartReportModel();
        List<MapDataModel> mapDataModels = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MapDataModel mapData = new MapDataModel();
            if (i == 0) {
                mapData.setName("不可识别");
                mapData.setValue(unIdentifyCount + "");
            } else if (i == 1) {
                mapData.setName("可识别");
                mapData.setValue(identifyCount + "");
            } /*else if (i == 2) {
                mapData.setName("archiveIdentify");
                mapData.setValue(archiveCount + "");
            }*/
            mapDataModels.add(mapData);
        }
        echartReportModel.setDataModels(mapDataModels);
        echartReportModelList.add(echartReportModel);

        Map<String, String> mapData = new HashMap<>();
        mapData.put("archiveIdentify", archiveCount + "");
        mapData.put("identify", identifyCount + "");
        mapData.put("unIdentify", unIdentifyCount + "");
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(echartReportModelList);
        envelop.setObj(mapData);
        return envelop;
    }

    @RequestMapping(value = "archive/getArchiveHospitalReportInfo", method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--住院/门诊")
    public Envelop getArchiveHospitalReportInfo() throws Exception {
        Envelop envelop = new Envelop();
        int inPatientCount = archiveRelationService.getInPatientCount();
        int outPatientCount = archiveRelationService.getOutPatientCount();
        int inAndOutPatientCount = archiveRelationService.getArchiveCount();
        int physicalCount = archiveRelationService.getPhysicalCount();
        List<EchartReportModel> echartReportModelList = new ArrayList<>();
        EchartReportModel echartReportModel = new EchartReportModel();
        List<MapDataModel> mapDataModels = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            MapDataModel mapData = new MapDataModel();
            if (i == 0) {
                mapData.setName("住院");
                mapData.setValue(inPatientCount + "");
            } else if (i == 1) {
                mapData.setName("门诊");
                mapData.setValue(outPatientCount + "");
            } else if (i == 2) {
                mapData.setName("体检");
                mapData.setValue(physicalCount + "");
            }
            mapDataModels.add(mapData);
        }
        echartReportModel.setDataModels(mapDataModels);
        echartReportModelList.add(echartReportModel);

        Map<String, String> mapData = new HashMap<>();
        mapData.put("hospital/outpatient",inAndOutPatientCount + "");
        mapData.put("hospital",inPatientCount + "");
        mapData.put("outpatient",outPatientCount + "");
        mapData.put("physical",physicalCount + "");
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(echartReportModelList);
        envelop.setObj(mapData);
        return envelop;
    }

    @RequestMapping(value = "archive/getArchiveStatisticalReportInfo", method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--数据统计")
    public Envelop getArchiveStatisticalReportInfo() throws Exception {
        Envelop envelop = new Envelop();
        int todayInWarehouseCount = archiveRelationService.getTodayInWarehouseCount();
        int archiveCount = archiveRelationService.getArchiveCount();
        List<EchartReportModel> echartReportModelList = new ArrayList<>();
        EchartReportModel echartReportModel = new EchartReportModel();
        List<MapDataModel> mapDataModels = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            MapDataModel mapData = new MapDataModel();
            if (i == 0) {
                mapData.setName("今日入库");
                mapData.setValue(todayInWarehouseCount + "");
            } else if (i == 1) {
                mapData.setName("数据统计");
                mapData.setValue(archiveCount + "");
            } /*else if (i == 2) {
                mapData.setName("dataStatistical");
                mapData.setValue("");
            }*/
            mapDataModels.add(mapData);
        }
        echartReportModel.setDataModels(mapDataModels);
        echartReportModelList.add(echartReportModel);

        Map<String,String> mapData = new HashMap<>();
        mapData.put("dataStatistical", archiveCount + "");
        mapData.put("todayInWarehouse", todayInWarehouseCount + "");
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(echartReportModelList);
        envelop.setObj(mapData);
        return envelop;
    }

    @RequestMapping(value = "archive/getArchiveTotalVisitReportInfo", method = RequestMethod.GET)
    @ApiOperation(value = "健康档案--累计就诊人次")
    public Envelop getArchiveTotalVisitReportInfo() throws Exception {
        Envelop envelop = new Envelop();
        int dailyAdd = archiveRelationService.getDailyAdd();
        int inAndOutPatientCount = archiveRelationService.getInAndOutPatientCount();
        Map<String,String> mapData = new HashMap<>();
        mapData.put("dailyAdd", dailyAdd + "");
        mapData.put("totalVisits", inAndOutPatientCount + "");
        envelop.setSuccessFlg(true);
        envelop.setObj(mapData);
        return envelop;
    }
}
