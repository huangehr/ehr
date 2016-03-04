<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_std_info_form" data-role-form class="m-form-inline f-mt20 f-ml26" data-role-form>

    <input type="hidden" id="id" data-attr-scan="id"/>
    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.type"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_std_type" data-type="select" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="type">
        </div>
    </div>
    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.encoding"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_std_code" class="required useTitle f-w240 max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="code"  />
        </div>
    </div>
    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_std_name" class="required useTitle f-w240 max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="name"/>
        </div>
    </div>

    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.introduction"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control ">
            <textarea id="inp_std_description" class="f-w240 max-length-200 validate-special-char" data-attr-scan="description" maxlength="500"></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa" style="bottom: 0;right: 10px;">
        <div class="m-form-control">
            <input type="button" value="<spring:message code="btn.save"/>" id="btn_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />
            <div id="btn_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span><spring:message code="btn.close"/></span>
            </div>
        </div>
    </div>
</div>

