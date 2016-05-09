package com.yihu.ehr.query.services;

import com.yihu.ehr.query.common.model.DataList;
import com.yihu.ehr.query.common.model.SolrGroupEntity;
import com.yihu.ehr.query.solr.SolrUtil;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.PivotField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Solr统计查询服务
 * add by hzp at 2016-04-26
 */
@Service
public class SolrQuery {

	@Autowired
	SolrUtil solr;

	/******************************** Count方法 ******************************************************/
	/**
	 * 获取总条数
	 * @param queryString
	 * @return
	 */
	public long count(String table,String queryString) throws Exception {
		return solr.count(table, queryString);
	}

	/**
	 * 单分组统计Count
	 * @return
	 */
	public DataList getGroupCount(String table,String groupField) throws Exception {
		return getGroupCount(table,groupField,"", 1, 1000);
	}

	/**
	 * 单分组统计Count(分页)
	 */
	public DataList getGroupCount(String table,String groupField,String q,int page,int rows) throws Exception {
		DataList re = new DataList();
		re.setName(table);
		re.setPage(page);
		re.setSize(rows);
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();

		if(rows < 0) rows = 10;
		if(rows >100) rows = 100;
		if(page <0) page = 1;
		int start= (page-1) * rows;

		/***** Solr查询 ********/
		Map<String,Long> list = solr.groupCount(table, q, groupField, start,rows);

		if(list!=null && list.size()>0){
			for (Map.Entry<String, Long> item : list.entrySet()) {
				Map<String,Object> obj = new HashMap<>();
				obj.put(groupField, item.getKey());
				obj.put("count", item.getValue());
				data.add(obj);
			}
		}
		re.setList(data);

		return re;
	}

	/**
	 * 递归获取分组数据
	 * @return
	 */
	private List<Map<String,Object>> recGroupCount(String table,List<SolrGroupEntity> grouplist,int num,List<Map<String,Object>> preList,String q) throws Exception
	{
		String conditionName = "$condition";
		if (num==grouplist.size()-1)
		{
			List<Map<String,Object>> list =  new ArrayList<Map<String,Object>>();//返回集合
			SolrGroupEntity group = grouplist.get(num);
			String groupName = group.getGroupField();

			for(Map<String,Object> preObj:preList) {
				String query = preObj.get(conditionName).toString();

				if(q!=null && !q.equals("") && !q.equals("*:*"))
				{
					query += " AND " +q;
				}

				//最后一级Solr统计
				Map<String,Long> maps = solr.groupCount(table, query, groupName, 0, 1000);
				if(maps!=null && maps.size()>0)
				{
					for (String key : maps.keySet())
					{
						Map<String,Object> obj = new HashMap<String,Object>();
						obj.putAll(preObj); //深拷贝
						obj.put(groupName,key);
						obj.put("$count",maps.get(key));
						obj.remove(conditionName);
						list.add(obj);
					}
				}
			}

			return list;
		}
		else
		{
			List<Map<String,Object>> list =  new ArrayList<Map<String,Object>>();//返回集合

			SolrGroupEntity group = grouplist.get(num); //当前分组
			Map<String,String> groupMap = group.getGroupCondition(); //当前分组项
			String groupName = group.getGroupField();

			if(preList!=null) {
				//遍历上级递归数据
				for (Map<String,Object> preObj : preList) {
					//遍历当前分组数据
					for (Map.Entry<String, String> item : groupMap.entrySet()) {
						Map<String,Object> obj = new HashMap<String,Object>();
						obj.putAll(preObj); //深拷贝
						obj.put(groupName, item.getKey());
						//拼接过滤条件
						String cond = obj.get(conditionName).toString();
						cond+=" AND "+item.getValue();
						obj.put(conditionName,cond);
						list.add(obj);
					}
				}
			}
			else{ //第一次遍历
				for (Map.Entry<String, String> item : groupMap.entrySet()) {
					Map<String,Object> obj = new HashMap<String,Object>();
					obj.put(groupName, item.getKey());
					obj.put(conditionName,item.getValue());
					list.add(obj);
				}
			}

			return recGroupCount(table,grouplist, num + 1, list,q);
		}

	}

	/**
	 * 多级分组统计Count
	 */
	public DataList getGroupMult(String table,String[] groups) throws Exception
	{
		return getGroupMult(table,groups,null,"");
	}

