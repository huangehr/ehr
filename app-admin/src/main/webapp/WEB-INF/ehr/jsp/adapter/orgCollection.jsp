
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="title.dataSet" /></title>

    <%--导入共同的文件--%>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>

    <script>
        seajs.use(['jquery','app/adapter/orgCollection','app/adapter/orgDataSet','app/adapter/orgDict'],function($,app){
        })
    </script>

</head>
<body>


<!--head -->
<div class="body-head f-h50">
    <a href="${contextRoot}/adapterorg/initial" target="background" class="f-fwb back">返回采集标准</a>
    <input id="adapter_org" value='${adapterOrg}' hidden="none" />
    <input class="f-fwb f-ml20 f-mt10" id="adapter_org_name"/>
    <span class="f-ml20">父级标准：</span><input class="f-mt10" id="adapter_org_parent"/>
    <span class="f-ml20">标准说明：</span><input class="f-mt10" id="adapter_org_description"/>
</div>
<!--head -->

<!--condition -->
<div class="switch f-tac f-h50">
    <button id="switch_dataSet" class="btn btn-primary f-mt10"><spring:message code="lbl.dataset"/></button>
    <button id="switch_dict" class="btn f-mt10"><spring:message code="lbl.dict"/></button>
</div>
<!--condition -->

<!--######数据集部分######-->
<div>
    <!--######数据的显示部分######-->
    <div id="orgDataSetDiv">
        <!--######机构数据集显示部分######-->
        <div class="left-gmenu">

            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-fl f-ml20"><strong><spring:message code="lbl.dataset"/></strong></span>
                <div class="f-mt10 f-fl f-ml50">
                    <input type="text" id="dataSet-codeAndName" placeholder="<spring:message code="lbl.input.placehold"/>">
                    <div class="image-search f-pa" id="searchOrgDataSets"></div>
                </div>
                <div id="addOrgDataSet" data-toggle="modal" data-target="#createOrgDataSetModal" class="addImg f-fr f-mr10"></div>
            </div>
            <div id="orgDataSetGrid" data-pagerbar-items="2">
            </div>
        </div>
        <!--######机构数据集数据元显示部分######-->
        <div class="right-gmenu">
            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-ml20 f-fl"><strong><spring:message code="title.metaData"/></strong></span>
                <div class="f-mt10 f-fl f-ml20">
                    <input type="text" id="metaData-codeAndName" placeholder="<spring:message code="lbl.input.placehold"/>">
                    <div class="image-search f-pa" id="searchOrgMetaDatas"></div>
                </div>
                <button id="addOrgMetaData" data-toggle="modal" data-target="#createOrgMetaDataModal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10"><spring:message code="btn.create"/></button>
                <button id="deleteOrgMetaDatas" class="btn btn-primary J_delEntryListBtn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#deleteMetaDataModals"><spring:message code="btn.multi.delete"/></button>
            </div>
            <div id="orgMetaDataGrid">
            </div>
        </div>
    </div>

    <!--######新增机构数据集信息弹出框######-->
    <div class="modal fade" id="createOrgDataSetModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgDataSetAdd-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.add.dataSet"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDataSetCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDataSetName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="orgDataSetDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgDataSetAdd"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!--######修改机构数据集信息弹出框######-->
    <div class="modal fade" id="updataOrgDataSetModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgDataSetUpdate-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.updata.data"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDataSetCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDataSetName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="updata-orgDataSetDescription"   class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updataOrgDataSet"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
    </div>

    <!--######删除弹出框######-->
    <div class="modal fade msg-modal" id="deleteOrgDataSetModal" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50 affirmdelete" data-dismiss="modal" ><spring:message code="btn.confirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <!--######批量删除弹出框######-->
    <div class="modal fade msg-modal" id="deleteMetaDataModals" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <h5><strong><spring:message code="msg.000012"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="batchDeleteOrgMetaData"><spring:message code="btn.confirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <input type="hidden" id="dataSet_Types">

    <!--######新增机构数据元信息弹出框######-->
    <div class="modal fade" id="createOrgMetaDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgMetaDataAdd-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.add.metaData"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgMetaDataCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgMetaDataName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="orgMetaDataDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgMetaDataAdd"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!--######修改机构数据元信息弹出框######-->
    <div class="modal fade" id="updataOrgMetaDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content">
                <form id="orgMetaDataUpdate-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.updata.metaData"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgMetaDataCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgMetaDataName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="updata-orgMetaDataDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgMetaDataUpdate"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>

        </div>
    </div>
