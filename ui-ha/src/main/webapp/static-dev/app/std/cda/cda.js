/**
 * Created by AndyCai on 2015/9/18.
 */
define(function (require, exports, module) {

    var $= require('base').$,
        Grid=require('modules/components/grid'),
        context = require('base').context,
        ViewController=require('viewController');
    require('app/common.css');

    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    var cda={};
    cda.list={
        top:null,
        _url:$("#hd_url").val(),
        grid:null,
        relationGrid:null,
        datasetGrid:null,
        columns:[],
        colNames:[],
        relationColumns:[],
        relationColNames:[],
        datasetColumns:[],
        datasetColNames:[],
        enableData : [],
        relationIds:"",
        relationSet:[],
        init: function () {

            cda.list.colNames=[ 'id', $.i18n.prop('grid.code'), $.i18n.prop('grid.name'),$.i18n.prop('grid.operation')];
            cda.list.columns=[
                {name: 'id', index: 'id', hidden:true},
                {name: 'code', index: 'code', width: 150, align: "center"},
                {name: 'name', index: 'name', width: 149, align: "center"},
                {name: 'operator ', index: 'operator', width: 100, sortable: false, align: "center",
                    formatter: function (value, cdagrid, data, state) {

                        return  "<div title='"+$.i18n.prop('btn.edit')+"' data-toggle='modal' data-target='#modifyDictRowModal' class='J_modify-btn image-modify' "+
                                "onclick='seajs.use(\""+cda.list._url+"/static-dev/app/std/cda/cda.js\", function (e) {e.editCDA(\""+data.id+"\");})'></div>"+
                            "<div title='"+$.i18n.prop('btn.delete')+"' data-toggle='modal' data-target='#deleteDictRowModal' class='J_delete-btn image-delete' style='margin-left: 50px;'"+
                            "onclick='seajs.use(\""+cda.list._url+"/static-dev/app/std/cda/cda.js\", function (e) {e.setDeleteCdaId(\""+data.id+"\");})'>"+
                            "</div>";
                    }
                }
            ];

            cda.list.relationColNames=[ 'id', $.i18n.prop('grid.dataset.code'), $.i18n.prop('grid.dataset.name'),$.i18n.prop('grid.summary'), $.i18n.prop('grid.operation')];

            cda.list.relationColumns=[
                {name: 'id', index: 'id', hidden:true},
                {name: 'dataset_code', index: 'dataset_code',width: '25%', align: "center"},
                {name: 'dataset_name', index: 'dataset_name', width: '30%',align: "center"},
                {name: 'summary', index: 'summary', width: '35%',align: "center"},
                {name: 'operator ', index: 'operator', width: '10%', sortable: false, align: "center",
                    formatter: function (value, cdagrid, data, state) {
                        return  "<a href='javascript:void(0);' data-toggle='modal' data-target='#deleteRowModal' class='J_delete-btn' "+
                            "onclick='seajs.use(\""+cda.list._url+"/static-dev/app/std/cda/cda.js\", function (e) {e.setDeleteRelationId(\""+data.id+"\");})'>"+$.i18n.prop('btn.delete')+"</a>";
                    }
                }
            ];

            cda.list.datasetColNames=['id',$.i18n.prop('grid.dataset.code'),$.i18n.prop('grid.dataset.name')];
            cda.list.datasetColumns=[
                {name: 'id', index: 'id', hidden:true},
                {name: 'code', index: 'code',width: '40%', align: "center"},
                {name: 'name', index: 'name', width: '60%',align: "center"}
            ];

            //加载标准版本
            cda.list.initVersionDDL($("#cdaVersion"));

            cda.list.event();
        },
        getCdaList: function () {

            var strKey = $("#searchNm").val();
            var strVersion = $("#cdaVersion").val();

            var u = cda.list;
            if(u.grid==null){
                u.grid = new Grid('#dictGrid', {
                    url:u._url+"/cda/GetCdaListByKey",
                    datatype: 'json',
                    height: 594,
                    rowNum: 10,
                    rowList : [ 10, 20, 30 ],
                    colNames: u.colNames,
                    colModel: u.columns,
                    postData: {strKey: strKey, strVersion: strVersion},
                    page: 1,
                    jsonReader: {
                        root: "detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: false,
                        id: "0"
                    },
                    onSelectRow : function(rowid) {

                        var cdaId = u.grid.instance.getRowData(rowid).id;
                            cda.list.createDictEntryGrid(cdaId);

                    },
                    gridComplete : function() {

                        if (u.grid.instance.jqGrid('getDataIDs')!=0)
                        {
                            //将第一行设置为选中行，并且触发事件
                            u.grid.instance.setSelection(1,true);
                        }else
                        {
                            cda.list.createDictEntryGrid("undefined");
                        }
                    },
                }).render();
            }
            else{//reload
                u.grid.instance.jqGrid('setGridParam', {
                    url : u._url+"/cda/GetCdaListByKey",
                    postData: {strKey: strKey, strVersion: strVersion},
                    page:1,
                    rows:10
                }).trigger('reloadGrid');
            }
        },
        initVersionDDL:function(objectTarget){
            $.ajax({
                url:cda.list._url+"/cdadict/getCdaVersionList",
                type:"post",
                dataType:"json",
                data:{page:"1",rows:"100"},
                success:function(data){
                    var result = eval(data.result);
                    var option ='';
                    for(var i=0;i<result.length;i++){
                        var version = result[i].version;
                        var versionArr=version.split(",");
                        option += '<option value=' + versionArr[0] + '>' + versionArr[1] + '</option>';
                    }
                    objectTarget.append(option);

                    objectTarget.change();
                    //cda.list.getCdaList();

                    //加载标准来源
                    cda.list.initStandardSource();
                }
            });
        },
        initStandardSource: function () {

            $("#sel_stdSource").empty();
            var cdaVersion = $("#cdaVersion").val();
            $.ajax({
                url:cda.list._url+"/cdadict/getStdSourceList",
                type:"post",
                dataType:"json",
                data:{strVersionCode:cdaVersion},
                success:function(data){
                    var result = eval(data.result);
                    var option ='';
                    if(result!=null){

                        for(var i=0;i<result.length;i++){
                            option +='<option value='+result[i].id+'>'+result[i].name+'</option>';
                        }
                    }
                    else
                    {
                        option +='<option value="">'+$.i18n.prop("message.please.maintain.standard.source")+'</option>';
                    }
                    $("#sel_stdSource").append(option);
                }
            });
        },
        event: function () {

            $("#cdaVersion").change(function(){
                cda.list.getCdaList();
            });

            $("#btn_create").click(function () {
                cda.list.clearCdaDetail();
            });

            $("#updateCDABtn").click(function () {
                cda.list.saveCdaInfo();
            });

            $("#searchNm").keyup(function(e){
                e = window.event || e;
                if(e.keyCode == 13) {
                    cda.list.getCdaList();
                }
            });

            $("#deleteDictBtn").click(function () {
                var cdaId = $("#deleteDictId").val();
                cda.list.deleteCDA(cdaId);
            });

            $("#updateDictEntryBtn").click(function () {
                cda.list.saveRelationship();
            });

            $("#deleteDictEntryBtn").click(function () {
                var ids = $("#hdrelationId").val();
                cda.list.deleteRelation(ids);
            });

            $("#btn_Update_relation").click(function () {
                $("#txt_search").val("");
                cda.list.dataSetSelectInit();
            });

            $("#txt_search").keyup(function(e){
                e = window.event || e;
                if(e.keyCode == 13) {
                    cda.list.dataSetSelectGrid();
                }
            });

            $("#searchNmEntry").keyup(function (e) {
                e = window.event || e;
                if(e.keyCode == 13) {
                    var cdaId =  $("#dictIdForEntry").val();
                    cda.list.createDictEntryGrid(cdaId);
                }
            });

            $("#div_search").click(function () {
                cda.list.getCdaList();
            });

            $("#div_search_entry").click(function(){
                var cdaId =  $("#dictIdForEntry").val();
                cda.list.createDictEntryGrid(cdaId);
            });

            $("#btn_create_file").click(function () {
                //TODO:需先判断文件是否已经存在

                var strCdaId = $("#hdId").val();
                var strVersionCode = $("#cdaVersion").val();

                $.ajax({
                    url: cda.list._url+"/cda/createCDASchemaFile",
                    type:"get",
                    datatype:"json",
                    data:{strCdaId:strCdaId,strVersionCode:strVersionCode},
                    success:function(data){
                        var _res =  eval(data);
                        if (_res.successFlg) {
                            alert("ok");
                        }
                        else
                        {
                            alert(_res.errorMsg);
                        }
                    }
                })
            });

            //$("#btn_test").click(function(){
            //    var strVersion = $("#cdaVersion").val();
            //    $.ajax({
            //        url: cda.list._url+"/cda/TestFileSend",
            //        type:"get",
            //        datatype:"json",
            //        data:{strVersion:strVersion},
            //        success:function(data){
            //            var _res =  eval(data);
            //            if (_res.successFlg) {
            //                alert("ok");
            //            }
            //            else
            //            {
            //                alert(_res.errorMsg);
            //            }
            //        }
            //    })
            //});
        },
        editCDA: function (id) {

            if(id!=null && id!="")
            {
                $("#btn_create_file").show();
            }
            else{
                $("#btn_create_file").hide();
            }

            var versionCode = $("#cdaVersion").val();

            $.ajax({
                url: cda.list._url+"/cda/getCDAInfoById",
                type:"get",
                datatype:"json",
                data:{strId:id,strVersion:versionCode},
                success:function(data){

                    cda.list.clearCdaDetail();

                    var result =  eval(data);
                    var info = result.obj;
                    if(info!=null){
                        $("#hdId").val(info.id);
                        $("#txt_Code").val(info.code);
                        $("#txt_Name").val(info.name);
                        $("#txt_schema_path").val(info.schema);
                        console.log(info.source_id);
                        $("#sel_stdSource").val(info.source_id)
                        $("#txt_description").val(info.description);
                    }
                    else{
                        alert(result.errorMsg)
                    }
                }
            })

        },
        setDeleteCdaId: function (id) {
            $("#deleteDictId").val(id);
        },
        setDeleteRelationId: function (id) {
            $("#hdrelationId").val(id);
        },
        //保存CDA信息
        saveCdaInfo: function () {
            var id = $("#hdId").val();
            var code = $("#txt_Code").val();
            var name = $("#txt_Name").val();
            var schema_path = $("#txt_schema_path").val();
            var sourceId = $("#sel_stdSource").val();
            var description = $("#txt_description").val();
            var strVersion=$("#cdaVersion").val();
            $.ajax({
                url: cda.list._url+"/cda/SaveCdaInfo",
                type:"post",
                datatype:"json",
                data:{id:id,code:code,name:name,schema_path:schema_path,sourceId:sourceId,description:description,strVersion:strVersion},
                success:function(data) {
                    if (data != null) {

                        var _res = eval(data);
                        if (_res.successFlg) {
                            alert($.i18n.prop('message.save.success'));
                            $(".close").click();
                            cda.list.getCdaList();
                        }
                        else {
                            alert(_res.errorMsg);
                        }
                    }
                    else
                    {
                        alert($.i18n.prop('message.save.failure'));
                    }
                }

            })
        },
        deleteCDA:function(ids){
            if(ids==null || ids=="")
            {
                alert($.i18n.prop('message.please.choose.cda.to.be.deleted'));
                return;
            }
            var strVersionCode=$("#cdaVersion").val();
            $.ajax({
                url: cda.list._url+"/cda/delteCdaInfo",
                type:"get",
                datatype:"json",
                data:{ids:ids,strVersionCode:strVersionCode},
                success:function(data) {
                    if (data != null) {

                        var _res = eval(data);
                        if (_res.successFlg) {
                            alert($.i18n.prop('message.delete.success'));
                            $(".close").click();
                            cda.list.getCdaList();
                        }
                        else {
                            alert(_res.errorMsg);
                        }
                    }
                    else
                    {
                        alert($.i18n.prop('message.delete.failure'));
                    }
                }
            })
        },
        clearCdaDetail: function () {

            if($("#sel_stdSource option").size()==0)
            {
                //加载标准来源
                cda.list.initStandardSource();
            }

            $("#hdId").val("");
            $("#txt_Code").val("");
            $("#txt_Name").val("");
            $("#txt_schema_path").val("");
            $("#sel_stdSource option:first").prop("selected", 'selected');
            $("#txt_description").val("");
        },
        //创建关系Grid
        createDictEntryGrid: function(cdaId) {

            var u=cda.list;

            var cdaVersion=$("#cdaVersion").val();
            var strkey=$("#searchNmEntry").val();

            var rowIds;
            if (u.relationGrid == undefined) {
                u.relationGrid = new Grid('#dictEntryGrid', {
                    url: u._url+ "/cda/getRelationByCdaId",
                    datatype: 'json',
                    height: 594,
                    //width:'590px',
                    autowidth: true,
                    multiselect: false,//多选
                    viewrecords: true, // 是否显示总记录数
                    //scroll: true,
                    rowNum: 10,
                    page: 1,
                    rowList: [10, 20, 30],
                    colNames:u.relationColNames,
                    colModel: u.relationColumns,
                    sortable: true,
                    jsonReader: {
                        root: "detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: true,
                        id: "0"
                    },
                    postData: {cdaId: cdaId, strVersionCode: cdaVersion, strkey: strkey}
                }).render();
            }
            else {
                u.relationGrid.instance.jqGrid('setGridParam',
                    {
                        url: cda.list._url+ "/cda/getRelationByCdaId",
                        postData: {cdaId: cdaId, strVersionCode: cdaVersion, strkey: strkey},
                        page: 1,
                        rows: 10
                    }).trigger('reloadGrid');

            }

            $("#dictIdForEntry").val(cdaId);
        },

        /*********************数据集选择器**********************/
        //  relationIds
        dataSetSelectInit:function(){


            var u=cda.list;
            var cdaId = $("#dictIdForEntry").val();
            var cdaVersion=$("#cdaVersion").val();

            $.ajax({
                url:u._url+"/cda/getALLRelationByCdaId",
                type:"get",
                dataType:"json",
                data:{cdaId:cdaId,strVersionCode:cdaVersion},
                success:function(data){

                    var result = eval(data.detailModelList);
                    u.relationIds ="";
                    u.relationSet=[];
                    if(result!=null){
                        var li_item="";
                        for(var i=0;i<result.length;i++){

                            u.relationIds+=result[i].dataset_id+"_"+result[i].dataset_code+",";

                            u.relationSet.push({
                                id:result[i].dataset_id,
                                code:result[i].dataset_code,
                                name:result[i].dataset_name
                            });

                        }
                        u.showSelect(u.relationSet);
                    }

                    cda.list.dataSetSelectGrid();
                }
            });
        },
        dataSetSelectGrid: function() {

            var u=cda.list;
            var cdaVersion=$("#cdaVersion").val();
            var strkey=$("#txt_search").val();

            var rowIds;
            if (u.datasetGrid == undefined) {
                u.datasetGrid = new Grid('#div_dataset', {
                    url: u._url+ "/cda/searchDataSets",
                    datatype: 'json',
                    height: 260,
                    width:560,
                    autowidth: false,
                    multiselect: true,//多选
                    viewrecords: true, // 是否显示总记录数
                    //scroll: true,
                    rowNum: 6,
                    page: 1,
                    rowList: [6, 20, 30],
                    colNames: u.datasetColNames,
                    colModel: u.datasetColumns,
                    sortable: true,
                    jsonReader: {
                        root: "detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: true,
                        id: "0"
                    },
                    postData: { version: cdaVersion, codename: strkey},
                    onSelectRow : function(rowid) {

                        var dataset_id=u.datasetGrid.instance.getRowData(rowid).id;
                        var set_id = u.datasetGrid.instance.getRowData(rowid).id+"_"+u.datasetGrid.instance.getRowData(rowid).code;

                        if(u.relationIds.indexOf(set_id+",")>-1){

                            u.relationIds=u.relationIds.replace(set_id+",","");

                            for(var i=0;i<u.relationSet.length;i++)
                            {
                                var id = u.relationSet[i].id;
                                if(id==dataset_id)
                                {
                                    u.relationSet.splice(i,1);
                                    break;
                                }
                            }
                        }
                        else{
                            u.relationIds+=set_id+",";
                            u.relationSet.push({
                                id:u.datasetGrid.instance.getRowData(rowid).id,
                                code:u.datasetGrid.instance.getRowData(rowid).code,
                                name:u.datasetGrid.instance.getRowData(rowid).name
                            });

                        }

                        u.showSelect(u.relationSet);
                    },
                    gridComplete:function(){

                        $("#div_dataset").children("div:first-child").css({"width":"560px"});

                        // $(".u-grid").css({"width":"560px"});

                        var row_num = u.datasetGrid.instance.jqGrid('getDataIDs');

                        if (row_num!=0)
                        {
                            for(var i=0;i<row_num.length;i++)
                            {
                                var set_id = u.datasetGrid.instance.getRowData(i+1).id+"_"+u.datasetGrid.instance.getRowData(i+1).code;
                                //判断是否已经存在关系，如果是，则这行打勾
                                if(u.relationIds.indexOf(set_id+",")>-1)
                                    u.datasetGrid.instance.setSelection(i+1,false);
                            }
                        }
                    }
                }).render();
            }
            else {
                u.datasetGrid.instance.jqGrid('setGridParam',
                    {
                        url: u._url+ "/cda/searchDataSets",
                        postData: { version: cdaVersion, codename: strkey},
                        page: 1,
                        rows: 10
                    }).trigger('reloadGrid');
            }
        },
        saveRelationship:function(){

            var u=cda.list;

            if(u.relationSet.length==0)
            {
                alert($.i18n.prop('message.first.select.the.data.set'));
                return;
            }
            u.relationIds="";
            for(var i=0;i< u.relationSet.length;i++)
            {
                u.relationIds+= u.relationSet[i].id+",";
            }

            var cdaId = $("#dictIdForEntry").val();
            var strVersionCode =$("#cdaVersion").val();

            $.ajax({
                url:u._url+"/cda/SaveRelationship",
                type:"get",
                dataType:"json",
                data:{strDatasetIds:u.relationIds,strCdaId: cdaId,strVersionCode:strVersionCode},
                success:function(data){

                    if(data.successFlg)
                    {
                        alert($.i18n.prop('message.save.success'));
                        $(".close").click();
                        u.createDictEntryGrid(cdaId);
                    }
                    else
                    {
                        alert(result.errorMsg);
                    }
                }
            });
        },
        deleteRelation: function (ids) {
            var u=cda.list;

            if(ids=="")
            {
                alert($.i18n.prop('message.first.select.the.data.set'));
                return;
            }
            var strVersionCode =$("#cdaVersion").val();
            $.ajax({
                url:u._url+"/cda/DeleteRelationship",
                type:"get",
                dataType:"json",
                data:{ids:ids,strVersionCode:strVersionCode},
                success:function(data){

                    if(data.successFlg)
                    {
                        alert($.i18n.prop('message.delete.success'));
                        u.createDictEntryGrid($("#dictIdForEntry").val());
                    }
                    else
                    {
                        alert(result.errorMsg);
                    }
                }
            });
        },
        showSelect: function (data) {

            var u=cda.list;

            var li_item="";
            if(data!=null)
            {
                for(var i=0;i<data.length;i++)
                {
                    li_item+="<li title='"+data[i].code+"'class='li_item'> <a class='li_close' set_id='"+data[i].id+"_"+data[i].code+"'>X</a>"
                        +data[i].code+"-"+data[i].name+"</li>";
                }
            }

            $("#pane-list-selected").html(li_item);

            $(".li_item").unbind("mouseenter").unbind("mouseleave");

            $(".li_item").hover(function (e) {
                $(this).children("a").show();
            }, function (e) {
                $(this).children("a").hide();
            });

            $(".li_close").click(function () {

                var set_id = $(this).attr("set_id");

                //去打钩
                var row_num = u.datasetGrid.instance.jqGrid('getDataIDs');

                if (row_num!=0)
                {
                    for(var i=0;i<row_num.length;i++)
                    {
                        var datset = u.datasetGrid.instance.getRowData(i+1).id+"_"+u.datasetGrid.instance.getRowData(i+1).code;
                        //判断是否已经存在关系，如果是，则这行打勾
                        if(set_id==datset)
                            u.datasetGrid.instance.setSelection(i+1,true);
                    }
                }

            });
        }
    };

    exports.init = function () {cda.list.init();}
    exports.editCDA= function (id) {cda.list.editCDA(id);}
    exports.setDeleteCdaId= function (ids) {cda.list.setDeleteCdaId(ids);}
    exports.setDeleteRelationId = function (ids) {cda.list.setDeleteRelationId(ids);}

})