package com.yihu.ehr.profile.service;

import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.feign.XCDAVersionClient;
import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Sand
 * @version 1.0
 * @created 16-7月-2015 20:57:06
 */
@Service
@Transactional
public class TemplateService extends BaseJpaService<Template, XTemplateRepository> {
    @Autowired
    XCDAVersionClient cdaVersionClient;

    @Autowired
    XCDADocumentClient cdaDocumentClient;

    public Template getTemplate(Integer id) {
        Template template = getRepo().findOne(id);
        return template;
    }

    public Template getTemplate(String orgCode, String cdaDocumentId, String cdaVersion) {
        Template template = getRepo().findByOrganizationCodeAndCdaDocumentIdAndCdaVersion(orgCode, cdaDocumentId, cdaVersion);
        return template;
    }

    public List<Template> searchTemplate(String fields, String filters, String sorts, int page, int size) throws ParseException {
        return search(fields, filters, sorts, page, size);
    }

    public boolean isExistName(String title, String cdaVersion, String orgCode){

        return getRepo().countByTitleAndCdaVersionAndOrgCode(title, cdaVersion, orgCode)>0;
    }
    public void createTemplate(Template template) {
        save(template);
    }

    public void updateTemplate(Template template) {
        save(template);
    }

    public void deleteTemplate(int id) {
        getRepo().delete(id);
    }

    /**
     * 按组织机构获取指定CDA类别的文档列表。
     *
     * @param orgCode
     * @param cdaVersion
     * @param cdaType
     * @return
     */
    public Map<Template, MCDADocument> getOrganizationTemplates(String orgCode, String cdaVersion, String cdaType) {
        List<Template> templates = getRepo().findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
        List<String> cdaDocumentIdList = new ArrayList<>(templates.size());
        cdaDocumentIdList.addAll(templates.stream().map(template -> template.getCdaDocumentId()).collect(Collectors.toList()));

        if (cdaDocumentIdList.size() == 0) return null;

        List<MCDADocument> documentList = cdaDocumentClient.getCDADocumentByIds(
                "id,name",
                "id=" + String.join(",", cdaDocumentIdList) + "&type=" + cdaType,
                "+name",
                1000,
                1,
                cdaVersion);

        Map<Template, MCDADocument> cdaDocumentMap =  new TreeMap<>();
        for (MCDADocument document : documentList){
            String cdaDocumentId = document.getId();
            for (Template template : templates){
                if(StringUtils.isEmpty(template.getCdaDocumentId())) continue;

                if(template.getCdaDocumentId().equals(cdaDocumentId)){
                    cdaDocumentMap.put(template, document);
                    break;
                }
            }
        }

        return cdaDocumentMap;
    }

    private XTemplateRepository getRepo() {
        return (XTemplateRepository) getRepository();
    }
}