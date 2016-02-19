<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><spring:message code="title.dict.manage"/></title>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>

    <script>
        seajs.use(['jquery','app/std/dict/dict'],function($,dict){})
    </script>

</head>
<body>
<div>
    <div id="conditionArea" class="f-mt10 f-mr10" align="right">
        <table>
            <tr>
                <span><spring:message code="lbl.version.select"/></span>
                <select id="cdaVersion">
                </select>
            </tr>
        </table>
    </div>

    <div id="dataGrid" class="f-mt10">
        <div id="left-gmenu" class="left-gmenu">
            <div class="f-h50 condition">
                <th colspan="4" class="f-mt10">
                    <span class="f-mt15 f-fs14 f-fl f-ml10">
                        <strong><spring:message code="title.sysDict.manage"/></strong>
                    </span>
                    <div class="f-mt10 f-fl f-ml50">
                        <input type="text" id="searchNm" placeholder="<spring:message code="lbl.input.code.name"/>"/>
                        <div class="J_searchBtn image-search f-pa" ></div>
                    </div>
                    <div data-toggle="modal" data-target="#modifyDictRowModal" class="J_add-btn f-fr f-mr10 image-create" ></div>
                </th>
            </div>
            <div id="dictGrid">
                <!-- 系统字典列表区域 -->
            </div>
        </div>

        <div id="right-gmenu" class="right-gmenu f-ml10  f-fr ">
            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-fl f-ml10">
                    <strong><spring:message code="lbl.dict.detail"/></strong>
                </span>
                <div class="f-fl f-mt10 f-ml50">
                    <input type="text" id="searchNmEntry" placeholder="<spring:message code="lbl.input.code.name"/>"/>
                    <div class="J_searchBtn image-search f-pa" ></div>
                </div>

                <a data-toggle="modal" data-target="#modifyDictEntryRowModal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10">
                    <spring:message code="btn.dictEntry.create"/>
                </a>
                <button type="button" class="btn btn-primary J_delEntryListBtn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#myModal">
                    <spring:message code="btn.multi.delete"/>
                </button>
                <%--<button id="delDictEntryListBtn" type="button" class="btn btn-primary J_delEntryListBtn f-fr f-mr10 f-mt10"><spring:message code="btn.multi.delete"/></button>--%>
                <input id="dictIdForEntry" hidden/>
                <input id="hd_url" type="hidden" value="${contextRoot}"/>
            </div>
            <div id="dictEntryGrid">
            </div>
        </div>
    </div>

    <!--点击新增或修改Dict弹出框-->
    <div class="modal fade" id="modifyDictRowModal" tabindex="-2" role="dialog" aria-labelledby="myModalLabel">
        <form id="updateDictForm">
            <div class="modal-dialog f-w400" role="document">
                <div class="modal-content f-w400">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="dictModalLabel"><spring:message code="title.dict.detail"/></h4>
                    </div>
                    <div class="modal-body">
                        <table class="table" id="updateDictTable">
                            <input id="dictId" hidden="hidden" />
                            <tr>
                                <td><label><spring:message code="lbl.code"/></label></td>
                                <td><input id="dictCode"  class="required" placeholder="请输入编码" /></td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.designation"/></label></td>
                                <td><input id="dictName" placeholder="请输入名称"  /></td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.dict.base"/></label></td>
                                <td>
                                    <select id="baseDict" data-placeholder="请选择基础字典">
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.std.source"/></label></td>
                                <td>
                                    <select id="stdSource">
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.version.ref"/></label></td>
                                <td><input id="stdVersion" /></td>
                            </tr>

                            <tr>
                                <td><label><spring:message code="lbl.description"/><spring:message code="spe.colon"/></label></td>
                                <td><input id="description"/></td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updateDictBtn"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <!--点击新增或修改Dict Entry弹出框-->
    <div class="modal fade" id="modifyDictEntryRowModal" tabindex="-2" role="dialog" aria-labelledby="myModalLabel">
        <form id="updateDictEntryForm">
            <div class="modal-dialog f-w400" role="document">
                <div class="modal-content f-w400">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title" id="dictEntryModalLabel"><spring:message code="lbl.dict.detail"/></h4>
                    </div>
                    <div class="modal-body">
                        <table class="table" id="updateDictEntryTable">
                            <input id="dictEntryId" hidden="hidden" />
                            <tr>
                                <td><label><spring:message code="lbl.code"/></label></td>
                                <td><input id="dictEntryCode"  class="required" /></td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.value"/></label></td>
                                <td><input id="dictEntryName"  /></td>
                            </tr>
                            <tr>
                                <td><label><spring:message code="lbl.description"/><spring:message code="spe.colon"/></label></td>
                                <td><input id="entryDescription"/></td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updateDictEntryBtn"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
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
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="confirmModalLabel"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <input id="deleteDictEntryId" type="hidden">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
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

    <!--点击批量删除DictEntryList弹出框-->
    <div class="modal fade msg-modal" id="myModal" tabindex="0" role="dialog" aria-labelledby="confirmDictListModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="confirmDictListModalLabel"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <input id="deleteDictListId" type="hidden">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delDictEntryListBtn"><spring:message code="btn.affirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>