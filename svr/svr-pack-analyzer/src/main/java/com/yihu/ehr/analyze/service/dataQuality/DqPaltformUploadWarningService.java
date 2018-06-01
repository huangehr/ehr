package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqDatasetWarningDao;
import com.yihu.ehr.analyze.dao.DqPaltformUploadWarningDao;
import com.yihu.ehr.entity.quality.DqPaltformUploadWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 数据质量-平台上传预警值
 * @author yeshijie on 2018/5/28.
 */
@Service
public class DqPaltformUploadWarningService extends BaseJpaService<DqPaltformUploadWarning, DqPaltformUploadWarningDao> {

    @Autowired
    private DqPaltformUploadWarningDao dqPaltformUploadWarningDao;
    @Autowired
    private DqDatasetWarningDao dqDatasetWarningDao;

    public DqPaltformUploadWarning findById(Long id) {
        return dqPaltformUploadWarningDao.findOne(id);
    }

    public DqPaltformUploadWarning findByOrgCode(String orgCode){
        return dqPaltformUploadWarningDao.findByOrgCode(orgCode);
    }

    /**
     * 新增平台上传预警
     * @param warning
     * @return
     */
    public DqPaltformUploadWarning paltformUploadWarningAdd(DqPaltformUploadWarning warning){

        save(warning);
        if(warning.getDatasetWarningList()!=null&&warning.getDatasetWarningList().size()>0){
            warning.getDatasetWarningList().forEach(dataset->{
                dataset.setOrgCode(warning.getOrgCode());
                dataset.setType("2");
            });
            dqDatasetWarningDao.save(warning.getDatasetWarningList());
        }

        return warning;
    }

    /**
     * 修改平台上传预警
     * @param warning
     * @return
     */
    public DqPaltformUploadWarning paltformUploadWarningUpd(DqPaltformUploadWarning warning){

        save(warning);
        return warning;
    }
}
