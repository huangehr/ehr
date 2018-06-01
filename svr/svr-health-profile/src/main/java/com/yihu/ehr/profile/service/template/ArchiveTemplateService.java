package com.yihu.ehr.profile.service.template;

import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.profile.dao.ArchiveTemplateDao;
import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.model.ArchiveTemplate;
import com.yihu.ehr.profile.model.MCDADocument;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Sand
 * @version 1.0
 * @created 16-7月-2015 20:57:06
 */
@Service
@Transactional
public class ArchiveTemplateService extends BaseJpaService<ArchiveTemplate, ArchiveTemplateDao> {

    @Autowired
    private ArchiveTemplateDao templateDao;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    public ArchiveTemplate getTemplate(Integer id) {
        ArchiveTemplate template = templateDao.findOne(id);
        return template;
    }

    public boolean isExistName(String version, String title) {
        return templateDao.findByTitleAndCdaVersion(version, title).size() > 0;
    }

    public List<ArchiveTemplate> findByCdaVersionAndAndCdaCode(String version, String code) {
        return templateDao.findByCdaVersionAndAndCdaCode(version, code);
    }

    public List<ArchiveTemplate> findByCdaDocumentId(List<String> docIds) {
        return templateDao.findByCdaDocumentId(docIds);
    }

    public void delete (Integer id) throws Exception {
        ArchiveTemplate archiveTemplate = retrieve(id);
        if (archiveTemplate != null) {
            if (StringUtils.isNotEmpty(archiveTemplate.getPcUrl())) {
                fastDFSUtil.delete(archiveTemplate.getPcUrl().split(ArchiveTemplate.UrlSeparator)[0], archiveTemplate.getPcUrl().split(ArchiveTemplate.UrlSeparator)[1]);
            }
            if (StringUtils.isNotEmpty(archiveTemplate.getMobileUrl())) {
                fastDFSUtil.delete(archiveTemplate.getMobileUrl().split(ArchiveTemplate.UrlSeparator)[0], archiveTemplate.getMobileUrl().split(ArchiveTemplate.UrlSeparator)[1]);
            }
            templateDao.delete(id);
        }
    }
}


