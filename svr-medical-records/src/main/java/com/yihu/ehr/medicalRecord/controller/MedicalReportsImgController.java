package com.yihu.ehr.medicalRecord.controller;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.model.MrMedicalReportImgEntity;
import com.yihu.ehr.medicalRecord.service.MedicalReportImgService;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalReportsImg", description = "病历报告图片服务接口")
public class MedicalReportsImgController extends EnvelopRestEndPoint {

    @Autowired
    MedicalReportImgService mRService;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 保存病历报告图片信息（可批量）
     *
     * @param medicalReportImgs [{"reportId":"String"
     *                          ,"reportImgUrl":"String"
     *                          ,"reportFastdfsImgUrl":"String"
     *                          ,"sort":"String"
     *                          }]
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("创建数据元")
    public boolean saveImgs(
            @ApiParam(name="medicalRecord",value="数据元JSON",defaultValue = "")
            @RequestBody String medicalReportImgs) throws Exception
    {
        List imgList = objectMapper.readValue(medicalReportImgs, new TypeReference<List>() {});
        mRService.createReportImg(imgList);
        return true;
    }

    /**
     * 根据病历报告id获取病历报告图片信息
     *
     * @param reportId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.GET)
    @ApiOperation("根据病历报告ID获取病历报告图片信息")
    public List<MrMedicalReportImgEntity> getImgInfo(
            @ApiParam(name="report_id",value="reportId",defaultValue = "")
            @PathVariable(value="report_id") int reportId) throws Exception
    {
        return mRService.getMedicalReportInfoByReportId(reportId);
    }


    /**
     * 根据病历报告id删除病历报告图片信息
     *
     * @param reportId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImg,method = RequestMethod.DELETE)
    @ApiOperation("批量删除数据元")
    public boolean deleteImgs(
            @ApiParam(name="reportId",value="数据元ID",defaultValue = "")
            @PathVariable(value="report_id") int reportId) throws Exception
    {
        mRService.deleteImgs(reportId);
        return true;
    }

    /**
     * 图片上传
     * @return
     * @throws IOException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImgPicture,method = RequestMethod.POST)
    @ApiOperation(value = "上传图片")
    public String uploadPicture(
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

        //返回文件路径
        return path;
    }

    /**
     * 图片下载
     * @return
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalReportImgPicture,method = RequestMethod.GET)
    @ApiOperation(value = "下载图片")
    public String downloadPicture(
            @ApiParam(name = "groupame", value = "分组", defaultValue = "")
            @RequestParam(value = "groupName") String groupName,
            @ApiParam(name = "remoteFileName", value = "文件名", defaultValue = "")
            @RequestParam(value = "remoteFileName") String remoteFileName,
            @ApiParam(name = "localPath", value = "本地路径", defaultValue = "")
            @RequestParam(value = "localPath") String localPath) throws Exception {

        return fastDFSUtil.download(groupName, remoteFileName, localPath);
    }
}