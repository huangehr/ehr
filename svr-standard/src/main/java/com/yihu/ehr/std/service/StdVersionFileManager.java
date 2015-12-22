package com.yihu.ehr.std.service;


import com.yihu.ehr.constrant.Services;
import com.yihu.ehr.std.data.SQLGeneralDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;


/**
 * 管理标准版本文件
 * @author AndyCai
 * @version 1.0
 * @created 24-7月-2015 13:56:50
 */
@Transactional
@Service
public class StdVersionFileManager extends SQLGeneralDAO {

	public StdVersionFileManager(){

	}

	public void finalize() throws Throwable {

	}

	@Transactional(Transactional.TxType.SUPPORTS)
	public StdVersionFileInfo createStdVersionFileInfo()
	{
		return new StdVersionFileInfo();
	}
	/**
	 * 根据待比较与被比较标准版本ID获取版本文件信息
	 * 初始版本则 被比较标准版本ID为0
	 * 
	 * @param sourceVersionId
	 * @param targetVersionId
	 */

	@Transactional(Transactional.TxType.SUPPORTS)
	public StdVersionFileInfo GetVersionFileInfo(String sourceVersionId, String targetVersionId){

		Session session =currentSession();
		Query query = session.createSQLQuery("select id,file_name,file_path,create_dtime,create_user,file_pwd from std_version_file_info where source_version_id=:sourceId and target_version_id=:targetId");
		query.setString("sourceId", sourceVersionId);
		query.setString("targetId", targetVersionId);

		Object[] record=(Object[]) query.uniqueResult();

		StdVersionFileInfo info=null;
		if (record != null && record.length>0)
		{
			info=new StdVersionFileInfo();
			info.setId(Long.parseLong(String.valueOf(record[0])));
			info.setFileName((String)record[1]);
			info.setFilePath((String)record[2]);
			info.setCreateDTime((Date)record[3]);
			info.setCreateUser((String)record[4]);
			info.setFile_pwd((String)record[5]);
			info.setSourceVersionId(sourceVersionId);
			info.setTargetVersionId(targetVersionId);
		}

		return info;
	}

	/**
	 * 保存版本文件信息（初始版本的 被比较版本ID(即targetVersionId) 为空）
	 * 
	 * @param info
	 */
	@Transactional(Transactional.TxType.SUPPORTS)
	public void SaveVersionFileInfo(StdVersionFileInfo info){

		StdVersionFileInfo fileInfo=(StdVersionFileInfo)info;
		Session session = currentSession();

		String strSql;
		Query query;
		//先获取字典最大编号
		strSql = "select max(id) from std_version_file_info";
		query = session.createSQLQuery(strSql);
		Object object = query.uniqueResult();

		long id = object == null ? 1 : Long.parseLong(object.toString()) + 1;

		strSql="insert into std_version_file_info(id,source_version_id,target_version_id,file_name,file_path,create_dtime,create_user,file_pwd)"+
				" values(:id,:source_version_id,:target_version_id,:file_name,:file_path,:create_dtime,:create_user,:file_pwd)";

		query = session.createSQLQuery(strSql);
		query.setLong("id",id);
		query.setString("source_version_id", fileInfo.getSourceVersionId());
		query.setString("target_version_id",fileInfo.getTargetVersionId());
		query.setString("file_name",fileInfo.getFileName());
		query.setString("file_path",fileInfo.getFilePath());
		query.setDate("create_dtime",fileInfo.getCreateDTime());
		query.setString("create_user",fileInfo.getCreateUser());
		query.setString("file_pwd",fileInfo.getFile_pwd());

		query.executeUpdate();
	}
}//end StdVersionFileManager