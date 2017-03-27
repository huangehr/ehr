package com.yihu.ehr.portal.controller;

import com.yihu.ehr.agModel.portal.PortalResourcesModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.portal.MPortalResources;
import com.yihu.ehr.portal.service.PortalResourcesClient;
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

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoujie on 2017/3/11.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "portalResources", description = "资源接口", tags = {"资源接口"})
public class ProtalResourcesController extends BaseController {

    @Autowired
    private PortalResourcesClient portalResourcesClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;
    @Autowired
    private  FileResourceClient fileResourceClient;


    @RequestMapping(value = "/portalResources", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源列表", notes = "根据查询条件获取资源列表在前端表格展示")
    public Envelop searchPortalResources(
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

        ResponseEntity<List<MPortalResources>> responseEntity = portalResourcesClient.searchPortalResources(fields, filters, sorts, size, page);
        List<MPortalResources> mPortalResourcesList = responseEntity.getBody();
        List<PortalResourcesModel> portalResourcesModels = new ArrayList<>();
        for (MPortalResources mPortalResources : mPortalResourcesList) {
            PortalResourcesModel portalResourcesModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            portalResourcesModel.setUploadTime(mPortalResources.getUploadTime() == null?"": DateTimeUtil.simpleDateTimeFormat(mPortalResources.getUploadTime()));

            //获取类别字典
            MConventionalDict dict = conventionalDictClient.getPortalResourcesPlatformTypeList(String.valueOf(mPortalResources.getPlatformType()));
            portalResourcesModel.setPlatformTypeName(dict == null ? "" : dict.getValue());

            MConventionalDict dict2 = conventionalDictClient.getPortalResourcesDevelopLanTypeList(String.valueOf(mPortalResources.getDevelopLan()));
            portalResourcesModel.setDevelopLanName(dict2 == null ? "" : dict2.getValue());
            portalResourcesModels.add(portalResourcesModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

        Envelop envelop = getResult(portalResourcesModels, totalCount, page, size);
        return envelop;
    }


    @RequestMapping(value = "portalResources/admin/{portalResources_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取资源信息", notes = "资源信息")
    public Envelop getPortalResources(
            @ApiParam(name = "portalResources_id", value = "", defaultValue = "")
            @PathVariable(value = "portalResources_id") Long portalResourcesId) {
        try {
            MPortalResources mPortalResources = portalResourcesClient.getPortalResources(portalResourcesId);
            if (mPortalResources == null) {
                return failed("资源信息获取失败!");
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

    @RequestMapping(value = "/portalResources/admin/{portalResources_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "根据资源id删除")
    public Envelop deletePortalResources(
            @ApiParam(name = "portalResources_id", value = "资源编号", defaultValue = "")
            @PathVariable(value = "portalResources_id") String portalResourcesId) {
        try {
            boolean result = portalResourcesClient.deletePortalResources(portalResourcesId);
            if (!result) {
                return failed("删除失败!");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalResources", method = RequestMethod.POST)
    @ApiOperation(value = "创建资源", notes = "重新绑定资源信息")
    public Envelop createDoctor(
            @ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalResources_json_data") String portalResourcesJsonData) {
        try {
            PortalResourcesModel detailModel = objectMapper.readValue(portalResourcesJsonData, PortalResourcesModel.class);

            String errorMsg = null;

            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "名称不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getVersion())) {
                errorMsg += "版本不能为空!";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalResources mPortalResources = convertToMPortalResources(detailModel);
            mPortalResources = portalResourcesClient.createPortalResources(objectMapper.writeValueAsString(mPortalResources));
            if (mPortalResources == null) {
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/portalResources", method = RequestMethod.PUT)
    @ApiOperation(value = "修改资源", notes = "重新绑定资源信息")
    public Envelop updatePortalResources(
            @ApiParam(name = "portalResources_json_data", value = "", defaultValue = "")
            @RequestParam(value = "portalResources_json_data") String portalResourcesJsonData) {
        try {
            PortalResourcesModel detailModel = toEntity(portalResourcesJsonData, PortalResourcesModel.class);
            String errorMsg = null;
            if (StringUtils.isEmpty(detailModel.getName())) {
                errorMsg += "名称不能为空!";
            }
            if (StringUtils.isEmpty(detailModel.getVersion())) {
                errorMsg += "版本不能为空!";
            }

            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MPortalResources mPortalResources = convertToMPortalResources(detailModel);
            mPortalResources = portalResourcesClient.updatePortalResources(objectMapper.writeValueAsString(mPortalResources));
            if(mPortalResources==null){
                return failed("保存失败!");
            }

            detailModel = convertToModel(mPortalResources, PortalResourcesModel.class);
            return success(detailModel);
        }
        catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }


    public MPortalResources convertToMPortalResources(PortalResourcesModel detailModel) throws ParseException {
        if(detailModel==null)
        {
            return null;
        }
        MPortalResources mPortalResources = convertToModel(detailModel,MPortalResources.class);
        mPortalResources.setUploadTime(DateTimeUtil.simpleDateTimeParse(detailModel.getUploadTime()));
        return mPortalResources;
    }
}

