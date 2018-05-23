package com.yihu.ehr.resolve.model.stage2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/5/22.
 */
public class QcRecords {

    private List<Map<String, Object>> records = new ArrayList<>();

    public void addRecord(Map<String, Object> data){
        records.add(data);
    }

    public List<Map<String, Object>> getRecords(){
        return records;
    }
}
