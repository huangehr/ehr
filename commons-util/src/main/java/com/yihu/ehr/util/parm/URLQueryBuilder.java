package com.yihu.ehr.util.parm;

import java.util.ArrayList;
import java.util.List;

/**
 * URL 查询构建器，在设置好需要的字段后，生成查询字段以方便URL使用。字段请使用Model中的名称。
 * 查询过滤器只支持单实体查询，不支持多实体联合查询，若需要联合查询，需要调用者自己根据需要调用多个服务的数据再合并。
 *
 * 解析使用 {@link URLQueryParser}。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 9:46
 */
public class URLQueryBuilder {
    private List<String> fields = new ArrayList<>();
    private List<String> sorter = new ArrayList<>();
    private int pageSize = 20;      // default page size
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
    public URLQueryBuilder addFilter(String field, String operator, Object value, String group) {
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

        if (fields.size() > 0) stringBuilder.append(String.join(",", fields)).append("&");
        if (sorter.size() > 0) stringBuilder.append(String.join(",", sorter)).append("&");

        stringBuilder.append("page=").append(pageNo).append("&").append("size=").append(pageSize);

        return stringBuilder.toString();
    }
}
