package com.yihu.ehr.government.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.government.GovernmentMenuModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.government.service.GovernmentMenuClient;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wxw on 2017/11/2.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "government_menu", description = "政府服务平台菜单", tags = {"政府服务平台-菜单"})
public class GovernmentMenuController extends ExtendController<GovernmentMenuModel> {
    @Autowired
    private GovernmentMenuClient governmentMenuClient;
    @Autowired
    private UserClient userClient;

    @RequestMapping(value = ServiceApi.Government.SearchGovernmentMenu, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询政府服务平台菜单")
    public Envelop getGovernmentMenuList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        Envelop envelop = new Envelop();
        try {
            ListResult governmentMenuList = governmentMenuClient.getGovernmentMenuList(fields, filters, sorts, size, page);
            List<GovernmentMenuModel> governmentMenus = new ArrayList<>();
            if (governmentMenuList.getTotalCount() != 0) {
                List<Map<String,Object>> modelList = governmentMenuList.getDetailModelList();
                for(Map<String,Object> map : modelList){
                    GovernmentMenuModel governmentMenuModel = objectMapper.convertValue(map,GovernmentMenuModel.class);
                    if (null != governmentMenuModel && StringUtils.isNotEmpty(governmentMenuModel.getCreateUser())) {
                        MUser user = userClient.getUser(governmentMenuModel.getCreateUser());
                        governmentMenuModel.setCreateUser((null != user) ? user.getRealName() : "" );
                    }
                    if (null != governmentMenuModel && StringUtils.isNotEmpty(governmentMenuModel.getUpdateUser())) {
                        MUser user = userClient.getUser(governmentMenuModel.getUpdateUser());
                        governmentMenuModel.setUpdateUser((null != user) ? user.getRealName() : "" );
                    }
                    governmentMenus.add(governmentMenuModel);
                }
                return getResult(governmentMenus, governmentMenuList.getTotalCount(), governmentMenuList.getCurrPage(), governmentMenuList.getPageSize());
            } else {
                envelop.setSuccessFlg(true);
                return envelop;
            }
        } catch (Exception e) {
            envelop.setErrorMsg("请求发生异常");
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuById , method = RequestMethod.GET)
    @ApiOperation(value = "根据Id查询详情")
    public Envelop detail(
            @ApiParam(name = "id", value = "id")
            @RequestParam(value = "id") Integer id) {
        try {
            String errorMsg = "";

            if (id == null) {
                errorMsg += "id不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            GovernmentMenuModel governmentMenuModel = governmentMenuClient.getGovernmentMenuById(id);
            if (governmentMenuModel == null) {
                return failed("获取详情失败!");
            }
            return success(governmentMenuModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckCode, method = RequestMethod.GET)
    @ApiOperation(value = "检查编码是否唯一")
    public Envelop checkCode(
            @ApiParam(name = "code", value = "编码")
            @RequestParam(value = "code") String code) {
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(code)) {
                envelop.setErrorMsg("编码不能为空！");
            }
            int num = governmentMenuClient.getCountByCode(code);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此编码!");
            }else{
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.Government.GovernmentMenuCheckName, method = RequestMethod.GET)
    @ApiOperation(value = "检查名称是否唯一")
    public Envelop checkName(
            @ApiParam(name = "name", value = "名称")
            @RequestParam(value = "name") String name) {
        try {
            Envelop envelop = new Envelop();
            if (StringUtils.isEmpty(name)) {
                envelop.setErrorMsg("名称不能为空！");
            }
            int num = governmentMenuClient.getCountByName(name);
            if (num > 0) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已经存在此名称!");
            } else {
                envelop.setSuccessFlg(true);
            }
            return envelop;
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.Government.AddGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "新增菜单")
    public Envelop create(
            @ApiParam(name = "jsonData", value = " 菜单信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData,
            @ApiParam(name = "ids", value = "资源报表监测类型ids", defaultValue = "")
            @RequestParam(value = "ids", required = false) String ids){
        try {
            String errorMsg = "";
            GovernmentMenuModel governmentMenuModel = objectMapper.readValue(jsonData, GovernmentMenuModel.class);

            if (StringUtils.isEmpty(governmentMenuModel.getCode())) {
                errorMsg+="编码不能为空！";
            }
            if (StringUtils.isEmpty(governmentMenuModel.getName())) {
                errorMsg+="名称不能为空！";
            }
            if (StringUtils.isEmpty(governmentMenuModel.getUrl())) {
                errorMsg+="链接地址不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }

            String json = objectMapper.writeValueAsString(governmentMenuModel);
            GovernmentMenuModel mQuotaCategory = governmentMenuClient.saveGovernmentMenu(json, ids);
            if (mQuotaCategory == null) {
                return failed("保存失败!");
            }
            return success(mQuotaCategory);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = ServiceApi.Government.UpdateGovernmentMenu, method = RequestMethod.POST)
    @ApiOperation(value = "修改菜单")
    public Envelop resetInfo(
            @ApiParam(name = "jsonData", value = " 菜单信息Json", defaultValue = "")
            @RequestParam(value = "jsonData", required = false) String jsonData,
            @ApiParam(name = "ids", value = "资源报表监测类型ids", defaultValue = "")
            @RequestParam(value = "ids", required = false) String ids){
        try {
            String errorMsg = "";
            GovernmentMenuModel governmentMenuModel = objectMapper.readValue(jsonData, GovernmentMenuModel.class);
            if (null == governmentMenuModel) {
                errorMsg += "内容出错！";
            }
            if (StringUtils.isEmpty(governmentMenuModel.getName())) {
                errorMsg += "名称不能为空！";
            }
            if (StringUtils.isEmpty(governmentMenuModel.getName())) {
                errorMsg+="名称不能为空！";
            }
            if (StringUtils.isEmpty(governmentMenuModel.getUrl())) {
                errorMsg+="链接地址不能为空！";
            }
            if(StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            String json = objectMapper.writeValueAsString(governmentMenuModel);
            GovernmentMenuModel mQuotaCategory = governmentMenuClient.updateGovernmentMenu(json, ids);
            if (mQuotaCategory == null) {
                return failed("修改菜单失败!");
            }
            return success(mQuotaCategory);
        } catch (Exception ex){
            ex.printStackTrace();
            return failedSystem();
        }
    }
}
