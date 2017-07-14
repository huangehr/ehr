package com.yihu.quota.service.source;

import com.yihu.quota.dao.jpa.source.TjDataSourceDao;
import com.yihu.quota.dao.jpa.source.TjQuotaDataSourceDao;
import com.yihu.quota.model.jpa.source.TjQuotaDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chenweida on 2017/6/1.
 */
@Service
public class TjDataSourceService {
    @Autowired
    private TjDataSourceDao dataSourceDao;
    @Autowired
    private TjQuotaDataSourceDao quotaDataSourceDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public TjQuotaDataSource findSourceByQuotaCode(String code) {
        String sql = " SELECT " +
                "  qds.*, ds.code " +
                "FROM " +
                "  tj_quota_data_source qds, " +
                "  tj_data_source ds " +
                "WHERE " +
                "  qds.source_code = ds.`code` " +
                "  and " +
                "  qds.quota_code = ?";
        List<TjQuotaDataSource> quotaDataSources = jdbcTemplate.query(sql, new BeanPropertyRowMapper(TjQuotaDataSource.class), code);
        if (quotaDataSources.size() > 0) {
            return quotaDataSources.get(0);
        }
        return null;
    }
}
