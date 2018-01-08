package com.yihu.ehr.basic.address.service;


import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.basic.address.dao.AddressDictRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 地址管理。对于相同地址，仅保存一份。
 * @author zqb
 * @version 1.0
 * @created 30-六月-2015 19:17:43
 */
@Transactional
@Service
public class AddressDictService extends BaseJpaService<AddressDict,AddressDictRepository>{

    @Autowired
    private AddressDictRepository geographyDictRepository;


	public List<AddressDict> getLevelToAddr(Integer level){
        List<AddressDict> addressDictList = geographyDictRepository.getAddrDictByLevel(level);
        return addressDictList;
	}

	public List<AddressDict> getPidToAddr(Integer pid){
        List<AddressDict> addressDictList = geographyDictRepository.getAddrDictByPid(pid);
        return addressDictList;
	}

    public AddressDict findById(String id) {
        return geographyDictRepository.findOne(Integer.valueOf(id));
    }

    public AddressDict findByName(String name) {
        List<AddressDict> DictList =geographyDictRepository.findByName(name);
        AddressDict geographyDict=new AddressDict();
        if(null!=DictList){
             geographyDict=DictList.get(0);
        }
        return geographyDict;
    }

    public List<AddressDict> getAllAddressDict() {
        return geographyDictRepository.getAll();
    }

    public List<AddressDict> getAddrDictByname(String name){
        List<AddressDict> addressDictList = geographyDictRepository.getAddrDictByname(name);
        return addressDictList;
    }
}