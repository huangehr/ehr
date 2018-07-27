package com.yihu.quota.scheduler;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.hbase.HBaseDao;
import com.yihu.ehr.profile.core.ResourceCore;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.service.scheduler.HealthArchiveSchedulerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.response.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 去重记录孕妇数据
 *
 * @author 张进军
 * @date 2018/7/17 11:42
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "去重记录孕妇数据", tags = {"solr跨数据集数据抽取--去重记录孕妇数据"})
public class PregnantWomanScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PregnantWomanScheduler.class);

    @Autowired
    private SolrUtil solrUtil;
    @Autowired
    private HBaseDao hBaseDao;
    @Autowired
    private HealthArchiveSchedulerService healthArchiveSchedulerService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    // ES 中孕妇 index
    private String P_INDEX = "pregnant";
    // ES 中孕妇（去重） type
    private String P_TYPE_WOMAN = "pregnant_woman";

    /**
     * 抽取昨天的去重记录孕妇数据
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void extractPregnantWomanJob() {
        try {
            Date currDate = new Date();
            // 抽取昨天数据
            Date yesterdayDate = DateUtils.addDays(currDate, -1);
            String yesterday = DateUtil.formatDate(yesterdayDate, DateUtil.DEFAULT_DATE_YMD_FORMAT);
            String fq = "event_date:[" + yesterday + "T00:00:00Z TO  " + yesterday + "T23:59:59Z]";

            // 整合并保存到ES
            saveTotalData(fq);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation("抽取指定时间段去重记录孕妇数据")
    @RequestMapping(value = ServiceApi.TJ.Scheduler.ExtractPregnantWoman, method = RequestMethod.GET)
    public Envelop extractPregnantWoman(
            @ApiParam(name = "startDate", value = "开始日期，格式 YYYY-MM-DD，接口里自动拼接 T00:00:00Z，", required = true)
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "截止日期，格式 YYYY-MM-DD，接口里自动拼接 T23:59:59Z", required = true)
            @RequestParam(value = "endDate") String endDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        try {
            String fq = "event_date:[" + startDate + "T00:00:00Z TO  " + endDate + "T23:59:59Z]";

            // 整合并保存到ES
            saveTotalData(fq);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    /**
     * 整合并保存到ES
     */
    private void saveTotalData(String fq) throws Exception {
        String idCardField = "EHR_000017"; // 身份证号数据元
        // 查询结果的返回字段
        String[] showFields = {"rowkey"};

        // 去重查询孕妇记录
        List<String> rowkeyList = new ArrayList();
        String q = "rowkey:*$HDSB02_05$* OR (rowkey:*$HDSD00_73$* AND EHR_000109:Z32.100)";
        int count = (int) solrUtil.count(ResourceCore.SubTable, q, fq);
        List<Group> groupList = solrUtil.queryDistinctOneField(ResourceCore.SubTable, q, fq, null, 0, count, showFields, idCardField, "event_date asc");
        SchedulerUtil.collectRowkeyFromDistinctGroup(rowkeyList, solrUtil, groupList, q, fq, idCardField, showFields);
        this.translateAndSaveData(rowkeyList);
    }

    /**
     * 转化并保存慢性病患者数据
     *
     * @param rowKeyList 档案细表 rowKey 字段值集合
     * @throws Exception
     */
    private void translateAndSaveData(List<String> rowKeyList) throws Exception {
        int currIndex = 0;
        int count = rowKeyList.size();
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, Object>> hBaseDataList = new ArrayList<>();
        List<String> pageRowKeyList = new ArrayList<>();
        while (currIndex < count) {
            // 避免一次操作太多次导致ES、HBase（厦门测试服务器）发生异常。
            pageRowKeyList.clear();
            for (int i = 0; i < 1000; i++) {
                if (currIndex < count) {
                    pageRowKeyList.add(rowKeyList.get(currIndex));
                    currIndex++;
                } else {
                    break;
                }
            }

            hBaseDataList.clear();
            dataList.clear();
            hBaseDataList = healthArchiveSchedulerService.selectHbaseData(ResourceCore.SubTable, pageRowKeyList);

            List<String> fields = new ArrayList<>();
            fields.add("id_card");
            fields.add("type");
            for (Map<String, Object> subInfo : hBaseDataList) {
                Map<String, Object> data = new HashMap<>();
                Map<String, Object> masterInfo = hBaseDao.getResultMap(ResourceCore.MasterTable, subInfo.get("profile_id").toString());

                // 身份证号
                String idCard = masterInfo.get("EHR_000017") != null ? masterInfo.get("EHR_000017").toString() : "";
                data.put("id_card", idCard);

                // 检测是否已有该患者，已有则不更新。
                String sql = "SELECT id_card FROM " + P_INDEX + "/" + P_TYPE_WOMAN + " WHERE id_card = '" + idCard;
                List<Map<String, Object>> infoList = elasticSearchUtil.findBySql(fields, sql);
                if (infoList != null || infoList.size() != 0) {
                    continue;
                }

                // 区县
                data.put("town", subInfo.get("org_area"));
                // 机构编码
                data.put("org_code", subInfo.get("org_code"));
                // 就诊日期
                data.put("event_date", subInfo.get("event_date"));
                // 就诊类型
                data.put("event_type", subInfo.get("event_type"));
                // 修改时间
                String now = DateUtil.getNowDate(DateUtil.utcDateTimePattern);
                data.put("modified_date", now);
                // 创建时间
                data.put("created_date", now);

                dataList.add(data);
            }

            // 保存到ES
            elasticSearchUtil.bulkIndex(P_INDEX, P_TYPE_WOMAN, dataList);
        }
    }
}
