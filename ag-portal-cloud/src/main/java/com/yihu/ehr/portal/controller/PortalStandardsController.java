package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalStandardModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MPortalStandards;
import com.yihu.ehr.portal.service.PortalStandardsClient;
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
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/doctor")
@RestController
@Api(value = "portalStandards", description = "PortalStandards", tags = {"标准规范管理接口"})
public class PortalStandardsController extends BaseController{

    @Autowired
    private PortalStandardsClient portalStandardsClient;


    @RequestMapping(value = "/portalStandards", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范管理列表", notes = "根据查询条件获取标准规范管理列表在前端表格展示")
    public Envelop searchPortalStandards(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        ResponseEntity<List<MPortalStandards>> responseEntity = portalStandardsClient.searchPortalStandards(fields, filters, sorts, size, page);
        List<MPortalStandards> mPortalStandardsList = responseEntity.getBody();
        List<PortalStandardModel>  portalStandardModels = new ArrayList<>();
        for (MPortalStandards mPortalStandards : mPortalStandardsList) {
            PortalStandardModel portalStandardModel = convertToModel(mPortalStandards, PortalStandardModel.class);
            portalStandardModel.setReleaseDate(mPortalStandards.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalStandards.getReleaseDate()));

            portalStandardModels.add(portalStandardModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalStandardModels, totalCount, page, size);
        return envelop;
    }


    @RequestMapping(value = "portalStandards/admin/{portalStandard_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取标准规范管理信息", notes = "标准规范管理信息")
    public Envelop getPortalStandard(
            @ApiParam(name = "portalStandard_id", value = "", defaultValue = "")
            @PathVariable(value = "portalStandard_id") Long portalStandardId) {
        try {
            MPortalStandards mPortalStandards = portalStandardsClient.getPortalStandard(portalStandardId);
            if (mPortalStandards == null) {
                return failed("标准规范管理信息获取失败!");
            }

            PortalStandardModel detailModel = convertToModel(mPortalStandards, PortalStandardModel.class);
            detailModel.setReleaseDate(mPortalStandards.getReleaseDate() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalStandards.getReleaseDate()));

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/portalStandards/admin/{portalStandard_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除标准规范管理", notes = "根据标准规范管理id删除医生")
    public Envelop deletePortalStandard(
            @ApiParam(name = "portalStandard_id", value = "标准规范管理编号", defaultValue = "")
            @PathVariable(value = "portalStandard_id") String portalStandardId) {
        try {
            boolean result = portalStandardsClient.deletePortalStandard(portalStandardId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalStandard", method = RequestMethod.POST)
    @ApiOperation(value = "创建标准规范管理", notes = "重新绑定标准规范管理信息")
    public Envelop createPortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalStandard_json_data") String portalStandardJsonData) {
        try {
            PortalStandardModel detailModel = objectMapper.readValue(portalStandardJsonData, PortalStandardModel.class);

            String errorMsg = null;

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalStandards mPortalStandard = convertToMPortalStandard(detailModel);
            mPortalStandard = portalStandardsClient.createPortalStandard(objectMapper.writeValueAsString(mPortalStandard));
            if (mPortalStandard == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalStandard, PortalStandardModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalStandard", method = RequestMethod.PUT)
    @ApiOperation(value = "修改标准规范管理", notes = "重新绑定标准规范管理信息")
    public Envelop updatePortalStandard(
            @ApiParam(name = "portalStandard_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalStandard_json_data") String portalStandardJsonData) {
        try {
            PortalStandardModel detailModel = toEntity(portalStandardJsonData, PortalStandardModel.class);
            String errorMsg = null;

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalStandards mPortalStandard = convertToMPortalStandard(detailModel);
            mPortalStandard = portalStandardsClient.updatePortalStandard(objectMapper.writeValueAsString(mPortalStandard));
            if(mPortalStandard==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalStandard, PortalStandardModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public MPortalStandards convertToMPortalStandard(PortalStandardModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MPortalStandards mPortalStandards = convertToModel(detailModel,MPortalStandards.class);
        mPortalStandards.setReleaseDate(DateTimeUtil.simpleDateTimeParse(detailModel.getReleaseDate()));

        return mPortalStandards;
    }
}
