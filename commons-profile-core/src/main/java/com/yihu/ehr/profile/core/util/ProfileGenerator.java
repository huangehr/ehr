package com.yihu.ehr.profile.core.util;

import com.yihu.ehr.profile.core.profile.FileProfile;
import com.yihu.ehr.profile.core.profile.LinkProfile;
import com.yihu.ehr.profile.core.profile.ProfileType;
import com.yihu.ehr.profile.core.profile.StandardProfile;
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
    public static StandardProfile generate(File root) {
        List<String> directories = CollectionUtils.arrayToList(root.list());
        List<String> files = CollectionUtils.arrayToList(root.listFiles());

        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return new StandardProfile();
        } else if (directories.contains(DocumentFolder) && files.contains(MetaDataFile)) {
            return new FileProfile();
        } else if (directories.size() == 1 && directories.contains(LinkFolder)) {
            return new LinkProfile();
        }

        return null;
    }

    public static StandardProfile generate(ProfileType type){
        switch(type){
            case Document:
                return new FileProfile();

            case Standard:
                return new StandardProfile();

            case Link:
                return new LinkProfile();

            default:
                return null;
        }
    }
}