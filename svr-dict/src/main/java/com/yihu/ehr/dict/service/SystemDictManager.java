package com.yihu.ehr.dict.service;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统字典管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.07.30 11:46
 */
@Service
@Transactional
public class SystemDictManager {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private XSystemDictRepository systemDictRepository;

    @Autowired
    private XSystemDictEntryRepository systemDictEntryRepository;

    public SystemDict[] getDictList(int from, int count) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = session.createCriteria(SystemDict.class);
        if (from >= 0 && count > 0){
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }

        List<SystemDict> list = criteria.list();
        return list.toArray(new SystemDict[list.size()]);
    }

    public SystemDict getDict(long dictId) {
        return systemDictRepository.findOne(dictId);
    }

    public int getNextSort(long dictId) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("select max(sort) from SystemDictEntry  where id.dictId= '"+dictId+"'");
        int result;
        if(query.uniqueResult()==null){
            result = 1;
        }else{
            result = Integer.parseInt(query.uniqueResult().toString())+1;
        }
        return result;
    }

    public boolean isExistDict(String name){

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("from SystemDict where name = :name");
        query.setString("name", name);

        return query.list().size() != 0;
    }


    public SystemDict createDict(String name, String reference, String author) {
        SystemDict systemDict = new SystemDict();
        systemDict.setName(name);
        systemDict.setReference(reference);
        systemDict.setAuthorId(author);
        systemDictRepository.save(systemDict);
        return systemDict;
    }

    public void updateDict(SystemDict dict) {
        systemDictRepository.save(dict);
    }

    public void deleteDict(long dictId) {
        systemDictRepository.delete(dictId);

        systemDictEntryRepository.deleteByDictId(dictId);
    }



    public List<SystemDict> searchSysDicts(Map<String, Object> args) {

        //参数获取处理
        String name = (String) args.get("name");
        String phoneticCode = (String) args.get("name");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        Session session = entityManager.unwrap(org.hibernate.Session.class);

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append("   from SystemDict 	   ");
        sb.append("  where 1=1     ");

        if (!(args.get("name")==null || args.get("name").equals(""))) {

            sb.append("    and (name like '%" + name + "%' or phoneticCode like '%" + phoneticCode + "%')   ");
        }
        sb.append("    order by  name asc  ");
        String hql = sb.toString();

        Query query = session.createQuery(hql);

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        return query.list();
    }

    public Integer searchAppsInt(Map<String, Object> args) {

        //参数获取处理
        String name = (String) args.get("name");
        String phoneticCode = (String) args.get("name");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");

        Session session = entityManager.unwrap(org.hibernate.Session.class);

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select 1  from SystemDict 	   ");
        sb.append("  where 1=1     ");

        if (!(args.get("name")==null || args.get("name").equals(""))) {

            sb.append("    and (name like '%" + name + "%' or phoneticCode like '%" + phoneticCode + "%')   ");
        }

        String hql = sb.toString();

        Query query = session.createQuery(hql);

        return query.list().size();
    }

    public Map<String, Object> searchEntryList(Map<String, Object> args){
        Map<String,Object> infoMap= new HashMap<>();
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Long dictId = (Long) args.get("dictId");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("rows");
        String hql="from SystemDictEntry where id.dictId=:dictId order by sort asc";

        Query query = session.createQuery(hql);
        query.setParameter("dictId",dictId);
        Integer totalCount =query.list().size();
        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);
        infoMap.put("SystemDictEntry",query.list());
        infoMap.put("totalCount",totalCount);
        return infoMap;
    }


    public SystemDictEntry saveSystemDictEntry(SystemDictEntry systemDictEntry) {
        return systemDictEntryRepository.save(systemDictEntry);
    }


    public boolean containEntry(String code) {
        SystemDictEntry systemDictEntry =  systemDictEntryRepository.getByCode(code);
        return systemDictEntry!=null;
    }

    public void createDictEntry(SystemDictEntry systemDictEntry) {
        systemDictEntryRepository.save(systemDictEntry);
    }

    public void deleteDictEntry(long dictId,String code) {
        systemDictEntryRepository.deleteByDictIdAndCode(dictId, code);
    }


    public List<SystemDictEntry> getEntryList(Long dictId) {
        Session session  = entityManager.unwrap(org.hibernate.Session.class);

        String hql="from SystemDictEntry where dictId = :dictId order by sort asc";

        Query query = session.createQuery(hql);
        return query.list();
    }
}
