package com.yihu.ehr.orgSaas.service;


import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.geography.dao.XGeographyDictRepository;
import com.yihu.ehr.orgSaas.dao.OrgSaasRepository;
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
public class OrgSaasService extends BaseJpaService<GeographyDict,XGeographyDictRepository>{

    @Autowired
    private OrgSaasRepository oOrgSaasRepository;

    /**
     * 根据机构获取saas机构数据
     * @param orgCode
     * @param type
     * @return
     */
	public List<Object> getOrgSaasByorgCode(String orgCode,String type){
        List<Object> OrgSaasList = oOrgSaasRepository.getOrgSaasByorgCode(orgCode,type);
        return OrgSaasList;
	}

}