</div>

<!--######字典的显示部分######-->
<div id="orgDictDiv">
    <div>
        <!--######机构字典显示部分######-->
        <div class="left-gmenu">
            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-fl f-ml20"><strong><spring:message code="lbl.dict"/>　</strong></span>
                <div class="f-mt10 f-fl f-ml50">
                    <input type="text" id="dict-codeAndName" placeholder="<spring:message code="lbl.input.code.name"/>">
                    <div class="image-search f-pa" id="searchOrgDict"></div>
                </div>
                <div id="addOrgDict" data-toggle="modal" data-target="#createOrgDictModal" class="addImg f-fr f-mr10"></div>
            </div>
            <div id="orgDictGrid" data-pagerbar-items="2">
            </div>
        </div>
        <!--######机构字典数据元显示部分######-->
        <div class="right-gmenu" id="searchMetaDatas">
            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-ml20 f-fl"><strong><spring:message code="lbl.dict.meta"/></strong></span>
                <div class="f-mt10 f-fl f-ml20">
                    <input type="text" id="dictItem-codeAndName" placeholder="<spring:message code="lbl.input.code.name"/>">
                    <div class="image-search f-pa" id="searchOrgDictItem"></div>
                </div>
                <button id="addOrgDictItem" data-toggle="modal" data-target="#createOrgDictItemModal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10">新增</button>
                <button id="deleteOrgDictItems" class="btn btn-primary J_delEntryListBtn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#deleteOrgDictItemModals">批量删除</button>
            </div>
            <div id="orgDictItemGrid">
            </div>
        </div>
    </div>

    <!--######新增机构字典信息弹出框######-->
    <div class="modal fade" id="createOrgDictModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgDictAdd-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.dict.create"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDictCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDictName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="orgDictDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgDictAdd"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" id="chakan" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
        <input id="orgDictId" hidden="none"/>
    </div>

    <!--######修改机构字典信息弹出框######-->
    <div class="modal fade" id="updataOrgDictModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgDictUpdate-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.dict.update"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDictCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDictName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>

                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="updata-orgDictDescription"   class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updataOrgDict"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
    </div>

    <!--######删除弹出框######-->
    <div class="modal fade msg-modal" id="deleteOrgDictModal" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title" id="confirmModalLabel"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <input id="deleteId" type="hidden">
                    <h5><strong><spring:message code="msg.000003"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50 affirmdelete" data-dismiss="modal" ><spring:message code="btn.confirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <!--######批量删除弹出框######-->
    <div class="modal fade msg-modal" id="deleteOrgDictItemModals" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <h5><strong><spring:message code="msg.000012"/></strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="batchDeleteOrgDictItem"><spring:message code="btn.confirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <input type="hidden" id="dataTypes">

    <!--######新增机构数据元信息弹出框######-->
    <div class="modal fade" id="createOrgDictItemModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="orgDictItemAdd-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.dictItem.create"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDictItemCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDictItemName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.sort"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="orgDictItemSort" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="orgDictItemDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgDictItemAdd"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
        </div>
        <input id="orgDictItemId" hidden="none"/>
    </div>

    <!--######修改机构数据元信息弹出框######-->
    <div class="modal fade" id="updataOrgDictItemModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content">
                <form id="orgDictItemUpdate-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.dictItem.update"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDictItemCode" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/>/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDictItemName" class="required useTitle"  required-title=<spring:message code="lbl.must.input"/> />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.sort"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-orgDictItemSort" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="updata-orgDictItemDescription"  class="description"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="orgDictItemUpdate"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>

        </div>
    </div>
</div>

</body>
</html>