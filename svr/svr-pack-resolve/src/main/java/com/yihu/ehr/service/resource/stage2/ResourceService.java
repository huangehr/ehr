package com.yihu.ehr.service.resource.stage2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.feign.XArchiveClient;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.service.resource.stage2.index.IndexService;
import com.yihu.ehr.service.resource.stage2.repo.FileResourceRepository;
import com.yihu.ehr.service.resource.stage2.repo.MasterResourceRepository;
import com.yihu.ehr.service.resource.stage2.repo.RelationRepository;
import com.yihu.ehr.service.resource.stage2.repo.SubResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

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
    MasterResourceRepository masterResRepo;

    @Autowired
    SubResourceRepository subResRepo;

    @Autowired
    FileResourceRepository fileResRepo;

    @Autowired
    IndexService indexService;

    @Autowired
    RelationRepository relationRepository;

    public void save(ResourceBucket resourceBucket) throws Throwable {
        // 资源主表
        masterResRepo.saveOrUpdate(resourceBucket);

        // 资源子表
        subResRepo.saveOrUpdate(resourceBucket);

        // 存储文件记录
        fileResRepo.save(resourceBucket);

        // 保存维度索引
        //indexService.save(resourceBucket);


        //保存MYSQL关联记录
        relationRepository.save(resourceBucket);
    }
}
