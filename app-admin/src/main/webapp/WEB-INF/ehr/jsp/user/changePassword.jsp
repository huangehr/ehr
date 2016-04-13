<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######密码修改页面Title设置######-->
<div class="l-page-top m-logo f-mb100"></div>
<div id="div_changePassWord_info_form" data-role-form class="m-form-inline f-m-auto m-form-bd f-mt20">
    <div class="div-pw-image"><div class="f-pl10 f-pt15 div-fs18-c0">修改</div><div class="div-fs18">密码</div></div>
    <hr class="u-hr-border">
    <div class="m-form-group f-mt20 f-ml100 f-w500 m-form-readonly">
        <label>用户名<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_userName" value="${current_user.loginCode}"
                   class="required useTitle validate-special-char" required-title=
                   <spring:message code="lbl.must.input"/> data-attr-scan="loginCode"/>
        </div>
    </div>
    <div class="m-form-group f-ml100 f-w500">
        <label>原密码<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="password" id="inp_old_passWord"
                   class="required useTitle ajax validate-special-char" required-title=""
                   data-attr-scan="password"/>
        </div>
    </div>
    <div class="m-form-group f-ml100 f-w500">
        <label>输入新密码<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential" id="div-pw-btn">
            <input type="password" id="inp_new_passWord"
                   class="required useTitle ajax length-range-8-16 validate-special-char" required-title="" data-attr-scan="newPassWord"/>
        </div>
    </div>
    <div class="m-form-group f-ml100 f-w500">
        <label></label>
        <div class="l-text-wrapper m-form-control">
            <table class="f-w200 f-m60 tab-txt-cen ">
                <tr>
                    <td class="f-w40 f-h20" id="td-intension-weak">弱</td>
                    <td class="f-w40 f-h20" id="td-intension-middle">中</td>
                    <td class="f-w40 f-h20" id="td-intension-powerful">强</td>
                </tr>
            </table>
        </div>
    </div>
    <div class="m-form-group f-ml100 f-w500 m-form-readonly">
        <label></label>
        <div class="l-text-wrapper m-form-control div-txt-msg">
            <div class="div-img"></div>
            <p class="f-c7 f-mb5">为了提升密码安全性，建议使用英文字母加数字或符号的混合密码！</p>
            <h4><p class="f-c7 f-mb5">密码提示:</p></h4>
            <p class="f-c7 f-mb5 p-ti2">(1)密码由8~16个字符组成,区分大小写。</p>
            <p class="f-c7 f-mb5 p-ti2">(2)不能和账号名一样,不包含空格。</p>
        </div>
    </div>
    <div class="m-form-group f-ml100 f-w500">
        <label>再次输入<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="password" id="inp_again_passWord"
                   class="required useTitle ajax equals-inp_new_passWord validate-space validate-special-char" required-title=""
                    <spring:message code="lbl.must.input"/> />
        </div>
    </div>
    <div id="div_changePassWord_toolbar" class="m-form-control pane-attribute-toolbar">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam save-toolbar" id="div_changePassWord_btn">
            <span><spring:message code="btn.save"/></span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="div_cancel_btn">
            <span><spring:message code="btn.cancel"/></span>
        </div>
    </div>

    <%--<input hidden="hidden" id="inp_userId" data-attr-scan="id" />--%>
</div>