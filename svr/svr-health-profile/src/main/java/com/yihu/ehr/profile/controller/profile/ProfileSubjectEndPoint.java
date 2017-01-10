package com.yihu.ehr.profile.controller.profile;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.profile.core.perspective.*;
import com.yihu.ehr.controller.BaseRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Date;

/**
 * 档案切片接口，提供档案分类数据功能。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.04.25 11:55
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "健康档案主题服务", description = "提供档案分类数据及统计数据")
public class ProfileSubjectEndPoint extends BaseRestEndPoint {
    @ApiOperation(value = "既往史", response = PastHistory.class, responseContainer = "list", notes = "患者在既往就诊中的病史，包括患者在医疗机构中登记的数据及自动分析出来的数据")
    @RequestMapping(value = ServiceApi.HealthProfile.PastHistories, method = RequestMethod.GET)
    public Collection<PastHistory> getPastHistories(
            @ApiParam("身份证号号")
            @RequestParam("demographic_id") String demographicId,
            @ApiParam("既往病史类型，all表示获取全部")
            @RequestParam(value = "type", defaultValue = "all") String type) {
        return null;
    }

    @ApiOperation(value = "主要健康问题", response = HealthIssue.class, responseContainer = "list", notes = "患者所有诊断中，疾病统计靠前的诊断，综合映射为健康问题")
    @RequestMapping(value = ServiceApi.HealthProfile.MajorIssues, method = RequestMethod.GET)
    public Collection<HealthIssue> getMajorIssues(@ApiParam("身份证号号")
                                                  @RequestParam("demographic_id") String demographicId,
                                                  @ApiParam("排名前X个的健康问题")
                                                  @RequestParam(value = "count", defaultValue = "5") int topCount) {
        return null;
    }

    @ApiOperation(value = "诊断", response = Diagnosis.class, responseContainer = "list", notes = "患者历次就诊中的诊断")
    @RequestMapping(value = ServiceApi.HealthProfile.Diagnosis, method = RequestMethod.GET)
    public Collection<Diagnosis> getDiagnosis(@ApiParam("身份证号号")
                                              @RequestParam("demographic_id") String demographicId,
                                              @ApiParam("起始时间")
                                              @RequestParam("since") Date since,
                                              @ApiParam("结束时间")
                                              @RequestParam("to") Date to) {
        return null;
    }

    @ApiOperation(value = "处方", response = Prescription.class, responseContainer = "list", notes = "与特定疾病相关的处方记录")
    @RequestMapping(value = ServiceApi.HealthProfile.Prescriptions, method = RequestMethod.GET)
    public Collection<Prescription> getPrescriptions(@ApiParam("身份证号号")
                                                     @RequestParam("demographic_id") String demographicId,
                                                     @ApiParam("疾病ID")
                                                     @RequestParam("disease_id") String diseaseId) {
        return null;
    }

    @ApiOperation(value = "药品", response = Drug.class, responseContainer = "list", notes = "与特定疾病相关的用药清单")
    @RequestMapping(value = ServiceApi.HealthProfile.Drugs, method = RequestMethod.GET)
    public Collection<Drug> getDrugs(@ApiParam("身份证号号")
                                     @RequestParam("demographic_id") String demographicId,
                                     @ApiParam("疾病ID")
                                     @RequestParam("disease_id") String diseaseId) {
        return null;
    }

    @ApiOperation(value = "检验", response = LisItem.class, responseContainer = "list", notes = "与特定疾病相关的检验记录")
    @RequestMapping(value = ServiceApi.HealthProfile.Lis, method = RequestMethod.GET)
    public Collection<LisItem> getLis(@ApiParam("身份证号号")
                                      @RequestParam("demographic_id") String demographicId,
                                      @ApiParam("疾病ID")
                                      @RequestParam("disease_id") String diseaseId) {
        return null;
    }

    @ApiOperation(value = "体格检查", response = PhysicalExam.class, responseContainer = "list", notes = "与特定疾病相关的体格检查记录")
    @RequestMapping(value = ServiceApi.HealthProfile.PhysicalExam, method = RequestMethod.GET)
    public Collection<PhysicalExam> getPhysicalExam(@ApiParam("身份证号号")
                                                    @RequestParam("demographic_id") String demographicId,
                                                    @ApiParam("疾病ID")
                                                    @RequestParam("disease_id") String diseaseId) {
        return null;
    }
}
