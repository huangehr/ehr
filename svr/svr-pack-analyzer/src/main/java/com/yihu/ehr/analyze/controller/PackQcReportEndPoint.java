package com.yihu.ehr.analyze.controller;

import com.yihu.ehr.analyze.service.dataQuality.DqDatasetWarningService;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhengwei
 * @Date: 2018/5/31 16:20
 * @Description: 质控报表
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PackQcReportEndPoint", description = "新质控管理报表", tags = {"新质控管理报表"})
public class PackQcReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private PackQcReportService packQcReportService;
    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private ElasticSearchUtil esUtil;

    @RequestMapping(value = ServiceApi.PackQcReport.dailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取医院数据")
    public Envelop dailyReport(
            @ApiParam(name = "startDate", value = "开始日期")
            @RequestParam(name = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "结束日期")
            @RequestParam(name = "endDate") String endDate,
            @ApiParam(name = "orgCode", value = "医院代码")
            @RequestParam(name = "orgCode", required = false) String orgCode) throws Exception {
        return packQcReportService.dailyReport(startDate, endDate, orgCode);
    }

    @RequestMapping(value = ServiceApi.PackQcReport.datasetWarningList, method = RequestMethod.GET)
    @ApiOperation(value = "预警数据集列表")
    public Envelop datasetWarningList(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam(name = "orgCode") String orgCode,
            @ApiParam(name = "type", value = "类型(1平台接收，2平台上传)")
            @RequestParam(name = "type", required = false) String type,
            @ApiParam(name = "pageIndex", value = "第几页", required = true)
            @RequestParam(name = "pageIndex") Integer pageIndex,
            @ApiParam(name = "pageSize", value = "每页数", required = true)
            @RequestParam(name = "pageSize") Integer pageSize) {
        Envelop envelop = new Envelop();
        try {
            String filters = "orgCode=:" + orgCode;
            if (!StringUtils.isEmpty(type)) {
                filters += ",type=:" + type;
            }
            String fields = "datasetCode,datasetName";
            List<DqDatasetWarning> redisMqChannelList = dqDatasetWarningService.search(fields, filters, "", pageIndex, pageSize);
            int count = (int) dqDatasetWarningService.getCount(filters);
            List<DqDatasetWarning> list = (List<DqDatasetWarning>) convertToModels(redisMqChannelList, new ArrayList<DqDatasetWarning>(), DqDatasetWarning.class, fields);
            envelop = getPageResult(list, count, pageIndex, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.PackQcReport.resourceSuccessfulCount, method = RequestMethod.GET)
    @ApiOperation(value = "资源化成功的计数统计")
    public Envelop resourceSuccessfulCount(
            @ApiParam(name = "orgCode", value = "机构编码", required = true)
            @RequestParam(name = "orgCode") String orgCode,
            @ApiParam(name = "receiveDate", value = "接收时间")
            @RequestParam(name = "receiveDate", required = false) String receiveDate) {
        Envelop envelop = new Envelop();

        String esIndex = "json_archives";
        String esType = "info ";
        String fq = "org_code:" + orgCode;
        if (!StringUtils.isEmpty(receiveDate)) {
            fq += "receive_date:" + receiveDate;
        }

        try {
            // 门诊
            String fqMz = fq + "event_type:0";
            Long mzCount = esUtil.count(esIndex, esType, fqMz);
            // 住院
            String fqZy = fq + "event_type:1";
            Long zyCount = esUtil.count(esIndex, esType, fqZy);
            // 体检
            String fqTj = fq + "event_type:2";
            Long tjCount = esUtil.count(esIndex, esType, fqTj);
            Map<String, Long> resultMap = new HashMap<>();
            resultMap.put("mzCount", mzCount);
            resultMap.put("zyCount", zyCount);
            resultMap.put("tjCount", tjCount);

            envelop.setObj(resultMap);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
