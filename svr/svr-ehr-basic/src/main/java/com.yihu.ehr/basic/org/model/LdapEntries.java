package com.yihu.ehr.basic.org.model;


import javax.persistence.*;
import java.io.Serializable;


/**
 * @author hzp
 */
@Entity
@Table(name = "ldap_entries")
public class LdapEntries implements Serializable {


    private Long id;
    private String dn;
    private Integer ocMapId;
    private Long parent;
    private Long keyval;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",unique = true,nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public Integer getOcMapId() {
        return ocMapId;
    }

    public void setOcMapId(Integer ocMapId) {
        this.ocMapId = ocMapId;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getKeyval() {
        return keyval;
    }

    public void setKeyval(Long keyval) {
        this.keyval = keyval;
    }
}