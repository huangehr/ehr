/**
 * Created by zqb on 2015/8/24.
 */
define(function (require, exports, module) {
    // TODO
    require('app/adapter/adapterDataSet.css');
    require('modules/chosen/chosen.css');
    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        viewController = require('viewController'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });
    $(function(){
        //获取适配方案id
        var adapterPlanId = $("#adapter_plan_id").val();
        var dataSetId ;//当前点击数据集ID
        var dictId ;//当前点击字典ID
        var metaDataGrid=null;
        var dictEntryGrid = null;
        initOrgDataSet($("#org_data_set"),adapterPlanId);
        initOrgDict($("#org_dict"),adapterPlanId);
        getAdapterPlan(adapterPlanId);

        //初始化加载数据集的方法
        var dataSetGrid = new Grid('#data_set_grid',{
            url:context.path+"/adapterDataSet/searchAdapterDataSet",
            datatype:'json',
            shrinkToFit: true,
            mtype:'POST',
            scrollOffset: 0,
            widthLock:true,
            width:400,
            height: $(window).height() - 244,
            autoFit: true,
            marginHeight:244,
            rowNum:10,
            colNames:['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',hidden:true},
                {name:'code',index:'code',width:150,align:"center"},
                {name:'name',index:'name',width:150,align:"center"},
                {name:'operator',index:'operator',width:100,align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#delete_row_modal" class="J_delete-btn" data-rowid="'+grid.rowId+'"><img src="/ha/static-dev/images/Delete_btn_pre.png" /></a>'
                }}
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false
            },
            postData: {adapterPlanId:adapterPlanId,strKey:''},
            page: 1,
            //点击行，获取行id
            onSelectRow: function (rowid, status) {
                dataSetId = dataSetGrid.instance.getRowData(rowid).id;
                reloadMetaDataGrid();
            },
            //初始化加载第一行数据集的id
            gridComplete:function(){
                var getRowData = dataSetGrid.instance.jqGrid("getRowData");
                if(getRowData.length>0){
                    dataSetId = getRowData[0].id;
                }
                //初始化加载数据元的方法
                if(!metaDataGrid){//创建
                    metaDataGrid = new Grid("#meta_data_grid",{
                        url:context.path+"/adapterDataSet/searchAdapterMetaData",
                        datatype:'json',
                        multiselect : true,
                        shrinkToFit: true,
                        mtype:'POST',
                        scrollOffset: 0,
                        width: $(window).width() - 422,
                        height: $(window).height() - 244,
                        autoFit: true,
                        marginHeight:244,
                        marginWidth:422,
                        rowNum:10,
                        colNames:['id','metaDataId','标准数据元代码','标准数据元名称','机构数据表代码','机构数据表名称','机构数据元代码','机构数据元名称',$.i18n.prop('grid.operation')],
                        colModel:[
                            {name:'id',index:'id',align:"center",hidden:true},
                            {name:'metaDataId',index:'metaDataId',align:"center",hidden:true},
                            {name:'metaDataCode',index:'metaDataCode',align:"center"},
                            {name:'metaDataName',index:'metaDataName',align:"center"},
                            {name:'orgDataSetCode',index:'orgDataSetCode',align:"center"},
                            {name:'orgDataSetName',index:'orgDataSetName',align:"center"},
                            {name:'orgMetaDataCode',index:'orgMetaDataCode',align:"center"},
                            {name:'orgMetaDataName',index:'orgMetaDataName',align:"center"},
                            {name:'operator',index:'operator',align:"center",formatter:function(value,grid){
                                return '<a data-toggle="modal" data-target="#meta_data_modal" class="J_modify-btn" data-rowid="'+grid.rowId+'">修改  / </a><a data-toggle="modal" data-target="#delete_row_modal"  class="J_delete-btn" data-rowid="'+grid.rowId+'">删除</a>'
                            }}
                        ],
                        jsonReader: {
                            root: "detailModelList",
                            page: "currPage",
                            total: "totalPage",
                            records: "totalCount",
                            repeatitems: false
                        },
                        postData: {adapterPlanId:adapterPlanId,dataSetId:dataSetId,strKey:''},
                        page: 1
                    }).render();
                }else{
                    reloadMetaDataGrid();
                }
            }
        }).render();

        //数据集新增、搜索、删除
        new viewController("#data_set",{
            events: {
                'click .J_add-btn': 'add',
                'click .J_search-btn': 'search',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                add:function(e){
                    //todo:标准定制
                },
                search:function(e){
                    reloadDataSetGrid();
                },
                deleteItem:function(e){
                    //就是数据元的全选批量删除
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    dataSetId = dataSetGrid.instance.getRowData(rowid).id;
                    reloadMetaDataGrid();
                    var rowIds = metaDataGrid.instance.jqGrid('getDataIDs');
                    var id=[];
                    for(var i= 0;i<rowIds.length;i++){
                        id[i]=metaDataGrid.instance.getRowData(rowIds[i]).id;
                    }
                    $("#delete_id").val(id);
                    $("#delete_type").val("delDataSet");
                }
            }
        });

        // 数据元新增、搜索、修改、删除
        new viewController("#meta_data",{
            events: {
                'click .J_add-btn': 'add',
                'click .J_del-rows-btn': 'delRows',
                'click .J_search-btn': 'search',
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                add:function(e){
                    //
                    metaDataMode("ADD");
                    clearMetaData();
                    initStdMetaData($("#std_meta_data"),dataSetId,getMetaDataIds());
                },
                delRows:function(e){
                    var rowIds = metaDataGrid.instance.jqGrid('getGridParam', 'selarrrow');
                    var id=[];
                    for(var i= 0;i<rowIds.length;i++){
                        id[i]=metaDataGrid.instance.getRowData(rowIds[i]).id;
                    }
                    $("#delete_ids").val(id);
                    $("#delete_rows_type").val("delMetaData");
                },
                search:function(e){
                    reloadMetaDataGrid();
                },
                modifyItem:function(e){
                    clearMetaData();
                    initStdMetaData($("#std_meta_data"),dataSetId,null);
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = metaDataGrid.instance.getRowData(rowid).id;
                    $.ajax({
                        url:context.path+"/adapterDataSet/getAdapterMetaData",
                        type:"post",
                        dataType:"json",
                        data:{id:id},
                        async:true,
                        success:function(data) {
                            if (data.successFlg) {
                                var adapterData = data.obj;
                                $('#adapter_data_set_id').val(adapterData.id);
                                $('#std_meta_data').find("option[value='" + adapterData.metaDataId + "']").prop("selected", true);
                                $('#org_data_set').find("option[value='" + adapterData.orgDataSetSeq + "']").prop("selected", true);
                                $('#org_meta_data').find("option[value='" + adapterData.orgMetaDataSeq + "']").prop("selected", true);
                                $('#data_type').find("option[value='" + adapterData.dataType + "']").prop("selected", true);
                                $("#data_set_description").val(adapterData.description);
                                metaDataMode("MODIFY");
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
                    var id = metaDataGrid.instance.getRowData(rowid).id;
                    $("#delete_id").val(id);
                    $("#delete_type").val("delMetaData");
                }
            }
        });

        //初始化加载平台字典的方法
        var dictGrid = new Grid('#dict_grid',{
            url:context.path+"/adapterDict/searchAdapterDict",
            datatype:'json',
            shrinkToFit: true,
            scrollOffset: 0,
            mtype:'POST',
            widthLock:true,
            width:400,
            height: $(window).height() - 244,
            autoFit: true,
            marginHeight:244,
            rowNum:10,
            colNames:['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',hidden:true},
                {name:'dictCode',index:'dictCode',width:150,align:"center"},
                {name:'dictName',index:'dictName',width:150,align:"center"},
                {name:'operator',index:'operator',width:100,align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#delete_row_modal" class="J_delete-btn" data-rowid="'+grid.rowId+'"><img src="/ha/static-dev/images/Delete_btn_pre.png" /></a>'
                }}
            ],
            jsonReader: {
                root: "detailModelList",
                page: "currPage",
                total: "totalPage",
                records: "totalCount",
                repeatitems: false
            },
            postData: {adapterPlanId:adapterPlanId,strKey:''},
            page: 1,
            //点击行，获取行id
            onSelectRow: function (rowid, status) {
                dictId = dictGrid.instance.getRowData(rowid).id;
                reloadDictEntryGrid();
            },

            //初始化加载第一行字典的id
            gridComplete:function(){
                var getRowData = dictGrid.instance.jqGrid("getRowData");
                if(getRowData.length>0){
                    dictId = getRowData[0].id;
                }
                //初始化加载字典适配方案的方法
                if(!dictEntryGrid){
                    dictEntryGrid = new Grid("#dict_entry_grid",{
                        url:context.path+"/adapterDict/searchAdapterDictEntry",
                        datatype:'json',
                        multiselect : true,
                        shrinkToFit: true,
                        mtype:'POST',
                        scrollOffset: 0,
                        width: $(window).width() - 422,
                        height: $(window).height() - 244,
                        autoFit: true,
                        marginHeight:244,
                        marginWidth:422,
                        rowNum:10,
                        colNames:['id','dictEntryId','标准字典项代码','标准字典项值','机构字典代码','机构字典名称','机构字典项代码','机构字典项值',$.i18n.prop('grid.operation')],
                        colModel:[
                            {name:'id',index:'id',align:"center",hidden:true},
                            {name:'dictEntryId',index:'dictEntryId',align:"center",hidden:true},
                            {name:'dictEntryCode',index:'dictEntryCode',align:"center"},
                            {name:'dictEntryName',index:'dictEntryName',align:"center"},
                            {name:'orgDictCode',index:'orgDictCode',align:"center"},
                            {name:'orgDictName',index:'orgDictName',align:"center"},
                            {name:'orgDictItemCode',index:'orgDictItemCode',align:"center"},
                            {name:'orgDictItemName',index:'orgDictItemName',align:"center"},
                            {name:'operator',index:'operator',align:"center",formatter:function(value,grid){
                                return '<a data-toggle="modal" data-target="#dict_entry_modal" class="J_modify-btn" data-rowid="'+grid.rowId+'">修改  / </a><a data-toggle="modal" data-target="#delete_row_modal"  class="J_delete-btn" data-rowid="'+grid.rowId+'">删除</a>'
                            }}
                        ],
                        jsonReader: {
                            root: "detailModelList",
                            page: "currPage",
                            total: "totalPage",
                            records: "totalCount",
                            repeatitems: false
                        },
                        postData: {adapterPlanId:adapterPlanId,dictId:dictId,strKey:''},
                        page: 1

                    }).render();
                }else{
                    reloadDictEntryGrid()
                }
            }
        }).render();

        //字典的新增、搜索、删除
        new viewController("#dict",{
            events: {
                'click .J_add-btn': 'add',
                'click .J_search-btn': 'search',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                add:function(e){
                    //todo:是否要整个字典添加？
                },
                search:function(e){
                    reloadDictGrid();
                },
                deleteItem:function(e){
                    //就是字典项的全选批量删除
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    dictId = dictGrid.instance.getRowData(rowid).id;
                    reloadDictEntryGrid();
                    var rowIds = dictEntryGrid.instance.jqGrid('getDataIDs');
                    var id=[];
                    for(var i= 0;i<rowIds.length;i++){
                        id[i]=dictEntryGrid.instance.getRowData(rowIds[i]).id;
                    }
                    $("#delete_id").val(id);
                    $("#delete_type").val("delDict");
                }
            }
        });

        // 字典项的新增、搜索、修改、删除
        new viewController("#dict_entry",{
            events: {
                'click .J_add-btn': 'add',
                'click .J_del-rows-btn': 'delRows',
                'click .J_search-btn': 'search',
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                add:function(e){
                    //
                    dictEntryMode("ADD");
                    clearDictEntry();
                    initStdDictEntry($("#std_dict_entry"),dictId,getDictEntryIds());
                },
                delRows:function(e){
                    var rowIds = dictEntryGrid.instance.jqGrid('getGridParam', 'selarrrow');
                    var id=[];
                    for(var i= 0;i<rowIds.length;i++){
                        id[i]=dictEntryGrid.instance.getRowData(rowIds[i]).id;
                    }
                    $("#delete_ids").val(id);
                    $("#delete_rows_type").val("delDictEntry");
                },
                search:function(e){
                    reloadDictEntryGrid();
                },
                modifyItem:function(e){
                    clearDictEntry();
                    initStdDictEntry($("#std_dict_entry"),dictId,null);
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = dictEntryGrid.instance.getRowData(rowid).id;
                    $.ajax({
                        url:context.path+"/adapterDict/getAdapterDictEntry",
                        type:"post",
                        dataType:"json",
                        data:{id:id},
                        async:true,
                        success:function(data){
                            if (data.successFlg) {
                                var adapterData = data.obj;
                                $('#adapter_dict_id').val(adapterData.id);
                                $('#std_dict_entry').find("option[value='" + adapterData.dictEntryId + "']").prop("selected", true);
                                $('#org_dict').find("option[value='" + adapterData.orgDictSeq + "']").prop("selected", true);
                                $('#org_dict_entry').find("option[value='" + adapterData.orgDictEntrySeq + "']").prop("selected", true);
                                $("#dict_entry_description").val(adapterData.description);
                                //可编辑控制
                                dictEntryMode("MODIFY");
                            }
                        },
                        error:function(data){
                            alert("字典适配失败");
                        }
                    });
                },
                deleteItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = dictEntryGrid.instance.getRowData(rowid).id;
                    $("#delete_id").val(id);
                    $("#delete_type").val("delDictEntry");
                }
            }
        });


        <!--校验器-->
        var validator = new JValidate.Validation($("#meta_data_form"),{immediate:true,onSubmit:false});
        //焦点丢失校验
        $('#meta_data_form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        //模态
        $('#meta_data_modal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        //数据集和字典切换的方法
        $("#btn_dataset").click(function(){
            $("#adapter_dict").hide();
            $("#adapter_data_set").show();
        });
        $("#btn_dict").click(function(){
            $("#adapter_data_set").hide();
            $("#adapter_dict").show();
            $(window).trigger('resize');
        });


        //delete one row
        $("#delete_btn").click(function () {
            var type = $("#delete_type").val();
            var id = $("#delete_id").val();
            deleteFunction[type](id);
        });
        //delete multi row
        $("#delete_rows_btn").click(function () {
            var type = $("#delete_rows_type").val();
            var ids = $("#delete_ids").val();
            deleteFunction[type](ids);
        });


        //数据集、数据元、字典、字典项---删除与批量删除
        var deleteFunction={
            "delDataSet":function delDataSet(id){
                //数据集的删除
                deleteFunction.delMetaData(id);
                reloadDataSetGrid();
            },
            "delMetaData":function delMetaData(id){
                //数据元适配的删除
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapterDataSet/delMetaData",
                    data: {id: id},
                    dataType: "json",
                    success: function (data) {
                        if (data.successFlg) {
                            reloadMetaDataGrid();
                        } else {
                            alert("删除失败");
                            return;
                        }
                    },
                    error: function (data) {
                    }
                });
            },
            "delDict":function delDict(id){
                //字典的删除
                deleteFunction.delDictEntry(id);
                reloadDictGrid();
            },
            "delDictEntry":function delDictEntry(id){
                //字典项适配的删除
                $.ajax({
                    type: "POST",
                    url: context.path + "/adapterDict/delDictEntry",
                    data: {id: id},
                    dataType: "json",
                    success: function (data) {
                        if (data.successFlg) {
                            reloadDictEntryGrid();
                        } else {
                            alert("删除失败");
                            return;
                        }
                    },
                    error: function (data) {
                    }
                });
            }
        };
        //头信息
        function getAdapterPlan(id){
            $("#adapter_plan_name").val();
            $("#adapter_plan_parent").val();
            $("#adapter_plan_type").val();
            $("#adapter_plan_org").val();
            $.ajax({
                type: "POST",
                url: context.path + "/adapter/getAdapterPlan",
                data: {id: id},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        var model = data.obj.adapterPlan;
                        $("#adapter_plan_name").val(model.name);
                        $('#adapter_plan_parent').val(model.parentName);
                        $('#adapter_plan_type').val(model.typeValue);
                        $('#adapter_plan_org').val(model.orgValue);
                    }
                },
                error: function (data) {
                }
            });
        }

        //---------------------------------------数据集部分------------------------------------
        //选择机构数据集
        $("#org_data_set").change(function(){
            var orgDataSetSeq = $("#org_data_set").val();
            initOrgMetaData($("#org_meta_data"),orgDataSetSeq);
        });

        //新增、修改数据元适配
        $("#meta_data_update").click(function () {
            if(!validator.validate() ) {return;}
            $.ajax({
                type: "POST",
                url: context.path + "/adapterDataSet/updateAdapterMetaData",
                data: $('#meta_data_form').serialize()+'&adapterPlanId='+adapterPlanId+'&dataSetId='+dataSetId,
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        reloadMetaDataGrid();
                        $(".close",$('#meta_data_modal')).click();
                    } else {
                        alert("提交失败");
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });
        function getMetaDataIds(){
            var rowIds = metaDataGrid.instance.jqGrid('getDataIDs');
            var ids=[];
            for(var i= 0;i<rowIds.length;i++){
                ids[i]=metaDataGrid.instance.getRowData(rowIds[i]).metaDataId;
            }
            return ids;
        }

        //标准数据元的下拉
        function initStdMetaData(target,dataSetId,ids){
            $.ajax({
                url:context.path + "/adapterDataSet/getStdMetaData",
                type:"post",
                dataType:"json",
                async: false,
                data:{adapterPlanId:adapterPlanId,dataSetId:dataSetId},
                success:function(data){
                    target.empty();
                    if (data.successFlg){
                        var stdMetaData = data.obj;
                        var option =' ';
                        var exist;
                        for(var i=0;i<stdMetaData.length;i++) {
                            //id,name
                            exist=false;
                            var metaData = stdMetaData[i].split(",");
                            var metaDataId = metaData[0];
                            if (ids!==null){
                                for (var j = 0; j < ids.length; j++){
                                    if (ids[j]==metaDataId){
                                        exist=true;
                                        break;
                                    }
                                }
                            }
                            if (!exist){
                                option += '<option value="' + metaDataId + '">' + metaData[1] + '</option>';
                            }
                        }
                        target.append(option);
                    }
                }
            });
        }

        //机构数据集的下拉
        function initOrgDataSet(target,adapterPlanId) {
            $.ajax({
                url: context.path + "/adapterDataSet/getOrgDataSet",
                type: "post",
                dataType: "json",
                async: false,
                data:{adapterPlanId:adapterPlanId},
                success: function (data) {
                    target.empty();
                    if (data.successFlg){
                        var dataSets = data.obj;
                        var option = '<option></option>';
                        for(var i=0;i<dataSets.length;i++){
                            //id,name
                            var dataSet=dataSets[i].split(",");
                            option += '<option value="' + dataSet[0] + '">' + dataSet[1] + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }
        //机构数据元的下拉
        function initOrgMetaData(target,orgDataSetSeq){
            $.ajax({
                url:context.path + "/adapterDataSet/getOrgMetaData",
                type:"post",
                dataType:"json",
                async:false,
                data:{orgDataSetSeq:orgDataSetSeq,adapterPlanId:adapterPlanId},
                success:function(data){
                    target.empty();
                    if (data.successFlg) {
                        var metaDatas = data.obj;
                        var option = '<option></option>';
                        for (var i = 0; i < metaDatas.length; i++) {
                            //id,name
                            var metaData = metaDatas[i].split(",");
                            option += '<option value="' + metaData[0] + '">' + metaData[1] + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }

        //数据集的刷新
        function reloadDataSetGrid(){
            var strKey = $("#input_data_set").val();
            dataSetGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/adapterDataSet/searchAdapterDataSet",
                mtype:"POST",
                page : 1,
                postData: {adapterPlanId:adapterPlanId,strKey:strKey} //发送数据
            }).trigger("reloadGrid");
        }
        //数据元适配的刷新（根据方案ID、数据集ID获取数据元映射关系）
        function reloadMetaDataGrid(){
            var strKey=$("#input_meta_data").val();
            metaDataGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/adapterDataSet/searchAdapterMetaData",
                mtype:"POST",
                page : 1,
                postData: {adapterPlanId:adapterPlanId,dataSetId:dataSetId,strKey:strKey} //发送数据
            }).trigger("reloadGrid");
        }

        //数据元新增、修改弹出框清空
        function clearMetaData() {
            $('input', $("#meta_data_form")).val("");
            $('select', $("#meta_data_form")).val("");
            $('textarea', $("#meta_data_form")).val("");
        }
        function metaDataMode(type){
            if (type=='ADD'){
                $('#std_meta_data').attr("disabled",false);
            }
            if (type=='MODIFY'){
                $('#std_meta_data').attr("disabled",true);
            }
        }

        //----------------------------字典部分--------------------------------------------
        //选择机构字典
        $("#org_dict").change(function(){
            var orgDictSeq = $("#org_dict").val();
            initOrgDictEntry($("#org_dict_entry"),orgDictSeq);
        });

        //新增、修改字典项适配
        $("#dict_entry_update").click(function () {
            if(!validator.validate() ) {return;}
            $.ajax({
                type: "POST",
                url: context.path + "/adapterDict/updateAdapterDictEntry",
                data: $('#dict_entry_form').serialize()+'&adapterPlanId='+adapterPlanId+'&dictId='+dictId,
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        reloadDictEntryGrid();
                        $(".close",$('#dict_entry_modal')).click();
                    } else {
                        alert("提交失败");
                        return;
                    }
                },
                error: function (data) {
                }
            });
        });
        function getDictEntryIds(){
            var rowIds = dictEntryGrid.instance.jqGrid('getDataIDs');
            var ids=[];
            for(var i= 0;i<rowIds.length;i++){
                ids[i]=dictEntryGrid.instance.getRowData(rowIds[i]).dictEntryId;
            }
            return ids;
        }
        //标准字典项的下拉
        function initStdDictEntry(target,dictId,ids){
            $.ajax({
                url:context.path + "/adapterDict/getStdDictEntry",
                type:"post",
                dataType:"json",
                async: false,
                data:{adapterPlanId:adapterPlanId,dictId:dictId},
                success:function(data){
                    target.empty();
                    if (data.successFlg){
                        var stdDictEntry = data.obj;
                        var option =' ';
                        var exist;
                        for(var i=0;i<stdDictEntry.length;i++) {
                            //id,name
                            exist=false;
                            var dictEntry = stdDictEntry[i].split(",");
                            var dictEntryId = dictEntry[0];
                            if (ids!==null){
                                for (var j = 0; j < ids.length; j++){
                                    if (ids[j]==dictEntryId){
                                        exist=true;
                                        break;
                                    }
                                }
                            }
                            if (!exist){
                                option += '<option value="' + dictEntryId + '">' + dictEntry[1] + '</option>';
                            }
                        }
                        target.append(option);
                    }
                }
            });
        }
        //机构字典的下拉
        function initOrgDict(target,adapterPlanId) {
            $.ajax({
                url: context.path + "/adapterDict/getOrgDict",
                type: "post",
                dataType: "json",
                async: false,
                data:{adapterPlanId:adapterPlanId},
                success: function (data) {
                    target.empty();
                    if (data.successFlg) {
                        var dicts = data.obj;
                        var option = '<option></option>';
                        for (var i = 0; i < dicts.length; i++) {
                            //id,name
                            var dict = dicts[i].split(",");
                            option += '<option value="' + dict[0] + '">' + dict[1] + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }
        //机构字典项的下拉
        function initOrgDictEntry(target,orgDictSeq){
            $.ajax({
                url:context.path + "/adapterDict/getOrgDictEntry",
                type:"post",
                dataType:"json",
                async:false,
                data:{orgDictSeq:orgDictSeq,adapterPlanId:adapterPlanId},
                success:function(data){
                    target.empty();
                    if (data.successFlg) {
                        var dictEntrys = data.obj;
                        var option = '<option></option>';
                        for (var i = 0; i < dictEntrys.length; i++) {
                            //id,name
                            var dictEntry = dictEntrys[i].split(",");
                            option += '<option value="' + dictEntry[0] + '">' + dictEntry[1] + '</option>';
                        }
                        target.append(option);
                    }
                }
            });
        }
        //字典的刷新
        function reloadDictGrid(){
            var strKey = $("#input_dict").val();
            dictGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/adapterDict/searchAdapterDict",
                mtype:"POST",
                page : 1,
                postData: {adapterPlanId:adapterPlanId,strKey:strKey} //发送数据
            }).trigger("reloadGrid");
        }
        //字典项适配的刷新（根据方案ID、字典ID获取字典项的映射关系）
        function reloadDictEntryGrid(){
            var strKey=$("#input_dict_entry").val();
            dictEntryGrid.instance.jqGrid('setGridParam', {
                url: context.path+"/adapterDict/searchAdapterDictEntry",
                mtype:"POST",
                page : 1,
                postData: {adapterPlanId:adapterPlanId,dictId:dictId,strKey:strKey} //发送数据
            }).trigger("reloadGrid");
        }
        //字典项新增、修改弹出框清空
        function clearDictEntry() {
            $('input', $("#dict_entry_form")).val("");
            $('select', $("#dict_entry_form")).val("");
            $('textarea', $("#dict_entry_form")).val("");
        }
        //字典项可编辑控制
        function dictEntryMode(type){
            if (type=='ADD'){
                $('#std_dict_entry').attr("disabled",false);
            }
            if (type=='MODIFY'){
                $('#std_dict_entry').attr("disabled",true);
            }
        }


        //滞后的初始化
        $("#btn_dataset").trigger('click');
    });

});