package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MrSystemDictEntryDao;
import com.yihu.ehr.medicalRecord.dao.intf.PronunciationDao;
import com.yihu.ehr.medicalRecord.model.MrSystemDictEntryEntity;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by shine on 2016/7/14.
 */
@Transactional
@Service
public class MrSystemDictEntryService {
    @Autowired
    MrSystemDictEntryDao mrSystemDictEntryDao;
    @Autowired
    PronunciationDao pronunciationDao;
    private String getPinyin(String s)throws Exception{
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        String py = "";
        String[] t;
        for(int i=0;i<s.length();i++) {
            char c = s.charAt(i);
            if ((int) c <= 128)
                py += c;
            else {
                t = PinyinHelper.toHanyuPinyinStringArray(c, format);
                if (t == null)
                    py += c;
                else {
                    py += t;
                }
            }
        }
        return py.toLowerCase();
    }

    public boolean addMrSystemDictEntry(MrSystemDictEntryEntity MrSystemDictEntry){
        mrSystemDictEntryDao.save(MrSystemDictEntry);
        return true;
    }


    public boolean deleteMrSystemDictEntry( String DictCode,String Code){
        mrSystemDictEntryDao.deleteByid(mrSystemDictEntryDao.findByDictCodeAndCode(DictCode,Code).getId());
        return true;
    }

    public List<MrSystemDictEntryEntity> searchMrDiagnosisDictByPinyin(String pinyin)throws Exception {
        pinyin = getPinyin(pinyin);
        String sql = "";
        String s = "";
        for (int i = 0; i < pinyin.length(); i++) {
            char c = pinyin.charAt(i);
            s += c;
            if (pronunciationDao.findBypronunciation(s) == 0 ) {
                sql+=s.substring(0,s.length()-1)+"%";
                s = "";
                s += c;
            }
            else{
                if(i==pinyin.length()-1){
                    sql+=s+"%";
                }
            }

        }
        return mrSystemDictEntryDao.findByPinyin(sql);
    }

    public boolean searchMrSystemDictEntry(Integer id){
        mrSystemDictEntryDao.findById(id);
        return true;
    }

    public boolean updataMrSystemDictEntry(MrSystemDictEntryEntity mrSystemDictEntry){
        if(mrSystemDictEntry!=null){
            MrSystemDictEntryEntity m=mrSystemDictEntryDao.findByDictCodeAndCode(mrSystemDictEntry.getDictCode(), mrSystemDictEntry.getCode());
            if(m!=null)
            {
                m.setName(mrSystemDictEntry.getName());
                m.setDescription(mrSystemDictEntry.getDescription());
                return true;
            }
            else
                return false;

        }
        else
            return false;
    }
}
