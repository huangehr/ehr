//package com.yihu.ehr.standard.cdaversion.service;
//
//import com.yihu.ehr.log.LogService;
//import com.yihu.ehr.standard.commons.BaseManager;
//import org.hibernate.Query;
//import org.hibernate.Session;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import org.springframework.transaction.annotation.Transactional;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * 版本管理实现类。
// *
// * @author Sand
// * @version 1.0
// * @updated 02-7月-2015 14:40:20
// */
//@Service
//@Transactional
//public class CDAVersionManager extends BaseManager{
//    public static final String FBVersion = "000000000000";
//
//    @Autowired
//    XCDAVersionRepository cdaVersionRepository;
//
//    /**
//     * 创建一个阶段性版本. 参数中的版本不能处于编辑状态.
//     *
//     * @param baseVersion
//     * @param author
//     */
//    @Transactional(Transactional.TxType.REQUIRED)
//    public CDAVersion createStageVersion(CDAVersion baseVersion, String author) {
//        if (author == null || author.length() == 0) throw new IllegalArgumentException("作者不能为空");
//
//        CDAVersion stagedVersion = null;
//        if (baseVersion == null) {
//            if (getVersionList().length != 0) {
//                throw new IllegalArgumentException("基础版本不能为空");
//            } else {
//                // 空库的情况下，使用 XCDAVersionManager.FBVersion 作为初始版本
//                stagedVersion = new CDAVersion(null, author, "0.1");
//                stagedVersion.setVersion(CDAVersionManager.FBVersion);
//            }
//
//        } else {
//            stagedVersion = new CDAVersion(baseVersion.getVersion(), author, baseVersion.getVersionName());
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
//        }
//
//        buildVersionTables(stagedVersion);
//
//        cdaVersionRepository.save(stagedVersion);
//        return stagedVersion;
//    }
//
//    /**
//     * 根据版本号获取版本信息
//     * @param version
//     * @return
//     */
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public CDAVersion getVersion(String version) {
//        return  cdaVersionRepository.findOne(version);
//    }
//
//    /**
//     * 获取最新的已发布版本
//     * @return
//     */
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public CDAVersion getLatestVersion() {
//        Session session = currentSession();
//
//        Query query = session.createQuery("from CDAVersion a where a.inStage = false order by a.version desc");
//        query.setFirstResult(0);
//        query.setMaxResults(1);
//
//        List<CDAVersion> versions = query.list();
//        CDAVersion cdaVersion = versions.size() > 0 ? versions.get(0) : null;
//
//        return cdaVersion;
//    }
//
//    /**
//     * 获取所有版本
//     * @return
//     */
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public CDAVersion[] getVersionList() {
//        Session session = currentSession();
//        Query query = session.createQuery("from CDAVersion order by commitTime");
//        List<CDAVersion> versionList = query.list();
//        return versionList.toArray(new CDAVersion[versionList.size()]);
//    }
//
//    /**
//     * 发布版本
//     * @param version
//     */
//    @Transactional(Transactional.TxType.REQUIRED)
//    public void commitVersion(CDAVersion version) {
//        if (!version.isInStage()) {
//            throw new IllegalArgumentException("此版本未处于版本化编辑状态");
//        }
//        version.setInStage(false);
//        version.setCommitTime(new Date());
//        cdaVersionRepository.save(version);
//    }
//
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    public void revertVersion(CDAVersion version) {
//        if (!version.isInStage()) {
//            throw new IllegalArgumentException("此版本未处于版本化编辑状态");
//        }
//        dropVersionTables(version);
//        cdaVersionRepository.delete(version);
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    public void dropVersion(CDAVersion version) {
//        Session session = currentSession();
//        Query query = session.createQuery("from CDAVersion where baseVersion = :version");
//        query.setString("version", version.getVersion());
//        CDAVersion subVersion = (CDAVersion) query.uniqueResult();
//        if (subVersion != null) {
//            subVersion.setBaseVersion(version.getBaseVersion());
//            session.save(subVersion);
//        }
//        dropVersionTables(version);
//        cdaVersionRepository.delete(version);
//    }
//
//    /**
//     * 回滚到编辑状态
//     * @param version
//     */
//    @Transactional(Transactional.TxType.REQUIRED)
//    public void rollbackToStage(CDAVersion version) {
//        // 已经是编辑状态，直接返回
//        if (version.isInStage()) {
//            return;
//        }
//
//        // 非最新版本，无法回滚
//        CDAVersion latestVersion = getLatestVersion();
//        if (latestVersion != null && !latestVersion.getVersion().equals(version.getVersion())) {
//            throw new IllegalArgumentException("非最新版本,无法回滚");
//        }
//
//        // 回滚
//        version.setInStage(true);
//        version.setCommitTime(new Date());
//        cdaVersionRepository.save(version);
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    private void dropVersionTables(CDAVersion version) {
//        String datasetTable = version.getDataSetTableName();
//        String metadataTable = version.getMetaDataTableName();
//        String dictTable = version.getDictTableName();
//        String dictEntryTable = version.getDictEntryTableName();
//        String cdaDocumentTable = version.getCDADocumentTableName();
//        String relationshipTable = version.getCDADatasetRelationshipTableName();
//
//        Session session = currentSession();
//
//        String[] toDropTables = {datasetTable, metadataTable, dictTable, dictEntryTable,cdaDocumentTable,relationshipTable};
//        for (String table : toDropTables) {
//            Query query = session.createSQLQuery("DROP TABLE IF EXISTS " + table);
//            query.executeUpdate();
//        }
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    private void buildVersionTables(CDAVersion version) {
//        //为空表示这是一个初始版本，不需要再创建表
//        if (version.getBaseVersion() == null) return;
//
//        String[] newTables = {version.getDataSetTableName(),
//                version.getMetaDataTableName(),
//                version.getDictTableName(),
//                version.getDictEntryTableName(),
//                version.getCDADocumentTableName(),
//                version.getCDADatasetRelationshipTableName()};
//
//        String[] baseTables = {version.getBaseDataSetTableName(),
//                version.getBaseMetaDataTableName(),
//                version.getBaseDictTableName(),
//                version.getBaseDictEntryTableName(),
//                version.getBaseCDADocumentTableName(),
//                version.getBaseCDADatasetRelationshipTableName()};
//
//        Session session = currentSession();
//        for (int i = 0; i < baseTables.length; ++i) {
//            String baseTable = baseTables[i];
//            String newTable = newTables[i];
//
//            Query query = session.createSQLQuery("CREATE TABLE " + newTable + " like " + baseTable);
//            query.executeUpdate();
//
//            query = session.createSQLQuery("INSERT INTO " + newTable + " SELECT * FROM " + baseTable);
//            query.executeUpdate();
//        }
//    }
//
//    //1多条件查询
//    @Transactional(Transactional.TxType.REQUIRED)
//    public List<CDAVersion> searchVersions(Map<String, Object> args) {
//        Session session = currentSession();
//        String version = (String) args.get("version");
//        String versionName = (String)args.get("versionName");
//        Integer page = (Integer) args.get("page");
//        Integer pageSize = (Integer) args.get("rows");
//        String hql = "from CDAVersion where (version like :version or versionName like :versionName) order by commitTime";
//        Query query = session.createQuery(hql);
//        query.setString("version", "%"+version+"%");
//        query.setString("versionName","%"+versionName+"%");
//        if(page!=0) {
//            query.setMaxResults(pageSize);
//            query.setFirstResult((page - 1) * pageSize);
//        }
//        return query.list();
//    }
//
//    //2查询符合条件记录数
//    @Transactional(Transactional.TxType.REQUIRED)
//    public Integer searchVersionInt(Map<String,Object> args){
//        Session session = currentSession();
//        String version = (String) args.get("version");
//        String versionName = (String)args.get("versionName");
//        String hql = "SELECT count(*) FROM CDAVersion WHERE (version like :version or versionName like :versionName)";
//        Query query = session.createQuery(hql);
//        query.setString("version", "%"+version+"%");
//        query.setString("versionName","%"+versionName+"%");
//        return Integer.parseInt(query.list().get(0).toString());
//    }
//
//    //3修改操作后更新到数据库
//    @Transactional(Transactional.TxType.REQUIRED)
//    public void updateVersion(CDAVersion xcdaVersion){
//        cdaVersionRepository.save(xcdaVersion);
//    }
//
//    @Transactional(Transactional.TxType.REQUIRED)
//    public Integer checkVersionName(String versionName){
//        Session session = currentSession();
//        String hql = "SELECT count(*) FROM CDAVersion WHERE versionName =:versionName";
//        Query query = session.createQuery(hql);
//        query.setString("versionName", versionName);
//        return Integer.parseInt(query.list().get(0).toString());
//
//    }
//
//    @Transactional(Transactional.TxType.SUPPORTS)
//    public Integer searchInStage() {
//        Session session = currentSession();
//        Query query = session.createQuery("SELECT count(*) FROM CDAVersion WHERE inStage = true");
//        return Integer.parseInt(query.list().get(0).toString());
//    }
//}