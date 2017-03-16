package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalResourcesModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MPortalResources;
import com.yihu.ehr.portal.service.PortalResourcesClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ysj on 2017年2月20日
 */
@RequestMapping(ApiVersion.Version1_0 +"/portal")
@RestController
@Api(value = "PortalResources", description = "PortalResources", tags = {"资源信息"})
public class PortalResourcesController extends BaseController {

    @Autowired
    PortalResourcesClient portalResourcesClient;

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息所有数据", notes = "查询所有数据在前端表格展示")
    public Envelop getAllPortalResources(){
        ResponseEntity<List<MPortalResources>> responseEntity = portalResourcesClient.getAllPortalResources();
        List<MPortalResources> mPortalResourcesList = responseEntity.getBody();
        List<PortalResourcesModel> portalResourcesModels = new ArrayList<>();
        for (MPortalResources mPortalResources : mPortalResourcesList) {
            PortalResourcesModel portalResourcesModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            portalResourcesModel.setUploadTime(mPortalResources.getUploadTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalResources.getUploadTime()));
            portalResourcesModels.add(portalResourcesModel);
        }
        Envelop envelop = getResult(portalResourcesModels, mPortalResourcesList.size(), 1, mPortalResourcesList.size());
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PortalResources.PortalResources, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息列表", notes = "根据查询条件获取资源信息列表在前端表格展示")
    public Envelop searchPortalResources(
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page) throws Exception{
        String filters = "";
        if(!StringUtils.isEmpty(startTime)){
            filters += "releaseDate>"+changeToUtc(startTime)+";";
        }
        if(!StringUtils.isEmpty(endTime)){
            filters += "releaseDate<"+changeToUtc(endTime)+";";
        }
        String sorts = "+releaseDate";
        ResponseEntity<List<MPortalResources>> responseEntity = portalResourcesClient.searchPortalResources(null, filters, sorts, size, page);
        List<MPortalResources> mPortalResourcesList = responseEntity.getBody();
        List<PortalResourcesModel> portalResourcesModels = new ArrayList<>();
        for (MPortalResources mPortalResources : mPortalResourcesList) {
            PortalResourcesModel portalResourcesModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            portalResourcesModel.setUploadTime(mPortalResources.getUploadTime() == null ? "" : DateTimeUtil.simpleDateTimeFormat(mPortalResources.getUploadTime()));
            portalResourcesModels.add(portalResourcesModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(portalResourcesModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PortalResources.PortalResourcesAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息信息", notes = "资源信息信息")
    public Envelop getPortalResources(@PathVariable(value = "portalResources_id") Long portalResourcesId){
        try {
            MPortalResources mPortalResources = portalResourcesClient.getPortalResources(portalResourcesId);
            if (mPortalResources == null) {
                return failed("资源信息信息获取失败!");
            }

            PortalResourcesModel detailModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            detailModel.setUploadTime(mPortalResources.getUploadTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalResources.getUploadTime()));

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    //yyyy-MM-dd HH:mm:ss 转换为yyyy-MM-dd'T'HH:mm:ss'Z 格式
    public String changeToUtc(String datetime) throws Exception{
        Date date = DateTimeUtil.simpleDateTimeParse(datetime);
        return DateTimeUtil.utcDateTimeFormat(date);
    }

}
