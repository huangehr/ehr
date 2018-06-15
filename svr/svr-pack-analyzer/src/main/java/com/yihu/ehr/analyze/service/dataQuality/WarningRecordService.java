package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.analyze.dao.DqWarningRecordDao;
import com.yihu.ehr.entity.quality.DqWarningRecord;
import com.yihu.ehr.profile.qualilty.DqWarningRecordSolveType;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import jxl.format.CellFormat;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 预警问题查询
 * @author yeshijie on 2018/6/11.
 */
@Service
public class WarningRecordService extends BaseJpaService<DqWarningRecord, DqWarningRecordDao> {

    @Autowired
    private WarningSettingService warningSettingService;
    @Autowired
    private DqWarningRecordDao dqWarningRecordDao;

    public DqWarningRecord findById(String id) {
        return dqWarningRecordDao.findOne(id);
    }

    /**
     * 处理问题
     * @param solveTime
     * @param solveId
     * @param solveName
     * @param solveType
     * @param id
     */
    public int warningRecordUpd(String solveTime,String solveId,String solveName,String solveType,String id){
        DqWarningRecord record = dqWarningRecordDao.findOne(id);
        if(record == null){
            return -1;
        }
        record.setStatus("2");
        record.setSolveId(solveId);
        record.setSolveName(solveName);
        record.setSolveTime(DateUtil.formatCharDateYMD(solveTime));
        record.setSolveType(solveType);
        dqWarningRecordDao.save(record);
        return 0;
    }

    /**
     * 添加行
     * @param type
     * @param wc
     * @param ws
     * @param record
     * @param j
     */
    public void addRow(String type,WritableCellFormat wc,WritableSheet ws,DqWarningRecord record,int j){
        try {
            if("1".equals(type)||"3".equals(type)){
                addCell(ws,0,j,DateUtil.toString(record.getWarningTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//预警时间
                addCell(ws,1,j,DateUtil.toString(record.getRecordTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//接收日期/上传时间
                addCell(ws,2,j,record.getOrgName(),wc);//医疗机构
                addCell(ws,3,j,record.getProblemDescription(),wc);//问题类型
                addCell(ws,4,j,getStatusName(record.getStatus()),wc);//状态
                addCell(ws,5,j,record.getQuota(),wc);//指标
                addCell(ws,6,j,record.getActualValue(),wc);//值
                addCell(ws,7,j,record.getWarningValue(),wc);//预警值
                addCell(ws,8,j,DateUtil.toString(record.getSolveTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//处理时间
                addCell(ws,9,j,getSolveTypeName(record.getSolveType()),wc);//处理结果
                addCell(ws,10,j,record.getSolveName(),wc);//操作人
            }else if("2".equals(type)){
                addCell(ws,0,j,DateUtil.toString(record.getWarningTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//预警时间
                addCell(ws,1,j,DateUtil.toString(record.getRecordTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//资源化时间
                addCell(ws,2,j,record.getProblemDescription(),wc);//问题类型
                addCell(ws,3,j,getStatusName(record.getStatus()),wc);//状态
                addCell(ws,4,j,record.getQuota(),wc);//指标
                addCell(ws,5,j,record.getActualValue(),wc);//值
                addCell(ws,6,j,record.getWarningValue(),wc);//预警值
                addCell(ws,7,j,DateUtil.toString(record.getSolveTime(),DateUtil.DEFAULT_DATE_YMD_FORMAT),wc);//处理时间
                addCell(ws,8,j,getSolveTypeName(record.getSolveType()),wc);//处理结果
                addCell(ws,9,j,record.getSolveName(),wc);//操作人
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 状态翻译
     * @param status
     * @return
     */
    public String getStatusName(String status){
        String re = "";
        switch (status){
            case "1":
                re = "未解决";
                break;
            case "2":
                re = "已解决";
                break;
            default:
                break;
        }
        return re;
    }

    /**
     * 解决方式翻译
     * @param solveType
     * @return
     */
    public String getSolveTypeName(String solveType){
        if(StringUtils.isBlank(solveType)){
            return "";
        }
        String re = "";
        switch (solveType){
            case "1":
                re = DqWarningRecordSolveType.solved.getName();
                break;
            case "2":
                re = DqWarningRecordSolveType.ignore.getName();
                break;
            case "3":
                re = DqWarningRecordSolveType.unSolve.getName();
                break;
            case "4":
                re = DqWarningRecordSolveType.notProblem.getName();
                break;
            default:
                break;
        }
        return re;
    }

    /**
     * 接收
     * excel中添加固定内容
     * @param ws
     */
    public void addReceiveStaticCell(WritableSheet ws){
        try {
            addCell(ws,0,0,"预警时间");
            addCell(ws,1,0,"接收日期");
            addCell(ws,2,0,"医疗机构");
            addCell(ws,3,0,"问题类型");
            addCell(ws,4,0,"状态");
            addCell(ws,5,0,"指标");
            addCell(ws,6,0,"值");
            addCell(ws,7,0,"预警值");
            addCell(ws,8,0,"处理时间");
            addCell(ws,9,0,"处理结果");
            addCell(ws,10,0,"操作人");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 资源化
     * excel中添加固定内容
     * @param ws
     */
    public void addResourceStaticCell(WritableSheet ws){
        try {
            addCell(ws,0,0,"预警时间");
            addCell(ws,1,0,"资源化时间");
            addCell(ws,2,0,"问题类型");
            addCell(ws,3,0,"状态");
            addCell(ws,4,0,"指标");
            addCell(ws,5,0,"值");
            addCell(ws,6,0,"预警值");
            addCell(ws,7,0,"处理时间");
            addCell(ws,8,0,"处理结果");
            addCell(ws,9,0,"操作人");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 平台上传
     * excel中添加固定内容
     * @param ws
     */
    public void addUploadStaticCell(WritableSheet ws){
        try {
            addCell(ws,0,0,"预警时间");
            addCell(ws,1,0,"上传时间");
            addCell(ws,2,0,"机构");
            addCell(ws,3,0,"问题类型");
            addCell(ws,4,0,"状态");
            addCell(ws,5,0,"指标");
            addCell(ws,6,0,"值");
            addCell(ws,7,0,"预警值");
            addCell(ws,8,0,"处理时间");
            addCell(ws,9,0,"处理结果");
            addCell(ws,10,0,"操作人");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addQualityMonitoringCell(WritableSheet ws){
        try {
            addCell(ws,0,0,"机构");
            addCell(ws,1,0,"医院档案数");
            addCell(ws,2,0,"医院数据集");
            addCell(ws,3,0,"接收档案数");
            addCell(ws,4,0,"接收数据集");
            addCell(ws,5,0,"接收质量异常数");
            addCell(ws,6,0,"资源化解析成功");
            addCell(ws,7,0,"资源化解析失败");
            addCell(ws,8,0,"资源化解析异常");
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
}
