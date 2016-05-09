package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.InputStream;
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
    private int id;
    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String organizationCode;
    private String pcTplURL;
    private String mobileTplURL;
    private Date createTime = new Date();

    final static String UrlSeparator = ";";

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "increment")
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
    public String getPcTplURL() throws Exception {
        return pcTplURL;
    }

    public void setPcTplURL(String url) throws Exception {
        pcTplURL = url;
    }

    @Column(name = "mobile_template", nullable = false)
    public String getMobileTplURL() {
        return mobileTplURL;
    }

    public void setMobileTplURL(String mobileTplURL) {
        this.mobileTplURL = mobileTplURL;
    }

    @Column(name = "create_time")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "org_code")
    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    /**
     * 读取模板内容。
     *
     * @return
     */
    public byte[] getContent(boolean pc) throws Exception {
        String url = pc ? pcTplURL : mobileTplURL;
        if (StringUtils.isEmpty(url)) {
            return null;
        }

        String[] tokens = pcTplURL.split(UrlSeparator);
        byte[] bytes = fastDFSUtil().download(tokens[0], tokens[1]);

        return bytes;
    }

    /**
     * 设置模板内容。
     *
     * @param fileStream
     * @throws Exception
     */
    public void setContent(boolean pc, InputStream fileStream) throws Exception {
        ObjectNode objectNode = fastDFSUtil().upload(fileStream, "html", "健康档案展示模板");
        String group = objectNode.get(FastDFSUtil.GroupField).asText();
        String remoteFile = objectNode.get(FastDFSUtil.RemoteFileField).asText();

        String url = group + UrlSeparator + remoteFile;
        if (pc){
            pcTplURL = url;
        } else {
            mobileTplURL = url;
        }
    }

    private FastDFSUtil fastDFSUtil(){
        return SpringContext.getService(FastDFSUtil.class);
    }
}
