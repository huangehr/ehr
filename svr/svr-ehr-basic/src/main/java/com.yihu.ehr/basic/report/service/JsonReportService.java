package com.yihu.ehr.basic.report.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.basic.report.dao.XJsonReportRepository;
import com.yihu.ehr.entity.report.JsonReport;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
@Transactional
public class JsonReportService extends BaseJpaService<JsonReport, XJsonReportRepository> {

    @Value("${deploy.region}")
    Short adminRegion;

    @Autowired
    FastDFSUtil fastDFSUtil;

    public JsonReport receive(InputStream is, String pwd, String encryptPwd,String md5, String orgCode,int type) {
        String remotePath = storeJsonJsonReport(is);
        return checkIn(remotePath, pwd,encryptPwd, md5, orgCode,type);
    }

    public JsonReport getJsonReport(Integer id) {
        return getRepo().findOne(id);
    }

    public InputStream downloadFile(Integer id) throws Exception {
        JsonReport aJsonReport = getRepo().findOne(id);
        if (aJsonReport == null) return null;
        String file[] = aJsonReport.getRemotePath().split(":");
        byte[] data = fastDFSUtil.download(file[0], file[1]);
        return new ByteArrayInputStream(data);
    }

    /**
     * 将JSON档案保存到特定的路径上。
     *
     * @param is 文件流
     * @return 完整路径
     */
     String storeJsonJsonReport(InputStream is) {
        try {
            ObjectNode msg = fastDFSUtil.upload(is, "zip", "质控包JSON临时文件");
            String group = msg.get(FastDFSUtil.GROUP_NAME).asText();
            String remoteFile = msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
            // 将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
            String remoteFilePath = String.join(JsonReport.pathSeparator, new String[]{group, remoteFile});
            return remoteFilePath;
        } catch (Exception e) {
            LogService.getLogger(JsonReportService.class).error("质控包文件失败, 错误原因: " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    /**
     * 在数据库待解析中登记.
     *
     * @param path 完整路径
     * @param pwd  zip密码
     * @return
     */
    JsonReport checkIn(String path, String pwd,String encryptPwd, String md5, String orgCode,int type) {
        try {
            JsonReport aJsonReport = new JsonReport();
            aJsonReport.setMd5(md5);
            aJsonReport.setEncryptPwd(encryptPwd);
            aJsonReport.setOrgCode(orgCode);
            aJsonReport.setRemotePath(path);
            aJsonReport.setPwd(pwd);
            aJsonReport.setReceiveDate(new Date());
            aJsonReport.setStatus(0);
            aJsonReport.setType(type);
            getRepo().save(aJsonReport);
            return aJsonReport;
        } catch (HibernateException ex) {
            LogService.getLogger(JsonReportService.class).error(ex.getMessage());
            return null;
        }
    }

    //更新解析状态 type 1 解析 2 统计
    public JsonReport updateJsonReport(JsonReport aJsonReport) {
        getRepo().save(aJsonReport);
        return aJsonReport;
    }

    public void deleteJsonReport(Integer id) {
        getRepo().delete(id);
    }

    public String downloadTo(String remotePath, String localPath) {
        try {
            String[] meta = remotePath.split(JsonReport.pathSeparator);
            return fastDFSUtil.download(meta[0], meta[1], localPath);
        } catch (Exception ex) {
            throw new RuntimeException("fastDFS - " + ex.getMessage());
        }
    }

    
    private XJsonReportRepository getRepo(){
        return (XJsonReportRepository)getRepository();
    }


    public List<Object> getStatistOrgData(Date statistsDate) {
        Session session = currentSession();
        String hql = "select distinct(jr.orgCode) from JsonReport jr where  jr.status=1 and  TO_DAYS(:statistsDate) - TO_DAYS(jr.receiveDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setDate("statistsDate", statistsDate);
        List<Object> list = query.list();
        return list;
    }

    public List<JsonReport> getJsonReportData(Date statistsDate) {
        Session session = currentSession();
        String hql = "select jr from JsonReport jr where  jr.status=1 and  TO_DAYS(:statistsDate) - TO_DAYS(jr.receiveDate) = 0 ";
        Query query = session.createQuery(hql);
        query.setDate("statistsDate", statistsDate);
        List<JsonReport> list = query.list();
        return list;
    }

}
