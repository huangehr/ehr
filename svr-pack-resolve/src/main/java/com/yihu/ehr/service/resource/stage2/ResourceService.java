package com.yihu.ehr.service.resource.stage2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.service.resource.stage2.repo.FileResourceRepository;
import com.yihu.ehr.service.resource.stage2.repo.MasterResourceRepository;
import com.yihu.ehr.service.resource.stage2.repo.SubResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

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
    ObjectMapper objectMapper;

    public void save(ResourceBucket resourceBucket) throws Throwable {
        // 资源主表
        masterResRepo.saveOrUpdate(resourceBucket);

        // 资源子表
        subResRepo.saveOrUpdate(resourceBucket);

        // 存储文件记录
        fileResRepo.save(resourceBucket);
    }
}
