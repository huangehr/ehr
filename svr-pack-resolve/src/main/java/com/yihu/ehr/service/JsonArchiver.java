package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ha.archives.model.ehr.*;
import com.yihu.ha.archives.model.ehr.filter.CardInfoFilter;
import com.yihu.ha.archives.model.ehr.filter.DemographicFilter;
import com.yihu.ha.archives.model.ehr.filter.EventDateFilter;
import com.yihu.ha.archives.model.ehr.filter.KeyDataFilter;
import com.yihu.ha.archives.model.pack.XJsonPackage;
import com.yihu.ha.archives.model.pack.XJsonPackageManager;
import com.yihu.ha.cache.model.CachedMetaData;
import com.yihu.ha.cache.model.StdDataRedisCache;
import com.yihu.ha.constrant.BizObject;
import com.yihu.ha.constrant.EnvironmentOptions;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.patient.model.demographic.DemographicId;
import com.yihu.ha.util.DateFormatter;
import com.yihu.ha.util.ObjectId;
import com.yihu.ha.util.XEnvironmentOption;
import com.yihu.ha.util.compress.Zipper;
import com.yihu.ha.util.log.LogService;
import com.yihu.ha.util.operator.StringUtil;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JSON 档案归档器.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
public class JsonArchiver {
    private final static KeyDataFilter dataFilter;

    private final static Pattern NumberPattern = Pattern.compile("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])");
    private static ObjectNode EhrSummaryDataSet;

    private final static char PathSep = File.separatorChar;

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String JsonExt = ".json";

    XJsonPackageManager jsonArchiveManager = ServiceFactory.getService(Services.JsonPackageManager);

    public void archive(String packageId){
        XJsonPackage jsonPackage = jsonArchiveManager.getJsonPackage(packageId);
        if (jsonPackage == null) return;

        doArchive(jsonPackage);
    }

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 InDoubt 状态，即无法决定该JSON档案的去向，需要人为干预。
     *
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    public void archive() {
        XJsonPackage jsonPackage = jsonArchiveManager.acquireArchive();
        if (jsonPackage == null/* || jsonArchive.getArchiveStatus() != XJsonPackage.ArchiveStatus.Acquired*/) {
            return;
        }

        doArchive(jsonPackage);
    }

    public void doArchive(XJsonPackage jsonPackage){
        try {
            String localFileName = jsonPackage.downloadTo(LocalTempPath);

            File unZippedPath = new Zipper().unzipFile(new File(localFileName),
                    LocalTempPath + PathSep + jsonPackage.getId(),
                    jsonPackage.getPwd());
            if (unZippedPath == null || !unZippedPath.isDirectory() || unZippedPath.list().length == 0) {
                throw new RuntimeException(jsonPackage.getId() + " 档案包结构错误，未包含数据或解压失败");
            }

            // build archive model
            EhrArchive ehrArchive = new EhrArchive();

            String basePackagePath = unZippedPath.getAbsolutePath();
            parseJsonDataSet(jsonPackage.getId(), ehrArchive, new File(basePackagePath + PathSep + StdFolder).listFiles(), false);

            File originFiles = new File(basePackagePath + PathSep + OriFolder);
            if (originFiles.exists()) {
                parseJsonDataSet(jsonPackage.getId(), ehrArchive, originFiles.listFiles(), true);
            }

            // make health event
            makeEventSummary(ehrArchive);

            // save to HBase
            importToHBase(ehrArchive);

            // report as finished
            jsonArchiveManager.reportArchiveFinished(jsonPackage.getId(), "身份证号: " + ehrArchive.getDemographicId() + ", 档案: " + ehrArchive.getId());

            // house keep
            houseKeep(localFileName, unZippedPath);
        } catch (NullPointerException ex){
            LogService.getLogger().error(ExceptionUtils.getStackTrace(ex));
        } catch (Exception ex) {
            String message = "归档出错. 原因: " + ex.getMessage();
            jsonArchiveManager.reportArchiveFailed(jsonPackage.getId(), message);
            LogService.getLogger().error(message);
        }
    }

