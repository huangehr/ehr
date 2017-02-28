package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalNoticeDetailModel;
import com.yihu.ehr.agModel.portal.PortalNoticeModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.portal.service.PortalNoticeClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MPortalNotice;
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
@RequestMapping(ApiVersion.Version1_0 +"/doctor")
@RestController
@Api(value = "PortalNotice", description = "PortalNotice", tags = {"通知公告"})
public class PortalNoticeController extends BaseController {

    @Autowired
    PortalNoticeClient portalNoticeClient;

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public Envelop getPortalNoticeTop10(){
        ResponseEntity<List<MPortalNotice>> responseEntity = portalNoticeClient.getPortalNoticeTop10();
        List<MPortalNotice> mPortalNoticeList = responseEntity.getBody();
        List<PortalNoticeModel> portalNoticeModels = new ArrayList<>();
        for (MPortalNotice mPortalNotice : mPortalNoticeList) {
            PortalNoticeModel portalNoticeModel = convertToModel(mPortalNotice, PortalNoticeModel.class);
            portalNoticeModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));

            portalNoticeModels.add(portalNoticeModel);
        }

        Envelop envelop = getResult(portalNoticeModels, mPortalNoticeList.size(), 1, 10);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告列表", notes = "根据查询条件获取通知公告列表在前端表格展示")
    public Envelop searchPortalNotices(
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
        ResponseEntity<List<MPortalNotice>> responseEntity = portalNoticeClient.searchPortalNotices(null, filters, sorts, size, page);
        List<MPortalNotice> mPortalNoticeList = responseEntity.getBody();
        List<PortalNoticeModel> portalNoticeModels = new ArrayList<>();
        for (MPortalNotice mPortalNotice : mPortalNoticeList) {
            PortalNoticeModel portalNoticeModel = convertToModel(mPortalNotice, PortalNoticeModel.class);
            portalNoticeModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));

            portalNoticeModels.add(portalNoticeModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalNoticeModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告信息", notes = "通知公告信息")
    public Envelop getPortalNotice(@PathVariable(value = "portalNotice_id") Long portalNoticeId){
        try {
            MPortalNotice mPortalNotice = portalNoticeClient.getPortalNotice(portalNoticeId);
            if (mPortalNotice == null) {
                return failed("通知公告信息获取失败!");
            }

            PortalNoticeDetailModel detailModel = convertToModel(mPortalNotice, PortalNoticeDetailModel.class);
            detailModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));

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
