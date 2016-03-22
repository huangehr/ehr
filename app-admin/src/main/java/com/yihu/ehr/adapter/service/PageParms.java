package com.yihu.ehr.adapter.service;

import java.util.HashMap;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/3/22
 */
public class PageParms extends HashMap<String, Object> {

    private int size = 15;
    private int page = 1;
    private String fields = "";
    private String filters = "";
    private String sorts = "";

    public PageParms() {

    }

    public PageParms(String fields, String filters, String sorts, int size, int page) {

        setValues(fields, filters, sorts, size, page);
    }

    public PageParms(String filters, int size) {

        setValues("", filters, "", size, 1);
    }

    public PageParms(String filters) {

        setValues("", filters, "", 10000, 1);
    }

    private void setValues(String fields, String filters, String sorts, int size, int page){

        put("fields", fields);
        put("filters", filters);
        put("sorts", sorts);
        put("size", size);
        put("page", page);
    }
}
