package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.extractor.EventExtractor;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.lightweight.LightWeightProfile;
import com.yihu.ehr.profile.core.nostructured.UnStructuredDocumentFile;
import com.yihu.ehr.profile.core.nostructured.UnStructuredProfile;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.log.LogService;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class PackageResolver {
    @Autowired
    ApplicationContext context;

    @Autowired
    DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExtractorChain extractorChain;

    @Autowired
    private StructuredPackageResolver structuredPackageResolver;

    @Autowired
    private UnStructuredPackageResolver unStructuredPackageResolver;

    @Autowired
    private  LightWeihgtPackageResolver lightWeihgtPackageResolver;


    private final static char PathSep = File.separatorChar;
    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String IndexFolder = "index";
    private final static String DocumentFolder = "document";
    private final static String JsonExt = ".json";

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 InDoubt 状态，即无法决定该JSON档案的去向，需要人为干预。
     * <p>
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    public Profile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }


        StructuredProfile structuredProfile = new StructuredProfile();          //结构化档案
        UnStructuredProfile unStructuredProfile = new UnStructuredProfile();    //非结构化档案
        LightWeightProfile lightWeightProfile = new LightWeightProfile();       //轻量级档案

        File[] files = root.listFiles();
        String firstFilepath = files[0].getPath();
        String firstFolderName =  firstFilepath.substring(firstFilepath.lastIndexOf("\\")+1);

        List<UnStructuredDocumentFile> unStructuredDocumentFileList = new ArrayList<>();  //document底下的文件
        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            switch (firstFolderName){
                case OriFolder:
                    //结构化档案报处理
                    structuredProfile = structuredPackageResolver.structuredDataSetParse(structuredProfile, file.listFiles(),folderName);
                    break;
                case IndexFolder:
                    //轻量级档案包处理
                    lightWeightProfile = lightWeihgtPackageResolver.lightWeightDataSetParse(lightWeightProfile,file.listFiles());
                case DocumentFolder:
                    //非结构化档案包处理
                    if(folderName.equals(DocumentFolder)){
                        unStructuredDocumentFileList = unStructuredPackageResolver.unstructuredDocumentParse(unStructuredProfile, file.listFiles());
                    }else if(folderName.equals("meta.json")){
                        unStructuredProfile = unStructuredPackageResolver.unstructuredDataSetParse(unStructuredProfile,file, unStructuredDocumentFileList);
                    }
                    break;
                default: break;
            }
        }

        //makeEventSummary(structuredProfile);

        houseKeep(zipFile, root);

        return structuredProfile;
    }


    public ProfileType getProfileType(MPackage pack, String zipFile) throws ZipException {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }


        File[] files = root.listFiles();
        String firstFilepath = files[0].getPath();
        String firstFolderName =  firstFilepath.substring(firstFilepath.lastIndexOf("\\")+1);

        if (firstFolderName.equals(OriFolder)){
            return ProfileType.Structured;
        }else if(firstFolderName.equals(IndexFolder)){
            return ProfileType.Lightweight;
        }else if(firstFolderName.equals(DocumentFolder)){
            return ProfileType.NoStructured;
        }
        return null;
    }

    private void houseKeep(String zipFile, File root) {
        try {
            FileUtils.deleteQuietly(new File(zipFile));
            FileUtils.deleteQuietly(root);
        } catch (Exception e) {
            LogService.getLogger(PackageResolver.class).warn("House keep failed after package resolve: " + e.getMessage());
        }
    }

}
