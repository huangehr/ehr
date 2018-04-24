package com.yihu.ehr.profile.service;


import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.FlushMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hzp on 2016/4/13.
 */
@Service
public class ResourcesTransformService extends BaseJpaService {


    public Map<String, Object> stdTransform (Map<String, Object> resource, String dataset, String version) {
        Map<String, Object> returnRs =  new HashMap<>();
        String sql = "SELECT\n" +
                "\tram.metadata_id,\n" +
                "\tram.src_metadata_code\n" +
                "FROM\n" +
                "\trs_adapter_metadata ram\n" +
                "LEFT JOIN rs_adapter_scheme ras ON ram.scheme_id = ras.id\n" +
                "WHERE\n" +
                "\tras.adapter_version = :version\n" +
                "AND ram.src_dataset_code = :dataset";
        Session session = currentSession();
        Query query = session.createSQLQuery(sql);
        query.setString("version", version);
        query.setString("dataset", dataset);
        query.setFlushMode(FlushMode.COMMIT);
        List<Object []> metadataList = query.list();
        metadataList.forEach(item -> {
            String metadataId = (String) item[0];
            String srcMetadataCode = (String) item[1];
            if (resource.containsKey(metadataId)) {
                returnRs.put(srcMetadataCode, resource.get(metadataId));
            }
            //同时返回字典值
            if (resource.containsKey(metadataId + "_VALUE")) {
                returnRs.put(srcMetadataCode + "_VALUE", resource.get(metadataId + "_VALUE"));
            }
            returnRs.put("org_area", resource.get("org_area"));
            returnRs.put("org_name", resource.get("org_name"));
            returnRs.put("event_date", resource.get("event_date"));
        });
        return returnRs;
    }
}