    /**
     * 解析JSON文件中的数据。
     *
     * @param ehrArchive
     * @param files
     * @throws IOException
     */
    void parseJsonDataSet(String jsonArchiveId, EhrArchive ehrArchive, File[] files, boolean isOriginDataSet) throws ParseException {
        for (File file : files) {
            if (!file.getAbsolutePath().endsWith(JsonExt)) continue;

            EhrDataSet dataSet = generateDataSet(jsonArchiveId, ehrArchive, file, isOriginDataSet);

            // 原始数据存储在单独的表中, 表名为"数据集代码_ORIGIN"
            String dataSetTable = isOriginDataSet ? StdObjectQualifierTranslator.makeOriginDataSetTable(dataSet.getCode()) : dataSet.getCode();
            ehrArchive.addDataSet(dataSetTable, dataSet);
            dataSet.setCode(dataSetTable);

            // 在标准数据集中查找病人的就诊卡，身份证号与事件时间（门诊，住院，体检等时间）如果存在.
            if (!isOriginDataSet) {
                if (ehrArchive.getCardId().length() == 0) {
                    Object object = dataFilter.filterKeyData(dataSet, KeyDataFilter.Filter.CardInfo);
                    if (null != object) {
                        Properties properties = (Properties) object;
                        ehrArchive.setCardId(properties.getProperty("CardNo"));
                    }
                }

                if (ehrArchive.getDemographicId() == null || ehrArchive.getDemographicId().idCardNo.length() == 0) {
                    ehrArchive.setDemographicId(new DemographicId((String) dataFilter.filterKeyData(dataSet, KeyDataFilter.Filter.DemographicInfo)));
                }

                if (ehrArchive.getEventDate() == null) {
                    ehrArchive.setEventDate((Date)dataFilter.filterKeyData(dataSet, KeyDataFilter.Filter.EventDate));
                }
            }
        }
    }

    public EhrDataSet generateDataSet(String jsonArchiveId, EhrArchive healthArchive, File jsonFile, boolean isOrigin) {
        EhrDataSet dataSet = new EhrDataSet();
        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonFile);
            if (jsonNode.isNull()) {
                throw new IOException("无效JSON文件，文件已损坏或数据格式不对");
            }

            String innerVersion = jsonNode.get("inner_version").asText();
            String code = jsonNode.get("code").asText();
            String eventNo = jsonNode.get("event_no").asText();
            String patientId = jsonNode.get("patient_id").asText();
            String orgCode = jsonNode.get("org_code").asText();
            String eventDate = jsonNode.path("event_time").asText();        // 旧的数据包可能不存在这个属性

            healthArchive.setPatientId(patientId);
            healthArchive.setEventNo(eventNo);
            healthArchive.setCreateDate(new Date());
            healthArchive.setCdaVersion(innerVersion);
            healthArchive.setOrgCode(orgCode);
            if(!StringUtil.isEmpty(eventDate)) healthArchive.setEventDate(DateFormatter.simpleDateTimeParse(eventDate));

            dataSet.setPatientId(patientId);
            dataSet.setEventNo(eventNo);
            dataSet.setCdaVersion(innerVersion);
            dataSet.setCode(code);
            dataSet.setOrgCode(orgCode);