	/**
	 * 多级分组统计Count（包含自定义分组）
	 */
	public DataList getGroupMult(String table,String[] groups,List<SolrGroupEntity> customGroup,String q) throws Exception{
		DataList re = new DataList();
		re.setName(table);
		if(groups!=null && groups.length>0)
		{
			List<SolrGroupEntity> grouplist = new ArrayList<SolrGroupEntity>();
			if(customGroup!=null && customGroup.size() > 0)
			{
				grouplist = customGroup;
			}

			//遍历字段分组
			List<FacetField> facets = solr.groupCount(table, q, groups);
			for (FacetField facet : facets) {
				String groupName = facet.getName();
				SolrGroupEntity group = new SolrGroupEntity(groupName);

				List<FacetField.Count> counts = facet.getValues();
				for (FacetField.Count count : counts) {
					String value = count.getName();
					group.putGroupCondition(value, groupName + ":" + value);
				}

				grouplist.add(group);
			}

			List<Map<String, Object>> data = recGroupCount(table,grouplist, 0, null,q);
			re.setList(data);
		}
		else
		{
			//全部自定义条件
			System.out.print("All custom condition!");
		}

		return re;
	}

	/**
	 * 多字段分组统计Count
	 * groupFields 逗号分隔
	 * @return
	 */
	public List<PivotField> getGroupMult(String table,String groupFields,String q,int page,int rows) throws Exception
	{
		if(rows < 0) rows = 10;
		if(rows >100) rows = 100;
		if(page <0) page = 1;
		int start= (page-1) * rows;
		List<PivotField> list = solr.groupCountMult(table,q,groupFields,start,rows);

		return list;
	}

	/************************* 数值统计 **********************************************/

	/**
	 * 递归数值统计
	 */
	private List<Map<String, Object>> recStats(String table,String[] stats,List<SolrGroupEntity> grouplist,String q,int num,List<Map<String, Object>> preList) throws Exception
	{
		String conditionName = "$condition";
		if (num==grouplist.size()-1)
		{
			List<Map<String, Object>> list =  new ArrayList<Map<String, Object>>();//返回集合
			SolrGroupEntity group = grouplist.get(num);
			Map<String,String> groupMap = group.getGroupCondition(); //当前分组项
			String groupName = group.getGroupField();
			DecimalFormat df = new DecimalFormat("#.00");

			if(preList!=null && preList.size()>0)
			{
				for(Map<String, Object> preObj:preList) {
					String query = preObj.get(conditionName).toString();

					if((q!=null && !q.equals(""))&&(query!=null && !query.equals("")))
					{
						query= q+" AND "+query;
					}
					else
					{
						query = q+query;
					}

					//根据条件最后一级数值统计
					Map<String,List<FieldStatsInfo>> statsMap = new HashMap<String,List<FieldStatsInfo>>();
					//所有统计字段
					for(String stat : stats)
					{
						List<FieldStatsInfo> statsList = solr.getStats(table, query,stat, groupName);
						statsMap.put(stat,statsList);
					}

					if(statsMap!=null&& statsMap.size()>0)
					{
						List<FieldStatsInfo> statsFirst = statsMap.get(stats[0]);
						if(statsFirst!=null){
							for (int i=0;i<statsFirst.size();i++)
							{
								String groupItem = statsFirst.get(i).getName();
								Map<String,Object> obj = new HashMap<String,Object>();
								obj.putAll(preObj); //深拷贝
								obj.put(groupName,groupItem==null?"":groupItem);
								obj.remove(conditionName);

								for(String stat : stats)
								{
									List<FieldStatsInfo> statsList = statsMap.get(stat);
									FieldStatsInfo item = statsList.get(i);
									obj.put("$count_"+stat, item.getCount());
									obj.put("$sum_"+stat, df.format(item.getSum()));
									obj.put("$avg_"+stat, df.format(item.getMean()));
									obj.put("$max_"+stat, df.format(item.getMax()));
									obj.put("$min_"+stat, df.format(item.getMin()));
								}

								list.add(obj);
							}
						}
					}
				}
			}
			else{
				//最后一级数值统计
				Map<String,List<FieldStatsInfo>> statsMap = new HashMap<String,List<FieldStatsInfo>>();
				//所有统计字段
				for(String stat : stats)
				{
					List<FieldStatsInfo> statsList = solr.getStats(table, q,stat, groupName);
					statsMap.put(stat,statsList);
				}

				if(statsMap!=null&& statsMap.size()>0)
				{
					List<FieldStatsInfo> statsFirst = statsMap.get(stats[0]);
					for (int i=0;i<statsFirst.size();i++)
					{
						String groupItem = statsFirst.get(i).getName();
						Map<String,Object> obj = new HashMap<String,Object>();
						obj.put(groupName,groupItem==null?"":groupItem);
						obj.remove(conditionName);

						for(String stat : stats)
						{
							List<FieldStatsInfo> statsList = statsMap.get(stat);
							FieldStatsInfo item = statsList.get(i);
							obj.put("$count_"+stat, item.getCount());
							obj.put("$sum_"+stat, df.format(item.getSum()));
							obj.put("$avg_"+stat, df.format(item.getMean()));
							obj.put("$max_"+stat, df.format(item.getMax()));
							obj.put("$min_"+stat, df.format(item.getMin()));
						}

						list.add(obj);
					}
				}
			}

			return list;
		}
		else
		{
			List<Map<String,Object>> list =  new ArrayList<Map<String,Object>>();//返回集合

			SolrGroupEntity group = grouplist.get(num); //当前分组
			Map<String,String> groupMap = group.getGroupCondition(); //当前分组项
			String groupName = group.getGroupField();

			if(preList!=null) {
				//遍历上级递归数据
				for (Map<String,Object> preObj : preList) {
					//遍历当前分组数据
					for (Map.Entry<String, String> item : groupMap.entrySet()) {
						Map<String,Object> obj = new HashMap<String,Object>();
						obj.putAll(preObj); //深拷贝
						obj.put(groupName, item.getKey());
						//拼接过滤条件
						String cond = obj.get(conditionName).toString();
						cond+=" AND "+item.getValue();
						obj.put(conditionName,cond);
						list.add(obj);
					}
				}
			}
			else{ //第一次遍历
				for (Map.Entry<String, String> item : groupMap.entrySet()) {
					Map<String,Object> obj = new HashMap<String,Object>();
					obj.put(groupName, item.getKey());
					obj.put(conditionName,item.getValue());
					list.add(obj);
				}
			}

			return recStats(table,stats,grouplist, q,num + 1, list);
		}

	}

