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
 * ���ݼ�������.
 *
 * @author Sand
 * @version 1.0
 * @created 30-6��-2015 16:19:05
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
            if (version == null || version.length() == 0) throw new IllegalArgumentException("��Ч�汾");

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
     * ��ѯ�������ķ���
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
     * ��ʼ����������ѯ�ķ���
     *
     * @param codename
     * @param from
     * @param count
     * @param version
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<DataSet> searchDataSets(String codename, int from, int count, String version) {

        //������ȡ����

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

        //������ȡ����
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
    /************* ����ΪExcel���뷽�� ***********/
    /**********************************************/
    //TODO ������

    //��excel�������ݼ�������Ԫ
//    public void importFromExcel(String filePath, CDAVersion cdaVersion){
//        try {
//            InputStream is = new FileInputStream(filePath);
//            Workbook rwb = Workbook.getWorkbook(is);
//            Sheet[] sheets = rwb.getSheets();
//            for (Sheet sheet : sheets) {
//                XDataSet dataSet = createDataSet(cdaVersion);
//                //��ȡ���ݼ���Ϣ
//                String dataSetNname = sheet.getCell(1, 0).getContents();//����
//                String dataSetCode = sheet.getCell(1, 1).getContents();//��ʶ
//                String reference = sheet.getCell(1, 2).getContents();//�ο�
//                //todo��test--����ʱ��ע�����𣬷���ɾ�����ԣ�summary��������
//                String summary = sheet.getCell(1, 3).getContents();//��ע
//                //String summary="����excel����";
//                //�������ݼ���Ϣ
//                //todo��test--����ʱcode���𣬷�����Բ��ɹ�����ΪcodeΨһ
//                //dataSet.setCode(dataSetCode+"excel");//codeΨһ
//                dataSet.setCode(dataSetCode);//codeΨһ
//                dataSet.setName(dataSetNname);
//                dataSet.setPublisher(0);
//                dataSet.setReference("0");//��׼��Դ
//                if (!StringUtil.isEmpty(reference)) {
//                    XStandardSource[] standardSources = standardSourceManager.getSourceByKey(reference);
//                    if (standardSources != null && standardSources.length > 0) {
//                        dataSet.setReference(standardSources[0].getId());//��׼��Դ
//                    }
//                }
//                dataSet.setStdVersion(cdaVersion.getVersionName());
//                dataSet.setCatalog(0);
//                dataSet.setSummary(summary);
//                saveDataSet(dataSet);//�������ݼ���Ϣ
//                //��ȡ����Ԫ��Ϣ
//                int rows = sheet.getRows();
//                for (int j = 0; j < rows - 5; j++) {
//                    MetaData metaData = new MetaData();
//                    int row = j + 5;
//                    String innerCode = sheet.getCell(1, row).getContents();//�ڲ���ʶ
//                    String code = sheet.getCell(2, row).getContents();//����Ԫ����
//                    String name = sheet.getCell(3, row).getContents();//����Ԫ����
//                    //todo��test--����ʱ��ע�����𣬷���ɾ�����ԣ�definition��������
//                    String definition = sheet.getCell(4, row).getContents();//����Ԫ����
//                    //String definition="����excel����";
//                    String type = sheet.getCell(5, row).getContents();//��������
//                    String format = sheet.getCell(6, row).getContents();//��ʾ��ʽ
//                    String dictCode = sheet.getCell(7, row).getContents();//���ﷶΧֵ
//                    String columnName = sheet.getCell(8, row).getContents();//����
//                    String columnType = sheet.getCell(9, row).getContents();//������
//                    String columnLength = sheet.getCell(10, row).getContents();//�г���
//                    String primaryKey = sheet.getCell(11, row).getContents();//����
//                    String nullable = sheet.getCell(12, row).getContents();//��Ϊ��
//
//                    //��������Ԫ��Ϣ
//                    metaData.setId(0);//Ϊ0�ڲ�����
//                    metaData.setDataSet(dataSet);
//                    metaData.setCode(code);
//                    metaData.setName(name);
//                    metaData.setInnerCode(innerCode);
//                    metaData.setType(type);
//                    if (!StringUtil.isEmpty(dictCode)) {
//                        XDict[] dicts = dictManager.getDictListForInter(0, 0, cdaVersion, dictCode);
//                        if (dicts != null && dicts.length > 0) {
//                            metaData.setDictId(dicts[0].getId()); //�ֵ�id
//                        }
//                    }
//                    metaData.setFormat(format);
//                    metaData.setDefinition(definition);
//                    metaData.setColumnName(columnName);
//                    metaData.setColumnLength(columnLength);
//                    metaData.setColumnType(columnType);
//                    metaData.setPrimaryKey(primaryKey.equals("1"));
//                    metaData.setNullable(nullable.equals("1"));
//                    metaDataManager.saveMetaData(dataSet, metaData);//��������Ԫ
//                }
//
//            }
//
//            //�ر�
//            rwb.close();
//            is.close();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }
//
//    //���ݼ�������Ԫ������excel
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
//                //����Excel������ ָ�����ƺ�λ��
//                WritableSheet ws = wwb.createSheet(dataSet.getName(),i);
//                addStaticCell(ws);//��ӹ̶���Ϣ����ͷ��
//                //������ݼ���Ϣ
//                addCell(ws,1,0,dataSet.getName());//����
//                addCell(ws,1,1,dataSet.getCode());//��ʶ
//                String reference = dataSet.getReference();
//                if (!StringUtil.isEmpty(reference)){
//                    XStandardSource standardSource=standardSourceManager.getSourceBySingleId(reference);
//                    if (standardSource!=null){
//                        addCell(ws,1,2,standardSource.getName());//�ο�
//                    }
//                }
//                addCell(ws,1,3,dataSet.getSummary());//��ע
//
//                //�������Ԫ��Ϣ
//                List<XMetaData> metaDataList = metaDataManager.getMetaDataList(dataSet);
//                WritableCellFormat wc = new WritableCellFormat();
//                wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//�߿�
//                for(int j=0;j<metaDataList.size();j++){
//                    MetaData metaData = (MetaData)metaDataList.get(j);
//                    int row=j+5;
//                    addCell(ws,0,row,j+1+"",wc);//���
//                    addCell(ws,1,row,metaData.getInnerCode(),wc);//�ڲ���ʶ
//                    addCell(ws,2,row,metaData.getCode(),wc);//����Ԫ����
//                    addCell(ws,3,row,metaData.getName(),wc);//����Ԫ����
//                    addCell(ws,4,row,metaData.getDefinition(),wc);//����Ԫ����
//                    addCell(ws,5,row,metaData.getType(),wc);//��������
//                    addCell(ws,6,row,metaData.getFormat(),wc);//��ʾ��ʽ
//                    XDict dict = metaData.getDict();
//                    if (dict!=null){
//                        addCell(ws,7,row,dict.getCode(),wc);//���ﷶΧֵ
//                    }else{
//                        addCell(ws,7,row,"",wc);//���ﷶΧֵ
//                    }
//                    addCell(ws,8,row,metaData.getColumnName(),wc);//����
//                    addCell(ws,9,row,metaData.getColumnType(),wc);//������
//                    addCell(ws,10,row,metaData.getColumnLength(),wc);//�г���
//                    addCell(ws,11,row,metaData.isPrimaryKey()?"1":"0",wc);//����
//                    addCell(ws,12,row,metaData.isNullable()?"1":"0",wc);//��Ϊ��
//                }
//            }
//            //д�빤����
//            wwb.write();
//            wwb.close();
//            os.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //excel����ӹ̶�����
//    private void addStaticCell(WritableSheet ws){
//        try {
//            addCell(ws,0,0,"����");
//            addCell(ws,0,1,"��ʶ");
//            addCell(ws,0,2,"�ο�");
//            addCell(ws,0,3,"��ע");
//            //--------------------
//            WritableFont wfc = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,
//                    UnderlineStyle.NO_UNDERLINE,jxl.format.Colour.WHITE);//���壺��С���Ӵ֣���ɫ
//            WritableCellFormat wcfFC = new WritableCellFormat(wfc);
//            wcfFC.setBackground(jxl.format.Colour.LIGHT_BLUE);//����ɫ
//            addCell(ws,0,4,"���",wcfFC);
//            addCell(ws,1,4,"�ڲ���ʶ",wcfFC);
//            addCell(ws,2,4,"����Ԫ����",wcfFC);
//            addCell(ws,3,4,"����Ԫ����",wcfFC);
//            addCell(ws,4,4,"����Ԫ����",wcfFC);
//            addCell(ws,5,4,"��������",wcfFC);
//            addCell(ws,6,4,"��ʾ��ʽ",wcfFC);
//            addCell(ws,7,4,"���ﷶΧֵ",wcfFC);
//            addCell(ws,8,4,"����",wcfFC);
//            addCell(ws,9,4,"������",wcfFC);
//            addCell(ws,10,4,"�г���",wcfFC);
//            addCell(ws,11,4,"����",wcfFC);
//            addCell(ws,12,4,"��Ϊ��",wcfFC);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    //��ӵ�Ԫ������
//    private void addCell(WritableSheet ws,int column,int row,String data){
//        try {
//            Label label = new Label(column,row,data);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    //��ӵ�Ԫ�����ݴ���ʽ
//    private void addCell(WritableSheet ws,int column,int row,String data,CellFormat cellFormat){
//        try {
//            Label label = new Label(column,row,data,cellFormat);
//            ws.addCell(label);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}