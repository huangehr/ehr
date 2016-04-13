<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;
        var adapterInfo = null;
        var jValidation = $.jValidation;
        //适配方案变量
        var adapterPlanModel = null;
        var adapterType = 21;
        var initType;
        var mode = '${mode}';
        var orgData;
        var versions;
        var types;
        var adapterPlan = $.parseJSON('${model}');
        var firstInit = true;
        /* ************************** 变量定义结束 **************************** */

        /* *************************** 函数定义 ******************************* */
        /**
         * 页面初始化
         */
        function pageInit() {
            adapterInfo.init();
        }

        /* ************************** 函数定义结束 **************************** */

        /* *************************** 模块初始化 ***************************** */
        adapterInfo = {
            adapterOrgDialog: null,
            $form: $("#div_adapter_info_form"),
            $id: $("#id"),
            $type: $('#ipt_type'),
            $code: $("#ipt_code"),
            $name: $('#ipt_name'),
            $version: $('#ipt_version'),
            $org: $('#ipt_org'),
            $parent: $('#ipt_parent'),
            $description: $('#ipt_description'),

            $updateAdapterBtn: $("#btn_save"),
            $cancelBtn: $("#btn_cancel"),
            $addOrg: $('#add_org'),
            $readonly: $('.u-readonly', this.$form),
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                var self = this;

                self.$form.removeClass("m-form-readonly");
                types = self.$type.ligerComboBox(
                        {
                            cancelable: false,
                            url: '${contextRoot}/dict/searchDictEntryList',
                            valueField: 'code',
                            textField: 'value',
                            dataParmName: 'detailModelList',
                            urlParms: {
                                page: 1,
                                rows: 1000,
                                dictId: adapterType
                            },
                            onSelected: function (value) {
                                var versionValue = versions.getValue();
                                if (Util.isStrEmpty(versionValue)) {
                                    return;
                                }
                                searchParent();
                                searchOrg();
                            }
                        });

                self.$code.ligerTextBox({width: 240});
                self.$name.ligerTextBox({width: 240});
                versions = self.$version.ligerComboBox(
                        {
                            cancelable: false,
                            url: '${contextRoot}/adapter/versions',
                            valueField: 'version',
                            textField: 'versionName',
                            dataParmName: 'detailModelList',
                            urlParms: {
                                page: 1,
                                rows: 1000,
                                dictId: 4
                            },
                            onSelected: function (value) {
                                var typeValue = types.getValue();
                                if (Util.isStrEmpty(typeValue)) {
                                    return;
                                }
                                searchParent();
                                searchOrg();
                            }
                        });
                searchOrg();
                self.$parent.ligerComboBox(
                        {
                            data: null
                        });
                self.$description.ligerTextBox({width: 240, height: 180});
                if (mode == 'new') {
                    //可编辑
                    self.$addOrg.show();
                    self.$readonly.removeClass("u-ui-readonly");
                } else if (mode == 'modify') {
                    //设值
                    self.$form.attrScan();
                    self.$form.Fields.fillValues({
                        id: adapterPlan.id,
                        type: adapterPlan.type,
                        code: adapterPlan.code,
                        name: adapterPlan.name,
                        version: adapterPlan.version,
//                        org: adapterPlan.org,
                        parentId: adapterPlan.parentId,
                        description: adapterPlan.description
                    });
                    //不可编辑
                    self.$addOrg.hide();
                    self.$readonly.addClass("u-ui-readonly");
                }

                function searchOrg() {
                    debugger
                    var p = {
                            type: types.getValue(),
                            version: versions.getValue(),
                            mode: mode};
                    if(orgData){
                        if(firstInit){
                            orgData.reload(p);
                            orgData.setValue(adapterPlan.org);
                            orgData.setText(adapterPlan.orgValue);
                            firstInit = false;
                        }
                        else
                            orgData.reload(p);
                    }
                    else{
                        orgData = self.$org.customCombo(
                                '${contextRoot}/adapter/getOrgList', p, undefined, undefined, false);
//                        orgData.setValue(adapterPlan.org);
//                        orgData.setText(adapterPlan.orgValue);
                    }
                }

                function searchParent() {
                    self.$parent.ligerComboBox(
                            {
                                url: '${contextRoot}/adapter/combo',
                                valueField: 'code',
                                textField: 'value',
                                dataParmName: 'detailModelList',
                                urlParms: {
                                    fields: '',
                                    filters: 'type=' + types.getValue() + ";version=" + versions.getValue(),
                                    sorts: '',
                                    page: 1,
                                    rows: 100
                                }
                            });
                }
            },
            bindEvents: function () {
                var self = this;

                var validator = new jValidation.Validation(this.$form, {
                    immediate: true, onSubmit: false,
                    onElementValidateForAjax: function (elm) {

                    }
                });

                //修改用户的点击事件
                this.$updateAdapterBtn.click(function () {
                    self.$form.attrScan();
                    adapterPlanModel = self.$form.Fields.getValues();
                    if(!validator.validate()){
                        return;
                    }
                    var parms = {
                        id: adapterPlanModel.id,
                        model: JSON.stringify(adapterPlanModel),
                        extParms: '{"isCover": "false"}'
                    }
                    var pv = self.$parent.ligerGetComboBoxManager().getValue();
                    if (pv && mode=='new') {
                        var parent = self.$parent.ligerGetComboBoxManager().getSelected();
                        $.Notice.confirm("映射机构采集标准与复方案采集标准不一致，是否覆盖映射机构采集标准？", function (r) {
                            parms.extParms = '{"isCover":"' + r + '"}';
                            updata(parms);
                        })
                    }
                    else {
                        updata(parms);
                    }

                });

                function updata(adapterPlanModel) {
                    var waittingDialog = $.ligerDialog.waitting('正在保存中,请稍候...');
                    var dataModel = $.DataModel.init();
                    dataModel.createRemote("${contextRoot}/adapter/update", {
                        data: adapterPlanModel,
                        success: function (data) {
                            waittingDialog.close();
                            if (data.successFlg) {
                                //调用主页面接口，重新刷新Grid
                                win.closePlanDialog("保存成功")
                            } else {
                                if (data.errorMsg)
                                    $.Notice.error(data.errorMsg);
                                else
                                    $.Notice.error('出错了！');
                            }
                        },
                        error: function () {
                            waittingDialog.close();
                        }
                    })
                }


                this.$cancelBtn.click(function () {
                    win.closePlanDialog();
                });
                //新增映射机构
                this.$addOrg.click(function () {
                    //
                    var title = '新增第三方标准';
                    var code = '';
                    var mode = 'new';
                    self.$form.attrScan();
                    initType = self.$form.Fields.getValues().type;
                    self.adapterOrgDialog = $.ligerDialog.open({
                        height: 550,
                        width: 460,
                        title: title,
                        url: '${contextRoot}/adapterorg/template/adapterOrgInfo',
                        urlParms: {
                            code: code,
                            type: initType,
                            mode: mode,
                            frm: "1"
                        },
                        isHidden: false,
                        opener: true,
                        load: true
                    });
                });
            },
            validate: function (values) {
                return !($.trim(values.code) == '' || $.trim(values.name) == '' || $.trim(values.type) == '' || $.trim(values.org) == '' || $.trim(values.version) == '');
            }

        };

        /* ************************* 模块初始化结束 ************************** */
        win.closeDialog = function (msg) {
            adapterInfo.adapterOrgDialog.close();
            if (msg)
                $.Notice.success(msg);
        };
        win.adapterModel = function (adapterModel) {
            orgData.reload({
                type: adapterModel.type,
                version: versions.getValue()
            });
            types.setValue(adapterModel.type);
            orgData.setValue(adapterModel.code);
            orgData.setText(adapterModel.name);
        }
        /* *************************** 页面初始化 **************************** */

        pageInit();
        /* ************************* 页面初始化结束 ************************** */

    })(jQuery, window);
</script>