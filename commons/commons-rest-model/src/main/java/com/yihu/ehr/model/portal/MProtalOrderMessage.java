package com.yihu.ehr.model.portal;
import org.springframework.core.annotation.Order;

import java.io.Serializable;
import java.util.List;


/**
 * 健康上饶app-预约成功后，获取订单号列表
 *
 * @author zdm
 * @vsrsion 1.0
 * Created at 2018/4/15.
 */
public class MProtalOrderMessage  implements Serializable {

    /**
     * 状态码
     */
    private Integer Code;
    /**
     * 提示消息
     */
    private String Message;
    /**
     * 记录数
     */
    private Integer total;
    /**
     * 结果集 JSON Array数组
     *orderCreateTime	String	下单时间	格式：yyyy-mm-dd hh24:mi:ss
     patientName	String	患者姓名
     hospitalName	String	医院名称
     deptName	String	科室名称
     doctorName	String	医生姓名
     state	Int  	订单状态	1:待付款  2:待就诊 11：预约中 22：退款中 99：已退号 -1：系统取消 3：已就诊
     stateDesc	String	订单状态描述	待付款；待就诊；预约中；退款中；已退号；系统取消  ；已就诊
     orderId	String	订单号
     thirdPartyOrderId	String	第三方订单号
     registerDate	String	就诊时间
     */
    private List<Order> result;

    public Integer getCode() {
        return Code;
    }

    public void setCode(Integer code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    public  class Order{
        /**
         * 下单时间	格式：yyyy-mm-dd hh24:mi:ss
         */
        private String orderCreateTime;
        /**
         * 患者姓名
         */
        private String patientName;
        /**
         * 医院名称
         */
        private String hospitalName;
        /**
         * 科室名称
         */
        private String deptName;
        /**
         * 医生姓名
         */
        private String doctorName;
        /**
         * 订单状态	1:待付款  2:待就诊 11：预约中 22：退款中 99：已退号 -1：系统取消 3：已就诊
         */
        private Integer state;
        /**
         * 订单状态描述	待付款；待就诊；预约中；退款中；已退号；系统取消  ；已就诊
         */
        private String stateDesc;
        /**
         * 订单号
         */
        private String orderId;
        /**
         * 第三方订单号
         */
        private String thirdPartyOrderId;
        /**
         * 就诊时间
         */
        private String registerDate	;

        public String getOrderCreateTime() {
            return orderCreateTime;
        }

        public void setOrderCreateTime(String orderCreateTime) {
            this.orderCreateTime = orderCreateTime;
        }

        public String getPatientName() {
            return patientName;
        }

        public void setPatientName(String patientName) {
            this.patientName = patientName;
        }

        public String getHospitalName() {
            return hospitalName;
        }

        public void setHospitalName(String hospitalName) {
            this.hospitalName = hospitalName;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public void setDoctorName(String doctorName) {
            this.doctorName = doctorName;
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

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getThirdPartyOrderId() {
            return thirdPartyOrderId;
        }

        public void setThirdPartyOrderId(String thirdPartyOrderId) {
            this.thirdPartyOrderId = thirdPartyOrderId;
        }

        public String getRegisterDate() {
            return registerDate;
        }

        public void setRegisterDate(String registerDate) {
            this.registerDate = registerDate;
        }
    }
}
