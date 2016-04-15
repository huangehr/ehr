package com.yihu.ehr.contraller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.Families;
import com.yihu.ehr.service.FamiliesService;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 *
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "families", description = "家庭关系管理接口")
public class FamiliesController extends BaseRestController {


    @Autowired
    private FamiliesService familiesService;


    @RequestMapping(value = "/families", method = RequestMethod.GET)
    @ApiOperation(value = "获取家庭关系列表")
    public List<Families> searchFamilies(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        return null;
    }

    @RequestMapping(value = "/families", method = RequestMethod.POST)
    @ApiOperation(value = "创建家庭关系")
    public Families createFamily(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        return null;
    }

    @RequestMapping(value = "/families", method = RequestMethod.PUT)
    @ApiOperation(value = "修改家庭关系")
    public Families updateFamily(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestParam(value = "json_data") String jsonData) throws Exception {
        return null;
    }

    @RequestMapping(value = "/families/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取家庭关系")
    public Families getFamily(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) {
        return null;
    }

    @RequestMapping(value = "/families/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除家庭关系")
    public boolean deleteFamily(
            @ApiParam(name = "id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return true;
    }



}