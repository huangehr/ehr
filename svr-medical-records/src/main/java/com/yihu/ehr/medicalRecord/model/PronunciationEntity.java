package com.yihu.ehr.medicalRecord.model;

import javax.persistence.*;

/**
 * Created by shine on 2016/7/22.
 */
@Entity
@Table(name = "pronunciation")
public class PronunciationEntity {
    private int id;
    private String pronunciation;


    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "pronunciation")
    public String getPronunciation() {
        return pronunciation;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }
}
