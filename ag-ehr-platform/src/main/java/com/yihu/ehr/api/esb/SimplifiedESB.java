package com.yihu.ehr.api.esb;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.SimplifiedESBClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Sand
 * @version 1.0
 * @created 2016.02.29 10:47
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/simplified-esb")
@Api(protocols = "https", value = "simplified-esb", description = "简易ESB服务临时接口")
public class SimplifiedESB {

    @Autowired
    private SimplifiedESBClient simplifiedESBClient;

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

    @ApiOperation("日志上传")
    @RequestMapping(value = "/uploadLog", method = RequestMethod.POST)
    public ResponseEntity<String> uploadLogger(@ApiParam("orgCode")
                                               @PathVariable("orgCode")
                                               String orgCode,
                                               @ApiParam("ip")
                                               @RequestParam("ip")
                                               String ip,
                                               @ApiParam("file")
                                               @RequestParam("file")
                                               MultipartFile file) {
        return new ResponseEntity<>(simplifiedESBClient.uploadLog(orgCode, ip, file) + "", HttpStatus.OK);
    }

    @ApiOperation("查询版本是否需要更新")
    @RequestMapping(value = "/getUpdateFlag", method = RequestMethod.POST)
    public ResponseEntity getUpdateFlag(@ApiParam("versionCode")
                                        @PathVariable("versionCode")
                                        String versionCode,
                                        @ApiParam("systemCode")
                                        @PathVariable("systemCode")
                                        String systemCode,
                                        @ApiParam("orgCode")
                                        @PathVariable("orgCode")
                                        String orgCode) {

        return new ResponseEntity<>(simplifiedESBClient.getUpdateFlag(versionCode, systemCode, orgCode), HttpStatus.OK);
    }


    @ApiOperation("下载项目")
    @RequestMapping(value = "/downUpdateWar", method = RequestMethod.POST)
    public ResponseEntity downUpdateWar(@ApiParam("systemCode")
                                        @PathVariable("systemCode")
                                        String systemCode,
                                        @ApiParam("orgCode")
                                        @PathVariable("orgCode")
                                        String orgCode
            , HttpServletResponse response) {

        return new ResponseEntity<>(simplifiedESBClient.downUpdateWar(systemCode, orgCode, response), HttpStatus.OK);
    }

    @ApiOperation("上传客户端升级信息")
    @RequestMapping(value = "/uploadResult", method = RequestMethod.POST)
    public ResponseEntity uploadResult(
            @ApiParam("systemCode")
            @PathVariable("systemCode")
            String systemCode,
            @ApiParam("orgCode")
            @PathVariable("orgCode")
            String orgCode,
            @ApiParam("versionCode")
            @PathVariable("versionCode")
            String versionCode,
            @ApiParam("versionName")
            @PathVariable("versionName")
            String versionName,
            @ApiParam("updateDate")
            @PathVariable("updateDate")
            String updateDate) {

        return new ResponseEntity<>(simplifiedESBClient.uploadResult(systemCode, orgCode, versionCode, versionName, updateDate), HttpStatus.OK);
    }

    @ApiOperation("补采功能")
    @RequestMapping(value = "/fillMining", method = RequestMethod.POST)
    public ResponseEntity fillMining(
            @ApiParam("systemCode")
            @PathVariable("systemCode")
            String systemCode,
            @ApiParam("orgCode")
            @PathVariable("orgCode")
            String orgCode) {

        return new ResponseEntity<>(simplifiedESBClient.fillMining(systemCode, orgCode), HttpStatus.OK);
    }

    @ApiOperation("补采功能")
    @RequestMapping(value = "/changeFillMiningStatus", method = RequestMethod.POST)
    public ResponseEntity changeFillMiningStatus(
            @ApiParam("result")
            @PathVariable("result")
            String result,
            @ApiParam("message")
            @PathVariable("message")
            String message,
            @ApiParam("id")
            @PathVariable("id")
            String id,
            @ApiParam("status")
            @PathVariable("status")
            String status) {
        return new ResponseEntity<>(simplifiedESBClient.changeFillMiningStatus(result, message, id, status), HttpStatus.OK);
    }

    @ApiOperation(" his穿透查询")
    @RequestMapping(value = "/hisPenetration", method = RequestMethod.POST)
    public ResponseEntity hisPenetration(
            @ApiParam("systemCode")
            @PathVariable("systemCode")
            String systemCode,
            @ApiParam("orgCode")
            @PathVariable("orgCode")
            String orgCode) {
        return new ResponseEntity<>(simplifiedESBClient.hisPenetration(systemCode, orgCode), HttpStatus.OK);
    }

    @ApiOperation("修改his穿透查询状态")
    @RequestMapping(value = "/changeHisPenetrationStatus", method = RequestMethod.POST)
    public ResponseEntity changeHisPenetrationStatus(
            @ApiParam("result")
            @PathVariable("result")
            String result,
            @ApiParam("status")
            @PathVariable("status")
            String status,
            @ApiParam("id")
            @PathVariable("id")
            String id) {
        return new ResponseEntity<>(simplifiedESBClient.changeHisPenetrationStatus(result, status, id), HttpStatus.OK);
    }
}
