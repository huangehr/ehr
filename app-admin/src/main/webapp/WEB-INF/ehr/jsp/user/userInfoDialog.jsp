<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######用户管理页面 > 用户信息对话框模板页######-->
<div id="div_user_info_form" data-role-form class="m-form-inline f-mt20 f-pb30" style="overflow:auto">
    <input data-attr-scan="id" hidden="hidden"/>
    <%--<input data-attr-scan="organizationCode" hidden="hidden"/>--%>
    <%--保存初始数据-------yww--%>
    <input hidden="hidden" id="idCardCopy"/>
    <input hidden="hidden" id="emailCopy"/>
<%--    <input data-attr-scan="idCardCopy" hidden="hidden" id="idCardCopy"/>
    <input data-attr-scan="emailCopy" hidden="hidden" id="emailCopy"/>--%>

    <div id="div_user_img_upload" class="u-upload alone f-ib f-tac f-vam u-upload-img" data-alone-file=true>
        <!--用来存放item-->
        <div id="div_file_list" class="uploader-list"></div>
        <div id="div_file_picker" class="f-mt10"><spring:message code="btn.file.choose"/></div>
    </div>
    <div class="m-form-group m-form-readonly">
        <label><spring:message code="lbl.loginCode"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_loginCode" class="required useTitle" data-attr-scan="loginCode"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_user_name" class="required useTitle max-length-50 validate-special-char" required-title=<spring:message code="lbl.must.input"/>
                   data-attr-scan="realName"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.identity.card"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_idCard" class="required useTitle ajax validate-id-number"  required-title=<spring:message code="lbl.must.input"/> validate-id-number-title=<spring:message code="lbl.input.true.idCard"/> data-attr-scan="idCardNo"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.user.sex"/><spring:message code="spe.colon"/></label>
        <div class="u-checkbox-wrap m-form-control">
            <input type="radio" value="Male" name="gender" data-attr-scan><spring:message code="lbl.male"/>
            <input type="radio" value="Female" name="gender" data-attr-scan><spring:message code="lbl.female"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.marriage.status"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control">
            <input type="text" id="inp_select_marriage" data-type="select" data-attr-scan="martialStatus">
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.user.mail"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_userEmail" class="required useTitle ajax validate-email max-length-50 validate-special-char" required-title=<spring:message code="lbl.must.input"/> validate-email-title=<spring:message code="lbl.input.true.email"/> data-attr-scan="email"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.user.tel"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_userTel" class="required useTitle validate-mobile-phone"  required-title=<spring:message code="lbl.must.input"/> validate-mobile-phone-title=<spring:message code="lbl.input.true.tel"/> data-attr-scan="telephone"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.user.role"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control">
            <input type="text" id="inp_select_userType" data-type="select" data-attr-scan="userType">
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.org.belong"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control f-w240 ">
            <input type="text" id="inp_org"  required-title=<spring:message code="lbl.must.input"/> data-type="comboSelect"
                   data-attr-scan="organization" class="validate-org-length f-w240"/>
        </div>
    </div>
    <div class="m-form-group" style="display: none" id="inp_major_div">
        <label><spring:message code="lbl.specialized.belong"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control ">
            <input type="text" id="inp_major" class="max-length-150 validate-special-char" data-attr-scan="major"/>
        </div>
    </div>
    <div>
        <hr class="u-border">
        <div class="f-pr u-bd">
            <div class="f-pa f-w20 f-wtl">
                高级
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKeyMessage">
                <label>公钥信息:</label>
                <div class="l-text-wrapper m-form-control ">
                    <textarea type="text" class="required useTitle u-public-key-msg" data-attr-scan="publicKey"
                              readonly="readonly"></textarea>
                </div>
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKey_validTime">
                <label>有效时间:</label>
                <div class="l-text-wrapper m-form-control ">
                    <input type="text" class="required useTitle u-f-mt5"
                           data-attr-scan="validTime"/>
                </div>
            </div>
            <div class="m-form-group" hidden="hidden" id="div_publicKey_startTime">
                <label>生成时间:</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" class="required useTitle u-f-mt5"
                           data-attr-scan="startTime"/>
                </div>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam u-reset-password" id="div_resetPassword">
                <span>重置密码</span>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-ml100 u-btn-pk-color" id="div_publicKey">
                <span>公钥管理</span>
            </div>
        </div>
    </div>
    <div id="div_public_manage" class="u-public-manage">
        <div class="l-button u-btn u-btn-small u-btn-cancel f-ib f-vam u-btn-color f-mb10" id="div_allot_publicKey">
            <span>分配公钥</span>
        </div>
        <textarea class="txt-public-content" id="txt_publicKey_message" data-attr-scan="publicKey"></textarea><br>
            <label class="f-fl">有效时间:</label><label id="lbl_publicKey_validTime" class="f-fl"></label><br>
            <label class="f-ml-t">生成时间:</label><label id="lbl_publicKey_startTime" class="f-mb"></label><br>
        <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_affirm_btn">
            <span><spring:message code="btn.close"/></span>
        </div>
    </div>

    <div id="div_toolbar" class="m-form-control pane-attribute-toolbar">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="div_update_btn">
            <span><spring:message code="btn.save"/></span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="div_cancel_btn">
            <span><spring:message code="btn.close"/></span>
        </div>
    </div>

</div>