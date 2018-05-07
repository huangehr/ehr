package com.yihu.ehr.resolve.service.resource.stage1;

import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.resolve.model.stage1.DataSetPackage;
import com.yihu.ehr.resolve.model.stage1.FilePackage;
import com.yihu.ehr.resolve.model.stage1.LinkPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
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
    public final static String LinkFile = "index";

    /**
     * 读取档案包目录结构判断档案类型。
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
        } else { // 数据集档案包（zip下只有 .json 数据文件）。
            return createPackModel(ProfileType.DataSet);
        }
    }

    public static StandardPackage createPackModel(ProfileType type){
        switch(type){
            case Standard:
                return new StandardPackage();
            case File:
                return new FilePackage();
            case Link:
                return new LinkPackage();
            case DataSet:
                return new DataSetPackage();
            default:
                return null;
        }
    }
}