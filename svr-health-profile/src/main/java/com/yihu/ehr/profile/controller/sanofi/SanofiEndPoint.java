package com.yihu.ehr.profile.controller.sanofi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.lang.SpringContext;
import com.yihu.ehr.profile.core.structured.StructuredDataSet;
import com.yihu.ehr.profile.core.structured.StructuredProfile;
import com.yihu.ehr.profile.persist.repo.ProfileRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.server.support.MulticoreSolrServerFactory;
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
    private CloudSolrServer cloudSolrServer;

    @Autowired
    private ProfileRepository profileRepo;

    private Map<String, SolrTemplate> solrTemplateMap = new HashMap<>();

    /*@ApiOperation(value = "获取体征数据", notes = "获取体征数据")
    @RequestMapping(value = RestApi.SanofiSupport.PhysicSigns, method = RequestMethod.POST)
    public ResponseEntity<List<ClinicalSign>> search(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDEyNzI2MTk1MTExMzA2MjY4")
            @RequestParam(value = "demographic_id", required = false) String demographicId,
            @ApiParam(value = "姓名")
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
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to) {
        // 搜索档案


        // 提取档案数据
        List<Demographic> demographics = page.getContent();
        if (demographics.size() == 0) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<ClinicalSign> clinicalSigns = new ArrayList<>();
        for (Demographic demographic : demographics){
            String profileId = demographic.getProfileId();

            Query query = buildQuery(profileId, Lis.CreateDate, since, to);
            Page<Lis> lis = getSolrTemplate(Lis.LisCore).queryForPage(query, Lis.class);

            ClinicalSign clinicalSign = new ClinicalSign();
            clinicalSign.setDemographic(demographic);
            clinicalSign.setLis(lis.getContent());

            clinicalSigns.add(clinicalSign);
        }

        return new ResponseEntity<>(clinicalSigns, HttpStatus.OK);
    }

    Query buildQuery(String profileId, String createDateField, Date since, Date to){
        Criteria criteria = new Criteria("rowkey").startsWith(profileId).and(createDateField).between(since, to);

        return new SimpleQuery(criteria);
    }*/

    SolrTemplate getSolrTemplate(String core) {
        SolrTemplate solrTemplate = solrTemplateMap.get(core);
        if (null == solrTemplate) {
            solrTemplate = new SolrTemplate(new MulticoreSolrServerFactory(cloudSolrServer));
            solrTemplate.setSolrCore(core);
            solrTemplate.afterPropertiesSet();
            solrTemplateMap.put(core, solrTemplate);
        }

        return solrTemplate;
    }

    @ApiOperation(value = "获取体征数据", notes = "获取体征数据")
    @RequestMapping(value = RestApi.SanofiSupport.PhysicSigns, method = RequestMethod.POST)
    public String getBodySigns(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDEyNzI2MTk1MTExMzA2MjY4")
            @RequestParam(value = "demographic_id", required = false) String demographicId,
            @ApiParam(value = "姓名")
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
        ObjectMapper objectMapper = SpringContext.getService("objectMapper");
        ArrayNode root = objectMapper.createArrayNode();

        List<Demographic> demographics = searchProfile(demographicId, name, telephone, gender, birthday, since, to);

        List<StructuredProfile> profiles = new ArrayList<>();
        for (Demographic demographic : demographics) {
            StructuredProfile structedProfile = profileRepo.findOne(demographic.getProfileId(), false, false);
            profiles.add(structedProfile);
        }

        for (StructuredProfile profile : profiles) {
            ObjectNode objectNode = root.addObject();
            convert(objectNode, profile);
        }

        return root.toString();
    }

    private List<Demographic> searchProfile(String demographicId,
                                            String name,
                                            String telephone,
                                            String gender,
                                            Date birthday,
                                            Date since,
                                            Date to) {
        Criteria criteria = null;
        if (StringUtils.isNotEmpty(demographicId)) {
            criteria = new Criteria(Demographic.IdCardNo).contains(new String(com.yihu.ehr.util.encode.Base64.decode(demographicId)));
        } else if (StringUtils.isNotEmpty(name)) {
            criteria = new Criteria(Demographic.LegacyName).contains(name).or(new Criteria(Demographic.Name).contains(name));

            if (StringUtils.isNotEmpty(telephone)) {
                criteria = criteria.connect();
                criteria.and(new Criteria(Demographic.LegacyTelephone).contains(telephone).or(new Criteria(Demographic.Telephone).contains(telephone)));
            } else if (StringUtils.isNotEmpty(gender) && birthday != null) {
                criteria = criteria.connect();
                criteria = criteria.and(new Criteria(Demographic.LegacyGender).contains(gender).or(new Criteria(Demographic.Gender).contains(gender)));

                criteria = criteria.connect();
                criteria = criteria.and(new Criteria(Demographic.Birthday).between(DateUtils.addDays(birthday, -3), DateUtils.addDays(birthday, 3)));
            }
        }

        Page<Demographic> page = getSolrTemplate(Demographic.DemographicCore).queryForPage(new SimpleQuery(criteria), Demographic.class);
        return page.getContent();
    }

    private void convert(ObjectNode root, StructuredProfile profile) throws IOException {
        JsonNode node = null;
        for (StructuredDataSet dataSet : profile.getDataSets()) {
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

            StructuredDataSet echoDataSet = profileRepo.findDataSet(profile.getCdaVersion(),
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
