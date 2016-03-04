<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<link rel="stylesheet" href="${staticRoot}/lib/plugin/captcha/idcode.css">
<style>
  #div_captcha_wrapper { position: absolute; top: 120px; left: 385px; }
  #btn_login { line-height: 25px; }
  .f-w290 { width: 290px; }

  .m-login { position: absolute; width: 100%; height:100%;}
  .m-login .box { position: absolute; left: 50%; top: 295px; width: 560px; height: 286px;  margin-left: -280px; z-index: 1;}
  .m-login .box-form { height: 265px; margin-left: 240px; margin-top: 20px;}

  .u-input-group { *position: relative; display: table; margin-top: 10px; border-collapse: separate;}
  .u-input-group .u-input-addon { display: table-cell; *display:inline-block; width: 39px; /*height: 40px;*/ border-radius: 0;   border: 1px solid #ccc; }
  .u-input-group .u-input-addon:first-child { border-right: 0; }
  .u-input-group.has-error input,
  .u-input-group.has-error select,
  .u-input-group.has-error textarea,
  .u-input-group.has-error .u-input-addon { border-color: #f09784; color: #d68273; }

  .u-input { *position: relative; *top: -1px; width: 250px; *width: 240px; height: 40px; *height: 30px; line-height:30px\9; padding: 5px 4px;  border: 1px solid #d5d5d5; border-left: 0; border-radius: 0; color: #858585; background-color: #fff; }
  .u-input.ui-input-vcode { width: 95px; }

  .u-icon-lbg { background: url(${staticRoot}/images/login_bg.jpg) 50% 0px no-repeat;}
  .u-icon-lbbg { background: url(${staticRoot}/images/login_input_bg.png) no-repeat; }
  .u-icon-user { background: url(${staticRoot}/images/icon_User.png) center no-repeat; }
  .u-icon-psw { background: url(${staticRoot}/images/icon_Cipher.png) center no-repeat; }
  .u-icon-vcode { background: url(${staticRoot}/images/icon_Verification-code.png) center no-repeat; }
  .u-icon-bb { position: absolute; top: 545px; width:100%; height: 59px; }
  .u-icon-bbp { background: url(${staticRoot}/images/login_Projection.png) center no-repeat;}


  /* 覆盖validate验证时的消息提示样式 */
  #form_login .has-error label.error { display: none !important; }
</style>
