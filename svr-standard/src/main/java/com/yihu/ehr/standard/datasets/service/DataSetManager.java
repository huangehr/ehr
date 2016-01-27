package com.yihu.ehr.standard.datasets.service;


import com.yihu.ehr.standard.cdaversion.service.CDAVersion;
import com.yihu.ehr.standard.cdaversion.service.CDAVersionManager;
import com.yihu.ehr.standard.commons.BaseManager;
import com.yihu.ehr.standard.dict.service.DictManager;
import com.yihu.ehr.standard.standardsource.service.StandardSourceManager;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * 数据集管理器.
 *
 * @author Sand
 * @version 1.0
 * @created 30-6月-2015 16:19:05
 */
@Transactional
@Service
public class DataSetManager extends BaseManager {

    @Autowired
    private CDAVersionManager cdaVersionManager;
    @Autowired
    private StandardSourceManager standardSourceManager;
    @Autowired
    private DictManager dictManager;
    @Autowired
    MetaDataManager metaDataManager;

    public DataSet createDataSet(CDAVersion version) {
        DataSet dataSet = new DataSet();
        dataSet.setInnerVersion(version);
        return dataSet;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public DataSet getDataSet(long dataSetId, CDAVersion innerVersion) {
        Session session = currentSession();
        String dataSetTable = innerVersion.getDataSetTableName();

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary from " + dataSetTable + " where id = :id");
        query.setLong("id", dataSetId);

        Object[] record = (Object[]) query.uniqueResult();

        DataSet dataSet = new DataSet();
        if (record != null) {
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersion(innerVersion);
        }
        return dataSet;
    }

    /**
     * @param dataSet
     */
    public DataSetModel getDataSet(DataSet dataSet) {
        DataSetModel dataSetModel = new DataSetModel();
        dataSetModel.setCode(dataSet.getCode());
        dataSetModel.setName(dataSet.getName());
        dataSetModel.setRefStandard(dataSet.getReference());
        dataSetModel.setSummary(dataSet.getSummary());
        return dataSetModel;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public DataSet[] getDataSetByIds(List<String> listIds, String versionid) {
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(versionid);

        String strIds = String.join(",", listIds);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable + " where id in(" + strIds + ")");

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(versionid);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DataSet getDataSet(String dataSetCode, String version) {
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(version);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary from " + dataSetTable + " where code = :code");
        query.setString("code", dataSetCode);

        Object[] record = (Object[]) query.uniqueResult();

        DataSet dataSet = null;
        if (record != null) {
            dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(version);
        }
        return dataSet;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    public DataSet[] getDataSetList(int from, int count, CDAVersion innerVersion) {
        return getDataSetList(from, count, innerVersion.getVersion());
    }

    public DataSet[] getDataSetList(int from, int count, String innerVersion) {
        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(innerVersion);

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable);
        if (count > 0) {
            query.setFirstResult(from);
            query.setMaxResults(count);
        }
        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setPublisher((Integer) record[3]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public DataSet[] getAllDataSet(CDAVersion innerVersion) {
        Session session = currentSession();
        String dataSetTable = innerVersion.getDataSetTableName();

        Query query = session.createSQLQuery("select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                "from " + dataSetTable);

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersion(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }

    public DataSet[] getDataSets(String[] dataSetCodes, String innerVersion) {

        if (dataSetCodes == null || dataSetCodes.length == 0) {
            return null;
        }

        String dataSetJoin = "(" + String.join(",", dataSetCodes) + ")";

        Session session = currentSession();
        String dataSetTable = CDAVersion.getDataSetTableName(innerVersion);

        Query query = session.createSQLQuery(" select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                " from " + dataSetTable +
                " where code in " + dataSetJoin);

        List<Object> records = query.list();
        DataSet[] dataSets = new DataSet[records.size()];
        for (int i = 0; i < records.size(); ++i) {
            Object[] record = (Object[]) records.get(i);
            DataSet dataSet = new DataSet();
            dataSet.setId((Integer) record[0]);
            dataSet.setCode((String) record[1]);
            dataSet.setName((String) record[2]);
            dataSet.setReference(record[4].toString());
            dataSet.setStdVersion((String) record[5]);
            dataSet.setLang((Integer) record[6]);
            dataSet.setCatalog((Integer) record[7]);
            dataSet.setHashCode((Integer) record[8]);
            dataSet.setDocumentId(record[9] == null ? 0 : ((Integer) record[9]).intValue());
            dataSet.setSummary((String) record[10]);
            dataSet.setInnerVersionId(innerVersion);

            dataSets[i] = dataSet;
        }

        return dataSets;
    }


    public boolean saveDataSet(DataSet xdataSet) {

        DataSet dataSet = (DataSet) xdataSet;
        Session session = currentSession();
        String sql;
        Query query;
        long id = dataSet.getId();
        try {
            String dataSetTableName = cdaVersionManager.getVersion(dataSet.getInnerVersionId()).getDataSetTableName();
            if (id == 0) {
                sql = "select max(id) from " + dataSetTableName;
                query = session.createSQLQuery(sql);
                Object object = query.uniqueResult();

                id = object == null ? 1 : Long.parseLong(object.toString()) + 1;
                dataSet.setId(id);

                sql = "insert into " + dataSetTableName +
                        "(id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary) " +
                        "values(:id, :code, :name, :publisher, :ref_standard, :std_version, :lang, :catalog, :hash, :document_id, :summary)";
            } else {
                sql = "update " + dataSetTableName +
                        " set " +
                        "code = :code, " +
                        "name = :name, " +
                        "publisher = :publisher, " +
                        "ref_standard = :ref_standard, " +
                        "std_version = :std_version, " +
                        "lang = :lang, " +
                        "catalog = :catalog, " +
                        "hash = :hash, " +
                        "document_id = :document_id, " +
                        "summary = :summary " +
                        "where id = :id";
            }
            String d = dataSet.getReference();
            query = session.createSQLQuery(sql);
            query.setLong("id", dataSet.getId());
            query.setString("code", dataSet.getCode());
            query.setString("name", dataSet.getName());
            query.setInteger("publisher", dataSet.getPublisher());
            query.setString("ref_standard", dataSet.getReference());
            query.setString("std_version", dataSet.getStdVersion());
            query.setInteger("lang", dataSet.getLang());
            query.setInteger("catalog", dataSet.getCatalog());
            query.setInteger("hash", dataSet.getHashCode());
            query.setLong("document_id", dataSet.getDocumentId());
            query.setString("summary", dataSet.getSummary());
            query.executeUpdate();
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public int deleteDataSet(long dataSetId, String version) {
        int iResult = 0;
        try {
            if (version == null || version.length() == 0) throw new IllegalArgumentException("无效版本");

            Session session = currentSession();
            String sql = "delete from " + CDAVersion.getDataSetTableName(version) + " where id = :id";
            Query query = session.createSQLQuery(sql);
            query.setLong("id", dataSetId);
            iResult = query.executeUpdate();
        } catch (Exception ex) {
            iResult = -1;
        }
        return iResult;
    }

    /**
     * 查询总条数的方法
     *
     * @param cdaVersion
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public Integer searchDataSetInt(CDAVersion cdaVersion) {

        String version = cdaVersion.getVersion();
        String dataSetTable = CDAVersion.getDataSetTableName(version);

        Session session = currentSession();

        Query query = session.createSQLQuery(" Select count(*) from " + dataSetTable);

        BigInteger bigInteger = (BigInteger) query.list().get(0);
        String num = bigInteger.toString();
        Integer totalCount = Integer.parseInt(num);
        return totalCount;
    }


    /**
     * 初始化和条件查询的方法
     *
     * @param codename
     * @param from
     * @param count
     * @param version
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<DataSet> searchDataSets(String codename, int from, int count, String version) {

        //参数获取处理

        String dataSetTable = CDAVersion.getDataSetTableName(version);
        Session session = currentSession();
        String sql = " select id, code, name, publisher, ref_standard, std_version, lang, catalog, hash, document_id, summary " +
                " from " + dataSetTable + " where 1=1 ";
        if (codename != null && !codename.equals("")) {
            sql += " and (code LIKE :codename or name LIKE :codename)";
        }

        Query query = session.createSQLQuery(sql);
        if (codename != null && !codename.equals("")) {
            query.setString("codename", "%" + codename + "%");
        }

        if (count != 0) {
            query.setMaxResults(count);
            query.setFirstResult((from - 1) * count);
        }
        return query.list();
    }


    public List<DataSetModel> searchDataSetList(String codename, int from, int count, CDAVersion innerVersion) {

        //参数获取处理
        List DataSetList = searchDataSets(codename, from, count, innerVersion.getVersion());
        List<DataSetModel> dataSetModels = new ArrayList<>();

        for (int i = 0; i < DataSetList.size(); i++) {

            Object[] record = (Object[]) DataSetList.get(i);
            DataSetModel dataSetModel = new DataSetModel();
            dataSetModel.setId((Integer) record[0]);
            dataSetModel.setCode((String) record[1]);
            dataSetModel.setName((String) record[2]);
/*            dataSetModel.setPublisher((int)record[3]);
            dataSetModel.setRefStandard((String)record[4]);
            dataSetModel.setStdVersion((String)record[5]);
            dataSetModel.setLang((int)record[6]);
            dataSetModel.setCatalog((int)record[7]);
            dataSetModel.setHash((int)record[8]);
            dataSetModel.setDocumentId((int) record[9]);
            dataSetModel.setSummary((String)record[10]);*/

            dataSetModels.add(dataSetModel);
        }

        return dataSetModels;
    }


    /********************************************/
    /************* 以下为Excel导入方法 ***********/
    /**********************************************/
    //TODO 后面做

    //从excel导入数据集、数据元
//    public void importFromExcel(String filePath, CDAVersion cdaVersion){
//        try {
//            InputStream is = new FileInputStream(filePath);
//            Workbook rwb = Workbook.getWorkbook(is);
//            Sheet[] sheets = rwb.getSheets();
//            for (Sheet sheet : sheets) {
//                XDataSet dataSet = createDataSet(cdaVersion);
//                //获取数据集信息
//                String dataSetNname = sheet.getCell(1, 0).getContents();//名称
//                String dataSetCode = sheet.getCell(1, 1).getContents();//标识
//                String reference = sheet.getCell(1, 2).getContents();//参考
//                //todo：test--测试时备注做区别，方便删除测试，summary变量区别
//                String summary = sheet.getCell(1, 3).getContents();//备注
//                //String summary="测试excel导入";
//                //插入数据集信息
//                //todo：test--测试时code区别，否则测试不成功，因为code唯一
//                //dataSet.setCode(dataSetCode+"excel");//code唯一
//                dataSet.setCode(dataSetCode);//code唯一
//                dataSet.setName(dataSetNname);
//                dataSet.setPublisher(0);
//                dataSet.setReference("0");//标准来源
//                if (!StringUtil.isEmpty(reference)) {
//                    XStandardSource[] standardSources = standardSourceManager.getSourceByKey(reference);
//                    if (standardSources != null && standardSources.length > 0) {
//                        dataSet.setReference(standardSources[0].getId());//标准来源
//                    }
//                }
//                dataSet.setStdVersion(cdaVersion.getVersionName());
//                dataSet.setCatalog(0);
//                dataSet.setSummary(summary);
//                saveDataSet(dataSet);//保存数据集信息
//                //获取数据元信息
//                int rows = sheet.getRows();
//                for (int j = 0; j < rows - 5; j++) {
//                    MetaData metaData = new MetaData();
//                    int row = j + 5;
//                    String innerCode = sheet.getCell(1, row).getContents();//内部标识
//                    String code = sheet.getCell(2, row).getContents();//数据元编码
//                    String name = sheet.getCell(3, row).getContents();//数据元名称
//                    //todo：test--测试时备注做区别，方便删除测试，definition变量区别
//                    String definition = sheet.getCell(4, row).getContents();//数据元定义
//                    //String definition="测试excel导入";
//                    String type = sheet.getCell(5, row).getContents();//数据类型
//                    String format = sheet.getCell(6, row).getContents();//表示形式
//                    String dictCode = sheet.getCell(7, row).getContents();//术语范围值
//                    String columnName = sheet.getCell(8, row).getContents();//列名
//                    String columnType = sheet.getCell(9, row).getContents();//列类型
//                    String columnLength = sheet.getCell(10, row).getContents();//列长度
//                    String primaryKey = sheet.getCell(11, row).getContents();//主键
//                    String nullable = sheet.getCell(12, row).getContents();//可为空
//
//                    //插入数据元信息
//                    metaData.setId(0);//为0内部自增
//                    metaData.setDataSet(dataSet);
//                    metaData.setCode(code);
//                    metaData.setName(name);
//                    metaData.setInnerCode(innerCode);
//                    metaData.setType(type);
//                    if (!StringUtil.isEmpty(dictCode)) {
//                        XDict[] dicts = dictManager.getDictListForInter(0, 0, cdaVersion, dictCode);
//                        if (dicts != null && dicts.length > 0) {
//                            metaData.setDictId(dicts[0].getId()); //字典id
//                        }
//                    }
//                    metaData.setFormat(format);
//                    metaData.setDefinition(definition);
//                    metaData.setColumnName(columnName);
//                    metaData.setColumnLength(columnLength);
//                    metaData.setColumnType(columnType);
//                    metaData.setPrimaryKey(primaryKey.equals("1"));
//                    metaData.setNullable(nullable.equals("1"));
//                    metaDataManager.saveMetaData(dataSet, metaData);//保存数据元
//                }
//
//            }
//
//            //关闭
//            rwb.close();
//            is.close();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    //数据集、数据元导出到excel
//    public void exportToExcel(String filePath,XDataSet[] dataSets){
//        XStandardSourceManager standardSourceManager = ServiceFactory.getService(Services.StandardSourceManager);
//        XMetaDataManager metaDataManager = ServiceFactory.getService(Services.MetaDataManager);
//        try {
//            File fileWrite = new File(filePath);
//            fileWrite.createNewFile();
//            OutputStream os = new FileOutputStream(fileWrite);
//
//            WritableWorkbook wwb = Workbook.createWorkbook(os);
//
//            for(int i=0;i<dataSets.length;i++){
//                XDataSet dataSet=dataSets[i];
//                //创建Excel工作表 指定名称和位置
//                WritableSheet ws = wwb.createSheet(dataSet.getName(),i);
//                addStaticCell(ws);//添加固定信息，题头等
//                //添加数据集信息
//                addCell(ws,1,0,dataSet.getName());//名称
//                addCell(ws,1,1,dataSet.getCode());//标识
//                String reference = dataSet.getReference();
//                if (!StringUtil.isEmpty(reference)){
//                    XStandardSource standardSource=standardSourceManager.getSourceBySingleId(reference);
//                    if (standardSource!=null){
//                        addCell(ws,1,2,standardSource.getName());//参考
//                    }
//                }
//                addCell(ws,1,3,dataSet.getSummary());//备注
//
//                //添加数据元信息
//                List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSet);
//                WritableCellFormat wc = new WritableCellFormat();
//                wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
//                for(int j=0;j<metaDataList.size();j++){
//                    MetaData metaData = (MetaData)metaDataList.get(j);
//                    int row=j+5;
//                    addCell(ws,0,row,j+1+"",wc);//序号
//                    addCell(ws,1,row,metaData.getInnerCode(),wc);//内部标识
//                    addCell(ws,2,row,metaData.getCode(),wc);//数据元编码
//                    addCell(ws,3,row,metaData.getName(),wc);//数据元名称
//                    addCell(ws,4,row,metaData.getDefinition(),wc);//数据元定义
//                    addCell(ws,5,row,metaData.getType(),wc);//数据类型
//                    addCell(ws,6,row,metaData.getFormat(),wc);//表示形式
//                    XDict dict = metaData.getDict();
//                    if (dict!=null){
//                        addCell(ws,7,row,dict.getCode(),wc);//术语范围值
//                    }else{
//                        addCell(ws,7,row,"",wc);//术语范围值
//                    }
//                    addCell(ws,8,row,metaData.getColumnName(),wc);//列名
//                    addCell(ws,9,row,metaData.getColumnType(),wc);//列类型
//                    addCell(ws,10,row,metaData.getColumnLength(),wc);//列长度
//                    addCell(ws,11,row,metaData.isPrimaryKey()?"1":"0",wc);//主键
//                    addCell(ws,12,row,metaData.isNullable()?"1":"0",wc);//可为空
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
//
//    //excel中添加固定内容
//    private void addStaticCell(WritableSheet ws){
//        try {
//            addCell(ws,0,0,"名称");
//            addCell(ws,0,1,"标识");
//            addCell(ws,0,2,"参考");
//            addCell(ws,0,3,"备注");
//            //--------------------
//            WritableFont wfc = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
//                    UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.WHITE);//字体：大小，加粗，颜色
//            WritableCellFormat wcfFC = new WritableCellFormat(wfc);
//            wcfFC.setBackground(jxl.format.Colour.LIGHT_BLUE);//北京色
//            addCell(ws,0,4,"序号",wcfFC);
//            addCell(ws,1,4,"内部标识",wcfFC);
//            addCell(ws,2,4,"数据元编码",wcfFC);
//            addCell(ws,3,4,"数据元名称",wcfFC);
//            addCell(ws,4,4,"数据元定义",wcfFC);
//            addCell(ws,5,4,"数据类型",wcfFC);
//            addCell(ws,6,4,"表示形式",wcfFC);
//            addCell(ws,7,4,"术语范围值",wcfFC);
//            addCell(ws,8,4,"列名",wcfFC);
//            addCell(ws,9,4,"列类型",wcfFC);
//            addCell(ws,10,4,"列长度",wcfFC);
//            addCell(ws,11,4,"主键",wcfFC);
//            addCell(ws,12,4,"可为空",wcfFC);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //添加单元格内容
//    private void addCell(WritableSheet ws,int column,int row,String data){
//        try {
//            Label label = new Label(column,row,data);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    //添加单元格内容带样式
//    private void addCell(WritableSheet ws,int column,int row,String data,CellFormat cellFormat){
//        try {
//            Label label = new Label(column,row,data,cellFormat);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}