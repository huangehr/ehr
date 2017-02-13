package com.yihu.ehr.portal.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

/**
 * APP对象。
 *
 * @author Sand
 * @version 1.0
 * @created 03_8月_2015 16:53:21
 */

@Entity
@Table(name = "it_resource_hardware_server")
@Access(value = AccessType.FIELD)
public class ItResourceHardwareServer {

    @Id
    @GeneratedValue(generator = "Generator")
    @GenericGenerator(name = "Generator", strategy = "assigned")
    @Column(name = "id", unique = true, nullable = false)
    private int id;

    @Column(name = "master_id", unique = true, nullable = false)
    private int masterId;

    @Column(name = "cpu", unique = true, nullable = false)
    private int cpu;

    @Column(name = "memory", unique = true, nullable = false)
    private int memory;

    @Column(name = "hardware", unique = true, nullable = false)
    private int hardware;

    @Column(name = "purpose", unique = true, nullable = false)
    private String purpose;

    @Column(name = "apply_qty", unique = true, nullable = false)
    private int applyQty;

    @Column(name = "band_widty", unique = true, nullable = false)
    private int bandWidty;

    @Column(name = "port", unique = true, nullable = false)
    private String port;

    @Column(name = "system", unique = true, nullable = false)
    private String system;

    @Column(name = "pre_installed_software", unique = true, nullable = false)
    private String preInstalledSoftware;

    @Column(name = "status", unique = true, nullable = false)
    private int status;

    @Column(name = "domain", unique = true, nullable = false)
    private String domain;

    public ItResourceHardwareServer() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getCpu() {
        return cpu;
    }

    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getHardware() {
        return hardware;
    }

    public void setHardware(int hardware) {
        this.hardware = hardware;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public int getApplyQty() {
        return applyQty;
    }

    public void setApplyQty(int applyQty) {
        this.applyQty = applyQty;
    }

    public int getBandWidty() {
        return bandWidty;
    }

    public void setBandWidty(int bandWidty) {
        this.bandWidty = bandWidty;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getPreInstalledSoftware() {
        return preInstalledSoftware;
    }

    public void setPreInstalledSoftware(String preInstalledSoftware) {
        this.preInstalledSoftware = preInstalledSoftware;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}