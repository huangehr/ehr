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
    public final static String MetaDataFile = "meta.json";
    public final static String LinkFolder = "index";
    public final static String JsonExt = ".json";

    /**
     * 读取档案包目录结构判断档案类型。
     *
     * @param root
     * @return
     */
    public static StdPackModel createPackModel(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());

        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return createPackModel(ProfileType.Standard);
        } else if (directories.contains(DocumentFolder)) {
            return createPackModel(ProfileType.File);
        } else if (directories.size() == 1 && directories.contains(LinkFolder)) {
            return createPackModel(ProfileType.Link);
        }

        return null;
    }

    public static StdPackModel createPackModel(ProfileType type){
        switch(type){
            case Standard:
                return new StdPackModel();

            case File:
                return new FilePackModel();

            case Link:
                return new LinkPackModel();

            default:
                return null;
        }
    }
}