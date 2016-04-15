package com.yihu.ehr.profile.core;

import org.springframework.util.CollectionUtils;

import java.io.File;
import java.util.List;

/**
 * 档案生成器。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.13 15:28
 */
public class ProfileGenerator {
    public final static String StructedProfileStdFolder = "standard";
    public final static String StructedProfileOriFolder = "origin";

    public final static String NonstructedProfileDocFolder = "documents";
    public final static String NonstructedProfileMetaFile = "meta.json";

    public final static String LightWeightProfileIndexFolder = "index";

    public final static String JsonExt = ".json";

    /**
     * 读取档案包目录结构判断档案类型。
     *
     * @param root
     * @return
     */
    public static Profile generate(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());
        List<String> files = CollectionUtils.arrayToList(root.listFiles());

        if (directories.contains(StructedProfileStdFolder) && directories.contains(StructedProfileOriFolder)) {
            return new StructedProfile();
        } else if (directories.contains(NonstructedProfileDocFolder) && files.contains(NonstructedProfileMetaFile)) {
            return new NonstructedProfile();
        } else if (directories.size() == 1 && directories.contains(LightWeightProfileIndexFolder)) {
            return new LightWeightProfile();
        }

        return null;
    }
}
