package com.yihu.ehr.profile.service.old;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.profile.model.MedicationStat;
import com.yihu.ehr.profile.service.template.ArchiveTemplateService;
import com.yihu.ehr.profile.util.BasicConstant;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

/**
 * @author hzp 2016-05-26
 */
public class PatientInfoDetailService {

    @Autowired
    private ResourceClient resource;
    //字典信息服务
    @Autowired
    private DictClient dictService;
    @Autowired
    private MedicationStatService medicationStatService;
    @Autowired
    private ArchiveTemplateService templateService;
    //CDA服务
    @Autowired
    private CDADocumentClient cdaService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TransformClient transform;
    @Autowired
    private ThridPrescriptionService thridPrescriptionService;

    /**
     * fastDfs服务器地址
     */
    @Value("${fast-dfs.public-server}")
    private String fastDfsUrl;

    @Value("${spring.application.id}")
    private String appId;

    /**
     * 【机构权限控制】通过身份证获取相关rowkeys
     */
    private String getProfileIds(String demographicId,String saasOrg) throws Exception {
        String re = "";
        //获取相关门诊住院记录
        Envelop main = resource.getResources(BasicConstant.patientEvent, "*", "*", "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (main.getDetailModelList() != null && main.getDetailModelList().size() > 0) {
            //主表rowkey条件
            StringBuilder rowkeys = new StringBuilder();
            for (Map<String, Object> map : (List<Map<String, Object>>) main.getDetailModelList()) {
                if (rowkeys.length() > 0) {
                    rowkeys.append(" OR ");
                }
                rowkeys.append(ResourceCells.PROFILE_ID + ":" + map.get("rowkey").toString());
            }

            re = "(" + rowkeys.toString() + ")";
        } else {
            re = ResourceCells.PROFILE_ID + ":(NOT *)";
        }

        return re;
    }

    /******************************* 用药信息 ***********************************************************/

    /*
     * 【机构权限控制】患者常用药（根据处方中次数）
     */
    public List<Map<String, Object>> getMedicationUsed(String demographicId, String hpCode) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        String rowkeys = getProfileIds(demographicId,null);

        String xyQueryParams = "{\"q\":\"" + rowkeys + "\"}";
        String zyQueryParams = "{\"q\":\"" + rowkeys + "\"}";
        if (hpCode != null && hpCode.length() > 0) {
            List<MDrugDict> drugList = dictService.getDrugDictListByHpCode(hpCode);
            if (drugList != null && drugList.size() > 0) {
                String drugQuery = "";
                for (MDrugDict drug : drugList) {
                    //drug.getType()是否需要药品类型判断***************
                    if (drugQuery.length() == 0) {
                        drugQuery = "{key}:" + drug.getName();
                    } else {
                        drugQuery += " OR {key}:" + drug.getName();
                    }
                }
                xyQueryParams = "{\"q\":\"" + rowkeys + " AND (" + drugQuery.replace("{key}", BasicConstant.xymc) + ")\"}";
                zyQueryParams = "{\"q\":\"" + rowkeys + " AND (" + drugQuery.replace("{key}", BasicConstant.zymc) + ")\"}";
            }
        }

        //西药统计
        Envelop resultWestern = resource.getResources(BasicConstant.medicationWesternStat, "*", "*", xyQueryParams.replace(" ", "+"), null, null);
        if (resultWestern.getDetailModelList() != null && resultWestern.getDetailModelList().size() > 0) {
            List<Map<String, Object>> list = resultWestern.getDetailModelList();
            for (Map<String, Object> map : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", map.get(BasicConstant.xymc));
                item.put("count", map.get("$count"));
                re.add(item);
            }
        }
        //中药统计
        Envelop resultChinese = resource.getResources(BasicConstant.medicationChineseStat, "*", "*", zyQueryParams.replace(" ", "+"), null, null);
        if (resultChinese.getDetailModelList() != null && resultChinese.getDetailModelList().size() > 0) {
            List<Map<String, Object>> list = resultChinese.getDetailModelList();
            for (Map<String, Object> map : list) {
                Map<String, Object> item = new HashMap<>();
                item.put("name", map.get(BasicConstant.zymc));
                item.put("count", map.get("$count"));
                re.add(item);
            }
        }

