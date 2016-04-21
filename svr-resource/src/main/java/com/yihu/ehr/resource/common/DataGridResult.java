package com.yihu.ehr.resource.common;

import com.yihu.ehr.dbhelper.common.DBList;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * 列表对象 add by hzp at 20160401
 */
public class DataGridResult extends Result implements Serializable{

	private int pageSize = 10;

	private int currPage = 1;

	private int totalPage;

	private int totalCount;

	private List detailModelList;

	public DataGridResult()
	{

	}

	public DataGridResult(int pageSize, int currPage) {
		this.pageSize = pageSize;
		this.currPage = currPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getTotalPage() {
		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}
		return totalPage;
	}


	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getStart() {
		if (currPage != 0) {
			return (currPage - 1) * pageSize;
		}
		return 0;
	}

	public int getEnd() {
		if (currPage != 0) {
			return currPage * pageSize;
		}
		return 0;
	}

	public int getTotalCount() {
		if(totalCount==0&&detailModelList!=null)
		{
			totalCount = detailModelList.size();
		}
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}


	public List getDetailModelList() {
		return detailModelList;
	}

	public void setDetailModelList(List detailModelList) {
		this.detailModelList = detailModelList;
	}

	/**
	 * json转列表对象
	 * @return
	 */
	public static DataGridResult fromJson(String json)
	{
		return (DataGridResult) JSONObject.toBean(JSONObject.fromObject(json), DataGridResult.class);
	}

	public static DataGridResult fromDBList(DBList list)
	{
		DataGridResult re = new DataGridResult();
		if(list.getPage()!=null)
			re.setCurrPage(list.getPage());

		if(list.getCount()!=null)
		{
			Integer count = list.getCount();
			re.setTotalCount(count);
			if(list.getSize()!= null) {
				Integer size = list.getSize();
				re.setPageSize(count);
				
			}
		}
		else{
			re.setSuccessFlg(false);
		}



		if(list.getList()!=null)
			re.setDetailModelList(JSONArray.fromObject(list.getList().toString()));
		return re;
	}
}