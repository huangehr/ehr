package com.yihu.ehr.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.feign.XPackageMgrClient;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.core.commons.Profile;
import com.yihu.ehr.profile.core.lightweight.LightWeightProfile;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.core.unStructured.UnStructuredDocumentFile;
import com.yihu.ehr.profile.core.unStructured.UnStructuredProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import com.yihu.ehr.service.LightWeihgtPackageResolver;
import com.yihu.ehr.service.PackageResolver;
import com.yihu.ehr.service.StructuredPackageResolver;
import com.yihu.ehr.service.UnStructuredPackageResolver;
import com.yihu.ehr.util.compress.Zipper;
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
import java.util.ArrayList;
import java.util.List;

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
        profileRepo.save(profile);

        packageMgrClient.reportStatus(pack.getId(), ArchiveStatus.Finished,
                "Identity: " + profile.getDemographicId() + ", profile: " + profile.getId());

        return new ResponseEntity<>(echo ? profile.toJson() : "", HttpStatus.OK);
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
            StructuredProfile structuredProfile = new StructuredProfile();          //结构化档案
            UnStructuredProfile unStructuredProfile = new UnStructuredProfile();    //非结构化档案
            LightWeightProfile lightWeightProfile = new LightWeightProfile();       //轻量级档案




            //// TODO: 2016/4/14  类型判断
            Profile profile = doResolve(pack, zipFile);





            if (!persist) {
                profileRepo.save(profile);
            }

            return new ResponseEntity<>(profile.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }


    /**
     * 执行归档作业。归档作为流程如下：
     * 1. 从JSON档案管理器中获取一个待归档的JSON文档，并标记为Acquired，表示正在归档，并记录开始时间。
     * 2. 解压zip档案包，如果解压失败，或检查解压后的目录结果不符合规定，将文档状态标记为 Failed，记录日志并返回。
     * 3. 读取包中的 origin, standard 文件夹中的 JSON 数据并解析。
     * 4. 对关联字典的数据元进行标准化，将字典的值直接写入数据
     * 5. 解析完的数据存入HBase，并将JSON文档的状态标记为 Finished。
     * 6. 以上步骤有任何一个失败的，将文档标记为 InDoubt 状态，即无法决定该JSON档案的去向，需要人为干预。
     * <p>
     * ObjectMapper Stream API使用，参见：http://wiki.fasterxml.com/JacksonStreamingApi
     */
    public Object doResolve(MPackage pack, String zipFile) throws Exception {
        File root = new Zipper().unzipFile(new File(zipFile), LocalTempPath + PathSep + pack.getId(), pack.getPwd());
        if (root == null || !root.isDirectory() || root.list().length == 0) {
            throw new RuntimeException("Invalid package file, package id: " + pack.getId());
        }


        StructuredProfile structuredProfile = new StructuredProfile();          //结构化档案
        UnStructuredProfile unStructuredProfile = new UnStructuredProfile();    //非结构化档案
        LightWeightProfile lightWeightProfile = new LightWeightProfile();       //轻量级档案

        File[] files = root.listFiles();
        String firstFilepath = files[0].getPath();
        String firstFolderName =  firstFilepath.substring(firstFilepath.lastIndexOf("\\")+1);

        List<UnStructuredDocumentFile> unStructuredDocumentFileList = new ArrayList<>();  //document底下的文件
        for(File file:files){
            String folderName = file.getPath().substring(file.getPath().lastIndexOf("\\")+1);
            switch (firstFolderName){
                case OriFolder:
                    //结构化档案报处理
                    structuredProfile = structuredPackageResolver.structuredDataSetParse(structuredProfile, file.listFiles(),folderName);
                    break;
                case IndexFolder:
                    //轻量级档案包处理
                    lightWeightProfile = lightWeihgtPackageResolver.lightWeightDataSetParse(lightWeightProfile,file.listFiles());
                case DocumentFolder:
                    //非结构化档案包处理
                    if(folderName.equals(DocumentFolder)){
                        unStructuredDocumentFileList = unStructuredPackageResolver.unstructuredDocumentParse(unStructuredProfile, file.listFiles());
                    }else if(folderName.equals("meta.json")){
                        unStructuredProfile = unStructuredPackageResolver.unstructuredDataSetParse(unStructuredProfile,file, unStructuredDocumentFileList);
                    }
                    break;
                default: break;
            }
        }


        return structuredProfile;
    }



    private final static String LocalTempPath = System.getProperty("java.io.tmpdir");

    private String downloadTo(String filePath) throws Exception {
        String[] tokens = filePath.split(":");
        return fastDFSUtil.download(tokens[0], tokens[1], LocalTempPath);
    }
}
