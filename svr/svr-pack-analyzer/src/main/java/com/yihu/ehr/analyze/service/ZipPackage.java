package com.yihu.ehr.analyze.service;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.hbase.HBaseAdmin;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * 档案包解析逻辑。
 *
 * @author Airhead
 * @created 2018.01.16
 */
public class ZipPackage {
    //    private static final String STANDARD = "standard";
    //    private static final String ORIGIN = "origin";
    public static final String DATA = "d";
    private final static String TempPath = System.getProperty("java.io.tmpdir") + java.io.File.separator;

    private MPackage mPackage;
    private Zipper zipper = new Zipper();
    //数据集合
    private Map<String, DataSetRecord> dataSets = new TreeMap<>();
    //1结构化档案，2文件档案，3链接档案，4数据集档案
    private ProfileType profileType;
    private File zipFile;   //Zip档案包文件
    private File packFile;  //解压后文件目录

    public ZipPackage(MPackage mPackage) {
        this.mPackage = mPackage;
    }

    public Map<String, DataSetRecord> getDataSets() {
        return dataSets;
    }

    public void insertDataSet(String dataSetCode, DataSetRecord dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public ProfileType getProfileType() {
        //目前只解析标准档案包，其他类型档案包不处理
        profileType = ProfileType.Standard;
        return profileType;
    }

    public File getPackFile() {
        return packFile;
    }

    public void download() throws IOException, MyException {
        String remotePath = mPackage.getRemotePath();

        FastDFSUtil fastDFSUtil = SpringContext.getService(FastDFSUtil.class);
        String[] tokens = remotePath.split(":");
        String download = fastDFSUtil.download(tokens[0], tokens[1], TempPath);
        zipFile = new File(download);
    }

    public void unZip() throws Exception {
        String temp = TempPath + mPackage.getId();
        packFile = zipper.unzipFile(zipFile, temp, mPackage.getPwd());
        if (packFile == null || packFile.list() == null) {
            throw new RuntimeException("Invalid package file, package id: " + mPackage.getId());
        }
    }

    public void houseKeep() {
        try {
            FileUtils.deleteQuietly(zipFile);
            FileUtils.deleteQuietly(packFile);
        } catch (Exception e) {
            LogService.getLogger(PackageAnalyzeService.class).warn("House keep failed after package analyze: " + e.getMessage());
        }
    }

    public void resolve() throws Exception {
        ProfileType profileType = getProfileType();
        //目前只解析标准档案包
        if (profileType != ProfileType.Standard) {
            throw new RuntimeException("not a package file, package id: " + mPackage.getId());
        }

        ApplicationContext context = SpringContext.getApplicationContext();
        StdPackageAnalyzer packageAnalyzer = context.getBean(StdPackageAnalyzer.class);
        packageAnalyzer.analyze(this);
    }

    /**
     * 保存标准档案数据
     *
     * @throws Exception
     */
    public void save() throws Exception {
        Set<String> keySet = dataSets.keySet();
        for (String key : keySet) {
            DataSetRecord dataSetRecord = dataSets.get(key);
            saveDataSet(dataSetRecord);
        }
    }

    public void sendQcMsg() {
        Set<String> keySet = dataSets.keySet();
        for (String key : keySet) {
            DataSetRecord dataSetRecord = dataSets.get(key);
//            saveDataSet(dataSetRecord);
        }
    }

    private void saveDataSet(DataSetRecord dataSetRecord) throws Exception {
        ApplicationContext context = SpringContext.getApplicationContext();
        HBaseDao hBaseDao = context.getBean(HBaseDao.class);
        HBaseAdmin hBaseAdmin = context.getBean(HBaseAdmin.class);
        if (!hBaseAdmin.isTableExists(dataSetRecord.getCode())) {
            hBaseAdmin.createTable(dataSetRecord.getCode(), DATA);
        }
        String table = dataSetRecord.getCode();
        String rowKeyPrefix = dataSetRecord.getRowKeyPrefix();

        TableBundle bundle = new TableBundle();
        if (dataSetRecord.isReUploadFlg()) {
            String legacyRowKeys[] = hBaseDao.findRowKeys(ResourceCore.MasterTable, "^" + rowKeyPrefix);
            if (legacyRowKeys != null && legacyRowKeys.length > 0) {
                bundle.addRows(legacyRowKeys);
                hBaseDao.delete(ResourceCore.MasterTable, bundle);
            }
        }

        Map<String, DataElementRecord> records = dataSetRecord.getRecords();
        records.forEach((key, metaDataRecord) -> {
            String rowKey = dataSetRecord.genRowKey(key);
            String legacy = hBaseDao.get(table, rowKey);
            if (StringUtils.isNotEmpty(legacy)) {
                hBaseDao.delete(ResourceCore.MasterTable, rowKey);
            }

            bundle.clear();
            bundle.addValues(
                    rowKey,
                    DATA,
                    metaDataRecord.getDataGroup()
            );

//            bundle.addValues(
//                    rowKey,
//                    ORIGIN,
//                    metaDataRecord.getDataGroup()
//            );
            hBaseDao.save(table, bundle);
        });
    }


}
