package com.yihu.ehr.standard.dict.service;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.query.BaseHbmService;
import com.yihu.ehr.util.CDAVersionUtil;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.23
 */
@Transactional
@Service
public class DictEntryService extends BaseHbmService<IDictEntry>{
	private final static String ENTITY_PRE = "com.yihu.ehr.standard.dict.service.DictEntry";

	public Class getServiceEntity(String version){
		try {
			return Class.forName(ENTITY_PRE + version);
		} catch (ClassNotFoundException e) {
			throw new ApiException(ErrorCode.NotFoundDictEntryView);
		}
	}

	public String getTaleName(String version){

		return CDAVersionUtil.getDictEntryTableName(version);
	}

	public boolean add(IDictEntry dictEntry, String version) {
		String sql =
				"INSERT INTO " + getTaleName(version) +
						"(code, value, dict_id, description, hash) " +
						"VALUES (:code, :value, :dict_id, :description, :hash)";
		Query query = currentSession().createSQLQuery(sql);
		query.setParameter("code", dictEntry.getCode());
		query.setParameter("value", dictEntry.getValue());
		query.setParameter("dict_id", dictEntry.getDictId());
		query.setParameter("description", dictEntry.getDesc());
		query.setParameter("hash", dictEntry.getHashCode());

		return query.executeUpdate()>0;
	}

	public boolean deleteByDictId(Object[] ids, String version){

		return deleteByField("dictId", ids, getServiceEntity(version)) > 0;
	}
}