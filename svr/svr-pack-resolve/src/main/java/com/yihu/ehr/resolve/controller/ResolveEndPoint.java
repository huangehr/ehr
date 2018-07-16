package com.yihu.ehr.resolve.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.ArchiveStatus;
import com.yihu.ehr.profile.ProfileType;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.packs.EsDetailsPackage;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.exception.IllegalJsonDataException;
import com.yihu.ehr.profile.exception.IllegalJsonFileException;
import com.yihu.ehr.profile.exception.ResolveException;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.resolve.model.stage1.OriginalPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.resource.stage1.ResolveService;
import com.yihu.ehr.resolve.service.resource.stage2.IdentifyService;
import com.yihu.ehr.resolve.service.resource.stage2.PackMillService;
import com.yihu.ehr.resolve.service.resource.stage2.ResourceService;
import com.yihu.ehr.resolve.service.resource.stage2.StatusReportService;
import com.yihu.ehr.resolve.util.LocalTempPathUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ResolveEndPoint", description = "资源化入库", tags = {"档案解析服务-资源化入库"})
public class ResolveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PackMillService packMillService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private ResolveService resolveService;
    @Autowired
    private PackageMgrClient packageMgrClient;
    @Autowired
    private IdentifyService identifyService;
    @Autowired
    private StatusReportService statusReportService;

    @ApiOperation(value = "健康档案包入库", notes = "若包ID为空，则取最旧的未解析健康档案包", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.PackageResolve.Resolve, method = RequestMethod.GET)
    public String resolve(
            @ApiParam(name = "id", value = "档案包编号", required = true)
            @PathVariable(value = "id") String id,
            @ApiParam(name = "clientId", value = "模拟应用ID")
            @RequestParam(value = "clientId", required = false) String clientId,
            @ApiParam(name = "echo", value = "返回档案数据", required = true, defaultValue = "true")
            @RequestParam(value = "echo") boolean echo) throws Throwable {
        EsSimplePackage pack = packageMgrClient.getPackage(id);
        if (null == pack) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("failure", "无可用档案包！");
            return objectMapper.writeValueAsString(resultMap);
        }
        try {
            statusReportService.reportStatus(pack.get_id(), ArchiveStatus.Acquired, 0, "正在入库中", null);
            OriginalPackage originalPackage = resolveService.doResolve(pack, downloadTo(pack.getRemote_path()));
            ResourceBucket resourceBucket = packMillService.grindingPackModel(originalPackage);
            identifyService.identify(resourceBucket, originalPackage);
            resourceService.save(resourceBucket, originalPackage);
            //回填入库状态
            Map<String, Object> map = new HashMap();
            map.put("defect", resourceBucket.getQcMetadataRecords().getRecords().isEmpty() ? 0 : 1); //是否解析异常
            map.put("patient_name", resourceBucket.getBasicRecord(ResourceCells.PATIENT_NAME));
            map.put("profile_id", resourceBucket.getId());
            map.put("demographic_id", resourceBucket.getBasicRecord(ResourceCells.DEMOGRAPHIC_ID));
            map.put("event_type", originalPackage.getEventType() == null ? -1 : originalPackage.getEventType().getType());
            map.put("event_no", originalPackage.getEventNo());
            map.put("event_date", DateUtil.toStringLong(originalPackage.getEventTime()));
            map.put("patient_id", originalPackage.getPatientId());
            map.put("dept", resourceBucket.getBasicRecord(ResourceCells.DEPT_CODE));
            long delay = pack.getReceive_date().getTime() - originalPackage.getEventTime().getTime();
            map.put("delay", delay % (1000 * 60 * 60 * 24) > 0 ? delay / (1000 * 60 * 60 * 24) + 1 : delay / (1000 * 60 * 60 * 24));
            map.put("re_upload_flg", String.valueOf(originalPackage.isReUploadFlg()));
            statusReportService.reportStatus(pack.get_id(), ArchiveStatus.Finished, 0, "resolve success", map);
            //发送事件处理消息
            if (originalPackage.getProfileType() == ProfileType.File || originalPackage.getProfileType() == ProfileType.Link) {
                KafkaTemplate kafkaTemplate = SpringContext.getService(KafkaTemplate.class);
                kafkaTemplate.send("svr-pack-event", "resolve", objectMapper.writeValueAsString(pack));
            }
            //是否返回数据
            if (echo) {
                return originalPackage.toJson();
            } else {
                Map<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("success", "入库成功！");
                return objectMapper.writeValueAsString(resultMap);
            }
        } catch (Exception e) {
            int errorType = -1;
            if (e instanceof ZipException) {
                errorType = 1;
            } else if (e instanceof IllegalJsonFileException) {
                errorType = 2;
            } else if (e instanceof IllegalJsonDataException) {
                errorType = 3;
            } else if (e instanceof ResolveException) {
                errorType = 21; //21以下为质控和解析的公共错误
            }
            if (StringUtils.isBlank(e.getMessage())) {
                statusReportService.reportStatus(pack.get_id(), ArchiveStatus.Failed, errorType, "Internal Server Error", null);
            } else {
                statusReportService.reportStatus(pack.get_id(), ArchiveStatus.Failed, errorType, e.getMessage(), null);
            }
            throw e;
        }
    }

    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 LegacyIgnored 状态，即无法决定该JSON档案的去向，需要人为干预。
     * <p>
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    @ApiOperation(value = "本地档案包解析", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.PackageResolve.Local, method = RequestMethod.POST)
    public ResponseEntity<String> resolve(
            @ApiParam(name = "id", value = "档案包ID，忽略此值", required = true, defaultValue = "LocalPackage")
            @PathVariable(value = "id") String packageId,
            @ApiParam(name = "file", value = "档案包文件", required = true)
            @RequestPart() MultipartFile file,
            @ApiParam(name = "password", value = "档案包密码", required = true)
            @RequestParam(value = "password") String password,
            @ApiParam(name = "clientId", value = "模拟应用ID", required = true, defaultValue = "PACK-RESOLVE")
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "persist", value = "是否入库", required = true, defaultValue = "false")
            @RequestParam(value = "persist", defaultValue = "false") boolean persist) throws Throwable {
        BufferedOutputStream stream = null;
        try {
            String zipFile = LocalTempPathUtil.getTempPathWithUUIDSuffix() + packageId + ".zip";
            stream = new BufferedOutputStream(new FileOutputStream(new File(zipFile)));
            FileCopyUtils.copy(file.getInputStream(), stream);
            EsSimplePackage pack = new EsSimplePackage();
            pack.set_id(packageId);
            pack.setPwd(password);
            pack.setReceive_date(new Date());
            pack.setClient_id(clientId);
            OriginalPackage originalPackage = resolveService.doResolve(pack, zipFile);
            ResourceBucket resourceBucket = packMillService.grindingPackModel(originalPackage);
            if (persist) {
                identifyService.identify(resourceBucket, originalPackage);
                resourceService.save(resourceBucket, originalPackage);
            }
            return new ResponseEntity<>(originalPackage.toJson(), HttpStatus.OK);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * 获取档案解析包内容
     * 可用于质量控制，或者用于问题跟踪
     * <p>
     * <p>
     */
    @ApiOperation(value = "获取档案解析包内容", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.PackageResolve.Fetch, method = RequestMethod.GET)
    public String fetch(
            @ApiParam(name = "id", value = "档案包ID", required = true)
            @PathVariable(value = "id") String id) throws Exception {
        EsSimplePackage esSimplePackage = packageMgrClient.getPackage(id);
        String zipFile = downloadTo(esSimplePackage.getRemote_path());
        OriginalPackage packModel = resolveService.doResolve(esSimplePackage, zipFile);
        return packModel.toJson();
    }

    @ApiOperation(value = "即时交互档案解析入库", notes = "即时交互档案解析入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.PackageResolve.Immediate, method = RequestMethod.PUT)
    public String immediateResolve(
            @ApiParam(name = "idCardNo", value = "身份证号")
            @RequestParam(value = "idCardNo", required = false) String idCardNo,
            @ApiParam(name = "data", value = "档案数据")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "clientId", value = "模拟应用ID")
            @RequestParam(value = "clientId", required = false) String clientId,
            @ApiParam(name = "echo", value = "返回档案数据")
            @RequestParam(value = "echo",required = false,defaultValue = "true") boolean echo) throws Throwable {
        EsSimplePackage esSimplePackage = new EsSimplePackage();
        esSimplePackage.set_id(UUID.randomUUID().toString());
        esSimplePackage.setReceive_date(new Date());
        StandardPackage standardPackage = resolveService.doResolveImmediateData(data, esSimplePackage);
        ResourceBucket resourceBucket = packMillService.grindingPackModel(standardPackage);
        identifyService.identify(resourceBucket, standardPackage);
        resourceService.save(resourceBucket, standardPackage);
        //回填入库状态
        Map<String, String> map = new HashMap();
        map.put("profileId", standardPackage.getId());
        map.put("demographicId", standardPackage.getDemographicId());
        map.put("eventType", String.valueOf(standardPackage.getEventType().getType()));
        map.put("eventNo", standardPackage.getEventNo());
        map.put("eventDate", DateUtil.toStringLong(standardPackage.getEventTime()));
        map.put("patientId", standardPackage.getPatientId());
        //是否返回数据
        if (echo) {
            return standardPackage.toJson();
        } else {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("success", "入库成功！");
            return objectMapper.writeValueAsString(resultMap);
        }

    }

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPathUtil.getTempPathWithUUIDSuffix());
    }
}
