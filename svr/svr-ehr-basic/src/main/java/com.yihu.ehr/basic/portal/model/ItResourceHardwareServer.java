package com.yihu.ehr.basic.portal.model;

import javax.persistence.*;

/**
 * Created by Dell on 2017/2/13.
 */
@Entity
@Table(name = "it_resource_hardware_server", schema = "", catalog = "healtharchive")
public class ItResourceHardwareServer {
    private int id;
    private Integer masterId;
    private Integer cpu;
    private Integer memory;
    private Integer hardware;
    private String purpose;
    private Integer applyQty;
    private Integer bandWidty;
    private String port;
    private String system;
    private String preInstalledSoftware;
    private String domain;
    private Integer status;

    @Id
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "master_id", nullable = true, insertable = true, updatable = true)
    public Integer getMasterId() {
        return masterId;
    }

    public void setMasterId(Integer masterId) {
        this.masterId = masterId;
    }

    @Basic
    @Column(name = "cpu", nullable = true, insertable = true, updatable = true)
    public Integer getCpu() {
        return cpu;
    }

    public void setCpu(Integer cpu) {
        this.cpu = cpu;
    }

    @Basic
    @Column(name = "memory", nullable = true, insertable = true, updatable = true)
    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    @Basic
    @Column(name = "hardware", nullable = true, insertable = true, updatable = true)
    public Integer getHardware() {
        return hardware;
    }

    public void setHardware(Integer hardware) {
        this.hardware = hardware;
    }

    @Basic
    @Column(name = "purpose", nullable = true, insertable = true, updatable = true)
    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    @Basic
    @Column(name = "apply_qty", nullable = true, insertable = true, updatable = true)
    public Integer getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(Integer applyQty) {
        this.applyQty = applyQty;
    }

    @Basic
    @Column(name = "band_widty", nullable = true, insertable = true, updatable = true)
    public Integer getBandWidty() {
        return bandWidty;
    }

    public void setBandWidty(Integer bandWidty) {
        this.bandWidty = bandWidty;
    }

    @Basic
    @Column(name = "port", nullable = true, insertable = true, updatable = true)
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Basic
    @Column(name = "system", nullable = true, insertable = true, updatable = true)
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    @Basic
    @Column(name = "pre_installed_software", nullable = true, insertable = true, updatable = true)
    public String getPreInstalledSoftware() {
        return preInstalledSoftware;
    }

    public void setPreInstalledSoftware(String preInstalledSoftware) {
        this.preInstalledSoftware = preInstalledSoftware;
    }

    @Basic
    @Column(name = "domain", nullable = true, insertable = true, updatable = true)
    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Basic
    @Column(name = "status", nullable = true, insertable = true, updatable = true)
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItResourceHardwareServer that = (ItResourceHardwareServer) o;

        if (id != that.id) return false;
        if (masterId != null ? !masterId.equals(that.masterId) : that.masterId != null) return false;
        if (cpu != null ? !cpu.equals(that.cpu) : that.cpu != null) return false;
        if (memory != null ? !memory.equals(that.memory) : that.memory != null) return false;
        if (hardware != null ? !hardware.equals(that.hardware) : that.hardware != null) return false;
        if (purpose != null ? !purpose.equals(that.purpose) : that.purpose != null) return false;
        if (applyQty != null ? !applyQty.equals(that.applyQty) : that.applyQty != null) return false;
        if (bandWidty != null ? !bandWidty.equals(that.bandWidty) : that.bandWidty != null) return false;
        if (port != null ? !port.equals(that.port) : that.port != null) return false;
        if (system != null ? !system.equals(that.system) : that.system != null) return false;
        if (preInstalledSoftware != null ? !preInstalledSoftware.equals(that.preInstalledSoftware) : that.preInstalledSoftware != null)
            return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (masterId != null ? masterId.hashCode() : 0);
        result = 31 * result + (cpu != null ? cpu.hashCode() : 0);
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + (hardware != null ? hardware.hashCode() : 0);
        result = 31 * result + (purpose != null ? purpose.hashCode() : 0);
        result = 31 * result + (applyQty != null ? applyQty.hashCode() : 0);
        result = 31 * result + (bandWidty != null ? bandWidty.hashCode() : 0);
        result = 31 * result + (port != null ? port.hashCode() : 0);
        result = 31 * result + (system != null ? system.hashCode() : 0);
        result = 31 * result + (preInstalledSoftware != null ? preInstalledSoftware.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
