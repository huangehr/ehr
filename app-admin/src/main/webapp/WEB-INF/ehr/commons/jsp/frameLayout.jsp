<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <%--定义页面文档类型以及使用的字符集,浏览器会根据此来调用相应的字符集显示页面内容--%>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8">

    <%--IE=edge告诉IE使用最新的引擎渲染网页，chrome=1则可以激活Chrome Frame.--%>
    <meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">

    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${title}</title>
    <tiles:insertAttribute name="header" />
    <tiles:insertAttribute name="layoutCss" ignore="true"/>
    <tiles:insertAttribute name="pageCss" ignore="true"/>
</head>
<body>

<div class="l-page-top m-logo">
    <div class="f-fr usr_msg">
        欢迎登录：${current_user.realName}&nbsp;&nbsp;|&nbsp;&nbsp;<a href="${contextRoot}/user/initialChangePassword"
                                                                 class="f-color-0">修改密码</a><br>
        上次登录：${last_login_time}&nbsp;&nbsp;|&nbsp;&nbsp;<a href="${contextRoot}/logout" class="f-color-0">退出</a>
    </div>
</div>
<div id="div_main_content" class="l-layout">
    <div position="left" class="l-layout-content f-hh" hidetitle="true">
        <!--菜单导航栏-->
        <div class="m-nav-menu f-hh" id="menucyc-scroll">
            <div class="m-snav-title f-pr f-h40 f-ww f-fs14 s-c0 s-bc2 f-fwb">
                <div class="img-bgp"></div>
                <spring:message code="title.navigation.menu"/>
            </div>
            <div class="m-nav-tree f-hh">
                <ul id="ul_tree" class="m-snav"></ul>
            </div>
        </div>
    </div>
    <div position="center" title="" class="l-layout-content">
        <%--<div id="div_notice_container" class="f-pa">--%>
        <%--<div class="oni-notice-flow">--%>
        <%--<div class="u-notice success" style="height: 9px;">--%>
        <%--<div class="msgContent f-dn">--%>
        <%--<div class="noty_bar">--%>
        <%--<div class="noty_message f-tac f-pr" style="padding: 8px 10px 9px; width: auto;">--%>
        <%--<span class="noty_text"></span>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--<div class="messageControlBar"></div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <%--</div>--%>
        <div id="div_nav_breadcrumb_bar" class="u-nav-breadcrumb f-pl10 s-bc5 f-fwb f-dn">位置：<span id="span_nav_breadcrumb_content"></span></div>
        <div data-content-page class="f-p10 f-pr" id="contentPage">
            <%--<tiles:insertAttribute name="contentPage" ignore="true"/>--%>
        </div>
    </div>
</div>

<tiles:insertAttribute name="footer" />
<tiles:insertAttribute name="layoutJs" ignore="true"/>
<tiles:insertAttribute name="pageJs" ignore="true"/>
</body>
</html>
