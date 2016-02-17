package com.yihu.ehr.standard.cda.service;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 版本管理实现类。
 *
 * @author Sand
 * @version 1.0
 * @updated 02-7月-2015 14:40:20
 */
@Service
public class CDAVersionManager {

    public static final String FBVersion = "000000000000";

    @PersistenceContext
    private EntityManager entityManager;

    Session currentSession(){
        return entityManager.unwrap(org.hibernate.Session.class);
    }

    public CDAVersionManager() {
    }

    /**
     * 创建一个阶段性版本. 参数中的版本不能处于编辑状态.
     *
     * @param baseVersion
     * @param author
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public CDAVersion createStageVersion(CDAVersion baseVersion, String author) throws Exception {
        if (author == null || author.length() == 0) throw new IllegalArgumentException("作者不能为空");

        CDAVersion stagedVersion = null;
        if (baseVersion == null) {
            if (getVersionList().size() != 0) {
                throw new IllegalArgumentException("基础版本不能为空");
            } else {
                // 空库的情况下，使用 CDAVersionManager.FBVersion 作为初始版本
                stagedVersion = new CDAVersion(null, author, "0.1");
                stagedVersion.setVersion(CDAVersionManager.FBVersion);
            }

        } else {
            stagedVersion = new CDAVersion(baseVersion.getVersion(), author, baseVersion.getVersionName());
            if (stagedVersion.getCommitTime() == null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date time = null;
                time = sdf.parse(sdf.format(new Date()));
                stagedVersion.setCommitTime(time);
            }
        }

        buildVersionTables(stagedVersion);

        //saveEntity(stagedVersion);

        return stagedVersion;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public CDAVersion getVersion(String version) {
        return null;
        //return (CDAVersion) getHibernateTemplate().get(CDAVersion.class, version);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public CDAVersion getLatestVersion() {
        Session session = currentSession();

        Query query = session.createQuery("from CDAVersion a where a.inStage = false order by a.version desc");
        //Query query = session.createQuery("from CDAVersion a where 1=1 order by a.version desc");
        query.setFirstResult(0);
        query.setMaxResults(1);

        List<CDAVersion> versions = query.list();
        CDAVersion cdaVersion = versions.size() > 0 ? versions.get(0) : null;

        return cdaVersion;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDAVersion> getVersionList() {
        Session session = currentSession();

        Query query = session.createQuery("from CDAVersion order by commitTime");
        List<CDAVersion> versionList = query.list();

        return versionList;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void commitVersion(CDAVersion version) {
        if (!version.isInStage()) {
            throw new IllegalArgumentException("此版本未处于版本化编辑状态");
        }

        CDAVersion cdaVersion = (CDAVersion) version;
        cdaVersion.setInStage(false);
        cdaVersion.setCommitTime(new Date());

        //updateEntity(version);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void revertVersion(CDAVersion version) {
        if (!version.isInStage()) {
            throw new IllegalArgumentException("此版本未处于版本化编辑状态");
        }

        dropVersionTables(version);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void dropVersion(CDAVersion version) {

        Session session = currentSession();
        Query query = session.createQuery("from CDAVersion where baseVersion = :version");
        query.setString("version", version.getVersion());

        CDAVersion subVersion = (CDAVersion) query.uniqueResult();
        if (subVersion != null) {
            subVersion.setBaseVersion(version.getBaseVersion());
            session.save(subVersion);
        }

        session.delete(version);

        dropVersionTables(version);
    }

    @Transactional(Transactional.TxType.REQUIRED)
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
        CDAVersion cdaVersion = (CDAVersion) version;
        cdaVersion.setInStage(true);
        cdaVersion.setCommitTime(new Date());

        //mergeEntity(cdaVersion);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private void dropVersionTables(CDAVersion version) {
        String datasetTable = version.getDataSetTableName();
        String metadataTable = version.getMetaDataTableName();
        String dictTable = version.getDictTableName();
        String dictEntryTable = version.getDictEntryTableName();
        String cdaDocumentTable = version.getCDADocumentTableName();
        String relationshipTable = version.getCDADatasetRelationshipTableName();

        Session session = currentSession();

        String[] toDropTables = {datasetTable, metadataTable, dictTable, dictEntryTable,cdaDocumentTable,relationshipTable};
        for (String table : toDropTables) {
            Query query = session.createSQLQuery("DROP TABLE IF EXISTS " + table);
            query.executeUpdate();
        }

        session.delete(version);                    // 删除版本对象
    }

    @Transactional(Transactional.TxType.REQUIRED)
    private void buildVersionTables(CDAVersion version) {
        //为空表示这是一个初始版本，不需要再创建表
        if (version.getBaseVersion() == null) return;

        String[] newTables = {version.getDataSetTableName(),
                version.getMetaDataTableName(),
                version.getDictTableName(),
                version.getDictEntryTableName(),
                version.getCDADocumentTableName(),
                version.getCDADatasetRelationshipTableName()};

        String[] baseTables = {version.getBaseDataSetTableName(),
                version.getBaseMetaDataTableName(),
                version.getBaseDictTableName(),
                version.getBaseDictEntryTableName(),
                version.getBaseCDADocumentTableName(),
                version.getBaseCDADatasetRelationshipTableName()};

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
    @Transactional(Transactional.TxType.REQUIRED)
    public List<CDAVersion> searchVersions(Map<String, Object> args) {
        Session session = currentSession();
        String version = (String) args.get("version");
        String versionName = (String)args.get("versionName");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");
        String hql = "from CDAVersion where (version like :version or versionName like :versionName) order by commitTime";
        Query query = session.createQuery(hql);
        query.setString("version", "%"+version+"%");
        query.setString("versionName","%"+versionName+"%");
        if(page!=0) {
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
        return query.list();
    }
    //2查询符合条件记录数
    @Transactional(Transactional.TxType.REQUIRED)
    public Integer searchVersionInt(Map<String,Object> args){
        Session session = currentSession();
        String version = (String) args.get("version");
        String versionName = (String)args.get("versionName");
        String hql = "SELECT count(*) FROM CDAVersion WHERE (version like :version or versionName like :versionName)";
        Query query = session.createQuery(hql);
        query.setString("version", "%"+version+"%");
        query.setString("versionName","%"+versionName+"%");
        return Integer.parseInt(query.list().get(0).toString());
    }

    //3修改操作后更新到数据库
    @Transactional(Transactional.TxType.REQUIRED)
    public void updateVersion(CDAVersion xcdaVersion){
        //this.updateEntity(xcdaVersion);

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Integer checkVersionName(String versionName){
        Session session = currentSession();
        String hql = "SELECT count(*) FROM CDAVersion WHERE versionName =:versionName";
        Query query = session.createQuery(hql);
        query.setString("versionName", versionName);
        return Integer.parseInt(query.list().get(0).toString());

    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchInStage() {
        Session session = currentSession();
        Query query = session.createQuery("SELECT count(*) FROM CDAVersion WHERE isInStage = true");
        return Integer.parseInt(query.list().get(0).toString());
    }
}