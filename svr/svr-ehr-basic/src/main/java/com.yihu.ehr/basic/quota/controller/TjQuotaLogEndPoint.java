package com.yihu.ehr.basic.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.quota.service.TjQuotaLogService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.TjQuotaLog;
import com.yihu.ehr.model.tj.MTjQuotaLog;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjQuotaLogEndPoint", description = "统计日志", tags = {"统计日志查询"})
public class TjQuotaLogEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    TjQuotaLogService tjQuotaLogService;

    @Autowired
    JdbcTemplate jdbcTemplate;
    @PersistenceContext
    protected EntityManager entityManager;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaLogList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询从纬度")
    public List<MTjQuotaLog> getTjQuotaLogList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "quotaCode", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "quotaCode", required = false) String quotaCode,
            @ApiParam(name = "startTime", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("quotaCode", quotaCode);
        conditionMap.put("page", page);
        conditionMap.put("pageSize", size);

        Date startDate = DateTimeUtil.simpleDateTimeParse(startTime);
        Date endDate = DateTimeUtil.simpleDateTimeParse(endTime);
        if(null!=endDate){
            Calendar calendar   =   new GregorianCalendar();
            calendar.setTime(endDate);
            calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动
            endDate=calendar.getTime();   //日期往后推一天
        }
        conditionMap.put("startDate", startDate);
        conditionMap.put("endDate", endDate);
        List<TjQuotaLog> tjQuotaLogs = tjQuotaLogService.searchQuotaLogByParams(conditionMap);
        Long totalCount =Long.parseLong(tjQuotaLogService.searchQuotaLogByParamsTotalCount(conditionMap).toString());
        pagedResponse(request, response, totalCount, page, size);
        return (List<MTjQuotaLog>)convertToModels(tjQuotaLogs,new ArrayList<MTjQuotaLog>(tjQuotaLogs.size()), MTjQuotaLog.class, null);
    }


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaLogRecentRecord, method = RequestMethod.GET)
    @ApiOperation(value = "获取最近日志列表")
    MTjQuotaLog getRecentRecord(
            @ApiParam(name = "quotaCode", value = "指标code", defaultValue = "")
            @RequestParam(value = "quotaCode", required = false) String quotaCode,
            @ApiParam(name = "endTime", value = "完成时间", defaultValue = "")
            @RequestParam(value = "endTime", required = false) String endTime
    ){
        MTjQuotaLog mTjQuotaLog = null;
        List<TjQuotaLog> tjQuotaLogs =  tjQuotaLogService.getRecentRecord(quotaCode,endTime);
        if(tjQuotaLogs != null && tjQuotaLogs.size() > 0){
            mTjQuotaLog = objectMapper.convertValue(tjQuotaLogs.get(0),MTjQuotaLog.class);
        }
        return mTjQuotaLog;
    }

}
