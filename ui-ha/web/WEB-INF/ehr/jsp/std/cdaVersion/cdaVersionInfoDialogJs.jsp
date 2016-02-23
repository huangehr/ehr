<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
	(function($,win){
		$(function(){
			//----------版本名字不能修改的话，cdaVersionInfoDialog相关的三个文件都可修改
			/* ************************* 变量声明 ************************** */
			// 通用工具类库
			var Util = $.Util;
			var cdaVersionInfo = null;
			// 表单校验工具类
			var jValidation = $.jValidation;
			// 版本修改变量
			var cdaVersionModel = null;

			var cdaVersion = "${cdaVersion}";
			/* ************************* 函数定义 ************************** */
			function pageInit(){
				cdaVersionInfo.init();
			}
			cdaVersionInfo = {
				$form: $('#div_addVersion_form'),
				$version: $('#inp_version'),
				$versionName: $('#inp_versionName'),
				$oldVersionName: $('#oldVersionName'),
				$author: $('#inp_author'),
				$commitTime:$('#inp_commitTime'),
				$baseVersion:$('#inp_base_version'),
				$displayStage:$('#inp_displayStage'),
				$updateVersionBtn:$('#div_btn_save'),
				$cancelBtn:$('#div_btn_cancel'),
				$toolbar: $('#div_toolbar'),
				init: function(){
					this.initForm();
				},
				initForm: function(){
					this.$toolbar.hide();
					if('${mode}'=='modify'){
						this.$toolbar.show();
						this.$versionName.parent().parent().removeClass('m-form-readonly');
					};
					this.$version.ligerTextBox({width: 200});
					this.$versionName.ligerTextBox({width: 200});
					this.$author.ligerTextBox({width: 200});
					this.$commitTime.ligerTextBox({width: 200});
					this.$baseVersion.ligerTextBox({width: 200});
					this.$displayStage.ligerTextBox({width: 200});
					this.$form.attrScan();
					this.$form.Fields.fillValues({
						version: cdaVersion.version,
						versionName: cdaVersion.versionName,
						author: cdaVersion.author,
						commitTime: cdaVersion.commitTime,
						baseVersion: cdaVersion.baseVersion,
						stage:cdaVersion.stage,
						<%--version: '${cdaVersion.version}',--%>
						<%--versionName: '${cdaVersion.versionName}',--%>
						<%--author: '${cdaVersion.author}',--%>
						<%--commitTime: '${cdaVersion.commitTime}',--%>
						<%--baseVersion: '${cdaVersion.baseVersion}',--%>
						<%--stage:'${cdaVersion.stage}',--%>
					});
					this.$oldVersionName.val(cdaVersion.versionName)
					this.$displayStage.val(cdaVersion.stage?'未发布':'已发布')
					this.bindEvents();
				},
				// 绑定事件
				bindEvents: function(){
					 //修改版本名字时非空、唯一性验证---若名字不可手动修改可删除-----1
					 var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
						 onElementValidateForAjax: function (elm) {
					 		if (Util.isStrEquals($(elm).attr("id"), 'inp_versionName')) {
					 			var versionName = $("#inp_versionName").val();
					 			var oldVersionName = $("#oldVersionName").val();
					 			if(Util.isStrEqualsIgnorecase(versionName,oldVersionName)){
					 				return true;
					 			}else{
									return checkVersionName(versionName, "该版本号已存在！");
								}
					 		}
						 }
					 });
					 //修改版本名字时非空、唯一性验证---若名字不可手动修改可删除----2
					 function checkVersionName(versionName,errorMsg){
					 	var result = new jValidation.ajax.Result();
					 	var dataModel = $.DataModel.init();
					 	dataModel.fetchRemote("${contextRoot}/cdaVersion/checkVersionName", {
					 		data: {versionName:versionName},
					 		async: false,
					 		success: function (data) {
					 			if (data.successFlg) {
					 				result.setResult(false);
					 				result.setErrorMsg(errorMsg);
					 			} else {
					 				result.setResult(true);
					 			}
					 		},
						});
					 		return result;
					 };
					//修改版本名字时非空、唯一性验证---若名字不可手动修改可删除----2
					this.$updateVersionBtn.click(function(){
						if(validator.validate()){
							var dataModel = $.DataModel.init();
							cdaVersionModel = cdaVersionInfo.$form.Fields.getValues();
							//delete cdaVersionModel.displayStage;
							var cdaVersionModelJsonData = JSON.stringify(cdaVersionModel);
							dataModel.updateRemote("${contextRoot}/cdaVersion/updateVersion", {
								data:{cdaVersionModelJsonData: cdaVersionModelJsonData},
								async: false,
								success: function (data) {
									if (data.successFlg) {
										win.closeCdaVersionInfoDialog(function () {
											win.$.Notice.success('CDA版本修改成功');
										});
									} else {
										window.top.$.Notice.error('CDA版本修改失败');
									}
								}
							});
						}else{
							return ;
						}

					});
					this.$cancelBtn.click(function(){
						win.closeCdaVersionInfoDialog();
					});
				},
			};
			pageInit();
		});
	})(jQuery,window);
</script>
