package com.yihu.ehr.api.patient;

/**
 * 健康档案接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.05 11:06
 */
//@RestController
//@RequestMapping(ApiVersion.Version1_0)
//@Api(value = "health_profiles", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, description = "健康档案服务")
public class HealthProfilesEndPoint {
    /*@Autowired
    ObjectMapper objectMapper;

    @Autowired
    HealthProfileClient profileClient;

    @RequestMapping(value = "/patient/health_profiles", method = RequestMethod.GET)
    @ApiOperation(value = "获取档案列表", notes = "获取档案列表")
    public Collection<MProfile> getProfiles(
            @ApiParam(value = "身份证号,使用Base64编码", defaultValue = "NDEyNzI2MTk1MTExMzA2MjY4")
            @RequestParam("demographic_id") String demographicId,
            @ApiParam(value = "就诊机构列表", defaultValue = "41872607-9")
            @RequestParam(value = "organizations", required = false) String[] organizations,
            @ApiParam(value = "事件类型", defaultValue = "住院")
            @RequestParam(value = "event_type", required = false) String[] eventType,
            @ApiParam(value = "起始时间", defaultValue = "2015-01-01")
            @RequestParam("since") @DateTimeFormat(pattern = "yyyy-MM-dd") Date since,
            @ApiParam(value = "结束时间", defaultValue = "2016-12-31")
            @RequestParam("to") @DateTimeFormat(pattern = "yyyy-MM-dd") Date to,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) {
        return profileClient.getProfiles(demographicId,
                organizations,
                eventType,
                DateTimeUtil.simpleDateFormat(since),
                DateTimeUtil.simpleDateFormat(to),
                loadStdDataSet,
                loadOriginDataSet);
    }

    @ApiOperation(value = "获取档案", notes = "读取一份档案")
    @RequestMapping(value = "/patient/health_profiles/{profile_id}", method = RequestMethod.GET)
    public MProfile getProfile(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet) throws IOException, ParseException {
        return profileClient.getProfile(profileId, loadStdDataSet, loadOriginDataSet);
    }

    @RequestMapping(value = "/patient/health_profiles/{profile_id}/documents/{document_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集", notes = "返回指定数据集对象，若key不存在，返回错误信息")
    public MProfileDocument getProfileDocument(
            @ApiParam(value = "档案ID", defaultValue = "41872607-9_10295435_000622450_1444060800000")
            @PathVariable("profile_id") String profileId,
            @ApiParam(value = "文档ID", defaultValue = "0dae00065684e6570dc35654490aacb3")
            @PathVariable("document_id") String documentId,
            @ApiParam(value = "是否加载标准数据集", defaultValue = "true")
            @RequestParam(value = "load_std_data_set") boolean loadStdDataSet,
            @ApiParam(value = "是否加载原始数据集", defaultValue = "false")
            @RequestParam(value = "load_origin_data_set") boolean loadOriginDataSet){
        return profileClient.getProfileDocument(profileId, documentId, loadStdDataSet, loadOriginDataSet);
    }*/
}
