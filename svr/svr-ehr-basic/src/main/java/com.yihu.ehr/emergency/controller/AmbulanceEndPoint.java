package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.apps.model.AppApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.AmbulanceService;
import com.yihu.ehr.entity.emergency.Ambulance;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.org.model.Organization;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * EndPoint - 救护车信息
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AmbulanceEndPoint", description = "救护车信息", tags = {"应急指挥-救护车信息"})
public class AmbulanceEndPoint extends BaseRestEndPoint {

    @Autowired
    private AmbulanceService ambulanceService;

    @RequestMapping(value = "/ambulance/getAll", method = RequestMethod.GET)
    @ApiOperation(value = "查询所有救护车辆列表")
    public Envelop getAll() throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<Ambulance> list = ambulanceService.search(null);
            envelop.setDetailModelList(list);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        return envelop;
    }

    @RequestMapping(value = "/ambulance/search", method = RequestMethod.GET)
    @ApiOperation(value = "查询救护车辆信息")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try {
            List<Ambulance> list = ambulanceService.search(fields, filters, sorts, page, size);
            envelop.setDetailModelList(list);
        }catch (Exception e) {
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
            return envelop;
        }
        return envelop;
    }
}
