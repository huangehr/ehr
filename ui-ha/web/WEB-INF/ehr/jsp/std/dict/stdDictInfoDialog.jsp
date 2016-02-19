<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_stddict_info_form" data-role-form class="m-form-inline f-mt20 f-ml30" data-role-form>

    <input type="hidden" id="id" data-attr-scan="id"/>
    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.dict"/><spring:message code="lbl.encoding"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_stddict_code" class="required useTitle f-w240 max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> placeholder="请输入编码"  data-attr-scan="code"/>
        </div>
    </div>
    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.dict.name"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_stddict_name" class="required useTitle f-w240 max-length-50 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> placeholder="请输入名称"  data-attr-scan="name"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.dict.base"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control ">
            <input type="text" id="inp_stddict_basedict" data-type="select" class="" data-attr-scan="baseDictId">
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.std.source"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_stddict_stdSource" data-type="select" class="required useTitle" data-attr-scan="stdSource" required-title=<spring:message code="lbl.must.input"/> >
        </div>
    </div>

    <div class="m-form-group" id="div_stddict_stdVersion" >
        <label class="label_title"><spring:message code="lbl.version.ref"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_stddict_stdVersion" data-attr-scan="stdVersion"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.description"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <textarea type="text" id="inp_stddict_description" class="max-length-225 validate-special-char"  data-attr-scan="description" ></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa" style="right: 10px;bottom:0;">
        <div class="m-form-control" >
            <input type="button" value="<spring:message code="btn.save"/>" id="btn_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />
            <div id="btn_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span><spring:message code="btn.close"/></span>
            </div>
        </div>
    </div>
</div>

