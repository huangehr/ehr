<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######人口管理页面 > 人口信息对话框模板页######-->
<div id="div_patient_info_form" data-role-form class="m-form-inline f-mt20" style="overflow:auto">
    <div>
        <div id="div_patient_img_upload" class="u-upload alone f-ib f-tac f-vam u-upload-img" data-alone-file=true>
            <!--用来存放item-->
            <div id="div_file_list" class="uploader-list"></div>
            <div id="div_file_picker" class="f-mt10"><spring:message code="btn.file.choose"/></div>
        </div>
        <div class="m-form-group">
            <label>姓名：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="inp_realName" class="required useTitle max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="name"/>
            </div>
        </div>
        <div class="m-form-group ">
            <label>身份证号：</label>

            <div class="l-text-wrapper m-form-control essential ">
                <input type="text" id="inp_idCardNo" class="required useTitle ajax validate-id-number"  validate-id-number-title="请输入合法的身份证号"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="idCardNo"/>
            </div>
        </div>
        <div class="m-form-group ">
            <label>性别：</label>

            <div class="u-checkbox-wrap m-form-control ">
                <input type="radio" value="Male" name="gender" data-attr-scan>男
                <input type="radio" value="Female" name="gender" data-attr-scan>女
            </div>
        </div>
        <div class="m-form-group ">
            <label>民族：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="inp_patientNation" data-type="select" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="nation"/>
            </div>
        </div>
        <div class="m-form-group ">
            <label>籍贯：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="inp_patientNativePlace" class="required useTitle max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="nativePlace"/>
            </div>
        </div>
        <div class="m-form-group">
            <label>婚姻状况：</label>

            <div class="m-form-control ">
                <input type="text" id="inp_select_patientMartialStatus" data-type="select"
                       data-attr-scan="martialStatus">
            </div>
        </div>
        <div class="m-form-group">
            <label>出生日期：</label>

            <div class="m-form-control">
                <input type="text" id="inp_patientBirthday" class="validate-date  l-text-field " placeholder="输入日期 格式(2016-04-15)"
                       required-title=<spring:message code="lbl.must.input"/> data-attr-scan="birthday"/>
            </div>
        </div>

        <div class="m-form-group">
            <label>户籍地址：</label>

            <div class="m-form-control f-w240">
                <input type="text" id="inp_birthPlace" class="validate-special-char" data-type="comboSelect" data-attr-scan="birthPlaceInfo">
            </div>
        </div>

        <div class="m-form-group">
            <label>家庭地址：</label>

            <div class="m-form-control f-w240">
                <input type="text" id="inp_homeAddress" class="validate-special-char" data-type="comboSelect"
                       data-attr-scan="homeAddressInfo"/>
            </div>
        </div>
        <div class="m-form-group">
            <label>工作地址：</label>

            <div class="m-form-control f-w240">
                <input type="text" id="inp_workAddress" class="validate-special-char"  data-type="comboSelect"
                       data-attr-scan="workAddressInfo"/>
            </div>
        </div>
        <div class="m-form-group">
            <label>户籍性质：</label>

            <div class="u-checkbox-wrap m-form-control">
                <input type="radio" value="temp" name="residenceType" data-attr-scan>农村
                <input type="radio" value="usual" name="residenceType" data-attr-scan>非农村
            </div>
        </div>
        <div class="m-form-group">
            <label>联系方式：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="inp_patientTel" class="required useTitle validate-mobile-phone"  required-title=<spring:message code="lbl.must.input"/> validate-mobile-phone-title="请输入正确的手机号码" data-attr-scan="telephoneNo"/>
            </div>
        </div>
        <div class="m-form-group">
            <label>邮箱：</label>

            <div class="l-text-wrapper m-form-control">
                <input type="text" id="inp_patientEmail" class="useTitle validate-email max-length-50 validate-special-char" validate-email-title="请输入正确的邮箱" placeholder="请输入邮箱" data-attr-scan="email"/>
            </div>
        </div>
        <div class="m-form-group f-mt20" id="reset_password">
            <hr class="u-border">
            <div class="f-pr u-bd">
                <div class="f-pa f-w20 f-wtl">
                    高级
                </div>
                <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam f-mt10 f-mb10 f-ml100" id="div_resetPassword">
                    <span>重置密码</span>
                </div>
            </div>
        </div>
    </div>

    <div class="m-form-control pane-attribute-toolbar">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="div_update_btn">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="div_cancel_btn">
            <span>关闭</span>
        </div>
    </div>
    <input type="hidden" id="inp_patientCopyId">
</div>
