package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.profile.feign.XOrganizationClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.redis.RedisClient;
import com.yihu.ehr.schema.Icd10HpRelationKeySchema;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {
    @Autowired
    XResourceClient resource; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    //特殊字典信息服务
    @Autowired
    CD10Service dictService;

    //ICD10缓存服务
    @Autowired
    private RedisClient redisClient;

    @Autowired
    Icd10HpRelationKeySchema keySchema;

    String appId = "svr-health-profile";

    /**
     * 获取患者档案基本信息
     * @return
     * @throws Exception
     */
    public Map<String,Object> getPatientInfo(String demographicId) throws Exception
    {
        //时间排序
        Envelop result = resource.getResources(BasisConstant.patientInfo,appId,"{\"q\":\"demographic_id:"+demographicId+"\",\"sort\":\"{\\\"event_date\\\":\\\"desc\\\"}\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            List<Map<String,Object>> list = result.getDetailModelList();
            if(list.size() == 1)
            {
                return list.get(0);
            }
            else{
                Map<String,Object> re = new HashMap<>();
                /***************** 需要合并数据 **************************/
                for(Map<String,Object> obj : list)
                {
                    for(String key : obj.keySet())
                    {
                        if(!re.containsKey(key))
                        {
                            re.put(key,obj.get(key));
                        }
                    }
                }
                /*******************************************************/

                return re;
            }
        }
        else{
            throw new Exception("查无此人！");
        }
    }

    //
    /*
     * 根据患者住院门诊记录做健康问题统计
     *"healthProblemCode":"健康问题代码",
    "healthProblemName":"高血压",
    "visitTimes":"3",
    "hospitalizationTimes":"3",
    "lastVisitDate":"2015-08-03",
    "lastVisitOrg":"厦门市第一医院",
    "vlastVisitRecord":"最新就诊档案ID",
    "medicine": "胺碘酮、奎尼丁、美西律、普罗帕酮、丙吡胺、莫雷西嗪",
    "recentEvent": "2015-08 就诊 厦门市第一医院"
     */
    public List<Map<String,Object>> getHealthProblem(String demographicId) throws Exception
    {
        List<Map<String,Object>> re = new ArrayList<>();
        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent,appId,"{\"q\":\"patient_id:"+demographicId+"\",\"sort\":\"{\\\"event_date\\\":\\\"desc\\\"}\"}");
        //暂时没数据demographic_id************10291272
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            List<Map<String,Object>> eventList = (List<Map<String,Object>>)result.getDetailModelList();
            //健康问题+相关门诊记录
            Map<String,List<String>> outpatientMap = new HashMap<>();
            //健康问题+相关住院记录
            Map<String,List<String>> hospitalizedMap = new HashMap<>();

            //门诊诊断
            Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis,appId,"{\"table\":\""+BasisConstant.mzzdTable+"\",\"join\":\"demographic_id:"+demographicId+"\"}");
            if(outpatient.getDetailModelList()!=null && outpatient.getDetailModelList().size()>0)
            {
                for(int i=0;i<outpatient.getDetailModelList().size();i++)
                {
                    Map<String,Object> obj = (Map<String,Object>)outpatient.getDetailModelList().get(i);
                    if(obj.containsKey(BasisConstant.mzzd))
                    {
                        String code = obj.get(BasisConstant.mzzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        String healthProblem = redisClient.get(keySchema.icd10HpRelation(code));
                        List<String> profileList = new ArrayList<>();
                        if(outpatientMap.containsKey(healthProblem))
                        {
                            profileList = outpatientMap.get(healthProblem);
                            if(!profileList.contains(profileId))
                            {
                                profileList.add(profileId);
                            }
                        }
                        else{
                            profileList.add(profileId);
                        }
                        outpatientMap.put(healthProblem,profileList);
                    }
                }

            }

            //住院诊断
            Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis,appId,"{\"table\":\""+BasisConstant.zyzdTable+"\",\"join\":\"patient_id:"+demographicId+"\"}");//暂时没数据demographic_id************10291272
            if(hospitalized.getDetailModelList()!=null && hospitalized.getDetailModelList().size()>0)
            {
                for(int i=0;i<hospitalized.getDetailModelList().size();i++)
                {
                    Map<String,Object> obj = (Map<String,Object>)hospitalized.getDetailModelList().get(i);
                    if(obj.containsKey(BasisConstant.zyzd))
                    {
                        String code = obj.get(BasisConstant.zyzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        String healthProblem = "GZ__骨折";//redisClient.get(keySchema.icd10HpRelation(code));
                        List<String> profileList = new ArrayList<>();
                        if(hospitalizedMap.containsKey(healthProblem))
                        {
                            profileList = hospitalizedMap.get(healthProblem);
                            if(!profileList.contains(profileId))
                            {
                                profileList.add(profileId);
                            }
                        }
                        else{
                            profileList.add(profileId);
                        }
                        hospitalizedMap.put(healthProblem,profileList);
                    }
                }
            }

            //遍历所有健康问题，门诊记录和住院记录整合
            for(String key : outpatientMap.keySet()) //门诊
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = outpatientMap.get(key);
                //最近的事件
                boolean breaked = false;
                for(Map<String,Object> event : eventList)
                {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_data");
                    String orgCode = event.get("orgCode").toString(); /*******改为机构名称*********/
                    for(String profileId:profileList)
                    {
                        if(profileId.equals(rowkey))
                        {
                            Map<String,Object> obj = new HashMap<>();
                            obj.put("healthProblemCode",healthProblemCode);
                            obj.put("healthProblemName",healthProblemName);
                            obj.put("visitTimes",profileList.size());
                            obj.put("hospitalizationTimes",0);
                            obj.put("lastVisitDate",eventData);
                            obj.put("lastVisitOrg",orgCode);
                            obj.put("lastVisitRecord",profileId);
                            obj.put("recentEvent",eventData + " " + " 就诊 " +orgCode);
                            obj.put("profileId",profileId);
                            re.add(obj);
                            breaked = true;
                            break;
                        }
                        if(breaked)
                        {
                            break;
                        }
                    }
                }
            }
            for(String key : hospitalizedMap.keySet()) //住院
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = hospitalizedMap.get(key);
                //最近的事件
                boolean breaked = false;
                for(Map<String,Object> event : eventList) {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_data");
                    String orgCode = event.get("org_code").toString(); /*******改为机构名称*********/
                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            //判断是否已经存在
                            for(Map<String,Object> obj:re)
                            {
                                if(obj.get("healthProblemCode").toString().equals(healthProblemCode))
                                {
                                    obj.put("hospitalizationTimes",profileList.size());
                                    //事件是否更早***************************
                                    //if(eventData<obj.get("lastVisitDate").toString())
                                    if(true)
                                    {
                                        obj.put("lastVisitDate",eventData);
                                        obj.put("lastVisitOrg",orgCode);
                                        obj.put("lastVisitRecord",profileId);
                                        obj.put("recentEvent",eventData + " " + " 住院 " +orgCode);
                                        obj.put("profileId",profileId);
                                    }
                                }
                            }

                            //已存在
                            if(breaked)
                            {
                                break;
                            }
                            else //不存在则新增
                            {
                                Map<String,Object> obj = new HashMap<>();
                                obj.put("healthProblemCode",healthProblemCode);
                                obj.put("healthProblemName",healthProblemName);
                                obj.put("visitTimes",0);
                                obj.put("hospitalizationTimes",profileList.size());
                                obj.put("lastVisitDate",eventData);
                                obj.put("lastVisitOrg",orgCode);
                                obj.put("lastVisitRecord",profileId);
                                obj.put("recentEvent",eventData + " " + " 住院 " +orgCode);
                                obj.put("profileId",profileId);
                                re.add(obj);
                                breaked = true;
                                break;
                            }
                        }
                    }
                    if(breaked)
                    {
                        break;
                    }
                }
            }

            //健康问题用药统计（另外获取）

        }

        return re;
    }

    //（疾病模型）患者 -> 门诊住院记录
    public List<Map<String,Object>> getMedicalEvents(String demographicId,String eventType,String year,String area,String hpId,String diseaseId) throws Exception
    {
        List<Map<String,Object>> re = new ArrayList<>();
        //患者事件列表
        String q = "demographic_id:"+demographicId;
        String join = "";
        //事件类型
        if(eventType!=null && eventType.length()>0)
        {
            if(eventType.equals("1") || eventType.equals("2"))
            {
                q += " AND event_type:"+eventType;
            }
            else if(eventType.equals("3")) //根据业务来区分数据
            {
                join = " ";
            }
            else if(eventType.equals("4")){
                join = " ";
            }
        }
        //事件年份
        if(year!=null && year.length()>0)
        {
            q += " AND event_date:["+year+" TO "+(Integer.parseInt(year)+1)+"]";
        }

        String queryParams = "{\"q\":\""+q+"\"}";

        //疾病ID
        if(diseaseId!=null &&diseaseId.length()>0)
        {
            if(join.length()>0)
            {
                join += " AND ("+BasisConstant.mzzd+":"+diseaseId+" OR "+BasisConstant.zyzd+":"+diseaseId+")";
            }
            else{
                join = BasisConstant.mzzd+":"+diseaseId+" OR "+BasisConstant.zyzd+":"+diseaseId;
            }
            queryParams = "{\"q\":\""+q+"\",\"join\":\""+join+"\"}";
        }
        else{
            //健康问题
            if(hpId!=null && hpId.length()>0) {
                //健康问题->疾病ICD10代码
                List<MIcd10Dict> dictCodeList = dictService.getIcd10DictList(hpId);
                String hpJoin = "";
                if (dictCodeList != null && dictCodeList.size() > 0) {
                    String mzJoin = "";
                    String zyJoin = "";
                    //遍历疾病列表
                    for (MIcd10Dict ICD10 : dictCodeList) {
                        if (mzJoin.length() > 0) {
                            mzJoin += " OR " + BasisConstant.mzzd + ":" + ICD10.getCode();
                            zyJoin += " OR " + BasisConstant.zyzd + ":" + ICD10.getCode();
                        } else {
                            mzJoin = BasisConstant.mzzd + ":" + ICD10.getCode();
                            zyJoin = BasisConstant.zyzd + ":" + ICD10.getCode();
                        }
                    }

                    if (mzJoin.length() > 0 && zyJoin.length() > 0) {
                        hpJoin = "(" + mzJoin + ") OR (" + zyJoin + ")";
                    } else {
                        if (mzJoin.length() > 0) {
                            hpJoin = "(" + mzJoin + ")";
                        } else if (zyJoin.length() > 0) {
                            hpJoin = "(" + zyJoin + ")";
                        }
                    }
                }

                if(join.length()>0)
                {
                    join += " AND "+hpJoin;
                }
                else{
                    join = hpJoin;
                }
            }
        }

        //获取相关门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent,appId,queryParams);
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            re = result.getDetailModelList();

            if(area!=null && area.length()>0)
            {
                //通过机构代码列表获取地区列表
                List<String> orgCodelist = new ArrayList<>();
                for(int i=0;i<result.getDetailModelList().size();i++)
                {
                    Map<String,Object> obj = (Map<String,Object>)result.getDetailModelList().get(i);
                    String orgCode = obj.get("org_code").toString();

                    if(!orgCodelist.contains(orgCode))
                    {
                        orgCodelist.add(orgCode);
                    }
                }
                List<MOrganization> orglist = organization.getOrgs(orgCodelist);

                //判断是否属于区域
                for(int i=re.size()-1;i>=0;i--)
                {
                    Map<String,Object> obj = re.get(i);
                    String orgCode = obj.get("org_code").toString();

                    for(MOrganization org :orglist)
                    {
                        if(orgCode.equals(org.getOrgCode()))
                        {
                            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
                            areaCode = areaCode.substring(0,areaCode.length()-2)+"00";
                            if(areaCode.equals(area))
                            {
                                re.remove(i);
                            }
                        }
                    }
                }
            }
        }

        return re;
    }


    //患者就诊过的疾病
    public List<String> getPatientDisease(String demographicId) throws Exception
    {
        List<String> list = new ArrayList<>();
        //门诊诊断
        Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis,appId,"{\"table\":\""+BasisConstant.mzzdTable+"\",\"join\":\"demographic_id:"+demographicId+"\"}");
        if(outpatient.getDetailModelList()!=null && outpatient.getDetailModelList().size()>0) {
            for(int i=0;i<outpatient.getDetailModelList().size();i++) {
                Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                if(obj.containsKey(BasisConstant.mzzd)) {
                    String code = obj.get(BasisConstant.mzzd).toString();
                    list.add(code);
                }
            }
        }
        //住院诊断
        Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis,appId,"{\"table\":\""+BasisConstant.zyzdTable+"\",\"join\":\"patient_id:"+demographicId+"\"}");//暂时没数据demographic_id************10291272
        if(hospitalized.getDetailModelList()!=null && hospitalized.getDetailModelList().size()>0) {
            for(int i=0;i<hospitalized.getDetailModelList().size();i++) {
                Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                if(obj.containsKey(BasisConstant.zyzd)) {
                    String code = obj.get(BasisConstant.zyzd).toString();
                    list.add(code);
                }
            }
        }
        return list;
    }

    //患者就诊过的年份
    public List<String> getPatientYear(String demographicId) throws Exception
    {
        List<String> list = new ArrayList<>();
        //患者事件列表
        Envelop result = resource.getResources(BasisConstant.patientEvent,appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            for(int i=0;i<result.getDetailModelList().size();i++)
            {
                Map<String,Object> obj = (Map<String,Object>)result.getDetailModelList().get(i);
                String year = obj.get("event_date").toString().substring(0,4);
                if(!list.contains(year))
                {
                    list.add(year);
                }
            }
        }

        return list;
    }


    //患者就诊过的地区
    public List<String> getPatientArea(String demographicId) throws Exception
    {
        List<String> re = new ArrayList<>();
        //患者事件列表
        List<String> orgCodelist = new ArrayList<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent,appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            for(int i=0;i<result.getDetailModelList().size();i++)
            {
                Map<String,Object> obj = (Map<String,Object>)result.getDetailModelList().get(i);
                String orgCode = obj.get("org_code").toString();

                if(!orgCodelist.contains(orgCode))
                {
                    orgCodelist.add(orgCode);
                }
            }
        }
        //通过机构代码列表获取地区列表
        List<MOrganization> orglist = organization.getOrgs(orgCodelist);

        for(MOrganization org:orglist)
        {
            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
            areaCode = areaCode.substring(0,areaCode.length()-2) +"00"; //转换成市级代码
            if(!re.contains(areaCode))
            {
                re.add(areaCode);
            }
        }

        return re;
    }


}
