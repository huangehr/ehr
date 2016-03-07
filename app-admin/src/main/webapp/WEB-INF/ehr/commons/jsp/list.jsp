<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/23
  Time: 10:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
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
    <tiles:insertAttribute name="header"/>
        <!--##list css##-->
    <tiles:insertAttribute name="listCss" ignore="true"/>
    <tiles:insertAttribute name="pageCss" ignore="true"/>
</head>
<body>
<div class="frame-list-layout">
    <div class="list-section">
        <div class="list-section-head"><h4>选择列表</h4></div>
        <div class="pane-search" id="pane-search">
            <label for="txb-key">检索关键字：</label>
            <input type="text" id="txb-key">
       <%--     <label id="txb-catalog" style="visibility:hidden">类别:</label>
            <select id="select-catalog" style="display:none"></select>--%>
        </div>
        <div id="pane-list"></div>
    </div>
    <div class="list-section">
        <div class="list-section-head"><h4>已选择项</h4></div>
        <ul id="pane-list-selected"></ul>
    </div>
    <div class="pane-list-toolbar">
        <%--<a class="btn" id="btn-submit">@*确认*@</a>--%>
        <%--<a class="btn" id="btn-cancel">@*取消*@</a>--%>
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="btn-submit">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="btn-cancel">
            <span>关闭</span>
        </div>
    </div>
    <input type="hidden" id="hd_url" value="${contextRoot}"/>
</div>

<tiles:insertAttribute name="footer"/>
<tiles:insertAttribute name="listJs" ignore="true"/>
<tiles:insertAttribute name="pageJs" ignore="true"/>
</body>
</html>