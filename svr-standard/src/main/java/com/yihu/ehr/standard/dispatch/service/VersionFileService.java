package com.yihu.ehr.standard.dispatch.service;


import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


/**
 * �����׼�汾�ļ�
 *
 * @author AndyCai
 * @version 1.0
 * @created 24-7��-2015 13:56:50
 */
@Transactional
@Service
public class VersionFileService extends BaseHbmService {


    /**
     * ���ݴ��Ƚ��뱻�Ƚϱ�׼�汾ID��ȡ�汾�ļ���Ϣ
     * ��ʼ�汾�� ���Ƚϱ�׼�汾IDΪ0
     *
     * @param sourceVersionId
     * @param targetVersionId
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public VersionFileInfo getVersionFileInfo(String sourceVersionId, String targetVersionId) {

        Session session = currentSession();
        Query query = session.createSQLQuery("select id,file_name,file_path,create_dtime,create_user,file_pwd from std_version_file_info where source_version_id=:sourceId and target_version_id=:targetId");
        query.setString("sourceId", sourceVersionId);
        query.setString("targetId", targetVersionId);

        Object[] record = (Object[]) query.uniqueResult();

        VersionFileInfo info = null;
        if (record != null && record.length > 0) {
            info = new VersionFileInfo();
            info.setId(Long.parseLong(String.valueOf(record[0])));
            info.setFileName((String) record[1]);
            info.setFilePath((String) record[2]);
            info.setCreateDTime((Date) record[3]);
            info.setCreateUser((String) record[4]);
            info.setFile_pwd((String) record[5]);
            info.setSourceVersionId(sourceVersionId);
            info.setTargetVersionId(targetVersionId);
        }

        return info;
    }

    /**
     * ����汾�ļ���Ϣ����ʼ�汾�� ���Ƚϰ汾ID(��targetVersionId) Ϊ�գ�
     *
     * @param info
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public void saveVersionFileInfo(VersionFileInfo info) {

        VersionFileInfo fileInfo = (VersionFileInfo) info;
        Session session = currentSession();

        String strSql;
        Query query;
        //�Ȼ�ȡ�ֵ������
        strSql = "select max(id) from std_version_file_info";
        query = session.createSQLQuery(strSql);
        Object object = query.uniqueResult();

        long id = object == null ? 1 : Long.parseLong(object.toString()) + 1;

        strSql = "insert into std_version_file_info(id,source_version_id,target_version_id,file_name,file_path,create_dtime,create_user,file_pwd)" +
                " values(:id,:source_version_id,:target_version_id,:file_name,:file_path,:create_dtime,:create_user,:file_pwd)";

        query = session.createSQLQuery(strSql);
        query.setLong("id", id);
        query.setString("source_version_id", fileInfo.getSourceVersionId());
        query.setString("target_version_id", fileInfo.getTargetVersionId());
        query.setString("file_name", fileInfo.getFileName());
        query.setString("file_path", fileInfo.getFilePath());
        query.setDate("create_dtime", fileInfo.getCreateDTime());
        query.setString("create_user", fileInfo.getCreateUser());
        query.setString("file_pwd", fileInfo.getFile_pwd());

        query.executeUpdate();
    }

    public boolean delete(String sourceVersionCode, String targetVersionCode) {

        Session session = currentSession();
        String sql = "delete from std_version_file_info where source_version_id=:sourceVersionCode ";
        if (targetVersionCode != null && targetVersionCode.equals("")) {
            sql += "and target_version_id=:targetVersionCode";
        }
        Query query = session.createSQLQuery(sql);
        query.setString("sourceVersionCode", sourceVersionCode);
        if (targetVersionCode != null && targetVersionCode.equals("")) {
            query.setString("targetVersionCode", targetVersionCode);
        }
        int res = query.executeUpdate();
        return res >= 0;
    }

}