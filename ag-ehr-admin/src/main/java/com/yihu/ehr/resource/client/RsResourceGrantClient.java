package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.org.MRsOrgResource;
import com.yihu.ehr.model.org.MRsOrgResourceMetadata;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsAppResourceMetadata;
import com.yihu.ehr.model.resource.MRsRolesResource;
import com.yihu.ehr.model.resource.MRsRolesResourceMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @created 2016.05.23 17:11
 */
@FeignClient(value = MicroServices.Resource)
@RequestMapping(value = ApiVersion.Version1_0)
@ApiIgnore
public interface RsResourceGrantClient {

    @ApiOperation("单个应用授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.AppsGrantResources, method = RequestMethod.POST)
    Collection<MRsAppResource> grantAppResource(
            @PathVariable(value = "appId") String appId,
            @RequestParam(value = "resourceIds") String resourceIds);

    @ApiOperation("资源授权多个应用")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantApps, method = RequestMethod.POST)
    Collection<MRsAppResource> grantResourceApp(
            @PathVariable(value = "resourceId") String resourceId,
            @RequestParam(value = "appIds") String appId);

    @ApiOperation("资源授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrant, method = RequestMethod.DELETE)
    boolean deleteGrant(
            @PathVariable(value = "id") String id);
    
    @ApiOperation("资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.DELETE)
    boolean deleteGrantBatch(
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Resources.ResourceGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源授权")
    public MRsAppResource getRsAppGrantById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrants, method = RequestMethod.GET)
    ResponseEntity<List<MRsAppResource>> queryAppResourceGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("资源授权查询-不分页")
    @RequestMapping(value = ServiceApi.Resources.ResourceGrantsNoPage,method = RequestMethod.GET)
    List<MRsAppResource> queryAppResourceGrantNoPage(
            @RequestParam(value="filters",required = false)String filters);

    @ApiOperation("资源数据元授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrantApp, method = RequestMethod.POST)
    MRsAppResourceMetadata grantRsMetaData(
            @PathVariable(value = "metadataId") String metadataId,
            @PathVariable(value = "appResourceId") String appResourceId);

    @ApiOperation("资源数据元批量授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataListGrantApp, method = RequestMethod.POST)
    Collection<MRsAppResourceMetadata> grantRsMetaDataBatch(
            @PathVariable(value = "appResourceId") String appResourceId,
            @RequestParam(value = "metadataIds") String metadataIds);

    @ApiOperation("资源数据元授权删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.DELETE)
    boolean deleteMetadataGrant(
            @PathVariable(value = "id") String id);

    @ApiOperation("资源数据元授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.DELETE)
    boolean deleteMetadataGrantBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("根据ID获取资源数据元授权")
    public MRsAppResourceMetadata getRsMetadataGrantById(
            @PathVariable(value="id") String id);

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.GET)
    ResponseEntity<List<MRsAppResourceMetadata>> queryAppRsMetadataGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadatasValid,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    boolean valid(
            @RequestBody String data,
            @RequestParam(value="valid") int valid);

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrant, method = RequestMethod.POST)
    MRsAppResourceMetadata metadataGrant(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "dimension") String dimension);

    @ApiOperation("资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsAppResourceMetadata metadataGrant(
            @RequestBody String model);

    @ApiOperation("资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrants,method = RequestMethod.GET)
    ResponseEntity<List<MRsAppResourceMetadata>> getAppRsMetadatas(
            @PathVariable(value="app_res_id")String appResId);

    @ApiOperation("查询资源应用下存在多少授权数据元")
    @RequestMapping(value = ServiceApi.Resources.ResourceAppMetadataGrantExistence, method = RequestMethod.GET)
    List<Map> appMetaExistence(
            @RequestParam("res_app_ids") String resAppIds);

    @ApiOperation("角色组资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.GET)
    ResponseEntity<List<MRsRolesResource>> queryRolesResourceGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("角色组资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadatasValid,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    boolean rolesValid(
            @RequestBody String data,
            @RequestParam(value="valid") int valid);

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrant, method = RequestMethod.POST)
    MRsRolesResourceMetadata metadataRolesGrant(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "dimension") String dimension);

    @ApiOperation("角色组资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsRolesResourceMetadata rolesMetadataGrant(
            @RequestBody String model);

    @ApiOperation("单个角色组授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.RolesGrantResources, method = RequestMethod.POST)
    Collection<MRsRolesResource> grantRolesResource(
            @PathVariable(value = "rolesId") String rolesId,
            @RequestParam(value = "resourceIds") String resourceIds);

    @ApiOperation("角色组资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesResMetadataGrants,method = RequestMethod.GET)
    ResponseEntity<List<MRsRolesResourceMetadata>> getRolesRsMetadatas(
            @PathVariable(value="roles_res_id")String rolesResId);

    @ApiOperation("角色组取消资源授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrantsNoPage,method = RequestMethod.GET)
    List<MRsRolesResource> queryRolesResourceGrantNoPage(
            @RequestParam(value="filters",required = false)String filters);
    @ApiOperation("角色组资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceRolesGrants, method = RequestMethod.DELETE)
    boolean deleteRolesGrantBatch(
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = ServiceApi.Resources.ResourceRolesMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("角色组-资源授权-维度授权-根据ID获取资源数据元授权")
    public MRsRolesResourceMetadata getRolesRsMetadataGrantById(
            @PathVariable(value="id") String id);

    @ApiOperation("单个机构授权多个资源")
    @RequestMapping(value = ServiceApi.Resources.OrgGrantResources, method = RequestMethod.POST)
    Collection<MRsOrgResource> grantOrgResource(
            @PathVariable(value = "orgCode") String orgCode,
            @RequestParam(value = "resourceIds") String resourceIds);


    @ApiOperation("机构资源授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.GET)
    ResponseEntity<List<MRsOrgResource>> queryOrgResourceGrant(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "page", required = false) int page,
            @RequestParam(value = "size", required = false) int size);

    @ApiOperation("机构资源数据元生失效操作")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadatasValid,method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    boolean orgValid(
            @RequestBody String data,
            @RequestParam(value="valid") int valid);

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrant, method = RequestMethod.POST)
    MRsOrgResourceMetadata metadataOrgGrant(
            @PathVariable(value = "id") String id,
            @RequestParam(value = "dimension") String dimension);

    @ApiOperation("机构资源数据元维度授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrants, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MRsOrgResourceMetadata orgMetadataGrant(
            @RequestBody String model);

    @ApiOperation("机构资源数据元授权查询")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgResMetadataGrants,method = RequestMethod.GET)
    ResponseEntity<List<MRsOrgResourceMetadata>> getOrgRsMetadatas(
            @PathVariable(value="Org_res_id")String orgResId);

    @ApiOperation("机构取消资源授权")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrantsNoPage,method = RequestMethod.GET)
    List<MRsOrgResource> queryOrgResourceGrantNoPage(
            @RequestParam(value="filters",required = false)String filters);

    @ApiOperation("机构资源授权批量删除")
    @RequestMapping(value = ServiceApi.Resources.ResourceOrgGrants, method = RequestMethod.DELETE)
    boolean deleteOrgGrantBatch(
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = ServiceApi.Resources.ResourceOrgMetadataGrant,method = RequestMethod.GET)
    @ApiOperation("机构-资源授权-维度授权-根据ID获取资源数据元授权")
    public MRsOrgResourceMetadata getOrgRsMetadataGrantById(
            @PathVariable(value="id") String id);
}
