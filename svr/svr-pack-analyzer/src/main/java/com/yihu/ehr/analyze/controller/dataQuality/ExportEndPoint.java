package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.DataQualityStatisticsService;
import com.yihu.ehr.analyze.service.dataQuality.WarningRecordService;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.write.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

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
    private DataQualityStatisticsService dataQualityStatisticsService;
    @Autowired
    private PackQcReportService packQcReportService;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

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
    @ApiOperation(value = "导出平台接收列表")
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
        int pageSize=1000;
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
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xls");
            OutputStream os = response.getOutputStream();
            //写excel
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            //创建Excel工作表 指定名称和位置
            WritableSheet ws = wwb.createSheet(fileName,0);
            //添加固定信息，题头等
            addCell(ws,0,0,"解析时间");
            addCell(ws,1,0,"接收时间");
            addCell(ws,2,0,"医疗机构");
            addCell(ws,3,0,"序列号");
            addCell(ws,4,0,"失败原因");
            WritableCellFormat wc = new WritableCellFormat();
            wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
            int count = (int) elasticSearchUtil.count("json_archives", "info", filters);
            int totalPage = count/pageSize+1;
            for(int p=0;p<totalPage;p++) {
                List<Map<String, Object>> list = packQcReportService.analyzeErrorList(filters,sorts,p+1,pageSize);
                for (int i = 0; i < list.size(); i++) {
                    int j = p*pageSize + i + 1;
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    addCell(ws, 0, j, ObjectUtils.toString(record.get("analyze_date")), wc);
                    addCell(ws, 1, j, ObjectUtils.toString(record.get("receive_date")), wc);
                    addCell(ws, 2, j, ObjectUtils.toString(record.get("org_name")), wc);
                    addCell(ws, 3, j, ObjectUtils.toString(record.get("_id")), wc);
                    addCell(ws, 4, j, getErrorType(ObjectUtils.toString(record.get("error_type"))), wc);
                }
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

    @RequestMapping(value = ServiceApi.DataQuality.ExportMetadataErrorList, method = RequestMethod.GET)
    @ApiOperation(value = "导出异常详情列表")
    public void exprotMetadataErrorList( @ApiParam(name = "filters", value = "过滤")
                                        @RequestParam(value = "filters", required = false) String filters,
                                        @ApiParam(name = "sorts", value = "排序")
                                        @RequestParam(value = "sorts", required = false) String sorts,
                                        HttpServletResponse response){
        int pageSize=1000;
        try {
            String fileName = "异常详情列表";
            //设置下载
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( fileName.getBytes("gb2312"), "ISO8859-1" )+".xls");
            OutputStream os = response.getOutputStream();
            //写excel
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            //创建Excel工作表 指定名称和位置
            WritableSheet ws = wwb.createSheet(fileName,0);
            //添加固定信息，题头等
            addCell(ws,0,0,"接收时间");
            addCell(ws,1,0,"医疗机构");
            addCell(ws,2,0,"数据集");
            addCell(ws,3,0,"数据元");
            addCell(ws,4,0,"主键");
            addCell(ws,5,0,"错误原因");
            WritableCellFormat wc = new WritableCellFormat();
            wc.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.SKY_BLUE);//边框
            int count = (int) elasticSearchUtil.count("json_archives_qc", "qc_metadata_info", filters);
            int totalPage = count/pageSize+1;
            for(int p=0;p<totalPage;p++) {
                List<Map<String, Object>> list = packQcReportService.metadataErrorList(filters,sorts,p+1,pageSize);
                for (int i = 0; i < list.size(); i++) {
                    int j = p*pageSize + i + 1;
                    Map<String, Object> record = list.get(i);
                    //添加列表明细
                    addCell(ws, 0, j, ObjectUtils.toString(record.get("receive_date")), wc);
                    addCell(ws, 1, j, ObjectUtils.toString(record.get("org_name")), wc);
                    addCell(ws, 2, j, ObjectUtils.toString(record.get("dataset")), wc);
                    addCell(ws, 3, j, ObjectUtils.toString(record.get("metadata")), wc);
                    addCell(ws, 4, j, ObjectUtils.toString(record.get("_id")), wc);
                    addCell(ws, 5, j, getExceptionType(ObjectUtils.toString(record.get("qc_error_type"))), wc);
                }
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
                re = "未定义";
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
            case "21":
                re = "压缩包错误";
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
}
