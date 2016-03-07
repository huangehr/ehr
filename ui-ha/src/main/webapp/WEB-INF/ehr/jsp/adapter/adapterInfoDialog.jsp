<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_adapter_info_form" data-role-form class="m-form-inline f-mt20">
  <input id="id" name="id" data-attr-scan="id"  hidden>
  <div class="m-form-group">
    <label>方案类别<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control essential u-readonly">
      <input type="text" id="ipt_type" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="type">
    </div>
  </div>
  <div class="m-form-group">
    <label>方案代码<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" id="ipt_code" class="required useTitle max-length-50 validate-special-char" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="code">
    </div>
  </div>
  <div class="m-form-group">
    <label>方案名称<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" id="ipt_name" class="required useTitle  max-length-50 validate-special-char" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="name"/>
    </div>
  </div>
  <div class="m-form-group u-readonly">
    <label>标准版本<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" id="ipt_version" data-type="select" class="required useTitle" data-attr-scan="version" required-title=<spring:message code="lbl.must.input"/>>
    </div>
  </div>
  <div class="m-form-group u-readonly">
    <label>采集机构<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" id="ipt_org" data-type="select" class="required useTitle" data-attr-scan="org" required-title=<spring:message code="lbl.must.input"/>>
    </div>
    <img id="add_org" src="${staticRoot}/images/add_btn_pre.png" class="add-image">
  </div>
  <div class="m-form-group u-readonly">
    <label>继承方案<spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control">
      <input type="text" id="ipt_parent" data-type="select" class="" data-attr-scan="parentId">
    </div>
  </div>
  <div class="m-form-group">
    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
    <div class="m-form-control ">
      <textarea id="ipt_description" class="f-w240 description  max-length-200 validate-special-char" data-attr-scan="description" maxlength="500"></textarea>
    </div>
  </div>
  <div class="m-form-group f-pa update-footer">
    <div class="m-form-control">
      <div id="btn_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
        <span>保存</span>
      </div>
      <div id="btn_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
        <span>关闭</span>
      </div>
    </div>
  </div>
</div>
