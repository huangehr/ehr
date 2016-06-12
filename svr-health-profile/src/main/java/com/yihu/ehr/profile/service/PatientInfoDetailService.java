package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.model.standard.MCdaDataSetRelationship;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    String appId = "svr-health-profile";


    //根据ProfileId或者EventNo查询CDA分类
    public List<Map<String, String>> getCDAClass(String profileId, String eventNo) throws Exception {
        List<Map<String, String>> re = new ArrayList<>();

        //根据profileId或者eventNo获取主记录
        String q = "{\"q\":\"rowkey:" + profileId + "\"}";
        if (profileId == null) {
            if (eventNo != null) {
                q = "{\"q\":\"event_no:" + eventNo + "\"}";
            } else {
                throw new Exception("非法传参！");
            }
        }

        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, q);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            profileId = obj.get("rowkey").toString();
            String cdaVersion = obj.get("cda_version").toString();
            String orgCode = obj.get("org_code").toString();

            //根据机构获取定制模板
            List<Template> list = templateRepository.findByOrganizationCodeAndCdaVersion(orgCode, cdaVersion);
            //遍历模板
            if (list != null && list.size() > 0) {
                for (Template template : list) {
                    String cdaDocumentId = template.getCdaDocumentId();
                    //获取CDA关联数据集************
                    List<MCdaDataSetRelationship> datasetList = cdaService.getCDADataSetRelationshipByCDAId(cdaVersion, cdaDocumentId);
                    if (datasetList != null && datasetList.size() > 0) {
                        String query = "";
                        for (MCdaDataSetRelationship dataset : datasetList) {
                            String datasetCode = "";
                            if (query.length() > 0) {
                                query += " OR rowkey:*" + datasetCode + "*";
                            } else {
                                query = "rowkey:*" + datasetCode + "*";
                            }
                        }
                        //判断是否包含相关数据
                        String queryParams = "{\"q\":\"(" + query + ") AND profile_id:" + profileId + "\"}";
                        Envelop data = resource.getEhrCenterSub(queryParams, null, null);
                        if (data.getDetailModelList() != null && data.getDetailModelList().size() > 0) {
                            Map<String, String> item = new HashMap<>();
                            item.put("profile_id", profileId);
                            item.put("template_id", String.valueOf(template.getId()));
                            item.put("template_name", template.getTitle());
                            re.add(item);
                        }
                    }
                }
            }

            return re;
        } else {
            throw new Exception("未查到相关记录！");
        }
    }

    //根据模板获取病人CDA数据
    public Map<String, List<Map<String, Object>>> getCDAData(String profileId, Integer templateId) throws Exception {
        Map<String, List<Map<String, Object>>> re = new HashMap<>();
        //主表记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"rowkey:" + profileId + "\"}");
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            //通过模板获取cda信息
            Template template = templateService.getTemplate(templateId);
            if (template != null) {
                String cdaVersion = ((Map<String, Object>) result.getDetailModelList().get(0)).get("cda_version").toString();
                String cdaDocumentId = template.getCdaDocumentId();
                //获取CDA关联数据集************
                List<MCdaDataSetRelationship> datasetList = cdaService.getCDADataSetRelationshipByCDAId(cdaVersion, cdaDocumentId);
                if (datasetList != null && datasetList.size() > 0) {
                    for (MCdaDataSetRelationship dataset : datasetList) {
                        String datasetCode = "";

                        String q = "{\"q\":\"rowkey:*" + datasetCode + "* AND profile_id:" + profileId + "\"}";
                        //获取Hbase数据
                        Envelop data = resource.getEhrCenterSub(URLEncoder.encode(q), null, null);

                        if (data.getDetailModelList() != null && data.getDetailModelList().size() > 0) {
                            List<Map<String, Object>> table = data.getDetailModelList();

                            //根据cdaVersion转译************
                            List<Map<String, Object>> dataList = new ArrayList<>();
                            re.put(datasetCode, dataList);
                        }
                    }
                }
            }
        } else {
            throw new Exception("未查到相关就诊住院记录！profileId:" + profileId);
        }


        return re;
    }

    //通过事件号获取模板
    public String getCDATemplate(String eventNo, String cdaType) throws Exception {
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"event_no:" + eventNo + "\"}");

        //是否有数据
        if (result.getDetailModelList() != null && result.getTotalCount() > 0) {
            Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(0);
            String orgCode = obj.get("org_code").toString();
            String cdaVersion = obj.get("cda_version").toString();
            //获取模板ID
            Template template = templateRepository.findByOrganizationCodeAndCdaVersionAndCdaType(orgCode, cdaVersion, cdaType);
            if (template != null) {
                return String.valueOf(template.getId());
            }
        }

        throw new Exception("未找到相关模板！");
    }


    //患者历史用药统计（未完成）
    public List<Map<String, Object>> getDrugListStat(String demographicId, String hpId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //中药统计
        Envelop result = resource.getResources(BasisConstant.medicationStat, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}");
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

        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
        if (q.length() > 0) {
            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        }
        return resource.getResources(resourceCode, appId, URLEncoder.encode(queryParams), page, size);
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


        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
        if (q.length() > 0) {
            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        }
        return resource.getResources(BasisConstant.laboratoryReport, appId, URLEncoder.encode(queryParams), page, size);
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

        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
        if (q.length() > 0) {
            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        }
        return resource.getResources(BasisConstant.outpatientCost, appId, URLEncoder.encode(queryParams), page, size);
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

        String queryParams = "{\"join\":\"demographic_id:" + demographicId + "\"}";
        if (q.length() > 0) {
            queryParams = "{\"join\":\"demographic_id:" + demographicId + "\",\"q\":\"" + q + "\"}";
        }
        return resource.getResources(BasisConstant.hospitalizedCost, appId, URLEncoder.encode(queryParams), page, size);
    }


}
