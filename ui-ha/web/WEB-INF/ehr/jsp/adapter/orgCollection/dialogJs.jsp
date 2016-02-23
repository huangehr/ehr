<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var infoForm = null;
        var jValidation = $.jValidation;
//        var dialog = frameElement.dialog;
        var orgCode = parent.getOrgCode();
        var seq = parent.getSeq();
        var mode = '${mode}';
        var info = JSON.parse('${info}');
        var cfgModel = parent.getDialogOpener();
        var cfg = [
            {new:'/orgdataset/createOrgDataSet', modify:'/orgdataset/updateOrgDataSet'},
            {new:'/orgdataset/createOrgMetaData', modify:'/orgdataset/updateOrgMetaData'},
            {new:'/orgdict/createOrgDict', modify:'/orgdict/updateOrgDict'},
            {new:'/orgdict/createOrgDictItem', modify:'/orgdict/updateDictItem'}
        ];
        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            infoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        infoForm = {
            $form: $("#div_info_form"),
            $code: $("#inp_info_code"),
            $name: $("#inp_info_name"),
            $sort: $("#inp_info_sort"),
            $id: $("#id"),
            $description: $("#inp_info_description"),

            $btnSave: $("#btn_save"),
            $btnCancel: $("#btn_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.$name.ligerTextBox({width:240,validate:{required:true }});
                this.$code.ligerTextBox({width:240,validate:{required:true }});
                this.$sort.ligerTextBox({width:240,digits:true,validate:{required:true }});
                this.$description.ligerTextBox({width:240,height:180 });

                this.$form.attrScan();
                this.$form.Fields.fillValues({
                    name:info.name,
                    code: info.code,
                    description:info.description,
                    id:info.id,
                    sort: '${sort}'  ,
                    orgDataSetSeq: seq,
                    orgDictSeq: seq,
                    orgCode: orgCode
                });
                this.setSortShow();
                this.$form.show();
            },
            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });

                this.$btnSave.click(function () {
                    var values = self.$form.Fields.getValues();
                    if(!validator.validate()){
                        return;
                    }
                    self.$btnSave.attr('disabled','disabled');
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}"+cfg[cfgModel][mode],{data: $.extend({}, values),
                        success: function(data) {
                            if(data.successFlg){
                                var app = data.obj;
                                parent.reloadMasterGrid();
                                parent.closeDialog( '保存成功！');
                            }else{
                                if(data.errorMsg)
                                    $.Notice.error( data.errorMsg);
                                else
                                    $.Notice.error( '保存失败！');
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            $.Notice.error( '保存失败！');
                            self.$btnSave.removeAttr('disabled');
                        }
                    });

                });
                this.$btnCancel.click(function () {
                    parent.closeDialog();
                });
            },
            validate : function (values) {
                if($.trim(values.code)=='' || $.trim(values.name)==''){
                    return false;
                }
                return true;
            },
            setSortShow : function () {
                if(cfgModel==3){
                    $('#divSort').css('display','block');
                }
            }


        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>