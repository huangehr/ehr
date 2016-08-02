package com.yihu.ehr.medicalRecord.service.record;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.medicalRecord.model.MedicalReportImg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/27.
 */
public class ReportDocument extends BaseController {

    String id;
    String reportName;
    Date   reportDate;
    String reportContent;
    List<MedicalReportImg> reportItemList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public List<MedicalReportImg> getReportItemList() {
        return reportItemList;
    }

    public void setReportItemList(List<MedicalReportImg> reportItemList) {
        this.reportItemList = reportItemList;
    }

    public ObjectNode toJson(){
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        ObjectNode parent = objectMapper.createObjectNode();
        parent.put("id", id);
        parent.put("report_name", reportName);
        parent.put("reportDate", DateToString(reportDate, AgAdminConstants.DateTimeFormat));
        parent.put("reportContent", reportContent);

        ArrayNode docList = parent.putArray("list");
        for (MedicalReportImg reportItem : reportItemList){
            ObjectNode objectNode = docList.addObject();
            objectNode.put("name", reportItem.getName());
            objectNode.put("report_img_url", reportItem.getReportImgUrl());
            objectNode.put("reportFastdfsImgUrl", reportItem.getReportFastdfsImgUrl());
            objectNode.put("sort", reportItem.getSort());
        }

        return parent;
    }


}
