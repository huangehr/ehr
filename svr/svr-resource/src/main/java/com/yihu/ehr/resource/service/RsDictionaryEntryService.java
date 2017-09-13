package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.RsDictionaryEntryDao;
import com.yihu.ehr.resource.model.RsDictionaryEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsDictionaryEntryService extends BaseJpaService<RsDictionaryEntry, RsDictionaryEntryDao> {

    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    private RsDictionaryEntryDao dictionaryEntryDao;

    public RsDictionaryEntry findById(int id) {
        return dictionaryEntryDao.findOne(id);
    }

    public List<RsDictionaryEntry> findByDictCode(String code) {
        return dictionaryEntryDao.findByDictCode(code);
    }

    public int countByDictId(int dictId) {

        return dictionaryEntryDao.countByDictId(dictId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public RsDictionaryEntry insert(RsDictionaryEntry model) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement pst = connection.prepareStatement(
                        "INSERT INTO rs_dictionary_entry(dict_code, code, name, description, dict_id) VALUES(?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, model.getDictCode());
                pst.setString(2, model.getCode());
                pst.setString(3, model.getName());
                pst.setString(4, model.getDescription());
                pst.setInt(5, model.getDictId());
                return pst;
            }
        }, keyHolder);
        model.setId(keyHolder.getKey().intValue());
        return model;
    }
}
