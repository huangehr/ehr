package com.yihu.ehr.resolve.model.stage1.details;

/**
 *轻量型档案文件解析而来
 */
public class LinkFile {

    //文件信息
    private String url;//文件存储地址
    private String originName;//原始文件名
    private String fileExtension;//文件扩展名
    private long fileSize;//文件大小


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
}