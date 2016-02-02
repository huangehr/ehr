package com.yihu.ehr.dict.service.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 常用字典项接口。因为实际只存在一个系统字典接口，所以使用时，字典对象是一个“伪字典”。
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:49
 */
@Service
@Transactional
public class ConventionalDictEntry {

    @Autowired
    private ConvertionalRepository convertionalRepository;

    public ConventionalDictEntry() {
    }

    public ConventionalDict getAppCatalog(String code) {
        return convertionalRepository.getConvertionalDict(1,code);
    }

    public ConventionalDict getAppStatus(String code) {
        return convertionalRepository.getConvertionalDict(2,code);
    }

    public ConventionalDict getGender(String code) {
        return convertionalRepository.getConvertionalDict(3,code);
    }

    public ConventionalDict getMartialStatus(String code) {
        return convertionalRepository.getConvertionalDict(4,code);
    }

    public ConventionalDict getNation(String code) {
        return convertionalRepository.getConvertionalDict(5,code);
    }

    public ConventionalDict getResidenceType(String code) {
        return convertionalRepository.getConvertionalDict(6,code);
    }

    public ConventionalDict getOrgType(String code) {
        return convertionalRepository.getConvertionalDict(7,code);
    }

    public ConventionalDict getSettledWay(String code) {
        return convertionalRepository.getConvertionalDict(8,code);
    }

    public ConventionalDict getCardStatus(String code) {
        return convertionalRepository.getConvertionalDict(9,code);
    }

    public ConventionalDict getCardType(String code) {
        return convertionalRepository.getConvertionalDict(10,code);
    }

    public ConventionalDict getRequestState(String code) {
        return convertionalRepository.getConvertionalDict(11,code);
    }

    public ConventionalDict getKeyType(String code) {
        return convertionalRepository.getConvertionalDict(12,code);
    }

    public ConventionalDict getMedicalRole(String code) {
        return convertionalRepository.getConvertionalDict(13,code);
    }

    public ConventionalDict getUserRole(String code) {
        return convertionalRepository.getConvertionalDict(14,code);
    }


    public ConventionalDict getUserType(String code) {
        return convertionalRepository.getConvertionalDict(15,code);
    }

    public ConventionalDict getLoginAddress(String code){
        return convertionalRepository.getConvertionalDict(20,code);
    }

    //..................................................
    public List<ConventionalDict> getUserTypeList(){
        return convertionalRepository.getConventionalDictList(15);
    }


    public List<ConventionalDict> getTagsList(){
        return convertionalRepository.getConventionalDictList(17);
    }
    //...............................................................

    public ConventionalDict getYesNo(boolean code) {
        String resultCode = "";
        resultCode = code ? "True" : "False";
        return convertionalRepository.getConvertionalDict(18,resultCode);
    }

    public ConventionalDict getAdapterType(String code) {
        return convertionalRepository.getConvertionalDict(21,code);
    }

    public ConventionalDict getStdSourceType(String code) {
        return convertionalRepository.getConvertionalDict(22,code);
    }


    public List<ConventionalDict> getStdSourceTypeList(String[] codes) {
        return convertionalRepository.getConventionalDictList(22,codes);
    }
}