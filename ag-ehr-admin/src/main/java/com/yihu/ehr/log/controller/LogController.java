package com.yihu.ehr.log.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.log.service.LogClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoujie on 2017/3/11.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "log", description = "日志页面展示", tags = {"云门户-日志页面展示"})
public class LogController extends BaseController {

    @Autowired
    private LogClient logClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;


    @RequestMapping(value = "/getLogList", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务日志列表", notes = "根据查询条件业务日志列表在前端展示")
    public Envelop getLogList(
            @ApiParam(name = "logType", value = "日志类型", defaultValue = "")
            @RequestParam(value = "logType", required = false) String logType,
            @ApiParam(name = "data", value = "数据", defaultValue = "")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ListResult listResult = null;
        if(logType !=null){
            if(logType.equals("1")){
                listResult = logClient.getOperatorLogs(data, startDate, endDate, caller, sorts, size, page);
            }else if(logType.equals("2")){
                listResult = logClient.getBussinessLogs(data, startDate, endDate, caller, sorts, size, page);
            }
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = "/getLogByIdAndType", method = RequestMethod.GET)
    @ApiOperation(value = "获取log信息", notes = "log信息")
    public Envelop getLogByIdAndType(
            @ApiParam(name = "logId", value = "日志id", defaultValue = "")
            @RequestParam(value = "logId") String logId,
            @ApiParam(name = "logType", value = "日志类型", defaultValue = "2")
            @RequestParam(value = "logType") String logType) {
        try {
            ListResult listResult = null;
            if(logType !=null){
                if(logType.equals("1")){
                    listResult = logClient.getOperatorLogById(logId);
                }else if(logType.equals("2")){
                    listResult = logClient.getBussinessLogById(logId);
                }else{
                    listResult = logClient.getBussinessLogById(logId);
                }
            }else {
                Envelop envelop = new Envelop();
                return envelop;
            }
            if(null!=listResult&&listResult.getTotalCount() != 0){
                List<Map<String,Object>> list = listResult.getDetailModelList();
                if(null!=list&&list.size()>0&&null!=list.get(0).get("response")){
                    list.get(0).put("response", list.get(0).get("response").toString().replace("\"","\\"+"\""));
                    //"response" -> "{"result":{"successFlg":true,"message":"登录成功!","errorCode":0,"data":{"userCode":"admin","token":"78df2308-deea-42c3-8939-12b11a1a917f","schema":"hos1","tenant":"yichang","schemaTemp":"hos1","role":null}}}"
                }
                return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
            }else{
                Envelop envelop = new Envelop();
                return envelop;
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/searchListLogs", method = RequestMethod.GET)
    @ApiOperation(value = "获取业务日志列表,姓名模糊查询", notes = "根据查询条件业务日志列表在前端展示")
    public Envelop searchListLogs(
            @ApiParam(name = "patient", value = "操作者", defaultValue = "")
            @RequestParam(value = "patient", required = false) String patient,
            @ApiParam(name = "logType", value = "日志类型", defaultValue = "")
            @RequestParam(value = "logType", required = false) String logType,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        String data="";
        String sorts="";
        ListResult listResult = null;
        if(logType !=null){
            if(logType.equals("1")){
                listResult = logClient.getOperatorListLogs(patient,data, startDate, endDate, caller, sorts, size, page);
            }else if(logType.equals("2")){
                listResult = logClient.getBussinessListLogs(patient,data, startDate, endDate, caller, sorts, size, page);
            }
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @RequestMapping(value = "/getOperatorLogByAppKey", method = RequestMethod.GET)
    @ApiOperation(value = "根据id进行MONGODB日志的查询")
    public Envelop getOperatorLogByAppKey(
            @ApiParam(name = "appKey", value = "应用ID appkey就是appId", defaultValue = "")
            @RequestParam(value = "appKey", required = true) String appKey,
            @ApiParam(name = "responseFlag", value = "接口请求返回标识 1 成功 2 失败", defaultValue = "1")
            @RequestParam(value = "responseFlag", required = true) int responseFlag) throws Exception{

        return logClient.getOperatorLogByAppKey(appKey,responseFlag);
    }



}

