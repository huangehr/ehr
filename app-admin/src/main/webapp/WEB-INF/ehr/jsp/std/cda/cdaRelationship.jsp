<%--
  Created by IntelliJ IDEA.
  User: AndyCai
  Date: 2015/12/21
  Time: 10:27
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<div class="frame-list-layout">
    <div id="div_left" style="float: left;width: 445px;">
        <div class="list-section">
            <div class="list-section-head"><h4>选择列表</h4></div>
            <div class="pane-search" id="pane-search">
                <label for="txb-key">检索关键字：</label>
                <input type="text" id="txb-key">
                <%--     <label id="txb-catalog" style="visibility:hidden">类别:</label>
                     <select id="select-catalog" style="display:none"></select>--%>
            </div>
            <div id="pane-list"></div>
        </div>
        <div class="list-section">
            <div class="list-section-head"><h4>已选择项</h4></div>
            <ul id="pane-list-selected"></ul>
        </div>
    </div>
    <div id="div_right" style="float: left;width: 474px; border-left: 1px solid #ccc;">
        <div class="list-section">
            <div class="list-section-head"><h4>XML文档</h4></div>
            <div id="div_edit">
                <textarea name="txb_Immed_Temp" id="txb_Immed_Temp" style="height:475px;width:462px;margin-left: 10px;margin-top: 5px;">
                      &lt;root&gt;
                      &lt;/root&gt;

                </textarea>

            </div>
        </div>



    </div>
    <div class="pane-list-toolbar div_toolbar">

        <div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam" name="btn-submit" id="btn_save" style="display:none;">
            <span>保存</span>
        </div>
        <div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" name="btn_close">
            <span>关闭</span>
        </div>
    </div>
    <input type="hidden" id="hdId" value=""/>
    <input type="hidden" id="hdversion" value=""/>
    <input type="hidden" id="hd_url" value="${contextRoot}"/>

</div>
