package com.yihu.ehr.government.service;

import com.yihu.ehr.agModel.government.GovernmentMenuModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by wxw on 2017/11/2.
 */
@FeignClient(name= MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface GovernmentMenuClient {

    @RequestMapping(value = ServiceApi.Government.SearchGovernmentMenu, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询政府服务平台菜单")
    ListResult getGovernmentMenuList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuById , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    GovernmentMenuModel getGovernmentMenuById(@RequestParam(value = "id") Integer id);

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckCode, method = RequestMethod.GET)
    @ApiOperation(value = "检查编码是否唯一")
    int getCountByCode(@RequestParam(value = "code") String code);

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckName, method = RequestMethod.GET)
    @ApiOperation(value = "检查名称是否唯一")
    int getCountByName(@RequestParam(value = "name") String name);

    @RequestMapping(value = ServiceApi.Government.AddGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "新增菜单")
    GovernmentMenuModel saveGovernmentMenu(@RequestBody String jsonData);

    @RequestMapping(value = ServiceApi.Government.UpdateGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "修改菜单")
    GovernmentMenuModel updateGovernmentMenu(@RequestBody String jsonData);
}
