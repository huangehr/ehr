<%--
  Created by IntelliJ IDEA.
  User: wq
  Date: 2015/9/15
  Time: 14:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title><spring:message code="title.dataSet" /></title>

    <%--导入共同的文件--%>
  <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>
  <script>
      seajs.use(['jquery','app/std/dataset/dataSet'],function($,app){

      })
  </script>

</head>
<body>
    <!--######版本选择######-->
    <div class="f-fr version f-mr20">
        <span class="f-fwb"><spring:message code="lbl.version.select"/></span>
            <select id="optionVersion"></select>
    </div>

    <!--######数据的显示部分######-->
    <div class="f-mt50" style="margin-bottom: 0;">
        <!--######数据集显示部分######-->
        <div class="left-gmenu">

            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-fl f-ml20"><strong>数据集</strong></span>
                <div class="f-mt10 f-fl f-ml50">
                    <input type="text" id="input-data" placeholder="请输入代码或名称">
                    <div class="image-search f-pa" id="searchDatasetCode"></div>
                </div>
                <div id="addDataSet" data-toggle="modal" data-target="#createDataModal" class="addDataSetImg f-fr f-mr10"></div>
            </div>
            <div id="dataSetGrid" data-pagerbar-items="2">
            </div>
        </div>
                <!--######数据源显示部分######-->
        <div class="right-gmenu" id="searchMetaDatas">
            <div class="f-h50 condition">
                <span class="f-mt15 f-fs14 f-ml20 f-fl"><strong>数据元</strong></span>
                <div class="f-mt10 f-fl f-ml20">
                    <input type="text" id="input-metaData" placeholder="请输入代码或名称">
                    <div class="image-search f-pa" id="searchMetaData"></div>
                </div>
                <button id="addMetaData" data-toggle="modal" data-target="#createMetaDataModal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10">新增</button>
                <button id="deleteMetaDatas" class="btn btn-primary J_delEntryListBtn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#deleteDataSetModals">批量删除</button>
            </div>
            <div id="metaDataGrid">
            </div>
        </div>
    </div>

    <!--######新增数据集信息弹出框######-->
    <div class="modal fade" id="createDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="add-form">
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
                                    <input id="id" class="required useTitle"  required-title="不能为空"/>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="dataSetName" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr class="f-dn">
                                <td>
                                    <label><spring:message code="lbl.source.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <select id="dataSetType">

                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.std.source"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <select id="dataSetStandardSource" class="required useTitle"  required-title="不能为空">

                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="dataSetexplain"  class="dataExplain"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="dataSetAdd"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" id="chakan" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
        <input id="datasetIdc" hidden="none"/>
    </div>

    <!--######修改数据集信息弹出框######-->
    <div class="modal fade" id="updataDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog f-w400" role="document">
            <div class="modal-content f-w400">
                <form id="update-form">
                    <div class="modal-header">
                        <input type="hidden" id="dataSet-Updata" />
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
                                    <input id="updata-id" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updata-dataSetName" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr class="f-dn">
                                <td>
                                    <label><spring:message code="lbl.source.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <select id="updata-dataSetType">
                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.std.source"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <select id="updata-dataSetStandardSource" class="required useTitle"  required-title="不能为空">

                                    </select>
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <textarea id="updata-dataSetexplain"   class="dataExplain"></textarea>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updataDataSet"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
    </div>

    <!--######删除弹出框######-->
    <div class="modal fade msg-modal" id="deleteDataSetModal" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <input id="delete-datasetId" type="hidden" />
                    <input id="delete-metadataId" type="hidden" />
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
    <div class="modal fade msg-modal" id="deleteDataSetModals" tabindex="0" role="dialog" data-backdrop="static" aria-labelledby="confirmModalLabel">
        <div class="modal-dialog msg-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header modal-title">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title"><spring:message code="title.info.delete"/></h4>
                </div>
                <div class="modal-body f-tac msg-body">
                    <h5><strong>确认删除这几行信息？</strong></h5>
                    <h5><spring:message code="msg.000004"/></h5>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="batchDeleteMetaData"><spring:message code="btn.confirm"/></button>
                    <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                </div>
            </div>
        </div>
    </div>

    <input type="hidden" id="dataTypes">

    <!--######新增数据元信息弹出框######-->
    <div class="modal fade" id="createMetaDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog metaData-width" role="document">
            <div class="modal-content">
                <form id="metaDataAdd-form">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.add.metaData"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr><th colspan="4" class="f-fs12 f-fwb split-line"><spring:message code="title.basic.attribute"/></th></tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="metaDataCode" class="required useTitle"  required-title="不能为空" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="metaDataName" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.inside.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="metaDataInnerCode" class="required useTitle"  required-title="不能为空" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.metaData.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="metaDataType" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.verify.dict"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <%--<input  id="metaDataDict"/>--%>
                                        <select id="criterionDict" data-placeholder="请选择检验字典"></select>
                                </td>
                                <td>
                                    <label><spring:message code="lbl.format"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input  id="metaDataFormat" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td  colspan="3">
                                    <textarea id="metaDataDefinition" class="metaDataExplain"></textarea>
                                </td>
                            </tr>
                            <tr>
                                <th colspan="4" class="f-fs12 f-fwb"><spring:message code="title.database.attribute"/></th>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.field.name"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="fieldName" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.field.length"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="fieldLength" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.data.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                   <%-- <input id="datatype" />--%>
                                    <select id="datatype">
                                        <option value="INT">INT</option>
                                        <option value="FLOAT">FLOAT </option>
                                        <option value="DOUBLE">DOUBLE </option>
                                        <option value="CHAR">CHAR </option>
                                        <option value="VARCHAR">VARCHAR </option>
                                        <option value="TEXT">TEXT</option>
                                        <option value="DATE">DATE </option>
                                    </select>
                                </td>
                                <th colspan="2" class="split-line">
                                    <div class="f-fs12 f-ml5">
                                        <label class="checkbox checkbox-title"><spring:message code="lbl.whether.key"/><spring:message code="spe.colon"/>
                                            <input type="checkbox" id="primaryKey" name="primaryKeyName"/>
                                        </label>
                                    </div>
                                    <div class="f-fs12 chexkbox-null">
                                        <label class="checkbox checkbox-title"><spring:message code="lbl.whether.null"/><spring:message code="spe.colon"/>
                                            <input type="checkbox" id="whetherNull" name="whetherNullName"/>
                                        </label>
                                    </div>
                                </th>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="metaDataAdd" class="metaDataUpdata"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>
            <div class="modal fade" id="metaDatachakan" tabindex="-1" role="dialog" aria-labelledby="readModalLabel">
            </div>
        </div>
    </div>

    <!--######修改数据元信息弹出框######-->
    <div class="modal fade" id="updataMetaDataModal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="readModalLabel">
        <div class="modal-dialog metaData-width" role="document">
            <div class="modal-content">
                <form id="metaDataUpdata-form">
                    <div class="modal-header">
                        <input id="updata-metadataId"type="hidden" />
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                            <span aria-hidden="true">&times;</span>
                        </button>
                        <h4 class="modal-title"><spring:message code="title.updata.metaData"/></h4>
                    </div>
                    <div class="modal-body f-tac">
                        <table class="table">
                            <tr><th colspan="4" class="f-fs12 f-fwb split-line"><spring:message code="title.basic.attribute"/></th></tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateMetaDataCode" class="required useTitle"  required-title="不能为空" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.designation"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateMetaDataName" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.inside.code"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateMetaDataInnerCode" class="required useTitle"  required-title="不能为空" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.metaData.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateMetaDataType" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.verify.dict"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <%--<input  id="updateMetaDataDict"/>--%>
                                    <select id="updatecriterionDict" data-placeholder="请选择检验字典"></select>
                                </td>
                                <td>
                                    <label><spring:message code="lbl.format"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input  id="updateMetaDataFormat" class="required useTitle"  required-title="不能为空" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td  colspan="3">
                                    <textarea id="updateMetaDataDefinition" class="metaDataExplain" ></textarea>
                                </td>
                            </tr>
                            <tr>
                                <th colspan="4" class="f-fs12 f-fwb"><spring:message code="title.database.attribute"/></th>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.field.name"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateFieldName" />
                                </td>
                                <td>
                                    <label><spring:message code="lbl.field.length"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <input id="updateFieldLength" />
                                </td>
                            </tr>
                            <tr>
                                <td>
                                    <label><spring:message code="lbl.data.type"/><spring:message code="spe.colon"/></label>
                                </td>
                                <td>
                                    <%--<input id="updateDatatype" />--%>
                                    <select id="updateDatatype">
                                        <option value="INT">INT</option>
                                        <option value="FLOAT">FLOAT </option>
                                        <option value="DOUBLE">DOUBLE </option>
                                        <option value="CHAR">CHAR </option>
                                        <option value="VARCHAR">VARCHAR </option>
                                        <option value="TEXT">TEXT</option>
                                        <option value="DATE">DATE </option>
                                    </select>
                                </td>
                                <th colspan="2" class="split-line">
                                    <div class="f-fs12 f-ml5">
                                        <label class="checkbox checkbox-title"><spring:message code="lbl.whether.key"/><spring:message code="spe.colon"/>
                                            <input type="checkbox" id="updatePrimaryKey" name="updatePrimaryKeyup"/>
                                        </label>
                                    </div>
                                    <div class="f-fs12 chexkbox-null">
                                        <label class="checkbox checkbox-title"><spring:message code="lbl.whether.null"/><spring:message code="spe.colon"/>
                                            <input type="checkbox" id="updateWhetherNull" name="updateWhetherNullup"/>
                                        </label>
                                    </div>
                                </th>
                            </tr>
                        </table>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-primary" id="updateMetaData"><spring:message code="btn.save"/></button>
                        <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
                    </div>
                </form>
            </div>

        </div>
    </div>

    <%--——————--%>
</body>
</html>