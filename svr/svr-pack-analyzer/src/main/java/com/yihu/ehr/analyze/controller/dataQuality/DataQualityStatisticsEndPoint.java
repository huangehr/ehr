package com.yihu.ehr.analyze.controller.dataQuality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihu.ehr.analyze.service.dataQuality.DataQualityStatisticsService;
import com.yihu.ehr.analyze.service.dataQuality.WarningProblemService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author yeshijie on 2018/6/1.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DataQualityStatisticsEndPoint", description = "质控-统计", tags = {"档案分析服务-质控-统计"})
public class DataQualityStatisticsEndPoint extends EnvelopRestEndPoint {

    @Value("${quality.orgCode}")
    private String defaultQualityOrgCode;
    @Value("${quality.cloud}")
    private String defaultCloud;
    @Autowired
    private DataQualityStatisticsService dataQualityStatisticsService;
    @Autowired
    private WarningProblemService warningProblemService;


    @RequestMapping(value = ServiceApi.DataQuality.QualityMonitorProvincePlatform, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询--省平台上传")
    public Envelop provincePaltformUpload(
            @ApiParam(name = "filters", value = "查询条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = new Envelop();
        try {
            return success(dataQualityStatisticsService.findUploadStatistics(filters));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.QualityMonitoringList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询--平台接收")
    public Envelop qualityMonitoringList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end,
            @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
            @RequestParam(value = "eventType", required = false) Integer eventType) throws Exception {
        Envelop envelop = new Envelop();
        try {
            return success(dataQualityStatisticsService.dataset(start, end, eventType));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceptionList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控查询--接收情况")
    public Envelop receptionList(
            @ApiParam(name = "start", value = "开始时间")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间", defaultValue = "")
            @RequestParam(value = "end", required = false) String end) throws Exception {
        Envelop envelop = new Envelop();
        try {
            return success(dataQualityStatisticsService.inTimeAndIntegrityRate(start,end));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceivedPacketNumList, method = RequestMethod.GET)
    @ApiOperation(value = "及时/完整采集的档案包数量集合")
    public Envelop receivedPacketNumList(
            @ApiParam(name = "pageIndex", value = "第几页", required = true)
            @RequestParam(name = "pageIndex") Integer pageIndex,
            @ApiParam(name = "pageSize", value = "每页数", required = true)
            @RequestParam(name = "pageSize") Integer pageSize,
            @ApiParam(name = "type", value = "类型，1及时率，2完整率", required = false)
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
        try {
            Map<String,Object> re = dataQualityStatisticsService.receivedPacketNumList(pageIndex, pageSize, orgCode, eventDateStart, eventDateEnd, eventType);
            List<Map<String, Object>> resultList = (List<Map<String, Object>>)re.get("list");
            int count = Integer.valueOf(re.get("count").toString());
            return getPageResult(resultList, count, pageIndex, pageSize);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceivedPacketReportData, method = RequestMethod.GET)
    @ApiOperation(value = "档案包接收情况报告数据接口")
    public Envelop receivedPacketReportData(
            @ApiParam(name = "reporter", value = "报告人", required = true)
            @RequestParam(name = "reporter") String reporter,
            @ApiParam(name = "orgInfoList", value = "机构编码、名称，例：[{\"orgName\":\"xx\",\"orgCode\":\"jkzl\"}]。", required = true)
            @RequestParam(name = "orgInfoList") String orgInfoList,
            @ApiParam(name = "eventDateStart", value = "就诊时间（起始），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateStart") String eventDateStart,
            @ApiParam(name = "eventDateEnd", value = "就诊时间（截止），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateEnd") String eventDateEnd) throws Exception {
        Envelop envelop = new Envelop();
        try{
            eventDateStart = eventDateStart + " 00:00:00";
            eventDateEnd = eventDateEnd + " 23:59:59";
            Map<String, Object> resultMap = new HashMap<>();
            JSONArray jsonArray = JSON.parseArray(orgInfoList);
            List<Map<String, String>> list = new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject json = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashedMap();
                map.put("orgCode",json.getString("orgCode"));
                map.put("orgName",json.getString("orgName"));
                list.add(map);
            }
            // 接收档案包总量
            Long receivedCount = dataQualityStatisticsService.packetCount(list, null, eventDateStart, eventDateEnd);
            // 成功解析档案包总量
            Long successfulAnalysisCount = dataQualityStatisticsService.packetCount(list, "3", eventDateStart, eventDateEnd);
            // 机构档案包报告汇总
            List<Map<String, Object>> orgPackReportDataList = dataQualityStatisticsService.orgPackReportData(list, eventDateStart, eventDateEnd);

            resultMap.put("searchedDateRange", eventDateStart.replace("-", "") + "-" + eventDateEnd.replace("-", ""));
            resultMap.put("reportDate", DateTimeUtil.simpleDateFormat(new Date()));
            resultMap.put("reporter", reporter);
            resultMap.put("receivedCount", receivedCount);
            resultMap.put("successfulAnalysisCount", successfulAnalysisCount);
            resultMap.put("orgPackReportDataList", orgPackReportDataList);
            return success(resultMap);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.ReceiveDataset, method = RequestMethod.GET)
    @ApiOperation(value = "档案包接收情况报告数据接口")
    public Envelop receiveDataset(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam(name = "orgCode") String orgCode,
            @ApiParam(name = "date", value = "时间 - 精确到天yyyy-MM-dd", required = true)
            @RequestParam(name = "date") String date,
            @ApiParam(name = "page", value = "页码", required = true)
            @RequestParam(name = "page") Integer page,
            @ApiParam(name = "size", value = "页数", required = true)
            @RequestParam(name = "size") Integer size) throws Exception {
        List<Map<String, Object>> list = warningProblemService.receiveDataset(orgCode, date);
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        envelop.setTotalPage(list.size() % size > 0 ? list.size() / size + 1 : list.size() / size);
        envelop.setTotalCount(list.size());
        List result = new ArrayList();
        for (int i = (page - 1) * size; i < page * size; i ++) {
            if (i > list.size() - 1) {
                break;
            }
            result.add(list.get(i));
        }
        envelop.setDetailModelList(result);
        return envelop;
    }

}
