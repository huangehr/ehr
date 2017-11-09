package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsMonitorTypeReportModel;
import com.yihu.ehr.agModel.user.RoleOrgModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.resource.MRsMonitorTypeReport;
import com.yihu.ehr.model.resource.MRsReportMonitorType;
import com.yihu.ehr.model.user.MRoleOrg;
import com.yihu.ehr.resource.client.RsMonitorTypeReportClient;
import com.yihu.ehr.resource.client.RsReportMonitorTypeClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 资源监测类型报表配置 controller
 *
 * @author jansney
 * @created 2017.11.8 14:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "metadata", description = "资源监测类型报表配置服务接口", tags = {"资源管理-资源监测类型报表配置服务接口"})
public class RsMonitorTypeReportController extends BaseController {

    @Autowired
    private RsMonitorTypeReportClient rsMonitorTypeReportClient;
    @Autowired
    private RsReportMonitorTypeClient rsReportMonitorTypeClient;

    @ApiOperation(value = "根据资源监测类型ID，获取资源监测类型报表配置")
    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.POST)
    public Envelop createRsMonitorTypeReport(
            @ApiParam(name = "data_json",value = "角色组-机构关系Json串")
            @RequestParam(value = "data_json") String dataJson){
        MRsMonitorTypeReport mMonitorTypeReport = rsMonitorTypeReportClient.createMonitorTypeReport(dataJson);
        if(null == mMonitorTypeReport){
            return failed("角色组添加机构失败！");
        }
        return success(mMonitorTypeReport);
    }

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,删除角色组机构")
    public Envelop deleteRsMonitorTypeReport(
            @ApiParam(name = "reportId",value = "报表ID")
            @RequestParam(value = "reportId") String reportId,
            @ApiParam(name = "monitorTypeId",value = "监测类型ID")
            @RequestParam(value = "monitorTypeId") String monitorTypeId){
        if(StringUtils.isEmpty(reportId)){
            return failed("报表ID不能为空！");
        }
        if(StringUtils.isEmpty(monitorTypeId)){
            return failed("监测类型ID不能为空！");
        }
        boolean bo = rsMonitorTypeReportClient.deleteMonitorTypeReport(reportId, monitorTypeId);
        if(bo){
            return success(null);
        }
        return failed("角色组删除机构失败！");
    }

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReports,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---分页")
    public Envelop searchRsMonitorTypeReport(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,orgId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        List<RsMonitorTypeReportModel> rsMonitorTypeReportModelList = new ArrayList<>();
        ResponseEntity<Collection<MRsMonitorTypeReport>> responseEntity = rsMonitorTypeReportClient.searchRsMonitorTypeReports(fields, filters, sorts, size, page);
        Collection<MRsMonitorTypeReport> mRsMonitorTypeReports  = responseEntity.getBody();
        for (MRsMonitorTypeReport m : mRsMonitorTypeReports){
            RsMonitorTypeReportModel rsMonitorTypeReportModel = objectMapper.convertValue(m, RsMonitorTypeReportModel.class);
            if(rsMonitorTypeReportModel != null ){
                MRsReportMonitorType mRsReportMonitorType = rsReportMonitorTypeClient.getById(rsMonitorTypeReportModel.getRsReoportMonitorTypeId());
                if(mRsReportMonitorType != null){
                    rsMonitorTypeReportModel.setRsReoportMonitorTypeName(mRsReportMonitorType.getName());
                }
            }
            rsMonitorTypeReportModelList.add(rsMonitorTypeReportModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(rsMonitorTypeReportModelList,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReportsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---不分页")
    public Envelop searchReportMonitorTypesNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RsMonitorTypeReportModel> roleOrgModelList = new ArrayList<>();
        Collection<MRsMonitorTypeReport> mRsMonitorTypeReport = rsMonitorTypeReportClient.searchRsMonitorTypeReportsNoPage(filters);
        for (MRsMonitorTypeReport m : mRsMonitorTypeReport){
            RsMonitorTypeReportModel roleOrgModel = objectMapper.convertValue(m, RsMonitorTypeReportModel.class);
            roleOrgModelList.add(roleOrgModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(roleOrgModelList);
        return envelop;
    }

}
