package com.yihu.ehr.resourcesbrowse;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.util.EhrFileUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    EhrFileUtils EhrFileUtils = new EhrFileUtils();

    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public String getPatientInfo(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {

        return EhrFileUtils.file2String("/json/user.json");
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
    @ApiOperation("门户 - 病史信息")
    @RequestMapping(value = "/home/getDiseaseHistory", method = RequestMethod.GET)
    public String getDiseaseHistory(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return EhrFileUtils.file2String("/json/pastHistory.json");

    }

    //
    @ApiOperation("门户 - 主要健康问题")
    @RequestMapping(value = "/home/getHealthProblem", method = RequestMethod.GET)
    public String getHealthProblem(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId) throws Exception {
        EhrFileUtils EhrFileUtils = new EhrFileUtils();
        return EhrFileUtils.file2String("/json/healthProblem.json");
    }
//


    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public String getMedicalEvents(
            @ApiParam(name = "demographicId", value = "身份证号")
            @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "medicalEventsType", value = "就诊事件类别")
            @RequestParam(value = "medicalEventsType", required = false) String medicalEventsType,
            @ApiParam(name = "year", value = "年份", defaultValue = "当前年份")
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @ApiParam(name = "area", value = "地区")
            @RequestParam(value = "area", required = false) String area,
            @ApiParam(name = "hpId", value = "健康问题id")
            @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "diagnosisCode", value = "疾病代码（逗号分隔）")
            @RequestParam(value = "diagnosisCode", required = false) String diagnosisCode,
            @ApiParam(name = "page", value = "当前页")
            @RequestParam(value = "page", required = false,defaultValue = "1") int page,
            @ApiParam(name = "size", value = "行数")
            @RequestParam(value = "size", required = false, defaultValue = "5") int size) throws Exception {

        return EhrFileUtils.file2String("/json/MedicalEventInfo.json");

    }

    @ApiOperation("门户 - 相关健康指标")
    @RequestMapping(value = "/home/getHealthIndicators", method = RequestMethod.GET)
    public String getHealthIndicators(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "medicalIndexId", value = "指标项ID（逗号分隔）") @RequestParam(value = "medicalIndexId", required = false) String medicalIndexId,
            @ApiParam(name = "startTime", value = "查询开始时间") @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "查询结束时间") @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return EhrFileUtils.file2String("/json/HealthIndicators.json");
    }


    /************************************
     * 就诊事件详情
     *****************************************************************/
//    @ApiOperation("左侧档案列表导航")
//    @RequestMapping(value = "/cda/getPatientCdaList", method = RequestMethod.GET)
//    public String getPatientCdaList(
//            @ApiParam(name = "rowKey", value = "档案ID") @RequestParam(value = "rowKey", required = true) String rowKey,
//            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
//            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
//            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {
//
//        return EhrFileUtils.file2String("/json/MedicalEventInfo.json");
//
//    }
    @ApiOperation("右侧档案模版展示")
    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    public String getPatientCdaInfo(
            @ApiParam(name = "profileId", value = "档案ID") @RequestParam(value = "profileId", required = true) String profileId) throws Exception {

        return "跟泽华要数据";
    }

    /************************************
     * 疾病事件
     *****************************************************************/
    @ApiOperation("疾病事件 - 历史用药 - 药品清单")
    @RequestMapping(value = "/disease/getDrugList", method = RequestMethod.GET)
    public String getDrugList(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "diagnosisCode", value = "疾病代码（逗号分隔）") @RequestParam(value = "diagnosisCode", required = false) String diagnosisCode,
            @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
            @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception {

        return EhrFileUtils.file2String("/json/DrugList.json");

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
    @RequestMapping(value = "/health/getHealthIndicatorItems", method = RequestMethod.GET)
    public String getHealthIndicatorItems(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = false) String demographicId,
            @ApiParam(name = "hpId", value = "指标项ID（逗号分隔）") @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return EhrFileUtils.file2String("/json/indicatorItems.json");

    }


    /************************************
     * 机构列表
     *****************************************************************/
    @ApiOperation("机构列表")
    @RequestMapping(value = "/common/getDiagnosisList", method = RequestMethod.GET)
    public String getDiagnosisList(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = false) String demographicId) throws Exception {

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


    /************************************
     * 就诊过的城市
     *****************************************************************/
    @ApiOperation("就诊过的城市")
    @RequestMapping(value = "/medic/getOrganizations", method = RequestMethod.GET)
    public String getEventOrganizations(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "beginTime", value = "查询开始时间") @RequestParam(value = "beginTime", required = false) String beginTime,
            @ApiParam(name = "endTime", value = "查询结束时间") @RequestParam(value = "endTime", required = false) String endTime) throws Exception {

        return EhrFileUtils.file2String("/json/MedicalOrganizations.json");


    }


    @ApiOperation("档案搜索接口")
    @RequestMapping(value = "/medic/getArchives", method = RequestMethod.GET)
    public String getArchives(
            @ApiParam(name = "demographicId", value = "身份证号") @RequestParam(value = "demographicId", required = true) String demographicId,
            @ApiParam(name = "archiveType", value = "就诊类型(传空默认搜索全部数据)") @RequestParam(value = "archiveType", required = false) String archiveType,
            @ApiParam(name = "name", value = "诊断记录名称") @RequestParam(value = "name", required = false) String name) throws Exception {
        return EhrFileUtils.file2String("/json/archiveSearch.json");

    }


}
