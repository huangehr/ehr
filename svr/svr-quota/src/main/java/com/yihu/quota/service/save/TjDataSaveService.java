package com.yihu.quota.service.save;

import com.yihu.quota.model.jpa.save.TjQuotaDataSave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by chenweida on 2017/6/1.
 */
@Service
public class TjDataSaveService {
    @Autowired
    private JdbcTemplate jdbcTmeplate;

    public TjQuotaDataSave findByQuota(String code) {
        String sql = "SELECT " +
                "  qds.*, ds.type " +
                "FROM " +
                "  tj_data_save ds, " +
                "  tj_quota_data_save qds " +
                "WHERE " +
                "  qds.quota_code = ?";
        List<TjQuotaDataSave> quotaDataSources = jdbcTmeplate.query(sql, new BeanPropertyRowMapper(TjQuotaDataSave.class), code);
        if (quotaDataSources.size() > 0) {
            return quotaDataSources.get(0);
        }
        return null;
    }
}
