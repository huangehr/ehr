package com.yihu.quota.etl.conver;


import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2018/5/9.
 */
public interface Convert {


    /**
     * @param jdbcTemplate jdbc工具
     * @param dataList 需要赋值的数据
     * @param slave 细维度
     * @return
     */
    public List<Map<String, Object>> convert(JdbcTemplate jdbcTemplate, List<Map<String, Object>> dataList , TjQuotaDimensionSlave slave);

}
