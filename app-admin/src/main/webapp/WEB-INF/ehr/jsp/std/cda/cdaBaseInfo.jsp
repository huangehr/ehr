<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/12/21
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<div id="div_cda_info_form" data-role-form class="m-form-inline">
    <div class=" f-mt20">
        <div class="m-form-group">
            <label>CDA编码：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" class="required useTitle ajax f-w240 max-length-100 validate-special-char" id="txt_Code" data-attr-scan="code"
                       required-title=<spring:message code="lbl.must.input"/>>
            </div>
        </div>
        <div class="m-form-group">
            <label>名称：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="txt_Name" data-attr-scan="name" class="required useTitle f-w240 max-length-100 validate-special-char"
                       required-title=<spring:message code="lbl.must.input"/>>
            </div>
        </div>
        <div class="m-form-group" style="display: none">
            <label>CDA文件路径：</label>

            <div class="m-form-control">
                <input type="text" id="txt_schema_path" data-attr-scan="schema">
            </div>
        </div>
        <div class="m-form-group">
            <label>标准来源：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="ipt_select" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/>
                       data-attr-scan="sourceId">
            </div>
        </div>
        <div class="m-form-group">
            <label>类别：</label>

            <div class="l-text-wrapper m-form-control essential">
                <input type="text" id="ipt_type" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/>
                       data-attr-scan="type">
            </div>
        </div>
        <div class="m-form-group">
            <label>说明：</label>

            <div class="m-form-control">
                <textarea rows="3" id="txt_description" name="txb_desc" style="width: 240px;height: 60px;" class="max-length-500 validate-special-char"
                          data-attr-scan="description"></textarea>
            </div>
        </div>
    </div>
    <div class="m-form-control pane-attribute-toolbar div_toolbar">
        <%--<div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" name="btn_create_file">--%>
        <%--<span>创建文档</span>--%>
        <%--</div>--%>
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" name="btn_save">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" name="btn_close">
            <span>关闭</span>
        </div>
    </div>
    <input type="hidden" id="hdId" value=""/>
    <input type="hidden" id="hdversion" value=""/>
    <input type="hidden" id="hd_url" value="${contextRoot}"/>
    <input type="hidden" id="hd_user" value="${UserId}"/>
    <input type="hidden" id="hdTypeId" value=""/>
    <input type="hidden" id="hdTypeName" value=""/>
</div>
