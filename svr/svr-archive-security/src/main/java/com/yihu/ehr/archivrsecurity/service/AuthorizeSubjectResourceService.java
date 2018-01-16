package com.yihu.ehr.archivrsecurity.service;

import com.yihu.ehr.archivrsecurity.dao.model.RsAuthorizeSubjectResource;
import com.yihu.ehr.archivrsecurity.dao.repository.SubjectResourceRepository;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lyr on 2016/7/12.
 */
@Service
@Transactional
public class AuthorizeSubjectResourceService extends BaseJpaService<RsAuthorizeSubjectResource,SubjectResourceRepository>{

    @Autowired
    SubjectResourceRepository subjectResourceRepository;

    /**
     * 保存主题资源
     * @param subjectResources
     * @return
     */
    public List<RsAuthorizeSubjectResource> saveSubjectResource(List<RsAuthorizeSubjectResource> subjectResources)
    {
        for(RsAuthorizeSubjectResource subjectResource : subjectResources)
        {
            subjectResourceRepository.save(subjectResource);
        }

        return subjectResources;
    }

    /**
     * 删除主题资源
     * @param subjectId
     * @param resourceId
     */
    public void deleteSubjectResouce(String subjectId,String resourceId)
    {
        if(!StringUtils.isBlank(resourceId))
        {
            String[] resourceIds = resourceId.split(",");

            for(String id : resourceIds)
            {
                subjectResourceRepository.deleteBySubjectIdAndResourceId(subjectId,id);
            }
        }
        else
        {
            subjectResourceRepository.deleteBySubjectId(subjectId);
        }
    }

    /**
     * 获取某个主题下资源
     * @param subjectId
     * @return
     */
    public  List<RsAuthorizeSubjectResource> getSubjectResources(String subjectId)
    {
        return subjectResourceRepository.findBySubjectId(subjectId);
    }
}
