package com.yihu.quota.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.singledisease.SingleDiseaseService;
import com.yihu.quota.service.singledisease.SingleDiseaseServiceNew;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2018/4/4.
 */
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "单病种报表统计 - 糖尿病")
public class SingleDiseasesController {

    @Autowired
    private SingleDiseaseService singleDiseaseService;
    @Autowired
    private SingleDiseaseServiceNew singleDiseaseServiceNew;

    public static String orgHealthCategoryCode = "orgHealthCategoryCode";

    @RequestMapping(value = ServiceApi.TJ.GetHeatMapByQuotaCode, method = RequestMethod.GET)
    @ApiOperation(value = "热力图")
    public Envelop getHeatMap() throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String,String>> heatMapPoint = singleDiseaseService.getHeatMap();
        envelop.setSuccessFlg(true);
        if (null != heatMapPoint && heatMapPoint.size() > 0) {
            envelop.setDetailModelList(heatMapPoint);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetNumberOfDiabetes, method = RequestMethod.GET)
    @ApiOperation(value = "糖尿病患者数")
    public Envelop getNumberOfDiabetes() throws Exception {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> numberOfDiabetes = singleDiseaseService.getNumberOfDiabetes();
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(numberOfDiabetes);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetPieData, method = RequestMethod.GET)
    @ApiOperation(value = "获取饼图数据")
    public Envelop getPieData(
            @ApiParam(name = "type", value = "类型")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code") String code) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, Object> pieDataInfo = singleDiseaseService.getPieDataInfo(type, code);
        envelop.setSuccessFlg(true);
        if (null != pieDataInfo && pieDataInfo.size() > 0) {
            envelop.setObj(pieDataInfo.get("legendData"));
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) pieDataInfo.get("seriesData");
            envelop.setDetailModelList(seriesData);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetLineData, method = RequestMethod.GET)
    @ApiOperation(value = "获取折线图数据")
    public Envelop getLineData() throws Exception {
        Envelop envelop = new Envelop();
        Map<String, List<String>> map = singleDiseaseService.getLineDataInfo();
        envelop.setSuccessFlg(true);
        if (null != map && map.size() > 0) {
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TJ.GetBarData, method = RequestMethod.GET)
    @ApiOperation(value = "获取柱状图数据")
    public Envelop getBarData(
            @ApiParam(name = "type", value = "类型 1并发症 2用药患者数 3空腹血糖统计 4糖耐量")
            @RequestParam(value = "type") String type) throws Exception {
        Envelop envelop = new Envelop();
        Map<String, List<String>> map = null;
        if ("1".equals(type) || "2".equals(type)) {
            if ("1".equals(type)) {
                map = singleDiseaseService.getSymptomDataInfo();
            } else {
                map = singleDiseaseService.getMedicineDataInfo();
            }
            if (null != map && map.size() > 0) {
                envelop.setDetailModelList(map.get("valueData"));
                envelop.setObj(map.get("xData"));
            }
        } else if ("3".equals(type) || "4".equals(type)){
            if ("3".equals(type)) {
                map = singleDiseaseService.getFastingBloodGlucoseDataInfo();
            } else {
                map = singleDiseaseService.getSugarToleranceDataInfo();
            }
//            if (null != map && map.size() > 0) {
//                List<Map<String, Object>> list = new ArrayList<>();
//                Map<String, Object> myMap = new HashMap<>();
//                myMap.put("男", map.get("valueData1"));
//                myMap.put("女", map.get("valueData2"));
//                list.add(myMap);
//                envelop.setDetailModelList(list);
//                envelop.setObj(map.get("xData"));
//            }

            if (null != map && map.size() > 0) {
                envelop.setDetailModelList(map.get("valueData"));
                envelop.setObj(map.get("xData"));
            }
        }
        envelop.setSuccessFlg(true);

        return envelop;
    }


    @RequestMapping(value = ServiceApi.SingleDisease.GetDropdownList, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症和药品查询下拉列表前十 数据")
    public Envelop getDropdownList(
            @ApiParam(name = "type", value = "类型 1并发症 2 药品")
            @RequestParam(value = "type" ,required =  true ) String type) throws Exception {
        Envelop envelop = new Envelop();
        String sql = "";
        Map<String, List<String>> map = null;
        if ("1".equals(type)) {
            sql = "select symptomName, count(*) count from single_disease_check_index where checkCode = 'CH001' group by symptomName order by count desc";
            map = singleDiseaseServiceNew.getSymptomDataInfo(sql,"symptomName");
        } else {
            sql = "select medicineName, count(*) count from single_disease_check_index where checkCode = 'CH004' group by medicineName order by count desc";
            map = singleDiseaseServiceNew.getMedicineDataInfo(sql, "medicineName");
        }
        if (null != map && map.size() > 0) {
            envelop.setDetailModelList(map.get("valueData").subList(0,9));
        }
        return  envelop;
    }


    @RequestMapping(value = ServiceApi.SingleDisease.GetSymptomDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症详细查询页 数据")
    public Envelop getSymptomDetailData(
            @ApiParam(name = "name", value = "并发症名称")
            @RequestParam(value = "name" ,required = false ,defaultValue = "") String name) throws Exception {
        Envelop envelop = new Envelop();
        String sql = "";
        if(StringUtils.isNotEmpty(name)) {
            name = "symptomName = " + name;
            sql = "select count(*) count from single_disease_check_index where checkCode = 'CH001' " + name + " group by date_histogram(field='eventDate','interval'='year')";
        }else {
            sql = "select count(*) count from single_disease_check_index where checkCode = 'CH001'  group by date_histogram(field='eventDate','interval'='year')";
        }
        Map<String, List<String>>  map = singleDiseaseServiceNew.getSymptomDataInfo(sql,"date_histogram(field=eventDate,interval=year)");
        if (null != map && map.size() > 0) {
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
        }

        return  envelop;
    }

    @RequestMapping(value = ServiceApi.SingleDisease.GetMedicineDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取药品详细查询页 数据")
    public Envelop getMedicineDetailData(
            @ApiParam(name = "name", value = "药品名称")
            @RequestParam(value = "name" ,required =  false, defaultValue = "") String name) throws Exception {
        Envelop envelop = new Envelop();
        String sql = "";
        if(StringUtils.isNotEmpty(name)){
            name = "medicineName = " + name;
            sql = "select count(*) count from single_disease_check_index where checkCode = 'CH004' "+ name+ " group by medicineName order by count desc";
        }else {
            sql = "select count(*) count from single_disease_check_index where checkCode = 'CH004' group by medicineName order by count desc";
        }
        Map<String, List<String>> map = singleDiseaseServiceNew.getMedicineDataInfo(sql, "date_histogram(field=eventDate,interval=year)");
        if (null != map && map.size() > 0) {
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
        }

        return  envelop;
    }

}
