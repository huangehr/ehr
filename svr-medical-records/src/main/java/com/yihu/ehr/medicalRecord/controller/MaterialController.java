package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.medicalRecord.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Created by shine on 2016/7/27.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "素材接口", description = "素材接口管理")
public class MaterialController {

    @Autowired
    MaterialService materialService;
    @Autowired
    FileOperationController fileOperationController;

//    @RequestMapping(value = ServiceApi.MedicalRecords.DcotorText,method = RequestMethod.GET)
//    @ApiOperation("判断文本素材是否存在")
//    public boolean checkMaterial( @ApiParam(name="creatorId",value="创建者Id",defaultValue = "")
//                                  @RequestParam(value="creatorId") String creatorId,
//                                  @ApiParam(name="businessClass",value="素材类型",defaultValue = "")
//                                  @RequestParam(value="businessClass") String businessClass,
//                                  @ApiParam(name="content",value="草稿内容",defaultValue = "")
//                                  @RequestParam(value="content") String content,
//                                  @ApiParam(name="patientId",value="患者Id",defaultValue = "")
//                                  @RequestParam(value="patientId") String patientId) throws Exception{
//        return materialService.checkMaterial(creatorId,businessClass,content,patientId);
//    }


    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorText,method = RequestMethod.POST)
    @ApiOperation("上传文本素材")
    public boolean uploadTextMaterial( @ApiParam(name="creatorId",value="创建者Id")
                                       @RequestParam(value="creatorId",required = true) String creatorId,
                                       @ApiParam(name="creatorName",value="创建者name")
                                       @RequestParam(value="creatorName",required = true) String creatorName,
                                       @ApiParam(name="businessClass",value="素材类型")
                                       @RequestParam(value="businessClass",required = true) String businessClass,
                                       @ApiParam(name="content",value="草稿内容")
                                       @RequestParam(value="content",required = true) String content,
                                       @ApiParam(name="patientId",value="患者Id")
                                       @RequestParam(value="patientId",required = false) String patientId,
                                       @ApiParam(name="patientName",value="患者name")
                                       @RequestParam(value="patientName",required = false) String patientName) throws Exception{
        if(materialService.checkTextMaterial(creatorId, businessClass, content, patientId)) {
            return materialService.uploadTextMaterial(creatorId,creatorName, businessClass, content, patientId,patientName);
        }
        else
            return false;
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorText,method = RequestMethod.GET)
    @ApiOperation("获取文本素材")
    public List<String> getTextMaterial(  @ApiParam(name="creatorId",value="创建者Id")
                                          @RequestParam(value="creatorId",required = true) String creatorId,
                                          @ApiParam(name="businessClass",value="素材类型")
                                          @RequestParam(value="businessClass",required = true) String businessClass,
                                          @ApiParam(name="patientId",value="患者Id")
                                          @RequestParam(value="patientId",required = false) String patientId,
                                          @ApiParam(name="page",value="page")
                                          @RequestParam(value="page",required = false) int page,
                                          @ApiParam(name="size",value="size")
                                          @RequestParam(value="size",required = false) int size) throws Exception{
        return materialService.getTextMaterial(creatorId, businessClass, patientId);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorImg,method = RequestMethod.POST)
    @ApiOperation("上传图片素材")
    public boolean uploadImgMaterial(  @ApiParam(name="documentName",value="文件名")
                                       @RequestParam(value="documentName",required = true) String documentName,
                                       @ApiParam(name="creatorId",value="创建者Id")
                                       @RequestParam(value="creatorId",required = true) String creatorId,
                                       @ApiParam(name="creatorName",value="创建者name")
                                       @RequestParam(value="creatorName",required = true) String creatorName,
                                       @ApiParam(name="fileType",value="文件类型")
                                       @RequestParam(value="fileType",required = true) String fileType,
                                       @ApiParam(name="patientId",value="患者Id")
                                       @RequestParam(value="patientId",required = false) String patientId,
                                       @ApiParam(name="patientName",value="患者name")
                                       @RequestParam(value="patientName",required = false) String patientName,
                                       @ApiParam(name = "jsonData", value = "图片转化后的输入流")
                                       @RequestBody String jsonData ) throws Exception{
            Map m=fileOperationController.uploadPicture(creatorId,jsonData);
        if(m!=null && m.get("path")!=null) {
            return materialService.uploadImgMaterial(documentName, creatorId,creatorName,fileType, "2", patientId,patientName, m.get("path").toString());
        }
        else
            return false;
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorImg,method = RequestMethod.GET)
    @ApiOperation("获取图片素材")
    public List<String> getImgMaterial(@ApiParam(name="creatorId",value="创建者Id")
                                       @RequestParam(value="creatorId",required = true) String creatorId,
                                       @ApiParam(name="patientId",value="患者Id")
                                       @RequestParam(value="patientId",required = false) String patientId,
                                       @ApiParam(name="page",value="page")
                                       @RequestParam(value="page",required = false) int page,
                                       @ApiParam(name="size",value="size")
                                       @RequestParam(value="size",required = false) int size) throws Exception{
        return materialService.getImgMaterial(creatorId, patientId);
    }
}
