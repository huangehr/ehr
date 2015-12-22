package com.yihu.ehr.std.model;

/**
 * Created by AndyCai on 2015/12/14.
 */
public class CDATypeModel {

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
    private String parent_id;

    private String parent_name;


    /**
     * 修改人
     */
    private String user_id;

    private String description;

    public CDATypeModel() {

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
     * @param parent_id
     */
    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
