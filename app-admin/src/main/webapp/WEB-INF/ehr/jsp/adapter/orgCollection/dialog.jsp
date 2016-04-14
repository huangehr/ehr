<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_info_form" data-role-form class="m-form-inline f-mt20 f-ml30" data-role-form>

    <input type="hidden" id="id" data-attr-scan="id"/>
    <input type="hidden" id="orgDataSetSeq" data-attr-scan="orgDataSetSeq"/>
    <input type="hidden" id="orgDictSeq" data-attr-scan="orgDictSeq"/>
    <input type="hidden" id="orgCode" data-attr-scan="organization"/>

    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.encoding"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_info_code" class="required useTitle f-w240 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="code"/>
        </div>
    </div>

    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_info_name" class="required useTitle f-w240 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="name"/>
        </div>
    </div>

    <div class="m-form-group" style="display: none" id="divSort">
        <label style="width:80px"><spring:message code="lbl.sort"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_info_sort" class="validate-special-char"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="sort"/>
        </div>
    </div>

    <div class="m-form-group">
        <label style="width:80px"><spring:message code="lbl.introduction"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control ">
            <textarea id="inp_info_description" class="f-w240 validate-special-char" data-attr-scan="description" maxlength="500"></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa update-footer">
        <div class="m-form-control">
            <input type="button" value="<spring:message code="btn.save"/>" id="btn_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />
            <div id="btn_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span><spring:message code="btn.close"/></span>
            </div>
        </div>
    </div>
</div>

