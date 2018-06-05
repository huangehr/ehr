package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.DataQualityStatisticsService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yeshijie on 2018/6/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DataQualityStatisticsEndPoint", description = "质控-统计", tags = {"档案分析服务-质控-统计"})
public class DataQualityStatisticsEndPoint extends EnvelopRestEndPoint {

    @Value("${quality.orgCode}")
    private String defaultQualityOrgCode;

    @Autowired
    private DataQualityStatisticsService dataQualityStatisticsService;
    @Autowired
    private DqPaltformReceiveWarningService dqPaltformReceiveWarningService;
    @Autowired
    private ElasticSearchUtil esUtil;


    @RequestMapping(value = ServiceApi.DataQuality.QualityMonitoringList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询")
    public Envelop qualityMonitoringList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end,
            @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
            @RequestParam(value = "eventType", required = false) Integer eventType) throws Exception {
        return success(dataQualityStatisticsService.dataset(start, end, eventType));
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceptionList, method = RequestMethod.GET)
    @ApiOperation(value = "接收情况")
    public Envelop receptionList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end) throws Exception {
        return success(dataQualityStatisticsService.inTimeAndIntegrityRate(start,end));
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceivedPacketNumList, method = RequestMethod.GET)
    @ApiOperation(value = "及时/完整采集的档案包数量集合")
    public Envelop ReceivedPacketNumList(
            @ApiParam(name = "type", value = "类型，1及时率，2完整率", required = true)
            @RequestParam(name = "type") String type,
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam(name = "orgCode") String orgCode,
            @ApiParam(name = "eventDateStart", value = "就诊时间（起始），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateStart") String eventDateStart,
            @ApiParam(name = "eventDateEnd", value = "就诊时间（截止），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateEnd") String eventDateEnd,
            @ApiParam(name = "eventType", value = "就诊类型，0门诊、1住院、2体检，不传则查全部就诊类型的")
            @RequestParam(name = "eventType", required = false) Integer eventType) {
        Envelop envelop = new Envelop();

        String filters = "org_code='" + orgCode + "'";
        try {
            filters += " AND event_date BETWEEN '" + eventDateStart + " 00:00:00' AND '" + eventDateEnd + " 23:59:59'";
            // 及时率场合
            if ("1".equals(type)) {
                DqPaltformReceiveWarning dqPaltformReceiveWarning = dqPaltformReceiveWarningService.findByOrgCode(orgCode);
                if (dqPaltformReceiveWarning == null) {
                    dqPaltformReceiveWarning = dqPaltformReceiveWarningService.findByOrgCode(defaultQualityOrgCode);
                }
                if (eventType == null) {
                    filters += " AND (delay <= " + dqPaltformReceiveWarning.getOutpatientInTime() +
                            " OR delay <= " + dqPaltformReceiveWarning.getHospitalInTime() +
                            " OR delay <= " + dqPaltformReceiveWarning.getPeInTime() + ")";
                } else if (eventType == 0) {
                    filters += " AND delay <= " + dqPaltformReceiveWarning.getOutpatientInTime();
                } else if (eventType == 1) {
                    filters += " AND delay <= " + dqPaltformReceiveWarning.getHospitalInTime();
                } else if (eventType == 2) {
                    filters += " AND delay <= " + dqPaltformReceiveWarning.getPeInTime();
                }
            }

            StringBuilder sql = new StringBuilder("SELECT count(event_no) packetCount FROM json_archives/info WHERE ");
            sql.append(filters);
            sql.append(" GROUP BY date_histogram(field='receive_date','interval'='1d',format='yyyy-MM-dd',alias=receiveDate)");
            List<String> fields = new ArrayList<>(2);
            fields.add("packetCount");
            fields.add("receiveDate");
            List<Map<String, Object>> resultList = esUtil.findBySql(fields, sql.toString());

            envelop.setDetailModelList(resultList);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
