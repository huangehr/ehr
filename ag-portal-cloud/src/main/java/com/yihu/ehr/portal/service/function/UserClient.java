package com.yihu.ehr.portal.service.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.user.MUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/29.
 */
@FeignClient(name=MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface UserClient {

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
    ResponseEntity<List<MUser>> searchUsers(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    MUser updateUser(@RequestBody String userJsonData);


    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    MUser getUser(@PathVariable(value = "user_id") String userId);


}
