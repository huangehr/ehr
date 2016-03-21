package com.yihu.ehr.task;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.cache.StdObjectQualifierTranslator;
import com.yihu.ehr.extractor.KeyDataExtractor;
import com.yihu.ehr.pack.TPackage;
import com.yihu.ehr.pack.TPackageService;
import com.yihu.ehr.persist.DataSetResolverWithChecker;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.profile.ProfileDataSet;
import com.yihu.ehr.persist.ProfileService;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class PackageResolveTask {
    @Autowired
    DataSetResolverWithChecker dataSetResolver;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TPackageService.Client packageService;

    @Autowired
    ProfileService profileService;

    private final static KeyDataExtractor dataFilter = null;

    private static ObjectNode EhrSummaryDataSet = null;
    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String JsonExt = ".json";

    public void archive(String packageId) throws TException {
        TPackage pack = packageService.getPackage(packageId);
        if (pack == null) return;

        doArchive(pack);
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
    @Scheduled(cron = "0/2 * * * * ?")
    public void archive() throws TException {
        /*TPackage pack = packageService.acquireArchive();
        if (pack == null) return;

        doArchive(pack);*/
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("The time is now " + dateFormat.format(new Date()));
    }

    public void doArchive(TPackage pack){
        try {
            String localFileName = downloadTo(LocalTempPath);

            File unZippedPath = new Zipper().unzipFile(
                    new File(localFileName),
                    LocalTempPath + PathSep + pack.getId(),
                    pack.getPwd());
            if (unZippedPath == null || !unZippedPath.isDirectory() || unZippedPath.list().length == 0) {
                throw new RuntimeException("Invalid package zip file, package id: " + pack.getId());
            }

            // build profile model
            Profile profile = new Profile();

            String basePackagePath = unZippedPath.getAbsolutePath();
            parseJsonDataSet(pack.getId(), profile, new File(basePackagePath + PathSep + StdFolder).listFiles(), false);

            File originFiles = new File(basePackagePath + PathSep + OriFolder);
            if (originFiles.exists()) {
                parseJsonDataSet(pack.getId(), profile, originFiles.listFiles(), true);
            }

            // make health event
            makeEventSummary(profile);

            // save to HBase
            importToHBase(profile);

            // report as finished
            //packageService.reportArchiveFinished(pack.getId(), "Identity card no: " + profile.getDemographicId() + ", archive id: " + profile.getId());

            // house keep
            houseKeep(localFileName, unZippedPath);
        } catch (NullPointerException ex){
            LogService.getLogger().error(ExceptionUtils.getStackTrace(ex));
        } catch (Exception ex) {
            String message = "Resolve package failed: " + ex.getMessage();
            //packageService.reportArchiveFailed(pack.getId(), message);

            LogService.getLogger().error(message);
        }
    }

    /**
     * 解析JSON文件中的数据。
     *
     * @param profile
     * @param files
     * @throws IOException
     */
    void parseJsonDataSet(String packageId, Profile profile, File[] files, boolean isOriginDataSet) throws ParseException, IOException {
        for (File file : files) {
            if (!file.getAbsolutePath().endsWith(JsonExt)) continue;

            ProfileDataSet dataSet = generateDataSet(packageId, profile, file, isOriginDataSet);

            // 原始数据存储在单独的表中, 表名为"数据集代码_ORIGIN"
            String dataSetTable = isOriginDataSet ? StdObjectQualifierTranslator.makeOriginDataSetTable(dataSet.getCode()) : dataSet.getCode();
            profile.addDataSet(dataSetTable, dataSet);
            dataSet.setCode(dataSetTable);

            // 在标准数据集中查找病人的就诊卡，身份证号与事件时间（门诊，住院，体检等时间）如果存在.
            if (!isOriginDataSet) {
                if (profile.getCardId().length() == 0) {
                    Object object = dataFilter.extract(dataSet, KeyDataExtractor.Filter.CardInfo);
                    if (null != object) {
                        Properties properties = (Properties) object;
                        profile.setCardId(properties.getProperty("CardNo"));
                    }
                }

                if (profile.getDemographicId() == null || profile.getDemographicId().length() == 0) {
                    profile.setDemographicId((String) dataFilter.extract(dataSet, KeyDataExtractor.Filter.DemographicInfo));
                }

                if (profile.getEventDate() == null) {
                    profile.setEventDate((Date)dataFilter.extract(dataSet, KeyDataExtractor.Filter.EventDate));
                }
            }
        }
    }

    public ProfileDataSet generateDataSet(String packageId, Profile profile, File jsonFile, boolean isOrigin) throws IOException {
        JsonNode jsonNode = objectMapper.readTree(jsonFile);
        if (jsonNode.isNull()) {
            throw new IOException("无效JSON文件，文件已损坏或数据格式不对");
        }

        ProfileDataSet dataSet = dataSetResolver.parseJsonDataSet(jsonNode, isOrigin);
        return dataSet;
    }

    /**
     * 根据此次的数据产生一个健康事件，并更新数据集的行ID.
     *
     * @param profile
     */
    public void makeEventSummary(Profile profile) {
        boolean summaryGenerated = false;
        for (String dataSetTable : profile.getDataSetTables()) {
            if (!summaryGenerated && EhrSummaryDataSet.has(dataSetTable)) {
                profile.setSummary(EhrSummaryDataSet.get(dataSetTable).textValue());

                summaryGenerated = true;
            }

            int rowIndex = 0;
            ProfileDataSet dataSet = (ProfileDataSet)profile.getDataSet(dataSetTable);
            String[] rowKeys = new String[dataSet.getRecordKeys().size()];
            dataSet.getRecordKeys().toArray(rowKeys);
            for (String rowKey : rowKeys){
                dataSet.updateRecordKey(rowKey, profile.getId() + "$" + rowIndex++);
            }
        }
    }

    /**
     * 将数据导入至HBase中
     *
     * @param profile
     */
    void importToHBase(Profile profile) throws IOException {

        profileService.saveProfile(profile);
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
            LogService.getLogger(PackageResolveTask.class).error("文档清除失败:" + e.getMessage());
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

    private String downloadTo(String location){
        return "";
    }
}
