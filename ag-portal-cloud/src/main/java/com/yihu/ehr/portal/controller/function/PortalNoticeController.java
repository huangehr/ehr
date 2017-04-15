package com.yihu.ehr.portal.controller.function;

import com.yihu.ehr.agModel.portal.PortalNoticeModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.portal.MPortalNotice;
import com.yihu.ehr.portal.service.function.PortalNoticeClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
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
@RequestMapping(ApiVersion.Version1_0 +"/portal/doctor")
@RestController
@Api(value = "PortalNotice", description = "PortalNotice", tags = {"云门户-通知公告"})
public class PortalNoticeController extends BaseController {

    @Autowired
    PortalNoticeClient portalNoticeClient;

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticesTop, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告前10数据", notes = "根据日期查询前10的数据在前端表格展示")
    public Result getPortalNoticeTop10(){
        try {
            ResponseEntity<List<MPortalNotice>> responseEntity = portalNoticeClient.getPortalNoticeTop10();
            List<MPortalNotice> mPortalNoticeList = responseEntity.getBody();
            List<PortalNoticeModel> portalNoticeModels = new ArrayList<>();
            for (MPortalNotice mPortalNotice : mPortalNoticeList) {
                PortalNoticeModel portalNoticeModel = convertToModel(mPortalNotice, PortalNoticeModel.class);
                portalNoticeModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));
                portalNoticeModels.add(portalNoticeModel);
            }
            ListResult re = new ListResult(1,10);
            re.setTotalCount(portalNoticeModels.size());
            re.setDetailModelList(portalNoticeModels);

            return re;
        }
        catch (Exception ex)
        {
            return Result.error(ex.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNotices, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告列表", notes = "根据查询条件获取通知公告列表在前端表格展示")
    public Result searchPortalNotices(
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page) throws Exception{
        try {
            String filters = "";
            if(!StringUtils.isEmpty(startTime)){
                filters += "releaseDate>"+changeToUtc(startTime)+";";
            }
            if(!StringUtils.isEmpty(endTime)){
                filters += "releaseDate<"+changeToUtc(endTime)+";";
            }
            String sorts = "+releaseDate";
            ResponseEntity<List<MPortalNotice>> responseEntity = portalNoticeClient.searchPortalNotices(null, filters, sorts, size, page);

            ListResult re = new ListResult(page,size);
            re.setTotalCount(getTotalCount(responseEntity));
            re.setDetailModelList(responseEntity.getBody());
            return re;
        }
        catch (Exception ex)
        {
            return Result.error(ex.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.PortalNotices.PortalNoticeAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告信息", notes = "通知公告信息")
    public Result getPortalNotice(@PathVariable(value = "portalNotice_id") Long portalNoticeId){
        try {
            MPortalNotice mPortalNotice = portalNoticeClient.getPortalNotice(portalNoticeId);
            if (mPortalNotice == null) {
                return Result.error("通知公告信息获取失败!");
            }
            ObjectResult re = new ObjectResult(true,"通知公告信息获取成功！");
            PortalNoticeModel portalNoticeModel = convertToModel(mPortalNotice, PortalNoticeModel.class);
            portalNoticeModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));
            re.setData(portalNoticeModel);
            return re;
        }
        catch (Exception ex){
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }

    //yyyy-MM-dd HH:mm:ss 转换为yyyy-MM-dd'T'HH:mm:ss'Z 格式
    private String changeToUtc(String datetime) throws Exception{
        Date date = DateTimeUtil.simpleDateTimeParse(datetime);
        return DateTimeUtil.utcDateTimeFormat(date);
    }

}
