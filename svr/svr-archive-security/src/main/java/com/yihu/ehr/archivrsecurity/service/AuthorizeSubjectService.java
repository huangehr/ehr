package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubject;
import com.yihu.ehr.archivrsecurity.dao.repository.SubjectRepository;
import com.yihu.ehr.archivrsecurity.dao.repository.SubjectResourceRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lyr on 2016/7/12.
 */
@Service
@Transactional
public class AuthorizeSubjectService extends BaseJpaService<RsAuthorizeSubject,SubjectRepository> {

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    SubjectResourceRepository subjectResourceRepository;

    /**
     * 删除授权主题
     * @param subjectId
     */
    public void deleteBySubjectId(String subjectId)
    {
        String[] subjectIds = subjectId.split(",");

        for(String id : subjectIds)
        {
            subjectResourceRepository.deleteBySubjectId(id);
            subjectRepository.deleteBySubjectId(id);
        }
    }

    /**
     * 资源获取
     * @param sorts 排序
     * @param page 页码
     * @param size 分页大小
     * @return Page<RsResources> 资源
     */
    public Page<RsAuthorizeSubject> getSubjects(String sorts, int page, int size)
    {
        Pageable pageable =  new PageRequest(page,size,parseSorts(sorts));

        return subjectRepository.findAll(pageable);
    }
}
