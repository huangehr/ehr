package com.yihu.ehr.analyze.service.dataQuality;


import com.yihu.ehr.analyze.dao.DqPaltformReceiveWarningDao;
import com.yihu.ehr.analyze.service.pack.PackQcReportService;
import com.yihu.ehr.elasticsearch.ElasticSearchPool;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqPaltformReceiveWarning;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.schema.AddressDictSchema;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.*;

/**
 *  质控管理- 首页逻辑类
 * @author HZY
 * @created 2018/8/17 11:24
 */
@Service
public class DataQualityHomeService extends BaseJpaService {

    private final static Logger logger = LoggerFactory.getLogger(DataQualityHomeService.class);
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private ElasticSearchPool elasticSearchPool;
    @Autowired
    private WarningSettingService warningSettingService;
    @Autowired
    private DqPaltformReceiveWarningDao dqPaltformReceiveWarningDao;
    @Autowired
    private PackQcReportService packQcReportService;
    @Value("${quality.orgCode}")
    private String defaultOrgCode;
    @Value("${quality.cloud}")
    private String cloud;
    @Value("${quality.cloudName}")
    private String cloudName;
    @Autowired
    private AddressDictSchema addressDictSchema;


    public Map<String, Object> getOrgMap() {
        //初始化 数据集数据
        Session session = currentSession();
        //获取医院数据
        Query query1 = session.createSQLQuery("SELECT org_code,full_name from organizations where org_type = 'Hospital' ");
        List<Object[]> orgList = query1.list();
        Map<String, Object> orgMap = new HashedMap();
        orgList.forEach(one -> {
            String orgCode = one[0].toString();
            String name = one[1].toString();
            orgMap.put(orgCode, name);
        });
        return orgMap;
    }


    /**
     * 初始化datamap 数据
     *
     * @param datasetMap
     * @param orgCode
     * @return
     */
    private Map initDataMap(Map<String, Object> datasetMap, Object orgName, String orgCode) {
        Map dataMap = new HashedMap();
        dataMap.put("orgCode", orgCode);//机构code
        dataMap.put("orgName", orgName);//机构名称
        dataMap.put("hospitalArchives", 0);//医院档案数
        dataMap.put("hospitalDataset", 0);//医院数据集
        dataMap.put("receiveArchives", 0);//接收档案数
        dataMap.put("receiveDataset", 0); //接收数据集
        dataMap.put("receiveException", 0);//接收异常
        dataMap.put("resourceSuccess", 0);//资源化-成功
        dataMap.put("resourceFailure", 0);//资源化-失败
        dataMap.put("resourceException", 0);//资源化-异常
        if (datasetMap.containsKey(orgCode)) {
            dataMap.put("hospitalDataset", datasetMap.get(orgCode));
        } else {
            dataMap.put("hospitalDataset", datasetMap.get(defaultOrgCode));
        }
        return dataMap;
    }

    /**
     * 初始化ratemap 数据
     *
     * @param warningMap
     * @param orgCode
     * @return
     */
    private Map initRateMap(Map<String, DqPaltformReceiveWarning> warningMap, Object orgName, String orgCode) {
        Map dataMap = new HashedMap();
        dataMap.put("orgCode", orgCode);//机构code
        dataMap.put("orgName", orgName);//机构名称
        dataMap.put("outpatientInTime", 0);//门诊及时数
        dataMap.put("hospitalInTime", 0);//住院及时数
        dataMap.put("peInTime", 0);//体检及时数
        dataMap.put("outpatientIntegrity", 0);//门诊完整数
        dataMap.put("hospitalIntegrity", 0);//住院完整数
        dataMap.put("peIntegrity", 0);//体检完整数
        dataMap.put("totalVisit", 0);//总就诊数
        dataMap.put("totalOutpatient", 0);//总门诊数
        dataMap.put("totalPe", 0);//总体检数
        dataMap.put("totalHospital", 0);//总住院数
        return dataMap;
    }

