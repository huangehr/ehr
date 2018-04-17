package com.yihu.ehr.basic.appointment.entity;

import com.yihu.ehr.entity.BaseAssignedEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 挂号单
 *
 * @author 张进军
 * @date 2018/4/16 19:18
 */
@Entity
@Table(name = "registration")
public class Registration extends BaseAssignedEntity {

    public String orderId; // 订单号
    public String orderCreateTime; // 下单时间
    public String userId; // 患者ID
    public String patientName; // 患者姓名
    public Integer cardType; // 患者证件类型，默认身份证，1：中国大陆身份证
    public String cardNo; // 患者证件号
    public String phoneNo; // 患者手机号
    public String hospitalId; // 医院ID
    public String hospitalName; // 医院名称
    public String deptId; // 科室ID
    public String deptName; // 科室名称
    public String doctorId; // 医生ID
    public String doctorName; // 医生姓名
    public String lczcName; // 临床职称名称
    public String photoUri; // 医生头像URL
    public Integer state; // 订单状态。1:待付款  2:待就诊 11：预约中 22：退款中 99：已退号 -1：系统取消 3：已就诊
    public String stateDesc; // 订单状态描述
    public Integer visitClinicResult; // 到诊情况。0：确认中，1：已到诊，-1：爽约。（医院如果未提供到诊信息，该返回值将永远处于确认中）
    public String visitClinicResultDesc; // 到诊情况描述
    public String registerDate; // 就诊时间
    public Integer timeId; // 就诊午别。1：上午，2：下午，3：晚上
    public String timeIdDesc; // 就诊午别描述
    public String commendTime; // 就诊时间段
    public Integer serialNo; // 就诊号数
    public String invalidDate; // 退号截止时间（与预约截止时间相同）
    public Integer originType; // 来源类型，1：PC，2：APP

    @Column(name = "order_id")
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Column(name = "order_create_time")
    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    @Column(name = "user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "patient_name")
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    @Column(name = "card_type")
    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    @Column(name = "card_no")
    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }


    @Column(name = "phone_no")
    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    @Column(name = "hospital_id")
    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Column(name = "hospital_name")
    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    @Column(name = "dept_id")
    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    @Column(name = "dept_name")
    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    @Column(name = "doctor_id")
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    @Column(name = "doctor_name")
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Column(name = "lczc_name")
    public String getLczcName() {
        return lczcName;
    }

    public void setLczcName(String lczcName) {
        this.lczcName = lczcName;
    }

    @Column(name = "photo_uri")
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Column(name = "state_desc")
    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    @Column(name = "visit_clinic_result")
    public Integer getVisitClinicResult() {
        return visitClinicResult;
    }

    public void setVisitClinicResult(Integer visitClinicResult) {
        this.visitClinicResult = visitClinicResult;
    }

    @Column(name = "visit_clinic_result_desc")
    public String getVisitClinicResultDesc() {
        return visitClinicResultDesc;
    }

    public void setVisitClinicResultDesc(String visitClinicResultDesc) {
        this.visitClinicResultDesc = visitClinicResultDesc;
    }

    @Column(name = "register_date")
    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    @Column(name = "time_id")
    public Integer getTimeId() {
        return timeId;
    }

    public void setTimeId(Integer timeId) {
        this.timeId = timeId;
    }

    @Column(name = "time_id_desc")
    public String getTimeIdDesc() {
        return timeIdDesc;
    }

    public void setTimeIdDesc(String timeIdDesc) {
        this.timeIdDesc = timeIdDesc;
    }

    @Column(name = "commend_time")
    public String getCommendTime() {
        return commendTime;
    }

    public void setCommendTime(String commendTime) {
        this.commendTime = commendTime;
    }

    @Column(name = "serial_no")
    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    @Column(name = "invalid_date")
    public String getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(String invalidDate) {
        this.invalidDate = invalidDate;
    }

    @Column(name = "origin_type")
    public Integer getOriginType() {
        return originType;
    }

    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

}
