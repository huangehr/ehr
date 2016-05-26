package com.yihu.ehr.resource.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.resource.dao.intf.RsInterfaceDao;
import com.yihu.ehr.resource.model.RsInterface;
import com.yihu.ehr.resource.service.intf.IRsInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.17 16:33
 */
@Service
@Transactional
public class RsInterfaceService extends BaseJpaService<RsInterface, RsInterfaceDao> implements IRsInterfaceService {
    @Autowired
    private RsInterfaceDao rsInterfaceDao;
    public boolean isNameExist(String name){
        List<RsInterface> list = rsInterfaceDao.findByName(name);
        return list != null&&list.size()!=0;
    }

}
