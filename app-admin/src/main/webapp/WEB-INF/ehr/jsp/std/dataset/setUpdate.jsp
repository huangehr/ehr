<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/25
  Time: 16:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<div id="div_set_info_form" data-role-form class="m-form-inline f-mt20">
  <div class="m-form-group">
    <label>数据集编码：</label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" class="required useTitle f-w240 max-length-15 validate-special-char" required-title=<spring:message code="lbl.must.input"/> id="txt_Code" data-attr-scan="code">
    </div>
  </div>
  <div class="m-form-group">
    <label>数据集名称：</label>
    <div class="l-text-wrapper m-form-control essential">
      <input type="text" id="txt_Name" data-attr-scan="name" class="required  useTitle f-w240 max-length-50 validate-special-char" required-title=<spring:message code="lbl.must.input"/>>
    </div>
  </div>
  <div class="m-form-group">
    <label><spring:message code="lbl.std.source"/><spring:message code="spe.colon"/></label>
    <div class="l-text-wrapper m-form-control">
      <input type="text" id="ipt_select" data-type="select" data-attr-scan="refStandard">
    </div>
  </div>
  <div class="m-form-group">
    <label><spring:message code="lbl.description"/><spring:message code="spe.colon"/></label>
    <div class="m-form-control">
      <textarea rows="3" id="txt_description" class="max-length-255 validate-special-char" name="txb_desc" style="width: 240px;height: 60px;" data-attr-scan="summary"  ></textarea>
    </div>
  </div>

  <div class="m-form-control pane-attribute-toolbar">
    <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="btn_save">
      <span><spring:message code="btn.save"/></span>
    </div>
    <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="btn_close">
      <span><spring:message code="btn.close"/></span>
    </div>
  </div>

  <input type="hidden" id="hdId" value=""/>
  <input type="hidden" id="hdversion" value=""/>
  <input type="hidden" id="hd_url" value="${contextRoot}"/>

</div>
