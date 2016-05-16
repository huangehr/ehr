package com.yihu.ehr.profile.memory.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @created 2016.05.16 15:56
 */
public class SubRecords {
    protected List<SubRecord> records = new ArrayList<>();

    public void addRecord(SubRecord subRecord){
        records.add(subRecord);
    }
}
