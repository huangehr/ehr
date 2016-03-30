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

            // 页面主模块
            var changePassWordInfo = null;

            /* *************************** 函数定义 ******************************* */
            /**
             * 页面初始化。
             */
            function pageInit() {
                changePassWordInfo.init();
            }

            /* *************************** 模块初始化 ***************************** */
            changePassWordInfo = {

                $form: $("#div_changePassWord_info_form"),

                $userName: $("#inp_userName"),
                $oldPassWord: $("#inp_old_passWord"),
                $newPassWord: $("#inp_new_passWord"),
                $againNewPassWord: $("#inp_again_passWord"),
                $changePassWordBtn: $("#div_changePassWord_btn"),
                $cancelBtn: $("#div_cancel_btn"),

                $intensionWeak: $("#td-intension-weak"),
                $intensionMiddle: $("#td-intension-middle"),
                $intensionPowerful: $("#td-intension-powerful"),


                init: function () {
                    var self = this;

                    self.initForm();
                    self.bindEvents();
                },

                initForm: function () {
                    var self = this;
                    self.$userName.ligerTextBox({width: 240});
                    self.$oldPassWord.ligerTextBox({width: 240});
                    self.$newPassWord.ligerTextBox({width: 240});
                    self.$againNewPassWord.ligerTextBox({width: 240});

                    self.$form.attrScan();
                },


                //重新加载
                reloadGrid: function () {
                    <%--var values = retrieve.$element.Fields.getValues();--%>
                    <%--reloadGrid.call(this, '${contextRoot}/user/searchUsers', values);--%>
                },

                bindEvents: function () {
                    var self = this;
                    var result = new jValidation.ajax.Result();

                    var validator = new jValidation.Validation(self.$form, {
                        immediate: true, onSubmit: false,
                        onElementValidateForAjax: function (elm) {
                            return dataValidation(elm);
                        }
                    });
                    self.$changePassWordBtn.click(function () {
                        var UserDetailModel = self.$form.Fields.getValues();
                        if (validator.validate()) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/user/changePassWord", {
                                data: UserDetailModel,
                                success: function (data) {

                                }
                            })
                        } else
                            return;

                    });

                    function dataValidation(idCode) {

                        var dataModel = $.DataModel.init();
                        var userName = self.$userName.val();
                        var passWord = self.$oldPassWord.val();
                        var newPassWord = self.$newPassWord.val();
                        var againNewPassWord = self.$againNewPassWord.val();

                        if (Util.isStrEquals($(idCode).attr("id"), "inp_old_passWord")) {

                            dataModel.fetchRemote("${contextRoot}/login/userVerification", {
                                data: {userName: userName, password: passWord},
                                async: false,
                                success: function (data) {
                                    if (data.successFlg) {
                                        result.setResult(true);
                                    } else {
                                        result.setResult(false);
                                        result.setErrorMsg(data.errorMsg);
                                    }
                                }
                            });
                            return result;
                        }
                        if (Util.isStrEquals($(idCode).attr("id"), "inp_new_passWord")) {
                            if (Util.isStrEquals(passWord, newPassWord)) {
                                return ValidationMsg(false,"密码与原始密码相近，请重新输入");
                            }
                            if(Util.isStrEquals(userName, newPassWord)){
                                return ValidationMsg(false,"新密码与用户名不能一样");
                            }
                            if(Util.isNum){
                                debugger
                                self.$intensionWeak.addClass("s-bc13");
                                return ValidationMsg(false,"新密码不能纯数字");

                            }
                        }
                        if (Util.isStrEquals($(idCode).attr("id"), "inp_again_passWord")) {
                            if (!Util.isStrEquals(newPassWord, againNewPassWord)) {
                                return ValidationMsg(false,"新密码不一致");
                            }
                        }
                    }
                    function ValidationMsg(bo,errorMsg){
                        result.setResult(bo);
                        result.setErrorMsg(errorMsg);
                        return result;
                    }
                },


            };

            /* ************************* 模块初始化结束 ************************** */


            /* *************************** 页面初始化 **************************** */

            pageInit();

            /* ************************* 页面初始化结束 ************************** */
        });
    })(jQuery, window);
</script>