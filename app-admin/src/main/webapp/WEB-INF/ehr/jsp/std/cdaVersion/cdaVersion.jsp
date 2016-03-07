<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######标准版本管理页面Title设置######-->
<div class="f-dn" data-head-title="true">标准版本管理</div>
<div id="div-wrapper">
	<!--#########查询部分##&ndash-->
	<div class="m-retrieve-area f-h50 f-pr m-form-inline" data-role-form>
		<div class="m-form-group f-mt10">
			<div class="m-form-control">
				<!--输入框-->
				<input type="text" id="inp_searchNm" placeholder="请输入版本号或版本名称" data-attr-scan="searchNm"/>
			</div>
			<!--按钮:搜索 & 新增-->
			<div class="m-form-control f-ml20">
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
	<!--###CDA版本信息表###-->
	<div id="div-cdaVersion-grid"></div>
</div>


