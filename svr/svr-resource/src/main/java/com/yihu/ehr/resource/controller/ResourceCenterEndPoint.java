package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.model.tj.MapDataModel;
import com.yihu.ehr.resource.service.RedisService;
import com.yihu.ehr.resource.service.ResourceCenterService;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.RangeFacet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * EndPoint - 数据资源中心
 * Created by Progr1mmer on 2018/01/05.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ResourceCenterStatisticsEndPoint", description = "数据资源中心首页", tags = {"资源服务-数据资源中心首页"})
public class ResourceCenterEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceCenterService resourceCenterService;
    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private RedisService redisService;

    // ------------------------------- 统计相关 start ------------------------------------

    @RequestMapping(value = ServiceApi.Resources.GetPatientArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 居民建档数")
    public Envelop getPatientArchiveCount() throws Exception{
        BigInteger totalDemographicsNum = resourceCenterService.getDemographicCount();
        return success(totalDemographicsNum);
    }

    @RequestMapping(value = ServiceApi.Resources.GetMedicalResourcesCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 医疗资源建档数")
    public Envelop getMedicalResourcesCount() {
        BigInteger count = resourceCenterService.getMedicalResourcesCount();
        return success(count);
    }

    @RequestMapping(value = ServiceApi.Resources.GetHealthArchiveCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 健康档案建档数")
    public Envelop getHealthArchiveCount() throws Exception {
        long count = solrUtil.count("HealthProfile", "*:*");
        return success(count);
    }

    @RequestMapping(value = ServiceApi.Resources.GetElectronicCasesCount, method = RequestMethod.GET)
    @ApiOperation(value = "顶部栏 - 电子病例建档数")
    public Envelop getElectronicCasesCount() throws Exception{
        long count = solrUtil.count("HealthProfile", "*:*");
        return success(count);
    }

    @RequestMapping(value = ServiceApi.Resources.GetHealthCardBindingAmount, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 健康卡绑定量")
    public Envelop getHealthCardBindingAmount() {
        //获取居民总数
        Map<String, Object> map = new HashedMap();
        map.put("search", null);
        map.put("province", null);
        map.put("city", null);
        map.put("district", null);
        map.put("gender", null);
        map.put("startDate", null);
        map.put("endDate", null);
        BigInteger totalDemographicsNum = resourceCenterService.getDemographicCount();
        //获取绑卡量 userCardsNum
        BigInteger userCardsNum = resourceCenterService.getUseCardCount();
        //计算未绑卡量 nonBindingCardNum、
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        BigInteger nonBindingCardNum = totalDemographicsNum.subtract(userCardsNum);
        EchartReportModel echartReportModel = new EchartReportModel();
        List<MapDataModel> mapData = new ArrayList<>();
        MapDataModel mapDataModel = null;
        mapDataModel = new MapDataModel();
        mapDataModel.setName("已绑定");
        mapDataModel.setValue(String.valueOf(userCardsNum));
        mapData.add(mapDataModel);
        mapDataModel = new MapDataModel();
        mapDataModel.setName("未绑定");
        mapDataModel.setValue(String.valueOf(nonBindingCardNum));
        mapData.add(mapDataModel);
        echartReportModel.setDataModels(mapData);
        List<EchartReportModel> quotaCategories = new ArrayList<>();
        quotaCategories.add(echartReportModel);
        Map<String, String> ma = new HashMap<>();
        ma.put("totalDemographicsNum", String.valueOf(totalDemographicsNum));
        return success(ma, quotaCategories);
    }

    @RequestMapping(value = ServiceApi.Resources.GetInfoDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 信息分布")
    public Envelop getInfoDistribution() throws Exception {
        List<Map> resultList = new ArrayList(1);
        int currentCityId = resourceCenterService.getCurrentCityId();
        List districtList = resourceCenterService.getDistrict(currentCityId);
        List areaIdGroupList = resourceCenterService.getOrgAreaIdGroup(currentCityId);
        Map<Integer, Double> distinctMap = new HashMap();
        for (int i = 0; i < areaIdGroupList.size(); i ++) {
            Object [] dataArr = (Object[]) areaIdGroupList.get(i);
            Integer areaId = (Integer) dataArr[0];
            double count = (double) dataArr[1];
            if (areaId != null) {
                if (distinctMap.containsKey(areaId)) {
                    distinctMap.put(areaId, count + distinctMap.get(areaId));
                } else {
                    distinctMap.put(areaId, count);
                }
            }
        }
        List<String> xData = new ArrayList<>(districtList.size());
        List<Double> yData = new ArrayList<>(districtList.size());
        for (int i = 0; i < districtList.size(); i ++) {
            Object [] dataArr = (Object[]) districtList.get(i);
            Integer areaId = (Integer) dataArr[0];
            String name = (String) dataArr[1];
            xData.add(name);
            if (distinctMap.containsKey(areaId)) {
                yData.add(distinctMap.get(areaId));
            } else {
                yData.add((double)0);
            }
        }
        /*
        for(Integer areaId : distinctMap.keySet()) {
            String areaName = statisticService.getAreaNameById(areaId);
            xData.add(areaName);
            yData.add(distinctMap.get(areaId));
        }*/
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("name", "人口个案信息分布");
        resultMap.put("dataModels", null);
        resultMap.put("xData", xData);
        resultMap.put("yData", yData);
        resultList.add(resultMap);
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.GetNewSituation, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案库 - 新增情况")
    public Envelop getNewSituation() throws Exception {
        List<Map> resultList = new ArrayList(1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = DateUtils.addDays(calendar.getTime(), 1);
        Date before = DateUtils.addDays(now, -30);
        List<RangeFacet> solrList = solrUtil.getFacetDateRange("HealthProfile", "create_date", DateUtils.addHours(before, 8), DateUtils.addHours(now, 8), "+1DAY",  "*:*");
        RangeFacet rangeFacet = solrList.get(0);
        List<RangeFacet.Count> rangeFacetCounts = rangeFacet.getCounts();
        List<String> xData = new ArrayList<>(30);
        List<Integer> yData = new ArrayList<>(30);
        rangeFacetCounts.forEach(item -> {
            xData.add(item.getValue().substring(0, 10));
            yData.add(item.getCount());
        });
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("name", "人口个案新增情况");
        resultMap.put("dataModels", null);
        resultMap.put("xData", xData);
        resultMap.put("yData", yData);
        resultList.add(resultMap);
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.GetOrgArchives, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗机构建档分布")
    public Envelop getOrgArchives() {
        int currentCityId = resourceCenterService.getCurrentCityId();
        List districtList = resourceCenterService.getDistrict(currentCityId);
        List allGroup = resourceCenterService.getOrgAreaNameGroupByClazz(null, currentCityId);
        Map<String, BigInteger> distinctMap = new HashMap();
        for (int i = 0; i < allGroup.size(); i ++) {
            Object [] dataArr = (Object[]) allGroup.get(i);
            String name = (String) dataArr[0];
            BigInteger count = (BigInteger) dataArr[1];
            if (name != null) {
                if (distinctMap.containsKey(name)) {
                    distinctMap.put(name, count.add(distinctMap.get(name)));
                } else {
                    distinctMap.put(name, count);
                }
            }
        }
        List<String> xData = new ArrayList<>(districtList.size());
        List<BigInteger> yData = new ArrayList<>(districtList.size());
        for (int i = 0; i < districtList.size(); i ++) {
            Object [] dataArr = (Object[]) districtList.get(i);
            //Integer areaId = (Integer) dataArr[0];
            String name = (String) dataArr[1];
            xData.add(name);
            if (distinctMap.containsKey(name)) {
                yData.add(distinctMap.get(name));
            } else {
                yData.add(new BigInteger("0"));
            }
        }
        /*for(int i = 0; i < allGroup.size(); i ++) {
            Object [] dataArr = (Object [])allGroup.get(i);
            String name = (String)dataArr[0];
            BigInteger count = (BigInteger) dataArr[1];
            if(!StringUtils.isEmpty(name)) {
                xData.add(name);
                yData.add(count);
            }
        }*/
        List group1 = resourceCenterService.getOrgAreaNameGroupByClazz("1", currentCityId);
        List<String> xData1 = new ArrayList<>(xData.size());
        List<BigInteger> yData1 = new ArrayList<>(xData.size());
        for (int i = 0; i < xData.size(); i ++) {
            boolean isInit = false;
            String xName = xData.get(i);
            for (int j = 0; j < group1.size(); j ++) {
                Object [] dataArr = (Object [])group1.get(j);
                String gName = (String) dataArr[0];
                BigInteger gCount = (BigInteger) dataArr[1];
                if (!StringUtils.isEmpty(gName)) {
                    if (gName.equals(xName)) {
                        xData1.add(gName);
                        yData1.add(gCount);
                        isInit = true;
                    }
                }
            }
            if (!isInit) {
                xData1.add(xName);
                yData1.add(new BigInteger("0"));
            }
        }
        List group2 = resourceCenterService.getOrgAreaNameGroupByClazz("2", currentCityId);
        List<String> xData2 = new ArrayList<>(xData.size());
        List<BigInteger> yData2 = new ArrayList<>(xData.size());
        for (int i = 0; i < xData.size(); i ++) {
            boolean isInit = false;
            String xName = xData.get(i);
            for (int j = 0; j < group2.size(); j ++) {
                Object [] dataArr = (Object [])group2.get(j);
                String gName = (String) dataArr[0];
                BigInteger gCount = (BigInteger) dataArr[1];
                if (!StringUtils.isEmpty(gName)) {
                    if (gName.equals(xName)) {
                        xData2.add(gName);
                        yData2.add(gCount);
                        isInit = true;
                    }
                }
            }
            if (!isInit) {
                xData2.add(xName);
                yData2.add(new BigInteger("0"));
            }
        }
        List group3 = resourceCenterService.getOrgAreaNameGroupByClazz("3", currentCityId);
        List<String> xData3 = new ArrayList<>(xData.size());
        List<BigInteger> yData3 = new ArrayList<>(xData.size());
        for (int i = 0; i < xData.size(); i ++) {
            boolean isInit = false;
            String xName = xData.get(i);
            for (int j = 0; j < group3.size(); j ++) {
                Object [] dataArr = (Object [])group3.get(j);
                String gName = (String) dataArr[0];
                BigInteger gCount = (BigInteger) dataArr[1];
                if (!StringUtils.isEmpty(gName)) {
                    if (gName.equals(xName)) {
                        xData3.add(gName);
                        yData3.add(gCount);
                        isInit = true;
                    }
                }
            }
            if (!isInit) {
                xData3.add(xName);
                yData3.add(new BigInteger("0"));
            }
        }
        List group4 = resourceCenterService.getOrgAreaNameGroupByClazz("4", currentCityId);
        List<String> xData4 = new ArrayList<>(xData.size());
        List<BigInteger> yData4 = new ArrayList<>(xData.size());
        for (int i = 0; i < xData.size(); i ++) {
            boolean isInit = false;
            String xName = xData.get(i);
            for (int j = 0; j < group4.size(); j ++) {
                Object [] dataArr = (Object [])group4.get(j);
                String gName = (String) dataArr[0];
                BigInteger gCount = (BigInteger) dataArr[1];
                if (!StringUtils.isEmpty(gName)) {
                    if (gName.equals(xName)) {
                        xData4.add(gName);
                        yData4.add(gCount);
                        isInit = true;
                    }
                }
            }
            if (!isInit) {
                xData4.add(xName);
                yData4.add(new BigInteger("0"));
            }
        }
        List<Map> eChartReportModels = new ArrayList<>(5);
        Map<String, Object> allMap = new HashMap<>(4);
        allMap.put("name", "医疗机构数量");
        allMap.put("xData", xData);
        allMap.put("yData", yData);
        allMap.put("dataModels", null);
        eChartReportModels.add(allMap);
        Map<String, Object> g1Map = new HashMap<>(4);
        g1Map.put("name", "医院数量");
        g1Map.put("xData", xData1);
        g1Map.put("yData", yData1);
        g1Map.put("dataModels", null);
        eChartReportModels.add(g1Map);
        Map<String, Object> g2Map = new HashMap<>(4);
        g2Map.put("name", "基层医疗机构数量");
        g2Map.put("xData", xData2);
        g2Map.put("yData", yData2);
        g2Map.put("dataModels", null);
        eChartReportModels.add(g2Map);
        Map<String, Object> g3Map = new HashMap<>(4);
        g3Map.put("name", "专业公共卫生机构");
        g3Map.put("xData", xData3);
        g3Map.put("yData", yData3);
        g3Map.put("dataModels", null);
        eChartReportModels.add(g3Map);
        Map<String, Object> g4Map = new HashMap<>(4);
        g4Map.put("name", "其他卫生机构");
        g4Map.put("xData", xData4);
        g4Map.put("yData", yData4);
        g4Map.put("dataModels", null);
        eChartReportModels.add(g4Map);
        return success(eChartReportModels);
    }

    @RequestMapping(value = ServiceApi.Resources.GetMedicalStaffDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医疗人员分布")
    public Envelop getMedicalStaffDistribution() {
        int currentCityId = resourceCenterService.getCurrentCityId();
        List districtList = resourceCenterService.getDistrict(currentCityId);
        List<Map> eChartReportModels = new ArrayList<>(2);
        Map<Integer, BigInteger> distinctMap = new HashMap();
        //医生信息
        List doctorGroup = resourceCenterService.getMedicalAreaCountGroupByRole("Doctor", currentCityId);
        for (int i = 0; i < doctorGroup.size(); i ++) {
            Object [] dataArr = (Object[]) doctorGroup.get(i);
            if (dataArr[0] != null) {
                Integer areaId = (Integer) dataArr[0];
                BigInteger count = (BigInteger) dataArr[1];
                if (areaId != null) {
                    if (distinctMap.containsKey(areaId)) {
                        distinctMap.put(areaId, count.add(distinctMap.get(areaId)));
                    } else {
                        distinctMap.put(areaId, count);
                    }
                }
            }
        }
        List<String> xData = new ArrayList<>(districtList.size());
        List<BigInteger> yData = new ArrayList<>(districtList.size());
        for (int i = 0; i < districtList.size(); i ++) {
            Object [] dataArr = (Object[]) districtList.get(i);
            Integer areaId = (Integer) dataArr[0];
            String name = (String) dataArr[1];
            xData.add(name);
            if (distinctMap.containsKey(areaId)) {
                yData.add(distinctMap.get(areaId));
            } else {
                yData.add(new BigInteger("0"));
            }
        }
        //护士信息
        distinctMap.clear();
        List nurseGroup = resourceCenterService.getMedicalAreaCountGroupByRole("Nurse", currentCityId);
        for (int i = 0; i < nurseGroup.size(); i ++) {
            Object [] dataArr = (Object[]) nurseGroup.get(i);
            if (dataArr[0] != null) {
                Integer areaId = (Integer) dataArr[0];
                BigInteger count = (BigInteger) dataArr[1];
                if (areaId != null) {
                    if (distinctMap.containsKey(areaId)) {
                        distinctMap.put(areaId, count.add(distinctMap.get(areaId)));
                    } else {
                        distinctMap.put(areaId, count);
                    }
                }
            }
        }
        List<String> xData1 = new ArrayList<>(districtList.size());
        List<BigInteger> yData1 = new ArrayList<>(districtList.size());
        for (int i = 0; i < districtList.size(); i ++) {
            Object [] dataArr = (Object[]) districtList.get(i);
            Integer areaId = (Integer) dataArr[0];
            String name = (String) dataArr[1];
            xData1.add(name);
            if (distinctMap.containsKey(areaId)) {
                yData1.add(distinctMap.get(areaId));
            } else {
                yData1.add(new BigInteger("0"));
            }
        }

        Map<String, Object> doctorMap = new HashMap<>(4);
        doctorMap.put("name", "医生");
        doctorMap.put("xData", xData);
        doctorMap.put("yData", yData);
        doctorMap.put("dataModels", null);
        eChartReportModels.add(doctorMap);
        Map<String, Object> nurseMap = new HashMap<>(4);
        nurseMap.put("name", "护士");
        nurseMap.put("xData", xData1);
        nurseMap.put("yData", yData1);
        nurseMap.put("dataModels", null);
        eChartReportModels.add(nurseMap);
        return success(eChartReportModels);
    }

    @RequestMapping(value = ServiceApi.Resources.GetMedicalStaffRatio, method = RequestMethod.GET)
    @ApiOperation(value = "医疗资源库 - 医护人员比例")
    public Envelop getMedicalStaffRatio() {
        EchartReportModel echartReportModel = new EchartReportModel();
        //根据角色/医院获取Doctor总数
        int currentCityId = resourceCenterService.getCurrentCityId();
        BigInteger doctorCount = resourceCenterService.getMedicalCountByRoleType("Doctor", currentCityId);
        List<MapDataModel> MapDataModelList = new ArrayList<>();
        MapDataModel mapDataModel = new MapDataModel();
        mapDataModel.setName("医生");
        mapDataModel.setValue(String.valueOf(doctorCount));
        MapDataModelList.add(mapDataModel);

        BigInteger nurseCount = resourceCenterService.getMedicalCountByRoleType("Nurse", currentCityId);
        mapDataModel = new MapDataModel();
        mapDataModel.setName("护士");
        mapDataModel.setValue(String.valueOf(nurseCount));
        MapDataModelList.add(mapDataModel);
        echartReportModel.setDataModels(MapDataModelList);
        return success(echartReportModel);
    }

    @RequestMapping(value = ServiceApi.Resources.GetCumulativeIntegration, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计整合档案数")
    public Envelop getCumulativeIntegration() throws Exception {
        double count = resourceCenterService.getJsonArchiveCount("3");
        return success(count);
    }

    @RequestMapping(value = ServiceApi.Resources.GteTotallyToBeIntegrated, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 累计待整合档案数")
    public Envelop gteTotallyToBeIntegrated() throws Exception {
        double count = resourceCenterService.getJsonArchiveCount("0");
        return success(count);
    }

    @RequestMapping(value = ServiceApi.Resources.GetArchiveSource, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 档案来源分布情况")
    public Envelop getArchiveSource() throws Exception {
        long clinic = solrUtil.count("HealthProfile", "event_type:0");
        long resident = solrUtil.count("HealthProfile", "event_type:1");
        long medicalExam = solrUtil.count("HealthProfile", "event_type:2");
        List<Map> eChartReportModelList = new ArrayList<>(3);
        Map<String, Object> clinicMap = new HashMap<>(2);
        clinicMap.put("name", "门诊");
        clinicMap.put("value", clinic);
        eChartReportModelList.add(clinicMap);
        Map<String, Object> residentMap = new HashMap<>(2);
        residentMap.put("name", "住院");
        residentMap.put("value", resident);
        eChartReportModelList.add(residentMap);
        Map<String, Object> medicalExamMap = new HashMap<>(2);
        medicalExamMap.put("name", "体检");
        medicalExamMap.put("value", medicalExam);
        eChartReportModelList.add(medicalExamMap);
        List<Map> resultList = new ArrayList<>(1);
        Map<String, Object> dataMap = new HashMap<>(4);
        dataMap.put("name", null);
        dataMap.put("xData", null);
        dataMap.put("yData", null);
        dataMap.put("dataModels", eChartReportModelList);
        resultList.add(dataMap);
        Map<String, Long> resultMap = new HashMap<>(4);
        resultMap.put("clinic", clinic);
        resultMap.put("resident", resident);
        resultMap.put("medicalExam", medicalExam);
        resultMap.put("total", clinic + resident + medicalExam);
        return success(resultMap, resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.GetArchiveDistribution, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案分布情况")
    public Envelop getArchiveDistribution() {
        //获取年龄字典
        /**
        List<SystemDictEntry> ageDictEntryList = statisticService.getSystemDictEntry((long)89);
        if (ageDictEntryList != null) {
            Map<String, Integer> ageMap = new LinkedHashMap<>();
            for (SystemDictEntry systemDictEntry : ageDictEntryList) {
                ageMap.put(systemDictEntry.getValue(), 0);
            }
            List<Object []> allGroup = statisticService.newStatisticsDemographicsAgeCount();
            Map<String, BigInteger> maleGroup = new HashMap<>(ageMap.size());
            Map<String, BigInteger> femaleGroup = new HashMap<>(ageMap.size());
            for(Object [] dataArr : allGroup) {
                String gender = (String) dataArr[2];
                String age = (String) dataArr[1];
                BigInteger count = (BigInteger) dataArr[0];
                if(gender.equals("1")) {
                    maleGroup.put(age, count);
                }else {
                    femaleGroup.put(age, count);
                }
            }
            List<String> xData = new ArrayList<>(ageMap.size());
            List<BigInteger> yData = new ArrayList<>(ageMap.size());
            List<String> xData1 = new ArrayList<>(ageMap.size());
            List<BigInteger> yData1 = new ArrayList<>(ageMap.size());
            for(String key : ageMap.keySet()) {
                xData.add(key);
                xData1.add(key);
                if(maleGroup.containsKey(key)) {
                    yData.add(maleGroup.get(key));
                }else {
                    yData.add(new BigInteger("0"));
                }
                if(femaleGroup.containsKey(key)) {
                    yData1.add(femaleGroup.get(key));
                }else {
                    yData1.add(new BigInteger("0"));
                }
            }
            List<Map> resultList = new ArrayList<>(2);
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("name", "男");
            resultMap.put("dataModels", null);
            resultMap.put("xData", xData);
            resultMap.put("yData", yData);
            resultList.add(resultMap);
            Map<String, Object> resultMap1 = new HashMap<>(4);
            resultMap1.put("name", "女");
            resultMap1.put("dataModels", null);
            resultMap1.put("xData", xData1);
            resultMap1.put("yData", yData1);
            resultList.add(resultMap1);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
            return envelop;
        } else { //年龄字典为空的时候
            Map<String, Integer> ageMap = getDefaultAgeMap();
            List<Object []> allGroup = statisticService.newStatisticsDemographicsAgeCount();
            Map<String, BigInteger> maleGroup = new HashMap<>();
            Map<String, BigInteger> femaleGroup = new HashMap<>();
            for(Object [] dataArr : allGroup) {
                String gender = (String) dataArr[2];
                String age = (String) dataArr[1];
                BigInteger count = (BigInteger) dataArr[0];
                if(gender.equals("1")) {
                    maleGroup.put(age, count);
                }else {
                    femaleGroup.put(age, count);
                }
            }
            List<String> xData = new ArrayList<>();
            List<BigInteger> yData = new ArrayList<>();
            List<String> xData1 = new ArrayList<>();
            List<BigInteger> yData1 = new ArrayList<>();
            for(String key : ageMap.keySet()) {
                xData.add(key);
                xData1.add(key);
                if(maleGroup.containsKey(key)) {
                    yData.add(maleGroup.get(key));
                }else {
                    yData.add(new BigInteger("0"));
                }
                if(femaleGroup.containsKey(key)) {
                    yData1.add(femaleGroup.get(key));
                }else {
                    yData1.add(new BigInteger("0"));
                }
            }
            List<Map> resultList = new ArrayList<>(2);
            Map<String, Object> resultMap = new HashMap<>(4);
            resultMap.put("name", "男");
            resultMap.put("dataModels", null);
            resultMap.put("xData", xData);
            resultMap.put("yData", yData);
            resultList.add(resultMap);
            Map<String, Object> resultMap1 = new HashMap<>(4);
            resultMap1.put("name", "女");
            resultMap1.put("dataModels", null);
            resultMap1.put("xData", xData1);
            resultMap1.put("yData", yData1);
            resultList.add(resultMap1);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(resultList);
            return envelop;
        }*/
        Map<String, Integer> ageMap = getDefaultAgeMap();
        List<Object []> allGroup = resourceCenterService.newStatisticsDemographicsAgeCount();
        Map<String, BigInteger> maleGroup = new HashMap<>();
        Map<String, BigInteger> femaleGroup = new HashMap<>();
        for (Object [] dataArr : allGroup) {
            String gender = (String) dataArr[2];
            String age = (String) dataArr[1];
            BigInteger count = (BigInteger) dataArr[0];
            if (gender.equals("1")) {
                maleGroup.put(age, count);
            } else {
                femaleGroup.put(age, count);
            }
        }
        List<String> xData = new ArrayList<>();
        List<BigInteger> yData = new ArrayList<>();
        List<String> xData1 = new ArrayList<>();
        List<BigInteger> yData1 = new ArrayList<>();
        for (String key : ageMap.keySet()) {
            xData.add(key);
            xData1.add(key);
            if (maleGroup.containsKey(key)) {
                yData.add(maleGroup.get(key));
            } else {
                yData.add(new BigInteger("0"));
            }
            if (femaleGroup.containsKey(key)) {
                yData1.add(femaleGroup.get(key));
            } else {
                yData1.add(new BigInteger("0"));
            }
        }
        List<Map> resultList = new ArrayList<>(2);
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("name", "男");
        resultMap.put("dataModels", null);
        resultMap.put("xData", xData);
        resultMap.put("yData", yData);
        resultList.add(resultMap);
        Map<String, Object> resultMap1 = new HashMap<>(4);
        resultMap1.put("name", "女");
        resultMap1.put("dataModels", null);
        resultMap1.put("xData", xData1);
        resultMap1.put("yData", yData1);
        resultList.add(resultMap1);
        return success(resultList);
    }

    private  Map<String, Integer> getDefaultAgeMap() {
        Map<String, Integer> map = new LinkedHashMap<>(5);
        map.put("0-6", 0);
        map.put("7-17", 0);
        map.put("18-40", 0);
        map.put("41-65", 0);
        map.put("> 65", 0);
        return map;
    }

    @RequestMapping(value = ServiceApi.Resources.GetStorageAnalysis, method = RequestMethod.GET)
    @ApiOperation(value = "健康档案 - 健康档案入库情况分析")
    public Envelop getStorageAnalysis() throws Exception {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = DateUtils.addDays(calendar.getTime(), 1);
        Date before = DateUtils.addDays(now, -29);
        Map<String, Long> receiveGroup = resourceCenterService.getJsonArchiveReceiveDateGroup(before, now);
        Map<String, Long> finishGroup = resourceCenterService.getJsonArchiveFinishDateGroup(before, now);
        List<String> xData = new ArrayList<>(30);
        List<Long> yData = new ArrayList<>(30);
        List<String> xData1 = new ArrayList<>(30);
        List<Long> yData1 = new ArrayList<>(30);
        for (int i = 30; i >= 1; i --) {
            String _day = dateFormat.format(DateUtils.addDays(now, -i));
            //采集量
            xData.add(_day);
            if (receiveGroup.containsKey(_day)) {
                yData.add(receiveGroup.get(_day));
            } else {
                yData.add(0L);
            }
            //入库量
            xData1.add(_day);
            if (finishGroup.containsKey(_day)) {
                yData1.add(finishGroup.get(_day));
            } else {
                yData1.add(0L);
            }
        }
        List<Map> resultList = new ArrayList<>(2);
        Map<String, Object> resultMap = new HashMap<>(4);
        resultMap.put("name", "采集量");
        resultMap.put("dataModels", null);
        resultMap.put("xData", xData);
        resultMap.put("yData", yData);
        resultList.add(resultMap);
        Map<String, Object> resultMap1 = new HashMap<>(4);
        resultMap1.put("name", "入库量");
        resultMap1.put("dataModels", null);
        resultMap1.put("xData", xData1);
        resultMap1.put("yData", yData1);
        resultList.add(resultMap1);
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.GetElectronicMedicalSource, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病例来源分布情况")
    public Envelop getElectronicMedicalSource() throws Exception {
        long clinic = solrUtil.count("HealthProfile", "event_type:0");
        Map<String, Object> clinicMap = new HashedMap();
        clinicMap.put("name", "门诊");
        clinicMap.put("value", clinic);
        long resident = solrUtil.count("HealthProfile", "event_type:1");
        Map<String, Object> residentMap = new HashedMap();
        residentMap.put("name", "住院");
        residentMap.put("value", resident);
        long medicalExam = solrUtil.count("HealthProfile", "event_type:2");
        Map<String, Object> medicalExamMap = new HashedMap();
        medicalExamMap.put("name", "体检");
        medicalExamMap.put("value", medicalExam);
        List<Map> dataModels = new ArrayList<>();
        dataModels.add(clinicMap);
        dataModels.add(residentMap);
        dataModels.add(medicalExamMap);
        List<Map> resultList = new ArrayList<>();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("name", null);
        dataMap.put("xData", null);
        dataMap.put("yData", null);
        dataMap.put("dataModels", dataModels);
        resultList.add(dataMap);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("门诊/住院/体检", clinic + resident + medicalExam);
        resultMap.put("门诊", clinic);
        resultMap.put("住院", resident);
        resultMap.put("体检", medicalExam);
        return success(resultMap, resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.GetElectronicMedicalOrgDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集医院分布")
    public Envelop getElectronicMedicalOrgDistributed() throws Exception {
        int currentCityId = resourceCenterService.getCurrentCityId();
        FacetField facetField = solrUtil.getFacetField("HealthProfile", "org_code", null, 0, 0, 1000000, false);
        List<FacetField.Count> countList = facetField.getValues();
        Map<String, Long> dataMap = new HashMap<>(countList.size());
        for (FacetField.Count count : countList) {
            String orgCode = count.getName();
            Integer areaId = resourceCenterService.getOrgAreaByCode(orgCode, currentCityId);
            if (areaId != null && areaId != 0) {
                String orgName = resourceCenterService.getOrgNameByCode(orgCode);
                if (!StringUtils.isEmpty(orgName)) {
                    long count1 = count.getCount();
                    dataMap.put(orgName, count1);
                }
            }
        }
        return success(dataMap);
    }

    @RequestMapping(value = ServiceApi.Resources.GetElectronicMedicalDeptDistributed, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集科室分布")
    public Envelop getElectronicMedicalDeptDistributed() throws Exception{
        Envelop envelop = new Envelop();
        Envelop orgEnvelop = getElectronicMedicalOrgDistributed();
        if (orgEnvelop.isSuccessFlg() ) {
            Map result1 = (Map)orgEnvelop.getObj();
            if (result1.size() <= 0) {
                envelop.setSuccessFlg(true);
                envelop.setObj(new HashMap<>());
                return envelop;
            }
        } else {
            envelop.setSuccessFlg(false);
            envelop.setErrorCode(orgEnvelop.getErrorCode());
            envelop.setErrorMsg(orgEnvelop.getErrorMsg());
            envelop.setObj(new HashMap<>());
            return envelop;
        }
        FacetField facetField = solrUtil.getFacetField("HealthProfile", "EHR_000081", null, 0, 0, 1000000, false);
        List<FacetField.Count> countList = facetField.getValues();
        Map<String, Long> dataMap = new HashMap<>(countList.size());
        for (FacetField.Count count : countList) {
            String deptCode = count.getName();
            String deptName = redisService.getDictEntryValue("59083976eebd", "253", deptCode);
            if (!StringUtils.isEmpty(deptName)) {
                long count1 = count.getCount();
                dataMap.put(deptName, count1);
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(dataMap);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.GetElectronicMedicalAcquisitionSituation, method = RequestMethod.GET)
    @ApiOperation(value = "电子病例 - 电子病历采集采集情况")
    public Envelop getElectronicMedicalAcquisitionSituation() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date now = DateUtils.addDays(calendar.getTime(), 1);
        Date before = DateUtils.addDays(now, -30);
        List<RangeFacet> clinicList = solrUtil.getFacetDateRange("HealthProfile", "create_date", DateUtils.addHours(before, 8), DateUtils.addHours(now, 8), "+1DAY", "event_type:0");
        RangeFacet clinicRangeFacet = clinicList.get(0);
        List<RangeFacet.Count> clinicCount = clinicRangeFacet.getCounts();
        Map<String, Integer> dataMap1 = new LinkedHashMap<>(clinicCount.size());
        for (RangeFacet.Count count : clinicCount) {
            dataMap1.put(count.getValue().substring(0, 10), count.getCount());
        }
        List<RangeFacet> residentList = solrUtil.getFacetDateRange("HealthProfile", "create_date", DateUtils.addHours(before, 8), DateUtils.addHours(now, 8), "+1DAY", "event_type:1");
        RangeFacet residentRangeFacet = residentList.get(0);
        List<RangeFacet.Count> residentCount = residentRangeFacet.getCounts();
        Map<String, Integer> dataMap2 = new LinkedHashMap<>(residentCount.size());
        for (RangeFacet.Count count : residentCount) {
            dataMap2.put(count.getValue().substring(0, 10), count.getCount());
        }
        List<Map> resultList = new ArrayList<>(2);
        resultList.add(dataMap1);
        resultList.add(dataMap2);
        return success(resultList);
    }
    // ------------------------------- 统计相关 end ------------------------------------

    // ------------------------------- 大数据展示相关 start ------------------------------------
    @RequestMapping(value = ServiceApi.Resources.Achievements, method = RequestMethod.GET)
    @ApiOperation(value = "成果展示")
    public Envelop achievements() throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //数据采集
        List<Object[]> list1 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("EHR_Quality_Trend", "zkGuSIm2Fg");
        if (list1.size() > 0) {
            Map<String, Object> dataMap1 = new LinkedHashMap<>();
            Object [] data = list1.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap1.put("label", "数据采集");
            dataMap1.put("appId", "zkGuSIm2Fg");
            dataMap1.put("name", name);
            dataMap1.put("menuId", id);
            double total = resourceCenterService.getJsonArchiveTotalCount();
            dataMap1.put("total", total);
            Map<String, Object> dataMap2 = new HashMap<>();
            List<Map<String, Object>> pieList1 = new ArrayList<>();
            double count1 = resourceCenterService.getJsonArchiveCount("3");
            Map<String, Object> pieMap1 = new HashMap<>();
            pieMap1.put("成功总量", count1);
            pieList1.add(pieMap1);
            double count2 = resourceCenterService.getJsonArchiveCount("2");
            Map<String, Object> pieMap2 = new HashMap<>();
            pieMap2.put("失败总量", count2);
            pieList1.add(pieMap2);
            dataMap2.put("1", pieList1);
            dataMap1.put("view", dataMap2);
            resultList.add(dataMap1);
        }
        //数据存储
        List<Object[]> list2 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Ehr_Data_Gateway", "R1yHNdX5Ud");
        if (list2.size() > 0) {
            Map<String, Object> dataMap1 = new LinkedHashMap<>();
            Object [] data = list2.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap1.put("label", "数据存储");
            dataMap1.put("appId", "R1yHNdX5Ud");
            dataMap1.put("name", name);
            dataMap1.put("menuId", id);
            Map<String, Object> dataMap2 = new HashMap<>();
            List<Map<String, Object>> pieList1 = new ArrayList<>();
            long count1 = solrUtil.count("HealthProfile", "*:*");
            Map<String, Object> pieMap1 = new HashMap<>();
            pieMap1.put("patient", count1);
            pieList1.add(pieMap1);
            BigInteger count2 = resourceCenterService.getMedicalResourcesCount();
            Map<String, Object> pieMap2 = new HashMap<>();
            pieMap2.put("medicalResources", count2);
            pieList1.add(pieMap2);
            double count3 = resourceCenterService.getJsonArchiveCount("3");
            Map<String, Object> pieMap3 = new HashMap<>();
            pieMap3.put("healthArchive", count3);
            pieList1.add(pieMap3);
            long count4 = solrUtil.count("HealthProfile", "*:*");
            Map<String, Object> pieMap4 = new HashMap<>();
            pieMap4.put("electronicCases", count4);
            pieList1.add(pieMap4);
            dataMap2.put("1", pieList1);
            dataMap1.put("view", dataMap2);
            resultList.add(dataMap1);
        }
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.Visualization, method = RequestMethod.GET)
    @ApiOperation(value = "可视化")
    public Envelop visualization() throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //已配置视图
        List<Object[]> list1 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Resource_ResourceManage_Initial", "R1yHNdX5Ud");
        if (list1.size() > 0) {
            Map<String, Object> dataMap1 = new LinkedHashMap<>();
            Object [] data = list1.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap1.put("label", "已配置视图");
            dataMap1.put("appId", "R1yHNdX5Ud");
            dataMap1.put("name", name);
            dataMap1.put("menuId", id);
            BigInteger total = resourceCenterService.getTotalViewCount(null);
            dataMap1.put("total", total);
            //大分类
            Map<String, Object> dataMap2 = new HashMap<>();
            List<Map<String, Object>> pieList1 = new ArrayList<>();
            BigInteger dataSource1 = resourceCenterService.getTotalViewCount(1);
            Map<String, Object> pieMap1 = new LinkedHashMap<>();
            pieMap1.put("档案数据", dataSource1);
            pieList1.add(pieMap1);
            BigInteger dataSource2 = resourceCenterService.getTotalViewCount(2);
            Map<String, Object> pieMap2 = new LinkedHashMap<>();
            pieMap2.put("指标统计", dataSource2);
            pieList1.add(pieMap2);
            dataMap2.put("1", pieList1);
            //小分类
            List<Map<String, Object>> pieList2 = new ArrayList<>();
            List<Object[]> cateList = resourceCenterService.getResourceCategoryIdAndNameList();
            for (Object [] temp : cateList) {
                Map<String, Object> pieMap3 = new HashMap<>();
                String cateId = (String) temp[0];
                String cateName = (String) temp[1];
                BigInteger count1 = resourceCenterService.countResourceByResourceCateIdAndDataSource(cateId, 1);
                if (count1.compareTo(new BigInteger("0")) > 0) {
                    pieMap3.put(cateName, count1);
                    pieList2.add(pieMap3);
                }
            }
            for (Object [] temp : cateList) {
                Map<String, Object> pieMap3 = new HashMap<>();
                String cateId = (String) temp[0];
                String cateName = (String) temp[1];
                BigInteger count1 = resourceCenterService.countResourceByResourceCateIdAndDataSource(cateId, 2);
                if (count1.compareTo(new BigInteger("0")) > 0) {
                    pieMap3.put(cateName, count1);
                    pieList2.add(pieMap3);
                }
            }
            dataMap2.put("2", pieList2);
            dataMap1.put("view", dataMap2);
            resultList.add(dataMap1);
        }
        //已配置资源报表
        List<Object[]> list2 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Ehr_Resource_Report_Index", "R1yHNdX5Ud");
        if (list2.size() > 0) {
            Map<String, Object> dataMap1 = new LinkedHashMap<>();
            Object [] data = list2.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap1.put("label", "已配置资源报表");
            dataMap1.put("appId", "R1yHNdX5Ud");
            dataMap1.put("name", name);
            dataMap1.put("menuId", id);
            BigInteger total = resourceCenterService.getTotalReportCount();
            dataMap1.put("total", total);
            Map<String, Object> dataMap2 = new HashMap<>();
            List<Map<String, Object>> pieList1 = new ArrayList<>();
            List<Object[]> cateList = resourceCenterService.getReportCategoryIdAndNameList();
            for (Object [] temp : cateList) {
                Map<String, Object> pieMap3 = new HashMap<>();
                Integer cateId = (Integer) temp[0];
                String cateName = (String) temp[1];
                BigInteger count1 = resourceCenterService.countReportByReportCateId(cateId);
                if (count1.compareTo(new BigInteger("0")) > 0) {
                    pieMap3.put(cateName, count1);
                    pieList1.add(pieMap3);
                }
            }
            dataMap2.put("1", pieList1);
            dataMap1.put("view", dataMap2);
            resultList.add(dataMap1);
        }
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.DataAnalysis, method = RequestMethod.GET)
    @ApiOperation(value = "数据分析")
    public Envelop dataAnalysis() throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //已注册指标
        List<Object[]> list1 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Ehr_Zhibiao_Initial", "R1yHNdX5Ud");
        if (list1.size() > 0) {
            Map<String, Object> dataMap1 = new LinkedHashMap<>();
            Object [] data = list1.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap1.put("label", "已注册指标");
            dataMap1.put("appId", "R1yHNdX5Ud");
            dataMap1.put("name", name);
            dataMap1.put("menuId", id);
            BigInteger total = resourceCenterService.getTotalQuotaCount();
            dataMap1.put("total", total);
            Map<String, Object> dataMap2 = new HashMap<>();
            List<Map<String, Object>> pieList1 = new ArrayList<>();
            List<Object[]> cateList = resourceCenterService.getQuotaCategoryIdAndNameList();
            for (Object [] temp : cateList) {
                Map<String, Object> pieMap3 = new HashMap<>();
                Integer cateId = (Integer) temp[0];
                String cateName = (String) temp[1];
                BigInteger count1 = resourceCenterService.countQuotaByQuotaCateId(cateId);
                if (count1.compareTo(new BigInteger("0")) > 0) {
                    pieMap3.put(cateName, count1);
                    pieList1.add(pieMap3);
                }
            }
            dataMap2.put("1", pieList1);
            dataMap1.put("view", dataMap2);
            resultList.add(dataMap1);
        }
        return success(resultList);
    }

    @RequestMapping(value = ServiceApi.Resources.HierarchicalManagement, method = RequestMethod.GET)
    @ApiOperation(value = "分级管理")
    public Envelop hierarchicalManagement() throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //机构数据授权
        List<Object[]> list1 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Ehr_Security_Org_Auth", "zkGuSIm2Fg");
        if (list1.size() > 0) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            Object [] data = list1.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap.put("label", "机构数据授权");
            dataMap.put("appId", "zkGuSIm2Fg");
            dataMap.put("name", name);
            dataMap.put("menuId", id);
            resultList.add(dataMap);
        }
        //应用授权
        List<Object[]> list2 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("Ehr_Public_Thirdparty_App", "zkGuSIm2Fg");
        if (list2.size() > 0) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            Object [] data = list2.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap.put("label", "应用授权");
            dataMap.put("appId", "zkGuSIm2Fg");
            dataMap.put("name", name);
            dataMap.put("menuId", id);
            resultList.add(dataMap);
        }
        //角色授权
        List<Object[]> list3 = resourceCenterService.findAppFeatureIdAndNameByAppIdAndCode("UserRoles_Initial", "zkGuSIm2Fg");
        if (list3.size() > 0) {
            Map<String, Object> dataMap = new LinkedHashMap<>();
            Object [] data = list3.get(0);
            Integer id = (Integer) data[0];
            String name = (String) data[1];
            dataMap.put("label", "角色授权");
            dataMap.put("appId", "zkGuSIm2Fg");
            dataMap.put("name", name);
            dataMap.put("menuId", id);
            resultList.add(dataMap);
        }
        return success(resultList);
    }

    // ------------------------------- 大数据展示相关 end ------------------------------------
}
