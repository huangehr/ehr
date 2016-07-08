package com.yihu.ehr.profile.controller.sanofi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.LisEntry;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.profile.service.BasisConstant;
import com.yihu.ehr.profile.service.SanofiService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * 康赛（赛诺菲）项目患者体征数据提取API。体征数据包括：
 * - 体温、呼吸与脉搏
 * - 血液检验数据
 * - 尿液检验数据
 *
 * @author Sand
 * @version 1.0
 * @created 2015.12.26 16:08
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Api(value = "赛诺菲数据服务", description = "赛诺菲项目体征数据提取服务")
public class SanofiEndPoint {

    @Autowired
    SanofiService sanofiService;

    @Autowired
    XTransformClient transformClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    XResourceClient resourceClient;

    @ApiOperation(value = "获取体征数据", notes = "获取体征数据")
    @RequestMapping(value = ServiceApi.SanofiSupport.PhysicSigns, method = RequestMethod.GET)
    public ResponseEntity<String> getBodySigns(
            @ApiParam(value = "身份证号", defaultValue = "360101200006011131")
            @RequestParam(value = "demographic_id", required = false) String demographicId,
            @ApiParam(value = "患者姓名")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "联系电话")
            @RequestParam(value = "telephone", required = false) String telephone,
            @ApiParam(value = "性别")
            @RequestParam(value = "gender", required = false) String gender,
            @ApiParam(value = "出生日期")
            @RequestParam(value = "birthday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday,
            @ApiParam(value = "起始日期", defaultValue = "1900-01-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws Exception {

        List<Map<String, Object>> profileList = sanofiService.findByDemographic(demographicId, name, telephone, gender, birthday);

        List<String> profileIds = new ArrayList<>();
        for (Map<String, Object> profile : profileList) {
            String profileId = (String) profile.get("rowkey");
            profileIds.add(profileId);
        }

        if (profileIds.size() == 0) return new ResponseEntity<>("", HttpStatus.NOT_FOUND);


        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        ArrayNode document = objectMapper.createArrayNode();

        ObjectNode section = objectMapper.createObjectNode();
        convert(section, profileIds, since, to);
        document.addPOJO(section);


        return new ResponseEntity<>(document.toString(), HttpStatus.OK);
    }

    private void convert(ObjectNode document, List<String> profileIds, Date since, Date to) throws Exception {
        JsonNode section;
        String[] innerCodes;
        MStdTransformDto stdTransformDto = new MStdTransformDto();

        List<Map<String, Object>> dataSetList;
        String query;
        // 人口学信息

        query = "{\"q\":\"profile_id:" + profileIds.get(0);
        dataSetList = sanofiService.getDataSet(BasisConstant.patientInfo, query);
        stdTransformDto.setVersion("56395d75b854");
        stdTransformDto.setSource(objectMapper.writeValueAsString(dataSetList));
        dataSetList = transformClient.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));

        if (dataSetList.size() > 0) {
            section = document.with("demographic_info");
            innerCodes = new String[]{
                    "HDSA00_01_012",                   //生日
                    "HDSA00_01_011",                   //性别代码，男1，女2
//                    "HDSA00_01_011_VALUE",             //性别文字描述
                    "HDSA00_01_017",                   //身份证号
                    "HDSD00_01_002"};                  //姓名

            mergeData(section, dataSetList, innerCodes);
        }

        // 生命体征：住院护理体征记录 // TODO: 2016/7/5 暂时未资源化
        document.withArray("physical_exam");
//        dataSet = profile.getDataSet("HDSD00_08");
//        for (String profileId : profileIds) {
//            section = document.withArray("physical_exam");
//            query = "{\"q\":\"profile_id:" + profileIds.get(0);
//            query += " AND EHR_000384:[" + DateTimeUtil.utcDateTimeFormat(since)+" TO " + DateTimeUtil.utcDateTimeFormat(since) + "]\"}".replace(" ","+");
//            dataSetList = sanofiService.getDataSet("生命体征", query);
//            stdTransformDto.setVersion("56395d75b854");
//            stdTransformDto.setSource(objectMapper.writeValueAsString(dataSetList));
//            dataSetList = transformClient.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));
//            if (dataSetList != null && dataSetList.size() > 0) {
//                innerCodes = new String[]{
//                        "HDSD00_08_025",   // 呼吸频率（次/min）
//                        "HDSD00_08_075",   // 体温
//                        "HDSD00_08_041",   // 脉率（次/min）
//                        "HDSD00_08_060",   // 收缩压（mmHg）
//                        "HDSD00_08_068",   // 舒张压（mmHg）
//                        "HDSD00_08_036"    // 记录日期时间
//                };
//                mergeData(section, dataSetList, innerCodes);
//            }
//        }

