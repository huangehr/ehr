<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
    (function ($, win) {

        /* ************************** 变量定义 ******************************** */
        // 通用工具类库
        var Util = $.Util;

        // 页面主模块，对应于用户信息表区域
        var patientInfo = null;

        // 表单校验工具类
        var jValidation = $.jValidation;
        var dialog = frameElement.dialog;
        var dataModel = $.DataModel.init();
        var patientModel = "";
        var patientDialogType = '${patientDialogType}';
        if (!(Util.isStrEquals(patientDialogType, 'addPatient'))) {
            patientModel =${patientModel}.obj;
        }
        /* ************************** 变量定义结束 ******************************** */

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            patientInfo.init();
        }

        /* *************************** 函数定义结束******************************* */

        /* *************************** 模块初始化 ***************************** */
        patientInfo = {
            $form: $("#div_patient_info_form"),
            $realName: $("#inp_realName"),
            $idCardNo: $("#inp_idCardNo"),
            $gender: $('input[name="gender"]', this.$form),
            $patientNation: $("#inp_patientNation"),
            $patientNativePlace: $("#inp_patientNativePlace"),
            $patientMartialStatus: $("#inp_select_patientMartialStatus"),
            $patientBirthday: $("#inp_patientBirthday"),
            $birthPlace: $("#inp_birthPlace"),
            $homeAddress: $("#inp_homeAddress"),
            $workAddress: $("#inp_workAddress"),
            $residenceType: $('input[name="residenceType"]', this.$form),
            $patientTel: $("#inp_patientTel"),
            $patientEmail: $("#inp_patientEmail"),
            $updateBtn: $("#div_update_btn"),
            $cancelBtn: $("#div_cancel_btn"),
            $resetArea: $("#reset_password"),
            $resetPassword: $("#div_resetPassword"),
            $patientImgUpload: $("#div_patient_img_upload"),
            $patientCopyId:$("#inp_patientCopyId"),
            $picPath:$('#div_file_list'),

            init: function () {
                var self = this;
                self.$gender.eq(0).attr("checked",'true');
                self.initForm();
                self.bindEvents();

                self.$patientImgUpload.instance = self.$patientImgUpload.webupload({
                    server: "${contextRoot}/patient/updatePatient",
                    pick: {id:'#div_file_picker'},
                    accept: {
                        title: 'Images',
                        extensions: 'gif,jpg,jpeg,bmp,png',
                        mimeTypes: 'image/*'
                    },
//                    formData:{msg:"upload"},
                    auto: false,
                    async:false
                });

                self.$patientImgUpload.instance.on( 'uploadSuccess', function( file, resp ) {
                    $('#inp_img_url').val(resp.url);
                    $( '#'+file.id ).addClass('upload-state-done');
                    $.ligerDialog.alert("保存成功",function () {
                        dialog.close();
                    });
                });
            },
            initForm: function () {
                var self = this;
                self.$realName.ligerTextBox({width: 240});
                self.$idCardNo.ligerTextBox({width: 240});
                self.$gender.ligerRadio();
                self.initDDL(4,this.$patientMartialStatus);
                self.initDDL(5, this.$patientNation);
                self.$patientNation.ligerTextBox({width: 240});
                self.$patientNativePlace.ligerTextBox({width: 240});
                self.$patientBirthday.ligerDateEditor({format: "yyyy-MM-dd"});
                self.initAddress(self.$birthPlace);
                self.initAddress(self.$homeAddress);
                self.initAddress(self.$workAddress);
                self.$residenceType.ligerRadio();
                self.$patientTel.ligerTextBox({width: 240});
                self.$patientEmail.ligerTextBox({width: 240});
                self.$resetArea.hide();
                self.$form.attrScan();
                if (!(Util.isStrEquals(patientDialogType, 'addPatient'))) {
                    self.$resetArea.show();
                    var birthPlaceInfo = patientModel.birthPlaceInfo;
                    var homeAddressInfo = patientModel.homeAddressInfo;
                    var workAddressInfo = patientModel.workAddressInfo;
                    self.$form.Fields.fillValues({
                        name: patientModel.name,
                        idCardNo: patientModel.idCardNo,
                        gender: patientModel.gender,
                        nativePlace: patientModel.nativePlace,
                        birthday: patientModel.birthday,
                        birthPlaceInfo: birthPlaceInfo&&[birthPlaceInfo.province,birthPlaceInfo.city,birthPlaceInfo.district,birthPlaceInfo.street],
                        homeAddressInfo: homeAddressInfo&&[homeAddressInfo.province,homeAddressInfo.city,homeAddressInfo.district,homeAddressInfo.street] ,
                        workAddressInfo: workAddressInfo&&[workAddressInfo.province,workAddressInfo.city,workAddressInfo.district,workAddressInfo.street],
                        residenceType: patientModel.residenceType,
                        telephoneNo: patientModel.telephoneNo,
                        email: patientModel.email
                    });
                    self.$patientCopyId.val(patientModel.idCardNo);

                   var pic = patientModel.localPath;
                    if(!(Util.isStrEquals(pic,null)||Util.isStrEquals(pic,""))){
                        self.$picPath.html('<img src="${contextRoot}/patient/showImage?localImgPath='+pic+'" class="f-w88 f-h110"></img>');
                    }
                }
            },
            initDDL: function (dictId, target) {
                var self = this;
                target.ligerComboBox({
                    url: "${contextRoot}/dict/searchDictEntryList",
                    dataParmName: 'detailModelList',
                    urlParms: {dictId: dictId},
                    valueField: 'code',
                    textField: 'value',
                    autocomplete:true,
                    onSuccess: function () {
                        self.$form.Fields.fillValues({nation: patientModel.nation});
                        self.$form.Fields.fillValues({martialStatus: patientModel.martialStatus});
                    }
                });
            },
            initAddress: function (target){
                target.addressDropdown({tabsData:[
                    {name: '省份',code:'id',value:'name', url: '${contextRoot}/address/getParent', params: {level:'1'}},
                    {name: '城市',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '县区',code:'id',value:'name', url: '${contextRoot}/address/getChildByParent'},
                    {name: '街道',maxlength: 200}
                ]});
            },

            bindEvents: function () {
                var self = this;
                $(".u-dropdown-icon").click(function(){
                    $('#inp_realName').click();
                });
                var idCardNo = self.$form.Fields.idCardNo.getValue();
                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,onElementValidateForAjax:function(elm){
                    if(Util.isStrEquals($(elm).attr('id'),'inp_idCardNo')){
                        var copyCardNo = self.$patientCopyId.val();
                        var result = new jValidation.ajax.Result();
                        var idCardNo = self.$idCardNo.val();
                        var dataModel = $.DataModel.init();
                        if(Util.isStrEquals(idCardNo,copyCardNo)){
                            return true;
                        }
                        dataModel.fetchRemote("${contextRoot}/patient/checkIdCardNo", {
                            data: {searchNm:idCardNo},
                            async: false,
                            success: function (data) {
                                if (!data.successFlg) {
                                    result.setResult(true);
                                } else {
                                    result.setResult(false);
                                    result.setErrorMsg("该身份证已被使用");
                                }
                            }
                        });
                        return result;
                    }
                }});

                //修改人口信息
                patientInfo.$updateBtn.click(function () {
                 var picHtml = self.$picPath.children().length;
                 if(validator.validate()){
                    var addressList = self.$form.Fields.birthPlaceInfo.getValue();
                    var homeAddressList = self.$form.Fields.homeAddressInfo.getValue();
                    var workAddressList = self.$form.Fields.workAddressInfo.getValue();
                    var values = $.extend({},self.$form.Fields.getValues(),{
                        birthPlaceInfo: {
                            province:  addressList.names[0] || null,
                            city: addressList.names[1] || null,
                            district: addressList.names[2] || null,
                            street: addressList.names[3] || null
                        },
                        homeAddressInfo:{
                            province:  homeAddressList.names[0] || null,
                            city: homeAddressList.names[1] || null,
                            district: homeAddressList.names[2] || null,
                            street: homeAddressList.names[3] || null
                        },
                        workAddressInfo:{
                            province:  workAddressList.names[0] || null,
                            city: workAddressList.names[1] || null,
                            district: workAddressList.names[2] || null,
                            street: workAddressList.names[3] || null
                        }
                    });
                     var jsonData = JSON.stringify(values)+";"+patientDialogType;
//                     values.patientDialogType = patientDialogType;
                    if(picHtml == 0){
                        updatePatient(jsonData);
//                        updatePatient(JSON.stringify(values));
                    }else{
                        var upload = self.$patientImgUpload.instance;
                        var image = upload.getFiles().length;
                        if(image){
                            upload.options.formData.patientJsonData =   encodeURIComponent(jsonData);
                            upload.upload();
                            win.parent.patientDialogRefresh();
                        }else{
                            updatePatient(jsonData);
                        }
                    }
                  }else{
                    return
                    }
                });
                function updatePatient(patientJsonData){
                    dataModel.updateRemote("${contextRoot}/patient/updatePatient", {
                        data: {patientJsonData:patientJsonData,patientDialogType:patientDialogType},
                        success: function (data) {
                            if(data.successFlg){
                                if (Util.isStrEquals(patientDialogType, 'addPatient')){
                                    win.parent.$.Notice.success('新增成功');
                                }else{
                                    win.parent.$.Notice.success('修改成功');
                                }
                            }else{
                                if (Util.isStrEquals(patientDialogType, 'addPatient')){
                                    win.parent.$.Notice.error('新增失败');
                                }else{
                                    win.parent.$.Notice.error('修改失败');
                                }
                            }
                            win.parent.patientDialogRefresh();
                            dialog.close();
                        }
                    })
                }
                //重置密码
                patientInfo.$resetPassword.click(function () {
                    var patientIdCardNo = self.$form.Fields.idCardNo.getValue();
                    $.ligerDialog.confirm('确认重置密码？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
                        if (yes) {
                            var dataModel = $.DataModel.init();
                            dataModel.updateRemote("${contextRoot}/patient/resetPass", {
                                data: {idCardNo: patientIdCardNo},
                                success: function (data) {
                                    if (data.successFlg) {
                                        win.parent.$.Notice.success('密码修改成功');
                                    } else {
                                        win.parent.$.Notice.error('密码修改失败');
                                    }
                                    //dialog.close();
                                }
                            })
                        }
                    });


                });
                //关闭dailog的方法
                patientInfo.$cancelBtn.click(function(){
                    dialog.close();
                })
            }
        };
        /* *************************** 模块初始化结束 ***************************** */

        /* *************************** 页面初始化 **************************** */
        pageInit();
        /* *************************** 页面初始化结束 **************************** */


    })(jQuery, window);
</script>