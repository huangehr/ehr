/**
 * Created by zqb on 2015/10/8.
 */

define(function (require, exports, module) {
    require('app/organization/organization.css');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        LinkageSelect = require('modules/components/linkageSelect'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        /*下拉框初始化*/
        initDDL(8, $('#searchWay'));
        initDDL(8, $('#settledWay'));
        initDDL(7, $('#orgType'));

        var searchNm = $("#searchNm").val();
        var searchWay = $("#searchWay").val();
        var grid = new Grid('#dataGrid',{
            url: context.path + "/organization/searchOrgs",
            datatype:'json',
            rowNum:10,
            shrinkToFit: true,
            scrollOffset: 0,
            mtype : "POST",
            width: $(window).width(),
            height: $(window).height() - 160,
            autoFit: true,
            marginHeight:160,
            colNames:[$.i18n.prop('grid.order'),$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.location'),'settledWay','activityFlag','是否激活',$.i18n.prop('grid.settledWay'), $.i18n.prop('grid.operation')],
            colModel:[
                {name:'order',index:'order',sorttype: 'int', width:100,  align: "center"},
                {name:'orgCode',index:'orgCode', width:300, align:"center"},
                {name:'fullName',index:'fullName', width:430, align:"center"},
                {name:'location',index:'location', width:450, align:"center"},
                {name:'settledWay',index:'settledWay', align:"center", hidden:true},
                {name:'activityFlag',index:'activityFlag', align:"center", hidden:true},
                {name:'activityName',index:'activityName', width:250, align:"center"},
                {name:'settledWayValue',index:'settledWayValue', width:250, align:"center"},
                {
                    name: 'operator ',
                    index: 'operator',
                    width: 140,
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        var result='<a data-toggle="modal" data-target="#modifyRowModal" class="J_modify-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")+'</a> / ';
                        if(rows.activityFlag==1) {
                            result += '<a data-toggle="modal" data-target="#disActivityModal" class="J_modify-btn activity" data-rowid="' + grid.rowId + '" >失效</a>';
                        }else{
                            result += '<a data-toggle="modal" data-target="#activityModal" class="J_modify-btn activity" data-rowid="' + grid.rowId + '" >开启</a>';
                        }
                        return result;
                        //<a data-toggle="modal" data-target="#deleteRowModal" class="J_delete-btn" data-rowid="' + grid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
                    }
                }
            ],
            jsonReader : {
                root:"detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false,
                id: "0"
            },
            postData:{searchNm:searchNm,searchWay:searchWay},
            ondblClickRow: function(id){
                var orgCode = grid.instance.getRowData(id).orgCode;
                $('.address','#updateTable').attr('placeholder','');
                $("input,select",$("#updateTable")).attr('disabled',false);
                $("#updateFlg").val("3");
                $(".modal-footer").hide();
                $("#senior").hide();
                $("#seniorRead").show();
                getModalInfo(orgCode);
                $("input,select",$("#updateTable")).attr('disabled',true);
                $('#modifyRowModal').modal();
            }
        }).render();
        //画面点击删除修改操作
        new ViewController('#dataGrid',{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem',
                'click .activity': 'activity'
            },
            handlers: {
                modifyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var orgCode = grid.instance.getRowData(rowid).orgCode;
                    $('.address','#updateTable').attr('placeholder',$.i18n.prop('lab.click.to.select.details'));
                    $("input,select",$("#updateTable")).attr('disabled',false);
                    $("#updateFlg").val("1");
                    $(".modal-footer").show();
                    $("#orgCode").attr("disabled",true);
                    $("#senior").show();
                    $("#seniorRead").hide();
                    getModalInfo(orgCode);
                },
                deleteItem: function (e) {
                    $(".modal-footer").show();
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var orgCode = grid.instance.getRowData(rowid).orgCode;
                    $("#deleteId").val(orgCode);
                },
                activity: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var orgCode = grid.instance.getRowData(rowid).orgCode;
                    $("#orgCode").val(orgCode);
                    var activityFlag = grid.instance.getRowData(rowid).activityFlag;
                    $("#activityFlag").val(activityFlag);
                }
            }
        });

        <!--地址级联 -->
        var addrSelector = new LinkageSelect([
            {
                selector: "#provinceSelect", // 一级联动下拉框id选择器
                url: context.path + "/patient/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector: "#citySelect", // 二级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "city"
                }
            },
            {
                selector: "#districtSelect", // 三级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "district"
                }
            },
            {
                selector: "#townSelect", // 四级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "town"
                }
            }
        ]);

        <!--地址级联 -->
        var addrSelector = new LinkageSelect([
            {
                selector: "#provinceSelect", // 一级联动下拉框id选择器
                url: context.path + "/patient/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector: "#citySelect", // 二级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "city"
                }
            },
            {
                selector: "#districtSelect", // 三级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "district"
                }
            },
            {
                selector: "#townSelect", // 四级联动下拉框id选择器
                url: context.path + "/patient/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    id: 'pid',
                    name: "town"
                }
            }
        ]);
        //画面点击搜索按钮
        $("#searchBtn").click(function(){
            reloadOrgGrid();
        });
        //画面点击删除按钮
        $("#deleteBtn").click(function() {
            var id = $("#deleteId").val();
            $.ajax({
                type : "POST",
                url : context.path + "/organization/deleteOrg",
                data : {orgCode:id},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadOrgGrid();
                    } else {
                        alert("更新失败");
                        return;
                    }
                },
                error :function(data){
                }
            });
        });
        //画面点击新增按钮
        $("#addBtn").click(function() {
            $('.address','#updateTable').attr('placeholder',"点击选择详细信息");
            $("input,select",$("#updateTable")).attr('disabled',false);
            $("#updateFlg").val("0");
            $("#senior").hide();
            $("#seniorRead").hide();
            $(".modal-footer").show();
            clearModal();
        });

        $('#location').click(function () {
            setAddress($('#province').val(), $('#city').val(), $('#district').val(), $('#town').val());
        });

        function setAddress(province, city, district, town) {
            addrSelector.selects[0].setValueByName(province);
            addrSelector.selects[1].setValueByName(city);
            addrSelector.selects[2].setValueByName(district);
            addrSelector.selects[3].setValueByName(town);
        }

        //画面点击选择省市下拉框确定事件
        $("#locationConfirmBtn").click(function() {
            var province = $('#provinceSelect').find('option:selected').html();
            var city = $('#citySelect').find('option:selected').html();
            var district = $('#districtSelect').find('option:selected').html();
            var town = $('#townSelect').find('option:selected').html();
            $("#province").val(province);
            $("#city").val(city);
            $("#district").val(district);
            $("#town").val(town);
            $("#location").val(province+city+district+town);
        });


        $("#keyReadBtn").click(function(){
            var key = $("#key").val();
            if(key!=""){
                $("#keyBtn").attr("data-target","#keyModalConfirm");
            }else{
                $("#keyBtn").attr("data-target","");
            }
        });

        //重新分配公钥点击确认事件
        $("#keyReSetBtn").click(function(){
            var orgCode = $("#orgCode").val();
            var key = $("#key").val();
            $.ajax({
                type : "POST",
                url : context.path + "/organization/distributeKey",
                data : {orgCode:orgCode},
                dataType : "json",
                success :function(data){
                    debugger
                    if (data.successFlg) {
                        $("#key").val(data.obj.publicKey);
                        $("#validTime").text(data.obj.validTime);
                        $("#startTime").text(data.obj.startTime);
                        alert($.i18n.prop('message.distribution.success'));
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error :function(data){
                }
            });
        });


        //画面点击公钥管理重新分配事件
        $("#keyBtn").click(function() {
            var orgCode = $("#orgCode").val();
            var key = $("#key").val();
            if(key!=""){
                //这里做是否重新分配操作。。。。。。
            }else{
                $.ajax({
                    type : "POST",
                    url : context.path + "/organization/distributeKey",
                    data : {orgCode:orgCode},
                    dataType : "json",
                    success :function(data){
                        if (data.successFlg) {
                            $("#key").val(data.obj.publicKey);
                            $("#validTime").text(data.obj.validTime);
                            $("#startTime").text(data.obj.startTime);
                            $("#keyBtn").attr("data-target","#keyModalConfirm");
                            alert($.i18n.prop('message.distribution.success'));
                        } else {
                            alert($.i18n.prop('message.distribution.failure'));
                            return;
                        }
                    },
                    error :function(data){
                    }
                });
            }

        });

        var validator = new JValidate.Validation($("#updateForm"),{immediate:true,onSubmit:false});

        $('#updateForm').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#modifyRowModal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        //画面点击新增修改事件
        $("#updateBtn").click(function(){
            if(!validator.validate() ) {return;}
            var orgType = $('#orgType').find('option:selected').val();
            var orgCode = $("#orgCode").val();
            var fullName = $("#fullName").val();
            var shortName = $("#shortName").val();
            var location = $("#location").val();
            var settledWay = $('#settledWay').find('option:selected').val();
            var tel = $("#tel").val();
            var province = $("#province").val();
            var city = $("#city").val();
            var district = $("#district").val();
            var town = $("#town").val();
            var updateFlg = $("#updateFlg").val();
            $.ajax({
                type : "POST",
                url : context.path + "/organization/updateOrg",
                data : {orgCode:orgCode,orgType:orgType,fullName:fullName,shortName:shortName,updateFlg:updateFlg,
                    settledWay:settledWay,tel:tel,province:province,city:city,district:district,town:town},
                dataType : "json",
                success :function(data){
                    debugger
                    if (data.successFlg) {
                        reloadOrgGrid();
                        $(".close").click();
                    } else {
                        alert(data.errorMsg);
                        return;
                    }
                },
                error :function(data){
                }
            });
        });
        //清楚弹出框
        function clearModal() {
            $("#orgCode").val("");
            $("#fullName").val("");
            $("#shortName").val("");
            $("#location").val("");
            $("#orgType").val("Hospital");
            $("#settledWay").val("Direct");
            $("#tel").val("");
            $("#key").val("");
        }
        //获取组织机构信息
        function getModalInfo(orgCode) {
            clearModal();
            $.ajax({
                type : "POST",
                url : context.path + "/organization/getOrg",
                data : {orgCode:orgCode},
                dataType : "json",
                success :function(data){
                    var orgModel = data.obj.orgModel;
                    $("#orgType").find("option[value='"+orgModel.orgType+"']").attr("selected",true);
                    $("#orgCode").val(orgModel.orgCode);
                    $("#fullName").val(orgModel.fullName);
                    $("#shortName").val(orgModel.shortName);
                    $("#location").val(orgModel.location);
                    $("#province").val(orgModel.province);
                    $("#city").val(orgModel.city);
                    $("#district").val(orgModel.district);
                    $("#town").val(orgModel.town);
                    $("#settledWay").find("option[value='"+orgModel.settledWay+"']").attr("selected",true);
                    $("#key").val(orgModel.publicKey);
                    $("#keyRead").val(orgModel.publicKey);
                    $("#validTime").html(orgModel.validTime);
                    $("#startTime").html(orgModel.startTime);
                    $("#tel").val(orgModel.tel);
                    $("#validTimeRead").html(orgModel.validTime);
                    $("#startTimeRead").html(orgModel.startTime);
                },
                error :function(data){
                }
            });
        }
        //重载grid画面
        function reloadOrgGrid() {
            var searchNm = $("#searchNm").val();
            var searchWay = $("#searchWay").val();
            grid.instance.jqGrid('setGridParam',{
                url: context.path + "/organization/searchOrgs",
                page : 1,
                postData:{searchNm:searchNm,searchWay:searchWay} //发送数据
            }).trigger("reloadGrid");
        }
        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, objectTarget){
            $.ajax({
                url:context.path + "/dict/searchDictEntryList",
                type:"POST",
                dataType:"json",
                data:{dictId:dictId,page:"1",rows:"100"},
                success:function(data){
                    var option ='<option value=""><spring:message code="lbl.selected"/></option>';
                    for(var i=0;i<data.detailModelList.length;i++){
                        option +='<option value='+data.detailModelList[i].code+'>'+data.detailModelList[i].value+'</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        $("#activityBtn").click(function(){
            activity();
        });
        $("#disActivityBtn").click(function(){
            activity();
        });

        function activity(){
            var orgCode = $("#orgCode").val();
            var activityFlag = $("#activityFlag").val();
            $.ajax({
                type : "POST",
                url : context.path + "/organization/activity",
                data : {orgCode:orgCode,activityFlag:activityFlag},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadOrgGrid();
                    } else {
                        alert($.i18n.prop('message.update.failure'));
                    }
                },
                error :function(data){
                }
            });
        }
    });

});
