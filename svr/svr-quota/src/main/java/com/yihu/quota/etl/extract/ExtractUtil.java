package com.yihu.quota.etl.extract;

import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.OrgHealthCategoryShowModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by janseny on 2017/7/10.
 */
@Component
@Scope("prototype")
public class ExtractUtil {

    private Logger logger = LoggerFactory.getLogger(ExtractUtil.class);
    private static String main_town = "twon";
    private static String main_org = "org";
    private static String main_dept = "dept";
    private static String main_year = "year";
    private static String slave_sex = "sex";
    private static String slave_age = "age";
    private static  String unknown = "未知";

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    SolrUtil solrUtil;
    @Autowired
    SolrQuery solrQuery;
    private QuotaVo quotaVo;
    private String timeLevel;

    /**
     * 抽取列表数据 关联数据字典
     * @param qdm
     * @param qds
     * @param dataList
     * @param timeKey  时间维度字段
     * @param aggregationKey 抽取统计的数据值字段
     * @param quotaVo
     * @return
     * @throws Exception
     */
    public  List<SaveModel> computeList(List<TjQuotaDimensionMain> qdm, List<TjQuotaDimensionSlave> qds,List<Map<String, Object>> dataList,
                                        String timeKey,String aggregationKey,QuotaVo quotaVo) throws Exception {
        this.quotaVo = quotaVo;
        List<SaveModel> returnList = new ArrayList<>();
        List<SaveModel> totalSaveModelDictList = getTotalDictDataList(qdm,qds);
        Map<String,String> townDictMap = new HashMap<>();
        Map<String,String> orgDictMap = new HashMap<>();
        Map<String,String> deptDictMap = new HashMap<>();
        Map<String,String> yearDictMap = new HashMap<>();
        Map<String,String> slave1DictMap = new HashMap<>();
        Map<String,String> slave2DictMap = new HashMap<>();
        Map<String,String> slave3DictMap = new HashMap<>();
        Map<String,String> slave4DictMap = new HashMap<>();
        for(SaveModel saveModel: totalSaveModelDictList){
            if(saveModel.getTown() != null){
                townDictMap.put(saveModel.getTown(), saveModel.getTownName());
            }
            if(saveModel.getOrg() != null){
                orgDictMap.put(saveModel.getOrg(), saveModel.getOrgName());
            }
            if(saveModel.getDept() != null){
                deptDictMap.put(saveModel.getDept(), saveModel.getDeptName());
            }
            if(saveModel.getYear() != null){
                yearDictMap.put(saveModel.getYear(), saveModel.getYearName());
            }
            if(saveModel.getSlaveKey1() != null){
                slave1DictMap.put(saveModel.getSlaveKey1(), saveModel.getSlaveKey1Name());
            }
            if(saveModel.getSlaveKey2() != null){
                slave2DictMap.put(saveModel.getSlaveKey2(), saveModel.getSlaveKey2Name());
            }
            if(saveModel.getSlaveKey3() != null){
                slave3DictMap.put(saveModel.getSlaveKey3(), saveModel.getSlaveKey3Name());
            }
            if(saveModel.getSlaveKey4() != null){
                slave4DictMap.put(saveModel.getSlaveKey4(), saveModel.getSlaveKey4Name());
            }
        }
        int errorCount = 0;
        for(Map<String, Object> map : dataList){
            SaveModel saveModel = new SaveModel();

            // 去重查询场合
            Object distinctField = map.get("distinctField");
            Object distinctFieldValue = map.get("distinctFieldValue");
            if (distinctField != null) {
                saveModel.setDistinctField(distinctField.toString());
                saveModel.setDistinctFieldValue(distinctFieldValue.toString());
            }

            for (TjQuotaDimensionMain main : qdm) {
                String keyVal = main.getKeyVal().contains(".")?main.getKeyVal().substring(main.getKeyVal().indexOf(".")+1):main.getKeyVal();//如mysql 多表拼接时 d.org_code
                if(map.get(keyVal.trim()) != null ){
                    String value = map.get(keyVal.trim()).toString();
                    if(main.getMainCode().equals(main_town) && !StringUtils.isEmpty(townDictMap.get(value))){
                        saveModel.setTown(value);
                        saveModel.setTownName(townDictMap.get(value));
                    }else if(main.getMainCode().equals(main_org) && !StringUtils.isEmpty(orgDictMap.get(value))){
                        saveModel.setOrg(value);
                        saveModel.setOrgName(orgDictMap.get(value));
                    }else if(main.getMainCode().equals(main_year) && !StringUtils.isEmpty(yearDictMap.get(value))){
                        saveModel.setYearName(yearDictMap.get(value));
                        saveModel.setYear(value);
                    }else if(main.getMainCode().equals(main_dept)){
                        if(value.length() > 2){
                            value = value.substring(0,2);
                        }
                        if( !StringUtils.isEmpty(deptDictMap.get(value))){
                            saveModel.setDeptName(deptDictMap.get(value));
                            saveModel.setDept(value);
                        }
                    }
                }
            }
            if(saveModel.getTown() != null || saveModel.getOrg() !=null){
                for (int i = 0; i < qds.size(); i++) {
                    int num = i+1 ;
                    if(num == 1) {
                        if(map.get(qds.get(i).getKeyVal().trim()) != null){
                            String value = map.get(qds.get(i).getKeyVal().trim()).toString();
                            if( !StringUtils.isEmpty(slave1DictMap.get(value))){
                                saveModel.setSlaveKey1(value);
                                saveModel.setSlaveKey1Name(slave1DictMap.get(value));
                            }
                        }
                    }else if(num == 2) {
                        if(map.get(qds.get(i).getKeyVal().trim()) != null){
                            String value = map.get(qds.get(i).getKeyVal().trim()).toString();
                            if( !StringUtils.isEmpty(slave2DictMap.get(value))){
                                saveModel.setSlaveKey2(value);
                                saveModel.setSlaveKey2Name(slave2DictMap.get(value));
                            }
                        }
                    }else if(num == 3) {
                        if(map.get(qds.get(i).getKeyVal().trim()) != null){
                            String value = map.get(qds.get(i).getKeyVal().trim()).toString();
                            if( !StringUtils.isEmpty(slave3DictMap.get(value))){
                                saveModel.setSlaveKey3(value);
                                saveModel.setSlaveKey3Name(slave3DictMap.get(value));
                            }
                        }
                    }else if(num == 4 ) {
                        if(map.get(qds.get(i).getKeyVal().trim()) != null){
                            String value = map.get(qds.get(i).getKeyVal().trim()).toString();
                            if( !StringUtils.isEmpty(slave4DictMap.get(value))){
                                saveModel.setSlaveKey4(value);
                                saveModel.setSlaveKey4Name(slave4DictMap.get(value));
                            }
                        }
                    }
                }
                if(!StringUtils.isEmpty(timeKey)){
                    if(!StringUtils.isEmpty( map.get(timeKey) ) ){
                        String date = "";
                        if(map.get(timeKey) instanceof String){
                            date = map.get(timeKey).toString().substring(0,10);
                        }else if(map.get(timeKey) instanceof Date){
                            date = DateUtil.formatDate((Date)map.get(timeKey),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                        }
                        saveModel.setQuotaDate(date);
                    }
//                    if(!StringUtils.isEmpty( map.get("quotaDate") ) ){
//                        String date = "";
//                        if(map.get("quotaDate") instanceof String){
//                            date = map.get("quotaDate").toString();
//                        }else if(map.get("quotaDate") instanceof Date){
//                            date = DateUtil.formatDate((Date)map.get("quotaDate"),DateUtil.DEFAULT_DATE_YMD_FORMAT);
//                        }
//                        saveModel.setQuotaDate(date);
//                    }
                    if(!StringUtils.isEmpty( map.get("event_date") )){
                        String date = "";
                        if(map.get("event_date") instanceof String){
                            date = map.get("event_date").toString().substring(0,10);
                        }else if(map.get("event_date") instanceof Date){
                            date = DateUtil.formatDate((Date)map.get("event_date"),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                        }
                        saveModel.setQuotaDate(date);
                    }
                    if(!StringUtils.isEmpty( map.get("eventDate") )){
                        String date = "";
                        if(map.get("eventDate") instanceof String){
                            date = map.get("eventDate").toString().substring(0,10);
                        }else if(map.get("eventDate") instanceof Date){
                            date = DateUtil.formatDate((Date)map.get("event_date"),DateUtil.DEFAULT_DATE_YMD_FORMAT);
                        }
                        saveModel.setQuotaDate(date);
                    }
                }
                if(!StringUtils.isEmpty(aggregationKey)){
                    if(map.get(aggregationKey) != null){
                        saveModel.setResult(map.get(aggregationKey).toString());
                    }
                }else {
                    saveModel.setResult("1");
                }
                saveModel.setQuotaCode(quotaVo.getCode().replaceAll("_",""));
                saveModel.setQuotaName(quotaVo.getName());
                returnList.add(saveModel);
            }else {
                errorCount++;
            }

        }
        //关联机构相关信息
        if(orgDictMap != null && orgDictMap.size() > 0){
            setSaveModelProperties(returnList);
        }
        logger.info("指标：" + quotaVo.getName() + "统计时指标或者机构未关联上错误数据有：" + errorCount);
        return returnList;
    }

    /**
     * 获取所有维度字典
     * @param qdm
     * @param qds
     * @return
     */
    public List<SaveModel> getTotalDictDataList(List<TjQuotaDimensionMain> qdm, List<TjQuotaDimensionSlave> qds){
        List<SaveModel> totalSaveModelDictList = new ArrayList<>();
        for (TjQuotaDimensionMain main : qdm) {
            List<SaveModel> saveModelDicts = jdbcTemplate.query(main.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
            if (saveModelDicts != null) {
                for(SaveModel saveModel :saveModelDicts){
                    totalSaveModelDictList.add(saveModel);
                }
            }
        }
        for (int i = 0; i < qds.size(); i++) {
            int num = i+1;
            if(qds.get(i).getDictSql() != null){
                List<DictModel> dictModels = jdbcTemplate.query(qds.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                if (dictModels != null) {
                    for(DictModel dictModel :dictModels){
                        SaveModel saveModel = new SaveModel();
                        if(num == 1){
                            saveModel.setSlaveKey1(dictModel.getCode());
                            saveModel.setSlaveKey1Name(dictModel.getName());
                        }else  if(num == 2){
                            saveModel.setSlaveKey2(dictModel.getCode());
                            saveModel.setSlaveKey2Name(dictModel.getName());
                        }else  if(num == 3){
                            saveModel.setSlaveKey3(dictModel.getCode());
                            saveModel.setSlaveKey3Name(dictModel.getName());
                        }else  if(num == 4){
                            saveModel.setSlaveKey4(dictModel.getCode());
                            saveModel.setSlaveKey4Name(dictModel.getName());
                        }
                        totalSaveModelDictList.add(saveModel);
                    }
                }
            }
        }
        return totalSaveModelDictList;
    }

    /**
     * 融合主细维度、其组合统计值为SaveModel
     *
     * @param qdm                 主维度集合
     * @param qds                 细维度集合
     * @param returnList          转换后的 SaveModel 集合
     * @param statisticsResultMap 统计结果集
     * @param daySlaveDictMap     按天统计的所有日期项
     * @param quotaVo             指标配置
     */
    public void compute(List<TjQuotaDimensionMain> qdm,
                        List<TjQuotaDimensionSlave> qds,
                        List<SaveModel> returnList,
                        Map<String, String> statisticsResultMap,
                        Map<String, String> daySlaveDictMap,
                        QuotaVo quotaVo) throws Exception {
        if (statisticsResultMap == null || statisticsResultMap.size() == 0) {
            logger.warn("没有查询到数据");
            return;
        }
        this.quotaVo = quotaVo;

        // 将主细维度的字典项转换成 SaveModel
        Map<String, SaveModel> allData = new HashMap<>();
        if (qdm.size() == 1) {
            allData = initDimension(qds, qdm.get(0), allData, daySlaveDictMap);
        } else {
            allData = initDimensionMoreMain(qds, qdm, allData, daySlaveDictMap);
        }

        // 设置维度组合的统计值
        for (Map.Entry<String, String> entry : statisticsResultMap.entrySet()) {
            String key = entry.getKey();
            SaveModel saveModel = allData.get(key);
            if (saveModel != null) {
                saveModel.setResult(statisticsResultMap.get(key));
                saveModel.setQuotaDate(daySlaveDictMap.get(key));
                returnList.add(saveModel);
            }
        }

        List<String> dimins = new ArrayList<>();
        for (TjQuotaDimensionSlave slave : qds) {
            if (slave.getId() != null) {
                dimins.add(slave.getSlaveCode());
            }
        }
        for (TjQuotaDimensionMain main : qdm) {
            dimins.add(main.getMainCode());
        }
        dimins.add("quotaDate");
        BasesicUtil baseUtil = new BasesicUtil();
        Map<String, String> diminMap = new HashMap<>();
        for (SaveModel saveModel : returnList) {
            String diminStr = "";
            for (String key : dimins) {
                diminStr += baseUtil.getFieldValueByName(key, saveModel);
            }
            diminMap.put(diminStr, diminStr);
        }

    }

    /**
     * 将主细维度的字典项转换成 SaveModel（一个主维度场合）
     */
    private Map<String, SaveModel> initDimension(List<TjQuotaDimensionSlave> dimensionSlaves,
                                                 TjQuotaDimensionMain dimensionMain,
                                                 Map<String, SaveModel> allData,
                                                 Map<String, String> daySlaveDictMap) throws Exception {
        try {
            if (dimensionMain != null && !StringUtils.isEmpty(dimensionMain.getDictSql())) {
                //查询字典数据
                List<SaveModel> dictData = jdbcTemplate.query(dimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                if (dictData == null) {
                    throw new Exception("主纬度配置有误");
                } else {
                    if (dimensionMain.getMainCode().equals("org")) {//机构关联出区县
                        setSaveModelProperties(dictData);
                    }
                    //设置到map里面
                    setAllData(allData, dictData, dimensionMain.getType());
                }
            }

            for (int i = 0; i < dimensionSlaves.size(); i++) {
                List<DictModel> dictDataSlave = new ArrayList<>();
                if (dimensionSlaves.get(i).getId() != null) {
                    dictDataSlave = jdbcTemplate.query(dimensionSlaves.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                } else { // 在solr、mysql抽取中默认追加的按天统计的维度
                    DictModel dict;
                    for (Map.Entry<String, String> item : daySlaveDictMap.entrySet()) {
                        dict = new DictModel();
                        dict.setName(item.getValue());
                        dict.setCode(item.getValue());
                        dictDataSlave.add(dict);
                    }
                }
                allData = setAllSlaveData(allData, dictDataSlave, i);
            }
        } catch (Exception e) {
            throw new Exception("纬度配置有误");
        }
        return allData;
    }

    /**
     * 将主细维度的字典项转换成 SaveModel（多个个主维度场合）
     */
    private Map<String, SaveModel> initDimensionMoreMain(List<TjQuotaDimensionSlave> dimensionSlaves,
                                                         List<TjQuotaDimensionMain> dimensionMains,
                                                         Map<String, SaveModel> allData,
                                                         Map<String, String> daySlaveDictMap) throws Exception {
        try {
            if (dimensionMains != null) {
                //查询字典数据
                List<SaveModel> dictData = jdbcTemplate.query(dimensionMains.get(0).getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                if (dictData == null) {
                    throw new Exception("主纬度配置有误");
                } else {
                    if (dimensionMains.get(0).getMainCode().equals("org")) {//机构关联出区县
                        setSaveModelProperties(dictData);
                    }
                    //设置到map里面
                    setAllData(allData, dictData, dimensionMains.get(0).getType());
                }
            }

            for (int i = 0; i < dimensionMains.size(); i++) {
                if (i != 0) {
                    List<SaveModel> saveDataMain = jdbcTemplate.query(dimensionMains.get(i).getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                    if (dimensionMains.get(i).getMainCode().equals("org")) {//机构关联出区县
                        setSaveModelProperties(saveDataMain);
                    }
                    allData = setOtherMainData(allData, saveDataMain, dimensionMains.get(i).getMainCode(), dimensionMains.get(i).getType());
                }
            }

            for (int i = 0; i < dimensionSlaves.size(); i++) {
                List<DictModel> dictDataSlave = new ArrayList<>();
                if (dimensionSlaves.get(i).getId() != null) {
                    dictDataSlave = jdbcTemplate.query(dimensionSlaves.get(i).getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                } else { // 在solr、mysql抽取中默认追加的按天统计的维度
                    DictModel dict;
                    for (Map.Entry<String, String> item : daySlaveDictMap.entrySet()) {
                        dict = new DictModel();
                        dict.setName(item.getValue());
                        dict.setCode(item.getValue());
                        dictDataSlave.add(dict);
                    }
                }
                allData = setAllSlaveData(allData, dictDataSlave, i);
            }
        } catch (Exception e) {
            throw new Exception("纬度配置有误");
        }
        return allData;
    }

    //如果选择多个维度，除了第一个维度外其他维度组合
    private Map<String, SaveModel> setOtherMainData(Map<String, SaveModel> allData,
                                                    List<SaveModel> saveDataMain,
                                                    String code,
                                                    String dimensionType) {
        Map<String, SaveModel> returnAllData = new HashMap<>();
        try {
            for (Map.Entry<String, SaveModel> one : allData.entrySet()) {
                for (int i = 0; i < saveDataMain.size(); i++) {
                    SaveModel mainOne = saveDataMain.get(i);
                    //设置新key
                    String codeVal = getMainCode(mainOne, dimensionType, "code");
                    String nameVal = getMainCode(mainOne, dimensionType, "name");

                    StringBuffer newKey = new StringBuffer(one.getKey() + "-" + codeVal);
                    //设置新的value
                    SaveModel saveModelTemp = new SaveModel();
                    BeanUtils.copyProperties(one.getValue(), saveModelTemp);
                    if (!StringUtils.isEmpty(mainOne.getTown())) {
                        saveModelTemp.setTown(mainOne.getTown());
                        saveModelTemp.setTownName(mainOne.getTownName());
                    }
                    if (!StringUtils.isEmpty(mainOne.getEconomic())) {
                        saveModelTemp.setEconomic(mainOne.getEconomic());
                        saveModelTemp.setEconomicName(mainOne.getEconomicName());
                    }
                    if (!StringUtils.isEmpty(mainOne.getLevel())) {
                        saveModelTemp.setLevel(mainOne.getLevel());
                        saveModelTemp.setLevelName(mainOne.getLevelName());
                    }
                    if (!StringUtils.isEmpty(mainOne.getOrgHealthCategoryId())) {
                        saveModelTemp.setOrgHealthCategoryId(mainOne.getOrgHealthCategoryId());
                    }
                    if (!StringUtils.isEmpty(mainOne.getOrgHealthCategoryCode())) {
                        saveModelTemp.setOrgHealthCategoryCode(mainOne.getOrgHealthCategoryCode());
                    }
                    if (!StringUtils.isEmpty(mainOne.getOrgHealthCategoryName())) {
                        saveModelTemp.setOrgHealthCategoryName(mainOne.getOrgHealthCategoryName());
                    }
                    if (!StringUtils.isEmpty(mainOne.getOrgHealthCategoryPid())) {
                        saveModelTemp.setOrgHealthCategoryPid(mainOne.getOrgHealthCategoryPid());
                    }
                    if (!StringUtils.isEmpty(mainOne.getOrgHealthCategoryTopPid())) {
                        saveModelTemp.setOrgHealthCategoryTopPid(mainOne.getOrgHealthCategoryTopPid());
                    }



                    code = code.substring(0, 1).toUpperCase() + code.substring(1);
                    StringBuffer keyMethodName = new StringBuffer("set" + code);
                    StringBuffer nameMethodName = new StringBuffer("set" + code + "Name");

                    SaveModel.class.getMethod(keyMethodName.toString(), String.class).invoke(saveModelTemp, codeVal);
                    SaveModel.class.getMethod(nameMethodName.toString(), String.class).invoke(saveModelTemp, nameVal);
                    returnAllData.put(newKey.toString(), saveModelTemp);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return returnAllData;
    }

    private Map<String, SaveModel> setAllSlaveData(Map<String, SaveModel> allData, List<DictModel> dictData, Integer key) {
        Map<String, SaveModel> returnAllData = new HashMap<>();
        try {
            for (Map.Entry<String, SaveModel> one : allData.entrySet()) {
                for (int i = 0; i < dictData.size(); i++) {
                    DictModel dictOne = dictData.get(i);
                    //设置新key
                    StringBuffer newKey = new StringBuffer(one.getKey() + "-" + dictOne.getCode());
                    //设置新的value
                    SaveModel saveModelTemp = new SaveModel();
                    BeanUtils.copyProperties(one.getValue(), saveModelTemp);

                    StringBuffer keyMethodName = new StringBuffer("setSlaveKey" + (key + 1));
                    StringBuffer nameMethodName = new StringBuffer("setSlaveKey" + (key + 1) + "Name");

                    SaveModel.class.getMethod(keyMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getCode());
                    SaveModel.class.getMethod(nameMethodName.toString(), String.class).invoke(saveModelTemp, dictOne.getName());
                    returnAllData.put(newKey.toString(), saveModelTemp);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return returnAllData;
    }

    /**
     * @param allData
     * @param dictData
     * @param dictType
     */
    private void setAllData(Map<String, SaveModel> allData, List<SaveModel> dictData, String dictType) {
        switch (dictType) {
            case Contant.main_dimension.area_province: {
                //设置省的全部的值
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getProvince(), one, Contant.main_dimension_areaLevel.area_province);
                });
                break;
            }
            case Contant.main_dimension.area_city: {
                //设置市的全部的值
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getCity(), one, Contant.main_dimension_areaLevel.area_city);
                });
                break;
            }
            case Contant.main_dimension.area_town: {
                //设置区的全部的值
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getTown(), one, Contant.main_dimension_areaLevel.area_town);
                });
                break;
            }
            case Contant.main_dimension.area_org: {
                //设置机构
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getOrg(), one, Contant.main_dimension_areaLevel.area_org);
                });
                break;
            }
            case Contant.main_dimension.area_dept: {
                //设置科室
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getDept(), one, Contant.main_dimension_areaLevel.area_dept);
                });
                break;
            }
            case Contant.main_dimension.time_year: {
                //设置年份
                dictData.stream().forEach(one -> {
                    this.timeLevel = Contant.main_dimension_timeLevel.year;
                    setOneData(allData, one.getYear(), one, null);
                });
                break;
            }
        }
    }

