package com.yihu.ehr.profile.model;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.entity.BaseIdentityEntity;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.io.InputStream;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.08 11:42
 */
@Entity
@Table(name = "archive_template")
@Access(value = AccessType.PROPERTY)
public class ArchiveTemplate extends BaseIdentityEntity {

    public final static String UrlSeparator = ";";

    public enum Type {
        clinic, //门诊
        resident, //住院
        medicalExam, //体检
        universal //通用
    }

    private String title;
    private String cdaVersion;
    private String cdaDocumentId;
    private String cdaDocumentName;
    private String cdaCode;
    private String pcUrl;
    private String mobileUrl;
    private Type type;

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

    @Column(name = "cda_document_id")
    public String getCdaDocumentId() {
        return cdaDocumentId;
    }
    public void setCdaDocumentId(String cdaDocumentId) {
        this.cdaDocumentId = cdaDocumentId;
    }

    @Column(name = "cda_document_name")
    public String getCdaDocumentName() {
        return cdaDocumentName;
    }

    public void setCdaDocumentName(String cdaDocumentName) {
        this.cdaDocumentName = cdaDocumentName;
    }

    @Column(name = "cda_code")
    public String getCdaCode() {
        return cdaCode;
    }
    public void setCdaCode(String cdaCode) {
        this.cdaCode = cdaCode;
    }

    @Column(name = "pc_url")
    public String getPcUrl() {
        return pcUrl;
    }

    public void setPcUrl(String pcUrl) {
        this.pcUrl = pcUrl;
    }

    @Column(name = "mobile_url")
    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

    @Column(name = "type")
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    /**
     * 读取模板内容。
     *
     * @return
     */
    public byte[] getContent(boolean pc) throws Exception {
        String url = pc ? pcUrl : mobileUrl;
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        String[] tokens = url.split(UrlSeparator);
        byte[] bytes = fastDFSUtil().download(tokens[0], tokens[1]);
        return bytes;
    }

    /**
     * 设置模板内容。
     *
     * @param fileStream
     * @throws Exception
     */
    public void setContent (boolean pc, InputStream fileStream) throws Exception {
        if (pc){
            if (StringUtils.isNotEmpty(pcUrl)) {
                fastDFSUtil().delete(pcUrl.split(UrlSeparator)[0], pcUrl.split(UrlSeparator)[1]);
            }
            ObjectNode objectNode = fastDFSUtil().upload(fileStream, "html", "健康档案展示模板");
            String group = objectNode.get(FastDFSUtil.GROUP_NAME).asText();
            String remoteFile = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
            String url = group + UrlSeparator + remoteFile;
            pcUrl = url;
        } else {
            if (StringUtils.isNotEmpty(mobileUrl)) {
                fastDFSUtil().delete(mobileUrl.split(UrlSeparator)[0], mobileUrl.split(UrlSeparator)[1]);
            }
            ObjectNode objectNode = fastDFSUtil().upload(fileStream, "html", "健康档案展示模板");
            String group = objectNode.get(FastDFSUtil.GROUP_NAME).asText();
            String remoteFile = objectNode.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
            String url = group + UrlSeparator + remoteFile;
            mobileUrl = url;
        }
    }

    private FastDFSUtil fastDFSUtil(){
        return SpringContext.getService(FastDFSUtil.class);
    }
}
