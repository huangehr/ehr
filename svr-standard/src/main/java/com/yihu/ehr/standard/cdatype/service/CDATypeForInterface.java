package com.yihu.ehr.standard.cdatype.service;

/**
 * Created by AndyCai on 2015/12/14.
 */
public class CDATypeForInterface {

    /**
     * id
     */
    private String id;
    /**
     * 代码
     */
    private String code;
    /**
     * 名称
     */
    private String name;
    /**
     * 父级ID
     */
    private String parentId;

    private String parentName;


    /**
     * 修改人
     */
    private String userId;

    private String description;

    public CDATypeForInterface() {

    }

    public void finalize() throws Throwable {

    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    /**
     * @param parentId
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
