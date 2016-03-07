<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>


<script>
    (function ($, win) {
        $(function () {

            /* ************************** 全局变量定义 **************************** */
            var Util = $.Util;
            var retrieve = null;
            var master = null;

            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                retrieve.init();

            }

            function reloadGrid (url, params) {
                this.grid.set({
                    url: url,
                    parms: params,
                    newPage:1
                });
                this.grid.reload();
            }

            /* *************************** 模块初始化 ***************************** */
            retrieve = {
                $element: $('.m-retrieve-area'),
                $searchVersionDDL: $('#inp_searchVersion'),
                $searchOrgName: $('#inp_searchOrgName'),
                $addBtn: $('#btn_add'),

                init: function () {
                    this.initVersionDDL(this.$searchVersionDDL);
                    this.$searchOrgName.ligerTextBox({width: 240,isSearch:true,search: function() {
                        master.reloadGrid();
                    }});
                    this.$element.show();
                    this.$element.attrScan();
                },
                initVersionDDL: function (target) {
                    /*target.ligerComboBox({
                        url: "${contextRoot}/standardsource/getVersionList",
                        dataParmName: 'obj',
                        //urlParms: {dictId: dictId},
                        valueField: 'version',
                        textField: 'versionName'
                    });*/
                    var dataModel = $.DataModel.init();
                    dataModel.fetchRemote("${contextRoot}/standardsource/getVersionList",{
                        success: function(data) {
                            target.ligerComboBox({
                                valueField: 'code',
                                textField: 'value',
                                data: [].concat(data.detailModelList),
                                //initValue: '000000000000',
                                //allowBlank: false,
                                onSelected:function(){
                                    master.reloadGrid();
                                }
                            });

                            var manager = target.ligerGetComboBoxManager();
                            master.init();
                            manager.selectItemByIndex(0);
                        }});
                },
                bindEvents: function () {
                }
            };

            master = {
                grid: null,
                $filePickerBtn: $('#div_file_picker'),
                init: function () {
                    retrieve.$element.attrScan();
                    var values = retrieve.$element.Fields.getValues();
                    var orgName = values.orgName;
                    var version = values.version;
                    //var version = '000000000000';

                    this.grid = $("#div_tpl_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/template/searchTemplate',
                        parms: {
                            version: version,
                            orgName: orgName
                        },
                        columns: [
                            {display: 'id', name: 'id', hide: true, isAllowHide: false},
                            {display: '模板', name: 'title', width: '15%', isAllowHide: false,align:'left'},
                            {display: '医疗机构', name: 'orgName', width: '30%', isAllowHide: false,align:'left'},
                            {display: 'CDA文档ID', name: 'cdaDocId', hide: true, isAllowHide: false},
                            {display: 'CDA文档', name: 'cdaDocName', width: '25%', minColumnWidth: 60,align:'left'},
                            {display: 'CDA版本ID', name: 'cdaVersionId', hide: true, isAllowHide: false},
                            {display: 'HTML模版', name: 'checkStatus', width: '15%', minColumnWidth: 20, render: function (row) {
                                    var html = '<a href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "tpl:tplInfo:open", row.id, 'modify') + '">修改</a> / ' +
                                            '<a href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "tpl:tplUpload:open", row.id, 'modify') + '">导入</a>';
                                    return html;
                                }
                            },
                            {
                                display: '复制模版', name: 'operator', width: '15%', render: function (row) {
                                var html = '<a href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "tpl:tplInfo:open", row.id, 'copy') + '">复制</a>';
                                return html;
                            }
                            }
                        ],
                        enabledEdit: true,
                        validate : true,
                        unSetValidateAttr:false,
                        onDblClickRow : function (row){
//                            alert("view");
                            /*var mode = 'view';
                            this.appInfoDialog = $.ligerDialog.open({
                                height:640,
                                width: 600,
                                title : '用户基本信息',
                                url: '${contextRoot}/app/template/appInfo',
                                urlParms: {
                                    appId: row.id,
                                    mode:mode
                                },
                                isHidden: false
                            });*/
                        }
                    }));

                    this.$filePickerBtn.instance = this.$filePickerBtn.webupload({
                        auto: true,
                        server: "${contextRoot}/template/update_tpl_content"
                    });

                    this.bindEvents();
                    // 自适应宽度
                    this.grid.adjustToWidth();
                },
                reloadGrid: function () {
                    var values = retrieve.$element.Fields.getValues();
                    reloadGrid.call(this, '${contextRoot}/template/searchTemplate', values);
                },
                bindEvents: function () {
                    var self = this;
                    $.subscribe('tpl:tplInfo:open',function(event,id,mode){
                        var urlParms = {};
                        if(!Util.isStrEmpty(id)){
                        urlParms['idNo'] = id;
                        }
                        urlParms['mode'] = mode;
						var title = '新增模板';
						if(mode=='copy'){
							title='复制模板';
						}else if(mode=='modify'){
							title='修改模板';
						}
                        this.archiveTplInfoDialog = $.ligerDialog.open({
                            height:370,
                            width: 450,
                            title : title,
                            url: '${contextRoot}/template/template/tplInfo',
                            urlParms: urlParms,
                            isHidden: false,
                            opener: true
                        });
                    });

					var uploader = self.$filePickerBtn.instance;
					var templateId = '';
					uploader.on('beforeSend',function( file, data ) {
						debugger;
						data.templateId = templateId;
					});
					uploader.on('success',function( ) {
						$.Notice.success('导入成功');
					});
					uploader.on('error',function( file, data ) {
						$.Notice.error('导入失败');
					});
                    $.subscribe('tpl:tplUpload:open',function(event,id,mode){
						templateId = id;
                        uploader.reset();
                        $(".webuploader-element-invisible",self.$filePickerBtn).trigger("click");
                    });
                },
            };

            /* *************************** 页面功能 **************************** */

            win.reloadGrids =  function (){
                master.reloadGrid();
            };

            pageInit();
        });
    })(jQuery, window);

</script>