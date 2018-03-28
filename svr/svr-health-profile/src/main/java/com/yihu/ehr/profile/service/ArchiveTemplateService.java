package com.yihu.ehr.profile.service;

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
    private CDADocumentClient cdaDocumentClient;
    @Autowired
    private ArchiveTemplateDao templateDao;

    public ArchiveTemplate getTemplate(Integer id) {
        ArchiveTemplate template = templateDao.findOne(id);
        return template;
    }

    public ArchiveTemplate getTemplate(String orgCode, String cdaDocumentId, String cdaVersion) {
        ArchiveTemplate template = templateDao.findByOrganizationCodeAndCdaDocumentIdAndCdaVersion(orgCode, cdaDocumentId, cdaVersion);
        return template;
    }

    public ArchiveTemplate getPresriptionTemplate(String orgCode, String cdaVersion, String cdaCode) {
        ArchiveTemplate template = templateDao.findByOrganizationCodeAndCdaVersionAndCdaCode(orgCode, cdaVersion, cdaCode);
        return template;
    }

    public List<ArchiveTemplate> searchTemplate(String fields, String filters, String sorts, int page, int size) throws ParseException {
        return search(fields, filters, sorts, page, size);
    }

    public boolean isExistName(String title, String cdaVersion, String orgCode){

        return templateDao.countByTitleAndCdaVersionAndOrgCode(title, cdaVersion, orgCode)>0;
    }

    public void createTemplate(ArchiveTemplate template) {
        save(template);
    }

    public void updateTemplate(ArchiveTemplate template) {
        save(template);
    }

    public void deleteTemplate(int id) {
        templateDao.delete(id);
    }

    /**
     * 按组织机构获取指定CDA类别的文档列表。
     *
     * @param orgCode
     * @param cdaVersion
     * @param cdaType
     * @return
     */
    public Map<ArchiveTemplate, MCDADocument> getOrganizationTemplates(String orgCode, String cdaVersion, String cdaType){
        List<ArchiveTemplate> templates = templateDao.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
        List<String> cdaDocumentIdList = new ArrayList<>(templates.size());
        cdaDocumentIdList.addAll(templates.stream().map(template -> template.getCdaDocumentId()).collect(Collectors.toList()));

        if (cdaDocumentIdList.size() == 0) return null;

        List<MCDADocument> documentList = cdaDocumentClient.getCDADocumentByIds(
                "id,name",
                "id=" + String.join(",", cdaDocumentIdList) + ";type=" + cdaType,
                "+name",
                1000,
                1,
                cdaVersion);

        Map<ArchiveTemplate, MCDADocument> cdaDocumentMap =  new HashMap<>();
        for (MCDADocument document : documentList){
            String cdaDocumentId = document.getId();
            for (ArchiveTemplate template : templates){
                if(StringUtils.isEmpty(template.getCdaDocumentId())) continue;

                if(template.getCdaDocumentId().equals(cdaDocumentId)){
                    cdaDocumentMap.put(template, document);
                    break;
                }
            }
        }

        return cdaDocumentMap;
    }

    public Pair<ArchiveTemplate, MCDADocument> getOrganizationTemplate(String orgCode, String cdaVersion, String cdaType, String cdaDocumentId) throws Exception {
        ArchiveTemplate template = templateDao.findByOrganizationCodeAndCdaVersionAndCdaDocumentId(orgCode, cdaVersion, cdaDocumentId);
        if (template == null) return null;

        List<MCDADocument> documentList = cdaDocumentClient.getCDADocumentByIds(
                "id,name",
                "id=" + cdaDocumentId + ";type=" + cdaType,
                "+name",
                1,
                1,
                cdaVersion);
        if (CollectionUtils.isEmpty(documentList)) return null;

        Pair<ArchiveTemplate, MCDADocument> cdaDocumentMap =  new ImmutablePair<>(template, documentList.get(0));
        return cdaDocumentMap;
    }

    @Transactional(readOnly = true)
    public List<ArchiveTemplate> findByOrganizationCodeAndCdaVersion(String orgCode, String cdaVersion){
        return templateDao.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
    }

    @Transactional(readOnly = true)
    public ArchiveTemplate findByOrganizationCodeAndCdaVersionAndCdaCode(String orgCode, String cdaVersion, String cdaCode) {
        return templateDao.findByOrganizationCodeAndCdaVersionAndCdaCode(orgCode, cdaVersion, cdaCode);
    }
}


