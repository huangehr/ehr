package com.yihu.ehr.portal.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.portal.MItResource;
import com.yihu.ehr.portal.model.ItResource;
import com.yihu.ehr.portal.service.ItResourceService;
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
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/17.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "itResource", description = "可下载资源管理服务", tags = {"云门户-下载资源管理"})
public class ItResourceEndPoint  extends EnvelopRestEndPoint {

    @Autowired
    private ItResourceService resourceService;

//    @Autowired
//    private FastDFSUtil fastDFSUtil;

    @RequestMapping(value = "/itResource/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询可下载资源列表")
    public List<MItResource> searchItResources(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,url")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestBody(required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "+name,+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<ItResource> itResourceList = resourceService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, resourceService.getCount(filters), page, size);
        return (List<MItResource>) convertToModels(itResourceList, new ArrayList<MItResource>(itResourceList.size()), MItResource.class, fields);
    }


    @RequestMapping(value = "/itResource", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增-可下载资源信息")
    public MItResource saveItResource(
            @ApiParam(name = "itResourceJsonData", value = "资源信息json数据")
            @RequestBody String itResourceJsonData
    ) throws Exception {
        ItResource itResource = toEntity(itResourceJsonData, ItResource.class);
        itResource.setStatus(0);
        resourceService.save(itResource);
        return convertToModel(itResource, MItResource.class);
    }

    @RequestMapping(value = "/itResource", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改-可下载资源信息")
    public MItResource updateItResource(
            @ApiParam(name = "itResourceJsonData", value = "资源json数据")
            @RequestBody String itResourceJsonData
    ) throws Exception {
        ItResource itResource = toEntity(itResourceJsonData, ItResource.class);
        resourceService.save(itResource);
        return convertToModel(itResource, MItResource.class);
    }

    @RequestMapping(value = "/itResource/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除-资源信息" )
    public boolean deleteItResource(
            @ApiParam(name = "itResourceId", value = "资源ID")
            @RequestParam(value = "itResourceId", required = true) Integer itResourceId
    ) throws Exception {

        resourceService.deleteItResource(itResourceId);
        return true;
    }

    //TODO 使用svr-file-resource 服务的上传和下载
//    /**
//     *
//     * 资质上传
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "/itResource/upload",method = RequestMethod.POST)
//    @ApiOperation(value = "上传文件,把文件转成流的方式发送")
//    public String uploadFile(
//            @ApiParam(name = "jsonData", value = "文件转化后的输入流")
//            @RequestBody String jsonData ) throws Exception {
//        if(jsonData == null){
//            return null;
//        }
//        String date = URLDecoder.decode(jsonData, "UTF-8");
//
//        String[] fileStreams = date.split(",");
//        String is = URLDecoder.decode(fileStreams[0],"UTF-8").replace(" ","+");
//        byte[] in = Base64.getDecoder().decode(is);
//
//        String pictureName = fileStreams[1].substring(0,fileStreams[1].length()-1);
//        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
//        String description = null;
//        if ((pictureName != null) && (pictureName.length() > 0)) {
//            int dot = pictureName.lastIndexOf('.');
//            if ((dot > -1) && (dot < (pictureName.length()))) {
//                description = pictureName.substring(0, dot);
//            }
//        }
//        InputStream inputStream = new ByteArrayInputStream(in);
//        ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
//        String groupName = objectNode.get("groupName").toString();
//        String remoteFileName = objectNode.get("remoteFileName").toString();
////        String path = "{\"groupName\":" + groupName + ",\"remoteFileName\":" + remoteFileName + "}";
//        String path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);
//        //返回文件路径
//        return path;
//    }
//
//    /**
//     * 机构资质下载
//     * @return
//     */
//    @RequestMapping(value = "/organizations/down",method = RequestMethod.GET)
//    @ApiOperation(value = "下载资源")
//    public String downloadFile(
//            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
//            @RequestParam(value = "group_name") String groupName,
//            @ApiParam(name = "remote_file_name", value = "服务器文件名称", defaultValue = "")
//            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {
//        byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);
//
//        String fileStream = new String(Base64.getEncoder().encode(bytes));
//        String enFileStream = URLEncoder.encode(fileStream, "UTF-8");
//        return enFileStream;
//    }

}
