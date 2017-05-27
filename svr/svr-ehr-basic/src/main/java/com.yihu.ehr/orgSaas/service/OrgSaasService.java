package com.yihu.ehr.orgSaas.service;


import com.yihu.ehr.entity.geography.GeographyDict;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.orgSaas.dao.OrgSaasRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 机构授权
 * @author zdm
 * @version 1.0
 * @created 2017-5-26
 */
@Transactional
@Service
public class OrgSaasService extends BaseJpaService<OrgSaas,OrgSaasRepository>{

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

    public List<OrgSaas> isOrgSaasExist(OrgSaas orgSaas) throws Exception {
        List<OrgSaas> OrgSaasList = oOrgSaasRepository.getOrgSaasByorgCode(orgSaas.getOrgCode(),orgSaas.getType(),orgSaas.getSaasCode());
        return OrgSaasList;
    }

    /**
     * 地址检查并保存
     * 返回新地址id
     * @param orgSaas
     */
    public String saveOrgSaas(OrgSaas orgSaas) {
        return oOrgSaasRepository.save(orgSaas).getId().toString();
    }

    public void deleteOrgSaas(String orgCode,String type) {
        oOrgSaasRepository.deleteOrgSaas(orgCode,type);
    }

}