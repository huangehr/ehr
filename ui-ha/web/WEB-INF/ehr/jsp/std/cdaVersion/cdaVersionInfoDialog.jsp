<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<div id="div_addVersion_form" data-role-form class="m-form-inline f-mt20 f-pb30" style="overflow:auto">
	<input data-attr-scan="stage" hidden="hidden"/>
	<input id="oldVersionName" hidden="hidden"/>
	<%--<div class="m-form-group m-form-readonly">
		<label>版本号:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_version"  data-attr-scan="version"/>
		</div>
	</div>--%>
	<div class="m-form-group m-form-readonly">
		<label>版本名称:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_version"  class="required useTitle ajax" required-title="不能为空" data-attr-scan="version"/>
		</div>
	</div>
	<div class="m-form-group m-form-readonly">
		<label>版本名称:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_versionName"  class="required useTitle ajax" required-title="不能为空" data-attr-scan="versionName"/>
		</div>
	</div>
	<div class="m-form-group m-form-readonly">
		<label>创建者:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_author"  data-attr-scan="author"/>
		</div>
	</div>
	<div class="m-form-group m-form-readonly">
		<label>创建时间:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_commitTime"  data-attr-scan="commitTime"/>
		</div>
	</div>
	<div class="m-form-group m-form-readonly">
		<label>继承版本号:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_base_version"  data-attr-scan="baseVersion"/>
		</div>
	</div>

	<div class="m-form-group m-form-readonly">
		<label>状态:</label>
		<div class="l-text-wrapper m-form-control">
			<input type="text" id="inp_displayStage" />
		</div>
	</div>
	<div id="div_toolbar" class="m-form-control pane-attribute-toolbar f-tar f-mr10">
		<div class="l-button u-btn u-btn-primary u-btn-large f-ib f-vam " id="div_btn_save">
			<span><spring:message code="btn.affirm"/></span>
		</div>
		<div class="l-button u-btn u-btn-cancel u-btn-large f-ib f-vam close-toolbar" id="div_btn_cancel">
			<span><spring:message code="btn.cancel"/></span>
		</div>
	</div>
</div>
