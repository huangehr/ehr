package com.yihu.ehr.profile.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.data.hbase.HBaseDao;
import com.yihu.ehr.data.hbase.ResultUtil;
import com.yihu.ehr.data.hbase.TableBundle;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.model.standard.MCdaDataSet;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoDetailService {
    @Autowired
    XResourceClient resource;

    //特殊字典信息服务
    @Autowired
    CD10Service dictService;

    //模板服务
    @Autowired
    XTemplateRepository templateRepository;

    @Autowired
    TemplateService templateService;

    //CDA服务
    @Autowired
    XCDADocumentClient cdaService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    XTransformClient transform;

    @Autowired
    HBaseDao hBaseDao;


    String appId = "svr-health-profile";

    public static final String Table = "RawFiles";



    //患者历史用药统计（未完成）
    public List<Map<String, Object>> getDrugListStat(String demographicId, String hpId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //中药统计
        Envelop result = resource.getResources(BasisConstant.medicationStat, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}",1,1000);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {

        }

        return re;
    }

    /**
     * 患者历史用药记录（可分页）
     * 0 中药处方 1西药处方
     */
    public Envelop getDrugList(String demographicId, String hpId, String type, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        String sj = BasisConstant.xysj;
        String bm = BasisConstant.xybm;
        String resourceCode = BasisConstant.medicationWestern;
        if (type != null && type.equals("1")) //默认查询西药
        {
            sj = BasisConstant.zysj;
            bm = BasisConstant.zybm;
            resourceCode = BasisConstant.medicationChinese;
        }

        //时间范围
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            q = sj + ":[" + startTime + " TO " + endTime + "]";
        } else {
            if (startTime != null && startTime.length() > 0) {
                q = sj + ":[" + startTime + " TO *]";
            } else if (endTime != null && endTime.length() > 0) {
                q = sj + ":[* TO " + endTime + "]";
            }
        }

        //健康问题
        if (hpId != null && hpId.length() > 0) {
            //健康问题->药品代码
            List<MDrugDict> drugList = dictService.getDrugDictList(hpId);
            String ypQuery = "";
            if (drugList != null && drugList.size() > 0) {
                //遍历药品列表
                for (MDrugDict drug : drugList) {
                    String dictCode = drug.getCode();
                    if (ypQuery.length() > 0) {
                        ypQuery += " OR " + bm + ":" + dictCode;
                    } else {
                        ypQuery = bm + ":" + dictCode;
                    }
                }
            }

            if (ypQuery.length() > 0) {
                if (q.length() > 0) {
                    q += " AND (" + ypQuery + ")";
                } else {
                    q = "(" + ypQuery + ")";
                }
            }
        }

        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",1,1000);

        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            StringBuilder rowkeys = new StringBuilder();

            for (Map<String, Object> event : eventList) {
                if (rowkeys.length() > 0) {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            if(rowkeys.length() > 0)
            {
                if (q.length() > 0) {
                    q += " AND (" + rowkeys.toString() + ")";
                } else {
                    q = "(" + rowkeys.toString() + ")";
                }
            }
        }
        else
        {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }

        String queryParams =  "{\"q\":\"" + q + "\"}";//"{\"join\":\"demographic_id:" + demographicId + "\"}";
        //        if (q.length() > 0) {
        //            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        //        }
        return resource.getResources(resourceCode, appId, queryParams, page, size);
    }

    /**
     * 检验指标（可分页）
     */
    public Envelop getHealthIndicators(String demographicId, String hpId, String medicalIndexId, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        //时间范围
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            q = BasisConstant.jysj + ":[" + startTime + " TO " + endTime + "]";
        } else {
            if (startTime != null && startTime.length() > 0) {
                q = BasisConstant.jysj + ":[" + startTime + " TO *]";
            } else if (endTime != null && endTime.length() > 0) {
                q = BasisConstant.jysj + ":[* TO " + endTime + "]";
            }
        }

        //指标ID不为空
        if (medicalIndexId != null && medicalIndexId.length() > 0) {
            if (q.length() > 0) {
                q += " AND " + BasisConstant.jyzb + ":" + medicalIndexId;
            } else {
                q = BasisConstant.jyzb + ":" + medicalIndexId;
            }
        } else {
            //健康问题
            if (hpId != null && hpId.length() > 0) {
                //健康问题->指标代码
                List<MIndicatorsDict> indicatorsList = dictService.getIndicatorsDictList(hpId);
                String jyzbQuery = "";
                if (indicatorsList != null && indicatorsList.size() > 0) {
                    //遍历指标列表
                    for (MIndicatorsDict indicators : indicatorsList) {
                        if (jyzbQuery.length() > 0) {
                            jyzbQuery += " OR " + BasisConstant.jyzb + ":" + indicators.getCode();
                        } else {
                            jyzbQuery = BasisConstant.jyzb + ":" + indicators.getCode();
                        }
                    }
                }

                if (jyzbQuery.length() > 0) {
                    if (q.length() > 0) {
                        q += " AND (" + jyzbQuery + ")";
                    } else {
                        q = "(" + jyzbQuery + ")";
                    }
                }
            }
        }

        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",1,1000);

        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            StringBuilder rowkeys = new StringBuilder();

            for (Map<String, Object> event : eventList) {
                if (rowkeys.length() > 0) {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            if(rowkeys.length() > 0)
            {
                if (q.length() > 0) {
                    q += " AND (" + rowkeys.toString() + ")";
                } else {
                    q = "(" + rowkeys.toString() + ")";
                }
            }
        }
        else
        {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }

        String queryParams = "{\"q\":\"" + q + "\"}";//"{\"join\":\"demographic_id:" + demographicId + "\"}";
        //        if (q.length() > 0) {
        //            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        //        }
        return resource.getResources(BasisConstant.laboratoryReport, appId, queryParams, page, size);
    }

    /**
     * 门诊费用（可分页）
     */
    public Envelop getOutpatientCost(String demographicId, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        //时间范围
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            q = BasisConstant.mzfysj + ":[" + startTime + " TO " + endTime + "]";
        } else {
            if (startTime != null && startTime.length() > 0) {
                q = BasisConstant.mzfysj + ":[" + startTime + " TO *]";
            } else if (endTime != null && endTime.length() > 0) {
                q = BasisConstant.mzfysj + ":[* TO " + endTime + "]";
            }
        }

        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",1,1000);

        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            StringBuilder rowkeys = new StringBuilder();

            for (Map<String, Object> event : eventList) {
                if (rowkeys.length() > 0) {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            if(rowkeys.length() > 0)
            {
                if (q.length() > 0) {
                    q += " AND (" + rowkeys.toString() + ")";
                } else {
                    q = "(" + rowkeys.toString() + ")";
                }
            }
        }
        else
        {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }

        String queryParams = "{\"q\":\"" + q + "\"}";
        //        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
        //        if (q.length() > 0) {
        //            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        //        }
        return resource.getResources(BasisConstant.outpatientCost, appId, queryParams, page, size);
    }

    /**
     * 住院费用（可分页）
     */
    public Envelop getHospitalizedCost(String demographicId, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        //时间范围
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            q = BasisConstant.zyfysj + ":[" + startTime + " TO " + endTime + "]";
        } else {
            if (startTime != null && startTime.length() > 0) {
                q = BasisConstant.zyfysj + ":[" + startTime + " TO *]";
            } else if (endTime != null && endTime.length() > 0) {
                q = BasisConstant.zyfysj + ":[* TO " + endTime + "]";
            }
        }

        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",1,1000);

        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            StringBuilder rowkeys = new StringBuilder();

            for (Map<String, Object> event : eventList) {
                if (rowkeys.length() > 0) {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            if(rowkeys.length() > 0)
            {
                if (q.length() > 0) {
                    q += " AND (" + rowkeys.toString() + ")";
                } else {
                    q = "(" + rowkeys.toString() + ")";
                }
            }
        }
        else
        {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }

        String queryParams = "{\"q\":\"" + q + "\"}";

//        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
//        if (q.length() > 0) {
//            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
//        }
        return resource.getResources(BasisConstant.hospitalizedCost, appId, queryParams, page, size);
    }



}
