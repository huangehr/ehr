<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_dictentry_info_form" data-role-form class="m-form-inline f-mt20 f-ml30" data-role-form>

    <input type="hidden" id="entryId" data-attr-scan="entryId"/>
    <input type="hidden" id="dictId" data-attr-scan="dictId"/>
    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.valueField.code"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_dictentry_code" class="required useTitle f-w240 max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/>  data-attr-scan="code"/>
        </div>
    </div>
    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.valueField.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_dictentry_value" class="required useTitle f-w240 max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/>  data-attr-scan="value"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.description"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <textarea type="text" id="inp_dictentry_desc" class="max-length-200 validate-special-char"  data-attr-scan="desc"></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa" style="right: 10px;bottom: 0;">
        <div class="m-form-control">
            <input type="button" value="<spring:message code="btn.save"/>" id="btn_entry_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />
            <div id="btn_entry_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span><spring:message code="btn.close"/></span>
            </div>
        </div>
    </div>
</div>

