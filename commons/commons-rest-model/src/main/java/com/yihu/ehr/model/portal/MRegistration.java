package com.yihu.ehr.model.portal;
import java.io.Serializable;

/**
 * 挂号单
 *
 * @author zdm
 * @date 2018/4/16 19:18
 */
public class MRegistration implements Serializable {

    public String order_id; // 订单号
    public String order_create_time; // 下单时间
    public String patient_name; // 患者姓名
    public String hospital_name; // 医院名称
    public String dept_name; // 科室名称
    public String doctor_name; // 医生姓名
    public Integer state; // 订单状态。1:待付款  2:待就诊 11：预约中 22：退款中 99：已退号 -1：系统取消 3：已就诊
    public String state_desc; // 订单状态描述
    public String visit_clinic_result; // 到诊情况。0：确认中，1：已到诊，-1：爽约。（医院如果未提供到诊信息，该返回值将永远处于确认中）
    public String visit_clinic_result_desc; // 到诊情况描述
    public String register_date; // 就诊时间
    public Integer time_id; // 就诊午别。1：上午，2：下午，3：晚上
    public String time_id_desc; // 就诊午别描述
    public String commend_time; // 就诊时间段
    public String serial_no; // 就诊号数
    public String origin_type; // 来源类型，1：PC，2：APP

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_create_time() {
        return order_create_time;
    }

    public void setOrder_create_time(String order_create_time) {
        this.order_create_time = order_create_time;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getHospital_name() {
        return hospital_name;
    }

    public void setHospital_name(String hospital_name) {
        this.hospital_name = hospital_name;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getState_desc() {
        return state_desc;
    }

    public void setState_desc(String state_desc) {
        this.state_desc = state_desc;
    }

    public String getVisit_clinic_result() {
        return visit_clinic_result;
    }

    public void setVisit_clinic_result(String visit_clinic_result) {
        this.visit_clinic_result = visit_clinic_result;
    }

    public String getVisit_clinic_result_desc() {
        return visit_clinic_result_desc;
    }

    public void setVisit_clinic_result_desc(String visit_clinic_result_desc) {
        this.visit_clinic_result_desc = visit_clinic_result_desc;
    }

    public String getRegister_date() {
        return register_date;
    }

    public void setRegister_date(String register_date) {
        this.register_date = register_date;
    }

    public Integer getTime_id() {
        return time_id;
    }

    public void setTime_id(Integer time_id) {
        this.time_id = time_id;
    }

    public String getTime_id_desc() {
        return time_id_desc;
    }

    public void setTime_id_desc(String time_id_desc) {
        this.time_id_desc = time_id_desc;
    }

    public String getCommend_time() {
        return commend_time;
    }

    public void setCommend_time(String commend_time) {
        this.commend_time = commend_time;
    }

    public String getSerial_no() {
        return serial_no;
    }

    public void setSerial_no(String serial_no) {
        this.serial_no = serial_no;
    }

    public String getOrigin_type() {
        return origin_type;
    }

    public void setOrigin_type(String origin_type) {
        this.origin_type = origin_type;
    }
}
