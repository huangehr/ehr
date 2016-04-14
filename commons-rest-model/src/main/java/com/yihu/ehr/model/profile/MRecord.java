package com.yihu.ehr.model.profile;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.12 20:07
 */
public class MRecord {
    private Map<String, String> cells = new HashMap<>();

    public MRecord(){
    }

    public MRecord(Map<String, String> cells){
        this.cells = cells;
    }

    public Map<String, String> getCells() {
        return cells;
    }

    public void setCells(Map<String, String> cells) {
        this.cells = cells;
    }
}
