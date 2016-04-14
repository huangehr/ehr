<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>

    (function ($, win) {
        $(function () {

            /* ************************** 全局变量定义 **************************** */
            var Util = $.Util;
            var retrieve = null;
            var master = null;
            var appInfoGrid = null;

            var catalogDictId = 1;
            var statusDictId = 2;

            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                retrieve.init();
                master.init();
            }

            function reloadGrid (url, params) {
                this.grid.set({
                    url: url,
                    parms: params
                });
                this.grid.reload();
            }

            /* *************************** 模块初始化 ***************************** */
            retrieve = {
                $element: $('.m-retrieve-area'),
                $searchNm: $('#inp_search'),
                $catalogDDL: $('#ipt_catalog'),
                $statusDDL: $('#ipt_status'),
                $searchBtn: $('#btn_search'),
                $addBtn: $('#btn_add'),

                init: function () {
                    this.initDDL(catalogDictId, $('#ipt_catalog'));
                    this.initDDL(statusDictId, $('#ipt_status'));

                    this.$searchNm.ligerTextBox({width: 240});
                    this.$element.show();

                    this.bindEvents();

                    this.$element.attrScan();
                    window.form = this.$element;
                },
                initDDL: function (dictId, target) {
                    var target = $(target);
                    var dataModel = $.DataModel.init();

                    dataModel.fetchRemote("${contextRoot}/dict/searchDictEntryList",{data:{dictId: dictId,page: 1, rows: 10},
                        success: function(data) {
                            target.ligerComboBox({
                                valueField: 'code',
                                textField: 'value',
                                data: [].concat({code:'',value:''},data.detailModelList)
                            });
                        }});
                },
                bindEvents: function () {
                    this.$searchBtn.click(function () {
                        master.reloadGrid();
                    })
                }
            };

            master = {
                appInfoDialog: null,
                grid: null,
                init: function () {
                    var searchNm = "";
                    var catalog = "";
                    var status = "";

                    this.grid = $("#div_app_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/app/searchApps',
                        parms: {
                            searchNm: searchNm,
                            catalog: catalog,
                            status: status
                        },
                        columns: [
                            { display: '名称', name: 'name',width: '15%', isAllowHide: false },
                            { display: 'APP ID',name: 'id', width: '10%',isAllowHide: false },
                            { display: 'APP Secret', name: 'secret', width: '10%', minColumnWidth: 60 },
                            { display: '类型', name: 'catalogName', width: '10%'},
                            { display: '回调URL', name: 'url', width: '25%',align:'left'},
                            { display: 'status', name: 'status',hide:true},
                            { display: '状态', name: 'statusName', width: '10%',resizable: true},
                            { display: '审核', name: 'checkStatus', width: '10%',minColumnWidth: 20,render: function (row){
                                if(Util.isStrEquals( row.status,'WaitingForApprove')) {
//									return '<div class="grid_edit"  style="margin-left: 20px;cursor:pointer;"  title="通过" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "appInfo:appInfoGrid:approved", row.id) + '"></div>'
//											+'<div class="grid_delete"  style="margin-left: 60px;cursor:pointer;" title="否决"' +
//											' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "appInfo:appInfoGrid:reject", row.id) + '"></div>';
                                    return '<a data-toggle="model"  class="checkPass label_a" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","appInfo:appInfoGrid:approved", row.id)+'">'+'通过'+'</a> /' +
                                            ' <a class="veto label_a" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","appInfo:appInfoGrid:reject", row.id)+'">'+'否决'+'</a>'
                                } else if(Util.isStrEquals( row.status,'Approved')){
//									return '<div class="grid_edit"  style="margin: 10px auto;cursor:pointer;"  title="禁用" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "appInfo:appInfoGrid:forbidden", row.id) + '"></div>'
									return '<a data-toggle="model"  class="Forbidden label_a" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","appInfo:appInfoGrid:forbidden", row.id)+'">'+'禁用'+'</a>'
                                }else if(Util.isStrEquals( row.status,'Forbidden')){
//									return '<div class="grid_edit"  style="margin: 10px auto;cursor:pointer;"  title="开启" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "appInfo:appInfoGrid:open", row.id) + '"></div>'
									return '<a data-toggle="model"  class="checkPass label_a" onclick="javascript:'+Util.format("$.publish('{0}',['{1}'])","appInfo:appInfoGrid:open", row.id)+'">'+'开启'+'</a>'
                                }else if(Util.isStrEquals( row.status,'Reject')){
                                    return '无'
                                }
                            }},
                            { display: '操作', name: 'operator', width: '10%', render: function (row) {
//								var html ='<div class="grid_edit"  style=""  title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "app:appInfo:open", row.id,'modify') + '"></div>'
                                var html = '<a class="grid_edit" title="编辑" href="#" onclick="javascript:'+Util.format("$.publish('{0}',['{1}','{2}'])","app:appInfo:open", row.id,'modify')+'"></a>' ;
                                return html;
                            }}
                        ],
                        enabledEdit: true,
                        validate : true,
                        unSetValidateAttr:false,
                        allowHideColumn: false,
                        onDblClickRow : function (row){
                            var mode = 'view';
                            master.appInfoDialog = $.ligerDialog.open({
                                height:640,
                                width: 600,
                                title : '应用基本信息',
                                url: '${contextRoot}/app/template/appInfo',
                                urlParms: {
                                    appId: row.id,
                                    mode:mode
                                },
                                isHidden: false
                            });
                        }
                    }));
                    this.bindEvents();
                    // 自适应宽度
                    this.grid.adjustToWidth();
                },
                check: function (id,status) {
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/app/check",{data:{appId:id,status:status},
                        success: function(data) {
                            master.reloadGrid();
                        }});
                },
                reloadGrid: function () {
                    var values = retrieve.$element.Fields.getValues();
                    reloadGrid.call(this, '${contextRoot}/app/searchApps', values);
                },
                bindEvents: function () {
                    $.subscribe('app:appInfo:open',function(event,appId,mode){

                        var title = '';

                        //只有new 跟 modify两种模式会到这个函数
                        if(mode == 'modify'){
                            title = '修改应用信息';
                        }
                        else{
                            title = '新增应用信息';
                        }

                        master.appInfoDialog = $.ligerDialog.open({
                            height:640,
                            width: 600,
                            title : title,
                            url: '${contextRoot}/app/template/appInfo',
                            urlParms: {
                                appId: appId,
                                mode:mode
                            },
                            isHidden: false,
                            opener: true
                        });
                    });
                    $.subscribe('appInfo:appInfoGrid:approved',function(event,id) {
                        var status = "Approved";
                        master.check(id,status);
                    });
                    $.subscribe('appInfo:appInfoGrid:reject',function(event,id) {
                        var status = "Reject";
                        master.check(id,status);
                    });
                    $.subscribe('appInfo:appInfoGrid:forbidden',function(event,id) {
                        var status = "Forbidden";
                        master.check(id,status);
                    });
                    $.subscribe('appInfo:appInfoGrid:open',function(event,id) {
                        var status = "Approved";
                        master.check(id,status);
                    });
                },
            };

            /* ******************Dialog页面回调接口****************************** */
            win.reloadMasterGrid = function () {
                master.reloadGrid();
            };

            win.closeDialog = function (callback) {
                if(callback){
                    callback.call(win);
                    master.reloadGrid();
                }
                master.appInfoDialog.close();
                $.Notice.success('更新成功');
            };

            /* *************************** 页面功能 **************************** */
            /* *************************** 页面功能 **************************** */
            pageInit();
        });
    })(jQuery, window);

</script>