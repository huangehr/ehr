package com.yihu.ehr.paient.paientIdx.model;

/**
 * Created by zqb on 2015/6/17.
 */
public class MappingColumn  {
    public Integer id;
    public String mapKey;
    public String tableName;
    public String columnName;
    public String searchType;

    public Integer getId() {
        return id;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getMapKey() {
        return mapKey;
    }

    public String getSearchType() {
        return searchType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setColumnName(String columnName) {
        this.columnName=columnName;
    }

    public void setMapKey(String mapKey) {
        this.mapKey=mapKey;
    }

    public void setSearchType(String searchType) {
        this.searchType=searchType;
    }

    public void setTableName(String tableName) {
        this.tableName=tableName;
    }
}
