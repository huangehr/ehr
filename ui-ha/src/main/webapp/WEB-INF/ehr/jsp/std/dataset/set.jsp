<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/25
  Time: 10:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######用户管理页面Title设置######-->
<div class="f-dn" data-head-title="true">数据集管理</div>
<div id="div_wrapper">
    <div id="conditionArea" class="f-mb10 f-mr10" align="right">
        <input type="text" data-type="select" id="cdaVersion" data-attr-scan="version">
    </div>
    <div style="width: 100%" id="grid_content">
        <!--######CDA信息表######-->
        <div id="div_left" style=" width:400px;float: left;">
            <div class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px;">
                <ul>
                    <li class=" f-mt15">
                        <span class="f-mt10 f-fs14 f-ml10" style="width: 70px;">
                            <strong style="font-weight: bolder;width: 70px;">数据集:</strong>
                        </span>

                        <input type="text" id="searchNm" placeholder="<spring:message code="lbl.input.placehold"/>">

                        <div title="新增" id="btn_create" class="image-create">
                        </div>
                    </li>
                </ul>
            </div>
            <div id="div_set_grid">
            </div>
        </div>
        <div id="div_right" style="float: left;">
            <div class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px;">
                <ul>
                    <li class=" f-mt15">
                        <span class="f-mt10 f-fs14 f-ml10">
                           <strong style="font-weight: bolder;">数据元:</strong>
                        </span>

                        <input type="text" id="searchNmEntry" placeholder="<spring:message code="lbl.input.placehold"/>"
                               class="f-ml10">

                        <a id="btn_Delete_relation" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr20"
                           style="  margin-right: 20px;margin-top: -26px;">
                            批量删除
                        </a>
                        <a id="btn_add_element" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr10"
                           style="  margin-right: 20px;margin-top: -26px;">
                            新增
                        </a>
                    </li>
                </ul>
            </div>
            <div id="div_element_grid"></div>
        </div>
        <!--######用户信息表#结束######-->
    </div>
</div>

<input type="hidden" id="hd_url" value="${contextRoot}"/>
<input type="hidden" id="hdId" value=""/>
