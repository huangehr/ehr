package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.tj.MQuotaCategory;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by wxw on 2017/8/31.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface QuotaCategoryClient {

    @RequestMapping(value = "/quotaCategory/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类列表")
    ListResult getQuotaCategoryList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/quotaCategory/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取指标分类列表")
    List<MQuotaCategory> getAllQuotaCategory();

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/quotaCategory/childs", method = RequestMethod.GET)
    List<MQuotaCategory> searchChildQuotaCategory(
            @RequestParam(value = "parentId", required = true) Integer parentId);

    @RequestMapping(value = "/quotaCategory/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    MQuotaCategory searchQuotaCategoryDetail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id);

    @RequestMapping(value = "/quotaCategory/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标分类")
    boolean deleteQuotaCategory(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id);

    @RequestMapping(value = "/quotaCategory/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    int getCountByName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name);

    @RequestMapping(value = "/quotaCategory/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    int getCountByCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code);

    @RequestMapping(value = "/quotaCategory/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    MQuotaCategory saveQuotaCategory(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData);

    @RequestMapping(value = "/quotaCategory/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    MQuotaCategory updateQuotaCategory(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData);

    @RequestMapping(value = "/quota/getQuotaCategoryOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    List<MQuotaCategory> getQuotaCategoryOfChild();

    @RequestMapping(value = "/quota/getQuotaCategoryChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类子类列表")
    List<MQuotaCategory> getQuotaCategoryChild();
}