    private String getMainCode(SaveModel mainOne, String dimensionType, String returnType) {
        String code = "";
        String name = "";
        switch (dimensionType) {
            case Contant.main_dimension.area_province: {
                code = mainOne.getProvince();
                name = mainOne.getProvinceName();
                break;
            }
            case Contant.main_dimension.area_city: {
                code = mainOne.getCity();
                name = mainOne.getCityName();
                break;
            }
            case Contant.main_dimension.area_town: {
                code = mainOne.getTown();
                name = mainOne.getTownName();
                break;
            }
            case Contant.main_dimension.area_org: {
                code = mainOne.getOrg();
                name = mainOne.getOrgName();
                break;
            }
            case Contant.main_dimension.area_dept: {
                code = mainOne.getDept();
                name = mainOne.getDeptName();
                break;
            }
            case Contant.main_dimension.time_year: {
                code = mainOne.getYear();
                name = mainOne.getYearName();
                break;
            }
        }
        if (returnType.equals("code")) {
            return code;
        }
        if (returnType.equals("name")) {
            return name;
        }
        return "";
    }

    private void setOneData(Map<String, SaveModel> allData, String key, SaveModel one, String areaLevel) {
        one.setAreaLevel(areaLevel);
        one.setResult("0");
        one.setCreateTime(new Date());
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(null);
        allData.put(key, one);
    }

