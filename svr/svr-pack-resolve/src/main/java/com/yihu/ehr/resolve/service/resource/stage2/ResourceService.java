package com.yihu.ehr.resolve.service.resource.stage2;


import com.yihu.ehr.resolve.dao.MasterResourceDao;
import com.yihu.ehr.resolve.dao.SubResourceDao;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.profile.ArchiveRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


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
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private QcRecordService qcRecordService;
    @Autowired
    private FtpFileService ftpFileService;

    public void save(ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        //资源主表
        masterResRepo.saveOrUpdate(resourceBucket, originalPackage);

        //资源子表
        subResRepo.saveOrUpdate(resourceBucket, originalPackage);

        //保存ES关联记录
        archiveRelationService.relation(resourceBucket, originalPackage);

        //保存ES质控数据
        qcRecordService.record(resourceBucket);

        //保存居民信息记录
        if (originalPackage.isIdentifyFlag()) {
            patientService.checkPatient(resourceBucket);
        }

        //数据入库后,然后删除ftp上的文件
        if(originalPackage instanceof LinkPackage){
            LinkPackage pack = (LinkPackage) originalPackage;
            ftpFileService.deleteFile(pack);
        }

    }

}
