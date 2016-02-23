<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2015/9/15
  Time: 14:28
  To change this template use File | Settings | File Templates.
--%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="title.std.source"/></title>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>

  <script>
    seajs.use(['jquery','app/std/standardsource/standardSource'],function($,StandardSource){})
  </script>

</head>
<body>
  <!--version start-->
  <div class="f-fr version">
    <span class="f-fwb"><spring:message code="lbl.version.select"/></span>
    <select id="version-no">
    </select>
  </div>
  <!--end version-->

  <!--condition、search、add、delAll start-->
  <div class="f-mt50 condition">
    <div class="f-mt10 f-ml10">
          <input type="text" id="search-no" placeholder="<spring:message code="lbl.code.name"/>">
          <div class="J_searchBtn image-search f-pa" ></div>
          <button type="button" class="btn btn-primary f-mr10 f-fr" id="del-btn" data-toggle="modal"
                  data-target="#delete-rows-modal"><spring:message code="btn.multi.delete"/></button>
          <button type="button" class="btn btn-primary f-mr20 f-fr" id="add-btn" data-toggle="modal"
                  data-target="#modify-row-modal">
            <spring:message code="btn.create"/></button>
    </div>
    <div id="data-grid" data-pagerbar-items="10" class="f-mt10"></div>
  </div>
  <!--end condition、search、add、delAll-->

  <!--delete box start-->
  <div class="modal fade msg-modal" id="delete-row-modal" tabindex="0" role="dialog" aria-labelledby="confirm-modal-label" data-backdrop="static">
    <div class="modal-dialog msg-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header modal-title">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                  aria-hidden="true">&times;</span></button>
          <h4 class="modal-title" id="confirm-modal-label"><spring:message code="lbl.delete.message"/></h4>
        </div>
        <div class="modal-body f-tac msg-body">
          <input id="delete-id" type="hidden"/>
          <h5><strong><spring:message code="msg.000003"/></strong></h5>
          <h5><spring:message code="msg.000004"/></h5>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete-btn">
            <spring:message code="btn.affirm"/></button>
          <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                  code="btn.cancel"/></button>
        </div>
      </div>
    </div>
  </div>
  <!--end delete box-->

  <!--deleteAll box start-->
  <div class="modal fade msg-modal" id="delete-rows-modal" tabindex="0" role="dialog" aria-labelledby="confirm-modal-labels" data-backdrop="static">
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
          <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete-rows-btn">
            <spring:message code="btn.affirm"/></button>
          <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                  code="btn.cancel"/></button>
        </div>
      </div>
    </div>
  </div>
  <!--end deleteAll box-->

  <div class="modal fade" id="modify-row-modal" tabindex="-1" role="dialog" aria-labelledby="my-modal-label" data-backdrop="static">
    <form id="update-form">
      <div class="modal-dialog f-w400" role="document">
        <div class="modal-content f-w400">
          <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            <h4 class="modal-title" id="my-modal-label"><spring:message code="lbl.std.source.detail"/></h4>
          </div>
          <div class="modal-body">
            <table class="table" id="update-table">
              <tr>
                <td><input id="id" type="hidden"/></td>
              </tr>
              <tr>
                <td><div class="f-tar"><spring:message code="lbl.code"/><spring:message code="spe.colon"/></div></td>
                <td><input id="code" class="required useTitle"  required-title="不能为空"/></td>
              </tr>
              <tr>
                <td><div class="f-tar"><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></div></td>
                <td><input id="name" class="required useTitle"  required-title="不能为空"/></td>
              </tr>
              <tr>
                <td><div class="f-tar"><spring:message code="lbl.source.type"/><spring:message code="spe.colon"/></div></td>
                <td><select id="type">
                  <option value="11111">类型1</option>
                  <option value="22222">类型2</option>
                  <option value="33333">类型3</option>
                </select></td>
              </tr>
              <tr>
                <td><div class="f-tar"><spring:message code="lbl.introduction"/><spring:message code="spe.colon"/></div></td>
                <td><textarea id="description" ></textarea><td>
              </tr>
            </table>
          </div>
          <!--footer start-->
          <div class="modal-footer" id="footer">
            <button type="button" class="btn btn-primary" id="update-btn">
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
