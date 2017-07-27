package com.yihu.ehr.health.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.health.MHealthBusiness;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface HealthBusinessClient {

    @RequestMapping(value = "/healthBusiness/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类管理列表")
    ListResult getHealthBusinessList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/healthBusiness/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取指标分类列表，不分页")
    List<MHealthBusiness> getAllHealthBusiness();

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/healthBusiness/childs", method = RequestMethod.GET)
    List<MHealthBusiness> searchChildHealthBusiness(
            @RequestParam(value = "parentId", required = true) Integer parentId);

    @RequestMapping(value = "/healthBusiness/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    MHealthBusiness searchHealthBusinessDetail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id);

    @RequestMapping(value = "/healthBusiness/delete", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除指标分类")
    boolean deleteHealthBusiness(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id);

    @RequestMapping(value = "/healthBusiness/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    int getCountByName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name);

    @RequestMapping(value = "/healthBusiness/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    int getCountByCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code);

    @RequestMapping(value = "/healthBusiness/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    MHealthBusiness saveHealthBusinesst(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData);

    @RequestMapping(value = "/healthBusiness/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    MHealthBusiness updateHealthBusiness(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData);
}
