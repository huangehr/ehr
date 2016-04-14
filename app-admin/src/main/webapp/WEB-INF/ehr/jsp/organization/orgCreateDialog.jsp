<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######机构管理页面 > 机构信息对话框模板页######-->
<div id="div_organization_info_form" data-role-form="" class="m-form-inline f-mt20" data-role-form style="overflow:auto">
    <div class="m-form-group">
        <label><spring:message code="lbl.org.code"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="org_code" class="required useTitle ajax validate-space validate-org-code f-w240 max-length-20"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="organizationCode"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.org.fullName"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="full_name" class="required useTitle f-w240 max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="fullName"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.org.shortName"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="short_name" class="required useTitle f-w240 max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="shortName"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.local"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="location" class="required useTitle f-w240 validate-special-char" data-type="comboSelect" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="location" maxlength="200"/>
        </div>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.join.mode"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="settled_way" class="required" data-type="select"  data-attr-scan="settledWay">
        </div>
    </div>

    <div class="m-form-group">
        <label>联系人<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="admin" class="useTitle f-w240 max-length-50 validate-special-char"  data-attr-scan="admin"/>
        </div>
    </div>
    <div class="m-form-group">
        <label>联系方式<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="tel" class="required useTitle  validate-mobile-and-phone f-w240"  required-title=<spring:message code="lbl.must.input"/> validate-mobile-and-phone-title="请输入正确的手机号码或固话"  data-attr-scan="tel"/>
        </div>
    </div>
    <div class="m-form-group">
        <label><spring:message code="lbl.org.type"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="org_type" class=" f-w240 required" data-type="select"  data-attr-scan="orgType"/>
        </div>
        <spring:message code="spe.colon"/>
    </div>

    <div class="m-form-group">
        <label><spring:message code="lbl.tip"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control ">
            <textarea id="dialog_tags" class="f-w240 max-length-100 validate-special-char" placeholder="多个标签，请用分号隔开" data-attr-scan="tags" maxlength="500"></textarea>
        </div>
    </div>
	<div class="m-form-control pane-attribute-toolbar">
		<div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam save-toolbar" id="div_update_btn">
			<span><spring:message code="btn.save"/></span>
		</div>
		<div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="btn_cancel">
			<span><spring:message code="btn.close"/></span>
		</div>
	</div>
</div>