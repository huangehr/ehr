package com.yihu.ehr.analyze.service.pack;

import com.yihu.ehr.analyze.config.FastDfsConfig;
import com.yihu.ehr.profile.EventType;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.model.PackageDataSet;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.system.LocalTempPathUtil;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
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

    public final static String StandardFolder = "standard";
    public final static String OriginFolder = "origin";
    public final static String DocumentsFile = "documents.json";
    public final static String LinkFile = "index";

    private static final RestTemplate REST_TEMPLATE;
    private static final HttpHeaders HTTP_HEADERS;
    static {
        REST_TEMPLATE = new RestTemplate();
        HTTP_HEADERS = new HttpHeaders();
        HTTP_HEADERS.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
    }

    //机构代码
    private String orgCode;
    //机构名称
    private String orgName;
    //机构地区
    private String orgArea;
    //cda版本
    private String cdaVersion;
    //事件号
    private String eventNo;
    //事件时间
    private Date eventDate;
    //事件类型
    private EventType eventType;
    //病人ID
    private String patientId;
    //科室代码
    private String deptCode;
    //ICD10诊断列表
    private Set<String> diagnosisCode = new HashSet<>() ;
    //ICD10诊断名称列表
    private Set<String> diagnosisName = new HashSet<>();
    //数据包
    private EsSimplePackage esSimplePackage;
    //zip辅助对象
    private Zipper zipper = new Zipper();
    //数据集合
    private Map<String, PackageDataSet> dataSets = new TreeMap<>();
    //Zip档案包文件
    private File zipFile;
    //解压后文件目录
    private File packFile;
    //private Set<String> tableSet = new HashSet<>();
    //数据集质控记录
    private Map<String, Object> qcDataSetRecord = new HashMap<>();
    //数据元质控记录
    private List<Map<String, Object>> qcMetadataRecords = new ArrayList<>();

    public ZipPackage(EsSimplePackage esSimplePackage) {
        this.esSimplePackage = esSimplePackage;
    }

    public String getOrgCode() {
        return orgCode;
    }
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgArea() {
        return orgArea;
    }
    public void setOrgArea(String orgArea) {
        this.orgArea = orgArea;
    }


    public String getCdaVersion() {
        return cdaVersion;
    }
    public void setCdaVersion(String cdaVersion) {
        this.cdaVersion = cdaVersion;
    }

    public String getEventNo() {
        return eventNo;
    }
    public void setEventNo(String eventNo) {
        this.eventNo = eventNo;
    }

    public Date getEventDate() {
        return eventDate;
    }
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getPatientId() {
        return patientId;
    }
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getDeptCode() {
        return deptCode;
    }
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public Set<String> getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(Set<String> diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Set<String> getDiagnosisName() {
        return diagnosisName;
    }

    public void setDiagnosisName(Set<String> diagnosisName) {
        this.diagnosisName = diagnosisName;
    }

    public EsSimplePackage getEsSimplePackage() {
        return esSimplePackage;
    }

    public Map<String, PackageDataSet> getDataSets() {
        return dataSets;
    }

    public void insertDataSet(String dataSetCode, PackageDataSet dataSet) {
        this.dataSets.put(dataSetCode, dataSet);
    }

    public File getPackFile() {
        return packFile;
    }

    public Map<String, Object> getQcDataSetRecord() {
        return qcDataSetRecord;
    }
    public void setQcDataSetRecord(Map<String, Object> qcDataSetRecord) {
        this.qcDataSetRecord = qcDataSetRecord;
    }

    public List<Map<String, Object>> getQcMetadataRecords() {
        return qcMetadataRecords;
    }
    public void setQcMetadataRecords(List<Map<String, Object>> qcMetadataRecords) {
        this.qcMetadataRecords = qcMetadataRecords;
    }

    public void download() throws IOException {
        FastDfsConfig config = SpringContext.getService(FastDfsConfig.class);
        String remotePath = esSimplePackage.getRemote_path();
        String url = config.getPublicServer() + "/" + remotePath.replace(":", "/");
        HttpEntity<String> entity = new HttpEntity<>(HTTP_HEADERS);
        ResponseEntity<byte[]> response = REST_TEMPLATE.exchange(
                url,
                HttpMethod.GET, entity, byte[].class);
        if (response.getStatusCode() == HttpStatus.OK) {
            Path path = Files.write(Paths.get(LocalTempPathUtil.getTempPathWithUUIDSuffix() +  esSimplePackage.get_id() + ".zip"), response.getBody());
            zipFile = path.toFile();
        } else {
            zipFile = null;
        }

    }

    public void unZip() throws Exception {
        if (zipFile == null) {
            return;
        }
        packFile = zipper.unzipFile(zipFile,  LocalTempPathUtil.getTempPathWithUUIDSuffix() + esSimplePackage.get_id(), esSimplePackage.getPwd());
        if (packFile == null || !packFile.isDirectory() || packFile.list().length == 0) {
            throw new ZipException("Invalid package file.");
        }
    }

    public void resolve() throws Exception {
        ProfileType profileType;
        List<String> directories = CollectionUtils.arrayToList(packFile.list());
        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            profileType = ProfileType.Standard;
        } else if (directories.contains(DocumentsFile)) {
            profileType = ProfileType.File;
        } else if (directories.size() == 1 && directories.contains(LinkFile)) {
            profileType = ProfileType.Link;
        } else { // 数据集档案包（zip下只有 .json 数据文件）。
            profileType = ProfileType.Simple;
        }
        //目前只解析标准档案包
        if (profileType != ProfileType.Standard) {
            throw new ZipException("Not a standard package file");
        }
        ApplicationContext context = SpringContext.getApplicationContext();
        StdPackageAnalyzer packageAnalyzer = context.getBean(StdPackageAnalyzer.class);
        packageAnalyzer.analyze(this);
    }

    public void houseKeep() {
        try {
            FileUtils.deleteQuietly(zipFile);
            FileUtils.deleteQuietly(packFile);
        } catch (Exception e) {
            LogService.getLogger(PackageAnalyzeService.class).warn("House keep failed after package analyze: " + e.getMessage());
        }
    }

    /**
     * 保存标准档案数据
     *
     * @throws Exception
     */
   /* public void save() throws Exception {
        Set<String> keySet = dataSets.keySet();
        for (String key : keySet) {
            DataSetRecord dataSetRecord = dataSets.get(key);
            saveDataSet(dataSetRecord);
        }
    }*/

   /* private void saveDataSet(DataSetRecord dataSetRecord) throws Exception {
        String table = dataSetRecord.getCode();
        createTable(table);
        ApplicationContext context = SpringContext.getApplicationContext();
        HBaseDao hBaseDao = context.getBean(HBaseDao.class);
        String rowKeyPrefix = dataSetRecord.getRowKeyPrefix();
        TableBundle bundle = new TableBundle();
        if (dataSetRecord.isReUploadFlg()) {
            String legacyRowKeys[] = hBaseDao.findRowKeys(table, rowKeyPrefix, rowKeyPrefix.substring(0, rowKeyPrefix.length() - 1) + "z", "^" + rowKeyPrefix);
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
            String receiveTime = DateUtil.toString(esSimplePackage.getReceive_date(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT);
            dataGroup.put("receiveTime", receiveTime);  //增加接收时间
            bundle.clear();
            bundle.addValues(
                    rowKey,
                    DATA,
                    dataGroup
            );

            hBaseDao.save(table, bundle);
        });
    }*/

    /*private synchronized void createTable(String table) throws Exception {
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
    }*/

}
