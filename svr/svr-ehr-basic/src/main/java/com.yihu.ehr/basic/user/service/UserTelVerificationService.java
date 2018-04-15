package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.XUserTelVerificationRepository;
import com.yihu.ehr.entity.user.UserTelVerification;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户手机验证管理接口实现类.
 *
 * @author Cws
 * @version 1.0
 * @updated 2018.02.22
 */

@Service
@Transactional
public class UserTelVerificationService extends BaseJpaService<UserTelVerification, XUserTelVerificationRepository> {

    @Autowired
    private XUserTelVerificationRepository xUserTelVerificationRepository;

    /**
     * 根据手机号码及应用ID获取用户手机验证信息.
     * @param telNo
     * @param appId
     */
    public UserTelVerification getTelVerification(String telNo, String appId) {
        List<UserTelVerification> telVerificationList = xUserTelVerificationRepository.findByTelNoAndAppId(telNo,appId);
        UserTelVerification telVerification = null;
        if(null != telVerificationList && telVerificationList.size()>0 ){
            telVerification = telVerificationList.get(0);
            return telVerification;
        }else{
            return null;
        }
    }

    /**
     * 存储用户手机验证信息.
     * @param userTelVerification
     */
    public UserTelVerification createTelVerification(UserTelVerification userTelVerification) {
        UserTelVerification telVerification = save(userTelVerification);
        if(telVerification == null){
            return null;
        }
        else{
            return telVerification;
        }
    }

    /**
     * 验证手机验证码的有效性
     * @param telNo
     * @param verificationCode
     * @param appId
     */
    public Boolean telValidation(String telNo, String verificationCode, String appId) {
        List<UserTelVerification> telVerificationList = xUserTelVerificationRepository.ListUserTelVerificationByTelNoAndAppId(telNo, appId,verificationCode);
        UserTelVerification telVerification = null;
        if (telVerificationList != null && telVerificationList.size()>0) {
            telVerification = telVerificationList.get(0);
            //当验证码不存在的情况下返回验证失败。
            if (telVerification.getVerificationCode().isEmpty()) {
                return false;
            }
            //验证码有有效性验证
            if (telVerification.getEffectivePeriod() != null) {
                int validationResult = telVerification.getEffectivePeriod().compareTo(DateUtil.getSysDate());
                //验证码未超期且验证通过，则返回成功
                if (validationResult > 0 && verificationCode.equals(telVerification.getVerificationCode().toString())) {
                    return true;
                } else {
                    return false;
                }
            }
            //验证码为空，返回验证失败。
            return false;
        } else {
            return null;
        }
    }

    /**
     * 删除用户手机验证信息.
     * @param userTelVerification
     */
    public Boolean delTelVerification(UserTelVerification userTelVerification) {
        delete(userTelVerification);
        return true;
    }
}