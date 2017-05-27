package com.yihu.ehr.agModel.report;


import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
public class QcDailyStorageModel {

    private String eventDate;
    private List<QcDailyStorageDetailModel> qcDailyStorageDetailModelList;


    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public List<QcDailyStorageDetailModel> getQcDailyStorageDetailModelList() {
        return qcDailyStorageDetailModelList;
    }

    public void setQcDailyStorageDetailModelList(List<QcDailyStorageDetailModel> qcDailyStorageDetailModelList) {
        this.qcDailyStorageDetailModelList = qcDailyStorageDetailModelList;
    }
}
