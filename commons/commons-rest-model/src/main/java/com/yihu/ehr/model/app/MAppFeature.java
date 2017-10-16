package com.yihu.ehr.model.app;

import java.io.Serializable;

/**
 * MAppApiFeature对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

public class MAppFeature implements Serializable {

    private int id;
    private String code; // 菜单编码
    private String name; // 菜单名称
    private String description; // 菜单描述
    private String type; // 类型，1：模块，2：菜单，3：功能按钮。
    private int parentId; // 父菜单
    private String url; // 菜单访问路径。模块时没有值，菜单时有值，按钮时不一定有值。
    private String prefixUrl; // 菜单相对访问路径前缀，即JavaWeb上下文路径或站点+上下文路径。
    private String iconUrl; // 图标访问路径
    private String auditLevel; // 审计类型，0：不审计，1：审计。
    private String openLevel; // 开放级别，0：私有（不作为授权对象），1：公开（收费/免费）
    private String appId; // 应用ID
    private Integer sort; // 菜单排序
    private Integer level; // 菜单级别
    private String content; // 页面菜单拼接对象

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    public String getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}