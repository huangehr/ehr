package com.yihu.ehr.profile.controller.sanofi;

import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.04.20 16:11
 */
public class ClinicalSign {
    Demographic demographic;
    List<Lis> lis;
    PhysicalSignals physicalSignals;

    public Demographic getDemographic() {
        return demographic;
    }

    public void setDemographic(Demographic demographic) {
        this.demographic = demographic;
    }

    public List<Lis> getLis() {
        return lis;
    }

    public void setLis(List<Lis> lis) {
        this.lis = lis;
    }

    public PhysicalSignals getPhysicalSignals() {
        return physicalSignals;
    }

    public void setPhysicalSignals(PhysicalSignals physicalSignals) {
        this.physicalSignals = physicalSignals;
    }
}
