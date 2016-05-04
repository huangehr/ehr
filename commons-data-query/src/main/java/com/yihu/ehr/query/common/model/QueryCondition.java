package com.yihu.ehr.query.common.model;


import com.yihu.ehr.query.common.enums.Logical;
import com.yihu.ehr.query.common.enums.Operation;

/**
 * 查询条件
 * @author hzp add 2016-04-28
 */
public class QueryCondition {

	private String field;
	private Object keyword;
	private Object[] keywords;
	private String operation;
	private String logical;

	/**
	 * 构造函数
	 * @param field
	 * @param keyword
	 */
	public QueryCondition(String field, Object keyword) {
		this.logical = Logical.AND;
		this.operation = Operation.EQ;
		this.field = field;
		this.keyword = keyword;
	}

	/**
	 * 构造函数
	 */
	public QueryCondition(String logical, String operation, String field, String keyword) {
		this.logical = logical;
		this.operation = operation;
		this.field = field;
		this.keyword = keyword;
	}

	/**
	 * 构造函数
	 */
	public QueryCondition(String logical, String operation, String field, String[] keywords) {
		this.logical = logical;
		this.operation = operation;
		this.field = field;
		this.keywords = keywords;
	}



	/**
	 * 获取条件关系
	 * @return
	 */
	public String getLogical(){
		return this.logical;
	}

	/**
	 * 获取条件类型
	 * @return
	 */
	public String getOperation() {
		return operation;
	}

	public String getField() {
		return field;
	}

	public Object getKeyword() {
		return keyword;
	}

	public Object[] getKeywords() {
		return keywords;
	}
}
