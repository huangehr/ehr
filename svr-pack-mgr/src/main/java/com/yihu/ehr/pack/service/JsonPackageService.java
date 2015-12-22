package com.yihu.ehr.pack.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constrant.ArchiveStatus;
import com.yihu.ehr.constrant.BizObject;
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
    @Value("${admin-region}")
    Short adminRegion;

    @Autowired
    XJsonPackageRepository jsonPackageRepo;

    @Autowired
    FastDFSUtil fastDFSUtil;

    private List<JsonPackage> packageQueue = new ArrayList<JsonPackage>();

    public String receive(InputStream is, String pwd) {
        Map<String, String> metaData = storeJsonPackage(is);
        checkIn(metaData.get("id"), metaData.get("path"), pwd);

        return metaData.get("id");
    }

    public List<JsonPackage> getArchiveList(Date from, Date to) {
        return jsonPackageRepo.findAll(from, to);
    }

    public JsonPackage getJsonPackage(String id) {
        return jsonPackageRepo.findOne(id);
    }

    public List<JsonPackage> searchArchives(Map<String, Object> args, Pageable pageable) throws ParseException {

        ArchiveStatus archiveStatus = (ArchiveStatus) args.get("archiveStatus");
        Date from = DateFormatter.simpleDateTimeParse((String) args.get("fromTime"));
        Date to = DateFormatter.simpleDateTimeParse((String) args.get("toTime"));

        return jsonPackageRepo.findAll(archiveStatus, from, to, pageable);
    }

    public int getArchiveCount(Date from, Date to) {
        return getArchiveList(from, to).size();
    }

    public void acquireArchive(String id) {
        JsonPackage jsonPackage = jsonPackageRepo.findOne(id);

        // 设置为'解析'状态
        jsonPackage.setArchiveStatus(ArchiveStatus.Acquired);
        jsonPackage.setParseDate(new Date());

        jsonPackageRepo.save(jsonPackage);
    }

    public void reportArchiveFinished(String id, String message) {
        JsonPackage jsonPackage = jsonPackageRepo.findOne(id);
        jsonPackage.setArchiveStatus(ArchiveStatus.Finished);
        jsonPackage.setMessage(message);
        jsonPackage.setFinishDate(new Date());

        jsonPackageRepo.save(jsonPackage);
    }

    public void reportArchiveFailed(String id, String message) {
        JsonPackage jsonPackage = jsonPackageRepo.findOne(id);
        jsonPackage.setArchiveStatus(ArchiveStatus.Failed);
        jsonPackage.setMessage(message);
        jsonPackage.setFinishDate(null);

        jsonPackageRepo.save(jsonPackage);
    }

    /**
     * 将JSON档案保存到特定的路径上。
     *
     * @param is 文件流
     * @return 完整路径
     */
    Map<String, String> storeJsonPackage(InputStream is) {
        ObjectId objectId = new ObjectId(adminRegion, BizObject.JsonPackage);

        try {
            ObjectNode msg = fastDFSUtil.upload(is, "zip", "健康档案JSON临时文件");
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
    boolean checkIn(String id, String path, String pwd) {
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

    public String downloadTo(String remotePath, String localPath) {
        try {
            String[] meta = remotePath.split(JsonPackage.pathSeparator);
            return fastDFSUtil.download(meta[0], meta[1], localPath);
        } catch (IOException | MyException ex) {
            throw new RuntimeException("fastDFS - " + ex.getMessage());
        }
    }
}
