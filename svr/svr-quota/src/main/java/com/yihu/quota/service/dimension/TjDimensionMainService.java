package com.yihu.quota.service.dimension;

import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chenweida on 2017/6/1.
 */
@Service
public class TjDimensionMainService {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<TjQuotaDimensionMain> findTjQuotaDimensionMainByQuotaIncudeAddress(String code) {
        String sql = "SELECT  qdm.*, dm.type FROM   tj_dimension_main dm, tj_quota_dimension_main qdm " +
        "WHERE   dm.`code` = qdm.main_code AND qdm.quota_code = ? and dm.code > 4 and dm.code <10 order by code desc";

        List<TjQuotaDimensionMain> quotaDataSources = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TjQuotaDimensionMain.class), code);
        return quotaDataSources;
    }
}
