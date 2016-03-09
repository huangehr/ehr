package com.yihu.ehr.api.esb;

import com.yihu.ehr.constants.ApiVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.29 10:47
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/simplified-esb")
@Api(protocols = "https", value = "simplified-esb", description = "简易ESB服务临时接口")
public class SimplifiedESB {
    @ApiOperation("获取版本列表")
    @RequestMapping(value = "/applications", method = RequestMethod.GET)
    public ResponseEntity<Json> getApplications() {
        return new ResponseEntity<>(new Json(""), HttpStatus.OK);
    }

    @ApiOperation("获取版本")
    @RequestMapping(value = "/application/{id}", produces = "application/binary", method = RequestMethod.GET)
    public ResponseEntity<Json> getApplication(@ApiParam(value = "id", defaultValue = "latest")
                                               @PathVariable("id")
                                               String id) {
        return new ResponseEntity<>(new Json(""), HttpStatus.OK);
    }

    @ApiOperation("登记版本升级信息")
    @RequestMapping(value = "/application/upgrade/{org_code}", produces = "application/binary", method = RequestMethod.POST)
    public ResponseEntity<Json> createApplication(@ApiParam(value = "org_code")
                                                  @PathVariable("org_code")
                                                  String org_code,
                                                  @ApiParam(value = "info")
                                                  @RequestParam("info")
                                                  String info) {
        return new ResponseEntity<>(new Json(""), HttpStatus.OK);
    }

    @ApiOperation("获取HIS穿透查询SQL")
    @RequestMapping(value = "/sql", method = RequestMethod.GET)
    public ResponseEntity<String> getSQL(@ApiParam("org_code")
                                         @RequestParam("org_code")
                                         String orgCode,
                                         @ApiParam("system_code")
                                         @RequestParam("system_code")
                                         String systemCode) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation("提交HIS穿透查询结果")
    @RequestMapping(value = "/sql", method = RequestMethod.POST)
    public ResponseEntity<String> postSQLResult(@ApiParam("id")
                                                @RequestParam("id")
                                                String id,
                                                @ApiParam("result")
                                                @RequestParam("result")
                                                String result,
                                                @ApiParam("message")
                                                @RequestParam("message")
                                                String message) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation("补采任务")
    @RequestMapping(value = "/tasks/refetch", method = RequestMethod.GET)
    public ResponseEntity<String> refetchTask(@ApiParam("org_code")
                                                @RequestParam("org_code")
                                                String orgCode,
                                                @ApiParam("system_code")
                                                @RequestParam("system_code")
                                                String systemCode) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation("补采任务")
    @RequestMapping(value = "/tasks/refetch", method = RequestMethod.POST)
    public ResponseEntity<String> checkRefetchTaskResult(@ApiParam("org_code")
                                                @RequestParam("org_code")
                                                String orgCode,
                                                @ApiParam("system_code")
                                                @RequestParam("system_code")
                                                String systemCode) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @ApiOperation("日志上传")
    @RequestMapping(value = "/log/{org_code}", method = RequestMethod.POST)
    public ResponseEntity<String> uploadLogger(@ApiParam("org_code")
                                               @PathVariable("org_code")
                                               String orgCode,
                                               @ApiParam("ip")
                                               @RequestParam("ip")
                                               String ip,
                                               @ApiParam("file")
                                               @RequestParam("file")
                                               String file,
                                               @ApiParam("log")
                                               @RequestParam("log")
                                               MultipartFile log) {
        return new ResponseEntity<>("", HttpStatus.OK);
    }
}
