<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {
        $(function () {

            /* ************************** 变量定义 ******************************** */
            // 通用工具类库
            var Util = $.Util;

            // 画面用户信息表对象
            var grid = null;

            // 页面表格条件部模块
            var patientRetrieve = null;

            // 页面主模块，对应于用户信息表区域
            var patientMaster = null;
            /* ************************** 变量定义结束 ******************************** */

            /* *************************** 函数定义 ******************************* */
            function pageInit() {
                patientRetrieve.init();
                patientMaster.init();
            }

            function reloadGrid (url, params) {
                grid.set({
                    url: url,
                    parms: params,
                    newPage:1
                });
                grid.reload();
            }
            /* *************************** 函数定义结束******************************* */

            patientRetrieve = {
                $element: $(".m-retrieve-area"),
                $searchBtn: $('#btn_search'),
                $searchPatient: $("#inp_search"),
                $homeAddress: $("#search_homeAddress"),
                $newPatient: $("#div_new_patient"),
                init: function () {
                    this.$element.show();
                    this.$homeAddress.addressDropdown({tabsData:[
                        {name: '省份',code:'id',value:'name', url: '${contextRoot}/address/getParent', params: {level:'1'}},
                        {name: '城市',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                        {name: '县区',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'}
                    ]});
                    this.$searchPatient.ligerTextBox({width: 240 });
                    this.bindEvents();
                },
                bindEvents: function () {
                    var self = this;
                    self.$searchBtn.click(function () {
                        patientMaster.reloadGrid();
                    });
                }
            };

            patientMaster = {
                init: function () {
                   grid = $("#div_patient_info_grid").ligerGrid($.LigerGridEx.config({
                        url: '${contextRoot}/patient/searchPatient',
                        // 传给服务器的ajax 参数
                        pageSize:20,
                        parms: {
                            searchNm: '',
                            searchType: '',
                            province:'',
                            city:'',
                            district:''
                        },
                        columns: [
                            {display: '姓名', name: 'name', width: '15%',align: 'left'},
                            {display: '身份证号', name: 'idCardNo', width: '20%', align: 'left'},
                            {display: '性别', name: 'gender', width: '10%'},
                            {display: '联系方式', name: 'telephoneNo', width: '15%', resizable: true,align: 'left'},
                            {display: '家庭地址', name: 'homeAddress', width: '30%', minColumnWidth: 20,align: 'left'},
                            {
                                display: '操作', name: 'operator', width: '10%', render: function (row) {
//									var html ='<div class="grid_edit" title="修改" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "patient:patientInfoModifyDialog:open", row.idCardNo) + '"></div>'
//										+'<div class="grid_delete" title="删除"' +
//										' onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "patient:patientInfoModifyDialog:delete", row.idCardNo) + '"></div>';
                                var html = '<a class="grid_edit" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "patient:patientInfoModifyDialog:open", row.idCardNo) + '"></a> ';
                                html += '<a class="grid_delete" href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}'])", "patient:patientInfoModifyDialog:delete", row.idCardNo) + '"></a>';
									return html;
								}
                            }
                        ],
                        onDblClickRow: function (row) {
                            $.ligerDialog.open({
                                title:'<button class="f-ib f-tac f-w100 f-click-down" id="div_patientBasicMsgDialog">病人基本信息</button><button class="f-ib f-ml10 f-tac f-w100"  id="div_cardManagerDialog">卡管理</button>',
                                height: 600,
                                width: 570,
                                url: '${contextRoot}/patient/patientDialogType',
                                load: true,
                                isDrag:true,
                                urlParms: {
                                    idCardNo: row.idCardNo,
                                    patientDialogType: 'patientInfoMessage'
                                },
                                isHidden: false
                            });
                        }
                    }));
                    grid.adjustToWidth();
                    this.bindEvents();
                },
                /*searchPatient: function (searchNm) {
                    grid.setOptions({parms: {searchNm: searchNm}});
                    grid.loadData(true);
                },*/
                reloadGrid: function () {
                    //var values = retrieve.$element.Fields.getValues();
                    patientRetrieve.$element.attrScan();
                    var homeAddress = patientRetrieve.$element.Fields.homeAddress.getValue();
                    var values = $.extend({},patientRetrieve.$element.Fields.getValues(),
                            {province: (homeAddress.names[0]==null?'':homeAddress.names[0])},
                            {city:  (homeAddress.names[1]==null?'':homeAddress.names[1])},
                            {district: (homeAddress.names[2]==null?'':homeAddress.names[2])});

                    reloadGrid.call(this, '${contextRoot}/patient/searchPatient', values);
                },
                bindEvents: function () {

                    //新增人口信息
                    patientRetrieve.$newPatient.click(function(){
                        $.ligerDialog.open({
                            isHidden:true,
                            title:'新增人口信息',
                            width:600,
                            height:600,
                            url:'${contextRoot}/patient/patientDialogType',
                            urlParms:{
                                patientDialogType:'addPatient'
                            }
                        })
                    });
                    //修改人口信息
                  $.subscribe('patient:patientInfoModifyDialog:open',function(event,idCardNo){
                        $.ligerDialog.open({
                            isHidden:true,
                            title:'修改人口信息',
                            width:600,
                            height:700,
                            url:'${contextRoot}/patient/patientDialogType',
                            urlParms:{
                                idCardNo:idCardNo,
                                patientDialogType:'updatePatient'
                            }
                        })
                    });
                    //删除人口信息
                    $.subscribe('patient:patientInfoModifyDialog:delete', function (event, idCardNo) {
                        $.ligerDialog.confirm('确认删除该行信息？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
                            if (yes) {
                                var dataModel = $.DataModel.init();
                                dataModel.updateRemote('${contextRoot}/patient/deletePatient', {
                                    data: {idCardNo: idCardNo},
                                    success: function (data) {
                                        if (data.successFlg) {
                                            //$.Notice.open({type: 'success', msg: '删除成功！'});
                                            $.Notice.success('操作成功。');
                                            patientMaster.reloadGrid();
                                        } else {
                                            $.Notice.open({type: 'error', msg: '操作失败。'});
                                        }
                                    }
                                });
                            }
                        });
                    })
                }
            };

            /* ************************* Dialog页面回调接口 ************************** */
            win.patientDialogRefresh = function () {
                patientMaster.reloadGrid();
            };
            /* ************************* Dialog页面回调接口结束 ************************** */
            /* *************************** 页面初始化 **************************** */
            pageInit();
            /* *************************** 页面初始化结束 **************************** */

        });
    })(jQuery, window);
</script>