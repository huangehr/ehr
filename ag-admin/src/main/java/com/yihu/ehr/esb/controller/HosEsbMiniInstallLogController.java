package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosEsbMiniInstallLogClient;
import com.yihu.ehr.model.esb.MHosEsbMiniInstallLog;
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

import java.text.ParseException;
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
@Api(value = "程序版本发布信息管理接口", description = "程序版本发布信息管理接口" ,tags = {"程序版本发布信息管理接口"})
public class HosEsbMiniInstallLogController extends BaseController {

    @Autowired
    private HosEsbMiniInstallLogClient hosEsbMiniInstallLogClient;
    @Autowired
    private OrganizationClient organizationClient;

    @RequestMapping(value = "/searchHosEsbMiniInstallLog", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取程序版本发布信息", notes = "根据查询条件获取程序版本发布信息")
    public Envelop searchHosEsbMiniInstallLogs(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws ParseException {
            ResponseEntity<List<MHosEsbMiniInstallLog>> responseEntity = hosEsbMiniInstallLogClient.searchHosEsbMiniInstallLogs(fields, filters, sorts, size, page);
        List<MHosEsbMiniInstallLog> mHosEsbMiniInstallLogList = responseEntity.getBody();
        if(mHosEsbMiniInstallLogList.size()>0){
            for(MHosEsbMiniInstallLog mHosEsbMiniInstallLog:mHosEsbMiniInstallLogList){
                if(StringUtils.isNotBlank(mHosEsbMiniInstallLog.getOrgCode())){
                    String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                    Pattern p   =   Pattern.compile(regEx);
                    Matcher m   =   p.matcher(mHosEsbMiniInstallLog.getOrgCode());
                    MOrganization mOrganization =  organizationClient.getOrg(m.replaceAll("").trim());
                    if(mOrganization!=null){
                        mHosEsbMiniInstallLog.setOrgName(mOrganization.getFullName());
                    }
                }
                if(mHosEsbMiniInstallLog.getInstallTime()!=null){
                    SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mHosEsbMiniInstallLog.setInstallDate(sm.format(mHosEsbMiniInstallLog.getInstallTime()));
                }
            }
        }
        int totalCount = getTotalCount (responseEntity);
        return getResult(mHosEsbMiniInstallLogList, totalCount, page, size);
    }
}