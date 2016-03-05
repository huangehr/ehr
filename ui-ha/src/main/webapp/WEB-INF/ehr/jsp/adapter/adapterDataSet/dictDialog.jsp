<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_dict_form" data-role-form class="m-form-inline f-mt20 f-ml26" style="display: none;" data-role-form>

    <input type="hidden" id="dictId" data-attr-scan="id"/>

    <div class="m-form-group">
        <label class="label_title">标准字典项<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="dict_info_dictEntryId" data-type="select" class="required f-w240"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="dictEntryId"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title">机构字典<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="dict_info_orgDictSeq" data-type="select" class="f-w240"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="orgDictSeq"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title">机构字典项<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="dict_info_orgDictEntrySeq" data-type="select" class="f-w240"  required-title=<spring:message code="lbl.must.input"/> data-attr-scan="orgDictEntrySeq"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
        <div class="m-form-control ">
            <textarea id="dict_info_description" class="f-w240 validate-special-char" data-attr-scan="description" maxlength="500"></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa update-footer">
        <div class="m-form-control">
            <input type="button" value="保存" id="btn_save_dict" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />
            <div id="btn_cancel_dict" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span>关闭</span>
            </div>
        </div>
    </div>
</div>

