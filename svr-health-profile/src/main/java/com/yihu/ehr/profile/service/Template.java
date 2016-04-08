package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:42
 */
@Entity
@Table(name = "archive_template")
@Access(value = AccessType.PROPERTY)
public class Template {
    @Autowired
    private FastDFSUtil fastDFSUtil;

    public final static String UrlSeparator = ";";

    private int id;
    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String organizationCode;
    private String pcTplURL;
    private String mobileTemplate;
    private Date createTime = new Date();

    @Id
    @GeneratedValue(generator = "increment")
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    @Column(name = "cda_version", nullable = false)
    public String getCdaVersion() {
        return cdaVersion;
    }

    public void setCdaVersion(String version) {
        this.cdaVersion = version;
    }

    @Column(name = "cda_document_id", nullable = false)
    public String getCdaDocumentId() {
        return cdaDocumentId;
    }

    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

    @Column(name = "pc_template", nullable = false)
    public String getPcTplContent() throws Exception {
        String[] tokens = getPcTplUrl();
        byte[] bytes = fastDFSUtil.download(tokens[0], tokens[1]);

        return new String(bytes, Charset.forName("UTF-8"));
    }

    public void setPcTplContent(InputStream fileStream) throws Exception {
        ObjectNode objectNode = fastDFSUtil.upload(fileStream, "html", "健康档案展示模板");
        String group = objectNode.get(FastDFSUtil.GroupField).asText();
        String remoteFile = objectNode.get(FastDFSUtil.RemoteFileField).asText();

        pcTplURL = group + UrlSeparator + remoteFile;
    }

    public String[] getPcTplUrl() {
        String[] tokens = pcTplURL == null ? null : pcTplURL.split(UrlSeparator);

        return tokens;
    }

    @Column(name = "mobile_template", nullable = false)
    public String getMobileTplContent() {
        return mobileTemplate;
    }

    public void setMobileTemplate(String mobileTemplate) {
        this.mobileTemplate = mobileTemplate;
    }

    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "create_time")
    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String orgCode) {
        this.organizationCode = orgCode;
    }
}
