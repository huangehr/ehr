package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.pack.feign.SecurityClient;
import com.yihu.ehr.pack.entity.Package;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 档案包控制器。
 *
 * @author Sand
 * @version 1.0
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackageEndPoint", description = "档案包", tags = {"档案包服务-档案包"})
public class PackageEndPoint extends EnvelopRestEndPoint {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";

    @Value("${deploy.region}")
    private Short adminRegion;

    @Autowired
    private SecurityClient securityClient;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public boolean savePackageWithOrg (
            @ApiParam(name = "pack", value = "档案包", allowMultiple = true)
            @RequestPart() MultipartFile pack,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            HttpServletRequest request) throws Exception {

        MKey key = securityClient.getOrgKey(orgCode);
        if (key == null || key.getPrivateKey() == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
        }
        String password;
        try {
            password = RSA.decrypt(packageCrypto, RSA.genPrivateKey(key.getPrivateKey()));
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "javax.crypto.BadPaddingException." + ex.getMessage());
        }
        //fastDfs
        ObjectNode msg = fastDFSUtil.upload(pack.getInputStream(), "zip", "健康档案JSON文件");
        String group = msg.get(FastDFSUtil.GROUP_NAME).asText();
        String remoteFile = msg.get(FastDFSUtil.REMOTE_FILE_NAME).asText();
        //将组与文件ID使用英文分号隔开, 提取的时候, 只需要将它们这个串拆开, 就可以得到组与文件ID
        String remoteFilePath = String.join(Package.pathSeparator, new String[]{group, remoteFile});
        //elasticSearch
        Date now = new Date();
        String nowStr = dateFormat.format(now);
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("pwd", password);
        sourceMap.put("remote_path", remoteFilePath);
        sourceMap.put("receive_date", nowStr);
        sourceMap.put("parse_date", null);
        sourceMap.put("finish_date", null);
        sourceMap.put("archive_status", 0);
        sourceMap.put("message", null);
        sourceMap.put("org_code", orgCode);
        sourceMap.put("client_id", getClientId(request));
        sourceMap.put("resourced", 0);
        sourceMap.put("md5_value", md5);
        sourceMap.put("event_type", null);
        sourceMap.put("event_no", null);
        sourceMap.put("event_date", null);
        sourceMap.put("patient_id", null);
        sourceMap.put("fail_count", 0);
        sourceMap.put("analyze_status", 0);
        sourceMap.put("analyze_fail_count", 0);
        sourceMap.put("analyze_date", null);
        //保存索引出错的时候，删除文件
        try {
            sourceMap = elasticSearchUtil.index(INDEX, TYPE, sourceMap);
        } catch (Exception e) {
            fastDFSUtil.delete(group, remoteFile);
            throw e;
        }
        String _id = (String) sourceMap.get("_id");
        EsSimplePackage esSimplePackage = new EsSimplePackage();
        esSimplePackage.set_id(_id);
        esSimplePackage.setPwd(password);
        esSimplePackage.setReceive_date(now);
        esSimplePackage.setRemote_path(remoteFilePath);
        esSimplePackage.setClient_id(getClientId(request));
        redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
        return true;
        //messageBuffer.putMessage(convertToModel(aPackage, MPackage.class));
    }

    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除档案包", notes = "每次删除一万条记录")
    public boolean deletePackages(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, 1, 10000);
        for (Map<String, Object> temp : resultList) {
            String [] tokens =  String.valueOf(temp.get("remote_path")).split(":");
            fastDFSUtil.delete(tokens[0], tokens[1]);
            elasticSearchUtil.delete(INDEX, TYPE, new String[]{String.valueOf(temp.get("_id"))});
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除单个档案包", notes = "删除一个数据包")
    public boolean deletePackage (
            @ApiParam(name = "id", value = "档案包编号")
            @PathVariable(value = "id") String id) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (null == source) {
            return false;
        }
        String [] tokens =  String.valueOf(source.get("remote_path")).split(":");
        fastDFSUtil.delete(tokens[0], tokens[1]);
        elasticSearchUtil.delete(INDEX, TYPE, new String[]{id});
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Package, method = {RequestMethod.PUT})
    @ApiOperation(value = "修改档案包状态", notes = "修改档案包状态")
    public boolean reportStatus(
            @ApiParam(name = "id", value = "档案包编号", required = true)
            @PathVariable(value = "id") String id,
            @ApiParam(name = "status", value = "状态", required = true)
            @RequestParam(value = "status") ArchiveStatus status,
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) throws Exception {
        Map<String, Object> sourceMap = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (null == sourceMap) {
            return false;
        }
        Map<String, Object> updateSource = new HashMap<>();
        updateSource.put("resourced", 1);
        updateSource.put("message", message);
        if (status == ArchiveStatus.Finished) {
            //入库成功
            Map<String, String> map = objectMapper.readValue(message, Map.class);
            updateSource.put("event_type", map.get("event_type"));
            updateSource.put("event_no", map.get("event_no"));
            updateSource.put("event_date", map.get("event_date"));
            updateSource.put("patient_id", map.get("patient_id"));
            updateSource.put("finish_date", dateFormat.format(new Date()));
        } else if (status == ArchiveStatus.Acquired) {
            //入库执行时间
            updateSource.put("parse_date", dateFormat.format(new Date()));
        } else {
            updateSource.put("finish_date", null);
            if (message.endsWith("do not deal with fail-tolerant.")) {
                updateSource.put("fail_count", 3);
            } else {
                if ((int)sourceMap.get("fail_count") < 3) {
                    int failCount = (int)sourceMap.get("fail_count");
                    failCount ++;
                    updateSource.put("fail_count", failCount);
                }
            }
        }
        updateSource.put("archive_status", status.ordinal());
        elasticSearchUtil.update(INDEX, TYPE, id, updateSource);
        return true;
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.Status, method = RequestMethod.PUT)
    @ApiOperation(value = "更新档案包分析状态", notes = "更新档案包分析状态")
    public boolean analyzeStatus(
            @ApiParam(value = "档案包编号")
            @PathVariable(value = "id") String id,
            @ApiParam(value = "分析状态")
            @RequestParam(value = "status") Integer status) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (null == source) {
            return false;
        }
        Map<String, Object> updateSource = new HashMap<>();
        //出错时记录错误次数
        if (status == 2) {
            updateSource.put("analyze_fail_count", (int)source.get("analyze_fail_count") + 1);
        } else {
            updateSource.put("analyze_fail_count", 0); //避免手动修改状态后无法解析
        }
        updateSource.put("analyze_status", status);
        elasticSearchUtil.update(INDEX, TYPE, id, updateSource);
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.GET)
    @ApiOperation(value = "搜索档案包")
    public Envelop page(
            @ApiParam(name = "filters", value = "过滤条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "page", value = "页码", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, page, size);
        int count = (int)elasticSearchUtil.count(INDEX, TYPE, filters);
        Envelop envelop = getPageResult(resultList, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Packages.PackageSearch, method = RequestMethod.GET)
    @ApiOperation(value = "搜索档案包")
    public List<EsDetailsPackage> search (
            @ApiParam(name = "filters", value = "过滤条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "分页大小", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, sorts, page, size);
        List<EsDetailsPackage> esDetailsPackages = new ArrayList<>();
        for (Map<String, Object> temp : resultList) {
            esDetailsPackages.add(objectMapper.readValue(objectMapper.writeValueAsString(temp), EsDetailsPackage.class));
        }
        return esDetailsPackages;
    }

    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息")
    public EsSimplePackage getPackage(
            @ApiParam(name = "id", value = "档案包编号")
            @PathVariable(value = "id") String id) throws IOException {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (source != null) {
            EsSimplePackage esSimplePackage = new EsSimplePackage();
            esSimplePackage.set_id(String.valueOf(source.get("_id")));
            esSimplePackage.setPwd(String.valueOf(source.get("pwd")));
            esSimplePackage.setRemote_path(String.valueOf(source.get("remote_path")));
            esSimplePackage.setClient_id(String.valueOf(source.get("client_id")));
            return esSimplePackage;
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.Packages.AcquirePackage, method = RequestMethod.GET)
    @ApiOperation(value = "处理档案包(更新状态)")
    public EsDetailsPackage acquirePackage(
            @ApiParam(name = "id", value = "档案包编号")
            @RequestParam(value = "id") String id) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (null == source) {
            return null;
        }
        Map<String, Object> updateSource = new HashMap<>();
        updateSource.put("resourced", 1);
        updateSource.put("message", "正在入库中");
        updateSource.put("parse_date", dateFormat.format(new Date()));
        updateSource.put("archive_status", 1);
        source = elasticSearchUtil.update(INDEX, TYPE, String.valueOf(source.get("_id")), updateSource);
        return objectMapper.readValue(objectMapper.writeValueAsString(source), EsDetailsPackage.class);
    }

    @RequestMapping(value = ServiceApi.Packages.PackageDownload, method = {RequestMethod.GET})
    @ApiOperation(value = "下载档案包", notes = "下载档案包")
    public ResponseEntity downloadPackage(
            @ApiParam(name = "id", value = "档案包编号")
            @PathVariable(value = "id") String id,
            HttpServletResponse response) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (source != null) {
            String [] tokens =  String.valueOf(source.get("remote_path")).split(":");
            byte [] data = fastDFSUtil.download(tokens[0], tokens[1]);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + id + ".zip");
            IOUtils.copy(new ByteArrayInputStream(data), response.getOutputStream());
            response.flushBuffer();
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = ServiceApi.Packages.PackageCrypto, method = RequestMethod.POST)
    @ApiOperation(value = "档案包密码加密")
    public String getPackageCrypto(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto) throws Exception {
        MKey key = securityClient.getOrgKey(orgCode);
        if (key == null || key.getPublicKey() == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
        }
        return RSA.encrypt(packageCrypto, RSA.genPublicKey(key.getPublicKey()));
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.POST)
    @ApiOperation(value = "添加解析队列", notes = "队列中无消息的时候才可添加状态为Received或者Acquired的数据")
    public String resolveQueue(
            @ApiParam(name = "status", value = "(Received || Acquired || Failed || Finished)", required = true)
            @RequestParam(value = "status") ArchiveStatus status,
            @ApiParam(name = "sorts", value = "排序(建议使用默认值，以解析较早之前的数据)", defaultValue = "receive_date asc")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "count", value = "数量（不要超过10000）", required = true, defaultValue = "500")
            @RequestParam(value = "count") int count) throws Exception {
        if (status == ArchiveStatus.Received || status == ArchiveStatus.Acquired) {
            if (redisTemplate.opsForList().size(RedisCollection.PackageList) > 0) {
                return "添加失败，队列中存在消息！";
            } else {
                List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "archive_status=" + status.ordinal(), sorts, 1, count);
                for (Map<String, Object> item : resultList) {
                    EsSimplePackage esSimplePackage = new EsSimplePackage();
                    esSimplePackage.set_id(String.valueOf(item.get("_id")));
                    esSimplePackage.setPwd(String.valueOf(item.get("pwd")));
                    esSimplePackage.setRemote_path(String.valueOf(item.get("remote_path")));
                    esSimplePackage.setClient_id(String.valueOf(item.get("client_id")));
                    redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
                }
            }
        } else {
            List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "archive_status=" + status.ordinal(), sorts, 1, count);
            for (Map<String, Object> item : resultList) {
                Map<String, Object> updateSource = new HashMap<>();
                updateSource.put("archive_status", 0);
                elasticSearchUtil.update(INDEX, TYPE, String.valueOf(item.get("_id")), updateSource);
                EsSimplePackage esSimplePackage = new EsSimplePackage();
                esSimplePackage.set_id(String.valueOf(item.get("_id")));
                esSimplePackage.setPwd(String.valueOf(item.get("pwd")));
                esSimplePackage.setRemote_path(String.valueOf(item.get("remote_path")));
                esSimplePackage.setClient_id(String.valueOf(item.get("client_id")));
                redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
            }
        }
        return "操作成功！";
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前解析队列数")
    public long queueSize() throws Exception {
        return redisTemplate.opsForList().size(RedisCollection.PackageList);
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除当前解析队列")
    public boolean deleteQueue() throws Exception {
        redisTemplate.delete(RedisCollection.PackageList);
        return true;
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.Queue, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案包分析消息队列数")
    public long analyzeQueueSize() throws Exception {
        return redisTemplate.opsForList().size(RedisCollection.AnalyzeQueue);
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.Queue, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案包分析消息队列")
    public boolean deleteAnalyzeQueue() throws Exception {
        redisTemplate.delete(RedisCollection.AnalyzeQueue);
        return true;
    }

    //-------------------------------------------------

}
