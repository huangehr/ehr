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

            var errorPassWords = sessionStorage.getItem("errorPassWord");

            var userId = '${current_user.id}';
            /* *************************** 函数定义 ******************************* */

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
                $pwBtn:$("#div-pw-btn"),

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

                bindEvents: function () {
                    var self = this;
                    var result = new jValidation.ajax.Result();
                    errorPassWord();

                    var validator = new jValidation.Validation(self.$form, {
                        immediate: true, onSubmit: false,
                        onElementValidateForAjax: function (elm) {
                            return dataValidation(elm);
                        }
                    });
                    self.$changePassWordBtn.click(function () {
                        if (validator.validate()) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/user/changePassWord", {
                                data: {userId: userId, passWord: self.$newPassWord.val()},
                                success: function (data) {
                                    $.ligerDialog.waitting('密码修改成功，请重新登录');
                                    setTimeout(function () {
                                        window.location.href = "${contextRoot}/login";
                                    }, 3000);
                                }
                            })
                        } else
                            return;
                    });

                    //取消修改密码
                    self.$cancelBtn.click(function () {
                        history.back();
                    });

                    //密码强度的实时监测
                    self.$newPassWord.bind('input propertychange', function () {

                        var value = self.$newPassWord.val();
                        var strongRegex = new RegExp("^(?=.{8,})(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*\\W).*$", "g");
                        var mediumRegex = new RegExp("^(?=.{9,})(((?=.*[A-Z])(?=.*[a-z]))|((?=.*[A-Z])(?=.*[0-9]))|((?=.*[a-z])(?=.*[0-9]))|((?=.*[a-z])(?=.*\\W))|((?=.*[0-9])(?=.*\\W))).*$");
                        var enoughRegex = new RegExp("(?=.{8,}).*");

                        if(Util.isStrEmpty(value)){
                            self.$intensionMiddle.removeClass('s-bc4');
                            self.$intensionPowerful.removeClass('s-bc12');
                            self.$intensionWeak.removeClass('s-bc13');
                            return ;
                        }
                        if (false == enoughRegex.test(value)) {
                            self.$intensionMiddle.removeClass('s-bc4');
                            self.$intensionPowerful.removeClass('s-bc12');
                            self.$intensionWeak.addClass('s-bc13');
                            return;
                            //密码小于8位，强度：弱
                        }
                        else if (strongRegex.test(value)) {
                            self.$intensionMiddle.addClass('s-bc4');
                            self.$intensionPowerful.addClass('s-bc12');
                            return;
                            //密码为8位及以上并且大、小字母、数字、特殊字符三项都包括,强度：强
                        }
                        else if (mediumRegex.test(value)) {
                            self.$intensionPowerful.removeClass('s-bc12');
                            self.$intensionMiddle.addClass('s-bc4');
                            return;
                            //密码为8位及以上并且字母、数字、特殊字符三项中有两项，强度：中
                        }
                        else {
                            self.$intensionMiddle.removeClass('s-bc4');
                            self.$intensionPowerful.removeClass('s-bc12');
                            self.$intensionWeak.addClass('s-bc13');
                            return;
                            //如果密码为8位以下，就算字母、数字、特殊字符三项都包括，强度：弱
                        }
                    });

                    self.$pwBtn.click(function () {
                        var newPassWord = self.$newPassWord.val();
                        if(Util.isStrEmpty(newPassWord)){
                            self.$intensionMiddle.removeClass('s-bc4');
                            self.$intensionPowerful.removeClass('s-bc12');
                            self.$intensionWeak.removeClass('s-bc13');
                        }
                    })

                    function dataValidation(idCode) {

                        var dataModel = $.DataModel.init();
                        var userName = self.$userName.val();
                        var passWord = self.$oldPassWord.val();
                        var newPassWord = self.$newPassWord.val();

                        if (Util.isStrEquals($(idCode).attr("id"), "inp_old_passWord")) {

                            dataModel.fetchRemote("${contextRoot}/login/userVerification", {
                                data: {userName: userName, password: passWord},
                                async: false,
                                success: function (data) {
                                    if (data.successFlg) {
                                        result.setResult(true);
                                        sessionStorage.removeItem("errorPassWord")
                                        errorPassWords = 0;
                                    } else {
                                        sessionStorage.setItem("errorPassWord", parseInt(errorPassWords) + 1);
                                        errorPassWords = sessionStorage.getItem("errorPassWord");
                                        errorPassWord(errorPassWords);
                                        result.setResult(false);
                                        result.setErrorMsg(data.errorMsg);
                                    }
                                }
                            });
                            return result;
                        }
                        if (Util.isStrEquals($(idCode).attr("id"), "inp_new_passWord")) {
//                            var reg = /^[a-zA-Z]{8,16}$/;
                            if (Util.isStrEquals(passWord, newPassWord)) {
                                return ValidationErrorMsg(false, "密码与原始密码相近，请重新输入！");
                            }
                            if (newPassWord.length < 8 || newPassWord.length > 16) {
                                return ValidationErrorMsg(false, "输入值的长度应该在8 至 16之间，当前长度" + newPassWord.length + "！");
                            }
//                            if(newPassWord.split(" ").length>=1){
//                                return ValidationErrorMsg(false, "不能输入空格！");
//                            }
                            if (Util.isStrEquals(userName, newPassWord)) {
                                return ValidationErrorMsg(false, "新密码与用户名不能一样！");
                            }
                        }
                    }

                    function ValidationErrorMsg(bo, errorMsg) {
                        result.setResult(bo);
                        result.setErrorMsg(errorMsg);
                        return result;
                    }

                    function errorPassWord(inpNum) {
                        if (Util.isStrEmpty(errorPassWords)) {
                            errorPassWords = 0;
                        }
                        if (errorPassWords >= 5 || inpNum >= 5) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/user/activityUser", {
                                data: {userId: userId, activated: 0},
                                success: function (data) {
                                    $.ligerDialog.waitting('错误密码输入次数过多，该账户已被锁定，请通过OA至管理员重置密码！');
                                    setTimeout(function () {
                                        window.location.href = "${contextRoot}/login";
                                    }, 3000);
                                }
                            })
                        }
                    }
                }
            };

            /* ************************* 模块初始化结束 ************************** */


            /* *************************** 页面初始化 **************************** */

            pageInit();

            /* ************************* 页面初始化结束 ************************** */
        });
    })(jQuery, window);
</script>