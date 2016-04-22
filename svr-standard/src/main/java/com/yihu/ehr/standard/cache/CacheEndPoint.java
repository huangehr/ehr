package com.yihu.ehr.standard.cache;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.standard.MCDAVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "standard cache", description = "标准缓存服务")
public class CacheEndPoint {
    @Autowired
    StdCache stdCache;

    @ApiOperation("缓存标准")
    @RequestMapping(value = ServiceApi.Caches.Versions, method = RequestMethod.PUT)
    public void versions(@ApiParam(value = "版本列表，使用逗号分隔", defaultValue = "000000000000,568ce002559f")
                         @RequestParam("versions") String versions,
                         @ApiParam(value = "强制清除再缓存", defaultValue = "true")
                         @RequestParam("force") boolean force) {
        for (String version : versions.split(",")) {
            stdCache.cacheData(version, force);
        }
    }

    @ApiOperation("获取缓存版本列表")
    @RequestMapping(value = ServiceApi.Caches.Versions, method = RequestMethod.GET)
    public ResponseEntity<List<MCDAVersion>> versions() {
        List<MCDAVersion> versions = stdCache.versions();
        if(versions.isEmpty()) throw new ApiException(HttpStatus.NOT_FOUND, "Not cached yet.");

        return new ResponseEntity<>(versions, HttpStatus.OK);
    }

    @ApiOperation("获取缓存版本")
    @RequestMapping(value = ServiceApi.Caches.Version, method = RequestMethod.GET)
    public ResponseEntity<MCDAVersion> version(@ApiParam(value = "version", defaultValue = "568ce002559f")
                                               @PathVariable("version") String version) {
        MCDAVersion mcdaVersion = stdCache.version(version);
        if (null == mcdaVersion) throw new ApiException(HttpStatus.NOT_FOUND, "Not cached yet.");

        return new ResponseEntity<>(mcdaVersion, HttpStatus.OK);
    }
}
