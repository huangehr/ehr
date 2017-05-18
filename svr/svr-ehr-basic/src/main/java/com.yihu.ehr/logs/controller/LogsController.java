package com.yihu.ehr.logs.controller;

import com.mongodb.Mongo;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.BaseUIController;
import com.yihu.ehr.logs.model.WLYY_BUSINESS_LOG;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by llh on 2017/5/9.
 */
@Controller
@RequestMapping(ApiVersion.Version1_0)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
@Api(value = "log", description = "日志管理", tags = {"日志管理 - 日志信息获取"})
public class LogsController extends BaseUIController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/getLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询", response = WLYY_BUSINESS_LOG.class, responseContainer = "List")
    public ListResult getLogs(
            @ApiParam(name = "logType", value = "日志类型", defaultValue = "")
            @RequestParam(value = "logType", required = false) String logType,
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

        ListResult listResult = new ListResult();
//        MongoOperations mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new Mongo(), "wlyy"));
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
        if(StringUtils.isNotEmpty(logType)){
            Criteria crLogType = new Criteria().where("logType").is(logType);
            criteriaList.add(crLogType);
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
        List<WLYY_BUSINESS_LOG> logsModelList =  mongoTemplate.find(query, WLYY_BUSINESS_LOG.class);

        if(logsModelList.size() > 0) {
            listResult.setDetailModelList(logsModelList);
            listResult.setSuccessFlg(true);
            listResult.setTotalCount(logsModelList.size());
            listResult.setCode(200);
            listResult.setMessage("日志查询成功！");
            return listResult;
        }
        return null;
    }

}
