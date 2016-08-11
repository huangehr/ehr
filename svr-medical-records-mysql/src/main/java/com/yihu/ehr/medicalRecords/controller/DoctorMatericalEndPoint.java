package com.yihu.ehr.medicalRecords.controller;

import com.ctc.wstx.util.StringUtil;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.model.Entity.MrDocumentEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrTextEntity;
import com.yihu.ehr.medicalRecords.service.MaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hzp on 2016/7/27.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "素材接口", description = "素材接口管理")
public class DoctorMatericalEndPoint {

    @Autowired
    MaterialService materialService;

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorText,method = RequestMethod.POST)
    @ApiOperation("上传文本素材")
    public boolean uploadTextMaterial(@ApiParam(name="doctor_id",value="创建者Id",defaultValue = "D20160810000001")
                                       @PathVariable(value="doctor_id") String creator,
                                       @ApiParam(name="business_class",value="素材类型",defaultValue = "0")
                                       @RequestParam(value="business_class",required = false) String businessClass,
                                       @ApiParam(name="content",value="内容",defaultValue = "测试")
                                       @RequestParam(value="content",required = true) String content,
                                       @ApiParam(name="patient_id",value="患者Id")
                                       @RequestParam(value="patient_id",required = false) String patientId)  throws Exception{

        return materialService.uploadTextMaterial(creator,businessClass,content,patientId);
    }


    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorText,method = RequestMethod.GET)
    @ApiOperation("获取文本素材")
    public List<String> getTextDialog( @ApiParam(name="doctor_id",value="创建者Id",defaultValue = "D20160810000001")
                                       @PathVariable(value="doctor_id") String doctorId,
                                       @ApiParam(name="business_class",value="素材类型",defaultValue = "0")
                                       @RequestParam(value="business_class",required = true) String businessClass,
                                       @ApiParam(name="page",value="page")
                                       @RequestParam(value="page",required = false) Integer page,
                                       @ApiParam(name="size",value="size")
                                       @RequestParam(value="size",required = false) Integer size) throws Exception{
        return materialService.getTextByClass(doctorId, businessClass, page,size);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorTextDialog,method = RequestMethod.GET)
    @ApiOperation("获取对话文本")
    public List<MrTextEntity> getTextMaterial(@ApiParam(name="doctor_id",value="医生Id",defaultValue = "D20160810000001")
                                          @PathVariable(value="doctor_id") String doctorId,
                                              @ApiParam(name="patient_id",value="患者Id",defaultValue = "D2016081005")
                                          @RequestParam(value="patient_id",required = true) String patientId,
                                              @ApiParam(name="page",value="page")
                                          @RequestParam(value="page",required = false) Integer page,
                                              @ApiParam(name="size",value="size")
                                          @RequestParam(value="size",required = false) Integer size) throws Exception{
        return materialService.getTextByDoctorAndPatient(doctorId, patientId, page,size);
    }

    /****************************************************************************************/

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorImg,method = RequestMethod.POST)
    @ApiOperation("上传图片素材")
    public String uploadImgMaterial(@ApiParam(name="doctor_id",value="医生Id",defaultValue = "D20160807000001")
                                     @RequestParam(value="doctor_id",required = true) String creator,
                                     @ApiParam(name="patient_id",value="患者Id",defaultValue = "350425198506080016")
                                     @RequestParam(value="patient_id",required = false) String patientId,
                                     @ApiParam(name = "content", value = "图片Base64转化后的输入流")
                                     @RequestBody String content,
                                     @ApiParam(name = "extension", value = "扩展名",defaultValue = "jpg")
                                     @RequestParam(value="extension",required = true) String extension
                                    ) throws Exception{

        return materialService.uploadImgMaterial(creator,patientId,content,extension);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.DoctorImg,method = RequestMethod.GET)
    @ApiOperation("获取图片素材")
    public List<MrDocumentEntity> getImgMaterial(@ApiParam(name="doctor_id",value="创建者Id",defaultValue = "D20160810000001")
                                       @RequestParam(value="doctor_id",required = true) String creator,
                                       @ApiParam(name="patient_id",value="患者Id")
                                       @RequestParam(value="patient_id",required = false) String patientId,
                                       @ApiParam(name="page",value="page")
                                       @RequestParam(value="page",required = false) Integer page,
                                       @ApiParam(name="size",value="size")
                                       @RequestParam(value="size",required = false) Integer size) throws Exception{

        return materialService.getImgMaterial(creator, patientId,page,size);
    }

    @RequestMapping(value = ServiceApi.MedicalRecords.ImgList,method = RequestMethod.GET)
    @ApiOperation("获取图片素材")
    public List<MrDocumentEntity> getImgMaterialByIds(@ApiParam(name="ids",value="id列表")
                                                      @RequestParam(value="ids",required = true) String ids) throws Exception {

        return materialService.getImgMaterialByIds(ids);
    }
}
