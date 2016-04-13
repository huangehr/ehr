<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_addArchiveTpl_form" data-role-form class="m-form-inline">
<input type="hidden" id="id" data-attr-scan="id">
	<input type="hidden" id="oldTitle">
    <div class="m-form-group">
        <label>模板：</label>

        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_title" class="required  useTitle ajax f-w238 max-length-32 validate-special-char"  required-title=<spring:message code="lbl.must.input"/>
                   data-attr-scan="title"/>
        </div>
    </div>

    <div class="m-form-group">
        <label>版本号：</label>

        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_versionNo" class="required  useTitle f-w238 " placeholder="请输入版本号" required-title=<spring:message code="lbl.must.input"/> data-type="select"
                   data-attr-scan="version"/>
        </div>
    </div>

    <div class="m-form-group">
        <label>CDA文档：</label>

        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_dataset" class="required  useTitle"  required-title=<spring:message code="lbl.must.input"/> data-type="select"
                   data-attr-scan="cdaType"/>
        </div>
    </div>

    <div class="m-form-group">
        <label>医疗机构：</label>

        <div class="m-form-control essential">
            <!--输入框带查询-->
            <input type="text" id="inp_org"  class="f-ml10 required  useTitle f-w238 validate-org-length"  data-type="comboSelect"
				   required-title=<spring:message code="lbl.must.input"/>  data-attr-scan="organizationCode" validate-org-length="请选择到医院一级！"/>
        </div>
    </div>

<%--    <div class="f-mt50 f-ml100">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="div_add_btn">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar f-ml50" id="div_cancel_btn">
            <span>关闭</span>
        </div>
    </div>--%>

    <div class="m-form-control pane-attribute-toolbar">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="div_add_btn">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="div_cancel_btn">
            <span>关闭</span>
        </div>
    </div>
</div>