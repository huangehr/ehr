<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/19
  Time: 16:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######用户管理页面Title设置######-->
<div class="f-dn" data-head-title="true">CDA文件管理</div>
<div id="div_wrapper" style="height: 100%;">
    <div id="conditionArea" class="f-mb10" align="right" style="margin-right: 10px;">

                <%--<select id="cdaVersion" class="inputwidth f-w200">--%>
                <%--</select>--%>
                <input type="text" data-type="select" id="cdaVersion" data-attr-scan="version" style="height: 27px;">

    </div>
    <!--######CDA信息表######-->
    <div id="div_left" style=" width:320px;float: left;">
        <div id="div_left_tree" style=" width:318px;border: 1px solid #D6D6D6; overflow: auto;">
            <%--<div id="div_search" class="m-retrieve-area f-h50 f-dn f-pr"--%>
            <%--style="display:block; border: 1px solid #D6D6D6;border-bottom: 0px;">--%>
            <%--<ul>--%>
            <%--<li class=" f-mt15">--%>
            <%--<span class="f-mt20 f-fs14 f-ml10 f-f1">--%>
            <%--<strong style="font-weight: bolder;"><spring:message code="title.CDA.manage"/>:</strong>--%>
            <%--</span>--%>

            <%--<input type="text" id="searchNm" placeholder="<spring:message code="lbl.input.placehold"/>">--%>

            <%--<div title="新增" id="btn_create" class="image-create">--%>
            <%--</div>--%>
            <%--</li>--%>
            <%--</ul>--%>
            <%--</div>--%>
            <%--<div id="div_cda_grid">--%>

            <%--</div>--%>
            <div id="div_typeTree" style=" width:316px;">

            </div>
        </div>
    </div>
    <div id="div_right" style="float: left; margin-left: 10px;">
        <div class="m-retrieve-area f-h50 f-dn f-pr"
             style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px;">
            <ul>
                <li class=" f-mt15">
                    <span class="f-mt10 f-fs14 f-ml10">
                       <strong style="font-weight: bolder;">CDA:</strong>
                    </span>

                    <%--<input type="text" id="searchNmEntry" placeholder="<spring:message code="lbl.input.placehold"/>"--%>
                    <%--class="f-ml10">--%>
                    <input type="text" id="searchNm" placeholder="<spring:message code="lbl.input.CDAplacehold"/>">

                    <%--<button type="button" class="btn btn-primary " id="btn_test" >获取映射关系</button>--%>

                    <%--<a id="btn_Delete_relation" class="btn btn-primary J_add-btn f-fr f-mr10"--%>
                    <%--style="  margin-right: 20px;margin-top: -26px;">--%>
                    <%--批量删除--%>
                    <%--</a>--%>
                    <a id="btn_Delete" class="btn btn-primary J_add-btn f-fr f-mr10"
                       style="  margin-right: 20px;margin-top: -26px;">
                        批量删除
                    </a>
                    <a id="btn_create" class="btn btn-primary J_add-btn f-fr f-mr10"
                       style="  margin-right: 20px;margin-top: -26px;">
                        新增
                    </a>
                    <%--<a id="btn_Update_relation" class="btn btn-primary J_add-btn f-fr f-mr10"--%>
                       <%--style="  margin-right: 20px;margin-top: -26px;">--%>
                        <%--<spring:message code="btn.edit"/>--%>
                    <%--</a>--%>
                    <%--<a id="btn_test" class="btn btn-primary J_add-btn f-fr f-mr10"--%>
                    <%--style="  margin-right: 20px;margin-top: -26px;">--%>
                    <%--API网关访问--%>
                    <%--</a>--%>

                </li>
            </ul>
        </div>
        <%--<div id="div_relation_grid"></div>--%>
        <div id="div_cda_grid">

        </div>
    </div>
    <!--######用户信息表#结束######-->
</div>

<input type="hidden" id="hd_url" value="${contextRoot}"/>
<input type="hidden" id="hdId" value=""/>
<input type="hidden" id="hdType" value=""/>
<input type="hidden" id="hdTypeName" value=""/>
