package com.yihu.ehr.resolve.controller;

import com.codahale.metrics.MetricRegistry;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.resolve.config.MetricNames;
import com.yihu.ehr.resolve.dao.DataSetPackageDao;
import com.yihu.ehr.resolve.feign.DataSetPackageMgrClient;
import com.yihu.ehr.resolve.feign.PackageMgrClient;
import com.yihu.ehr.resolve.model.stage1.DataSetPackage;
import com.yihu.ehr.resolve.model.stage1.StandardPackage;
import com.yihu.ehr.resolve.model.stage2.ResourceBucket;
import com.yihu.ehr.resolve.service.resource.stage1.PackageResolveService;
import com.yihu.ehr.resolve.service.resource.stage2.PackMillService;
import com.yihu.ehr.resolve.service.resource.stage2.PatientRegisterService;
import com.yihu.ehr.resolve.service.resource.stage2.ResourceService;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ResolveEndPoint", description = "资源化入库", tags = {"档案解析服务-资源化入库"})
public class ResolveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PatientRegisterService patientRegisterService;
    @Autowired
    private PackMillService packMillService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private PackageResolveService packageResolveService;
    @Autowired
    private PackageMgrClient packageMgrClient;
    @Autowired
    private DataSetPackageMgrClient datasetPackageMgrClient;
    @Autowired
    private DataSetPackageDao dataSetPackageDao;

    @ApiOperation(value = "健康档案包入库", notes = "若包ID为空，则取最旧的未解析健康档案包", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.Packages.PackageResolve, method = RequestMethod.PUT)
    public String resolve(
            @ApiParam(name = "id", value = "档案包ID")
            @RequestParam(value = "id", required = false) String packageId,
            @ApiParam(name = "clientId", value = "模拟应用ID")
            @RequestParam(value = "clientId", required = false) String clientId,
            @ApiParam(name = "echo", value = "返回档案数据", required = true, defaultValue = "true")
            @RequestParam(value = "echo") boolean echo) throws Throwable {
        String packString = packageMgrClient.acquirePackage(packageId);
        if (StringUtils.isEmpty(packString)) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("failure", "无可用档案包！");
            return objectMapper.writeValueAsString(resultMap);
        }
        MPackage pack = objectMapper.readValue(packString, MPackage.class);  //已修改包状态为1 正在入库库
        String packId = pack.getId();
        try {
            long start = System.currentTimeMillis();
            if (StringUtils.isEmpty(pack.getClientId())) {
                pack.setClientId(clientId);
            }
            String zipFile = downloadTo(pack.getRemotePath());
            StandardPackage standardPackage = packageResolveService.doResolve(pack, zipFile);
            ResourceBucket resourceBucket = packMillService.grindingPackModel(standardPackage);
            resourceService.save(resourceBucket);
            //居民信息注册
            patientRegisterService.checkPatient(resourceBucket, packId);
            //回填入库状态
            Map<String, String> map = new HashMap();
            map.put("profileId", standardPackage.getId());
            map.put("demographicId", standardPackage.getDemographicId());
            map.put("eventType", String.valueOf(standardPackage.getEventType().getType()));
            map.put("eventNo", standardPackage.getEventNo());
            map.put("eventDate", DateUtil.toStringLong(standardPackage.getEventDate()));
            map.put("patientId", standardPackage.getPatientId());
            packageMgrClient.reportStatus(packId, ArchiveStatus.Finished, objectMapper.writeValueAsString(map));
            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
            //是否返回数据
            if (echo) {
                return standardPackage.toJson();
            } else {
                Map<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("success", "入库成功！");
                return objectMapper.writeValueAsString(resultMap);
            }
        } catch (Exception e) {
            Map<String, String> resultMap = new HashMap<String, String>();
            if (StringUtils.isBlank(e.getMessage())) {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, "Internal Server Error");
                resultMap.put("error", "Internal Server Error");
                return objectMapper.writeValueAsString(resultMap);
            } else {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
                resultMap.put("error", e.getMessage());
                return objectMapper.writeValueAsString(resultMap);
            }
        }
    }

    //new add by HZY in 2017/06/29
    @ApiOperation(value = "健康档案-（非病人维度-数据集包入库）", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.Packages.PackageResolve + 2, method = RequestMethod.PUT)
    public List<String> resolveDataSetPackage(
            @ApiParam(name = "id", value = "档案包ID", required = true)
            @RequestParam(value = "id") String packageId,
            @ApiParam(name = "clientId", value = "模拟应用ID")
            @RequestParam(value = "clientId", required = false) String clientId,
            @ApiParam(name = "echo", value = "返回档案数据", required = true, defaultValue = "true")
            @RequestParam(value = "echo") boolean echo) throws Throwable {
        String packString = datasetPackageMgrClient.acquireDatasetPackage(packageId);
        List<String> returnJson = new ArrayList<>();
        if (StringUtils.isEmpty(packString)) {
            returnJson.add("无可用档案包！");
            return returnJson;
        }
        MPackage pack = objectMapper.readValue(packString, MPackage.class);  //已修改包状态为1 正在入库库
        String packId = pack.getId();
        try {
            long start = System.currentTimeMillis();
            if (StringUtils.isEmpty(pack.getClientId())) pack.setClientId(clientId);
            String zipFile = downloadTo(pack.getRemotePath());
            List<StandardPackage> standardPackages = packageResolveService.doResolveNonArchive(pack, zipFile);
            for (StandardPackage standardPackage : standardPackages) {
                ResourceBucket resourceBucket = packMillService.grindingPackModel(standardPackage);
                resourceService.save(resourceBucket);
                patientRegisterService.checkPatient(resourceBucket, packId);
                String json = standardPackage.toJson();
                returnJson.add(json);
            }
            //居民信息注册
            //回填入库状态
            Map<String, String> map = new HashMap();
            map.put("profileId", standardPackages.get(0).getId());
            map.put("demographicId", standardPackages.get(0).getDemographicId());
            map.put("eventType", standardPackages.get(0).getEventType() == null ? "" : String.valueOf(standardPackages.get(0).getEventType().getType()));
            map.put("eventNo", standardPackages.get(0).getEventNo());
            map.put("eventDate", DateUtil.toStringLong(standardPackages.get(0).getEventDate()));
            map.put("patientId", standardPackages.get(0).getPatientId());
            datasetPackageMgrClient.reportStatus(packId, ArchiveStatus.Finished, objectMapper.writeValueAsString(map));
            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
            if (echo) {
                return returnJson;
            } else {
                returnJson = new ArrayList<>();
                returnJson.add("档案包入库成功！");
                return returnJson;
            }
        } catch (Exception e) {
            List<String> resultList = new ArrayList<String>();
            if (StringUtils.isBlank(e.getMessage())) {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, "Internal Server Error");
                resultList.add("Internal Server Error");
                return resultList;
            } else {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
                resultList.add(e.getMessage());
                return resultList;
            }
        }
    }

    @ApiOperation(value = "数据集档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = ServiceApi.DatasetPackages.PackageResolve, method = RequestMethod.PUT)
    public String resolveDataSet(
            @ApiParam(name = "id", value = "包ID", required = true)
            @RequestParam(value = "id") String packageId,
            @ApiParam(name = "clientId", value = "模拟应用ID")
            @RequestParam(value = "clientId", required = false) String clientId,
            @ApiParam(name = "echo", value = "返回档案数据", required = true)
            @RequestParam(value = "echo") boolean echo) throws Exception {
        String packStr = datasetPackageMgrClient.acquireDatasetPackage(packageId);
        if (StringUtils.isEmpty(packStr)) {
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("msg", "无可用数据集档案包！");
            return objectMapper.writeValueAsString(resultMap);
        }
        MPackage pack = objectMapper.readValue(packStr, MPackage.class);
        String packId = pack.getId();
        try {
            long start = System.currentTimeMillis();
            if (StringUtils.isEmpty(pack.getClientId())) {
                pack.setClientId(clientId);
            }
            String zipFile = downloadTo(pack.getRemotePath());
            DataSetPackage datasetPackage = packageResolveService.doResolveDataset(pack, zipFile);
            dataSetPackageDao.saveDataset(datasetPackage);
            // 回写入库状态
            Map<String, String> map = new HashMap<>();
            map.put("eventType", null);
            map.put("eventNo", null);
            map.put("eventDate", null);
            map.put("patientId", null);
            datasetPackageMgrClient.reportStatus(packId, ArchiveStatus.Finished, objectMapper.writeValueAsString(map));
            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);
            if (echo) {
                return datasetPackage.toJson();
            } else {
                Map<String, String> resultMap = new HashMap<String, String>();
                resultMap.put("msg", "数据集档案包入库成功！");
                return objectMapper.writeValueAsString(resultMap);
            }
        } catch (Exception e) {
            if (StringUtils.isBlank(e.getMessage())) {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, "Internal Server Error");
            } else {
                packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
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
    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.POST)
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
        String zipFile = System.getProperty("java.io.tmpdir") + packageId + ".zip";
        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(zipFile)));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();
            MPackage pack = new MPackage();
            pack.setPwd(password);
            pack.setId(packageId);
            pack.setClientId(clientId);
            pack.setArchiveStatus(ArchiveStatus.Received);
            StandardPackage packModel = packageResolveService.doResolve(pack, zipFile);
            packModel.setClientId(clientId);
            ResourceBucket resourceBucket = packMillService.grindingPackModel(packModel);
            if (persist) {
                resourceService.save(resourceBucket);
                patientRegisterService.checkPatient(resourceBucket, packageId);
            }
            return new ResponseEntity<>(packModel.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取档案解析包内容
     * 可用于质量控制，或者用于问题跟踪
     * <p>
     * <p>
     */
    @ApiOperation(value = "获取档案解析包内容", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = "/packages/fetch/{id}", method = RequestMethod.GET)
    public ResponseEntity<String> fetch(
            @ApiParam(name = "id", value = "档案包ID", required = true)
            @PathVariable(value = "id") String id) throws Throwable {
        try {
            ResponseEntity<MPackage> entity = packageMgrClient.getPackage(id);
            MPackage pack = entity.getBody();
            String zipFile = downloadTo(pack.getRemotePath());
            StandardPackage packModel = packageResolveService.doResolve(pack, zipFile);

            return new ResponseEntity<>(packModel.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], System.getProperty("java.io.tmpdir"));
    }

    private MetricRegistry getMetricRegistry() {
        return SpringContext.getService(MetricRegistry.class);
    }

}
