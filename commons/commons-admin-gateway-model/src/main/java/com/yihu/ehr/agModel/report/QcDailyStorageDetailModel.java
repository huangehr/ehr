package com.yihu.ehr.agModel.report;


/**
 * Created by janseny on 2017/5/8.
 */
public class QcDailyStorageDetailModel {

    private String dataType;//数据集类型
    private Integer totalStorageNum;
    private Integer todayStorageNum;
    private Integer totalIdentifyNum;
    private Integer totalNoIdentifyNum;
    private Integer todayIdentifyNum;
    private Integer todayNoIdentifyNum;


    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getTotalStorageNum() {
        return totalStorageNum;
    }

    public void setTotalStorageNum(Integer totalStorageNum) {
        this.totalStorageNum = totalStorageNum;
    }

    public Integer getTodayStorageNum() {
        return todayStorageNum;
    }

    public void setTodayStorageNum(Integer todayStorageNum) {
        this.todayStorageNum = todayStorageNum;
    }

    public Integer getTotalIdentifyNum() {
        return totalIdentifyNum;
    }

    public void setTotalIdentifyNum(Integer totalIdentifyNum) {
        this.totalIdentifyNum = totalIdentifyNum;
    }

    public Integer getTotalNoIdentifyNum() {
        return totalNoIdentifyNum;
    }

    public void setTotalNoIdentifyNum(Integer totalNoIdentifyNum) {
        this.totalNoIdentifyNum = totalNoIdentifyNum;
    }

    public Integer getTodayIdentifyNum() {
        return todayIdentifyNum;
    }

    public void setTodayIdentifyNum(Integer todayIdentifyNum) {
        this.todayIdentifyNum = todayIdentifyNum;
    }

    public Integer getTodayNoIdentifyNum() {
        return todayNoIdentifyNum;
    }

    public void setTodayNoIdentifyNum(Integer todayNoIdentifyNum) {
        this.todayNoIdentifyNum = todayNoIdentifyNum;
    }
}
