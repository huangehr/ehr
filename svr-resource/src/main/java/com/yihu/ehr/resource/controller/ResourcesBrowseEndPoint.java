package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.query.common.model.DataList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/browse")
@Api(value = "rsBrowse", description = "业务资源浏览接口")
public class ResourcesBrowseEndPoint {

    /********************************* 门户接口 ************************************************************/
    @ApiOperation("门户 - 用户基本信息")
    @RequestMapping(value = "/home/getPatientInfo", method = RequestMethod.GET)
    public String getPatientInfo(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo) throws Exception{
         return null;
    }

    @ApiOperation("门户 - 挂号信息展开")
    @RequestMapping(value = "/home/getRegistrationInfo", method = RequestMethod.GET)
    public DataList getRegistrationInfo(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                      @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                      @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
         return null;
    }

    @ApiOperation("门户 - 在线问诊展开")
    @RequestMapping(value = "/home/getOnlineVisit", method = RequestMethod.GET)
    public DataList getOnlineVisit(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                 @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                 @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{
         return null;
    }

    @ApiOperation("门户 - 病史信息")
    @RequestMapping(value = "/home/getDiseaseHistory", method = RequestMethod.GET)
    public DataList getDiseaseHistory(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                    @ApiParam(name = "searchFlag", value = "是否存在数据过滤", defaultValue = "0") @RequestParam(value = "searchFlag", required = false,defaultValue="0") String searchFlag,
                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

            return null;

    }

    @ApiOperation("门户 - 主要健康问题")
    @RequestMapping(value = "/home/getHealthProblem", method = RequestMethod.GET)
    public DataList getHealthProblem(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                   @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = true) String healthProblemCode,
                                   @ApiParam(name = "drugsFlag", value = "常用药查询标识") @RequestParam(value = "drugsFlag", required = true) String drugsFlag,
                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

        return null;

    }



    @ApiOperation("门户 - 最近就诊事件")
    @RequestMapping(value = "/home/getMedicalEvents", method = RequestMethod.GET)
    public DataList getMedicalEvents(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                   @ApiParam(name = "medicalEventsType", value = "就诊事件类别") @RequestParam(value = "medicalEventsType", required = true) String medicalEventsType,
                                   @ApiParam(name = "year", value = "年份", defaultValue = "当前年份") @RequestParam(value = "year", required = false) String year,

                                   @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            Date date = new Date();
            year = sdf.format(date);

            return null;

    }

    @ApiOperation("门户 - 相关健康指标")
    @RequestMapping(value = "/home/getHealthIndicators", method = RequestMethod.GET)
    public DataList getHealthIndicators(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                   @ApiParam(name = "medicalIndexId", value = "指标项ID") @RequestParam(value = "medicalIndexId", required = false) String medicalIndexId,
                                   @ApiParam(name = "startTime", value = "查询开始时间") @RequestParam(value = "startTime", required = false) String startTime,
                                   @ApiParam(name = "endTime", value = "查询结束时间") @RequestParam(value = "endTime", required = false) String endTime,
                                   @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
                                   @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{


            return null;
    }


    /************************************ 就诊事件详情 *****************************************************************/
    @ApiOperation("左侧档案列表导航")
    @RequestMapping(value = "/cda/getPatientCdaList", method = RequestMethod.GET)
    public DataList getPatientCdaList(@ApiParam(name = "rowKey", value = "档案ID") @RequestParam(value = "rowKey", required = true) String rowKey,
                                    @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                    @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                    @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

            return null;

    }

    @ApiOperation("右侧档案模版展示")
    @RequestMapping(value = "/cda/getPatientCdaInfo", method = RequestMethod.GET)
    public DataList getPatientCdaInfo(@ApiParam(name = "rowKey", value = "档案ID") @RequestParam(value = "rowKey", required = true) String rowKey,
                                    @ApiParam(name = "cdaId", value = "CDA编号") @RequestParam(value = "cdaId", required = true) String cdaId,
                                    @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo) throws Exception{

            return null;
    }

    /************************************ 疾病事件 *****************************************************************/
    @ApiOperation("疾病事件 - 历史用药 - 药品清单")
    @RequestMapping(value = "/disease/getDrugList", method = RequestMethod.GET)
    public DataList getDrugList(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                              @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
                              @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                              @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

            return null;

    }

    @ApiOperation("疾病事件 - 历史用药 - 处方记录")
    @RequestMapping(value = "/disease/getPrescriptionRecord", method = RequestMethod.GET)
    public DataList getPrescriptionRecord(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                              @ApiParam(name = "queryType", value = "查询内容代码") @RequestParam(value = "queryType", required = true) Integer queryType,
                              @ApiParam(name = "year", value = "年份") @RequestParam(value = "year", required = false) String year,
                              @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
                              @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                              @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

            return null;

    }

    @ApiOperation("疾病事件 - 历史用药 - 用药记录")
    @RequestMapping(value = "/disease/getUsingDrugRecords", method = RequestMethod.GET)
    public DataList getUsingDrugRecords(@ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = true) String idNo,
                                      @ApiParam(name = "year", value = "年份") @RequestParam(value = "year", required = false) String year,
                                      @ApiParam(name = "month", value = "月份") @RequestParam(value = "month", required = false) String month,
                                        @ApiParam(name = "healthProblemCode", value = "健康问题代码") @RequestParam(value = "healthProblemCode", required = false) String healthProblemCode,
                                        @ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                        @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

        return null;

    }


    /************************************ 健康指标 *****************************************************************/
    @ApiOperation("左侧指标列表导航")
    @RequestMapping(value = "/health/getHealthIndicatorsList", method = RequestMethod.GET)
    public String getHealthIndicatorsList(@ApiParam(name = "contentFlag", value = "查询是否有值的情况") @RequestParam(value = "contentFlag", required = false) String contentFlag,
                                          @ApiParam(name = "idNo", value = "身份证号码") @RequestParam(value = "idNo", required = false) String idNo,
                                          @ApiParam(name = "indicatorsType", value = "指标分类") @RequestParam(value = "indicatorsType", required = false) String indicatorsType) throws Exception{

        return null;

    }


    /************************************ 共通接口 *****************************************************************/
    @ApiOperation("疾病一览")
    @RequestMapping(value = "/common/getDiagnosisList", method = RequestMethod.GET)
    public DataList getDiagnosisList(@ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

        return null;

    }

    @ApiOperation("卡信息一览")
    @RequestMapping(value = "/common/getCardTypeList", method = RequestMethod.GET)
    public DataList getCardTypeList(@ApiParam("page") @RequestParam(value = "page", required = false) Integer page,
                                   @ApiParam("size") @RequestParam(value = "size", required = false) Integer size) throws Exception{

        return null;

    }
}
