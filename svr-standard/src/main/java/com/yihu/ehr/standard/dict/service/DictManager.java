package com.yihu.ehr.standard.dict.service;


import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.commons.BaseManager;
import com.yihu.ehr.standard.standardsource.service.StandardSource;
import com.yihu.ehr.standard.standardsource.service.StandardSourceManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Sand
 * @version 1.0
 * @created 14-7月-2015 15:44:21
 */
@Service
@Transactional
public class DictManager extends BaseManager {

    @Autowired
    private StandardSourceManager standardSourceManager;

    @Autowired
    private CDAVersionManager cdaVersionManager;



    public Dict createDict(CDAVersion version) {
        Dict dict = new Dict();
        dict.setInnerVersion(version);
        return dict;
    }

    /**
     * 根据字典ID获取字典详细信息
     *
     * @param dictId
     */
    public Dict getDict(long dictId, CDAVersion innerVersion) {
        Session session = currentSession();
        String strTableName = innerVersion.getDictTableName();
        Query query = session.createSQLQuery(
                "select id, code, name, author, " +
                        "base_dict as baseDictId, " +
                        "create_date as createDate, description, std_version as stdVersion, " +
                        "source as sourceId, hash as hashCode from " + strTableName + " where id = :id");
        query.setLong("id", dictId);
        query.setResultTransformer(Transformers.aliasToBean(Dict.class));
        List<Dict> ls = query.list();
        if(ls.size()==0)
            return null;
        Dict dict = ls.get(0);
        dict.setSource(standardSourceManager.getSourceBySingleId(dict.getSourceId()));
        dict.setInnerVersion(innerVersion);
        return dict;
    }

    /**
     * 查询字典列表
     * @param page
     * @param pageSize
     * @param innerVersion
     * @param searchNm
     * @return
     */
    public Dict[] getDictListForInter(int page, int pageSize, CDAVersion innerVersion,String searchNm){
        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,name        ");
        sb.append("        ,author      ");
        sb.append("        ,base_dict as baseDictId   ");
        sb.append("        ,create_date as createDate");
        sb.append("        ,description ");
        sb.append("        ,std_version as stdVersion");
        sb.append("        ,source  as sourceId    ");
        sb.append("        ,hash   as hashCode     ");
        sb.append("   from   " + strDictName);
        sb.append("  where 1=1 ");

        if (!(searchNm==null || searchNm.equals(""))) {
            sb.append("    and (code like :searchNm or name like :searchNm)   ");
        }
        sb.append("    order by code asc  ");

        String strSql = sb.toString();
        Query query = session.createSQLQuery(strSql);
        if (!(searchNm==null || searchNm.equals(""))) {
            query.setString("searchNm", "%"+searchNm+"%");
        }
        if (page>0){
            query.setMaxResults(pageSize);
            query.setFirstResult((page - 1) * pageSize);
        }
        query.setResultTransformer(Transformers.aliasToBean(Dict.class));
        return analyse(query.list(), innerVersion);
    }

