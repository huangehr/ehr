package com.yihu.ehr.resolve.service.resource.stage2;


import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.resolve.dao.FileResourceDao;
import com.yihu.ehr.resolve.dao.MasterResourceDao;
import com.yihu.ehr.resolve.dao.SubResourceDao;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.profile.ArchiveRelationService;
import com.yihu.ehr.resolve.service.profile.UploadService;
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
    private ArchiveRelationService archiveRelationService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private PatientService patientService;
    @Autowired
    private QcRecordService qcRecordService;

    public void save(ResourceBucket resourceBucket, OriginalPackage originalPackage) throws Exception {
        //资源主表
        masterResRepo.saveOrUpdate(resourceBucket, originalPackage);

        //资源子表
        subResRepo.saveOrUpdate(resourceBucket, originalPackage);

        //存储文件记录
        fileResRepo.save(originalPackage);

        //保存ES关联记录
        archiveRelationService.relation(resourceBucket, originalPackage);

        //保存ES质控数据
        qcRecordService.record(resourceBucket);

        //保存ES 待上传省平台upload记录
        uploadService.addWaitUpload(resourceBucket, originalPackage);

        //保存居民信息记录
        if (originalPackage.isIdentifyFlag()) {
            patientService.checkPatient(resourceBucket);
        }
    }

}
