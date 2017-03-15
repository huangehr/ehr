package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalNoticeDetailModel;
import com.yihu.ehr.agModel.portal.PortalNoticeModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.portal.MPortalNotice;
import com.yihu.ehr.portal.service.PortalNoticesClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeshijie on 2017/2/17.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "portalNotices", description = "通知公告接口", tags = {"通知公告接口"})
public class PortalNoticesController extends BaseController{

    @Autowired
    private PortalNoticesClient portalNoticesClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;


    @RequestMapping(value = "/portalNotices", method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告列表", notes = "根据查询条件获取通知公告列表在前端表格展示")
    public Envelop searchPortalNotices(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MPortalNotice>> responseEntity = portalNoticesClient.searchPortalNotices(fields, filters, sorts, size, page);
        List<MPortalNotice> mPortalNoticeList = responseEntity.getBody();
        List<PortalNoticeModel> portalNoticeModels = new ArrayList<>();
        for (MPortalNotice mPortalNotice : mPortalNoticeList) {
            PortalNoticeModel portalNoticeModel = convertToModel(mPortalNotice, PortalNoticeModel.class);
            portalNoticeModel.setReleaseDate(mPortalNotice.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalNotice.getReleaseDate()));

            //获取类别字典
            MConventionalDict dict = conventionalDictClient.getPortalNoticeTypeList(String.valueOf(mPortalNotice.getType()));
            portalNoticeModel.setTypeName(dict == null ? "" : dict.getValue());
            MConventionalDict dict2 = conventionalDictClient.getPortalNoticeTypeList(String.valueOf(mPortalNotice.getPortalType()));
            portalNoticeModel.setPortalTypeName(dict2 == null ? "" : dict2.getValue());
            portalNoticeModels.add(portalNoticeModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalNoticeModels, totalCount, page, size);
        return envelop;
    }


    @RequestMapping(value = "portalNotices/admin/{portalNotice_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取通知公告信息", notes = "通知公告信息")
    public Envelop getPortalNotice(
            @ApiParam(name = "portalNotice_id", value = "", defaultValue = "")
            @PathVariable(value = "portalNotice_id") Long portalNoticeId) {
        try {
            MPortalNotice mPortalNotice = portalNoticesClient.getPortalNotice(portalNoticeId);
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

    @RequestMapping(value = "/portalNotices/admin/{portalNotice_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除通知公告", notes = "根据通知公告id")
    public Envelop deletePortalNotice(
            @ApiParam(name = "portalNotice_id", value = "通知公告编号", defaultValue = "")
            @PathVariable(value = "portalNotice_id") String portalNoticeId) {
        try {
            boolean result = portalNoticesClient.deletePortalNotice(portalNoticeId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalNotice", method = RequestMethod.POST)
    @ApiOperation(value = "创建通知公告", notes = "重新绑定通知公告信息")
    public Envelop createDoctor(
            @ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalNotice_json_data") String portalNoticeJsonData) {
        try {
            PortalNoticeDetailModel detailModel = objectMapper.readValue(portalNoticeJsonData, PortalNoticeDetailModel.class);

            String errorMsg = null;

            if (StringUtils.isEmpty(detailModel.getTitle())) {
                errorMsg += "标题不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getContent())) {
                errorMsg += "内容不能为空!";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalNotice mPortalNotice = convertToMPortalNotice(detailModel);
            mPortalNotice = portalNoticesClient.createPortalNotice(objectMapper.writeValueAsString(mPortalNotice));
            if (mPortalNotice == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalNotice, PortalNoticeDetailModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalNotice", method = RequestMethod.PUT)
    @ApiOperation(value = "修改通知公告", notes = "重新绑定通知公告信息")
    public Envelop updatePortalNotice(
            @ApiParam(name = "portalNotice_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalNotice_json_data") String portalNoticeJsonData) {
        try {
            PortalNoticeDetailModel detailModel = toEntity(portalNoticeJsonData, PortalNoticeDetailModel.class);
            String errorMsg = null;
            if (StringUtils.isEmpty(detailModel.getTitle())) {
                errorMsg += "标题不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getContent())) {
                errorMsg += "内容不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalNotice mPortalNotice = convertToMPortalNotice(detailModel);
            mPortalNotice = portalNoticesClient.updatePortalNotice(objectMapper.writeValueAsString(mPortalNotice));
            if(mPortalNotice==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalNotice, PortalNoticeDetailModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public MPortalNotice convertToMPortalNotice(PortalNoticeDetailModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MPortalNotice mPortalNotice = convertToModel(detailModel,MPortalNotice.class);
        mPortalNotice.setReleaseDate(DateTimeUtil.simpleDateTimeParse(detailModel.getReleaseDate()));

        return mPortalNotice;
    }
}
