<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var infoForm = null;
        var jValidation = $.jValidation;
//        var dialog = frameElement.dialog;
        var adapterPlanId = parent.getAdapterPlanId();
        var parentId = parent.getParentId();
        var mode = '${mode}';
        var info = JSON.parse('${model}');
        var cfgModel = parent.getDialogOpener();
        var cfg = [
            {new:'', modify:'/adapterDataSet/updateAdapterMetaData'},
            {new:'', modify:'/adapterDict/updateAdapterDictEntry'}
        ];
        var dictUrl = [
                '/adapterDict/getStdDictEntry',
                '/adapterDict/getOrgDict',
                '/adapterDict/getOrgDictEntry'
        ];
        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            infoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        infoForm = {
            orgDictEntrySeq : info.orgDictEntrySeq,
            $dictForm: $("#div_dict_form"),
            $btnSave:$("#btn_save_dict"),
            isInit: false,
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                $('#dict_info_description').ligerTextBox({width:240,height:140 });
                this.initCombo(0, $('#dict_info_dictEntryId'), info.dictEntryId, info.dictEntryName);
                this.initCombo(1, $('#dict_info_orgDictSeq'), info.orgDictSeq, info.orgDictName);
                this.initCombo(2, $('#dict_info_orgDictEntrySeq'), info.orgDictEntrySeq, info.orgDictEntryName, info.orgDictSeq);
                this.$dictForm.attrScan();
                this.$dictForm.Fields.fillValues({
                    description:info.description,
                    id:info.id
                });
                this.$dictForm.show();
                this.$dictForm.css('display','block');
            },
            initCombo : function (dictId, target, value, text, parentValue){
                var url = "${contextRoot}" + dictUrl[dictId];
                var child = dictId == 1 ? $('#dict_info_orgDictEntrySeq') : undefined;
                var combo = target.customCombo(
                        url, {
                            adapterPlanId: adapterPlanId,
                            dictId: parentId,
                            mode: mode,
                            parentId: parentValue},
                        undefined, child, dictId==0 ? mode=='modify'|| mode=='view' : false
                )
                if(!Util.isStrEmpty(value)){
                    combo.setValue(value);
                    combo.setText(text);
                }
            },
            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$dictForm, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });
                this.$btnSave.click(function () {
                    if(!validator.validate()){
                        return;
                    }
                    self.$btnSave.attr('disabled','disabled');
                    var model = self.$dictForm.Fields.getValues();
                    model.adapterPlanId = adapterPlanId;
                    model.dictId = parentId;
                    var parms = {
                        model: JSON.stringify(model),
                        id: model.id
                    }
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote(
                            "${contextRoot}/adapterDict/update",
                            {
                                data: parms,
                                success: function(data) {
                                    if(data.successFlg){
                                        var app = data.obj;
                                        parent.reloadEntryMasterGrid();
                                        parent.closeDialog('保存成功！');
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
                            }
                    );
                });
                $('#btn_cancel_dict').click(function () {
                    parent.closeDialog();
                });
            },
            validate : function (values) {
                if($.trim(values.dictEntryId)==''){
                    return false;
                }
                return true;
            },
            toJson : function (data) {
                var tmp = [];
                var resData = [];
                resData.push({id:'',name:''});
                for(var i=0;i<data.length;i++){
                    tmp = data[i].split(',');
                    resData.push({id:tmp[0],name:tmp[1]});
                }

                return resData;
            }

        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>