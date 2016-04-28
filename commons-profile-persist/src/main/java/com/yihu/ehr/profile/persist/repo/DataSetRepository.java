package com.yihu.ehr.profile.persist.repo;

import com.yihu.ehr.cache.CacheReader;
import com.yihu.ehr.profile.core.profile.ProfileType;
import com.yihu.ehr.data.hbase.CellBundle;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.profile.core.profile.DataRecord;
import com.yihu.ehr.profile.core.profile.LinkDataSet;
import com.yihu.ehr.profile.core.profile.StdDataSet;
import com.yihu.ehr.profile.core.profile.DataSetFamily;
import com.yihu.ehr.profile.core.util.QualifierTranslator;
import com.yihu.ehr.schema.StdKeySchema;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.hadoop.hbase.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 数据集库。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.15 16:50
 */
@Service
public class DataSetRepository {
    @Autowired
    HBaseDao hbaseDao;

    @Autowired
    CacheReader cacheReader;

    @Autowired
    StdKeySchema keySchema;

    /**
     * 获取数据集。
     *
     * @param dataSetCode 欲加载的数据集代码
     * @param rowkeys     数据集rowkey列表
     * @return
     * @throws IOException
     */
    public Pair<String, StdDataSet> findOne(String cdaVersion,
                                            String dataSetCode,
                                            ProfileType profileType,
                                            String[] rowkeys) throws IOException {
        StdDataSet dataSet = profileType == ProfileType.Link ? new LinkDataSet() : new StdDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        CellBundle dataSetBundle = new CellBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addRow(rowkey);
            /*dataSetBundle.addFamily(rowkey, DataSetFamily.Basic);
            dataSetBundle.addFamily(rowkey, DataSetFamily.MetaData);
            dataSetBundle.addFamily(rowkey, DataSetFamily.Extension);*/
        }

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 获取部分数据集。
     *
     * @param version
     * @param dataSetCode
     * @param rowkeys
     * @param metaDataCodes
     * @return
     * @throws IOException
     */
    public Pair<String, StdDataSet> findOne(String version,
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

        CellBundle dataSetBundle = new CellBundle();
        for (String rowkey : rowkeys) {
            dataSetBundle.addFamily(rowkey, DataSetFamily.Basic);
            dataSetBundle.addColumns(rowkey, DataSetFamily.MetaData, metaDataCode.toArray());
            dataSetBundle.addFamily(rowkey, DataSetFamily.Extension);
        }

        StdDataSet dataSet = profileType == ProfileType.Link ? new LinkDataSet() : new StdDataSet();
        dataSet.setCdaVersion(version);
        dataSet.setCode(dataSetCode);

        Object[] results = hbaseDao.get(dataSetCode, dataSetBundle);
        extractResultToDataSet(dataSet, results);

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    /**
     * 只获取数据集索引，不加载数据集内容，提高效率。
     *
     * @param cdaVersion
     * @param dataSetCode
     * @param rowKeys
     * @return
     */
    public Pair<String, StdDataSet> findIndices(String cdaVersion,
                                                String dataSetCode,
                                                String[] rowKeys) {
        StdDataSet dataSet = new StdDataSet();
        dataSet.setCdaVersion(cdaVersion);
        dataSet.setCode(dataSetCode);

        for (String rowKey : rowKeys) {
            dataSet.addRecord(rowKey, null);
        }

        return new ImmutablePair<>(dataSetCode, dataSet);
    }

    private void extractResultToDataSet(StdDataSet dataSet, Object[] results) {
        for (Object item : results) {
            DataRecord record = new DataRecord();

            // 数据集内容
            ResultUtil result = new ResultUtil(item);
            List<Cell> cellList = result.listCells();

            for (Cell cell : cellList) {
                String qualifier = result.getCellQualifier(cell);
                if (qualifier.startsWith("HDS") || qualifier.startsWith("JDS")) {
                    qualifier = qualifier.substring(0, qualifier.lastIndexOf('_'));
                    record.putMetaData(qualifier, result.getCellValue(cell));
                }

                if (qualifier.equals(dataSet.getCode()) && dataSet instanceof LinkDataSet) {
                    ((LinkDataSet) dataSet).setUrl(result.getCellValue(cell));
                }
            }

            dataSet.addRecord(result.getRowKey(), record);
        }
    }
}