    private void setSaveModelProperties(List<SaveModel> saveDataMain) {
        //上饶区县
        String townSql = "SELECT id as code,name as name  from address_dict where pid = '361100'";
        List<DictModel> townDictDatas = jdbcTemplate.query(townSql, new BeanPropertyRowMapper(DictModel.class));
        Map<String,String> townMap = new HashMap<>();
        for(DictModel dictModel : townDictDatas){
            townMap.put(dictModel.getCode(), dictModel.getName());
        }
        //机构类型 目录对应的名称和节点id
        String orgHealthCategorySql = "SELECT id as orgHealthCategoryId,pid as orgHealthCategoryPid,top_pid as orgHealthCategoryTopPid,code as orgHealthCategoryCode, name as orgHealthCategoryName from org_health_category";
        List<OrgHealthCategoryShowModel> orgHealthCategoryDictDatas = jdbcTemplate.query(orgHealthCategorySql, new BeanPropertyRowMapper(OrgHealthCategoryShowModel.class));
        Map<String,OrgHealthCategoryShowModel>  orgHealthCategoryMap = new HashMap<>();
        for(OrgHealthCategoryShowModel orgHealthCategory : orgHealthCategoryDictDatas){
            orgHealthCategoryMap.put(orgHealthCategory.getOrgHealthCategoryCode(), orgHealthCategory);
        }

        //经济类型
        String economicSql = "SELECT code, catalog from system_dict_entries WHERE dict_id = 102 ";
        Map<String, Object> economicMap = new HashMap<>();
        List<Map<String, Object>> list = jdbcTemplate.queryForList(economicSql);
        if (null != list && list.size() > 0) {
            for (Map<String, Object> map : list) {
                economicMap.put(map.get("code") + "", map.get("catalog") + "");
            }
        }

        for (SaveModel model : saveDataMain) {
            String dictSql = "SELECT org_code as orgCode,hos_type_id as hosTypeId,administrative_division as administrativeDivision, hos_economic as hosEconomic, level_id as levelId  from organizations where org_code=";
            dictSql += "'" + model.getOrg() + "'";
            List<MOrganization> organizations = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(MOrganization.class));

            if (organizations != null && organizations.size() > 0) {
                MOrganization organization = organizations.get(0);
                if (!StringUtils.isEmpty(organization.getAdministrativeDivision())) {
                    String orgCode = organization.getAdministrativeDivision().toString();
                    if(townMap.get(orgCode) != null ){
                        model.setTown(orgCode);
                        model.setTownName(townMap.get(orgCode));
                    }
                }
                //关联出对应的机构类型
                if (!StringUtils.isEmpty(organization.getHosTypeId())) {
                    if(orgHealthCategoryMap.get(organization.getHosTypeId()) != null ){
                        OrgHealthCategoryShowModel orgHealthCategory = orgHealthCategoryMap.get(organization.getHosTypeId());
                        model.setOrgHealthCategoryCode(organization.getHosTypeId());
                        model.setOrgHealthCategoryName(orgHealthCategory.getOrgHealthCategoryName());
                        model.setOrgHealthCategoryId(orgHealthCategory.getOrgHealthCategoryId());
                        model.setOrgHealthCategoryPid(orgHealthCategory.getOrgHealthCategoryPid());
                        model.setOrgHealthCategoryTopPid(orgHealthCategory.getOrgHealthCategoryTopPid());
                    }
                }
                if (!StringUtils.isEmpty(organization.getHosEconomic())) {
                    String name = economicMap.get(organizations.get(0).getHosEconomic()) + "";
                    if ("公立".equals(name)) {
                        model.setEconomic("1021");
                        model.setEconomicName("公立");
                    } else {
                        model.setEconomic("1022");
                        model.setEconomicName("非公立");
                    }
                }
                if (!StringUtils.isEmpty(organization.getLevelId())) {
                    String levelId = organization.getLevelId();
                    if ("1".equals(levelId)) {
                        model.setLevel(levelId);
                        model.setLevelName("一级");
                    } else if ("2".equals(levelId)) {
                        model.setLevel(levelId);
                        model.setLevelName("二级");
                    } else if ("3".equals(levelId)) {
                        model.setLevel(levelId);
                        model.setLevelName("三级");
                    } else {
                        model.setLevel(levelId);
                        model.setLevelName("未定级");
                    }
                }
            }
        }
    }
}
