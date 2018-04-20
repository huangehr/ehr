package com.yihu.ehr.basic.portal.service;

import com.yihu.ehr.basic.portal.dao.ItResourceRepository;
import com.yihu.ehr.basic.portal.model.ItResource;
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
public class ItResourceService extends BaseJpaService<ItResource, ItResourceRepository> {

    @Autowired
    private ItResourceRepository resourceRepository;

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
