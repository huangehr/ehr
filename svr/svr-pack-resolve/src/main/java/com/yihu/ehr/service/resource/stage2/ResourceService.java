package com.yihu.ehr.service.resource.stage2;


import com.yihu.ehr.service.resource.stage2.index.IndexService;
import com.yihu.ehr.service.resource.stage2.repo.FileResourceDao;
import com.yihu.ehr.service.resource.stage2.repo.MasterResourceDao;
import com.yihu.ehr.service.resource.stage2.repo.RelationDao;
import com.yihu.ehr.service.resource.stage2.repo.SubResourceDao;
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
    private IndexService indexService;
    @Autowired
    private RelationDao relationDao;

    public void save(ResourceBucket resourceBucket) throws Exception {
        // 资源主表
        masterResRepo.saveOrUpdate(resourceBucket);

        // 资源子表
        subResRepo.saveOrUpdate(resourceBucket);

        // 存储文件记录
        fileResRepo.save(resourceBucket);

        // 保存维度索引
        //indexService.save(resourceBucket);

        //保存MYSQL关联记录
        relationDao.save(resourceBucket);
    }
}
