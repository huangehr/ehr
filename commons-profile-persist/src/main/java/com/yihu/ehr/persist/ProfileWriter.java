package com.yihu.ehr.persist;

import com.yihu.ehr.data.XHBaseClient;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.profile.ProfileTableOptions;
import com.yihu.ehr.util.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 档案存储器。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.27 11:56
 */
@Service
public class ProfileWriter {
    @Autowired
    private XHBaseClient hbaseClient;

    public ProfileWriter() {
    }

    public void writeArchive(Profile healthArchive) throws IOException {
        // 先存档案
        hbaseClient.insertRecord(ProfileTableOptions.ProfileTable,
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

            hbaseClient.beginBatchInsert(tableName, false);
            for (String key : dataSet.getRecordKeys()) {
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
            }

            hbaseClient.endBatchInsert();
        }
    }
}
