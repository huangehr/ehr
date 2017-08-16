package com.yihu.ehr.logs.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.logs.model.CloudBusinessLog;
import com.yihu.ehr.logs.model.CloudOperatorLog;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llh on 2017/5/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
@Api(value = "log", description = "日志管理", tags = {"日志管理 - 日志信息获取"})
public class LogsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/log/getBussinessLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询", response = CloudBusinessLog.class, responseContainer = "List")
    public ListResult getBusinessLogs(
            @ApiParam(name = "data", value = "数据", defaultValue = "")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        int begin = (page - 1) * size + 1 ;
        int end = page * size;
        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(data)){
            Criteria crData = new Criteria("data").regex(".*?" + data + ".*"); //模糊查询
            criteriaList.add(crData);
        }
        if(StringUtils.isNotEmpty(caller)){
            Criteria crCaller = new Criteria().where("caller").is(caller);
            criteriaList.add(crCaller);
        }
        if(StringUtils.isNotEmpty(startDate)){
            Criteria crStarTime = new Criteria().where("time").gte(startDate);// 大于等于开始日期
            criteriaList.add(crStarTime);
        }
        if(StringUtils.isNotEmpty(endDate)){
            Criteria  crEndTime = new Criteria().where("time").lte(endDate);// 小于结束日期 + 1
            criteriaList.add(crEndTime);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        query.limit(end - begin).skip(begin);//分页数据
        List<CloudBusinessLog> logsModelList =  mongoTemplate.find(query, CloudBusinessLog.class);

        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }


    @RequestMapping(value = "/log/getOperatorLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询", response = CloudOperatorLog.class, responseContainer = "List")
    public ListResult getOperatorLogs(
            @ApiParam(name = "data", value = "数据", defaultValue = "")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        int begin = (page - 1) * size + 1 ;
        int end = page * size;
        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(data)){
            Criteria crData = new Criteria("data").regex(".*?" + data + ".*"); //模糊查询
            criteriaList.add(crData);
        }
        if(StringUtils.isNotEmpty(caller)){
            Criteria crCaller = new Criteria().where("caller").is(caller);
            criteriaList.add(crCaller);
        }
        if(StringUtils.isNotEmpty(startDate)){
            Criteria crStarTime = new Criteria().where("time").gte(startDate);// 大于等于开始日期
            criteriaList.add(crStarTime);
        }
        if(StringUtils.isNotEmpty(endDate)){
            Criteria  crEndTime = new Criteria().where("time").lte(endDate);// 小于结束日期 + 1
            criteriaList.add(crEndTime);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        query.limit(end - begin).skip(begin);//分页数据
        List<CloudOperatorLog> logsModelList =  mongoTemplate.find(query, CloudOperatorLog.class);
        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }



    @RequestMapping(value = "/getBussinessLogById/{logId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id进行MONGODB日志的查询", response = CloudBusinessLog.class, responseContainer = "List")
    public ListResult getBussinessLogById(
            @ApiParam(name = "logId", value = "logId", defaultValue = "")
            @RequestParam(value = "logId", required = false) String logId) throws Exception{
        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(logId)){
            Criteria crCaller = new Criteria().where("id").is(logId);
            criteriaList.add(crCaller);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        List<CloudBusinessLog> logsModelList =  mongoTemplate.find(query, CloudBusinessLog.class);

        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }


    @RequestMapping(value = "/getOperatorLogById/{logId}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id进行MONGODB日志的查询", response = CloudOperatorLog.class, responseContainer = "List")
    public ListResult getOperatorLogById(
            @ApiParam(name = "logId", value = "logId", defaultValue = "")
            @RequestParam(value = "logId", required = false) String logId) throws Exception{

        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(logId)){
            Criteria crCaller = new Criteria().where("id").is(logId);
            criteriaList.add(crCaller);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        List<CloudOperatorLog> logsModelList =  mongoTemplate.find(query, CloudOperatorLog.class);
        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }


    @RequestMapping(value = "/log/getBussinessListLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询,姓名模糊查询", response = CloudBusinessLog.class, responseContainer = "List")
    public ListResult getBusinessListLogs(
            @ApiParam(name = "patient", value = "姓名", defaultValue = "")
            @RequestParam(value = "patient", required = false) String patient,
            @ApiParam(name = "data", value = "数据", defaultValue = "")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        int begin = (page - 1) * size + 1 ;
        int end = page * size;
        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(patient)){
            Criteria crPatient = new Criteria("patient").regex(".*?" + patient + ".*"); //姓名模糊查询
            criteriaList.add(crPatient);
        }
        if(StringUtils.isNotEmpty(data)){
            Criteria crData = new Criteria("data").regex(".*?" + data + ".*"); //模糊查询
            criteriaList.add(crData);
        }
        if(StringUtils.isNotEmpty(caller)){
            Criteria crCaller = new Criteria().where("caller").is(caller);
            criteriaList.add(crCaller);
        }
        if(StringUtils.isNotEmpty(startDate)){
            Criteria crStarTime = new Criteria().where("time").gte(startDate);// 大于等于开始日期
            criteriaList.add(crStarTime);
        }
        if(StringUtils.isNotEmpty(endDate)){
            Criteria  crEndTime = new Criteria().where("time").lte(endDate);// 小于结束日期 + 1
            criteriaList.add(crEndTime);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        query.limit(end - begin).skip(begin);//分页数据
        List<CloudBusinessLog> logsModelList =  mongoTemplate.find(query, CloudBusinessLog.class);

        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }


    @RequestMapping(value = "/log/getOperatorListLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询,姓名模糊查询", response = CloudOperatorLog.class, responseContainer = "List")
    public ListResult getOperatorListLogs(
            @ApiParam(name = "patient", value = "姓名", defaultValue = "")
            @RequestParam(value = "patient", required = false) String patient,
            @ApiParam(name = "data", value = "数据", defaultValue = "")
            @RequestParam(value = "data", required = false) String data,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate", required = false) String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate", required = false) String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller", required = false) String caller,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "当前页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        int begin = (page - 1) * size + 1 ;
        int end = page * size;
        Query query = new Query();
        Criteria cr = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(StringUtils.isNotEmpty(patient)){
            Criteria crPatient = new Criteria("patient").regex(".*?" + patient + ".*"); //姓名模糊查询
            criteriaList.add(crPatient);
        }
        if(StringUtils.isNotEmpty(data)){
            Criteria crData = new Criteria("data").regex(".*?" + data + ".*"); //模糊查询
            criteriaList.add(crData);
        }
        if(StringUtils.isNotEmpty(caller)){
            Criteria crCaller = new Criteria().where("caller").is(caller);
            criteriaList.add(crCaller);
        }
        if(StringUtils.isNotEmpty(startDate)){
            Criteria crStarTime = new Criteria().where("time").gte(startDate);// 大于等于开始日期
            criteriaList.add(crStarTime);
        }
        if(StringUtils.isNotEmpty(endDate)){
            Criteria  crEndTime = new Criteria().where("time").lte(endDate);// 小于结束日期 + 1
            criteriaList.add(crEndTime);
        }
        if(criteriaList != null && criteriaList.size() > 0 ){
            Criteria[] criterias = new  Criteria[criteriaList.size()];
            for(int i=0 ;i < criteriaList.size() ; i++){
                criterias[i] = criteriaList.get(i);
            }
            cr.andOperator(criterias);
        }
        query.addCriteria(cr);
        query.limit(end - begin).skip(begin);//分页数据
        List<CloudOperatorLog> logsModelList =  mongoTemplate.find(query, CloudOperatorLog.class);
        long totalCount = mongoTemplate.count(query, CloudBusinessLog.class);
        ListResult listResult = new ListResult();
        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount((int)totalCount);
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }



}
