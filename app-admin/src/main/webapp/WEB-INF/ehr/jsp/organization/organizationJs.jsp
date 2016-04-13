<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {
        $(function () {

            /* ************************** 变量定义 ******************************** */
            // 通用工具类库
            var Util = $.Util;
            var retrieve = null;
            var master = null;

            var settledWayDictId = 8;
            var orgTypeDictId = 7;

            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                retrieve.init();
                master.init();
            }

            function reloadGrid (url, params) {
                this.grid.setOptions({parms: params,newPage:1});
                this.grid.loadData(true);
            }

            /* *************************** 模块初始化 ***************************** */
            retrieve = {
                $element: $('.m-retrieve-area'),
                $searchNm: $('#inp_search'),
                $settledWay: $('#inp_settledWay'),
                $orgType:$('#inp_orgType'),
                $searchBtn: $('#btn_search'),
                $newRecordBtn: $('#div_new_record'),
                $location:$('#inp_orgArea'),

                addOrgInfoDialog:null,

                init: function () {
                    this.initDDL(settledWayDictId, this.$settledWay);
                    this.initDDL(orgTypeDictId,this.$orgType);

                    this.$searchNm.ligerTextBox({width: 240});

                    this.$location.addressDropdown({tabsData:[
                        {name: '省份', code:'id',value:'name',url: '${contextRoot}/address/getParent', params: {level:'1'}},
                        {name: '城市', code:'id',value:'name',url: '${contextRoot}/address/getChildByParent'},
                        {name: '县区', code:'id',value:'name',url: '${contextRoot}/address/getChildByParent'}
                    ]});

                    this.bindEvents();

                    this.$element.show();
                    this.$element.attrScan();
                    window.form = this.$element;
                },
                initDDL: function (dictId, target) {
                    var target = $(target);
                    var dataModel = $.DataModel.init();

                    dataModel.fetchRemote("${contextRoot}/dict/searchDictEntryList",{data:{dictId: dictId},
                        success: function(data) {
                            target.ligerComboBox({
                                valueField: 'code',
                                textField: 'value',
                                width:'180',
                                data: [].concat(data.detailModelList)
                            });
                        }});
                },
                bindEvents: function () {
                    var self = this;
                    self.$searchBtn.click(function () {
                        master.grid.options.newPage =  1;
                        master.reloadGrid();
                    });
                    self.$newRecordBtn.click(function () {
                        self.addOrgInfoDialog = $.ligerDialog.open({
                            height: 640,
                            width: 600,
                            title: '新增机构信息',
                            url: '${contextRoot}/organization/dialog/create'
                        })
                    });
                }
            };
            master = {
                orgInfoDialog: null,
                grid: null,
                init: function () {
                    this.grid = $("#div_org_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/organization/searchOrgs',
                        parms: {
                            searchNm:'',
                            searchType:'',
                            orgType:'',
                            province:'',
                            city:'',
                            district:''
                        },
                        columns: [
                            {display:'机构类型',name:'orgTypeName', width: '8%', align:"left"},
                            {display:'机构代码',name:'orgCode', width: '9%', align:"left"},
                            {display:'机构全名',name:'fullName', width: '15%', align:"left"},
                            {display:'联系人',name:'admin', width: '8%', align:"left"},
                            {display:'联系方式',name:'tel', width: '8%', align:"left"},
                            {display:'机构地址',name:'locationStrName', width: '20%', align:"left"},
                            {display:'是否激活',name:'activityFlagName',width: '8%',isAllowHide: false,render:function(row){
								var html = '';
								if(row.activityFlag == 1){
//									html +='<div class="grid_off" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:activityFlg", row.orgCode,'1') + '"></div>';
									html +='<a class="grid_off" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:activityFlg", row.orgCode,'1') + '"></a>';

								}else{
//									html +='<div class="grid_on" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:activityFlg", row.orgCode,'0') + '"></div>';
									html +='<a class="grid_on" title="" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:activityFlg", row.orgCode,'0') + '"></a>';

								}
								return html;
							}},
                            {display:'入驻方式',name:'settledWayName',width: '10%',isAllowHide: false},
                            {display:'操作', name: 'operator', width: '14%', render: function (row) {
								var html = '';

//								html +='<div class="grid_edit"  style="" title="编辑" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:modify", row.orgCode,'modify') + '"></div>'
//										+'<div class="grid_delete"  style="" title="删除"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:del", row.orgCode,'del') + '"></div>';
								html += '<a class="label_a" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:modify", row.orgCode,'modify') + '">模板配置</a>';
								html += '<a class="grid_edit" style="margin-left:10px;" title="编辑" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:modify", row.orgCode,'modify') + '"></a>';
                                html += '<a class="grid_delete" style="margin-left:0px;" title="删除" href="javascript:void(0)"  onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "org:orgInfoDialog:del", row.orgCode, 'del') + '"></a>';
                                return html;
                            }},
                            {name:'activityFlag',hide:true, align:"center"},
                            {name:'settledWay',hide:true,  align:"center"}
                        ],
                        enabledEdit: true,
                        validate : true,
                        unSetValidateAttr:false,
                        onDblClickRow : function (row){
                            var mode = 'view';
                            this.orgInfoDialog = $.ligerDialog.open({
                                height: 700,
                                width: 600,
                                title : '机构基本信息',
                                url: '${contextRoot}/organization/dialog/orgInfo',
                                load: true,
                                urlParms: {
                                    orgCode: encodeURIComponent(row.orgCode),
                                    mode:mode
                                }
                            });
                        }
                    }));
                    // 自适应宽度
                    this.grid.adjustToWidth();
                    this.bindEvents();
                },
                activity: function (orgCode,activityFlag) {
                    var dataModel = $.DataModel.init();
                    dataModel.createRemote('${contextRoot}/organization/activity',{
                        data: {orgCode:orgCode,activityFlag:activityFlag},
                        success:function(data){
                            if (data.successFlg) {
                                master.reloadGrid();
                            }
                        }
                    });
                },
                reloadGrid: function () {
                    //var values = retrieve.$element.Fields.getValues();
                    retrieve.$element.attrScan();
                    var orgAddress = retrieve.$element.Fields.location.getValue();
                    var values = $.extend({},retrieve.$element.Fields.getValues(),
                            {province: (orgAddress.names[0]==null?'':orgAddress.names[0])},
                            {city:  (orgAddress.names[1]==null?'':orgAddress.names[1])},
                            {district: (orgAddress.names[2]==null?'':orgAddress.names[2])});

                    reloadGrid.call(this, '${contextRoot}/organization/searchOrgs', values);
                },
                delRecord:function(orgCode){
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/organization/deleteOrg",{data:{orgCode:orgCode},
                        success: function(data) {
                            if(data.successFlg){
                                $.Notice.success('操作成功。');
                                master.reloadGrid();
                            }else{
                                $.Notice.error('删除失败。');
                            }
                        }});
                },
                bindEvents: function () {
                    var self = this;
                    $.subscribe('org:orgInfoDialog:modify', function (event, orgCode, mode) {
                        var title = '修改机构信息';
                            self.orgInfoDialog = $.ligerDialog.open({
                            isHidden: false,
                            height: 650,
                            width: 600,
                            title:title,
                            url: '${contextRoot}/organization/dialog/orgInfo',
                            load: true,
                            urlParms: {
                                orgCode: encodeURIComponent(orgCode),
                                mode:mode
                            }
                        });
                    });
                    $.subscribe('org:orgInfoDialog:activityFlg', function (event, orgCode, activityFlg) {
                        $.ligerDialog.confirm('确认修改该行信息？<br>如果是请点击确认按钮，否则请点击取消。',function(yes){
                            if(yes){
                                self.activity(orgCode,activityFlg);
                            }
                        });

                    });
                    $.subscribe('org:orgInfoDialog:del', function (event, orgCode, activityFlg) {
                        $.ligerDialog.confirm('确认删除该行信息？<br>如果是请点击确认按钮，否则请点击取消。',function(yes){
                            if(yes){
                                self.delRecord(orgCode);
                            }
                        });

                    })
                }
            };

            /* ************************* Dialog页面回调接口 ************************** */
            win.reloadMasterGrid = function () {
                master.reloadGrid();
            };
            win.closeDialog = function () {
                master.orgInfoDialog.close();
            };

            win.closeAddOrgInfoDialog = function (callback) {
                if(callback){
                    callback.call(win);
                    master.reloadGrid();
                }
                retrieve.addOrgInfoDialog.close();
            };

            /* *************************** 页面初始化 **************************** */
            pageInit();
        });
    })(jQuery, window);
</script>