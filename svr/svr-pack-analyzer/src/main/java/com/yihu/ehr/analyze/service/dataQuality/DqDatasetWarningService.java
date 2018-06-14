package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqDatasetWarningDao;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.model.profile.MDataSet;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 数据质量-数据集预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqDatasetWarningService extends BaseJpaService<DqDatasetWarning, DqDatasetWarningDao> {

    private String DataSetTable = "std_data_set_";
    @Value("${quality.version}")
    private String defaultQualityVersion;
    @Autowired
    protected RedisClient redisClient;
    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;

    public List<DqDatasetWarning> findByOrgCodeAndType(String orgCode,String type){
        return dqDatasetWarningDao.findByOrgCodeAndType(orgCode,type);
    }

    /**
     * 返回数据集
     * @param page
     * @param size
     * @return
     */
    public List<MDataSet> getDataSet(Integer page,Integer size){
        //std_data_set_版本号: 数据集编码: name
        Set<String> dataset = redisClient.keys(DataSetTable+defaultQualityVersion+":*:name");
        List<MDataSet> dataSets = new ArrayList<>(size);
        int i = 0;
        int start = (page-1)*size;
        int end = page*size;
        for(String data:dataset){
            if(i>=start&&i<end){
                MDataSet mDataSet = new MDataSet();
                mDataSet.setCode(data);
                mDataSet.setName(redisClient.get(data));
                dataSets.add(mDataSet);
            }
            if(i>=end){
                break;
            }
            i++;
        }
        return dataSets;
    }
}
