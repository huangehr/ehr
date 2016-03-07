<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######人口管理页面Title设置######-->
<div class="f-dn" data-head-title="true">人口管理</div>
<div id="div_wrapper" >
  <div class="m-retrieve-area f-h50 f-dn f-pr  m-form-inline" data-role-form>
    <%--<input type="text" id="inp_search" placeholder="输入姓名或身份证号"/>--%>
      <div class="m-form-group">
        <div class="m-form-control">
          <!--输入框-->
          <input type="text" id="inp_search" placeholder="请输入姓名或身份证号" class="f-ml10" data-attr-scan="searchNm"/>
        </div>

        <div class="m-form-control f-ml10">
          <!--下拉框-->
          <input type="text" id="search_homeAddress"  data-type="comboSelect" data-attr-scan="homeAddress"/>
        </div>

        <div class="m-form-control f-ml10">
          <!--按钮:查询 & 新增-->
          <div id="btn_search" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
            <span><spring:message code="btn.search"/></span>
          </div>
        </div>

        <div class="m-form-control m-form-control-fr">
          <div id="div_new_patient" class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam" >
            <span><spring:message code="btn.create"/></span>
          </div>
        </div>
        <%--<div id="div_new_patient" class="l-button u-btn u-btn-primary u-btn-small l-button-over">
          <span>新增</span>
        </div>--%>
      </div>
  </div>
  <!--######人口信息表######-->
  <div id="div_patient_info_grid" >

  </div>
  <!--######人口信息表#结束######-->
</div>
<div id="div_user_info_dialog">

</div>