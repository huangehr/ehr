package com.yihu.ehr.model.portal;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/21.
 */
public class MMessageRemind {

    private Long id;
    private String app_id;
    private String app_name;
    private String from_user_id;
    private String to_user_id;
    private String type_id;
    private String content;
    private String work_uri;
    private Integer readed;
    private Date create_date;
    private Long message_template_id;
    /**
     * 推送过来的消息
     */
    private String received_messages;

    /**
     * 就诊时间
     */
    private Date visit_time;
    /**
     * 就诊部门地址
     */
    private String deptAdress;
    /**
     * 保存 从消息推送过来的时间到当前时间 的订单信息
     */
    private String order_info;
    /**
     * 预约挂号订单id
     */
    private String notice;
    /**
     * 温馨提示
     */
    private String order_id;
    private String fromUserName;
    private String toUserName;
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
    private Map result;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getFrom_user_id() {
        return from_user_id;
    }

    public void setFrom_user_id(String from_user_id) {
        this.from_user_id = from_user_id;
    }

    public String getTo_user_id() {
        return to_user_id;
    }

    public void setTo_user_id(String to_user_id) {
        this.to_user_id = to_user_id;
    }

    public String getType_id() {
        return type_id;
    }

    public void setType_id(String type_id) {
        this.type_id = type_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getWork_uri() {
        return work_uri;
    }

    public void setWork_uri(String work_uri) {
        this.work_uri = work_uri;
    }

    public Integer getReaded() {
        return readed;
    }

    public void setReaded(Integer readed) {
        this.readed = readed;
    }
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }

    public Long getMessage_template_id() {
        return message_template_id;
    }

    public void setMessage_template_id(Long message_template_id) {
        this.message_template_id = message_template_id;
    }

    public String getReceived_messages() {
        return received_messages;
    }

    public void setReceived_messages(String received_messages) {
        this.received_messages = received_messages;
    }
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getVisit_time() {
        return visit_time;
    }

    public void setVisit_time(Date visit_time) {
        this.visit_time = visit_time;
    }

    public String getDeptAdress() {
        return deptAdress;
    }

    public void setDeptAdress(String deptAdress) {
        this.deptAdress = deptAdress;
    }

    public String getOrder_info() {
        return order_info;
    }

    public void setOrder_info(String order_info) {
        this.order_info = order_info;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
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

    public Map getResult() {
        return result;
    }

    public void setResult(Map result) {
        this.result = result;
    }
}
