/**
 * Created by zqb on 2015/8/24.
 */
define(function (require, exports, module) {
    // TODO
    //require('app/adapter/orgDict.css');
    require('modules/chosen/chosen.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        viewController = require('viewController'),
        chosen = require('modules/chosen/chosen.jquery'),
        Util = require('utils'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });
    $(function(){

        <!--创建校验器-->
        var orgDictAddValidator = new JValidate.Validation($("#orgDictAdd-form"),{immediate:true,onSubmit:false});
        var orgDictUpdataValidator = new JValidate.Validation($("#orgDictUpdate-form"),{immediate:true,onSubmit:false});
        var orgDictItemAddValidator = new JValidate.Validation($("#orgDictItemAdd-form"),{immediate:true,onSubmit:false});
        var orgDictItemUpdataValidator = new JValidate.Validation($("#orgDictItemUpdate-form"),{immediate:true,onSubmit:false});

        var codename;              //查询条件
        var orgDictId;             //机构字典id
        var orgDictSeq;             //机构数据字典序号数据元保存此信息
        var orgDictItemId;         //机构字典项id
        var orgDictGrid = null;    //机构字典表格对象
        var orgDictItemGrid=null;  //机构字典项表格对象
        var orgCode = null;

        $("#orgDictAdd-form","#orgDictUpdate-form","#orgDictItemAdd-form","#orgDictItemUpdate-form").on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });

        $("#createOrgDictModal").on('hidden.bs.modal', function (e) {
            orgDictAddValidator.reset();
        });
        $('#updataOrgDictModal').on('hidden.bs.modal', function (e) {
            orgDictUpdataValidator.reset();
        });
        $('#createOrgDictItmModal').on('hidden.bs.modal', function (e) {
            orgDictItemAddValidator.reset();
        });
        $('#updataMetaDataModal').on('hidden.bs.modal', function (e) {
            orgDictItemUpdataValidator.reset();
        });

        orgCode = $("#adapter_org").val();
        codename = $("#dict-codeAndName").val();

        orgDictItemGrid = new Grid("#orgDictItemGrid",{
            //url:context.path+"/orgdict/searchOrgDictItems",
            postData: {orgDictSeq:orgDictSeq,codename:codename},
            datatype:'json',
            multiselect : true,
            mtype:'POST',
            shrinkToFit: true,
            scrollOffset: 0,
            width: $(window).width() - 430,
            height: $(window).height() - 250,
            autoFit: true,
            marginHeight:250,
            marginWidth:422,
            rowNum:10,
            colNames:['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',align:"center",hidden:true},
                {name:'code',index:'code',align:"center"},
                {name:'name',index:'name',align:"center"},
                {name:'operator',index:'operator',align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#updataOrgDictItemModal" class="J_modify-btn" data-rowid="'+grid.rowId+'">修改  / </a>' +
                        '<a data-toggle="modal" data-target="#deleteOrgDictModal" data-type="orgDictItem"  class="J_delete-btn" data-rowid="'+grid.rowId+'">删除</a>'
                }}
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false,
                id: "0"
            },
            page: 1

        }).render();


        orgDictGrid = new Grid('#orgDictGrid',{
            url:context.path+"/orgdict/searchOrgDicts",
            datatype:'json',
            shrinkToFit: true,
            scrollOffset: 0,
            widthLock:true,
            mtype:'POST',
            width:400,
            height: $(window).height() - 250,
            autoFit: true,
            marginHeight:250,
            rowNum:10,
            colNames:['id','sequence',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',hidden:true},
                {name:'sequence',index:'sequence',hidden:true},
                {name:'code',index:'code',width:150,align:"center"},
                {name:'name',index:'name',width:150,align:"center"},
                {name:'operator',index:'operator',width:100,align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#updataOrgDictModal"  class="J_modify-btn" data-rowid="'+grid.rowId+'">' +
                        '<img  src="/ha/static-dev/images/Modify_btn_pre.png" /></a>' +
                        '<a data-toggle="modal" data-target="#deleteOrgDictModal" data-type="orgDict" class="J_delete-btn" data-rowid="'+grid.rowId+'">' +
                        '<img class="f-ml30" src="/ha/static-dev/images/Delete_btn_pre.png" /></a>'
                }}
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false,
                id: "0"
            },
            postData: {"codename":codename,"orgCode":orgCode},
            onSelectRow: function (rowid, status) {
                orgDictId = orgDictGrid.instance.getRowData(rowid).id;
                orgDictSeq = orgDictGrid.instance.getRowData(rowid).sequence;
                loadOrgDictItemGrid(orgDictSeq);
            },
            page: 1,
            gridComplete:function(){
                var getRowData = orgDictGrid.instance.jqGrid("getRowData");
                if(getRowData.length>0){
                    orgDictId = getRowData[0].id;
                    orgDictSeq = getRowData[0].sequence;
                    loadOrgDictItemGrid(orgDictSeq);
                }else{
                    loadOrgDictItemGrid("");
                }
            }

        }).render();


        function loadOrgDictItemGrid(orgDictSeq){
            //机构字典列表单击加载机构字典数据元方法
            codename = $("#dictItem-codeAndName").val();
            orgDictItemGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdict/searchOrgDictItems",
                mtype:"POST",
                page : 1,
                postData: {orgCode:orgCode,orgDictSeq:orgDictSeq,codename:codename}//发送数据
            }).trigger("reloadGrid");
        }

        //数据集根据条件查询的方法
        $("#searchOrgDict").click(function(){
            codename = $("#dict-codeAndName").val();
            orgDictGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdict/searchOrgDicts",
                mtype:"POST",
                page : 1,
                postData: {codename:codename,orgCode:orgCode} //发送数据
            }).trigger("reloadGrid");
        });

        //数据源根据条件查询的方法
        $("#searchOrgDictItem").click(function(){
            loadOrgDictItemGrid(orgDictSeq);
        });

        function reloadGrid(){
            var codename = $("#dict-codeAndName").val();
            orgDictGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdict/searchOrgDicts",
                mtype:"POST",
                page : 1,
                postData: {codename:codename,orgCode:orgCode} //发送数据
            }).trigger("reloadGrid");
        }

        //数据集和数据元新增前清空的方法
        $("#addOrgDict,#addOrgDictItem").click(function(){
            clearModal();
        });

        function clearModal(){
            $("#orgDictCode").val("");
            $("#orgDictName").val("");
            $("#orgDictDescription").val("");

            $("#orgDictItemCode").val("");
            $("#orgDictItemName").val("");
            $("#orgDictItemDescription").val("");
            $("#orgDictItemSort").val("");
        }

        //机构字典新增的方法
        $("#orgDictAdd").click(function(){
            if(!orgDictAddValidator.validate()){return;}
            var orgDictCode = $("#orgDictCode").val();
            var orgDictName = $("#orgDictName").val();
            var orgDictDescription = $("#orgDictDescription").val();
            $.ajax({
                url:context.path+"/orgdict/createOrgDict",
                dataType:"json",
                type:"post",
                data:{code:orgDictCode,name:orgDictName,description:orgDictDescription,orgCode:orgCode},
                async:false,
                success:function(data){
                    if(data.successFlg){
                        reloadGrid();
                        $(".close",$("#createOrgDictModal")).click();
                        alert($.i18n.prop("message.new.success"));
                    }else{
                        alert(data.errorMsg);
                    }

                },
                error:function(data){
                    alert($.i18n.prop("message.new.failure"));
                }
            });
        });



        //机构字典修改和删除的方法
        new viewController("#orgDictGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    orgDictId = orgDictGrid.instance.getRowData(rowid).id;
                    orgDictSeq = orgDictGrid.instance.getRowData(rowid).sequence;
                    $.ajax({
                        url:context.path+"/orgdict/getOrgDict",
                        dataType:"json",
                        type:"post",
                        data:{id:orgDictId},
                        async:false,
                        success:function(data){
                            if (data.successFlg) {
                                var orgDictModel = data.obj;
                                $("#updata-orgDictCode").val(orgDictModel.code);
                                $("#updata-orgDictName").val(orgDictModel.name);
                                $("#updata-orgDictDescription").val(orgDictModel.description);
                            }
                        },
                        error:function(data){
                            alert($.i18n.prop('message.datasets.failed.to.load'));
                        }
                    });
                },
                deleteItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var datatype = cell.attr("data-type");
                    orgDictId = orgDictGrid.instance.getRowData(rowid).id;
                    orgDictSeq = orgDictGrid.instance.getRowData(rowid).sequence;
                    $("#dataTypes").val(datatype);
                }
            }
        });

        //数据集修改的点击事件
        $("#updataOrgDict").click(function(){
            if(!orgDictUpdataValidator.validate()){return;}
            var code = $("#updata-orgDictCode").val();
            var name = $("#updata-orgDictName").val();
            var description = $("#updata-orgDictDescription").val();
            $.ajax({
                url:context.path+"/orgdict/updateOrgDict",
                dataType:"json",
                type:"post",
                data:{orgCode:orgCode,id:orgDictId,code:code,name:name,description:description},
                async:false,
                success: function (data) {
                    if(data.successFlg){
                        reloadGrid();
                        $(".close",$("#updataOrgDictModal")).click();
                    }else{
                        alert(data.errorMsg);
                    }
                },
                error:function(data){
                    alert($.i18n.prop('message.update.failure'));
                }
            });
        });

        //删除数据集和数据元的点击事件
        $(".affirmdelete").click(function(){
            if($("#dataTypes").val()=="orgDict"){    //机构字典
                $.ajax({
                    url:context.path+"/orgdict/deleteOrgDict",
                    dataType:"json",
                    type:"post",
                    data:{id:orgDictId},
                    async:false,
                    success:function(data){
                        if (data.successFlg) {
                            reloadGrid();
                            //alert($.i18n.prop('message.delete.success'));
                        }
                    },
                    error:function(data){
                        alert($.i18n.prop('message.delete.failure'));
                    }
                });
            }
            if($("#dataTypes").val()=="orgDictItem"){     //机构字典项
                $.ajax({
                    url:context.path+"/orgdict/deleteOrgDictItem",
                    dataType:"json",
                    type:"post",
                    data:{id:orgDictItemId},
                    async:false,
                    success:function(data){
                        if (data.successFlg) {
                            loadOrgDictItemGrid(orgDictSeq);
                            //alert($.i18n.prop('message.delete.success'));
                        }
                    },
                    error:function(data){
                        alert($.i18n.prop('message.delete.failure'));
                    }
                });
                $("#dataTypes").val("");
            }
        });

        //批量删除数据源的方法
        $("#batchDeleteOrgDictItem").click(function(){
            var rowIds = orgDictItemGrid.instance.jqGrid('getGridParam', 'selarrrow');
            var ids=[];
            for(var i= 0;i<rowIds.length;i++){
                ids[i]=orgDictItemGrid.instance.getRowData(rowIds[i]).id;
            }
            $.ajax({
                url:context.path+"/orgdict/deleteOrgDictItemList",
                dataType:"json",
                type:"post",
                data:{ids:ids},
                async:false,
                success:function(data){
                    if (data.successFlg) {
                        loadOrgDictItemGrid(orgDictSeq);
                    }
                },
                error:function(data){
                    alert($.i18n.prop('message.batch.delete.failure'));
                }
            });
        });


        //数据元修改和删除的方法
        new viewController("#orgDictItemGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    orgDictItemId = orgDictItemGrid.instance.getRowData(rowid).id;
                    $.ajax({
                        url:context.path+"/orgdict/getOrgDictItem",
                        type:"post",
                        dataType:"json",
                        data:{id:orgDictItemId},
                        async:false,
                        success:function(data){
                            if (data.successFlg) {
                                var orgDictItemModel = data.obj;
                                $("#updata-orgDictItemCode").val(orgDictItemModel.code);
                                $("#updata-orgDictItemName").val(orgDictItemModel.name);
                                $("#updata-orgDictItemSort").val(orgDictItemModel.sort);
                                $("#updata-orgDictItemDescription").val(orgDictItemModel.description);
                            }
                        },
                        error:function(data){
                            alert($.i18n.prop('message.dataset.failed.to.load'));
                        }
                    });

                },
                deleteItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    orgDictItemId = orgDictItemGrid.instance.getRowData(rowid).id;
                    var datatype = cell.attr("data-type");
                    $("#dataTypes").val(datatype);
                }
            }
        });


        //数据元修改的点击事件
        $("#orgDictItemUpdate").click(function(){
            if(!orgDictItemUpdataValidator.validate()){return;}
                var code = $("#updata-orgDictItemCode").val();
                var name = $("#updata-orgDictItemName").val();
                var sort = $("#updata-orgDictItemSort").val();
                var description = $("#updata-orgDictItemDescription").val();
            $.ajax({
                url:context.path+"/orgdict/updateDictItem",
                type:"post",
                dataType:"json",
                data:{orgCode:orgCode,id:orgDictItemId,code:code,name:name,description:description,sort:sort},
                async:false,
                success: function (data) {
                    if(data.successFlg){
                        loadOrgDictItemGrid(orgDictSeq);
                        $(".close",$("#updataOrgDictItemModal")).click();
                    }else{
                        alert(data.errorMsg);
                    }
                },
                error:function(data){
                    alert($.i18n.prop('message.update.failure'));
                }
            });
        });

        //新增数据元的方法
        $("#orgDictItemAdd").click(function(){
            if(!orgDictItemAddValidator.validate()){return;}
            var code = $("#orgDictItemCode").val();
            var name = $("#orgDictItemName").val();
            var description = $("#orgDictItemDescription").val();
            var sort = $("#orgDictItemSort").val();
            $.ajax({
                url:context.path+"/orgdict/createOrgDictItem",
                type:'post',
                dataType:"json",
                data:{orgCode:orgCode,orgDictSeq:orgDictSeq,code:code,name:name,description:description,sort:sort},
                async:false,
                success:function(data){
                    if(data.successFlg){
                        loadOrgDictItemGrid(orgDictSeq);
                        $(".close",$("#createOrgDictItemModal")).click();
                        alert($.i18n.prop("message.new.success"));
                    }else{
                        alert(data.errorMsg);
                    }
                },
                error:function(data){
                    alert($.i18n.prop('message.new.failure'));
                }
            })
        });

    });

});