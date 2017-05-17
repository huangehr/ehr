package com.yihu.ehr.common.logs.controller;

import com.mongodb.Mongo;
import com.yihu.ehr.common.logs.model.WLYY_BUSINESS_LOG;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.controller.BaseUIController;
import com.yihu.ehr.model.common.ListResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import java.util.List;

/**
 * Created by llh on 2017/5/9.
 */
@Controller
@RequestMapping("/logs")
@SessionAttributes(SessionAttributeKeys.CurrentUser)
@Api(value = "Logs", description = "日志管理", tags = {"日志管理 - 日志信息获取"})
public class LogsController extends BaseUIController {

    @Autowired
    private MongoTemplate mongoTemplate;

    @RequestMapping(value = "/getLogs", method = RequestMethod.GET)
    @ApiOperation(value = "根据传入的参数进行MONGODB日志列表的查询", response = WLYY_BUSINESS_LOG.class, responseContainer = "List")
    public ListResult getLogs(
            @ApiParam(name = "logType", value = "日志类型", defaultValue = "")
            @RequestParam(value = "logType") String logType,
            @ApiParam(name = "startDate", value = "查询开始时间", defaultValue = "")
            @RequestParam(value = "startDate") String startDate,
            @ApiParam(name = "endDate", value = "查询结束时间", defaultValue = "")
            @RequestParam(value = "endDate") String endDate,
            @ApiParam(name = "caller", value = "调用者", defaultValue = "")
            @RequestParam(value = "caller") String caller,
            @ApiParam(name = "page", value = "当前页码", defaultValue = "")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "当前页行数", defaultValue = "")
            @RequestParam(value = "rows") int rows) throws Exception{

        ListResult listResult = new ListResult();
        MongoOperations mongoOps = new MongoTemplate(new SimpleMongoDbFactory(new Mongo(), "wlyy"));
        int begin = (page - 1) * rows + 1 ;
        int end = page * rows;

        Query query = new Query();
        query.addCriteria(Criteria.where("time").gte(startDate)); // 大于等于开始日期
        query.addCriteria(Criteria.where("time").lt(endDate));    // 小于结束日期 + 1
        query.addCriteria(Criteria.where("caller").is(caller));
        query.addCriteria(Criteria.where("logType").is(logType));
        List<WLYY_BUSINESS_LOG> logsModelList =  mongoTemplate.find(query.limit(end - begin).skip(begin), WLYY_BUSINESS_LOG.class);

        if(logsModelList.size() > 0){
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
