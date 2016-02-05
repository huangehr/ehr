package com.yihu.ehr.util.parm;

import org.apache.commons.lang.StringUtils;

/**
 * URL 查询串解析器。将 {@link URLQueryBuilder} 中产生的查询字符串反解析。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:17
 */
public class URLQueryParser {
    public static int DefaultPage = 1;
    public static int DefaultSize = 15;

    private String fields;
    private String filters;
    private String orders;
    private int page;
    private int size;

    public URLQueryParser(String fields, String filters, String orders, int page, int size) {
        this.fields = fields;
        this.filters = filters;
        this.orders = orders;

        this.page = page;
        this.size = size;
    }

    /**
     * 生成搜索页面模型。
     * 
     * @return
     */
    public PageModel makePageModel() {
        PageModel pageModel = new PageModel(page, size);
        if (StringUtils.isNotEmpty(fields)) pageModel.setResult(fields.split(","));
        if (StringUtils.isNotEmpty(orders)) pageModel.setOrder(orders.split(","));

        pageModel.setPage(page == 0 ? DefaultPage : page);
        pageModel.setRows(size == 0 ? DefaultSize : size);

        return pageModel;
    }
}
