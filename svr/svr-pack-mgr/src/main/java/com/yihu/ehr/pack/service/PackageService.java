package com.yihu.ehr.pack.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.pack.dao.PackageRepository;
import com.yihu.ehr.pack.entity.Package;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.id.ObjectId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * JSON档案管理器。将档案存到fastDFS中, 并做文件索引
 *
 * @author Air
 * @version 1.0
 * @created 2015.07.09 9:50
 */
@Deprecated
public class PackageService extends BaseJpaService<Package, PackageRepository> {

    @Value("${deploy.region}")
    private Short adminRegion;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private PackageRepository packageRepository;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public Package receive(InputStream is, String pwd, String md5, String orgCode, String clientId) throws Exception {
        Map<String, String> metaData = storeJsonPackage(is);
        return checkIn(metaData.get("id"), metaData.get("path"), pwd, md5, orgCode, clientId);
    }

    public Package getPackage(String id) {
        return packageRepository.findOne(id);
    }

    public InputStream downloadFile(String id) throws Exception {
        Package aPackage = packageRepository.findOne(id);
        if (aPackage == null) return null;

        String file[] = aPackage.getRemotePath().split(":");
        byte[] data = fastDFSUtil.download(file[0], file[1]);

        return new ByteArrayInputStream(data);
    }

    public Package acquirePackage(String id) {
        Package aPackage = null;
        if (StringUtils.isEmpty(id)){
            Pageable pageable = new PageRequest(0, 1);
            List<Package> packages = packageRepository.findEarliestOne(pageable);
            if (packages.size() > 0) {
                aPackage = packages.get(0);
            }
        } else {
            aPackage = packageRepository.findOne(id);
        }

        if (aPackage == null) {
            return null;
        }

        // 设置为'解析'状态
        aPackage.setArchiveStatus(ArchiveStatus.Acquired);
        aPackage.setParseDate(new Date());
        packageRepository.save(aPackage);
        return aPackage;
    }

    /**
     * 将JSON档案保存到特定的路径上。
     *
     * @param is 文件流
     * @return 完整路径
     */
    Map<String, String> storeJsonPackage(InputStream is) throws Exception {
        ObjectId objectId = new ObjectId(adminRegion, BizObject.JsonPackage);

        ObjectNode msg = fastDFSUtil.upload(is, "zip", "健康档案JSON临时文件");
        String group = msg.get(FastDFSUtil.GROUP_NAME).asText();
        String remoteFile = msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();

        // 将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
        String remoteFilePath = String.join(Package.pathSeparator, new String[]{group, remoteFile});

        Map<String, String> metaData = new HashMap<>();
        metaData.put("id", objectId.toString());
        metaData.put("path", remoteFilePath);

        return metaData;
    }

    /**
     * 在数据库待解析的队列中登记.
     *
     * @param path 完整路径
     * @param pwd  zip密码
     * @return 索引存储成功
     */
    private Package checkIn(String id, String path, String pwd, String md5, String orgCode, String clientId) {
        Package aPackage = new Package();
        aPackage.setId(id);
        aPackage.setMd5(md5);
        aPackage.setOrgCode(orgCode);
        aPackage.setClientId(clientId);
        aPackage.setRemotePath(path);
        aPackage.setPwd(pwd);
        aPackage.setReceiveDate(new Date());
        aPackage.setArchiveStatus(ArchiveStatus.Received);
        aPackage.setFailCount(0);
        packageRepository.save(aPackage);
        return aPackage;
    }

    public String downloadTo(String remotePath, String localPath) throws Exception {
        String[] meta = remotePath.split(Package.pathSeparator);
        return fastDFSUtil.download(meta[0], meta[1], localPath);
    }

    public void deletePackage(String id) {
        packageRepository.delete(id);
    }

    public void updateFailPackage(int failCount){
        packageRepository.updateFailPackage(failCount);
    }
}
