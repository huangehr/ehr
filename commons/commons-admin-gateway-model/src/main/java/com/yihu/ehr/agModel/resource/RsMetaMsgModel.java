package com.yihu.ehr.agModel.resource;

import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/13
 */
public class RsMetaMsgModel {
    @Ignore
    Pattern codePtn = Pattern.compile("[0-9A-Za-z_.]{1,50}");
    @Ignore
    Pattern idPtn = Pattern.compile("EHR_[0-9]{6}");

    private String id;
    private String domain;
    private String name;
    private String stdCode;
    private String displayCode;
    private String columnType;
    private String nullAble;
    private String dictCode;
    private String description;
    private int dictId;

    private int seq = 0;

    private Map<String, String> errMsg = new HashMap<>();

    public String findIdMsg(){
        return nullToSpace(errMsg.get("id"));
    }
    public void addIdMsg(String msg){
        errMsg.put("id", msg);
    }

    public String findStdCodeMsg(){
        return nullToSpace(errMsg.get("stdCode"));
    }
    public void addStdCodeMsg(String msg){
        errMsg.put("stdCode", msg);
    }

    public String findDomainMsg(){
        return nullToSpace(errMsg.get("domain"));
    }

    public String findNameMsg(){
        return nullToSpace(errMsg.get("name"));
    }

    private String nullToSpace(String str){
        return str ==null? "" : str;
    }

    public int getDictId() {
        return dictId;
    }
    public void setDictId(int dictId) {
        this.dictId = dictId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStdCode() {
        return this.stdCode;
    }

    public void setStdCode(String stdCode) {
        this.stdCode = stdCode;
    }

    public String getDisplayCode() {
        return this.displayCode;
    }

    public void setDisplayCode(String displayCode) {
        this.displayCode = displayCode;
    }

    public String findDictCodeMsg(){
        return nullToSpace(errMsg.get("dictCode"));
    }

    public String getDictCode() {
        return this.dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String findColumnTypeMsg(){
        return nullToSpace(errMsg.get("columnType"));
    }

    public String getColumnType() {
        return this.columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String findNullAbleMsg(){
        return nullToSpace(errMsg.get("nullAble"));
    }

    public String getNullAble() {
        return this.nullAble;
    }

    public void setNullAble(String nullAble) {
        this.nullAble = nullAble;
    }

    public String findDescMsg(){
        return nullToSpace(errMsg.get("desc"));
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getErrMsg() {
        return errMsg==null? new HashMap<>() : errMsg;
    }

    public void setErrMsg(Map<String, String> errMsg) {
        this.errMsg = errMsg;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public boolean validateHas(String domainData, String columnTypeData, String ynData){
        boolean rs = true;
        if(columnTypeData.indexOf(nullToSpace(getColumnType()))==-1){
            getErrMsg().put("columnType", "请输入" + columnTypeData + "之中的一个");
            rs = false;
        }

        if(domainData.indexOf(nullToSpace(getDomain()))==-1){
            getErrMsg().put("domain", "请输入"+ domainData + "之中的一个");
            rs = false;
        }

        if(ynData.indexOf(nullToSpace(getNullAble()))==-1){
            getErrMsg().put("nullAble", "请输入"+ ynData + "之中的一个");
            rs = false;
        }
        return rs;
    }

    public boolean validate(){
        boolean valid = true;
        Map errMsg = getErrMsg();
        String validateStr = nullToSpace(getStdCode());
        if(!codePtn.matcher(validateStr).matches()){
            errMsg.put("stdCode", "只允许输入数字、英文、小数点与下划线！");
            valid = false;
        }

        validateStr = nullToSpace(getId());
        if(!idPtn.matcher(validateStr).matches()){
            errMsg.put("id", "请输入以 EHR_ 开头，后面跟着6位数字的字符串，如：EHR_000001！");
            valid = false;
        }

        validateStr = nullToSpace(getDictCode());
        if(!Pattern.compile("[0-9A-Za-z_.]{0,50}").matcher(validateStr).matches()){
            errMsg.put("dictCode", "只允许输入数字、英文、小数点与下划线！");
            valid = false;
        }

        validateStr = nullToSpace(getName());
        if(validateStr.length()<=0 || validateStr.length()>200){
            errMsg.put("name", "请输入1~200个字符！");
            valid = false;
        }

        setErrMsg(errMsg);
        return valid;
    }

    public void addErrorMsg(String field, String msg){
        this.errMsg.put(field, msg);
    }

    public String findErrorMsg(String field){
        return this.errMsg.get(field);
    }
    @Override
    public boolean equals(Object obj) {
        RsMetaMsgModel target = (RsMetaMsgModel) obj;
        return getSeq() == target.getSeq();
    }
}
