<%--
  Created by IntelliJ IDEA.
  User: zqb
  Date: 2015/10/26
  Time: 17:19
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>采集标准管理</title>
  <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>

  <script>
    seajs.use(['jquery','app/adapter/adapterOrg'],function($,AdapterOrg){})
  </script>

</head>
<body>

<!--condition、search、add、delAll start-->
<div class="condition">
  <div class="f-mt10 f-ml10">
    <input type="text" id="search_no" placeholder="采集标准代码或名称">
    <div class="J_searchBtn image-search f-pa" ></div>
    <button type="button" class="btn btn-primary f-mr10 f-fr" data-toggle="modal"
            data-target="#delete_rows_modal"><spring:message code="btn.multi.delete"/></button>
    <button type="button" class="btn btn-primary f-mr20 f-fr" id="add_btn" data-toggle="modal"
            data-target="#modify_row_modal">
      <spring:message code="btn.create"/></button>
  </div>
  <div id="adapter_grid" data-pagerbar-items="10" class="f-mt10"></div>
</div>
<!--end condition、search、add、delAll-->

<!--delete box start-->
<div class="modal fade msg-modal" id="delete_row_modal" tabindex="0" role="dialog" aria-labelledby="confirm_modal_label" data-backdrop="static">
  <div class="modal-dialog msg-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header modal-title">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="confirm_modal_label"><spring:message code="lbl.delete.message"/></h4>
      </div>
      <div class="modal-body f-tac msg-body">
        <input id="delete_id" type="hidden"/>
        <h5><strong><spring:message code="msg.000003"/></strong></h5>
        <h5><spring:message code="msg.000004"/></h5>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete_btn">
          <spring:message code="btn.affirm"/></button>
        <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                code="btn.cancel"/></button>
      </div>
    </div>
  </div>
</div>
<!--end delete box-->

<!--deleteAll box start-->
<div class="modal fade msg-modal" id="delete_rows_modal" tabindex="0" role="dialog" aria-labelledby="confirm-modal-labels" data-backdrop="static">
  <div class="modal-dialog msg-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header modal-title">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="confirm-modal-labels"><spring:message code="lbl.delete.message"/></h4>
      </div>
      <div class="modal-body f-tac msg-body">
        <h5><strong>确认删除这几行信息？</strong></h5>
        <h5><spring:message code="msg.000004"/></h5>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete_rows_btn">
          <spring:message code="btn.affirm"/></button>
        <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                code="btn.cancel"/></button>
      </div>
    </div>
  </div>
</div>
<!--end deleteAll box-->

<!--地址信息-->
<div class="modal fade f-pt20 local-model" id="location_model" tabindex="-1" role="dialog"
     aria-labelledby="location_modal_label" data-backdrop="static">
  <div class="modal-dialog f-w500" role="document">
    <div class="modal-content f-w500">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="location_modal_label">请选择位置信息</h4>
      </div>
      <div class="modal-body f-tac local-body">
                <span>
                    <label>省：</label>
                    <select id="province_select" class="f-w200">
                    </select>
                    <label>市：</label>
                    <select id="city_select" class="f-w200">
                    </select>
                    <label>区：</label>
                    <select id="district_select" class="f-w200">
                    </select>
                    <label>县：</label>
                    <select id="town_select" class="f-w200">
                    </select>
                </span>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal" id="location_confirm">
          <spring:message code="btn.affirm"/></button>
        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                code="btn.cancel"/></button>
      </div>
    </div>
  </div>
</div>

<div class="modal fade" id="modify_row_modal" tabindex="-1" role="dialog" aria-labelledby="my_modal_label" data-backdrop="static">
  <form id="update_form">
    <div class="modal-dialog f-w400" role="document">
      <div class="modal-content f-w400">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="my_modal_label">采集标准基本信息</h4>
        </div>
        <div class="modal-body">
          <table class="table">
            <tr>
              <td><div class="f-tar">类型</div></td>
              <td><select id="type" name="type" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>>
              </select></td>
            </tr>
            <tr id="orgModel">
              <td><div class="f-tar">采集机构</div></td>
              <td><select id="org" name="org" data-placeholder="请选择映射机构" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>>
              </select></td>
            </tr>
            <tr id="areaModel">
              <td><div class="f-tar">地区</div></td>
              <td>
                <input id="province" name="area.province" type="hidden">
                <input id="city" name="area.city" type="hidden">
                <input id="district" name="area.district" type="hidden">
                <input id="town" name="area.town" type="hidden">
                <input id="area_code" type="hidden">
                <input id="area" placeholder="<spring:message code="lbl.address.tips"/>" readOnly data-toggle="modal"
                       data-target="#location_model" class="address">
              </td>
            </tr>
            <tr>
              <td><input id="code" name="code" type="hidden"></td>
            </tr>
            <tr>
              <td><div class="f-tar"><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></div></td>
              <td><input id="name" name="name" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>></td>
            </tr>
            <tr id="parentModel">
              <td><div class="f-tar">初始标准</div></td>
              <td><select id="parent" name="parent" data-placeholder="请选择初始标准">
              </select></td>
            </tr>
            <tr>
              <td><div class="f-tar"><spring:message code="lbl.introduction"/><spring:message code="spe.colon"/></div></td>
              <td><textarea id="description" name="description"></textarea><td>
            </tr>
          </table>
        </div>
        <!--footer start-->
        <div class="modal-footer" id="footer">
          <button type="button" class="btn btn-primary" id="update_btn">
            <spring:message code="btn.save"/></button>
          <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message
                  code="btn.cancel"/></button>
        </div>
        <!--end footer-->
      </div>
    </div>
  </form>
</div>

</body>
</html>
