package com.yihu.ehr.profile.converter;

import com.yihu.ehr.constants.EventType;
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.profile.config.CdaDocumentTypeOptions;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.model.Template;
import com.yihu.ehr.profile.service.TemplateService;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 健康档案工具。
 *
 * @author Sand
 * @created 2016.05.03 14:08
 */
@Service
public class StdProfileConverter {
    @Autowired
    private TemplateService templateService;

    @Autowired
    private XCDADocumentClient cdaDocumentClient;

    @Autowired
    private CdaDocumentTypeOptions cdaDocumentTypeOptions;



    /**
     * 获取机构定制CDA文档列表
     * <p>
     * 定制的CDA文档列表根据档案类别，从而获取这份档案的CDA类别。此CDA类别包含与文档相关的模板，CDA文档。
     */
    protected Map<Template, MCDADocument> getCustomizedCDADocuments(String cdaVersion, String orgCode, EventType eventType) throws Exception {
        // 使用事件-CDA类别映射，取得与此档案相关联的CDA类别ID
        String cdaType = cdaDocumentTypeOptions.getCdaDocumentTypeId(Integer.toString(eventType.getType()));

        if (StringUtils.isEmpty(cdaType)) {
            LogService.getLogger().error("Cannot find cda document type by health event, forget event & cda document type mapping?");

            return null;
        }

        // 此类别下卫生机构定制的CDA文档列表
        Map<Template, MCDADocument> cdaDocuments = templateService.getOrganizationTemplates(orgCode, cdaVersion, cdaType);
        if (CollectionUtils.isEmpty(cdaDocuments)) {
            LogService.getLogger().error(
                    String.format("Unable to get cda document of version %s for organization %s, template not prepared?",
                            cdaVersion, orgCode));

            return null;
        }

        return cdaDocuments;
    }

    protected Pair<Template, MCDADocument> getCustomizedCDADocument(String cdaVersion, String orgCode, EventType eventType, String cdaDocumentId) throws Exception{
        String cdaType = cdaDocumentTypeOptions.getCdaDocumentTypeId(Integer.toString(eventType.getType()));

        if (StringUtils.isEmpty(cdaType)) {
            LogService.getLogger().error("Cannot find cda document type by health event, forget event & cda document type mapping?");

            return null;
        }

        Pair<Template, MCDADocument> cdaDocuments = templateService.getOrganizationTemplate(orgCode, cdaVersion, cdaType, cdaDocumentId);
        if (cdaDocuments == null) {
            LogService.getLogger().error(
                    String.format("Unable to get cda document of version %s for organization %s, template not prepared?",
                            cdaVersion, orgCode));

            return null;
        }

        return cdaDocuments;
    }
}
