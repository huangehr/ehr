package com.yihu.ehr.profile.core.profile.slice;

import com.yihu.ehr.profile.core.profile.StandardProfile;
import com.yihu.ehr.profile.core.profile.StdDataSet;

import java.util.List;

/**
 * 处方。
 *
 * @author Sand
 * @created 2016.04.25 16:56
 */
public class Prescription {
    StandardProfile profile;            // 处方所在档案
    List<StdDataSet> data;              // 处方数据
}
