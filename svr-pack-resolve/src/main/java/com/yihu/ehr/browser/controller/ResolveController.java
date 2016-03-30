package com.yihu.ehr.browser.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.persist.ProfileService;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.service.PackageResolver;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "档案包解析", description = "档案包解析服务")
public class ResolveController {
    @Autowired
    PackageResolver resolver;

    @Autowired
    ProfileService profileService;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    XPackageMgrClient packageMgrClient;

    @ApiOperation(value = "档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.PUT)
    public ResponseEntity<String> resolve(@ApiParam("id")
                                          @PathVariable("id") String packageId) throws Exception {

        MPackage pack = packageMgrClient.acquirePackage("RANDOM");
        if (pack != null) {
            String zipFile = downloadTo(pack.getRemotePath());

            Profile profile = resolver.doResolve(pack, zipFile);
            profileService.saveProfile(profile);

            packageMgrClient.reportStatus(packageId, ArchiveStatus.Finished,
                    "身份证号: " + profile.getDemographicId() + ", 档案: " + profile.getId());
        }

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation(value = "本地档案包解析，仅供测试用", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.POST)
    public ResponseEntity<String> resolve(@ApiParam(value = "档案包ID，忽略此值", defaultValue = "LocalPackage")
                                          @PathVariable("id") String packageId,
                                          @ApiParam("档案包文件")
                                          @RequestPart(required = true) MultipartFile file,
                                          @ApiParam("档案包密码")
                                          @RequestParam("password") String password,
                                          @ApiParam(value = "仅解析并返回，不入库", defaultValue = "true")
                                          @RequestParam("onlyEcho") boolean onlyEcho) throws FileNotFoundException {

        String zipFile = System.getProperty("java.io.tmpdir") + packageId + ".zip";

        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(zipFile)));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();

            MPackage pack = new MPackage();
            pack.setPwd(password);
            pack.setId(packageId);
            pack.setArchiveStatus(ArchiveStatus.Received);

            Profile profile = resolver.doResolve(pack, zipFile);
            if (!onlyEcho) {
                profileService.saveProfile(profile);
            }

            return new ResponseEntity<>(profile.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(";");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }
}
