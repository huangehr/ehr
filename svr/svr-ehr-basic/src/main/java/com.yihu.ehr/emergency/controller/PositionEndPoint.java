package com.yihu.ehr.emergency.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.emergency.service.PositionService;
import com.yihu.ehr.entity.emergency.Position;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * End - 定位数据信息表 (暂时不用)
 * Created by progr1mmer on 2017/11/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "PositionEndPoint", description = "定位数据", tags = {"应急指挥-定位数据"})
public class PositionEndPoint extends BaseRestEndPoint {

    @Autowired
    private PositionService positionService;

    @RequestMapping(value = "/position/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation("保存单条记录")
    public Envelop save(
            @ApiParam(name = "position", value = "定位数据")
            @RequestBody String position) throws Exception{
        Envelop envelop = new Envelop();
        Position position1 = objectMapper.readValue(position, Position.class);
        positionService.save(position1);
        envelop.setSuccessFlg(true);
        return envelop;
    }

}
