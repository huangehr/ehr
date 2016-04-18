package com.yihu.ehr.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ProfileType;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.lightweight.LightWeightProfile;
import com.yihu.ehr.profile.core.nostructured.UnStructuredProfile;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.service.LightWeihgtPackageResolver;
import com.yihu.ehr.service.PackageResolver;
import com.yihu.ehr.service.StructuredPackageResolver;
import com.yihu.ehr.service.UnStructuredPackageResolver;
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
public class ResolveEndPoint {
    @Autowired
    PackageResolver resolver;

    @Autowired
    ProfileRepository profileRepo;

    @Autowired
    FastDFSUtil fastDFSUtil;

    @Autowired
    XPackageMgrClient packageMgrClient;

    @Autowired
    private StructuredPackageResolver structuredPackageResolver;

    @Autowired
    private UnStructuredPackageResolver unStructuredPackageResolver;

    @Autowired
    private LightWeihgtPackageResolver lightWeihgtPackageResolver;


    private final static char PathSep = File.separatorChar;
    private final static String StdFolder = "standard";
    private final static String OriFolder = "origin";
    private final static String IndexFolder = "index";
    private final static String DocumentFolder = "document";
    private final static String JsonExt = ".json";

    @ApiOperation(value = "档案包入库", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.PUT)
    public ResponseEntity<String> resolve(
            @ApiParam("id")
            @PathVariable("id") String packageId,
            @ApiParam(value = "返回档案数据", defaultValue = "false")
            @RequestParam("echo") boolean echo) throws Exception {

        MPackage pack = packageMgrClient.getPackage(packageId);
        if (pack == null) throw new ApiException(HttpStatus.NOT_FOUND, "Package not found.");

        String zipFile = downloadTo(pack.getRemotePath());

        Profile profile = resolver.doResolve(pack, zipFile);

        //// TODO: 2016/4/15
        //profileRepo.save(profile);

        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                "Identity: " + profile.getDemographicId() + ", profile: " + profile.getId());

        //return new ResponseEntity<>(echo ? profile.toJson() : "", HttpStatus.OK);
        return null;
    }

    @ApiOperation(value = "本地档案包解析", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.POST)
    public ResponseEntity<String> resolve(
            @ApiParam(value = "档案包ID，忽略此值", defaultValue = "LocalPackage")
            @PathVariable("id") String packageId,
            @ApiParam("档案包文件")
            @RequestPart() MultipartFile file,
            @ApiParam("档案包密码")
            @RequestParam("password") String password,
            @ApiParam(value = "是否入库")
            @RequestParam(value = "persist", defaultValue = "false") boolean persist) throws FileNotFoundException {

        String zipFile = System.getProperty("java.io.tmpdir") + packageId + ".zip";

        try {
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(zipFile)));
            FileCopyUtils.copy(file.getInputStream(), stream);
            stream.close();

            MPackage pack = new MPackage();
            pack.setPwd(password);
            pack.setId(packageId);
            pack.setArchiveStatus(ArchiveStatus.Received);


            //三种档案
            StructuredProfile structuredProfile;           //结构化档案
            UnStructuredProfile unStructuredProfile;       //非结构化档案
            LightWeightProfile lightWeightProfile;         //轻量级档案

            ProfileType profileType = resolver.getProfileType(pack, zipFile);
            if (profileType == ProfileType.Structured) {
                structuredProfile = structuredPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveStructuredProfile(structuredProfile);
                return new ResponseEntity<>(structuredProfile.toJson(), HttpStatus.OK);
            } else if (profileType == ProfileType.NoStructured) {
                unStructuredProfile = unStructuredPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveUnStructuredProfile(unStructuredProfile);
                //return new ResponseEntity<>(unStructuredProfile.toJson(), HttpStatus.OK);
            } else if (profileType == ProfileType.Lightweight) {
                lightWeightProfile = lightWeihgtPackageResolver.doResolve(pack, zipFile);
                profileRepo.saveLightWeightProfile(lightWeightProfile);
                return new ResponseEntity<>(lightWeightProfile.toJson(), HttpStatus.OK);
            }
            return null;

//            if (!persist) {
//                profileRepo.save(profile);
//            }

            //return new ResponseEntity<>(profile.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }


    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }
}
