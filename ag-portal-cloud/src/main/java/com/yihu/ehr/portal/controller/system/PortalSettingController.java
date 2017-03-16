package com.yihu.ehr.portal.controller.system;

import com.yihu.ehr.agModel.portal.PortalSettingModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.portal.MPortalSetting;
import com.yihu.ehr.portal.service.PortalSettingClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yeshijie on 2017/2/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/doctor")
@RestController
@Api(value = "portalSetting", description = "PortalSetting", tags = {"门户配置接口"})
public class PortalSettingController extends BaseController{

    @Autowired
    private PortalSettingClient portalSettingClient;


    @RequestMapping(value = "/portalSetting", method = RequestMethod.GET)
    @ApiOperation(value = "获取门户配置列表", notes = "根据查询条件获取门户配置列表在前端表格展示")
    public Envelop searchPortalSetting(
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

        ResponseEntity<List<MPortalSetting>> responseEntity = portalSettingClient.searchPortalSetting(fields, filters, sorts, size, page);
        List<MPortalSetting> mPortalSettingList = responseEntity.getBody();
        List<PortalSettingModel> portalSettingModels = new ArrayList<>();
        for (MPortalSetting mPortalSetting :  mPortalSettingList) {
            PortalSettingModel portalSettingModel = convertToModel(mPortalSetting, PortalSettingModel.class);

            portalSettingModels.add(portalSettingModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalSettingModels, totalCount, page, size);
        return envelop;
    }


    @RequestMapping(value = "portalSetting/admin/{portalSetting_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取门户配置信息", notes = "门户配置信息")
    public Envelop getPortalSetting(
            @ApiParam(name = "portalSetting_id", value = "", defaultValue = "")
            @PathVariable(value = "portalSetting_id") Long portalSettingId) {
        try {
            MPortalSetting mPortalSetting = portalSettingClient.getPortalSetting(portalSettingId);
            if (mPortalSetting == null) {
                return failed("门户配置信息获取失败!");
            }

            PortalSettingModel detailModel = convertToModel(mPortalSetting, PortalSettingModel.class);

            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/portalSetting/admin/{portalSetting_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除门户配置", notes = "根据门户配置id删除")
    public Envelop deletePortalSetting(
            @ApiParam(name = "portalSetting_id", value = "门户配置编号", defaultValue = "")
            @PathVariable(value = "portalSetting_id") String portalSettingId) {
        try {
            boolean result = portalSettingClient.deletePortalSetting(portalSettingId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalSetting", method = RequestMethod.POST)
    @ApiOperation(value = "创建门户配置", notes = "重新绑定门户配置信息")
    public Envelop createPortalSetting(
            @ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalSetting_json_data") String portalSettingJsonData) {
        try {
            PortalSettingModel detailModel = objectMapper.readValue(portalSettingJsonData, PortalSettingModel.class);

            String errorMsg = null;

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalSetting mPortalSetting = portalSettingClient.createPortalSetting(objectMapper.writeValueAsString(detailModel));
            if (mPortalSetting == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalSetting, PortalSettingModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalSetting", method = RequestMethod.PUT)
    @ApiOperation(value = "修改门户配置", notes = "重新绑定门户配置信息")
    public Envelop updatePortalSetting(
            @ApiParam(name = "portalSetting_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalSetting_json_data") String portalSettingJsonData) {
        try {
            PortalSettingModel detailModel = objectMapper.readValue(portalSettingJsonData, PortalSettingModel.class);
            String errorMsg = null;

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalSetting mPortalSetting = portalSettingClient.updatePortalSetting(objectMapper.writeValueAsString(detailModel));
            if(mPortalSetting==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalSetting, PortalSettingModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


}
