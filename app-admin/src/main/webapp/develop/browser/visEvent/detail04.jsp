<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>



    <!-- 体征指标动态 -->
    <%--<div id="b_detail_4" class="vis-details" style="height: 870px">--%>
    <div class="bodysta-type-bar">
        <a href="javascript:void(0)" id="ms_left" class="ms-left bodysta-type-left">&nbsp;</a>
        <div id="ms_typeLs" style="float: left; overflow: hidden; height: 56px; ">
            <%--<ul>--%>
                <%--<li><a id="ms_type_1" class="active" href="javascript:void (0)">身高体重</a></li>--%>
                <%--<li><a id="ms_type_2" href="javascript:void (0)">血压脉搏</a></li>--%>
                <%--<li><a id="ms_type_3" href="javascript:void (0)">血糖</a></li>--%>
                <%--<li><a id="ms_type_4" href="javascript:void (0)">血氧</a></li>--%>
                <%--<li><a id="ms_type_5" href="javascript:void (0)">脂肪</a></li>--%>
                <%--<li><a id="ms_type_6" href="javascript:void (0)">骨密度</a></li>--%>
            <%--</ul>--%>
        </div>
        <a href="javascript:void(0)" id="ms_right" class="ms-right ms-right-active bodysta-type-right">&nbsp;</a>
    </div>

    <div class="bodysta-type-content">
        <div id="charts" class="bodysta-type-charts"></div>
        <div class="bodysta-type-info">
            <div class="bodysta-type-info-title">指标说明</div>
            <div style="height: 200px" class="">&nbsp;</div>
            <div class="bodysta-type-info-title">指标分析</div>
            <div class="">&nbsp;</div>
        </div>
    </div>
    <div class="bodysta-type-records">
        <div class="bodysta-type-r-bar">
            <div class="bodysta-type-r-title">历史记录</div>
            <div class="bodysta-type-r-search"></div>
        </div>
        <div class="bodysta-type-r-grid">
            <table id="bt_table"></table>
        </div>
    </div>
<%--</div>--%>

