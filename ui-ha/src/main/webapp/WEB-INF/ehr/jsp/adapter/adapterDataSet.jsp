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
    seajs.use(['jquery','app/adapter/adapterDataSet'],function($,app){
    })
  </script>

</head>
<body>

<!--head -->
<div class="body-head f-h50">
  <a href="${contextRoot}/adapter/initial" target="background" class="f-fwb back">返回适配方案</a>
  <input id="adapter_plan_id" value=${adapterPlanId} hidden="none" />
  <input class="f-fwb f-ml20 f-mt10" id="adapter_plan_name"/>
  <span class="f-ml20">初始方案：</span><input class="f-mt10" id="adapter_plan_parent"/>
  <span class="f-ml20">方案类别：</span><input class="f-mt10" id="adapter_plan_type"/>
  <span class="f-ml20">映射机构/区域：</span><input class="f-mt10" id="adapter_plan_org"/>
</div>
<!--head -->

<!--condition -->
<div class="switch f-tac f-h50">
  <button id="btn_dataset" class="btn btn-primary btn-dataset f-mt10"><spring:message code="lbl.dataset"/></button>
  <button id="btn_dict" class="btn btn-dict f-mt10"><spring:message code="lbl.dict"/></button>
  <!--######导入/导出######-->
  <div class="f-fr imp-exp">
    <button class="btn btn-primary J_exp-btn f-fr f-mr10 f-mt10">导出</button>
    <button class="btn btn-primary J_inp-btn f-fr f-mr10 f-mt10" >导入</button>
  </div>
</div>
<!--condition -->

<!--######数据(数据集)的显示部分######-->
<div id="adapter_data_set">
  <!--######数据集显示部分######-->
  <div id="data_set" class="left-gmenu">
    <div class="f-h50 condition">
      <span class="f-mt15 f-fs14 f-fl f-ml20 f-w100"><strong>平台数据集</strong></span>
      <div class="f-mt10 f-fl">
        <input type="text" id="input_data_set" placeholder="请输入代码或名称">
        <div class="image-search f-pa J_search-btn"></div>
      </div>
      <div data-toggle="modal" data-target="#data_set_modal" class="J_add-btn f-fr f-mr10 image-create" ></div>
    </div>
    <div id="data_set_grid" data-pagerbar-items="2">
    </div>
  </div>
  <!--######数据元显示部分######-->
  <div id="meta_data" class="right-gmenu f-mt50">
    <div class="f-h50 condition">
      <span class="f-mt15 f-fs14 f-ml20 f-fl"><strong>数据元映射</strong></span>
      <div class="f-mt10 f-fl f-ml20">
        <input type="text" id="input_meta_data" placeholder="请输入代码或名称">
        <div class="image-search f-pa J_search-btn"></div>
      </div>
      <button data-toggle="modal" data-target="#meta_data_modal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10">新增</button>
      <button class="btn btn-primary J_del-rows-btn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#delete_rows_modal">批量删除</button>
    </div>
    <div id="meta_data_grid">
    </div>
  </div>
</div>

<!--######字典的显示部分######-->
<div id="adapter_dict">
  <!--######平台字典显示部分######-->
  <div id="dict" class="left-gmenu">
    <div class="f-h50 condition">
      <span class="f-mt15 f-fs14 f-fl f-ml20 f-w100"><strong>平台字典</strong></span>
      <div class="f-mt10 f-fl">
        <input type="text" id="input_dict" placeholder="请输入代码或名称">
        <div class="image-search f-pa J_search-btn" ></div>
      </div>
      <div  data-toggle="modal" data-target="#dict_modal" class="J_add-btn image-create f-fr f-mr10"></div>
    </div>
    <div id="dict_grid" data-pagerbar-items="2">
    </div>
  </div>
  <!--######字典项显示部分######-->
  <div id="dict_entry" class="right-gmenu f-mt50" >
    <div class="f-h50 condition">
      <span class="f-mt15 f-fs14 f-ml20 f-fl"><strong>字典项映射</strong></span>
      <div class="f-mt10 f-fl f-ml20">
        <input type="text" id="input_dict_entry"  placeholder="请输入代码或名称">
        <div class="image-search f-pa J_search-btn" ></div>
      </div>
      <button  data-toggle="modal" data-target="#dict_entry_modal" class="btn btn-primary J_add-btn f-fr f-mr10 f-mt10">新增</button>
      <button  class="btn btn-primary J_del-rows-btn f-fr f-mr10 f-mt10" data-toggle="modal" data-target="#delete_rows_modal">批量删除</button>
    </div>
    <div id="dict_entry_grid">
    </div>
  </div>
