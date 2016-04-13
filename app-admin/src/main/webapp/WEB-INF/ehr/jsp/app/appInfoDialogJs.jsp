<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var appInfoForm = null;

        // 表单校验工具类
        var jValidation = $.jValidation;

        var catalogDictId = 1;
        var statusDictId = 2;

        var dialog = frameElement.dialog;

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            appInfoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        appInfoForm = {
            $form: $("#div_app_info_form"),
            $name: $("#inp_app_name"),
            $catalog: $("#inp_dialog_catalog"),
            $status: $("#inp_dialog_status"),
            $tags: $("#inp_tags"),
            $appId: $("#inp_app_id"),
            $secret: $("#inp_app_secret"),
            $url: $("#inp_url"),
            $description: $("#inp_description"),
            $btnSave: $("#btn_save"),
            $btnCancel: $("#btn_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.initDDL(catalogDictId, this.$catalog);
                this.initDDL(statusDictId, this.$status);

                this.$name.ligerTextBox({width:240});
                this.$url.ligerTextBox({width:240, height: 50 });
                this.$appId.ligerTextBox({width:240});
                this.$tags.ligerTextBox({width:240});
                this.$secret.ligerTextBox({width:240});
                this.$description.ligerTextBox({width:240, height: 100 });

                var mode = '${mode}';
				if(mode != 'view'){
					$(".my-footer").show();
				}
				if(mode == 'view'){
					appInfoForm.$form.addClass('m-form-readonly')
					$("input,select", this.$form).prop('disabled', false);
				}
                this.$form.attrScan();
                if(mode !='new'){
                    var app = (JSON.parse('${app}')).obj;
					var tags = '';
					for(var i=0;i<app.tags.length;i++){
						tags += (app.tags)[i]+';'
					}
					tags = tags.substring(0,tags.length-1);
					app.tags = tags;
                    this.$form.Fields.fillValues({
                        name:app.name,
                        catalog: app.catalog,
                        status:app.status,
                        tags:app.tags,
                        id:app.id,
                        secret:app.secret,
                        url:app.url,
                        description:app.description
                    });
                }
                this.$form.show();
            },
            initDDL: function (dictId, target) {
                //var self = this;
                //var target = $(target);
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
                var validator =  new jValidation.Validation(this.$form, {immediate:true,onSubmit:false,
                    onElementValidateForAjax:function(elm){
                    }
                });
                this.$btnSave.click(function () {
                    if(validator.validate()){
                        if('${mode}' == 'new'){
                            var values = self.$form.Fields.getValues();
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/app/createApp",{data: $.extend({}, values),
                                success: function(data) {
                                    if (data.successFlg) {
                                        win.parent.closeDialog(function () {
                                        });
                                    } else {
                                        window.top.$.Notice.error(data.errorMsg);
                                    }
                                }});
                        }else{
                            var values = self.$form.Fields.getValues();
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/app/updateApp",{data: $.extend({}, values),
                                success: function(data) {
                                    if (data.successFlg) {
                                        win.parent.closeDialog(function () {
                                        });
                                    } else {
                                        window.top.$.Notice.error(data.errorMsg);
                                    }
                                }});
                        }
                    }else{
                        return;
                    }
                });

                this.$btnCancel.click(function () {
                    dialog.close();
                });
            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>