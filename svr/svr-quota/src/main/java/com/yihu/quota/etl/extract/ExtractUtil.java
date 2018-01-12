package com.yihu.quota.etl.extract;

import com.yihu.ehr.entity.address.AddressDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.query.services.SolrQuery;
import com.yihu.ehr.solr.SolrUtil;
import com.yihu.quota.etl.Contant;
import com.yihu.quota.etl.model.EsConfig;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.vo.DictModel;
import com.yihu.quota.vo.QuotaVo;
import com.yihu.quota.vo.SaveModel;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by janseny on 2017/7/10.
 */
@Component
@Scope("prototype")
public class ExtractUtil {

    private Logger logger = LoggerFactory.getLogger(ExtractUtil.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    SolrUtil solrUtil;
    @Autowired
    SolrQuery solrQuery;

    private QuotaVo quotaVo;
    private String startTime;
    private String endTime;
    private String timeLevel;
    private EsConfig esConfig;

    /**
     * 融合主细维度、其组合统计值为SaveModel
     */
    public void compute(List<TjQuotaDimensionMain> qdm,
                        List<TjQuotaDimensionSlave> qds,
                        List<SaveModel> returnList,
                        Map<String, Integer> countsMap,
                        Map<String, String> daySlaveDictMap,
                        QuotaVo quotaVo) throws Exception {
        if (countsMap == null || countsMap.size() == 0) {
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
        for (Map.Entry<String, Integer> entry : countsMap.entrySet()) {
            String key = entry.getKey();
            SaveModel saveModel = allData.get(key);
            if (saveModel != null) {
                saveModel.setResult(countsMap.get(key).toString());
                saveModel.setQuotaDate(daySlaveDictMap.get(key));
                returnList.add(saveModel);
            }
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
            if (dimensionMain != null) {
                //查询字典数据
                List<SaveModel> dictData = jdbcTemplate.query(dimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                if (dictData == null) {
                    throw new Exception("主纬度配置有误");
                } else {
                    if(dimensionMain.getMainCode().equals("org")){//机构关联出区县
                        for(SaveModel model: dictData){
                            String dictSql = "SELECT org_code as orgCode,hos_type_id as hosTypeId,administrative_division as administrativeDivision from organizations where org_code=";
                            dictSql += "'"+ model.getOrg() + "'";
                            List<MOrganization> organizations = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(MOrganization.class));
                            if(organizations != null && organizations.size() > 0){
                                if(!StringUtils.isEmpty(organizations.get(0).getAdministrativeDivision())){
                                    model.setCity("shangrao");
                                    model.setCityName("上饶市");
                                    String townSql = "SELECT id ,name from address_dict where id = " + organizations.get(0).getAdministrativeDivision();
                                    List<AddressDict> addressDicts = jdbcTemplate.query(townSql, new BeanPropertyRowMapper(AddressDict.class));
                                    model.setTown(String.valueOf(addressDicts.get(0).getId()));
                                    model.setTownName(addressDicts.get(0).getName());
                                }
                            }
                        }
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
                    //设置到map里面
                    setAllData(allData, dictData, dimensionMains.get(0).getType());
                }
            }

            for (int i = 0; i < dimensionMains.size(); i++) {
                if (i != 0) {
                    List<SaveModel> saveDataMain = jdbcTemplate.query(dimensionMains.get(i).getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                    if(dimensionMains.get(i).getMainCode().equals("org")){//机构关联出区县
                        for(SaveModel model: saveDataMain){
                            String dictSql = "SELECT org_code as orgCode,hos_type_id as hosTypeId,administrative_division as administrativeDivision from organizations where org_code=";
                            dictSql += "'"+ model.getOrg() + "'";
                            List<MOrganization> organizations = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(MOrganization.class));
                            if(organizations != null && organizations.size() > 0){
                                if(!StringUtils.isEmpty(organizations.get(0).getAdministrativeDivision())){
                                    model.setCity("shangrao");
                                    model.setCityName("上饶市");
                                    String townSql = "SELECT id ,name from address_dict where id = " + organizations.get(0).getAdministrativeDivision();
                                    List<AddressDict> addressDicts = jdbcTemplate.query(townSql, new BeanPropertyRowMapper(AddressDict.class));
                                    model.setTown(String.valueOf(addressDicts.get(0).getId()));
                                    model.setTownName(addressDicts.get(0).getName());
                                }
                            }
                        }
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
                    ;
                    StringBuffer newKey = new StringBuffer(one.getKey() + "-" + codeVal);
                    //设置新的value
                    SaveModel saveModelTemp = new SaveModel();
                    BeanUtils.copyProperties(one.getValue(), saveModelTemp);
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
            case Contant.main_dimension.area_team: {
                //设置团队
                dictData.stream().forEach(one -> {
                    setOneData(allData, one.getTeam(), one, Contant.main_dimension_areaLevel.area_team);
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
            case Contant.main_dimension.area_team: {
                code = mainOne.getTeam();
                name = mainOne.getTeamName();
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
        LocalDate today = LocalDate.now();
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(null);
        allData.put(key, one);
    }

    private SaveModel setSaveModel(SaveModel one) {
        one.setResult("0");
        one.setCreateTime(new Date());
        LocalDate today = LocalDate.now();
        String yesterDay = (new DateTime().minusDays(1)).toString("yyyy-MM-dd");
        one.setQuotaDate(yesterDay);
        one.setQuotaCode(quotaVo.getCode());
        one.setQuotaName(quotaVo.getName());
        one.setTimeLevel(timeLevel);
        one.setSaasId(null);
        return one;
    }

}
