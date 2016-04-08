<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/12/11
  Time: 14:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<div class="f-dn" data-head-title="true">CDA类别</div>
<div id="div_wrapper">
    <div class="m-retrieve-area f-h50 f-dn f-pr"
         style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px;">
        <ul>
            <li class="f-mt15 li_seach">
                <div style="float: left; width: 250px; margin-left: 12px; margin-top: -5px;">
                    <input type="text" id="inp_search" name="inp_search" placeholder="<spring:message code="lbl.input.placehold"/>"
                           class="f-ml10" >
                </div>
                <div style="float: right; width: 250px; margin-top: -5px;">
                    <%--<a id="btn_Delete_relation" class="btn btn-primary J_add-btn f-fr f-mr10">--%>
                        <%--批量删除--%>
                    <%--</a>--%>
                    <a id="btn_Update_relation" class="btn btn-primary J_add-btn f-fr f-mr10">
                        新增
                    </a>
                </div>
            </li>
        </ul>
    </div>
    <div id="div_cda_type_grid">

    </div>
    <input type="hidden" id="hd_url" value="${contextRoot}"/>
</div>