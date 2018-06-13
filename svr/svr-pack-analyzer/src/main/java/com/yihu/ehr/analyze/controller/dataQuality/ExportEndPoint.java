package com.yihu.ehr.analyze.controller.dataQuality;

import com.yihu.ehr.analyze.service.dataQuality.WarningRecordService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import jxl.Workbook;
import jxl.write.Colour;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
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

    @RequestMapping(value = ServiceApi.DataQuality.ExportWarningRecordToExcel, method = RequestMethod.POST)
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


}
