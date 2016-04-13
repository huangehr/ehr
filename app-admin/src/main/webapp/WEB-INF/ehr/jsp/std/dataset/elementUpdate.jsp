<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/11/26
  Time: 14:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<%--<div id="div_set_info_form" data-role-form class="m-form-inline f-mt20">--%>
<div class="modal-dialog metaData-width" id="div_element_info_form" data-role-form>
    <div class="modal-content">
        <div class="modal-body f-tac">
            <table class="table">
                <tr>
                    <th colspan="4" class="f-fwb split-line" style="font-size: 15px;"><spring:message
                            code="title.basic.attribute"/></th>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.inside.identifier"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="btn-isnull td-left">
                        <input id="metaDataInnerCode" class="required useTitle ajax max-length-128 validate-special-char" style="width: 227px;" required-title=
                        <spring:message code="lbl.must.input"/>
                                data-attr-scan="innerCode"/>
                    </td>
                    <td>
                        <label><spring:message code="lbl.metaData.name"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="btn-isnull td-left">
                        <input id="metaDataName" class="required useTitle max-length-255 validate-special-char" style="width: 227px;" required-title=
                        <spring:message code="lbl.must.input"/> data-attr-scan="name"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.metaData.encoding"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="btn-isnull td-left">
                        <input id="metaDataCode" class="required useTitle max-length-128 validate-special-char" style="width: 227px;" required-title=
                        <spring:message code="lbl.must.input"/> data-attr-scan="code"/>
                    </td>

                    <td>
                        <label><spring:message code="lbl.data.type"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="td-left">
                        <input id="metaDataType" class="max-length-10 validate-special-char" style="width: 227px;" data-attr-scan="type"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.verify.dict"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="td-left">
                        <%--<input type="text" id="criterionDict" class="validate-special-char" style="width: 227px;" placeholder="请选择检验字典"/>--%>
                        <%--<input id="metaDataDict"/>--%>
                        <div class="l-text-wrapper m-form-control ">
                            <input type="text" id="criterionDict" data-type="select" class="">
                        </div>
                        <%--<select id="criterionDict" data-type="select" data-placeholder="请选择检验字典" data-attr-scan="dictId"></select>--%>
                    </td>
                    <td>
                        <label><spring:message code="lbl.data.format"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="td-left">
                        <input id="metaDataFormat" class="max-length-50 validate-special-char" style="width: 227px;"
                               data-attr-scan="format"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td colspan="3" class="td-left">
                        <textarea id="metaDataDefinition" class="metaDataExplain max-length-255 validate-special-char" style=" width: 563px;"
                                  data-attr-scan="definition"></textarea>
                    </td>
                </tr>
                <tr>
                    <th colspan="4" class="f-fwb" style="font-size: 15px;"><spring:message
                            code="title.database.attribute"/></th>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.column.name"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="btn-isnull td-left">
                        <input id="fieldName" class="required useTitle ajax max-length-64 validate-special-char" required-title=
                        <spring:message code="lbl.must.input"/> data-attr-scan="columnName" style="width: 227px;"/>
                    </td>
                    <td>
                        <label><spring:message code="lbl.column.length"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="td-left">
                        <input id="fieldLength" class="max-length-15 validate-positive-integer useTitle" data-attr-scan="columnLength" style="width: 227px;"  validate-positive-integer-title="请输入正整数" />
                    </td>
                </tr>
                <tr>
                    <td>
                        <label><spring:message code="lbl.column.type"/><spring:message code="spe.colon"/></label>
                    </td>
                    <td class="td-left">
     <%--                   <div class="m-form-group">
                            <label><spring:message code="lbl.column.type"/>：</label>
                            <div class="m-form-control">
                                <input type="text" id="ipt_select" data-type="select" data-attr-scan="select" class="required useTitle" required-title="不能为空">
                            </div>
                        </div>--%>
                        <select id="datatype"  data-attr-scan="code" style="width: 227px;">
                            <option value="VARCHAR">VARCHAR</option>
                            <option value="INT">INT</option>
                            <option value="FLOAT">FLOAT</option>
                            <option value="DOUBLE">DOUBLE</option>
                            <option value="CHAR">CHAR</option>
                            <option value="TEXT">TEXT</option>
                            <option value="DATE">DATE</option>
                        </select>
                    </td>
                    <th colspan="2" class="split-line">
                        <div class="f-fs12 f-ml5">
                            <div>
                                <label class="checkbox checkbox-title" style="width: 60px;"><spring:message
                                        code="lbl.whether.key"/><spring:message code="spe.colon"/>
                                </label>
                                <input type="checkbox" id="primaryKey" name="primaryKeyName"
                                       style="position: absolute;margin-top: -30px; margin-left: 70px;"/>
                            </div>
                        </div>
                        <div class="f-fs12 chexkbox-null">
                            <div>
                                <label class="checkbox checkbox-title" style="width: 60px;"><spring:message
                                        code="lbl.whether.null"/><spring:message code="spe.colon"/>
                                </label>
                                <input type="checkbox" id="whetherNull" name="whetherNullName"
                                       style="position: absolute;margin-top: -30px;margin-left: 70px;"/>
                            </div>
                        </div>
                    </th>
                </tr>
            </table>
        </div>
    </div>
    <div class="m-form-control pane-attribute-toolbar">
        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" id="btn_save">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="btn_close">
            <span>关闭</span>
        </div>
    </div>
</div>

<input type="hidden" id="hdId" value=""/>
<input type="hidden" id="hdsetid" value=""/>
<input type="hidden" id="hdversion" value=""/>
<input type="hidden" id="hd_url" value="${contextRoot}"/>
<input type="hidden" id="metaDataCodeCopy"/>
<input type="hidden" id="fieldNameCopy"/>

<%--</div>--%>
