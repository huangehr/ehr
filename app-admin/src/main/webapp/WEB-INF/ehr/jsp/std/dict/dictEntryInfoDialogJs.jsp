<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var entryInfoForm = null;
        var jValidation = $.jValidation;

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            entryInfoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        entryInfoForm = {
            $form: $("#div_dictentry_info_form"),
            $code: $("#inp_dictentry_code"),
            $value: $("#inp_dictentry_value"),
            $dictId: $("#dictId"),
            $desc: $("#inp_dictentry_desc"),
            $id: $("#entryId"),

            $btnSave: $("#btn_entry_save"),
            $btnCancel: $("#btn_entry_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.$value.ligerTextBox({width:240,validate:{required:true }});
                this.$code.ligerTextBox({width:240,validate:{required:true }});
                this.$desc.ligerTextBox({width:240,height:100 });

                this.$form.attrScan();
				var info = $.parseJSON('${info}');
                this.$form.Fields.fillValues({
                    value: info.value,
                    code: info.code,
                    desc: info.desc,
                    entryId : info.id,
                    dictId : info.dictId
                });
                this.$form.show();
            },

            bindEvents: function () {
                var self = this;

                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });

                this.$btnSave.click(function () {
                    self.$btnSave.attr('disabled','disabled');
                    var values = self.$form.Fields.getValues();
                    var strVersionCode = parent.getStrVersion();
                    var data = {
                        cdaVersion:strVersionCode,
                        dictId:values.dictId,
                        id:values.entryId,
                        code:values.code,
                        value:values.value,
                        desc:values.desc};
                    if(!validator.validate()){
                        self.$btnSave.removeAttr('disabled');
                        return;
                    }
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/cdadict/saveDictEntry",{
                        data: data,
                        success: function(data) {
                            if(data.successFlg){
                                var app = data.obj;
                                //调用主页面接口，重新刷新Grid
                                parent.reloadEntryMasterGrid();
                                parent.closeDialog('right','保存成功！');
                            }else{
                                if(data.errorMsg){
                                     $.Notice.error(data.errorMsg);
                                }
                                else if(data.message)
                                    $.Notice.error( data.message);
                                else
                                    $.Notice.error( '系统出错了，请联系管理员！');
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            $.Notice.error( '系统出错了，请联系管理员！');
                            self.$btnSave.removeAttr('disabled');
                        }
                    });
                });

                this.$btnCancel.click(function () {
                    parent.closeDialog('right');
                });
            },
            validate: function (values) {
                if($.trim(values.code)=='' || $.trim(values.value)=='')
                    return false;
                return true;
            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>