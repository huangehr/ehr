<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {
        $(function () {

            /* ************************** 变量定义 ******************************** */

            // 通用工具类库
            var Util = $.Util;

            // 页面主模块，对应于用户信息表区域
            var master = null;
            // 表单校验工具类
            var jValidation = $.jValidation;

            var retrieve = null;

            var systemDictInfoGrid = null;
            var systemDictEntryInfoGrid = null;

            var systemDictUpdateDialog = null;

            var addSystemDictEntityDialog = null;
            var updateSystemDictEntityDialog = null;

            var code = null;

            /* ************************** 变量定义结束 **************************** */

            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                resizeContent();
                retrieve.init();
                master.initSystemDict();
                master.initSystemDictForm();
            }
            function resizeContent(){
                var contentW = $('#grid_content').width();
                var leftW = $('.u-left').width();
                $('.u-right').width(contentW-leftW-20);
            }
            /* ************************** 函数定义结束 **************************** */

            /* *************************** 模块初始化 ***************************** */

            retrieve = {

//                // 模块对应的容器
//                $element: $('.m-retrieve-area'),
                // 搜索框
                $searchBox: $('#inp_search'),

                init: function () {
//                    this.$element.show();
                    this.$searchBox.ligerTextBox({
                        width: 240, isSearch: true, search: function () {
                            var searchNm = $("#inp_search").val();
                            master.searchSystemDict(searchNm);
                        }
                    });
                }
            };
            master = {
                // 新增按钮
                $form:$("#inp_form"),
                $newRecordBtn: $('#div_new_record'),
                $addSystemDict: $('#div_addSystemDict'),
                $updateSystemDictDialog: $('#div_updateSystemDictDialog'),
                $systemDictName: $('#inp_systemDictName'),
                $addSystemDictBtn: $('#div_addSystemDict_btn'),
                $systemDictReference: $("#inp_systemDictReference"),
                $systemDictId: $("#inp_systemDictId"),
                $addSystemDictEntity: $("#div_addSystemDictEntity"),
                $addSystemDictEntityDialog: $("#div_add_systemDictEntityDialog"),
                $updateSystemDictEntityDialog: $("#div_update_systemDictEntityDialog"),
                $systemDictEntityValue: $("#inp_systemDictEntity_value"),
                $systemDictEntityCode: $("#inp_systemDictEntity_code"),
                $systemDictEntitySort: $("#inp_systemDictEntity_sort"),
                $addSystemDictEntityBtn: $('#div_addSystemDictEntity_btn'),
                $updateSystemDictEntityBtn: $('#div_update_SystemDictEntity_btn'),
                $systemDictEntityCatalog: $("#inp_systemDictEntity_catalog"),
                $updateSystemDictEntityCode: $("#inp_update_systemDictEntity_code"),
                $updateSystemDictEntityValue: $("#inp_update_systemDictEntity_value"),
                $updateSystemDictEntitySort: $("#inp_update_systemDictEntity_sort"),
                $updateSystemDictEntityCatalog: $("#inp_update_systemDictEntity_catalog"),
                $systemNameCopy:$("#inp_systemNameCopy"),

                initSystemDictForm: function () {
                    var self = this;
                    self.$systemDictName.ligerTextBox({width: 240});
                    self.$systemDictEntityValue.ligerTextBox({width: 240});
                    self.$systemDictEntitySort.ligerTextBox({width: 240});
                    self.$systemDictEntityCatalog.ligerTextBox({width: 240});
                    self.$systemDictEntityCode.ligerTextBox({width: 240});
                    self.$updateSystemDictEntityCode.ligerTextBox({width: 240});
                    self.$updateSystemDictEntityValue.ligerTextBox({width: 240});
                    self.$updateSystemDictEntitySort.ligerTextBox({width: 240});
                    self.$updateSystemDictEntityCatalog.ligerTextBox({width: 240});

                    self.$systemDictReference.hide();
                },
                userInfoDialog: null,
                initSystemDict: function () {
                    systemDictInfoGrid = $("#div_SystemDict_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/dict/searchSysDicts',
                        parms: {
                            searchNm: '',
                            searchType: ''
                        },
                        columns: [
                            {display: 'dictId', name: 'id', hide: true},
                            {display: '字典名称', name: 'name', width: '70%', align: 'left'},
                            {
                                display: '操作', name: 'operator', width: '30%', render: function (row) {
								var html ='<div class="grid_edit"  style=""  title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "systemDict:systemInfoModifyDialog:update", row.id,row.name) + '"></div>'
										+'<div class="grid_delete"  style="" title="删除"' +
										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "systemDict:systemInfoModifyDialog:delete", row.id) + '"></div>';
//                                var html = '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "systemDict:systemInfoModifyDialog:update", row.id, row.name) + '">编辑</a>/' +
//                                        '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "systemDict:systemInfoModifyDialog:delete", row.id) + '">删除</a>';
                                return html;
                            }
                            }
                        ],
                        allowHideColumn:false,
                        validate: true,
                        unSetValidateAttr: false,
                        onSelectRow: function (data, rowindex, rowobj) {
                            master.initSystemDictEntity(data.id);
                            master.$systemDictId.val(data.id);
                        },
                        onAfterShowData: function () {
                            systemDictInfoGrid.select(0);
                        }

                    }));
                    // 自适应宽度
                    systemDictInfoGrid.adjustToWidth();
                    this.bindEvents();
                },
                // 重新查询
                searchSystemDict: function (searchNm) {
                    if (systemDictEntryInfoGrid!=null){
                        //debugger;//todo 清空字典项
                        master.initSystemDictEntity(-1);
                    }
                    systemDictInfoGrid.setOptions({parms: {searchNm: searchNm},newPage:1});
                    systemDictInfoGrid.loadData(true);
                },

                initSystemDictEntity: function (dictId) {
                    systemDictEntryInfoGrid = $("#div_systemEntity_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/dict/searchDictEntryList',
                        parms: {
                            dictId: dictId
                        },
                        columns: [
                            {display: 'sort', name: 'sort', hide: true},
                            {display: 'catalog', name: 'catalog', hide: true},
                            {display: '字典代码', name: 'code', width: '40%'},
                            {display: '值', name: 'value', width: '45%'},
                            {
                                display: '操作', name: 'operator', width: '15%', render: function (row) {
//								var html ='<div class="grid_edit"  style=""  title="修改" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}','{4}'])", "systemDictEntity:systemDictEntityInfoModifyDialog:update", row.code,row.value,row.sort,row.catalog) + '"></div>'
//										+'<div class="grid_delete"  style="" title="删除"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "systemDictEntity:systemDictEntityInfoModifyDialog:delete", row.code) + '"></div>';
                                var html = '<a class="grid_edit" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}','{3}','{4}'])", "systemDictEntity:systemDictEntityInfoModifyDialog:update", row.code, row.value, row.sort, row.catalog) + '"></a>' +
                                        '<a class="grid_delete" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "systemDictEntity:systemDictEntityInfoModifyDialog:delete", row.code) + '"></a> ';
                                return html;
                            }
                            }
                        ],
                        allowHideColumn:false,
                        validate: true
                    }));
                    // 自适应宽度
                    systemDictEntryInfoGrid.adjustToWidth();
                },

                bindEvents: function () {
                    //窗体改变大小事件
                    $(window).bind('resize', function() {
                        resizeContent();
                    });
                    var self = this;

                    //新增系统字典弹出窗口点击事件
                    self.$addSystemDict.click(function () {
                        self.$systemDictName.val("");
                        self.$systemDictId.val("");
                        self.updateSystemDialog("add");
                    });
                    //新增系统字典明细弹出窗口点击事件
                    self.$addSystemDictEntity.click(function () {
                        self.$systemDictEntityCode.val("");
                        self.$systemDictEntityValue.val("");
                        self.$systemDictEntitySort.val("");
                        self.$systemDictEntityCatalog.val("");
                        self.addSystemEntityDialog();
                    });

                    //修改系统字典弹出窗口点击事件
                    $.subscribe('systemDict:systemInfoModifyDialog:update', function (event, systemDictId, systemDictName) {
                        self.$systemDictName.val(systemDictName);
                        self.$systemDictId.val(systemDictId);
                        self.$systemNameCopy.val(systemDictName);
                        self.updateSystemDialog();
                    });
                    //删除系统字典弹出窗口点击事件
                    $.subscribe('systemDict:systemInfoModifyDialog:delete', function (event, systemDictId) {
                        self.deleteSystemDictDialog('Dict',systemDictId, '', '${contextRoot}/dict/deleteDict');
                    });
                    //删除系统字典 详情 弹出窗口点击事件
                    $.subscribe('systemDictEntity:systemDictEntityInfoModifyDialog:delete', function (event, code) {
                        var dictId = self.$systemDictId.val();
                        self.deleteSystemDictDialog('DictEntry',dictId, code, '${contextRoot}/dict/deleteDictEntry');
                    });

                    //修改系统字典 详情 弹出窗口点击事件
                    $.subscribe('systemDictEntity:systemDictEntityInfoModifyDialog:update', function (event, code, value, sort, catalog) {
                        self.$updateSystemDictEntityCode.val(code);
                        self.$updateSystemDictEntityValue.val(value);
                        self.$updateSystemDictEntitySort.val(sort);
                        self.$updateSystemDictEntityCatalog.val(catalog);
                        self.updateSystemEntityDialog();
                    });

                },
                updateSystemDialog: function (msg) {
                    var self = this;
                    var title = '';
                    if (Util.isStrEquals(msg, 'add')) {
                        title = '新增字典'
                    } else {
                        title = '修改字典'
                    }
                    systemDictUpdateDialog = $.ligerDialog.open({
                        title: title,
                        width: 416,
                        height: 200,
                        target: self.$updateSystemDictDialog
                    });
                    var validator = new jValidation.Validation(self.$updateSystemDictDialog, {immediate: true, onSubmit: false,onElementValidateForAjax:function(elm){
                        var systemNameCopy =  self.$systemNameCopy.val();
                        var systemName = self.$systemDictName.val();
                        var result = new jValidation.ajax.Result();
                        if(Util.isStrEquals(systemNameCopy,systemName)){
                            return true;
                        }
                        var dataModel = $.DataModel.init();
                        dataModel.updateRemote('${contextRoot}/dict/validator', {
                            data: {systemName: systemName},
                            async: false,
                            success: function (data) {
                                if (data.successFlg) {
                                    result.setResult(true);
                                } else {
                                    result.setResult(false);
                                    result.setErrorMsg("该字典名称已被使用");
                                }
                            }
                        });
                        return result;
                    }
                    });
                    //新增/修改 系统字典保存点击事件
                    this.$addSystemDictBtn.click( function () {
                        if (validator.validate()) {
                            var systemName = self.$systemDictName.val();
                            var reference = self.$systemDictReference.val();         // reference 参数预留
                            var systemDictId = self.$systemDictId.val();
                            var systemDictUpdateUrl = null;
                            var data = null;
                            if (Util.isStrEquals(systemDictId, "")) {
                                systemDictUpdateUrl = '${contextRoot}/dict/createDict';
                                data = {name: systemName, reference: ''};
                            } else {
                                systemDictUpdateUrl = '${contextRoot}/dict/updateDict';
                                data = {dictId: systemDictId, name: systemName};
                            }
                            var dataModel = $.DataModel.init();
                            dataModel.createRemote(systemDictUpdateUrl, {
                                data: data,
                                success: function (data) {
                                    if (data.successFlg) {
                                        $.Notice.success('更新成功');
                                        self.searchSystemDict();
                                    } else {
                                        $.Notice.error("更新失败");
                                    }
                                    systemDictUpdateDialog.close();
                                }
                            });
                        } else {
                            //return;
                        }
                    });
                    validator.reset();//还原
                },
                addSystemEntityDialog: function () {
                    var self = this;
                    addSystemDictEntityDialog = $.ligerDialog.open({
                        title: '新增字典详情',
                        width: 416,
                        height: 320,
                        target: self.$addSystemDictEntityDialog
                    });
                    //新增、修改字典详情验证
                    var validateEntityAdd = new jValidation.Validation(self.$addSystemDictEntityDialog, {immediate: true, onSubmit: false,
                        onElementValidateForAjax:function(elm){}});

                    //新增字典 详情 保存事件
                    this.$addSystemDictEntityBtn.click(function () {
                        if (validateEntityAdd.validate()) {
                            var code = self.$systemDictEntityCode.val();
                            var value = self.$systemDictEntityValue.val();
                            var sort = self.$systemDictEntitySort.val();
                            var catalog = self.$systemDictEntityCatalog.val();

                            var systemDictId = master.$systemDictId.val();
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote('${contextRoot}/dict/createDictEntry', {
                                data: {dictId: systemDictId, code: code, value: value, sort: sort, catalog: catalog},
                                success: function (data) {
                                    if (data.successFlg) {
                                        $.Notice.success('保存成功');
                                        //self.searchSystemDict();
                                        master.initSystemDictEntity(master.$systemDictId.val());
                                    } else {
                                        $.Notice.error(data.errorMsg);
                                    }
                                    addSystemDictEntityDialog.close();
                                }
                            });
                        }
                    });
                    validateEntityAdd.reset();//还原
                },
                updateSystemEntityDialog: function () {
                    var self = this;
                    updateSystemDictEntityDialog = $.ligerDialog.open({
                        title: '修改字典详情',
                        width: 416,
                        height: 320,
                        target: self.$updateSystemDictEntityDialog
                    });
                    var validateEntityUpdate = new jValidation.Validation(self.$updateSystemDictEntityDialog, {immediate: true, onSubmit: false,
                        onElementValidateForAjax:function(elm){}});
                    //修改字典 详情 保存事件
                    this.$updateSystemDictEntityBtn.click(function ()  {
                        if (validateEntityUpdate.validate()) {
                            var code = self.$updateSystemDictEntityCode.val();
                            var value = self.$updateSystemDictEntityValue.val();
                            var sort = self.$updateSystemDictEntitySort.val();
                            var catalog = self.$updateSystemDictEntityCatalog.val();

                            var systemDictId = master.$systemDictId.val();
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote('${contextRoot}/dict/updateDictEntry', {
                                data: {dictId: systemDictId, code: code, value: value, sort: sort, catalog: catalog},
                                success: function (data) {
                                    if (data.successFlg) {
                                        $.Notice.success('更新成功');
                                        //self.searchSystemDict();
                                        master.initSystemDictEntity(master.$systemDictId.val());
                                    } else {
                                        $.Notice.error('更新失败');
                                    }
                                    updateSystemDictEntityDialog.close();
                                }
                            });
                        }
                    });
                    validateEntityUpdate.reset();//还原
                },
                deleteSystemDictDialog: function (type,dictId, code, deleteUrl) {
                    var self = this;
                    var data = '';
                    if (Util.isStrEquals(code, '')) {
                        data = {dictId: dictId}
                    } else {
                        data = {dictId: dictId, code: code}
                    }
                    $.ligerDialog.confirm('确认删除该行信息？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
                        if (yes) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote(deleteUrl, {
                                data: data,
                                success: function (data) {
                                    if (data.successFlg) {
                                        $.Notice.success('删除成功');
                                        if (type=='Dict') {
                                            self.searchSystemDict();
                                        }else if (type=='DictEntry') {
                                            master.initSystemDictEntity(master.$systemDictId.val());
                                        }

                                    } else {
                                        $.Notice.error('删除失败');
                                    }
                                }
                            });
                        }
                    });
                }
            };
            /* ************************* 模块初始化结束 ************************** */
            /* *************************** 页面初始化 **************************** */
            pageInit();
            /* ************************* 页面初始化结束 ************************** */
        });
    })(jQuery, window);
</script>