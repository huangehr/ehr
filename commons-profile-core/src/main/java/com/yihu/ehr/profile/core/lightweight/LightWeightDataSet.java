package com.yihu.ehr.profile.core.lightweight;

import com.yihu.ehr.profile.core.structured.FullWeightDataSet;

/**
 * 档案数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class LightWeightDataSet extends FullWeightDataSet {
    //机构健康档案Url
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
