package com.yihu.ehr.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;

/**
 * URL 查询构建器，在设置好需要的字段后，生成查询字段以方便URL使用。字段请使用Model中的名称。
 * 查询过滤器只支持单实体查询，不支持多实体联合查询，若需要联合查询，需要调用者自己根据需要调用多个服务的数据再合并。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 9:46
 */
public class URLQueryBuilder {
    private List<String> fields = new ArrayList<>();
    private List<String> filters = new ArrayList<>();
    private List<String> sorter = new ArrayList<>();
    private int pageSize = 15;      // default page size
    private int pageNo = 1;         // default page number

    public URLQueryBuilder() {
    }

    /**
     * 添加要返回的字段。
     *
     * @param field
     * @return
     */
    public URLQueryBuilder addField(String field) {
        fields.add(field);

        return this;
    }

    /**
     * 添加过滤条件，
     *
     * @param field
     * @param operator
     * @param value
     * @param group    为空表示与其他的过滤条件之间的关系为 and. 若组名已存在，则与同名的过滤条件之间的关系为 or.
     * @return
     */
    public URLQueryBuilder addFilter(String field, String operator, String value, String group) {
        filters.add(field + operator + value + (StringUtils.isNotEmpty(group) ? " " + group : ""));

        return this;
    }

    public URLQueryBuilder addSorter(String field, boolean asc) {
        sorter.add(asc ? "+" : "-" + field);

        return this;
    }

    public URLQueryBuilder setPageSize(int size) {
        this.pageSize = size;

        return this;
    }

    public URLQueryBuilder setPageNumber(int pageNo) {
        this.pageNo = pageNo;

        return this;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        if (fields.size() > 0) {
            stringBuilder.append("fields=");
            stringBuilder.append(String.join(";", fields)).append("&");
        }
        if (filters.size() > 0) {
            stringBuilder.append("filters=");
            stringBuilder.append(String.join(";", filters)).append("&");
        }
        if (sorter.size() > 0) {
            stringBuilder.append("sort=");
            stringBuilder.append(String.join(";", sorter)).append("&");
        }
        stringBuilder.append("page=").append(pageNo).append("&").append("size=").append(pageSize);
        return stringBuilder.toString();
    }

    public String toString(Map<String, Object>  params) {
        StringBuilder stringBuilder = new StringBuilder();
        int i=0;
        for (Object responseBody : params.keySet()) {
            i++;
            String encodedName = encode((String) responseBody);
            String encodedValue = encode(String.valueOf(params.get(responseBody)));
            if (i>1){
                stringBuilder.append("&");
            }
            stringBuilder.append(encodedName).append("=");
            stringBuilder.append(encodedValue);
        }

        return stringBuilder.toString();
    }

    public String encode(String source){
        try {
            return URLEncoder.encode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public String decode(String source){
        try {
            return URLDecoder.decode(source, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}
