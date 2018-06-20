package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.entity.resource.RsAppResource;
import com.yihu.ehr.entity.resource.RsAppResourceMetadata;
import com.yihu.ehr.model.org.MRsOrgResource;
import com.yihu.ehr.model.org.MRsOrgResourceMetadata;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import com.yihu.ehr.model.resource.MRsRolesResource;
import com.yihu.ehr.model.resource.MRsRolesResourceMetadata;
import com.yihu.ehr.resource.model.*;
import com.yihu.ehr.resource.service.*;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by lyr on 2016/4/26.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "RsResourceGrantEndPoint", description = "资源授权", tags = {"资源服务-资源授权"})
public class RsResourceGrantEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsResourceService rsResourceService;
    @Autowired
    private RsResourceGrantService rsGrantService;
    @Autowired
    private RsAppResourceMetadataGrantService rsMetadataGrantService;
    @Autowired
    private RsRolesResourceMetadataGrantService rolesResourceMetadataGrantService;
    @Autowired
    private RsRolesResourceGrantService rolesResourceGrantService;
    @Autowired
    private RsOrgResourceGrantService orgResourceGrantService;
    @Autowired
    private RsOrgResourceMetadataGrantService orgResourceMetadataGrantService;
    @Autowired
    private RsResourceMetadataService rsResourceMetadataService;

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    public Collection<MRsAppResource> grantAppResource(
            @ApiParam(name = "appId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appId") String appId,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        String[] resourceIdArray = resourceIds.split(",");
        List<RsAppResource> appRsList = new ArrayList<RsAppResource>();

        for (String resoruceId : resourceIdArray) {
            RsAppResource appRs = new RsAppResource();

            appRs.setId(getObjectId(BizObject.AppResource));
            appRs.setAppId(appId);
            appRs.setResourceId(resoruceId);

            appRsList.add(appRs);
        }

        return convertToModels(rsGrantService.grantResourceBatch(appRsList), new ArrayList<>(appRsList.size()), MRsAppResource.class, "");
    }

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    public Collection<MRsAppResource> grantResourceApp(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "resourceId") String resourceId,
            @ApiParam(name = "appIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "appIds") String appIds) throws Exception {
        String[] appIdArray = appIds.split(",");
        List<RsAppResource> appRsList = new ArrayList<RsAppResource>();
        List<RsAppResource> rsGrant = rsGrantService.search("", "resourceId=" + resourceId + ";appId=" + appIds, "", 1, 999);
        String existGrant = "";
        for (RsAppResource rsAppResource : rsGrant) {
            existGrant += "," + rsAppResource.getAppId();
        }
        for (String _appId : appIdArray) {
            if (existGrant.contains(_appId)) {
                continue;
            }
            RsAppResource appRs = new RsAppResource();

            appRs.setId(getObjectId(BizObject.AppResource));
            appRs.setAppId(_appId);
            appRs.setResourceId(resourceId);

            appRsList.add(appRs);
        }
        return convertToModels(rsGrantService.addList(appRsList, resourceId), new ArrayList<>(appRsList.size()), MRsAppResource.class, "");
    }


    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    public boolean deleteGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsGrantService.deleteResourceGrant(id);
        return true;
    }

    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    public boolean deleteGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        rsGrantService.deleteGrantByIds(ids.split(","));
        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源授权")
    public MRsAppResource getRsAppGrantById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsGrantService.getRsAppGrantById(id), MRsAppResource.class);
    }

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    public List<MRsAppResource> queryAppResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsAppResource> rsAppList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsAppResource> rsGrant = rsGrantService.getAppResourceGrant(sorts, reducePage(page), size);
            total = rsGrant.getTotalElements();
            rsAppList = convertToModels(rsGrant.getContent(), new ArrayList<>(rsGrant.getNumber()), MRsAppResource.class, fields);
        } else {
            List<RsAppResource> rsGrant = rsGrantService.search(fields, filters, sorts, page, size);
            total = rsGrantService.getCount(filters);
            rsAppList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsAppResource.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsAppResource>) rsAppList;
    }

    @ApiOperation("资源授权查询-不分页")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantsNoPage, method = RequestMethod.GET)
    public List<MRsAppResource> queryAppResourceGrantNoPage(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Collection<MRsAppResource> rsAppList;
        List<RsAppResource> rsGrant = rsGrantService.search(filters);
        rsAppList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsAppResource.class, null);
        return (List<MRsAppResource>) rsAppList;
    }

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    public MRsAppResourceMetadata grantRsMetaData(
            @ApiParam(name = "metadataId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "metadataId") String metadataId,
            @ApiParam(name = "appResourceId", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId) throws Exception {
        RsAppResourceMetadata appRsMetadata = new RsAppResourceMetadata();
        appRsMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
        appRsMetadata.setAppResourceId(appResourceId);
        appRsMetadata.setResourceMetadataId(metadataId);

        rsMetadataGrantService.grantRsMetadata(appRsMetadata);
        return convertToModel(appRsMetadata, MRsAppResourceMetadata.class);
    }

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataListGrantApp, method = RequestMethod.POST)
    public Collection<MRsAppResourceMetadata> grantRsMetaDataBatch(
            @ApiParam(name = "appResourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId,
            @ApiParam(name = "metadataIds", value = "资源数据元ID", defaultValue = "")
            @RequestParam(value = "metadataIds") String metadataIds) throws Exception {
        String[] metadataIdArray = metadataIds.split(",");
        List<RsAppResourceMetadata> appRsMetadataList = new ArrayList<RsAppResourceMetadata>();

        for (String _metadataId : metadataIdArray) {
            RsAppResourceMetadata appRsMetadata = new RsAppResourceMetadata();

            appRsMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
            appRsMetadata.setAppResourceId(appResourceId);
            appRsMetadata.setResourceMetadataId(_metadataId);

            appRsMetadataList.add(appRsMetadata);
        }

        rsMetadataGrantService.grantRsMetadataBatch(appRsMetadataList);
        return convertToModels(appRsMetadataList, new ArrayList<>(appRsMetadataList.size()), MRsAppResourceMetadata.class, "");
    }

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.DELETE)
    public boolean deleteMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        rsMetadataGrantService.deleteRsMetadataGrant(id);
        return true;
    }

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.DELETE)
    public boolean deleteMetadataGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        rsMetadataGrantService.deleteRsMetadataGrant(ids);

        return true;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public MRsAppResourceMetadata getRsMetadataGrantById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rsMetadataGrantService.getRsMetadataGrantById(id), MRsAppResourceMetadata.class);
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.GET)
    public List<MRsAppResourceMetadata> queryAppRsMetadataGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsAppResourceMetadata> rsAppMetaList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsAppResourceMetadata> rsMetadataGrant = rsMetadataGrantService.getAppRsMetadataGrant(sorts, reducePage(page), size);
            total = rsMetadataGrant.getTotalElements();
            rsAppMetaList = convertToModels(rsMetadataGrant.getContent(), new ArrayList<>(rsMetadataGrant.getNumber()), MRsAppResourceMetadata.class, fields);
        } else {
            List<RsAppResourceMetadata> rsMetadataGrant = rsMetadataGrantService.search(fields, filters, sorts, page, size);
            total = rsMetadataGrantService.getCount(filters);
            rsAppMetaList = convertToModels(rsMetadataGrant, new ArrayList<>(rsMetadataGrant.size()), MRsAppResourceMetadata.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsAppResourceMetadata>) rsAppMetaList;
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrants, method = RequestMethod.GET)
    public Collection<MRsAppResourceMetadata> getAppRsMetadatas(
            @ApiParam(name = "app_res_id", value = "授权应用编号", defaultValue = "1")
            @PathVariable(value = "app_res_id") String appResId) throws Exception {
        RsAppResource appResource = rsGrantService.retrieve(appResId);
        List<RsAppResourceMetadata> rsMetadataGrant = new ArrayList<>();
        if (appResource != null) {
            rsMetadataGrant = rsMetadataGrantService.getAppRsMetadatas(appResource.getId(), appResource.getAppId(), appResource.getResourceId());
        }
        return convertToModels(rsMetadataGrant, new ArrayList<>(rsMetadataGrant.size()), MRsAppResourceMetadata.class, "");
    }

    @ApiOperation("资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasValid, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean valid(
            @ApiParam(name = "data", value = "授权数据元", defaultValue = "")
            @RequestBody List<RsAppResourceMetadata> data,
            @ApiParam(name = "valid", value = "授权数据元ID", defaultValue = "")
            @RequestParam(value = "valid") int valid) throws Exception {
        String ids = "";
        if (valid == 0) {
            for (RsAppResourceMetadata metadata : data) {
                ids += "," + metadata.getId();
            }
        } else {
            List addLs = new ArrayList<>();
            for (RsAppResourceMetadata metadata : data) {
                if (!StringUtils.isEmpty(metadata.getId())) {
                    ids += "," + metadata.getId();
                } else {
                    metadata.setId(getObjectId(BizObject.AppResourceMetadata));
                    addLs.add(metadata);
                }
            }
            rsMetadataGrantService.grantRsMetadataBatch(addLs);
        }
        if (ids.length() > 0) {
            rsMetadataGrantService.valid(ids.substring(1), valid);
        }
        return true;
    }

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.POST)
    public MRsAppResourceMetadata metadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        RsAppResourceMetadata rsAppResourceMetadata = rsMetadataGrantService.retrieve(id);
        rsAppResourceMetadata.setDimensionValue(dimension);
        rsMetadataGrantService.save(rsAppResourceMetadata);
        return convertToModel(rsAppResourceMetadata, MRsAppResourceMetadata.class);
    }

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsAppResourceMetadata metadataGrant(
            @RequestBody RsAppResourceMetadata model) throws Exception {

        if (StringUtils.isEmpty(model.getId())) {
            model.setId(getObjectId(BizObject.AppResourceMetadata));
        }
        return convertToModel(rsMetadataGrantService.save(model), MRsAppResourceMetadata.class);
    }

    @ApiOperation("查询资源应用下存在多少授权数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrantExistence, method = RequestMethod.GET)
    List<Map> appMetaExistence(
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam("res_app_ids") String resAppIds) throws Exception {

        List<Map> ls = rsMetadataGrantService.appMetaExistence(resAppIds.split(","));
        return ls;
    }

    @ApiOperation("角色组资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.GET)
    public List<MRsRolesResource> queryRolesResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "999")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsRolesResource> rsRolesList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsRolesResource> rsGrant = rolesResourceGrantService.getRolesResourceGrant(sorts, reducePage(page), size);
            total = rsGrant.getTotalElements();
            rsRolesList = convertToModels(rsGrant.getContent(), new ArrayList<>(rsGrant.getNumber()), MRsRolesResource.class, fields);
        } else {
            List<RsRolesResource> rsGrant = rolesResourceGrantService.search(fields, filters, sorts, page, size);
            total = rolesResourceGrantService.getCount(filters);
            rsRolesList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsRolesResource.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsRolesResource>) rsRolesList;
    }

    @ApiOperation("角色组资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadatasValid, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean rolesValid(
            @ApiParam(name = "data", value = "授权数据元", defaultValue = "")
            @RequestBody List<RsRolesResourceMetadata> data,
            @ApiParam(name = "valid", value = "授权数据元ID", defaultValue = "")
            @RequestParam(value = "valid") int valid) throws Exception {
        String ids = "";
        if (valid == 0) {
            for (RsRolesResourceMetadata metadata : data) {
                ids += "," + metadata.getId();
            }
        } else {
            List addLs = new ArrayList<>();
            for (RsRolesResourceMetadata metadata : data) {
                if (!StringUtils.isEmpty(metadata.getId())) {
                    ids += "," + metadata.getId();
                } else {
                    metadata.setId(getObjectId(BizObject.RolesResourceMetadata));
                    addLs.add(metadata);
                }
            }
            rolesResourceMetadataGrantService.grantRsRolesMetadataBatch(addLs);
        }
        if (ids.length() > 0) {
            rolesResourceMetadataGrantService.rolesValid(ids.substring(1), valid);
        }
        return true;
    }

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrant, method = RequestMethod.POST)
    public MRsRolesResourceMetadata metadataRolesGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        RsRolesResourceMetadata rsRolesResourceMetadata = rolesResourceMetadataGrantService.retrieve(id);
        rsRolesResourceMetadata.setDimensionValue(dimension);
        rolesResourceMetadataGrantService.save(rsRolesResourceMetadata);
        return convertToModel(rsRolesResourceMetadata, MRsRolesResourceMetadata.class);
    }

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsRolesResourceMetadata rolesMetadataGrant(
            @RequestBody RsRolesResourceMetadata model) throws Exception {

        if (StringUtils.isEmpty(model.getId())) {
            model.setId(getObjectId(BizObject.RolesResourceMetadata));
        }
        return convertToModel(rolesResourceMetadataGrantService.save(model), MRsRolesResourceMetadata.class);
    }

    @ApiOperation("单个角色组授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.RolesGrantResources, method = RequestMethod.POST)
    public Collection<MRsRolesResource> grantRolesResource(
            @ApiParam(name = "rolesId", value = "角色组ID", defaultValue = "")
            @PathVariable(value = "rolesId") String rolesId,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        String[] resourceIdArray = resourceIds.split(",");
        List<RsRolesResource> rolesRsList = new ArrayList<RsRolesResource>();
        for (String resoruceId : resourceIdArray) {
            RsRolesResource rolesRs = new RsRolesResource();
            rolesRs.setId(getObjectId(BizObject.RolesResource));
            rolesRs.setRolesId(rolesId);
            rolesRs.setResourceId(resoruceId);
            rolesRsList.add(rolesRs);
        }
        return convertToModels(rolesResourceGrantService.grantResourceBatch(rolesRsList), new ArrayList<>(rolesRsList.size()), MRsRolesResource.class, "");
    }

    @ApiOperation("角色组授权资源查询")
    @RequestMapping(value = ServiceApi.Resources.GetRolesGrantResources, method = RequestMethod.GET)
    public Envelop getRolesGrantResources(
            @ApiParam(name = "rolesId", value = "角色组ID")
            @RequestParam(value = "rolesId") String rolesId,
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId") String userId) throws Exception {
        Envelop envelop = new Envelop();
        Set<String> idSet = new HashSet<String>();
        List<RsRolesResource> rolesRsList = rolesResourceGrantService.search("rolesId=" + rolesId);
        for (RsRolesResource rsRolesResource : rolesRsList) {
            idSet.add(rsRolesResource.getResourceId());
        }
        List<RsResource> rsResourceList = rsResourceService.search("creator=" + userId);
        for (RsResource rsResource : rsResourceList) {
            idSet.add(rsResource.getId());
        }
        List<String> idList = new ArrayList<String>();
        idList.addAll(idSet);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(idList);
        return envelop;
    }


    @ApiOperation("角色组资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesResMetadataGrants, method = RequestMethod.GET)
    public Collection<MRsRolesResourceMetadata> getRolesRsMetadatas(
            @ApiParam(name = "roles_res_id", value = "授权应用编号", defaultValue = "1")
            @PathVariable(value = "roles_res_id") String rolesResId,
            @ApiParam(name = "appId", value = "应用编号", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId) throws Exception {
        RsRolesResource rolesResource = rolesResourceGrantService.retrieve(rolesResId);
        List<RsRolesResourceMetadata> rsMetadataGrant = new ArrayList<>();
        List<RsRolesResourceMetadata> rsMetadataGrantApp = new ArrayList<>();
        if (rolesResource != null) {
            rsMetadataGrant = rolesResourceMetadataGrantService.getRolesRsMetadatas(rolesResource.getId(), rolesResource.getRolesId(), rolesResource.getResourceId());
        }
        if (!StringUtils.isEmpty(appId)) {
            List<String> list = rsMetadataGrantService.getMetadataIdByAppId(appId);
            for (RsRolesResourceMetadata resourceMetadata : rsMetadataGrant) {
                if (list.contains(resourceMetadata.getResourceMetadataId())) {
                    rsMetadataGrantApp.add(resourceMetadata);
                }
            }
            return convertToModels(rsMetadataGrantApp, new ArrayList<>(rsMetadataGrantApp.size()), MRsRolesResourceMetadata.class, "");
        }
        return convertToModels(rsMetadataGrant, new ArrayList<>(rsMetadataGrant.size()), MRsRolesResourceMetadata.class, "");
    }

    @ApiOperation("角色组取消资源授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrantsNoPage, method = RequestMethod.GET)
    public List<MRsRolesResource> queryRolesResourceGrantNoPage(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Collection<MRsRolesResource> rsRolesList;
        List<RsRolesResource> rsGrant = rolesResourceGrantService.search(filters);
        rsRolesList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsRolesResource.class, null);
        return (List<MRsRolesResource>) rsRolesList;
    }

    @ApiOperation("角色组资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.DELETE)
    public boolean deleteRolesGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        rolesResourceGrantService.deleteGrantByIds(ids.split(","));
        return true;
    }


    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrant, method = RequestMethod.GET)
    @ApiOperation("角色组-资源授权-维度授权-根据ID获取资源数据元授权")
    public MRsRolesResourceMetadata getRolesRsMetadataGrantById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(rolesResourceMetadataGrantService.getRsRolesMetadataGrantById(id), MRsRolesResourceMetadata.class);
    }


    //机构授权
    @ApiOperation("机构资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.GET)
    public List<MRsOrgResource> queryOrgResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "999")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        long total = 0;
        Collection<MRsOrgResource> rsOrgList;

        //过滤条件为空
        if (StringUtils.isEmpty(filters)) {
            Page<RsOrgResource> rsGrant = orgResourceGrantService.getOrgResourceGrant(sorts, reducePage(page), size);
            total = rsGrant.getTotalElements();
            rsOrgList = convertToModels(rsGrant.getContent(), new ArrayList<>(rsGrant.getNumber()), MRsOrgResource.class, fields);
        } else {
            List<RsOrgResource> rsGrant = orgResourceGrantService.search(fields, filters, sorts, page, size);
            total = orgResourceGrantService.getCount(filters);
            rsOrgList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsOrgResource.class, fields);
        }

        pagedResponse(request, response, total, page, size);
        return (List<MRsOrgResource>) rsOrgList;
    }

    @ApiOperation("机构资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadatasValid, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public boolean orgValid(
            @ApiParam(name = "data", value = "授权数据元", defaultValue = "")
            @RequestBody List<RsOrgResourceMetadata> data,
            @ApiParam(name = "valid", value = "授权数据元ID", defaultValue = "")
            @RequestParam(value = "valid") int valid) throws Exception {
        String ids = "";
        if (valid == 0) {
            for (RsOrgResourceMetadata metadata : data) {
                ids += "," + metadata.getId();
            }
        } else {
            List addLs = new ArrayList<>();
            for (RsOrgResourceMetadata metadata : data) {
                if (!StringUtils.isEmpty(metadata.getId()))
                    ids += "," + metadata.getId();
                else {
                    metadata.setId(getObjectId(BizObject.OrgResourceMetadata));
                    addLs.add(metadata);
                }
            }
            orgResourceMetadataGrantService.grantRsOrgMetadataBatch(addLs);
        }
        if (ids.length() > 0) {
            orgResourceMetadataGrantService.orgValid(ids.substring(1), valid);
        }
        return true;
    }

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrant, method = RequestMethod.POST)
    public MRsOrgResourceMetadata metadataOrgGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        RsOrgResourceMetadata rsOrgResourceMetadata = orgResourceMetadataGrantService.retrieve(id);
        rsOrgResourceMetadata.setDimensionValue(dimension);
        orgResourceMetadataGrantService.save(rsOrgResourceMetadata);
        return convertToModel(rsOrgResourceMetadata, MRsOrgResourceMetadata.class);
    }

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsOrgResourceMetadata orgMetadataGrant(
            @RequestBody RsOrgResourceMetadata model) throws Exception {

        if (StringUtils.isEmpty(model.getId())) {
            model.setId(getObjectId(BizObject.OrgResourceMetadata));
        }
        return convertToModel(orgResourceMetadataGrantService.save(model), MRsOrgResourceMetadata.class);
    }

    @ApiOperation("单个机构授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.OrgGrantResources, method = RequestMethod.POST)
    public Collection<MRsOrgResource> grantOrgResource(
            @ApiParam(name = "orgCode", value = "机构ID", defaultValue = "")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        String[] resourceIdArray = resourceIds.split(",");
        List<RsOrgResource> orgRsList = new ArrayList<RsOrgResource>();

        for (String resoruceId : resourceIdArray) {
            RsOrgResource orgRs = new RsOrgResource();
            orgRs.setId(getObjectId(BizObject.RolesResource));
            orgRs.setOrganizationId(orgCode);
            orgRs.setResourceId(resoruceId);

            orgRsList.add(orgRs);
        }

        return convertToModels(orgResourceGrantService.grantResourceBatch(orgRsList), new ArrayList<>(orgRsList.size()), MRsOrgResource.class, "");
    }

    @ApiOperation("机构资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgResMetadataGrants, method = RequestMethod.GET)
    public Collection<MRsOrgResourceMetadata> getOrgRsMetadatas(
            @ApiParam(name = "Org_res_id", value = "授权应用编号", defaultValue = "1")
            @PathVariable(value = "Org_res_id") String orgResId) throws Exception {
        RsOrgResource orgResource = orgResourceGrantService.retrieve(orgResId);
        List<RsOrgResourceMetadata> rsMetadataGrant = new ArrayList<>();
        if (orgResource != null) {
            rsMetadataGrant = orgResourceMetadataGrantService.getOrgRsMetadatas(orgResource.getId(), orgResource.getOrganizationId(), orgResource.getResourceId());
        }
        return convertToModels(rsMetadataGrant, new ArrayList<>(rsMetadataGrant.size()), MRsOrgResourceMetadata.class, "");
    }

    @ApiOperation(" 机构取消资源授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrantsNoPage, method = RequestMethod.GET)
    public List<MRsOrgResource> queryOrgResourceGrantNoPage(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Collection<MRsOrgResource> rsOrgList;
        List<RsOrgResource> rsGrant = orgResourceGrantService.search(filters);
        rsOrgList = convertToModels(rsGrant, new ArrayList<>(rsGrant.size()), MRsOrgResource.class, null);
        return (List<MRsOrgResource>) rsOrgList;
    }

    @ApiOperation("机构资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.DELETE)
    public boolean deleteOrgGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        orgResourceGrantService.deleteGrantByIds(ids.split(","));
        return true;
    }


    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrant, method = RequestMethod.GET)
    @ApiOperation("机构-资源授权-维度授权-根据ID获取资源数据元授权")
    public MRsOrgResourceMetadata getOrgRsMetadataGrantById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return convertToModel(orgResourceMetadataGrantService.getRsOrgMetadataGrantById(id), MRsOrgResourceMetadata.class);
    }

    //-----------------------------------------------------------应用数据元一键(取消)授权  start----------------------------------------------------------------------------
    @ApiOperation("单个应用一键授权多个资源（数据元）")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResourcesByCategoryId, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Envelop AppsGrantResourcesByCategoryId(
            @ApiParam(name = "appId", value = "应用ID", defaultValue = "zkGuSIm2Fg")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "categoryIds", value = "视图分类ID", defaultValue = "0dae002159535497b3865e129433e933")
            @RequestParam(value = "categoryIds", required = false) String categoryIds,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "d3beebf86ed611e89f87fa163e20f96e")
            @RequestParam(value = "resourceIds", required = false) String resourceIds) throws Exception {
        List<RsAppResource> appRsList = new ArrayList<RsAppResource>();
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(resourceIds)) {
            if (StringUtils.isEmpty(categoryIds)) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("视图分类id与资源id不能同时为空！");
                return envelop;
            } else {
                //根据视图分类id获取视图list
                String[] categoryIdArray = categoryIds.split(",");
                for (String categoryId : categoryIdArray) {
                    //根据视图分类id获取 档案数据资源list
                    List<RsResource> resourceList = rsResourceService.findByCategoryIdAndDataSource(categoryId, 1);
                    for (RsResource rsResource : resourceList) {
                        RsAppResource appRs = new RsAppResource();
                        appRs.setId(getObjectId(BizObject.AppResource));
                        appRs.setAppId(appId);
                        appRs.setResourceId(rsResource.getId());
                        appRsList.add(appRs);
                    }
                }
            }

        } else {
            String[] resourceIdArray = resourceIds.split(",");
            for (String resoruceId : resourceIdArray) {
                RsAppResource appRs = new RsAppResource();
                appRs.setId(getObjectId(BizObject.AppResource));
                appRs.setAppId(appId);
                appRs.setResourceId(resoruceId);
                appRsList.add(appRs);
            }
        }
        //查找视图是否已经授权，删除已授权数据
        for (RsAppResource rsAppResource : appRsList) {
            List<String> rsAppResourcesear = rsGrantService.findByrsResourceIdAndAppId(rsAppResource.getResourceId(), appId);
            if (null != rsAppResourcesear && rsAppResourcesear.size() > 0) {
                rsGrantService.deleteGrantByIds(rsAppResourcesear.toArray(new String[rsAppResourcesear.size()]));
            }
        }
        //授权视图
        rsGrantService.grantResourceBatch(appRsList);
        //授权视图数据元
        updateRsAppResourceMetadataByRsAppResource(appId, appRsList);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appRsList);
        envelop.setTotalCount(appRsList.size());
        return envelop;
    }

    /**
     * 一键授权-授权数据元
     *
     * @param list
     * @return
     * @throws Exception
     */
    public void updateRsAppResourceMetadataByRsAppResource(String appId, List<RsAppResource> list) throws Exception {
        List<RsAppResourceMetadata> appRsMetadataList = new ArrayList<RsAppResourceMetadata>();
        if (null != list && list.size() > 0) {
            for (RsAppResource rsAppResource : list) {
                //根据rs_resoruceId获取rs_resource_metadata中的数据元，授权
                List<RsResourceMetadata> rsResourceMetadataList = rsResourceMetadataService.getRsMetadataByResourcesId(rsAppResource.getResourceId());
                for (RsResourceMetadata rsResourceMetadata : rsResourceMetadataList) {
                    RsAppResourceMetadata appRsMetadata = new RsAppResourceMetadata();
                    appRsMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
                    appRsMetadata.setAppResourceId(rsAppResource.getId());
                    appRsMetadata.setResourceMetadataId(rsResourceMetadata.getMetadataId());
                    appRsMetadata.setAppId(appId);
                    appRsMetadata.setResourceMetadataName((null != rsResourceMetadata.getDescription() ? rsResourceMetadata.getDescription().substring(rsResourceMetadata.getDescription().indexOf("--") + 2) : ""));
                    appRsMetadataList.add(appRsMetadata);
                }
                rsMetadataGrantService.grantRsMetadataBatch(appRsMetadataList);
            }
        }
    }

    @ApiOperation("单个应用--------一键取消授权多个资源（数据元）")
    @RequestMapping(value = ServiceApi.Resources.DeleteAppsGrantResourcesByCategoryId, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public Envelop DeleteAppsGrantResourcesByCategoryId(
            @ApiParam(name = "appId", value = "应用ID", defaultValue = "zkGuSIm2Fg")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "categoryIds", value = "视图分类ID", defaultValue = "")
            @RequestParam(value = "categoryIds", required = false) String categoryIds,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "d3beebf86ed611e89f87fa163e20f96e")
            @RequestParam(value = "resourceIds", required = false) String resourceIds) throws Exception {
        List<RsAppResource> appRsList = new ArrayList<RsAppResource>();
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(resourceIds)) {
            if (StringUtils.isEmpty(categoryIds)) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("视图分类id与资源id不能同时为空！");
                return envelop;
            } else {
                //根据视图分类id获取视图list
                String[] categoryIdArray = categoryIds.split(",");
                for (String categoryId : categoryIdArray) {
                    //根据视图分类id获取 档案数据资源list
                    List<RsResource> resourceList = rsResourceService.findByCategoryIdAndDataSource(categoryId, 1);
                    for (RsResource rsResource : resourceList) {
                        RsAppResource appRs = new RsAppResource();
                        appRs.setId(getObjectId(BizObject.AppResource));
                        appRs.setAppId(appId);
                        appRs.setResourceId(rsResource.getId());
                        appRsList.add(appRs);
                    }
                }
            }

        } else {
            String[] resourceIdArray = resourceIds.split(",");
            for (String resoruceId : resourceIdArray) {
                RsAppResource appRs = new RsAppResource();
                appRs.setId(getObjectId(BizObject.AppResource));
                appRs.setAppId(appId);
                appRs.setResourceId(resoruceId);
                appRsList.add(appRs);
            }
        }
        //查找视图是否已经授权，删除已授权数据
        for (RsAppResource rsAppResource : appRsList) {
            List<String> rsAppResourcesear = rsGrantService.findByrsResourceIdAndAppId(rsAppResource.getResourceId(), appId);
            if (null != rsAppResourcesear && rsAppResourcesear.size() > 0) {
                rsGrantService.deleteGrantByIds(rsAppResourcesear.toArray(new String[rsAppResourcesear.size()]));
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appRsList);
        envelop.setTotalCount(appRsList.size());
        return envelop;
    }

    //-----------------------------------------------------------应用数据元一键(取消)授权 end----------------------------------------------------------------------------

    //-----------------------------------------------------------角色组数据元一键(取消)授权 start-------------------------------------------------------------------------
    @ApiOperation("单个角色组----一键授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.RolesGrantResourcesByCategoryId, method = RequestMethod.POST)
    public Envelop rolesGrantResourcesByCategoryId(
            @ApiParam(name = "rolesId", value = "角色组ID", defaultValue = "")
            @PathVariable(value = "rolesId") String rolesId,
            @ApiParam(name = "categoryIds", value = "视图分类ID", defaultValue = "0dae002159535497b3865e129433e933")
            @RequestParam(value = "categoryIds", required = false) String categoryIds,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "d3beebf86ed611e89f87fa163e20f96e")
            @RequestParam(value = "resourceIds", required = false) String resourceIds) throws Exception {
        List<RsRolesResource> rolesRsList = new ArrayList<RsRolesResource>();
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(resourceIds)) {
            if (StringUtils.isEmpty(categoryIds)) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("视图分类id与资源id不能同时为空！");
                return envelop;
            } else {
                //根据视图分类id获取视图list
                String[] categoryIdArray = categoryIds.split(",");
                for (String categoryId : categoryIdArray) {
                    //根据视图分类id获取 档案数据资源list
                    List<RsResource> rsResourceList = rsResourceService.findByCategoryIdAndDataSource(categoryId, 1);
                    for (RsResource rsResource : rsResourceList) {
                        RsRolesResource rolesRs = new RsRolesResource();
                        rolesRs.setId(getObjectId(BizObject.RolesResource));
                        rolesRs.setRolesId(rolesId);
                        rolesRs.setResourceId(rsResource.getId());
                        rolesRsList.add(rolesRs);
                    }
                }
            }
        } else {
            String[] resourceIdArray = resourceIds.split(",");
            for (String resoruceId : resourceIdArray) {
                RsRolesResource rolesRs = new RsRolesResource();
                rolesRs.setId(getObjectId(BizObject.RolesResource));
                rolesRs.setRolesId(rolesId);
                rolesRs.setResourceId(resoruceId);
                rolesRsList.add(rolesRs);
            }
        }
        //查找视图是否已经授权，删除已授权数据
        for (RsRolesResource rsAppResource : rolesRsList) {
            List<String> rsAppResourcesear = rolesResourceGrantService.findIdByResourceIdAndRolesId(rsAppResource.getResourceId(), rolesId);
            if (null != rsAppResourcesear && rsAppResourcesear.size() > 0) {
                rolesResourceGrantService.deleteGrantByIds(rsAppResourcesear.toArray(new String[rsAppResourcesear.size()]));
            }
        }
        //授权视图
        rolesResourceGrantService.grantResourceBatch(rolesRsList);
        //授权视图数据元
        updateRsRolrResourceMetadataByRsAppResource(rolesId, rolesRsList);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rolesRsList);
        envelop.setTotalCount(rolesRsList.size());
        return envelop;
    }

    /**
     * 一键授权-授权数据元
     *
     * @param list
     * @return
     * @throws Exception
     */
    public void updateRsRolrResourceMetadataByRsAppResource(String rolesId, List<RsRolesResource> list) throws Exception {
        List<RsRolesResourceMetadata> appRsMetadataList = new ArrayList<RsRolesResourceMetadata>();
        if (null != list && list.size() > 0) {
            for (RsRolesResource rsAppResource : list) {
                //根据rs_resoruceId获取rs_resource_metadata中的数据元，授权
                List<RsResourceMetadata> rsResourceMetadataList = rsResourceMetadataService.getRsMetadataByResourcesId(rsAppResource.getResourceId());
                for (RsResourceMetadata rsResourceMetadata : rsResourceMetadataList) {
                    RsRolesResourceMetadata rsRolesResourceMetadata = new RsRolesResourceMetadata();
                    rsRolesResourceMetadata.setId(getObjectId(BizObject.AppResourceMetadata));
                    rsRolesResourceMetadata.setRolesResourceId(rsAppResource.getId());
                    rsRolesResourceMetadata.setResourceMetadataId(rsResourceMetadata.getMetadataId());
                    rsRolesResourceMetadata.setRolesId(rolesId);
                    rsRolesResourceMetadata.setResourceMetadataName((null != rsResourceMetadata.getDescription() ? rsResourceMetadata.getDescription().substring(rsResourceMetadata.getDescription().indexOf("--") + 2) : ""));
                    appRsMetadataList.add(rsRolesResourceMetadata);
                }
                rolesResourceMetadataGrantService.grantRsRolesMetadataBatch(appRsMetadataList);
            }
        }
    }

    @ApiOperation("单个角色组----一键取消授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.DeleteRolesGrantResourcesByCategoryId, method = RequestMethod.PUT)
    public Envelop deleteRolesGrantResourcesByCategoryId(
            @ApiParam(name = "rolesId", value = "角色组ID", defaultValue = "")
            @PathVariable(value = "rolesId") String rolesId,
            @ApiParam(name = "categoryIds", value = "视图分类ID", defaultValue = "0dae002159535497b3865e129433e933")
            @RequestParam(value = "categoryIds", required = false) String categoryIds,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "d3beebf86ed611e89f87fa163e20f96e")
            @RequestParam(value = "resourceIds", required = false) String resourceIds) throws Exception {
        List<RsRolesResource> rolesRsList = new ArrayList<RsRolesResource>();
        Envelop envelop = new Envelop();
        if (StringUtils.isEmpty(resourceIds)) {
            if (StringUtils.isEmpty(categoryIds)) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("视图分类id与资源id不能同时为空！");
                return envelop;
            } else {
                //根据视图分类id获取视图list
                String[] categoryIdArray = categoryIds.split(",");
                for (String categoryId : categoryIdArray) {
                    //根据视图分类id获取 档案数据资源list
                    List<RsResource> rsResourceList = rsResourceService.findByCategoryIdAndDataSource(categoryId, 1);
                    for (RsResource rsResource : rsResourceList) {
                        RsRolesResource rolesRs = new RsRolesResource();
                        rolesRs.setId(getObjectId(BizObject.RolesResource));
                        rolesRs.setRolesId(rolesId);
                        rolesRs.setResourceId(rsResource.getId());
                        rolesRsList.add(rolesRs);
                    }
                }
            }
        } else {
            String[] resourceIdArray = resourceIds.split(",");
            for (String resoruceId : resourceIdArray) {
                RsRolesResource rolesRs = new RsRolesResource();
                rolesRs.setId(getObjectId(BizObject.RolesResource));
                rolesRs.setRolesId(rolesId);
                rolesRs.setResourceId(resoruceId);
                rolesRsList.add(rolesRs);
            }
        }
        //查找视图是否已经授权，删除已授权数据
        for (RsRolesResource rsAppResource : rolesRsList) {
            List<String> rsAppResourcesear = rolesResourceGrantService.findIdByResourceIdAndRolesId(rsAppResource.getResourceId(), rolesId);
            if (null != rsAppResourcesear && rsAppResourcesear.size() > 0) {
                rolesResourceGrantService.deleteGrantByIds(rsAppResourcesear.toArray(new String[rsAppResourcesear.size()]));
            }
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rolesRsList);
        envelop.setTotalCount(rolesRsList.size());
        return envelop;
    }

//-----------------------------------------------------------角色组数据元一键(取消)授权 end----------------------------------------------------------------------------

}
