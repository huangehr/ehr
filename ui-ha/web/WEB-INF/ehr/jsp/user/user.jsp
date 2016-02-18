<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######用户管理页面Title设置######-->
<div class="f-dn" data-head-title="true"><spring:message code="title.user.manage"/></div>
<div id="div_wrapper" >
    <!-- ####### 查询条件部分 ####### -->
    <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" data-role-form>
        <div class="m-form-group f-mt10 ">
            <div class="m-form-control">
                <!--输入框-->
                <input type="text" id="inp_search" placeholder=<spring:message code="lbl.input.name.org"/> class="f-ml10" data-attr-scan="searchNm"/>
            </div>

            <div class="m-form-control f-ml20">
                <!--下拉框-->
                <input type="text" id="inp_select_searchType" placeholder=<spring:message code="lbl.select.userType"/> data-type="select" data-attr-scan="searchType">
            </div>
            <div class="m-form-control f-ml20">
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
    <!--######用户信息表######-->
    <div id="div_user_info_grid" >

    </div>
    <!--######用户信息表#结束######-->
</div>