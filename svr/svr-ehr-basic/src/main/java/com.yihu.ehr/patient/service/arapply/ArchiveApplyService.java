package com.yihu.ehr.patient.service.arapply;

import com.yihu.ehr.entity.patient.ArchiveApply;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.patient.dao.XArchiveApplyDao;
import com.yihu.ehr.patient.dao.XArchiveRelationDao;
import com.yihu.ehr.patient.feign.PatientArchiveClient;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;


/**
 * 档案申领实现类.
 *
 */
@Transactional
@Service
public class ArchiveApplyService extends BaseJpaService<ArchiveApply, XArchiveApplyDao> {

    @Autowired
    private XArchiveApplyDao archiveApplyDao;
    @Autowired
    private XArchiveRelationDao archiveRelationDao;
    @Autowired
    private PatientArchiveClient patientArchiveClient;

    /**
     * 个人档案认领列表
     */
    public ListResult archiveApplyList(String userId,String status,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveApply> list = null;
        if(!StringUtils.isEmpty(status))
        {
            list = archiveApplyDao.findByUserIdAndStatus(userId,status,pageRequest);
        }
        else{
            list = archiveApplyDao.findByUserId(userId,pageRequest);
        }

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 卡认证申请新增/修改
     */
    public ArchiveApply getArchiveApply(Long id) throws Exception{
        return archiveApplyDao.findOne(id);
    }

    /**
     * 卡认证申请新增/修改
     */
    public ArchiveApply archiveApply(ArchiveApply card) throws Exception{
        return archiveApplyDao.save(card);
    }

    /**
     * 卡认证申请删除
     */
    public void archiveApplyDelete(Long id) throws Exception{
        archiveApplyDao.delete(id);
    }

    /**
     * 管理员--档案认领列表
     */
    public ListResult archiveApplyListManager(String status,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveApply> list = null;
        if(!StringUtils.isEmpty(status))
        {
            list = archiveApplyDao.findByStatus(status,pageRequest);
        }
        else{
            list = archiveApplyDao.findAll(pageRequest);
        }

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 管理员--档案认领审核操作
     */
    public Result archiveVerifyManager(Long id,String status, String auditor,String auditReason, String archiveRelationIds) throws Exception{

        ArchiveApply apply = archiveApplyDao.findOne(id);

        if(apply!=null){
            String[] ids = archiveRelationIds.replace("，",",").split(",");
            String msg = "";

            for(String idString :ids){
                if(!StringUtils.isEmpty(idString)) {
                    ArchiveRelation relation = archiveRelationDao.findOne(Long.valueOf(idString));
                    if (relation != null) {
                        try {
                            if ("1".equals(apply.getStatus())) {  //审批通过
                                //操作hbase
                                patientArchiveClient.archiveRelation(relation.getProfileId(), apply.getIdCard());

                                //档案关联绑定
                                relation.setIdCardNo(apply.getIdCard());
                                relation.setStatus("1");
                                relation.setRelationDate(new Date());
                                relation.setApplyId(apply.getId());
                                archiveRelationDao.save(relation);
                            }
                        } catch (Exception ex) {
                            System.out.print(ex.getMessage());
                            msg += "";
                        }
                    } else {
                        return Result.error("不存在该份档案记录！");
                    }
                }
            }

            //档案认领审批
            apply.setStatus(status);
            apply.setAuditDate(new Date());
            apply.setAuditor(auditor);
            apply.setAuditReason(auditReason);
            archiveApplyDao.save(apply);

            if(!StringUtils.isEmpty(msg)){
                return Result.error("archiveRelationId:"+msg+" ");
            }

            return Result.success("档案认领审核成功！");
        }else{
            return Result.error("不存在该条申请记录！");
        }
    }
}