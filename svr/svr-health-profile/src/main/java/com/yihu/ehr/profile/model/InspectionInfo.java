package com.yihu.ehr.profile.model;

/**
 * Created by progr1mmer on 2018/8/14.
 */
public class InspectionInfo {

    private String name;
    private Integer count;
    private String firstTime;
    private String lastTime;
    private String lastOrg;
    private String lastRecord;
    //1.检查结果信息
    //2.因为检验的检测结果在子项目中，故此处没有值
    private Integer exCount = 0;
    private String result = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastOrg() {
        return lastOrg;
    }

    public void setLastOrg(String lastOrg) {
        this.lastOrg = lastOrg;
    }

    public String getLastRecord() {
        return lastRecord;
    }

    public void setLastRecord(String lastRecord) {
        this.lastRecord = lastRecord;
    }

    public Integer getExCount() {
        return exCount;
    }

    public void setExCount(Integer exCount) {
        this.exCount = exCount;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
