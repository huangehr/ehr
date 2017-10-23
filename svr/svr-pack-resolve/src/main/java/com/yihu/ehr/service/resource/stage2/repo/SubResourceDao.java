package com.yihu.ehr.service.resource.stage2.repo;

import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.profile.family.SubResourceFamily;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.SubRecord;
import com.yihu.ehr.service.resource.stage2.SubRecords;
import com.yihu.ehr.util.ResourceStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 档案资源子库。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class SubResourceDao {

    @Autowired
    private HBaseDao hbaseDao;

    public void saveOrUpdate(ResourceBucket resBucket) throws Exception {
        TableBundle bundle = new TableBundle();

        // delete legacy data if they are exist
        String legacyRowKeys[] = hbaseDao.findRowKeys(ResourceCore.SubTable, "^" + resBucket.getId());
        if (legacyRowKeys != null && legacyRowKeys.length > 0){
            bundle.addRows(legacyRowKeys);
            hbaseDao.delete(ResourceCore.SubTable, bundle);
        }

        bundle = new TableBundle();
        // now save the data to hbase
        SubRecords subRecords = resBucket.getSubRecords();
        for (SubRecord record : subRecords.getRecords()){
            bundle.addValues(
                    record.getRowkey(),
                    SubResourceFamily.Basic,
                    ResourceStorageUtil.getSubResCells(SubResourceFamily.Basic, record));

            bundle.addValues(
                    record.getRowkey(),
                    SubResourceFamily.Data,
                    ResourceStorageUtil.getSubResCells(SubResourceFamily.Data, record));
        }

        hbaseDao.save(ResourceCore.SubTable, bundle);
    }

    /**
     * 获取数据集。
     *
     * @param dataSetCode 欲加载的数据集代码
     * @param rowkeys     数据集rowkey列表
     * @return
     * @throws IOException
     *//*
    public Pair<String, PackageDataSet> findOne(String cdaVersion,
                                            String dataSetCode,
                                            ProfileType profileType,
                                            String[] rowkeys) throws IOException {
        PackageDataSet dataSet = profileType == ProfileType.Link ? new LinkPackageDataSet() : new PackageDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        TableBundle dataSetBundle = new TableBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addRows(rowkey);
        }

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    *//**
     * 获取部分数据集。
     *
     * @param version
     * @param dataSetCode
     * @param rowkeys
     * @param metaDataCodes
     * @return
     * @throws IOException
     *//*
    public Pair<String, PackageDataSet> findOne(String version,
                                            String dataSetCode,
                                            ProfileType profileType,
                                            Set<String> rowkeys,
                                            String[] metaDataCodes) throws IOException {
        List<String> metaDataCode = new ArrayList<>(metaDataCodes.length);
        for (int i = 0; i < metaDataCodes.length; ++i) {
            Long dictId = cacheReader.read(keySchema.metaDataDict(version, dataSetCode, metaDataCodes[i]));
            String type = cacheReader.read(keySchema.metaDataType(version, dataSetCode, metaDataCodes[i]));
            type = type == null ? "S1" : type;
            if (dictId == null || dictId == 0) {
                metaDataCode.add(QualifierTranslator.hBaseQualifier(metaDataCodes[i], type));
            } else if (dictId > 0) {
                String[] temp = QualifierTranslator.splitMetaData(metaDataCodes[i]);
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[0], type));
                metaDataCode.add(QualifierTranslator.hBaseQualifier(temp[1], type));
            }
        }

        TableBundle dataSetBundle = new TableBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addFamily(rowkey, SubResourceFamily.Basic);
            dataSetBundle.addColumns(rowkey, SubResourceFamily.MetaData, metaDataCode.toArray());
            dataSetBundle.addFamily(rowkey, SubResourceFamily.Extension);
        }

        PackageDataSet dataSet = profileType == ProfileType.Link ? new LinkPackageDataSet() : new PackageDataSet();
        dataSet.setCdaVersion(version);
        dataSet.setCode(dataSetCode);

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    *//**
     * 只获取数据集索引，不加载数据集内容，提高效率。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @return
     *//*
    public Pair<String, PackageDataSet> findIndices(String cdaVersion,
                                                String dataSetCode,
                                                String[] rowKeys) {
        PackageDataSet dataSet = new PackageDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        for (String rowKey : rowKeys) {
            dataSet.addRecord(rowKey, null);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    private void extractResultToDataSet(PackageDataSet dataSet, Object[] results) {
        for (Object item : results) {
            MetaDataRecord record = new MetaDataRecord();

            // 数据集内容
            ResultUtil result = new ResultUtil(item);
            List<Cell> cellList = result.listCells();

            for (Cell cell : cellList) {
                String qualifier = result.getCellQualifier(cell);
                if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    qualifier = qualifier.substring(0, qualifier.lastIndexOf('_'));
                    record.putMetaData(qualifier, result.getCellValue(cell));
                }

                if (qualifier.equals(dataSet.getCode()) && dataSet instanceof LinkPackageDataSet) {
                    ((LinkPackageDataSet) dataSet).setUrl(result.getCellValue(cell));
                }
            }

            dataSet.addRecord(result.getRowKey(), record);
        }
    }*/
}
