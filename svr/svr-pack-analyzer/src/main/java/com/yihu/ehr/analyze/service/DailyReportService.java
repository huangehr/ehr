package com.yihu.ehr.analyze.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.analyze.feign.PackageMgrClient;
import com.yihu.ehr.analyze.feign.RedisServiceClient;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 档案分析服务
 */
@Service
public class DailyReportService {
    public static String index = "qc";
    public static String type = "daily_report";
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 日报上传
     * @param report
     * @return
     */
    public Envelop dailyReport(String report){
        Envelop envelop = new Envelop();
        try {
            String msg = "";
            List<Map<String, Object>> list = objectMapper.readValue(report,List.class);
            for(Map<String, Object> map : list){
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
                if(map.get("created_time")==null||"".equals(map.get("created_time"))){
                    msg = msg + "上报时间不能为空、";
                }
            }

            if(StringUtils.isNotEmpty(msg)){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("参数校验失败");
            }else{
                for(Map<String, Object> map : list) {
                    elasticSearchUtil.index(index, type, map);
                }
                envelop.setSuccessFlg(true);
            }
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("日报上传错误");
        }
        return envelop;
    }

    /**
     * 日报查询
     * @param filter
     * @return
     */
    public Envelop list(String filter){
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
            List<Map<String, Object>> list = elasticSearchUtil.list(index, type, filterMap);
            envelop.setDetailModelList(list);
            envelop.setSuccessFlg(true);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 根据某个字段查询
     * @param field
     * @param value
     * @return
     */
    public Envelop findByField(String field, String value){
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> list = elasticSearchUtil.findByField(index, type, field , value);
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
