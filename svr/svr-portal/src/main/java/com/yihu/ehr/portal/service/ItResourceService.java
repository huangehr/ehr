package com.yihu.ehr.portal.service;

import com.yihu.ehr.portal.dao.XItResourceRepository;
import com.yihu.ehr.portal.model.ItResource;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
@Service
@Transactional(rollbackFor = {ServiceException.class})
public class ItResourceService extends BaseJpaService<ItResource, XItResourceRepository> {

    @Autowired
    private XItResourceRepository resourceRepository;

    List<ItResource> searchByPlatformType(String platformType){
        return resourceRepository.searchByPlatformType(platformType);
    }

    public ItResource deleteItResource(Integer itResourceId){
        ItResource itResource = resourceRepository.findOne(itResourceId);
        itResource.setStatus(1);
        resourceRepository.save(itResource);
        return itResource;
    }

}
