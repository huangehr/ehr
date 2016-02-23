/**
 *
 */
define(function (require, exports, module) {
    // TODO
    require('app/adapter/orgDataSet.css');
    require('modules/chosen/chosen.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        viewController = require('viewController'),
        chosen = require('modules/chosen/chosen.jquery'),
        Util = require('utils'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',              //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });
    $(function(){

        <!--创建校验器-->
        var orgDataSetAddValidator = new JValidate.Validation($("#orgDataSetAdd-form"),{immediate:true,onSubmit:false});
        var orgDataSetUpdataValidator = new JValidate.Validation($("#orgDataSetUpdate-form"),{immediate:true,onSubmit:false});
        var orgMetaDataAddValidator = new JValidate.Validation($("#orgMetaDataAdd-form"),{immediate:true,onSubmit:false});
        var orgMetaDataUpdataValidator = new JValidate.Validation($("#orgMetaDataUpdate-form"),{immediate:true,onSubmit:false});

        var codename;                 //查询条件
        var orgDataSetId;             //机构数据集id
        var orgDataSetSeq;             //机构数据集序号数据元保存此信息
        var orgMetaDataId;            //机构数据元id
        var orgDataSetGrid = null;    //机构数据集表格对象
        var orgMetaDataGrid=null;     //机构数据集元表格对象
        var orgCode = null;           //机构对象

        $("#orgDataSetAdd-form","#orgMetaDataAdd-form","#orgDataSetUpdate-form","#orgMetaDataUpdate-form").on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });

        $("#createOrgDataSetModal").on('hidden.bs.modal', function (e) {
            orgDataSetAddValidator.reset();
        });
        $('#updataOrgDataSetModal').on('hidden.bs.modal', function (e) {
            orgDataSetUpdataValidator.reset();
        });
        $('#createOrgMetaDataModal').on('hidden.bs.modal', function (e) {
            orgMetaDataAddValidator.reset();
        });
        $('#updataOrgMetaDataModal').on('hidden.bs.modal', function (e) {
            orgMetaDataUpdataValidator.reset();
        });

        orgCode = $("#adapter_org").val();
        codename = $("#dataSet-codeAndName").val();

        orgMetaDataGrid = new Grid("#orgMetaDataGrid",{
            //url:context.path+"/orgdataset/searchOrgMetaDatas",
            postData: {orgDataSetSeq:orgDataSetSeq,codename:codename},
            datatype:'json',
            multiselect : true,
            shrinkToFit: true,
            mtype:'POST',
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
                    return '<a data-toggle="modal" data-target="#updataOrgMetaDataModal" class="J_modify-btn" data-rowid="'+grid.rowId+'">修改  / </a>' +
                        '<a data-toggle="modal" data-target="#deleteOrgDataSetModal" data-type="orgMetaData"  class="J_delete-btn" data-rowid="'+grid.rowId+'">删除</a>'
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


        orgDataSetGrid = new Grid('#orgDataSetGrid',{
            url:context.path+"/orgdataset/searchOrgDataSets",
            datatype:'json',
            shrinkToFit: true,
            scrollOffset: 0,
            mtype:'POST',
            widthLock:true,
            width:400,
            height: $(window).height() - 250,
            autoFit: true,
            marginHeight:250,
            rowNum:10,
            colNames:['id','sequence',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',hidden:true},
                {name:'sequence',index:'sequence',align:"center",hidden:true},
                {name:'code',index:'code',width:150,align:"center"},
                {name:'name',index:'name',width:150,align:"center"},
                {name:'operator',index:'operator',width:100,align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#updataOrgDataSetModal"  class="J_modify-btn" data-rowid="'+grid.rowId+'">' +
                        '<img  src="/ha/static-dev/images/Modify_btn_pre.png" /></a>' +
                        '<a data-toggle="modal" data-target="#deleteOrgDataSetModal" data-type="orgDataSet" class="J_delete-btn" data-rowid="'+grid.rowId+'">' +
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
                orgDataSetId = orgDataSetGrid.instance.getRowData(rowid).id;
                orgDataSetSeq = orgDataSetGrid.instance.getRowData(rowid).sequence;
                loadOrgMetaDataGrid(orgDataSetSeq);
            },
            page: 1,
            gridComplete:function(){
                var getRowData = orgDataSetGrid.instance.jqGrid("getRowData");
                if(getRowData.length>0){
                    orgDataSetSeq = getRowData[0].sequence;
                    orgDataSetId = getRowData[0].id;
                    loadOrgMetaDataGrid(orgDataSetSeq);
                }else{
                    loadOrgMetaDataGrid("");
                }
            }

        }).render();


        function loadOrgMetaDataGrid(orgDataSetSeq){
            //机构数据集列表单击加载机构数据集数据元方法
            codename = $("#metaData-codeAndName").val();
            orgMetaDataGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdataset/searchOrgMetaDatas",
                mtype:"POST",
                page : 1,
                postData: {orgCode:orgCode,orgDataSetSeq:orgDataSetSeq,codename:codename} //发送数据
            }).trigger("reloadGrid");
        }

        //数据集根据条件查询的方法
        $("#searchOrgDataSets").click(function(){
            codename = $("#dataSet-codeAndName").val();
            orgDataSetGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdataset/searchOrgDataSets",
                mtype:"POST",
                page : 1,
                postData: {codename:codename,orgCode:orgCode} //发送数据
            }).trigger("reloadGrid");
        });

        //数据源根据条件查询的方法
        $("#searchOrgMetaDatas").click(function(){
            loadOrgMetaDataGrid(orgDataSetSeq);
        });

        function reloadGrid(){
            var codename = $("#dataSet-codeAndName").val();
            orgDataSetGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/orgdataset/searchOrgDataSets",
                mtype:"POST",
                page : 1,
                postData: {codename:codename,orgCode:orgCode} //发送数据
            }).trigger("reloadGrid");
        }

        //数据集和数据元新增前清空的方法
        $("#addOrgDataSet,#addOrgMetaData").click(function(){
            clearModal();
        });


        function clearModal(){
            $("#orgDataSetCode").val("");
            $("#orgDataSetName").val("");
            $("#orgDataSetDescription").val("");
            $("#orgMetaDataCode").val("");
            $("#orgMetaDataName").val("");
            $("#orgMetaDataDescription").val("");
        }

        //机构数据集新增的方法
        $("#orgDataSetAdd").click(function(){
            if(!orgDataSetAddValidator.validate()){return;}
            var orgDataSetCode = $("#orgDataSetCode").val();
            var orgDataSetName = $("#orgDataSetName").val();
            var orgDataSetDescription = $("#orgDataSetDescription").val();
            $.ajax({
                url:context.path+"/orgdataset/createOrgDataSet",
                dataType:"json",
                type:"post",
                data:{"code":orgDataSetCode,"name":orgDataSetName,"description":orgDataSetDescription,"orgCode":orgCode},
                async:false,
                success:function(data){
                    if(data.successFlg){
                        reloadGrid();
                        $(".close",$("#createOrgDataSetModal")).click();
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


        //机构数据集修改和删除的方法
        new viewController("#orgDataSetGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    orgDataSetId = orgDataSetGrid.instance.getRowData(rowid).id;
                    orgDataSetSeq = orgDataSetGrid.instance.getRowData(rowid).sequence;
                    $("#orgDictUpdate").val(orgDataSetId);
                    $.ajax({
                        url:context.path+"/orgdataset/getOrgDataSet",
                        dataType:"json",
                        type:"post",
                        data:{id:orgDataSetId},
                        async:false,
                        success:function(data){
                            if (data.successFlg){
                                var orgDictModel = data.obj;
                                $("#updata-orgDataSetCode").val(orgDictModel.code);
                                $("#updata-orgDataSetName").val(orgDictModel.name);
                                $("#updata-orgDataSetDescription").val(orgDictModel.description);
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
                    orgDataSetId = orgDataSetGrid.instance.getRowData(rowid).id;
                    orgDataSetSeq = orgDataSetGrid.instance.getRowData(rowid).sequence;
                    $("#dataSet_Types").val(datatype);
                }
            }
        });

        //数据集修改的点击事件
        $("#updataOrgDataSet").click(function(){
            if(!orgDataSetUpdataValidator.validate()){return;}
            var code = $("#updata-orgDataSetCode").val();
            var name = $("#updata-orgDataSetName").val();
            var description = $("#updata-orgDataSetDescription").val();
            $.ajax({
                url:context.path+"/orgdataset/updateOrgDataSet",
                dataType:"json",
                type:"post",
                data:{orgCode:orgCode,id:orgDataSetId,code:code,name:name,description:description},
                async:false,
                success: function (data) {
                    if(data.successFlg){
                        reloadGrid();
                        $(".close",$("#updataOrgDataSetModal")).click();
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
            if($("#dataSet_Types").val()=="orgDataSet"){    //机构数据集
                $.ajax({
                    url:context.path+"/orgdataset/deleteOrgDataSet",
                    dataType:"json",
                    type:"post",
                    data:{id:orgDataSetId},
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
            if($("#dataSet_Types").val()=="orgMetaData"){     //机构数据集元
                $.ajax({
                    url:context.path+"/orgdataset/deleteOrgMetaData",
                    dataType:"json",
                    type:"post",
                    data:{id:orgMetaDataId},
                    async:false,
                    success:function(data){
                        if (data.successFlg) {
                            loadOrgMetaDataGrid(orgDataSetSeq);
                            //alert($.i18n.prop('message.delete.success'));
                        }
                    },
                    error:function(data){
                        alert($.i18n.prop('message.delete.failure'));
                    }
                });
                $("#dataSet_Types").val("");
            }
        });

        //批量删除数据源的方法
        $("#batchDeleteOrgMetaData").click(function(){
            var rowIds = orgMetaDataGrid.instance.jqGrid('getGridParam', 'selarrrow');
            var ids=[];
            for(var i= 0;i<rowIds.length;i++){
                ids[i]=orgMetaDataGrid.instance.getRowData(rowIds[i]).id;
            }
            $.ajax({
                url:context.path+"/orgdataset/deleteOrgMetaDataList",
                dataType:"json",
                type:"post",
                data:{ids:ids},
                async:false,
                success:function(data){
                    if (data.successFlg) {
                        loadOrgMetaDataGrid(orgDataSetSeq);
                    }
                },
                error:function(data){
                    alert($.i18n.prop('message.batch.delete.failure'));
                }
            });
        });


        //数据元修改和删除的方法
        new viewController("#orgMetaDataGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    orgMetaDataId = orgMetaDataGrid.instance.getRowData(rowid).id;
                    $.ajax({
                        url:context.path+"/orgdataset/getOrgMetaData",
                        type:"post",
                        dataType:"json",
                        data:{id:orgMetaDataId},
                        async:true,
                        success:function(data){
                            if(data.successFlg){
                                var orgMetaDataModel = data.obj;
                                $("#updata-orgMetaDataCode").val(orgMetaDataModel.code);
                                $("#updata-orgMetaDataName").val(orgMetaDataModel.name);
                                $("#updata-orgMetaDataDescription").val(orgMetaDataModel.description);
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
                    orgMetaDataId = orgMetaDataGrid.instance.getRowData(rowid).id;
                    var datatype = cell.attr("data-type");
                    $("#dataSet_Types").val(datatype);
                }
            }
        });


        //数据元修改的点击事件
        $("#orgMetaDataUpdate").click(function(){
            if(!orgMetaDataUpdataValidator.validate()){return;}
            var code = $("#updata-orgMetaDataCode").val();
            var name = $("#updata-orgMetaDataName").val();
            var description = $("#updata-orgMetaDataDescription").val();
            $.ajax({
                url:context.path+"/orgdataset/updateOrgMetaData",
                dataType:"json",
                type:"post",
                data:{orgDataSetSeq:orgDataSetSeq,orgCode:orgCode,id:orgMetaDataId,code:code,name:name,description:description},
                async:false,
                success: function (data) {
                    if(data.successFlg==true){
                        loadOrgMetaDataGrid(orgDataSetSeq);
                        $(".close",$("#updataOrgMetaDataModal")).click();
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
        $("#orgMetaDataAdd").click(function(){
            if(!orgMetaDataAddValidator.validate()){return;}
            var code = $("#orgMetaDataCode").val();
            var name = $("#orgMetaDataName").val();
            var description = $("#orgMetaDataDescription").val();
            $.ajax({
                url:context.path+"/orgdataset/createOrgMetaData",
                type:'post',
                dataType:"json",
                data:{orgDataSetSeq:orgDataSetSeq,orgCode:orgCode,code:code,name:name,description:description},
                async:false,
                success:function(data){
                    if(data.successFlg==true){
                        loadOrgMetaDataGrid(orgDataSetSeq);
                        $(".close",$("#createOrgMetaDataModal")).click();
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