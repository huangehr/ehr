package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.org.RsOrgResourceMetadataModel;
import com.yihu.ehr.agModel.resource.RsAppResourceMetadataModel;
import com.yihu.ehr.agModel.resource.RsRolesResourceMetadataModel;
import com.yihu.ehr.agModel.resource.TreeModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.org.MRsOrgResource;
import com.yihu.ehr.model.org.MRsOrgResourceMetadata;
import com.yihu.ehr.model.resource.*;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.resource.client.MetadataClient;
import com.yihu.ehr.resource.client.ResourceMetadataClient;
import com.yihu.ehr.resource.client.ResourcesGrantClient;
import com.yihu.ehr.resource.client.RsDictionaryEntryClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/admin")
@Api(value = "resourceGrant", description = "资源授权服务接口", tags = {"资源管理-资源授权服务接口"})
public class ResourcesGrantController extends BaseController {

    @Autowired
    private ResourcesGrantClient resourcesGrantClient;
    @Autowired
    AppClient appClient;
    @Autowired
    OrganizationClient organizationClient;
    @Autowired
    SystemDictClient systemDictClient;
    @Autowired
    ResourceMetadataClient resourceMetadataClient;
    @Autowired
    MetadataClient metadataClient;
    @Autowired
    private RsDictionaryEntryClient rsDictionaryEntryClient;

    private List<MRsAppResource> searchGrantApp(String fields, String filters, String sorts, int page, int size){
        ResponseEntity<List<MRsAppResource>> responseEntity =
                resourcesGrantClient.queryAppResourceGrant(fields, filters, sorts , page, size);
        return responseEntity.getBody();
    }

    private List<MApp> searchApp(String fields, String filters, String sorts, int page, int size){
        ResponseEntity<List<MApp>> responseEntity =
                appClient.getApps(fields, filters, sorts, size, page);
        return responseEntity.getBody();
    }

    private List<MOrganization> searchOrgs(String fields, String filters, String sorts, int page, int size){
        ResponseEntity<List<MOrganization>> responseEntity =
                organizationClient.searchOrgs(fields, filters, sorts, size, page);
        return responseEntity.getBody();
    }

    private List<MDictionaryEntry> searchDictEntries(String fields, String filters, String sorts, int page, int size){
        ResponseEntity<List<MDictionaryEntry>> responseEntity =
                systemDictClient.getDictEntries(fields, filters, sorts, size, page);
        return responseEntity.getBody();
    }

