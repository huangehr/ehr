<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######密码修改页面Title设置######-->
<div class="l-page-top m-logo"></div>
<div data-role-form class="">
    <div class="m-form-group">
        <label><spring:message code="lbl.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="1" class="required useTitle max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="realName"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="2" class="required useTitle max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="realName"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="3" class="required useTitle max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="realName"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="4" class="required useTitle max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="realName"/>
        </div>
    </div>
</div>