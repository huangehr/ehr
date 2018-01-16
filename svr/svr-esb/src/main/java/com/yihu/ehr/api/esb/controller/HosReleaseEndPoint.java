package com.yihu.ehr.api.esb.controller;

import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.api.esb.service.HosEsbMiniReleaseService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.12 18:00
 */
@RequestMapping(ApiVersion.Version1_0 + "/esb")
@RestController
@Api(value = "程序版本发布信息管理接口", description = "程序版本发布信息管理接口")
public class HosReleaseEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private HosEsbMiniReleaseService hosEsbMiniReleaseService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

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
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<HosEsbMiniRelease> hosEsbMiniReleases = hosEsbMiniReleaseService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hosEsbMiniReleaseService.getCount(filters), page, size);

        return (List<MHosEsbMiniRelease>) convertToModels(hosEsbMiniReleases, new ArrayList<MHosEsbMiniRelease>(hosEsbMiniReleases.size()), MHosEsbMiniRelease.class, fields);
    }



    @RequestMapping(value = "/createHosEsbMiniRelease", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建程序版本发布信息", notes = "创建程序版本发布信息")
    public MHosEsbMiniRelease createHosEsbMiniRelease(
            @ApiParam(name = "json_data", value = "", defaultValue = "")
            @RequestBody String jsonData) throws Exception {
             HosEsbMiniRelease hosEsbMiniRelease = toEntity(jsonData, HosEsbMiniRelease.class);
            hosEsbMiniReleaseService.save(hosEsbMiniRelease);
            return convertToModel(hosEsbMiniRelease, MHosEsbMiniRelease.class, null);
    }

    @RequestMapping(value = "/updateHosEsbMiniRelease", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改程序版本发布信息", notes = "修改程序版本发布信息")
    public MHosEsbMiniRelease updateHosEsbMiniRelease(
            @ApiParam(name = "json_data", value = "")
            @RequestBody String jsonData) throws Exception {
        HosEsbMiniRelease hosEsbMiniRelease = toEntity(jsonData, HosEsbMiniRelease.class);
        hosEsbMiniReleaseService.save(hosEsbMiniRelease);
        return convertToModel(hosEsbMiniRelease, MHosEsbMiniRelease.class, null);
    }


    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除程序版本发布信息", notes = "删除程序版本发布信息")
    public String deleteHosEsbMiniRelease(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        HosEsbMiniRelease hosEsbMiniRelease = hosEsbMiniReleaseService.retrieve(id);
        String filePath = hosEsbMiniRelease.getFile();
        try{
            deleteFile(filePath);
        }catch (Exception e){
            return "版本文件删除失败！";
        }
        hosEsbMiniReleaseService.delete(id);
        return "success";
    }


    @RequestMapping(value = "/deleteHosEsbMiniReleases", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据查询条件批量删除程序版本发布信息", notes = "根据查询条件批量删除程序版本发布信息")
    public boolean deleteHosEsbMiniReleases(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<HosEsbMiniRelease> hosEsbMiniReleases = hosEsbMiniReleaseService.search(filters);
        for(HosEsbMiniRelease hosEsbMiniRelease : hosEsbMiniReleases){
            String filePath = hosEsbMiniRelease.getFile();
            try{
                deleteFile(filePath);
            }catch (Exception e){
               throw new  RuntimeException("文件删除失败！");
            }
            hosEsbMiniReleaseService.delete(hosEsbMiniRelease.getId());
        }
        return true;
    }

    private void deleteFile(String filePath) throws Exception {
        String groupName = filePath.split(":")[0];
        String remoteFileName = filePath.substring(groupName.length()+1,filePath.length());
        fastDFSUtil.delete(groupName,remoteFileName);
    }
}