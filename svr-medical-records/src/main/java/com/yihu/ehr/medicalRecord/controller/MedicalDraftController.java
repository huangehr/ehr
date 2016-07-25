package com.yihu.ehr.medicalRecord.controller;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.medicalRecord.model.MrMedicalDraftEntity;
import com.yihu.ehr.medicalRecord.service.MedicalDraftService;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Guo Yanshan on 2016/7/15.
 */


@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "MedicalDraft", description = "医生对话草稿接口")
public class MedicalDraftController extends EnvelopRestEndPoint {

    @Autowired
    MedicalDraftService mDService;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * 检查对话草稿是否存在
     * 存在：  更新创建时间
     * 不存在：新建对话草稿
     *
     * @param doctorId
     * @param type
     * @param content
     * @param fastdfsURL
     * @param patientId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.CheckMedicalDraft,method = RequestMethod.GET)
    @ApiOperation("检查对话草稿是否存在")
    public boolean checkMedicalDraft(
            @ApiParam(name="doctorId",value="医生Id",defaultValue = "")
            @RequestParam(value="doctorId") String doctorId,
            @ApiParam(name="type",value="草稿类型",defaultValue = "")
            @RequestParam(value="type") String type,
            @ApiParam(name="content",value="草稿内容",defaultValue = "")
            @RequestParam(value="content") String content,
            @ApiParam(name="fastdfsURL",value="图片路径",defaultValue = "")
            @RequestParam(value="fastdfsURL") String fastdfsURL,
            @ApiParam(name="patientId",value="患者Id",defaultValue = "")
            @RequestParam(value="patientId") int patientId) throws Exception
    {

        mDService.checkMedicalDraft(doctorId, type, content, fastdfsURL, patientId);
        return true;
    }

    /**
     * 根据医生ID获取对话草稿
     * 根据医生ID+患者ID+草稿类型获取数据，创建时间倒序（可分页）
     *
     * @param fields   id,doctorId,patientId,type,content,fastdsUrl,createTime
     * @param filters  doctorId,patientId,type
     * @param sorts    createTime
     * @param page     1
     * @param size     10
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDraft,method = RequestMethod.GET)
    @ApiOperation("根据医生ID获取对话草稿")
    public List<MrMedicalDraftEntity> getMedicalDraft(
            @ApiParam(name="fields",value="返回字段",defaultValue = "")
            @RequestParam(name="fields",required = false)String fields,
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(name="filters",required = false)String filters,
            @ApiParam(name="sorts",value="排序",defaultValue = "")
            @RequestParam(name="sorts",required = false)String sorts,
            @ApiParam(name="page",value="页码",defaultValue = "1")
            @RequestParam(name="page",required = false)int page,
            @ApiParam(name="size",value="分页大小",defaultValue = "10")
            @RequestParam(name="size",required = false)int size,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception
    {
        long total = 0;
        Collection<MrMedicalDraftEntity> metaList;

        //过滤条件为空
        if(StringUtils.isEmpty(filters))
        {
            Page<MrMedicalDraftEntity> metadataPage = mDService.getDraft(sorts,reducePage(page),size);
            total = metadataPage.getTotalElements();
            metaList = convertToModels(metadataPage.getContent(),
                    new ArrayList<>(metadataPage.getNumber()),MrMedicalDraftEntity.class,fields);
        }
        else
        {
            List<MrMedicalDraftEntity> metadata = mDService.search(fields,filters,sorts,page,size);
            total = mDService.getCount(filters);
            metaList = convertToModels(metadata,
                    new ArrayList<>(metadata.size()),MrMedicalDraftEntity.class,fields);
        }

        pagedResponse(request,response,total,page,size);
        return (List<MrMedicalDraftEntity>)metaList;
    }

    /**
     * 根据草稿Id删除（可批量）
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDraft,method = RequestMethod.DELETE)
    @ApiOperation("删除草稿")
    public boolean deleteDrafts(
            @ApiParam(name="id",value="id",defaultValue = "")
            @RequestParam(name="id") String ids) throws Exception
    {
        mDService.deleteDraft(ids);
        return true;
    }

    /**
     * 图片上传
     * @return
     * @throws IOException
     */
    @RequestMapping(value = ServiceApi.MedicalRecords.MedicalDraftPicture,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
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

            LogService.getLogger(MrMedicalDraftEntity.class).error("图片上传失败；错误代码："+e);
        }

        //返回文件路径
        return path;
    }
}