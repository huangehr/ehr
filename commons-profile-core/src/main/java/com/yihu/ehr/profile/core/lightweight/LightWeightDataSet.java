package com.yihu.ehr.profile.core.lightweight;

import com.yihu.ehr.profile.core.commons.DataSet;

/**
 * 档案数据集。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.16 11:13
 */
public class LightWeightDataSet extends DataSet{
    private String remotePath;

    public String getRemotePath() {
        return remotePath;
    }

    public void setRemotePath(String remotePath) {
        this.remotePath = remotePath;
    }
}
