package com.yihu.quota.scheduler;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.scheduler.HealthArchiveSchedulerService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.HealthArchiveInfoModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wxw on 2018/3/12
 * 统计solr主表档案人数（去重）.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "统计solr主表档案人数（去重）", tags = {"统计solr主表档案人数（根据省份证或就诊卡去重）"})
public class HealthArchiveScheduler {
    private static final Logger log = LoggerFactory.getLogger(HealthArchiveScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;

    /**
     * 定时任务抽取数据，每天统计前一天的
     */
    @Scheduled(cron = "0 30 7 * * ?")
    public void validatorIdentityScheduler() {
        try {
            Date now = new Date();
            Date yesterdayDate = DateUtils.addDays(now,-1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";
            log.info("抽取时间：fq = {}", fq);
            // 抽取并保存到ES
            handleData(fq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段档案去重")
    @RequestMapping(value = "/extractHealthArchive", method = RequestMethod.GET)
    public Envelop extractHealthArchive(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";
            log.info("抽取时间：fq = {}", fq);
            // 抽取并保存到ES
            handleData(fq);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    private void handleData(String fq) throws Exception {
        String q =  ""; // 查询条件
        String keyEventDate = "event_date";
        String keyEventType = "event_type"; // 就诊类型
        String keyArea = "org_area";  // 行政区划代码  EHR_001225
        String keyDemographicId = "demographic_id"; // 身份证
        String keyCardId = "card_id";
        String keyHealthProblem = "health_problem"; // 健康问题
        String keyPatientName = "patient_name"; // 患者姓名
        String keySex = "EHR_000019";   // 性别
        String keySexValue = "EHR_000019_VALUE";
        String keyAge = "EHR_000007";   // 出生日期
        String keyOrgCode = "org_code"; // 机构编码
        String keyOrgName = "org_name";
        String keyAddress = "EHR_001211"; //地址 EHR_001227

        List<HealthArchiveInfoModel> healthArchiveInfoModelList = new ArrayList<>();
        // 找出就诊档案数
        long count = solrUtil.count(ResourceCore.MasterTable, q, fq);
        List<String> rowKeyList = healthArchiveSchedulerService.selectSubRowKey(ResourceCore.MasterTable, q, fq, count);
        if(rowKeyList != null && rowKeyList.size() > 0) {
            List<Map<String,Object>> hbaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.MasterTable, rowKeyList);
            if( hbaseDataList != null && hbaseDataList.size() > 0 ) {
                for(Map<String,Object> map : hbaseDataList) {
                    // 档案信息 > 姓名等
                    HealthArchiveInfoModel healthArchiveInfoModel = new HealthArchiveInfoModel();
                    healthArchiveInfoModel.setCreateTime(new Date());
                    if(map.get(keyDemographicId) != null){
                        healthArchiveInfoModel.setDemographicId(map.get(keyDemographicId).toString());
                    }
                    if(map.get(keyCardId) != null){
                        healthArchiveInfoModel.setCardId(map.get(keyCardId).toString());
                    }
                    if(map.get(keyEventDate) != null){
                        healthArchiveInfoModel.setEventDate(DateUtil.formatCharDate(map.get(keyEventDate).toString(), DateUtil.DATE_WORLD_FORMAT));					}
                    if(map.get(keyArea) != null){
                        healthArchiveInfoModel.setTown(map.get(keyArea).toString());
                    }
                    if(map.get(keyPatientName) != null){
                        healthArchiveInfoModel.setName(map.get(keyPatientName).toString());
                    }
                    if(map.get(keySex) != null){
                        healthArchiveInfoModel.setSex(Integer.valueOf(map.get(keySex).toString()));
                        healthArchiveInfoModel.setSexName(map.get(keySexValue).toString());
                    }
                    if(map.get(keyAge) != null){
                        String birthday = map.get(keyAge).toString().substring(0, 10);
                        // 计算年龄
                        int age = healthArchiveSchedulerService.getAgeByBirth(birthday);
                        healthArchiveInfoModel.setAgeCode(healthArchiveSchedulerService.exchangeCodeByAge(age));
                        healthArchiveInfoModel.setAgeName(healthArchiveSchedulerService.exchangeNameByAge(age));
                    }
                    if (map.get(keyEventType) != null) {
                        healthArchiveInfoModel.setEventType(map.get(keyEventType).toString());
                    }
                    if (map.get(keyAddress) != null) {
                        healthArchiveInfoModel.setAddress(map.get(keyAddress).toString());
                    }
                    if (map.get(keyHealthProblem) != null) {
                        healthArchiveInfoModel.setHealthProblem(map.get(keyHealthProblem).toString());
                    }
                    if (map.get(keyOrgCode) != null) {
                        healthArchiveInfoModel.setOrgCode(map.get(keyOrgCode).toString());
                    }
                    if (map.get(keyOrgName) != null) {
                        healthArchiveInfoModel.setOrgName(map.get(keyOrgName).toString());
                    }
                    healthArchiveInfoModel.setResult(1);
                    healthArchiveInfoModelList.add(healthArchiveInfoModel);
                }
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                // 保存到ES库
                String index = "health_archive_index";
                String type = "archive_info";
                for(HealthArchiveInfoModel archiveInfo : healthArchiveInfoModelList){
                    healthArchiveSchedulerService.saveHealthArchiveInfo(index, type, archiveInfo);
                }
            }
        }
    }
}
