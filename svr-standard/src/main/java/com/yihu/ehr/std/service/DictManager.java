package com.yihu.ehr.std.service;


import com.yihu.ehr.data.SQLGeneralDAO;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 14-7月-2015 15:44:21
 */
@Transactional
@Service
public class DictManager extends SQLGeneralDAO {

    public DictManager() {
    }

    @Autowired
    private StandardSourceManager xStandardSourceManager;

    public void finalize() throws Throwable {

    }

    /**
     * 创建一个空字典
     *
     * @param code
     * @param name
     */
    public Dict createEmptyDict(String code, String name) {
        return null;
    }


    public Dict createStageDict(Dict dict) {
        return null;
    }

    public Dict createDict(CDAVersion version) {
        Dict dict = new Dict();
        dict.setInnerVersion(version);

        return dict;
    }

    /**
     * 获取字典
     *
     * @param dictId
     */
    public Dict getDict(long dictId, CDAVersion innerVersion) {

        //根据字典ID获取字典详细信息
        Session session = currentSession();
        String strTableName = innerVersion.getDictTableName();

        Query query = session.createSQLQuery("select id, code, name,author,base_dict, create_date, description, std_version, source, hash from " + strTableName + " where id = :id");
        query.setLong("id", dictId);

        Object[] record = (Object[]) query.uniqueResult();
        if(record == null) return null;

        Dict dict = new Dict();
        dict.setId((Integer) record[0]);
        dict.setCode((String) record[1]);
        dict.setName((String) record[2]);
        dict.setAuthor((String) record[3]);
        dict.setBaseDictId(record[4]==null?0:(Integer) record[4]);
        dict.setCreateDate((Date) record[5]);
        dict.setDescription((String) record[6]);
        dict.setStdVersion(String.valueOf(record[7]));

        List<String> ids = new ArrayList<>();
        ids.add(record[8].toString());
        StandardSource[] xStandardSource = xStandardSourceManager.getSourceById(ids);
        if(xStandardSource.length == 0){
            dict.setSource(null);
        }
        else{
            dict.setSource(xStandardSource[0]);
        }

        dict.setHashCode((Integer) record[9]);
        dict.setInnerVersion(innerVersion);

        return dict;
    }

    /**
     * 获取字典列表
     *
     * @param from
     * @param to
     */
    public Dict[] getDictList(int from, int to, CDAVersion innerVersion) {
        //根据查询页数获取字典列表
        Session session = currentSession();
        String strTableName = innerVersion.getDictTableName();

        Query query = session.createSQLQuery("select  id, code, name,author,base_dict, create_date, description, std_version, source,hash " +
                "from " + strTableName);
        if (to > 0) {
            query.setFirstResult(from);
            query.setMaxResults(to);
        }
        List<String> ids = new ArrayList<>();
        List<Object> records = query.list();
        Dict[] dicts = new Dict[records.size()];
        for (int i = 0; i < records.size(); ++i) {

            Object[] record = (Object[]) records.get(i);
            Dict dict = new Dict();
            dict.setId((Integer) record[0]);
            dict.setCode((String) record[1]);
            dict.setName((String) record[2]);
            dict.setAuthor((String) record[3]);
            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);

            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dict.setInnerVersionId(innerVersion.getVersion());
            dicts[i] = dict;
        }

