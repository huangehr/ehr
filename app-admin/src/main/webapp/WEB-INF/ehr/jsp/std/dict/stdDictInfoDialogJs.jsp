<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var stdDictInfoForm = null;
        var jValidation = $.jValidation;
//        var dialog = frameElement.dialog;

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            stdDictInfoForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        stdDictInfoForm = {

            $form: $("#div_stddict_info_form"),
            $code: $("#inp_stddict_code"),
            $name: $("#inp_stddict_name"),
            $basedict: $("#inp_stddict_basedict"),
            $stdSource: $("#inp_stddict_stdSource"),
            $stdVersion: $("#inp_stddict_stdVersion"),
            $description: $("#inp_stddict_description"),
            $id: $("#id"),

            $btnSave: $("#btn_save"),
            $btnCancel: $("#btn_cancel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
               $("#div_stddict_stdVersion").hide();
                this.$stdSource.ligerComboBox({valueField: 'id',textField: 'name'});
                this.initDDL(1, this.$stdSource);
                this.initDDL(2, this.$basedict);
                this.$name.ligerTextBox({width:240,validate:{required:true }});
                this.$code.ligerTextBox({width:240,validate:{required:true }});
                this.$stdVersion.ligerTextBox({width:240 });
                this.$description.ligerTextBox({width:240,height:100 });

                this.$form.attrScan();

				var info = $.parseJSON('${info}')
                this.$form.Fields.fillValues({
                    name: info.name,
                    code: info.code,
                    //baseDictId: info.baseDictId,
                    stdSource : info.sourceId,
                    stdVersion : info.stdVersion,
                    id: info.id ,
                    description: info.description,
                });

                this.$form.show();
            },
            initDDL: function (mode, target) {
                var dataModel = $.DataModel.init();
                var strVersionCode = parent.getStrVersion();
                var url = '';
                if(mode==1){
                    url = "${contextRoot}/cdadict/getStdSourceList";
                    dataModel.fetchRemote(url,{
                        success: function(data) {
                            //var d = eval('('+ data.detailModelList +')');
                            target.ligerComboBox({
                                selectBoxHeight:220,
                                valueField: 'id',
                                textField: 'name',
                                data: data.detailModelList
                            });
                    }});
                }
                else{
                    url = "${contextRoot}/cdadict/searchCdaBaseDictList?strVersionCode="+strVersionCode;
                    target.ligerComboBox({
                        condition: { inputWidth: 90 ,width:0,labelWidth:0,hideSpace:true,fields: [{ name: 'param', label:''}] },//搜索框的字段, name 必须是服务器返回的字段
                        grid: getGridOptions(true),
                        valueField: 'id',
                        textField: 'name',
                        width : 240,
                        selectBoxHeight : 260,
                        onSelected: function(id,name){//name为选择的值
                            target.val(name);//下拉框选择后文本框的值
                        },
                        conditionSearchClick: function (g) {
                            var param = g.rules.length>0? g.rules[0].value : '';
                            param = {param:param }
                            g.grid.set({
                                parms: param,
                                newPage: 1
                            });
                            g.grid.reload();
                        }
                    });
                    function getGridOptions(checkbox) {
                        var options = {
                            columns: [
                                {display : '名称', name :'name',width : 210}
                            ],
                            allowAdjustColWidth : true,
                            editorTopDiff : 41,
                            headerRowHeight : 0,
                            height : '100%',
                            heightDiff : 0,
                            pageSize: 15,
                            pagesizeParmName : 'rows',
                            record : "totalCount",
                            root : "detailModelList",
                            rowHeight : 30,
                            rownumbers :false,
                            switchPageSizeApplyComboBox: false,
                            width :"98%",
                            url : url
                        };
                        return options;
                    }
                    this.$basedict.ligerGetComboBoxManager().setValue('${baseDictId}');
                    this.$basedict.ligerGetComboBoxManager().setText('${baseDictName}');
                }
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
                        dictId:values.id,
                        code:values.code,
                        name:values.name,
                        baseDict:values.baseDictId,
                        stdSource:values.stdSource,
                        stdVersion:values.stdVersion,
                        description:values.description};
                    if(!validator.validate()){
                        self.$btnSave.removeAttr('disabled');
                        return;
                    }
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/cdadict/saveDict",{
                        data: data,
                        success: function(data) {
                            if(data.successFlg){
                                var app = data.obj;
                                //$.Notice.success( '操作成功！');
                                //调用主页面接口，重新刷新Grid
                                parent.reloadMasterGrid();
                                parent.closeDialog('left', '保存成功！');
//                                dialog.close();
                            }else{
                                $.Notice.error(data.errorMsg);
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            $.Notice.error('出错了！');
                            self.$btnSave.removeAttr('disabled');
                        }
                    });

                });

                this.$btnCancel.click(function () {
                    parent.closeDialog('left');
//                    dialog.close();
                });
            }
        };

        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>