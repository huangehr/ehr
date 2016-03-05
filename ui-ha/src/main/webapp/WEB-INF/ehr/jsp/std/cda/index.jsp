<%--
  Created by IntelliJ IDEA.
  User: cms
  Date: 2015/9/15
  Time: 14:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title></title>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>

    <style>
        .left-gmenu {
            width:400px;
            border: 1px solid #d3d3d3;
            border-left:0 ;
            margin-left: 0;
        }
        .left-gmenu .u-grid {
            width: 400px;
            height: auto;
            border: 0;
        }
        .left-gmenu .u-grid .grid-body tr:not(.jqgfirstrow) { height: 60px; font-size: 12px; text-align: center; }
        .right-gmenu{
            width: 1234px;
            position: absolute;
            margin-top: -743px;
            border: 1px solid #d3d3d3;
            border-right:0 ;
            margin-left: 420px;
        }
        .right-gmenu .u-grid{
            width: 1234px;
            height: auto;
            border: 0;
        }
        .right-gmenu .u-grid .grid-body tr:not(.jqgfirstrow) { height: 60px; font-size: 12px; text-align: center; }
        input {
            font-family: SimSun;
            font-size: 14px;
            height: 30px;
            width: 240px;
        }
        .msg-modal {
            border-radius: 2px;
        }

        .msg-dialog {
            width: 322px;
        }

        .msg-body {
            height: 116px;
        }
        .modal-header {
            background-color: #2d9bd2;
            height: 40px;
        }

        .seach_img{
            margin-left: -26px;
            margin-top: -3px;
        }
        .search_btn{
            background-image:url(${staticRoot}/images/Search_btn.png) ;
        }

        #pane-list-selected {
            position: relative;
            display: block;
            overflow: hidden;
            overflow-y: auto;
            width: auto;
            height: 110px;
            margin: 0 0px 4px;
            border: 1px solid #ccc;
            border-top-width: 0;
        }

        #pane-list-selected .li_item {
            display: inline-block;
            position: relative;
            width: auto;
            height: 25px;
            margin: 5px 1px 0 5px;
            padding: 3px 7px 2px;
            background-color: #016cba;
            line-height: 18px;
            white-space: nowrap;
            color: #fff;
        }

        #pane-list-selected .li_item .li_close {
            display: none;
            position: absolute;
            top: 0;
            right: 0;
            width: 23px;
            height: 23px;
            cursor: pointer;
            background-color: #005d9d;
            border-left: 1px solid #005d9d;
            line-height: 23px;
            text-align: center;
            text-decoration: none;
            color: #fff;
        }

        .list-section-head {
            height: 29px;
            padding-right: 18px;
            border-bottom: 1px solid #ccc;
        }

        .image-create{
            margin-left:340px;
            margin-top: -25px;
            width: 22px;
            height: 22px;
            background: url(${staticRoot}/images/add_btn.png);
        }

        .image-create:hover{
            margin-left:340px;
            margin-top: -25px;
            width: 22px;
            height: 22px;
            background: url(${staticRoot}/images/add_btn_pre.png);
        }
        .image-modify{
            width: 22px;
            height: 22px;
            background: url(${staticRoot}/images/Modify_btn_pre.png);
        }
        .image-delete{
            width: 22px;
            height: 22px;
            margin-top: -22px;
            background: url(${staticRoot}/images/Delete_btn_pre.png);
        }

        .image-search{
            position: absolute;
            width: 22px;
            height: 22px;
            margin-top: 3px;
            background: url(${staticRoot}/images/Search_btn.png);
        }

        .image-search:hover{
            position: absolute;
            width: 22px;
            height: 22px;
            margin-top: 3px;
            background: url(${staticRoot}/images/Search_btn_pre.png);
        }

        .font_right{
            text-align: right;
        }

    </style>

    <script>
        seajs.use(['app/std/cda/cda'], function (e) {
            e.init();
        })
    </script>
