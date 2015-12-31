package com.yihu.ehr.std.service;

import com.yihu.ehr.constrant.BizObject;
import com.yihu.ehr.data.SQLGeneralDAO;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.compress.Zipper;
import com.yihu.ehr.util.encrypt.DES;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 标准适配方案资源 操作类
 * 将标准资源包存盘,并做文件索引
 *
 * @author Hzy
 * @version 1.0
 * @created 2015.07.24 9:50
 */
@Transactional
@Service
public class SchemaManager extends SQLGeneralDAO {
    @Value("${admin-region}")
    short adminRegion;

    @Value("${standard-svr.schema-storage-path}")
    String schemaStoragePath;

    /**
     * 压缩标准适配xml文件，并保存文件索引
     *
     * @param filePath xml文件，文件夹路径
     * @param version  版本号
     * @param priKey   私钥（用于加密密码）
     *                 数据库中密码以DES加密，传输给客户端以RSA私钥加密
     * @return 档案存储成功
     */
    public Map<String, String> createZip(String filePath, String version, String priKey) {
        Map<String, String> map = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        String pwd = uuid.toString();//密码生成
        try {
            String encryptPwd = RSA.encryptByPriKey(pwd, priKey);//密码加密
            String index = storage(filePath, getStoragePath(), pwd);
            String schemaId = saveIndex(index, pwd, version);
            if (schemaId != null) {
                map.put("sheme_id", schemaId);
                map.put("cryptograph", encryptPwd);
                return map;
            }
        } catch (Exception e) {
            LogService.getLogger(SchemaManager.class).debug(e.getMessage());
        }
        return null;
    }

    /**
     * @return 存储路径
     */
    public String getStoragePath() {
        return schemaStoragePath;
    }

    /**
     * @param xmlFilePath 标准xml文件文件夹路径
     * @param path        保存路径
     * @param pwd         zip解压密码
     * @return 完整路径 ,返回null则压缩失败
     */
    public String storage(String xmlFilePath, String path, String pwd) {
        UUID uuid = UUID.randomUUID();
        Zipper zipper = new Zipper();
        String fileName = uuid.toString();
        String filePath = path + "/" + fileName + ".zip";
        try {
            File fileP = zipper.zipFile(new File(xmlFilePath), filePath, pwd);//TODO  做文件压缩保存操作
            if (fileP == null) {
                LogService.getLogger(SchemaManager.class).error("压缩保存文件失败.");
            }
            return filePath;
        } catch (Exception e) {
            LogService.getLogger(SchemaManager.class).error("存病人档案文件失败.");
        }
        return null;
    }

    /**
     * zip文件 索引存储
     *
     * @param index   完整路径
     * @param pwd     zip密码
     * @param version 版本号
     * @return 索引存储成功
     */
    public String saveIndex(String index, String pwd, String version) {
        String schemaId = null;

        ObjectId objectId = new ObjectId(adminRegion, BizObject.Geography);
        try {
            SchemaList schemaList = new SchemaList();
            schemaList.setId(objectId.toString());
            schemaList.setVersion(version);
            schemaList.setPath(index);
            schemaList.setPwd(DES.encrypt(DES.PASS_WORD, pwd));//DES加密存入数据库的密码
            saveEntity(schemaList);

            schemaId = objectId.toString();
        } catch (Exception e) {
            LogService.getLogger(SchemaManager.class).error(e.getMessage());
        }
        return schemaId;
    }

    /**
     * 通过版本号和sheme_id获取标准适配资源信息
     *
     * @param schemaId
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public SchemaList getSchemaList(String schemaId) {
        return (SchemaList) getHibernateTemplate().get(SchemaList.class, schemaId);
    }

}
