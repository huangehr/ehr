package com.yihu.ehr.basic.apps.model;

import javax.persistence.*;

/**
 * apps_api_feature对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

@Entity
@Table(name = "apps_feature")
public class AppFeature {

    private int id;
    private String code; // 菜单编码
    private String name; // 菜单名称
    private String description; // 菜单描述
    private String type; // 类型，1：模块，2：菜单，3：功能按钮。
    private int parentId; // 父菜单
    private String url; // 菜单相对访问路径。模块时没有值，菜单时有值，按钮时不一定有值。
    private String prefixUrl; // 菜单相对访问路径前缀，即JavaWeb上下文路径或站点+上下文路径。
    private String iconUrl; // 图标访问路径
    private String auditLevel; // 审计类型，0：不审计，1：审计。
    private String openLevel; // 开放级别，0：私有（不作为授权对象），1：公开（收费/免费）
    private String appId; // 应用ID
    private Integer sort; // 菜单排序
    private Integer level; // 菜单级别
    private String content; // 页面菜单拼接对象

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name", nullable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "description", nullable = true)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "code", nullable = true)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "type", nullable = true)
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "parent_id", nullable = true)
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Column(name = "url", nullable = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "prefix_url", nullable = true)
    public String getPrefixUrl() {
        return prefixUrl;
    }

    public void setPrefixUrl(String prefixUrl) {
        this.prefixUrl = prefixUrl;
    }

    @Column(name = "icon_url", nullable = true)
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Column(name = "audit_level", nullable = true)
    public String getAuditLevel() {
        return auditLevel;
    }

    public void setAuditLevel(String auditLevel) {
        this.auditLevel = auditLevel;
    }

    @Column(name = "open_level", nullable = true)
    public String getOpenLevel() {
        return openLevel;
    }

    public void setOpenLevel(String openLevel) {
        this.openLevel = openLevel;
    }

    @Column(name = "app_id", nullable = true)
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "sort", nullable = true)
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Column(name = "level", nullable = true)
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @Column(name = "content", nullable = true)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}