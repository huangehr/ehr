<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<body>

<div class="evt-1">
    <input id="calendar_picker" class="calendar_picker">
</div>
<div class="evt-1-c">
    <div style="
        border-bottom: 1px solid #23AFFF;
        position: absolute;
        width: 936px;
        margin-top: 35px;
    "></div>
    <a href="javascript:void(0)" class="evt_1_large_prev">&nbsp;</a>
    <a href="javascript:void(0)" class="evt_1_large_next">&nbsp;</a>
    <div id="ev-detail" class="time-line-horizontal" style="width: 770px; margin: auto"></div>
</div>

</body>