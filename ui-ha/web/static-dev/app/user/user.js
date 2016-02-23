/**
 * Created by zqb on 2015/9/30.
 */

define(function (require, exports, module) {
    require('app/user/user.css');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        LinkageSelect = require('modules/components/linkageSelect'),
        JValidate = require('jValidate'),
        Util = require('utils');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',              //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        <!--校验器-->
        var validator = new JValidate.Validation($("#updateForm"),{immediate:true,onSubmit:false});

        var UserTypeDictId = 15;
        initDDL(UserTypeDictId, $("#searchType"));
        initDDLMustInput(UserTypeDictId, $("#userType"));
        initDDLMustInput(UserTypeDictId, $("#userTypeRead"));
        var MartialStatusDictId = 4;
        initDDL(MartialStatusDictId, $("#marriage"));
        initDDL(MartialStatusDictId, $("#marriageRead"));

        //grid生成
        var searchNm = $("#searchNm").val();
        var searchType = $("#searchType").val();

        $('#updateForm').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#modifyRowModal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        var grid = new Grid('#dataGrid',{
            url: context.path + "/user/searchUsers",
            datatype:'json',
            shrinkToFit: true,
            mtype:'POST',
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 160,
            autoFit: true,
            marginHeight:160,
            rowNum : 10,
            colNames:[$.i18n.prop('grid.order'),'id','loginCode',$.i18n.prop('grid.userName'), $.i18n.prop('grid.type'),"UserType", $.i18n.prop('grid.org.belong'),$.i18n.prop('grid.email'),$.i18n.prop('grid.activation'),$.i18n.prop('grid.Last.login.time'), $.i18n.prop('grid.operation')],
            colModel:[
                {name:'order',index:'order',sorttype: 'int', width:80,  align: "center"},
                {name:'id',index:'id', hidden: true},
                {name:'loginCode',index:'loginCode', hidden: true},
                {name:'realName',index:'realName', width:260, align:"center"},
                {name:'userTypeValue',index:'userTypeValue',width:200, align:"center"},
                {name:'userType',index:'userType',hidden:true},
                {name:'organization',index:'organization', width:400, align:"center"},
                {name:'email',index:'email', width:260,align:"center"},
                {name:'activated',index:'activated', width:80, align:"center"},
                {name:'lastLoginTime',index:'lastLoginTime', width:250, align:"center"},
                {
                    name: 'operator ',
                    index: 'operator',
                    width: 140,
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        var result;
                        result = '<a data-toggle="modal" data-target="#modifyRowModal" class="J_modify-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")+'</a> / ';
                        if(Util.isStrEquals( rows.activated,'是')) {
                            result += '<a data-toggle="modal" data-target="#deleteRowModal" class="J_delete-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.invalid")+'</a>';
                        } else if(Util.isStrEquals( rows.activated,'否')){
                            result += '<a data-toggle="modal" data-target="#openRowModal" class="J_open-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.open")+'</a>';
                        }
                        return result;
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
            postData:{searchNm:searchNm,searchType:searchType},

            //grid点击显示用户信息
            onSelectRow: function(id){
                var userId = grid.instance.getRowData(id).id;
                $.ajax({
                    type : "POST",
                    url : context.path + "/user/getUser",
                    data : {userId:userId},
                    dataType : "json",
                    async:false,
                    success :function(data){
                        var userModel = data.obj.userModel;

                        $("#userTypeRead").find("option[value='"+userModel.userType+"']").attr("selected",true);
                        $("#marriageRead").find("option[value='"+userModel.marriage+"']").attr("selected",true);
                        $("#realNameRead").val(userModel.realName);
                        $("#loginCodeRead").val(userModel.loginCode);
                        $("#emailRead").val(userModel.email);
                        $("#idCardRead").val(userModel.idCard);
                        $("input[name='sexRead'][value='"+userModel.sex+"']").prop("checked",true);
                        $("#telRead").val(userModel.tel);
                        $("#orgNameRead").val(userModel.orgName);
                        $("#majorRead").val(userModel.major);
                        $("#keyRead").val(userModel.publicKey);
                        $("#validTimeRead").html(userModel.validTime);
                        $("#startTimeRead").html(userModel.startTime);
                        $("input,select,textarea",$("#readTable")).attr('disabled',true);

                        if(userModel.userType == "Doctor"){
                            $("#majorRead").attr("hidden",false);
                        }
                        else if(userModel.userType == "GovEmployee"){
                            $("#orgNameRead").attr("hidden",false);
                        }
                        else{
                            $("#major").attr("hidden",true);
                            $("#orgName").attr("hidden",true);
                        }
                    },
                    error :function(data){
                        $("input,select,textarea",$("#readTable")).attr('disabled',true);
                    }
                });
                $('#readModal').modal();
            }
        }).render();

        //grid点击修改，开启，失效
        new ViewController('#dataGrid',{
            events: {'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem',
                'click .J_open-btn': 'openItem'
            },
            handlers: {
                modifyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    clearModal();
                    $("#addUser").hide();
                    $("#updateUser").show();
                    $("#senior").show();
                    $("#mojorMgr").hide();
                    $("#orgMgr").hide();
                    var id = grid.instance.getRowData(rowid).id;
                    $.ajax({
                        type : "POST",
                        url : context.path + "/user/getUser",
                        data : {userId:id},
                        dataType : "json",
                        async:false,
                        success :function(data){
                            var userModel = data.obj.userModel;
                            $("#userType").find("option[value='"+userModel.userType+"']").attr("selected",true);
                            $("#marriage").find("option[value='"+userModel.marriage+"']").attr("selected",true);
                            $("#id").val(userModel.id);
                            $("#realName").val(userModel.realName);
                            $("#loginCode1").val(userModel.loginCode);
                            $("#loginCode").val(userModel.loginCode);
                            $("#email").val(userModel.email);
                            $("#idCard").val(userModel.idCard);
                            $("input[name='sex'][value='"+userModel.sex+"']").prop("checked",true);
                            $("#tel").val(userModel.tel);
                            $("#orgCode").val(userModel.orgCode);
                            $("#orgName").val(userModel.orgName);
                            $("#major").val(userModel.major);
                            $("#key").val(userModel.publicKey);
                            $("#validTime").html(userModel.validTime);
                            $("#startTime").html(userModel.startTime);
                        }
                    });
                },
                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $("#deleteId").val(id);
                },
                openItem : function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $("#openId").val(id);
                }

            }
        });
        //联动下拉框
        new LinkageSelect([
            {
                selector:"#province", // 一级联动下拉框id选择器
                url: context.path + "/address/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                selected: { // 设置默认选择项
                    id: 3  // 根据服务器所返回的数据的id，设置默认选项
                },
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector:"#city", // 二级联动下拉框id选择器
                url: context.path + "/address/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                selected: {  // 设置默认选择项
                    index: 1 // 根据服务器所返数据的顺序索引，设置默认选项
                },
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    name: "city"
                }
            },
            {
                selector:"#org", // 三级联动下拉框id选择器
                url: context.path + "/address/getOrgs"
            }
        ]);

        $("#userType").change(function(){
            var userType = $("#userType").val();

            if(userType == "Doctor"){
                $("#mojorMgr").show();
                $("#orgMgr").hide();
            }
            else if(userType == "GovEmployee"){
                $("#mojorMgr").hide();
                $("#orgMgr").show();
            }
            else{
                $("#orgMgr").hide();
                $("#mojorMgr").hide();
            }
        });

        //搜索按钮点击事件
        $("#searchBtn").click(function(){
            reloadGrid();
        });

        $("#resetPKBtn").click(function() {
            createPublicKey();
        });

        //公钥管理点击重新分配事件
        $("#keyBtn").click(function() {
            if($("#key").val() == ''){
                createPublicKey();
            }
            else{
                $('#resetPublicKeyModel').modal();
            }
        });

        //新增按钮点击事件
        $("#addBtn").click(function() {
            clearModal();
            $("#addUser").show();
            $("#updateUser").hide();
            $("#senior").hide();
        });

        //删除按钮点击保存事件
        $("#deleteBtn").click(function() {
            var id = $("#deleteId").val();
            $.ajax({
                type : "POST",
                url : context.path + "/user/activityUser",
                data : {userId:id,activity:false},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadGrid();
                    } else {
                        alert($.i18n.prop("message.update.success"));
                        return;
                    }
                },
                error :function(data){
                }
            });
        });

        //开启按钮点击保存事件
        $("#openBtn").click(function() {
            var id = $("#openId").val();
            $.ajax({
                type : "POST",
                url : context.path + "/user/activityUser",
                data : {userId:id,activity:true},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadGrid();
                    } else {
                        alert($.i18n.prop("message.update.success"));
                        return;
                    }
                },
                error :function(data){
                }
            });
        });

        //联动下拉框选择机构点击保存事件
        $("#orgConfirmBtn").click(function() {
            var orgCode = $('#org').find('option:selected').val();
            var orgName = $('#org').find('option:selected').html();
            $("#orgCode").val(orgCode);
            $("#orgName").val(orgName);
        });

        //重置密码按钮事件
        $("#resetPassBtn").click(function() {
            var userId = $("#id").val();
            $.ajax({
                type : "POST",
                url : context.path + "/user/resetPass",
                data : {userId:userId},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        alert($.i18n.prop("message.reset.success"));
                    } else {
                        alert($.i18n.prop("message.reset.failure"));
                        return;
                    }
                }
            });
        });

        //新增或修改点击保存事件
        $("#updateBtn").click(function(){
           // var validator = new JValidate.Validation($("#updateForm"),{immediate:true,onSubmit:false});
           // if(!validator.validate()){return;}
            if(!validator.validate()){return;}
            var userType = $('#userType').find('option:selected').val();
            var realName = $("#realName").val();
            var loginCode = $("#loginCode").val();
            var email = $("#email").val();
            var idCard = $("#idCard").val();
            var marriage = $('#marriage').find('option:selected').val();
            var id = $("#id").val();
            var tel = $("#tel").val();
            var org = $("#org").val();
            var major = $("#major").val();
            var orgCode = $("#orgCode").val();
            var orgName = $("#orgName").val();
            var sex;
            var chkObjs1 = $("input[name='sex']");
            for(var i=0;i<chkObjs1.length;i++){
                if(chkObjs1[i].checked){
                    sex = chkObjs1[i].value;
                    break;
                }
            }

            $.ajax({
                type : "POST",
                url : context.path + "/user/updateUser",
                data : {id:id,userType:userType,realName:realName,loginCode:loginCode,email:email,
                    idCard:idCard,sex:sex,marriage:marriage,tel:tel,major:major,orgCode:orgCode,orgName:orgName},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadGrid();
                        $(".close").click();
                    } else {
                        alert($.i18n.prop("账号或邮箱已存在"));
                        return;
                    }
                }
            });
        });

        //清除弹出框信息
        function createPublicKey() {
            //获取需要分配公钥信息的用户名称。
            var loginCode = $("#loginCode").val();
            $.ajax({
                type : "POST",
                url : context.path + "/user/distributeKey",
                data : {loginCode:loginCode},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        $("#key").val(data.obj.publicKey);
                        $("#validTime").html(data.obj.validTime);
                        $("#startTime").html(data.obj.startTime);
                        alert($.i18n.prop("message.distribution.success"));
                    } else {
                        alert($.i18n.prop("message.distribution.failure"));
                        return;
                    }
                }
            });
        }

        //重新分配公钥点击确认事件
        $("#resetPKBtn").click(function(){
            var loginCode = $("#loginCode").val();
            var key = $("#key").val();
            $.ajax({
                type : "POST",
                url : context.path + "/user/distributeKey",
                data : {loginCode:loginCode},
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
        //清除弹出框信息
        function clearModal() {
            $("#realName").val("");
            $("#loginCode").val("");
            $("input[name='sex'][value='Male']").prop("checked",true);
            $("#marriageRead").find("option[value='marriage']").attr("selected",true);
            $("#userType").val("Nurse");
            $("#email").val("");
            $("#key").val("");
            $("#keyRead").val("");
            $("#vaildTime").val("");
            $("#startTime").val("");
            $("#vaildTimeRead").val("");
            $("#startTimeRead").val("");
            $("#idCard").val("");
            $("#id").val("");
            $("#tel").val("");
            $("#orgCode").val("");
            $("#orgName").val("");
            $("#major").val("");
        }

        //画面重查询
        function reloadGrid() {
            var searchNm = $("#searchNm").val();
            var searchType = $("#searchType").val();
            grid.instance.jqGrid('setGridParam',{
                url: context.path + "/user/searchUsers",
                page : 1,
                postData:{searchNm:searchNm,searchType:searchType} //发送数据
            }).trigger("reloadGrid");
        }

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, objectTarget){
            $.ajax({
                url:context.path + "/dict/searchDictEntryList",
                type:"post",
                dataType:"json",
                data:{dictId:dictId,page:"1",rows:"5"},
                success:function(data){
                    var option ='<option value=""><spring:message code="lbl.selected"/></option>';
                    for(var i=0;i<data.detailModelList.length;i++){
                        option +='<option value='+data.detailModelList[i].code+'>'+data.detailModelList[i].value+'</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDLMustInput(dictId, objectTarget){
            $.ajax({
                url:context.path + "/dict/searchDictEntryList",
                type:"post",
                dataType:"json",
                data:{dictId:dictId,page:"1",rows:"5"},
                success:function(data){
                    var option =' ';
                    for(var i=0;i<data.detailModelList.length;i++){
                        option +='<option value='+data.detailModelList[i].code+'>'+data.detailModelList[i].value+'</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        // TODO
        //window.top.showTopNoticeBar({type:'success', msg: '操作成功。'});
    });
});
