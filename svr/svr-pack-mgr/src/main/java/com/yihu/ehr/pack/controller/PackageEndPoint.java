package com.yihu.ehr.pack.controller;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.pack.feign.SecurityClient;
import com.yihu.ehr.pack.task.FastDFSTask;
import com.yihu.ehr.profile.AnalyzeStatus;
import com.yihu.ehr.profile.ArchiveStatus;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PackageEndPoint.class);

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
    @Autowired
    private FastDFSTask fastDFSTask;
   /* @Autowired
    private JsonArchivesService jsonArchivesService;*/

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
            @ApiParam(name = "packType", value = "包类型 默认为1(结构化) 1结构化档案，2文件档案，3链接档案，4数据集档案")
            @RequestParam(value = "packType", required = false) Integer packType,
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
        String clientId = getClientId(request);
        if (packType == null){
            packType = 1;
        }
        //更改成异步--->>防止大文件接收,导致阻塞,超时等问题
        fastDFSTask.savePackageWithOrg(pack.getInputStream(), password, orgCode, md5, clientId, packType);
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除档案包", notes = "每次删除一万条记录")
    public boolean deletePackages(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "count", value = "删除数量", required = true, defaultValue = "10000")
            @RequestParam(value = "count") Integer count) throws Exception {
        if (count > 10000) {
            count = 10000;
        }
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, 1, count);
        List<String> idList = new ArrayList<>();
        for (Map<String, Object> temp : resultList) {
            String [] tokens =  String.valueOf(temp.get("remote_path")).split(":");
            fastDFSUtil.delete(tokens[0], tokens[1]);
            idList.add(String.valueOf(temp.get("_id")));
        }
        if (idList.size() > 0) {
            String [] _id = new String[idList.size()];
            elasticSearchUtil.bulkDelete(INDEX, TYPE, idList.toArray(_id));
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
        elasticSearchUtil.delete(INDEX, TYPE, id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Package, method = {RequestMethod.PUT})
    @ApiOperation(value = "修改档案包状态", notes = "修改档案包状态")
    public boolean reportStatus(
            @ApiParam(name = "id", value = "档案包编号", required = true)
            @PathVariable(value = "id") String id,
            @ApiParam(name = "status", value = "状态", required = true)
            @RequestParam(value = "status") ArchiveStatus status,
            @ApiParam(name = "errorTye", value = "错误类型(0 = 正常; -1 = 质控服务出错; -2 解析服务出错; 1 = 压缩包有误; 2 = Json文件有误; 3 = Json数据有误)", required = true)
            @RequestParam(value = "errorTye") int errorTye,
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) throws Exception {
        Map<String, Object> updateSource = new HashMap<>();
        if (status == ArchiveStatus.Finished) {
            //入库成功
            Map<String, String> map = objectMapper.readValue(message, Map.class);
            updateSource.put("event_type", map.get("event_type"));
            updateSource.put("event_no", map.get("event_no"));
            updateSource.put("event_date", map.get("event_date"));
            updateSource.put("patient_id", map.get("patient_id"));
            updateSource.put("demographic_id", map.get("demographic_id"));
            updateSource.put("re_upload_flg", map.get("re_upload_flg"));
            updateSource.put("profile_id", map.get("profile_id"));
            updateSource.put("finish_date", dateFormat.format(new Date()));
            updateSource.put("message", "success");
        } else if (status == ArchiveStatus.Acquired) {
            //开始入库
            updateSource.put("parse_date", dateFormat.format(new Date()));
            updateSource.put("message", message);
        } else {
            //入库失败
            updateSource.put("finish_date", null);
            if (3 == errorTye) {
                updateSource.put("fail_count", 3);
            } else {
                Map<String, Object> sourceMap = elasticSearchUtil.findById(INDEX, TYPE, id);
                if (null == sourceMap) {
                    return false;
                }
                if ((int)sourceMap.get("fail_count") < 3) {
                    int failCount = (int)sourceMap.get("fail_count");
                    updateSource.put("fail_count", failCount + 1);
                }
            }
            updateSource.put("message", message);
        }
        updateSource.put("resourced", 1);
        updateSource.put("error_type", errorTye);
        updateSource.put("archive_status", status.ordinal());
        elasticSearchUtil.voidUpdate(INDEX, TYPE, id, updateSource);
        return true;
    }

    @RequestMapping(value = ServiceApi.PackageAnalyzer.Status, method = RequestMethod.PUT)
    @ApiOperation(value = "更新档案包分析状态", notes = "更新档案包分析状态")
    public boolean analyzeStatus(
            @ApiParam(name = "id", value = "档案包编号", required = true)
            @PathVariable(value = "id") String id,
            @ApiParam(name = "status", value = "状态", required = true)
            @RequestParam(value = "status") AnalyzeStatus status,
            @ApiParam(name = "errorTye", value = "错误类型(0 = 正常; -1 = 质控服务出错; -2 解析服务出错; 1 = 压缩包有误; 2 = Json文件有误; 3 = Json数据有误)", required = true)
            @RequestParam(value = "errorTye") int errorTye,
            @ApiParam(name = "message", value = "消息", required = true)
            @RequestBody String message) throws Exception {
        Map<String, Object> updateSource = new HashMap<>();
        if (status == AnalyzeStatus.Failed) {
            if (3 == errorTye) {
                updateSource.put("analyze_fail_count", 3);
            } else {
                Map<String, Object> sourceMap = elasticSearchUtil.findById(INDEX, TYPE, id);
                if (null == sourceMap) {
                    return false;
                }
                if ((int)sourceMap.get("analyze_fail_count") < 3) {
                    int failCount = (int)sourceMap.get("analyze_fail_count");
                    updateSource.put("analyze_fail_count", failCount + 1);
                }
            }
            updateSource.put("message", message);
        }
        updateSource.put("error_type", errorTye);
        updateSource.put("analyze_status", status.ordinal());
        elasticSearchUtil.voidUpdate(INDEX, TYPE, id, updateSource);
        return true;
    }

    @RequestMapping(value = ServiceApi.Packages.Update, method = RequestMethod.PUT)
    @ApiOperation(value = "根据条件批量修改档案包状态", notes = "修改档案包状态")
    public Integer update(
            @ApiParam(name = "filters", value = "条件", required = true)
            @RequestParam(value = "filters") String filters,
            @ApiParam(name = "status", value = "状态", required = true)
            @RequestParam(value = "status") ArchiveStatus status,
            @ApiParam(name = "page", value = "消息", required = true)
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "size", value = "状态", required = true)
            @RequestParam(value = "size") Integer size) throws Exception {
        List<Map<String, Object>> sourceList = elasticSearchUtil.page(INDEX, TYPE, filters, page, size);
        List<Map<String, Object>> updateSourceList = new ArrayList<>(sourceList.size());
        final int _status = status.ordinal();
        sourceList.forEach(item -> {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", item.get("_id"));
            updateSource.put("archive_status", status.ordinal());
            if (_status == 2) {
                updateSource.put("fail_count", 3);
            } else {
                updateSource.put("fail_count", 0);
            }
            updateSourceList.add(updateSource);
        });
        elasticSearchUtil.bulkUpdate(INDEX, TYPE, updateSourceList);
        return sourceList.size();
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
            @PathVariable(value = "id") String id) throws Exception {
        Map<String, Object> source = elasticSearchUtil.findById(INDEX, TYPE, id);
        if (source != null) {
            EsSimplePackage esSimplePackage = new EsSimplePackage();
            esSimplePackage.set_id(String.valueOf(source.get("_id")));
            esSimplePackage.setPwd(String.valueOf(source.get("pwd")));
            esSimplePackage.setRemote_path(String.valueOf(source.get("remote_path")));
            esSimplePackage.setClient_id(String.valueOf(source.get("client_id")));
            esSimplePackage.setReceive_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(String.valueOf(source.get("receive_date"))));
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
        updateSource.put("parse_date", dateFormat.format(new Date()));
        updateSource.put("message", "正在入库中");
        updateSource.put("resourced", 1);
        updateSource.put("error_type", 0);
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
        return null;
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
            if (redisTemplate.opsForList().size(RedisCollection.ResolveQueue) > 0) {
                return "添加失败，队列中存在消息！";
            } else {
                List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "archive_status=" + status.ordinal(), sorts, 1, count);
                for (Map<String, Object> item : resultList) {
                    EsSimplePackage esSimplePackage = new EsSimplePackage();
                    esSimplePackage.set_id(String.valueOf(item.get("_id")));
                    esSimplePackage.setPwd(String.valueOf(item.get("pwd")));
                    esSimplePackage.setRemote_path(String.valueOf(item.get("remote_path")));
                    esSimplePackage.setClient_id(String.valueOf(item.get("client_id")));
                    redisTemplate.opsForList().leftPush(RedisCollection.ResolveQueue, objectMapper.writeValueAsString(esSimplePackage));
                }
            }
        } else {
            List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "archive_status=" + status.ordinal(), sorts, 1, count);
            List<Map<String, Object>> updateSourceList = new ArrayList<>(resultList.size());
            for (Map<String, Object> item : resultList) {
                Map<String, Object> updateSource = new HashMap<>();
                updateSource.put("archive_status", 0);
                updateSource.put("_id", item.get("_id"));
                updateSourceList.add(updateSource);
                EsSimplePackage esSimplePackage = new EsSimplePackage();
                esSimplePackage.set_id(String.valueOf(item.get("_id")));
                esSimplePackage.setPwd(String.valueOf(item.get("pwd")));
                esSimplePackage.setRemote_path(String.valueOf(item.get("remote_path")));
                esSimplePackage.setClient_id(String.valueOf(item.get("client_id")));
                redisTemplate.opsForList().leftPush(RedisCollection.ResolveQueue, objectMapper.writeValueAsString(esSimplePackage));
            }
            elasticSearchUtil.bulkUpdate(INDEX, TYPE, updateSourceList);
        }
        return "操作成功！";
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前解析队列数")
    public long queueSize() throws Exception {
        return redisTemplate.opsForList().size(RedisCollection.ResolveQueue);
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除当前解析队列")
    public boolean deleteQueue() throws Exception {
        redisTemplate.delete(RedisCollection.ResolveQueue);
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

    /*@RequestMapping(value = ServiceApi.Packages.Migrate, method = RequestMethod.POST)
    @ApiOperation(value = "数据迁移")
    public boolean migrate() throws Exception {
        jsonArchivesService.migrate();
        return true;
    }*/
}
