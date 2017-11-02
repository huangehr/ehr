package com.yihu.ehr.model.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 列表对象
 */
public class ListResult extends Result implements Serializable {

    private int pageSize = 10;

    private int currPage = 1;

    private int totalPage;

    private int totalCount;

    private List detailModelList;

    private Object obj;

    public ListResult() {

    }

    public ListResult(int currPage,int pageSize) {
        this.pageSize = pageSize;
        this.currPage = currPage;
    }


    public static ListResult fromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, ListResult.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
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

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getTotalCount() {
        if (totalCount == 0 && detailModelList != null) {
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

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}