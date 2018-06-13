package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.WarningRecordService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.model.quality.MDqWarningRecord;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yeshijie on 2018/6/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WarningRecordEndPoint", description = "质控-预警问题", tags = {"档案分析服务-质控-预警问题"})
public class WarningRecordEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private WarningRecordService warningRecordService;


    @RequestMapping(value = ServiceApi.DataQuality.WarningRecordList, method = RequestMethod.GET)
    @ApiOperation(value = "预警问题列表")
    public Envelop warningRecordList(
            @ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quota", value = "指标（传warningType）", defaultValue = "101")
            @RequestParam(value = "quota", required = false) String quota,
            @ApiParam(name = "status", value = "状态（1未解决，2已解决）", defaultValue = "1")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "type", value = "类型（1接收，2资源化，3上传）", defaultValue = "1")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "startTime", value = "开始时间", defaultValue = "2018-06-11")
            @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间", defaultValue = "2018-06-11")
            @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Envelop envelop = new Envelop();
        try {
            String filters = "type="+type;
            if(StringUtils.isNotBlank(orgCode)){
                filters += ";orgCode="+orgCode;
            }
            if(StringUtils.isNotBlank(quota)){
                filters += ";warningType="+quota;
            }
            if(StringUtils.isNotBlank(status)){
                filters += ";status="+status;
            }
            if(StringUtils.isNotBlank(startTime)){
                filters += ";recordTime>="+startTime;
            }
            if(StringUtils.isNotBlank(endTime)){
                filters += ";recordTime<="+endTime;
            }
            String sorts = "-warningTime";
            List<DqWarningRecord> list = warningRecordService.search(null, filters, sorts, page, size);
            pagedResponse(request, response, warningRecordService.getCount(filters), page, size);
            return success(convertToModels(list, new ArrayList<>(list.size()), MDqWarningRecord.class, null));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.WarningRecord, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询平台接收预警")
    public Envelop warningRecord(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        try {
            DqWarningRecord warning =  warningRecordService.findById(id);
            return success(convertToModel(warning, MDqWarningRecord.class));
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.WarningRecordUpd, method = RequestMethod.POST)
    @ApiOperation(value = "处理问题")
    public Envelop warningRecordUpd(
            @ApiParam(name = "solveTime", value = "解决时间", defaultValue = "2018-06-12")
            @RequestParam(value = "solveTime", required = true) String solveTime,
            @ApiParam(name = "solveId", value = "解决人id", defaultValue = "101")
            @RequestParam(value = "solveId", required = true) String solveId,
            @ApiParam(name = "solveName", value = "解决人姓名", defaultValue = "1")
            @RequestParam(value = "solveName", required = true) String solveName,
            @ApiParam(name = "solveType", value = "解决方式（1已解决，2忽略，3无法解决，4不是问题）", defaultValue = "1")
            @RequestParam(value = "solveType", required = true) String solveType,
            @ApiParam(name = "id", value = "id", defaultValue = "1")
            @RequestParam(value = "id", required = true) String id) {
        Envelop envelop = new Envelop();
        try {
            int re = warningRecordService.warningRecordUpd(solveTime, solveId, solveName, solveType, id);
            if(re==-1){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("记录不存在");
            }else if(re==-2){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("已处理，请勿重复操作");
            }else {
                return success(null);
            }
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
