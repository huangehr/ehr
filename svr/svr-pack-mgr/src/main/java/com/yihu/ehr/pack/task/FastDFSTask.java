package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

@Component
public class FastDFSTask {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Autowired
    protected ObjectMapper objectMapper;
    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";
    private static final Logger logger = LoggerFactory.getLogger(FastDFSTask.class);

    /**
     * 异步接收档案,存入fast dfs
     * @return
     */
    @Async
    public Future<Boolean> savePackageWithOrg(MultipartFile pack, String password, String orgCode, String md5, String clientId, Integer packType) throws Exception {
        logger.info("正在存入fast dfs");
        long t1 = System.currentTimeMillis();
        //fastDfs
        ObjectNode msg = fastDFSUtil.upload(pack.getInputStream(), "zip", "健康档案JSON文件");
        String group = msg.get(FastDFSUtil.GROUP_NAME).asText();
        String remoteFile = msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
        //将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
        String remoteFilePath = String.join(EsDetailsPackage.PATH_SEPARATOR, new String[]{group, remoteFile});
        //elasticSearch
        Date now = new Date();
        String _now = dateFormat.format(now);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("pwd", password);
        sourceMap.put("remote_path", remoteFilePath);
        sourceMap.put("receive_date", _now);
        sourceMap.put("archive_status", 0);
        sourceMap.put("org_code", orgCode);
        sourceMap.put("client_id", clientId);
        sourceMap.put("resourced", 0);
        sourceMap.put("md5_value", md5);
        sourceMap.put("fail_count", 0);
        sourceMap.put("analyze_status", 0);
        sourceMap.put("analyze_fail_count", 0);
        sourceMap.put("pack_type", packType);
        //保存索引出错的时候，删除文件
        try {
            sourceMap = elasticSearchUtil.index(INDEX, TYPE, sourceMap);
        } catch (Exception e) {
            fastDFSUtil.delete(group, remoteFile);
            throw e;
        }
        EsSimplePackage esSimplePackage = new EsSimplePackage();
        esSimplePackage.set_id((sourceMap.get("_id").toString()));
        esSimplePackage.setPwd(password);
        esSimplePackage.setReceive_date(now);
        esSimplePackage.setRemote_path(remoteFilePath);
        esSimplePackage.setClient_id(clientId);
        redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
        long t2 = System.currentTimeMillis();
        long t = t2 - t1;
        logger.info("耗时:" + t);
        logger.info("保存文件至fast dfs成功");
        return new AsyncResult<>(true);
    }
}
