package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.ScAuthorizeDoctorApply;
import com.yihu.ehr.archivrsecurity.dao.repository.DoctorApplyRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by lyr on 2016/7/12.
 */
@Service
@Transactional
public class DoctorApplyService extends BaseJpaService<ScAuthorizeDoctorApply,DoctorApplyRepository> {

    @Autowired
    DoctorApplyRepository doctorApplyRepository;

    /**
     * 医生授权查询
     * @param sorts
     * @param page
     * @param size
     * @return
     */
    public Page<ScAuthorizeDoctorApply> getDoctorApply(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return doctorApplyRepository.findAll(pageable);
    }

    /**
     * 删除医生授权
     * @param id
     */
    public void deleteDoctorApply(String id)
    {
        String[] ids = id.split(",");

        for(String id_ : ids)
        {
            doctorApplyRepository.delete(id_);
        }
    }

    /**
     * 变更医生授权
     * @param id
     * @param authorizeType
     * @param authorizeScope
     * @param startTime
     * @param endTime
     */
    public void modifyDoctorAuthorize(String id, int authorizeType,int authorizeScope, Date startTime, Date endTime) throws Exception {
        ScAuthorizeDoctorApply doctorApply = doctorApplyRepository.findOne(id);

        if(doctorApply != null)
        {
            if(authorizeType> 0)
            {
                doctorApply.setAuthorizeType(authorizeType);
            }
            if(authorizeScope> 0)
            {
                doctorApply.setAuthorizeScope(authorizeScope);
            }
            if(startTime != null)
            {
                doctorApply.setStartTime(startTime);
            }
            if(endTime != null)
            {
                doctorApply.setEndTime(endTime);
            }
        }
        else
        {
            throw new Exception("授权信息不存在");
        }
    }


    /**
     * 医生申请授权
     * @param id
     * @param authorizeStatus
     * @param authorizeMode
     */
    public void authorizeDoctorApply(String id,int authorizeStatus,int authorizeMode) throws Exception {
        ScAuthorizeDoctorApply doctorApply = doctorApplyRepository.findOne(id);

        if(doctorApply != null)
        {
            doctorApply.setAuthorizeStatus(authorizeStatus);
            doctorApply.setAuthorizeMode(authorizeMode);
            doctorApply.setAuthorizeTime(new Date());
        }
        else
        {
            throw new Exception("申请信息不存在");
        }
    }

}
