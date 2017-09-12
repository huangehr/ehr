package com.yihu.ehr.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.*;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MQuotaConfigModel;
import com.yihu.ehr.model.tj.MTjQuotaDataSaveModel;
import com.yihu.ehr.model.tj.MTjQuotaDataSourceModel;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.quota.service.*;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ResourceStatistics", description = "资源中心首页报表", tags = {"资源中心首页报表-入口"})
public class ResourceStatisticsEndPoint extends EnvelopRestEndPoint {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案-健康卡绑定量")
    public ListResult getTjDataSaveList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters
    ) throws Exception {

        ListResult listResult = new ListResult();

        return listResult;
    }


}
