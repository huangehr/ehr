
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- ####### Title设置 ####### -->
<div class="f-dn" data-head-title="true"><spring:message code="title.template.manage"/></div>

<!-- ####### 页面部分 ####### -->
<div id="div_wrapper" >


    <!-- ####### 查询条件部分 ####### -->
    <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" data-role-form>

        <div class="f-fr version">
<%--
            <label for="inp_searchVersion"><spring:message code="lbl.version.select"/></label>
--%>
            <input type="text" data-type="select" id="inp_searchVersion" data-attr-scan="version">
        </div>

        <div class="m-form-group f-mt10">
            <div class="m-form-control">
                <!--输入框带查询-->
                <input type="text" id="inp_searchOrgName" placeholder="请输入模板或医疗机构" class="f-ml10 " data-attr-scan="orgName"/>
            </div>
            <div class="m-form-control m-form-control-fr f-ml10">
                <div id="btn_add" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" onclick="javascript:$.publish('tpl:tplInfo:open',['','new'])" >
                    <span><spring:message code="btn.create"/></span>
                </div>
            </div>
        </div>
    </div>

    <!--###### 查询明细列表 ######-->
    <div id="div_tpl_info_grid" >

    </div>

    <div id="div_user_img_upload" class="f-dn" >
        <div id="div_file_picker" class="f-mt10">选择文件</div>
    </div>
</div>