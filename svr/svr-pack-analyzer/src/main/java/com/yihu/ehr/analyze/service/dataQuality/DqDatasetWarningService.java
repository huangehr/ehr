package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqDatasetWarningDao;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqDatasetWarningService extends BaseJpaService<DqDatasetWarning, DqDatasetWarningDao> {

    @Value("${quality.version}")
    private String defaultQualityVersion;
    private String DataSetTable = "std_data_set_";
    @Autowired
    private RedisClient redisClient;

    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;

    public List<DqDatasetWarning> findByOrgCodeAndType(String orgCode,String type){
        return dqDatasetWarningDao.findByOrgCodeAndType(orgCode,type);
    }

    /**
     * 导入excel
     * @param codeList
     */
    public List<DqDatasetWarning> importDatasetExcel(List<String> codeList){
        List<DqDatasetWarning> warningList = new ArrayList<>(codeList.size());
        codeList.forEach(code->{
            String key = DataSetTable+defaultQualityVersion+":"+code+":name";
            if(redisClient.hasKey(key)){
                DqDatasetWarning warning = new DqDatasetWarning();
                warning.setCode(code);
                warning.setName(redisClient.get(key));
                warningList.add(warning);
            }
        });
        return warningList;
    }
}
