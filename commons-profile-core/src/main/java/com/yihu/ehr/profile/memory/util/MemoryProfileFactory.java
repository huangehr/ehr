package com.yihu.ehr.profile.memory.util;

import com.yihu.ehr.profile.memory.intermediate.MemoryFileProfile;
import com.yihu.ehr.profile.memory.intermediate.MemoryLinkProfile;
import com.yihu.ehr.profile.memory.intermediate.MemoryProfile;
import com.yihu.ehr.profile.memory.commons.ProfileType;
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
public class MemoryProfileFactory {
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
    public static MemoryProfile createProfile(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());

        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return new MemoryProfile();
        } else if (directories.contains(DocumentFolder)) {
            return new MemoryFileProfile();
        } else if (directories.size() == 1 && directories.contains(LinkFolder)) {
            return new MemoryLinkProfile();
        }

        return null;
    }

    public static MemoryProfile createProfile(ProfileType type){
        switch(type){
            case File:
                return new MemoryFileProfile();

            case Standard:
                return new MemoryProfile();

            case Link:
                return new MemoryLinkProfile();

            default:
                return null;
        }
    }
}