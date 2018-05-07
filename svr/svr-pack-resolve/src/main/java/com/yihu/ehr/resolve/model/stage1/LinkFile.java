package com.yihu.ehr.resolve.model.stage1;

/**
 * 非结构化档案原始文件。包含一个文档地址中的信息。
 *
 * @author Sand
 * @created 2015.08.16 10:44
 */
public class LinkFile {
    private String url;//文件存储地址
    private String originName;//原始文件名
    private String fileExtension;//文件扩展名

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
}