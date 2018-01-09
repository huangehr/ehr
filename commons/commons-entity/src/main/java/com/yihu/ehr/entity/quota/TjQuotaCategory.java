package com.yihu.ehr.entity.quota;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by wxw on 2017/8/31.
 */
@Entity
@Table(name = "tj_quota_category", schema = "")
public class TjQuotaCategory implements Serializable{
    private int id;
    private String name;
    private int parentId;
    private String code;
    private String note;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "parent_id")
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
