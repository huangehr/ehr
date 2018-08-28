package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.WarningQuestionService;
import com.yihu.ehr.analyze.service.dataQuality.WarningRecordService;
import com.yihu.ehr.analyze.service.scheduler.WarningSchedulerService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.model.quality.MDqWarningRecord;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author yeshijie on 2018/6/12.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "WarningRecordEndPoint", description = "质控-预警问题", tags = {"档案分析服务-质控-预警问题"})
public class WarningRecordEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private WarningRecordService warningRecordService;
    @Autowired
    private WarningQuestionService warningQuestionService;
    @Autowired
    private WarningSchedulerService warningSchedulerService;

    @RequestMapping(value = ServiceApi.DataQuality.WarningQuestionAnalyze, method = RequestMethod.POST)
    @ApiOperation(value = "生成指定日期的预警记录")
    public Envelop warningQuestionAnalyze(@ApiParam(name = "dateStr", value = "指定日期生成某天的预警信息", defaultValue = "2018-01-01")
                                          @RequestParam(value = "dateStr", required = false) String dateStr) {
        Envelop envelop = new Envelop();
        try {
            warningQuestionService.analyze(dateStr);
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.DataQuality.WarningQuestionJob, method = RequestMethod.POST)
    @ApiOperation(value = "手动启动预警问题job")
    public Envelop warningQuestionJob() {
        Envelop envelop = new Envelop();
        try {
            warningSchedulerService.init();
            return success(null);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

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
            @RequestParam(value = "type", required = false) String type,
            @ApiParam(name = "startTime", value = "开始时间", defaultValue = "2018-06-11")
            @RequestParam(value = "startTime", required = false) String startTime,
            @ApiParam(name = "endTime", value = "结束时间", defaultValue = "2018-06-11")
            @RequestParam(value = "endTime", required = false) String endTime,
            @ApiParam(name = "id", value = "将某条数据置顶")
            @RequestParam(value = "id", required = false) String id,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        Envelop envelop = new Envelop();
        try {
            String filters = "";
            if(StringUtils.isNotBlank(type)){
                filters += "type="+type+";";
            }
            if(StringUtils.isNotBlank(orgCode)){
                filters += "orgCode="+orgCode+";";
            }
            if(StringUtils.isNotBlank(quota)){
                filters += "warningType="+quota+";";
            }
            if(StringUtils.isNotBlank(status)){
                filters += "status="+status+";";
            }
            if(StringUtils.isNotBlank(startTime)){
                filters += "recordTime>="+startTime+";";
            }
            if(StringUtils.isNotBlank(endTime)){
                filters += "recordTime<="+endTime+";";
            }
            if(StringUtils.isNotBlank(id)){
                filters +="id<>"+id+";";
            }
            String sorts = "-warningTime";
            List<DqWarningRecord> list = warningRecordService.search(null, filters, sorts, page, size);

            if(StringUtils.isNotBlank(id)){
                DqWarningRecord upDqWarningRecord = warningRecordService.findById(id);
                if(!CollectionUtils.isEmpty(list)){
                    //移出最后一条,
                    list.remove(list.size()-1);
                }
                list.add(0,upDqWarningRecord);
            }

            List<MDqWarningRecord> records = (List<MDqWarningRecord>) convertToModels(list, new ArrayList<>(list.size()), MDqWarningRecord.class, null);
            return getPageResult(records,(int)warningRecordService.getCount(filters), page, size);
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


    @RequestMapping(value = ServiceApi.DataQuality.RealTimeMonitorList, method = RequestMethod.GET)
    @ApiOperation(value = "实时问题监控列表")
    public Envelop realTimeMonitor() {
        Envelop envelop = new Envelop();
        try {
            //获取上个月第一天
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            Date lastMonStart = calendar.getTime();
            String lastMonStartStr = DateUtil.toString(lastMonStart);

            //获取上个月最后一天
            Calendar calendar2 = Calendar.getInstance();
            calendar2.set(Calendar.DAY_OF_MONTH, 1);
            calendar2.add(Calendar.DATE, -1);
            Date lastMonEnd = calendar2.getTime();
            String lastMonEndStr = DateUtil.toString(lastMonEnd);

            //拼接上个月未解决的过滤条件
            String filters = "recordTime>="+lastMonStartStr+";recordTime<="+lastMonEndStr+";status=1;";
            //上个月未解决问题数量
            long lastMonth = warningRecordService.getCount(filters);

            //获取当月第一天
            Calendar c = Calendar.getInstance();
            c.add(Calendar.MONTH, 0);
            c.set(Calendar.DAY_OF_MONTH,1);
            String presentMonthStr = DateUtil.toString(c.getTime());

            //获取当月最后一天
            Calendar ca = Calendar.getInstance();
            ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
            String presentMonthEnd = DateUtil.toString(ca.getTime());

            //拼接本月新增问题过滤条件
            String filters2 = "recordTime>="+presentMonthStr+";recordTime<="+presentMonthEnd+";";
            //本月新增问题数量
            long thisMonthAdd = warningRecordService.getCount(filters2);

            String filters3 = filters2+"status=2;";
            //获取本月已解决问题数量
            long thisMonthDealed = warningRecordService.getCount(filters3);

            //待处理数量 = 本月新增+上月待处理-本月已解决
            long unDeal = lastMonth + thisMonthAdd - thisMonthDealed;

            Map<String, Long> map = new HashMap<String, Long>();
            map.put("lastMonth",lastMonth);//上个月待处理
            map.put("thisMonthAdd",thisMonthAdd);//本月新增
            map.put("thisMonthDealed",thisMonthDealed);//本月已解决
            map.put("unDeal",unDeal);//待解决
            envelop.setSuccessFlg(true);
            envelop.setObj(map);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
