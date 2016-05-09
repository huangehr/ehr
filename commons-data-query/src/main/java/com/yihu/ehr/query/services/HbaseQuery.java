package com.yihu.ehr.query.services;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.query.common.enums.Logical;
import com.yihu.ehr.query.common.enums.Operation;
import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.common.model.QueryCondition;
import com.yihu.ehr.query.common.model.QueryEntity;
import com.yihu.ehr.query.common.model.SolrJoinEntity;
import com.yihu.ehr.query.solr.SolrUtil;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Hase - Solr查询服务
 * add by hzp at 2016-04-26
 */
@Service
public class HbaseQuery {
	@Autowired
	SolrUtil solr;

	@Autowired
	HBaseUtil hbu;

	/**
	 * 排序转换
	 * @param sort(格式1：{"field1":"asc","field2":"desc"}  格式2：field1 asc,field2 desc)
	 * @return
	 */
	private Map<String,String> getSortMap(String sort){
		if(sort!=null && sort.length()>0)
		{
			Map<String,String> sortMap = new HashMap<String, String>();
			try{ //json数据
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> obj = objectMapper.readValue(sort, Map.class);
				if(obj!=null){
					Set<String> set = obj.keySet();
					for (Iterator<String> it = set.iterator();it.hasNext();) {
						String field = it.next();
						String value = obj.get(field);

						sortMap.put(field, value);
					}
				}
			}
			catch (Exception ex)
			{
				String[] items = sort.split(",");

				if(items.length>0)
				{
					for(String str : items) {
						String[] sortItem = sort.split(" ");

						sortMap.put(sortItem[0],sortItem[1]!=null?sortItem[1]:"asc");
					}
				}
			}

			return sortMap;
		}
		else
			return null;

	}

	/**
	 * Result 转 JSON
	 * @return
	 */
	private Map<String,Object> resultToMap(Result result,String fl){

		String rowkey = Bytes.toString(result.getRow());
		if(rowkey!=null && rowkey.length()>0)
		{

			Map<String,Object> obj = new HashMap<>();
			obj.put("rowkey", rowkey);
			for  (Cell cell : result.rawCells()) {
				String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
				String fieldValue = Bytes.toString(CellUtil. cloneValue(cell));
				if(fl!=null&&!fl.equals("")&&!fl.equals("*"))
				{
					String[] fileds = fl.split(",");
					for(String filed : fileds){
						if(filed.equals(fieldName))
						{
							obj.put(fieldName, fieldValue);
							continue;
						}
					}
				}
				else
				{
					obj.put(fieldName, fieldValue);
				}

			}
			return obj;
		}
		else
			return null;

	}

	/**
	 * 条件转字符串
	 * @return
	 */
	private String qcToString(QueryCondition qc){
		String s = "";
		String field = qc.getField();
		Object keyword = qc.getKeyword();
		Object[] keywords = qc.getKeywords();
		switch(qc.getOperation()){
			case Operation.LIKE:
				s = field+":*"+keyword+"*";
				break;
			case Operation.LEFTLIKE:
				s = field+":*"+keyword+"";
				break;
			case Operation.RIGHTLIKE:
				s = field+":"+keyword+"*";
				break;
			case Operation.RANGE: {
				if(keywords.length==2)
				{
					s = field + ":[" +  keywords[0] + " TO " + keywords[1] + "]";
				}
				else if(keywords.length==1)
				{
					s = field + ":[" +  keywords[0] + " TO *]";
				}
				else if(keyword!=null&&!keyword.equals(""))
				{
					s = field + ":[" +  keyword + " TO *]";
				}

				break;
			}
			case Operation.IN:
			{
				String in = "";
				if(keywords!=null && keywords.length>0)
				{
					for (Object key : keywords)
					{
						if(in!=null&&in.length()>0)
						{
							in+=" OR " +field+":"+key;
						}
						else
						{
							in = field+":"+key;
						}
					}
				}
				s = "("+in+")";
				break;
			}
			default:
				s = field+":\""+keyword+"\"";
		}

		return s;
	}

	/**
	 * 条件列表转字符串
	 * @param conditions
	 * @return
	 */
	private String conditionToString(List<QueryCondition> conditions)
	{
		String re ="";
		if(conditions!=null && conditions.size()>0)
		{
			for(QueryCondition condition:conditions){
				if(!re.equals(""))
				{
					switch (condition.getLogical())
					{
						case Logical.AND:
							re+=" AND ";
							break;
						case Logical.OR:
							re+=" OR ";
							break;
						case Logical.NOT:
							re+=" NOT ";
							break;
					}
				}

				re+=qcToString(condition);
			}
		}
		else {
			re ="*:*";
		}
		return re;
	}


