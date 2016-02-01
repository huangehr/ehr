package com.yihu.ehr.pack.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.DateFormatter;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.csource.common.MyException;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

/**
 * JSON档案管理器。将档案存到fastDFS中, 并做文件索引
 *
 * @author Air
 * @version 1.0
 * @created 2015.07.09 9:50
 */
@Service
@Transactional
public class JsonPackageService {
    @Value("${admin.region}")
    Short adminRegion = 3502;

    @Autowired
    XJsonPackageRepository jsonPackageRepo;

    @Autowired
    FastDFSUtil fastDFSUtil;

    private List<JsonPackage> packageQueue = new ArrayList<JsonPackage>();

    public String savePackage(InputStream is, String pwd) {
        Map<String, String> metaData = storeJsonPackage(is);
        checkIn(metaData.get("id"), metaData.get("path"), pwd);

        return metaData.get("id");
    }

    public void deletePackage(String id){
        jsonPackageRepo.delete(id);
    }

    public List<JsonPackage> getArchiveList(Date from, Date to) {
        return jsonPackageRepo.findAll(from, to);
    }

    public JsonPackage getPackage(String id) {
        return jsonPackageRepo.findOne(id);
    }

    public List<JsonPackage> search(Date since, Date to, ArchiveStatus archiveStatus, Pageable pageable) {
        return jsonPackageRepo.findAll(archiveStatus, since, to, pageable);
    }

    public int packageCount(Date from, Date to) {
        return getArchiveList(from, to).size();
    }

    public void acquirePackage(String id) {
        JsonPackage jsonPackage = jsonPackageRepo.findOne(id);

        // 设置为'解析'状态
        jsonPackage.setArchiveStatus(ArchiveStatus.Acquired);
        jsonPackage.setParseDate(new Date());

        jsonPackageRepo.save(jsonPackage);
    }

    public void updatePackageStatus(String id, ArchiveStatus status, String message) {
        JsonPackage jsonPackage = jsonPackageRepo.findOne(id);
        jsonPackage.setArchiveStatus(status);
        jsonPackage.setMessage(message);
        jsonPackage.setFinishDate(new Date());

        jsonPackageRepo.save(jsonPackage);
    }

    /**
     * 将JSON档案保存到特定的路径上。
     *
     * @param is 文件流
     * @return 完整路径
     */
    private Map<String, String> storeJsonPackage(InputStream is) {
        ObjectId objectId = new ObjectId(adminRegion, BizObject.JsonPackage);

        try {
            ObjectNode msg = fastDFSUtil.upload(is, "zip", "健康档案JSON文件");
            String group = msg.get(FastDFSUtil.GroupField).asText();
            String remoteFile = msg.get(FastDFSUtil.RemoteFileField).asText();

            // 将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
            String remoteFilePath = String.join(JsonPackage.pathSeparator, new String[]{group, remoteFile});

            Map<String, String> metaData = new HashMap<>();
            metaData.put("id", objectId.toString());
            metaData.put("path", remoteFilePath);

            return metaData;
        } catch (Exception e) {
            LogService.getLogger(JsonPackageService.class)
                    .error("存病人档案文件失败, 错误原因: " + ExceptionUtils.getStackTrace(e));

            return null;
        }
    }

    /**
     * 在数据库待解析的队列中登记.
     *
     * @param path 完整路径
     * @param pwd  zip密码
     * @return 索引存储成功
     */
    private boolean checkIn(String id, String path, String pwd) {
        try {
            JsonPackage jsonPackage = new JsonPackage();
            jsonPackage.setId(id);
            jsonPackage.setRemotePath(path);
            jsonPackage.setPwd(pwd);
            jsonPackage.setReceiveDate(new Date());
            jsonPackage.setArchiveStatus(ArchiveStatus.Received);

            jsonPackageRepo.save(jsonPackage);
            return true;
        } catch (HibernateException ex) {
            LogService.getLogger(JsonPackageService.class).error(ex.getMessage());

            return false;
        }
    }
}
