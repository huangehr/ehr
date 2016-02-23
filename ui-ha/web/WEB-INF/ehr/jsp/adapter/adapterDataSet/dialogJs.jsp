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
        var dictUrl = [
                '/adapterDataSet/getStdMetaData',
                '/adapterDataSet/getOrgDataSet',
                '/adapterDataSet/getOrgMetaData'
        ];
        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            infoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        infoForm = {
            orgMetaDataSeq : info.orgMetaDataSeq,
            $dataMataForm: $("#div_dataMata_form"),
            $btnSave:$("#btn_save"),
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                $('#inp_info_description').ligerTextBox({width:240,height:140 });
                var dataType = $('#inp_info_dataTypeCode').ligerComboBox({
                    valueField: 'id',
                    textField: 'name',
                    data:[{id:'',name:'请选择'},{id:'0',name:'值'},{id:'1',name:'编码'}]
                });
                $('#inp_info_metaDataId').ligerComboBox({readonly:mode=='modify'||mode=='view'});
                this.initDDL(0,$('#inp_info_metaDataId'),info.metaDataId);
                this.initDDL(1,$('#inp_info_orgDataSetSeq'),info.orgDataSetSeq);
                this.$dataMataForm.attrScan();
                this.$dataMataForm.Fields.fillValues({
                    description:info.description,
                    id:info.id
                });
                if(info.id!="")
                {
                    var dataTypeId = info.dataType;
                    if(dataTypeId==null){
                        dataTypeId='';
                    }
                    dataType.setValue(dataTypeId);
                }

                this.$dataMataForm.show();
                this.$dataMataForm.css('display','block');
            },
            initDDL: function (dictId, target, value, childValue, text) {
                var self = this;
                var dataModel = $.DataModel.init();
                var url = "${contextRoot}"+dictUrl[dictId];
                var data = this.getDictParms(dictId);
                dataModel.fetchRemote(url,{
                    data:data,
                    success: function(data) {
                        debugger;
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
                        debugger;
                        var manager = target.ligerGetComboBoxManager();
                        manager.selectValue(value);
                    }
                });
            },
            getDictParms: function (dictId) {
                switch (dictId){
                    case 0:return {adapterPlanId:adapterPlanId,dataSetId:parentId,mode:mode};
                    case 1:return {adapterPlanId:adapterPlanId};
                    case 2:
                            var orgDataSetSeq = $('#inp_info_orgDataSetSeq').ligerGetComboBoxManager().getValue();
                            return {orgDataSetSeq:orgDataSetSeq,adapterPlanId:adapterPlanId};
                }
            },
            getSelectFunc: function (dictId,childValue) {
                var self = this;
                if(dictId==1){
                    return function (value, text) {
                        debugger;
                        if(self.orgMetaDataSeq){
                            self.initDDL(2, $('#inp_info_orgMetaDataSeq'),self.orgMetaDataSeq);
                            self.orgMetaDataSeq = '';
                        }
                        else
                            self.initDDL(2, $('#inp_info_orgMetaDataSeq'));
                    }
                }
                return function (value, text) {
                    
                };
            },
            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$dataMataForm, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });
                this.$btnSave.click(function () {
                    if(!validator.validate()){
                        return;
                    }
                    self.$btnSave.attr('disabled','disabled');
                    var values = self.$dataMataForm.Fields.getValues();
                    var v = self.$dataMataForm.Fields.toSerializedString() +'&adapterPlanId='+adapterPlanId+'&dataSetId='+parentId;
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/adapterDataSet/updateAdapterMetaData",{data: v,
                        success: function(data) {
                            if(data.successFlg){
                                var app = data.obj;
                                parent.reloadEntryMasterGrid();
                                parent.closeDialog('保存成功！');
                            }else{
                                if(data.errorMsg)
                                    $.Notice.error( data.errorMsg);
                                else
                                    $.Notice.error( '出错了！');
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            $.Notice.error( '出错了！');
                            self.$btnSave.removeAttr('disabled');
                        }
                    });
                });
                $('#btn_cancel').click(function () {
                    parent.closeDialog();
                });
            },
            validate : function (values) {
                if($.trim(values.metaDataId)==''){
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