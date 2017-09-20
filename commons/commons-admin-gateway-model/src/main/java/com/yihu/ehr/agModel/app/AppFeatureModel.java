package com.yihu.ehr.agModel.app;

/**
 * AppFeature对象。
 *
 * @author linzhuo
 * @version 1.0
 * @created 2016年7月7日17:45:30
 */

public class AppFeatureModel {

    private int id;
    private String code; // 菜单编码
    private String name; // 菜单名称
    private String description; // 菜单描述
    private String type; // 类型，1：模块，2：菜单，3：功能按钮。
    private int parentId; // 父菜单
    private String url; // 菜单访问路径。模块时没有值，菜单时有值，按钮时不一定有值。
    private String iconUrl; // 图标访问路径
    private String auditLevel; // 审计类型，0：不审计，1：审计。
    private String openLevel; // 开放级别，0：私有（不作为授权对象），1：公开（收费/免费）
    private String appId; // 应用ID
    private Integer sort; // 菜单排序
    private Integer level; // 菜单级别
    private String content; // 页面菜单拼接对象

    private String typeName;
    private String auditLevelName;
    private String openLevelName;
    private String roleId;
    // 界面上适配选中是否适配用做界面展示，代表是否以及被适配
    private Boolean ischecked;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getAuditLevelName() {
        return auditLevelName;
    }

    public void setAuditLevelName(String auditLevelName) {
        this.auditLevelName = auditLevelName;
    }

    public String getOpenLevelName() {
        return openLevelName;
    }

    public void setOpenLevelName(String openLevelName) {
        this.openLevelName = openLevelName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public Boolean getIschecked() {
        return ischecked;
    }

    public void setIschecked(Boolean ischecked) {
        this.ischecked = ischecked;
    }
}