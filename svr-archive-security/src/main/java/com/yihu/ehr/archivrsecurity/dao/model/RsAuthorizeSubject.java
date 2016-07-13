package com.yihu.ehr.archivrsecurity.dao.model;

import com.yihu.ehr.util.datetime.DateTimeUtil;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by lyr on 2016/7/11.
 */
@Entity
@Table(name="rs_authorize_subject")
public class RsAuthorizeSubject {

    private String id;
    private String subjectId;
    private String subjectName;
    private String description;
    private Date modifyTime;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id",unique = true,nullable = false)
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    @Column(name="subject_id",nullable = false)
    public String getSubjectId() {
        return subjectId;
    }
    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Column(name="subject_name",nullable = false)
    public String getSubjectName() {
        return subjectName;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Column(name="description")
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name="modify_time")
    @DateTimeFormat(pattern = DateTimeUtil.simpleDateTimePattern)
    public Date getModifyTime() {
        return modifyTime;
    }
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
