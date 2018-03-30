package com.yihu.ehr.model.portal;

/**
 * 医疗云-预约挂号订单推送接入
 *
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.30
 */
public class MFzH5Message {

    /**
     * hosDeptId : 111
     * orderId : 健康之路订单号
     * orderPushType : 101
     * pushClientId : 推送目标渠道ID
     * cardNo : 证件号码
     * orderIdHis : 医院His订单号
     * endDraw : 取号时间
     * cName : 患者姓名
     * hosDeptName : 医院科室名称
     * ghFeeWay : 11
     * hosYuyueNum : 医院预约序号
     * timeId : 1
     * thInvalidDate : 退号截止时间
     * noticeSn : 站内信编号
     * serialNo : 1
     * accountId : 院方门诊号
     * orderUid : 挂号系统订单号
     * lsh : 院方发票编号
     * passWord : 取号密码
     * commendTime : 就诊时间
     * drawPoint : 取号地点
     * cancelType : 111
     * clinicCardType : 11
     * doctorName : 医生姓名
     * doctorId : 111
     * hospitalId : 111
     * clinicCard : 就诊卡
     * smsContent : 挂号/退号成功后下发的短信内容
     * cMobile : 患者手机号
     * workPlace : 坐诊地点
     * registerDate : 就诊日期
     * sex : 1
     * hospitalName : 医院名称
     * aggOrderId : 部分医院特殊的聚合单单号
     * cAddress : 就诊人地址
     * regSn : 预约流水ID
     * isSuccess : false
     */

    private int hosDeptId;
    private String orderId;
    private int orderPushType;
    private String pushClientId;
    private String cardNo;
    private String orderIdHis;
    private String endDraw;
    private String cName;
    private String hosDeptName;
    private int ghFeeWay;
    private String hosYuyueNum;
    private int timeId;
    private String thInvalidDate;
    private String noticeSn;
    private int serialNo;
    private String accountId;
    private String orderUid;
    private String lsh;
    private String passWord;
    private String commendTime;
    private String drawPoint;
    private int cancelType;
    private int clinicCardType;
    private String doctorName;
    private int doctorId;
    private int hospitalId;
    private String clinicCard;
    private String smsContent;
    private String cMobile;
    private String workPlace;
    private String registerDate;
    private int sex;
    private String hospitalName;
    private String aggOrderId;
    private String cAddress;
    private String regSn;
    private boolean isSuccess;
    private String userId;//健康之路用户ID

    public int getHosDeptId() {
        return hosDeptId;
    }

    public void setHosDeptId(int hosDeptId) {
        this.hosDeptId = hosDeptId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderPushType() {
        return orderPushType;
    }

    public void setOrderPushType(int orderPushType) {
        this.orderPushType = orderPushType;
    }

    public String getPushClientId() {
        return pushClientId;
    }

    public void setPushClientId(String pushClientId) {
        this.pushClientId = pushClientId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getOrderIdHis() {
        return orderIdHis;
    }

    public void setOrderIdHis(String orderIdHis) {
        this.orderIdHis = orderIdHis;
    }

    public String getEndDraw() {
        return endDraw;
    }

    public void setEndDraw(String endDraw) {
        this.endDraw = endDraw;
    }

    public String getCName() {
        return cName;
    }

    public void setCName(String cName) {
        this.cName = cName;
    }

    public String getHosDeptName() {
        return hosDeptName;
    }

    public void setHosDeptName(String hosDeptName) {
        this.hosDeptName = hosDeptName;
    }

    public int getGhFeeWay() {
        return ghFeeWay;
    }

    public void setGhFeeWay(int ghFeeWay) {
        this.ghFeeWay = ghFeeWay;
    }

    public String getHosYuyueNum() {
        return hosYuyueNum;
    }

    public void setHosYuyueNum(String hosYuyueNum) {
        this.hosYuyueNum = hosYuyueNum;
    }

    public int getTimeId() {
        return timeId;
    }

    public void setTimeId(int timeId) {
        this.timeId = timeId;
    }

    public String getThInvalidDate() {
        return thInvalidDate;
    }

    public void setThInvalidDate(String thInvalidDate) {
        this.thInvalidDate = thInvalidDate;
    }

    public String getNoticeSn() {
        return noticeSn;
    }

    public void setNoticeSn(String noticeSn) {
        this.noticeSn = noticeSn;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOrderUid() {
        return orderUid;
    }

    public void setOrderUid(String orderUid) {
        this.orderUid = orderUid;
    }

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getCommendTime() {
        return commendTime;
    }

    public void setCommendTime(String commendTime) {
        this.commendTime = commendTime;
    }

    public String getDrawPoint() {
        return drawPoint;
    }

    public void setDrawPoint(String drawPoint) {
        this.drawPoint = drawPoint;
    }

    public int getCancelType() {
        return cancelType;
    }

    public void setCancelType(int cancelType) {
        this.cancelType = cancelType;
    }

    public int getClinicCardType() {
        return clinicCardType;
    }

    public void setClinicCardType(int clinicCardType) {
        this.clinicCardType = clinicCardType;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getClinicCard() {
        return clinicCard;
    }

    public void setClinicCard(String clinicCard) {
        this.clinicCard = clinicCard;
    }

    public String getSmsContent() {
        return smsContent;
    }

    public void setSmsContent(String smsContent) {
        this.smsContent = smsContent;
    }

    public String getCMobile() {
        return cMobile;
    }

    public void setCMobile(String cMobile) {
        this.cMobile = cMobile;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getAggOrderId() {
        return aggOrderId;
    }

    public void setAggOrderId(String aggOrderId) {
        this.aggOrderId = aggOrderId;
    }

    public String getCAddress() {
        return cAddress;
    }

    public void setCAddress(String cAddress) {
        this.cAddress = cAddress;
    }

    public String getRegSn() {
        return regSn;
    }

    public void setRegSn(String regSn) {
        this.regSn = regSn;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
