<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!--######用户管理页面Title设置######-->
<div class="f-dn" data-head-title="true">机构管理</div>

<!-- ####### 页面部分 ####### -->
<div id="div_wrapper" >
    <!-- ####### 查询条件部分 ####### -->
    <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" data-role-form>
        <div class="m-form-group f-mt10">
            <div class="m-form-control">
                <!--输入框-->
                <input type="text" id="inp_search" placeholder="请输入代码或名称" class="f-ml10" data-attr-scan="searchNm"/>
            </div>

            <div class="m-form-control f-ml10">
                <!--下拉框-->
                <input type="text" id="inp_settledWay" placeholder="请选择入驻方式" data-type="select" data-attr-scan="searchWay">
            </div>
            <div class="m-form-control f-ml10">
                <!--下拉框-->
                <input type="text" id="inp_orgType" placeholder="请选择机构类型" data-type="select" data-attr-scan="orgType">
            </div>
            <div class="m-form-control f-ml10">
                <!--下拉框-->
                <input type="text" id="inp_orgArea" data-type="comboSelect" data-attr-scan="location">
            </div>
            <div class="m-form-control f-ml10">
                <!--按钮:查询 & 新增-->
                <div id="btn_search" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
                    <span><spring:message code="btn.search"/></span>
                </div>
            </div>
            <div class="m-form-control m-form-control-fr">
                <div id="div_new_record" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
                    <span><spring:message code="btn.create"/></span>
                </div>
            </div>
        </div>
    </div>

    <!--######机构信息表######-->
    <div id="div_org_info_grid" >

    </div>
</div>
