/**
 * Created by zqb on 2015/8/24.
 */
define(function (require, exports, module) {
    // TODO
    require('app/std/dataset/dataset.css');
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
        var dataSetAddValidator = new JValidate.Validation($("#add-form"),{immediate:true,onSubmit:false});
        var  dataSetUpdataValidator = new JValidate.Validation($("#update-form"),{immediate:true,onSubmit:false});
        var metaDataAddValidator = new JValidate.Validation($("#metaDataAdd-form"),{immediate:true,onSubmit:false});
        var metaDataUpdataValidator = new JValidate.Validation($("#metaDataUpdata-form"),{immediate:true,onSubmit:false});

        var codename = $("#input-data").val();
        var metaData =$("#input-metaData").val();
        var dataSetId ="";
        var metaDatagrid=null;
        //版本选择的方法
        var version;

        $("#add-form").on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $("#createDataModal").on('hidden.bs.modal', function (e) {
            dataSetAddValidator.reset();
        });

        $('#update-form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#updataDataModal').on('hidden.bs.modal', function (e) {
            dataSetUpdataValidator.reset();
        });

        $('#metaDataAdd-form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#createMetaDataModal').on('hidden.bs.modal', function (e) {
            metaDataAddValidator.reset();
        });

        $('#metaDataUpdata-form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        $('#updataMetaDataModal').on('hidden.bs.modal', function (e) {
            metaDataUpdataValidator.reset();
        });

        initVersionDDL($('#optionVersion'));

        $('#optionVersion').change(function(){
            var version=$(this).val();
            var codename = $("#input-data").val();
            grid.instance.jqGrid('setGridParam', {
                page : 1,
                postData: {codename:codename,version:version}
            }).trigger("reloadGrid");
        });

        version = $("#optionVersion").val();
        //初始化加载数据集的方法
        var grid = new Grid('#dataSetGrid',{
            url:context.path+"/std/dataset/searchDataSets",
            datatype:'json',
            shrinkToFit: true,
            scrollOffset: 0,
            widthLock:true,
            mtype:'POST',
            width:400,
            //width: $(window).width() - 410,
            height: $(window).height() - 194,
            autoFit: true,
            marginHeight:194,
            rowNum:10,
            colNames:['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel:[
                {name:'id',index:'id',hidden:true},
                {name:'code',index:'code',width:150,align:"center"},
                {name:'name',index:'name',width:150,align:"center"},
                {name:'operator',index:'operator',width:100,align:"center",formatter:function(value,grid){
                    return '<a data-toggle="modal" data-target="#updataDataModal" class="J_modify-btn" data-rowid="'+grid.rowId+'"><img  src="/ha/static-dev/images/Modify_btn_pre.png" /></a><a data-toggle="modal" data-target="#deleteDataSetModal" data-type="dataset" class="J_delete-btn" data-rowid="'+grid.rowId+'"><img class="f-ml30" src="/ha/static-dev/images/Delete_btn_pre.png" /></a>'
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
            postData: {codename:codename,version:version},
            page: 1,

            onSelectRow: function (rowid, status) {

                var id = grid.instance.getRowData(rowid).id;

                $("#datasetIdc").val(id);
                metaDataGrid(id);
            },

            //初始化加载第一行数据集的id
            gridComplete:function(){
                var getRowData = grid.instance.jqGrid("getRowData");
                if(getRowData.length>0){
                    dataSetId = getRowData[0].id;
                }
                $("#datasetIdc").val(dataSetId);

                //初始化加载数据源的方法
                if(metaDatagrid==null){//创建
                    metaDatagrid = new Grid("#metaDataGrid",{

                        url:context.path+"/std/dataset/searchMetaData",
                        datatype:'json',
                        mtype:'POST',
                        multiselect : true,
                        shrinkToFit: true,
                        scrollOffset: 0,
                        width: $(window).width() - 422,
                        height: $(window).height() - 194,
                        autoFit: true,
                        marginHeight:194,
                        marginWidth:422,
                        rowNum:10,
                        colNames:['id',$.i18n.prop('grid.code1'),$.i18n.prop('grid.innerCode'),$.i18n.prop('grid.data.element.name'),$.i18n.prop('grid.columnType'),$.i18n.prop('grid.dictName'),$.i18n.prop('grid.operation')],
                        colModel:[
                            {name:'id',index:'id',align:"center",hidden:true},
                            {name:'code',index:'code',align:"center"},
                            {name:'innerCode',index:'innerCode',align:"center"},
                            {name:'name',index:'name',align:"center"},
                            {name:'columnType',index:'columnType',align:"center"},
                            {name:'dictName',index:'dictName',align:"center"},
                            {name:'operator',index:'operator',align:"center",formatter:function(value,grid){
                                return '<a data-toggle="modal" data-target="#updataMetaDataModal" class="J_modify-btn" data-rowid="'+grid.rowId+'">修改  / </a><a data-toggle="modal" data-target="#deleteDataSetModal" data-type="metadata"  class="J_delete-btn" data-rowid="'+grid.rowId+'">删除</a>'
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

                        postData: {id:dataSetId,version:version,metaDataCode:metaData},
                        page: 1

                    }).render();
                }else{
                    metaDataGrid(dataSetId);
                }

            }

        }).render();

        //数据集根据条件查询的方法
        $("#searchDatasetCode").click(function(){
            reloadGrid();
        });
        function reloadGrid(){
            var codename = $("#input-data").val();
            if(!(Util.isStrEquals(codename,null)||Util.isStrEquals(codename,""))){
                grid.instance.jqGrid('setGridParam', {
                    url: context.path+"/std/dataset/searchDataSets",
                    mtype:"POST",
                    page : 1,
                    postData: {codename:codename,version:version} //发送数据
                }).trigger("reloadGrid");
            }else{
                grid.instance.jqGrid('setGridParam', {
                    url: context.path+"/std/dataset/searchDataSets",
                    mtype:"POST",
                    postData: {codename:codename,version:version} //发送数据
                }).trigger("reloadGrid");
            }
        }

        //数据集和数据元新增前清空的方法
        $("#addDataSet,#addMetaData").click(function(){
            clearModal();
        });

        function initVersionDDL(target){
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
                    target.append(option);
                }
            });
        }

        function clearModal(){
            $("#dataSetCode").val("");
            $("#dataSetName").val("");
            $("#dataSetType").val("");
            $("#dataSetStandardSource").val("");
            $("#dataSetexplain").val("");

            $("#metaDataCode").val("");//代码
            $("#metaDataName").val("");//名称
            $("#metaDataInnerCode").val("");//内部代码
            $("#metaDataType").val("");//数据源类型
            $("#metaDataDict").val("");//检查字典
            $("#metaDataFormat").val("");//格式
            $("#metaDataDefinition").val("");//说明
            $("#fieldName").val("");//字段名
            $("#fieldLength").val("");//字段长度
            $("#datatype").val("");//数据类型
            $("#primaryKey").val("");//是否主键
            $("#whetherNull").val("");//是否为空
        }

       //数据集新增的方法
        $("#dataSetAdd").click(function(){

            if(!dataSetAddValidator.validate()){return;}
            var dataSetCode = $("#dataSetCode").val();
            var dataSetName = $("#dataSetName").val();
            var dataSetType = $("#dataSetType").val();
            var dataSetStandardSource = $("#dataSetStandardSource").val();
            var dataSetexplain = $("#dataSetexplain").val();

            $.ajax({
                url:context.path+"/std/dataset/addDataSet",
                datatype:"json",
                type:"post",
                data:{dataSetCode:dataSetCode,dataSetName:dataSetName,dataSetType:dataSetType,dataSetStandardSource:dataSetStandardSource,dataSetexplain:dataSetexplain},
                async:true,
                success:function(data){
                    reloadGrid();
                    $(".close",$("#createDataModal")).click();
                },
                error:function(data){
                    alert($.i18n.prop("message.new.failure"));
                }
            });
        });

        //数据集修改和删除的方法
        new viewController("#dataSetGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var dataSetId = grid.instance.getRowData(rowid).id;
                    $("#dataSet-Updata").val(dataSetId);
                    $.ajax({
                        url:context.path+"/std/dataset/getDataSet",
                        mtype:"POST",
                        datatype:"json",
                        data:{dataSetId:dataSetId},
                        async:true,
                        success:function(data){

                            var result = $.parseJSON(data).obj;

                            $("#updata-dataSetCode").val(result.code);
                            $("#updata-dataSetName").val(result.name);
                            $("#updata-dataSetType").val();
                            $("#updata-dataSetStandardSource").find("option[value='"+result.refStandard+"']").attr("selected",true);
                            //$("#updata-dataSetStandardSource").val(result.reference);
                            $("#updata-dataSetexplain").val(result.summary);
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
                    var dataSetId = grid.instance.getRowData(rowid).id;
                    $("#delete-datasetId").val(dataSetId);
                    $("#dataTypes").val(datatype);
                }
            }
        });
        //数据集修改的点击事件
        $("#updataDataSet").click(function(){
            if(!dataSetUpdataValidator.validate()){return;}
            var dataSetUpdata = $("#dataSet-Updata").val();
            var code = $("#updata-dataSetCode").val();
            var name = $("#updata-dataSetName").val();
            var type = $("#updata-dataSetType").val();
            var standardSource = $("#updata-dataSetStandardSource").val();
            var summary= $("#updata-dataSetexplain").val();

            $.ajax({
                url:context.path+"/std/dataset/updataDataSet",
                datatype:"json",
                type:"post",
                data:{dataSetId:dataSetUpdata,code:code,name:name,type:type,standardSource:standardSource,summary:summary},
                async:true,
                success: function (data) {
                    reloadGrid();
                    $(".close",$("#updataDataModal")).click();
                },
                error:function(data){
                    alert($.i18n.prop('message.update.failure'));
                }
            });
        });

        //删除数据集和数据元的点击事件
        $(".affirmdelete").click(function(){
            if($("#dataTypes").val()=="dataset"){//数据集

                var dataSetId = $("#delete-datasetId").val();
                $.ajax({
                    url:context.path+"/std/dataset/deleteDataSet",
                    datatype:"json",
                    type:"post",
                    data:{dataSetId:dataSetId,version:version},
                    async:true,
                    success:function(data){
                        reloadGrid();
                    },
                    error:function(data){
                        alert($.i18n.prop('message.delete.failure'));
                    }
                });
            }
            if($("#dataTypes").val() == "metadata"){//数据元
                var metadataId = $("#delete-metadataId").val();
                $.ajax({
                    url:context.path+"/std/dataset/deleteMetaData",
                    datatype:"json",
                    type:"post",
                    data:{metaDataId:metadataId,version:version},
                    async:true,
                    success:function(data){
                        var dataSetId = $("#datasetIdc").val();
                        metaDataGrid(dataSetId);
                    },
                    error:function(data){
                        alert($.i18n.prop('message.delete.failure'));
                    }
                });
                $("#dataTypes").val("");
            }
        });

        //批量删除数据源的方法
        $("#deleteMetaDatas").click(function(){

        });

        $("#batchDeleteMetaData").click(function(){
            var ids = metaDatagrid.instance.jqGrid('getGridParam','selarrrow');
            var idStr = "";
            for(var i = 0; i<ids.length;i++){
                idStr+=metaDatagrid.instance.getRowData(ids[i]).id+",";
            }
            $.ajax({
                url:context.path+"/std/dataset/deleteMetaDatas",
                datatype:"json",
                type:"post",
                data:{metaDataId:idStr,version:version},
                async:false,
                success:function(data){
                    var dataSetId = $("#datasetIdc").val();
                    metaDataGrid(dataSetId);
                },
                error:function(data){
                    alert($.i18n.prop('message.batch.delete.failure'));
                }
            });
        });

        //数据源根据条件查询的方法
        $("#searchMetaData").click(function(){
            var metaDataId=grid.instance.jqGrid('getGridParam','selrow');
            var id = grid.instance.getRowData(metaDataId).id;
            metaDataGrid(id);
        });
        function metaDataGrid(dataSetId){
            var metaData=$("#input-metaData").val();
            metaDatagrid.instance.jqGrid('setGridParam', {
                url: context.path+"/std/dataset/searchMetaData",
                mtype:"POST",
                page : 1,
                postData: {id:dataSetId,version:version,metaDataCode:metaData}, //发送数据
            }).trigger("reloadGrid");
        }

        //给数据元主键和是否空值的选择
        $("#primaryKey").click(function(){
            if($(this).prop("checked"))
            {
                $('#whetherNull').prop('checked',true);

            }else{
                $('#whetherNull').prop('checked',false);
            }
        });

        $("#updatePrimaryKey").click(function(){
            if($(this).prop("checked"))
            {
                $('#updateWhetherNull').prop('checked',true);
            }else{
                $('#updateWhetherNull').prop('checked',false);
            }
        });

        //数据元修改和删除的方法
        new viewController("#metaDataGrid",{
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers:{
                modifyItem:function(e){
                    var dataSetId = $("#datasetIdc").val();
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var metaDataId = metaDatagrid.instance.getRowData(rowid).id;
                    $("#updata-metadataId").val(metaDataId);
                    $.ajax({
                        url:context.path+"/std/dataset/getMetaData",
                        type:"post",
                        datatype:"json",
                        data:{dataSetId:dataSetId,metaDataId:metaDataId,version:version},
                        async:true,
                        success:function(data){
                            $("#updateMetaDataCode").val($.parseJSON(data).obj[0][0]);
                            $("#updateMetaDataName").val($.parseJSON(data).obj[0][2]);
                            $("#updateMetaDataInnerCode").val($.parseJSON(data).obj[0][1]);
                            $("#updateMetaDataType").val($.parseJSON(data).obj[0][3]);
                            $("#updatecriterionDict").find("option[value='-1']").attr("selected",true).trigger("liszt:updated");
                            $("#updatecriterionDict").find("option[value='"+$.parseJSON(data).obj[0][5]+"']").attr("selected",true).trigger("liszt:updated");
                            $("#updateMetaDataFormat").val($.parseJSON(data).obj[0][4]);
                            $("#updateMetaDataDefinition").val($.parseJSON(data).obj[0][6]);
                            $("#updateFieldName").val($.parseJSON(data).obj[0][9]);
                            $("#updateFieldLength").val($.parseJSON(data).obj[0][10]);
                            $("#updateDatatype").val($.parseJSON(data).obj[0][8]);
                            $("#updatePrimaryKey").val($.parseJSON(data).obj[0][11]);
                            $("#updateWhetherNull").val($.parseJSON(data).obj[0][7]);
                            if(Util.isStrEquals($.parseJSON(data).obj[0][11],false)){
                                $("#updatePrimaryKey").prop('checked',false);
                            }else{
                                $("#updatePrimaryKey").prop('checked',true);
                                $("#updateWhetherNull").prop('checked',true);
                            }
                            if(Util.isStrEquals($.parseJSON(data).obj[0][7],false)){
                                $("#updateWhetherNull").prop('checked',false);
                            }else{
                                $("#updateWhetherNull").prop('cheched',true);
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
                    var metaDataId = metaDatagrid.instance.getRowData(rowid).id;
                    var datatype = cell.attr("data-type");
                    $("#delete-metadataId").val(metaDataId);
                    $("#dataTypes").val(datatype);
                }
            }
        });


        //数据元修改的点击事件
        $("#updateMetaData").click(function(){
            if(!metaDataUpdataValidator.validate()){return;}
            var dataSetId = $("#datasetIdc").val();
            var metaDataId = $("#updata-metadataId").val();
            var metaDataCode = $("#updateMetaDataCode").val();
            var metaDataName = $("#updateMetaDataName").val();
            var metaDataInnerCode = $("#updateMetaDataInnerCode").val();
            var metaDataType = $("#updateMetaDataType").val();
           //var metaDataDict = $("#criterionDict").val();

            var metaDataDict = $("#updatecriterionDict").val();
            var metaDataFormat = $("#updateMetaDataFormat").val();
            var metaDataDefinition = $("#updateMetaDataDefinition").val();
            var fieldName = $("#updateFieldName").val();
            var fieldLength = $("#updateFieldLength").val();
            var datatype = $("#updateDatatype").val();
            var primaryKey=0;//是否主键
            var whetherNull = 0;//是否为空
            if ($('#updatePrimaryKey').prop('checked')) {
                primaryKey=1;
                whetherNull=1;
            }
            if ($('#updateWhetherNull').prop('checked')) {
                whetherNull=1;
            }

            $.ajax({
                url:context.path+"/std/dataset/updataMetaSet",
                type:"post",
                datatype:"json",
                data:{metaDataId:metaDataId,version:version,dataSetId:dataSetId,metaDataCode:metaDataCode,metaDataName:metaDataName,metaDataInnerCode:metaDataInnerCode,metaDataType:metaDataType,metaDataDict:metaDataDict,metaDataFormat:metaDataFormat,
                    metaDataDefinition:metaDataDefinition,fieldName:fieldName,fieldLength:fieldLength,datatype:datatype,primaryKey:primaryKey,whetherNull:whetherNull},
                async:false,
                success: function (data) {
                    var dataSetId = $("#datasetIdc").val();
                    metaDataGrid(dataSetId);
                    $(".close",$("#updataMetaDataModal")).click();
                },
                error:function(data){
                    alert($.i18n.prop('message.update.failure'));
                }
            });
        });

        //新增数据元的方法
        $("#metaDataAdd").click(function(){
            if(!metaDataAddValidator.validate()){return;}
            var dataSetId = $("#datasetIdc").val();
            var metaDataCode = $("#metaDataCode").val();//代码
            var metaDataName = $("#metaDataName").val();//名称
            var metaDataInnerCode = $("#metaDataInnerCode").val();//内部代码
            var metaDataType =  $("#metaDataType").val();//数据源类型
            var metaDataDict = $("#criterionDict").val();//检查字典
            var metaDataFormat = $("#metaDataFormat").val();//格式
            var metaDataDefinition = $("#metaDataDefinition").val();//说明
            var fieldName = $("#fieldName").val();//字段名
            var fieldLength = $("#fieldLength").val();//字段长度
            var datatype = $("#datatype").val();//数据类型
            $("#criterionDict").find("option[value='-1']").attr("selected",true).trigger("liszt:updated");
            var primaryKey=0;//是否主键
            var whetherNull = 0;//是否为空
            if ($('#primaryKey').prop('checked')) {
                primaryKey=1;
                whetherNull=1;
            }
            if ($('#whetherNull').prop('checked')) {
                whetherNull=1;
            }
            $.ajax({
                url:context.path+"/std/dataset/addMetaData",
                type:'post',
                datatype:"json",
                data:{version:version,dataSetId:dataSetId,metaDataCode:metaDataCode,metaDataName:metaDataName,metaDataInnerCode:metaDataInnerCode,metaDataType:metaDataType,metaDataDict:metaDataDict,
                    metaDataFormat:metaDataFormat,metaDataDefinition:metaDataDefinition,fieldName:fieldName,fieldLength:fieldLength,datatype:datatype,primaryKey:primaryKey,whetherNull:whetherNull},
                async:true,
                success:function(data){
                    var dataSetId = $("#datasetIdc").val();
                    metaDataGrid(dataSetId);
                    $(".close",$("#createMetaDataModal")).click();
                },
                error:function(data){
                    alert($.i18n.prop('message.new.failure'));
                }
            })
        });

        //标准来源的方法
        $.ajax({
            url:context.path+"/std/dataset/getStdSourceList",
            type:"post",
            async:false,
            dataType:"json",
            data:{version:version},
            success:function(data){

                var result = JSON.parse(data.result);
                var option =' ';
                for(var i=0;i<result.length;i++){
                    option +='<option value='+result[i].id+'>'+result[i].name+'</option>';
                }
                $("#dataSetStandardSource").append(option);
                $("#updata-dataSetStandardSource").append(option);
            }
        });

        //检验字典的方法
        $.ajax({
            url:context.path+"/std/dataset/getMetaDataDict",
            type:"post",
            datatype:"json",
            data:{version:version},
            async:false,
            success:function(data){
                var result = $.parseJSON(data).detailModelList;
                var option = '<option value="-1">请选择检验字典</option>';
                for(var i=0;i<result.length;i++){
                    option +='<option value="'+result[i].id+'">'+result[i].name+'</option>';
                }
                $("#criterionDict").append(option);  //新增字段绑定
                $("#updatecriterionDict").append(option);//修改字段绑定

            }
        });
        $("#criterionDict").chosen();
        $("#updatecriterionDict").chosen().change();

    });

});