package com.yihu.ehr.basic.portal.service;

import com.google.common.collect.Lists;
import com.yihu.ehr.basic.portal.dao.PortalAccountRepresentationDao;
import com.yihu.ehr.basic.portal.model.PortalAccountRepresentation;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by zhangdan on 2018/4/19.
 */
@Service
@Transactional
public class PortalAccountRepresentationService extends BaseJpaService<PortalAccountRepresentation,PortalAccountRepresentationDao> {

    @Autowired
    private PortalAccountRepresentationDao portalAccountRepresentationDao;

    public boolean saveAccountRepresentation(String type,String content,String photo,String createName,String idCard,String mobile){
        PortalAccountRepresentation portalAccountRepresentation = new PortalAccountRepresentation(getCode(),0,createName,mobile,idCard,Integer.valueOf(type),content,photo,new Date(),null,createName,null,null,null);
        PortalAccountRepresentation result = portalAccountRepresentationDao.save(portalAccountRepresentation);
        if (result!=null){
            return true;
        }else {
            return false;
        }
    }

}
