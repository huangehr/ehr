<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<div class="f-dn" data-head-title="true"><spring:message code="title.dict.manage"/></div>
<div id="div_wrapper" >
  <div id="conditionArea" class="f-mb10" align="right" style="margin-right: 10px;">
    <div class="body-head f-h50" align="left" style="border-bottom: 1px solid #ccc">
      <a href="javascript:$('#contentPage').empty();$('#contentPage').load('${contextRoot}/adapterorg/initial');"  class="f-fwb back">返回上一级</a>
      <input id="adapter_org" value='${adapterOrg}' hidden="none" />
		<span class="f-ml20">标准名称：</span><input class="f-fwb f-ml20 f-mt10" id="adapterorg_name"/>
		<span class="f-ml20">标准类别：</span><input class="f-mt10" id="adapterorg_type"/>
		<span class="f-ml20">采集机构：</span><input class="f-mt10" id="adapterorg_org"/>
      <span class="f-ml20">继承标准：</span><input class="f-mt10" id="adapterorg_parent"/>
    </div>
    <div class="switch f-tac f-h50">
      <button id="switch_dataSet" class="btn btn-primary f-mt10"><spring:message code="lbl.dataset"/></button>
      <button id="switch_dict" class="btn f-mt10"><spring:message code="lbl.dict"/></button>
    </div>
  </div>

<div id="grid_content" style="width: 100%">
  <!--######数据集部分######-->
  <div id="div_left" style=" width:400px;float: left;">
    <div id="retrieve" class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px">
      <ul>
        <li class=" f-mt15">
          <div class="f-mt10 f-fs14 f-ml10 f-fl f-fwb">
              <span id="left_title" ><spring:message code="lbl.dataset"/></span>
          </div>
          <div class="f-fl f-ml10">
            <input type="text" id="searchNm" class="f-fs12" placeholder="<spring:message code="lbl.input.placehold"/>">
          </div>
          <div style="margin-left: 340px;padding-top: 4px">
          <div title="新增" id="btn_create" class="image-create"  onclick="javascript:$.publish('grid:left:open',['','new'])"></div>
          </div>
        </li>
      </ul>
    </div>
    <div id="div_left_grid" >

    </div>
  </div>

<!--   字典详情   -->
  <div id="div_right" style="float: left;width: 700px;margin-left: 10px">
    <div id="entryRetrieve" class="m-retrieve-area f-h50 f-dn f-pr" style="display:block;border: 1px solid #D6D6D6;border-bottom: 0px">
    <ul>
      <li class=" f-mt15">
        <div class="f-mt10 f-fs14 f-ml10 f-fl f-fwb">
          <span id="right_title"><spring:message code="title.metaData"/></span>
        </div>
        <div class="f-fl f-ml10">
          <input type="text" id="searchNmEntry" class="f-fs12" placeholder="<spring:message code="lbl.input.placehold"/>">
        </div>
        <div style="margin-left: 340px;">
          <a id="btn_Delete_relation" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr10" style="  margin-right: 10px;margin-left: 10px;"
             href="javascript:$.publish('grid:right:delete',[''])" href="#">
            <spring:message code="btn.multi.delete"/>
          </a>
        </div>
        <div style="margin-left: 340px;">
          <a id="btn_create_relation" class="btn u-btn-primary u-btn-small s-c0 J_add-btn f-fr f-mr10" style="  margin-right: 20px;"
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
