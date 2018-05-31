package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yeshijie on 2018/5/31.
 */
@Service
public class DataQualityStatisticsService {
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private WarningSettingService warningSettingService;



    public void dataset() throws Exception{
        String dateStr = DateUtil.toString(new Date());
        String sql1 = "SELECT org_code, COUNT(org_code) FROM qc_dataset_info WHERE  receive_date BETWEEN '" + dateStr + " 00:00:00' AND '" +  dateStr + " 23:59:59' GROUP BY org_code";
        ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
        Map<String, Object[]> dataMap1 = new HashMap<>();
        try {
            while (resultSet1.next()) {
                Object[] tempArr = new Object[3];
                String code = ObjectUtils.toString(resultSet1.getObject("org_code"));
                double count = resultSet1.getDouble("COUNT(org_code)");
                tempArr[0] = code;
                tempArr[2] = count;
                dataMap1.put(code, tempArr);
            }
        }catch (Exception e){
            dataMap1 = new HashMap<>();
        }
    }

}
