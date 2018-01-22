package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Airhead
 * @version 1.0
 * @created 2016.01.18
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DailyReportEndPoint", description = "档案日报上传", tags = {"档案分析服务-档案日报上传"})
public class DailyReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @ApiOperation(value = "档案日报上传")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.DailyReport, method = RequestMethod.POST)
    public Envelop dailyReport(
            @ApiParam(name = "json", value = "日报json对象")
            @RequestParam(value = "json", required = true) String json) {
        Envelop envelop = new Envelop();
        try {
            String msg = "";
            Map<String, Object> map = objectMapper.readValue(json,Map.class);
            if(map.get("org_code")==null||"".equals(map.get("org_code"))){
                msg = msg + "机构代码不能为空、";
            }
            if(map.get("event_date")==null||"".equals(map.get("event_date"))){
                msg = msg + "事件时间不能为空、";
            }
            if(map.get("HSI07_01_001")==null||"".equals(map.get("HSI07_01_001"))){
                msg = msg + "门诊人数不能为空、";
            }
            if(map.get("HSI07_01_002")==null||"".equals(map.get("HSI07_01_002"))){
                msg = msg + "急诊人数不能为空、";
            }
            if(map.get("HSI07_01_004")==null||"".equals(map.get("HSI07_01_004"))){
                msg = msg + "健康检查人数不能为空、";
            }
            if(map.get("HSI07_01_011")==null||"".equals(map.get("HSI07_01_011"))){
                msg = msg + "入院人数不能为空、";
            }
            if(map.get("HSI07_01_012")==null||"".equals(map.get("HSI07_01_012"))){
                msg = msg + "出院人数不能为空、";
            }
            if(map.get("created_date")==null||"".equals(map.get("created_date"))){
                msg = msg + "上报时间不能为空、";
            }
            if(StringUtils.isNotEmpty(msg)){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(msg.substring(0,msg.length()-1));
            }else{
                elasticSearchUtil.index("qc","daily_report",map);
                envelop.setSuccessFlg(true);
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("日报上传错误");
        }
        return envelop;
    }

    @ApiOperation(value = "档案日报查询")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.List, method = RequestMethod.POST)
    public Envelop list(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter) {
        Envelop envelop = new Envelop();
        List<Map<String, Object>> filterMap;
        if(!StringUtils.isEmpty(filter)) {
            try {
                filterMap = objectMapper.readValue(filter, List.class);
            } catch (Exception e) {
                e.printStackTrace();
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(e.getMessage());
                return envelop;
            }
        }else {
            filterMap = new ArrayList<Map<String, Object>>(0);
        }
        try {
            List<Map<String, Object>> list = elasticSearchUtil.list("analyze","upload",filterMap);
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation(value = "根据某个字段查询档案")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.FindByField, method = RequestMethod.POST)
    public Envelop findByField(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> list = elasticSearchUtil.findByField("analyze","upload",field , value);
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }
}
