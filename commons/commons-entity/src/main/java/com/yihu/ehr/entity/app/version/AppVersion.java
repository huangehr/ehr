package com.yihu.ehr.entity.app.version;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.*;

/**
 * Created by Trick on 2018/3/12.
 */
@Entity
@Table(name = "app_version")
@Access(value = AccessType.PROPERTY)
public class AppVersion extends BaseAssignedEntity {

    private String code; //版本（用于区分大小版本）
    private String name; //版本名称
    private Double versionInt; //数字版本号
    private String versionStr; //字符串版本号
    private String url; //更新链接地址
    private String info; //版本信息
    private Double size; //升级包大小，单位M

    @Column(name = "code", nullable = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "size", nullable = false)
    public Double getSize() {
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "version_int", nullable = false)
    public Double getVersionInt() {
        return versionInt;
    }

    public void setVersionInt(Double versionInt) {
        this.versionInt = versionInt;
    }

    @Column(name = "version_str", nullable = false)
    public String getVersionStr() {
        return versionStr;
    }

    public void setVersionStr(String versionStr) {
        this.versionStr = versionStr;
    }

    @Column(name = "url", nullable = false)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "info", nullable = false)
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
