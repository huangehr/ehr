package com.yihu.ehr.std.service;

import com.yihu.ehr.data.SQLGeneralDAO;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * @author Sand
 * @version 1.0
 * @created 16-7月-2015 20:57:06
 */
@Transactional
@Service
public class DictEntryManager extends SQLGeneralDAO {

	public DictEntryManager(){
	}

	public void finalize() throws Throwable {
	}

	public DictEntry createDictEntry(Dict xDict) {
		DictEntry dictEntry = new DictEntry();
		dictEntry.setDict(xDict);

		return dictEntry;
	}

	/**
	 * 
	 * @param entry
	 */
	public int deleteEntry(DictEntry entry){
		Session session = currentSession();
		String sql = "delete from " + entry.getDict().getInnerVersion().getDictEntryTableName() + " where id = " + entry.getId();
		return session.createSQLQuery(sql).executeUpdate();
	}

	public int DeleteEntryByDictId(String strVersion,String strDictId)
	{
		Session session = currentSession();
		String sql = "delete from " + CDAVersion.getDictEntryTableName(strVersion) + " where dict_id = " + strDictId;
		return session.createSQLQuery(sql).executeUpdate();
	}


	public DictEntry getEntries(long id,Dict xDict)
	{
		Session session = currentSession();
		String strTableName = xDict.getInnerVersion().getDictEntryTableName();

		Query query = session.createSQLQuery("select id, code,value,dict_id,description,hash from " + strTableName + " where id = :id and dict_id=:dictId");
		query.setLong("id", id);
		query.setLong("dictId", xDict.getId());

		Object[] record = (Object[]) query.uniqueResult();

		DictEntry dictEntry =null;
		if(record.length>0)
		{
			dictEntry=new DictEntry();
			dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
			dictEntry.setCode((String) record[1]);
			dictEntry.setValue((String) record[2]);
			dictEntry.setDict(xDict);
			dictEntry.setDesc((String) record[4]);
			dictEntry.setHashCode((Integer)record[5]);
		}
		return dictEntry;
	}
	//public
	/**
	 * 保存字典项
	 * @param entry
	 */
	public int saveEntry(DictEntry entry){
		DictEntry dictEntry = (DictEntry)entry;
		Session session = currentSession();

		String strTableName = dictEntry.getDict().getInnerVersion().getDictEntryTableName();
		String sql;
		Query query;

		long id=dictEntry.getId();
		if (id==0) {//新增字典值

			//先获取字典最大编号
			sql = "select max(id) from " + strTableName;
			query = session.createSQLQuery(sql);
			Object object = query.uniqueResult();

			id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
			dictEntry.setId(id);
			//新增字典值
			sql = "insert into " + strTableName +
					"(id, code, value,dict_id,description,hash) " +
					"values(:id, :code, :vaule, :dictId,:desc,:hash)";
		} else {//修改字典值
			sql = "update " + strTableName +
					" set " +
					"code = :code, " +
					"value = :vaule, " +
					"dict_id = :dictId, " +
					"description = :desc, " +
					"hash=:hash "+
					"where id = :id";
		}

		query = session.createSQLQuery(sql);
		query.setLong("id", dictEntry.getId());
		query.setString("code", dictEntry.getCode());
		query.setString("vaule", dictEntry.getValue());
		query.setLong("dictId", dictEntry.getDict().getId());
		query.setString("desc", dictEntry.getDesc());
		query.setInteger("hash",dictEntry.getHashCode());
		return query.executeUpdate();
	}

	public boolean isDictEntryCodeExist(Dict dict, String code){
		Session session = currentSession();
		String strTableName = dict.getInnerVersion().getDictEntryTableName();
		StringBuilder sb = new StringBuilder();
		sb.append(" select  1   from   " + strTableName );
		sb.append(" where dict_id = " + dict.getId());
		sb.append(" and code = '" + code + "' ");
		String strSql = sb.toString();
		Query query = session.createSQLQuery(strSql);
		return query.list().size()>0?true:false;
	}

}//end DictEntryManager