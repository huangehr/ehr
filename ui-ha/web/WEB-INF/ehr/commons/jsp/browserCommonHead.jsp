<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<%--定义页面文档类型以及使用的字符集,浏览器会根据此来调用相应的字符集显示页面内容--%>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">

<%--IE=edge告诉IE使用最新的引擎渲染网页，chrome=1则可以激活Chrome Frame.--%>
<meta http-equiv="X-UA-Compatible" content="IE=Edge,chrome=1">

<meta name="viewport" content="width=device-width, initial-scale=1">

<link rel="stylesheet" href="${staticRoot}/common/reset.css">
<link rel="stylesheet" href="${staticRoot}/lib/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="${staticRoot}/common/font-awesome.css">
<link rel="stylesheet" href="${staticRoot}/common/function.css">
<link rel="stylesheet" href="${staticRoot}/common/unit.css">
<link rel="stylesheet" href="${staticRoot}/common/skin.css">

<link rel="stylesheet" href="${staticRoot}/lib/plugin/tips/tips.css">
<link rel="stylesheet" href="${staticRoot}/lib/ligerui/skins/Aqua/css/ligerui-all.css">
<link rel="stylesheet" href="${staticRoot}/lib/ligerui/skins/Gray2014/css/all.css">
<link rel="stylesheet" href="${staticRoot}/lib/plugin/scrollbar/jquery.mCustomScrollbar.css">
<link rel="stylesheet" href="${staticRoot}/lib/plugin/upload/webuploader.css">
<link rel="stylesheet" href="${staticRoot}/lib/plugin/upload/upload.css">
<link rel="stylesheet" href="${staticRoot}/lib/plugin/combo/comboDropdown.css">

<link rel="stylesheet" href="${staticRoot}/lib/ligerui/skins/custom/css/all.css">
<link rel="stylesheet" href="${staticRoot}/common/cover.css">

<link rel="stylesheet" href="${staticRoot}/browser/css/browse.css">
<link rel="stylesheet" href="${staticRoot}/browser/lib/timepager/timepager.css">
<link rel="stylesheet" href="${staticRoot}/browser/lib/timeline/timeline.css">
<link rel="stylesheet" href="${staticRoot}/browser/lib/grid/css/bootstrap.min.css">
<link rel="stylesheet" href="${staticRoot}/browser/lib/grid/css/bootstrap-table.min.css">
<link rel='stylesheet' href='${staticRoot}/browser/lib/fullcalendar/fullcalendar.css'  />
<link rel='stylesheet' href='${staticRoot}/browser/lib/fullcalendar/fullcalendarExt.css'  />
<link rel='stylesheet' href='${staticRoot}/browser/lib/fullcalendar/fullcalendar.print.css'  media='print' />
<link rel='stylesheet' href='${staticRoot}/browser/lib/mspagination/ms-pagination.css'  />
<link rel="stylesheet" href="${staticRoot}/browser/lib/floatMenu/floatMenu.css">
<!--[if lt IE 9]>
<%-- 让IE9以下的IE支持HTML5元素 --%>
<script src="${staticRoot}/extra/html5shiv.js"></script>
<![endif]-->

<script src="${staticRoot}/lib/jquery/jquery-1.9.1.js"></script>
<script>
    $.extend({
        Context: {
            PATH: '${contextRoot}',
            STATIC_PATH: '${staticRoot}'
        }
    })
</script>

