package com.yihu.ehr.medicalRecord.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.model.MrFileEntity;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import com.yihu.ehr.medicalRecord.service.FileOperationService;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "FileOperation", description = "文件操作接口")
public class FileOperationController extends EnvelopRestEndPoint {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    FileOperationService fileService;

    /**
     * 图片上传
     * @return
     * @throws IOException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.imgFile,method = RequestMethod.POST)
    @ApiOperation(value = "把图片转成流的方式发送")
    public Map uploadPicture(
            @ApiParam(name = "id", value = "用户id")
            @PathVariable(value = "id")String id,
            @ApiParam(name = "jsonData", value = "图片转化后的输入流")
            @RequestBody String jsonData ) throws IOException {

        if(jsonData == null){
            return null;
        }

        String data = URLDecoder.decode(jsonData,"UTF-8");

        String[] fileStreams = data.split(",");
        String is = URLDecoder.decode(fileStreams[0],"UTF-8").replace(" ","+");
        byte[] in = Base64.getDecoder().decode(is);

        String pictureName = fileStreams[1].substring(0,fileStreams[1].length()-1);
        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;

        if ((pictureName != null) && (pictureName.length() > 0)) {

            int dot = pictureName.lastIndexOf('.');
            if ((dot > -1) && (dot < (pictureName.length()))) {

                description = pictureName.substring(0, dot);
            }
        }

        String path = null;

        try {

            InputStream inputStream = new ByteArrayInputStream(in);

            ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();

            path = groupName.substring(1,groupName.length()-1) + ":" + remoteFileName.substring(1,remoteFileName.length()-1);

        } catch (Exception e) {

            LogService.getLogger(MrMedicalReportImgEntity.class).error("图片上传失败；错误代码："+e);
        }

        MrFileEntity fileEntity = new MrFileEntity();
        Map map = new HashMap<>();

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());

        fileEntity.setCreateTime(t);
        fileEntity.setCreateUser(id);
        fileEntity.setFilePath(path);
        fileEntity.setFileType("1");
        fileEntity = fileService.save(fileEntity);
        map.put("id", fileEntity.getId());
        map.put("path", path);


        //返回文件路径
        return map;
    }

    /**
     * 图片下载
     * @return
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.imgFile,method = RequestMethod.GET)
    @ApiOperation(value = "下载图片")
    public String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器图片名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {

        String imageStream = null;

        try {

            byte[] bytes = fastDFSUtil.download(groupName,remoteFileName);

            String fileStream = Base64.getEncoder().encodeToString(bytes);
            imageStream = URLEncoder.encode(fileStream,"UTF-8");

        } catch (IOException e) {

            e.printStackTrace();
        } catch (MyException e) {

            LogService.getLogger(MrMedicalReportImgEntity.class).error("图片下载失败；错误代码：" + e);
        }

        return imageStream;
    }

    /**
     * 文件上传
     * @return
     * @throws IOException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.file,method = RequestMethod.POST)
    @ApiOperation(value = "文件上传")
    public Map uploadFile(
            @ApiParam(name = "id", value = "用户id")
            @PathVariable(value = "id")String id,
            @ApiParam(name = "localPath", value = "本地路径")
            @RequestParam(name="localPath") String localPath,
            @ApiParam(name = "picName", value = "文件名称")
            @RequestParam(name="picName") String picName) throws IOException {

        InputStream in = new FileInputStream(localPath);
        String prefix = localPath.substring(localPath.indexOf(".")+1);

        String path = null;

        try {

            ObjectNode objectNode = fastDFSUtil.upload(in, prefix, picName);

            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();

            path = groupName.substring(1,groupName.length()-1) + ":" +
                    remoteFileName.substring(1,remoteFileName.length()-1);

        } catch (Exception e) {

            LogService.getLogger(MrMedicalReportImgEntity.class).error("图片上传失败；错误代码："+e);
        }

        MrFileEntity fileEntity = new MrFileEntity();
        Map map = new HashMap<>();

        Date date = new Date();
        Timestamp t = new Timestamp(date.getTime());

        fileEntity.setCreateTime(t);
        fileEntity.setCreateUser(id);
        fileEntity.setFilePath(path);
        fileEntity.setFileType("1");
        fileEntity = fileService.save(fileEntity);
        map.put("id", fileEntity.getId());
        map.put("path", path);

        //返回文件路径
        return map;
    }

    /**
     * 文件下载
     * @return
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.file,method = RequestMethod.GET)
    @ApiOperation(value = "下载文件")
    public String downloadFile(
            @ApiParam(name = "groupame", value = "分组", defaultValue = "")
            @RequestParam(value = "groupName") String groupName,
            @ApiParam(name = "remoteFileName", value = "文件名", defaultValue = "")
            @RequestParam(value = "remoteFileName") String remoteFileName,
            @ApiParam(name = "localPath", value = "本地路径", defaultValue = "")
            @RequestParam(value = "localPath") String localPath) throws Exception {

        return fastDFSUtil.download(groupName, remoteFileName, localPath);
    }
}