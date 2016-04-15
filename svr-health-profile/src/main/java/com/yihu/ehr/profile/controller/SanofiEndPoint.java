package com.yihu.ehr.profile.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.model.profile.MProfile;
import com.yihu.ehr.profile.core.ProfileDataSet;
import com.yihu.ehr.profile.core.StructedProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @ApiOperation(value = "获取体征数据", notes = "获取体征数据")
    @RequestMapping(value = RestApi.SanofiSupport.PhysicSigns, method = RequestMethod.POST)
    public String getBodySigns(
            @ApiParam(value = "档案ID列表", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @RequestParam("profile_list") String profileList[]) throws IOException, ParseException {
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ObjectNode root = objectMapper.createObjectNode();

        List<StructedProfile> profiles = new ArrayList<>();
        for (String id : profileList){
            StructedProfile structedProfile = profileRepo.findOne(id, false, false);
            profiles.add(structedProfile);
        }

        for (StructedProfile profile : profiles){
            convert(root, profile);
        }

        return root.toString();
    }

    private void convert(ObjectNode root, StructedProfile profile) throws IOException {
        JsonNode node = null;
        for (ProfileDataSet dataSet : profile.getDataSets()) {
            String[] innerCodes;
            switch (dataSet.getCode()) {
                case "HDSA00_01": { // 人口学信息
                    node = root.with("demographic_info");
                    innerCodes = new String[]{
                            "HDSA00_01_009",
                            "HDSA00_01_011",
                            "HDSA00_01_012",
                            "HDSA00_01_017"};
                }
                break;

                case "HDSC01_02": { // 体格检查-来源：门诊挂号
                    node = root.withArray("physical_exam");
                    innerCodes = new String[]{
                            "JDSC01_02_07",
                            "JDSC01_02_08"};
                }
                break;

                case "HDSD00_08": { // 体格检查-来源：住院护理体征记录
                    node = root.withArray("physical_exam");
                    innerCodes = new String[]{
                            "HDSD00_08_025",
                            "HDSD00_08_075",
                            "HDSD00_08_041",
                            "HDSD00_08_060",
                            "HDSD00_08_068"};
                }
                break;

                case "HDSD02_03": { // 检验
                    node = root.withArray("lis");
                    innerCodes = new String[]{
                            "JDSD02_03_13", // 中文名
                            "JDSD02_03_14", // 英文名
                            "JDSD02_03_04", // 结果值
                            "JDSD02_03_05", // 结果类型
                            "HDSD00_01_547",// 单位
                            "JDSD02_03_06", // 参考值上限
                            "JDSD02_03_07", // 参考值下限
                    };
                }
                break;

                case "HDSC02_11": { // 临时医嘱
                    node = root.withArray("stat_order");
                    innerCodes = new String[]{
                            "HDSD00_15_020",
                            "HDSD00_15_028"
                    };
                }
                break;

                case "HDSC02_12": {   // 长期医嘱
                    node = root.withArray("stand_order");
                    innerCodes = new String[]{
                            "HDSD00_15_020",
                            "HDSD00_15_026",
                            "HDSD00_15_028"
                    };
                }
                break;

                default:
                    continue;
            }

            ProfileDataSet echoDataSet = profileRepo.findDataSet(profile.getCdaVersion(),
                    dataSet.getCode(),
                    dataSet.getRecordKeys(),
                    innerCodes).getRight();

            if (node.isArray()) {
                ArrayNode array = (ArrayNode) node;
                for (String recordKey : echoDataSet.getRecordKeys()) {
                    ObjectNode arrayNode = array.addObject();
                    Map<String, String> record = echoDataSet.getRecord(recordKey);
                    for (String innerCode : record.keySet()) {
                        arrayNode.put(innerCode, record.get(innerCode));
                    }
                }
            } else if (node.isObject()) {
                ObjectNode objectNode = (ObjectNode) node;
                for (String recordKey : echoDataSet.getRecordKeys()) {
                    Map<String, String> record = echoDataSet.getRecord(recordKey);
                    for (String innerCode : record.keySet()) {
                        objectNode.put(innerCode, record.get(innerCode));
                    }

                    break;  // 就一行
                }
            }
        }
    }
}
