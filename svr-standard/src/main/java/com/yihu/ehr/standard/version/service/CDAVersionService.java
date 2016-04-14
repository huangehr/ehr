package com.yihu.ehr.standard.version.service;

import com.yihu.ehr.config.StdHibernateConfig;
import com.yihu.ehr.config.StdSessionFactoryBean;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.CDAVersionUtil;
import com.yihu.ehr.util.classpool.ClassPoolUtils;
import com.yihu.ehr.util.log.LogService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版本管理实现类。
 * @author lincl
 * @version 1.0
 * @created 2016.2.3
 */
@Service
public class CDAVersionService extends BaseJpaService<CDAVersion, XCDAVersionRepository> {
    public static final String FBVersion = "000000000000";

    @Autowired
    StdSessionFactoryBean sessionFactory;

    /**
     * 创建一个阶段性版本. 参数中的版本不能处于编辑状态.
     *
     * @param baseVersion
     * @param author
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CDAVersion createStageVersion(CDAVersion baseVersion, String author) throws Exception {
        if (author == null || author.length() == 0) throw new IllegalArgumentException("作者不能为空");

        CDAVersion stagedVersion = null;
        if (baseVersion == null) {
            if (getVersionList().length != 0) {
                throw new IllegalArgumentException("基础版本不能为空");
            } else {
                // 空库的情况下，使用 XCDAVersionManager.FBVersion 作为初始版本
                stagedVersion = new CDAVersion(null, author, "0.1");
                stagedVersion.setVersion(CDAVersionService.FBVersion);
            }

        } else {
            stagedVersion = new CDAVersion(baseVersion.getVersion(), author, baseVersion.getVersionName());
//            if (stagedVersion.getCommitTime() == null) {
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//                Date time = null;
//                try {
//                    time = sdf.parse(sdf.format(new Date()));
//                    stagedVersion.setCommitTime(time);
//                } catch (ParseException e) {
//                    LogService.getLogger().error(e.getMessage());
//                }
//            }
        }

        buildVersionTables(stagedVersion);
        save(stagedVersion);
        createVersionedClass(stagedVersion);
        return stagedVersion;
    }

    private void createVersionedClass(CDAVersion stagedVersion) throws Exception {
        String version = stagedVersion.getVersion();
        List<Class> tableClass = new ArrayList<>();
        for (String vesionedEntity : StdHibernateConfig.vesionedEntitys.keySet()){
            tableClass.add(
                    ClassPoolUtils.tableMapping(
                            vesionedEntity,
                            StdHibernateConfig.vesionedEntitys.get(vesionedEntity) + version,
                            vesionedEntity + version));
        }
        sessionFactory.addClassToBuildSessionFactory(tableClass.toArray(new Class[tableClass.size()]));
    }

    /**
     * 根据版本号获取版本信息
     *
     * @param version
     * @return
     */
    public CDAVersion getVersion(String version) {
        return retrieve(version);
    }

    /**
     * 获取最新的已发布版本
     *
     * @return
     */
    public CDAVersion getLatestVersion() {
        Session session = currentSession();

        Query query = session.createQuery("from CDAVersion a where a.inStage = false order by a.version desc");
        query.setFirstResult(0);
        query.setMaxResults(1);

        List<CDAVersion> versions = query.list();
        CDAVersion cdaVersion = versions.size() > 0 ? versions.get(0) : null;

        return cdaVersion;
    }

    /**
     * 获取所有版本
     *
     * @return
     */
    public CDAVersion[] getVersionList() {
        Session session = currentSession();
        Query query = session.createQuery("from CDAVersion order by commitTime");
        List<CDAVersion> versionList = query.list();
        return versionList.toArray(new CDAVersion[versionList.size()]);
    }

