package com.yihu.ehr.service.resource.stage1;

import com.yihu.ehr.constants.ProfileType;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 包模型生成器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 15:28
 */
public class PackModelFactory {
    public final static String StandardFolder = "standard";
    public final static String OriginFolder = "origin";
    public final static String DocumentFolder = "documents";
    public final static String DocumentsFile = "documents.json";
    public final static String LinkFolder = "index";
    public final static String LinkFile = "link.json";

    /**
     * 读取档案包目录结构判断档案类型。
     *
     * @param root
     * @return
     */
    public static StandardPackage createPackModel(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());

        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return createPackModel(ProfileType.Standard);
        } else if (directories.contains(DocumentsFile)) {
            return createPackModel(ProfileType.File);
        } else if (directories.size() == 1 && directories.contains(LinkFile)) {
            return createPackModel(ProfileType.Link);
        }

        return null;
    }

    public static StandardPackage createPackModel(ProfileType type){
        switch(type){
            case Standard:
                return new StandardPackage();

            case File:
                return new FilePackage();

            case Link:
                return new LinkPackage();

            default:
                return null;
        }
    }
}