package com.yihu.ehr.resolve.service.resource.stage1;

import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.resolve.model.stage1.*;
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
    public final static String DocumentsFile = "documents.json";
    public final static String LinkFile = "index";

    /**
     * 读取档案包目录结构判断档案类型。
     * @param root
     * @return
     */
    public static OriginalPackage createPackModel(File root, EsSimplePackage esSimplePackage) {
        List<String> directories = CollectionUtils.arrayToList(root.list());
        if (directories.contains(StandardFolder) && directories.contains(OriginFolder)) {
            return createPackModel(ProfileType.Standard, esSimplePackage);
        } else if (directories.contains(DocumentsFile)) {
            return createPackModel(ProfileType.File, esSimplePackage);
        } else if (directories.size() == 1 && directories.contains(LinkFile)) {
            return createPackModel(ProfileType.Link, esSimplePackage);
        } else { // 数据集档案包（zip下只有 .json 数据文件）。
            return createPackModel(ProfileType.Simple, esSimplePackage);
        }
    }

    public static OriginalPackage createPackModel(ProfileType type, EsSimplePackage esSimplePackage){
        switch(type){
            case Standard:
                return new StandardPackage(esSimplePackage.get_id(), esSimplePackage.getReceive_date());
            case File:
                return new FilePackage(esSimplePackage.get_id(), esSimplePackage.getReceive_date());
            case Link:
                return new LinkPackage(esSimplePackage.get_id(), esSimplePackage.getReceive_date());
            case Simple:
                return new SimplePackage(esSimplePackage.get_id(), esSimplePackage.getReceive_date());
            default:
                return null;
        }
    }
}