        //自定义排序规则，进行排序
        Collections.sort(re, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Integer d1 = (Integer) o1.get("count");
                Integer d2 = (Integer) o2.get("count");
                return d1.compareTo(d2);
            }
        });
        return re;
    }


    /*
     * 患者用药清单（根据数量，近三个月/近六个月）
     */
    public List<MedicationStat> getMedicationStat(String demographicId, String hpCode) throws Exception {
        List<MedicationStat> re = new ArrayList<>();
        List<String> drugList = new ArrayList<>();
        if (hpCode != null && hpCode.length() > 0) {
            List<MDrugDict> drugDictList = dictService.getDrugDictListByHpCode(hpCode);
            if (drugDictList != null && drugDictList.size() > 0) {
                String drugQuery = "";
                for (MDrugDict drug : drugDictList) {
                    drugList.add(drug.getName());
                }
            } else {
                return re;
            }
        }

        re = medicationStatService.getMedicationStat(demographicId, drugList);
        return re;
    }

    /*
     * 处方细表记录
     * 1.西药处方；2.中药处方
     */
    public List<Map<String, Object>> getMedicationDetail(String prescriptionNo, String type) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        String queryParams = BasicConstant.cfbh + ":" + prescriptionNo;
        String resourceCode = BasicConstant.medicationWestern;
        if (type != null && type.equals("2")) //默认西药
        {
            resourceCode = BasicConstant.medicationChinese;
        }
        Envelop result = resource.getResources(resourceCode, "*", "*","{\"q\":\"" + queryParams + "\"}", null, null);
        re = result.getDetailModelList();
        return re;
    }

    /*
     * 【机构权限控制】处方主表记录
     */
    public List<Map<String, Object>> getMedicationMaster(String demographicId, String profileId, String prescriptionNo,String saasOrg) throws Exception {
        String queryParams = "";
        if (prescriptionNo != null && prescriptionNo.length() > 0) {
            queryParams = BasicConstant.cfbh + ":" + prescriptionNo;
        } else {
            if (profileId != null && profileId.length() > 0) {
                queryParams = ResourceCells.PROFILE_ID + ":" + profileId;
            } else {
                queryParams = getProfileIds(demographicId,saasOrg);
            }
        }

        queryParams = "{\"q\":\"" + queryParams + "\"}";
        //获取数据
        Envelop result = resource.getResources(BasicConstant.medicationMaster, "*","*", queryParams.replace(" ", "+"), null, null);

        return result.getDetailModelList();
    }

    /**
     * 【机构权限控制】患者处方（可分页）
     * 1.西药处方；2.中药处方
     */
    public Envelop getMedicationList(String type, String demographicId, String hpCode, String startTime, String endTime, Integer page, Integer size,String saasOrg) throws Exception {
        String q = "";
        String date = BasicConstant.xysj;
        String name = BasicConstant.xymc;
        String resourceCode = BasicConstant.medicationWestern;
        if (type != null && type.equals("2")) //默认查询西药
        {
            date = BasicConstant.zysj;
            name = BasicConstant.zymc;
            resourceCode = BasicConstant.medicationChinese;
        }

        //时间范围
        if (startTime != null && startTime.length() > 0 && endTime != null && endTime.length() > 0) {
            q = date + ":[" + startTime + " TO " + endTime + "]";
        } else {
            if (startTime != null && startTime.length() > 0) {
                q = date + ":[" + startTime + " TO *]";
            } else if (endTime != null && endTime.length() > 0) {
                q = date + ":[* TO " + endTime + "]";
            }
        }

        //健康问题
        if (hpCode != null && hpCode.length() > 0) {
            //健康问题->药品代码
            List<MDrugDict> drugList = dictService.getDrugDictListByHpCode(hpCode);
            String ypQuery = "";
            if (drugList != null && drugList.size() > 0) {
                //遍历药品列表
                for (MDrugDict drug : drugList) {
                    String dictCode = drug.getCode();
                    if (ypQuery.length() > 0) {
                        ypQuery += " OR " + name + ":" + dictCode;
                    } else {
                        ypQuery = name + ":" + dictCode;
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
        String rowkeys = "";
        if (demographicId != null && demographicId != "")
            rowkeys = getProfileIds(demographicId,saasOrg);
        if (!("profile_id:(NOT *)").equals(rowkeys) && rowkeys != "") {
            if (q.length() > 0) {
                q += " AND (" + rowkeys.toString() + ")";
            } else {
                q = "(" + rowkeys.toString() + ")";
            }
        } else if (("profile_id:(NOT *)").equals(rowkeys) && demographicId != null) {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }
        if (q == "")
            q = "*:*";
        String queryParams = "{\"q\":\"" + q + "\"}";
        return resource.getResources(resourceCode, appId, null,queryParams.replace(" ", "+"), page, size);
    }


    /*************************  分页查细表数据，简单公用方法 *************************************************/
    /**
     * 【机构权限控制】处方主表、处方细表、处方笺
     * 门诊诊断、门诊症状、门诊费用汇总、门诊费用明细
     * 住院诊断、住院症状、住院费用汇总、住院费用明细、住院临时医嘱、住院长期医嘱、住院死亡记录
     * 检查报告单、检查报告单图片
     * 检验报告单、检验报告单项目
     * 手术记录
     */
    public Envelop getProfileSub(String resourceCode, String demographicId, String profileId, String eventNo, Integer page, Integer size,String saasOrg) throws Exception {
        if (demographicId == null && profileId == null && eventNo == null) {
            throw new Exception("非法传参！");
        }

        String queryParams = "";

        if (profileId != null) {
            queryParams = "profile_id:" + profileId;
        } else {
            if (eventNo != null) {
                //获取相关门诊住院记录
                Envelop main = resource.getResources(BasicConstant.patientEvent, "*","*", "{\"q\":\"event_no:" + eventNo + "\"}", 1, 1);
                if (main.getDetailModelList() != null && main.getDetailModelList().size() > 0) {
                    profileId = ((Map<String, String>) main.getDetailModelList().get(0)).get("rowkey");
                    queryParams = "profile_id:" + profileId;
                } else {
                    throw new Exception("不存在该档案信息！（event_no:" + eventNo + "）");
                }
            } else {
                queryParams = getProfileIds(demographicId,saasOrg);
            }
        }
        return resource.getResources(resourceCode, "*", "*", "{\"q\":\"" + queryParams.replace(' ', '+') + "\"}", page, size);
    }
}
