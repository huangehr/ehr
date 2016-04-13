<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

    (function ($, win) {

        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;

        // 表单校验工具类
        var jValidation = $.jValidation;

        var addUserInfo = null;

        var dialog = frameElement.dialog;


        /* ************************** 变量定义结束 **************************** */

        /* *************************** 函数定义 ******************************* */
        /**
         * 页面初始化。
         * @type {Function}
         */
        function pageInit() {
            addUserInfo.init();
            $("#div_addUser_form").show();
        }

        /* ************************** 函数定义结束 **************************** */

        /* *************************** 模块初始化 ***************************** */

        addUserInfo = {
            $form: $("#div_addUser_form"),
            $loginCode: $("#inp_loginCode"),
            $userName: $('#inp_userName'),
            $idCard: $('#inp_idCard'),
            $userEmail: $('#inp_userEmail'),
            $userTel: $('#inp_userTel'),
            $org: $('#inp_org'),
            $major: $('#inp_major'),
            $sex: $('input[name="gender"]', this.$form),
            $uploader: $("#div_user_img_upload"),
            $inp_select_marriage: $("#inp_select_marriage"),
            $inp_select_userType: $("#inp_select_userType"),
            $addUserBtn: $("#div_btn_add"),
            $cancelBtn: $("#div_cancel_btn"),
            $imageShow:$("#div_file_list"),
            init: function () {
                var self = this;
                self.$sex.eq(0).attr("checked", 'true');
                self.initForm();
                self.bindEvents();
                self.$uploader.instance = self.$uploader.webupload({
                    server: "${contextRoot}/user/updateUser",
                    pick: {id:'#div_file_picker'},
                    accept: {
                        title: 'Images',
                        extensions: 'gif,jpg,jpeg,bmp,png',
                        mimeTypes: 'image/*'
                    },
                    auto: false
                });
                self.$uploader.instance.on( 'uploadSuccess', function( file, resp ) {
                    $.ligerDialog.alert("保存成功",function () {
                        win.parent.closeAddUserInfoDialog(function () {
                            win.parent.$.Notice.success('用户新增成功');
                        });
                    });
                });
            },
            initForm: function () {
                this.$loginCode.ligerTextBox({width: 240});
                this.$userName.ligerTextBox({width: 240});
                this.$idCard.ligerTextBox({width: 240});
                this.$userEmail.ligerTextBox({width: 240});
                this.$userTel.ligerTextBox({width: 240});
                this.$major.ligerTextBox({width: 240});
                this.$sex.ligerRadio();
                this.$org.addressDropdown({
                    tabsData: [
                        {name: '省份', code:'id',value:'name',url: '${contextRoot}/address/getParent', params: {level: '1'}},
                        {name: '城市', code:'id',value:'name',url: '${contextRoot}/address/getChildByParent'},
                        {
                            name: '医院', code:'organizationCode',value:'fullName',url: '${contextRoot}/address/getOrgs', beforeAjaxSend: function (ds, $options) {
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
                var select_marriage = this.$inp_select_marriage.ligerComboBox({
                    url: '${contextRoot}/dict/searchDictEntryList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    urlParms: {
                        dictId: 4
                    },
                    autocomplete: true,
                    onSuccess: function (data) {
                        if (data.length > 0) {
                            select_marriage.setValue(data[0].code);
                        }
                    }
                });
                var select_user_type = this.$inp_select_userType.ligerComboBox({
                    url: '${contextRoot}/dict/searchDictEntryList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    urlParms: {
                        dictId: 15
                    },
                    onSelected: function (value) {
                        if(value=='Doctor')
                            $('#inp_major_div').show();
                        else
                            $('#inp_major_div').hide();
                    },
                    //autocomplete: true,
                    onSuccess: function (data) {
                        if (data.length > 0) {
                            select_user_type.setValue(data[0].code);
                        }
                    }
                });

                this.$form.attrScan();
            },

            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$form, {immediate:true,onSubmit:false,
                   onElementValidateForAjax:function(elm){
                        if(Util.isStrEquals($(elm).attr("id"),'inp_loginCode')){
                            var loginCode = $("#inp_loginCode").val();
                            return checkDataSourceName('login_code',loginCode,"该账号已存在");
                        }
                        if(Util.isStrEquals($(elm).attr("id"),'inp_idCard')){
                            var idCard = $("#inp_idCard").val();
                            return checkDataSourceName('id_card_no',idCard,"该身份证号已被注册，请确认。");
                        }
                       if(Util.isStrEquals($(elm).attr("id"),'inp_userEmail')){
                           var email = $("#inp_userEmail").val();
                           return checkDataSourceName('email',email,"该邮箱已存在");
                       }

                   }
                });
                //唯一性验证--账号/身份证号(字段名、输入值、提示信息）
                function checkDataSourceName(type,inputValue,errorMsg){
                    var result = new jValidation.ajax.Result();
                    var dataModel = $.DataModel.init();
                    dataModel.fetchRemote("${contextRoot}/user/existence", {
                        data: {existenceType:type,existenceNm:inputValue},
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

                //新增的点击事件
                this.$addUserBtn.click(function () {
                    var userImgHtml = self.$imageShow.children().length;
                    var addUser = self.$form.Fields.getValues();
                   if(validator.validate()){
                        var organizationKeys = addUser.organization['keys'];
//                        addUser.organizationCode = organizationKeys[2];
//                        addUser.orgName = addUser.organization['names'][2];
                    addUser.organization = organizationKeys[2];
                    if (userImgHtml == 0) {
                        updateUser(addUser);
                    } else {
                        var upload = self.$uploader.instance;
                        var image = upload.getFiles().length;
                        if (image) {
                            upload.options.formData.userModelJsonData = encodeURIComponent(JSON.stringify(addUser));
                            upload.upload();
                        } else {
                            updateUser(addUser);
                        }
                    }

                    }else{
                   return;
                   }


                });

                function updateUser(userModel) {
                    var userModelJsonData = JSON.stringify(userModel);
                    var dataModel = $.DataModel.init();
                    dataModel.updateRemote("${contextRoot}/user/updateUser", {
                        data: {userModelJsonData:userModelJsonData},
                        success: function (data) {
                            if (data.successFlg) {
                                win.parent.closeAddUserInfoDialog(function () {
                                    win.parent.$.Notice.success('用户新增成功');
                                });
                            } else {
                                window.top.$.Notice.error(data.errorMsg);
                            }
                        }
                    })
                }

                self.$cancelBtn.click(function () {
                    dialog.close();
                });
            }

        };

        /* ************************* 模块初始化结束 ************************** */

        /* *************************** 页面初始化 **************************** */

        pageInit();

        /* ************************* 页面初始化结束 ************************** */

    })(jQuery, window);
</script>