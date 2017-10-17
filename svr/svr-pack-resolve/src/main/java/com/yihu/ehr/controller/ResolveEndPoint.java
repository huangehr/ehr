package com.yihu.ehr.controller;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.config.MetricNames;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.DataSetPackageMgrClient;
import com.yihu.ehr.feign.PackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.service.resource.stage1.DatasetPackage;
import com.yihu.ehr.service.resource.stage1.PackageResolveEngine;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage2.PackMill;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.ResourceService;
import com.yihu.ehr.service.resource.stage2.repo.DataSetPackageDao;
import com.yihu.ehr.service.resource.stage2.repo.PatientInfoDao;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.log.LogService;
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
@Api(value = "档案资源化", description = "档案包资源化服务。在包解析完之后，资源化入库。")
public class ResolveEndPoint {

    @Autowired
    private ResourceService resourceService;
    @Autowired
    private PackMill packMill;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private PackageResolveEngine packResolveEngine;
    @Autowired
    private PackageMgrClient packageMgrClient;
    @Autowired
    private DataSetPackageMgrClient datasetPackageMgrClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private DataSetPackageDao dataSetPackageDao;
    @Autowired
    private PatientInfoDao patientInfoDao;

    @ApiOperation(value = "健康档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "若包ID为空，则取最旧的未解析健康档案包")
    @RequestMapping(value = ServiceApi.Packages.PackageResolve, method = RequestMethod.PUT)
    public String resolve(
            @ApiParam(value = "档案包ID", defaultValue = "")
            @RequestParam(required = false) String packageId,
            @ApiParam(value = "模拟应用ID", defaultValue = "")
            @RequestParam(required = false) String clientId,
            @ApiParam(value = "返回档案数据", defaultValue = "true")
            @RequestParam("echo") boolean echo) throws Throwable {
        String packString = packageMgrClient.acquirePackage(packageId);
        if (StringUtils.isEmpty(packString)) {
           return "无可用档案包！";
        }
        MPackage pack = objectMapper.readValue(packString, MPackage.class);  //已修改包状态为1 正在入库库
        String packId = pack.getId();
        try {
            long start = System.currentTimeMillis();
            if (StringUtils.isEmpty(pack.getClientId())) pack.setClientId(clientId);
            String zipFile = downloadTo(pack.getRemotePath());
            StandardPackage standardPackage = packResolveEngine.doResolve(pack, zipFile);
            ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
            resourceService.save(resourceBucket);
            //获取注册信息
            String idCardNo = resourceBucket.getDemographicId() == null ? "":resourceBucket.getDemographicId().toString();
            if(!idCardNo.equals("")) {
                boolean isRegistered = patientInfoDao.isRegistered(idCardNo);
                if (!isRegistered) {
                    MDemographicInfo demoInfo = new MDemographicInfo();
                    demoInfo.setIdCardNo(resourceBucket.getDemographicId() == null ? "" : resourceBucket.getDemographicId().toString());
                    demoInfo.setName(resourceBucket.getPatientName() == null ? "" : resourceBucket.getPatientName().toString());
                    demoInfo.setBirthday(resourceBucket.getMasterRecord().getResourceValue("EHR_000320") == null ? null : DateTimeUtil.simpleDateParse(resourceBucket.getMasterRecord().getResourceValue("EHR_000320")));
                    demoInfo.setNativePlace(resourceBucket.getMasterRecord().getResourceValue("EHR_000015") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000015").toString());
                    demoInfo.setGender(resourceBucket.getMasterRecord().getResourceValue("EHR_000019") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000019").toString());
                    demoInfo.setMartialStatus(resourceBucket.getMasterRecord().getResourceValue("EHR_000014") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000014").toString());
                    demoInfo.setNation(resourceBucket.getMasterRecord().getResourceValue("EHR_000016") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000016").toString());
                    demoInfo.setTelephoneNo(resourceBucket.getMasterRecord().getResourceValue("EHR_000003") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000003").toString());
                    demoInfo.setEmail(resourceBucket.getMasterRecord().getResourceValue("EHR_000008") == null ? "" : resourceBucket.getMasterRecord().getResourceValue("EHR_000008").toString());
                    //注册
                    boolean isSucceed = patientInfoDao.save(demoInfo);
                    if (!isSucceed) {
                        LogService.getLogger().info("idCardNo:" + idCardNo + " registration failed !");
                    }
                }
            }else {
                LogService.getLogger().info("idCardNo is empty !");
            }
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
                return "档案包入库成功！";
            }
        } catch (Exception e) {
            packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
            throw e;
        }
    }

    //new add by HZY in 2017/06/29
    @ApiOperation(value = "健康档案-（非病人维度-数据集包入库）", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "若包ID为空，则取最旧的未解析健康档案包")
    @RequestMapping(value = ServiceApi.Packages.PackageResolve + 2, method = RequestMethod.PUT)
    public List<String> resolveDataSetPackage(
            @ApiParam(value = "档案包ID", defaultValue = "")
            @RequestParam(required = false) String packageId,
            @ApiParam(value = "模拟应用ID", defaultValue = "")
            @RequestParam(required = false) String clientId,
            @ApiParam(value = "返回档案数据", defaultValue = "true")
            @RequestParam("echo") boolean echo) throws Throwable {
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
            List<StandardPackage> standardPackages = packResolveEngine.doResolveNonArchive(pack, zipFile);
            for (StandardPackage standardPackage : standardPackages){
                ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
                resourceService.save(resourceBucket);
                String json = standardPackage.toJson();
                returnJson.add(json);
            }
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
            packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
            throw e;
        }
    }

    @ApiOperation(value = "数据集档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "若包ID为空，则取最旧的未解析数据集档案包")
    @RequestMapping(value = ServiceApi.DatasetPackages.PackageResolve, method = RequestMethod.PUT)
    public String resolveDataSet(
            @ApiParam(value = "包ID")
            @RequestParam(required = false) String packageId,
            @ApiParam(value = "模拟应用ID")
            @RequestParam(required = false) String clientId,
            @ApiParam(value = "返回档案数据", required = true)
            @RequestParam boolean echo) throws Exception {

        String packStr = datasetPackageMgrClient.acquireDatasetPackage(packageId);
        if (StringUtils.isEmpty(packStr)) {
            return "无可用数据集档案包！";
        }
        MPackage pack = objectMapper.readValue(packStr, MPackage.class);
        String packId = pack.getId();
        try {
            long start = System.currentTimeMillis();
            if (StringUtils.isEmpty(pack.getClientId())) {
                pack.setClientId(clientId);
            }
            String zipFile = downloadTo(pack.getRemotePath());
            DatasetPackage datasetPackage = packResolveEngine.doResolveDataset(pack, zipFile);
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
                return "数据集档案包入库成功！";
            }
        } catch (Exception e) {
            packageMgrClient.reportStatus(packId, ArchiveStatus.Failed, e.getMessage());
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
            @ApiParam(value = "档案包ID，忽略此值", defaultValue = "LocalPackage")
            @PathVariable("id") String packageId,
            @ApiParam("档案包文件")
            @RequestPart() MultipartFile file,
            @ApiParam("档案包密码")
            @RequestParam("password") String password,
            @ApiParam(value = "模拟应用ID", defaultValue = "FBIWarning911")
            @RequestParam("clientId") String clientId,
            @ApiParam(value = "是否入库")
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
            StandardPackage packModel = packResolveEngine.doResolve(pack, zipFile);
            packModel.setClientId(clientId);
            ResourceBucket resourceBucket = packMill.grindingPackModel(packModel);
            if (persist) resourceService.save(resourceBucket);
            return new ResponseEntity<>(packModel.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw e;
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
