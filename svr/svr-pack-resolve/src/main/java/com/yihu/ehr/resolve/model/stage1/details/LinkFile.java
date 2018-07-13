package com.yihu.ehr.resolve.model.stage1.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *轻量型档案文件解析而来
 */
public class LinkFile {

    //文件信息
    private String url;//文件存储地址
    private String originName;//原始文件名
    private String fileExtension;//文件扩展名
    private long fileSize;//文件大小
    private String reportFormNo;//所属报告单号
    private String serialNo;//子项序号
    private String md5;//文件md5

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getReportFormNo() {
        return reportFormNo;
    }

    public void setReportFormNo(String reportFormNo) {
        this.reportFormNo = reportFormNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("url", this.url);
        root.put("originName", this.originName);
        root.put("fileExtension", this.fileExtension);
        root.put("fileSize", this.fileSize);
        root.put("reportFormNo", this.reportFormNo);
        root.put("serialNo", this.serialNo);
        root.put("md5",this.md5);
        return root.toString();
    }
}