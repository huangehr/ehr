package com.yihu.ehr.profile.service;


import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {
    @Autowired
    XResourceClient resource; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    @Autowired
    XDictClient dictClient;


    @Autowired
    XGeographyClient addressClient;

    String appId = "svr-health-profile";

    //event_type对应数据集
    Map<String,String[]> eventTypeDataset = new HashMap() {
        {
            put("3", new String[]{"HDSC01_04","HDSC01_08"}); //处方
            put("4", new String[]{"HDSC02_11","HDSC02_12"}); //医嘱
            put("5", new String[]{"HDSD01_01","HDSD02_01"}); //检查检验
        }
    };

    /**
     * 返回包含event_type对应数据的事件
     */
    private  List<String> geyProfileByEventType(List<String> rowkeys,String eventType) throws Exception
    {
        List<String> re = new ArrayList<>();
        if(rowkeys!=null && rowkeys.size()>0)
        {
            String rowkeyString = "";
            String datasetString = "";
            //筛选事件
            for(String rowkey:rowkeys)
            {
                if(rowkeyString.length()>0)
                {
                    rowkeyString += " OR profile_id:"+rowkey;
                }
                else{
                    rowkeyString = "profile_id:"+rowkey;
                }
            }
            //筛选对应数据集
            String[] datasets = eventTypeDataset.get(eventType);
            if(datasets!=null && datasets.length>0)
            {
                for(String dataset:datasets)
                {
                    if(datasetString.length()>0)
                    {
                        datasetString += " OR rowkey:*"+dataset+"*";
                    }
                    else{
                        datasetString = "rowkey:*"+dataset+"*";
                    }
                }
            }
            else{
                return re;
            }

            String query = "{\"groupFields\":\"profile_id\",\"q\":\"("+rowkeyString+") AND ("+datasetString+")\"}";
            Envelop result = resource.getSubStat(query.replace(" ","+"), null ,null);
            if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
            {
                List<Map<String,Object>> list = result.getDetailModelList();
                for(Map<String,Object> map:list)
                {
                    re.add(map.get("profile_id").toString());
                }
            }
        }

        return re;
    }

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

    /**
     * 根据时间获取病龄
     * @param eventData
     * @return
     */
    private String getAgeOfDisease(Object eventData){
        SimpleDateFormat sd = new SimpleDateFormat("yyyyMM");
        String eventDataYear=eventData.toString().substring(0, 7).substring(0,4);
        String eventDataMonth=eventData.toString().substring(0, 7).substring(5,7);
        String ageOfDisease="";


        if(Integer.parseInt(sd.format(new Date()).substring(4, 6)) - Integer.parseInt(eventDataMonth)<0){
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear)-1;
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))+12- Integer.parseInt(eventDataMonth);
            if(year>0)
            {
                ageOfDisease = year+"年"+month+"个月";
            }
            else{
                ageOfDisease = month+"个月";
            }
        }
        else{
            Integer year = Integer.parseInt(sd.format(new Date()).substring(0, 4)) - Integer.parseInt(eventDataYear);
            Integer month = Integer.parseInt(sd.format(new Date()).substring(4, 6))- Integer.parseInt(eventDataMonth);
            if(year>0)
            {
                ageOfDisease = year+"年"+month+"个月";
            }
            else{
                ageOfDisease = month+"个月";
            }
        }
        return ageOfDisease;
    }

    /**
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId) throws Exception {
        //时间排序
        Envelop result = resource.getResources(BasisConstant.patientInfo, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> list = result.getDetailModelList();
            if (list.size() == 1) {
                return list.get(0);
            } else {
                Map<String, Object> re = new HashMap<>();
                /***************** 需要合并数据 **************************/
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!re.containsKey(key)) {
                            re.put(key, obj.get(key));
                        }
                    }
                }
                /*******************************************************/

                return re;
            }
        } else {
            throw new Exception("查无此人！");
        }
    }

    /*
     * @根据患者住院门诊记录做健康问题统计
     */
    public List<Map<String, Object>> getHealthProblem(String demographicId) throws Exception {
        List<Map<String, Object>> re = new ArrayList<>();
        //获取门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            List<Map<String, Object>> eventList = (List<Map<String, Object>>) result.getDetailModelList();
            //健康问题+相关门诊记录
            Map<String, List<String>> outpatientMap = new HashMap<>();
            //健康问题+相关住院记录
            Map<String, List<String>> hospitalizedMap = new HashMap<>();
            StringBuilder rowkeys = new StringBuilder();

            for(Map<String,Object> event : eventList)
            {
                if(rowkeys.length() > 0)
                {
                    rowkeys.append(" OR ");
                }
                rowkeys.append("profile_id:" + event.get("rowkey"));
            }

            String queryParams = "{\"q\":\"" + rowkeys.toString() + "\"}";
            //门诊诊断
            Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId, queryParams.replace(' ','+'),1,1000);///"{\"join\":\"demographic_id:" + demographicId + "\"}");
            if (outpatient.getDetailModelList() != null && outpatient.getDetailModelList().size() > 0) {
                for (int i = 0; i < outpatient.getDetailModelList().size(); i++) {
                    Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                    if (obj.containsKey(BasisConstant.mzzd)) {
                        String code = obj.get(BasisConstant.mzzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        MHealthProblemDict hp = dictClient.getHealthProblemByIcd10(code);
                        String healthProblem = hp.getCode() + "__" + hp.getName();
                        List<String> profileList = new ArrayList<>();
                        if (outpatientMap.containsKey(healthProblem)) {
                            profileList = outpatientMap.get(healthProblem);
                            if (!profileList.contains(profileId)) {
                                profileList.add(profileId);
                            }
                        } else {
                            profileList.add(profileId);
                        }
                        outpatientMap.put(healthProblem, profileList);
                    }
                }

            }

            //住院诊断
            Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, queryParams.replace(' ','+'),1,1000); //"{\"join\":\"demographic_id:" + demographicId + "\"}");
            if (hospitalized.getDetailModelList() != null && hospitalized.getDetailModelList().size() > 0) {
                for (int i = 0; i < hospitalized.getDetailModelList().size(); i++) {
                    Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                    if (obj.containsKey(BasisConstant.zyzd)) {
                        String code = obj.get(BasisConstant.zyzd).toString();
                        String profileId = obj.get(BasisConstant.profileId).toString();
                        //通过疾病ID获取健康问题
                        MHealthProblemDict hp = dictClient.getHealthProblemByIcd10(code);
                        String healthProblem = hp.getCode() + "__" + hp.getName();
                        List<String> profileList = new ArrayList<>();
                        if (hospitalizedMap.containsKey(healthProblem)) {
                            profileList = hospitalizedMap.get(healthProblem);
                            if (!profileList.contains(profileId)) {
                                profileList.add(profileId);
                            }
                        } else {
                            profileList.add(profileId);
                        }
                        hospitalizedMap.put(healthProblem, profileList);
                    }
                }
            }

            //遍历所有健康问题，门诊记录和住院记录整合
            for (String key : outpatientMap.keySet()) //门诊
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = outpatientMap.get(key);
                //最近的事件
                boolean breaked = false;
                for (Map<String, Object> event : eventList) {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_date");
                    String orgCode = event.get("org_code").toString();
                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            Map<String, Object> obj = new HashMap<>();
                            obj.put("healthProblemCode", healthProblemCode);
                            obj.put("healthProblemName", healthProblemName);
                            obj.put("visitTimes", profileList.size());
                            obj.put("hospitalizationTimes", 0);
                            obj.put("lastVisitDate", eventData);
                            obj.put("lastVisitOrg", orgCode);
                            obj.put("lastVisitRecord", profileId);
                            obj.put("recentEvent", "就诊");
                            obj.put("profileId", profileId);
                            obj.put("ageOfDisease",getAgeOfDisease(eventData));
                            re.add(obj);
                            breaked = true;
                            break;
                        }
                        if (breaked) {
                            break;
                        }
                    }
                }
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String key : hospitalizedMap.keySet()) //住院
            {
                String healthProblemCode = key.split("__")[0];
                String healthProblemName = key.split("__")[1];

                List<String> profileList = hospitalizedMap.get(key);
                //最近的事件
                boolean breaked = false;
                for (Map<String, Object> event : eventList) {
                    String rowkey = event.get("rowkey").toString();
                    Object eventData = event.get("event_date");
                    String orgCode = event.get("org_code").toString();
                    for (String profileId : profileList) {
                        if (profileId.equals(rowkey)) {
                            //判断是否已经存在
                            for (Map<String, Object> obj : re) {
                                if (obj.get("healthProblemCode").toString().equals(healthProblemCode)) {
                                    obj.put("hospitalizationTimes", profileList.size());
                                    //事件是否更早
                                    if (sdf.parse(eventData.toString()).getTime() > sdf.parse(obj.get("lastVisitDate").toString()).getTime()) {
                                        obj.put("lastVisitDate", eventData);
                                        obj.put("lastVisitOrg", orgCode);
                                        obj.put("lastVisitRecord", profileId);
                                        obj.put("recentEvent", "住院");
                                        obj.put("profileId", profileId);
                                    }
                                    else if(sdf.parse(eventData.toString()).getTime()  < sdf.parse(obj.get("ageOfDisease").toString()).getTime()){
                                        obj.put("ageOfDisease",getAgeOfDisease(eventData));
                                    }
                                }
                            }

                            //已存在
                            if (breaked) {
                                break;
                            } else //不存在则新增
                            {
                                Map<String, Object> obj = new HashMap<>();
                                obj.put("healthProblemCode", healthProblemCode);
                                obj.put("healthProblemName", healthProblemName);
                                obj.put("visitTimes", 0);
                                obj.put("hospitalizationTimes", profileList.size());
                                obj.put("lastVisitDate", eventData);
                                obj.put("lastVisitOrg", orgCode);
                                obj.put("lastVisitRecord", profileId);
                                obj.put("recentEvent", "住院");
                                obj.put("profileId", profileId);
                                obj.put("ageOfDisease",getAgeOfDisease(eventData));
                                re.add(obj);
                                breaked = true;
                                break;
                            }
                        }
                    }
                    if (breaked) {
                        break;
                    }
                }
            }
        }


        return re;
    }

    /*
     * 通过eventNo获取某次门诊住院记录
     */
    public Map<String, Object> getMedicalEvent(String orgCode,String eventNo) throws Exception {
        Map<String, Object> re = new HashMap<>();
        //获取相关门诊住院记录
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"org_code:"+orgCode+"+AND+event_no:" + eventNo + "\"}",1,1);
        if(result.getDetailModelList()!=null&& result.getDetailModelList().size()>0)
        {
            re = (Map<String, Object>)result.getDetailModelList().get(0);
        }
        else{
            throw new Exception("不存在该档案信息！（event_no:"+eventNo+"）");
        }
        return re;
    }

    /**
     * 获取病人门诊住院事件
     */
    public List<Map<String,Object>> getPatientEvents(String demographicId, String eventType, String year, String area, String hpCode, String diseaseId) throws Exception {
        List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
        //门诊过滤参数
        String mzQuery = "";
        //住院过滤参数
        String zyQuery = "";
        //事件过滤参数
        String query = "{\"q\":\"demographic_id:" + demographicId;
        //病人事件纪录rowkey
        String rowkeys = "";
        //疾病事件纪录rowkey
        List<String> returnRowkey = new ArrayList<String>();

        //事件类型
        if (!StringUtils.isBlank(eventType))
        {
            //0门诊 1住院 2体检 3处方 4医嘱 5检查检验
            if(eventType.equals("0") || eventType.equals("1") || eventType.equals("2"))
            {
                query += " AND "+BasisConstant.eventType+":" + eventType;
            }
        }
        //事件年份
        if (!StringUtils.isBlank(year))
        {
            query += " AND event_date:[" + year + "-01-01T00:00:00Z TO " + year + "-12-31T23:59:59Z]";
        }
        //地区
        if(!StringUtils.isBlank(area))
        {
            char[] areaCodes = area.toCharArray();
            int count = 0;

            //获取结尾0的个数
            for(int i = areaCodes.length - 1;i > -1;i--)
            {
                if(areaCodes[i] == '0')
                {
                    count++;
                }
                else
                {
                    break;
                }
            }

            //结尾0截除
            String areaSub = area.substring(0,area.length() - count);
            //获取对应地区下机构
            List<MOrganization> organizations = organization.getOrganizationByAreaCode(areaSub);

            if(organizations != null && organizations.size() > 0)
            {   //机构代码条件组合
                StringBuilder orgQuery = new StringBuilder();

                for(MOrganization org : organizations)
                {
                    if(orgQuery.length() > 0)
                    {
                        orgQuery.append(" OR ");
                    }
                    orgQuery.append("org_code:" + org.getOrgCode());
                }

                query += " AND (" + orgQuery.toString() + ")";
            }
            else
            {   //地区不为空，地区下机构为空时返回空
                return returnList;
            }
        }

        query += "\"}";

        //门诊住院事件
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, query.replace(' ','+'), null, null);
        //事件集合
        List<Map<String, Object>> eventList = new ArrayList<Map<String, Object>>();

        //获取rowkey
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            eventList = (List<Map<String, Object>>) result.getDetailModelList();

            for (Map<String, Object> event : eventList) {
                if (rowkeys.length() > 0) {
                    rowkeys += " OR ";
                }
                rowkeys += "profile_id:" + event.get("rowkey");
            }
        }
        //健康问题
        if (!StringUtils.isBlank(hpCode)) {
            //健康问题->疾病ICD10代码
            List<MIcd10Dict> dictCodeList = dictClient.getIcd10ByHpCode(hpCode);

            if (dictCodeList != null && dictCodeList.size() > 0)
            {
                //遍历疾病列表
                for (MIcd10Dict ICD10 : dictCodeList)
                {
                    if (mzQuery.length() > 0)
                    {
                        mzQuery += " OR " + BasisConstant.mzzd + ":" + ICD10.getCode();
                        zyQuery += " OR " + BasisConstant.zyzd + ":" + ICD10.getCode();
                    }
                    else
                    {
                        mzQuery = BasisConstant.mzzd + ":" + ICD10.getCode();
                        zyQuery = BasisConstant.zyzd + ":" + ICD10.getCode();
                    }
                }
            }
        }
        //疾病
        if (!StringUtils.isBlank(diseaseId))
        {
            if (mzQuery.length() > 0)
            {
                mzQuery += " OR " + BasisConstant.mzzd + ":" + diseaseId;
                zyQuery += " OR " + BasisConstant.zyzd + ":" + diseaseId;
            }
            else
            {
                mzQuery = BasisConstant.mzzd + ":" + diseaseId;
                zyQuery = BasisConstant.zyzd + ":" + diseaseId;
            }
        }
        //过滤门诊纪录
        if (!StringUtils.isBlank(mzQuery))
        {
            String queryParams = "{\"q\":\"(" + mzQuery + ") AND (" + rowkeys +")\"";
            //门诊诊断纪录
            Envelop resultMzzd = resource.getResources(BasisConstant.outpatientDiagnosis, appId, queryParams.replace(' ', '+'), null, null);

            //获取疾病门诊事件纪录rowkey
            if(resultMzzd.getDetailModelList() != null && resultMzzd.getDetailModelList().size() > 0)
            {
                for(Map<String,Object> map : (List<Map<String, Object>>)resultMzzd.getDetailModelList())
                {
                    returnRowkey.add((String)map.get("profile_id"));
                }
            }
        }
        //过滤住院纪录
        if(!StringUtils.isBlank(zyQuery))
        {
            String queryParams = "{\"q\":\"(" + zyQuery + ") AND (" + rowkeys +")\"";
            //门诊诊断纪录
            //住院诊断纪录
            Envelop resultZyzd = resource.getResources(BasisConstant.hospitalizedDiagnosis,appId,queryParams.replace(' ', '+'), null, null);

            //获取疾病住院事件纪录rowkey
            if(resultZyzd.getDetailModelList() != null && resultZyzd.getDetailModelList().size() > 0)
            {
                for (Map<String, Object> map : (List<Map<String, Object>>)resultZyzd.getDetailModelList())
                {
                    returnRowkey.add((String)map.get("profile_id"));
                }
            }
        }


        if(eventList != null)
        {

            //诊断疾病为空，全部事件
            if(StringUtils.isBlank(diseaseId) && StringUtils.isBlank(hpCode)) {
                for(Map<String,Object> map : eventList) {
                    String rowkey = map.get("rowkey").toString();
                    returnRowkey.add(rowkey);
                }
            }
            //判断该事件是否符合event_type
            if (!StringUtils.isBlank(eventType) && !eventType.equals("0") && !eventType.equals("1") && !eventType.equals("2")) {
                returnRowkey = geyProfileByEventType(returnRowkey,eventType);
            }


            //获取所有client_id->client_type数据来源
            List<MConventionalDict> clientList = dictClient.getRecordDataSourceList();
            //筛选事件
            for(Map<String,Object> map : eventList) {
                //包含该事件
                if(returnRowkey.contains(map.get("rowkey")))
                {
                    //通过client_id获取数据来源
                    map.put("data_from","");
                    String clientId = map.get("client_id").toString();
                    for(MConventionalDict client:clientList)
                    {
                        if(client.getCode().equals(clientId))
                        {
                            map.put("data_from",client.getValue());
                            break;
                        }
                    }
                    returnList.add(map);
                }
            }
        }


        return returnList;
    }

    /**
     * 通过代码获取疾病名称
     * @return
     */
    private List<Map<String, String>> getDiseaseList(List<String> codeList)
    {
        List<Map<String, String>> re = new ArrayList<>();

        if(codeList!=null && codeList.size()>0)
        {
            for(String code: codeList)
            {
                Map<String, String> map = new HashMap<>();
                map.put("code",code);
                MIcd10Dict dict = dictClient.getIcd10ByCode(code);
                if(dict!=null)
                {
                    map.put("name",dict.getName());
                }
                else{
                    map.put("name",code);
                }
                re.add(map);
            }
        }
        return re;
    }

    /*
     * @患者就诊过的疾病
     */
    public List<Map<String, String>> getPatientDisease(String demographicId) throws Exception {
        //获取门诊住院记录
        String rowkeys = getProfileIds(demographicId);
        String queryParams = "{\"q\":\""+ rowkeys +"\"}";

        List<String> codeList = new ArrayList<>();
        //门诊诊断
        Envelop outpatient = resource.getResources(BasisConstant.outpatientDiagnosis, appId, queryParams.replace(' ','+'), null, null);
        if (outpatient.getDetailModelList() != null && outpatient.getDetailModelList().size() > 0) {
            for (int i = 0; i < outpatient.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) outpatient.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.mzzd)) {
                    String code =obj.get(BasisConstant.mzzd).toString();
                    if(!codeList.contains(code))
                    {
                        codeList.add(code);
                    }
                }
            }
        }
        //住院诊断
        Envelop hospitalized = resource.getResources(BasisConstant.hospitalizedDiagnosis, appId, queryParams.replace(' ','+'),1,1000);
        if (hospitalized.getDetailModelList() != null && hospitalized.getDetailModelList().size() > 0) {
            for (int i = 0; i < hospitalized.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) hospitalized.getDetailModelList().get(i);
                if (obj.containsKey(BasisConstant.zyzd)) {
                    String code =obj.get(BasisConstant.zyzd).toString();
                    if(!codeList.contains(code))
                    {
                        codeList.add(code);
                    }
                }
            }
        }
        return getDiseaseList(codeList);
    }


    /*
     * @患者就诊过的年份
     */
    public List<String> getPatientYear(String demographicId) throws Exception {
        List<String> list = new ArrayList<>();
        //患者事件列表
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            for (int i = 0; i < result.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(i);
                String year = obj.get("event_date").toString().substring(0, 4);
                if (!list.contains(year)) {
                    list.add(year);
                }
            }
        }

        return list;
    }


    /*
     * @患者就诊过的地区
     */
    public List<Map<String, String>> getPatientArea(String demographicId) throws Exception {
        List<Map<String, String>> organizationMapList = new ArrayList<>();

        //患者事件列表
        List<String> orgCodeList = new ArrayList<>();
        Envelop result = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\"demographic_id:" + demographicId + "\"}", null, null);
        if (result.getDetailModelList() != null && result.getDetailModelList().size() > 0) {
            for (int i = 0; i < result.getDetailModelList().size(); i++) {
                Map<String, Object> obj = (Map<String, Object>) result.getDetailModelList().get(i);
                String orgCode = obj.get("org_code").toString();

                if (!orgCodeList.contains(orgCode)) {
                    orgCodeList.add(orgCode);
                }
            }
        }

        //通过机构代码列表获取地区列表
        List<MOrganization> orgList = organization.getOrgs(orgCodeList);
        List<String> array = new ArrayList<>();
        for (MOrganization org : orgList) {
            Map<String, String> organizationMap = new HashMap<>();
            String areaCode = new Integer(org.getAdministrativeDivision()).toString();
            areaCode = areaCode.substring(0, areaCode.length() - 2) + "00"; //转换成市级代码
            if (!array.contains(areaCode)) {
                array.add(areaCode);
                organizationMap.put("code", areaCode);

                MGeographyDict area = addressClient.getAddressDictById(areaCode);
                if(area !=null)
                {
                    String areaName = area.getName();
                    organizationMap.put("name", areaName);
                }
                else{
                    organizationMap.put("name", areaCode);
                }
                organizationMapList.add(organizationMap);
            }
        }
        return organizationMapList;
    }


    /**
     * 全文检索
     */
    public Envelop getProfileLucene(String startTime,String endTime,List<String> lucene,Integer page,Integer size) throws Exception
    {
        String queryParams = "";
        if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0)
        {
            queryParams = BasisConstant.eventDate+":["+startTime+" TO "+endTime+"]";
        }
        else {
            if(startTime!=null && startTime.length()>0)
            {
                queryParams = BasisConstant.eventDate+":["+startTime+" TO *]";
            }
            else if(endTime!=null && endTime.length()>0){
                queryParams = BasisConstant.eventDate+":[* TO "+endTime+"]";
            }
        }

        //全文检索
        Envelop re = resource.getResources(BasisConstant.patientEvent, appId, "{\"q\":\""+queryParams.replace(' ','+')+"\"}", page, size);
        return re;
    }
}
