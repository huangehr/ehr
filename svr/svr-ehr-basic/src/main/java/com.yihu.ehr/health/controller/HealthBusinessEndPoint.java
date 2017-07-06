package com.yihu.ehr.health.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.health.HealthBusiness;
import com.yihu.ehr.health.service.HealthBusinessService;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.health.MHealthBusiness;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/22.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "HealthBusiness", description = "指标分类管理", tags = {"指标分类-管理"})
public class HealthBusinessEndPoint  extends EnvelopRestEndPoint {
    @Autowired
    HealthBusinessService healthBusinessService;

    @RequestMapping(value = "/healthBusiness/pageList", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询指标分类列表")
    public ListResult getHealthBusinessList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<HealthBusiness> healthBusinessList = healthBusinessService.search(fields, filters, sorts, page, size);
        if(healthBusinessList != null){
            listResult.setDetailModelList(healthBusinessList);
            listResult.setTotalCount((int)healthBusinessService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = "/healthBusiness/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "获取指标分类列表")
    public List<MHealthBusiness> getAllHealthBusiness() throws Exception {
        List<HealthBusiness> healthBusinesses = healthBusinessService.getAllHealthBusiness();
        return (List<MHealthBusiness>) convertToModels(healthBusinesses, new ArrayList<MHealthBusiness>(healthBusinesses.size()), MHealthBusiness.class, null);
    }

    @ApiOperation(value = "根据父ID获取子指标分类列表")
    @RequestMapping(value = "/healthBusiness/childs", method = RequestMethod.GET)
    List<MHealthBusiness> searchChildHealthBusiness(
            @RequestParam(value = "parentId", required = true) Integer parentId) throws Exception {
        List<HealthBusiness> healthBusinesses = healthBusinessService.searchByParentId(parentId);
        return (List<MHealthBusiness>) convertToModels(healthBusinesses, new ArrayList<MHealthBusiness>(healthBusinesses.size()), MHealthBusiness.class, null);
    }

    @RequestMapping(value = "/healthBusiness/detailById" , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    MHealthBusiness searchHealthBusinessDetail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id) throws Exception {
        HealthBusiness healthBusiness = healthBusinessService.getById(id);
        MHealthBusiness mHealthBusiness = convertToModel(healthBusiness, MHealthBusiness.class);
        return mHealthBusiness;
    }

    @RequestMapping(value = "/healthBusiness/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标分类")
    public boolean deleteHealthBusiness(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id", required = true) Integer id
    ) throws Exception {
        healthBusinessService.deleteHealthBusiness(id);
        return true;
    }

    @RequestMapping(value = "/healthBusiness/checkName" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查名称是否唯一")
    int getCountByName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name", required = true) String name) throws Exception {
        return healthBusinessService.getCountByName(name);
    }

    @RequestMapping(value = "/healthBusiness/checkCode" , method = RequestMethod.PUT)
    @ApiOperation(value = "检查编码是否唯一")
    int getCountByCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code", required = true) String code) throws Exception {
        return healthBusinessService.getCountByCode(code);
    }

    @RequestMapping(value = "/healthBusiness/add" , method = RequestMethod.POST)
    @ApiOperation(value = "新增指标分类")
    MHealthBusiness saveHealthBusinesst(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData) {
        try {
            HealthBusiness healthBusinesst = toEntity(jsonData, HealthBusiness.class);
            healthBusinesst = healthBusinessService.saveHealthBusinesst(healthBusinesst);
            return convertToModel(healthBusinesst, MHealthBusiness.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(value = "/healthBusiness/update" , method = RequestMethod.POST)
    @ApiOperation(value = "修改指标分类")
    MHealthBusiness updateHealthBusiness(
            @ApiParam(name = "jsonData", value = "json信息")
            @RequestBody String jsonData) {
        try {
            HealthBusiness healthBusiness = toEntity(jsonData, HealthBusiness.class);
            healthBusiness = healthBusinessService.updateHealthBusiness(healthBusiness);
            return convertToModel(healthBusiness, MHealthBusiness.class);

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    @RequestMapping(value = "/health/getHealthBusinessOfChild", method = RequestMethod.GET)
    @ApiOperation(value = "获取指标分类医疗服务子类目列表")
    public List<MHealthBusiness> getHealthBusinessOfChild() {
        List<HealthBusiness> healthBusinesses = healthBusinessService.getHealthBusinessOfChild();
        return (List<MHealthBusiness>) convertToModels(healthBusinesses, new ArrayList<MHealthBusiness>(healthBusinesses.size()), MHealthBusiness.class, null);
    }
}
