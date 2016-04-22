<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

    (function ($, win) {

        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;
        var userInfo = null;

        //修改用户变量
        var userModel = null;

        //公钥管理弹框
        var publicKeyMsgDialog = null;

        // 表单校验工具类
        var jValidation = $.jValidation;

        var allData = ${allData};
        var user = allData.obj;


        /* ************************** 变量定义结束 **************************** */

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            userInfo.init();
        }

        /* ************************** 函数定义结束 **************************** */

        /* *************************** 模块初始化 ***************************** */
        userInfo = {
            /*----------yww--1217*/
            $idCardCopy: $('#idCardCopy'),
            $emailCopy: $('#emailCopy'),

            $form: $("#div_user_info_form"),
            $loginCode: $("#inp_loginCode"),
            $realName: $('#inp_user_name'),
            $idCard: $('#inp_idCard'),
            $email: $('#inp_userEmail'),
            $tel: $('#inp_userTel'),
            $org: $('#inp_org'),
            $major: $('#inp_major'),
            $userSex: $('input[name="gender"]', this.$form),
            $marriage: $("#inp_select_marriage"),
            $userType: $("#inp_select_userType"),
            $updateUserDtn: $("#div_update_btn"),
            $cancelBtn: $("#div_cancel_btn"),
            $resetPassword: $("#div_resetPassword"),
            $publicKey: $("#div_publicKey"),
            $uploader: $("#div_user_img_upload"),
            $emailRelieve: $("#div_emailRelieve"),
            $telRelieve: $("#div_telRelieve"),
            $publicManage: $("#div_public_manage"),
            $allotpublicKey: $("#div_allot_publicKey"),
            $publicKeyMessage: $("#txt_publicKey_message"),
            $publicKeyValidTime: $("#lbl_publicKey_validTime"),
            $publicKeyStartTime: $("#lbl_publicKey_startTime"),
            $selectPublicKeyMessage: $("#div_publicKeyMessage"),
            $selectPublicKeyValidTime: $("#div_publicKey_validTime"),
            $selectPublicKeyStartTime: $("#div_publicKey_startTime"),
            $filePicker: $("#div_file_picker"),
            $affirmBtn: $('#div_affirm_btn'),
            $toolbar: $('#div_toolbar'),
            $imageShow: $('#div_file_list'),

            init: function () {
                var self = this;
                self.initForm();
                self.bindEvents();
                //self.$uploader.webupload();
                self.$uploader.instance = self.$uploader.webupload({
                    server: "${contextRoot}/user/updateUser",
                    pick: {id: '#div_file_picker'},
                    accept: {
                        title: 'Images',
                        extensions: 'gif,jpg,jpeg,bmp,png',
                        mimeTypes: 'image/*'
                    },
                    auto: false
                });
            },
            initForm: function () {
                var self = this;
                this.$form.removeClass("m-form-readonly");
                this.$loginCode.ligerTextBox({width: 240});
                this.$realName.ligerTextBox({width: 240});
                this.$idCard.ligerTextBox({width: 240});
                this.$email.ligerTextBox({width: 240});
                this.$tel.ligerTextBox({width: 240});
                this.$org.addressDropdown({
                    tabsData: [
                        {
                            name: '省份',
                            code: 'id',
                            value: 'name',
                            url: '${contextRoot}/address/getParent',
                            params: {level: '1'}
                        },
                        {name: '城市', code: 'id', value: 'name', url: '${contextRoot}/address/getChildByParent'},
                        {
                            name: '医院',
                            code: 'organizationCode',
                            value: 'fullName',
                            url: '${contextRoot}/address/getOrgs',
                            beforeAjaxSend: function (ds, $options) {
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
                this.$major.ligerTextBox({width: 240});
                this.$userSex.ligerRadio();
                this.$marriage.ligerComboBox({
                    url: '${contextRoot}/dict/searchDictEntryList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    urlParms: {
                        dictId: 4
                    },
                    onSuccess: function () {
                        self.$form.Fields.fillValues({martialStatus: user.martialStatus});
                    }
                });

                this.$userType.ligerComboBox({
                    url: '${contextRoot}/dict/searchDictEntryList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    urlParms: {
                        dictId: 15
                    },
                    onSuccess: function () {
                        debugger
                        self.$form.Fields.fillValues({userType: user.userType});
                        self.$userType.parent().removeClass('l-text-focus')
//                        self.$form.Fields.fillValues({martialStatus: user.martialStatus});
                    },
                    onSelected: function (value) {
                        if (value == 'Doctor')
                            $('#inp_major_div').show();
                        else
                            $('#inp_major_div').hide();
                    }
                });
                this.$form.attrScan();
                this.$form.Fields.fillValues({
                    id: user.id,
                    loginCode: user.loginCode,
                    realName: user.realName,
                    idCardNo: user.idCardNo,
                    gender: user.gender,
                    email: user.email,
                    telephone: user.telephone,
                    organization: [user.province, user.city, user.organization],
                    major: user.major,
                    publicKey: user.publicKey,
                    validTime: user.validTime,
                    startTime: user.startTime
                });
                self.$publicKeyMessage.val(user.publicKey);
                self.$publicKeyValidTime.html(user.validTime);
                self.$publicKeyStartTime.html(user.startTime);
                /*---------yww*/
                self.$idCardCopy.val(user.idCardNo);
                self.$emailCopy.val(user.email);

                debugger
                var pic = user.imgLocalPath;
                if (!Util.isStrEmpty(pic)) {
                    self.$imageShow.html('<img src="${contextRoot}/user/showImage?localImgPath=' + pic + '" class="f-w88 f-h110"></img>');
                }

                if ('${mode}' == 'view') {
                    this.$form.addClass("m-form-readonly");
                    this.$resetPassword.hide();
                    this.$publicKey.hide();
                    this.$updateUserDtn.hide();
                    this.$cancelBtn.hide();
                    this.$filePicker.hide();
                    this.$toolbar.hide();
                    this.$selectPublicKeyMessage.show();
                    this.$selectPublicKeyValidTime.show();
                    this.$selectPublicKeyStartTime.show();
                }
            },

            bindEvents: function () {
                var self = this;
                var validator = new jValidation.Validation(this.$form, {
                    immediate: true, onSubmit: false,
                    onElementValidateForAjax: function (elm) {
                        if (Util.isStrEquals($(elm).attr("id"), 'inp_userEmail')) {
                            var email = $("#inp_userEmail").val();
                            var emailCopy = self.$emailCopy.val();
                            if (emailCopy != null && emailCopy != '' && emailCopy == email) {
                                return true;
                            }
                            return checkDataSourceName('email', email, "该邮箱已被绑定，请确认。");
                        }
                        if (Util.isStrEquals($(elm).attr("id"), 'inp_idCard')) {
                            var idCard = $("#inp_idCard").val();
                            var idCardCopy = self.$idCardCopy.val();
                            if (idCardCopy != null && idCardCopy != '' && idCardCopy == idCard) {
                                return true;
                            }
                            return checkDataSourceName('id_card_no', idCard, "该身份证号已被注册，请确认。");
                        }
                    }
                });
                //唯一性验证身份证/邮箱
                function checkDataSourceName(type, searchNm, errorMsg) {
                    var result = new jValidation.ajax.Result();
                    var dataModel = $.DataModel.init();
                    dataModel.fetchRemote("${contextRoot}/user/existence", {
                        data: {existenceType: type, existenceNm: searchNm},
                        async: false,
                        success: function (data) {
                            if (data.successFlg) {
                                result.setResult(false);
                                result.setErrorMsg(errorMsg);
                            } else {
                                result.setResult(true);
                            }
                        }
                    });
                    return result;
                }

                //修改用户的点击事件
                this.$updateUserDtn.click(function () {

                    var userImgHtml = self.$imageShow.children().length;
                    if (validator.validate()) {
                        userModel = self.$form.Fields.getValues();
                        var organizationKeys = userModel.organization['keys'];

                        userModel.organization = organizationKeys[2];
                        if (userImgHtml == 0) {
                            updateUser(userModel);
                        } else {
                            var upload = self.$uploader.instance;
                            var image = upload.getFiles().length;
                            if (image) {
                                upload.options.formData.userModelJsonData = encodeURIComponent(JSON.stringify(userModel));
                                upload.upload();
                                win.closeUserInfoDialog();
                                win.reloadMasterUpdateGrid();
                            } else {
                                updateUser(userModel);
                            }
                        }
                    } else {
                        return;
                    }
                });

                function updateUser(userModel) {
                    var userModelJsonData = JSON.stringify(userModel);
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/user/updateUser", {
                        data: {userModelJsonData: userModelJsonData},
                        success: function (data) {
                            if (data.successFlg) {
                                win.closeUserInfoDialog();
                                win.reloadMasterUpdateGrid();
                                $.Notice.success('修改成功');
                            } else {
                                $.Notice.error('修改失败');
                            }
                        }
                    })
                }

                //重置密码的点击事件
                this.$resetPassword.click(function () {
                    var userModelres = self.$form.Fields.getValues();
                    $.ligerDialog.confirm('确认重置密码？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {

                        if (yes) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote('${contextRoot}/user/resetPass', {
                                title: 'asd',
                                data: {userId: userModelres.id},
                                success: function (data) {
                                    if (data.successFlg)
                                    //重置当前用户密码，需重登
                                        if ((userModelres.id) == (data.obj)) {
                                            $.Notice.success('重置成功，请重新登录!', function () {
                                                location.href = '${contextRoot}/logout';
                                            });
                                        } else {
                                            $.Notice.success('重置成功!');
                                        }
                                    else
                                        $.Notice.error('重置失败!');
                                },
                                error: function () {
                                    $.Notice.error('重置失败!');
                                }
                            });
                        }
                    });
                });
                //公钥管理窗口点击事件

                this.$publicKey.click(function () {
                    publicKeyMsgDialog = $.ligerDialog.open({
                        title: '公钥管理',
                        width: 416,
                        height: 320,
                        target: self.$publicManage
                    });
                    $('#div_affirm_btn').click(function () {
                        publicKeyMsgDialog.close();
                    });
                    //分配公钥点击事件
                    self.$allotpublicKey.click(function () {
                        var code = self.$form.Fields.loginCode.getValue();
                        var dataModel = $.DataModel.init();
                        dataModel.createRemote('${contextRoot}/user/distributeKey', {
                            data: {loginCode: code},
                            success: function (data) {
                                var keyData = $.parseJSON(data.obj);
                                self.$publicKeyMessage.val(keyData.publicKey);
                                self.$publicKeyValidTime.html(keyData.validTime);
                                self.$publicKeyStartTime.html(keyData.startTime);
                                $.ligerDialog.alert('分配公钥成功');
                            }
                        });
                    });
                });

                this.$cancelBtn.click(function () {
                    win.closeUserInfoDialog();
                });
                this.$affirmBtn.click(function () {
                    publicKeyMsgDialog.close();
                })
                self.$userType.removeClass("l-text-focus")
            }
        };

        /* ************************* 模块初始化结束 ************************** */

        /* *************************** 页面初始化 **************************** */

        pageInit();
        /* ************************* 页面初始化结束 ************************** */

    })(jQuery, window);
</script>