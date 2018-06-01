package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhengwei
 * @Date: 2018/5/31 16:21
 * @Description:质控报表
 */
@Service
public class PackQcReportService extends BaseJpaService {

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;

    /**
     * 获取医院数据
     * @param startDate
     * @param endDate
     * @param orgCode
     * @return
     * @throws Exception
     */
    public Envelop dailyReport(String startDate, String endDate, String orgCode) throws Exception{
        Envelop envelop = new Envelop();
        Map<String,Object> resMap = new HashMap<String,Object>();
        int total=0;
        int inpatient_total=0;
        int oupatient_total=0;
        int physical_total=0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("create_date=>" + startDate + " 00:00:00;");
        stringBuilder.append("create_date<=" + endDate + " 23:59:59;");
        if (StringUtils.isNotEmpty(orgCode)) {
            stringBuilder.append("org_code?" + orgCode);
        }
        List<Map<String, Object>> res = elasticSearchUtil.list("qc","daily_report", stringBuilder.toString());
        if(res!=null && res.size()>0){
            for(Map<String,Object> report : res){
                total+=Integer.parseInt(report.get("HSI07_01_001").toString());
                inpatient_total+=Integer.parseInt(report.get("HSI07_01_012").toString());
                oupatient_total+=Integer.parseInt(report.get("HSI07_01_002").toString());
                physical_total+=Integer.parseInt(report.get("HSI07_01_004").toString());
            }
        }
        resMap.put("total",total);
        resMap.put("inpatient_total",inpatient_total);
        resMap.put("oupatient_total",oupatient_total);
        resMap.put("physical_total",physical_total);
        envelop.setObj(resMap);
        envelop.setSuccessFlg(true);
        return envelop;
    }
}
