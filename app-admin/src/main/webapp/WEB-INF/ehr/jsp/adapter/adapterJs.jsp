<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>

    (function ($, win) {
        $(function () {

            /* ************************** 全局变量定义 **************************** */
            var Util = $.Util;
            var retrieve = null;
            var master = null;
            var adapterGrid = null;
            var adapterDataSet = null;
            var adapterType = 21;
            var searchOrg;
            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                retrieve.init();
                master.init();
            }

            function getAdapterPlan(target, id, mode) {

                var title = '';
                //只有new 跟 modify两种模式会到这个函数
                if (mode == 'modify') {
                    title = '修改标准适配';
                } else if (mode == 'new') {
                    title = '新增标准适配';
                }
                target.adapterInfoDialog = $.ligerDialog.open({
                    height: 600,
                    width: 440,
                    title: title,
                    load: true,
                    url: '${contextRoot}/adapter/gotoModify',
                    urlParms: {
                        id: id,
                        mode: mode
                    },
                    isHidden: false,
                    opener: true
                });
            }

            function delAdapterPlan(id) {
                var dialog = $.ligerDialog.waitting('正在删除中,请稍候...');
                var dataModel = $.DataModel.init();
                dataModel.updateRemote('${contextRoot}/adapter/delete', {
                    data: {ids: id},
                    success: function (data) {
                        if (data.successFlg) {
                            $.Notice.success('操作成功！');
                            //重新刷新Grid
                            master.reloadGrid();
                        } else {
                            $.Notice.error(data.errorMsg);
                        }
                    },
                    complete: function () {
                        dialog.close();
                    }
                });
            }

            function initSearchType(target, type) {
                var p = {
                    type: type,
                    mode: "modify",
                    version: "notVersion"};
                if(searchOrg)
                    searchOrg.reload(p);
                else
                    searchOrg = target.customCombo(
                            '${contextRoot}/adapter/getOrgList', p, undefined, undefined, false);
            }

            function release(id,organizationCode,versionCode) {
                var dialog = $.ligerDialog.waitting('正在发布中,请稍候...');
                var dataModel = $.DataModel.init();
                dataModel.updateRemote('${contextRoot}/adapter/adapterDispatch', {
                    data: {planId:id,organizationCode:organizationCode,versionCode:versionCode},
                    success: function (data) {
                        if (data.successFlg) {
                            $.ligerDialog.alert("发布成功!");
                            master.reloadGrid();
                        } else {
                            $.Notice.error(data.errorMsg);
                        }

                    },
                    complete: function () {
                        dialog.close();
                    }
                });
            }


            /* *************************** 模块初始化 ***************************** */
            retrieve = {
                $element: $('.m-retrieve-area'),
                $searchBox: $('#ipt_search'),
                $searchType: $('#ipt_search_type'),
                $searchOrg: $('#ipt_search_org'),
                $searchBtn: $('#btn_search'),
                $addBtn: $('#btn_add'),
                $deleteBtn: $('#btn_del'),

                init: function () {
                    var self = this;
                    self.$searchBox.ligerTextBox({
                        width: 240
                    });
                    initSearchType(self.$searchOrg, '');
                    self.$searchType.ligerComboBox({
                        url: '${contextRoot}/dict/searchDictEntryList',
                        valueField: 'code',
                        textField: 'value',
                        dataParmName: 'detailModelList',
                        urlParms: {
                            page: 1,
                            rows: 1000,
                            dictId: adapterType
                        },
                        onSelected: function (value) {
                            initSearchType(self.$searchOrg, value);
                        },
                        onClear: function () {
                            initSearchType(self.$searchOrg, '');
                        }
                    });
                    self.$element.show();
                    self.$element.attrScan();
                    window.form = self.$element;
                },
                bindEvents: function () {
                    //
                }
            };

            master = {
                adapterInfoDialog: null,
                adapterCustomize: null,
                init: function () {
                    adapterGrid = $("#div_adapter_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/adapter/list',
                        parms: this.formatParms(),
                        columns: [
                            {display: 'ID', name: 'id', hide: true},
                            {display: '方案类别', name: 'typeValue', width: '5%', align: 'left'},
                            {display: '方案类别代码', name: 'type', hide: true},
                            {display: '方案代码', name: 'code', width: '10%', minColumnWidth: 60, align: 'left'},
                            {display: '方案名称', name: 'name', width: '20%', align: 'left'},
                            {display: '标准版本', name: 'versionName', width: '6%', align: 'left'},
                            {display: '标准版本代码', name: 'version', hide: true},
                            {display: '采集机构代码', name: 'org', hide: true},
                            {display: '采集机构', name: 'orgValue', width: '20%', align: 'left'},
                            {display: '继承方案代码', name: 'parentCode', hide: true},
                            {display: '继承方案', name: 'parentName', width: '15%', align: 'left'},
                            {
                                display: '状态',name: 'status',width: '5%',align: 'left',
                                render: function (rowdata, rowindex, value) {

                                    var returnValue = "未发布";
                                    if (rowdata.status == 1) {
                                        returnValue = "已发布";
                                    }
                                    return returnValue;
                                }
                            },
                            {
                                display: '操作', name: 'operator', width: '19%', render: function (row) {
//								var html ='<div class="grid_edit"  style="margin-left: 10px;cursor:pointer;"  title="定制" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:open", row.id,'modify') + '"></div>'
//										+'<div class="grid_delete"  style="margin-left: 40px;cursor:pointer;" title="适配"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoDialog:del", row.id,'delete') + '"></div>'
//										+'<div class="grid_edit"  style="margin-left: 70px;margin-top:-22px;cursor:pointer;" title="修改"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoDialog:del", row.id,'delete') + '"></div>'
//										+'<div class="grid_delete"  style="margin-left: 100px;cursor:pointer;" title="删除"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoDialog:del", row.id,'delete') + '"></div>';

                                var html = '<a class="label_a" title="定制" style="margin-left:0px;" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "adapter:adapterInfo:customize", row.id, row.version) + '">定制</a>' +
                                        '<a class="label_a" title="适配" style="margin-left:5px;" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "adapter:adapterInfo:adapter", row.id, 'modify') + '">适配</a>';
//                                        '/<a href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}',])", "adapter:adapterInfo:open", row.id, 'modify') + '">修改</a>' +
//                                        '/<a href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "adapter:adapterInfo:delete", row.id) + '">删除</a>';
                                var text = "发布";
                                if (row.status == 1) {
                                    text = "重新发布";
                                }
//								html+='<div class="grid_delete"  style="margin-left: 130px;cursor:pointer;" title="'+text+'"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3},'{4}','{5}'])", "adapter:adapterInfo:release", row.org,row.version,text,row.id) + '"></div>';
                                html += '<a class="label_a" title="'+text+'" style="margin-left:5px; href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}','{4}','{5}'])", "adapter:adapterInfo:release", row.org, row.version,text,row.id) + '">' + text + '</a>';
								html += '<a class="grid_edit" title="编辑" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}',])", "adapter:adapterInfo:open", row.id, 'modify') + '"></a>' +
								'<a class="grid_delete" title="删除" href="#" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "adapter:adapterInfo:delete", row.id) + '"></a>';


								return html;
                            }
                            }
                        ],
                        selectRowButtonOnly: false,
                        checkbox: true,
                        allowHideColumn: false,
                        onDblClickRow: function (row) {
                            //
                        }
                    }));
                    this.bindEvents();
                    // 自适应宽度
                    adapterGrid.adjustToWidth();
                },
                reloadGrid: function () {
                    var values = this.formatParms(retrieve.$element.Fields.getValues());
                    adapterGrid.options.newPage = 1;
                    adapterGrid.setOptions({parms: $.extend({}, values)});
                    //重新查询
                    adapterGrid.loadData(true);
                },
                formatParms: function (values) {
                    var filters = "";
                    if(values) {
                        if (values.searchNm) {
                            filters += ";code?" + values.searchNm + " g1;name?" + values.searchNm + " g1"
                        }
                        if (values.searchType) {
                            filters += ";type=" + values.searchType;
                        } else {
                            filters += ";type=1,2,3";
                        }
                        if (values.searchOrg) {
                            filters += ";org=" + values.searchOrg ;
                        }
                    }
                    return {
                        filters: filters.length>0 ? filters.substring(1) : "",
                        fields: "",
                        sorts: ""
                    };
                },
                bindEvents: function () {
                    var self = this;
                    //搜索适配方案
                    retrieve.$searchBtn.click(function () {
                        self.reloadGrid();
                    });
                    //新增适配方案
                    retrieve.$addBtn.click(function () {
                        getAdapterPlan(self, '', 'new');
                    });
                    //批量删除适配方案
                    retrieve.$deleteBtn.click(function () {
                        var rows = adapterGrid.getSelectedRows();
                        if (rows.length > 0) {
                            $.ligerDialog.confirm('确定删除这些方案信息?', function (yes) {
                                if (yes) {
                                    var id = "";
                                    for (var i = 0; i < rows.length; i++) {
                                        id += "," + rows[i].id;
                                    }
                                    delAdapterPlan(id.substring(1));
                                }
                            });
                        }else{
                            $.ligerDialog.warn("请选择要删除的数据适配方案");
                        }
                    });
                    //修改适配方案
                    $.subscribe('adapter:adapterInfo:open', function (event, id, mode) {
                        getAdapterPlan(self, id, mode);
                    });
                    //删除适配方案
                    $.subscribe('adapter:adapterInfo:delete', function (event, id,status) {
                        if(status==1)
                        {
                            $.ligerDialog.tip("已发布方案不可删除！");
                            return;
                        }

                        $.ligerDialog.confirm('确定删除该方案信息?', function (yes) {
                            if (yes) {
                                delAdapterPlan(id);
                            }
                        });
                    });
                    //定制适配方案
                    $.subscribe('adapter:adapterInfo:customize', function (event, id, version,status) {
                        self.adapterCustomize = $.ligerDialog.open({
                            height: 500,
                            width: 700,
                            title: '标准定制',
                            url: '${contextRoot}/adapter/getAdapterCustomize',
                            urlParms: {
                                planId: id,
                                version: version,
                                status:status
                            },
                            isHidden: false,
                            opener: true
                        });
                    });
                    //适配
                    $.subscribe('adapter:adapterInfo:adapter', function (event, id, mode) {
                        var url = '${contextRoot}/adapterDataSet/initial?treePid=4&treeId=26&dataModel=' + id;
                        $("#contentPage").empty();
                        $("#contentPage").load(url);

                        <%--location.href= '${contextRoot}/adapterDataSet/initial?treePid=4&treeId=26&adapterPlanId='+id;--%>
                    });

                    //定制适配方案
                    $.subscribe('adapter:adapterInfo:release', function (event, organizationCode, versionCode,title,id) {

                        $.ligerDialog.confirm('确定'+title+'该方案?', function (yes) {
                            if (yes) {
                                release(id, organizationCode, versionCode);
                            }
                        });
                    });
                }
            };

            /* ******************Dialog页面回调接口****************************** */
            win.reloadAdapterMasterGrid = function () {
                master.reloadGrid();
            };
            win.closePlanDialog = function (msg) {
                master.reloadGrid();
                master.adapterInfoDialog.close();
                if (msg)
                    $.Notice.success(msg);
            };
            win.closeCustomizeDialog = function (msg) {
                master.adapterCustomize.close();
                if (msg)
                    $.Notice.success(msg);
            };
            /* *************************** 页面功能 **************************** */
            pageInit();

        });
    })(jQuery, window);

</script>