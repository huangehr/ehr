package com.yihu.ehr.pack.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.pack.dao.XDatasetPackageRepository;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.id.ObjectId;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容非病人维度
 * JSON档案管理器。将档案存到fastDFS中, 并做文件索引
 *
 * @author HZY
 * @version 1.0
 * @created 2017.06.26 9:50
 */
@Service
@Transactional
public class DatasetPackageService extends BaseJpaService<DatasetPackage, XDatasetPackageRepository> {
    @Value("${deploy.region}")
    private Short adminRegion;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private XDatasetPackageRepository datasetPackageRepository;


    public DatasetPackage receiveDatasets(InputStream is, String pwd, String md5, String orgCode, String clientId) {
        Map<String, String> metaData = storeJsonPackage(is);
        return checkDatasetIn(metaData.get("id"), metaData.get("path"), pwd, md5, orgCode, clientId);
    }

    public DatasetPackage getPackage(String id) {
        return getRepo().findOne(id);
    }


    public DatasetPackage acquirePackage(String id) {
        DatasetPackage aPackage = null;
        if (StringUtils.isEmpty(id)){
            Pageable pageable = new PageRequest(0, 1);
            List<DatasetPackage> packages = getRepo().findEarliestOne(pageable);

            if(packages.size() > 0) aPackage = packages.get(0);
        } else {
            aPackage = getRepo().findOne(id);
        }

        if(aPackage == null) return null;

        // 设置为'解析'状态
        aPackage.setArchiveStatus(ArchiveStatus.Acquired);
        aPackage.setParseDate(new Date());

        getRepo().save(aPackage);

        return aPackage;
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
            String group = msg.get(FastDFSUtil.GROUP_NAME).asText();
            String remoteFile = msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();

            // 将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
            String remoteFilePath = String.join(Package.pathSeparator, new String[]{group, remoteFile});

            Map<String, String> metaData = new HashMap<>();
            metaData.put("id", objectId.toString());
            metaData.put("path", remoteFilePath);

            return metaData;
        } catch (Exception e) {
            LogService.getLogger(DatasetPackageService.class)
                    .error("存病人档案文件失败, 错误原因: " + ExceptionUtils.getStackTrace(e));

            return null;
        }
    }


    /**
     * 在数据库待解析的队列中登记. （新）（兼容非病人维度采集）
     * add in 2017/06/22 by HZY
     * @param path 完整路径
     * @param pwd  zip密码
     * @return 索引存储成功
     */
    DatasetPackage checkDatasetIn(String id, String path, String pwd, String md5, String orgCode, String clientId) {
        try {
            DatasetPackage aPackage = new DatasetPackage();
            aPackage.setId(id);
            aPackage.setMd5(md5);
            aPackage.setOrgCode(orgCode);
            aPackage.setClientId(clientId);
            aPackage.setRemotePath(path);
            aPackage.setPwd(pwd);
            aPackage.setReceiveDate(new Date());
            aPackage.setArchiveStatus(ArchiveStatus.Received);
            datasetPackageRepository.save(aPackage);

            return aPackage;
        } catch (HibernateException ex) {
            LogService.getLogger(DatasetPackageService.class).error(ex.getMessage());

            return null;
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
        getRepo().delete(id);
    }
    
    private XDatasetPackageRepository getRepo(){
        return (XDatasetPackageRepository)getRepository();
    }
}
