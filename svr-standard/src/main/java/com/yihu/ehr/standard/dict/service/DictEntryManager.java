package com.yihu.ehr.standard.dict.service;



import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.commons.BaseManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * @author lincl
 * @version 1.0
 * @created 16-7��-2015 20:57:06
 */
@Transactional
@Service
public class DictEntryManager extends BaseManager{
	@Autowired
	private CDAVersionManager cdaVersionManager;


	/**
	 * �Ѳ�ѯ���ת��Ϊ��Ҫ�ĸ�ʽ
	 * @param ls
	 * @param dict
	 * @return
	 */
	private DictEntry[]  analyse(List<DictEntry> ls, Dict dict){
		DictEntry[] dictEntrys = new DictEntry[ls.size()];
		int i =0;
		for(DictEntry dictEntry: ls){
			dictEntry.setDict(dict);
			dictEntrys[i] = dictEntry;
			i++;
		}
		return dictEntrys;
	}

	/**
	 * ��ҳ��ѯ�ֵ�������ֵ���
	 * @param dict
	 * @param searchNm
	 * @param page
	 * @param rows
	 * @return
	 */
	public DictEntry[] getDictEntries(Dict dict, String searchNm, Integer page, Integer rows) {
		Session session = currentSession();
		String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();

		StringBuilder sb = new StringBuilder();
		sb.append(" select  id          ");
		sb.append("        ,code        ");
		sb.append("        ,value       ");
		sb.append("        ,dict_id as dictId   ");
		sb.append("        ,description as desc ");
		sb.append("  from   " + strTableName );
		sb.append("  where dict_id = :dictId ");

		if (!(searchNm==null || searchNm.equals(""))) {
			sb.append("    and (code like :searchNm or value like :searchNm)   ");
		}
		sb.append("    order by code asc  ");

		Query query = session.createSQLQuery(sb.toString());
		query.setParameter("dictId", dict.getId());
		if (!(searchNm==null || searchNm.equals(""))) {
			query.setString("searchNm", "%"+ searchNm +"%");
		}
		if(page>0){
			query.setMaxResults(rows);
			query.setFirstResult((page - 1) * rows);
		}
		query.setResultTransformer(Transformers.aliasToBean(DictEntry.class));
		return analyse(query.list(), dict);
	}

	/**
	 * ��ѯ��ҳ����
	 * @param dict
	 * @param searchNm
	 * @return
	 */
	public int getDictEntriesForInt(Dict dict, String searchNm){
		Session session = currentSession();
		String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();

		StringBuilder sb = new StringBuilder();
		sb.append(" select  count(*)   from   " + strTableName );
		sb.append("  where dict_id = :dictId " );

		if (!(searchNm==null || searchNm.equals(""))) {
			sb.append("    and (code like :searchNm or value like :searchNm)   ");
		}
		sb.append("    order by code asc  ");

		Query query = session.createSQLQuery(sb.toString());
		query.setParameter("dictId", dict.getId());
		if (!(searchNm==null || searchNm.equals(""))) {
			query.setString("searchNm", "%"+searchNm+"%");
		}
		return ((BigInteger)query.list().get(0)).intValue();
	}

	/**
	 * ͨ��id��ɾ���ֵ���
	 * @param xcdaVersion
	 * @param ids
	 * @return
	 */
	public int delDictEntryList(CDAVersion xcdaVersion, List<String> ids) {
		Session session = currentSession();
		String strTableName = xcdaVersion.getDictEntryTableName();
		String query = "delete from " + strTableName + " where Id in (:ids)";
		Query q = session.createSQLQuery(query);
		q.setParameterList("ids", ids);
		return q.executeUpdate();
	}

	/**
	 * ɾ���ֵ���
	 * @param entry
	 */
	public int deleteEntry(DictEntry entry){
		Session session = currentSession();
		String sql = "delete from " +
						cdaVersionManager.getVersion(entry.getDict().getInnerVersion()).getDictEntryTableName() +
							" where id = :id";
		Query q = session.createSQLQuery(sql);
		q.setParameter("id", entry.getId());
		return q.executeUpdate();
	}



