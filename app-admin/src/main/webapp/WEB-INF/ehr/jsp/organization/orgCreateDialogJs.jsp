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

        var dialog = frameElement.dialog;

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            organizationInfo.init();
        }

        /* *************************** 模块初始化 ***************************** */
        organizationInfo = {
            $form: $("#div_organization_info_form"),
            $organizationCode: $("#org_code"),
            $fullName: $('#full_name'),
            $shortName: $('#short_name'),
            $location: $('#location'),
            $settledWay: $('#settled_way'),
            $admin: $('#admin'),
            $tel: $('#tel'),
            $orgType: $('#org_type'),
            $tags: $("#tags"),
            $keyReadBtn:$("#keyReadBtn"),
            $updateOrgBtn: $("#div_update_btn"),
            $cancelBtn: $("#btn_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.$organizationCode.ligerTextBox({width: 240});
                this.$fullName.ligerTextBox({width: 240});
                this.$shortName.ligerTextBox({width: 240});
                this.$location.ligerComboBox({width: 240});
                this.initDDL(orgTypeDictId,this.$orgType);
                this.initDDL(settledWayDictId,this.$settledWay);

                this.$form.attrScan();
                this.$location.addressDropdown({tabsData:[
                    {name: '省份',code:'id',value:'name',url: '${contextRoot}/address/getParent', params: {level:'1'}},
                    {name: '城市',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '县区',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '街道', maxlength: 200}
                ]});
                this.$admin.ligerTextBox({width: 240, height:28});
                this.$tags.ligerTextBox({width: 240, height:28});
                this.$tel.ligerTextBox({width: 240, height:28});
                this.$tel.removeClass('l-text-field-number');
            },
            initDDL: function (dictId, target) {
                var self = this;
                target.ligerComboBox({
                    url: "${contextRoot}/dict/searchDictEntryList",
                    dataParmName: 'detailModelList',
                    urlParms: {dictId: dictId},
                    valueField: 'code',
                    textField:'value',
                    onSuccess: function () {
                        self.$form.Fields.fillValues({orgType: "Hospital"});
                        self.$form.Fields.fillValues({settledWay: "Direct"});
                    }
                });
            },
            bindEvents: function () {
                var self = this;
                $("#location,.u-dropdown-icon").click(function(){
                    self.$organizationCode.click();
                });
                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,onElementValidateForAjax:function(elm){
                    if(Util.isStrEquals($(elm).attr('id'),'org_code')){
                        var result = new jValidation.ajax.Result();
                        var organizationCode = self.$organizationCode.val();
                        var dataModel = $.DataModel.init();
                        dataModel.fetchRemote("${contextRoot}/organization/validationOrg", {
                            data: {organizationCode:organizationCode},
                            async: false,
                            success: function (data) {
                                if (data.successFlg) {
                                    result.setResult(true);
                                } else {
                                    result.setResult(false);
                                    result.setErrorMsg("组织机构已存在");
                                }
                            }
                        });
                        return result;

                    }
                }});
                this.$updateOrgBtn.click(function () {
                    if(validator.validate()){
                    	var dataModel = $.DataModel.init();
                    	self.$form.attrScan();
                    	var orgAddress = self.$form.Fields.location.getValue();
						var orgModel = self.$form.Fields.getValues();
						//原location是对象，传到controller转化成model会报错--机构、地址分开传递（json串）
						orgModel.location = "";
						var addressModel = {
							province:  orgAddress.names[0],
							city: orgAddress.names[1],
							district: orgAddress.names[2],
							town: "",
							street: orgAddress.names[3]
						};

						dataModel.createRemote("${contextRoot}/organization/updateOrg", {
							data: {orgModel:JSON.stringify(orgModel),addressModel:JSON.stringify(addressModel),mode:"new"},
							success: function (data) {
								if(data.successFlg){
									win.parent.closeAddOrgInfoDialog(function (){
										win.parent.$.Notice.success('机构新增成功');
									});
								}else{
									window.top.$.Notice.error(data.errorMsg);
								}
							}
						})
                    }else{
                        return;
                    }
                });
                self.$cancelBtn.click(function(){
                    dialog.close();
                })
            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>