<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">
    (function ($, win) {
        /* ************************** 变量定义 ******************************** */
        var Util = $.Util;
        var infoForm = null;
        var selForm = null;
        var jValidation = $.jValidation;
        var mode = '${mode}';
        var initType = '${initType}';
        if(!Util.isStrEquals(mode,'new')){
            var info = JSON.parse('${info}');
        }

        /* *************************** 函数定义 ******************************* */
        function pageInit() {
            infoForm.init();
            selForm.init();
        }

        /* *************************** 模块初始化 ***************************** */
        infoForm = {
            $form: $("#div_adapterorg_info_form"),
            $type: $("#inp_adapterorg_type"),
            $org: $("#inp_adapterorg_org"),
            $area: $("#inp_adapterorg_area"),
            $province: $("#province"),
            $city: $("#city"),
            $district: $("#district"),
            $town: $("#town"),
            $areaCode: $("#area_code"),
            $name: $("#inp_adapterorg_name"),
            $parent: $("#inp_adapterorg_parent"),
            $description: $("#inp_adapterorg_description"),
            $code: $("#code"),

            $btnSave: $("#btn_save_adapterOrg"),
            $btnCancel: $("#btn_cancel_adapterOrg"),

            orgCodeCombo : null,
            adapterOrgCombo : null,
            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                if(initType == 3){
                    $("#areaDiv").css('display','block');
                }
                this.$area.ligerComboBox({valueField: 'id',textField: 'name',readonly:mode=='modify',
                    onBeforeOpen: function () {
                        selForm.show(true);
                    }});
                this.$type.ligerComboBox({valueField: 'id',textField: 'name',readonly:mode=='modify'});
                infoForm.initOrg(infoForm.$org, "");
                infoForm.initAdapterOrg(infoForm.$parent, "");
                this.initDDL(21, this.$type);
                this.$name.ligerTextBox({width:240,validate:{required:true }});
                this.$description.ligerTextBox({width:240,height:180 });

                this.$form.attrScan();
                if(!Util.isStrEmpty(info)){

                    var adapter = info.obj;

//                    var province = adapter.address.province || '';
//                    var city =adapter.address.city || '';
//                    var district =adapter.address.district || '';
//                    var town =adapter.address.town || '';

                    var province = "省";
                    var city ="市";
                    var district ="县";
                    var town ="街道/地址测试";
                    var area=province;
                    if (province!=city){
                        area +=city;
                    }
                    area+=district+town;
                    this.$area.ligerGetComboBoxManager().setValue(area);
                    this.$area.ligerGetComboBoxManager().setText(area);
                    this.$form.Fields.fillValues({
                        code:adapter.code,
                        name:adapter.name,
                        description:adapter.description ,
                        province : province ,
                        city :city,
                        district : district,
                        town : town,
                    });
                }

                this.$form.show();
            },
            initDDL: function (dictId, target) {
                var dataModel = $.DataModel.init();
                var url = "${contextRoot}/dict/searchDictEntryList";
                dataModel.fetchRemote(url,{
                    data: {dictId: dictId, page: "1", rows: "5"},
                    success: function(data) {
                        target.ligerComboBox({
                            selectBoxHeight:220,
                            valueField: 'code',
                            textField: 'value',
                            allowBlank: false,
                            data: data.detailModelList,
                            onSelected : function (value, text) {
                                var type = value;
                                if(type==1){
                                    //厂商，初始标准只能是厂商
                                    infoForm.initAdapterOrg(infoForm.$parent, type);
                                    $("#areaDiv").css('display','none');
                                }
                                else if(type==2){
                                    //医院，初始标准没有限制
                                    infoForm.initAdapterOrg(infoForm.$parent, type);
                                    $("#areaDiv").css('display','none');
                                }
                                else if(type==3){
                                    //区域,初始标准只能选择厂商或区域
                                    infoForm.initAdapterOrg(infoForm.$org, '1');
                                    $("#areaDiv").css('display','block');
                                }
                                infoForm.initOrg(infoForm.$org, type);
                            }
                        });
                        var manager = target.ligerGetComboBoxManager();
                        if(!Util.isStrEmpty(info)){
                            var type = info.obj.type;
                            if(type)
                                manager.selectValue(type);
                            else
                                manager.selectItemByIndex(0);
                        }else if(initType){
                            manager.selectValue(initType);
                        }
                    }});


            },
            getGridOp : function (columns,url) {
                var options = {
                    columns: columns,
                    allowAdjustColWidth : true,
                    editorTopDiff : 41,
                    headerRowHeight : 0,
                    heightDiff : 0,
                    pageSize: 15,
                    pagesizeParmName : 'rows',
                    record : "totalCount",
                    root : "detailModelList",
                    rowHeight : 30,
                    height:200,
                    rownumbers :false,
                    switchPageSizeApplyComboBox: false,
                    width :"98%",
                    url:url
                };
                return options;
            },
            initOrg : function (target,type){
                var url = "${contextRoot}/adapterorg/searchOrgList?type="+type;
                var gridOp = this.getGridOp([
                    {display : '名称', name :'fullName',width : 210}
                ],url) ;
                this.orgCodeCombo = target.ligerComboBox({
                    condition: { inputWidth: 90 ,width:0,labelWidth:0,hideSpace:true,fields: [{ name: 'param', label:''}] },//搜索框的字段, name 必须是服务器返回的字段
                    grid: gridOp,
                    valueField: 'orgCode',
                    textField: 'fullName',
                    selectBoxHeight : 300,
                    readonly:mode=='modify',
                    onSelected: function(id,name){
                        if(!name)
                            return;
                        target.val(name);
                        $("#inp_adapterorg_name").focus();
                        var area;
                        if((area=$('#inp_adapterorg_area').val())){
                            name += '(' + area + ')';
                        }
                        $('#inp_adapterorg_name').val(name);
                    },
                    conditionSearchClick: function (g) {
                        debugger;
                        var param = g.rules.length>0? g.rules[0].value : '';
                        param = {param:param }
                        g.grid.set({
                            parms: param
                        });
                        g.grid.options.newPage = 1;
                        g.grid.reload();
                    }
                });

                if(!Util.isStrEmpty(info)){
                    this.orgCodeCombo.setValue(info.obj.org);
                    this.orgCodeCombo.setText(info.obj.orgValue);
                }
                var grid = this.orgCodeCombo.grid;
                if(grid){
                    grid.set({
                        url: url,
                        newPage:1
                    });
                    grid.reload();
                }
             },
            initAdapterOrg : function (target, type) {

                var self = this;
                var url = "${contextRoot}/adapterorg/searchAdapterOrgList?type="+type;
                var gridOp = this.getGridOp([
                    {display : '名称', name :'name',width : 210}
                ],url) ;
                self.adapterOrgCombo = target.ligerComboBox({
                    condition: { inputWidth: 90 ,width:0,labelWidth:0,hideSpace:true,fields: [{ name: 'param', label:''}] },//搜索框的字段, name 必须是服务器返回的字段
                    grid: gridOp,
                    valueField: 'code',
                    textField: 'name',
                    width : 240,
                    selectBoxHeight : 200,
                    alwayShowInDown: true,
                    readonly:mode=='modify',
                    onSelected: function(id,name){//name为选择的值
                        target.val(name);//下拉框选择后文本框的值
                    },
                    conditionSearchClick: function (g) {
                        var param = g.rules.length>0? g.rules[0].value : '';
                        param = {param:param }
                        g.grid.set({
                            parms: param,
                            newPage : 1
                        });
                        g.grid.reload();
                    }
                });

                if(!Util.isStrEmpty(info)){
                    self.adapterOrgCombo.setValue(info.obj.parent);
                    self.adapterOrgCombo.setText(info.obj.parentValue);
                }
                var grid = this.adapterOrgCombo.grid;
                if(grid){
                    grid.set({
                        url: url
                    });
                    grid.reload();
                }
            },
            toJson : function (data) {
                var tmp = [];
                for(var i=0;i<data.length;i++){
                    tmp = data[i].split(',');
                    data[i] = {id:tmp[0],name:tmp[1]}
                }
                return data;
            },
            bindEvents: function () {
                var self = this;
                var validator =  new jValidation.Validation(this.$form, {immediate: true, onSubmit: false,
                    onElementValidateForAjax:function(elm){

                    }
                });
                this.$btnSave.click(function () {
                    var dataMode;
                    var values = self.$form.Fields.getValues();
                    var ajaxFun;
                    if(!validator.validate()){
                        return;
                    }
                    self.$btnSave.attr('disabled','disabled');
                    if(mode=='new'){
                        var code;
                        if(values.type==3 && values.area)
                            code = values.org + 'ORG' + values.areaCode;
                        else{
                            code = values.org;
                            values.province = '';
                            values.city = '';
                            values.district = '';
                            values.town = '';
                        }
                        dataMode = "type="+values.type+"&org="+values.org
                                +"&address.province="+values.province+"&address.city="+values.city+"&address.district="+values.district+"&address.town="+values.town
                                +"&code="+code+"&name="+values.name+"&parent="+values.parent+"&description="+values.description;
                        ajaxFun = 'addAdapterOrg';
                    }
                    else{
                        dataMode = {code:values.code, name: values.name, description:values.description}
                        ajaxFun = 'updateAdapterOrg';
                    }
                    var dataModel = $.DataModel.init();
                    var waittingDialog = $.ligerDialog.waitting('正在保存中,请稍候...');
                    dataModel.updateRemote("${contextRoot}/adapterorg/"+ajaxFun,{
                        data: dataMode,
                        success: function(data) {
                            waittingDialog.close();
                            if(data.successFlg){
                                var app = data.obj;
                                if(parent.reloadMasterGrid){
                                    parent.reloadMasterGrid();
                                    parent.closeDialog('保存成功！');
                                }
                                else{
                                    parent.adapterModel(self.$form.Fields.getValues());
                                    $.ligerDialog.alert("保存成功", "提示", "success", function(){
                                        parent.closeDialog();
                                    }, null);
                                }
                            }else{
                                if(data.errorMsg)
                                    $.Notice.error(data.errorMsg);
                                else
                                    $.Notice.error('出错了！');
                            }
                            self.$btnSave.removeAttr('disabled');
                        },
                        error: function () {
                            waittingDialog.close();
                            $.Notice.error('出错了！');
                            self.$btnSave.removeAttr('disabled');
                        }
                    });
                });

                this.$btnCancel.click(function () {
                    parent.closeDialog();
//                    dialog.close();
                });
            },
            setAreaInfo: function (province, city, district, town, areaCode) {
                this.$province.val(province);
                this.$city.val(city);
                this.$district.val(district);
                this.$town.val(town);
                this.$areaCode.val(areaCode);
                var area=province;
                if (province!=city){
                    area +=city;
                }
                area+=district+town;
                this.$area.ligerGetComboBoxManager().setValue(area);
                this.$area.ligerGetComboBoxManager().setText(area);

                var org = this.$org.ligerGetComboBoxManager().getText();
                if(area && org){
                    this.$name.val(org + '('+ area +')');
                }
            }
        };
        selForm = {
            selArea : $('#selArea'),
            $form:$('#area_sel_form'),
            $province: $("#sel_province"),
            $city: $("#sel_city"),
            $district: $("#sel_district"),
            $town: $("#sel_town"),

            $btnOk: $("#btn_ok_sel"),
            $btnCancel: $("#btn_cancel_sel"),

            init: function () {
                this.initForm();
                this.bindEvents();
            },
            initForm: function () {
                this.initDDL(1, this.$province);
                this.$city.ligerComboBox({valueField: 'id',textField: 'name',width:160});
                this.$district.ligerComboBox({valueField: 'id',textField: 'name',width:160});
                this.$town.ligerComboBox({valueField: 'id',textField: 'name',width:160});
                this.$form.show();
            },
            show : function (b, top, left) {
                if(b){
                    this.selArea.css('display','block');
                }
                else
                    this.selArea.css('display','none');
            },
            initDDL: function (dictId, target, pid) {
                var ajaxFunc = '';
                var parms = {};
                if(dictId==1){
                    ajaxFunc = 'getParent';
                    parms.level = 1;
                }
                else{
                    ajaxFunc = 'getChildByParent';
                    parms.pid = pid;
                }
                target.ligerComboBox({
                    valueField: 'id',
                    textField: 'value',
                    width:160,
                    allowBlank:true,
                    selectBoxHeight:200,
                    url :"${contextRoot}/address/"+ajaxFunc,
                    parms:parms,
                    dataGetter: function (result) {
                        var rs = [];
                        for(var p in result.obj){
                            if(p)
                                rs.push({id:result.obj[p].id,value:result.obj[p].name})
                        }
                        return rs;
                    },
                    onSelected:getOnSelectedFunc(dictId)
                });
                function getOnSelectedFunc(dictId){
                    switch (dictId){
                        case 1 :return function (id,text) {
                                selForm.initDDL(2, selForm.$city,id);
                                clearCombobox(selForm.$district);
                                clearCombobox(selForm.$town);
                            };
                            break;
                        case 2 : return function (id,text) {
                                selForm.initDDL(3, selForm.$district,id);
                                clearCombobox(selForm.$town);
                            };
                            break;
                        case 3 : return function (id,text) {
                                selForm.initDDL(4, selForm.$town,id);
                            };
                            break;
                    }

                    function clearCombobox(target){
                        var manager = selForm.$district.ligerGetComboBoxManager();
                        if(manager.set)
                            manager.set({url:null,data:[]});
                    }
                }
            },
            bindEvents: function () {
                this.$btnOk.click(function () {
                    var province = selForm.$province.ligerGetComboBoxManager().getText();
                    var city = selForm.$city.ligerGetComboBoxManager().getText();
                    var district = selForm.$district.ligerGetComboBoxManager().getText();
                    var town = selForm.$town.ligerGetComboBoxManager().getText();
                    var provinceCode = selForm.$province.ligerGetComboBoxManager().getValue();
                    var cityCode = selForm.$city.ligerGetComboBoxManager().getValue();
                    var districtCode = selForm.$district.ligerGetComboBoxManager().getValue();
                    var townCode = selForm.$town.ligerGetComboBoxManager().getValue();
                    var areaCode = townCode||districtCode||cityCode||provinceCode;
                    infoForm.setAreaInfo(province, city, district, town, areaCode);
                    selForm.show(false);
                });

                this.$btnCancel.click(function () {
                    selForm.show(false);
                });
            }
        };
        /* *************************** 页面初始化 **************************** */
        pageInit();

    })(jQuery, window);
</script>