<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div>
  <div>
    <div class="tree-from-title">标准:</div>
    <div id="tree_from"></div>
    <div class="tree-option">
      <img src="${staticRoot}/images/arrows.png">
    </div>
    <div class="tree-to-title">已选择:</div>
    <div id="tree_to"></div>
  </div>
  <!--footer start-->
  <div class="m-form-group f-pa update-footer f-mt10 f-mb10">
    <div class="m-form-control">
      <div id="btn_save" class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" >
        <span>保存</span>
      </div>
      <div id="btn_cancel" class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam" >
        <span>关闭</span>
      </div>
    </div>
  </div>
  <!--end footer-->
</div>