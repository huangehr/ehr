package com.yihu.ehr.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.dict.service.SystemDictEntry;
import com.yihu.ehr.dict.service.SystemDictEntryService;
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
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    SystemDictEntryService systemDictEntryService;


    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsElectronicMedicalCount, method = RequestMethod.GET)
    @ApiOperation(value = "电子病历-最近七天采集总数统计，门诊住院数 - 柱状")
    public Envelop getStatisticsElectronicMedicalCount( ) throws Exception {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar lastDate = Calendar.getInstance();
        dateList.add(sdf.format(lastDate.getTime()));
        for(int i=0;i<6;i++){
            lastDate.roll(Calendar.DATE, -1);//日期回滚7天
            String dateStr=sdf.format(lastDate.getTime());
            dateList.add(dateStr);
        }
        Collections.reverse(dateList); // 倒序排列

        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        List<Object> list = archiveRelationService.getCollectTocalCount();
        EchartReportModel echartReportModel = setEchartReportModel(list,"total",dateList);
        echartReportModels.add(echartReportModel);

        List<Object> list0 = archiveRelationService.getCollectEventTypeCount(0);
        EchartReportModel echartReportMode2 = setEchartReportModel(list0,"outpatient",dateList);
        echartReportModels.add(echartReportMode2);

        List<Object> list1 = archiveRelationService.getCollectEventTypeCount(1);
        EchartReportModel echartReportMode3 = setEchartReportModel(list1,"hospital",dateList);
        echartReportModels.add(echartReportMode3);

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
        }else{
            EchartReportModel echartReportModel = new EchartReportModel();
            MapDataModel hospitalModel = new MapDataModel();
            hospitalModel.setName("hospital");
            hospitalModel.setValue("0");
            dataList.add(hospitalModel);
            MapDataModel patientModel = new MapDataModel();
            patientModel.setName("outpatient");
            patientModel.setValue("0");
            dataList.add(patientModel);

            echartReportModel.setDataModels(dataList);
            echartReportModels.add(echartReportModel);
        }
        envelop.setDetailModelList(echartReportModels);
        Map<String,Object> mapObj = new HashMap<>();
        if(dataList !=null && dataList.size()>0){
            for(MapDataModel dataModel:dataList){
                if(null!=dataModel.getName()&&dataModel.getName().equals("outpatient")){
                    mapObj.put("outpatient",dataModel.getValue());
                }
                if(null!=dataModel.getName()&&dataModel.getName().equals("hospital")){
                    mapObj.put("hospital",dataModel.getValue());
                }
            }
        }else {
            mapObj.put("outpatient",0);
            mapObj.put("hospital",0);
        }
        int total = 0;
        if (mapObj != null && mapObj.size() > 0 ){
            for(String key :mapObj.keySet()){
                total = total + Integer.valueOf(mapObj.get(key).toString());
            }
        }
        mapObj.put("hospital/outpatient",total);

        envelop.setDetailModelList(echartReportModels);
        envelop.setObj(mapObj);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.StasticReport.GetStatisticsDemographicsAgeCount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 年龄段人数统计 -柱状")
    public Envelop getStatisticsDemographicsAgeCount( ) throws Exception {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();

        Page<SystemDictEntry> systemDictEntryList = systemDictEntryService.findByDictId(89, 0, 1000);
        if(systemDictEntryList !=null && systemDictEntryList.getContent() != null){
            List<SystemDictEntry> ageList = systemDictEntryList.getContent();
            Map<String,Integer> ageMap = new LinkedHashMap<>();
            for(SystemDictEntry systemDictEntry:ageList){
                ageMap.put(systemDictEntry.getValue(),0);
            }

            List<Object> list = demographicService.getStatisticsDemographicsAgeCount();
            if( list != null && list.size() > 0){
                EchartReportModel echartReportModel = new EchartReportModel();
                Map<String,Integer> newAgeMap = ageMap;
                for(int i=0 ; i < list.size(); i++) {
                    Map<Integer, Object> mapVal = converMapObject(list.get(i));
                    int val = Integer.valueOf(mapVal.get(0).toString());
                    String age = mapVal.get(1).toString();
                    for(String key :ageMap.keySet()){
                        if(key.equals(age)){
                            newAgeMap.put(key,val);
                            break;
                        }
                    }
                }
                Object [] xObdata = new String[newAgeMap.size()];
                Object [] yObdata = new Integer[newAgeMap.size()];
                String [] xdata = new String[newAgeMap.size()];
                int [] ydata = new int[newAgeMap.size()];
                yObdata = newAgeMap.values().toArray();
                xObdata = newAgeMap.keySet().toArray();
                for(int i=0;i<newAgeMap.size();i++){
                    ydata[i] = Integer.valueOf(yObdata[i].toString());
                    xdata[i] = xObdata[i].toString();
                }
                echartReportModel.setxData(xdata);
                echartReportModel.setyData(ydata);
                echartReportModels.add(echartReportModel);
            }
        }else{
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
        }
        envelop.setDetailModelList(echartReportModels);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    public EchartReportModel setEchartReportModel(List<Object> objectList,String name,List<String> dateList){
        EchartReportModel echartReportModel = new EchartReportModel();
        echartReportModel.setName(name);
        if(dateList != null){
            int num = dateList.size();
            int[] ydata = new int[num];
            String[] xdata = new String[num];
            for(int k=0 ; k < dateList.size() ; k++){
                int val = 0;
                if( objectList != null && objectList.size() > 0) {
                    for(int i=0 ; i < objectList.size() ; i++){
                        Map<Integer,Object> mapVal  = converMapObject(objectList.get(i));
                        if(mapVal.get(1).toString().equals(dateList.get(k))){
                            val = Integer.valueOf(mapVal.get(0).toString());
                            break;
                        }
                    }
                }
                ydata[k] = val;
                xdata[k] = dateList.get(k);
            }
            echartReportModel.setxData(xdata);
            echartReportModel.setyData(ydata);
        }
        return echartReportModel;
    }

    public Map<Integer,Object> converMapObject(Object object){
        Map<Integer,Object> map = new HashMap<>();
        Object[] obj = (Object[]) object;
        for (int i = 0; i < obj.length; i++) {
            if(obj[i] != null){
                map.put(i, obj[i].toString());
            }
        }
        return map;
    }


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
        mapDataModel.setName("已绑定");
        mapDataModel.setValue(String.valueOf(userCardsNum));
        mapData.add(mapDataModel);

        mapDataModel=new MapDataModel();
        mapDataModel.setName("未绑定");
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
                mapData.setName("unIdentify");
                mapData.setValue(unIdentifyCount + "");
            } else if (i == 1) {
                mapData.setName("identify");
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
                mapData.setName("hospital");
                mapData.setValue(inPatientCount + "");
            } else if (i == 1) {
                mapData.setName("patient");
                mapData.setValue(outPatientCount + "");
            } else if (i == 2) {
                mapData.setName("healthExamination");
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
                mapData.setName("todayInWarehouse");
                mapData.setValue(todayInWarehouseCount + "");
            } else if (i == 1) {
                mapData.setName("statistics");
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
    @ApiOperation(value = "按机构Doctorse、Nurse、床位的统计")
    public Envelop getStatisticsDoctorByRoleType() {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        //根据角色/医院获取Doctorse总数
        List<Object> doctorLost=  doctorService.getStatisticsDoctorsByRoleType("Doctor");
        //根据角色/医院获取Nurse总数
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
            echartReportModel.setName("Doctor");
            echartReportModel.setxData(xdata);
            echartReportModel.setyData(ydata);
            echartReportModels.add(echartReportModel);
        }else{
            echartReportModel = new EchartReportModel();
            echartReportModel.setName("Doctor");
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
            echartReportModel.setName("Nurse");
            echartReportModel.setxData(xDataN);
            echartReportModel.setyData(yDataN);
            echartReportModels.add(echartReportModel);
        }else{
            echartReportModel = new EchartReportModel();
            echartReportModel.setName("Nurse");
            echartReportModel.setxData(xDataN);
            echartReportModel.setyData(yDataN);
            echartReportModels.add(echartReportModel);
        }

        envelop.setDetailModelList(echartReportModels);
        envelop.setSuccessFlg(true);
        return envelop;
    }


    @RequestMapping(value =  ServiceApi.StasticReport.getStatisticsCityDoctorByRoleType, method = RequestMethod.GET)
    @ApiOperation(value = "全市Doctorse、Nurse、床位的统计")
    public Envelop getStatisticsCityDoctorByRoleType() {
        Envelop envelop = new Envelop();
        List<EchartReportModel> echartReportModels = new ArrayList<>();
        EchartReportModel echartReportModel = new EchartReportModel();
        //根据角色/医院获取Doctorse总数
        int doctorCount = doctorService.getStatisticsCityDoctorByRoleType("Doctor");
        List<MapDataModel> MapDataModelList=new ArrayList<>();
        MapDataModel mapDataModel=null;
        mapDataModel=new MapDataModel();
        mapDataModel.setName("Doctor");
        mapDataModel.setValue(String.valueOf(doctorCount));
        MapDataModelList.add(mapDataModel);

        int nurseCount = doctorService.getStatisticsCityDoctorByRoleType("Nurse");
        mapDataModel=new MapDataModel();
        mapDataModel.setName("Nurse");
        mapDataModel.setValue(String.valueOf(nurseCount));
        MapDataModelList.add(mapDataModel);
        echartReportModel.setDataModels(MapDataModelList);
        envelop.setObj(echartReportModel);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
