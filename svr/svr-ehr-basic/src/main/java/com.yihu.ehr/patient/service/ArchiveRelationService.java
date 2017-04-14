package com.yihu.ehr.patient.service;

import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.patient.ArchiveRelation;
import com.yihu.ehr.patient.dao.XArchiveRelationDao;
import com.yihu.ehr.patient.feign.PatientArchiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 档案申领实现类.
 *
 */
@Transactional
@Service
public class ArchiveRelationService {

    @Autowired
    private XArchiveRelationDao archiveRelationDao;
    @Autowired
    private PatientArchiveClient patientArchiveClient;

    /**
     * 个人档案列表
     */
    public ListResult archiveList(String idCardNo,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveRelation> list = archiveRelationDao.findByIdCardNo(idCardNo,pageRequest);

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 管理员--通过卡号获取未认领档案
     */
    public ListResult archiveUnbind(String cardNo,Integer page,Integer rows) throws Exception{
        ListResult re = new ListResult(page,rows);
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
        PageRequest pageRequest  = new PageRequest(page, rows, sort);

        Page<ArchiveRelation> list = archiveRelationDao.findByCardNo(cardNo,pageRequest);

        if(list!=null) {
            re.setDetailModelList(list.getContent());
            re.setTotalCount(list.getTotalPages());
        }
        return re;
    }

    /**
     * 建立档案关联
     */
    public ArchiveRelation archiveRelation(ArchiveRelation relation) throws Exception
    {
        relation = archiveRelationDao.save(relation);

        return relation;
    }




    /**
     * 通过卡号+姓名关联HBase档案
     */
    public void relationByCardNoAndName(String cardNo,String name,String idcard) throws Exception
    {
        List<ArchiveRelation> list = archiveRelationDao.findByCardNoAndName(cardNo,name);
        if(list!=null && list.size()>0)
        {
              for(ArchiveRelation item:list)
              {
                 try {
                     patientArchiveClient.archiveRelation(item.getProfileId(), idcard);
                     item.setStatus("1");
                     item.setRelationDate(new Date());
                     item.setIdCardNo(idcard);
                 }
                 catch (Exception ex)
                 {
                     System.out.print("ProfileId:"+item.getProfileId()+" relation idcard:" + idcard +"fail!"+ex.getMessage());
                 }
              }
              archiveRelationDao.save(list);
        }
    }
}