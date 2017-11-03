package com.yihu.ehr.government.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.government.GovernmentMenu;
import com.yihu.ehr.government.service.GovernmentMenuService;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by wxw on 2017/11/2.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "government_menu", description = "政府服务平台菜单", tags = {"政府服务平台-菜单"})
public class GovernmentMenuEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GovernmentMenuService governmentMenuService;

    @RequestMapping(value = ServiceApi.Government.SearchGovernmentMenu, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询政府服务平台菜单")
    public ListResult getGovernmentMenuList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        ListResult listResult = new ListResult();
        List<GovernmentMenu> governmentMenuList = governmentMenuService.search(fields, filters, sorts, page, size);
        if (governmentMenuList != null) {
            listResult.setDetailModelList(governmentMenuList);
            listResult.setTotalCount((int)governmentMenuService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        } else {
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuById , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    public GovernmentMenu detail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") Integer id) {
        GovernmentMenu governmentMenu = governmentMenuService.getById(id);
        return governmentMenu;
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckCode, method = RequestMethod.GET)
    @ApiOperation(value = "检查编码是否唯一")
    public int checkCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code") String code) throws Exception {
        int i = governmentMenuService.checkCode(code);
        return i;
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckName, method = RequestMethod.GET)
    @ApiOperation(value = "检查名称是否唯一")
    public int checkName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) throws Exception {
        int result = governmentMenuService.checkName(name);
        return result;
    }

    @RequestMapping(value = ServiceApi.Government.AddGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "新增菜单")
    public GovernmentMenu create(
            @ApiParam(name = "jsonData", value = " 菜单信息Json", defaultValue = "")
            @RequestBody String jsonData){
        GovernmentMenu governmentMenu = toEntity(jsonData, GovernmentMenu.class);
        governmentMenu = governmentMenuService.saveGovernmentMenu(governmentMenu);
        return governmentMenu;
    }

    @RequestMapping(value = ServiceApi.Government.UpdateGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "修改菜单")
    public GovernmentMenu resetInfo(
            @ApiParam(name = "jsonData", value = " 菜单信息Json", defaultValue = "")
            @RequestBody String jsonData){
        GovernmentMenu governmentMenu = toEntity(jsonData, GovernmentMenu.class);
        governmentMenu = governmentMenuService.updateGovernmentMenu(governmentMenu);
        return governmentMenu;
    }
}
