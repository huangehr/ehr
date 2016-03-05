<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######登录页面Title设置######-->
<div class="f-dn" data-head-title="true">登录</div>

<!--######用户登录部分######-->
<div class="m-login u-icon-lbg">
    <section class="box u-icon-lbbg">
        <form id="form_login" class="box-form" action="${contextRoot}/login/validate" method="post">
            <div class="u-input-group">
                <span class="u-input-addon u-icon-user"></span>
                <input name="userName" id="inp_user_name" data-attr-scan type="text" value="${userName}" class="u-input required useTitle" placeholder="输入用户名/邮箱/手机号"  required-title=<spring:message code="lbl.must.input"/>>
            </div>
            <div class="u-input-group">
                <span class="u-input-addon u-icon-psw"></span>
                <input name="password" id="inp_password" data-attr-scan type="password" class="u-input required useTitle" placeholder="输入密码"  required-title=<spring:message code="lbl.must.input"/>>
            </div>
            <div class="u-input-group">
                <span class="u-input-addon u-icon-vcode"></span>
                <input name="captcha" id="inp_captcha_code" data-attr-scan type="text" class="u-input ui-input-vcode required useTitle equals-ignore-case-inp_captcha_hidden"  required-title=<spring:message code="lbl.must.input"/> equals-ignore-case-title="验证码错误" placeholder="输入验证码">
                <!--###### 设置 #inp_captcha_hidden 隐藏域存储验证码，给jValidate插件提供校验 #inp_captcha_code 输入值的比对 ######-->
                <input type="hidden" id="inp_captcha_hidden" >
            </div>
            <!--###### 验证码显示容器 ######-->
            <div id="div_captcha_wrapper"></div>
            <div class="f-h20 f-mt10 f-tac"><span id="vcode-error" class="f-dn s-c13">验证码错误，请重新输入！</span></div>
            <div class="f-w290 f-mt10">
                <a class="f-fr s-c1 f-dn">忘记密码？ </a>
            </div>
            <div class="f-tac s-c13">${failMsg}</div>
            <div class="f-mt5">
                <a id="btn_login" class="btn btn-primary f-w290 f-mt10 f-fs16 f-fwb" >登录</a>
            </div>
        </form>
    </section>
    <div class="u-icon-bb u-icon-bbp"></div>
</div>
<!--######用户登录部分结束######-->