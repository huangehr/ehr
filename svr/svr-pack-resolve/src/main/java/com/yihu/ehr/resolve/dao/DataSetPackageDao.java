package com.yihu.ehr.resolve.dao;

import com.yihu.ehr.resolve.model.stage1.SimplePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 数据集档案包入库（Mysql）操作
 *
 * @author 张进军
 * @date 2017/6/27 17:17
 */
@Repository
@Transactional(readOnly = true)
public class DataSetPackageDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 通过转换拼接数据集档案包数据的新增/更新的SQL语句，将档案数据保存到 mysql
     *
     * @param simplePackage
     */
    @Transactional()
    public void saveDataset(SimplePackage simplePackage) {
        String[] insertSqlArr = new String[simplePackage.getSqlList().size()];
        insertSqlArr = simplePackage.getSqlList().toArray(insertSqlArr);
        jdbcTemplate.batchUpdate(insertSqlArr);
    }

}
