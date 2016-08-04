package com.yihu.ehr.medicalRecords.model.DTO;

import org.omg.CORBA.Object;

import java.util.Date;
import java.util.Map;

/**
 * Created by hzp on 2016/8/1.
 */
public class MedicalReportDTO {
    private String rowkey;
    private String reportName;
    private Date reportDate;
    private String reportContent;
    private String detailList;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Date getReportDate() {
        return reportDate;
    }

    public void setReportDate(Date reportDate) {
        this.reportDate = reportDate;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getDetailList() {
        return detailList;
    }

    public void setDetailList(String detailList) {
        this.detailList = detailList;
    }

    /**
     * 实体转json
     */
    public String toJson()
    {
        return "{\"rowkey\":\""+getRowkey()+"\",\"report_name\":\""+getReportName()+"\",\"report_date\":\""+getReportDate()+"\",\"report_content\":\""+getReportContent()+"\",\"detail_list\":\""+getDetailList()+"\"}";
    }

    /**
     * 读取json数据
     */
    public void readJson(Map<String,Object> map)
    {
        if(map.containsKey("rowkey"))
        {
            setRowkey(map.get("rowkey").toString());
        }
        if(map.containsKey("report_name"))
        {
            setReportName(map.get("report_name").toString());
        }
        if(map.containsKey("report_date"))
        {
            //setReportDate(map.get("report_date"));
        }
        if(map.containsKey("report_content"))
        {
            setReportContent(map.get("report_content").toString());
        }
        if(map.containsKey("detail_list"))
        {
            setDetailList(map.get("detail_list").toString());
        }
    }
}
