package com.yihu.ehr.esb.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.esb.client.HosReleaseClient;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import com.yihu.ehr.model.esb.MHosLog;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    public Envelop searchHosEsbMiniReleases(
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
        ResponseEntity<List<MHosEsbMiniRelease>> responseEntity = hosReleaseClient.searchHosEsbMiniReleases(fields,filters,sorts,size,page);
        List<MHosEsbMiniRelease> mHosEsbMiniReleases = responseEntity.getBody();
        if(mHosEsbMiniReleases.size()>0){
            for(MHosEsbMiniRelease mHosEsbMiniRelease:mHosEsbMiniReleases){
                if(mHosEsbMiniRelease.getReleaseTime()!=null){
                    SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    mHosEsbMiniRelease.setReleaseDate(sm.format(mHosEsbMiniRelease.getReleaseTime()));
                }
            }
        }
        int totalCount = getTotalCount (responseEntity);
        return getResult(mHosEsbMiniReleases, totalCount, page, size);


    }

    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除程序版本发布信息", notes = "删除程序版本发布信息")
    public boolean deleteHosEsbMiniRelease(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        return hosReleaseClient.deleteHosEsbMiniRelease(id);
    }
}