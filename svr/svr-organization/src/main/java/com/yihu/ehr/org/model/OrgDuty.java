package com.yihu.ehr.org.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 *  机构职务表
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/15.
 */
@Entity
@Table(name = "org_duty", schema = "", catalog = "healtharchive")
public class OrgDuty {
    private int id;
    private String orgId;
    private String name;
    private Date createDate;

    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "org_id", nullable = false, insertable = true, updatable = true)
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    @Column(name = "name", nullable = false, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "create_date", nullable = false)
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