            JsonNode jsonRecords = jsonNode.get("data");
            for (int i = 0; i < jsonRecords.size(); ++i) {
                Map<String, String> record = new HashMap<>();

                JsonNode jsonRecord = jsonRecords.get(i);
                Iterator<Map.Entry<String, JsonNode>> iterator = jsonRecord.fields();
                while (iterator.hasNext()) {
                    Map.Entry<String, JsonNode> item = iterator.next();
                    String key = item.getKey();
                    if (key.equals("PATIENT_ID") || key.equals("EVENT_NO")) continue;

                    if (key.contains("HDSA00_01_017") && !item.getValue().asText().contains("null")) {
                        System.out.println(item.getValue().asText());
                    }

                    String[] standardizedMetaData = standardizeMetaData(jsonArchiveId,
                            innerVersion,
                            code,
                            isOrigin,
                            key,
                            item.getValue().isNull() ? "" : item.getValue().asText());
                    if (standardizedMetaData != null) {
                        record.put(standardizedMetaData[0], standardizedMetaData[1]);
                        if (standardizedMetaData.length > 2)
                            record.put(standardizedMetaData[2], standardizedMetaData[3]);
                    }
                }

                dataSet.addRecord(new ObjectId((short) 0, BizObject.StdArchive).toString(), record);
            }
        } catch (NullPointerException ex) {
            throw new ArchiveException("JsonFileParser.generateDataSet 出现空指针");
        } catch (Exception ex) {
            throw new ArchiveException(ex.getMessage());
        } finally {
        }

        return dataSet;
    }

    /**
     * 翻译数据元。
     *
     * @param jsonArchiveId
     * @param innerVersion
     * @param dataSetCode
     * @param isOriginDataSet
     * @param metaDataInnerCode
     * @param actualData
     * @return
     */
    String[] standardizeMetaData(String jsonArchiveId, String innerVersion, String dataSetCode, boolean isOriginDataSet,
                                 String metaDataInnerCode, String actualData) {
        actualData = (actualData == null) ? "" : actualData.trim();

        // 找不到
        CachedMetaData metaData = StdDataRedisCache.getMetaData(innerVersion, dataSetCode, metaDataInnerCode);
        if (null == metaData) {
            String msg = "版本 %1, 数据集 %2, 数据元 %3 在标准库中不存在, JSON档案ID: %4. 请检查此数据元的来历."
                    .replace("%1", innerVersion)
                    .replace("%2", dataSetCode)
                    .replace("%3", metaDataInnerCode)
                    .replace("%4", jsonArchiveId);

            LogService.getLogger(JsonArchiver.class).warn(msg);
            return null;
        }

        // 仅对标准化数据集及有关联字典的数据元进行翻译
        if (!isOriginDataSet && actualData != null && actualData.length() > 0 && metaData.dictId > 0) {
            String[] tempQualifiers = StdObjectQualifierTranslator.splitInnerCodeAsCodeValue(metaDataInnerCode);

            String codeQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[0], metaData.type);
            String valueQualifier = StdObjectQualifierTranslator.toHBaseQualifier(tempQualifiers[1], metaData.type);

            String value = StdDataRedisCache.getDictEntryValue(innerVersion, metaData.dictId, actualData);

            return new String[]{codeQualifier, actualData, valueQualifier, value == null ? "" : value};
        } else {
            if (metaData.type.equals("D")) {
                actualData = actualData.length() <= 10 ? actualData: actualData.substring(0, actualData.lastIndexOf(' ')) + " 00:00:00";
            } else if(metaData.type.equals("DT")) {
                actualData = actualData.contains(".") ? actualData.substring(0, actualData.lastIndexOf('.')) : actualData;
            }else if (metaData.type.equals("N")) {
                Matcher matcher = NumberPattern.matcher(actualData);
                if (matcher.find()) {
                    actualData = matcher.group();
                } else {
                    actualData = "";
                }
            }

            return new String[]{StdObjectQualifierTranslator.toHBaseQualifier(metaDataInnerCode, metaData.type), actualData};
        }
    }

    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param healthArchive
     */
    public void makeEventSummary(EhrArchive healthArchive) {
        boolean summaryGenerated = false;
        for (String dataSetTable : healthArchive.getDataSetTables()) {
            if (!summaryGenerated && EhrSummaryDataSet.has(dataSetTable)) {
                healthArchive.setSummary(EhrSummaryDataSet.get(dataSetTable).textValue());

                summaryGenerated = true;
            }

            int rowIndex = 0;
            EhrDataSet dataSet = (EhrDataSet)healthArchive.getDataSet(dataSetTable);
            String[] rowKeys = new String[dataSet.getRecordKeys().size()];
            dataSet.getRecordKeys().toArray(rowKeys);
            for (String rowKey : rowKeys){
                dataSet.updateRecordKey(rowKey, healthArchive.getId() + "$" + rowIndex++);
            }
        }
    }

    /**
     * 将数据导入至HBase中
     *
     * @param healthArchive
     */
    void importToHBase(EhrArchive healthArchive) throws IOException {
        XEhrArchiveManager healthArchiveManager = ServiceFactory.getService(Services.EhrArchiveManager);
        healthArchiveManager.saveArchive(healthArchive);
    }

    /**
     * 清理临时文件。
     *
     * @param zipFile
     * @param unZippedPath
     */
    private void houseKeep(String zipFile, File unZippedPath) {
        try {
            new File(zipFile).delete();

            recursiveDelete(unZippedPath);
        } catch (Exception e) {
            LogService.getLogger(JsonArchiveJob.class).error("文档清除失败:" + e.getMessage());
        }
    }

    void recursiveDelete(File file) {
        if (!file.exists())
            return;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                recursiveDelete(f);
            }
        }

        file.delete();
    }

    static {
        // init data filter
        dataFilter = new DemographicFilter();
        dataFilter.setNextFilter(new EventDateFilter()).setNextFilter(new CardInfoFilter());

        // summary data set
        XEnvironmentOption environmentOption = ServiceFactory.getService(Services.EnvironmentOption);
        String ehrSummaryDataSet = environmentOption.getOption(EnvironmentOptions.EhrSummaryDataSet);

        ObjectMapper objectMapper = ServiceFactory.getService(Services.ObjectMapper);
        try {
            EhrSummaryDataSet = (ObjectNode) objectMapper.readTree(ehrSummaryDataSet);
        } catch (IOException e) {
            LogService.getLogger(JsonArchiver.class).error("未配置" + EnvironmentOptions.EhrSummaryDataSet + ", 无法生成健康事件摘要。");
        }
    }
}
