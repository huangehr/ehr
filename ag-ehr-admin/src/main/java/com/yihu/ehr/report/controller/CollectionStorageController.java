package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.EventsModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by janseny on 2017/5/9.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "CollectionStorage", description = "质控包数据采集入库", tags = {"报表管理-质控包数据采集入库"})
public class CollectionStorageController  extends BaseController {


    @RequestMapping(value = "/getEventDataReport", method = RequestMethod.POST)
    @ApiOperation(value = "日报数据上传")
    public Envelop add(
            @ApiParam(name = "eventsData", value = "采集json数据", defaultValue = "")
            @RequestParam("eventsData") String collectionData) {
        try {

            EventsModel eventsModel = objectMapper.readValue(collectionData, EventsModel.class);


            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


}