</div>


<!--delete box start-->
<div class="modal fade msg-modal" id="delete_row_modal" tabindex="0" role="dialog" aria-labelledby="del_row_modal_label" data-backdrop="static">
  <div class="modal-dialog msg-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header modal-title">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="del_row_modal_label"><spring:message code="lbl.delete.message"/></h4>
      </div>
      <div class="modal-body f-tac msg-body">
        <input id="delete_type" type="hidden"/>
        <input id="delete_id" type="hidden"/>
        <h5><strong><spring:message code="msg.000003"/></strong></h5>
        <h5><spring:message code="msg.000004"/></h5>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete_btn">
          <spring:message code="btn.affirm"/></button>
        <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                code="btn.cancel"/></button>
      </div>
    </div>
  </div>
</div>
<!--end delete box-->

<!--deleteAll box start-->
<div class="modal fade msg-modal" id="delete_rows_modal" tabindex="0" role="dialog" aria-labelledby="del_rows_modal_label" data-backdrop="static">
  <div class="modal-dialog msg-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header modal-title">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="del_rows_modal_label"><spring:message code="lbl.delete.message"/></h4>
      </div>
      <div class="modal-body f-tac msg-body">
        <input id="delete_rows_type" type="hidden"/>
        <input id="delete_ids" type="hidden"/>
        <h5><strong>确认删除这几行信息？</strong></h5>
        <h5><spring:message code="msg.000004"/></h5>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary f-fl f-ml50" data-dismiss="modal" id="delete_rows_btn">
          <spring:message code="btn.affirm"/></button>
        <button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal"><spring:message
                code="btn.cancel"/></button>
      </div>
    </div>
  </div>
</div>
<!--end deleteAll box-->

<!--######新增、修改数据元信息弹出框######-->
<div class="modal fade" id="meta_data_modal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="meta_data_model_label">
  <div class="modal-dialog f-w400" role="document">
    <div class="modal-content">
      <form id="meta_data_form">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
          <h4 id="meta_data_model_label" class="modal-title">新增数据元适配</h4>
        </div>
        <div class="modal-body f-tac">
          <table class="table">
            <input id="adapter_data_set_id" name="id" hidden>
            <tr>
              <td>
                <label>标准数据元<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="std_meta_data" name="metaDataId"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label>机构数据集<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="org_data_set" name="orgDataSetSeq"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label>机构数据元<spring:message code="spe.colon"/></label>
              </td>
              <td>
                    <select id="org_meta_data" name="orgMetaDataSeq"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label>数据类型<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="data_type" name="dataTypeCode"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
              </td>
              <td>
                <textarea id="data_set_description" name="description" class="description"></textarea>
              </td>
            </tr>
          </table>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" id="meta_data_update"><spring:message code="btn.save"/></button>
          <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
        </div>
      </form>
    </div>
  </div>
</div>

<!--######新增、修改字典项信息弹出框######-->
<div class="modal fade" id="dict_entry_modal" tabindex="-1" role="dialog" data-backdrop="static" aria-labelledby="dict_entry_modal_label">
  <div class="modal-dialog f-w400" role="document">
    <div class="modal-content">
      <form id="dict_entry_form">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
          <h4 id="dict_entry_modal_label" class="modal-title">新增数据元适配</h4>
        </div>
        <div class="modal-body f-tac">
          <table class="table">
            <input id="adapter_dict_id" name="id" hidden>
            <tr>
              <td>
                <label>标准字典项<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="std_dict_entry" name="dictEntryId"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label>机构字典<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="org_dict" name="orgDictSeq"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label>机构字典项<spring:message code="spe.colon"/></label>
              </td>
              <td>
                <select id="org_dict_entry" name="orgDictEntrySeq"></select>
              </td>
            </tr>
            <tr>
              <td>
                <label><spring:message code="lbl.definition"/><spring:message code="spe.colon"/></label>
              </td>
              <td>
                <textarea id="dict_entry_description" name="description" class="description"></textarea>
              </td>
            </tr>
          </table>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" id="dict_entry_update"><spring:message code="btn.save"/></button>
          <button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="btn.cancel"/></button>
        </div>
      </form>
    </div>
  </div>
</div>

<%--——————--%>
</body>
</html>
