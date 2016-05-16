package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosLogClient;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.esb.MHosLog;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/esb")
@RestController
@Api(value = "上传日志管理接口", description = "上传日志管理接口" ,tags = {"上传日志管理接口"})
public class HosLogController extends BaseController {

    @Autowired
    private HosLogClient hosLogClient;

    @RequestMapping(value = "/searchHosLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取上传日志信息", notes = "根据查询条件获取用户列表在前端表格展示")
    public List<MHosLog> searchHosLogs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ResponseEntity<List<MHosLog>> responseEntity = hosLogClient.searchHosLogs(fields, filters,sorts,size,page);
        List<MHosLog> hosLogs = responseEntity.getBody();

        return hosLogs;
    }



    @RequestMapping(value = "/deleteHosLogs/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除上传日志", notes = "根据id删除上传日志")
    public boolean deleteHosLogs(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosLogClient.deleteHosLogs(id);
    }
}