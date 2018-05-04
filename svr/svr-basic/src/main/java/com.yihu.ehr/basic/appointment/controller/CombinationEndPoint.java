package com.yihu.ehr.basic.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.appointment.service.CombinationService;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 * 组合福州总部的预约挂号接口为我方需要的数据结构
 *
 * @author 张进军
 * @date 2018/4/17 17:05
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(description = "组合福州总部的预约挂号接口为我方需要的数据结构", tags = {"预约挂号--组合福州总部的预约挂号接口为我方需要的数据结构"})
public class CombinationEndPoint {

    @Value("${fz-gateway.url}")
    private String fzGatewayUrl;
    @Value("${fz-gateway.clientId}")
    private String fzClientId;
    @Value("${fz-gateway.clientVersion}")
    private String fzClientVersion;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OpenService openService;
    @Autowired
    private CombinationService combinationService;

    /**
     * 由于总部开放平台的医生列表接口，没有就诊日期筛选条件，所以不满足于医疗云PC端预约挂号的原型设计。
     * 另求他法，先从医生列表接口获取医生，再从排班接口获取医生sn，对比去掉没有排班的医生，再获取医生详情，最后获取每个医生的排班。
     */
    @ApiOperation("有排班的医生列表")
    @RequestMapping(value = ServiceApi.FzCombination.FindDoctorList, method = RequestMethod.GET)
    public Envelop findDoctorList(
            @ApiParam(value = "医生列表分页参数，起始页，从1开始", required = true)
            @RequestParam int pageIndex,
            @ApiParam(value = "医生列表分页参数，每页条数，不能超过100", required = true)
            @RequestParam int pageSize,
            @ApiParam(value = "医生总数，头次为 0，后续后台返回", required = true)
            @RequestParam int total,
            @ApiParam(value = "标记医生列表上次查询到第几页，头次为 0，后续后台返回", required = true)
            @RequestParam int lastPageIndex,
            @ApiParam(value = "标记医生列表上次那页遍历到第几条，头次为 0，后续后台返回", required = true)
            @RequestParam int lastPageNo,
            @ApiParam(value = "医院ID", required = true)
            @RequestParam String hospitalId,
            @ApiParam(value = "科室ID", required = true)
            @RequestParam String hosDeptId,
            @ApiParam(value = "就诊日期，yyyy-MM-dd")
            @RequestParam(required = false) String registerDate) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);

        try {
            Map<String, Object> result = new HashMap<>();
            Map<String, Object> params = new HashMap<>();
            List<Map<String, Object>> doctorList = new ArrayList<>();

            // 获取一页数量有排班的医生，及其详情、排班列表
            params.clear();
            params.put("pageIndex", pageIndex);
            params.put("pageSize", pageSize);
            params.put("hospitalId", hospitalId);
            params.put("hosDeptId", hosDeptId);
            if (StringUtils.isNotEmpty(registerDate)) {
                params.put("registerDate", registerDate);
            }
            Map<String, Integer> flagMap = new HashMap<>();
            flagMap.put("lastPageIndex", lastPageIndex);
            flagMap.put("lastPageNo", lastPageNo);
            combinationService.getOnePageDoctorList(doctorList, flagMap, params);
            result.put("doctorList", doctorList);
            result.put("lastPageIndex", flagMap.get("lastPageIndex"));
            result.put("lastPageNo", flagMap.get("lastPageNo"));

            // 获取有排班的医生总数
            if (total == 0) {
                params.clear();
                params.put("pageIndex", 1);
                params.put("pageSize", 100);
                params.put("hospitalId", hospitalId);
                params.put("hosDeptId", hosDeptId);
                params.put("registerDate", registerDate);
                result.put("total", combinationService.getTotalDoctors(params));
            } else {
                result.put("total", total);
            }

            envelop.setObj(result);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

    @ApiOperation("医院列表")
    @RequestMapping(value = ServiceApi.FzCombination.FindHospitalList, method = RequestMethod.GET)
    public Envelop findHospitalList(
            @ApiParam(value = "分页参数，起始页，从1开始", required = true)
            @RequestParam int pageIndex,
            @ApiParam(value = "分页参数，每页条数，不能超过100", required = true)
            @RequestParam int pageSize,
            @ApiParam(value = "省份行政编码", required = true)
            @RequestParam String provinceCode,
            @ApiParam(value = "城市行政编码，如果是直辖市，则此字段可以不用传递，表示查询该市下全部的区域", required = true)
            @RequestParam Integer cityCode,
            @ApiParam(value = "医院名称模块查询")
            @RequestParam(required = false) String hosNameLike,
            @ApiParam(value = "是否提供预约，2有提供预约的医院")
            @RequestParam(required = false) Integer state,
            @ApiParam(value = "医院等级")
            @RequestParam(required = false) Integer levelId,
            @ApiParam(value = "医院性质，(1公立，2民营，其他数字为尚未配置)")
            @RequestParam(required = false) Integer nature) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);

        try {
            // 从总部获取医院列表
            String hosApi = "baseinfo/HospitalApi/querySimpleHospitalList";
            Map<String, Object> hosParams = new HashMap<>();
            hosParams.put("pageIndex", pageIndex);
            hosParams.put("pageSize", 100);
            hosParams.put("provinceCode", provinceCode);
            hosParams.put("cityCode", cityCode);
            if (StringUtils.isNotEmpty(hosNameLike)) {
                hosParams.put("hosNameLike", hosNameLike);
            }
            if (state != null) {
                hosParams.put("state", state);
            }
            if (levelId != null) {
                hosParams.put("levelId", levelId);
            }
            if (nature != null) {
                hosParams.put("nature", nature);
            }

            //region  正式线测试用
            List<Map<String, Object>> allHosList = new ArrayList<>();
            // 获取上饶医院
//            hosParams.put("provinceCode", "360000");
//            hosParams.put("cityCode", "361100");
            Map<String, Object> hosResMapSr = objectMapper.readValue(openService.callFzOpenApi(hosApi, hosParams), Map.class);
            if (!"10000".equals(hosResMapSr.get("Code").toString())) {
                envelop.setErrorMsg("获取福州总部医院列表，" + hosResMapSr.get("Message").toString());
                return envelop;
            }
            List<Map<String, Object>> hosListSr = (ArrayList) hosResMapSr.get("Result");
            int totalSr = (int) hosResMapSr.get("Total");
            allHosList.addAll(hosListSr);
            // 获取林芝医院
            hosParams.put("provinceCode", "540000");
            hosParams.put("cityCode", "540400");
            Map<String, Object> hosResMapLz = objectMapper.readValue(openService.callFzOpenApi(hosApi, hosParams), Map.class);
            if (!"10000".equals(hosResMapLz.get("Code").toString())) {
                envelop.setErrorMsg("获取福州总部医院列表，" + hosResMapLz.get("Message").toString());
                return envelop;
            }
            List<Map<String, Object>> hosListLz = (ArrayList) hosResMapLz.get("Result");
            int totalLz = (int) hosResMapLz.get("Total");
            allHosList.addAll(hosListLz);

            String hosInfoApi = "baseinfo/HospitalApi/querySimpleHospitalById";
            Map<String, Object> hosInfoParams = new HashMap<>();
            for (int i = 0, size = allHosList.size(); i < size; i++) {
                // 获取医院详情
                hosInfoParams.clear();
                hosInfoParams.put("hospitalId", allHosList.get(i).get("hospitalId").toString());
                Map<String, Object> hosInfoResultMap = objectMapper.readValue(openService.callFzOpenApi(hosInfoApi, hosInfoParams), Map.class);
                if (!"10000".equals(hosInfoResultMap.get("Code").toString())) {
                    envelop.setErrorMsg("获取福州总部医院详情，" + hosInfoResultMap.get("Message").toString());
                    return envelop;
                }

                // 医生数
                allHosList.get(i).put("doctorCount", hosInfoResultMap.get("doctorCount"));
            }

            hosResMapLz.put("Result", allHosList);
            hosResMapLz.put("Total", totalLz + totalSr);

            envelop.setObj(hosResMapLz);
            envelop.setSuccessFlg(true);
            //endregion  正式线测试用

            /*Map<String, Object> hosResMap = objectMapper.readValue(openService.callFzOpenApi(hosApi, hosParams), Map.class);
            if (!"10000".equals(hosResMap.get("Code").toString())) {
                envelop.setErrorMsg("获取福州总部医院列表，" + hosResMap.get("Message").toString());
                return envelop;
            }
            List<Map<String, Object>> hosList = (ArrayList) hosResMap.get("Result");

            String hosInfoApi = "baseinfo/HospitalApi/querySimpleHospitalById";
            Map<String, Object> hosInfoParams = new HashMap<>();
            for (int i = 0, size = hosList.size(); i < size; i++) {
                // 获取医院详情
                hosInfoParams.clear();
                hosInfoParams.put("hospitalId", hosList.get(i).get("hospitalId").toString());
                Map<String, Object> hosInfoResultMap = objectMapper.readValue(openService.callFzOpenApi(hosInfoApi, hosInfoParams), Map.class);
                if (!"10000".equals(hosInfoResultMap.get("Code").toString())) {
                    envelop.setErrorMsg("获取福州总部医院详情，" + hosInfoResultMap.get("Message").toString());
                    return envelop;
                }

                // 医生数
                hosList.get(i).put("doctorCount", hosInfoResultMap.get("doctorCount"));
            }

            hosResMap.put("Result", hosList);

            envelop.setObj(hosResMap);
            envelop.setSuccessFlg(true);*/
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
