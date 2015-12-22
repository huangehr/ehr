package com.yihu.ehr.std.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Solr 查询构建工具。支持以下几种操作。
 * where
 * equals
 * in
 * begin (开始一个新的子条件块)
 * end (结构子条件块)
 * or
 * any
 * between
 * betweenWithOpenIntervals
 * lt
 * gt
 * lte
 * gte
 *
 * Usage:
 * String query = new SolrQueryBuilder()
 *  .begin()
 *  .where("key1")
 *  .equals("value1")
 *  .and()
 *  .where("key2")
 *  .equals("value2")
 *  .end()
 *  .buildQuery();
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.16 18:55
 */

public class SolrQueryBuilder {
    SolrQueryBuilder parentNode;

    List<Object> queryNodes = new ArrayList<>();
    String tempFieldName;

    public SolrQueryBuilder(){
        this.parentNode = null;
    }

    private SolrQueryBuilder(SolrQueryBuilder parentNode){
        this.parentNode = parentNode;
    }

    // 开始新的块
    public SolrQueryBuilder begin() {
        SolrQueryBuilder subQueryNode = new SolrQueryBuilder(this);
        addSubQueryNode(LogicOperator.None, subQueryNode);

        return subQueryNode;
    }

    public SolrQueryBuilder end() {
        if(parentNode != null){
            return parentNode;
        }

        return this;
    }

    // 同级条件的逻辑运算
    public SolrQueryBuilder or() {
        addSubQueryNode(LogicOperator.Or, null);

        return this;
    }

    public SolrQueryBuilder and() {
        addSubQueryNode(LogicOperator.And, null);

        return this;
    }

    public SolrQueryBuilder where(String fieldName) {
        tempFieldName = fieldName;
        return this;
    }

    public SolrQueryBuilder equals(String value) {
        addQueryElement(tempFieldName, ValueOperator.Equals, new String[]{value});
        return this;
    }

    public SolrQueryBuilder equals(Number value) {
        addQueryElement(tempFieldName, ValueOperator.Equals, new Number[]{value});
        return this;
    }

    public SolrQueryBuilder in(String[] values) {
        addQueryElement(tempFieldName, ValueOperator.In, values);
        return this;
    }

    public SolrQueryBuilder between(String begin, String end) {
        addQueryElement(tempFieldName, ValueOperator.Between, new String[]{begin, end});
        return this;
    }

    public SolrQueryBuilder betweenWithOpenIntervals(String begin, String end) {
        addQueryElement(tempFieldName, ValueOperator.BetweenWithOpenInterval, new String[]{begin, end});
        return this;
    }

    public SolrQueryBuilder lt(String value) {
        addQueryElement(tempFieldName, ValueOperator.Lt, new String[]{"*", value});
        return this;
    }

    public SolrQueryBuilder gt(String value) {
        addQueryElement(tempFieldName, ValueOperator.Gt, new String[]{value, "*"});
        return this;
    }

    public SolrQueryBuilder lte(String value) {
        addQueryElement(tempFieldName, ValueOperator.Lte, new String[]{"*", value});
        return this;
    }

    public SolrQueryBuilder gte(String value) {
        addQueryElement(tempFieldName, ValueOperator.Gte, new String[]{value, "*"});
        return this;
    }

    public String buildQuery(){
        StringBuffer buffer = new StringBuffer();
        recursiveBuildQuery(buffer);

        return buffer.toString();
    }

    private void recursiveBuildQuery(StringBuffer buffer) {
        for (Object object : queryNodes) {
            if (object instanceof SolrQueryElement) {
                SolrQueryElement element = (SolrQueryElement) object;
                element.buildQuery(buffer);
            } else if (object instanceof QueryBlock) {
                QueryBlock subNode = (QueryBlock) object;
                switch (subNode.logicOperator) {
                    case And:
                        buffer.append(" AND ");
                        break;

                    case Or:
                        buffer.append(" OR ");
                        break;
                }

                if(subNode.queryNode != null){
                    if(parentNode != null) buffer.append("(");
                    subNode.queryNode.recursiveBuildQuery(buffer);
                    if(parentNode != null) buffer.append(")");
                }
            }
        }
    }

    private void addQueryElement(String fieldName, ValueOperator operator, Object[] values) {
        SolrQueryElement element = new SolrQueryElement();
        element.setField(fieldName);
        element.setValueOperator(operator);
        element.setValues(values);

        queryNodes.add(element);
    }

    private SolrQueryBuilder addSubQueryNode(LogicOperator logicOperator, SolrQueryBuilder subQueryNode) {
        QueryBlock queryBlock = new QueryBlock();

        queryBlock.logicOperator = logicOperator;
        queryBlock.queryNode = subQueryNode;
        this.queryNodes.add(queryBlock);

        return subQueryNode;
    }
}

enum ValueOperator {
    Equals,
    Between,
    BetweenWithOpenInterval,
    Lt,
    Lte,
    Gt,
    Gte,
    In,
    Like
}

enum LogicOperator {
    None,
    And,
    Or
}

class SolrQueryElement {
    private String field;
    private ValueOperator valueOperator;
    private Object[] values;

    public void setField(String field) {
        this.field = field;
    }

    public void setValueOperator(ValueOperator operator) {
        this.valueOperator = operator;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

    public void buildQuery(StringBuffer buffer) {
        buffer.append(field).append(" : ");

        switch (valueOperator) {
            case Equals:
                if (values[0] instanceof Number) {
                    buffer.append(values[0]);
                } else {
                    buffer.append(values[0]);
                }
                break;

            case Lt:
                makeInterval(buffer, true, values[0], true, values[1]);
                break;

            case Lte:
                makeInterval(buffer, false, values[0], false, values[1]);
                break;

            case Gt:
                makeInterval(buffer, true, values[0], true, values[1]);
                break;

            case Gte:
                makeInterval(buffer, false, values[0], false, values[1]);
                break;

            case In:
                buffer.append("(");
                for (Object value : values) {
                    buffer.append(value instanceof Number ? "" : "\"")
                            .append(value)
                            .append(value instanceof Number ? "" : "\" ");
                }
                buffer.append(")");

            case Between:
            case BetweenWithOpenInterval:
                makeInterval(buffer, valueOperator == ValueOperator.BetweenWithOpenInterval, values[0], valueOperator == ValueOperator.BetweenWithOpenInterval, values[1]);
                break;
        }
    }

    private void makeInterval(StringBuffer buffer, boolean openBeginInterval, Object value1, boolean openEndInterval, Object value2) {
        buffer.append(openBeginInterval ? "{" : "[")
                .append(value1)
                .append(" TO ")
                .append(value2)
                .append(openEndInterval ? "}" : "]");
    }
}

class QueryBlock {
    public LogicOperator logicOperator;
    public SolrQueryBuilder queryNode;
}
