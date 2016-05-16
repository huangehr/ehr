package com.yihu.ehr.profile.controller.indices;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Sand
 * @created 2016.05.14 18:13
 */
@Service
public class SolrCriteriaBuilder {
    private final static Set<String> operands;

    SolrCriteriaBuilder(){
        operands = new HashSet<>();
        operands.add("?");
        operands.add(">=");
        operands.add(">");
        operands.add("<=");
        operands.add("<");
        operands.add("=");
    }

    public Criteria buildCriteria(String query){
        int start = 0;
        int end = 0;
        String operand;
        String field;
        String value;
        for (int i = 0; i < query.length(); ++i){

        }
    }
}
