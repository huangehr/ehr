package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosLogClient;
import com.yihu.ehr.model.esb.MHosLog;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @Autowired
    private OrganizationClient organizationClient;

    @RequestMapping(value = "/searchHosLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取上传日志信息", notes = "根据查询条件获取用户列表在前端表格展示")
    public Envelop searchHosLogs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ResponseEntity<List<MHosLog>> responseEntity = hosLogClient.searchHosLogs(fields, filters,sorts,size,page);
        List<MHosLog> hosLogs = responseEntity.getBody();
        if(hosLogs.size()>0){
            for (MHosLog mHosLog :hosLogs){
                if(StringUtils.isNotBlank(mHosLog.getUploadTime())){
                    SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mHosLog.setUploadTime(sm.format(sm.parse(mHosLog.getUploadTime())));
                }
                if(StringUtils.isNotBlank(mHosLog.getOrgCode())){
                    //过滤特殊字符，防止GET出错
                    String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                    Pattern p   =   Pattern.compile(regEx);
                    Matcher m   =   p.matcher(mHosLog.getOrgCode());
                    MOrganization mOrganization =  organizationClient.getOrg(m.replaceAll("").trim());

                    if(mOrganization!=null){
                        mHosLog.setOrgName(mOrganization.getFullName());
                    }
                }

            }
        }
        int totalCount = getTotalCount (responseEntity);
        return getResult(hosLogs, totalCount, page, size);
    }



    @RequestMapping(value = "/deleteHosLog/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除上传日志", notes = "根据id删除上传日志")
    public boolean deleteHosLog(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosLogClient.deleteHosLog(id);
    }

    @RequestMapping(value = "/deleteHosLogs", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除上传日志", notes = "根据id删除上传日志")
    public boolean deleteHosLogs(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) throws Exception {
        return hosLogClient.deleteHosLogs(filters);
    }
}