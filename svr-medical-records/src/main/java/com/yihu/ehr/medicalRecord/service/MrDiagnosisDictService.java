package com.yihu.ehr.medicalRecord.service;

import com.yihu.ehr.medicalRecord.dao.intf.MrDiagnosisDictDao;
import com.yihu.ehr.medicalRecord.dao.intf.PronunciationDao;
import com.yihu.ehr.medicalRecord.model.MrDiagnosisDictEntity;
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
public class MrDiagnosisDictService {

    @Autowired
    MrDiagnosisDictDao mrDiagnosisDictDao;
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


    public boolean addMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict) throws Exception{
        String name=mrDiagnosisDict.getName();
        String py=getPinyin(name);
        mrDiagnosisDict.setPhoneticCode(py.trim());
        mrDiagnosisDictDao.save(mrDiagnosisDict);
        return true;
    }


    public boolean deleteMrDiagnosisDict( String code){
        mrDiagnosisDictDao.deleteBycode(code);
        return true;
    }

    public boolean searchMrDiagnosisDict(String code){
        mrDiagnosisDictDao.findBycode(code);
        return true;
    }

    public List<MrDiagnosisDictEntity> searchMrDiagnosisDictByPinyin(String pinyin)throws Exception {
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
        return mrDiagnosisDictDao.findByPinyin(sql);
    }

    public boolean updateMrDiagnosisDict(MrDiagnosisDictEntity mrDiagnosisDict){
        if(mrDiagnosisDict!=null){
            MrDiagnosisDictEntity m=mrDiagnosisDictDao.findBycode(mrDiagnosisDict.getCode());
            if(m!=null)
            {
                m.setName(mrDiagnosisDict.getName());
                m.setDescription(mrDiagnosisDict.getDescription());
                return true;
            }
            else
                return false;

        }
        else
            return false;
    }
}
