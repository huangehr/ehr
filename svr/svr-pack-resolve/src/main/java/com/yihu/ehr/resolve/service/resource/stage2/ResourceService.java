package com.yihu.ehr.resolve.service.resource.stage2;


import com.yihu.ehr.resolve.dao.FileResourceDao;
import com.yihu.ehr.resolve.dao.MasterResourceDao;
import com.yihu.ehr.resolve.dao.RelationDao;
import com.yihu.ehr.resolve.dao.SubResourceDao;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 资源服务。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class ResourceService {

    @Autowired
    private MasterResourceDao masterResRepo;
    @Autowired
    private SubResourceDao subResRepo;
    @Autowired
    private FileResourceDao fileResRepo;
    @Autowired
    private RelationDao relationDao;

    public void save(ResourceBucket resourceBucket, StandardPackage standardPackage) throws Exception {
        // 资源主表
        masterResRepo.saveOrUpdate(resourceBucket, standardPackage);

        // 资源子表
        subResRepo.saveOrUpdate(resourceBucket);

        // 存储文件记录
        fileResRepo.save(resourceBucket);

        //保存MYSQL关联记录
        relationDao.save(resourceBucket);
    }
}
