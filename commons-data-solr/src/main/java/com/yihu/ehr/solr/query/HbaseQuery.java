package com.yihu.ehr.solr.query;

import com.yihu.ehr.hbase.HBaseUtil;
import com.yihu.ehr.solr.service.SolrUtil;
import net.sf.json.JSONObject;

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
				JSONObject obj = JSONObject.fromObject(sort);
				if(obj!=null){
					Iterator it = obj.keys();
					while(it.hasNext()){
						String field = String.valueOf(it.next());
						String value = obj.getString(field);

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
	private JSONObject resultToJson(Result result,String fl){

		String rowkey = Bytes.toString(result.getRow());
		if(rowkey!=null && rowkey.length()>0)
		{

			JSONObject obj = new JSONObject();
			obj.put("rowkey", rowkey);
			for  (Cell cell : result.rawCells()) {
				String fieldName = Bytes.toString(CellUtil.cloneQualifier(cell));
				String fieldValue = Bytes.toString(CellUtil. cloneValue(cell));
				if(fl!=null&&!fl.equals(""))
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
	 * 条件列表转字符串
	 * @param conditions
	 * @return
	 */
	private String conditionToString(List<HbaseQueryCondition> conditions)
	{
		String re ="";
		if(conditions!=null && conditions.size()>0)
		{
			for(HbaseQueryCondition condition:conditions){
				if(!re.equals(""))
				{
					switch (condition.getLogical())
					{
						case AND:
							re+=" AND ";
							break;
						case OR:
							re+=" OR ";
							break;
						case NOT:
							re+=" NOT ";
							break;
					}
				}

				re+=condition.toString() +" ";
			}
		}
		else {
			re ="*:* ";
		}
		return re;
	}

    /*********************** 查询方法 ******************************************************************/
	/**
	 * 根据Query查询
	 * @return
	 */
	public HbaseList query(HbaseQueryEntity query) throws Exception
	{
		String table = query.getTable();
		String q = "";
		String fq = "";
		String sort = query.getSort();
		long page = query.getPage();
		long rows = query.getRows();
		List<HbaseQueryCondition> conditions = query.getConditions();
		List<HbaseQueryJoin> joins = query.getJoins();

		if(joins!=null&&joins.size()>0)
		{
			fq =conditionToString(conditions);
			for(HbaseQueryJoin join :joins){
				q+=join.toString();
			}
		}
		else{
			q=conditionToString(conditions);
		}

		return queryBySolr(table,q,sort,page,rows,fq);

	}

	/**
	 * Keyrow条件查询
	 * @param rowkey
	 * @return
	 */
	public JSONObject queryByRowkey(String table,String rowkey) throws Exception
	{
		Result rs = hbu.getResult(table, rowkey);
		return resultToJson(rs,"");
	}

	/**
	 * json条件查询
	 * @param json{'q':'*:*','fq':'','sort':'','page':1,'row':10}
	 * @return
	 */
	public HbaseList queryByJson(String table,String json) throws Exception
	{
		JSONObject query = JSONObject.fromObject(json);

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
	public HbaseList queryBySolr(String table,String q,String sort,long page,long rows) throws Exception
	{
		return queryBySolr(table,q, sort, page, rows,"");
	}

	/**
	 * 查询
	 */
	public HbaseList queryBySolr(String table,String q,String sort,long page,long rows,String fq) throws Exception {
		return queryBySolr(table,q, sort, page, rows,fq,"");
	}

	/**
	 * 查询方法
	 */
	public HbaseList queryBySolr(String table,String q,String sort,long page,long rows,String fq,String fl) throws Exception{
		HbaseList re = new HbaseList();
		re.setPage(page);
		List<JSONObject> data = new ArrayList<JSONObject>();

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
				JSONObject obj = resultToJson(result,fl);

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
	public HbaseList getLucene(String table,String fields,String query,String sort,long page,long rows) throws Exception{

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
