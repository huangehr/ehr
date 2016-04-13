<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var organizationInfo = null;
        var orgModel = null;

        var settledWayDictId = 8;
        var orgTypeDictId =  7;

        // 表单校验工具类
        var jValidation = $.jValidation;

        //公钥管理弹框
        var publicKeyMsgDialog = null;
		var envelop = JSON.parse('${envelop}');
		var org = envelop.obj;

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            organizationInfo.init();
        }

        /* *************************** 模块初始化 ***************************** */
        organizationInfo = {
            //form
            $form: $("#div_organization_info_form"),

            $orgCode: $("#org_code"),
            $fullName: $('#full_name'),
            $shortName: $('#short_name'),
            $location: $('#location'),
            $settledWay: $('#settled_way'),
            $admin: $('#admin'),
            $tel: $('#tel'),
            $orgType: $('#org_type'),
            $tags: $("#tags"),

            $keyReadBtn:$("#keyReadBtn"),
            $footer:$("#div_footer"),
            $updateOrgBtn: $("#div_update_btn"),
            $cancelBtn: $("#btn_cancel"),

            $publicKey: $("#div_publicKey"),
            $allotpublicKey: $("#div_allot_publicKey"),
            $publicManage: $("#div_public_manage"),

            //公钥管理弹窗控件
            $publicKeyMessage: $("#txt_publicKey_message"),
            $publicKeyValidTime: $("#lbl_publicKey_validTime"),
            $publicKeyStartTime: $("#lbl_publicKey_startTime"),

            //明细公钥信息显示
            $selectPublicKeyMessage: $("#div_publicKeyMessage"),
            $selectPublicKeyValidTime: $("#div_publicKey_validTime"),
            $selectPublicKeyStartTime: $("#div_publicKey_startTime"),

            $affirmBtn:$('#div_affirm_btn'),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {

                this.$orgCode.ligerTextBox({width: 240});
                this.$fullName.ligerTextBox({width: 240});
                this.$shortName.ligerTextBox({width: 240});
                this.$location.ligerComboBox({width: 240});
//                this.$publicKeyValidTime.ligerTextBox({width: 240});
//                this.$publicKeyStartTime.ligerTextBox({width: 240});
                this.$tags.ligerTextBox({width: 240, height:28});
                this.$admin.ligerTextBox({width: 240, height:28});
                this.$tel.ligerTextBox({width: 240, height:28});
                this.$tel.removeClass('l-text-field-number');
                this.$location.addressDropdown({tabsData:[
                    {name: '省份',code:'id',value:'name', url: '${contextRoot}/address/getParent', params: {level:'1'}},
                    {name: '城市',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '县区',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '街道', maxlength:200}
                ]});

                this.initDDL(orgTypeDictId,this.$orgType);
                this.initDDL(settledWayDictId,this.$settledWay);
				this.$form.attrScan();
				var tags = '';
				for(var i=0;i<org.tags.length;i++){
					tags += (org.tags)[i]+';'
				}
				tags = tags.substring(0,tags.length-1);
				this.$form.Fields.fillValues({
					orgCode: org.orgCode,
					fullName: org.fullName,
					shortName: org.shortName,
					//location: org.location,
					orgType: org.orgType,
					settledWay: org.settledWay,
					admin:org.admin,
					tel: org.tel,
					tags: tags,
					publicKey: org.publicKey,
					validTime:org.validTime,
					startTime:org.startTime
				});
				this.$publicKeyMessage.val(org.publicKey);
				this.$publicKeyValidTime.html(org.validTime);
				this.$publicKeyStartTime.html(org.startTime);

				this.$form.Fields.location.setValue([org.province,org.city,org.district,org.street]);

                if ('${mode}' == 'view') {
                    this.$form.addClass("m-form-readonly");
                    this.$publicKey.hide();
                    this.$footer.hide();
//                    this.$updateOrgBtn.hide();
//                    this.$cancelBtn.hide();
                    this.$selectPublicKeyMessage.show();
                    this.$selectPublicKeyValidTime.show();
                    this.$selectPublicKeyStartTime.show();
                }
                if ('${mode}' == 'modify') {
                    //this.$publicManage.hide();
                }
            },
            initDDL: function (dictId, target) {
                target.ligerComboBox({
                    url: "${contextRoot}/dict/searchDictEntryList",
                    dataParmName: 'detailModelList',
                    urlParms: {dictId: dictId},
                    valueField: 'code',
                    textField: 'value'
                });
            },
            bindEvents: function () {
                var self = this;
                $('#location,.u-dropdown-icon').click(function(){
                    self.$orgCode.click();
                });
                var validator =  new jValidation.Validation(self.$form, {immediate: true, onSubmit: false});
                self.$updateOrgBtn.click(function () {
                    if(validator.validate()){
                    var dataModel = $.DataModel.init();
                    self.$form.attrScan();
                    var orgAddress = self.$form.Fields.location.getValue();
					var orgModel = self.$form.Fields.getValues();
					//标签字符串转化为数组
					var tags = orgModel.tags;
					tags = tags.split(/[;；]/)
					orgModel.tags = tags;
					//原location是对象，传到controller转化成model会报错--机构、地址分开传递（json串）
					orgModel.location = "";
					var addressModel = {
                            province:  orgAddress.names[0],
                            city: orgAddress.names[1],
                            district: orgAddress.names[2],
                            town: "",
                            street: orgAddress.names[3]
					};
                    /*var orgModel = $.extend({},self.$form.Fields.getValues(),
                            {location: "" },
                            {province:  orgAddress.names[0]},
                            {city: orgAddress.names[1]},
                            {district: orgAddress.names[2]},
                            {town: ""},
                            {street: orgAddress.names[3]}
                    );*/
                   /* if(Util.isStrEquals(orgModel.orgCode,'')){
                        $.Notice.warn('组织机构代码不能为空');
                        return;
                    }
                    if(Util.isStrEquals(orgModel.fullName,'')){
                        $.Notice.warn('机构全名不能为空');
                        return;
                    }
                    if(Util.isStrEquals(orgModel.shortName,'')){
                        $.Notice.warn('组织简称不能为空');
                        return;
                    }
                    if(Util.isStrEquals(orgModel.province,'')){
                        $.Notice.warn('位置不能为空');
                        return;
                    }
                    if(Util.isStrEquals(orgModel.tel,'')){
                        $.Notice.warn('联系电话不能为空');
                        return;
                    }*/
                    dataModel.createRemote("${contextRoot}/organization/updateOrg", {
                        data:  {orgModel:JSON.stringify(orgModel),addressModel:JSON.stringify(addressModel),mode:"modify"},
						success: function (data) {
                                    if(data.successFlg){
                                        parent.reloadMasterGrid();
                                        $.Notice.success('操作成功');
                                        //dialog.close();
                                        win.closeDialog();
                                    }else{
                                        $.Notice.error(data.errorMsg);
                                    }
                                }
                    })
                    }else{
                        return;
                    }
                });

                self.$cancelBtn.click(function(){
                    win.closeDialog();
                });

                //公钥管理窗口点击事件
                this.$publicKey.click(function () {
                    publicKeyMsgDialog = $.ligerDialog.open({title:'公钥管理',width:416,height:320,target: self.$publicManage});
                    //分配公钥点击事件
                    self.$allotpublicKey.click(function () {

                        var code = self.$form.Fields.orgCode.getValue();
                        var dataModel = $.DataModel.init();
                        dataModel.createRemote('${contextRoot}/organization/distributeKey', {
                            data: {orgCode:code},
                            success: function (data) {
                                if(data.successFlg){
                                    self.$publicKeyMessage.val(data.obj.publicKey);
                                    self.$publicKeyValidTime.html(data.obj.validTime);
                                    self.$publicKeyStartTime.html(data.obj.startTime);
                                    $.ligerDialog.alert('分配公钥成功');
                                }else{
                                    $.Notice.error(data.errorMsg);
                                    //$.Notice.error({type: 'success', msg: '公钥分配失败！'});
                                }
                            }
                        });
                    });

                    $(document.body).on('click','#div_affirm_btn',function(){
                        publicKeyMsgDialog.close();
                    })
                });






            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>