package com.yihu.ehr.solr.query;


/**
 * solr查询条件
 * @author szx
 *
 */
public class HbaseQueryCondition {

	private String field;
	private String keyword;
	private String keywordTo;
	private String[] keywords;
	private HbaseQueryEnum.Operation operation;
	private HbaseQueryEnum.Logical logical;

	/**
	 * 构造函数
	 * @param field
	 * @param keyword
	 */
	public HbaseQueryCondition(String field, String keyword) {
		this.logical = HbaseQueryEnum.Logical.AND;
		this.operation = HbaseQueryEnum.Operation.EQUAL;
		this.field = field;
		this.keyword = keyword;
	}

	/**
	 * 构造函数
	 */
	public HbaseQueryCondition(HbaseQueryEnum.Logical logical, HbaseQueryEnum.Operation operation, String field, String[] keywords) {
		this.logical = logical;
		this.operation = operation;
		this.field = field;
		this.keywords = keywords;
	}

	/**
	 * 构造函数
	 */
	public HbaseQueryCondition(HbaseQueryEnum.Logical logical, HbaseQueryEnum.Operation operation, String field, String keyword) {
		this.logical = logical;
		this.operation = operation;
		this.field = field;
		this.keyword = keyword;
	}

	/**
	 * 构造函数
	 */
	public HbaseQueryCondition(HbaseQueryEnum.Logical logical, HbaseQueryEnum.Operation operation, String field, String keyword, String keywordTo) {
		this.logical = logical;
		this.operation = operation;
		this.field = field;
		this.keyword = keyword;
		this.keywordTo = keywordTo;
	}

	/**
	 * 获取条件关系
	 * @return
	 */
	public HbaseQueryEnum.Logical getLogical(){
		return this.logical;
	}

	/**
	 * 条件转字符串
	 * @return
	 */
	public String toString(){
		String s = "";
		switch(this.operation){
			case LIKE:
				s = this.field+":*"+this.keyword+"*";
				break;
			case LEFTLIKE:
				s = this.field+":*"+this.keyword+"";
				break;
			case RIGHTLIKE:
				s = this.field+":"+this.keyword+"*";
				break;
			case RANGE: {
				if(keyword==null||keyword.equals(""))
				{
					keyword = "*";
				}
				if(keywordTo==null||keywordTo.equals(""))
				{
					keywordTo = "*";
				}
				s = this.field + ":[" + this.keyword + " TO " + keywordTo + "]";
				break;
			}
			case IN:
			{
				String in = "";
				if(keywords!=null && keywords.length>0)
				{
					for (String key : keywords)
					{
						if(in!=null&&in.length()>0)
						{
							in+=" OR " +this.field+":"+key;
						}
						else
						{
							in = this.field+":"+key;
						}
					}
				}
				s = "("+in+")";
				break;
			}
			default:
				s = this.field+":"+this.keyword;
		}
		
		return s;
	}


}
