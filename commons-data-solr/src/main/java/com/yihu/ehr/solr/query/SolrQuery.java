package com.yihu.ehr.solr.query;

import com.yihu.ehr.solr.service.SolrUtil;
import net.sf.json.JSONObject;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FieldStatsInfo;
import org.apache.solr.client.solrj.response.PivotField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Solrͳ�Ʋ�ѯ����
 * add by hzp at 2016-04-26
 */
@Service
public class SolrQuery {

	@Autowired
	SolrUtil solr;

	/******************************** Count���� ******************************************************/
	/**
	 * ��ȡ������
	 * @param queryString
	 * @return
	 */
	public long count(String table,String queryString) throws Exception {
		return solr.count(table, queryString);
	}

	/**
	 * ������ͳ��Count
	 * @return
	 */
	public HbaseList getGroupCount(String table,String groupField) throws Exception {
		return getGroupCount(table,groupField,"", 1, 1000);
	}

	/**
	 * ������ͳ��Count(��ҳ)
	 */
	public HbaseList getGroupCount(String table,String groupField,String q,int page,int rows) throws Exception {
		HbaseList re = new HbaseList();
		re.setPage(page);
		List<JSONObject> data = new ArrayList<JSONObject>();

		if(rows < 0) rows = 10;
		if(rows >100) rows = 100;
		if(page <0) page = 1;
		int start= (page-1) * rows;

		/***** Solr��ѯ ********/
		Map<String,Long> list = solr.groupCount(table, q, groupField, start,rows);

		if(list!=null && list.size()>0){
			for (Map.Entry<String, Long> item : list.entrySet()) {
				JSONObject obj = new JSONObject();
				obj.put(groupField, item.getKey());
				obj.put("count", item.getValue());
				data.add(obj);
			}
		}
		re.setList(data);

		return re;
	}

