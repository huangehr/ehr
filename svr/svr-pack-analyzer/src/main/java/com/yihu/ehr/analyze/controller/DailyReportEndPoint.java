package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DailyReportEndPoint", description = "档案日报上传", tags = {"档案分析服务-档案日报上传"})
public class DailyReportEndPoint extends EnvelopRestEndPoint {

    public static final String INDEX = "qc";
    public static final String TYPE = "daily_report";
    private static final Logger log = LoggerFactory.getLogger(DailyReportEndPoint.class);

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @ApiOperation(value = "档案日报上传")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.DailyReport, method = RequestMethod.POST)
    public Envelop dailyReport(
            @ApiParam(name = "report", value = "日报json对象", required = true)
            @RequestParam(value = "report") String report) throws Exception {
        String msg = "";
        List<Map<String, Object>> list = objectMapper.readValue(report, List.class);
        for (Map<String, Object> map : list){
            if (map.get("org_code")== null|| "".equals(map.get("org_code"))){
                msg = msg + "机构代码不能为空、";
            }
            if (map.get("event_date") == null || "".equals(map.get("event_date"))){
                msg = msg + "事件时间不能为空、";
            }
            if (map.get("HSI07_01_001") == null || "".equals(map.get("HSI07_01_001"))){
                msg = msg + "总诊疗人数不能为空、";
            }
            if (map.get("HSI07_01_002") == null || "".equals(map.get("HSI07_01_002"))){
                msg = msg + "门急诊人数不能为空、";
            }
            if (map.get("HSI07_01_004") == null|| "".equals(map.get("HSI07_01_004"))){
                msg = msg + "健康检查人数不能为空、";
            }
            if (map.get("HSI07_01_011") == null || "".equals(map.get("HSI07_01_011"))){
                msg = msg + "入院人数不能为空、";
            }
            if (map.get("HSI07_01_012") == null || "".equals(map.get("HSI07_01_012"))){
                msg = msg + "出院人数不能为空、";
            }
        }
        if (StringUtils.isNotEmpty(msg)){
            log.error(msg);
            return failed("参数校验失败");
        } else {
            for (Map<String, Object> map : list) {
                //补传的时候删除原来数据
                String filter = "event_date=" + map.get("event_date") + ";org_code?"+map.get("org_code");
                List<Map<String, Object>> res = elasticSearchUtil.list(INDEX, TYPE, filter);
                if(res!=null && res.size()>0){
                    for(Map<String, Object> m : res){
                        elasticSearchUtil.delete(INDEX, TYPE ,m.get("_id").toString());
                    }
                }
                elasticSearchUtil.index(INDEX, TYPE, map);
            }
            return success(true);
        }
    }

    @ApiOperation(value = "档案日报查询")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.List, method = RequestMethod.POST)
    public Envelop list(
            @ApiParam(name = "filter", value = "过滤条件")
            @RequestParam(value = "filter", required = false) String filter) throws Exception {
        List<Map<String, Object>> list = elasticSearchUtil.list(INDEX, TYPE, filter);
        return success(list);
    }

    @ApiOperation(value = "根据某个字段查询档案")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.FindByField, method = RequestMethod.POST)
    public Envelop findByField(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "字段值", required = true)
            @RequestParam(value = "value") String value) {
        List<Map<String, Object>> list =  elasticSearchUtil.findByField(INDEX, TYPE, field , value);
        return success(list);
    }

    @ApiOperation(value = "根据sql查询")
    @RequestMapping(value = ServiceApi.PackageAnalyzer.FindBySql, method = RequestMethod.POST)
    public List<Map<String,Object>> findBySql(
            @ApiParam(name = "field", value = "字段列表", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "sql", value = "sql", required = true)
            @RequestParam(value = "sql") String sql) throws Exception {
        List<Map<String, Object>> list = elasticSearchUtil.findBySql(objectMapper.readValue(field, List.class), sql);
        return list;
    }
}
