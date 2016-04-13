<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div id="div_adapterorg_info_form" data-role-form class="m-form-inline f-mt20 f-ml26" data-role-form>

    <input type="hidden" id="code" data-attr-scan="code"/>

    <div class="m-form-group">
        <label class="label_title">标准类别<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_adapterorg_type" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="type">
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title">发布机构<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_adapterorg_org" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="org">
        </div>
    </div>

    <%--<div id="areaDiv" class="m-form-group" style="display: none">--%>
        <%--<label class="label_title">地区<spring:message code="spe.colon"/></label>--%>
        <%--<input id="province" name="address.province" type="hidden" data-attr-scan="province">--%>
        <%--<input id="city" name="address.city" type="hidden" data-attr-scan="city">--%>
        <%--<input id="district" name="address.district" type="hidden" data-attr-scan="district">--%>
        <%--<input id="town" name="address.town" type="hidden" data-attr-scan="town">--%>
        <%--<input id="area_code" type="hidden" data-attr-scan="areaCode">--%>
        <%--<div class="l-text-wrapper m-form-control">--%>
            <%--<input type="text" id="inp_adapterorg_area" class=" f-w240"  required-title=<spring:message code="lbl.must.input"/> placeholder="请选择地址"  data-attr-scan="area"--%>
                   <%--data-type="select" />--%>

            <%--<div id="selArea" style="border: 1px solid #ccc;height:240px;position: absolute;width: 240px;background-color: white;z-index: 1000;display: none" >--%>
                <%--<div id="area_sel_form" data-role-form class="m-form-inline f-mt20" data-role-form>--%>
                    <%--<div class="m-form-group">--%>
                        <%--<label style="width:40px">省：</label>--%>
                        <%--<div class="l-text-wrapper m-form-control">--%>
                            <%--<input type="text" id="sel_province" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="selprovince">--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="m-form-group">--%>
                        <%--<label style="width:40px">市：</label>--%>
                        <%--<div class="l-text-wrapper m-form-control">--%>
                            <%--<input type="text" id="sel_city" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="selcity">--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="m-form-group">--%>
                        <%--<label style="width:40px">区：</label>--%>
                        <%--<div class="l-text-wrapper m-form-control">--%>
                            <%--<input type="text" id="sel_district" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="seldistrict">--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="m-form-group">--%>
                        <%--<label style="width:40px">县：</label>--%>
                        <%--<div class="l-text-wrapper m-form-control">--%>
                            <%--<input type="text" id="sel_town" data-type="select" class="required useTitle" required-title=<spring:message code="lbl.must.input"/> data-attr-scan="seltown">--%>
                        <%--</div>--%>
                    <%--</div>--%>

                    <%--<div class="m-form-group f-pr" align="right">--%>
                        <%--<div class="m-form-control f-pa" style="right: 10px">--%>
                            <%--<div id="btn_ok_sel" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >--%>
                                <%--<span>确定</span>--%>
                            <%--</div>--%>
                            <%--<div id="btn_cancel_sel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >--%>
                                <%--<span>取消</span>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>
        <%--</div>--%>
    <%--</div>--%>

    <div class="m-form-group">
        <label class="label_title">标准名称<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control essential">
            <input type="text" id="inp_adapterorg_name" class="required useTitle f-w240  max-length-100 validate-special-char"  required-title=<spring:message code="lbl.must.input"/> placeholder="请输入名称"  data-attr-scan="name"/>
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title">继承标准<spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <input type="text" id="inp_adapterorg_parent" data-type="select" class="" data-attr-scan="parent">
        </div>
    </div>

    <div class="m-form-group">
        <label class="label_title"><spring:message code="lbl.introduction"/><spring:message code="spe.colon"/></label>
        <div class="l-text-wrapper m-form-control">
            <textarea  id="inp_adapterorg_description"  data-attr-scan="description"  class="max-length-200 validate-special-char"></textarea>
        </div>
    </div>

    <div class="m-form-group f-pa update-footer">
        <div class="m-form-control">
            <input type="button" value="保存" id="btn_save_adapterOrg" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" />

            <div id="btn_cancel_adapterOrg" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
                <span>关闭</span>
            </div>
        </div>
    </div>
</div>



