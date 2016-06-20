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



    /******************************* 用药信息 ***********************************************************/
    /*
     * 患者历史用药统计
     */
    public List<Map<String, Object>> getMedicationStat(String demographicId, String hpId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //中药统计
        Envelop result = resource.getResources(BasisConstant.medicationStat, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {

        }

        return re;
    }

    /*
     * 处方细表记录
     * 1.西药处方；2.中药处方
     */
    public List<Map<String, Object>> getMedicationDetail(String prescriptionNo, String type) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();

        return re;
    }

    /*
     * 处方主表记录
     */
    public List<Map<String, Object>> getMedicationMaster(String demographicId, String profileId,String prescriptionNo) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();

        return re;
    }


    /**
     * 处方笺
     * @return
     */
    public String getMedicationPrescription(String profileId,String prescriptionNo) throws Exception
    {
        return "";
    }


    /**
     * 患者中药处方（可分页）
     * 1.西药处方；2.中药处方
     */
    public Envelop getMedicationList(String type, String demographicId, String hpId, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        String sj = BasisConstant.xysj;
        String bm = BasisConstant.xybm;
        String resourceCode = BasisConstant.medicationWestern;
        if (type != null && type.equals("2")) //默认查询西药
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
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);

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




    /*************************** 指标 **********************************************/
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
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);

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



    /*************************  查细表数据，简单公用方法 *************************************************/
    /**
     * 处方主表、处方细表、处方笺
     * 门诊诊断、门诊症状、门诊费用汇总、门诊费用明细
     * 住院诊断、住院症状、住院费用汇总、住院费用明细、住院临时医嘱、住院长期医嘱、住院死亡记录
     * 检查报告单、检查报告单图片
     * 检验报告单、检验报告单项目
     * 手术记录
     */
    public Envelop getProfileSub(String resourceCode,String demographicId, String profileId,String eventNo, Integer page, Integer size) throws Exception {
        if(demographicId==null&&profileId==null&&eventNo==null)
        {
            throw new Exception("非法传参！");
        }

        String queryParams = "";

        if(profileId!=null)
        {
            queryParams = "profile_id:"+profileId;
        }
        else{
            if(eventNo!=null)
            {
                //获取相关门诊住院记录
                Envelop main = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"event_no:" + eventNo + "\"}",1,1);
                if(main.getDetailModelList()!=null&& main.getDetailModelList().size()>0)
                {
                    profileId = ((Map<String, String>)main.getDetailModelList().get(0)).get("rowkey");
                    queryParams = "profile_id:"+profileId;
                }
                else{
                    throw new Exception("不存在该档案信息！（event_no:"+eventNo+"）");
                }
            }
            else{
                //获取相关门诊住院记录
                Envelop main = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",1,1);
                if(main.getDetailModelList() != null && main.getDetailModelList().size() > 0)
                {
                    //主表rowkey条件
                    StringBuilder rowkeys = new StringBuilder();
                    for(Map<String,Object> map : (List<Map<String,Object>>)main.getDetailModelList())
                    {
                        if(rowkeys.length() > 0)
                        {
                            rowkeys.append(" OR ");
                        }
                        rowkeys.append("profile_id:" + (String)map.get("rowkey"));
                    }

                    queryParams = "(" + rowkeys.toString() +")";
                }

            }
        }
        return resource.getResources(resourceCode, appId, "{\"q\":\""+queryParams+"\"}", page, size);
    }
}
