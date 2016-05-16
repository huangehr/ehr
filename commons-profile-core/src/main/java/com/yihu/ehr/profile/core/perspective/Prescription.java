package com.yihu.ehr.profile.core.perspective;

import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.memory.intermediate.StdDataSet;

import java.util.List;

/**
 * 处方。
 *
 * @author Sand
 * @created 2016.04.25 16:56
 */
public class Prescription {
    MemoryProfile profile;            // 处方所在档案
    List<StdDataSet> data;              // 处方数据
}
