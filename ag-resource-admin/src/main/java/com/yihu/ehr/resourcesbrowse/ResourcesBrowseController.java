package com.yihu.ehr.resourcesbrowse;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.FileUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/browse")
@Api(value = "rsBrowse", description = "业务资源浏览接口")
public class ResourcesBrowseController {

    FileUtil fileUtil = new FileUtil();

    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public String getPatientInfo(
            @ApiParam(name = "id_no", value = "身份证号码")
            @RequestParam(value = "id_no", required = true) String idNo,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "行数")
            @RequestParam(value = "size", required = false) int size) throws Exception {

        return fileUtil.file2String("/json/pastHistory.json");
    }

    //
//    @ApiOperation("门户 - 挂号信息展开")
//    @RequestMapping(value = "/home/getRegistrationInfo", method = RequestMethod.GET)
//    public DataList getRegistrationInfo(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
//                                      @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                                      @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//         return null;
//    }
//
//    @ApiOperation("门户 - 在线问诊展开")
//    @RequestMapping(value = "/home/getOnlineVisit", method = RequestMethod.GET)
//    public DataList getOnlineVisit(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
//                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//         return null;
//    }
//
//    @ApiOperation("门户 - 病史信息")
//    @RequestMapping(value = "/home/getDiseaseHistory", method = RequestMethod.GET)
//    public DataList getDiseaseHistory(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
//                                    @ApiParam(name = "searchFlag", value = "是否存在数据过滤", defaultValue = "0") @RequestParam(value = "searchFlag", required = false,defaultValue="0") String searchFlag,
//                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//
//            return null;
//
//    }
//
    @ApiOperation("门户 - 主要健康问题")
    @RequestMapping(value = "/home/getHealthProblem", method = RequestMethod.GET)
    public String getHealthProblem(
            @ApiParam(name = "id_no", value = "身份证号码")
            @RequestParam(value = "id_no", required = true) String idNo,
            @ApiParam(name = "health_problem_code", value = "健康问题代码")
            @RequestParam(value = "health_problem_code", required = true) String healthProblemCode,
            @ApiParam(name = "drugs_flag", value = "常用药查询标识")
            @RequestParam(value = "drugs_flag", required = false) String drugsFlag,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "行数")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        FileUtil fileUtil = new FileUtil();
        return fileUtil.file2String("/json/healthProblem.json");
    }
