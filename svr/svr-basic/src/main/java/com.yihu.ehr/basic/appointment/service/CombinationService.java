package com.yihu.ehr.basic.appointment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 组合福州总部的预约挂号接口为我方需要的数据结构 Service
 *
 * @author 张进军
 * @date 2018/4/18 20:27
 */
@Service
public class CombinationService {

    // 排班接口
    private String schedulingApi = "gh/GhOpen/QueryGhtArrangeWater";
    // 医生列表接口
    private String doctorListApi = "baseinfo/DoctorInfoApi/querySimpleDoctorList";
    // 医生详情接口
    private String docInfoApi = "baseinfo/DoctorInfoApi/querySimpleDoctorBySn";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OpenService openService;

    /**
     * 从医生列表接口中，获取一页数量有排班的医生，
     * 根据排班接口确定医生是否有排班，没有则移除。
     *
     * @param doctorList 返回医生集合的容器
     * @param flagMap    标记容器，包括：
     *                   lastPageIndex：标记医生列表上次查询到第几页，头次为 0
     *                   lastPageNo：标记医生列表上次那页遍历到第几条，头次为 0
     * @param params     参数
     */
    public List<Map<String, Object>> getOnePageDoctorList(List<Map<String, Object>> doctorList,
                                                          Map<String, Integer> flagMap,
                                                          Map<String, Object> params) throws Exception {
        int lastPageIndex = (int) flagMap.get("lastPageIndex");
        int lastPageNo = (int) flagMap.get("lastPageNo");
        int pageIndex = (int) params.get("pageIndex");
        int pageSize = (int) params.get("pageSize");
        String hospitalId = params.get("hospitalId").toString();
        String hosDeptId = params.get("hosDeptId").toString();
        Object registerDate = params.get("registerDate");
        int originLastPageNo = lastPageNo;
        Map<String, Object> tParams = new HashMap<>();

        tParams.clear();
        // 判断上次那页医生数据是否用完，没用完则接着那页开始查询。
        if (originLastPageNo != 0 && originLastPageNo < pageSize) {
            tParams.put("pageIndex", lastPageIndex);
        } else {
            lastPageIndex++;
            tParams.put("pageIndex", lastPageIndex);
        }
        tParams.put("pageSize", pageSize);
        tParams.put("hospitalId", hospitalId);
        tParams.put("hosDeptId", hosDeptId);
        Map<String, Object> doctorsResMap = objectMapper.readValue(
                openService.callFzOpenApi(doctorListApi, params), Map.class);
        if (!"10000".equals(doctorsResMap.get("Code").toString())) {
            throw new ApiException("获取总部医生列表时，" + doctorsResMap.get("Message").toString());
        }
        List<Map<String, Object>> resDoctorList = ((ArrayList) doctorsResMap.get("Result"));

        int resDocListSize = resDoctorList.size();
        if (resDocListSize == 0) {
            return doctorList;
        }

        for (int i = 0; i < resDocListSize; i++) {
            // 判断上次那页医生数据是否用完，没用完则接着那页未用的接着遍历。
            Map<String, Object> doctor = null;
            if (originLastPageNo != 0 && originLastPageNo < pageSize) {
                doctor = resDoctorList.get(originLastPageNo + 1);
            } else {
                if (i == 0) {
                    lastPageNo = 0;
                }
                doctor = resDoctorList.get(i);
            }
            lastPageNo++;

            // 获取医生的排班
            tParams.clear();
            tParams.put("pageIndex", 1);
            tParams.put("pageSize", 100);
            tParams.put("hospitalId", hospitalId);
            tParams.put("doctorSn", doctor.get("doctorSn"));
            tParams.put("hosDeptId", hosDeptId);
            if (registerDate != null) {
                tParams.put("registerDate", registerDate);
            }
            Map<String, Object> schedulingResMap = objectMapper.readValue(
                    openService.callFzOpenApi(schedulingApi, tParams), Map.class);
            if (!"10000".equals(schedulingResMap.get("Code").toString())) {
                throw new ApiException("获取总部排班列表时，" + schedulingResMap.get("Message").toString());
            }
            List<Map<String, Object>> schedulingList = (ArrayList) schedulingResMap.get("Result");

            if (schedulingResMap.size() > 0) {
                // 过滤掉需代缴挂号费的排班
                List<Map<String, Object>> freeSchedulingList = new ArrayList<>();
                for (int j = 0, jSize = schedulingList.size(); j < jSize; j++) {
                    if ((int) schedulingList.get(j).get("ghfeeWay") == 0) {
                        freeSchedulingList.add(schedulingList.get(j));
                    }
                }

                if (freeSchedulingList.size() != 0) {
                    // 赋值医生排班
                    doctor.put("schedulingList", freeSchedulingList);
                    // 获取医生详情
                    tParams.clear();
                    tParams.put("doctorSn", doctor.get("doctorSn"));
                    Map<String, Object> docResMap = objectMapper.readValue(
                            openService.callFzOpenApi(docInfoApi, tParams), Map.class);
                    if (!"10000".equals(docResMap.get("Code").toString())) {
                        throw new ApiException("获取总部医生详情时，" + docResMap.get("Message").toString());
                    }
                    docResMap.remove("Code");
                    docResMap.remove("Message");
                    doctor.putAll(docResMap);

                    doctorList.add(doctor);
                }
            }

            // 当医生数据不足，或收集满当前分页条数的医生数量，则停止收集。
            if ((i == (resDocListSize - 1) && resDocListSize < pageSize) || doctorList.size() == pageSize) {
                break;
            }
        }

        flagMap.put("lastPageNo", lastPageNo);
        flagMap.put("lastPageIndex", lastPageIndex);
        // 当医生数据充足，并且之前存在没有排班的医生，则递归补满一页医生
        if (resDocListSize == pageSize && doctorList.size() < pageSize) {
            getOnePageDoctorList(doctorList, flagMap, params);
        }

        return doctorList;
    }

    /**
     * 从排班列表中，去重获取有排班的医生总数
     */
    public Integer getTotalDoctors(Map<String, Object> params) throws Exception {
        Integer count = 0;
        Integer pageIndex = (Integer) params.get("pageIndex");
        Integer pageSize = (Integer) params.get("pageSize");

        Map<String, Object> schedulingResMap = objectMapper.readValue(
                openService.callFzOpenApi(schedulingApi, params), Map.class);
        if (!"10000".equals(schedulingResMap.get("Code").toString())) {
            throw new ApiException("获取总部排班列表时，" + schedulingResMap.get("Message").toString());
        }

        List<Map<String, Object>> schList = (ArrayList) schedulingResMap.get("Result");
        Set<String> doctorSnSet = new HashSet<>();
        schList.forEach(item -> {
            // 过滤掉需代缴挂号费的排班
            if ((int) item.get("ghfeeWay") == 0) {
                doctorSnSet.add(item.get("doctorSn").toString());
            }
        });
        count += doctorSnSet.size();

        // 递归获取
        if (schList.size() == pageSize) {
            params.put("pageIndex", pageIndex + 1);
            count += getTotalDoctors(params);
        }

        return count;
    }

}