        return dicts;
    }

    public Dict[] getDictListForInter(int page, int pageSize, CDAVersion innerVersion,String searchNm){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,name        ");
        sb.append("        ,author      ");
        sb.append("        ,base_dict   ");
        sb.append("        ,create_date ");
        sb.append("        ,description ");
        sb.append("        ,std_version ");
        sb.append("        ,source      ");
        sb.append("        ,hash        ");
        sb.append("   from   " + strDictName);
        sb.append("  where 1=1 ");

        if (!(searchNm==null || searchNm.equals(""))) {

            sb.append("    and (code like '%" + searchNm + "%' or name like '%" + searchNm + "%')   ");
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        List<Object> records = query.list();

        Dict[] dicts = new Dict[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            Dict dict = new Dict();

            dict.setId((Integer) record[0]);
            dict.setCode((String) record[1]);
            dict.setName((String) record[2]);
            dict.setAuthor((String) record[3]);
            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);
            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dict.setInnerVersionId(innerVersion.getVersion());
            dicts[i] = dict;
        }

        return dicts;
    }

    public int getDictListInt(CDAVersion innerVersion,String searchNm){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select 1  from   " + strDictName );
        sb.append("  where 1=1 ");

        if (!(searchNm==null || searchNm.equals(""))) {

            sb.append("    and (code like '%" + searchNm + "%' or name like '%" + searchNm + "%')   ");
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);

        List<Object> records = query.list();

        return records.size();
    }

    public Dict[] getBaseDictList(CDAVersion innerVersion,String dictId){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,name        ");
        sb.append("        ,author      ");
        sb.append("        ,base_dict   ");
        sb.append("        ,create_date ");
        sb.append("        ,description ");
        sb.append("        ,std_version ");
        sb.append("        ,source      ");
        sb.append("        ,hash        ");
        sb.append("   from   " + strDictName );
        sb.append("  where 1=1 ");

        if (!(dictId==null || dictId.equals(""))) {

            sb.append("    and id <> " + dictId );
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);

        List<Object> records = query.list();

        Dict[] dicts = new Dict[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            Dict dict = new Dict();

            dict.setId((Integer) record[0]);
            dict.setCode((String) record[1]);
            dict.setName((String) record[2]);
            dict.setAuthor((String) record[3]);
            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);
            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dict.setInnerVersionId(innerVersion.getVersion());
            dicts[i] = dict;
        }

        return dicts;
    }

    /**
     * 删除字典.
     *
     * @param dict
     */
    public int removeDict(Dict dict) {
        Session session = currentSession();
        String sql = "delete from " + dict.getInnerVersion().getDictTableName() + " where id = " + dict.getId();
        session.createSQLQuery(sql).executeUpdate();
        String sql1 = "delete from " + dict.getInnerVersion().getDictEntryTableName() + " where dict_id = " + dict.getId();
        return session.createSQLQuery(sql1).executeUpdate();
    }

    /**
     * 保存字典.
     *
     * @param xDict
     */
    public int saveDict(Dict xDict) {

        Dict dict = (Dict) xDict;
        Session session = currentSession();
        long id = dict.getId();
        String strTableName = dict.getInnerVersion().getDictTableName();
        String sql;
        Query query;

        if (id == 0) {
            sql = "select max(id) from " + strTableName;
            query = session.createSQLQuery(sql);
            Object object = query.uniqueResult();

            id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
            dict.setId(id);

            sql = "insert into " + strTableName +
                    "( id, code, name,author,base_dict, create_date, description, Std_Version,source,hash) " +
                    "values(:id, :code, :name, :author, :baseDict, :createdate, :description, :stdVersion, :source,:Hash)";
        } else {
            sql = "update " + strTableName +
                    " set " +
                    "code = :code, " +
                    "name = :name, " +
                    "author = :author, " +
                    "base_dict = :baseDict, " +
                    "create_date = :createdate, " +
                    "description = :description, " +
                    "std_version = :stdVersion, " +
                    "source=:source, " +
                    "hash=:Hash " +
                    "where id = :id";
        }

        query = session.createSQLQuery(sql);
        query.setLong("id", dict.getId());
        query.setString("code", dict.getCode());
        query.setString("name", dict.getName());
        query.setString("author", dict.getAuthor());
        query.setLong("baseDict", dict.getBaseDictId());
        query.setDate("createdate", dict.getCreateDate());
        query.setString("description", dict.getDescription());
        query.setString("stdVersion", dict.getStdVersion());
        query.setString("source", dict.getSource().getId());
        query.setInteger("Hash", dict.getHashCode());
        return query.executeUpdate();
    }

    public DictEntry[] getDictEntries(Dict dict) {
        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = dict.getInnerVersion().getDictEntryTableName();

        Query query = session.createSQLQuery("select  id, code,value,dict_id,description from " + strTableName + " where  dict_id=:dictId");
        query.setLong("dictId", dict.getId());
        List<Object> records = query.list();

        DictEntry[] dictEntryList = new DictEntry[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            DictEntry dictEntry = new DictEntry();
            dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
            dictEntry.setCode((String) record[1]);
            dictEntry.setValue((String) record[2]);
            dictEntry.setDict(dict);
            dictEntry.setDictId(Long.parseLong(record[3].toString()));
            dictEntry.setDesc((String) record[4]);
            dictEntry.setDictCode(dict.getCode());
            dictEntryList[i] = dictEntry;
        }

        return dictEntryList;
    }

    public DictEntry[] getDictEntries(Dict dict, List<Integer> ids) {
        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = dict.getInnerVersion().getDictEntryTableName();

        String strIds = StringUtils.join(ids, ',');

        Query query = session.createSQLQuery("select id, code,value,dict_id,description from " + strTableName + " where Id in (" + strIds + ")");

        List<Object> records = query.list();

        DictEntry[] dictEntryList = new DictEntry[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            DictEntry dictEntry = new DictEntry();
            dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
            dictEntry.setCode((String) record[1]);
            dictEntry.setValue((String) record[2]);
            dictEntry.setDict(dict);
            dictEntry.setDesc((String) record[4]);

            dictEntryList[i] = dictEntry;
        }

        return dictEntryList;
    }

    public DictEntry getDictEntries(Dict dict, String strCode) {
        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = dict.getInnerVersion().getDictEntryTableName();

        Query query = session.createSQLQuery("select id, code,value,dict_id,description,hash from " + strTableName + "  where  dict_id=:dictId and code=:code");
        query.setLong("dictId", dict.getId());
        query.setString("code", strCode);


        Object[] record = (Object[]) query.uniqueResult();

        DictEntry dictEntry =null;
        if(record.length>0)
        {
            dictEntry=new DictEntry();
            dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
            dictEntry.setCode((String) record[1]);
            dictEntry.setValue((String) record[2]);
            dictEntry.setDict(dict);
            dictEntry.setDesc((String) record[4]);
            dictEntry.setHashCode((Integer)record[5]);
        }
        return dictEntry;
    }

    public DictEntry[] getDictEntries(Dict dict, String searchNm, Integer page, Integer rows) {

        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = dict.getInnerVersion().getDictEntryTableName();
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,value       ");
        sb.append("        ,dict_id     ");
        sb.append("        ,description ");
        sb.append("   from   " + strTableName );
        sb.append("  where 1=1 ");
        sb.append("    and dict_id = " + dict.getId());

        if (!(searchNm==null || searchNm.equals(""))) {

            sb.append("    and (code like '%" + searchNm + "%' or value like '%" + searchNm + "%')   ");
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);
        query.setMaxResults(rows);
        query.setFirstResult((page - 1) * rows);

        List<Object> records = query.list();

        DictEntry[] dictEntryList = new DictEntry[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);

            DictEntry dictEntry = new DictEntry();
            dictEntry.setId(Long.parseLong(String.valueOf(record[0])));
            dictEntry.setCode((String) record[1]);
            dictEntry.setValue((String) record[2]);
            dictEntry.setDict(dict);
            dictEntry.setDictId(Long.parseLong(record[3].toString()));
            dictEntry.setDesc((String) record[4]);

            dictEntryList[i] = dictEntry;
        }

        return dictEntryList;
    }

    public int getDictEntriesForInt(Dict dict, String searchNm){
        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = dict.getInnerVersion().getDictEntryTableName();
        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  1   from   " + strTableName );
        sb.append("  where 1=1 ");
        sb.append("    and dict_id = " + dict.getId());

        if (!(searchNm==null || searchNm.equals(""))) {

            sb.append("    and (code like '%" + searchNm + "%' or value like '%" + searchNm + "%')   ");
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);

        return query.list().size();
    }

    public int delDictEntryList(CDAVersion xcdaVersion, List<String> ids) {

        int result =0;

        try {
            Session session = currentSession();
            String strTableName = xcdaVersion.getDictEntryTableName();

            String strIds = StringUtils.join(ids, "','");

            String query = "delete from " + strTableName + " where Id in (" + strIds + ")";
            result = session.createSQLQuery(query).executeUpdate();
        }catch (Exception ex){
            result=-1;
        }
        return result;
    }


    public Dict[] getDictLists(CDAVersion innerVersion,String key){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,name        ");
        sb.append("        ,author      ");
        sb.append("        ,base_dict   ");
        sb.append("        ,create_date ");
        sb.append("        ,description ");
        sb.append("        ,std_version ");
        sb.append("        ,source      ");
        sb.append("        ,hash        ");
        sb.append("   from   " + strDictName );
        sb.append("   where 1=1   " );
        if(key!=null && key!="")
        {
            sb.append("   and (code like '%"+ key+"%' or name like '%"+key+"%') ");
        }

        sb.append("    order by code asc  ");

        String strSql = sb.toString();
        Query query = session.createSQLQuery(strSql);

        List<Object> records = query.list();

        Dict[] dicts = new Dict[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            Dict dict = new Dict();

            dict.setId((Integer) record[0]);
            dict.setCode((String) record[1]);
            dict.setName((String) record[2]);
            dict.setAuthor((String) record[3]);
            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);
            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dict.setInnerVersionId(innerVersion.getVersion());
            dicts[i] = dict;
        }

        return dicts;
    }

    public boolean isDictCodeExist(CDAVersion innerVersion,String code){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();
        sb.append(" select 1  from   " + strDictName );
        sb.append(" where code = '" + code + "'");
        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);

        List<Object> records = query.list();
        return records.size()>0?true:false;
    }


}
