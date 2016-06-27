package com.yihu.ehr.profile.legacy.memory.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sand
 * @created 2016.05.16 15:56
 */
public class SubRecords {
    protected List<SubRecord> records = new ArrayList<>();

    public void addRecord(SubRecord subRecord){
        records.add(subRecord);
    }

    public List<SubRecord> getRecords(){
        return records;
    }
}
