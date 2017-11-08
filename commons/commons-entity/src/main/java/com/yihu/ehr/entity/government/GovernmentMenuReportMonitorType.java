package com.yihu.ehr.entity.government;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jansney on 2017/11/7.
 */
@Entity
@Table(name = "government_menu_report_monitor_type")
public class GovernmentMenuReportMonitorType {
    private int id;
    private String rsReoportMonitorTypeId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "rs_reoport_monitor_type_id")
    public String getRsReoportMonitorTypeId() {
        return rsReoportMonitorTypeId;
    }

    public void setRsReoportMonitorTypeId(String rsReoportMonitorTypeId) {
        this.rsReoportMonitorTypeId = rsReoportMonitorTypeId;
    }
}
