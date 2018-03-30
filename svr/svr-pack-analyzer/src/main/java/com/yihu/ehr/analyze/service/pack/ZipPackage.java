package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.analyze.config.FastdfsConfig;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.hbase.HBaseAdmin;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.hbase.TableBundle;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private Set<String> tableSet = new HashSet<>();

    public ZipPackage(MPackage mPackage) {
        this.mPackage = mPackage;
    }

    public MPackage getmPackage() {
        return mPackage;
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
        FastdfsConfig config = SpringContext.getService(FastdfsConfig.class);
        String remotePath = mPackage.getRemotePath();
        String url = config.getPublicServer() + "/" + remotePath.replace(":", "/");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<byte[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET, entity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Path path = Files.write(Paths.get(TempPath + mPackage.getId() + ".zip"), response.getBody());
            zipFile = path.toFile();
        } else {
            zipFile = null;
        }

    }

    public void unZip() throws Exception {
        if (zipFile == null) {
            return;
        }
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

    private void saveDataSet(DataSetRecord dataSetRecord) throws Exception {
        String table = dataSetRecord.getCode();
        createTable(table);

        ApplicationContext context = SpringContext.getApplicationContext();
        HBaseDao hBaseDao = context.getBean(HBaseDao.class);


        String rowKeyPrefix = dataSetRecord.getRowKeyPrefix();

        TableBundle bundle = new TableBundle();
        if (dataSetRecord.isReUploadFlg()) {
            String legacyRowKeys[] = hBaseDao.findRowKeys(table, rowKeyPrefix, rowKeyPrefix.substring(0, rowKeyPrefix.length() - 1) + "z",  "^" + rowKeyPrefix);
            if (legacyRowKeys != null && legacyRowKeys.length > 0) {
                bundle.addRows(legacyRowKeys);
                hBaseDao.delete(table, bundle);
            }
        }

        Map<String, DataElementRecord> records = dataSetRecord.getRecords();
        records.forEach((key, metaDataRecord) -> {
            String rowKey = dataSetRecord.genRowKey(key);
            String legacy = hBaseDao.get(table, rowKey);
            if (StringUtils.isNotEmpty(legacy)) {
                hBaseDao.delete(table, rowKey);
            }

            Map<String, String> dataGroup = metaDataRecord.getDataGroup();
            String receiveTime = DateUtil.toString(mPackage.getReceiveDate(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
            dataGroup.put("receiveTime", receiveTime);  //增加接收时间
            bundle.clear();
            bundle.addValues(
                    rowKey,
                    DATA,
                    dataGroup
            );

            hBaseDao.save(table, bundle);
        });
    }

    private synchronized void createTable(String table) throws Exception {
        boolean created = tableSet.contains(table);
        if (created) {
            return;
        }

        ApplicationContext context = SpringContext.getApplicationContext();
        HBaseAdmin hBaseAdmin = context.getBean(HBaseAdmin.class);
        if (!hBaseAdmin.isTableExists(table)) {
            hBaseAdmin.createTable(table, DATA);
            tableSet.add(table);
        }
    }

}
