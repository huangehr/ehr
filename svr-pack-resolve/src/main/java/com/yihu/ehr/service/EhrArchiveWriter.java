package com.yihu.ehr.service;

import com.yihu.ehr.data.XHBaseClient;
import com.yihu.ehr.util.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 11:56
 */
public class EhrArchiveWriter {
    @Value("${max-batch-size}")
    private int MaxBatchInsertSize;

    @Autowired
    private XHBaseClient hbaseClient;

    public EhrArchiveWriter() {
    }

    public void writeArchive(Profile healthArchive) throws IOException {
        // 先存档案
        hbaseClient.insertRecord(ProfileTableOptions.ArchiveTable,
                healthArchive.getId(),
                ProfileTableOptions.FamilyBasic,
                ProfileTableOptions.getColumns(ProfileTableOptions.Table.ArchiveTable, ProfileTableOptions.TableFamily.Basic),
                new String[]{
                        healthArchive.getCardId(),
                        healthArchive.getOrgCode(),
                        healthArchive.getPatientId(),
                        healthArchive.getEventNo(),
                        DateFormatter.utcDateTimeFormat(healthArchive.getEventDate()),
                        healthArchive.getSummary(),
                        healthArchive.getDemographicId() == null ? "" : healthArchive.getDemographicId(),
                        DateFormatter.utcDateTimeFormat(healthArchive.getCreateDate()),
                        healthArchive.getDataSetsAsString(),
                        healthArchive.getCdaVersion()
                });

        // 数据集
        Set<String> tableSet = healthArchive.getDataSetTables();
        for (String tableName : tableSet) {
            ProfileDataSet dataSet = healthArchive.getDataSet(tableName);

            int rowIndex = 1;
            int totalRowCount = dataSet.getRecordKeys().size();
            for (String key : dataSet.getRecordKeys()) {
                if (rowIndex == 1 || rowIndex == MaxBatchInsertSize) {
                    hbaseClient.beginBatchInsert(tableName, false);
                }

                Map<String, String> record = dataSet.getRecord(key);
                String[][] hbDataArray = ProfileTableOptions.dataSetRecordToHBaseColumn(record);

                // 病人ID, 标准版本号, 采集机构等信息
                hbaseClient.batchInsert(key,
                        ProfileTableOptions.FamilyBasic,
                        ProfileTableOptions.getColumns(ProfileTableOptions.Table.DataSetTable, ProfileTableOptions.TableFamily.Basic),
                        new String[]{
                                healthArchive.getId(),
                                healthArchive.getOrgCode(),
                                dataSet.getPatientId(),
                                dataSet.getEventNo(),
                                dataSet.getCdaVersion(),
                                DateFormatter.utcDateTimeFormat(new Date())
                        });

                // 标准数据元
                hbaseClient.batchInsert(key,
                        ProfileTableOptions.FamilyMetaData,
                        hbDataArray[0],
                        hbDataArray[1]);

                if (rowIndex == MaxBatchInsertSize || rowIndex == totalRowCount) {
                    hbaseClient.endBatchInsert();
                    rowIndex = 1;
                } else {
                    rowIndex++;
                }
            }
        }
    }
}