    /**
     * 平台就诊人数 去重复(完整人数)  档案完整性
     * @param dateField 时间区间查询字段
     * @param start
     * @param end
     * @param orgCode
     * @return
     */
    public void getPatientCount(String dateField,String start, String end, String orgCode, Map<String, Object> map) throws Exception {
        try {
            long starttime = System.currentTimeMillis();
            String sql0 = "";
            String sql1 = "";
            String sql2 = "";
            String sql3 = "";
            if (StringUtils.isNotEmpty(orgCode)) {
                sql0 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=2 AND pack_type=1 AND org_code='" + orgCode + "' AND " + dateField + " BETWEEN" +
                        " '" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND org_code='" + orgCode + "' AND " + dateField + " BETWEEN" +
                        " '" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND org_code='" + orgCode + "' AND " + dateField + " BETWEEN " +
                        "'" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND org_code='" + orgCode + "' AND " + dateField + " BETWEEN " +
                        "'" + start + " 00:00:00' AND '" + end + " 23:59:59'";
            } else {
                sql0 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=2 AND pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59'";

                sql3 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE pack_type=1 AND " + dateField +
                        " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59'";
            }
            ResultSet resultSet0 = elasticSearchUtil.findBySql(sql0);
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            ResultSet resultSet3 = elasticSearchUtil.findBySql(sql3);
            resultSet0.next();
            resultSet1.next();
            resultSet2.next();
            resultSet3.next();
            map.put("peIntegrity", new Double(resultSet0.getObject("COUNT(DISTINCT event_no)").toString()).intValue());//体检
            map.put("hospitalIntegrity", new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString()).intValue());//住院
            map.put("outpatientIntegrity", new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString()).intValue());//门诊
            map.put("visitIntegrity", new Double(resultSet3.getObject("COUNT(DISTINCT event_no)").toString()).intValue());//就诊
            logger.info("平台就诊人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 百分比计算
     *
     * @param molecular   分子
     * @param denominator 分母
     * @return
     */
    public String calRate(double molecular, double denominator) {
        if (molecular == 0) {
            return "0.00%";
        } else if (denominator == 0) {
            return "100.00%";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        return decimalFormat.format(molecular / denominator);
    }

    /**
     *  百分比计算
     * @param molecular    分子
     * @param denominator 分母
     * @return
     */
    public String calRate(Integer molecular, Integer denominator) {
        if (molecular == 0) {
            return "0.00%";
        } else if (denominator == 0) {
            return "100.00%";
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        return decimalFormat.format(molecular / denominator);
    }


    private List getPageList(int pageNum, int pageSize, List data) {
        int fromIndex = (pageNum - 1) * pageSize;
        if (fromIndex >= data.size()) {
            return Collections.emptyList();
        }

        int toIndex = pageNum * pageSize;
        if (toIndex >= data.size()) {
            toIndex = data.size();
        }
        return data.subList(fromIndex, toIndex);
    }


    /**
     * 质控情况 -  总计 （及时率，完整率，准确率）
     *
     * @param start 就诊时间- 起始
     * @param end   就诊时间- 截止
     *              【目前取的是平台全部机构，若做多区域平台的话，需要添加区域参数】
     */
    public Map<String, Object> getQuailyDetail(String start, String end) throws Exception {
        Map<String, Object> totalMap = new HashMap<>();
        List<Map<String, Object>> archiveMapList = new ArrayList<>();
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }

        //初始化 及时率预警信息
        DqPaltformReceiveWarning warning = dqPaltformReceiveWarningDao.findByOrgCode(defaultOrgCode);

        int totalHospitalNum = 0;//医院总就诊数
        double totalOutpatientNum = 0;//总门诊数
        double totalExamNum = 0;//总体检数
        double totalInpatientNum = 0;//总住院数
        double totalInTime = 0;//总及时数
        double totalVisitNum = 0;//总完整数（平台总就诊数）
        double totalCorrect = 0;//总准确数

        //1. 获取医院档案量;
        Envelop envelop = packQcReportService.dailyReport("create_date", start + "T00:00:00Z", end + "T23:59:59Z", null);
        Map<String, Object> hospitalDataMap = (Map<String, Object>) envelop.getDetailModelList().get(0);
        //医院总数据量
        totalHospitalNum = (int) hospitalDataMap.get("total");
        Map<String, Object> dataMap = new HashMap<>();
        //2. 平台就诊完整数
        getPatientCount("receive_date",start, end, null, dataMap);
        totalOutpatientNum = Double.valueOf(dataMap.get("outpatientIntegrity").toString());//门诊完整数
        totalInpatientNum = Double.valueOf(dataMap.get("hospitalIntegrity").toString());//住院完整数
        totalExamNum = Double.valueOf(dataMap.get("peIntegrity").toString());//体检完整数
        totalVisitNum = Double.valueOf(dataMap.get("visitIntegrity").toString());//就诊完整数
        //3. 及时数
        totalInTime = getInTimeNum("receive_date" ,start, end, warning);
        // 3. 准确数
        totalCorrect = getErrorDataSetData("receive_date",start, end, null);
        //4. 数据集总条数
        int dataSetsMun = getDataSetsMap(start, end, null);

        totalMap.put("orgCode", cloud);//机构code
        totalMap.put("orgName", cloudName);//机构名称
        totalMap.put("totalInTime", totalInTime);//及时数
        totalMap.put("totalComplete", totalVisitNum);//总就诊完整数
        totalMap.put("totalCorrect", totalCorrect);//准确数
        totalMap.put("totalHospital", totalHospitalNum);//医院总就诊数
        totalMap.put("totalDataSet", dataSetsMun);//数据集总行数
        //计算及时率及完整率,准确性
        totalMap.put("inTimeRate", calRate(totalInTime, totalHospitalNum));//及时率
        totalMap.put("completeRate", calRate(totalVisitNum, totalHospitalNum));//完整率
        totalMap.put("correctRate", calRate(totalCorrect, dataSetsMun));//数据集准确率

        Map<String, Object> outPatientMap = genVisitMap("outPatient", totalOutpatientNum, totalVisitNum);
        Map<String, Object> inPatientMap = genVisitMap("inPatient", totalInpatientNum, totalVisitNum);
        Map<String, Object> examPatientMap = genVisitMap("exam", totalExamNum, totalVisitNum);
        archiveMapList.add(outPatientMap);
        archiveMapList.add(inPatientMap);
        archiveMapList.add(examPatientMap);
        //档案包采集情况
        totalMap.put("rate", archiveMapList);
        return totalMap;
    }


    /**
     * 获取质控错误 - 数据集总条数
     * @param dateField  时间区间查询字段
     * @param start
     * @param end
     * @param orgCode
     * @return
     */
    public double getErrorDataSetData(String dateField,String start, String end, String orgCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        double num1 = 0;
        try {
            String dateStr = DateUtil.toString(new Date());
            if (StringUtils.isBlank(start)) {
                start = dateStr;
            }
            if (StringUtils.isBlank(end)) {
                end = dateStr;
            }
            List<String> fields = new ArrayList<String>();
            fields.add("dataset");
            fields.add("count");
            String sql1 = "";
            if (StringUtils.isNotEmpty(orgCode)) {
                sql1 = "SELECT dataset,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where org_code='" + orgCode + "' " +
                        dateField + ">='" + start + " 00:00:00' and "+ dateField + "<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by dataset";
            } else {
                sql1 = "SELECT dataset,count(DISTINCT event_no) as count from json_archives_qc/qc_metadata_info where " +
                        dateField +">='" + start + " 00:00:00' and "+dateField+"<='" + end + " 23:59:59' and (qc_step=1 or qc_step=2) group by dataset";
            }

            List<Map<String, Object>> list1 = elasticSearchUtil.findBySql(fields, sql1);
            if (list1 != null && list1.size() > 0) {
                for (Map<String, Object> map1 : list1) {
                    num1 += (double) map1.get("count");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            num1 = 0;
        }
        return num1;
    }


    /**
     * 获取数据集总量
     *
     * @param start
     * @param end
     * @param orgCode
     * @return
     */
    public int getDataSetsMap(String start, String end, String orgCode) throws IOException {
        // TODO 数据集总量
        int totalNum = 0;
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append("receive_date>=").append(start).append(" 00:00:00").append(";");
        stringBuilder1.append("receive_date<=").append(end).append(" 23:59:59").append(";");
        if (StringUtils.isNotBlank(orgCode)) {
            stringBuilder1.append("org_code=" + orgCode);
        }
        List<Map<String, Object>> dataSets = elasticSearchUtil.list("json_archives_qc", "qc_dataset_detail", stringBuilder1.toString());
        for (Map<String, Object> dataSet : dataSets) {
            for (Map.Entry<String, Object> entry : dataSet.entrySet()) {
                totalNum += (Integer) dataSet.get("row");
            }
        }
        return totalNum;
    }

    /**
     *  获取及时上传数
     * @param dateField  时间区间查询字段
     * @param start
     * @param end
     * @param warning  预警信息
     * @return
     */
    public double getInTimeNum(String dateField,String start, String end, DqPaltformReceiveWarning warning) {
        double totalInTime = 0;
        //及时率
        try {
            long starttime = System.currentTimeMillis();
            String sql0 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=2 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getPeInTime();

            String sql1 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=1 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getHospitalInTime();

            String sql2 = "SELECT COUNT(DISTINCT event_no) FROM json_archives WHERE event_type=0 AND pack_type=1 AND " + dateField +
                    " BETWEEN '" + start + " 00:00:00' AND '" + end + " 23:59:59' and delay <=" + warning.getOutpatientInTime();

            ResultSet resultSet0 = elasticSearchUtil.findBySql(sql0);
            ResultSet resultSet1 = elasticSearchUtil.findBySql(sql1);
            ResultSet resultSet2 = elasticSearchUtil.findBySql(sql2);
            resultSet0.next();
            resultSet1.next();
            resultSet2.next();
            double outpatientInTime = new Double(resultSet2.getObject("COUNT(DISTINCT event_no)").toString());//门诊及时数
            double inpatientInTime = new Double(resultSet1.getObject("COUNT(DISTINCT event_no)").toString());//住院及时数
            double examInTime = new Double(resultSet0.getObject("COUNT(DISTINCT event_no)").toString());//体检及时数
            totalInTime = outpatientInTime + inpatientInTime + examInTime; // //就诊及时性
            logger.info("平台就诊及时人数 去重复：" + (System.currentTimeMillis() - starttime) + "ms");
        } catch (Exception e) {
            if (!"Error".equals(e.getMessage())) {
                e.printStackTrace();
            }
        }
        return totalInTime;
    }

    public Map<String, Object> genVisitMap(String typeField, double value, double total) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", typeField);
        map.put("value", value);
        map.put("rate", calRate(value,total));
        return map;
    }

    /**
     *  获取市区域的下级区域质控情况
     * @param dataType 数据维度  （及时性，完整性，准确性）
     * @param areaCode 上区域编码
     * @param start
     * @param end
     * @return
     */
    public List<Map<String,Object>> getAreaData(String dataType ,String areaCode,String start,String end) throws Exception {
        List<Map<String,Object>> list = new ArrayList<>();
        String dateStr = DateUtil.toString(new Date());
        if (StringUtils.isBlank(start)) {
            start = dateStr;
        }
        if (StringUtils.isBlank(end)) {
            end = dateStr;
        }

        switch (dataType) {
            case "complete" :
                //完整性
                break;
            case "inTime" :
                //及时性
                break;
            case "correct" :
                //准确性
                break;
                default:break;
        }

        return list;
    }


    public List<Map<String,Object>> getHospitalDataByGroup(String startDate, String endDate, String orgCode,String groupField) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(count) as count ,SUM(row) as row, dataset_name, dataset ");
        sql.append("FROM json_archives_qc/qc_dataset_detail");
        sql.append(" WHERE receive_date>='" + startDate + " 00:00:00' and receive_date<='" + endDate + " 23:59:59'");
        if (StringUtils.isNotEmpty(orgCode) && !"null".equals(orgCode)&&!cloud.equals(orgCode)){
            sql.append(" and org_code='" + orgCode +"'");
        }
        sql.append("GROUP BY dataset_name,dataset");
        List<String> field = new ArrayList<>();
        field.add("count");
        field.add("row");
        field.add("dataset_name");
        field.add("dataset");
        List<Map<String,Object>> list = elasticSearchUtil.findBySql(field, sql.toString());
        Map<String, Object> totalMap = new HashMap<>();
        totalMap.put("dataset","总计");
        totalMap.put("dataset_name","-");
        double rowTotal = 0;
        double countTotal = 0;
        for(Map<String,Object> map :list){
            map.put("name" ,map.get("dataset_name"));
            rowTotal += Double.valueOf(map.get("row").toString());
            countTotal += Double.valueOf(map.get("count").toString());
        }
        totalMap.put("row",rowTotal);
        totalMap.put("count",countTotal);
        list.add(0,totalMap);
        return list;
    }

    /**
     * 【完整性】 获取区域数据 - 行政区号分组
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Envelop areaDataComplete(String startDate, String endDate) throws Exception{
        Envelop envelop = new Envelop();
        String end = DateUtil.addDate(1, endDate,DateUtil.DEFAULT_DATE_YMD_FORMAT);
        Map<String,Object> resMap = null;
        List<Map<String,Object>> list = new ArrayList<>();
        //机构数据
        List<Map<String,Object>> groupList = getOrgDataGroup(startDate,end);
        //平台接收数据量
        Map<String, Object> platformDataGroup = getPlatformDataGroup(startDate, end);
        // 计算
        for (Map<String,Object> map:groupList){
            resMap = new HashMap<String,Object>();
            Integer platPormNum = Integer.parseInt(platformDataGroup.get("org_area").toString());
            Integer orgNum = Integer.parseInt(map.get("count").toString());
            String rate = calRate(platPormNum,orgNum);
            resMap.put("org_area",map.get("org_area"));
            resMap.put("count",platPormNum);
            resMap.put("total",orgNum);
            resMap.put("rate",rate);
            list.add(resMap);
        }
        envelop.setDetailModelList(list);
        envelop.setSuccessFlg(true);
        return envelop;
    }

    /**
     * 获取平台区域分组档案数据量 - （区域编码分组）
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public Map<String,Object> getPlatformDataGroup(String startDate,String endDate) throws Exception {
        Map<String,Object> resMap = new HashMap<>();
        //平台数据
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(row) as count ,org_area ");
        sql.append("FROM json_archives_qc/qc_dataset_detail");
        sql.append(" WHERE receive_date>='" + startDate + " 00:00:00' and receive_date<='" + endDate + " 23:59:59'");
        sql.append("GROUP BY org_area");
        List<String> field2 = new ArrayList<>();
        field2.add("count");
        field2.add("org_area");
        List<Map<String,Object>> platformList = elasticSearchUtil.findBySql(field2, sql.toString());
        for (Map<String,Object> map : platformList){
            resMap.put(map.get("org_area").toString(),map.get("count"));
        }
        return resMap;
    }

    /**
     *  获取机构采集- 区域分组数据量 - （区域编码分组）
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public  List<Map<String,Object>> getOrgDataGroup(String startDate,String endDate) throws Exception {
        //机构数据
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT SUM(HSI07_01_001) as count ,org_area ");
        sql.append("FROM qc/daily_report");
        sql.append(" WHERE create_date>='" + startDate + "T00:00:00Z' and create_date<='" + endDate + "T23:59:59Z'");
        sql.append("GROUP BY org_area");
        List<String> field = new ArrayList<>();
        field.add("count");
        field.add("org_area");
        List<Map<String,Object>> groupList = elasticSearchUtil.findBySql(field, sql.toString());
        return groupList;
    }


}
