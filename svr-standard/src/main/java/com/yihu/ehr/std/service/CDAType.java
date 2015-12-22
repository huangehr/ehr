package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.lang.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * CDA类型
 *
 * @author AndyCai
 * @version 1.0
 * @created 11-12月-2015 15:52:22
 */
public class CDAType {

    @Autowired
    private CDATypeManager cdaTypeManager;

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

    /**
     * 父级名称
     */
    private CDAType cdaType;
    /**
     * 新增时间
     */
    private Date create_date;
    /**
     * 创建人
     */
    private String create_user;
    /**
     * 修改时间
     */
    private Date update_date;
    /**
     * 修改人
     */
    private String update_user;

    private String description;

    public CDAType() {

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

    /**
     * @param create_date
     */
    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Date getCreate_date() {
        return this.create_date;
    }

    /**
     * @param create_user
     */
    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public String getCreate_user() {
        return this.create_user;
    }

    /**
     * @param update_date
     */
    public void setUpdate_date(Date update_date) {
        this.update_date = update_date;
    }

    public Date getUpdate_date() {
        return this.update_date;
    }

    /**
     * @param update_user
     */
    public void setUpdate_user(String update_user) {
        this.update_user = update_user;
    }

    public String getUpdate_user() {
        return this.update_user;
    }

    public CDAType getCDAType(){
        if(cdaTypeManager ==null)
        {
            cdaTypeManager = ServiceFactory.getService(Services.CDATypeManager);
        }

        List<CDAType> listType = cdaTypeManager.getCDAtypeInfoByIds(this.parent_id);
        if(listType.size()>0)
        {
            return (CDAType)listType.get(0);
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}