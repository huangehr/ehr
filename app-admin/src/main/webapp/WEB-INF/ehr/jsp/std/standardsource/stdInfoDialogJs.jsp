<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var stdInfoForm = null;

//        var dialog = frameElement.dialog;
        // 表单校验工具类
        var jValidation = $.jValidation;
		var mode = '${mode}';


        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            stdInfoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        stdInfoForm = {
            $form: $("#div_std_info_form"),
            $code: $("#inp_std_code"),
            $name: $("#inp_std_name"),
            $type: $("#inp_std_type"),
            $id: $("#id"),
            $description: $("#inp_std_description"),

            $btnSave: $("#btn_save"),
            $btnCancel: $("#btn_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.initDDL(22, this.$type);
                this.$name.ligerTextBox({width:240,validate:{required:true }});
                this.$code.ligerTextBox({width:240,validate:{required:true }});
                this.$description.ligerTextBox({width:240,height:150 });

                this.$form.attrScan();
				var std = '';
				if(mode == 'modify'){
					var envelop = JSON.parse('${envelop}');
					var std = envelop.obj;
				}
                this.$form.Fields.fillValues({
                    name: std.name,
                    code: std.code,
                    type: std.sourceType,
                    id: std.id,
                    description: std.description,
                });

                this.$form.show();
            },
            initDDL: function (dictId, target) {
                target.ligerComboBox({
                    //data : [{code:'11111',value:'类型1'},{code:'22222',value:'类型2'},{code:'33333',value:'类型3'}],
                    url: "${contextRoot}/dict/searchDictEntryList",
                    dataParmName: 'detailModelList',
                    urlParms: {dictId: dictId},
                    valueField: 'code',
                    textField: 'value'
                });
            },

            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });

                this.$btnSave.click(function () {

                    var dialog = $.ligerDialog.waitting("正在保存数据...");

                    var values = self.$form.Fields.getValues();
                    if(!validator.validate()){
                        return;
                    }
                    self.$btnSave.attr('disabled','disabled');
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/standardsource/updateStdSource",{data: $.extend({}, values),
                        success: function(data) {
                            if(data.successFlg){
                                var app = data.obj;
                                //调用主页面接口，重新刷新Grid
                                parent.reloadMasterGrid();
                                parent.closeDialog('保存成功');
                            }else{
                                if(data.errorMsg=='codeNotUnique'){
                                    $.Notice.error('该代码在系统中已存在，请确认！');
                                }
                                else if(data.errorMsg)
                                    $.Notice.error( data.errorMsg);
                                else
                                    $.Notice.error( '对不起，更新失败，请联系管理员。');
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            $.Notice.error( '对不起，更新失败，请联系管理员。');
                            self.$btnSave.removeAttr('disabled');
                        },
                        complete:function(){
                            dialog.close();
                        }
                    });
                });

                this.$btnCancel.click(function () {
                    win.closeDialog();
                });
            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>