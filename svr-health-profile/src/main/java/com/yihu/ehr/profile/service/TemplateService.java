package com.yihu.ehr.profile.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.data.sql.SQLGeneralDAO;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.std.model.*;
import com.yihu.ha.util.fastdfs.FastDFSUtil;
import com.yihu.ha.util.operator.DateUtil;
import com.yihu.ha.util.operator.StringUtil;
import org.csource.common.MyException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Sand
 * @version 1.0
 * @created 16-7月-2015 20:57:06
 */
@Service
@Transactional
public class TemplateService extends BaseJpaService<Template, XTemplateRepository> {
    @Resource(name = Services.CDAVersionManager)
    XCDAVersionManager cdaVersionManager;

    @Resource(name = Services.CDADocumentManager)
    XCDADocumentManager cdaDocumentManager;

    /**
     * 根据ID获取模板接口.
     *
     * @param id
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Template getArchiveTemplate(Integer id) {

        Template template = getRepo().findOne(id);
        return template;
    }
    
    @Transactional(propagation = Propagation.SUPPORTS)
    public Template getArchiveTemplate(String orgCode, String cdaDocumentId, String cdaVersion) {
        return getRepo().findByOrgCodeAndCdaDocumentIdAndCdaVersion(orgCode, cdaDocumentId, cdaVersion);
    }

    /**
     * 根据条件搜索模板管理数据.
     *
     * @param args
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Template> searchTemplate(Map<String, Object> args) {
        String version = (String) args.get("version");
        String orgName = (String) args.get("orgName");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");

        Session session = currentSession();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();
        String hql = "";

        if (StringUtil.isEmpty(orgName)) {

            sb.append("   from Template     ");
            sb.append("  where 1=1            ");
            sb.append("    and cdaVersion = '" + version + "'");
            sb.append("    order by id  ");

            hql = sb.toString();

            Query query = session.createQuery(hql);

            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);

            List<Template> records = query.list();

            if (records.size() == 0) {
                return null;
            } else {
                return records;
            }
        } else {
            sb.append(" select  at.id                   ");
            sb.append("       , at.title                ");
            sb.append("       , at.org_code             ");
            sb.append("       , at.cda_document_id         ");
            sb.append("       , at.cda_version          ");
            sb.append("       , at.pc_template          ");
            sb.append("       , at.mobile_template      ");
            sb.append("       , at.create_time          ");
            sb.append("   from archive_template  at                   ");
            sb.append("        left join organizations org            ");
            sb.append("               on org.org_code = at.org_code   ");
            sb.append("  where org.full_name like '%" + orgName + "%'   ");
            sb.append("  or at.title like '%" + orgName + "%'   ");
            sb.append("    and at.cda_version = '" + version + "'        ");
            sb.append("    order by at.id               ");

            hql = sb.toString();

            Query query = session.createSQLQuery(hql);
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);

            List records = query.list();
            if (records.size() == 0) {
                return null;
            } else {
                Template Template = null;
                List<Template> TemplateList = new ArrayList<>();

                for (int i = 0; i < records.size(); ++i) {
                    Template = new Template();
                    Object[] record = (Object[]) records.get(i);

                    Template.setId(Integer.parseInt(StringUtil.toString(record[0])));
                    Template.setTitle((String) record[1]);
                    Template.setOrg((String) record[2]);
                    Template.setCdaDocumentId((String) record[3]);
                    Template.setCdaVersion((String) record[4]);
                    Template.setMobileTemplate((String) record[6]);
                    Template.setCreateTime((Date) record[7]);

                    TemplateList.add(Template);
                }
                return TemplateList;
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<TemplateDetailModel> searchTemplateDetailModel(List<Template> templateList) {
        Session session = currentSession();

        List<TemplateDetailModel> detailModelList = new ArrayList<>();
        int order = 1;
        for (Template template : templateList) {
            TemplateDetailModel detailModel = new TemplateDetailModel();
            detailModel.setOrder(order++);
            detailModel.setId(template.getId());
            detailModel.setTitle(template.getTitle());

            if (template.getOrg() != null) {
                detailModel.setOrgName(template.getOrg().getFullName());
            }

            XCDADocument cdaDocument = cdaDocumentManager.getDocument(template.getCdaVersion(), template.getCdaDocumentId());
            if (cdaDocument != null) {
                detailModel.setCdaDocId(template.getCdaDocumentId());
                detailModel.setCdaDocName(cdaDocument.getName());
                detailModel.setVersionId(Integer.toString(template.getId()));
                detailModel.setVersionName(template.getCdaVersion());
            }

            detailModelList.add(detailModel);
        }

        return detailModelList;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer searchTemplateEchoId(Map<String, Object> args) {
        String version = (String) args.get("version");
        String orgName = (String) args.get("orgName");

        Session session = currentSession();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();
        String hql = "";

        if (StringUtil.isEmpty(orgName)) {

            sb.append(" select 1 from archive_template     ");
            sb.append("  where 1=1            ");
            sb.append("    and cda_Version = '" + version + "'");
            sb.append("    order by id  ");

            hql = sb.toString();
        } else {
            sb.append(" select  1                   ");
            sb.append("   from archive_template  at                   ");
            sb.append("        left join organizations org            ");
            sb.append("               on org.org_code = at.org_code   ");
            sb.append("  where org.full_name like '%" + orgName + "%'   ");
            sb.append("    and at.cda_version = '" + version + "'        ");
            sb.append("    order by at.id                     ");

            hql = sb.toString();
        }

        Query query = session.createSQLQuery(hql);

        return query.list().size();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer validateTitle(String version,String title) {
        Session session = currentSession();
        String hql = "select title from archive_template where cda_version ='" + version + "' and title = '"+title+"'" ;
        Query query = session.createSQLQuery(hql);
        return query.list().size();
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public TemplateModel getTemplateById(String id) {
        Template template = getEntity(Template.class, Integer.parseInt(id));
        TemplateModel templateModel = new TemplateModel();
        templateModel.setVersion(template.getCdaVersion());

        if (template.getOrg() != null) {
            templateModel.setOrgCode(template.getOrg().getOrgCode());
            if (template.getOrg().getLocation() != null) {
                templateModel.setProvince(template.getOrg().getLocation().getProvince());
                templateModel.setCity(template.getOrg().getLocation().getCity());
            }
        }

        templateModel.setDsCode(template.getCdaDocumentId());

        return templateModel;
    }

    public void addTemplate(TemplateModel templateModel) {
        Template template = new Template();
        template.setCdaVersion(templateModel.getVersion());
        template.setOrg(templateModel.getOrgCode());
        template.setCdaDocumentId(templateModel.getDsCode());
        template.setCreateTime(DateUtil.getSysDate());

        if (!StringUtil.isEmpty(templateModel.getCopyId())) {
            Template copyTemplate = getEntity(Template.class, Integer.parseInt(templateModel.getCopyId()));
            //template.setPcTplContent(copyTemplate.getPcTplContent());
        }

        saveTemplate(template);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void copyTemplate(int pid,Template tplNew) {
        saveEntity(tplNew);
        Boolean flag = true;
        Template tplOld = getArchiveTemplate(pid);
        String[] tokens = tplOld.getPcTplUrl();
        if(tokens!=null&&!"".equals(tokens)){
            try{
                byte[] bytes = FastDFSUtil.download(tokens[0], tokens[1]);
                InputStream in = new ByteArrayInputStream(bytes);
                tplNew.setPcTplContent(in);
            }catch (Exception e){
                flag = false ;
            }
        }
        if (flag){
            this.updateEntity(tplNew);
        }
    }
    @Override
    public void saveTemplate(Template template) {
        saveEntity(template);
    }

    @Override
    public void updateTemplate(Template tpl) {
        updateEntity(tpl);
    }

    @Override
    public void deleteTemplate(int id) throws IOException, MyException {
        Template tpl = (Template) currentSession().get(Template.class, id);
        deleteEntity(tpl);
    }

    @Override
    public XCDADocument[] getAdaptedDocumentsList(String orgCode, String cdaVersion, String cdaType) {
        XCDAVersionManager cdaVersionManager = ServiceFactory.getService(Services.CDAVersionManager);
        XCDAVersion version = cdaVersionManager.getVersion(cdaVersion);

        Query query = getSessionFactory().getCurrentSession().createSQLQuery("SELECT cda_document_id " +
                "FROM archive_template tpl, std_cda_type cda_type, " + version.getCDADocumentTableName() + " documents " +
                "WHERE tpl.org_code = :orgCode AND tpl.cda_document_id = documents.id AND documents.type = cda_type.id AND " +
                "cda_type.id = :cdaType");
        query.setString("cdaType", cdaType);
        query.setString("orgCode", orgCode);

        List<String> cdaDocumentIdList = query.list();
        XCDADocumentManager xcdaDocumentManager = ServiceFactory.getService(Services.CDADocumentManager);
        return xcdaDocumentManager.getDocumentList(cdaVersion, cdaDocumentIdList);
    }

    private XTemplateRepository getRepo(){
        return (XTemplateRepository)getRepository();
    }
}