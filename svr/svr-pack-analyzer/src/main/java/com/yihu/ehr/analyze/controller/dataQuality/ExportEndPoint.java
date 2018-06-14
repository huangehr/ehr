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

    @RequestMapping(value = ServiceApi.DataQuality.ExportQualityMonitoringListToExcel, method = RequestMethod.GET)
    @ApiOperation(value = "生成报告")
    public void exportQualityMonitoringListToExcel(
            @ApiParam(name = "reporter", value = "报告人", required = true)
            @RequestParam(name = "reporter") String reporter,
            @ApiParam(name = "orgInfoList", value = "机构编码、名称，例：[{\"orgName\":\"xx\",\"orgCode\":\"jkzl\"}]。", required = true)
            @RequestParam(name = "orgInfoList") String orgInfoList,
            @ApiParam(name = "eventDateStart", value = "就诊时间（起始），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateStart") String eventDateStart,
            @ApiParam(name = "eventDateEnd", value = "就诊时间（截止），格式 yyyy-MM-dd", required = true)
            @RequestParam(name = "eventDateEnd") String eventDateEnd,
            HttpServletRequest request,
            HttpServletResponse response) {
        OutputStream ostream = null;
        XWPFDocument document = null;
        try{
            String title = eventDateStart.replace("-", "") + "-" + eventDateEnd.replace("-", "")+"接收数据报告";
            response.setContentType("octets/stream");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String( title.getBytes("gb2312"), "ISO8859-1" )+".doc");

//            //输出文件
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/msword");//导出word格式
//            response.addHeader("Content-Disposition", "attachment;filename=" +
//                    new String((title + ".doc").getBytes(),"UTF-8"));

            eventDateStart = eventDateStart + " 00:00:00";
            eventDateEnd = eventDateEnd + " 23:59:59";
            JSONArray jsonArray = JSON.parseArray(orgInfoList);
            List<Map<String, String>> list = new ArrayList<>();
            for(int i=0;i<jsonArray.size();i++){
                JSONObject json = jsonArray.getJSONObject(i);
                Map<String, String> map = new HashedMap();
                map.put("orgCode",json.getString("orgCode"));
                map.put("orgName",json.getString("orgName"));
                list.add(map);
            }
            // 接收档案包总量
            Long receivedCount = dataQualityStatisticsService.packetCount(list, null, eventDateStart, eventDateEnd);
            // 成功解析档案包总量
            Long successfulAnalysisCount = dataQualityStatisticsService.packetCount(list, "3", eventDateStart, eventDateEnd);
            // 机构档案包报告汇总
            List<Map<String, Object>> orgPackReportDataList = dataQualityStatisticsService.orgPackReportData(list, eventDateStart, eventDateEnd);

            //设置word
            document = new XWPFDocument();
            //添加标题
            XWPFParagraph titleParagraph = document.createParagraph();
            //设置段落居中
            titleParagraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleParagraphRun = titleParagraph.createRun();
            titleParagraphRun.setText(title);
            titleParagraphRun.setColor("000000");
            titleParagraphRun.setFontSize(20);
            titleParagraphRun.setBold(true);
            addEmptyRow(document);
            //段落
            String reportDate = DateTimeUtil.simpleDateFormat(new Date());
            XWPFParagraph paragraph1 = document.createParagraph();
            XWPFRun run1 = paragraph1.createRun();
            String text1 = "                    统计时间:"+reportDate+"\n\r" +
                           "                    报告时间:"+reportDate+"\n\r" +
                           "                    报告人: "+reporter;
            run1.setText(text1);
            run1.setFontSize(12);
            addEmptyRow(document);
            XWPFParagraph paragraph2 = document.createParagraph();
            XWPFRun run2 = paragraph2.createRun();
            String text2 = "接收总量:"+receivedCount+"\n\r" +
                    "成功解析: "+successfulAnalysisCount;
            run2.setText(text2);
            run2.setFontSize(18);
            run2.setBold(true);
            addEmptyRow(document);
            int i =0;
            for (Map<String, Object> map:orgPackReportDataList){
                i++;
                XWPFParagraph orgParagraph1 = document.createParagraph();
                XWPFRun orgRun1 = orgParagraph1.createRun();
                String orgText1 = i+"."+map.get("orgCode")+"(" + map.get("orgName")+")";
                orgRun1.setText(orgText1);
                orgRun1.setFontSize(16);
                orgRun1.setBold(true);

                XWPFParagraph orgParagraph2 = document.createParagraph();
                XWPFRun orgRun2 = orgParagraph2.createRun();
                String orgText2 = i+".数据接收情况";
                orgRun2.setText(orgText2);
                orgRun2.setFontSize(12);
                orgRun2.setBold(true);

                int j = 1;
                XWPFParagraph orgParagraph3 = document.createParagraph();
                XWPFRun orgRun3 = orgParagraph3.createRun();
                String orgText3 = i+"."+j+"医院上报及采集情况";
                orgRun3.setText(orgText3);
                orgRun3.setFontSize(12);

                //设置表格
                XWPFTable table1 = document.createTable();
                //列宽自动分割
                CTTblWidth width1 = table1.getCTTbl().addNewTblPr().addNewTblW();
                width1.setType(STTblWidth.DXA);
                width1.setW(BigInteger.valueOf(9072));
                //表格第一行
                XWPFTableRow table11RowTitle = table1.getRow(0);
                table11RowTitle.getCell(0).setText("环节");
                table11RowTitle.addNewTableCell().setText("门诊档案数");
                table11RowTitle.addNewTableCell().setText("住院档案数");
                table11RowTitle.addNewTableCell().setText("体检档案数");
                table11RowTitle.addNewTableCell().setText("总计");
                List<Map<String, Object>> reportedNumList1 = (List<Map<String, Object>>)map.get("reportedNumList1");
                if(reportedNumList1.size()>0){
                    Map<String, Object> orgMap = reportedNumList1.get(0);
                    XWPFTableRow tableRow = table1.createRow();
                    tableRow.getCell(0).setText("医院上报");
                    tableRow.getCell(1).setText(Long.valueOf(orgMap.get("outpatientNum").toString())+"");
                    tableRow.getCell(2).setText(Long.valueOf(orgMap.get("hospitalDischargeNum").toString())+"");
                    tableRow.getCell(3).setText(Long.valueOf(orgMap.get("healthExaminationNum").toString())+"");
                    tableRow.getCell(4).setText(Long.valueOf(orgMap.get("total").toString())+"");
                }
                double receiveAcrhive = 0;
                Map<String, Object> collectionMap = (Map<String, Object>)map.get("collectionMap");
                if(collectionMap.size()>0){
                    XWPFTableRow tableRow = table1.createRow();
                    tableRow.getCell(0).setText("平台接收");
                    tableRow.getCell(1).setText(Long.valueOf(collectionMap.get("outpatientNum").toString())+"");
                    tableRow.getCell(2).setText(Long.valueOf(collectionMap.get("hospitalDischargeNum").toString())+"");
                    tableRow.getCell(3).setText(Long.valueOf(collectionMap.get("healthExaminationNum").toString())+"");
                    receiveAcrhive = Double.valueOf(collectionMap.get("total").toString());
                    tableRow.getCell(4).setText(Long.valueOf(collectionMap.get("total").toString())+"");
                }
                addEmptyRow(document);
                j++;
                XWPFParagraph orgParagraph4 = document.createParagraph();
                XWPFRun orgRun4 = orgParagraph4.createRun();
                String orgText4 = i+"."+j+"采集内容";
                orgRun4.setText(orgText4);
                orgRun4.setFontSize(12);

                //设置表格
                XWPFTable table2 = document.createTable();
                //列宽自动分割
                CTTblWidth width2 = table2.getCTTbl().addNewTblPr().addNewTblW();
                width2.setType(STTblWidth.DXA);
                width2.setW(BigInteger.valueOf(9072));
                //表格第一行
                XWPFTableRow table12RowTitle = table2.getRow(0);
                table12RowTitle.getCell(0).setText("日期");
                table12RowTitle.addNewTableCell().setText("门诊档案数");
                table12RowTitle.addNewTableCell().setText("住院档案数");
                table12RowTitle.addNewTableCell().setText("体检档案数");
                table12RowTitle.addNewTableCell().setText("总计");
                List<Map<String, Object>> reportedNumList3 = (List<Map<String, Object>>)map.get("reportedNumList3");
                if(reportedNumList3.size()>0){
                    reportedNumList3.forEach(item->{
                        XWPFTableRow tableRow = table2.createRow();
                        tableRow.getCell(0).setText(item.get("receiveDate").toString());
                        tableRow.getCell(1).setText(Long.valueOf(item.get("outpatientNum").toString())+"");
                        tableRow.getCell(2).setText(Long.valueOf(item.get("hospitalDischargeNum").toString())+"");
                        tableRow.getCell(3).setText(Long.valueOf(item.get("healthExaminationNum").toString())+"");
                        tableRow.getCell(4).setText(Long.valueOf(item.get("total").toString())+"");
                    });
                }
                addEmptyRow(document);
                i++;
                XWPFParagraph orgParagraph5 = document.createParagraph();
                XWPFRun orgRun5 = orgParagraph5.createRun();
                String orgText5 = i+".数据解析情况";
                orgRun5.setText(orgText5);
                orgRun5.setFontSize(12);
                orgRun5.setBold(true);
                //设置表格
                XWPFTable table3 = document.createTable();
                //列宽自动分割
                CTTblWidth width3 = table3.getCTTbl().addNewTblPr().addNewTblW();
                width3.setType(STTblWidth.DXA);
                width3.setW(BigInteger.valueOf(9072));
                //表格第一行
                XWPFTableRow table13RowTitle = table3.getRow(0);
                table13RowTitle.getCell(0).setText("接收总量");
                table13RowTitle.addNewTableCell().setText("解析成功");
                table13RowTitle.addNewTableCell().setText("解析失败");
                table13RowTitle.addNewTableCell().setText("未解析");
                Map<String, Object> archiveMap = (Map<String, Object>)map.get("archiveMap");
                if(collectionMap.size()>0){
                    XWPFTableRow tableRow = table3.createRow();
                    tableRow.getCell(0).setText(Long.valueOf(receiveAcrhive+"")+"");//0未解析 1正在解析 2解析失败 3解析完成
                    tableRow.getCell(1).setText(Long.valueOf(archiveMap.get("archive_status3").toString())+"");
                    tableRow.getCell(2).setText(Long.valueOf(archiveMap.get("archive_status2").toString())+"");
                    tableRow.getCell(3).setText(Long.valueOf(archiveMap.get("archive_status0").toString())+"");
                }
                addEmptyRow(document);
                i++;
                XWPFParagraph orgParagraph6 = document.createParagraph();
                XWPFRun orgRun6 = orgParagraph6.createRun();
                String orgText6 = i+".数据集总量{解析完成}";
                orgRun6.setText(orgText6);
                orgRun6.setFontSize(12);
                orgRun6.setBold(true);
                //设置表格
                XWPFTable table4 = document.createTable();
                //列宽自动分割
                CTTblWidth width4 = table4.getCTTbl().addNewTblPr().addNewTblW();
                width4.setType(STTblWidth.DXA);
                width4.setW(BigInteger.valueOf(9072));
                //表格第一行
                XWPFTableRow table14RowTitle = table4.getRow(0);
                table14RowTitle.getCell(0).setText("数据集编码");
                table14RowTitle.addNewTableCell().setText("数据集名称");
                table14RowTitle.addNewTableCell().setText("总数");
                table14RowTitle.addNewTableCell().setText("行数");
                List<Map<String, Object>> reportedNumList5 = (List<Map<String, Object>>)map.get("reportedNumList5");
                if(reportedNumList5.size()>0){
                    reportedNumList5.forEach(item->{
                        XWPFTableRow tableRow = table4.createRow();
                        tableRow.getCell(0).setText(item.get("dataset").toString());
                        tableRow.getCell(1).setText(item.get("name").toString());
                        tableRow.getCell(2).setText(item.get("count").toString());
                        tableRow.getCell(3).setText(item.get("row").toString());
                    });
                }
                addEmptyRow(document);
                i++;
                XWPFParagraph orgParagraph7 = document.createParagraph();
                XWPFRun orgRun7 = orgParagraph7.createRun();
                String orgText7 = i+".分析";
                orgRun7.setText(orgText7);
                orgRun7.setFontSize(12);
                orgRun7.setBold(true);
                XWPFParagraph orgParagraph8 = document.createParagraph();
                XWPFRun orgRun8 = orgParagraph8.createRun();
                String orgText8 = i+".1解析失败分析";
                orgRun8.setText(orgText8);
                orgRun8.setFontSize(12);
                //设置表格
                XWPFTable table5 = document.createTable();
                //列宽自动分割
                CTTblWidth width5 = table5.getCTTbl().addNewTblPr().addNewTblW();
                width5.setType(STTblWidth.DXA);
                width5.setW(BigInteger.valueOf(9072));
                //表格第一行
                XWPFTableRow table15RowTitle = table5.getRow(0);
                table15RowTitle.getCell(0).setText("错误原因");
                table15RowTitle.addNewTableCell().setText("数量");
                List<Map<String, Object>> reportedNumList6 = (List<Map<String, Object>>)map.get("reportedNumList6");
                if(reportedNumList6.size()>0){
                    reportedNumList6.forEach(item->{
                        XWPFTableRow tableRow = table5.createRow();
                        tableRow.getCell(0).setText(getErrorType(item.get("error_type").toString()));
                        tableRow.getCell(1).setText(item.get("error_count").toString());
                    });
                }
            }
            ostream = response.getOutputStream();
            document.write(ostream);
            ostream.flush();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(document!=null){
                try {
                    document.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            if(ostream!=null){
                try {
                    ostream.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 添加空行
     * @param document
     */
    private void addEmptyRow (XWPFDocument document){
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setText("\r");
    }

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
}
