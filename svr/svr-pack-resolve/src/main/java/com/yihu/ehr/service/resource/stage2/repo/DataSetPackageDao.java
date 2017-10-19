package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据集档案包入库（Mysql）操作
 *
 * @author 张进军
 * @date 2017/6/27 17:17
 */
@Service
@Transactional(readOnly = true)
public class DataSetPackageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过转换拼接数据集档案包数据的新增/更新的SQL语句，将档案数据保存到 mysql
     *
     * @param datasetPackage
     */
    @Transactional(readOnly = false)
    public void saveDataset(DatasetPackage datasetPackage) {
        String[] insertSqlArr = new String[datasetPackage.getSqlList().size()];
        insertSqlArr = datasetPackage.getSqlList().toArray(insertSqlArr);
        jdbcTemplate.batchUpdate(insertSqlArr);
    }

}
