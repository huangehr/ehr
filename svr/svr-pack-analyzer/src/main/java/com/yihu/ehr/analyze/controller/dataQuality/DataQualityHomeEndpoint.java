package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.DataQualityHomeService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *  质控管理- 首页
 * @author HZY
 * @created 2018/8/17 11:24
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "DataQualityHomeEndpoint", description = "质控管理首页", tags = {"档案分析服务-质控-首页"})
public class DataQualityHomeEndpoint extends EnvelopRestEndPoint {

    @Autowired
    private DataQualityHomeService dataQualityHomeService;




    @RequestMapping(value = ServiceApi.DataQuality.HomeSummary, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控首页--质控情况")
    public Envelop homneSummary(
            @ApiParam(name = "start", value = "开始时间，（接收时间）")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间，（接收时间）", defaultValue = "")
            @RequestParam(value = "end", required = false) String end ) throws Exception {
        Envelop envelop = new Envelop();
        try {
            return success(dataQualityHomeService.getQuailyDetail(start,end));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.HomeAreaDataList, method = RequestMethod.GET)
    @ApiOperation(value = "质量监控首页--各区县质控情况")
    public Envelop orgAndAreDatas(
            @ApiParam(name = "orgArea", value = "市区域编码")
            @RequestParam(value = "orgArea", required = false) String orgArea,
            @ApiParam(name = "dataType", value = "数据维度  （0: 完整性，1:及时性，2:准确性）")
            @RequestParam(value = "dataType", required = false) Integer dataType,
            @ApiParam(name = "start", value = "开始时间，（接收时间）")
            @RequestParam(value = "start", required = false) String start,
            @ApiParam(name = "end", value = "结束时间，（接收时间）", defaultValue = "")
            @RequestParam(value = "end", required = false) String end ) throws Exception {
        Envelop envelop = new Envelop();
        try {
            return success(dataQualityHomeService.findAreaData(dataType,orgArea,start,end));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }







    @RequestMapping(value = "bulkUploadOrgArea", method = RequestMethod.GET)
    @ApiOperation(value = "批量更新机构关联的区域编码")
    public void bulkUploadOrgArea(
            @ApiParam(name = "index", value = "索引")
            @RequestParam(value = "index", required = false) String index,
            @ApiParam(name = "type", value = "type")
            @RequestParam(value = "type", required = false) String type
    ) throws Exception {
        dataQualityHomeService.bulkUpdateOrgArea(index,type);
    }




}
