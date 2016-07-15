package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrSystemDictEntryEntity;
import com.yihu.ehr.medicalRecord.service.MrSystemDictEntryService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class MrSystemDictEntryController {

    @Autowired
    MrSystemDictEntryService mrSystemDictEntryService;


    public boolean addMrSystemDictEntry(MrSystemDictEntryEntity MrSystemDictEntry){
        return mrSystemDictEntryService.addMrSystemDictEntry(MrSystemDictEntry);
    }

    public boolean deleteMrSystemDictEntry( String DictCode,String Code){
        return mrSystemDictEntryService.deleteMrSystemDictEntry(DictCode,Code);
    }

    public boolean updataMrSystemDictEntry(MrSystemDictEntryEntity mrSystemDictEntry){
        return mrSystemDictEntryService.updataMrSystemDictEntry(mrSystemDictEntry);
    }
}
