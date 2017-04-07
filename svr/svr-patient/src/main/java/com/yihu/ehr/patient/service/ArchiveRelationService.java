package com.yihu.ehr.patient.service;

import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.patient.ArchiveRelation;
import com.yihu.ehr.patient.dao.XArchiveRelationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 档案申领实现类.
 *
 */
@Transactional
@Service
public class ArchiveRelationService {

    @Autowired
    private XArchiveRelationDao archiveRelationDao;

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


}