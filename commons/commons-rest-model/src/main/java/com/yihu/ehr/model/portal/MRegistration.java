package com.yihu.ehr.model.portal;
import java.io.Serializable;

/**
 * 挂号单
 *
 * @author zdm
 * @date 2018/4/16 19:18
 */
public class MRegistration implements Serializable {

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
    public Integer registerType; // 挂号方式，1：预约挂号，2：现场挂号
    public String registerTypeDesc; // 挂号方式描述
    public String id; // 第三方订单号

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Integer getCardType() {
        return cardType;
    }

    public void setCardType(Integer cardType) {
        this.cardType = cardType;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getLczcName() {
        return lczcName;
    }

    public void setLczcName(String lczcName) {
        this.lczcName = lczcName;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getStateDesc() {
        return stateDesc;
    }

    public void setStateDesc(String stateDesc) {
        this.stateDesc = stateDesc;
    }

    public Integer getVisitClinicResult() {
        return visitClinicResult;
    }

    public void setVisitClinicResult(Integer visitClinicResult) {
        this.visitClinicResult = visitClinicResult;
    }

    public String getVisitClinicResultDesc() {
        return visitClinicResultDesc;
    }

    public void setVisitClinicResultDesc(String visitClinicResultDesc) {
        this.visitClinicResultDesc = visitClinicResultDesc;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public Integer getTimeId() {
        return timeId;
    }

    public void setTimeId(Integer timeId) {
        this.timeId = timeId;
    }

    public String getTimeIdDesc() {
        return timeIdDesc;
    }

    public void setTimeIdDesc(String timeIdDesc) {
        this.timeIdDesc = timeIdDesc;
    }

    public String getCommendTime() {
        return commendTime;
    }

    public void setCommendTime(String commendTime) {
        this.commendTime = commendTime;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public String getInvalidDate() {
        return invalidDate;
    }

    public void setInvalidDate(String invalidDate) {
        this.invalidDate = invalidDate;
    }

    public Integer getOriginType() {
        return originType;
    }

    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    public Integer getRegisterType() {
        return registerType;
    }

    public void setRegisterType(Integer registerType) {
        this.registerType = registerType;
    }

    public String getRegisterTypeDesc() {
        return registerTypeDesc;
    }

    public void setRegisterTypeDesc(String registerTypeDesc) {
        this.registerTypeDesc = registerTypeDesc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
