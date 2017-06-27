package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据集档案包入库操作
 *
 * @author 张进军
 * @date 2017/6/27 17:17
 */
@Service
@Transactional(readOnly = true)
public class DatasetPackageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过转换拼接数据集档案包数据的SQL插入语句，将档案数据保存起来
     *
     * @param datasetPackage
     */
    @Transactional(readOnly = false)
    public void saveDataset(DatasetPackage datasetPackage) {
        String[] insertSqlArr = (String[]) datasetPackage.getInsertSqlList().toArray();
        jdbcTemplate.batchUpdate(insertSqlArr);
    }

}
