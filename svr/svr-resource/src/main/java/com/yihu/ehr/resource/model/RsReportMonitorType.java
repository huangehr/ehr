package com.yihu.ehr.resource.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;

/**
 * 资源报表监测类型 entity
 *
 * @author janseny
 * @created 2017年11月7日14:27:18
 */
@Entity
@Table(name = "rs_report_monitor_type")
public class RsReportMonitorType {

    private Integer id; // 主键
    private String name; // 名称
    private String note; // 类型

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