        // 检验
        for (String profileId : profileIds) {
            section = document.withArray("lis");
            query = "{\"q\":\"profile_id:" + profileId;
//            query += " AND EHR_000384:[" + DateTimeUtil.utcDateTimeFormat(since) + " TO " + DateTimeUtil.utcDateTimeFormat(to) + "]";
            query += "\"}";
            query = query.replace(" ", "+");
            dataSetList = sanofiService.getDataSet(BasisConstant.laboratoryProject, query);
            stdTransformDto.setVersion("56395d75b854");
            stdTransformDto.setSource(objectMapper.writeValueAsString(dataSetList));
            dataSetList = transformClient.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));
            if (dataSetList != null && dataSetList.size() > 0) {
                innerCodes = new String[]{
                        "JDSD02_03_13", // 中文名
                        "JDSD02_03_14", // 英文名
                        "JDSD02_03_04", // 结果值
                        "JDSD02_03_05", // 结果类型
                        "HDSD00_01_547",// 单位
                        "JDSD02_03_06", // 参考值上限
                        "JDSD02_03_07", // 参考值下限
                        "JDSD02_03_10"  // 创建时间
                };

                //检验项目过滤 符合要求的项目有{"PRO", "CREA", "GLU", "TCHO", "HDL-C", "TG", "K", "HbAc1", "GLU(2h)", "cTnI", "PRO", "UTP"}
                for (Map<String, Object> dataSet : dataSetList) {
                    String JDSD02_03_14 = (String) dataSet.get("JDSD02_03_14");
                    String JDSD02_03_04 = (String) dataSet.get("JDSD02_03_04");
                    String[] entryNames = LisEntry.ENTRY_NAME;
                    List<String> entryNameList = Arrays.asList(entryNames);
                    if (entryNameList.contains(JDSD02_03_14) && !StringUtils.isEmpty(JDSD02_03_04)) {
                        mergeData(section, dataSetList, innerCodes);
                    }
                    mergeData(section, dataSetList, innerCodes);
                }
            }
        }

        // 临时医嘱
        for (String profileId : profileIds) {
            section = document.withArray("stat_order");
            query = "{\"q\":\"profile_id:" + profileId;
            query += " AND EHR_000207:[" + DateTimeUtil.utcDateTimeFormat(since) + " TO " + DateTimeUtil.utcDateTimeFormat(to) + "]";
            query += "\"}";
            query = query.replace(" ", "+");
            dataSetList = sanofiService.getDataSet(BasisConstant.hospitalizedOrdersTemporary, query);
            stdTransformDto.setVersion("56395d75b854");
            stdTransformDto.setSource(objectMapper.writeValueAsString(dataSetList));
            dataSetList = transformClient.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));
            if (dataSetList != null && dataSetList.size() > 0) {
                innerCodes = new String[]{
                        "HDSD00_15_020",  //医嘱下嘱时间
                        "HDSD00_15_028"   //医嘱名称
                };
                mergeData(section, dataSetList, innerCodes);
            }
        }


        // 长期医嘱
        for (String profileId : profileIds) {
            section = document.withArray("stand_order");
            query = "{\"q\":\"profile_id:" + profileId;
            query += " AND EHR_000207:[" + DateTimeUtil.utcDateTimeFormat(since) + " TO " + DateTimeUtil.utcDateTimeFormat(to) + "]";
            query += "\"}";
            query = query.replace(" ", "+");
            dataSetList = sanofiService.getDataSet(BasisConstant.hospitalizedOrdersLongtime, query);
            stdTransformDto.setVersion("56395d75b854");
            stdTransformDto.setSource(objectMapper.writeValueAsString(dataSetList));
            dataSetList = transformClient.stdTransformList(objectMapper.writeValueAsString(stdTransformDto));
            if (dataSetList != null && dataSetList.size() > 0) {
                innerCodes = new String[]{
                        "HDSD00_15_020",  //医嘱下嘱时间
                        "HDSD00_15_026",  //医嘱停嘱时间
                        "HDSD00_15_028"   //医嘱名称
                };
                mergeData(section, dataSetList, innerCodes);
            }
        }
    }

    private void mergeData(JsonNode section, List<Map<String, Object>> dataSetList, String[] metaDataCodes) throws IOException {
        ObjectMapper objectMapper = SpringContext.getService(ObjectMapper.class);
        if (section.isArray()) {
            ArrayNode array = (ArrayNode) section;
            for (Map<String, Object> dataSet : dataSetList) {
                ObjectNode arrayNode = objectMapper.createObjectNode();
                boolean flag = false;
                for (String metaDataCode : metaDataCodes) {
                    String value = (String) dataSet.get(metaDataCode);
                    if (!StringUtils.isEmpty(value)) {
                        flag = true;
                    }
                    arrayNode.put(metaDataCode, StringUtils.isEmpty(value) ? "" : value);
                }
                if (flag) {
                    array.addPOJO(arrayNode);
                }
            }
        } else if (section.isObject()) {
            //人口学信息只要取一条记录
            Map<String, Object> dataSet = dataSetList.get(0);
            ObjectNode objectNode = (ObjectNode) section;
            for (String metaDataCode : metaDataCodes) {
                String value = dataSet.get(metaDataCode) == null ? null : dataSet.get(metaDataCode).toString();
                objectNode.put(metaDataCode, StringUtils.isEmpty(value) ? "" : value);
            }
        }
    }


}
