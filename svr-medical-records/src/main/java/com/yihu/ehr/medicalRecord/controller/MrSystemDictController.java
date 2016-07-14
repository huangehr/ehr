package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.medicalRecord.model.MrSystemDictEntity;
import com.yihu.ehr.medicalRecord.service.MrSystemDictService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shine on 2016/7/14.
 */
public class MrSystemDictController {

    @Autowired
    MrSystemDictService mrSystemDictService;

    public boolean addMrSystemDictEntry(MrSystemDictEntity MrSystemDictEntry){
        return mrSystemDictService.addMrSystemDictEntry(MrSystemDictEntry);
    }

    public boolean deleteMrSystemDictEntry( String dictCode){
        return mrSystemDictService.deleteMrSystemDictEntry(dictCode);
    }

    public boolean updataMrSystemDictEntry(MrSystemDictEntity mrSystemDictEntity){
        return mrSystemDictService.updataMrSystemDictEntry(mrSystemDictEntity);
    }
}
