package com.yihu.ehr.medicalRecords.service;

import com.yihu.ehr.medicalRecords.comom.Message;
import com.yihu.ehr.medicalRecords.comom.WlyyResponse;
import com.yihu.ehr.medicalRecords.comom.WlyyService;
import com.yihu.ehr.medicalRecords.dao.DoctorDao;
import com.yihu.ehr.medicalRecords.dao.DoctorMedicalRecordDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsDao;
import com.yihu.ehr.medicalRecords.dao.MedicalRecordsQueryDao;
import com.yihu.ehr.medicalRecords.model.DTO.DictDTO;
import com.yihu.ehr.medicalRecords.model.DTO.MedicalRecordDTO;
import com.yihu.ehr.medicalRecords.model.Entity.MrDoctorsEntity;
import com.yihu.ehr.medicalRecords.model.Entity.MrMedicalRecordsEntity;
import com.yihu.ehr.medicalRecords.model.EnumClass;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.models.auth.In;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hzp on 2016/7/14.
 */
@Transactional
@Service
public class DoctorService {

    @Autowired
    DoctorDao doctorDao;

    @Autowired
    WlyyService wlyyService;

    @Autowired
    PatientService patientService;

    @Autowired
    MedicalRecordsDao medicalRecordsDao;

    @Autowired
    MedicalRecordsQueryDao medicalRecordsQueryDao;

    @Autowired
    MedicalLabelService medicalLabelService;


    /**
     * 获取医生信息,不存在则新增，存在则修改
     */
    public MrDoctorsEntity getDoctorInformation(String doctorId,String headInfo)throws Exception
    {
        MrDoctorsEntity re = new MrDoctorsEntity();
        WlyyResponse response = wlyyService.queryDoctorInfoByID(doctorId,headInfo);
        //获取医生信息成功
        if(response.getStatus() == 200)
        {
            Map<String,Object> map = (Map<String,Object>)response.getData();
            if (map!=null && map.size()>0) {
                MrDoctorsEntity doctor = new MrDoctorsEntity();
                doctor.setId(doctorId);
                doctor.setName(map.get("name").toString());
                //doctor.setDemographicId(map.get("").toString());
                doctor.setSex(map.get("sex").toString());
                if (map.get("birthday") != null && map.get("birthday").toString().length() > 0) {
                    doctor.setBirthday(java.sql.Timestamp.valueOf(map.get("birthday").toString()));
                }
                doctor.setPhoto(map.get("photo").toString());
                doctor.setPhone(map.get("mobile").toString());

                doctor.setTitle(map.get("jobName").toString());
                doctor.setGood(map.get("expertise").toString());
                doctor.setOrgName(map.get("hospitalName").toString());
                doctor.setOrgDept(map.get("deptName").toString());
                doctorDao.save(doctor);
                re = doctor;
            }
        }
        else {
            re = doctorDao.findById(doctorId);
            Message.debug(response.getMsg());
        }
        return re;
    }

    /**
     * 获取医生信息
     */
    public MrDoctorsEntity getDoctor(String doctorId) throws Exception
    {
        return doctorDao.findById(doctorId);
    }


    /**
     * 保存医生信息
     */
    public boolean saveDoctor(MrDoctorsEntity doctor){
        doctorDao.save(doctor);
        return true;
    }


    /**
     * 获取医生诊断
     */
    public List<DictDTO> getDoctorDiagnosis(String doctorId)throws Exception {

        return  medicalRecordsQueryDao.findDoctorDiagnosis(doctorId);
    }

    /**
     * 根据年龄获取时间
     */
    private String getDatetimeByAge(Integer age) throws Exception
    {
        SimpleDateFormat sf  =new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar gc=new GregorianCalendar();
        gc.setTime(new Date());
        gc.add(1,-age);
        gc.getTime();
        return sf.format(gc.getTime());
    }

    /**
     * 获取医生病历
     */
    public Envelop getDoctorRecords(String doctorId, String label, String medicalTimeFrom, String medicalTimeEnd, String recordType, Integer ageFrom, Integer ageEnd, String sex, String filter, Integer page, Integer size) throws Exception {

        String sql = "SELECT concat(a.id) as id,a.medical_Time as medicalTime,'0' as medicalType,a.MEDICAL_DIAGNOSIS as medicalDiagnosis,b.name as doctorName,b.org_dept as orgDept,b.org_name as orgName,b.title as doctorTitle,c.medical_info as medicalInfo\n" +
                "from mr_medical_records a \n" +
                "left join mr_doctors b on a.DOCTOR_ID=b.ID\n" +
                "left join (SELECT record_id, GROUP_CONCAT(value) AS medical_info FROM mr_medical_info GROUP BY record_id) c on a.id = c.record_id \n" +
                "where doctor_id='"+doctorId+"'";
        //自定义标签
        if(label!=null && label.length()>0)
        {
            String[] labels = label.split(",");
            String labelString = "";
            for(String item:labels)
            {
                labelString += "'"+item+"',";
            }
            labelString = labelString.substring(0,labelString.length()-1);
            sql += " and a.id in (select l.RECORD_ID from mr_medical_label l where l.LABEL in("+labelString+") and l.doctor_id = '"+doctorId+"')";
        }

        //就诊开始时间
        if(!StringUtils.isEmpty(medicalTimeFrom))
        {
            sql += " and unix_timestamp(a.medical_time) >= unix_timestamp('"+medicalTimeFrom+"')";
        }

        //就诊结束时间
        if(!StringUtils.isEmpty(medicalTimeEnd))
        {
            sql += " and unix_timestamp(a.medical_time) < unix_timestamp('"+medicalTimeEnd+"')";
        }

        //就诊类型
        if(!StringUtils.isEmpty(recordType) && !recordType.equals(EnumClass.RecordType.Online))
        {
            sql += " and 1!=1";
        }

        //年龄
        if(ageFrom!=null)
        {
            sql += " and unix_timestamp(a.birthday) < unix_timestamp('"+getDatetimeByAge(ageFrom)+"')";
        }
        if(ageEnd!=null)
        {
            sql += " and unix_timestamp(a.birthday) > unix_timestamp('"+getDatetimeByAge(ageEnd)+"')";
        }

        //性别
        if(sex!=null)
        {
            sql += " and a.sex < '"+sex+"'";
        }

        //过滤条件
        if(!StringUtils.isEmpty(filter))
        {
            sql += " and (a.MEDICAL_DIAGNOSIS like '%"+filter+"%' or c.medical_info like '%"+filter+"%')";
        }

        return  medicalRecordsQueryDao.queryPage(sql,page,size);
    }
}
