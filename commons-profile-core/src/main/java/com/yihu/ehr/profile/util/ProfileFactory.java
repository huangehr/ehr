package com.yihu.ehr.profile.util;

import com.yihu.ehr.profile.core.FileProfile;
import com.yihu.ehr.profile.core.LinkProfile;
import com.yihu.ehr.profile.core.ProfileType;
import com.yihu.ehr.profile.core.StdProfile;
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
public class ProfileFactory {
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
    public static StdProfile createProfile(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());

        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return new StdProfile();
        } else if (directories.contains(DocumentFolder)) {
            return new FileProfile();
        } else if (directories.size() == 1 && directories.contains(LinkFolder)) {
            return new LinkProfile();
        }

        return null;
    }

    public static StdProfile createProfile(ProfileType type){
        switch(type){
            case File:
                return new FileProfile();

            case Standard:
                return new StdProfile();

            case Link:
                return new LinkProfile();

            default:
                return null;
        }
    }
}