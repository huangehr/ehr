<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {

        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;

        // 页面主模块，对应于用户信息表区域
        var patientInfo = null;

        var cardInfoGrid = null;

        var cardFormInit = null;
        var patientModel =${patientModel}.obj;
        var idCardNo =patientModel.idCardNo;
        /* ************************** 变量定义结束 ******************************** */

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            patientInfo.init();
        }
        function cardInfoRefresh(){
            var searchNm = patientInfo.$cardSearch.val();
            var cardType = patientInfo.$selectCardType.ligerComboBox().getValue();
            cardFormInit.searchCard(searchNm,cardType);
        }

        /* *************************** 函数定义结束******************************* */

        /* *************************** 模块初始化 ***************************** */
        patientInfo = {
            $form: $("#div_patient_info_form"),
            $cardInfo:$("#div_card_info"),
            $cardForm: $("#div_card_info_form"),
            $recordForm: $("#div_record_info_form"),

            $realName: $("#inp_realName"),
            $idCardNo: $("#inp_idCardNo"),
            $gender: $('input[name="gender"]', this.$form),
            $patientNation: $("#inp_patientNation"),
            $patientNativePlace: $("#inp_patientNativePlace"),
            $patientMartialStatus: $("#inp_select_patientMartialStatus"),
            $patientBirthday: $("#inp_patientBirthday"),
            $birthPlace: $("#inp_birthPlace"),
            $homeAddress: $("#inp_homeAddress"),
            $workAddress: $("#inp_workAddress"),
            $residenceType: $('input[name="residenceType"]', this.$form),
            $patientTel: $("#inp_patientTel"),
            $patientEmail: $("#inp_patientEmail"),
            $patientBasicMsgDialog: $("#div_patientBasicMsgDialog"),
            $cardManagerDialog: $("#div_cardManagerDialog"),
            $recordManagerDialog: $("#div_recordManagerDialog"),
            $cardSearch: $("#inp_card_search"),
            $selectCardType:$('#inp_select_cardType'),
            $cardBasicMsg: $("#div_card_basicMsg"),
            $addCard: $("#div_addCard"),

            $cardType:$("#inp_cardType"),
            $cardNo:$("#inp_cardNo"),
            $holderName:$("#inp_HolderName"),
            $issueAddress:$("#inp_issueAddress"),
            $issueOrg:$("#inp_issueOrg"),
            $addDate:$("#inp_addDate"),
            $cardStatus:$("#inp_cardStatus"),
            $cardExplain:$("#inp_cardExplain"),
            picPath:$('#div_file_list'),

            init: function () {
                this.initForm();
                cardFormInit.bindEvents();
                cardFormInit.init();
            },
            initForm: function () {
                this.$realName.ligerTextBox({width: 240});
                this.$idCardNo.ligerTextBox({width: 240});
                this.$gender.ligerRadio();
                //this.$patientNation.ligerTextBox({width: 240});
                this.$patientNativePlace.ligerTextBox({width: 240});
                this.$patientBirthday.ligerDateEditor({format: "yyyy-MM-dd"});
                this.$birthPlace.ligerTextBox({width: 240});
                this.$homeAddress.ligerTextBox({width: 240});
                this.$workAddress.ligerTextBox({width: 240});
                this.$residenceType.ligerRadio();
                this.$patientTel.ligerTextBox({width: 240});
                this.$patientEmail.ligerTextBox({width: 240});
                //this.$patientMartialStatus.ligerTextBox({width: 240});
                this.$patientMartialStatus.ligerComboBox(
                        {
                            url: '${contextRoot}/dict/searchDictEntryList',
                            valueField: 'code',
                            textField: 'value',
                            dataParmName: 'detailModelList',
                            urlParms: {
                                dictId: 4
                            }
                        });
                this.$patientNation.ligerComboBox(
                        {
                            url: '${contextRoot}/dict/searchDictEntryList',
                            valueField: 'code',
                            textField: 'value',
                            dataParmName: 'detailModelList',
                            urlParms: {
                                dictId: 5
                            }
                        });

                this.$cardSearch.ligerTextBox({
                    width: 240, isSearch: true, search: function () {
                        cardInfoRefresh();
//                        var searchNm = patientInfo.$cardSearch.val();
//                        var cardType = patientInfo.$selectCardType.ligerComboBox().getValue();
//                        cardFormInit.searchCard(searchNm,cardType);
                    }
                });

                this.$selectCardType.ligerComboBox(
                        {
                            url: '${contextRoot}/dict/searchDictEntryList',
                            valueField: 'code',
                            textField: 'value',
                            dataParmName: 'detailModelList',
                            urlParms: {
                                dictId: 10
                            },
                            width:120,
                            autocomplete: true,
                            onSelected: function (v, t) {
                                cardFormInit.searchCard(patientInfo.$cardSearch.val(), v);
                            }
                        });

                this.$cardType.ligerTextBox({width: 240});
                this.$cardNo.ligerTextBox({width: 240});
                this.$holderName.ligerTextBox({width: 240});
                this.$issueAddress.ligerTextBox({width: 240});
                this.$issueOrg.ligerTextBox({width: 240});
                this.$addDate.ligerTextBox({width: 240});
                this.$cardStatus.ligerTextBox({width: 240});
                this.$cardExplain.ligerTextBox({width: 240});

                this.$form.attrScan();
                this.$form.Fields.fillValues({
                    name: patientModel.name,
                    idCardNo: idCardNo,
                    gender: patientModel.gender,
                    nation: patientModel.nation,
                    nativePlace: patientModel.nativePlace,
                    martialStatus: patientModel.martialStatus,
                    birthday: patientModel.birthday,
                    birthPlaceFull: patientModel.birthPlaceFull,
                    homeAddressFull: patientModel.homeAddressFull,
                    workAddressFull: patientModel.workAddressFull,
                    residenceType: patientModel.residenceType,
                    telephoneNo: patientModel.telephoneNo,
                    email: patientModel.email
                });
                this.$cardForm.attrScan();
                var pic = patientModel.localPath;
                if(!(Util.isStrEquals(pic,null)||Util.isStrEquals(pic,""))){
                    this.picPath.html('<img src="${contextRoot}/patient/showImage?localImgPath='+pic+'" class="f-w88 f-h110" ></img>');
                }
            }
        };
        cardFormInit = {
            init: function () {
                cardInfoGrid = $("#div_card_info_form").ligerGrid($.LigerGridEx.config({
                    url: '${contextRoot}/card/searchCard',
                    parms: {
                        idCardNo: idCardNo,
                        searchNm: '',
                        cardType: ''
                    },
                    columns: [
                        { name: 'objectId',hide: true},
                        { name: 'type',hide: true},
                        {display: '类型', name: 'typeValue', width: '10%'},
                        {display: '卡号', name: 'number', width: '30%'},
                        {display: '发行机构', name: 'releaseOrg', width: '20%'},
                        {display: '创建时间', name: 'createDate', width: '18%'},
                        {display: '状态', name: 'statusValue', width: '8%'},
                        {
                            display: '操作', name: 'operator', width: '14%', render: function (row) {
                            var html = '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "patient:cardInfoModifyDialog:open", row.objectId,row.type) + '">解除关联</a>  ';
                            return html;
                        }
                        }
                    ],
                    inWindow: false,
                    height:400,
                    onDblClickRow: function (row) {
                        //查看卡信息
                        $.ligerDialog.open({ width:450, height:500,target: patientInfo.$cardBasicMsg});
                        var self = this;
                        var dataModel = $.DataModel.init();
                        dataModel.createRemote('${contextRoot}/card/getCard', {
                            data: {objectId: row.objectId,type:row.type},
                            success: function (data) {
                                patientInfo.$cardForm.Fields.fillValues({
                                    type:data.obj.cardModel.typeValue,
                                    number:data.obj.cardModel.number,
                                    ownerName:data.obj.cardModel.ownerName,
                                    local:data.obj.cardModel.local,
                                    releaseOrg:data.obj.cardModel.releaseOrg,
                                    createDate:data.obj.cardModel.createDate,
                                    statusValue:data.obj.cardModel.statusValue,
                                    description:data.obj.cardModel.description
                                })
                            }
                        });
                    }
                }));
                cardInfoGrid.adjustToWidth();
            },

            searchCard: function (searchNm, cardType) {
                cardInfoGrid.setOptions({parms: {searchNm: searchNm, idCardNo: idCardNo, cardType: cardType},newPage:1});
                cardInfoGrid.loadData(true);
            },
            bindEvents: function () {
                var self = patientInfo;
                self.$recordForm.hide();

                self.$patientBasicMsgDialog.click(function () {
                    self.$form.show();
                    self.$cardInfo.css('visibility','hidden');
                    self.$recordForm.hide();
                });
                self.$cardManagerDialog.click(function () {
                    self.$cardInfo.css('visibility','visible');
                    self.$recordForm.hide();
                    self.$form.hide();
                });

                self.$recordManagerDialog.click(function () {
                    self.$recordForm.show();
                    self.$cardInfo.css('visibility','hidden');
                    self.$form.hide();
                });

                //解绑卡信息
                $.subscribe('patient:cardInfoModifyDialog:open',function(event,objectId,type){
                    $.ligerDialog.confirm('确认解除关联该卡信息？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
                        if (yes) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote('${contextRoot}/card/detachCard', {
                                data: {objectId: objectId,type:type},
                                success: function (data) {
                                    if (data.successFlg) {
                                        $.ligerDialog.alert('解除关联成功');
                                        cardFormInit.searchCard();
                                    } else {
                                        $.Notice.error('解除关联失败');
                                    }
                                }
                            });
                        }
                    })
                });
                //添加卡
                patientInfo.$addCard.click(function(){
                    debugger;
                    var idCardNo = patientInfo.$form.Fields.idCardNo.getValue();
                    $.ligerDialog.open({
                        height: 640,
                        width: 600,
                        title: '新增卡',
                        url: '${contextRoot}/card/addCardInfoDialog',
                        urlParms: {
                            idCardNo: idCardNo
                        },
                        onClosed: function () {
                            cardInfoRefresh();
                        }
                    })
                })
            }

        };

        /* *************************** 模块初始化结束 ***************************** */

        /* *************************** 页面初始化 **************************** */
        pageInit();
        /* *************************** 页面初始化结束 **************************** */

    })(jQuery, window);
</script>