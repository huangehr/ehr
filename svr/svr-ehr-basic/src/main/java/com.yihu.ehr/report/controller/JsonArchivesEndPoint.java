package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.JsonArchives;
import com.yihu.ehr.report.service.JsonArchivesService;
import com.yihu.ehr.report.service.QcQuotaDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "JsonArchives", description = "档案数据", tags = {"档案数据"})
public class JsonArchivesEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    JsonArchivesService jsonArchivesService;

    @RequestMapping(value = ServiceApi.Report.GetJsonArchives, method = RequestMethod.GET)
    @ApiOperation(value = "查询档案包数据")
    JsonArchives search( @RequestParam(value = "filters", required = false) String filters){
        List<JsonArchives> list = null;
        try {
            list = jsonArchivesService.search(filters);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(list.isEmpty()){
            return  null;
        }else{
            return list.get(0);
        }
    }

}
