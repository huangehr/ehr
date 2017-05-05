package com.yihu.ehr.geography.service;


import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.geography.dao.XGeographyDictRepository;
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
public class GeographyDictService extends BaseJpaService<GeographyDict,XGeographyDictRepository>{

    @Autowired
    private XGeographyDictRepository geographyDictRepository;


	public List<GeographyDict> getLevelToAddr(Integer level){
        List<GeographyDict> addressDictList = geographyDictRepository.getAddrDictByLevel(level);
        return addressDictList;
	}

	public List<GeographyDict> getPidToAddr(Integer pid){
        List<GeographyDict> addressDictList = geographyDictRepository.getAddrDictByPid(pid);
        return addressDictList;
	}

    public GeographyDict findById(String id) {
        return geographyDictRepository.findOne(Integer.parseInt(id));
    }

    public List<GeographyDict> getAllAddressDict() {
        return geographyDictRepository.getAll();
    }
}