	/**
	 * 多级数值统计
	 */
	public DataList getStats(String table,String statsFields,String groupFields) throws Exception{
		return getStats(table,statsFields,groupFields,"",null);
	}

	/**
	 * 多级数值统计
	 */
	public DataList getStats(String table,String statsFields,String groupFields,String q) throws Exception{
		return getStats(table,statsFields,groupFields,q,null);
	}

	/**
	 * 多级数值统计
	 * statsFields 统计字段
	 * groupFields 分组字段
	 * q 查询条件
	 * customGroup 额外自定义分组
	 */
	public DataList getStats(String table,String statsFields,String groupFields,String q,List<SolrGroupEntity> customGroup) throws Exception{
		String[] groups =groupFields.split(",");
		String[] stats = statsFields.split(",");

		DataList re = new DataList();
		re.setName(table);
		re.setPage(1);
		if(groups!=null && groups.length>0)
		{
			List<SolrGroupEntity> grouplist = new ArrayList<SolrGroupEntity>();
			if(customGroup!=null && customGroup.size() > 0)
			{
				grouplist = customGroup;
			}

			//遍历字段分组
			List<FacetField> facets = solr.groupCount(table, "", groups);
			for (FacetField facet : facets) {
				String groupName = facet.getName();
				SolrGroupEntity group = new SolrGroupEntity(groupName);

				List<FacetField.Count> counts = facet.getValues();
				for (FacetField.Count count : counts) {
					String value = count.getName();
					group.putGroupCondition(value, groupName + ":" + value);
				}

				grouplist.add(group);
			}

			List<Map<String,Object>> data = recStats(table,stats,grouplist,q, 0, null);
			re.setList(data);
		}
		else
		{
			//全部自定义条件
			System.out.print("All custom condition!");
		}


		return re;
	}


}
