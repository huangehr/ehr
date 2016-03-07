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
    <tiles:insertAttribute name="header" />
    <link rel="stylesheet" href="${contextRoot}<tiles:getAsString name="pageCss" ignore="true"/>">

</head>
<body>
    <div data-content-page>
        <tiles:insertAttribute name="contentPage" />
    </div>
    <tiles:insertAttribute name="footer"/>
    <script src="${contextRoot}<tiles:getAsString name="pageJs" ignore="true"/>"></script>
</body>
</html>