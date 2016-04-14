<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {
        $(function () {

            /* ************************** 全局变量定义 **************************** */
            var Util = $.Util;
            var dictRetrieve = null;
            var dictMaster = null;
            var conditionArea = null;

            var entryRetrieve = null;
            var entryMater = null;

            var versionStage = null;
            /* *************************** 函数定义 ******************************* */
            function pageInit() {

                resizeContent();
                dictRetrieve.init();
                conditionArea.init();
                entryRetrieve.init();
                //versionStage=getStagedByValue();
            }

            function getStagedByValue()
            {
                debugger;
                var _value = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                if (!_value && _value == "") return false;
                var data = $("#stdDictVersion").ligerComboBox().data;
                for(var i =0;i<data.length;i++)
                {
                    if(data[i].version==_value)
                    {
                        return data[i].inStage;
                    }
                }
                return false;
            }

            function resizeContent() {
                var contentW = $('#grid_content').width();
                var leftW = $('#div_left').width();
                $('#div_right').width(contentW - leftW - 20);
            }

            function reloadGrid(url, params) {
                this.grid.setOptions({parms: params, newPage: 1});
                this.grid.loadData(true);
            }

            /* *************************** 标准字典模块初始化 ***************************** */
            conditionArea = {
                $element: $('#conditionArea'),
                $stdDictVersion: $('#stdDictVersion'),

                init: function () {
                    this.initDDL(this.$stdDictVersion);
                    this.$element.show();
                },
                initDDL: function (target) {
                    var dataModel = $.DataModel.init();
                    dataModel.fetchRemote("${contextRoot}/cdaVersion/getVersionList", {
                        success: function (data) {
                            target.ligerComboBox({
                                valueField: 'version',
                                textField: 'versionName',
                                data: data.detailModelList,
                                initValue: 1,
                                allowBlank: false,
                                onSelected: function (value, text) {
                                    versionStage=getStagedByValue();
                                    dictMaster.init();
                                }
                            });
                            var manager = target.ligerGetComboBoxManager();
                            manager.selectItemByIndex(0);

                        }
                    });
                }
            };

            dictRetrieve = {
                $element: $('#dictRetrieve'),
                $searchNm: $('#searchNm'),
                $addBtn: $('#btn_create'),

                init: function () {

                    this.$searchNm.ligerTextBox({
                        width: 240, isSearch: true, search: function () {
                            dictMaster.grid.options.newPage = 1;
                            dictMaster.reloadGrid();
                        }
                    });
                    this.$element.show();
                    window.form = this.$element;
                }
            };

            dictMaster = {
                dictInfoDialog: null,
                grid: null,
                init: function () {
                    if (this.grid) {
                        this.reloadGrid();
                    }
                    else {
                        var searchNm = $("#searchNm").val();
                        var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                        this.grid = $("#div_stdDict_grid").ligerGrid($.LigerGridEx.config({
                            url: '${contextRoot}/cdadict/getCdaDictList',
                            parms: {
                                searchNm: searchNm,
                                strVersionCode: stdDictVersion
                            },
                            columns: [
                                {display: 'id', name: 'id', hide: true},
                                {display: '字典编码', name: 'code', width: '33%', isAllowHide: false, align: 'left'},
                                {display: '字典名称', name: 'name', width: '34%', isAllowHide: false, align: 'left'},
                                {
                                    display: '操作', name: 'operator', width: '33%', render: function (row) {
//					var html ='<div class="grid_edit"  style="" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "stddict:dictInfo:open", row.id,'modify') + '"></div>'
//							+'<div class="grid_delete"  style="" title=""' +
//							' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "stddict:dictInfoGrid:delete", row.id) + '"></div>';
                                    var html = '<a class="grid_edit"  href="#" title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "stddict:dictInfo:open", row.id, 'modify') + '"></a>' +
                                            '<a class="grid_delete" href="#" title="删除" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "stddict:dictInfoGrid:delete", row.id) + '"></a>';
                                    return html;
                                }
                                }
                            ],
                            //enabledEdit: true,
                            validate: true,
                            unSetValidateAttr: false,
                            allowHideColumn: false,
                            onBeforeShowData: function (data) {
                                if (data.totalCount == 0) {
                                    entryMater.reloadGrid('');
                                }
                            },
                            onAfterShowData: function () {
                                this.select(0);
                            },
                            onSelectRow: function (row) {
                                entryMater.init();
                                entryMater.reloadGrid();
                            }
                        }));
                        this.bindEvents();
                        // 自适应宽度
                        this.grid.adjustToWidth();
                    }
                },
                reloadGrid: function () {
                    var searchNm = $("#searchNm").val();
                    var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                    var values = {
                        searchNm: searchNm,
                        strVersionCode: stdDictVersion
                    };
                    reloadGrid.call(this, '${contextRoot}/cdadict/getCdaDictList', values);
                },
                bindEvents: function () {
                    $.subscribe('stddict:dictInfo:open', function (event, id, mode) {
                        var title = '';
                        //只有new 跟 modify两种模式会到这个函数
                        if (mode == 'modify') {
                            title = '修改标准字典';
                        }
                        else {
                            title = '新增标准字典';
                        }
                        var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                        dictMaster.dictInfoDialog = $.ligerDialog.open({
                            height: 462,
                            width: 460,
                            title: title,
                            url: '${contextRoot}/cdadict/template/stdDictInfo',
                            urlParms: {
                                dictId: id,
                                mode: mode,
                                strVersionCode: stdDictVersion,
                                staged:versionStage
                            },
                            isHidden: false,
                            opener: true,
                            load: true
                        });
                    });

                    $.subscribe('stddict:dictInfoGrid:delete', function (event, id) {
                        console.log(versionStage);
                        if(!versionStage)
                        {
                            $.Notice.error("已发布版本不可删除，请确认!");
                            return;
                        }

                        $.Notice.confirm('确认要删除所选数据？', function (r) {
                            if (r) {
                                var dataModel = $.DataModel.init();
                                var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                                dataModel.updateRemote('${contextRoot}/cdadict/deleteDict', {
                                    data: {dictId: id, cdaVersion: stdDictVersion},
                                    success: function (data) {
                                        $.Notice.success('操作成功！');
                                        dictMaster.reloadGrid();
                                    }
                                });
                            }
                        })
                    });
                }
            };

            entryRetrieve = {
                $element: $('#entryRetrieve'),
                $searchNm: $('#searchNmEntry'),

                init: function () {
                    this.$searchNm.ligerTextBox({
                        width: 200, isSearch: true, search: function () {
                            entryMater.grid.options.newPage = 1;
                            entryMater.reloadGrid();
                        }
                    });
                    this.$element.show();
                    window.form = this.$element;
                }
            };

            entryMater = {
                entryInfoDialog: null,
                grid: null,
                init: function (dictId) {
                    if (this.grid)
                        return;
                    this.grid = $("#div_relation_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/cdadict/searchDictEntryList',
                        columns: [
                            {display: 'id', name: 'id', hide: true},
                            {display: 'dictId', name: 'dictId', hide: true},
                            {display: '值域编码', name: 'code', width: '33%', isAllowHide: false, align: 'left'},
                            {display: '值域名称', name: 'value', width: '34%', isAllowHide: false, align: 'left'},
                            {
                                display: '操作', name: 'operator', width: '33%', render: function (row) {

//				  var html ='<div class="grid_edit"  style="" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}'])", "entry:dictInfo:open", row.id,row.dictId,'modify') + '"></div>'
//						  +'<div class="grid_delete"  style="" title=""' +
//						  ' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "entry:dictInfoGrid:delete", row.id) + '"></div>';
                                var html = '<a class="grid_edit" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}'])", "entry:dictInfo:open", row.id, row.dictId, 'modify') + '"></a>' +
                                        '<a class="grid_delete" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "entry:dictInfoGrid:delete", row.id) + '"></a>';
                                return html;
                            }
                            }
                        ],
                        //delayLoad:true,
                        selectRowButtonOnly: false,
                        validate: true,
                        unSetValidateAttr: false,
                        allowHideColumn: false,
                        checkbox: true,
                        onDblClickRow: function (row) {
                            //$.publish('entry:dictInfo:open',[row.id, row.dictId, 'modify']);
                        }
                    }));
                    this.bindEvents();
                    // 自适应宽度
                    this.grid.adjustToWidth();
                },
                reloadGrid: function (dictId) {
                    var searchNmEntry = $("#searchNmEntry").val();
                    var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                    var dictId = dictId == '' ? -1 : dictMaster.grid.getSelectedRow().id;
                    var values = {
                        searchNmEntry: searchNmEntry,
                        strVersionCode: stdDictVersion,
                        dictId: dictId
                    };
                    reloadGrid.call(this, '${contextRoot}/cdadict/searchDictEntryList', values);
                },
                bindEvents: function () {
                    //窗体改变大小事件
                    $(window).bind('resize', function () {
                        resizeContent();
                    });
                    //
                    $.subscribe('entry:dictInfo:open', function (event, id, dictId, mode) {
                        if (!dictMaster.grid.getSelectedRow()) {
                            $.Notice.warn('请先添加标准字典数据！');
                            return;
                        }
                        var title = '';
                        //只有new 跟 modify两种模式会到这个函数
                        if (mode == 'modify') {
                            title = '修改字典项';
                        }
                        else {
                            dictId = dictMaster.grid.getSelectedRow().id;
                            title = '新增字典项';
                        }
                        var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                        entryMater.entryInfoDialog = $.ligerDialog.open({
                            height: 360,
                            width: 460,
                            title: title,
                            url: '${contextRoot}/cdadict/template/dictEntryInfo',
                            urlParms: {
                                id: id,
                                dictId: dictId,
                                mode: mode,
                                strVersionCode: stdDictVersion,
                                staged:versionStage
                            },
                            isHidden: false,
                            opener: true,
                            load: true
                        });
                    });

                    $.subscribe('entry:dictInfoGrid:delete', function (event, ids) {

                        if(!versionStage)
                        {
                            $.Notice.error("已发布版本不可删除，请确认!");
                            return;
                        }

                        if (!ids) {
                            var rows = entryMater.grid.getSelectedRows();
                            if (rows.length == 0) {
                                $.Notice.warn('请选择要删除的数据行！');
                                return;
                            }
                            for (var i = 0; i < rows.length; i++) {
                                ids += ',' + rows[i].id;
                            }
                            ids = ids.length > 0 ? ids.substring(1, ids.length) : ids;
                        }

                        $.Notice.confirm('确认要删除所选数据？', function (r) {
                            if (r) {
                                var dataModel = $.DataModel.init();
                                var stdDictVersion = $("#stdDictVersion").ligerGetComboBoxManager().getValue();
                                dataModel.updateRemote('${contextRoot}/cdadict/deleteDictEntryList', {
                                    data: {id: ids, cdaVersion: stdDictVersion},
                                    success: function (data) {
                                        $.Notice.success('操作成功！');
                                        entryMater.reloadGrid();
                                    }
                                });
                            }
                        })

                    });
                },
                closeDl: function () {
                    this.entryInfoDialog.close();
                }
            };
            /* ******************Dialog页面回调接口****************************** */
            win.reloadMasterGrid = function () {
                dictMaster.reloadGrid();
            };
            win.getStrVersion = function () {
                return $("#stdDictVersion").ligerGetComboBoxManager().getValue();
            };
            win.reloadEntryMasterGrid = function () {
                entryMater.reloadGrid();
            };
            win.closeDialog = function (type, msg) {
                if (type == 'right')
                    entryMater.closeDl();
                else
                    dictMaster.dictInfoDialog.close();
                if (msg)
                    $.Notice.success(msg);
            };
            /* *************************** 页面功能 **************************** */
            pageInit();
        });
    })(jQuery, window);
</script>
