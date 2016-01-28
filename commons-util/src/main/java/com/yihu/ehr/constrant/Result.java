package com.yihu.ehr.constrant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.util.log.LogService;

import java.io.Serializable;
import java.util.List;

/**
 * 结果VO
 * @author llh
 *
 */
public class Result implements Serializable{

	private static final long serialVersionUID = 2076324875575488461L;
	
	private boolean successFlg;

	private int pageSize = 10;

	private int currPage;

	private int totalPage;

	private int totalCount;

	private List detailModelList;

	private Object obj;

	private String errorMsg;

	private int errorCode;

	public String toJson(){
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            LogService.getLogger().error(e.getMessage());
            return "";
        }
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

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
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
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public boolean isSuccessFlg() {
		return successFlg;
	}

	public void setSuccessFlg(boolean successFlg) {
		this.successFlg = successFlg;
	}

	public List getDetailModelList() {
		return detailModelList;
	}

	public void setDetailModelList(List detailModelList) {
		this.detailModelList = detailModelList;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("result=");
		sb.append(successFlg);
		sb.append("obj=");
		sb.append(detailModelList);
		sb.append("errorMsg");
		sb.append(errorMsg);
		sb.append("errorCode");
		sb.append(errorCode);
		return sb.toString();
	}

	public Result getResult(List detaiModelList, int totalCount, int currPage, int rows) {

		Result result = new Result();
		result.setSuccessFlg(true);
		result.setDetailModelList(detaiModelList);
		result.setTotalCount(totalCount);
		result.setCurrPage(currPage);
		result.setPageSize(rows);
		if(result.getTotalCount()%result.getPageSize()>0){
			result.setTotalPage((result.getTotalCount()/result.getPageSize())+1);
		}else {
			result.setTotalPage(result.getTotalCount()/result.getPageSize());
		}
		return result;
	}
}