    public int getDictListInt(CDAVersion innerVersion, String searchNm){
        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();
        StringBuilder sb = new StringBuilder();
        sb.append(" select count(*)  from   " + strDictName );
        if (!(searchNm==null || searchNm.equals(""))) {
            sb.append(" where code like :searchNm or name like :searchNm");
        }
        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);
        if (!(searchNm==null || searchNm.equals(""))) {
            query.setString("searchNm", "%"+searchNm+"%");
        }
        List<Object> records = query.list();
        return ((BigInteger)query.list().get(0)).intValue();
    }

    public Dict[] getBaseDictList(CDAVersion innerVersion, String dictId){

        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        //动态SQL文拼接
        StringBuilder sb = new StringBuilder();

        sb.append(" select  id          ");
        sb.append("        ,code        ");
        sb.append("        ,name        ");
        sb.append("        ,author      ");
        sb.append("        ,base_dict   as baseDictId");
        sb.append("        ,create_date as createDate");
        sb.append("        ,description ");
        sb.append("        ,std_version as stdVersion");
        sb.append("        ,source      as sourceId");
        sb.append("        ,hash        as hashCode");
        sb.append("   from   " + strDictName );

        if (!(dictId==null || dictId.equals(""))) {
            sb.append("    where id <> :dictId" );
        }
        sb.append("    order by code asc  ");
        String strSql = sb.toString();
        Query query = session.createSQLQuery(strSql);
        if (!(dictId==null || dictId.equals(""))) {
            query.setParameter("dictId", dictId);
        }
        query.setResultTransformer(Transformers.aliasToBean(Dict.class));
        return analyse(query.list(), innerVersion);
    }

    /**
     * 把查询结果转换为想要的格式
     * @param ls
     * @param innerVersion
     * @return
     */
    private Dict[]  analyse(List<Dict> ls, CDAVersion innerVersion){
        Dict[] dicts = new Dict[ls.size()];
        int i =0;
        for(Dict dict: ls){
            dict.setInnerVersion(innerVersion);
            dict.setSource(null);
            dicts[i] = dict;
            i++;
        }
        return dicts;
    }
    /**
     * 删除字典.
     *
     * @param dict
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public int removeDict(Dict dict) {
        CDAVersion innerVersion = cdaVersionManager.getVersion(dict.getInnerVersion());
        Session session = currentSession();
        //删除字典
        String sql = "delete from " + innerVersion.getDictTableName() + " where id = :id ";
        Query q = session.createSQLQuery(sql);
        q.setParameter("id", dict.getId());
        q.executeUpdate();
        //删除关联字典项
        sql = "delete from " + innerVersion.getDictEntryTableName() + " where dict_id = :id";
        q = session.createSQLQuery(sql);
        q.setParameter("id", dict.getId());
        return q.executeUpdate();
    }

    /**
     * 保存字典.
     *
     * @param dict
     */
    public int saveDict(Dict dict) {
        Session session = currentSession();
        long id = dict.getId();
        String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictTableName();
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
        StandardSource standardSource = dict.getSource();
        if (standardSource!=null){
            query.setString("source", standardSource.getId());
        }else{
            query.setParameter("source",null);
        }
        query.setInteger("Hash", dict.getHashCode());
        return query.executeUpdate();
    }




    /**
     * 判断字典代码是否已存在
     * @param innerVersion 版本
     * @param code 代码
     * @return
     */
    public boolean isDictCodeExist(CDAVersion innerVersion,String code){
        Session session = currentSession();
        String strDictName = innerVersion.getDictTableName();

        StringBuilder sb = new StringBuilder();
        sb.append(" select 1  from   " + strDictName );
        sb.append(" where code = :code ");
        String strSql = sb.toString();

        Query query = session.createSQLQuery(strSql);
        query.setString("code", code);
        List<Object> records = query.list();
        return records.size()>0;
    }

    /**
     * 设置字典属性
     * @param dict
     * @param id
     * @param code
     * @param name
     * @param userId
     * @param stdSource
     * @param baseDict
     * @param createDate
     * @param description
     * @param stdVersion
     * @param innerVersion
     * @return
     */
    public Dict setDictValues(
            Dict dict, Long id, String code, String name, String userId, StandardSource stdSource,
                Long baseDict, Date createDate, String description, String stdVersion, CDAVersion innerVersion){
        dict.setId(id);
        dict.setCode(code);
        dict.setName(name);
        dict.setAuthor(userId);
        dict.setSource(stdSource);
        dict.setBaseDictId(baseDict);
        dict.setCreateDate(createDate);
        dict.setDescription(description);
        dict.setStdVersion(stdVersion);
        dict.setInnerVersion(innerVersion);
        return dict;
    }


    /**
     * 模型转化
     * @param ls
     * @return
     */
    public List<DictForInterface> dictTransfer(Dict[] ls){
        List<DictForInterface> rs = new ArrayList<>();
        if(ls==null)
            return rs;
        DictForInterface info;
        for (Dict xDict : ls) {
            info = new DictForInterface();
            info.setId(String.valueOf(xDict.getId()));
            info.setCode(xDict.getCode());
            info.setName(xDict.getName());
            info.setAuthor(xDict.getAuthor());
            info.setBaseDictId(String.valueOf(xDict.getBaseDictId()));
            info.setCreateDate(String.valueOf(xDict.getCreateDate()));
            info.setDescription(xDict.getDescription());
            info.setStdVersion(xDict.getStdVersion());
            info.setHashCode(String.valueOf(xDict.getHashCode()));
            info.setInnerVersionId(xDict.getInnerVersion());
            if (xDict.getSource() == null) {
                info.setStdSource(null);
            } else {
                info.setStdSource(xDict.getSource().getId());
            }
            rs.add(info);
        }
        return rs;
    }




    /*************************************
        以下Excel导入导出
     ***************************/
    //从excel导入字典、字典项
//    public void importFromExcel(String filePath, CDAVersion cdaVersion){
//        DictEntryManager dictEntryManager = ServiceFactory.getService(Services.DictEntryManager);
//        try {
//            InputStream is = new FileInputStream(filePath);
//            Workbook rwb = Workbook.getWorkbook(is);
//            Sheet[] sheets = rwb.getSheets();
//            for (Sheet sheet : sheets) {
//                XDict dict = createDict(cdaVersion);
//                //获取字典信息
//                String dictCode = sheet.getCell(1, 0).getContents();//代码
//                String dictName = sheet.getCell(1, 1).getContents();//名称
//                //插入字典信息
//                dict.setCode(dictCode);//
//                dict.setName(dictName);
//                Date createTime = new Date();
//                dict.setId(0);//内部自增长
//                dict.setCreateDate(createTime);
//                //todo：test--测试时备注做区别，方便删除测试
//                dict.setDescription("测试excel导入");
//                dict.setStdVersion(cdaVersion.getVersion());
//                saveDict(dict);
//
//                //获取字典项信息
//                int rows = sheet.getRows();
//                for (int j = 0; j < rows; j++) {
//                    XDictEntry dictEntry = dictEntryManager.createDictEntry(dict);
//                    String code = sheet.getCell(3, j).getContents();//字典项编码
//                    String name = sheet.getCell(4, j).getContents();//字典项名称
//                    //插入字典项信息
//                    dictEntry.setId(0);//为0内部自增
//                    dictEntry.setCode(code);
//                    dictEntry.setValue(name);
//                    //todo：test--测试时备注做区别，方便删除测试
//                    dictEntry.setDesc("测试excel导入");
//                    dictEntryManager.saveEntry(dictEntry);//保存字典项
//                }
//            }
//            //关闭
//            rwb.close();
//            is.close();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }


    //字典、字典项导出到excel
