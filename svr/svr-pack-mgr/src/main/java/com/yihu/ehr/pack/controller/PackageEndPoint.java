package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.pack.feign.SecurityClient;
import com.yihu.ehr.pack.feign.UserClient;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.pack.task.MessageBuffer;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.encrypt.RSA;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
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
    @Autowired
    private SecurityClient securityClient;
    @Autowired
    private PackageService packService;
    @Autowired
    private UserClient userClient;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @RequestMapping(value = ServiceApi.Packages.PackageSearch, method = RequestMethod.GET)
    @ApiOperation(value = "搜索档案包", response = MPackage.class, responseContainer = "List", notes = "搜索档案包")
    public Collection<MPackage> packageList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "receiveDate>2016-06-02T03:00:24Z+0800")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+receiveDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {

        List<Package> packageList = packService.search(fields, filters, sorts, page, size);

        pagedResponse(request, response, packService.getCount(filters), page, size);

        return convertToModels(packageList, new ArrayList<>(packageList.size()), MPackage.class, fields);
    }

    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案包", response = MPackage.class, responseContainer = "List", notes = "每次删除一万条记录")
    public Collection<Package> deletePackages(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "message?jkzl")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+receiveDate")
            @RequestParam(value = "sorts", required = false) String sorts) throws ParseException {

        List<Package> packageList = packService.search("id", filters, sorts, 0, 10000);

        for (Package pack : packageList) {
            String tokens[] = pack.getRemotePath().split(":");
            try {
                fastDFSUtil.delete(tokens[0], tokens[1]);
                packService.deletePackage(pack.getId());
            } catch (Exception e) {
                LogService.getLogger().warn("Error occurred while deleting package: " + e.getMessage());
            }
        }

        return packageList;
    }

    @RequestMapping(value = ServiceApi.Packages.PackageCrypto, method = RequestMethod.POST)
    @ApiOperation(value = "档案包密码加密")
    public String getPackageCrypto(
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto) throws Exception {
        MKey key = securityClient.getOrgKey(orgCode);
        if (key == null ||  key.getPublicKey()==null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
        }

        return RSA.encrypt(packageCrypto,RSA.genPublicKey(key.getPublicKey()));
    }


    @RequestMapping(value = ServiceApi.Packages.Packages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案", notes = "从集成开放平台接收健康档案数据包")
    public void savePackageWithOrg (
            @ApiParam(name = "pack", value = "档案包", allowMultiple = true)
            @RequestPart() MultipartFile pack,
            @ApiParam(name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            HttpServletRequest request) throws JsonProcessingException {

        MKey key = securityClient.getOrgKey(orgCode);
        Package aPackage;
        if (key == null ||  key.getPrivateKey()==null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Invalid private key, maybe you miss the organization code?");
        }
        try {
            String password = RSA.decrypt(packageCrypto, RSA.genPrivateKey(key.getPrivateKey()));
            aPackage = packService.receive(pack.getInputStream(), password, md5, orgCode, getClientId(request));
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.FORBIDDEN, "javax.crypto.BadPaddingException." + ex.getMessage());
        }
        MPackage mPackage = convertToModel(aPackage, MPackage.class);
        redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
        //messageBuffer.putMessage(convertToModel(aPackage, MPackage.class));
    }

    @RequestMapping(value = ServiceApi.Packages.AcquirePackage, method = RequestMethod.GET)
    @ApiOperation(value = "处理档案包(更新状态)")
    public String acquirePackage(
            @ApiParam(name = "id", value = "档案包编号", defaultValue = "")
            @RequestParam(required = false) String id) throws Exception {
        String re = "";
        Package aPackage = packService.acquirePackage(id);
        if(aPackage != null) {
            re = objectMapper.writeValueAsString(aPackage);
        }
        return re;
    }

    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息，若ID为OLDEST，则获取最早，还没解析的档案包")
    public ResponseEntity<MPackage> getPackage(
            @ApiParam(name = "id", value = "档案包编号", defaultValue = "OLDEST")
            @PathVariable(value = "id") String id) throws IOException {
        Package aPackage = packService.getPackage(id);

        if (aPackage == null) return new ResponseEntity<>((MPackage) new MPackage(), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(convertToModel(aPackage, MPackage.class), HttpStatus.OK);
    }


    @RequestMapping(value = ServiceApi.Packages.Package, method = {RequestMethod.PUT})
    @ApiOperation(value = "修改档案包状态", notes = "修改档案包状态")
    public ResponseEntity<MPackage> reportStatus(
            @ApiParam(value = "档案包编号")
            @PathVariable(value = "id") String id,
            @ApiParam(value = "状态")
            @RequestParam(value = "status") ArchiveStatus status,
            @ApiParam(value = "消息")
            @RequestBody String message) throws Exception {
        Package aPackage = packService.getPackage(id);
        if (aPackage == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

        aPackage.setArchiveStatus(status);
        //资源化状态，0未入库 1已入库
        aPackage.setResourced(true);
        aPackage.setMessage(message);
        if (status == ArchiveStatus.Finished) {
            //入库成功
            Map<String,String> map = objectMapper.readValue(message,Map.class);
            aPackage.setEventType(map.get("eventType"));
            aPackage.setEventNo(map.get("eventNo"));
            aPackage.setEventDate(DateUtil.strToDate(map.get("eventDate")));
            aPackage.setPatientId(map.get("patientId"));
            aPackage.setFinishDate(new Date());
        }
        else if(status == ArchiveStatus.Acquired) {
            aPackage.setParseDate(new Date()); //入库执行时间
        }
        else {
            aPackage.setFinishDate(null);
            Package oldPackage =  packService.getPackage(aPackage.getId());
            if(oldPackage.getFailCount() < 3) {
                int failCount = oldPackage.getFailCount();
                failCount ++;
                aPackage.setFailCount(failCount);
            }
        }
        packService.save(aPackage);
        return new ResponseEntity<>((MPackage) null, HttpStatus.OK);
    }


    @RequestMapping(value = ServiceApi.Packages.Package, method = {RequestMethod.DELETE})
    @ApiOperation(value = "删除档案包", response = Object.class, notes = "删除一个数据包")
    public void deletePackage(
            @ApiParam(name = "id", value = "档案包编号")
            @PathVariable(value = "id") String id) {
        packService.deletePackage(id);
    }

    @RequestMapping(value = ServiceApi.Packages.PackageDownloads, method = {RequestMethod.GET})
    @ApiOperation(value = "下载档案包", notes = "下载档案包")
    public ResponseEntity<MPackage> downloadPackage(
            @ApiParam(name = "id", value = "档案包编号")
            @PathVariable(value = "id") String id,
            HttpServletResponse response) throws Exception {
        try {
            InputStream is = packService.downloadFile(id);
            if (is == null) return new ResponseEntity<>((MPackage) null, HttpStatus.NOT_FOUND);

            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setHeader("Content-Disposition", "attachment; filename=" + id + ".zip");

            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();

            return null;
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot download package from server. " + ex.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.Packages.ResolveQueue, method = RequestMethod.POST)
    @ApiOperation(value = "添加解析队列", notes = "队列中无消息的时候才可添加状态为Received或者Acquired的数据")
    public String resolveQueue(
            @ApiParam(name = "status", value = "(Received || Acquired || Failed || Finished)", required = true)
            @RequestParam(value = "status") String status,
            @ApiParam(name = "sorts", value = "排序(建议使用默认值，以解析较早之前的数据)", defaultValue = "+receiveDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "count", value = "数量", required = true, defaultValue = "500")
            @RequestParam(value = "count") int count) throws Exception {
        if(status.trim().equals("Received") || status.trim().equals("Acquired")) {
            if(redisTemplate.opsForList().size(RedisCollection.PackageList) > 0) {
                return "添加失败，队列中存在消息！";
            }
        }
        List<Package> packageList = packService.search(null, "archiveStatus=" + status, sorts, 1, count);
        for(Package rPackage : packageList) {
            rPackage.setArchiveStatus(ArchiveStatus.Received);
            packService.save(rPackage);
            MPackage mPackage = convertToModel(rPackage, MPackage.class);
            redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
        }
        return "添加成功！";
    }

    @RequestMapping(value = ServiceApi.Packages.QueueSize, method = RequestMethod.GET)
    @ApiOperation(value = "获取当前解析队列数")
    public long queueSize() throws Exception {
        return redisTemplate.opsForList().size(RedisCollection.PackageList);
    }

    @RequestMapping(value = ServiceApi.Packages.LegacyPackages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案(兼容旧接口)", notes = "从集成开放平台接收健康档案数据包(兼容旧接口)")
    @Deprecated
    public void savePackageWithUser(
            @ApiParam(name = "package", value = "档案包", allowMultiple = true) MultipartHttpServletRequest pack,
            @ApiParam(name = "user_name", value = "用户名")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(name = "package_crypto", value = "档案包解压密码,二次加密")
            @RequestParam(value = "package_crypto") String packageCrypto,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5") String md5) throws Exception {

        MultipartFile multipartFile = pack.getFile("file");
        if (multipartFile == null) throw new ApiException(HttpStatus.FORBIDDEN, ErrorCode.MissParameter, "file");

        MUser user = userClient.getUserByUserName(userName);
        MKey key = securityClient.getUserKey(user.getId());
        String privateKey = key.getPrivateKey();
        if (null == privateKey)
            throw new ApiException(HttpStatus.FORBIDDEN, "Invalid public key, maybe you miss the user name?");

        String unzipPwd = RSA.decrypt(packageCrypto, RSA.genPrivateKey(privateKey));
        Package aPackage = packService.receive(multipartFile.getInputStream(), unzipPwd, md5, null, null);

        //messageBuffer.putMessage(convertToModel(aPackage, MPackage.class));
    }

}