	/**
	 * JSON条件转字符串
	 * @return
	 */
	public String jsonToCondition(String json) throws Exception
	{
		List<QueryCondition> conditions = new ArrayList<>();
		if(json!=null && json.length()>0) {
			ObjectMapper objectMapper = new ObjectMapper();
			if (json.startsWith("[") && json.endsWith("]")) {
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, QueryCondition.class);
				List<QueryCondition> qcList = objectMapper.readValue(json, javaType);
				if (qcList!=null && qcList.size()>0)
				{
					for(QueryCondition qc : qcList){
						conditions.add(qc);
					}
				}
			} else {
				QueryCondition qc = objectMapper.readValue(json, QueryCondition.class);
				conditions.add(qc);
			}

			return this.conditionToString(conditions);
		}
		else{
			return "";
		}
	}
	/*********************** 查询方法 ******************************************************************/
	/**
	 * 根据Query查询
	 * @return
	 */
	public DataList query(QueryEntity query) throws Exception {
		return queryJoin(query, null);
	}

	/**
	 * 根据Query查询，
	 * @return
	 */
	public DataList queryJoinJson(QueryEntity query,String joinsQuery) throws Exception {
		List<SolrJoinEntity>  joins = new ArrayList<>();
		if(joinsQuery!=null && joinsQuery.length()>0) {
			ObjectMapper objectMapper = new ObjectMapper();
			if (joinsQuery.startsWith("[") && joinsQuery.endsWith("]")) {
				JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, SolrJoinEntity.class);
				List<SolrJoinEntity> joinList = objectMapper.readValue(joinsQuery, javaType);
				if (joinList!=null && joinList.size()>0)
				{
					for(SolrJoinEntity join : joinList){
						joins.add(join);
					}
				}
			} else {
				SolrJoinEntity join = objectMapper.readValue(joinsQuery, SolrJoinEntity.class);
				joins.add(join);
			}
		}
		return queryJoin(query, joins);
	}

	/**
	 * 根据Query查询，
	 * @return
	 */
	public DataList queryJoin(QueryEntity query,List<SolrJoinEntity> joins) throws Exception {
		String table = query.getTableName();
		String q = "";
		String fq = "";
		String fl = query.getFields();
		String sort = query.getSort();
		long page = query.getPage();
		long rows = query.getRows();
		List<QueryCondition> conditions = query.getConditions();

		if(joins!=null&&joins.size()>0)
		{
			fq =conditionToString(conditions);
			for(SolrJoinEntity join :joins){
				q += join.toString();
			}
		}
		else{
			q=conditionToString(conditions);
		}

		return queryBySolr(table,q,sort,page,rows,fq,fl);
	}



	/**
	 * Keyrow条件查询
	 * @param rowkey
	 * @return
	 */
	public Map<String,Object> queryByRowkey(String table,String rowkey) throws Exception
	{
		Result rs = hbu.getResult(table, rowkey);
		return resultToMap(rs, "");
	}

	/**
	 * json条件查询
	 * @param json{'q':'*:*','fq':'','sort':'','page':1,'row':10}
	 * @return
	 */
	public DataList queryByJson(String table,String json) throws Exception
	{
		ObjectMapper objectMapper = new ObjectMapper();
		Map<String, String> query = objectMapper.readValue(json, Map.class);

		if(query!=null)
		{
			String q = query.containsKey("q")?query.get("q").toString():"";
			String fq = query.containsKey("q")?query.get("fq").toString():"";
			String sort = query.containsKey("sort")?query.get("sort").toString():"";
			long page = query.containsKey("page")?Long.parseLong(query.get("page").toString()):1;
			long rows = query.containsKey("rows")?Long.parseLong(query.get("rows").toString()):50;

			return queryBySolr(table,q,sort,page,rows,fq);
		}

		return null;
	}

	/**
	 * 查询
	 */
	public DataList queryBySolr(String table,String q,String sort,long page,long rows) throws Exception
	{
		return queryBySolr(table,q, sort, page, rows,"");
	}

	/**
	 * 查询
	 */
	public DataList queryBySolr(String table,String q,String sort,long page,long rows,String fq) throws Exception {
		return queryBySolr(table,q, sort, page, rows,fq,"");
	}

	/**
	 * 查询方法
	 */
	public DataList queryBySolr(String table,String q,String sort,long page,long rows,String fq,String fl) throws Exception{
		DataList re = new DataList();
		re.setPage(page);
		List<Map<String,Object>> data = new ArrayList<>();

		if(rows < 0) rows = 10;
		if(rows >100) rows = 100;
		if(page <0) page = 1;
		long start= (page-1) * rows;

		Map<String, String> sortMap = getSortMap(sort);

		/***** Solr查询 ********/
		SolrDocumentList solrList = solr.query(table, q, fq, sortMap, start,rows);

		/***** Hbase查询 ********/
		List<String> list = new ArrayList<String>();
		if(solrList!=null && solrList.getNumFound()>0)
		{
			re.setCount(solrList.getNumFound());
			for (SolrDocument doc : solrList){
				String rowkey = String.valueOf(doc.getFieldValue("rowkey"));
				list.add(rowkey);
			}
		}

		Result[] resultList = hbu.getResultList(table,list); //hbase结果集
		if(resultList!=null&&resultList.length>0){
			for (Result result :resultList) {
				Map<String,Object> obj = resultToMap(result, fl);

				if(obj!=null)
				{
					data.add(obj);
				}

			}
		}
		re.setList(data);

		return re;
	}

	/************************* 全文检索 ******************************************************/
	/**
	 * 全文检索
	 * fields 检索和返回字段，逗号分隔
	 * query 空格分隔
	 */
	public DataList getLucene(String table,String fields,String query,String sort,long page,long rows) throws Exception{

		String[] queryList = query.split(" ");
		String[] fieldList = fields.split(",");
		String q = "";
		for(String fieldName :fieldList)
		{
			for(String item :queryList) {
				if (q.length() == 0) {
					q = fieldName + ":" + item;
				} else {
					q += " OR " + fieldName + ":" + item;
				}
			}
		}

		return queryBySolr(table,q, sort, page, rows, "", fields);
	}
}
