package com.yihu.ehr.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.model.tj.MapDataModel;
import com.yihu.ehr.patient.service.arapply.ArchiveRelationService;
import com.yihu.ehr.patient.service.arapply.UserCardsService;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import com.yihu.ehr.user.service.DoctorService;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    DoctorService doctorService;



    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsElectronicMedicalCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历-最近七天采集总数统计，门诊住院数 - 柱状")
    public Envelop getStatisticsElectronicMedicalCount( ) throws Exception {
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
    public Envelop getStatisticsElectronicMedicalEventTypeCount( ) throws Exception {
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
                    mapDataModel.setValue(mapVal.get(0).toString());
                    dataList.add(mapDataModel);
                }else  if(mapVal.get(1).toString().equals("1")){
                    mapDataModel.setName("hospital");
                    mapDataModel.setValue(mapVal.get(0).toString());
                    dataList.add(mapDataModel);
                }

            }
            echartReportModel.setDataModels(dataList);
            echartReportModels.add(echartReportModel);
        }
        envelop.setDetailModelList(echartReportModels);
        Map<String,Object> mapObj = new HashMap<>();
        for(MapDataModel dataModel:dataList){
            if(null!=dataModel.getName()&&dataModel.getName().equals("outpatient")){
                mapObj.put("outpatient",dataModel.getValue());
            }
            if(null!=dataModel.getName()&&dataModel.getName().equals("hospital")){
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

    @RequestMapping(value = ServiceApi.StasticReport.getStatisticsUserCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public Envelop getStatisticsUserCards() {
        Envelop envelop = new Envelop();
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
        EchartReportModel echartReportModel=new EchartReportModel();
        List<MapDataModel> mapData=new ArrayList<>();
        MapDataModel mapDataModel=null;
        mapDataModel=new MapDataModel();
        mapDataModel.setName("健康卡绑定量");
        mapDataModel.setValue(String.valueOf(userCardsNum));
        mapData.add(mapDataModel);

        mapDataModel=new MapDataModel();
        mapDataModel.setName("未绑定量");
        mapDataModel.setValue(String.valueOf(nonBindingCardNum));
        mapData.add(mapDataModel);
        echartReportModel.setDataModels(mapData);
        List<EchartReportModel> quotaCategories = new ArrayList<>();
        quotaCategories.add(echartReportModel);

        Map<String, String> ma= new HashMap<>();
        ma.put("totalDemographicsNum",String.valueOf(totalDemographicsNum));
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(quotaCategories);
        envelop.setObj(ma);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveIdentifyReportInfo, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveHospitalReportInfo, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveStatisticalReportInfo, method = RequestMethod.GET)
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

    @RequestMapping(value = ServiceApi.StasticReport.GetArchiveTotalVisitReportInfo, method = RequestMethod.GET)
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

    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "按机构医生、护士、床位的统计")
    public Envelop getStatisticsDoctorByRoleType() {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        //根据角色/医院获取医生总数
        List<Object> doctorLost=  doctorService.getStatisticsDoctorsByRoleType("Doctor");
        //根据角色/医院获取护士总数
        List<Object> nurseLost= doctorService.getStatisticsDoctorsByRoleType("Nurse");
        int listSize=0;
        if( doctorLost != null && doctorLost.size() > 0&&doctorLost.size()>listSize){
            listSize=doctorLost.size();
        }
        if( nurseLost != null && nurseLost.size() > 0&& nurseLost.size()>listSize){
            listSize=nurseLost.size();
        }

        String [] xdata = new String[listSize];
        int [] ydata = new int[listSize];
        EchartReportModel echartReportModel = null;
        if( doctorLost != null && doctorLost.size() > 0){
            echartReportModel = new EchartReportModel();
            for(int i=0 ; i < doctorLost.size(); i++){
                Map<Integer,Object> mapVal  = converMapObject(doctorLost.get(i));
                ydata[i] = Integer.valueOf(mapVal.get(0).toString());
                xdata[i] = mapVal.get(2).toString();
            }
            echartReportModel.setName("医生");
            echartReportModel.setxData(xdata);
            echartReportModel.setyData(ydata);
            echartReportModels.add(echartReportModel);
        }else{
            echartReportModel = new EchartReportModel();
            echartReportModel.setName("医生");
            echartReportModel.setxData(xdata);
            echartReportModel.setyData(ydata);
            echartReportModels.add(echartReportModel);
        }
        String [] xDataN = new String[listSize];
        int [] yDataN = new int[listSize];
        if( nurseLost != null && nurseLost.size() > 0){
            echartReportModel = new EchartReportModel();
            for(int i=0 ; i < nurseLost.size(); i++){
                Map<Integer,Object> mapVal  = converMapObject(nurseLost.get(i));
                yDataN[i] = Integer.valueOf(mapVal.get(0).toString());
                xDataN[i] = mapVal.get(2).toString();
            }
            echartReportModel.setName("护士");
            echartReportModel.setxData(xDataN);
            echartReportModel.setyData(yDataN);
            echartReportModels.add(echartReportModel);
        }else{
            echartReportModel = new EchartReportModel();
            echartReportModel.setName("护士");
            echartReportModel.setxData(xDataN);
            echartReportModel.setyData(yDataN);
            echartReportModels.add(echartReportModel);
        }

        envelop.setDetailModelList(echartReportModels);
        envelop.setSuccessFlg(true);
        return envelop;
    }


    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsCityDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "全市医生、护士、床位的统计")
    public Envelop getStatisticsCityDoctorByRoleType() {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        EchartReportModel echartReportModel = new EchartReportModel();
        //根据角色/医院获取医生总数
        int doctorCount = doctorService.getStatisticsCityDoctorByRoleType("Doctor");
        List<MapDataModel> MapDataModelList=new ArrayList<>();
        MapDataModel mapDataModel=null;
        mapDataModel=new MapDataModel();
        mapDataModel.setName("医生");
        mapDataModel.setValue(String.valueOf(doctorCount));
        MapDataModelList.add(mapDataModel);

        int nurseCount = doctorService.getStatisticsCityDoctorByRoleType("Nurse");
        mapDataModel=new MapDataModel();
        mapDataModel.setName("护士");
        mapDataModel.setValue(String.valueOf(nurseCount));
        MapDataModelList.add(mapDataModel);
        echartReportModel.setDataModels(MapDataModelList);
        envelop.setObj(echartReportModel);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
