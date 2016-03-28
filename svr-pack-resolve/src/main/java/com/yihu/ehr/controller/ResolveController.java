package com.yihu.ehr.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.profile.Profile;
import com.yihu.ehr.task.PackageResolver;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping(ApiVersion.Version1_0)
public class ResolveController {

    @Autowired
    PackageResolver resolver;

    @ApiOperation("档案包入库")
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.PUT)
    public ResponseEntity<String> resolve(@ApiParam("id")
                        @PathVariable("id") String packageId) {

        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation("本地档案包入库")
    @RequestMapping(value = RestApi.Packages.Package, method = RequestMethod.POST)
    public ResponseEntity<String> resolve(@ApiParam(value = "id", defaultValue = "NotNecessary")
                                        @PathVariable("id") String packageId,
                                        @ApiParam("file")
                                        @RequestParam("file") InputStream file,
                                        @ApiParam("password")
                                        @RequestParam("password") String password,
                                        @ApiParam("echo")
                                        @RequestParam("echo") boolean echo) throws FileNotFoundException {
        try{
            String zipFile = System.getProperty("system.io.temp") + File.pathSeparator + packageId + ".zip";
            OutputStream outputStream = new FileOutputStream(zipFile);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = file.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            MPackage pack = new MPackage();
            pack.setPwd(password);
            pack.setId(packageId);
            pack.setArchiveStatus(ArchiveStatus.Received);

            Profile profile = resolver.doResolve(pack, zipFile);

            return new ResponseEntity<>(profile.toJson(), HttpStatus.OK);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.EXPECTATION_FAILED, ErrorCode.SystemError, e.getMessage());
        }
    }
}