//    public void exportToExcel(String filePath,XDict[] dicts){
//        try {
//            File fileWrite = new File(filePath);
//            fileWrite.createNewFile();
//            OutputStream os = new FileOutputStream(fileWrite);
//            WritableWorkbook wwb = Workbook.createWorkbook(os);
//
//            for(int i=0;i<dicts.length;i++){
//                XDict dict=dicts[i];
//                //创建Excel工作表 指定名称和位置
//                WritableSheet ws = wwb.createSheet(dict.getName(),i);
//                addStaticCell(ws);//添加固定信息，题头等
//                //添加字典信息
//                addCell(ws,1,0,dict.getCode());//代码
//                addCell(ws,1,1,dict.getName());//名称
//                //添加字典项信息
//                XDictEntry[] dictEntries = getDictEntries(dict);
//                WritableCellFormat wc = new WritableCellFormat();
//                wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
//                for(int j=0;j<dictEntries.length;j++){
//                    XDictEntry dictEntry = dictEntries[j];
//                    addCell(ws,3,j,dictEntry.getCode(),wc);//代码
//                    addCell(ws,4,j,dictEntry.getValue(),wc);//名称
//                }
//            }
//            //写入工作表
//            wwb.write();
//            wwb.close();
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //excel中添加固定内容
//    private void addStaticCell(WritableSheet ws){
//        try {
//            addCell(ws,0,0,"代码");
//            addCell(ws,0,1,"名称");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    //添加单元格内容
//    private void addCell(WritableSheet ws,int column,int row,String data){
//        try {
//            Label label = new Label(column,row,data);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    //添加单元格内容带样式
//    private void addCell(WritableSheet ws,int column,int row,String data,CellFormat cellFormat){
//        try {
//            Label label = new Label(column,row,data,cellFormat);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } +
//    }


    /******************************************************************************************************************/
    /************************ 以下暂没用到                                                      **********************/
    /****************************************************************************************************************/
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

    /**
     * 查询字典（编码与名称模糊查询）
     * @param innerVersion
     * @param key 查询值
     * @return
     */
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
            sb.append("   and (code like :key or name like :key) ");
        sb.append("    order by code asc  ");

        String strSql = sb.toString();
        Query query = session.createSQLQuery(strSql);
        if(key!=null && key!="")
            query.setParameter("key", key);

        List<Object> records = query.list();
        Dict[] dicts = new Dict[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            Dict dict = new Dict();

            dict.setId((Integer) record[0]);
            dict.setCode((String) record[1]);
            dict.setName((String) record[2]);
            dict.setAuthor((String) record[3]);
//            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);
            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dicts[i] = dict;
        }
        return dicts;
    }


    /**
     * 根据字典项IDs获取字典值域内容
     * @param dict
     * @param ids
     * @return
     */
    public DictEntry[] getDictEntries(Dict dict, List<Integer> ids) {
        //
        Session session = currentSession();
        String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();
        Query query = session.createSQLQuery("select id, code,value,dict_id,description from " + strTableName + " where Id in (:ids)");
        query.setParameterList("ids", ids);
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

    /**
     * 通过字典id和字典项code查询字典项
     * @param dict
     * @param strCode
     * @return
     */
    public DictEntry getDictEntries(Dict dict, String strCode) {
        //根据字典ID获取字典值域内容
        Session session = currentSession();
        String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();

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

    /**
     * 获取关联字典项
     * @param dict
     * @return
     */
    public DictEntry[] getDictEntries(Dict dict) {
        Session session = currentSession();
        String strTableName = cdaVersionManager.getVersion(dict.getInnerVersion()).getDictEntryTableName();

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
//            dict.setBaseDictId( record[4]==null?0: (Integer) record[4]);
            dict.setCreateDate((Date) record[5]);
            dict.setDescription((String) record[6]);
            dict.setStdVersion(String.valueOf(record[7]));
            dict.setSource(null);

            dict.setHashCode(record[9]==null?0:(Integer) record[9]);
            dict.setInnerVersion(innerVersion);
            dicts[i] = dict;
        }

        return dicts;
    }
}
