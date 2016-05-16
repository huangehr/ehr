package com.yihu.ehr.profile.memory.resource;

import java.util.Map;
import java.util.TreeMap;

/**
 * 打包资源数据组，形成一个统一的包。
 *
 * @author Sand
 * @created 2016.05.16 14:31
 */
public class DataPack {
    Map<String, MainRecord> recordGroup = new TreeMap<>();
}
