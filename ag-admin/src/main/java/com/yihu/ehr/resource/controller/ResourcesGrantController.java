package com.yihu.ehr.resource.controller;

import com.yihu.ehr.agModel.resource.RsAppResourceMetadataModel;
import com.yihu.ehr.agModel.resource.TreeModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.resource.*;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.resource.client.*;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.util.Envelop;
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
@Api(value = "resourceGrant", description = "资源授权服务接口")
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
    private RsDictionaryClient rsDictionaryClient;
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
            for(MApp app: apps){
                if(ids.indexOf(app.getOrg())==-1)
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
            @ApiParam(name = "addIds", value = "新增应用ID", defaultValue = "")
            @RequestParam(value = "addIds") String addIds,
            @ApiParam(name = "deleteIds", value = "删除应用ID", defaultValue = "")
            @RequestParam(value = "deleteIds") String deleteIds) throws Exception {

        Envelop envelop = new Envelop();
        try{
            if(StringUtils.isEmpty(deleteIds) || resourcesGrantClient.deleteGrantByResId(resourceId, deleteIds)){
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

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceApps, method = RequestMethod.DELETE)
    public boolean deleteGrantByResId(
            @ApiParam(name="resource_id",value="授权ID",defaultValue = "")
            @PathVariable(value="resource_id")String resourceId,
            @ApiParam(name="app_ids",value="删除应用ID",defaultValue = "")
            @RequestParam(value="app_ids") String appIds) throws Exception
    {
        resourcesGrantClient.deleteGrantByResId(resourceId, appIds);
        return true;
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
            if(model!=null){
                MRsResourceMetadata rsResourceMetadata = resourceMetadataClient.getRsMetadataById(model.getResourceMetadataId());
                MRsMetadata mRsMetadata = metadataClient.getMetadataById(rsResourceMetadata.getMetadataId());
                model.setMetaColunmType(mRsMetadata.getColumnType());
                model.setMetaId(mRsMetadata.getId());
                if(!StringUtils.isEmpty(mRsMetadata.getDictCode()) && !"0".equals(mRsMetadata.getDictCode()))
                    model.setDictEntries(rsDictionaryEntryClient.searchRsDictionaryEntries("", "dictCode=" +mRsMetadata.getDictCode(), "", 1, 500).getBody());
            }
            envelop.setObj(model);
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
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasValid,method = RequestMethod.PUT)
    public boolean valid(
            @ApiParam(name="ids",value="授权数据元ID",defaultValue = "")
            @RequestParam(value="ids") String ids,
            @ApiParam(name="valid",value="授权数据元ID",defaultValue = "")
            @RequestParam(value="valid") int valid) throws Exception
    {
        return resourcesGrantClient.valid(ids, valid);
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

}
