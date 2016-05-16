package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosReleaseClient;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin/esb")
@RestController
@Api(value = "程序版本发布信息管理接口", description = "程序版本发布信息管理接口" ,tags = {"程序版本发布信息管理接口"})
public class HosReleaseController extends BaseController {

    @Autowired
    private HosReleaseClient hosReleaseClient;


    @RequestMapping(value = "/searchHosEsbMiniReleases", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件获取程序版本发布信息", notes = "根据查询条件获取程序版本发布信息")
    public List<MHosEsbMiniRelease> searchHosEsbMiniReleases(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws ParseException {
        return hosReleaseClient.searchHosEsbMiniReleases(fields,filters,sorts,size,page);
    }



//    @RequestMapping(value = "/createHosEsbMiniRelease", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation(value = "创建程序版本发布信息", notes = "创建程序版本发布信息")
//    public MHosEsbMiniRelease createHosEsbMiniRelease(
//            @ApiParam(name = "json_data", value = "", defaultValue = "")
//            @RequestParam(value = "json_data", required = true) String jsonData,
//            @ApiParam(name = "file", value = "日志文件")
//            @RequestPart() MultipartFile file) throws Exception {
//       return hosReleaseClient.createHosEsbMiniRelease(jsonData,file);
//    }
//
//    @RequestMapping(value = "/updateHosEsbMiniRelease", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    @ApiOperation(value = "修改程序版本发布信息", notes = "修改程序版本发布信息")
//    public MHosEsbMiniRelease updateHosEsbMiniRelease(
//            @ApiParam(name = "json_data", value = "")
//            @RequestParam(value = "json_data") String jsonData,
//            @ApiParam(name = "file", value = "日志文件")
//            @RequestPart() MultipartFile file) throws Exception {
//        return hosReleaseClient.updateHosEsbMiniRelease(jsonData,file);
//    }


    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除程序版本发布信息", notes = "删除程序版本发布信息")
    public boolean deleteHosEsbMiniRelease(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosReleaseClient.deleteHosEsbMiniRelease(id);
    }
}