    @ApiOperation("资源授权应用树结构")
    @RequestMapping(value = "/resources/grant/app/tree", method = RequestMethod.GET)
    public Object resourceGrantTree(
            @ApiParam(name = "resourceId", value = "返回字段", defaultValue = "")
            @RequestParam(value = "resourceId", required = true) String resourceId) throws Exception {
        try
        {
            List<MRsAppResource> rsAppResources = searchGrantApp("", "resourceId="+resourceId, "" ,1 ,500);
            String ids = "";
            Map<String, String> mapping = new HashMap<>();
            for(MRsAppResource appResource: rsAppResources){
                ids += "," + appResource.getAppId();
                mapping.put(appResource.getAppId(), appResource.getId());
            }
            List<MApp> apps = searchApp("", "id=" + ids.substring(1), "+org", 1, 500);
            ids = "";
            for(MApp app: apps){
                if(app.getOrg()!=null && ids.indexOf(app.getOrg())==-1)
                    ids += ","+ app.getOrg();
            }
            List<MOrganization> orgs = searchOrgs("", "orgCode=" + ids.substring(1), "+orgCode", 1, 500);
            List<MDictionaryEntry> dicts = searchDictEntries("", "dictId=7", "", 1, 50);
            Map<String, List<TreeModel>> rs = new HashMap<>();
            List<TreeModel> orgTree, appTree;
            MApp app;
            int i =0;
            TreeModel treeModel;
            for(MOrganization org: orgs){
                if((orgTree = rs.get(org.getOrgType()))==null){
                    orgTree = new ArrayList<>();
                    rs.put(org.getOrgType(), orgTree);
                }
                appTree = new ArrayList<>();
                for(; i<apps.size(); i++){
                    app = apps.get(i);
                    if(!org.getOrgCode().equals(app.getOrg())){
                        break;
                    }
                    treeModel = new TreeModel(app.getId(), app.getName());
                    treeModel.addOtherPro("appResourceId", mapping.get(app.getId()));
                    appTree.add(treeModel);
                }
                orgTree.add(new TreeModel("org_" + org.getOrgCode(), org.getFullName(), appTree));
            }
            List ls = new ArrayList<>();
            for(MDictionaryEntry dict: dicts){
                if((orgTree=rs.get(dict.getCode())) != null){
                    ls.add(new TreeModel(dict.getCode(), dict.getValue(), orgTree));
                }
            }
            return ls;
        }
        catch (Exception e) {
            e.printStackTrace();
            return failed("获取数据错误！");
        }
    }

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    public Envelop grantAppResource(
            @ApiParam(name = "appId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appId") String appId,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsAppResource> rsAppResource = resourcesGrantClient.grantAppResource(appId,resourceIds);
            envelop.setObj(rsAppResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    public Envelop grantResourceApp(
            @ApiParam(name = "resourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "resourceId") String resourceId,
            @ApiParam(name = "addIds", value = "新增的应用ID", defaultValue = "")
            @RequestParam(value = "addIds") String addIds,
            @ApiParam(name = "deleteIds", value = "删除的授权应用ID", defaultValue = "")
            @RequestParam(value = "deleteIds") String deleteIds) throws Exception {

        Envelop envelop = new Envelop();
        try{
            String existMetaApp;
            if(!StringUtils.isEmpty(deleteIds) && !StringUtils.isEmpty((existMetaApp= appMetaExistence(deleteIds)))) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(existMetaApp.substring(1) + "已存在授权数据，不可取消！");
                return envelop;
            }

            if(resourcesGrantClient.deleteGrantBatch(deleteIds)) {
                Collection<MRsAppResource> rsAppResource = new ArrayList<>();
                if(!StringUtils.isEmpty(addIds))
                    rsAppResource = resourcesGrantClient.grantResourceApp(resourceId, addIds);
                envelop.setObj(rsAppResource);
                envelop.setSuccessFlg(true);
                return envelop;
            }else
                return failed("授权失败");
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败");
        }
    }

    private String appMetaExistence(String deleteIds){
        List<Map> exiLs = resourcesGrantClient.appMetaExistence(deleteIds);
        if(exiLs.size()>0){
            String apps = "";
            for(Map m : exiLs){
                apps += "," + m.get("appId");
            }
            List<MApp> appLs = appClient.getApps("name", "id=" + apps.substring(1), "", 10, 1).getBody();
            apps = "";
            for(MApp app: appLs){
                apps += "," + app.getName();
            }
            return apps;
        }
        return "";
    }

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    public Envelop deleteGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteGrant(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    public Envelop deleteGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }


    @RequestMapping(value = ServiceApi.Resources.ResourceGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源授权")
    public Envelop getRsAppGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAppResource rsAppResource = resourcesGrantClient.getRsAppGrantById(id);
            envelop.setObj(rsAppResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    public Envelop queryAppResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsAppResource>> responseEntity = resourcesGrantClient.queryAppResourceGrant(fields,filters,sorts,page,size);
            List<MRsAppResource> rsAppResources = responseEntity.getBody();
            Envelop envelop = getResult(rsAppResources, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @ApiOperation("资源授权查询-不分页")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantsNoPage,method = RequestMethod.GET)
    public Envelop queryAppResourceGrantNoPage(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsAppResource> mRsAppResources = resourcesGrantClient.queryAppResourceGrantNoPage(filters);
            if(mRsAppResources != null){
                envelop.setSuccessFlg(true);
                envelop.setDetailModelList(mRsAppResources);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    public Envelop grantRsMetaData(
            @ApiParam(name = "metadataId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "metadataId") String metadataId,
            @ApiParam(name = "appResourceId", value = "资源数据元ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId) throws Exception {
        Envelop envelop = new Envelop();
        try{
            MRsAppResourceMetadata rsAppResourceMetadata = resourcesGrantClient.grantRsMetaData(metadataId,appResourceId);
            envelop.setObj(rsAppResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataListGrantApp, method = RequestMethod.POST)
    public Envelop grantRsMetaDataBatch(
            @ApiParam(name = "appResourceId", value = "资源ID", defaultValue = "")
            @PathVariable(value = "appResourceId") String appResourceId,
            @ApiParam(name = "metadataIds", value = "资源数据元ID", defaultValue = "")
            @RequestParam(value = "metadataIds") String metadataIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsAppResourceMetadata> rsAppResourceMetadata = resourcesGrantClient.grantRsMetaDataBatch(appResourceId,metadataIds);
            envelop.setObj(rsAppResourceMetadata);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.DELETE)
    public Envelop deleteMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteMetadataGrant(id);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.DELETE)
    public Envelop deleteMetadataGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteMetadataGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrant, method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public Envelop getRsMetadataGrantByRes(
            @ApiParam(name="app_res_id",value="app_res_id",defaultValue = "")
            @RequestParam(value="app_res_id") String appResId,
            @ApiParam(name="res_meta_id",value="res_meta_id",defaultValue = "")
            @RequestParam(value="res_meta_id") String resMetaId,
            @ApiParam(name="app_id",value="app_id",defaultValue = "")
            @RequestParam(value="app_id") String appId) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            RsAppResourceMetadataModel model = new RsAppResourceMetadataModel();
            model.setResourceMetadataId(resMetaId);
            model.setAppResourceId(appResId);
            model.setAppId(appId);
            envelop.setObj(getDetail(model));
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    private RsAppResourceMetadataModel getDetail(RsAppResourceMetadataModel model){
        if(model!=null){
            MRsResourceMetadata rsResourceMetadata = resourceMetadataClient.getRsMetadataById(model.getResourceMetadataId());
            MRsMetadata mRsMetadata = metadataClient.getMetadataById(rsResourceMetadata.getMetadataId());
            if(StringUtils.isEmpty(model.getId()))
                model.setResourceMetadataName(mRsMetadata.getName());
            model.setMetaColunmType(mRsMetadata.getColumnType());
            model.setMetaId(mRsMetadata.getId());
            if(!StringUtils.isEmpty(mRsMetadata.getDictCode()) && !"0".equals(mRsMetadata.getDictCode()))
                model.setDictEntries(rsDictionaryEntryClient.searchRsDictionaryEntries("", "dictCode=" +mRsMetadata.getDictCode(), "", 1, 500).getBody());
        }
        return model;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public Envelop getRsMetadataGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsAppResourceMetadata rsAppResourceMetadata = resourcesGrantClient.getRsMetadataGrantById(id);
            RsAppResourceMetadataModel model = convertToModel(rsAppResourceMetadata, RsAppResourceMetadataModel.class);
            envelop.setObj(getDetail(model));
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.GET)
    public Envelop queryAppRsMetadataGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsAppResourceMetadata>> responseEntity = resourcesGrantClient.queryAppRsMetadataGrant(fields,filters,sorts,page,size);
            List<MRsAppResourceMetadata> rsAppResourceMetadatas = responseEntity.getBody();
            Envelop envelop = getResult(rsAppResourceMetadatas, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }

    @ApiOperation("资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasValid,method = RequestMethod.POST)
    public boolean valid(
            @ApiParam(name="data",value="授权数据元",defaultValue = "")
            @RequestParam(value="data") String data,
            @ApiParam(name="valid",value="授权数据元ID",defaultValue = "")
            @RequestParam(value="valid") int valid) throws Exception
    {
        return resourcesGrantClient.valid(data, valid);
    }

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.POST)
    public Envelop metadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.metadataGrant(id, dimension));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.POST)
    public Envelop metadataGrant(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam String model) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.metadataGrant(model));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrants,method = RequestMethod.GET)
    public Envelop queryAppRsMetadataGrant(
            @ApiParam(name="app_res_id",value="授权应用编号",defaultValue = "1")
            @PathVariable(value="app_res_id")String appResId,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size) throws Exception
    {
        try
        {
            ResponseEntity<List<MRsAppResourceMetadata>> responseEntity = resourcesGrantClient.getAppRsMetadatas(appResId);
            return getResult(responseEntity.getBody(), responseEntity.getBody().size(), 1, 15);
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询出错");
            return envelop;
        }
    }

    @ApiOperation("角色组资源授权初始查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.GET)
    public Envelop queryRolesResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "999")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsRolesResource>> responseEntity = resourcesGrantClient.queryRolesResourceGrant(fields,filters,sorts,page,size);
            List<MRsRolesResource> rsAppResources = responseEntity.getBody();
            Envelop envelop = getResult(rsAppResources, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }


    @ApiOperation("角色组资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadatasValid,method = RequestMethod.POST)
    public boolean rolesValid(
            @ApiParam(name="data",value="授权数据元",defaultValue = "")
            @RequestParam(value="data") String data,
            @ApiParam(name="valid",value="授权数据元ID",defaultValue = "")
            @RequestParam(value="valid") int valid) throws Exception
    {
        return resourcesGrantClient.rolesValid(data, valid);
    }

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrant, method = RequestMethod.POST)
    public Envelop rolesMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.metadataRolesGrant(id, dimension));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrants, method = RequestMethod.POST)
    public Envelop rolesMetadataGrant(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam String model) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.rolesMetadataGrant(model));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("单个角色组授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.RolesGrantResources, method = RequestMethod.POST)
    public Envelop grantRolesResource(
            @ApiParam(name = "rolesId", value = "角色组ID", defaultValue = "")
            @PathVariable(value = "rolesId") String rolesId,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsRolesResource> rsRolesResource = resourcesGrantClient.grantRolesResource(rolesId,resourceIds);
            envelop.setObj(rsRolesResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("角色组资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesResMetadataGrants,method = RequestMethod.GET)
    public Envelop queryRolesRsMetadataGrant(
            @ApiParam(name="roles_res_id",value="授权角色编号",defaultValue = "1")
            @PathVariable(value="roles_res_id")String rolesResId,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size) throws Exception
    {
        try
        {
            ResponseEntity<List<MRsRolesResourceMetadata>> responseEntity = resourcesGrantClient.getRolesRsMetadatas(rolesResId);
            return getResult(responseEntity.getBody(), responseEntity.getBody().size(), 1, 15);
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询出错");
            return envelop;
        }
    }

    @ApiOperation("角色组资源取消授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrantsNoPage,method = RequestMethod.GET)
    public Envelop queryRolesResourceGrantNoPage(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsRolesResource> mRsRolesResources = resourcesGrantClient.queryRolesResourceGrantNoPage(filters);
            if(mRsRolesResources != null){
                envelop.setSuccessFlg(true);
                envelop.setDetailModelList(mRsRolesResources);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
    @ApiOperation("角色组资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.DELETE)
    public Envelop deleteRolesGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteRolesGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("角色组-资源授权-维度授权-根据ID获取资源数据元授权")
    public Envelop getRolesRsMetadataGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsRolesResourceMetadata rsRolesResourceMetadata = resourcesGrantClient.getRolesRsMetadataGrantById(id);
            RsRolesResourceMetadataModel model = convertToModel(rsRolesResourceMetadata, RsRolesResourceMetadataModel.class);
            envelop.setObj(getRolesDetail(model));
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    private RsRolesResourceMetadataModel getRolesDetail(RsRolesResourceMetadataModel model){
        if(model!=null){
            MRsResourceMetadata rsResourceMetadata = resourceMetadataClient.getRsMetadataById(model.getResourceMetadataId());
            MRsMetadata mRsMetadata = metadataClient.getMetadataById(rsResourceMetadata.getMetadataId());
            if(StringUtils.isEmpty(model.getId()))
                model.setResourceMetadataName(mRsMetadata.getName());
            model.setMetaColunmType(mRsMetadata.getColumnType());
            model.setMetaId(mRsMetadata.getId());
            if(!StringUtils.isEmpty(mRsMetadata.getDictCode()) && !"0".equals(mRsMetadata.getDictCode()))
                model.setDictEntries(rsDictionaryEntryClient.searchRsDictionaryEntries("", "dictCode=" +mRsMetadata.getDictCode(), "", 1, 500).getBody());
        }
        return model;
    }


    //机构-资源授权
    @ApiOperation("机构资源授权初始查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.GET)
    public Envelop queryOrgResourceGrant(
            @ApiParam(name = "fields", value = "返回字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "999")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        try
        {
            ResponseEntity<List<MRsOrgResource>> responseEntity = resourcesGrantClient.queryOrgResourceGrant(fields,filters,sorts,page,size);
            List<MRsOrgResource> rsOrgResources = responseEntity.getBody();
            Envelop envelop = getResult(rsOrgResources, getTotalCount(responseEntity), page, size);
            return envelop;
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            return envelop;
        }
    }


    @ApiOperation("机构资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadatasValid,method = RequestMethod.POST)
    public boolean orgValid(
            @ApiParam(name="data",value="授权数据元",defaultValue = "")
            @RequestParam(value="data") String data,
            @ApiParam(name="valid",value="授权数据元ID",defaultValue = "")
            @RequestParam(value="valid") int valid) throws Exception
    {
        return resourcesGrantClient.orgValid(data, valid);
    }

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrant, method = RequestMethod.POST)
    public Envelop orgMetadataGrant(
            @ApiParam(name = "id", value = "授权ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "dimension", value = "授权ID", defaultValue = "")
            @RequestParam(value = "dimension") String dimension) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.metadataOrgGrant(id, dimension));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrants, method = RequestMethod.POST)
    public Envelop orgMetadataGrant(
            @ApiParam(name = "model", value = "数据模型", defaultValue = "")
            @RequestParam String model) throws Exception {

        Envelop envelop = new Envelop();
        try{
            envelop.setObj(resourcesGrantClient.orgMetadataGrant(model));
            envelop.setSuccessFlg(true);
            return envelop;
        }catch (Exception e){
            e.printStackTrace();
            return failed("授权失败！");
        }
    }

    @ApiOperation("单个机构授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.OrgGrantResources, method = RequestMethod.POST)
    public Envelop grantOrgResource(
            @ApiParam(name = "orgCode", value = "机构ID", defaultValue = "")
            @PathVariable(value = "orgCode") String orgCode,
            @ApiParam(name = "resourceIds", value = "资源ID", defaultValue = "")
            @RequestParam(value = "resourceIds") String resourceIds) throws Exception {
        Envelop envelop = new Envelop();
        try{
            Collection<MRsOrgResource> rsOrgResource = resourcesGrantClient.grantOrgResource(orgCode,resourceIds);
            envelop.setObj(rsOrgResource);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @ApiOperation("机构资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgResMetadataGrants,method = RequestMethod.GET)
    public Envelop queryOrgRsMetadataGrant(
            @ApiParam(name="Org_res_id",value="授权角色编号",defaultValue = "1")
            @PathVariable(value="Org_res_id")String orgResId,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(value="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "15")
            @RequestParam(value="size",required = false)int size) throws Exception
    {
        try
        {
            ResponseEntity<List<MRsOrgResourceMetadata>> responseEntity = resourcesGrantClient.getOrgRsMetadatas(orgResId);
            return getResult(responseEntity.getBody(), responseEntity.getBody().size(), 1, 15);
        }
        catch (Exception e)
        {
            Envelop envelop = new Envelop();
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("查询出错");
            return envelop;
        }
    }

    @ApiOperation("机构资源取消授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrantsNoPage,method = RequestMethod.GET)
    public Envelop queryOrgResourceGrantNoPage(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters) throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsOrgResource> mRsOrgResources = resourcesGrantClient.queryOrgResourceGrantNoPage(filters);
            if(mRsOrgResources != null){
                envelop.setSuccessFlg(true);
                envelop.setDetailModelList(mRsOrgResources);
            }
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }
    @ApiOperation("机构资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.DELETE)
    public Envelop deleteOrgGrantBatch(
            @ApiParam(name = "ids", value = "授权ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) throws Exception {
        Envelop envelop = new Envelop();
        try{
            resourcesGrantClient.deleteOrgGrantBatch(ids);
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("机构-资源授权-维度授权-根据ID获取资源数据元授权")
    public Envelop getOrgRsMetadataGrantById(
            @ApiParam(name="id",value="id",defaultValue = "")
            @PathVariable(value="id") String id) throws Exception
    {
        Envelop envelop = new Envelop();
        try{
            MRsOrgResourceMetadata rsOrgResourceMetadata = resourcesGrantClient.getOrgRsMetadataGrantById(id);
            RsOrgResourceMetadataModel model = convertToModel(rsOrgResourceMetadata, RsOrgResourceMetadataModel.class);
            envelop.setObj(getOrgDetail(model));
            envelop.setSuccessFlg(true);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    private RsOrgResourceMetadataModel getOrgDetail(RsOrgResourceMetadataModel model){
        if(model!=null){
            MRsResourceMetadata rsResourceMetadata = resourceMetadataClient.getRsMetadataById(model.getResourceMetadataId());
            MRsMetadata mRsMetadata = metadataClient.getMetadataById(rsResourceMetadata.getMetadataId());
            if(org.apache.commons.lang3.StringUtils.isEmpty(model.getId()))
                model.setResourceMetadataName(mRsMetadata.getName());
            model.setMetaColunmType(mRsMetadata.getColumnType());
            model.setMetaId(mRsMetadata.getId());
            if(!org.apache.commons.lang3.StringUtils.isEmpty(mRsMetadata.getDictCode()) && !"0".equals(mRsMetadata.getDictCode()))
                model.setDictEntries(rsDictionaryEntryClient.searchRsDictionaryEntries("", "dictCode=" +mRsMetadata.getDictCode(), "", 1, 500).getBody());
        }
        return model;
    }

}
