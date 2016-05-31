package com.yihu.ehr.resource.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.model.resource.MRsInterface;
import com.yihu.ehr.resource.model.RsInterface;
import com.yihu.ehr.resource.service.RsInterfaceService;
import com.yihu.ehr.util.controller.EnvelopRestEndPoint;
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
 * @author linaz
 * @created 2016.05.17 16:33
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "interfaces", description = "资源服务接口")
public class RsInterfaceEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RsInterfaceService interfaceService;

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取资源列表", notes = "根据查询条件获取资源列表")
    public List<MRsInterface> searchRsInterfaces(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<RsInterface> interfaces = interfaceService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, interfaceService.getCount(filters), page, size);

        return (List<MRsInterface>) convertToModels(interfaces, new ArrayList<MRsInterface>(interfaces.size()), MRsInterface.class, fields);
    }


    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建资源", notes = "创建资源")
    public MRsInterface createRsInterface(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
        RsInterface rsInterface = toEntity(jsonData, RsInterface.class);
        rsInterface.setId(getObjectId(BizObject.RsInterface));
        interfaceService.save(rsInterface);
        return convertToModel(rsInterface, MRsInterface.class, null);

    }

    @RequestMapping(value = ServiceApi.Resources.Interfaces, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改资源", notes = "修改资源")
    public MRsInterface updateRsInterface(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        RsInterface rsInterface = toEntity(jsonData, RsInterface.class);
        interfaceService.save(rsInterface);
        return convertToModel(rsInterface, MRsInterface.class, null);
    }

    @RequestMapping(value = ServiceApi.Resources.Interface, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public MRsInterface getRsInterfaceById(
            @ApiParam(name = "id", value = "", defaultValue = "")
            @PathVariable(value = "id") String id) {
        RsInterface rsInterface = interfaceService.retrieve(id);
        return convertToModel(rsInterface, MRsInterface.class);
    }

    @RequestMapping(value = ServiceApi.Resources.InterfaceNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取标准字典")
    public boolean isExistenceName(
            @ApiParam(name = "name", value = "", defaultValue = "")
            @RequestParam(value = "name") String name) {
        return interfaceService.isNameExist(name);
    }

    @RequestMapping(value = ServiceApi.Resources.Interface, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除资源", notes = "删除资源")
    public boolean deleteRsInterface(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        interfaceService.delete(id);
        return true;
    }
}