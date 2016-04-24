package com.yihu.ehr.profile.controller.portals.sanofi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.ProfileIndices;
import com.yihu.ehr.profile.persist.ProfileIndicesService;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
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
    private ProfileRepository profileRepo;

    @Autowired
    private ProfileIndicesService indicesService;

    @ApiOperation(value = "获取体征数据", notes = "获取体征数据")
    @RequestMapping(value = ServiceApi.SanofiSupport.PhysicSigns, method = RequestMethod.GET)
    public ResponseEntity<String> getBodySigns(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDEyNzI2MTk1MTExMzA2MjY4")
            @RequestParam(value = "demographic_id", required = false) String demographicId,
            @ApiParam(value = "患者姓名")
            @RequestParam(value = "name", required = false) String name,
            @ApiParam(value = "联系电话")
            @RequestParam(value = "telephone", required = false) String telephone,
            @ApiParam(value = "性别")
            @RequestParam(value = "gender", required = false) String gender,
            @ApiParam(value = "出生日期")
            @RequestParam(value = "birthday", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthday,
            @ApiParam(value = "起始日期", defaultValue = "2015-10-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束日期", defaultValue = "2016-10-01")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) throws IOException, ParseException {
        demographicId = demographicId == null ? null : new String(Base64.getDecoder().decode(demographicId));

        Pageable pageable = new PageRequest(0, 20);
        Page<ProfileIndices> profileIndices = indicesService.findByDemographic(demographicId, null, name, telephone, gender, birthday, since, to, pageable);

        if (profileIndices == null) return new ResponseEntity<>("", HttpStatus.NOT_FOUND);

        List<StructuredProfile> profiles = new ArrayList<>();
        for (ProfileIndices indices : profileIndices.getContent()) {
            StructuredProfile structedProfile = profileRepo.findOne(indices.getProfileId(), false, false);
            profiles.add(structedProfile);
        }

        if (profiles.size() == 0) return new ResponseEntity<>("", HttpStatus.NOT_FOUND);

        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ArrayNode document = objectMapper.createArrayNode();
        for (StructuredProfile profile : profiles) {
            ObjectNode section = objectMapper.createObjectNode();
            convert(section, profile);

            if (section.size() > 1) document.addPOJO(section);
        }

        return new ResponseEntity<>(document.toString(), HttpStatus.NOT_FOUND);
    }

    private void convert(ObjectNode document, StructuredProfile profile) throws IOException {
        JsonNode section;
        StructuredDataSet dataSet;
        String[] innerCodes;

        // 人口学信息
        dataSet = profile.getDataSet("HDSA00_01");
        if (dataSet.getRecordKeys().size() > 0){
            section = document.with("demographic_info");
            innerCodes = new String[]{
                    "HDSA00_01_009",
                    "HDSA00_01_011",
                    "HDSA00_01_012",
                    "HDSA00_01_017"};

            mergeData(section, profile, dataSet, innerCodes);
        }

        // 生命体征：住院护理体征记录
        dataSet = profile.getDataSet("HDSD00_08");
        if (dataSet != null && dataSet.getRecordKeys().size() > 0){
            section = document.withArray("vitals");
            innerCodes = new String[]{
                    "HDSD00_08_025",
                    "HDSD00_08_075",
                    "HDSD00_08_041",
                    "HDSD00_08_060",
                    "HDSD00_08_068"};

            mergeData(section, profile, dataSet, innerCodes);
        }

        // 检验
        dataSet = profile.getDataSet("HDSD02_03");
        if (dataSet != null && dataSet.getRecordKeys().size() > 0){
            section = document.withArray("lis");
            innerCodes = new String[]{
                    "JDSD02_03_13", // 中文名
                    "JDSD02_03_14", // 英文名
                    "JDSD02_03_04", // 结果值
                    "JDSD02_03_05", // 结果类型
                    "HDSD00_01_547",// 单位
                    "JDSD02_03_06", // 参考值上限
                    "JDSD02_03_07", // 参考值下限
            };

            mergeData(section, profile, dataSet, innerCodes);
        }

        // 临时医嘱
        dataSet = profile.getDataSet("HDSC02_11");
        if (dataSet != null && dataSet.getRecordKeys().size() > 0){
            section = document.withArray("stat_order");
            innerCodes = new String[]{
                    "HDSD00_15_020",
                    "HDSD00_15_028"
            };

            mergeData(section, profile, dataSet, innerCodes);
        }

        // 长期医嘱
        dataSet = profile.getDataSet("HDSC02_12");
        if (dataSet != null && dataSet.getRecordKeys().size() > 0){
            section = document.withArray("stand_order");
            innerCodes = new String[]{
                    "HDSD00_15_020",
                    "HDSD00_15_026",
                    "HDSD00_15_028"
            };

            mergeData(section, profile, dataSet, innerCodes);
        }
    }

    private void mergeData(JsonNode section, StructuredProfile profile, StructuredDataSet emptyDataSet, String[] innerCodes) throws IOException {
        StructuredDataSet dataSet = profileRepo.findDataSet(profile.getCdaVersion(),
                emptyDataSet.getCode(),
                emptyDataSet.getRecordKeys(),
                innerCodes).getRight();

        if (section.isArray()) {
            ArrayNode array = (ArrayNode) section;
            for (String recordKey : dataSet.getRecordKeys()) {
                ObjectNode arrayNode = array.addObject();
                Map<String, String> record = dataSet.getRecord(recordKey);
                for (String innerCode : innerCodes) {
                    String value = record.get(innerCode);
                    arrayNode.put(innerCode, StringUtils.isEmpty(value) ? "" : value);
                }
            }
        } else if (section.isObject()) {
            ObjectNode objectNode = (ObjectNode) section;
            for (String recordKey : dataSet.getRecordKeys()) {
                Map<String, String> record = dataSet.getRecord(recordKey);
                for (String innerCode : record.keySet()) {
                    objectNode.put(innerCode, record.get(innerCode));
                }

                break;  // 就一行
            }
        }
    }
}
