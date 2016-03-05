<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<!--######系统字典管理页面Title设置######-->
<div id="inp_form">
    <input id="inp_systemDictId" type="hidden">
    <div>
        <input type="text" id="inp_search" placeholder="请输入字典名称"/>
    </div>

    <div style="width: 100%" id="grid_content" class="f-mt10">
        <div class="u-left">
            <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px">
                <div class="m-form-group f-mt10">
                    <div class="m-form-control f-mt5 f-fs14 f-fwb f-ml10">
                        <div>系统字典：</div>
                    </div>
                    <div id="div_addSystemDict" class="l-button u-btn u-btn-primary u-btn-small l-button-over f-fr f-mr10">
                        <span>新增字典</span>
                    </div>
                </div>
            </div>

            <div id="div_SystemDict_info_grid" class="m-systemDict-form-info"></div>
        </div>

        <div class="u-right">
            <div class="m-retrieve-area f-h50 f-dn f-pr m-form-inline" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0">
                <div class="m-form-group f-mt10">
                    <div class="m-form-control f-mt5 f-fs14 f-fwb f-ml10">
                        <div>字典详情：</div>
                    </div>
                    <div id="div_addSystemDictEntity" class="l-button u-btn u-btn-primary u-btn-small l-button-over f-fr f-mr10">
                        <span>新增明细</span>
                    </div>
                </div>
            </div>

            <div id="div_systemEntity_info_grid" class="m-systemDictEntity-form-info"></div>
        </div>
    </div>

    <div>
       <%-- 系统字典的新增和修改--%>
        <div id="div_updateSystemDictDialog" class="u-public-manage m-form-inline">
            <div class="m-form-group">
                <label>字典名称：</label>
                <div class="l-text-wrapper m-form-control essential">
                    <input type="text" id="inp_systemDictName" class="required useTitle ajax max-length-50" required-title=<spring:message code="lbl.must.input"/> />
                </div>
            </div>
            <%--预留reference字段--%>
            <div class="m-form-group">
                <label></label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" id="inp_systemDictReference" class="required useTitle" data-attr-scan=""/>
                </div>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_addSystemDict_btn">
                <span>确认</span>
            </div>
        </div>

        <%--系统字典详情的新增--%>
        <div id="div_add_systemDictEntityDialog" class="u-public-manage m-form-inline">
            <div class="m-form-group">
                <label>字典编码:</label>
                <div class="l-text-wrapper m-form-control essential">
                    <input type="text" id="inp_systemDictEntity_code" class="required useTitle max-length-30" required-title=<spring:message code="lbl.must.input"/>  />
                </div>
            </div>
            <div class="m-form-group">
                <label>字典值:</label>
                <div class="l-text-wrapper m-form-control essential">
                    <input type="text" id="inp_systemDictEntity_value" class="required useTitle max-length-100" required-title=<spring:message code="lbl.must.input"/> />
                </div>
            </div>
            <div class="m-form-group">
                <label>序号:</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" id="inp_systemDictEntity_sort" class="max-length-11" />
                </div>
            </div>
            <div class="m-form-group">
                <label>分类:</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" id="inp_systemDictEntity_catalog" class="max-length-32" />
                </div>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_addSystemDictEntity_btn">
                <span>确认</span>
            </div>
        </div>

        <%--系统字典详情的修改--%>
        <div id="div_update_systemDictEntityDialog" class="u-public-manage m-form-inline">
            <div class="m-form-group m-form-readonly">
                <label>字典编码:</label>
                <div class="l-text-wrapper m-form-control essential">
                    <input type="text" id="inp_update_systemDictEntity_code" class="required useTitle" data-attr-scan="code" required-title=<spring:message code="lbl.must.input"/> />
                </div>
            </div>
            <div class="m-form-group">
                <label>字典值:</label>
                <div class="l-text-wrapper m-form-control essential">
                    <input type="text" id="inp_update_systemDictEntity_value" class="required useTitle" data-attr-scan="value" required-title=<spring:message code="lbl.must.input"/>  />
                </div>
            </div>
            <div class="m-form-group">
                <label>序号:</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" id="inp_update_systemDictEntity_sort" data-attr-scan="sort"  />
                </div>
            </div>
            <div class="m-form-group">
                <label>分类:</label>
                <div class="l-text-wrapper m-form-control">
                    <input type="text" id="inp_update_systemDictEntity_catalog" data-attr-scan="catalog"  />
                </div>
            </div>
            <div class="l-button u-btn u-btn-primary u-btn-small f-ib f-vam f-t30" id="div_update_SystemDictEntity_btn">
                <span>确认</span>
            </div>
        </div>

    </div>
</div>
<input id="inp_systemNameCopy" type="hidden" value="">