package com.yihu.ehr.pack.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.feign.AppClient;
import com.yihu.ehr.pack.service.DatasetPackage;
import com.yihu.ehr.pack.service.DatasetPackageService;
import com.yihu.ehr.pack.task.MessageBuffer;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.encrypt.MD5;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 非病人维度档案包控制器。
 *
 * @author HZY
 * @version 1.0
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "DatasetPackageEndPoint", description = "档案包服务（非病人维度兼容）", tags = {"档案包服务-档案包服务（非病人维度兼容）"})
public class DatasetPackageEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DatasetPackageService datasetPackService;
    @Autowired
    private AppClient appClient;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;


    @RequestMapping(value = ServiceApi.DatasetPackages.AcquirePackage, method = RequestMethod.GET)
    @ApiOperation(value = "处理档案包(更新状态)")
    public String acquirePackage(
            @ApiParam(name = "id", value = "档案包编号", defaultValue = "")
            @RequestParam(required = false) String id) throws Exception {
        String re = "";
        DatasetPackage aPackage = datasetPackService.acquirePackage(id);
        if(aPackage!=null)
        {
            re = objectMapper.writeValueAsString(aPackage);
        }
        return re;
    }

    @RequestMapping(value = ServiceApi.DatasetPackages.Package, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案包", notes = "获取档案包的信息，若ID为OLDEST，则获取最早，还没解析的档案包")
    public ResponseEntity<MPackage> getPackage(
            @ApiParam(name = "id", value = "档案包编号", defaultValue = "OLDEST")
            @PathVariable(value = "id") String id) throws IOException {
        DatasetPackage aPackage = datasetPackService.getPackage(id);

        if (aPackage == null) return new ResponseEntity<>((MPackage) new MPackage(), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(convertToModel(aPackage, MPackage.class), HttpStatus.OK);
    }


    @RequestMapping(value = ServiceApi.DatasetPackages.Package, method = {RequestMethod.PUT})
    @ApiOperation(value = "修改档案包状态", notes = "修改档案包状态")
    public ResponseEntity<MPackage> reportStatus(@ApiParam(value = "档案包编号")
                                                 @PathVariable(value = "id") String id,
                                                 @ApiParam(value = "状态")
                                                 @RequestParam(value = "status") ArchiveStatus status,
                                                 @ApiParam(value = "消息")
                                                 @RequestBody String message) throws Exception {
        DatasetPackage aPackage = datasetPackService.getPackage(id);
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
        else if(status == ArchiveStatus.Acquired)
        {
            aPackage.setParseDate(new Date()); //入库执行时间
        }
        else {
            aPackage.setFinishDate(null);
        }

        datasetPackService.save(aPackage);

        return new ResponseEntity<>((MPackage) null, HttpStatus.OK);
    }


    @RequestMapping(value = ServiceApi.DatasetPackages.Package, method = {RequestMethod.DELETE})
    @ApiOperation(value = "删除档案包", response = Object.class, notes = "删除一个数据包")
    public void deletePackage(@ApiParam(name = "id", value = "档案包编号")
                              @PathVariable(value = "id") String id) {
        datasetPackService.deletePackage(id);
    }


    //zip包密码规则：MD5(secret+packPwdSeed+secret)
    @RequestMapping(value = ServiceApi.DatasetPackages.Packages, method = RequestMethod.POST)
    @ApiOperation(value = "接收档案（兼容非病人维度)", notes = "支持数非健康档案维度的数据包接收")
    public DatasetPackage savePackageForGateway(
            @ApiParam(name = "pack", value = "档案包", allowMultiple = true)
            @RequestPart() MultipartFile pack,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "packPwdSeed", value = "档案包密码种子")
            @RequestParam(value = "packPwdSeed") String packPwdSeed,
            @ApiParam(name = "md5", value = "档案包MD5")
            @RequestParam(value = "md5", required = false) String md5,
            @ApiParam(name = "appKey", value = "应用ＩＤ，用于获取secret生成包密码")
            @RequestParam(value = "appKey", required = false) String appKey,
            HttpServletRequest request) throws JsonProcessingException {

        MApp app = appClient.getApp(appKey);
        String secret = null;
        if (app!=null && !StringUtils.isEmpty(app.getSecret())){
            secret = app.getSecret();
        }else {
            throw new ApiException(HttpStatus.FORBIDDEN, "Invalid app key, maybe you used the error appKey?");
        }
        DatasetPackage aPackage =null;
        try {
            String password = MD5.hash(secret+packPwdSeed+secret);//MD5 生成zip包密码
            aPackage = datasetPackService.receiveDatasets(pack.getInputStream(), password, md5, orgCode, getClientId(request));
        } catch (Exception ex) {
            throw new ApiException(HttpStatus.FORBIDDEN, "javax.crypto.BadPaddingException." + ex.getMessage());
        }
        redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(aPackage));
        //messageBuffer.putMessage(convertToModel(aPackage, MPackage.class));
        return aPackage;
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        MApp app = appClient.getApp(appId);
        return app;
    }

}
