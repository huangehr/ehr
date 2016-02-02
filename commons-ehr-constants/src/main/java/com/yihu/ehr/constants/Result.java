package com.yihu.ehr.constants;

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


}