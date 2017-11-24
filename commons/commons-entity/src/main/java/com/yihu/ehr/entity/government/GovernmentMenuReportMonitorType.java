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
    private int rsReoportMonitorTypeId;
    private int governmentMenuId;




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
    public int getRsReoportMonitorTypeId() {
        return rsReoportMonitorTypeId;
    }

    public void setRsReoportMonitorTypeId(int rsReoportMonitorTypeId) {
        this.rsReoportMonitorTypeId = rsReoportMonitorTypeId;
    }

    @Column(name = "government_menu_id")
    public int getGovernmentMenuId() {
        return governmentMenuId;
    }

    public void setGovernmentMenuId(int governmentMenuId) {
        this.governmentMenuId = governmentMenuId;
    }
}
