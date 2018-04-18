package com.yihu.ehr.basic.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    /*@ApiOperation("有排班的医生列表")
    @RequestMapping(value = ServiceApi.FzCombination.FindDoctorList, method = RequestMethod.GET)
    public Envelop findDoctorList(
            @ApiParam(value = "", required = true)
            @RequestParam String entityJson) {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);
        try {

            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }*/

    @ApiOperation("医院列表")
    @RequestMapping(value = ServiceApi.FzCombination.FindHospitalList, method = RequestMethod.GET)
    public Envelop findHospitalList(
            @ApiParam(value = "分页参数，起始页，从1开始", required = true)
            @RequestParam int pageIndex,
            @ApiParam(value = "分页参数，每页条数", required = true)
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
            String hosApiUrl = "baseinfo/HospitalApi/querySimpleHospitalList";
            Map<String, Object> hosParams = new HashMap<>();
            hosParams.put("pageIndex", pageIndex);
            hosParams.put("pageSize", pageSize);
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
            Map<String, Object> hosResultMap = objectMapper.readValue(openService.callFzOpenApi(hosApiUrl, hosParams), Map.class);
            if (!"10000".equals(hosResultMap.get("Code").toString())) {
                envelop.setErrorMsg("获取福州总部医院列表，" + hosResultMap.get("Message").toString());
                return envelop;
            }

            List<Map<String, Object>> hosList = (ArrayList) hosResultMap.get("Result");
            String hosInfoApiUrl = "baseinfo/HospitalApi/querySimpleHospitalById";
            Map<String, Object> hosInfoParams = new HashMap<>();
            for (int i = 0, size = hosList.size(); i < size; i++) {
                // 获取医院详情
                hosInfoParams.clear();
                hosInfoParams.put("hospitalId", hosList.get(i).get("hospitalId").toString());
                Map<String, Object> hosInfoResultMap = objectMapper.readValue(openService.callFzOpenApi(hosInfoApiUrl, hosInfoParams), Map.class);
                if (!"10000".equals(hosResultMap.get("Code").toString())) {
                    envelop.setErrorMsg("获取福州总部医院详情，" + hosInfoResultMap.get("Message").toString());
                    return envelop;
                }

                // 医生数
                hosList.get(i).put("doctorCount", hosInfoResultMap.get("doctorCount"));
            }

            hosResultMap.put("Result", hosList);

            envelop.setObj(hosResultMap);
            envelop.setSuccessFlg(true);
        } catch (Exception e) {
            e.printStackTrace();
            envelop.setErrorMsg(e.getMessage());
        }
        return envelop;
    }

}