    /**
     * 发布版本
     *
     * @param version
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void commitVersion(CDAVersion version) {

        version.setInStage(false);
        version.setCommitTime(new Date());
        save(version);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void revertVersion(CDAVersion version) {

        dropVersionTables(version);
        delete(version);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void dropVersion(CDAVersion version) {
        Session session = currentSession();
        Query query = session.createQuery("from CDAVersion where baseVersion = :version");
        query.setString("version", version.getVersion());
        CDAVersion subVersion = (CDAVersion) query.uniqueResult();
        if (subVersion != null) {
            subVersion.setBaseVersion(version.getBaseVersion());
            session.save(subVersion);
        }
        dropVersionTables(version);
        delete(version.getVersion());
    }

    /**
     * 回滚到编辑状态
     *
     * @param version
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void rollbackToStage(CDAVersion version) {
        // 已经是编辑状态，直接返回
        if (version.isInStage()) {
            return;
        }

        // 非最新版本，无法回滚
        CDAVersion latestVersion = getLatestVersion();
        if (latestVersion != null && !latestVersion.getVersion().equals(version.getVersion())) {
            throw new IllegalArgumentException("非最新版本,无法回滚");
        }

        // 回滚
        version.setInStage(true);
        version.setCommitTime(new Date());
        save(version);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void dropVersionTables(CDAVersion cdaVersionVersion) {
        String version = cdaVersionVersion.getVersion();
        String datasetTable = CDAVersionUtil.getDataSetTableName(version);
        String metadataTable = CDAVersionUtil.getMetaDataTableName(version);
        String dictTable = CDAVersionUtil.getDictTableName(version);
        String dictEntryTable = CDAVersionUtil.getDictEntryTableName(version);
        String cdaDocumentTable = CDAVersionUtil.getCDATableName(version);
        String relationshipTable = CDAVersionUtil.getCDADatasetRelationshipTableName(version);

        Session session = currentSession();

        String[] toDropTables = {datasetTable, metadataTable, dictTable, dictEntryTable, cdaDocumentTable, relationshipTable};
        for (String table : toDropTables) {
            Query query = session.createSQLQuery("DROP TABLE IF EXISTS " + table);
            query.executeUpdate();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    private void buildVersionTables(CDAVersion cdaVersion) {
        //为空表示这是一个初始版本，不需要再创建表
        if (cdaVersion.getBaseVersion() == null) return;
        String version = cdaVersion.getVersion();
        String[] newTables = {CDAVersionUtil.getDataSetTableName(version),
                CDAVersionUtil.getMetaDataTableName(version),
                CDAVersionUtil.getDictTableName(version),
                CDAVersionUtil.getDictEntryTableName(version),
                CDAVersionUtil.getCDATableName(version),
                CDAVersionUtil.getCDADatasetRelationshipTableName(version)};

        version = cdaVersion.getBaseVersion();
        String[] baseTables = {CDAVersionUtil.getDataSetTableName(version),
                CDAVersionUtil.getMetaDataTableName(version),
                CDAVersionUtil.getDictTableName(version),
                CDAVersionUtil.getDictEntryTableName(version),
                CDAVersionUtil.getCDATableName(version),
                CDAVersionUtil.getCDADatasetRelationshipTableName(version)};

        Session session = currentSession();
        for (int i = 0; i < baseTables.length; ++i) {
            String baseTable = baseTables[i];
            String newTable = newTables[i];

            Query query = session.createSQLQuery("CREATE TABLE " + newTable + " like " + baseTable);
            query.executeUpdate();

            query = session.createSQLQuery("INSERT INTO " + newTable + " SELECT * FROM " + baseTable);
            query.executeUpdate();
        }
    }

    //1多条件查询
    public List<CDAVersion> searchVersions(Map<String, Object> args) {
        Session session = currentSession();
        String version = (String) args.get("version");
        String versionName = (String) args.get("versionName");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");
        String hql = "from CDAVersion where (version like :version or versionName like :versionName) order by commitTime";
        Query query = session.createQuery(hql);
        query.setString("version", "%" + version + "%");
        query.setString("versionName", "%" + versionName + "%");
        if (page != 0) {
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
        return query.list();
    }

    //2查询符合条件记录数
    public Integer searchVersionInt(Map<String, Object> args) {
        Session session = currentSession();
        String version = (String) args.get("version");
        String versionName = (String) args.get("versionName");
        String hql = "SELECT count(*) FROM CDAVersion WHERE (version like :version or versionName like :versionName)";
        Query query = session.createQuery(hql);
        query.setString("version", "%" + version + "%");
        query.setString("versionName", "%" + versionName + "%");
        return Integer.parseInt(query.list().get(0).toString());
    }


    public Integer checkVersionName(String versionName) {
        Session session = currentSession();
        String hql = "SELECT count(*) FROM CDAVersion WHERE versionName =:versionName";
        Query query = session.createQuery(hql);
        query.setString("versionName", versionName);
        return Integer.parseInt(query.list().get(0).toString());

    }

    public Integer searchInStage() {
        Session session = currentSession();
        Query query = session.createQuery("SELECT count(*) FROM CDAVersion WHERE inStage = true");
        return Integer.parseInt(query.list().get(0).toString());
    }
}