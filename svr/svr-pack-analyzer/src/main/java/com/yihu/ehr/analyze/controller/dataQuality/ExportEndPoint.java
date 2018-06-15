package com.yihu.ehr.analyze.controller.dataQuality;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yihu.ehr.analyze.service.dataQuality.DataQualityStatisticsService;
import com.yihu.ehr.analyze.service.dataQuality.WarningRecordService;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.*;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.*;

/**
 * 导出
 * @author yeshijie on 2018/6/13.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "ExportEndPoint", description = "质控-导出", tags = {"档案分析服务-质控-导出"})
public class ExportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private WarningRecordService warningRecordService;
    @Autowired
    private PackQcReportService packQcReportService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private DataQualityStatisticsService dataQualityStatisticsService;

    public static int maxRowSize = 60000;

    @RequestMapping(value = ServiceApi.DataQuality.ExportWarningRecordToExcel, method = RequestMethod.GET)
    @ApiOperation(value = "导出预警问题列表")
    public void exportToExcel(@ApiParam(name = "orgCode", value = "机构code", defaultValue = "jkzl")
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
                              HttpServletResponse response){
        try {
            String fileName = "预警问题列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xls");
            OutputStream os = response.getOutputStream();

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
            List<DqWarningRecord> list = warningRecordService.search(null, filters, sorts, 1, 99999);
            //写excel
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            //创建Excel工作表 指定名称和位置
            WritableSheet ws = wwb.createSheet(fileName,0);
            //添加固定信息，题头等
            if("1".equals(type)){
                warningRecordService.addReceiveStaticCell(ws);
            }else if("2".equals(type)){
                warningRecordService.addResourceStaticCell(ws);
            }else if("3".equals(type)){
                warningRecordService.addUploadStaticCell(ws);
            }
            WritableCellFormat wc = new WritableCellFormat();
            wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
            for(int i=0;i<list.size();i++) {
                int j=i+1;
                DqWarningRecord record = list.get(i);
                //添加列表明细
                warningRecordService.addRow(type,wc,ws,record,j);
            }
            //写入工作表
            wwb.write();
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = ServiceApi.DataQuality.ExportQualityMonitoring, method = RequestMethod.GET)
    @ApiOperation(value = "导出平台接收列表")
    public void exportQualityMonitoring( @ApiParam(name = "start", value = "开始时间")
                               @RequestParam(value = "start", required = false) String start,
                               @ApiParam(name = "end", value = "结束时间", defaultValue = "")
                               @RequestParam(value = "end", required = false) String end,
                               @ApiParam(name = "eventType", value = "就诊类型 0门诊 1住院 2体检,null全部", defaultValue = "")
                               @RequestParam(value = "eventType", required = false) Integer eventType,
                              HttpServletResponse response){
        try {
            String fileName = "平台接收列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xls");
            OutputStream os = response.getOutputStream();
            List<Map<String, Object>> list = dataQualityStatisticsService.dataset(start,end,eventType);
            //写excel
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            //创建Excel工作表 指定名称和位置
            WritableSheet ws = wwb.createSheet(fileName,0);
            //添加固定信息，题头等
            addCell(ws,0,0,"机构");
            addCell(ws,1,0,"医院档案数");
            addCell(ws,2,0,"医院数据集");
            addCell(ws,3,0,"接收档案数");
            addCell(ws,4,0,"接收数据集");
            addCell(ws,5,0,"接收质量异常数");
            addCell(ws,6,0,"资源化解析成功");
            addCell(ws,7,0,"资源化解析失败");
            addCell(ws,8,0,"资源化解析异常");
            WritableCellFormat wc = new WritableCellFormat();
            wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
            for(int i=0;i<list.size();i++) {
                int j=i+1;
                Map<String,Object> record = list.get(i);
                //添加列表明细
                addCell(ws,0,j,ObjectUtils.toString(record.get("orgName")),wc);
                addCell(ws,1,j,ObjectUtils.toString(record.get("hospitalArchives")),wc);
                addCell(ws,2,j,ObjectUtils.toString(record.get("hospitalDataset")),wc);
                addCell(ws,3,j,ObjectUtils.toString(record.get("receiveArchives")),wc);
                addCell(ws,4,j,ObjectUtils.toString(record.get("receiveDataset")),wc);
                addCell(ws,5,j,ObjectUtils.toString(record.get("receiveException")),wc);
                addCell(ws,6,j,ObjectUtils.toString(record.get("resourceSuccess")),wc);
                addCell(ws,7,j,ObjectUtils.toString(record.get("resourceFailure")),wc);
                addCell(ws,8,j,ObjectUtils.toString(record.get("resourceException")),wc);
            }
            //写入工作表
            wwb.write();
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = ServiceApi.DataQuality.ExportReceptionList, method = RequestMethod.GET)
    @ApiOperation(value = "导出接收情况列表")
    public void exportReceptionList( @ApiParam(name = "start", value = "开始时间")
                                         @RequestParam(value = "start", required = false) String start,
                                         @ApiParam(name = "end", value = "结束时间", defaultValue = "")
                                         @RequestParam(value = "end", required = false) String end,
                                         HttpServletResponse response){
        try {
            String fileName = "平台接收列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xls");
            OutputStream os = response.getOutputStream();
            List<Map<String, Object>> list = dataQualityStatisticsService.inTimeAndIntegrityRate(start,end);
            //写excel
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            //创建Excel工作表 指定名称和位置
            WritableSheet ws = wwb.createSheet(fileName,0);
            //添加固定信息，题头等
            addCell(ws,0,0,"机构");
            addCell(ws,1,0,"及时率-就诊");
            addCell(ws,2,0,"及时率-门诊");
            addCell(ws,3,0,"及时率-住院");
            addCell(ws,4,0,"及时率-体检");
            addCell(ws,5,0,"完整率-就诊");
            addCell(ws,6,0,"完整率-门诊");
            addCell(ws,7,0,"完整率-住院");
            addCell(ws,8,0,"完整率-体检");
            WritableCellFormat wc = new WritableCellFormat();
            wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
            for(int i=0;i<list.size();i++) {
                int j=i+1;
                Map<String,Object> record = list.get(i);
                //添加列表明细
                addCell(ws,0,j,ObjectUtils.toString(record.get("orgName")),wc);
                addCell(ws,1,j,ObjectUtils.toString(record.get("visitIntimeRate")),wc);
                addCell(ws,2,j,ObjectUtils.toString(record.get("outpatientInTimeRate")),wc);
                addCell(ws,3,j,ObjectUtils.toString(record.get("hospitalInTimeRate")),wc);
                addCell(ws,4,j,ObjectUtils.toString(record.get("peInTimeRate")),wc);
                addCell(ws,5,j,ObjectUtils.toString(record.get("visitIntegrityRate")),wc);
                addCell(ws,6,j,ObjectUtils.toString(record.get("outpatientIntegrityRate")),wc);
                addCell(ws,7,j,ObjectUtils.toString(record.get("hospitalIntegrityRate")),wc);
                addCell(ws,8,j,ObjectUtils.toString(record.get("visitIntegrityRate")),wc);
            }
            //写入工作表
            wwb.write();
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = ServiceApi.DataQuality.ExportAnalyzeErrorList, method = RequestMethod.GET)
    @ApiOperation(value = "导出解析失败问题列表")
    public void exportAnalyzeErrorList( @ApiParam(name = "filters", value = "过滤")
                                        @RequestParam(value = "filters", required = false) String filters,
                                        @ApiParam(name = "sorts", value = "排序")
                                        @RequestParam(value = "sorts", required = false) String sorts,
                                        HttpServletResponse response){
        if(StringUtils.isNotEmpty(filters)){
            filters+="analyze_status=2;"+filters;
        }else{
            filters="analyze_status=2";
        }
        try {
            String fileName = "解析失败问题列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xlsx");
            OutputStream os = response.getOutputStream();
            //写excel
            SXSSFWorkbook wwb = new SXSSFWorkbook(100);
            wwb.setCompressTempFiles(true);
            String[] title = {"解析时间","接收时间","医疗机构","序列号","失败原因"};
            int count = (int) elasticSearchUtil.count("json_archives", "info", filters);
            double pageNum = count % maxRowSize > 0 ? count / maxRowSize + 1 : count / maxRowSize;
            for (int i = 0; i < pageNum; i++) {
                List<Map<String, Object>> list = packQcReportService.analyzeErrorList(filters,sorts,i+1,maxRowSize);
                //创建Excel工作表 指定名称和位置
                Sheet sheet = wwb.createSheet("Sheet" + (i+1));
                //添加固定信息，题头等
                Row titleRow = sheet.createRow(0);
                for (int t = 0; t < title.length; t++) {
                    Cell xcell = titleRow.createCell(t);
                    xcell.setCellValue(title[t] + "");
                }
                for (int j = 0; j < list.size(); j++) {
                    Row row = sheet.createRow(j+ 1);
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    row.createCell(0).setCellValue(ObjectUtils.toString(record.get("analyze_date")));
                    row.createCell(1).setCellValue(ObjectUtils.toString(record.get("receive_date")));
                    row.createCell(2).setCellValue(ObjectUtils.toString(record.get("org_name")));
                    row.createCell(3).setCellValue(ObjectUtils.toString(record.get("_id")));
                    row.createCell(4).setCellValue(ObjectUtils.toString(record.get("_id")));
                    row.createCell(5).setCellValue(getErrorType(ObjectUtils.toString(record.get("error_type"))));
                }
            }
            wwb.write(os);
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = ServiceApi.DataQuality.ExportMetadataErrorList, method = RequestMethod.GET)
    @ApiOperation(value = "导出异常详情列表")
    public void exprotMetadataErrorList( @ApiParam(name = "filters", value = "过滤")
                                         @RequestParam(value = "filters", required = false) String filters,
                                         @ApiParam(name = "sorts", value = "排序")
                                         @RequestParam(value = "sorts", required = false) String sorts,
                                         HttpServletResponse response){
        try {
            String fileName = "异常详情列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xlsx");
            OutputStream os = response.getOutputStream();
            //写excel
            SXSSFWorkbook  wwb = new SXSSFWorkbook(100);
            wwb.setCompressTempFiles(true);
            String[] title = {"接收时间","医疗机构","数据集","数据元","主键","错误原因"};
            int count = (int) elasticSearchUtil.count("json_archives_qc", "qc_metadata_info", filters);
            double pageNum = count % maxRowSize > 0 ? count / maxRowSize + 1 : count / maxRowSize;
            for (int i = 0; i < pageNum; i++) {
                List<Map<String, Object>> list = packQcReportService.metadataErrorList(filters,sorts,i+1,maxRowSize);
                //创建Excel工作表 指定名称和位置
                Sheet sheet = wwb.createSheet("Sheet" + (i+1));
                //添加固定信息，题头等
                Row titleRow = sheet.createRow(0);
                for (int t = 0; t < title.length; t++) {
                    Cell xcell = titleRow.createCell(t);
                    xcell.setCellValue(title[t] + "");
                }
                for (int j = 0; j < list.size(); j++) {
                    Row row = sheet.createRow(j+ 1);
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    row.createCell(0).setCellValue(ObjectUtils.toString(record.get("receive_date")));
                    row.createCell(1).setCellValue(ObjectUtils.toString(record.get("org_name")));
                    row.createCell(2).setCellValue(ObjectUtils.toString(record.get("dataset")));
                    row.createCell(3).setCellValue(ObjectUtils.toString(record.get("metadata")));
                    row.createCell(4).setCellValue(ObjectUtils.toString(record.get("_id")));
                    row.createCell(5).setCellValue(getExceptionType(ObjectUtils.toString(record.get("qc_error_type"))));
                }
            }
            wwb.write(os);
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = ServiceApi.DataQuality.ExportArchiveList, method = RequestMethod.GET)
    @ApiOperation(value = "导出档案包列表")
    public void exportArchiveList( @ApiParam(name = "filters", value = "过滤")
                                   @RequestParam(value = "filters", required = false) String filters,
                                   @ApiParam(name = "sorts", value = "排序")
                                   @RequestParam(value = "sorts", required = false) String sorts,
                                   HttpServletResponse response){
//        if(StringUtils.isNotEmpty(filters)){
//            filters+="archive_status=3;"+filters;
//        }else{
//            filters="archive_status=3";
//        }

        try {
            String fileName = "接收包列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xlsx");
            OutputStream os = response.getOutputStream();
            //写excel
            SXSSFWorkbook  wwb = new SXSSFWorkbook(100);
            wwb.setCompressTempFiles(true);

            String[] title = {"接收时间","解析状态","医疗机构","序列号","患者姓名","证件号","就诊时间","就诊类型"};

            int count = (int) elasticSearchUtil.count("json_archives", "info", filters);
            double pageNum = count % maxRowSize > 0 ? count / maxRowSize + 1 : count / maxRowSize;
            for (int i = 0; i < pageNum; i++) {
                List<Map<String, Object>> list = packQcReportService.archiveList(filters,sorts,i+1,maxRowSize);
                //创建Excel工作表 指定名称和位置
                Sheet sheet = wwb.createSheet("Sheet" + (i+1));
                //添加固定信息，题头等
                Row titleRow = sheet.createRow(0);
                for (int t = 0; t < title.length; t++) {
                    Cell xcell = titleRow.createCell(t);
                    xcell.setCellValue(title[t] + "");
                }
                for (int j = 0; j < list.size(); j++) {
                    Row row = sheet.createRow(j+ 1);
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    row.createCell(0).setCellValue(ObjectUtils.toString(record.get("receive_date")));
                    row.createCell(1).setCellValue(getAnalyzerStatus(ObjectUtils.toString(record.get("analyze_status"))));
                    row.createCell(2).setCellValue(ObjectUtils.toString(record.get("org_name")));
                    row.createCell(3).setCellValue(ObjectUtils.toString(record.get("_id")));
                    row.createCell(4).setCellValue(ObjectUtils.toString(record.get("patient_name")));
                    row.createCell(5).setCellValue(ObjectUtils.toString(record.get("demographic_id")));
                    row.createCell(6).setCellValue(ObjectUtils.toString(record.get("event_date")));
                    row.createCell(7).setCellValue(getEventType(ObjectUtils.toString(record.get("event_type"))));
                }
            }
            wwb.write(os);
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = ServiceApi.DataQuality.ExportUploadRecordList, method = RequestMethod.GET)
    @ApiOperation(value = "导出上传纪录列表")
    public void exportUploadRecordList( @ApiParam(name = "filters", value = "过滤")
                                        @RequestParam(value = "filters", required = false) String filters,
                                        @ApiParam(name = "sorts", value = "排序")
                                        @RequestParam(value = "sorts", required = false) String sorts,
                                        HttpServletResponse response){
        try {
            String fileName = "上传纪录列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xlsx");
            OutputStream os = response.getOutputStream();
            //写excel
            SXSSFWorkbook  wwb = new SXSSFWorkbook(100);
            wwb.setCompressTempFiles(true);

            String[] title = {"上传时间","接收平台","医疗机构","序列号","患者姓名","证件号","就诊时间","就诊类型","数据集数量"};

            int count = (int) elasticSearchUtil.count("upload", "record", filters);
            double pageNum = count % maxRowSize > 0 ? count / maxRowSize + 1 : count / maxRowSize;
            for (int i = 0; i < pageNum; i++) {
                List<Map<String, Object>> list = packQcReportService.uploadRecordList(filters,sorts,i+1,maxRowSize);
                //创建Excel工作表 指定名称和位置
                Sheet sheet = wwb.createSheet("Sheet" + (i+1));
                //添加固定信息，题头等
                Row titleRow = sheet.createRow(0);
                for (int t = 0; t < title.length; t++) {
                    Cell xcell = titleRow.createCell(t);
                    xcell.setCellValue(title[t] + "");
                }
                for (int j = 0; j < list.size(); j++) {
                    Row row = sheet.createRow(j+ 1);
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    row.createCell(0).setCellValue(ObjectUtils.toString(record.get("analyze_date")));
                    row.createCell(1).setCellValue(getPlatform(ObjectUtils.toString(record.get("to_platform"))));
                    row.createCell(2).setCellValue(ObjectUtils.toString(record.get("org_name")));
                    row.createCell(3).setCellValue(ObjectUtils.toString(record.get("_id")));
                    row.createCell(4).setCellValue(ObjectUtils.toString(record.get("patient_name")));
                    row.createCell(5).setCellValue(ObjectUtils.toString(record.get("idcard_no")));
                    row.createCell(6).setCellValue(ObjectUtils.toString(record.get("event_date")));
                    row.createCell(7).setCellValue(getEventType(ObjectUtils.toString(record.get("event_type"))));
                    row.createCell(8).setCellValue(ObjectUtils.toString(record.get("dataset_count")));
                }
            }
            wwb.write(os);
            wwb.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 添加单元格内容
     * @param ws
     * @param column
     * @param row
     * @param data
     */
    public void addCell(WritableSheet ws,int column,int row,String data){
        try {
            Label label = new Label(column,row,data);
            ws.addCell(label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加单元格内容带样式
     * @param ws
     * @param column
     * @param row
     * @param data
     * @param cellFormat
     */
    public void addCell(WritableSheet ws,int column,int row,String data,CellFormat cellFormat){
        try {
            Label label = new Label(column,row,data,cellFormat);
            ws.addCell(label);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 错误类型
     * @param errorType
     * @return
     */
    public String getErrorType(String errorType){
        String re = "";
        switch (errorType){
            case "-1":
                re = "质控服务内部出错";
                break;
            case "-2":
                re = "解析服务内部出错";
                break;
            case "1":
                re = "压缩包错误";
                break;
            case "2":
                re = "Json文件错误";
                break;
            case "3":
                re = "Json数据错误";
                break;
            case "4":
                re = "数据元非空错误";
                break;
            case "5":
                re = "数据元超出值域错误";
                break;
            case "6":
                re = "字段类型错误";
                break;
            case "7":
                re = "字段格式错误";
                break;
            case "21":
                re = "数据缓存错误";
                break;
            default:
                break;
        }
        return re;
    }

    /**
     * 异常类型
     * @param exceptionType
     * @return
     */
    public String getExceptionType(String exceptionType){
        String re = "";
        switch (exceptionType){
            case "1":
                re = "字段值为空";
                break;
            case "2":
                re = "值域超出";
                break;
            case "3":
                re = "类型错误";
                break;
            case "4":
                re = "格式错误";
                break;
            case "5":
                re = "资源适配错误";
                break;
            case "6":
                re = "字典适配错误";
                break;
            default:
                break;
        }
        return re;
    }
    /**
     * 解析状态
     * @param analyzerStatus
     * @return
     */
    public String getAnalyzerStatus(String analyzerStatus){
        String re = "";
        switch (analyzerStatus){
            case "0":
                re = "未解析";
                break;
            case "1":
                re = "正在解析";
                break;
            case "2":
                re = "解析失败";
                break;
            case "3":
                re = "解析完成";
                break;
            default:
                break;
        }
        return re;
    }

    /**
     * 就诊类型
     * @param eventType
     * @return
     */
    public String getEventType(String eventType){
        String re = "";
        switch (eventType){
            case "0":
                re = "门诊";
                break;
            case "1":
                re = "住院";
                break;
            case "2":
                re = "体检";
                break;
            default:
                break;
        }
        return re;
    }

    /**
     * 就诊类型
     * @param platform
     * @return
     */
    public String getPlatform(String platform){
        String re = "";
        switch (platform){
            case "10":
                re = "省平台";
                break;
            default:
                break;
        }
        return re;
    }
}
