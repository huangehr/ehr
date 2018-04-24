//package com.yihu.ehr.basic.logs.controller;
//
//import com.yihu.ehr.basic.logs.es.ElasticFactory;
//import com.yihu.ehr.basic.logs.es.ElasticsearchUtil;
//import com.yihu.ehr.basic.logs.model.CloudBusinessLog;
//import com.yihu.ehr.basic.logs.model.CloudOperatorLog;
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.constants.SessionAttributeKeys;
//import com.yihu.ehr.controller.EnvelopRestEndPoint;
//import com.yihu.ehr.model.common.ListResult;
//import com.yihu.ehr.util.datetime.DateUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.commons.lang.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by chenweida on 2018/1/9
// */
//@RestController
//@RequestMapping(ApiVersion.Version1_0)
//@SessionAttributes(SessionAttributeKeys.CurrentUser)
//@Api(value = "log", description = "日志管理", tags = {"日志管理 - 日志信息获取"})
//public class LogsEndPointEs extends EnvelopRestEndPoint {
//    @Autowired
//    private ElasticsearchUtil elasticsearchUtil;
//
//    public static String mongoDb_Business_TableName = "cloud_business_log";
//    public static String mongoDb_Operator_TableName = "cloud_operator_log";
//
//
//    @RequestMapping(value = "/log/getBussinessLogs", method = RequestMethod.GET)
//    @ApiOperation(value = "根据传入的参数进行ES日志列表的查询", response = CloudBusinessLog.class, responseContainer = "List")
//    public ListResult getBusinessLogs(
//            @ApiParam(name = "data", value = "数据", defaultValue = "")
//            @RequestParam(value = "data", required = false) String data,
//            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
//            @RequestParam(value = "endDate", required = false) String endDate,
//            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
//            @RequestParam(value = "caller", required = false) String caller,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
//            @RequestParam(value = "sorts", required = false) String sorts,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(value = "size", required = false) int size,
//            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
//            @RequestParam(value = "page", required = false) int page) throws Exception {
//
//        int begin = (page - 1) * size + 1;
//        StringBuffer sql = new StringBuffer("select * from "+mongoDb_Business_TableName+" where 1=1 ");
//        StringBuffer sqlCount = new StringBuffer("select count(*) num from "+mongoDb_Business_TableName+" where 1=1 ");
//        if (!org.springframework.util.StringUtils.isEmpty(data)) {
//            String dataSql = " and response like '%" + data + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(startDate)) {
//            Date startDateObj = DateUtil.strToDate(startDate);
//            startDate = DateUtil.toString(startDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time >= '" + startDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
//            Date endDateObj = DateUtil.strToDate(endDate);
//            endDate = DateUtil.toString(endDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time <= '" + endDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(caller)) {
//            String dataSql = " and caller like '%" + caller + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        sql.append(" limit " + begin + "," + size);
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql.toString());
//
//        long totalCount = elasticsearchUtil.excuteCount(sqlCount.toString());
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setTotalCount((int) totalCount);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//            listResult.setCurrPage(page);
//            listResult.setPageSize(size);
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//    @RequestMapping(value = "/log/getOperatorLogs", method = RequestMethod.GET)
//    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询", response = CloudOperatorLog.class, responseContainer = "List")
//    public ListResult getOperatorLogs(
//            @ApiParam(name = "data", value = "数据", defaultValue = "")
//            @RequestParam(value = "data", required = false) String data,
//            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
//            @RequestParam(value = "endDate", required = false) String endDate,
//            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
//            @RequestParam(value = "caller", required = false) String caller,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
//            @RequestParam(value = "sorts", required = false) String sorts,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(value = "size", required = false) int size,
//            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
//            @RequestParam(value = "page", required = false) int page) throws Exception {
//
//        int begin = (page - 1) * size + 1;
//        StringBuffer sql = new StringBuffer("select * from "+mongoDb_Operator_TableName+" where 1=1 ");
//        StringBuffer sqlCount = new StringBuffer("select count(*) num from "+mongoDb_Operator_TableName+" where 1=1 ");
//        if (!org.springframework.util.StringUtils.isEmpty(data)) {
//            String dataSql = " and response like '%" + data + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(startDate)) {
//            Date startDateObj = DateUtil.strToDate(startDate);
//            startDate = DateUtil.toString(startDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time >= '" + startDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
//            Date endDateObj = DateUtil.strToDate(endDate);
//            endDate = DateUtil.toString(endDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time <= '" + endDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(caller)) {
//            String dataSql = " and caller like '%" + caller + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        sql.append(" limit " + begin + "," + size);
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql.toString());
//
//        long totalCount = elasticsearchUtil.excuteCount(sqlCount.toString());
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setTotalCount((int) totalCount);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//            listResult.setCurrPage(page);
//            listResult.setPageSize(size);
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//    @RequestMapping(value = "/getBussinessLogById/{logId}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据id进行MONGODB日志的查询", response = CloudBusinessLog.class, responseContainer = "List")
//    public ListResult getBussinessLogById(
//            @ApiParam(name = "logId", value = "logId", defaultValue = "")
//            @RequestParam(value = "logId", required = false) String logId) throws Exception {
//        String sql = "select * from "+mongoDb_Business_TableName+" where _id='" + logId + "'";
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql);
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//    @RequestMapping(value = "/getOperatorLogById/{logId}", method = RequestMethod.GET)
//    @ApiOperation(value = "根据id进行MONGODB日志的查询", response = CloudOperatorLog.class, responseContainer = "List")
//    public ListResult getOperatorLogById(
//            @ApiParam(name = "logId", value = "logId", defaultValue = "")
//            @RequestParam(value = "logId", required = false) String logId) throws Exception {
//
//        String sql = "select * from "+mongoDb_Operator_TableName+" where _id='" + logId + "'";
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql);
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//    @RequestMapping(value = "/log/getBussinessListLogs", method = RequestMethod.GET)
//    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询,姓名模糊查询", response = CloudBusinessLog.class, responseContainer = "List")
//    public ListResult getBusinessListLogs(
//            @ApiParam(name = "patient", value = "姓名", defaultValue = "")
//            @RequestParam(value = "patient", required = false) String patient,
//            @ApiParam(name = "data", value = "数据", defaultValue = "")
//            @RequestParam(value = "data", required = false) String data,
//            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
//            @RequestParam(value = "endDate", required = false) String endDate,
//            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
//            @RequestParam(value = "caller", required = false) String caller,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
//            @RequestParam(value = "sorts", required = false) String sorts,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(value = "size", required = false) int size,
//            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
//            @RequestParam(value = "page", required = false) int page) throws Exception {
//
//        int begin = (page - 1) * size + 1;
//        StringBuffer sql = new StringBuffer("select * from "+mongoDb_Business_TableName+" where 1=1 ");
//        StringBuffer sqlCount = new StringBuffer("select count(*) num from "+mongoDb_Business_TableName+" where 1=1 ");
//        if (!org.springframework.util.StringUtils.isEmpty(data)) {
//            String dataSql = " and response like '%" + data + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(patient)) {
//            String dataSql = " and patient like '" + patient + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(startDate)) {
//            Date startDateObj = DateUtil.strToDate(startDate);
//            startDate = DateUtil.toString(startDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time >= '" + startDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
//            Date endDateObj = DateUtil.strToDate(endDate);
//            endDate = DateUtil.toString(endDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time <= '" + endDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(caller)) {
//            String dataSql = " and caller like '%" + caller + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        sql.append(" limit " + begin + "," + size);
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql.toString());
//
//        long totalCount = elasticsearchUtil.excuteCount(sqlCount.toString());
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setTotalCount((int) totalCount);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//            listResult.setCurrPage(page);
//            listResult.setPageSize(size);
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//    @RequestMapping(value = "/log/getOperatorListLogs", method = RequestMethod.GET)
//    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询,姓名模糊查询", response = CloudOperatorLog.class, responseContainer = "List")
//    public ListResult getOperatorListLogs(
//            @ApiParam(name = "patient", value = "姓名", defaultValue = "")
//            @RequestParam(value = "patient", required = false) String patient,
//            @ApiParam(name = "data", value = "数据", defaultValue = "")
//            @RequestParam(value = "data", required = false) String data,
//            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
//            @RequestParam(value = "startDate", required = false) String startDate,
//            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
//            @RequestParam(value = "endDate", required = false) String endDate,
//            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
//            @RequestParam(value = "caller", required = false) String caller,
//            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
//            @RequestParam(value = "sorts", required = false) String sorts,
//            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
//            @RequestParam(value = "size", required = false) int size,
//            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
//            @RequestParam(value = "page", required = false) int page) throws Exception {
//        int begin = (page - 1) * size + 1;
//        StringBuffer sql = new StringBuffer("select * from "+mongoDb_Operator_TableName+" where 1=1 ");
//        StringBuffer sqlCount = new StringBuffer("select count(*) num from "+mongoDb_Operator_TableName+" where 1=1 ");
//        if (!org.springframework.util.StringUtils.isEmpty(data)) {
//            String dataSql = " and response like '%" + data + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(patient)) {
//            String dataSql = " and patient like '" + patient + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(startDate)) {
//            Date startDateObj = DateUtil.strToDate(startDate);
//            startDate = DateUtil.toString(startDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time >= '" + startDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(endDate)) {
//            Date endDateObj = DateUtil.strToDate(endDate);
//            endDate = DateUtil.toString(endDateObj, DateUtil.utcDateTimePatternTZ);
//            String dataSql = " and time <= '" + endDate + "'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        if (!org.springframework.util.StringUtils.isEmpty(caller)) {
//            String dataSql = " and caller like '%" + caller + "%'";
//            sql.append(dataSql);
//            sqlCount.append(dataSql);
//        }
//        sql.append(" limit " + begin + "," + size);
//        List<Map<String, Object>> logsModelList = elasticsearchUtil.excuteDataModel(sql.toString());
//
//        long totalCount = elasticsearchUtil.excuteCount(sqlCount.toString());
//        ListResult listResult = new ListResult();
//        if (logsModelList.size() > 0) {
//            listResult.setDetailModelList(logsModelList);
//            listResult.setSuccessFlg(true);
//            listResult.setTotalCount((int) totalCount);
//            listResult.setCode(200);
//            listResult.setMessage("日志查询成功！");
//            listResult.setCurrPage(page);
//            listResult.setPageSize(size);
//        } else {
//            listResult.setCode(200);
//            listResult.setMessage("查询无数据");
//            listResult.setTotalCount(0);
//        }
//        return listResult;
//    }
//
//
//}
