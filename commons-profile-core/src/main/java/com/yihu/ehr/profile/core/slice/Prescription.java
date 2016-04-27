package com.yihu.ehr.profile.core.slice;

import com.yihu.ehr.profile.core.StdDataSet;
import com.yihu.ehr.profile.core.StructedProfile;

import java.util.List;

/**
 * 处方。
 *
 * @author Sand
 * @created 2016.04.25 16:56
 */
public class Prescription {
    StructedProfile profile;            // 处方所在档案
    List<StdDataSet> data;              // 处方数据
}
