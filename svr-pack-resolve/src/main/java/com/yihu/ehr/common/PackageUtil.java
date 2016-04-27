package com.yihu.ehr.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ProfileConstant;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.profile.core.extractor.ExtractorChain;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.persist.DataSetResolverWithTranslator;
import com.yihu.ehr.util.compress.Zipper;
import net.lingala.zip4j.exception.ZipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 档案包工具.
 *
 * @author Sand
 * @created 2015.09.09 15:04
 */
@Component
public class PackageUtil {
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

    public ProfileType getProfileType(MPackage pack, String zipFile) throws ZipException {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }

        File[] files = root.listFiles();
        String firstFilepath = files[0].getPath();
        String firstFolderName =  firstFilepath.substring(firstFilepath.lastIndexOf("\\")+1);

        if (firstFolderName.equals(ProfileConstant.OriFolder)){
            return ProfileType.Structured;
        }else if(firstFolderName.equals(ProfileConstant.IndexFolder)){
            return ProfileType.Link;
        }else if(firstFolderName.equals(ProfileConstant.DocumentFolder)){
            return ProfileType.NonStructured;
        }

        return null;
    }
}
