package com.yihu.ehr.controller;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.config.MetricNames;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.service.resource.stage1.StandardPackage;
import com.yihu.ehr.service.resource.stage1.PackageResolveEngine;
import com.yihu.ehr.service.resource.stage2.PackMill;
import com.yihu.ehr.service.resource.stage2.ResourceBucket;
import com.yihu.ehr.service.resource.stage2.ResourceService;
import com.yihu.ehr.profile.exception.LegacyPackageException;
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

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案资源化", description = "档案包资源化服务。在包解析完之后，资源化入库。")
public class ResolveEndPoint {
    @Autowired
    ResourceService resourceService;

    @Autowired
    PackMill packMill;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    private PackageResolveEngine packResolveEngine;

    @Autowired
    XPackageMgrClient packageMgrClient;

    @Autowired
    ObjectMapper objectMapper;

    @ApiOperation(value = "档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "若包ID为OLDEST，则取最旧的未解析档案包")
    @RequestMapping(value = ServiceApi.Packages.Package, method = RequestMethod.PUT)
    public ResponseEntity<String> resolve(
            @ApiParam(value = "id", defaultValue = "OLDEST")
            @PathVariable("id") String packageId,
            @ApiParam(value = "模拟应用ID", defaultValue = "FBIWarning911")
            @RequestParam("clientId") String clientId,
            @ApiParam(value = "返回档案数据", defaultValue = "true")
            @RequestParam("echo") boolean echo) throws Throwable {
        try {
            MPackage pack = packageMgrClient.getPackage(packageId);
            if (pack == null) throw new ApiException(HttpStatus.NOT_FOUND, "Package not found.");

            long start = System.currentTimeMillis();

            if (StringUtils.isEmpty(pack.getClientId())) pack.setClientId(clientId);
            String zipFile = downloadTo(pack.getRemotePath());

            StandardPackage standardPackage = packResolveEngine.doResolve(pack, zipFile);
            ResourceBucket resourceBucket = packMill.grindingPackModel(standardPackage);
            resourceService.save(resourceBucket);

            packageMgrClient.reportStatus(pack.getId(),
                    ArchiveStatus.Finished,
                    String.format("Profile: %s, identity: %s", standardPackage.getId(), standardPackage.getDemographicId()));

            getMetricRegistry().histogram(MetricNames.ResourceJob).update((System.currentTimeMillis() - start) / 1000);

            if (echo) {
                return new ResponseEntity<>(standardPackage.toJson(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("", HttpStatus.OK);
            }
        } catch (LegacyPackageException e) {
            packageMgrClient.reportStatus(packageId, ArchiveStatus.LegacyIgnored, e.getMessage());

            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
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
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], System.getProperty("java.io.tmpdir"));
    }

    private MetricRegistry getMetricRegistry(){
        return SpringContext.getService(MetricRegistry.class);
    }


}
