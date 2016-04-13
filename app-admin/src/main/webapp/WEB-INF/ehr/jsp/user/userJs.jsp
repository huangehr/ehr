<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {
        $(function () {
            /* ************************** 变量定义 ******************************** */
            // 通用工具类库
            var Util = $.Util;
            // 页面表格条件部模块
            var retrieve = null;
            // 页面主模块，对应于用户信息表区域
            var master = null;
            // 画面用户信息表对象
            var userInfoGrid = null;
            // 用户类型字典Id
            var settledWayDictId = 15;

            var isFirstPage = true;

            /* *************************** 函数定义 ******************************* */
            /**
             * 页面初始化。
             * @type {Function}
             */
            function pageInit() {
                retrieve.init();
                master.init();
            }
            //多条件查询参数设置
            function reloadGrid (url, params) {
                if (isFirstPage){
                    userInfoGrid.options.newPage = 1;
                }
                userInfoGrid.set({
                    url: url,
                    parms: params
                });

                userInfoGrid.reload();
                isFirstPage = true;
            }

            /* *************************** 模块初始化 ***************************** */
            retrieve = {
                // 模块对应的容器
                $element: $('.m-retrieve-area'),
                // 搜索框
                $searchBox: $('#inp_search'),
                // 新增按钮
                $newRecordBtn: $('#div_new_record'),
                //用户类型查询下拉框
                $searchType: $('#inp_select_searchType'),
                //查询按钮
                $searchBtn: $('#btn_search'),
                init: function () {
                    retrieve.initDDL(settledWayDictId,this.$searchType);
                    this.$element.show();
                    this.$element.attrScan();
                    window.form = this.$element;
                    this.$searchBox.ligerTextBox({width:240});
                },
                //下拉框列表项初始化
                initDDL: function (dictId, target) {
                    var target = $(target);
                    var dataModel = $.DataModel.init();
                    dataModel.fetchRemote("${contextRoot}/dict/searchDictEntryList",{data:{dictId: dictId},
                        success: function(data) {
                            target.ligerComboBox({
                                valueField: 'code',
                                textField: 'value',
                                data: [].concat(data.detailModelList)
                            });
                        }
                    });
                }
            };
            master = {
                userInfoDialog: null,
                addUserInfoDialog:null,
                init: function () {
                    userInfoGrid = $("#div_user_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/user/searchUsers',
                        // 传给服务器的ajax 参数
                        parms: {
                            searchNm: '',
                            searchType: ''
                        },
                        columns: [
                            // 隐藏列：hide: true（隐藏），isAllowHide: false（列名右击菜单中不显示）
                            {name: 'id', hide: true, isAllowHide: false},
                            {display: '用户类型', name: 'userTypeName', width: '10%',align:'left'},
                            {display: '姓名', name: 'realName', width: '8%',align:'left'},
                            {display: '账号',name: 'loginCode', width:'12%', isAllowHide: false,align:'left'},
                            {display: '所属机构', name: 'organizationName', width: '17%',align:'left'},
                            {display: '联系方式', name: 'telephone',width: '12%',align:'left'},
                            {display: '用户邮箱', name: 'email', width: '12%', resizable: true,align:'left'},
                            {display: '是否生/失效', name: 'activated', width: '8%', minColumnWidth: 20,render:function(row){
								var html ='';
								if(Util.isStrEquals(row.activated,true)){
//										html +='<div class="grid_on"  onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,0) + '"></div>';
									html+= '<a class="grid_on" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,0) + '"></a>';
								}else if(Util.isStrEquals(row.activated,false)){
//										html +='<div class="grid_off" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,1) + '"></div>';
									html+='<a class="grid_off" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:failure", row.id,1) + '"></a>';
								}
								return html;
                            }},
							{display: '最近登录时间', name: 'lastLoginTime', width: '12%',align:'left'},
                            {
                                display: '操作', name: 'operator', width: '10%', render: function (row) {
//								var html ='<div class="grid_edit"    title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:open", row.id,'modify') + '"></div>'
//										+'<div class="grid_delete"   title="删除"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoDialog:del", row.id,'delete') + '"></div>';
                                var html = '<a class="grid_edit" style="" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoModifyDialog:open", row.id, 'modify') + '"></a>';
                                    html+= '<a class="grid_delete" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "user:userInfoDialog:del", row.id, 'delete') + '"></a>';
                                return html;
							}}
                        ],
                        enabledEdit: true,
                        validate: true,
                        unSetValidateAttr: false,
                        onDblClickRow : function (row){
                            var mode = 'view';
                            $.ligerDialog.open({
                                height: 750,
                                width: 600,
                                isDrag:true,
                                //isResize:true,
                                title:'用户基本信息',
                                url: '${contextRoot}/user/getUser',
                                load: true,
                                urlParms: {
                                    userId: row.id,
                                    mode:mode
                                }
                            });
                        }
                    }));
                    // 自适应宽度
                    userInfoGrid.adjustToWidth();

                    this.bindEvents();
                },
                reloadGrid: function () {
                    var values = retrieve.$element.Fields.getValues();
                    reloadGrid.call(this, '${contextRoot}/user/searchUsers', values);
                },
                bindEvents: function () {
                    var self = this;
                    //查询事件
                    retrieve.$searchBtn.click(function(){
                            master.reloadGrid();
                    });
                    //修改用户信息
                    $.subscribe('user:userInfoModifyDialog:open', function (event, userId, mode) {
                        self.userInfoDialog = $.ligerDialog.open({
                            //  关闭对话框时销毁对话框
                            isHidden: false,
                            title:'修改基本信息',
                            height: 650,
                            width: 600,
                            isDrag:true,
                            isResize:true,
                            url: '${contextRoot}/user/getUser',
                            load: true,
                            urlParms: {
                                userId: userId,
                                mode:mode
                            }
                        });
                    });
                    //新增用户
                    retrieve.$newRecordBtn.click(function () {
                        self.addUserInfoDialog = $.ligerDialog.open({
                            height: 620,
                            width: 600,
                            title: '新增用户信息',
                            url: '${contextRoot}/user/addUserInfoDialog?'+ $.now()
                        })
                    });
                    //修改用户状态
                    $.subscribe('user:userInfoModifyDialog:failure', function (event, userId,activated) {
                        $.ligerDialog.confirm('确认要修改该行信息？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
                            if (yes) {
                                var dataModel = $.DataModel.init();
                                dataModel.updateRemote('${contextRoot}/user/activityUser', {
                                    data: {userId: userId,activated:activated},
                                    success: function (data) {
                                        if (data.successFlg) {
                                            $.Notice.success('修改成功');
                                            isFirstPage = false;
                                            master.reloadGrid();
                                        } else {
                                            $.Notice.error('修改失败');
                                        }
                                    }
                                });
                            }
                        });
                    });
                    //删除用户
                    $.subscribe('user:userInfoDialog:del', function (event, userId, activityFlg) {
                        $.ligerDialog.confirm('确认删除该行信息？<br>如果是请点击确认按钮，否则请点击取消。',function(yes){
                            if(yes){
                                var dataModel = $.DataModel.init();
                                dataModel.updateRemote("${contextRoot}/user/deleteUser",{
                                    data:{userId:userId},
                                    async:true,
                                    success: function(data) {
                                        if(data.successFlg){
                                            $.Notice.success('删除成功。');
                                            isFirstPage = false;
                                            master.reloadGrid();
                                        }else{
                                            $.Notice.error('删除失败。');
                                        }
                                    }
                                });
                            }
                        });

                    })

                }
            };

            /* ************************* 模块初始化结束 ************************** */

            /* ************************* Dialog页面回调接口 ************************** */
            win.reloadMasterUpdateGrid = function () {
                master.reloadGrid();
            };
            win.closeUserInfoDialog = function (callback) {
                isFirstPage = false;
                if(callback){
                    callback.call(win);
                    master.reloadGrid();
                }
                master.userInfoDialog.close();
            };
            win.closeAddUserInfoDialog = function (callback) {
                isFirstPage = false;
                if(callback){
                    callback.call(win);
                    master.reloadGrid();
                }
                master.addUserInfoDialog.close();
            };



            /* ************************* Dialog页面回调接口结束 ************************** */

            /* *************************** 页面初始化 **************************** */

            pageInit();

            /* ************************* 页面初始化结束 ************************** */
        });
    })(jQuery, window);
</script>