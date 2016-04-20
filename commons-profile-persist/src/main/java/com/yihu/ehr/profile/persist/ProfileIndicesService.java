package com.yihu.ehr.profile.persist;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.data.solr.core.query.Field;

import java.util.*;

/**
 * 档案索引服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class ProfileIndicesService {
    @Autowired
    XProfileIndicesRepo profileIndicesRepo;

    public List<ProfileIndices> findByDemographicIdAndEventDateBetween(String demographicId, Date since, Date to) {
        return profileIndicesRepo.findByDemographicIdAndEventDateBetween(demographicId, since, to);
    }

    public Page<ProfileIndices> search(String queryString, Pageable pageable) {
        Criteria criteria = buildCriteria(queryString);

        return profileIndicesRepo.find(new SimpleQuery(criteria), pageable);
    }

    private Criteria buildCriteria(String query) {
        Criteria criteria = new Criteria("rowkey").contains("*");

        String conditions[] = query.split(";");
        for (String condition : conditions){
            String tokens[] = condition.split(" ");

            if (tokens[0].contains("demographicId")){
                criteria = criteria.or(new Criteria("demographic_id").contains("412726195111306268"));
            }

            if (condition.contains("eventDate")){
                criteria = criteria.or("event_date").between("2015-01-01", "2017-01-01");
            }
        }

        return criteria;
    }

    /**
     * 谓词参见：{@link Criteria.OperationKey}
     *
     * equal：使用"="来表示，如：status=2
     * contain：使用"?"来表示，如：name?'%医'
     * startsWith：
     * endsWith：
     * expression：
     * between：
     * near：
     * within：
     * fuzzy：
     * sloppy：
     * function：
     * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
     * >=：使用大于号和大于等于语法，如：createDate>2012
     * <=：使用小于号和小于等于语法，如：createDate<=2015
     * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接
     * 多条件组合：使用";"来分隔
     */
    static class SolrQueryParser{

        static Criteria parseQuery(String query){
            Multimap<String, Criteria> groups = ArrayListMultimap.create();
            for (String filter : query.split(";")){
                Criteria criteria = splitFilter(filter);
                if (criteria == null) continue;

                groups.put("", criteria);
            }

            Criteria root = null;
            for (String key : groups.keySet()){
                Collection<Criteria> criteriaCollection = groups.get(key);

                Criteria sub = null;
                for (Criteria criteria : criteriaCollection){
                    if (sub == null){
                        sub = criteria;
                    } else {
                        sub = sub.or(criteria);
                    }
                }

                if (root == null){
                    root = sub;
                } else {
                    root = root.and(sub);
                }
            }

            return root;
        }

        static Criteria splitFilter(String filter) {
            if (filter.contains("?")) {
                Pair<String, String> pair = getPair(filter, "[?]");
                return new Criteria(pair.getKey()).contains(pair.getValue());
            } else if (filter.contains(">=")) {
                Pair<String, String> pair = getPair(filter, ">=");
                return new Criteria(pair.getKey()).greaterThanEqual(pair.getValue());
            } else if (filter.contains(">")) {
                Pair<String, String> pair = getPair(filter, ">");
                return new Criteria(pair.getKey()).greaterThan(pair.getValue());
            } else if (filter.contains("<=")) {
                Pair<String, String> pair = getPair(filter, "<=");
                return new Criteria(pair.getKey()).lessThanEqual(pair.getValue());
            } else if (filter.contains("<")) {
                Pair<String, String> pair = getPair(filter, "<");
                return new Criteria(pair.getKey()).lessThan(pair.getValue());
            } else if (filter.contains("=")) {
                Pair<String, String> pair = getPair(filter, "=");

                Set<Object> values = new HashSet<>();
                for (String data : pair.getValue().split(",")){
                    values.add(data);
                }

                return new Criteria(pair.getKey()).contains(pair.getValue());
            }

            return null;
        }

        static Pair<String, String> getPair(String filter, String splitter) {
            String[] tokens = filter.split(splitter);
            return new Pair<>(tokens[0], tokens[1]);
        }

        static void fieldConvert(Criteria criteria){
            Field field = criteria.getField();
            if (field.getName().equals("demographic_id")){
            }
        }
    }
}