//


    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public String getMedicalEvents(
            @ApiParam(name = "idNo", value = "身份证号码")
            @RequestParam(value = "idNo", required = true) String idNo,
            @ApiParam(name = "medicalEventsType", value = "就诊事件类别")
            @RequestParam(value = "medicalEventsType", required = true) String medicalEventsType,
            @ApiParam(name = "year", value = "年份", defaultValue = "当前年份")
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @ApiParam(name = "orgCode", value = "机构代码（逗号分隔）")
            @RequestParam(value = "orgCode", required = true) String orgCode,
            @ApiParam(name = "cardType", value = "卡类型")
            @RequestParam(value = "cardType", required = false) String cardType,
            @ApiParam(name = "diagnosisCode", value = "疾病代码（逗号分隔）")
            @RequestParam(value = "diagnosisCode", required = true) String diagnosisCode,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "行数")
            @RequestParam(value = "size", required = false) int size) throws Exception {

        return fileUtil.file2String("/json/MedicalEventInfo.json");

    }

    @ApiOperation("门户 - 相关健康指标")
    @RequestMapping(value = "/home/getHealthIndicators", method = RequestMethod.GET)
    public String getHealthIndicators(
            @ApiParam(name = "id_no", value = "身份证号码") @RequestParam(value = "id_no", required = true) String idNo,
            @ApiParam(name = "medical_index_id", value = "指标项ID") @RequestParam(value = "medical_index_id", required = false) String medicalIndexId,
            @ApiParam(name = "start_time", value = "查询开始时间") @RequestParam(value = "start_time", required = false) String startTime,
            @ApiParam(name = "end_time", value = "查询结束时间") @RequestParam(value = "end_time", required = false) String endTime,
            @ApiParam(name = "health_problem_code", value = "健康问题代码") @RequestParam(value = "health_problem_code", required = false) String healthProblemCode,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return fileUtil.file2String("/json/HealthIndicators.json");
    }


    /************************************
     * 就诊事件详情
     *****************************************************************/
    @ApiOperation("左侧档案列表导航")
    @RequestMapping(value = "/cda/getPatientCdaList", method = RequestMethod.GET)
    public String getPatientCdaList(
            @ApiParam(name = "row_key", value = "档案ID") @RequestParam(value = "row_key", required = true) String rowKey,
            @ApiParam(name = "id_no", value = "身份证号码") @RequestParam(value = "id_no", required = true) String idNo,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return fileUtil.file2String("/json/MedicalEventInfo.json");

    }

    @ApiOperation("右侧档案模版展示")
    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    public String getPatientCdaInfo(
            @ApiParam(name = "rowKey", value = "档案ID") @RequestParam(value = "rowKey", required = true) String rowKey,
            @ApiParam(name = "cdaId", value = "CDA编号") @RequestParam(value = "cdaId", required = true) String cdaId,
            @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo) throws Exception {

        return "跟泽华要数据";
    }

    /************************************
     * 疾病事件
     *****************************************************************/
    @ApiOperation("疾病事件 - 历史用药 - 药品清单")
    @RequestMapping(value = "/disease/getDrugList", method = RequestMethod.GET)
    public String getDrugList(
            @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
            @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return fileUtil.file2String("/json/DrugList.json");

    }
//
//    @ApiOperation("疾病事件 - 历史用药 - 处方记录")
//    @RequestMapping(value = "/disease/getPrescriptionRecord", method = RequestMethod.GET)
//    public DataList getPrescriptionRecord(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
//                              @ApiParam(name = "queryType", value = "查询内容代码") @RequestParam(value = "queryType", required = true) Integer queryType,
//                              @ApiParam(name = "year", value = "年份") @RequestParam(value = "year", required = false) String year,
//                              @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
//                              @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                              @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//
//            return null;
//
//    }
//
//    @ApiOperation("疾病事件 - 历史用药 - 用药记录")
//    @RequestMapping(value = "/disease/getUsingDrugRecords", method = RequestMethod.GET)
//    public DataList getUsingDrugRecords(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
//                                      @ApiParam(name = "year", value = "年份") @RequestParam(value = "year", required = false) String year,
//                                      @ApiParam(name = "month", value = "月份") @RequestParam(value = "month", required = false) String month,
//                                        @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
//                                        @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                                        @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//
//        return null;
//
//    }
//

    /************************************
     * 健康指标
     *****************************************************************/
    @ApiOperation("左侧指标列表导航")
    @RequestMapping(value = "/health/getHealthIndicatorsList", method = RequestMethod.GET)
    public String getHealthIndicatorsList(@ApiParam(name = "contentFlag", value = "查询是否有值的情况") @RequestParam(value = "contentFlag", required = false) String contentFlag,
                                          @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = false) String idNo,
                                          @ApiParam(name = "indicatorsType", value = "指标分类") @RequestParam(value = "indicatorsType", required = false) String indicatorsType) throws Exception {

        return fileUtil.file2String("/json/healthProblem.json");

    }


    /************************************
     * 共通接口
     *****************************************************************/
    @ApiOperation("疾病一览")
    @RequestMapping(value = "/common/getDiagnosisList", method = RequestMethod.GET)
    public String getDiagnosisList(
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return "跟泽华要数据";

    }
//
//    @ApiOperation("卡信息一览")
//    @RequestMapping(value = "/common/getCardTypeList", method = RequestMethod.GET)
//    public DataList getCardTypeList(@ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
//
//        return null;
//
//    }
}
