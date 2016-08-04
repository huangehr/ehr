package com.yihu.ehr.medicalRecords.comom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2016/8/4.
 * 网络医院用户账号管理接口
 */
@Service
public class WlyyService {


    /***
     * 单点登录接口
     */
    public String userSessionCheck(String userId,String ticket,String appUid)
    {
        return "";
    }

    /***
     * 获取医生信息
     */
    public String queryDoctorInfoByID (String userID)
    {
        return "";
    }

    /***
     * 获取患者信息
     */
    public String queryPatientInfoByID (String userID)
    {
        return "";
    }


}
