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
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by janseny on 2018/4/4.
 */

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "单病种报表统计 - 糖尿病")
public class SingleDiseasesController {

    @Autowired
    private SingleDiseaseService singleDiseaseService;
    @Autowired
    private SingleDiseaseServiceNew singleDiseaseServiceNew;

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
            map = singleDiseaseServiceNew.getDataInfo(sql, "symptomName");
        } else {
            sql = "select medicineName, count(*) count from single_disease_check_index where checkCode = 'CH004' group by medicineName order by count desc";
            map = singleDiseaseServiceNew.getDataInfo(sql, "medicineName");
        }
        if (null != map && map.size() > 0) {
            if(map.get("xData").size()>9){
                envelop.setDetailModelList(map.get("xData").subList(0,10));
            }else {
                envelop.setDetailModelList(map.get("xData"));
            }
        }
        envelop.setSuccessFlg(true);
        return  envelop;
    }


    @RequestMapping(value = ServiceApi.SingleDisease.GetSymptomDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取并发症详细查询页 数据")
    public Object getSymptomDetailData(
            @ApiParam(name = "name", value = "并发症名称")
            @RequestParam(value = "name" ,required = false ,defaultValue = "") String name) throws Exception {
        Map resultMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) + 1;
        String range = "range(birthYear," + (year - 151) + "," + (year - 66) + "," + (year - 41) + "," + (year - 18) + "," + (year - 7) + "," + year + ")";
        String yearSql = "";
        String typeSql = "";
        String ageSql = "";
        String sexSql = "";
        if(StringUtils.isNotEmpty(name)) {
            name = " and symptomName = '" + name + "'";
            yearSql = "select count(*) count from single_disease_check_index where checkCode = 'CH001' " + name + " group by date_histogram(field='eventDate','interval'='year')";
            typeSql = "select diseaseTypeName,count(*) count from single_disease_check_index where checkCode = 'CH001' " + name + " group by diseaseTypeName";
            ageSql = "select count(*) from single_disease_check_index where checkCode = 'CH001' and birthYear <> 0 " + name + " group by " + range;
            sexSql = "select sexName, count(*) count from single_disease_check_index where checkCode = 'CH001' " + name + " group by sexName";
        }else {
            yearSql = "select count(*) count from single_disease_check_index where checkCode = 'CH001' group by date_histogram(field='eventDate','interval'='year')";
            typeSql = "select diseaseTypeName,count(*) count from single_disease_check_index where checkCode = 'CH001' group by diseaseTypeName";
            ageSql = "select count(*) from single_disease_check_index where checkCode = 'CH001'and birthYear <> 0  group by " + range;
            sexSql = "select sexName, count(*) count from single_disease_check_index where checkCode = 'CH001' group by sexName";
        }
        //按年趋势 柱状图
        Map<String, List<String>>  map = singleDiseaseServiceNew.getDataInfo(yearSql,"date_histogram(field=eventDate,interval=year)");
        if (null != map && map.size() > 0) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
            resultMap.put("year",envelop);
        }
        //按糖尿病类型 饼图
        Map<String, Object> diseaseTypeDataInfo = singleDiseaseServiceNew.getPieDataInfoBySql("1", typeSql, "diseaseTypeName");
        if (null != diseaseTypeDataInfo && diseaseTypeDataInfo.size() > 0) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setObj(diseaseTypeDataInfo.get("legendData"));
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) diseaseTypeDataInfo.get("seriesData");
            envelop.setDetailModelList(seriesData);
            resultMap.put("type",envelop);
        }
        //按年龄段 饼图
        Map<String, Object> ageDataInfo = singleDiseaseServiceNew.getPieDataInfoBySql("2", ageSql, "birthYear");
        if (null != ageDataInfo && ageDataInfo.size() > 0) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setObj(ageDataInfo.get("legendData"));
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) ageDataInfo.get("seriesData");
            envelop.setDetailModelList(seriesData);
            resultMap.put("age",envelop);
        }
        //按性别饼图
        Map<String, Object> sexDataInfo = singleDiseaseServiceNew.getPieDataInfoBySql("3", sexSql, "sexName");
        if (null != sexDataInfo && sexDataInfo.size() > 0) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setObj(sexDataInfo.get("legendData"));
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) sexDataInfo.get("seriesData");
            envelop.setDetailModelList(seriesData);
            resultMap.put("sex",envelop);
        }
        return  resultMap;
    }

    @RequestMapping(value = ServiceApi.SingleDisease.GetMedicineDetailData, method = RequestMethod.GET)
    @ApiOperation(value = "获取药品详细查询页 数据")
    public Object getMedicineDetailData(
            @ApiParam(name = "name", value = "药品名称")
            @RequestParam(value = "name" ,required =  false, defaultValue = "") String name) throws Exception {
        Map resultMap = new HashMap<>();
        String yearsql = "";
        String typeSql = "";
        String symptomSql = "";
        if(StringUtils.isNotEmpty(name)){
            name = "medicineName = '" + name + "'";
            yearsql = "select count(*) count from single_disease_check_index where checkCode = 'CH004' " + name +  " group by date_histogram(field='eventDate','interval'='year')";
            typeSql = "select diseaseTypeName,count(*) count from single_disease_check_index where checkCode = 'CH004' " + name + " group by diseaseTypeName";
            symptomSql = "select symptomName,count(*) count from single_disease_check_index where checkCode = 'CH004' " + name + " group by symptomName";
        }else {
            yearsql = "select count(*) count from single_disease_check_index where checkCode = 'CH004' group by date_histogram(field='eventDate','interval'='year')";
            typeSql = "select diseaseTypeName,count(*) count from single_disease_check_index where checkCode = 'CH004' group by diseaseTypeName";
            symptomSql = "select symptomName,count(*) count from single_disease_check_index where checkCode = 'CH004' group by symptomName";
        }
        //按年趋势
        Map<String, List<String>> map = singleDiseaseServiceNew.getDataInfo(yearsql, "date_histogram(field=eventDate,interval=year)");
        if (null != map && map.size() > 0) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(map.get("valueData"));
            envelop.setObj(map.get("xData"));
            resultMap.put("year",envelop);
        }
        //按糖尿病类型 饼图
        Map<String, Object> diseaseTypeDataInfo = singleDiseaseServiceNew.getPieDataInfoBySql("1", typeSql, "diseaseTypeName");
        if (null != diseaseTypeDataInfo && diseaseTypeDataInfo.size() > 0) {
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) diseaseTypeDataInfo.get("seriesData");
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(seriesData);
            envelop.setObj(diseaseTypeDataInfo.get("legendData"));
            resultMap.put("type",envelop);
        }
        //按相关并发症 饼图
        Map<String, Object> symptomDataInfo = singleDiseaseServiceNew.getPieDataInfoBySql("4", symptomSql, "symptomName");
        if (null != symptomDataInfo && symptomDataInfo.size() > 0) {
            ArrayList<Map<String, Object>> seriesData = (ArrayList<Map<String, Object>>) diseaseTypeDataInfo.get("seriesData");
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(seriesData);
            envelop.setObj(diseaseTypeDataInfo.get("legendData"));
            resultMap.put("symptom",envelop);
        }
        return  resultMap;
    }

}
