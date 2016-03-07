<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<div id="div_addCard_info_form" data-role-form class="m-form-inline">
    <div class="f-pr u-bd">
        <div class="f-pa f-w20 f-wtl">
            卡关联
        </div>
    <div class="m-form-group f-mt30">
        <div class="m-form-control f-ml10">
            <input type="text" id="inp_select_cardType" placeholder="类型" data-type="select" data-attr-scan="">
        </div>
    </div>
    <div class="m-retrieve-area f-h50 f-dn f-pr f-w50">
        <input type="text" id="inp_search" placeholder="卡号"/>
    </div>
    <div id="div_addCard_info" class="f-mt-45"></div>
    </div>
</div>

