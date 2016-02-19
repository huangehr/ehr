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
        var info = JSON.parse('${info}');
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
            orgDictEntrySeq : info.orgDictItemSeq,
            $dictForm: $("#div_dict_form"),
            $btnSave:$("#btn_save_dict"),
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                $('#dict_info_description').ligerTextBox({width:240,height:140 });
                $('#dict_info_dictEntryId').ligerComboBox({readonly:mode=='modify'||mode=='view'});
                this.initDDL(0,$('#dict_info_dictEntryId'),info.dictEntryId);
                this.initDDL(1,$('#dict_info_orgDictSeq'),info.orgDictSeq);
                this.$dictForm.attrScan();
                this.$dictForm.Fields.fillValues({
                    description:info.description,
                    id:info.id
                });
                this.$dictForm.show();
                this.$dictForm.css('display','block');
            },
            initDDL: function (dictId, target, value, childValue, text) {
                var self = this;
                var dataModel = $.DataModel.init();
                var url = "${contextRoot}"+dictUrl[dictId];
                var data = this.getDictParms(dictId);
                dataModel.fetchRemote(url,{
                    data:data,
                    success: function(data) {
                        data = self.toJson(data.obj);
                        target.ligerComboBox({
                            selectBoxHeight:220,
                            valueField: 'id',
                            textField: 'name',
                            allowBlank: false,
                            data: data,
                            onSelected : self.getSelectFunc(dictId),
                            cancelable:false
                        });
                        var manager = target.ligerGetComboBoxManager();
                        manager.selectValue(value);
                    }
                });
            },
            getDictParms: function (dictId) {
                switch (dictId){
                    case 0:return {adapterPlanId:adapterPlanId,dictId:parentId,mode:mode};
                    case 1:return {adapterPlanId:adapterPlanId};
                    case 2:
                            var orgDictSeq = $('#dict_info_orgDictSeq').ligerGetComboBoxManager().getValue();
                            return {orgDictSeq:orgDictSeq,adapterPlanId:adapterPlanId};
                }
            },
            getSelectFunc: function (dictId,childValue) {
                var self = this;
                if(dictId==1){
                    return function (value, text) {
                        if(self.orgDictEntrySeq){
                            self.initDDL(2, $('#dict_info_orgDictEntrySeq'),self.orgDictEntrySeq);
                            self.orgDictEntrySeq = '';
                        }
                        else
                            self.initDDL(2, $('#dict_info_orgDictEntrySeq'));
                    }
                }
                return function (value, text) {
                    
                };
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
                    var values = self.$dictForm.Fields.getValues();
                    var v = self.$dictForm.Fields.toSerializedString() +'&adapterPlanId='+adapterPlanId+'&dictId='+parentId;
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/adapterDict/updateAdapterDictEntry",{data: v,
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
                    });
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