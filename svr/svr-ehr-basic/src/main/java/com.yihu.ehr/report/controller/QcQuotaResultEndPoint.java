package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.report.MQcDailyReportQuotaResult;
import com.yihu.ehr.model.report.MQcDailyReportResultAnalyse;
import com.yihu.ehr.model.report.MQcDailyReportResultDetail;
import com.yihu.ehr.report.service.QcQuotaResultService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "QcQuotaResult", description = "数据统计指标结果", tags = {"数据统计指标结果"})
public class QcQuotaResultEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    QcQuotaResultService qcQuotaResultService;
    calculatePointUtil calculatePointUtil=new calculatePointUtil();

    @RequestMapping(value = ServiceApi.Report.GetQcQuotaResultList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询数据统计指标结果")
    public ListResult getQcQuotaResultList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<QcQuotaResult> qcQuotaResultList = qcQuotaResultService.search(fields, filters, sorts, page, size);
        if(qcQuotaResultList != null){
            listResult.setDetailModelList(qcQuotaResultList);
            listResult.setTotalCount((int)qcQuotaResultService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增统计指标统计结果")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        QcQuotaResult obj = objectMapper.readValue(model,QcQuotaResult.class);
        obj = qcQuotaResultService.save(obj);
        return Result.success("指标统计更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        qcQuotaResultService.delete(id);
        return Result.success("统计指标删除成功！");
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "根据区域获取统计指标列表")
    public ListResult getQuotaByOrg(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception{

        ListResult result = new ListResult();
        List quotaList = new ArrayList<>();
        Date startDate = DateTimeUtil.simpleDateTimeParse(startTime);
        Date endDate = DateTimeUtil.simpleDateTimeParse(endTime);
        if(!StringUtils.isEmpty(orgCode)){
            //按机构查询整体统计结果
            quotaList = qcQuotaResultService.getQuotaListByOrgCode(orgCode, startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }
        if(!StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分，
            quotaList = qcQuotaResultService.getQuotaListByLocationGBOrg(location, Long.parseLong(quotaId),startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }
        if(StringUtils.isEmpty(orgCode) && StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分，
            quotaList = qcQuotaResultService.getQuotaListByLocation(location, startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }

        return null;
    }

    @ApiOperation("趋势分析 -按区域列表查询,按日初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaIntegrity, method = RequestMethod.GET)
    public ListResult queryQcQuotaIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        List<MQcDailyReportQuotaResult> newQuotaList = new ArrayList<MQcDailyReportQuotaResult>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        MQcDailyReportQuotaResult qc=null;
        if(!StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分
            quotaList = qcQuotaResultService.getQuotaListByLocation(location,Long.parseLong(quotaId),startDate, endDate);
            if(quotaList.size() > 0){
                for(int i=0;i<quotaList.size();i++){
                    Object[] obj = (Object[])quotaList.get(i);
                    //json处理
                    qc=new MQcDailyReportQuotaResult();
                    String quotaIdstr = obj[0].toString();
                    //指标名称
                    qc.setQuotaName(obj[1].toString());
                    String eventTime=obj[2].toString();
                    if(null!=eventTime&&!"".equals(eventTime)){
                        //事件时间
                        qc.setEventTime(eventTime.substring(0,4)+'年'+eventTime.substring(5,7)+'月'+eventTime.substring(8,10)+'日');
                    }
                    int realNum = 0;
                    int totalNum = 0;
                    int errorNum = 0;
                    int timelyNum = 0;
                    String value="";
                    if(obj[3] != null && obj[4] != null && obj[5] != null && obj[6] != null){
                        //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
                        totalNum = Integer.valueOf(obj[3].toString());
                        qc.setTotalNum(totalNum);
                        //应收数
                        realNum = Integer.valueOf(obj[4].toString());
                        qc.setRealNum(realNum);
                        //错误数量（该字段只针对数据元的准确性统计）
                        errorNum = Integer.valueOf(obj[5].toString());
                        qc.setErrorNum(errorNum);
                        //及时采集的档案数量
                        timelyNum = Integer.valueOf(obj[6].toString());
                        qc.setTimelyNum(timelyNum);
                        value=calculatePointUtil.calculatePoint(quotaIdstr, realNum, totalNum , errorNum , timelyNum);
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    if(obj[0] != null){
                        //指标ID
                        qc.setQuotaId( Long.valueOf(obj[0].toString()));
                    }
                    qc.setValue(value+"%");
                    String an=obj[7].toString();
                    if(null!=an&&an.length()>5){
                        qc.setAn(an.substring(0,6)+'%');
                    }else{
                        qc.setAn(an+'%');
                    }
                    String mom=obj[8].toString();
                    if(null!=mom&&mom.length()>5){
                        qc.setMom(mom.substring(0,6)+'%');
                    }else{
                        qc.setMom(mom+'%');
                    }
                    newQuotaList.add(qc);
                }
                result.setDetailModelList(newQuotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
            }else {
            }
        }
        return result;
    }

    @ApiOperation("趋势分析 - 按机构列表查询,按日初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaOrgIntegrity, method = RequestMethod.GET)
    public ListResult queryQcQuotaOrgIntegrity(
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        List<MQcDailyReportQuotaResult> newQuotaList = new ArrayList<MQcDailyReportQuotaResult>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        MQcDailyReportQuotaResult qc = null;
        if(!StringUtils.isEmpty(quotaId)){
        quotaList = qcQuotaResultService.getQuotaListByOrg(orgCode, Long.parseLong(quotaId), startDate, endDate);
        if (quotaList.size() > 0) {
            for (int i = 0; i < quotaList.size(); i++) {
                Object[] obj = (Object[]) quotaList.get(i);
                //json处理
                qc = new MQcDailyReportQuotaResult();
                String quotaIdstr = obj[0].toString();
                //指标名称
                qc.setQuotaName(obj[1].toString());
                String eventTime=obj[2].toString();
                if(null!=eventTime&&!"".equals(eventTime)){
                    //事件时间
                    qc.setEventTime(eventTime.substring(0,4)+'年'+eventTime.substring(5,7)+'月'+eventTime.substring(8,10)+'日');
                }
                int realNum = 0;
                int totalNum = 0;
                int errorNum = 0;
                int timelyNum = 0;
                String value="";
                if (obj[3] != null && obj[4] != null && obj[5] != null && obj[6] != null) {
                    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
                    totalNum = Integer.valueOf(obj[3].toString());
                    qc.setTotalNum(totalNum);
                    //应收数
                    realNum = Integer.valueOf(obj[4].toString());
                    qc.setRealNum(realNum);
                    //错误数量（该字段只针对数据元的准确性统计）
                    errorNum = Integer.valueOf(obj[5].toString());
                    qc.setErrorNum(errorNum);
                    //及时采集的档案数量
                    timelyNum = Integer.valueOf(obj[6].toString());
                    qc.setTimelyNum(timelyNum);
                    value=calculatePointUtil.calculatePoint(quotaIdstr, realNum, totalNum , errorNum , timelyNum);
                }
                DecimalFormat df = new DecimalFormat("0.00");
                if (obj[0] != null) {
                    //指标ID
                    qc.setQuotaId( Long.valueOf(obj[0].toString()));
                }
                qc.setValue(value+ "%");
                String an=obj[7].toString();
                if(null!=an&&an.length()>5){
                    qc.setAn(an.substring(0,6)+'%');
                }else{
                    qc.setAn(an+'%');
                }
                String mom=obj[8].toString();
                if(null!=mom&&mom.length()>5){
                    qc.setMom(mom.substring(0,6)+'%');
                }else{
                    qc.setMom(mom+'%');
                }
                newQuotaList.add(qc);
            }
            result.setDetailModelList(newQuotaList);
            result.setSuccessFlg(true);
            result.setCode(200);
        } else {
            result.setCode(200);
            result.setMessage("查询无数据");
            result.setTotalCount(0);
        }
    }
        return result;
    }

    @ApiOperation("根据区域查询所有指标统计结果,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcOverAllIntegrity, method = RequestMethod.GET)
    public ListResult queryQcOverAllIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        List<QcQuotaResult> newQuotaList = new ArrayList<QcQuotaResult>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        QcQuotaResult qc=null;
        //按区域查询统计结果集
        quotaList = qcQuotaResultService.getQuotaListByLocation(location,startDate,endDate);
        for(int i=0;i<quotaList.size();i++){
                Object[] obj = (Object[])quotaList.get(i);
                //json处理
                qc=new QcQuotaResult();
                 //指标Id
                String quotaId=obj[0].toString();
                qc.setQuotaId(Long.parseLong(quotaId));
                //指标名称
                qc.setQuotaName(obj[1].toString());
                int realNum = 0;
                int totalNum = 0;
                int errorNum = 0;
                int timelyNum = 0;
                String value="";
                if(obj[2] != null && obj[3] != null && obj[4] != null && obj[5] != null){
                    //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
                    totalNum = Integer.valueOf(obj[2].toString());
                    qc.setTotalNum(totalNum);
                    //应收数
                    realNum = Integer.valueOf(obj[3].toString());
                    qc.setRealNum(realNum);
                    //错误数量（该字段只针对数据元的准确性统计）
                    errorNum = Integer.valueOf(obj[4].toString());
                    qc.setErrorNum(errorNum);
                    //及时采集的档案数量
                    timelyNum = Integer.valueOf(obj[5].toString());
                    qc.setTimelyNum(timelyNum);
                   value=calculatePointUtil.calculatePoint(quotaId, realNum, totalNum , errorNum , timelyNum);
                }
                qc.setValue(value+"%");
                newQuotaList.add(qc);
            }
        if(newQuotaList.size() > 0){
            result.setDetailModelList(newQuotaList);
            result.setSuccessFlg(true);
            result.setCode(200);
        }else {
        }
        return result;
    }

    @ApiOperation("根据机构查询所有指标统计结果,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcOverAllOrgIntegrity, method = RequestMethod.GET)
    public ListResult queryQcOverAllOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        List<QcQuotaResult> newQuotaList = new ArrayList<QcQuotaResult>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        QcQuotaResult qc=null;
        //按区域查询统计结果集
        quotaList = qcQuotaResultService.getQuotaListByOrgCode(orgCode,startDate,endDate);
      //  @Query("select qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName,sum(qc.totalNum),sum(qc.realNum),sum(qc.errorNum),sum(qc.timelyNum) from QcQuotaResult qc where qc.orgCode = :orgCode and qc.eventTime >= :startTime and qc.eventTime< :endTime group by qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName ")

        for(int i=0;i<quotaList.size();i++){
            Object[] obj = (Object[])quotaList.get(i);
            //json处理
            qc=new QcQuotaResult();
            //指标Id
            String quotaId=obj[2].toString();
            //指标名称
            qc.setQuotaName(obj[3].toString());
            qc.setOrgName(obj[1].toString());
            qc.setOrgCode(obj[0].toString());
            int realNum = 0;
            int totalNum = 0;
            int errorNum = 0;
            int timelyNum = 0;
            String value="";
            if(obj[4] != null && obj[5] != null && obj[6] != null && obj[7] != null){
                //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
                totalNum = Integer.valueOf(obj[4].toString());
                qc.setTotalNum(totalNum);
                //应收数
                realNum = Integer.valueOf(obj[5].toString());
                qc.setRealNum(realNum);
                //错误数量（该字段只针对数据元的准确性统计）
                errorNum = Integer.valueOf(obj[6].toString());
                qc.setErrorNum(errorNum);
                //及时采集的档案数量
                timelyNum = Integer.valueOf(obj[7].toString());
                qc.setTimelyNum(timelyNum);
                value=calculatePointUtil.calculatePoint(quotaId, realNum, totalNum , errorNum , timelyNum);
            }
            qc.setValue(value+"%");
            newQuotaList.add(qc);
        }
        if(newQuotaList.size() > 0){
            result.setDetailModelList(newQuotaList);
            result.setSuccessFlg(true);
            result.setCode(200);
        }else {
        }
        return result;
    }

    @ApiOperation("分析明细列表")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaDailyIntegrity, method = RequestMethod.GET)
    public ListResult queryQcQuotaDailyIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        Map<String,String> QcQuotaResultAnalyseMap=new TreeMap();
        //获取区域名称和事件时间
        quotaList = qcQuotaResultService.getfindQcListByLocationAndTime(location,startDate, endDate);
        if(quotaList.size()>0) {
            for (int i = 0; i < quotaList.size(); i++) {
                Object[] obj = (Object[]) quotaList.get(i);
                String eventTime=obj[2].toString();
                String newEventTime="";
                if(null!=eventTime&&!"".equals(eventTime)){
                    //事件时间
                     newEventTime= eventTime.substring(0,4)+'年'+eventTime.substring(5,7)+'月'+eventTime.substring(8,10)+'日';
                }
                String qcKey=obj[2].toString().substring(0,10)+obj[3].toString();
                QcQuotaResultAnalyseMap.put(qcKey,newEventTime);
            }
        }
        //区域整体统计结果
        quotaList = qcQuotaResultService.getQuotaListByLocationAndTime(location,startDate, endDate);
        Map<String,MQcDailyReportResultDetail> detailMap=new TreeMap();
        Map<String,MQcDailyReportResultDetail> detailAnMap=new TreeMap();
        Map<String,MQcDailyReportResultDetail> detailMonMap=new TreeMap();
        MQcDailyReportResultDetail qrd=null;
            for (int i = 0; i < quotaList.size(); i++) {
                Object[] obj = (Object[]) quotaList.get(i);
                String qcKey=obj[0].toString().substring(0, 10)+obj[2].toString();
                if (null != QcQuotaResultAnalyseMap.get(qcKey)) {
                    String resultQuotaId=obj[3].toString();
                    //总比
                    String detailMapKey=qcKey+"总比";
                    Map<String,String> map=calculatePointUtil.reportDetailData("1",obj);
                    detailMap=calculatePointUtil.detailValue(resultQuotaId,i,detailMap,detailMapKey,QcQuotaResultAnalyseMap.get(qcKey),obj[2].toString(),obj[1].toString(),"总比",map.get("1"));

                    //同比
                    String detailAnMapKey=qcKey+"同比";
                    map=calculatePointUtil.reportDetailData("2",obj);
                    detailAnMap=calculatePointUtil.detailValue(resultQuotaId,i,detailAnMap,detailAnMapKey,QcQuotaResultAnalyseMap.get(qcKey),obj[2].toString(),obj[1].toString(),"同比",map.get("2"));

                    //环比
                    String detailMonMapKey=qcKey+"环比";
                    map=calculatePointUtil.reportDetailData("3",obj);
                    detailMonMap=calculatePointUtil.detailValue(resultQuotaId,i,detailMonMap,detailMonMapKey,QcQuotaResultAnalyseMap.get(qcKey),obj[2].toString(),obj[1].toString(),"环比",map.get("3"));

                }
            }
        List<MQcDailyReportResultDetail>  objectList=new ArrayList<MQcDailyReportResultDetail>();
        for (String key : detailMap.keySet()) {
            String mapKey=key.substring(0,key.length()-2);
            objectList.add(detailMap.get(key));
            objectList.add(detailAnMap.get(mapKey+"同比"));
            objectList.add(detailMonMap.get(mapKey+"环比"));
        }
        if(objectList.size() > 0){
            result.setDetailModelList(objectList);
            result.setSuccessFlg(true);
            result.setCode(200);
        }else {
        }

        return result;
    }


    @ApiOperation("根据地区、期间查询各机构某项指标的值")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaByLocation, method = RequestMethod.GET)
    public ListResult queryQcQuotaByLocation(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<Object> quotaList = new ArrayList<Object>();
        List<QcQuotaResult> newQuotaList = new ArrayList<QcQuotaResult>();
        Date startDate = DateUtil.formatYMDToYMDHMS(startTime);
        Date endDate =DateUtil.formatYMDToYMDHMS(endTime);
        QcQuotaResult qc=null;
        if(!StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分
            //@Query("select qc.orgCode,qc.orgName,qc.quotaId,qc.quotaName,sum(qc.totalNum) as totalNum,sum(qc.realNum) as realNum,sum(qc.errorNum) as errorNum,sum(qc.timelyNum) as timelyNum
            quotaList = qcQuotaResultService.getQuotaListByLocationGBOrg(location,Long.parseLong(quotaId),startDate, endDate);
            if(quotaList.size() > 0){
                for(int i=0;i<quotaList.size();i++){
                    Object[] obj = (Object[])quotaList.get(i);
                    //json处理
                    qc=new QcQuotaResult();
                    qc.setOrgCode(obj[0].toString());
                    qc.setOrgName(obj[1].toString());
                    String quotaIdstr = obj[2].toString();
                    //指标名称
                    qc.setQuotaName(obj[3].toString());
                    int realNum = 0;
                    int totalNum = 0;
                    int errorNum = 0;
                    int timelyNum = 0;
                    String value="";
                    if(obj[4] != null && obj[5] != null && obj[6] != null && obj[7] != null){
                        //实收数 （数据元的实收为 应收 - 错误数（标识为空的错误code））
                        totalNum = Integer.valueOf(obj[4].toString());
                        qc.setTotalNum(totalNum);
                        //应收数
                        realNum = Integer.valueOf(obj[5].toString());
                        qc.setRealNum(realNum);
                        //错误数量（该字段只针对数据元的准确性统计）
                        errorNum = Integer.valueOf(obj[6].toString());
                        qc.setErrorNum(errorNum);
                        //及时采集的档案数量
                        timelyNum = Integer.valueOf(obj[7].toString());
                        qc.setTimelyNum(timelyNum);
                        value=calculatePointUtil.calculatePoint(quotaIdstr, realNum, totalNum , errorNum , timelyNum);
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    if(obj[2] != null){
                        //指标ID
                        qc.setQuotaId( Long.valueOf(obj[2].toString()));
                    }
                    qc.setValue(value+"%");
                    newQuotaList.add(qc);
                }
                result.setDetailModelList(newQuotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
            }else {
            }
        }
        return result;
    }



}