</head>
<body>
<input type="hidden" id="hd_url" value="${contextRoot}">
<div>
    <%--选择版本--%>
    <div id="conditionArea" class="f-mt10" align="right" style="margin-right: 10px;">
        <table>
            <tr>
                <select id="cdaVersion" class="inputwidth f-w200">
                </select>

            </tr>
        </table>
    </div>

    <div id="dataGrid" class="f-mt20">
        <%--左边列表--%>
        <div id="left-gmenu" class="left-gmenu">
            <%--查询条件--%>

            <ul>
                <li class=" f-mt15">
                    <span class="f-mt20 f-fs14 f-ml10 f-f1">
                        <strong><spring:message code="title.CDA.manage"/></strong>
                    </span>

                    <input type="text" style="width: 220px; margin-left: 50px;" id="searchNm" placeholder="<spring:message code="lbl.input.placehold"/>">
                    <dic class="seach_img image-search" id="div_search">
                    </dic>

                    <div title="<spring:message code="btn.create"/>" id="btn_create" data-toggle="modal" data-target="#modifyDictRowModal" class="J_add-btn f-fr f-mr15 image-create" >
                    </div>
                </li>
            </ul>

            <!-- CDA列表区域 -->
            <div id="dictGrid">

            </div>
        </div>
        <%--右边列表--%>
        <div id="right-gmenu" class="right-gmenu f-ml10  f-fr">
            <%--查询条件--%>
            <ul>
                <li class=" f-mt15">
                      <span class="f-mt10 f-fs14 f-ml10">
                         <strong>关联数据集</strong>
                      </span>

                    <input type="text" id="searchNmEntry" placeholder="<spring:message code="lbl.input.placehold"/>" class="f-ml10">

                    <dic class="seach_img image-search" id="div_search_entry">
                    </dic>
                    <%--<button type="button" class="btn btn-primary " id="btn_test" >标准下发</button>--%>
                    <a id="btn_Update_relation" data-toggle="modal" data-target="#modifyDictEntryRowModal" class="btn btn-primary J_add-btn f-fr f-mr10" style="  margin-right: 20px;">
                        <spring:message code="btn.edit"/>
                    </a>

                    <input id="dictIdForEntry" hidden="">
                </li>
            </ul>

            <%--数据集列表区--%>
            <div id="dictEntryGrid">
            </div>
        </div>
    </div>

    <!--点击新增或修改cda弹出框-->
    <div class="modal fade" id="modifyDictRowModal" tabindex="-2" role="dialog" aria-labelledby="myModalLabel">
        <form id="updateDictForm">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="margin-top: -13px;"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="dictModalLabel" style="margin-top: -5px;"><spring:message code="btn.edit"/></h4>
                    </div>
                    <div class="modal-body">
                        <table class="table" id="updateDictTable">
                            <tr>
                                <input type="hidden" id="hdId"/>
                                <td class="font_right"><label><spring:message code="lbl.code"/>:</label></td>
                                <td><input id="txt_Code"  class="required" /></td>
                            </tr>
                            <tr>
                                <td class="font_right"><label><spring:message code="lbl.designation"/>:</label></td>
                                <td><input id="txt_Name" /></td>
                            </tr>
                            <tr>
                                <td class="font_right"><label><spring:message code="lbl.input.schemapath"/>:</label></td>

                                <td><input id="txt_schema_path"  /></td>

                            </tr>
                            <tr>
                                <td class="font_right"><label><spring:message code="lbl.std.source"/></label>:</td>
                                <td>
                                    <select id="sel_stdSource" style="width: 240px;" >
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td class="font_right"><label><spring:message code="lbl.description"/></label>:</td>
                                <td><input id="txt_description"/></td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary " id="btn_create_file" style="display:none;">创建文档</button>
                        <button type="button" class="btn btn-primary " id="updateCDABtn"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default " data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <!--点击新增或修改Dict Entry弹出框-->
    <div class="modal fade" id="modifyDictEntryRowModal" tabindex="-2" role="dialog" aria-labelledby="myModalLabel">
        <form id="updateDictEntryForm">
            <div class="modal-dialog" role="document">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="margin-top: -13px;"><span aria-hidden="true">&times;</span></button>

                        <h4 class="modal-title" id="dictEntryModalLabel" style="margin-top: -5px;"><spring:message code="lbl.dataset"/></h4>
                    </div>
                    <div class="modal-body">
                        <table class="table" id="updateDictEntryTable">
                            <input id="lbl_relation_ids" hidden="hidden" />
                            <tr>
                                <td style=" border-top: 0px solid #ddd;text-align:center;">
                                    <input id="txt_search" style="width: 360px;" placeholder="<spring:message code="lbl.input.placehold"/>"/>
                                    <a href="javascript:void(0);" class="seach_img">
                                        <img  src="${staticRoot}/images/Search_btn.png">
                                    </a>
                                </td>
                            </tr>
                            <tr style="width: 560px;">
                                <td style="width: 560px;">
                                    <div id="div_dataset" style="width: 560px;"></div>
                                </td>
                            </tr>
                            <tr style="width: 560px;">
                                <td style="width: 560px; border-top: 0px solid #ddd;">

                                    <div class="list-section-head"><h4><spring:message code="lbl.selected_Item"/></h4></div>
                                    <ul id="pane-list-selected" class="changed">
                                        <li title="卫生综合决策云服务" class="li_item">
                                            <a class="li_close">X</a>卫生综合决策云服务
                                        </li>
                                    </ul>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary f-fl f-ml100" id="updateDictEntryBtn"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default f-fr f-mr100" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <!--点击删除Dict Entry弹出框-->
    <div class="modal fade msg-modal" id="deleteRowModal" tabindex="0" role="dialog" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close" style="margin-top: -13px;"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="confirmModalLabel" style="margin-top: -5px;"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <input id="deleteDictEntryId" type="hidden">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <input type="hidden" id="hdrelationId"/>
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="deleteDictEntryBtn"><spring:message code="btn.affirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <!--点击删除Dict弹出框-->
    <div class="modal fade msg-modal" id="deleteDictRowModal" tabindex="0" role="dialog" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="confirmDictModalLabel"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <input id="deleteDictId" type="hidden">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="deleteDictBtn"><spring:message code="btn.affirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>
