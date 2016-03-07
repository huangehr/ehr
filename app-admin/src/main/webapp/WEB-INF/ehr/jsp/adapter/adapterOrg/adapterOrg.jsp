<%--
  Created by IntelliJ IDEA.
  User: zqb
  Date: 2015/10/26
  Time: 17:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!-- ####### Title设置 ####### -->
<div class="f-dn" data-head-title="true">第三方标准</div>

<!-- ####### 页面部分 ####### -->
<div id="div_wrapper" >

  <!-- ####### 查询条件部分 ####### -->
  <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" data-role-form>
    <div class="m-form-group f-mt10">
      <div class="m-form-control">
        <input type="text" id="inp_search" placeholder="请输入标准名称" class="f-ml10" data-attr-scan="searchNm"/>
      </div>
      <div class="m-form-control f-ml20">
        <input type="text" id="inp_type" placeholder="请选择标准类别" data-type="select" class="f-ml20" data-attr-scan="type"/>
      </div>
      <div class="m-form-control f-ml20">
        <div id="btn_search" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
          <span><spring:message code="btn.search"/></span>
        </div>
      </div>
      <div class="m-form-control m-form-control-fr">
        <div id="btn_add" onclick="javascript:$.publish('adapter:adapterInfo:open',['','new'])" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
          <span><spring:message code="btn.create"/></span>
        </div>
        <div id="btn_multiDel" onclick="javascript:$.publish('adapter:adapterInfo:del',[''])" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-ml10" >
          <span><spring:message code="btn.multi.delete"/></span>
        </div>
      </div>
    </div>
  </div>

  <!--###### 查询明细列表 ######-->
  <div id="div_adapter_info_grid" >

  </div>
</div>
