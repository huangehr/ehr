package com.yihu.ehr.pack.service;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.pack.dao.JsonArchivesDao;
import com.yihu.ehr.pack.entity.JsonArchives;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 临时数据处理
 * Created by progr1mmer on 2018/4/23.
 */
//@Service
public class JsonArchivesService extends BaseJpaService<JsonArchives, JsonArchivesDao> {

    private static final Integer SINGLE_SIZE = 2000;
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private boolean isRun = false;

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public void migrate() throws Exception {
        if (isRun) {
            return;
        }
        isRun = true;
        String countSql = "SELECT COUNT(1) FROM json_archives";
        Session session = currentSession();
        Query query = session.createSQLQuery(countSql);
        query.setFlushMode(FlushMode.COMMIT);
        int count = ((BigInteger)query.uniqueResult()).intValue();
        int totalPage;
        if (count % SINGLE_SIZE > 0) {
            totalPage  = count / SINGLE_SIZE + 1;
        } else {
            totalPage  = count / SINGLE_SIZE;
        }
        new Thread(() -> {
            try {
                migrateJsonArchives(totalPage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void migrateJsonArchives(int totalPage) throws Exception {
        for (int i = 1 ; i <= totalPage; i ++) {
            List<JsonArchives> list = search(null, null, "+receiveDate", i, SINGLE_SIZE);
            List<Map<String, Object>> sourceList = new ArrayList<>(list.size());
            list.forEach(item -> {
                Map<String, Object> sourceMap = new HashMap<>();
                sourceMap.put("pwd", item.getPwd());
                sourceMap.put("remote_path", item.getRemotePath());
                sourceMap.put("receive_date", dateFormat.format(item.getReceiveDate()));
                sourceMap.put("archive_status", 0);
                sourceMap.put("org_code", item.getOrgCode());
                sourceMap.put("client_id", item.getClientId());
                sourceMap.put("resourced", 0);
                sourceMap.put("md5_value", item.getMd5());
                sourceMap.put("fail_count", 0);
                sourceMap.put("analyze_status", 0);
                sourceMap.put("analyze_fail_count", 0);
                sourceMap.put("pack_type", 1);
                sourceList.add(sourceMap);
            });
            elasticSearchUtil.bulkIndex("json_archives", "info", sourceList);
        }
    }
}