	/**
	 * ��ȡ
	 * @param id
	 * @param xDict
	 * @return
	 */
	public DictEntry getEntries(long id, Dict xDict){
		Session session = currentSession();
		String strTableName = cdaVersionManager.getVersion(xDict.getInnerVersion()).getDictEntryTableName();

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
	 * �����ֵ���
	 * @param entry
	 */
	public int saveEntry(DictEntry entry){
		DictEntry dictEntry = (DictEntry)entry;
		Session session = currentSession();

		String strTableName = cdaVersionManager.getVersion(dictEntry.getDict().getInnerVersion()).getDictEntryTableName();
		String sql;
		Query query;

		long id=dictEntry.getId();
		if (id==0) {//�����ֵ�ֵ

			//�Ȼ�ȡ�ֵ������
			sql = "select max(id) from " + strTableName;
			query = session.createSQLQuery(sql);
			Object object = query.uniqueResult();

			id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
			dictEntry.setId(id);
			//�����ֵ�ֵ
			sql = "insert into " + strTableName +
					"(id, code, value,dict_id,description,hash) " +
					"values(:id, :code, :vaule, :dictId,:desc,:hash)";
		} else {//�޸��ֵ�ֵ
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

	/**
	 * �жϴ����Ƿ��Ѵ���
	 * @param dict
	 * @param code
	 * @return
	 */
	public boolean isDictEntryCodeExist(Dict dict, String code){
		Session session = currentSession();
		String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();
		StringBuilder sb = new StringBuilder();
		sb.append(" select  1   from   " + strTableName );
		sb.append(" where dict_id = " + dict.getId());
		sb.append(" and code = '" + code + "' ");
		String strSql = sb.toString();
		Query query = session.createSQLQuery(strSql);
		return query.list().size()>0?true:false;
	}

	/**
	 * �����ֵ�������
	 * @param dictEntry
	 * @param id
	 * @param dict
	 * @param code
	 * @param value
	 * @param desc
	 * @return
	 */
	public DictEntry setDictEntryValues(DictEntry dictEntry, long id, Dict dict, String code, String value, String desc){
		dictEntry.setId(id);
		dictEntry.setDict(dict);
		dictEntry.setCode(code);
		dictEntry.setValue(value);
		dictEntry.setDesc(desc);
		return dictEntry;
	}

	/**
	 * ģ��ת��
	 * @param ls
	 * @return
	 */
	public List<DictEntryForInterface> dictEntryTransfer(DictEntry[] ls){
		List<DictEntryForInterface> rs = new ArrayList<>();
		if(ls==null)
			return rs;
		DictEntryForInterface info;
		for (DictEntry dictEntry : ls) {
			info = new DictEntryForInterface();
			info.setId(String.valueOf(dictEntry.getId()));
			info.setCode(dictEntry.getCode());
			info.setValue(dictEntry.getValue());
			info.setDictId(String.valueOf(dictEntry.getDictId()));
			info.setDesc(dictEntry.getDesc());
			rs.add(info);
		}
		return rs;
	}










	/**
	 * �����ֵ������
	 * @param xDict
	 * @return
	 */
	public DictEntry createDictEntry(Dict xDict) {
		DictEntry dictEntry = new DictEntry();
		dictEntry.setDict(xDict);
		return dictEntry;
	}


	/**
	 * ɾ���������ֵ���
	 * @param strVersion
	 * @param strDictId
	 * @return
	 */
	public int DeleteEntryByDictId(String strVersion, String strDictId){
		Session session = currentSession();
		String sql = "delete from " + CDAVersion.getDictEntryTableName(strVersion) + " where dict_id = :dictId";
		Query q = session.createSQLQuery(sql);
		q.setParameter("dictId", strDictId);
		return q.executeUpdate();
	}
}