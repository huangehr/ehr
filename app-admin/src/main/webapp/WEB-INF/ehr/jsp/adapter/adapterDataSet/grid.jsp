<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!--######用户管理页面Title设置######-->
<div class="f-dn" data-head-title="true">标准适配</div>
<div id="div_wrapper" >
  <div id="conditionArea" class="f-mb10 f-mr10" align="right">
    <div class="body-head f-h50" align="left">
      <a href="javascript:$('#contentPage').empty();$('#contentPage').load('${contextRoot}/adapter/initial');"  class="f-fwb">返回上一层 </a>
      <input id="adapter_plan_id" value='${adapterPlanId}' hidden="none" />
      <span class="f-ml20">方案名称：</span><input class="f-fwb f-mt10" readonly id="adapter_plan_name"/>
      <span class="f-ml20">方案类别：</span><input class="f-mt10" readonly id="adapter_plan_type"/>
      <span class="f-ml20">采集机构：</span><input class="f-mt10" readonly id="adapter_plan_org"/>
      <span class="f-ml20">继承方案：</span><input class="f-mt10" readonly id="adapter_plan_parent"/>
    </div>
    <div class="switch f-tac f-h50">
      <button id="switch_dataSet" class="btn btn-primary f-mt10"><spring:message code="lbl.dataset"/></button>
      <button id="switch_dict" class="btn f-mt10"><spring:message code="lbl.dict"/></button>
      <div class="f-fr imp-exp" style="display: none">
        <button class="btn btn-primary J_exp-btn f-fr f-mr10 f-mt10">导出</button>
        <button class="btn btn-primary J_inp-btn f-fr f-mr10 f-mt10" >导入</button>
      </div>
    </div>
  </div>

<div id="grid_content" style="width: 100%">

  <div id="div_left" class="f-fl f-w400">
    <div id="retrieve" class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0">
      <ul>
        <li class=" f-mt15">
          <div class="f-mt10 f-fs14 f-ml10 f-f1" style="float: left">
              <span id="left_title" style="font-weight: bolder;"><spring:message code="lbl.dataset"/></span>
          </div>
          <div class="f-fl f-ml10">
            <input type="text" id="searchNm"  class="f-fs12" placeholder="<spring:message code="lbl.input.placehold"/>">
          </div>
          <div style="margin-left: 340px;padding-top: 4px;display: none">
          <div title="新增" id="btn_create" class="image-create"  onclick="javascript:$.publish('grid:left:open',['','new'])"></div>
          </div>
        </li>
      </ul>
    </div>
    <div id="div_left_grid" >

    </div>
  </div>


  <div id="div_right" style="float: left;width: 700px;margin-left: 10px">
    <div id="entryRetrieve" class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px">
    <ul>
      <li class=" f-mt15">
        <div class="f-mt10 f-fs14 f-ml10 f-f1" style="float: left">
          <span id="right_title" style="font-weight: bolder;"><spring:message code="title.metaData"/></span>
        </div>
        <div class="f-fl f-ml10">
          <input type="text" id="searchNmEntry"  class="f-fs12" placeholder="<spring:message code="lbl.input.placehold"/>">
        </div>
        <div style="margin-left: 340px;">
          <a id="btn_Delete_relation" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr10"
             href="javascript:$.publish('grid:right:delete',[''])" href="#">
            <spring:message code="btn.multi.delete"/>
          </a>
        </div>
        <div style="margin-left: 340px;">
          <a id="btn_create_relation" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr10"
             href="javascript:$.publish('grid:right:open',['','new'])">
            <spring:message code="btn.create"/>
          </a>
        </div>
      </li>
    </ul>
      </div>
    <div id="div_relation_grid" ></div>
  </div>
</div>
</div>
