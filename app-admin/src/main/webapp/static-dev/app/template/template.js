/**
 * Created by zqb on 2015/10/12.
 */
define(function (require, exports, module) {

    require('app/template/template.css');

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

        initVersionDDL($("#searchVersion"));
        initVersionDDL($("#newVersion"));

        function initVersionDDL(objectTarget){
            $.ajax({
                url:context.path + "/standardsource/getVersionList",
                type:"post",
                dataType:"json",
                async: false,
                success:function(data){
                    var versions = data.obj;
                    var option =' ';
                    for(var i=0;i<versions.length;i++){
                        //version,versionName
                        var version=versions[i].split(",");
                        option += '<option value=' + version[0] + '>' + version[1] + '</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        //grid生成
        var version = $("#searchVersion").val();
        var orgName = $("#searchOrgName").val();
        var grid = new Grid('#dataGrid',{
            url: context.path + "/template/searchTemplate",
            datatype:'json',
            shrinkToFit: true,
            mtype:'POST',
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 210,
            autoFit: true,
            marginHeight:210,
            rowNum : 10,
            colNames:[$.i18n.prop('grid.order'),'id',$.i18n.prop('grid.orgName'),$.i18n.prop('grid.dsCode'),$.i18n.prop('grid.dsName'),$.i18n.prop('grid.version'),$.i18n.prop('grid.cdaType'),$.i18n.prop('grid.htmlModal'),$.i18n.prop('grid.copyModal')],
            colModel:[
                {name:'order',index:'order',sorttype: 'int',align: "center"},
                {name:'id',index:'id', hidden: true},
                {name:'orgName',index:'orgName',align:"center"},
                {name:'dsCode',index:'dsCode', align:"center"},
                {name:'dsName',index:'dsName', align:"center"},
                {name:'version',index:'version',align:"center"},
                {name:'cdaType',index:'cdaType',align:"center"},
                {
                    name: 'operator ',
                    index: 'operator',
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#htmlModal" class="J_search-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.browse")+'</a> / <a data-toggle="modal" data-target="#htmlModal" class="J_edit-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.edit")+'</a>';
                    }
                },
                {
                    name: 'copy ',
                    index: 'copy',
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#templateModal" class="J_copy-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.copy")+'</a>';
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
            postData:{version:version,orgName:orgName}
        }).render();

        new ViewController('#dataGrid',{
            events: {'click .J_search-btn': 'searchItem',
                'click .J_edit-btn': 'editItem',
                'click .J_copy-btn': 'copyItem'
            },
            handlers: {
                searchItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var templateId = grid.instance.getRowData(rowid).id;
                    $(".modal-body textarea").attr('disabled',true);
                    $.ajax({
                        type : "POST",
                        url : context.path + "/template/getTemplateHtml",
                        data : {templateId:templateId},
                        dataType : "json",
                        success :function(data){
                            $("#htmlCode").val(data.obj);
                        },
                        error :function(data){
                        }
                    });
                },
                editItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var templateId = grid.instance.getRowData(rowid).id;
                    $(".modal-body textarea").attr('disabled',false);
                    $.ajax({
                        type : "POST",
                        url : context.path + "/template/getTemplateHtml",
                        data : {templateId:templateId},
                        dataType : "json",
                        success :function(data){
                            $("#htmlCode").val(data.obj);
                        },
                        error :function(data){
                        }
                    });
                },
                copyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var templateId = grid.instance.getRowData(rowid).id;

                    $("#copyId").val(templateId);
                    $(".modal-body textarea").attr('disabled',false);
                    $.ajax({
                        type : "POST",
                        url : context.path + "/template/getTemplateModel",
                        data : {templateId:templateId},
                        dataType : "json",
                        success :function(data){
                            if (data.successFlg) {
                                var templateModel = data.obj;
                                $("#newVersion").find("option[value='"+templateModel.version+"']").attr("selected",true);
                                //$("#newOrg").find("option[value='"+templateModel.orgCode +"']").attr("selected",true);
                                selectComponent.selects[0].setValueByName(templateModel.province);
                                selectComponent.selects[1].setValueByName(templateModel.city);
                                selectComponent.selects[2].setValueById(templateModel.orgCode);
                                $("#newDataset").val(templateModel.dsCode);
                            }
                        },
                        error :function(data){
                        }
                    });
                }
            }
        });

        var selectComponent = new LinkageSelect([
            {
                selector:"#province", // 一级联动下拉框id选择器
                url: context.path + "/address/getParent", // 一级联动下拉框数据请求路径
                data: {level: 1}, // 一级联动下拉框数据请求发送给服务器的ajax数据
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的value值和其显示文本设置为pid和province请求参数
                    id: 'pid',
                    name: 'province'
                }
            },
            {
                selector:"#city", // 二级联动下拉框id选择器
                url: context.path + "/address/getChildByParent",
                blankItem: true, // 是否插入置空选项行，默认为true
                paramMap: { // 请求参数映射: 在触发下拉框change事件时，将选中项的显示文本设置为city请求参数
                    name: "city"
                }
            },
            {
                selector:"#newOrg", // 三级联动下拉框id选择器
                url: context.path + "/address/getOrgs"
            }
        ]);

        <!--校验器-->
        var validator = new JValidate.Validation($("#updateForm"),{immediate:true,onSubmit:false});
        $('#updateForm').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#templateModal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        //搜索按钮点击事件
        $(".J_searchBtn").click(function(){
            reloadGrid();
        });

        //新增按钮点击事件
        $("#addBtn ").click(function(){
            clearModel();
            var version = $("#searchVersion").val();
            $("#newVersion").find("option[value='"+version+"']").attr("selected",true);
        });

        //新增按钮点击保存事件
        $("#updateBtn").click(function(){
            if(!validator.validate() ) {return;}
            var version = $("#newVersion").val();
            var orgCode = $("#newOrg").val();
            var dsCode = $("#newDataset").val();
            var copyId = $("#copyId").val();
            $.ajax({
                type : "POST",
                url : context.path + "/template/addTemplate",
                data : {version:version,orgCode:orgCode,dsCode:dsCode,copyId:copyId},
                dataType : "json",
                success :function(data){
                    if (data.successFlg) {
                        reloadGrid();
                        $(".close",$('#updateForm')).click();
                    } else {
                        alert($.i18n.prop('message.new.failure'));
                        return;
                    }
                },
                error :function(data){
                }
            });
        });

        //画面重查询
        function reloadGrid() {
            var version = $("#searchVersion").val();
            var orgName = $("#searchOrgName").val();

            grid.instance.jqGrid('setGridParam',{
                url: context.path + "/template/searchTemplate",
                postData:{version:version,orgName:orgName} //发送数据
            }).trigger("reloadGrid");
        }

        //清空复制新增弹出框
        function clearModel() {
            $("#copyId").val("");
            $("#newVersion").val("");
            $("#province").val("");
            $("#city").val("");
            $("#newOrg").val("");
            $("#newDataset").val("");
        }
    });

});