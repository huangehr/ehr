package com.yihu.ehr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.extractor.ExtractorChain;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.lightweight.LightWeightProfile;
import com.yihu.ehr.profile.core.nostructured.UnStructuredDocumentFile;
import com.yihu.ehr.profile.core.nostructured.UnStructuredProfile;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * 档案归档任务.
 *
 * @author Sand
 * @version 1.0
 * @created 2015.09.09 15:04
 */
@Component
public class LightWeihgtPackageResolver {
    @Autowired
    ApplicationContext context;

    @Autowired
    DataSetResolverWithTranslator dataSetResolverWithTranslator;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExtractorChain extractorChain;

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
    public LightWeightProfile doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }


        LightWeightProfile lightWeightProfile = new LightWeightProfile();       //轻量级档案

        File[] files = root.listFiles();

        for(File file:files){
            lightWeightProfile = lightWeightDataSetParse(lightWeightProfile,file.listFiles());
        }

        //// TODO: 2016/4/15
        //makeEventSummary(structuredProfile);
        
        return lightWeightProfile;
    }


    /**
     * 轻量级档案包解析JSON文件中的数据。
     * @param lightWeightProfile
     * @param
     * @throws IOException
     */
    public LightWeightProfile lightWeightDataSetParse(LightWeightProfile lightWeightProfile, File[] files) throws IOException, ParseException {
        if(files==null){
            throw new IOException("There is no file");
        }
        File file = files[0];
        JsonNode jsonNode = objectMapper.readTree(file);
        if (jsonNode.isNull()) {
            throw new IOException("Invalid json file when generate data set");
        }
        //设置数据集
        dataSetResolverWithTranslator.parseLightJsonDataSet(lightWeightProfile,jsonNode);
        file.delete();
        return lightWeightProfile;
    }



}
