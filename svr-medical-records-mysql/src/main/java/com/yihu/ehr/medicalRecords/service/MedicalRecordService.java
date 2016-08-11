package com.yihu.ehr.medicalRecords.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrPatientsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hzp on 2016/7/14.
 */

@Service
public class MedicalRecordService{

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    DoctorMedicalRecordDao doctorMedicalRecordDao;

    @Autowired
    PatientService patientService;

    @Autowired
    DoctorService doctorService;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    MaterialService materialService;

    /**
     * 根据医生ID和病人ID获取最近的一次病历
     */
    public MrMedicalRecordsEntity systemAccess(String patientId, String userId,String json) throws Exception {
        MrMedicalRecordsEntity re = new MrMedicalRecordsEntity();

        Map<String,String> map =(Map<String,String>)objectMapper.readValue(json,Map.class);
        //必传校验参数
        if(map.containsKey("id") && map.containsKey("uid") && map.containsKey("imei") && map.containsKey("token") && map.containsKey("token"))
        {
            //单点登录校验
            WlyyResponse response = wlyyService.userSessionCheck(json);
            if (response.getStatus() != 200) {
                Message.error(response.getMsg());
            }
            else {
                //获取医生信息
                MrDoctorsEntity doctor = doctorService.getDoctorInformation(userId,json);
                //获取患者信息
                MrPatientsEntity patient = patientService.getPatientInformation(patientId,json);
                //获取最新病历
                MrMedicalRecordsEntity record = medicalRecordsDao.getLastRecord(userId, patientId);

                //不存在则新增病历
                if (record != null) {
                    //获取病历信息
                    re = record;
                } else {
                    //新增病历信息
                    re = addRecord(doctor, patient);
                }
            }
        }
        else{
            Message.error("缺失校验参数！");
        }


        return re;
    }

    /**
     * 获取病历
     */
    public MrMedicalRecordsEntity getMedicalRecord(String recordId) throws Exception {
        return  medicalRecordsDao.findById(Integer.valueOf(recordId));
    }


    /**
     * 新增病历
     */
    private MrMedicalRecordsEntity addRecord(MrDoctorsEntity doctor,MrPatientsEntity patient) throws Exception{
        MrMedicalRecordsEntity re = new MrMedicalRecordsEntity();
        if(doctor!=null && patient!=null)
        {
            MrMedicalRecordsEntity record = new MrMedicalRecordsEntity();
            Timestamp datetimeNow = new Timestamp(System.currentTimeMillis());
            String doctorId = doctor.getId();
            String patientId = patient.getId();

            record.setCreateTime(datetimeNow);
            record.setMedicalTime(datetimeNow);
            record.setDoctorId(doctorId);
            record.setOrgDept(doctor.getOrgDept());
            record.setOrgName(doctor.getOrgName());
            record.setPatientId(patientId);
            record.setPatientName(patient.getName());
            record.setDemographicId(patient.getDemographicId());
            record.setSex(patient.getSex());
            record.setBirthday(patient.getBirthday());
            record.setIsMarried(patient.getMaritalStatus());
            record.setPhone(patient.getPhone());

            medicalRecordsDao.save(record);

            re = record;

            //建立医生--病历关联
            MrDoctorMedicalRecordsEntity dr = new MrDoctorMedicalRecordsEntity();
            dr.setDoctorId(doctorId);
            dr.setPatientId(patientId);
            dr.setRecordId(String.valueOf(re.getId()));
            dr.setIsCreator("1");
            dr.setRecordType(EnumClass.RecordType.Online);

            doctorMedicalRecordDao.save(dr);
        }
        else{
            Message.error("医生或者患者信息缺失！");
        }
        return re;
    }

    /**
     * 新增病历
     */
    @Transactional
    public MrMedicalRecordsEntity addRecord(String doctorId, String patientId,String firstRecordId) throws Exception {
        //获取医生信息
        MrDoctorsEntity doctor = doctorService.getDoctor(doctorId);
        //获取患者信息
        MrPatientsEntity patient = patientService.getPatient(patientId);

        //新增病历信息
        MrMedicalRecordsEntity re = addRecord(doctor,patient);

        if(firstRecordId!=null && firstRecordId.length()>0)
        {
            re.setFirstRecordId(firstRecordId);
            medicalRecordsDao.save(re);
        }

        return re;
    }




    /**
     * 修改病历
     */
    @Transactional
    public boolean editRecord(String recordId, Map<String,String> map) throws Exception {
        MrMedicalRecordsEntity obj = medicalRecordsDao.findById(Integer.valueOf(recordId));
        if(obj!=null)
        {
            if(map!=null && map.size()>0)
            {
                for(String key : map.keySet()) {
                    String value = map.get(key);
                    switch (key) {
                        case "medicalDiagnosis": //诊断名称
                        {
                            obj.setMedicalDiagnosis(value);
                            break;
                        }
                        case "medicalDiagnosisCode": //诊断代码
                        {
                            obj.setMedicalDiagnosisCode(value);
                            break;
                        }
                        case "medicalSuggest": //治疗建议
                        {
                            //新增或统计医生治疗建议
                            materialService.addOrUpdateTextMaterial(obj.getDoctorId(),"0",value,obj.getPatientId());
                            obj.setMedicalSuggest(value);
                            break;
                        }
                        case "medicalTime": //就诊时间
                        {
                            //obj.setMedicalTime(value);
                            break;
                        }
                        case "orgName": //就诊机构
                        {
                            obj.setOrgName(value);
                            break;
                        }
                        case "orgDept": //就诊科室
                        {
                            obj.setOrgDept(value);
                            break;
                        }
                        case "patientName": //患者姓名
                        {
                            obj.setPatientName(value);
                            break;
                        }
                        case "demographicId": //患者身份证
                        {
                            obj.setDemographicId(value);
                            break;
                        }
                        case "sex": //患者性别
                        {
                            obj.setSex(value);
                            break;
                        }
                        case "birthday": //患者生日
                        {
                            //obj.setBirthday(value);
                            break;
                        }
                        case "isMarried": //是否已婚
                        {
                            obj.setIsMarried(value);
                            break;
                        }
                        case "phone": //患者手机
                        {
                            obj.setPhone(value);
                            break;
                        }
                        case "firstRecordId"  : //修改首诊病历
                        {
                            obj.setFirstRecordId(value);
                            break;
                        }
                    }
                }
                medicalRecordsDao.save(obj);
            }
            else{
                Message.error("无修改项！");
            }
        }
        else{
            Message.error("不存在改条就诊记录！recordId:" + recordId);
        }
        return true;
    }


    /**
     * 删除病历
     */
    public boolean deleteRecord(String recordId) throws Exception {
        medicalRecordsDao.delete(Integer.valueOf(recordId));
        return true;
    }

    /**
     * 获取关联病历
     */
    public List<MrMedicalRecordsEntity> getMedicalRecordRelated(String recordId) throws Exception {
        return  medicalRecordsDao.findRelatedRecord(recordId);
    }



    /**
     * 病历分享
     */
    public boolean shareRecord(String recordId,String patientId,String doctorId) throws Exception {
        MrDoctorMedicalRecordsEntity dr = new MrDoctorMedicalRecordsEntity();
        dr.setDoctorId(doctorId);
        dr.setPatientId(patientId);
        dr.setRecordId(recordId);
        dr.setIsCreator("0");
        dr.setRecordType(EnumClass.RecordType.Online);

        doctorMedicalRecordDao.save(dr);

        return true;
    }
}