<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- ####### Title设置 ####### -->
<div class="f-dn" data-head-title="true"><spring:message code="title.std.source"/></div>

<!-- ####### 页面部分 ####### -->
<div id="div_wrapper" >

    <!-- ####### 查询条件部分 ####### -->
    <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" data-role-form>
        <div class="m-form-group f-mt10">
            <div class="m-form-control">
                <!--输入框-->
                <input type="text" id="ipt_search" placeholder="请输入编码或名称" class="f-ml10" data-attr-scan="searchNm"/>
            </div>
            <div class="m-form-control f-ml10">
                <!--类别-->
                <input type="text" id="ipt_search_type" placeholder="请选择类型" data-type="select" data-attr-scan="searchType">
            </div>
            <div class="m-form-control f-ml10">
                <!--按钮:搜索-->
                <div id="btn_search" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
                    <span>搜索</span>
                </div>
            </div>

            <!--按钮: 新增 & 批量删除-->
            <div class="m-form-control m-form-control-fr">
                <div onclick="javascript:$.publish('stdInfo:stdInfoGrid:delete',[''])" id="btn_del" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
                    <span ><spring:message code="btn.multi.delete"/></span>
                </div>
            </div>

            <div class="m-form-control m-form-control-fr-10">
                <div onclick="javascript:$.publish('std:stdInfo:open',['','new'])" id="btn_add" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
                    <span ><spring:message code="btn.create"/></span>
                </div>
            </div>
        </div>
    </div>

    <!--###### 查询明细列表 ######-->
    <div id="div_std_info_grid" >

    </div>
</div>