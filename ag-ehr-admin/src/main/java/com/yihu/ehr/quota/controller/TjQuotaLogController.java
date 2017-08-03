package com.yihu.ehr.quota.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.tj.TjQuotaLogModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjQuotaLog;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.tj.MTjQuotaLog;
import com.yihu.ehr.quota.service.TjQuotaLogClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.6.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "TjQuotaLog", description = "统计指标管理", tags = {"统计管理-日志查询"})
public class TjQuotaLogController extends ExtendController<TjQuotaLog> {

    @Autowired
    TjQuotaLogClient tjQuotaLogClient;
    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaLogList, method = RequestMethod.GET)
    @ApiOperation(value = "主维度列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "quotaCode", value = "过滤器，为空检索所有条件", defaultValue = "")
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
            @RequestParam(value = "page", required = false) int page){

        try {
            ResponseEntity<List<MTjQuotaLog>> responseEntity = tjQuotaLogClient.search(fields, quotaCode, startTime, endTime, sorts, size, page);
            List<MTjQuotaLog> demographicInfos = responseEntity.getBody();
            List<TjQuotaLogModel> list = new ArrayList<>();
            for (MTjQuotaLog quotaLogInfo : demographicInfos) {
                TjQuotaLogModel tjQuotaLogModel = convertToModel(quotaLogInfo, TjQuotaLogModel.class);
                if(quotaLogInfo.getStartTime() != null){
//                    Date createTime = DateUtil.parseDate(quotaLogInfo.getStartTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaLogModel.setStartTime(DateUtil.toStringLong(quotaLogInfo.getStartTime()));
                }
                if(quotaLogInfo.getEndTime() != null){
//                    Date updateTime = DateUtil.parseDate(tjQuotaLogModel.getEndTime(),"yyyy-MM-dd'T'HH:mm:ss'Z'Z");
                    tjQuotaLogModel.setEndTime( DateUtil.toStringLong(quotaLogInfo.getEndTime()));
                }
                //获取类别字典
                MConventionalDict dict = conventionalDictClient.getTjQuotaLogStatus(String.valueOf(tjQuotaLogModel.getStatus()));
                tjQuotaLogModel.setStatusName(dict == null ? "" : dict.getValue());
                list.add(tjQuotaLogModel);
            }

            return getResult(list, getTotalCount(responseEntity), page, size);
        } catch (Exception e) {
            e.printStackTrace();
            return failedSystem();
        }

    }


}
