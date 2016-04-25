package com.yihu.ehr.profile.core.slice;

import com.yihu.ehr.profile.core.commons.DataSet;
import com.yihu.ehr.profile.core.commons.Profile;

import java.util.List;

/**
 * 处方。
 *
 * @author Sand
 * @created 2016.04.25 16:56
 */
public class Prescription {
    Profile profile;            // 处方所在档案
    List<DataSet> data;         // 处方数据
}