	/**
	 * �ݹ��ȡ��������
	 * @return
	 */
	private List<JSONObject> recGroupCount(String table,List<HbaseQueryGroup> grouplist,int num,List<JSONObject> preList,String q) throws Exception
	{
		String conditionName = "$condition";
		if (num==grouplist.size()-1)
		{
			List<JSONObject> list =  new ArrayList<JSONObject>();//���ؼ���
			HbaseQueryGroup group = grouplist.get(num);
			String groupName = group.getGroupField();

			for(JSONObject preObj:preList) {
				String query = preObj.optString(conditionName);

				if(q!=null && !q.equals("") && !q.equals("*:*"))
				{
					query += " AND " +q;
				}

				//���һ��Solrͳ��
				Map<String,Long> maps = solr.groupCount(table, query, groupName, 0, 1000);
				if(maps!=null && maps.size()>0)
				{
					for (String key : maps.keySet())
					{
						JSONObject obj = JSONObject.fromObject(preObj); //����֮ǰjson����
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
			List<JSONObject> list =  new ArrayList<JSONObject>();//���ؼ���

			HbaseQueryGroup group = grouplist.get(num); //��ǰ����
			Map<String,String> groupMap = group.getGroupCondition(); //��ǰ������
			String groupName = group.getGroupField();

			if(preList!=null) {
				//�����ϼ��ݹ�����
				for (JSONObject preObj : preList) {
					//������ǰ��������
					for (Map.Entry<String, String> item : groupMap.entrySet()) {
						JSONObject obj = JSONObject.fromObject(preObj); //����֮ǰjson����
						obj.put(groupName, item.getKey());
						//ƴ�ӹ�������
						String cond = obj.optString(conditionName);/***/
						cond+=" AND "+item.getValue();
						obj.put(conditionName,cond);
						list.add(obj);

					}
				}
			}
			else{ //��һ�α���
				for (Map.Entry<String, String> item : groupMap.entrySet()) {
					JSONObject obj = new JSONObject();
					obj.put(groupName, item.getKey());
					obj.put(conditionName,item.getValue());
					list.add(obj);
				}
			}

			return recGroupCount(table,grouplist, num + 1, list,q);
		}

	}

	/**
	 * �༶����ͳ��Count
	 */
	public HbaseList getGroupMult(String table,String[] groups) throws Exception
	{
		return getGroupMult(table,groups,null,"");
	}

	/**
	 * �༶����ͳ��Count�������Զ�����飩
	 */
	public HbaseList getGroupMult(String table,String[] groups,List<HbaseQueryGroup> customGroup,String q) throws Exception{
		HbaseList re = new HbaseList();

		if(groups!=null && groups.length>0)
		{
			List<HbaseQueryGroup> grouplist = new ArrayList<HbaseQueryGroup>();
			if(customGroup!=null && customGroup.size() > 0)
			{
				grouplist = customGroup;
			}

			//�����ֶη���
			List<FacetField> facets = solr.groupCount(table, q, groups);
			for (FacetField facet : facets) {
				String groupName = facet.getName();
				HbaseQueryGroup group = new HbaseQueryGroup(groupName);

				List<FacetField.Count> counts = facet.getValues();
				for (FacetField.Count count : counts) {
					String value = count.getName();
					group.putGroupCondition(value, groupName + ":" + value);
				}

				grouplist.add(group);
			}

			List<JSONObject> data = recGroupCount(table,grouplist, 0, null,q);
			re.setList(data);
		}
		else
		{
			//ȫ���Զ�������
			System.out.print("All custom condition!");
		}

		return re;
	}
	
	/**
	 * ���ֶη���ͳ��Count
	 * groupFields ���ŷָ�
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

	/************************* ��ֵͳ�� **********************************************/

	/**
	 * �ݹ���ֵͳ��
	 */
	private List<JSONObject> recStats(String table,String[] stats,List<HbaseQueryGroup> grouplist,String q,int num,List<JSONObject> preList) throws Exception
	{
		String conditionName = "$condition";
		if (num==grouplist.size()-1)
		{
			List<JSONObject> list =  new ArrayList<JSONObject>();//���ؼ���
			HbaseQueryGroup group = grouplist.get(num);
			Map<String,String> groupMap = group.getGroupCondition(); //��ǰ������
			String groupName = group.getGroupField();
			DecimalFormat df = new DecimalFormat("#.00");

			if(preList!=null && preList.size()>0)
			{
				for(JSONObject preObj:preList) {
					String query = preObj.optString(conditionName);

					if((q!=null && !q.equals(""))&&(query!=null && !query.equals("")))
					{
						query= q+" AND "+query;
					}
					else
					{
						query = q+query;
					}

					//�����������һ����ֵͳ��
					Map<String,List<FieldStatsInfo>> statsMap = new HashMap<String,List<FieldStatsInfo>>();
					//����ͳ���ֶ�
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
								JSONObject obj = JSONObject.fromObject(preObj); //����֮ǰjson����
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
				//���һ����ֵͳ��
				Map<String,List<FieldStatsInfo>> statsMap = new HashMap<String,List<FieldStatsInfo>>();
				//����ͳ���ֶ�
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
						JSONObject obj = new JSONObject();
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
			List<JSONObject> list =  new ArrayList<JSONObject>();//���ؼ���

			HbaseQueryGroup group = grouplist.get(num); //��ǰ����
			Map<String,String> groupMap = group.getGroupCondition(); //��ǰ������
			String groupName = group.getGroupField();

			if(preList!=null) {
				//�����ϼ��ݹ�����
				for (JSONObject preObj : preList) {
					//������ǰ��������
					for (Map.Entry<String, String> item : groupMap.entrySet()) {
						JSONObject obj = JSONObject.fromObject(preObj); //����֮ǰjson����
						obj.put(groupName, item.getKey());
						//ƴ�ӹ�������
						String cond = obj.optString(conditionName);/***/
						cond+=" AND "+item.getValue();
						obj.put(conditionName,cond);
						list.add(obj);
					}
				}
			}
			else{ //��һ�α���
				for (Map.Entry<String, String> item : groupMap.entrySet()) {
					JSONObject obj = new JSONObject();
					obj.put(groupName, item.getKey());
					obj.put(conditionName,item.getValue());
					list.add(obj);
				}
			}

			return recStats(table,stats,grouplist, q,num + 1, list);
		}

	}

	/**
	 * �༶��ֵͳ��
	 */
	public HbaseList getStats(String table,String statsFields,String groupFields) throws Exception{
		return getStats(table,statsFields,groupFields,"",null);
	}

	/**
	 * �༶��ֵͳ��
	 */
	public HbaseList getStats(String table,String statsFields,String groupFields,String q) throws Exception{
		return getStats(table,statsFields,groupFields,q,null);
	}

	/**
	 * �༶��ֵͳ��
	 * statsFields ͳ���ֶ�
	 * groupFields �����ֶ�
	 * q ��ѯ����
	 * customGroup �����Զ������
	 */
	public HbaseList getStats(String table,String statsFields,String groupFields,String q,List<HbaseQueryGroup> customGroup) throws Exception{
		String[] groups =groupFields.split(",");
		String[] stats = statsFields.split(",");

		HbaseList re = new HbaseList();
		re.setPage(1);
		if(groups!=null && groups.length>0)
		{
			List<HbaseQueryGroup> grouplist = new ArrayList<HbaseQueryGroup>();
			if(customGroup!=null && customGroup.size() > 0)
			{
				grouplist = customGroup;
			}

			//�����ֶη���
			List<FacetField> facets = solr.groupCount(table, "", groups);
			for (FacetField facet : facets) {
				String groupName = facet.getName();
				HbaseQueryGroup group = new HbaseQueryGroup(groupName);

				List<FacetField.Count> counts = facet.getValues();
				for (FacetField.Count count : counts) {
					String value = count.getName();
					group.putGroupCondition(value, groupName + ":" + value);
				}

				grouplist.add(group);
			}

			List<JSONObject> data = recStats(table,stats,grouplist,q, 0, null);
			re.setList(data);
		}
		else
		{
			//ȫ���Զ�������
			System.out.print("All custom condition!");
		}


		return re;
	}


}
