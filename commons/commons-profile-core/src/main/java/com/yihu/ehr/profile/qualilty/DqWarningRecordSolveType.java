package com.yihu.ehr.profile.qualilty;

/**
 * @author yeshijie on 2018/6/13.
 */
public enum DqWarningRecordSolveType {
    solved("已解决", "1"),
    ignore("忽略", "2"),
    unSolve("无法解决", "3"),
    notProblem("不是问题", "4");
    private String name;
    private String value;

    DqWarningRecordSolveType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
