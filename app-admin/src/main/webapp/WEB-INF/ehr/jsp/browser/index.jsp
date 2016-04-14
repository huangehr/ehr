<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!DOCTYPE html>
<html>
	<head>
		<%--导入共通头部文件 --%>
		<%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp"%>
		<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
		<link rel="stylesheet" href="${staticRoot}/lib/browser/timeline/timeline.css">

		<style>
			.ms-controller,.ms-important,[ms-controller],[ms-important]{ visibility: hidden; }
			.f-dn-force { display: none !important; }
			/* Scrollbar */
			.scrollbar { position: absolute; right: 10px; top: 30px; width: 3px; height: 200px; background: #ccc; line-height: 0;  }
			.scrollbar .handle { width: 100%; height: 100px; background: #292a33; cursor: pointer; }
			.scrollbar .handle .mousearea { position: absolute; top: 0; left: -10px; width: 22px; height: 100%; }

			#div_detail_info_tag { position: absolute;width: 100%;height: 30px;line-height: 30px;padding-left: 10px; background: #edf6fa;z-index: 1; }
			#div_time_line_retrieve { padding-left: 55px; background-color: #EDF6FA; }
			#div_time_line_retrieve .time-line-icon { height: 30px;width: 30px; background:url(${staticRoot}/lib/browser/timeline/images/Hr_icon.png) no-repeat; }
			.event.parent>.event-title .title-wrap { color: #2d9bd2; }

			.item-slider-widget { display: inline-block;  position: absolute; top: 2px; right: 10px;  z-index: 200;  }
			.item-slider-widget .item-slider-container { background-color: #FFF; border: 1px solid #999; padding: 0 3px;  }
			.item-slider-widget .item-slider { position: absolute; height: 32px;  width: 62px;  border-radius: 2px;  margin-top: -4px; z-index: 100; background: #edf6fa; background: -moz-linear-gradient(top,#2d9bd2 0%,#0082CD 50%,#2d9bd2 100%); background: -webkit-gradient(linear,left top,left bottom,color-stop(0%,#2d9bd2),color-stop(50%,#0082CD),color-stop(100%,#2d9bd2)); background: -webkit-linear-gradient(top,#2d9bd2 0%,#0082CD 50%,#2d9bd2 100%); background: -o-linear-gradient(top,#2d9bd2 0%,#0082CD 50%,#2d9bd2 100%); background: -ms-linear-gradient(top,#2d9bd2 0%,#0082CD 50%,#2d9bd2 100%); background: linear-gradient(to bottom,#2d9bd2 0%,#0082CD 50%,#2d9bd2 100%); filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#2d9bd2',endColorstr='#2d9bd2',GradientType=0); cursor: pointer; transition: all 0.25s; -webkit-transition: all 0.25s; -moz-webkit-transition: all 0.25s; -ms--webkit-transition: all 0.25s; -o-webkit-transition: all 0.25s;  }
			.item-slider-widget .item-slider-container .item { display: inline-block; color: #c3c3c3; line-height: 12px; text-transform: uppercase; padding: 7px 6px 5px;  cursor: pointer;  transition: color 0.25s; -webkit-transition: color 0.25s; -moz-webkit-transition: color 0.25s; -ms--webkit-transition: color 0.25s; -o-webkit-transition: color 0.25s;  }
			.item-slider-widget .item-slider-container .item.active { z-index: 101; position: relative; color: #f1f1f1; }
		</style>
	</head>
<body >
	<div class="m-container l-layout f-oh">
		<div position="left" class="l-layout-content" hidetitle="true">
			<div>
				<div id="div_time_line_retrieve" class="f-dn">
					<span class="time-line-icon f-ib f-vam"></span>
					<span class="f-ib f-vam f-fwb s-c1">医疗事件</span>
				</div>
			</div>
			<div class="m-event-timeline"></div>
		</div>

		<div position="center" class="l-layout-content m-record-view">
			<div id="div_detail_info_tag" class="f-tal f-c1 f-fwb s-c1 f-dn" >详细信息</div>
			<div class="item-slider-widget j-item-slider-wrapper">
				<div class="item-slider-container">
					<div class="item-slider j-item-slider" style="width: 59px; margin-left: 0px;"></div>
					<div class="item j-item active" data-origin="true">
						医院视图
					</div>
					<div class="item j-item" data-origin="false">
						标准视图
					</div>
				</div>
			</div>
			<iframe id="ifm_record_template" name="recordTemplate" class="f-ww f-hh">

			</iframe>
		</div>
	</div>
	<div id="div_time_line_data" class="f-dn">
		${data}
	</div>
	<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>
	<script src="${staticRoot}/lib/plugin/easing/jquery.easing.1.3.js"></script>
	<script src="${staticRoot}/lib/plugin/sly/sly.js"></script>
	<script src="${staticRoot}/lib/browser/timeline/timeline.js"></script>
	<script>
		(function ($, win) {
			$(function () {
				var Util = $.Util;

				var mainLayout = null;
				var timeline = null;
				var recordView = null;

				function pageInit() {
					mainLayout.init();
					timeline.init();
				}

				mainLayout = {
					$element: $('.m-container'),
					$timelineRetrieve: $('#div_time_line_retrieve'),
					$detailInfoTag: $('#div_detail_info_tag'),
					$transferSlider: $('.j-item[data-origin="false"]'),
					$originalSlider: $('.j-item[data-origin="true"]'),
					$sliderBar: $('.j-item-slider'),
					init: function () {
						var self = this;
						this.$element.ligerLayout({
							leftWidth: 340,
							allowLeftResize: false,
							onHeightChanged: function () {
								var timelineInstance = $.TimeLine.getInstance(timeline.$element[0]);
								timeline.$element.height(self.$element.height());
								timelineInstance.slyScroll.reload();
							}
						});
						timeline.$element.height(this.$element.height()-30);
						this.$timelineRetrieve.show();
						this.$detailInfoTag.show();
						this.bindEvents();
					},
					bindEvents: function () {
						var self = this;
						this.$originalSlider.click(function () {
							self.$transferSlider.removeClass('active');
							$(this).addClass('active');
							self.$sliderBar.animate({"margin-left":'0'},'fast');
							window.loadTemplate&&window.loadTemplate(true,timeline.cacheData);
						});
						this.$transferSlider.click(function () {
							self.$originalSlider.removeClass('active');
							$(this).addClass('active');
							self.$sliderBar.animate({"margin-left":'67px'},'fast');
							window.loadTemplate&&window.loadTemplate(false,timeline.cacheData);
						});
					}
				};
				timeline = {
					$element: $('.m-event-timeline'),
					$timelineData: $('#div_time_line_data'),
					cacheData: {},
					init: function () {
						var data = this.getData();
						var option = {
							contentWidth: 250,
							data:data
						};
						this.$element.timeline(option);
						this.bindEvent();
					},
					getYears: function(n) {
						var date = new Date();
						var curYear = date.getFullYear();
						var n = n || 10;
						var years = [];
						for(var i=0; i<n; i++) {
							years.unshift(curYear-i);
						}

						return years;
					},
					getData: function() {
						var json = this.$timelineData.text()|| "";
						var data = null;
						var events = [];
						var result = [];
						var map = {};
//						$.each(this.getYears(10), function (i, y) {
//							map[y] = [];
//						});
						var from =  parseInt($.Util.getUrlQueryString("from").substring(0,4));
						var to =  parseInt($.Util.getUrlQueryString("to").substring(0,4));
						for(var i=from; i<=to; i++) {
							map[i] = [];
						}
						if(!Util.isStrEmpty(json)) {
							try{
								data = JSON.parse(json);
							} catch(e){
								data = {events:[]}
							}
							events = data.events;
							$.each(events, function (j,e) {
								if(!Util.isStrEmpty(e.date)) {
									var year = e.date.split('-')[0];
									var chlid = {
										title: e.date.substr(5),
										content: Util.format('<div class="f-toe" title="{0} {1} {2}"><span>{0}</span><span class="f-pl10">{1}</span class="f-pl10"><span>{2}</span></div>',
												e.summary||"",e.orgName||"", e.diagnosisName||"")
									};

									if(e.documents) {
										chlid.children = [];
										$.each(e.documents, function (k, d) {
											chlid.children.push({
												content: d.name,
												cache: JSON.stringify({
													cdaVersion: d.cdaVersion,
													id: d.id,
													organizationCode: d.organizationCode,
													archiveKey: d.archiveKey,
													dataSets: d.dataSets
												})
											});
										});
									}

									map[year].push(chlid);
								} else {
									win.console && win.console.warn("ignore! event has no date.")
								}
							});
						}

						for(var p in map) {
							result.push({
								title: p+'年',
								open: true,
								children: map[p].sort(function(a,b){
									if(a.title>b.title) {
										return -1;
									}else{
										return 1;
									}
								})
							});
						}

						result.sort(function(a,b){
							if(a.title>b.title) {
								return -1;
							}else{
								return 1;
							}
						});

						$.each(result, function (m, item) {
							var year = parseInt(item.title);
							if(!item.children.length) {
								item.children.push({
									content: '本年度无任何就诊记录！'
								});
							}
						});

						return result;
					},
					bindEvent: function () {
						var self = this;
						var loadingDialog = null;
						this.$element.on('detailClick', function (e,el,data) {

							loadingDialog = $.Notice.waitting({msg: '正在加载中...'});

							var cacheData = self.cacheData = JSON.parse(data);

							var url = '${contextRoot}/browser/personal-profile/document';

							var origin = $('.j-item.active').attr('data-origin');
							//$('#ifm_record_template').attr('src', url);
							var html='<form id="div_query_form" name="queryForm" method="post" target="_self" action="${contextRoot}/browser/personal-profile/document">'+
									'<input type="hidden" id="cda_version" name="cda_version" value="'+cacheData.cdaVersion+'"/>'+
									'<input type="hidden" id="cda_document_Id" name="cda_document_Id" value="'+cacheData.id+'"/>'+
									'<input type="hidden" id="org_code" name="org_code" value="'+cacheData.organizationCode+'"/>'+
									'<input type="hidden" id="archive_key" name="archive_key" value="'+cacheData.archiveKey+'"/>'+
									'<input type="hidden"  name="origin_data" value="'+origin+'"/>'+
									'<input type="hidden" id="data_set_list" name="data_set_list" />'+
									'</form>';
							var iframeDoc = document.getElementById("ifm_record_template").contentWindow.document;
							iframeDoc.write(html);
							iframeDoc.getElementById('cda_version').value = cacheData.cdaVersion;
							iframeDoc.getElementById('cda_document_Id').value = cacheData.id;
							iframeDoc.getElementById('org_code').value = cacheData.organizationCode;
							iframeDoc.getElementById('archive_key').value = cacheData.archiveKey;
							iframeDoc.getElementById('data_set_list').value = JSON.stringify(cacheData.dataSets||[]);
							iframeDoc.getElementById('div_query_form').submit();

						});

						var iframe = document.getElementById("ifm_record_template");
						if (iframe.attachEvent){
							iframe.attachEvent("onload", function(){
								loadingDialog.close();
							});
						} else {
							iframe.onload = function () {
								loadingDialog.close();
							}
						}
					}
				};

				pageInit();
			});
		})(jQuery, window);
	</script>
</body>
</html>