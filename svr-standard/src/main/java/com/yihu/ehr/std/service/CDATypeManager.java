package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.data.SQLGeneralDAO;
import com.yihu.ehr.util.log.LogService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * @author AndyCai
 * @version 1.0
 * @created 11-12月-2015 15:53:02
 */
@Transactional
@Service
public class CDATypeManager extends SQLGeneralDAO {

    public CDATypeManager() {

    }

    public void finalize() throws Throwable {

    }

    /**
     * 根据查询条件获取cda类别
     *
     * @param mapKey key:查询条件  page:查询页  rows:查询行数
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDAType> getCDATypeListByKey(Map<String, Object> mapKey) {

        List<CDAType> listInfo = null;
        try {
            String strKey = mapKey.get("key").toString();
            int iPage = Integer.parseInt(mapKey.get("page").toString());
            int iRows = Integer.parseInt(mapKey.get("rows").toString());

            Session session = currentSession();

            String strSql = "from CDAType a where 1=1";
            if (strKey != null && strKey != "") {
                strSql += " and (a.code like :key or a.name like :key)";
            }

            Query query = session.createQuery(strSql);
            if (strKey != null && strKey != "") {
                query.setString("key", "%" + strKey + "%");
            }

            query.setMaxResults(iRows);
            query.setFirstResult((iPage - 1) * iRows);

            listInfo = query.list();
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
        }

        return listInfo;
    }

    /*
    * 根据查询条件获取 数量
    * @param mapKey  key:查询条件
    * @return iCount 总量
    * */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int getCDATypeCountByKey(String strKey) {

        int iCount = 0;
        try {
            Session session = currentSession();

            String strSql = "from CDAType a where 1=1";
            if (strKey != null && strKey != "") {
                strSql += " and (a.code like :key or a.name like :key)";
            }

            Query query = session.createQuery(strSql);
            if (strKey != null && strKey != "") {
                query.setString("key", strKey);
            }

            iCount = query.list().size();
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
        }
        return iCount;
    }

    /**
     * @param info
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean insertCDAType(CDAType info) {

        boolean boolResult = true;
        try {
            saveEntity(info);
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
            boolResult = false;
        }

        return boolResult;
    }

    /**
     * @param info
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean updateCDAType(CDAType info) {

        boolean boolResult = true;
        try {
            updateEntity(info);
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
            boolResult = false;
        }

        return boolResult;
    }

    /**
     * 根据ID删除类别
     *
     * @param ids (批量删除时id以逗号隔开)
     * @return boolResult 操作结果
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public int deleteCDAType(String ids) {

        boolean boolResult = true;
        try {
            String[] strIds = ids.split(",");
            for (int i = 0; i < strIds.length; i++) {
                deleteEntity(getEntity(CDAType.class, strIds[i]));
            }

        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
            boolResult = false;
        }
        return 0;
    }

    /**
     * 根据id 获取类别信息
     *
     * @param ids
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDAType> getCDAtypeInfoByIds(String ids) {


        List<CDAType> listInfo = null;
        try {

            ids = ids.replaceAll(",", "','");
            ids = "'" + ids + "'";

            Session session = currentSession();

            String strSql = "from CDAType a where a.id in(" + ids + ")";

            Query query = session.createQuery(strSql);

            listInfo = query.list();
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
        }

        return listInfo;
    }

    /**
     * 根据父级ID获取下级类别
     *
     * @param strparentId
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDAType> getCDATypeListByParentId(String strparentId) {
        List<CDAType> listInfo = null;
        try {

            Session session = currentSession();

            String strSql = "from CDAType a where 1=1";
            if (strparentId.equals("")) {
                strSql += " and (a.parent_id is null or a.parent_id='')";
            } else {
                strSql += " and a.parent_id =:parent_id";
            }
            Query query = session.createQuery(strSql);
            if (!strparentId.equals("")) {
                query.setString("parent_id", strparentId);
            }

            listInfo = query.list();
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
        }

        return listInfo;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<CDAType> getParentType(String strId, String strKey) {
        List<CDAType> listType = null;

        try {
            Session session = currentSession();

            String strSql = "from CDAType a where 1=1 ";
            if (strId != null && strId.equals("")) {
                strSql += " and a.id !=:id ";
            }

            if (strKey != null && !strKey.equals("")) {
                strSql += " and (a.code like :strkey or a.name like :strkey)";
            }

            Query query = session.createQuery(strSql);

            if (strId != null && strId.equals("")) {
                query.setString("id", strId);
            }

            if (strKey != null && !strKey.equals("")) {
                query.setString("strkey", "%" + strKey + "%");
            }

            listType = query.list();
        } catch (Exception ex) {
            LogService.getLogger(CDATypeManager.class).error(ex.getMessage());
        }

        return listType;
    }
}