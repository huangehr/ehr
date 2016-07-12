//package com.yihu.ehr.profile.controller.sanofi.legacy;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
//import com.fasterxml.jackson.databind.node.ObjectNode;
//import com.yihu.ehr.api.ServiceApi;
//import com.yihu.ehr.constants.ApiVersion;
//import com.yihu.ehr.constants.LisEntry;
//import com.yihu.ehr.lang.SpringContext;
//import com.yihu.ehr.profile.legacy.sanofi.memory.model.MemoryProfile;
//import com.yihu.ehr.profile.legacy.sanofi.memory.model.MetaDataRecord;
//import com.yihu.ehr.profile.legacy.sanofi.memory.model.StdDataSet;
//import com.yihu.ehr.profile.legacy.sanofi.persist.ProfileIndices;
//import com.yihu.ehr.profile.legacy.sanofi.persist.ProfileIndicesService;
//import com.yihu.ehr.profile.legacy.sanofi.persist.ProfileService;
//import com.yihu.ehr.profile.legacy.sanofi.persist.repo.DataSetRepository;
//import com.yihu.ehr.util.datetime.DateTimeUtil;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
//import io.swagger.annotations.ApiParam;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.format.annotation.DateTimeFormat;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * 康赛（赛诺菲）项目患者体征数据提取API。体征数据包括：
// * - 体温、呼吸与脉搏
// * - 血液检验数据
// * - 尿液检验数据
// *
// * @author Sand
// * @version 1.0
// * @created 2015.12.26 16:08
// */
//@RestController
//@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//@Api(value = "赛诺菲数据服务", description = "赛诺菲项目体征数据提取服务")
//public class SanofiEndPoint {
//    @Autowired
//    private ProfileService profileService;
//
//    @Autowired
//    private DataSetRepository dataSetRepo;
//
//    @Autowired
//    private ProfileIndicesService indicesService;
//
//    @ApiOperation(value = "获取体征数据", notes = "获取体征数据")
//    @RequestMapping(value = ServiceApi.SanofiSupport.PhysicSigns, method = RequestMethod.GET)
//    public ResponseEntity<String> getBodySigns(
//            @ApiParam(value = "身份证号", defaultValue = "412726195111306268")
//            @RequestParam(value = "demographic_id", required = false) String demographicId,
//            @ApiParam(value = "患者姓名")
//            @RequestParam(value = "name", required = false) String name,
//            @ApiParam(value = "联系电话")
//            @RequestParam(value = "telephone", required = false) String telephone,
//            @ApiParam(value = "性别")
//            @RequestParam(value = "gender", required = false) String gender,
//            @ApiParam(value = "出生日期")
//            @RequestParam(value = "birthday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday,
//            @ApiParam(value = "起始日期", defaultValue = "2015-01-01")
//            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
//            @ApiParam(value = "结束日期", defaultValue = "2016-01-01")
//            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws Exception {
//        Pageable pageable = new PageRequest(0, 20);
//        Page<ProfileIndices> profileIndices = indicesService.findByDemographic(demographicId, null, name, telephone, gender, birthday, since, to, pageable);
//
//        if (profileIndices == null) return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
//
//        List<MemoryProfile> profiles = new ArrayList<>();
//        for (ProfileIndices indices : profileIndices.getContent()) {
//            MemoryProfile memoryProfile = profileService.getProfile(indices.getProfileId());
//            profiles.add(memoryProfile);
//        }
//
//        if (profiles.size() == 0) return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
//
//        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
//        ArrayNode document = objectMapper.createArrayNode();
//        for (MemoryProfile profile : profiles) {
//            ObjectNode section = objectMapper.createObjectNode();
//            convert(section, profile ,since, to);
//
//            if (section.size() > 1) document.addPOJO(section);
//        }
//
//        return new ResponseEntity<>(document.toString(), HttpStatus.OK);
//    }
//
//    private void convert(ObjectNode document, MemoryProfile profile ,Date since, Date to) throws IOException, ParseException {
//        JsonNode section;
//        StdDataSet dataSet;
//        String[] innerCodes;
//
//        // 人口学信息
//        dataSet = profile.getDataSet("HDSA00_01");
//        if (dataSet.getRecordKeys().size() > 0) {
//            section = document.with("demographic_info");
//            innerCodes = new String[]{
//                    "HDSA00_01_012",                   //生日
//                    "HDSA00_01_011_CODE",              //性别代码，男1，女2
//                    "HDSA00_01_011_VALUE",             //性别文字描述
//                    "HDSA00_01_017",                   //身份证号
//                    "HDSA00_01_009"};                  //姓名
//
//            StdDataSet stdDataSet = getStdDataSet(profile, dataSet, innerCodes);
//            mergeData(section, stdDataSet, innerCodes);
//        }
//
//        // 生命体征：住院护理体征记录
//        dataSet = profile.getDataSet("HDSD00_08");
//        Date profileCreateDate = profile.getCreateDate();
//        innerCodes = new String[]{
//                "HDSD00_08_025",   // 呼吸频率（次/min）
//                "HDSD00_08_075",   // 体温
//                "HDSD00_08_041",   // 脉率（次/min）
//                "HDSD00_08_060",   // 收缩压（mmHg）
//                "HDSD00_08_068",   // 舒张压（mmHg）
//                "HDSD00_08_036"    // 记录日期时间
//        };
//        if (dataSet != null && dataSet.getRecordKeys().size() > 0) {
//            section = document.withArray("physical_exam");
//            String checkDate = "HDSD00_08_036";
//            Filter(section,dataSet,profile,innerCodes,since,to,checkDate,profileCreateDate);
//        }
//
//        // 检验
//        dataSet = profile.getDataSet("HDSD02_03");
//        if (dataSet != null && dataSet.getRecordKeys().size() > 0) {
//            section = document.withArray("lis");
//            innerCodes = new String[]{
//                    "JDSD02_03_13", // 中文名
//                    "JDSD02_03_14", // 英文名
//                    "JDSD02_03_04", // 结果值
//                    "JDSD02_03_05", // 结果类型
//                    "HDSD00_01_547",// 单位
//                    "JDSD02_03_06", // 参考值上限
//                    "JDSD02_03_07", // 参考值下限
//                    "JDSD02_03_10"  // 创建时间
//            };
//
//            if (dataSet != null && dataSet.getRecordKeys().size() > 0) {
//                String checkDate = "JDSD02_03_10";
//                Filter(section,dataSet,profile,innerCodes,since,to,checkDate,null);
//            }
//            //检验项目过滤 符合要求的项目有{"PRO", "CREA", "GLU", "TCHO", "HDL-C", "TG", "K", "HbAc1", "GLU(2h)", "cTnI", "PRO", "UTP"}
//            StdDataSet stdDataSet = getStdDataSet(profile, dataSet, innerCodes);
//            Set<String> keys = stdDataSet.getRecordKeys();
//            List<String> listKeys = new ArrayList<String>();
//            listKeys.addAll(keys);
//            for (String recordKey : listKeys) {
//                MetaDataRecord record = stdDataSet.getRecord(recordKey);
//                String JDSD02_03_14 = record.getDataGroup().get("JDSD02_03_14");
//                String JDSD02_03_04 = record.getDataGroup().get("JDSD02_03_04");
//                String[] entryNames = LisEntry.ENTRY_NAME;
//                List<String> entryNameList = Arrays.asList(entryNames);
//                if (!entryNameList.contains(JDSD02_03_14)|| StringUtils.isEmpty(JDSD02_03_04)) {
//                    stdDataSet.removeRecord(recordKey, record);
//                }
//            }
//            mergeData(section, stdDataSet, innerCodes);
//        }
//
//        // 临时医嘱
//        dataSet = profile.getDataSet("HDSC02_11");
//        innerCodes = new String[]{
//                "HDSD00_15_020",  //医嘱下嘱时间
//                "HDSD00_15_028"   //医嘱名称
//        };
//        if (dataSet != null && dataSet.getRecordKeys().size() > 0) {
//            String checkDate = "HDSD00_15_020";
//            section = document.withArray("stat_order");
//            Filter(section,dataSet,profile,innerCodes,since,to,checkDate,null);
//        }
//
//        // 长期医嘱
//        dataSet = profile.getDataSet("HDSC02_12");
//        innerCodes = new String[]{
//                "HDSD00_15_020",  //医嘱下嘱时间
//                "HDSD00_15_026",  //医嘱停嘱时间
//                "HDSD00_15_028"   //医嘱名称
//        };
//        if (dataSet != null && dataSet.getRecordKeys().size() > 0) {
//            String checkDate = "HDSD00_15_020";
//            section = document.withArray("stand_order");
//            Filter(section,dataSet,profile,innerCodes,since,to,checkDate,null);
//        }
//    }
//
//    private void mergeData(JsonNode section, StdDataSet stdDataSet, String[] metaDataCodes) throws IOException {
//        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
//        if (section.isArray()) {
//            ArrayNode array = (ArrayNode) section;
//            for (String recordKey : stdDataSet.getRecordKeys()) {
//                ObjectNode arrayNode = objectMapper.createObjectNode();
//                MetaDataRecord record = stdDataSet.getRecord(recordKey);
//                boolean flag = false;
//                for (String metaDataCode : metaDataCodes) {
//                    String value = record.getMetaData(metaDataCode);
//                    if (!StringUtils.isEmpty(value) && !metaDataCode.equals("HDSD00_08_036")) {
//                        flag = true;
//                    }
//                    arrayNode.put(metaDataCode, StringUtils.isEmpty(value) ? "" : value);
//                }
//                if (flag) {
//                    array.addPOJO(arrayNode);
//                }
//            }
//        } else if (section.isObject()) {
//            ObjectNode objectNode = (ObjectNode) section;
//            for (String recordKey : stdDataSet.getRecordKeys()) {
//                MetaDataRecord record = stdDataSet.getRecord(recordKey);
//                for (String metaDataCode : metaDataCodes) {
//                    String value = record.getMetaData(metaDataCode);
//                    objectNode.put(metaDataCode, StringUtils.isEmpty(value) ? "" : value);
//                }
//
//                break;  // 就一行
//            }
//        }
//    }
//
//    private StdDataSet getStdDataSet(MemoryProfile profile, StdDataSet dataSet, String[] innerCodes) throws IOException {
//        StdDataSet stdDataSet = dataSetRepo.findOne(
//                profile.getCdaVersion(),
//                dataSet.getCode(),
//                profile.getProfileType(),
//                dataSet.getRecordKeys(),
//                innerCodes
//        ).getRight();
//        return stdDataSet;
//    }
//
//    /**
//     * 档案时间过滤
//     */
//    private void Filter(JsonNode section,StdDataSet dataSet ,MemoryProfile profile,String[] innerCodes,Date sinceDate ,Date toDate,String checkDate,Date profileCreateDate) throws ParseException, IOException {
//        StdDataSet stdDataSet = getStdDataSet(profile, dataSet, innerCodes);
//        Set<String> keys = stdDataSet.getRecordKeys();
//        List<String> listKeys = new ArrayList<String>();
//        listKeys.addAll(keys);
//
//        String sinceDateStr="";
//        String toDateStr="";
//        for (String recordKey : listKeys) {
//            MetaDataRecord record = stdDataSet.getRecord(recordKey);
//            if(record!=null & profileCreateDate!=null){
//                sinceDateStr = DateTimeUtil.utcDateTimeFormat(sinceDate);
//                toDateStr = DateTimeUtil.utcDateTimeFormat(toDate);
//                String profileCreateDateStr = DateTimeUtil.utcDateTimeFormat(profileCreateDate);
//                if (sinceDateStr.compareTo(profileCreateDateStr)>0 || profileCreateDateStr.compareTo(toDateStr)>0) {
//                    //删除不合格的记录
//                    stdDataSet.removeRecord(recordKey, record);
//                }else {
//                    //// TODO: 2016/7/1 因为体征数据查询不到报告时间所以暂时 把入库时间作为报告时间
//                     record.putMetaData("HDSD00_08_036",profileCreateDateStr);
//                }
//                continue;
//            }
//            if(record.getDataGroup().get(checkDate)!=null){
//                String checkDateStr = record.getDataGroup().get(checkDate); //要检查的时间
//                String generalDateEL = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";
//                String utcDateEL = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}Z";
//                Matcher m;
//
//                Pattern pGeneral = Pattern.compile(generalDateEL);
//                m = pGeneral.matcher(checkDateStr);
//
//                if(m.matches()){
//                    //普通日期格式
//                    sinceDateStr = DateTimeUtil.simpleDateTimeFormat(sinceDate);
//                    toDateStr = DateTimeUtil.simpleDateTimeFormat(toDate);
//                }
//                Pattern pUtc = Pattern.compile(utcDateEL);
//                m = pUtc.matcher(checkDateStr);
//                if(m.matches()){
//                    //utc日期格式
//                    sinceDateStr = DateTimeUtil.utcDateTimeFormat(sinceDate);
//                    toDateStr = DateTimeUtil.utcDateTimeFormat(toDate);
//                }
//                if (sinceDateStr.compareTo(checkDateStr)>0 || checkDateStr.compareTo(toDateStr)>0 ) {
//                    //删除不合格的记录
//                    stdDataSet.removeRecord(recordKey, record);
//                }
//            }else {
//                //删除不合格的记录
//                stdDataSet.removeRecord(recordKey, record);
//            }
//
//        }
//        mergeData(section, stdDataSet, innerCodes);
//    }
//
//
//
//
//}
