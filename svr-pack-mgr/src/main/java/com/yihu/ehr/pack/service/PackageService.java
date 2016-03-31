package com.yihu.ehr.pack.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.ObjectId;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
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
public class PackageService {
    @Value("${deploy.region}")
    Short adminRegion;

    @Autowired
    XPackageRepository jsonPackageRepo;

    @Autowired
    FastDFSUtil fastDFSUtil;

    public String receive(InputStream is, String pwd) {
        Map<String, String> metaData = storeJsonPackage(is);
        checkIn(metaData.get("id"), metaData.get("path"), pwd);

        return metaData.get("id");
    }

    public List<Package> getArchiveList(Date from, Date to) {
        return jsonPackageRepo.findAll(from, to);
    }

    public Package getJsonPackage(String id) {
        return jsonPackageRepo.findOne(id);
    }

    public InputStream downloadFile(String id) throws Exception {
        Package aPackage = jsonPackageRepo.findOne(id);
        if (aPackage == null) return null;

        String file[] = aPackage.getRemotePath().split(":");
        byte[] data = fastDFSUtil.download(file[0], file[1]);

        return new ByteArrayInputStream(data);
    }

    public List<Package> searchArchives(Map<String, Object> args, Pageable pageable) throws ParseException {
        Date since = (Date)args.get("since");
        Date to = (Date)args.get("to");
        ArchiveStatus archiveStatus = (ArchiveStatus) args.get("archiveStatus");

        return jsonPackageRepo.findAll(archiveStatus, since, to, pageable);
    }

    public int getArchiveCount(Date from, Date to) {
        return getArchiveList(from, to).size();
    }

    public void acquireArchive(String id) {
        Package aPackage = jsonPackageRepo.findOne(id);

        // 设置为'解析'状态
        aPackage.setArchiveStatus(ArchiveStatus.Acquired);
        aPackage.setParseDate(new Date());

        jsonPackageRepo.save(aPackage);
    }

    public void reportArchiveFinished(String id, String message) {
        Package aPackage = jsonPackageRepo.findOne(id);
        aPackage.setArchiveStatus(ArchiveStatus.Finished);
        aPackage.setMessage(message);
        aPackage.setFinishDate(new Date());

        jsonPackageRepo.save(aPackage);
    }

    public void reportArchiveFailed(String id, String message) {
        Package aPackage = jsonPackageRepo.findOne(id);
        aPackage.setArchiveStatus(ArchiveStatus.Failed);
        aPackage.setMessage(message);
        aPackage.setFinishDate(null);

        jsonPackageRepo.save(aPackage);
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
            String remoteFilePath = String.join(Package.pathSeparator, new String[]{group, remoteFile});

            Map<String, String> metaData = new HashMap<>();
            metaData.put("id", objectId.toString());
            metaData.put("path", remoteFilePath);

            return metaData;
        } catch (Exception e) {
            LogService.getLogger(PackageService.class)
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
            Package aPackage = new Package();
            aPackage.setId(id);
            aPackage.setRemotePath(path);
            aPackage.setPwd(pwd);
            aPackage.setReceiveDate(new Date());
            aPackage.setArchiveStatus(ArchiveStatus.Received);

            jsonPackageRepo.save(aPackage);
            return true;
        } catch (HibernateException ex) {
            LogService.getLogger(PackageService.class).error(ex.getMessage());

            return false;
        }
    }

    public String downloadTo(String remotePath, String localPath) {
        try {
            String[] meta = remotePath.split(Package.pathSeparator);
            return fastDFSUtil.download(meta[0], meta[1], localPath);
        } catch (Exception ex) {
            throw new RuntimeException("fastDFS - " + ex.getMessage());
        }
    }

    public void deletePackage(String id) {
        jsonPackageRepo.delete(id);
    }
}
