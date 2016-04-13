<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
	(function($,win){
		$(function(){
			/* ***************************变量定义************************ */
			// 通用工具类库
			var Util = $.Util;
			// 页面查询模块
			var retrieve = null;
			// 页面信息模块
			var masters = null;
			// cda 版本信息表对象
			var cdaVersionInfoGrid = null;
			/* **************************函数定义************************ */
			// 页面初始化函数
			function pageInit(){
				retrieve.init();
				masters.init();
			};
			// 数据重载函数
			function reloadGrid(url,params){
				cdaVersionInfoGrid.set({
					url:url,
					parms:params,
				});
				cdaVersionInfoGrid.reload();
			};

			/* *************************模块初始化*********************** */
			retrieve = {
				$element:$('.m-retrieve-area'),
				$searchNm:$('#inp_searchNm'),
				$searchBtn:$('#btn_search'),
				$newRecordBtn:$('#div_new_record'),
				init:function(){
					this.$element.attrScan();
					window.form=this.$element;
					this.$searchNm.ligerTextBox({width:240});
				},
			};
			masters = {
				init:function(){
					cdaVersionInfoGrid = $('#div-cdaVersion-grid').ligerGrid($.LigerGridEx.config({
						url: '${contextRoot}/cdaVersion/searchVersions',
						parms: {
							searchNm: '',
							page:0,
							rows:0,
						},
						columns: [
							{display: '版本号', name: 'version', width: '15%', align: 'left'},
							{display: '版本名称', name: 'versionName', width: '10%', align: 'center'},
							{display: '创建者', name: 'author', width: '15%',align:'left'},
							{display: '创建时间', name: 'commitTime', width: '15%', align: 'left'},
							{display: '继承版本', name: 'baseVersion', width: '15%', align: 'left'},
							{display: '状态码', name: 'inStage', align: 'left',hide: true},
							{display: '状态', name: 'inStage', width: '15%', align: 'center',
								render:function(row){
									var html ='<a class="label_a" name="delete_click"  onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:commitVersion", row.version,row.inStage,0) + '">发布</a>'
									return row.inStage !=true?'已发布':html;
								}
							},
							{
								display: '操作', name: 'operator', width: '15%', align: 'center',render: function(row){
								var _title = "";
//								if(row.inStage == false){
//									_title="重新发布";
//									html ='<div class="grid_edit" name="delete_click"  style="margin: 10px auto;cursor:pointer;" title="'+_title+'" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:commitVersion", row.version,row.inStage,0) + '"></div>';
//								} else{
//									_title = "发布";
//									html ='<div class="grid_edit" name="delete_click" style="margin-left: 60px;cursor:pointer;" title="'+_title+'" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:commitVersion", row.version,row.inStage,0) + '"></div>'
//											+'<div class="grid_delete" name="delete_click" style="margin-left: 90px;cursor:pointer; title="删除"' +
//											' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:del", row.version,row.inStage,0) + '"></div>';
//								}
								html ='<a class="grid_edit" name="delete_click" style="" title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:commitVersion", row.version,row.inStage,0) + '"></a>'
											+'<a class="grid_delete" name="delete_click" style="" title="删除"' +
											' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "cdaVersion:del", row.version,row.inStage,0) + '"></a>';
								return html;
								}
							},
						],
						//enabledEdit: true,
						validate: true,
						unSetValidateAttr: false,
						usePager:false
					}));
					cdaVersionInfoGrid.adjustToWidth();
					this.bindEvents();
				},

				reloadGrid: function () {
					var searchNm = retrieve.$searchNm.val();
					var  values ={
						searchNm: searchNm,
						page:0,
						rows:0,
					};

					reloadGrid.call(this, '${contextRoot}/cdaVersion/searchVersions', values);
				},
				bindEvents: function(){
					//查询事件绑定
					retrieve.$searchBtn.click(function(){
						masters.reloadGrid();
					});
					//新增按钮事件绑定，提示继承版本，确认直接创建
					retrieve.$newRecordBtn.click(function(){
						var dataModel = $.DataModel.init();
						dataModel.createRemote("${contextRoot}/cdaVersion/existInStage", {
							async: false,
							success: function (data) {
								if (data.successFlg) {
									window.top.$.Notice.error('已经有一个编辑状态的版本，不能再新增！');
								} else {
									createNewVersion(data.obj);
								}
							}
						});
					});
					// 判断数据库是否已有存在的处于编辑状态的版本
					function createNewVersion(baseVersion){
						$.ligerDialog.confirm('最新版本将继承'+baseVersion+'版本，确认新增？',function(yes){
							if(yes){
								var dialog = $.ligerDialog.waitting('正在新版本创建中,请稍候...');
								var dataModel = $.DataModel.init();
								//var baseVersion = cdaVersionInfo.$baseVersion.val();
								dataModel.createRemote("${contextRoot}/cdaVersion/addVersion", {
									async: true,
									success: function (data) {
										dialog.close();
										if (data.successFlg) {
											masters.reloadGrid();
											win.parent.$.Notice.success('CDA版本新增成功');
										} else {
											window.top.$.Notice.error('CDA版本新增失败');
										}
									},
								});
							}
						});
					}

					//编辑cda版本
					$.subscribe('cdaVersion:modify', function (event, strVersion,stage,d) {
						if(stage=='false'){
							$.Notice.error('已经发布版本不能编辑！');
							return
						}else{
							var mode = 'modify';
							masters.cdaVersionInfoDialog = $.ligerDialog.open({
								height:460,
								width: 420,
								isDrag:true,
								title:'标准版本基本信息',
								url: '${contextRoot}/cdaVersion/getVersionById',
								load: true,
								urlParms: {
									strVersion: strVersion,
									mode:mode,
								}
							});
						}
					});
					//删除编辑状态的cda版本
					$.subscribe('cdaVersion:del', function (event, strVersion,stage,d) {
						if(stage=='false'){
							$.Notice.error('已发布版本不能删除。');
						}else{
							$.ligerDialog.confirm('确认删除该行信息？<br>如果是请点击确认按钮，否则请点击取消。',function(yes){
								if(yes){
									var dataModel = $.DataModel.init();
									masters.reloadGrid();
									dataModel.updateRemote("${contextRoot}/cdaVersion/deleteStageVersion",{data:{strVersion: strVersion},
										success: function(data) {
											if(data.successFlg){
												$.Notice.success('删除成功。');
												masters.reloadGrid();
											}else{
												$.Notice.error('删除失败。');
											}
										}
									});
								}
							});
						}
					});
					//cda版本发布
					$.subscribe('cdaVersion:commitVersion', function (event, strVersion,stage,d) {
						$.ligerDialog.confirm('发布后将不可更改，确认发布？',function(yes){
							if(yes){
								var _dialog = $.ligerDialog.waitting('正在发布中,请稍候...');
								var dataModel = $.DataModel.init();
								dataModel.updateRemote("${contextRoot}/cdaVersion/commitVersion",{data:{strVersion: strVersion},
									success: function(data) {
										if(data.successFlg){
											$.Notice.success('发布成功。');
											masters.reloadGrid();
										}else{
											$.Notice.error('发布失败！');
										}
									},
									complete:function(){
										_dialog.close();
									}
								});
							}
						});
					});
					//修改为为阶段版本
					$.subscribe('cdaVersion:rollbackToStage', function (event, strVersion,stage,d) {
						var dataModel= $.DataModel.init();
						dataModel.updateRemote("${contextRoot}/cdaVersion/isLatestVersion",{data:{strVersion: strVersion},
							success: function(data) {
								if(data.successFlg){
									rollback(strVersion);
								}else{
									$.Notice.error('非最新版本不能回滚为编辑状态！');
								}
							}
						});
						function rollback(strVersion){
							$.ligerDialog.confirm('确认修改为编辑状态？<br>如果是请点击确认按钮，否则请点击取消。',function(yes){
								if(yes){
									var dataModel = $.DataModel.init();
									dataModel.updateRemote("${contextRoot}/cdaVersion/rollbackToStage",{data:{strVersion: strVersion},
										success: function(data) {
											if(data.successFlg){
												$.Notice.success('修改版本状态成功。');
												masters.reloadGrid();
											}else{
												$.Notice.error('修改版本状态失败。');
											}
										}
									});
								}

							});
						}
					});
				}
			};
			/* ************************* Dialog页面回调接口 ************************** */
			win.reloadMasterUpdateGrid = function () {
				masters.reloadGrid();
			};
			win.closeCdaVersionInfoDialog = function (callback) {
				if(callback){
					callback.call(win);
					masters.reloadGrid();
				}
				masters.cdaVersionInfoDialog.close();
			};
			/* ************************* Dialog页面回调接口结束 ************************** */

			/* ************************* 页面初始化 ************************** */
			pageInit();
		});
	})(jQuery,window);
</script>