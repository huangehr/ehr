package com.yihu.ehr.api.esb.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.esb.model.HosEsbMiniRelease;
import com.yihu.ehr.api.esb.service.HosEsbMiniReleaseService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.esb.MHosEsbMiniRelease;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
public class HosReleaseController extends BaseRestController{

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
            @RequestParam(value = "json_data", required = true) String jsonData,
            @ApiParam(name = "file", value = "日志文件")
            @RequestPart(value = "file") MultipartFile file) throws Exception {
        if (!file.isEmpty()) {
            HosEsbMiniRelease hosEsbMiniRelease = toEntity(jsonData, HosEsbMiniRelease.class);
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            ObjectNode objectNode = fastDFSUtil.upload(file.getInputStream(),fileExtension,"");
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            String filePath = groupName.substring(1,groupName.length()-1) + "/" + remoteFileName.substring(1,remoteFileName.length()-1);
            hosEsbMiniRelease.setFile(filePath);
            hosEsbMiniReleaseService.save(hosEsbMiniRelease);
            return convertToModel(hosEsbMiniRelease, MHosEsbMiniRelease.class, null);
        }else {
            throw new Exception("日志文件不能为空");
        }

    }

    @RequestMapping(value = "/updateHosEsbMiniRelease", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改程序版本发布信息", notes = "修改程序版本发布信息")
    public MHosEsbMiniRelease updateHosEsbMiniRelease(
            @ApiParam(name = "json_data", value = "")
            @RequestParam(value = "json_data") String jsonData,
            @ApiParam(name = "file", value = "日志文件")
            @RequestPart(value = "file") MultipartFile file) throws Exception {
        HosEsbMiniRelease hosEsbMiniRelease = toEntity(jsonData, HosEsbMiniRelease.class);
        if(!file.isEmpty()){
            //删除旧文件
            String filePath = hosEsbMiniRelease.getFile();
            String groupName = filePath.split("/")[0];
            String remoteFileName = filePath.substring(groupName.length()+1,filePath.length());
            fastDFSUtil.delete(groupName,remoteFileName);
            //新文件
            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
            ObjectNode objectNode = fastDFSUtil.upload(file.getInputStream(),fileExtension,"");
            String groupNameNew = objectNode.get("groupName").toString();
            String remoteFileNameNew = objectNode.get("remoteFileName").toString();
            String filePathNew = groupNameNew.substring(1,groupNameNew.length()-1) + "/" + remoteFileNameNew.substring(1,remoteFileNameNew.length()-1);
            hosEsbMiniRelease.setFile(filePathNew);
        }
        hosEsbMiniReleaseService.save(hosEsbMiniRelease);
        return convertToModel(hosEsbMiniRelease, MHosEsbMiniRelease.class, null);
    }


    @RequestMapping(value = "/deleteHosEsbMiniRelease/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除程序版本发布信息", notes = "删除程序版本发布信息")
    public boolean deleteHosEsbMiniRelease(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) throws Exception {
        HosEsbMiniRelease hosEsbMiniRelease = hosEsbMiniReleaseService.retrieve(id);
        String filePath = hosEsbMiniRelease.getFile();
        String groupName = filePath.split("/")[0];
        String remoteFileName = filePath.substring(groupName.length()+1,filePath.length());
        fastDFSUtil.delete(groupName,remoteFileName);
        hosEsbMiniReleaseService.delete(id);
        return true;
    }
}