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
import com.yihu.ehr.model.standard.MCDADocument;
import com.yihu.ehr.model.standard.MCdaDataSet;
import com.yihu.ehr.profile.feign.XCDADocumentClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
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

    @Autowired
    ThridPrescriptionService thridPrescriptionService;

    @Autowired
    PrescriptionPersistService presPersistService;

    /**
     * fastDfs服务器地址
     */
    @Value("${fast-dfs.public-server}")
    private String fastDfsUrl;


    String appId = "svr-health-profile";

    /**
     * 通过身份证获取相关rowkeys
     * @return
     */
    private String getProfileIds(String demographicId) throws Exception
    {
        String re = "";
        //获取相关门诊住院记录
        Envelop main = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}",null,null);
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
                rowkeys.append(BasisConstant.profileId + ":" + map.get("rowkey").toString());
            }

            re = "(" + rowkeys.toString() +")";
        }
        else{
            re = BasisConstant.profileId+":(NOT *)";
        }

        return re;
    }

    /******************************* 用药信息 ***********************************************************/

    /*
     * 患者常用药（根据次数）
     */
    public List<Map<String, Object>> getMedicationUsed(String demographicId, String hpId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        String rowkeys = getProfileIds(demographicId);

        String xyQueryParams = "{\"q\":\""+rowkeys+"\"}";
        String zyQueryParams = "{\"q\":\""+rowkeys+"\"}";
        if(hpId!=null && hpId.length()>0)
        {
            List<MDrugDict> drugList = dictService.getDrugDictList(hpId);
            if(drugList!=null && drugList.size()>0)
            {
                String drugQuery = "";
                for(MDrugDict drug : drugList){
                    if(drugQuery.length()==0)
                    {
                        drugQuery = "{key}:"+drug.getName();
                    }
                    else{
                        drugQuery = " OR {key}:"+drug.getName();
                    }
                }
                xyQueryParams = "{\"q\":\""+rowkeys+" AND ("+ drugQuery.replace("{key}",BasisConstant.xymc) +")\"}";
                zyQueryParams = "{\"q\":\""+rowkeys+" AND ("+ drugQuery.replace("{key}",BasisConstant.zymc) +")\"}";
            }
        }

        //西药统计
        Envelop resultWestern = resource.getResources(BasisConstant.medicationWesternStat, appId, xyQueryParams.replace(" ","+"),null,null);
        if(resultWestern.getDetailModelList()!=null && resultWestern.getDetailModelList().size()>0)
        {
            List<Map<String, Object>> list = resultWestern.getDetailModelList();
            for(Map<String, Object> map:list)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("name",map.get(BasisConstant.xymc));
                item.put("count",map.get("$count"));
                re.add(item);
            }
        }
        //中药统计
        Envelop resultChinese = resource.getResources(BasisConstant.medicationChineseStat,appId,zyQueryParams.replace(" ","+"),null,null);
        if(resultChinese.getDetailModelList()!=null && resultChinese.getDetailModelList().size()>0)
        {
            List<Map<String, Object>> list = resultChinese.getDetailModelList();
            for(Map<String, Object> map:list)
            {
                Map<String, Object> item = new HashMap<>();
                item.put("name",map.get(BasisConstant.zymc));
                item.put("count",map.get("$count"));
                re.add(item);
            }
        }

        //自定义排序规则，进行排序
        Collections.sort(re, new Comparator<Map<String, Object>>()
        {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2)
            {
                Integer d1 = (Integer)o1.get("count");
                Integer d2 = (Integer)o2.get("count");
                return d1.compareTo(d2);
            }
        });
        return re;
    }

    /*
     * 患者用药清单（根据数量，近三个月/近六个月）
     */
    public List<Map<String, Object>> getMedicationStat(String demographicId, String hpId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //中药统计
        Envelop result = resource.getResources(BasisConstant.medicationWesternStat, appId, "{\"join\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {

        }
        //西药统计

        return re;
    }

    /*
     * 处方细表记录
     * 1.西药处方；2.中药处方
     */
    public List<Map<String, Object>> getMedicationDetail(String prescriptionNo, String type) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        String queryParams = BasisConstant.cfbh + ":"+prescriptionNo;
        String resourceCode = BasisConstant.medicationWestern;
        if(type!=null && type.equals("2")) //默认西药
        {
            resourceCode = BasisConstant.medicationChinese;
        }
        Envelop result = resource.getResources(resourceCode, appId, "{\"q\":\"" + queryParams + "\"}", null, null);
        re=result.getDetailModelList();
        return re;
    }

    /*
     * 处方主表记录
     */
    public List<Map<String, Object>> getMedicationMaster(String demographicId, String profileId,String prescriptionNo) throws Exception {
        String queryParams = "";
        if(prescriptionNo!=null&&prescriptionNo.length()>0)
        {
            queryParams = BasisConstant.cfbh + ":"+prescriptionNo;
        }
        else{
            if(profileId!=null&&profileId.length()>0)
            {
                queryParams = BasisConstant.profileId + ":"+profileId;
            }
            else{
                queryParams = getProfileIds(demographicId);
                if(("profile_id:(NOT *)").equals(demographicId) && demographicId!=null){
                    Envelop envelop = new Envelop();
                    envelop.setSuccessFlg(false);
                    envelop.setErrorMsg("找不到此人相关记录");

                    return envelop.getDetailModelList();
                }
            }
        }

        //获取数据
        Envelop result = resource.getResources(BasisConstant.medicationMaster, appId, "{\"q\":\"" + queryParams + "\"}", null, null);

        return result.getDetailModelList();
    }


    /**
     * 查询某个事件或某个处方处方笺信息
     * @param profileId 主表rowkey
     * @param prescriptionNo 处方编号
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getPrescription(String profileId,String prescriptionNo) throws Exception
    {
        //处方笺数据
        List<Map<String,Object>> returnMap =  new ArrayList<Map<String,Object>>();

        //profile_id为空返回null，不为空则返回处方笺信息
        if(!StringUtils.isBlank(profileId))
        {
            Map<String,Object> mainEvent = new HashMap<String,Object>();
            //根据rowkey查询门诊事件
            Envelop envelop = resource.getResources(BasisConstant.patientEvent,appId,"{\"q\":\"rowkey:" + profileId + "\"}",null,null);

            //门诊事件为空返回null，不为空获取事件信息
            if(envelop.getDetailModelList() == null || envelop.getDetailModelList().size() < 1)
            {
                return returnMap;
            }
            else
            {
                mainEvent = (Map<String,Object>)envelop.getDetailModelList().get(0);
            }

            //查询事件对应主处方信息
            Envelop mainPres = resource.getResources(BasisConstant.medicationMaster,appId,"{\"q\":\"profile_id:" + profileId
                    + (!StringUtils.isBlank(prescriptionNo) ? ("+AND+EHR_000086:" + prescriptionNo) : "")+ "\"}",null,null);

            //主处方存在查询对应处方笺是否存在，不存在则根据处方信息生成处方笺
            if(mainPres.getDetailModelList() != null && mainPres.getDetailModelList().size() > 0)
            {
                //主处方列表
                List<Map<String,Object>> mainList = mainPres.getDetailModelList();
                //待入库处方笺数据列表
                List<Map<String,String>> dataList = new ArrayList<Map<String,String>>();
                //查询处方对应处方笺
                Envelop presription = resource.getResources(BasisConstant.medicationPrescription,appId,"{\"q\":\"profile_id:"
                        + profileId + "\"}",null,null);
                //处方笺List
                List<Map<String,Object>> presriptions = presription.getDetailModelList();

                for(Map<String,Object> main : mainList)
                {
                    boolean existedFlag = false;
                    Map<String,Object> currentPres = new HashMap<String,Object>();

                    //判断对应处方笺是否存在
                    if(presriptions != null)
                    {
                        for(Map<String,Object> map : presriptions)
                        {
                            if(map.containsKey("EHR_000086") && map.get("EHR_000086").toString().equals(main.get("EHR_000086").toString()))
                            {
                                existedFlag = true;
                                currentPres = map;
                            }
                        }
                    }

                    //对应处方笺不存在则根据处方对应CDA数据生成处方笺图片
                    if(!existedFlag)
                    {
                        //处方笺数据
                        Map<String,String> data = new HashMap<String,String>();
                        //处方笺不存在则生成保存
                        String picPath = thridPrescriptionService.transformImage(profileId,mainEvent.get("org_code").toString(),mainEvent.get("cda_version").toString()
                                ,main.get("EHR_001203").toString().equals("1") ? BasisConstant.xycd : BasisConstant.zycd,0,0);
                        //处方笺文件类型
                        data.put("EHR_001194","png");
                        //处方编码
                        data.put("EHR_000086",main.get("EHR_000086").toString());
                        //处方笺图片
                        data.put("EHR_001195",picPath);

                        dataList.add(data);
                    }
                    else
                    {
                        returnMap.add(currentPres);
                    }
                }

                //新生成的处方笺保存到HBASE
                if(dataList.size() > 0)
                {
                    //处方笺保存到HBASE
                    List<Map<String,Object>> savedDataList = presPersistService.savePrescription(profileId,dataList,returnMap.size());
                    returnMap.addAll(savedDataList);
                }
            }
        }

        //返回处方笺的图片完整地址
        if(returnMap.size() > 0)
        {
            for(Map<String,Object> map : returnMap)
            {
                String fileUrl = fastDfsUrl+ "/" + map.get("EHR_001195").toString();
                map.put("EHR_001195",fileUrl);
            }
        }

        return returnMap;
    }

    /**
     * 患者中药处方（可分页）
     * 1.西药处方；2.中药处方
     */
    public Envelop getMedicationList(String type, String demographicId, String hpId, String startTime, String endTime, Integer page, Integer size) throws Exception {
        String q = "";
        String date = BasisConstant.xysj;
        String name = BasisConstant.xymc;
        String resourceCode = BasisConstant.medicationWestern;
        if (type != null && type.equals("2")) //默认查询西药
        {
            date = BasisConstant.zysj;
            name = BasisConstant.zymc;
            resourceCode = BasisConstant.medicationChinese;
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
        if (hpId != null && hpId.length() > 0) {
            //健康问题->药品代码
            List<MDrugDict> drugList = dictService.getDrugDictList(hpId);
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
        String rowkeys="";
        if(demographicId!=null && demographicId!="")
            rowkeys = getProfileIds(demographicId);
        if(!("profile_id:(NOT *)").equals(rowkeys) && rowkeys!="")
        {
            if (q.length() > 0) {
                q += " AND (" + rowkeys.toString() + ")";
            } else {
                q = "(" + rowkeys.toString() + ")";
            }
        }
        else  if(("profile_id:(NOT *)").equals(rowkeys) && demographicId!=null)
        {
            Envelop envelop = new Envelop();
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("找不到此人相关记录");

            return envelop;
        }
        if(q=="")
            q="*:*";
        String queryParams =  "{\"q\":\"" + q + "\"}";
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



    /*************************  分页查细表数据，简单公用方法 *************************************************/
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
                queryParams = getProfileIds(demographicId);
            }
        }
        return resource.getResources(resourceCode, appId, "{\"q\":\""+queryParams+"\"}", page, size);
    }
}
