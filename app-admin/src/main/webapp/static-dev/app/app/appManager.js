/**
 * Created by zqb on 2015/10/8.
 */

define(function (require, exports, module) {
    require('app/app/appManager.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        Util        = require('utils'),
        Common = require('app/common'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        <!--校验器-->
        var addValidator = new JValidate.Validation($("#addApp-form"),{immediate:true,onSubmit:false});
        var updateValidator = new JValidate.Validation($("#updateApp-form"),{immediate:true,onSubmit:false});

        $("#addApp-form").on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $("#createRowModal").on('hidden.bs.modal', function (e) {
            addValidator.reset();
        });

        $("#updateApp-form").on('blur','.required',function() {
           JValidate.Validation.validateElement(this);
        });
        $("#modifyRowModal").on('hidden.bs.modal', function (e) {
            updateValidator.reset();
        });

        /*下拉框初始化*/
        var appCatalogDictId = 1;
        var appStatusDictId = 2;
        initDDL(appCatalogDictId, $('#search-catalog'));
        initDDL(appCatalogDictId, $('#option-catalog'));
        initDDL(appCatalogDictId, $('#update-catalog'));
        initDDL(appStatusDictId, $('#search-status'));
        initDDL(appStatusDictId, $('#update-status'));

        // 初始化加载
        var searchNm = $("#search-number").val();
        var catalog = $("#search-catalog").val();
        var status = $("#search-status").val();
        var grid = new Grid('#dataGrid', {
            url: context.path + "/app/searchApps",
            datatype: 'json',
            shrinkToFit: true,
            mtype:'POST',
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 160,
            autoFit: true,
            rowNum: 10,
            colNames: [$.i18n.prop('grid.name'), $.i18n.prop('grid.id'), $.i18n.prop('grid.secret'), $.i18n.prop('grid.type'), $.i18n.prop('grid.url'), $.i18n.prop('grid.status'),$.i18n.prop('grid.audit'), $.i18n.prop('grid.operation')],
            colModel: [
                {name: 'name', index: 'name', width: 160,align: "center"},
                {name: 'id', index: 'id', width: 110, align: "center"},
                {name: 'secret', index: 'secret', width: 80, align: "center"},
                {name: 'catalog', index: 'catalog', width: 150, align: "center",formatter:function(value,grid,rows,state){
                    return rows.catalog.value;
                }},
                {name: 'url', index: 'url', width: 120, align: "center"},
                {name:'status', index: 'status', width: 190, align: "center",formatter:function(value,grid,rows,state){
                    return rows.status.value;
                }},
                {name:'checkStatus',index:'checkStatus',width:100,align:"center",frozen:true,
                    formatter:function(value,grid,rows,state){
                        if(Util.isStrEquals( rows.status.code,'WaitingForApprove')) {
                            return '<a data-toggle="model"  class="checkPass" data-rowid="'+grid.rowId+'">'+$.i18n.prop("btn.pass")+'</a> / <a class="veto" data-rowid="'+grid.rowId+'">'+$.i18n.prop("btn.reject")+'</a>'
                        } else if(Util.isStrEquals( rows.status.code,'Approved')){
                            return '<a data-toggle="model"  class="Forbidden" data-rowid="'+grid.rowId+'">'+$.i18n.prop("btn.forbidden")+'</a>'
                        }else if(Util.isStrEquals( rows.status.code,'Forbidden')){
                            return '<a data-toggle="model"  class="checkPass" data-rowid="'+grid.rowId+'">'+$.i18n.prop("btn.open")+'</a>'
                        }else if(Util.isStrEquals( rows.status.code,'Reject')){
                            return '<span>'+$.i18n.prop("btn.nothing")+'</span>'
                        }

                    }
                },
                {
                    name: 'operator ',
                    index: 'operator',
                    width: 140,
                    sortable: false,
                    align: "center",frozen:true,
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#modifyRowModal" class="J_modify-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update");
                    }
                }
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false,
                id: "0"
            },
            postData: {searchNm: searchNm, catalog: catalog, status: status},
            //点击行事件
            onSelectRow: function (rowid) {
                var id = grid.instance.getRowData(rowid).id;
                $.ajax({
                    type: "POST",
                    url: context.path + "/app/getAppDetail",
                    data: {appId: id},
                    dataType: "json",
                    success :function(data){
                        $("#select-name").val(data.obj.appModel.name);
                        $("#select-catalog").val(data.obj.appModel.catalog.value);//类型
                        $("#select-status").val(data.obj.appModel.status.value);//状态
                        $("#select-id").val(data.obj.appModel.id);//内码
                        $("#select-secret").val(data.obj.appModel.secret);//密钥
                        $("#select-url").val(data.obj.appModel.url);//回调url
                        $("#select-description").val(data.obj.appModel.description);//描述
                    },
                    error :function(data){
                        $("input,select,textarea",$("#readTable")).attr('disabled',true);
                    }
                });
                $('#readModal').modal();
            }
        }).render();

        //修改和删除的方法
        new ViewController('#dataGrid', {
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers: {
                modifyItem: function (e) {
                    clearModal();
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $.ajax({
                        type: "POST",
                        url: context.path + "/app/getAppDetail",
                        data: {appId: id},
                        dataType: "json",
                        success: function (data) {
                            $("#new-name").val(data.obj.appModel.name);
                            $("#new-app-id").val(data.obj.appModel.id);
                            $("#new-secret").val(data.obj.appModel.secret);
                            $("#update-catalog").val(data.obj.appModel.catalog.code);
                            $("#new-url").val(data.obj.appModel.url);
                            $("#update-status").val(data.obj.appModel.status.code);
                            $("#new-description").val(data.obj.appModel.description);
                            $(".tags").val(data.obj.appModel.strTags);
                        },
                        error: function (data) {
                        }
                    });
                    $("#new-update-btn").off('click').on('click',function(){
                        //获取输入的值
                        if(!updateValidator.validate() ) {return;}
                        var name = $("#new-name").val();
                        var catalog = $("#update-catalog").val();
                        var status = $("#update-status").val();
                        var url = $("#new-url").val();
                        var description = $("#new-description").val();
                        var tags = $(".tags").val();

                        $.ajax({
                            url:context.path + "/app/updateApp",
                            type:"POST",
                            data:{appId: id,name:name,catalog:catalog,status:status,url:url,description:description,tags:tags},
                            dataType:"json",
                            success:function(data){
                                reloadGrid();
                                $(".close",$("#modifyRowModal")).click();
                               Common.msgBox(data);
                            }
                        });
                    });
                    //修改标签
                    selecttags(".tags-names");
                },

                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $("#deleteAppId").val(id);
                }
            }
        });
        //删除的点击事件
        $("#affirm-delete").click(function(){
            var appId = $("#deleteAppId").val();
            $.ajax({
                type: "POST",
                url: context.path + "/app/deleteApp",
                data: {appId: appId},
                dataType: "json",
                success: function (data) {
                    reloadGrid();
                },
                error: function (data) {
                    reloadGrid();
                }
            });
        })

        //APP审核的方法
        new ViewController('#dataGrid',{
            events:{
                'click .checkPass':'checkPass',
                'click .veto':'veto',
                'click .Forbidden':'Forbidden'
            },
            handlers:{
                //通过的方法
                checkPass:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    var status = "Approved";
                    check(id,status);
                },
                //否决的方法
                veto:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    var status = "Reject";
                    check(id,status);
                },
                //禁止的方法
                Forbidden: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    var status = "Forbidden";
                    check(id,status);
                }
            }

        });


        $("#search-btn").click(function () {
            reloadGrid();
        });

        //app的新增
        $("#add-btn").click(function(){
            clearModal();
            selecttags(".tags-name");
        });

        $("#update-btn").click(function(){
            if(!addValidator.validate()){return;}
            var name = $("#name").val();
            var catalog = $("#option-catalog").val();
            var url = $("#url").val();
            var description = $("#description").val();
            var tags = $(".tags").val();
            $.ajax({
                url:context.path + "/app/createApp",
                type:"post",
                dataType:"json",
                data:{name:name,catalog:catalog,url:url,description:description,tags:tags},
                success:function(data){
                    reloadGrid();
                    $(".close",$("#createRowModal")).click();
                }
            });
        });

        //通过、否决、禁止
        function check(id,status){
            $.ajax({
                url: context.path + "/app/check",
                type:"POST",
                data:{appId:id,status:status},
                dataType:"json",
                success:function(data){
                    reloadGrid();
                }
            })
        }

        //标签遍历的方法
        function selecttags(initTarget){
            var target = $(initTarget);
            var tet = "";
            $(".option-tags").val("");
            //修改时选择便签的方法
            $(target).hide();
            $(".tags-image").click(function(){
                $(target).show();
                tet = $(".tags").val() +" ";
                $(".option-tags").val(tet);
            });
            $(".tags-clean").click(function(){
                tet = "";
                $(".option-tags").val(tet);
            });
            $.ajax({
                url:context.path + "/dict/selecttags",
                type:"post",
                dataType:"json",
                data:{},
                success:function(data){
                    var len = data.obj.length;
                    var add = 4 - (len % 4);
                    var node =  $('<tr class="option-tr-td">');
                    $(initTarget+" tr").not(':first').remove();
                    $(target).append(node);
                    for(var i=0;  i < len; i++) {
                        if(i % 4 == 0 && i != 0) {
                            node = $('<tr class="option-tr-td">');
                            $(target).append(node);
                        }
                        node.append('<td class="tds">'+data.obj[i].value+'</td>');
                    }
                    //table 补空行
                    for(var i=0;  i < add; i++) {
                        node.append('<td class="tds"></td>');
                    }
                    $(".option-tr-td").find(".tds").each(function(){
                        $(this).click(function(){
                            if (tet.indexOf($(this).text())>=0){
                                //循环，继续下一个
                                return;
                            }
                            tet= tet+ $(this).text()+" ";
                            $(".option-tags").val(tet);
                        });
                    });
                    $(".option-up").click(function(){
                        $(target).hide();
                        $(".tags").show();
                        $(".tags").val(tet.substring(0,tet.length-1));
                    });
                }
            });
        }

        //根据传入的字典ID及显示对象，加载下拉框的数据。
        function initDDL(dictId, target) {
            //下拉框的方法
            var target = $(target);
            $.ajax({
                url: context.path + "/dict/searchDictEntryList",
                type: "post",
                dataType: "json",
                data: {dictId: dictId, page: "1", rows: "5"},
                success: function (data) {
                    var option ='<option value="">'+$.i18n.prop("select.choose.all")+'</option>';
                    for (var i = 0; i < data.detailModelList.length; i++) {
                        option += '<option value=' + data.detailModelList[i].code + '>' + data.detailModelList[i].value + '</option>';
                    }
                    $(target).append(option);
                }
            });
        }

        //清除新增弹出框信息
        function clearModal() {
            $("#name").val("");
            $("#option-catalog").find("option[value='']").attr("selected",true);
            $("#url").val("");
            $("#description").val("");
            $(".option-tags").val("");
            $(".tags").val("");
        }

        function reloadGrid() {
            var searchNm = $("#search-number").val();
            var catalog = $("#search-catalog").val();
            var status = $("#search-status").val();
            grid.instance.jqGrid('setGridParam', {
                mtype:"post",
                url: context.path + "/app/searchApps",
                page : 1,
                postData:{searchNm:searchNm,catalog:catalog,status:status} //发送数据
            }).trigger("reloadGrid");
        }
    });
});