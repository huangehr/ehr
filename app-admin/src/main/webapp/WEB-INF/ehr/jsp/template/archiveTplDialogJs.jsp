<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

    (function ($, win) {
        $(function () {
        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;

        // 表单校验工具类
        var jValidation = $.jValidation;

        var addArchiveTplInfo = null;
        var dialog = frameElement.dialog;
        var mode = '${mode}';
        var archiveTpl = '${tpl}';
        var ajaxFun = mode == 'copy' ? 'copyTemplate' : 'updateTemplate';
        /* ************************** 变量定义结束 **************************** */

        /* *************************** 函数定义 ******************************* */

        function pageInit() {
            addArchiveTplInfo.init();
        }

        /* ************************** 函数定义结束 **************************** */

        /* *************************** 模块初始化 ***************************** */

        addArchiveTplInfo = {
            $form: $("#div_addArchiveTpl_form"),
            $versionNo: $("#inp_versionNo"),
            $org: $("#inp_org"),
            $title: $("#inp_title"),

            versionNo: '',
            organizationCode: '',
            cda: '',

            $dataset: $("#inp_dataset"),
            $addBtn: $("#div_add_btn"),
            $cancelBtn: $("#div_cancel_btn"),
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                var self = this;
                self.$title.ligerTextBox({width: 240});
                self.versionNo = self.$versionNo.ligerComboBox({
                    url: '${contextRoot}/standardsource/getVersionList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    width: 240,
                    onSelected: function (value) {
                        searchCda(value);
                    }
                });
                <%--self.organizationCode = this.$org.ligerComboBox({--%>
                <%--url:'${contextRoot}/template/searchOrg',--%>
                <%--valueField: 'organizationCode',--%>
                <%--textField: 'fullName',--%>
                <%--dataParmName:'detailModelList',--%>
                <%--autocomplete:true,--%>
                <%--width: 240--%>
                <%--});--%>

                this.$org.addressDropdown({
                    width: 240,
                    selectBoxWidth: 240,
                    tabsData: [
                        {name: '省份',code:'id',values:'name', url: '${contextRoot}/address/getParent', params: {level: '1'}},
                        {name: '城市',code:'id',values:'name', url: '${contextRoot}/address/getChildByParent'},
                        {
                            name: '医院',code:'id',values:'name', url: '${contextRoot}/address/getOrgs', beforeAjaxSend: function (ds, $options) {
                            var province = $options.eq(0).attr('title'),
                                    city = $options.eq(1).attr('title');
                            ds.params = $.extend({}, ds.params, {
                                province: province,
                                city: city
                            });
                        }
                        }
                    ]
                });

                searchCda('');
                function searchCda(value) {
                    self.cda = self.$dataset.ligerComboBox({
                        url: '${contextRoot}/template/getCDAListByVersionAndKey',
                        valueField: 'id',
                        textField: 'value',
                        dataParmName: 'detailModelList',
                        urlParms: {
                            value: value
                        },
                        width: 240
                    });
                }


                this.$form.attrScan();
                this.$form.Fields.fillValues({
                    id: archiveTpl.id,
                    title: mode=='copy'?'':archiveTpl.title,
                    version: archiveTpl.cdaVersion,
                    cdaType: archiveTpl.id,
                    organizationCode: ["${province}", "${city}", "${organizationCode}"]
                });
				$('#oldTitle').val(archiveTpl.title);
            },

            bindEvents: function () {
                var self = this;
                var validator = new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
					onElementValidateForAjax: function (elm) {
						var oldTitle =$('#oldTitle').val();
						var values = addArchiveTplInfo.$form.Fields.getValues();
						var newTitle = values.title;
						var version = values.version;
						if(mode=='modify'&&Util.isStrEquals(oldTitle,newTitle)){
							return true;
						}else{
							return checkTitle(elm,version,newTitle);
						}
					}
				})

				function checkTitle(elm,version,newTitle){
					if(Util.isStrEquals($(elm).attr('id'),'inp_title')){
						var result = new jValidation.ajax.Result();
						var dataModel = $.DataModel.init();
						dataModel.fetchRemote("${contextRoot}/template/validateTitle", {
							data: {version:version,title: newTitle},
							async: false,
							success: function (data) {
								if (data.successFlg) {
									result.setResult(false);
									result.setErrorMsg('当前版本下，该模板名称已存在！');
								} else {
									result.setResult(true);
								}
							}
						});
						return result;
					};
				}
                self.$addBtn.click(function () {
                    if (validator.validate()) {
                        var TemplateModel = self.$form.Fields.getValues();
                        TemplateModel.organizationCode = TemplateModel.organizationCode.keys[2];
                        var dataModel = $.DataModel.init();

                        dataModel.createRemote("${contextRoot}/template/" + ajaxFun, {
                            data: TemplateModel,
                            async: false,
                            success: function (data) {
                                if (data.successFlg) {
                                    win.parent.$.Notice.success('新增成功');
                                } else {
                                    win.parent.$.Notice.error('新增失败');
                                }
                            }
                        });
                        win.parent.reloadGrids();
                        dialog.close();
                    } else {
                        return
                    }
                });

                self.$cancelBtn.click(function () {
                    dialog.close();
                });
            }

        };

        /* ************************* 模块初始化结束 ************************** */

        /* *************************** 页面初始化 **************************** */

        pageInit();

        /* ************************* 页面初始化结束 ************************** */
    });
    })(jQuery, window);
</script>