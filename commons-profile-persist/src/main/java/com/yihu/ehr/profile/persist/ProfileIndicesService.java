package com.yihu.ehr.profile.persist;

import com.yihu.ehr.profile.persist.repo.XProfileIndicesRepo;
import com.yihu.ehr.util.DateFormatter;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.NumberUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    static class SolrQueryParser{
        /**
         * like：使用"?"来表示，如：name?'%医'
         * in：使用"="来表示并用","逗号对值进行分隔，如：status=2,3,4,5
         * not in：使用"<>"来表示并用","逗号对值进行分隔，如：status=2,3,4,5
         * =：使用"="来表示，如：status=2
         * >=：使用大于号和大于等于语法，如：createDate>2012
         * <=：使用小于号和小于等于语法，如：createDate<=2015
         * 分组：在条件后面加上空格，并设置分组号，如：createDate>2012 g1，具有相同组名的条件将使用or连接
         * 多条件组合：使用";"来分隔
         * <p>
         * 生成 where 条件。
         *
         * @param filter
         */
        protected Criteria splitFilter(String filter) throws ParseException {
            Predicate predicate = null;
            if (filter.contains("?")) {
                Pair<Path, String> pair = getPair(filter, "[?]", root);
                predicate = cb.like(pair.getKey(), "%" + pair.getValue() + "%");
            } else if (filter.contains("<>")) {
                Pair<Path, String> pair = getPair(filter, "<>", root);

                if (pair.getValue().contains(",")) {
                    predicate = cb.not(pair.getKey().in(pair.getValue().split(",")));
                } else {
                    predicate = cb.notEqual(pair.getKey(), pair.getValue());
                }
            } else if (filter.contains(">=")) {
                Pair<Path, String> pair = getPair(filter, ">=", root);
                predicate = cb.ge(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
            } else if (filter.contains(">")) {
                Pair<Path, String> pair = getPair(filter, ">", root);
                if (pair.getKey().getJavaType() == Date.class) {
                    Date date = DateFormatter.simpleDateParse(pair.getValue());
                    predicate = cb.greaterThan(pair.getKey(), date);
                } else {
                    predicate = cb.gt(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
                }
            } else if (filter.contains("<=")) {
                Pair<Path, String> pair = getPair(filter, "<=", root);
                predicate = cb.le(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
            } else if (filter.contains("<")) {
                Pair<Path, String> pair = getPair(filter, "<", root);
                predicate = cb.lt(pair.getKey(), NumberUtils.parseNumber(pair.getValue(), pair.getKey().getJavaType()));
            } else if (filter.contains("=")) {
                Pair<Path, String> pair = getPair(filter, "=");

                Set<Object> values = new HashSet<>();
                for (String data : pair.getValue().split(",")){
                    if (pair.getKey().getJavaType().isEnum()){
                        values.add(Enum.valueOf(pair.getKey().getJavaType(), data));
                    } else{
                        values.add(data);
                    }
                }

                predicate = pair.getKey().in(values);
            }

            return predicate;
        }

        protected Pair<String, String> getPair(String filter, String splitter) {
            String[] tokens = filter.split(splitter);
            return new Pair<>(tokens[0], tokens[1]);
        }
    }
}
