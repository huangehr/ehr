/**
 * Created by AndyCai on 2015/9/18.
 */
define(function (require, exports, module) {

    require('app/std/dict/dict.css');
    require('modules/chosen/chosen.css');

    var $ = require('base').$,
        Context = require('base').context,
        Grid = require('modules/components/grid'),
        chosen = require('modules/chosen/chosen.jquery'),
        ViewController = require('viewController');

    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: Context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function(){

        var dictGrid;
        var dictEntryGrid;
        var searchNm = "";

        initVersionDDL($("#cdaVersion"));

        function initVersionDDL(target){
            $.ajax({
                url:Context.path + "/standardsource/getVersionList",
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

        function initSourceDDL(cdaVersion,objectTarget){

            $("#stdSource").empty();

            $.ajax({
                url:Context.path +"/cdadict/getStdSourceList",
                type:"post",
                async:false,
                dataType:"json",
                data:{strVersionCode:cdaVersion},
                success:function(data){
                    var result = JSON.parse(data.result);
                    var option ='<option value="-1"></option>';
                    for(var i=0;i<result.length;i++){
                        option +='<option value='+result[i].id+'>'+result[i].name+'</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        function initBaseDictDDL(cdaVersion,objectTarget,dictId){
            $.ajax({
                url:Context.path +"/cdadict/getCdaBaseDictList",
                type:"post",
                async:false,
                dataType:"json",
                data:{strVersionCode:cdaVersion,dictId:dictId},
                success:function(data){
                    var result = data.detailModelList;
                    var option = '<option value="-1"></option>';
                    for(var i=0;i<result.length;i++){
                        option +='<option value='+result[i].id+'>'+result[i].name+'</option>';
                    }
                    objectTarget.append(option);
                }
            });
        }

        function createDictGrid(searchNm, cdaVersion){
            if(dictGrid == undefined){
                dictGrid = new Grid('#dictGrid', {
                    url:Context.path +"/cdadict/getCdaDictList",
                    datatype: 'json',
                    mtype:'post',
                    shrinkToFit: true,
                    scrollOffset: 0,
                    widthLock:true,
                    width:400,
                    //width: $(window).width() - 410,
                    height: $(window).height() - 194,
                    autoFit: true,
                    marginHeight:194,
                    rowNum: 10,
                    rowList : [ 15, 20, 30 ],
                    //colNames: ['id','<spring:message code="lbl.code"/>','<spring:message code="lbl.designation"/>','<spring:message code="lbl.operation"/>'],
                    colNames: ['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
                    colModel: [
                        {name: 'id', index: 'id', hidden:true},
                        {name: 'code', index: 'code', width: 150, align: "center"},
                        {name: 'name', index: 'name', width: 150, align: "center"},
                        {
                            name: 'operator ',
                            index: 'operator',
                            width: 100,
                            sortable: false,
                            align: "center",
                            formatter: function (value, dictGrid, rows, state) {
                                var _url=$("#hd_url").val();
                                var _editImageUrl = _url+"/static-dev/images/Modify_btn_pre.png";
                                var deleteImageUrl= _url+"/static-dev/images/Delete_btn_pre.png";
                                return  '<a data-toggle="modal" data-target="#modifyDictRowModal" class="J_modify-btn" data-rowid="' + dictGrid.rowId + '" ><img class="f-mt10" src= "'+_editImageUrl+'"/></a>'+
                                        '<a data-toggle="modal" data-target="#deleteDictRowModal" class="J_delete-btn" data-rowid="' + dictGrid.rowId + '"><img class="f-mt10 f-ml30" src= "'+deleteImageUrl+'"/></a>';
                            }
                        }
                    ],
                    postData: {searchNm: searchNm, strVersionCode: cdaVersion},
                    page: 1,
                    jsonReader: {
                        root: "detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: false,
                        id: "0"
                    },
                    onSelectRow : function(ids) {
                        var dictId;
                        var searchNmEntry = "";
                        var cdaVersion = $("#cdaVersion").val();

                        if (ids == null) {
                            ids = 0;
                            dictId = dictGrid.getRowData(ids).id;
                            if (dictEntryGrid.instance.jqGrid('getGridParam', 'records') > 0) {
                                dictEntryGrid.instance.jqGrid('setGridParam',
                                    {
                                        url : Context.path +"/cdadict/searchDictEntryList",
                                        postData:  {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                                        page:1,
                                        rows:10
                                    }).trigger('reloadGrid');
                                $("#dictIdForEntry").val(dictId);
                            }
                        } else {
                            dictId = dictGrid.instance.getRowData(ids).id;
                            dictEntryGrid.instance.jqGrid('setGridParam', {
                                url : Context.path +"/cdadict/searchDictEntryList",
                                postData:  {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                                page:1,
                                rows:15
                            }).trigger('reloadGrid');
                            $("#dictIdForEntry").val(dictId);
                        }
                    },
                    gridComplete : function() {
                        var ids = dictGrid.instance.jqGrid('getDataIDs');

                        var searchNmEntry = $("#searchNmEntry").val();
                        var cdaVersion = $("#cdaVersion").val();

                        if(ids != 0){

                            var dictId = dictGrid.instance.getRowData(1).id;

                            if(dictEntryGrid == undefined){
                                createDictEntryGrid(dictId, cdaVersion,searchNmEntry);
                            }
                            else{
                                dictEntryGrid.instance.jqGrid('setGridParam',
                                    {
                                        url : Context.path +"/cdadict/searchDictEntryList",
                                        postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                                        page:1,
                                        rows:15
                                    }).trigger('reloadGrid');
                                $("#dictIdForEntry").val(dictId);
                            }
                        }
                    }
                }).render();
            }
            else{
                dictGrid.instance.jqGrid('setGridParam', {
                    url :Context.path +"/cdadict/getCdaDictList",
                    postData:  {searchNm: searchNm, strVersionCode: cdaVersion},
                    page:1,
                    rows:15
                }).trigger('reloadGrid');
            }
        }

        function createDictEntryGrid(dictId, cdaVersion,searchNmEntry){

            var rowIds;

            if(dictEntryGrid == undefined){
                dictEntryGrid = new Grid('#dictEntryGrid', {
                    url: Context.path +"/cdadict/searchDictEntryList",
                    datatype: 'json',
                    shrinkToFit: true,
                    scrollOffset: 0,
                    width: $(window).width() - 422,
                    height: $(window).height() - 194,
                    autoFit: true,
                    marginHeight:194,
                    marginWidth:422,
                    mtype:'post',
                    multiselect: true,//多选
                    viewrecords: true, // 是否显示总记录数
                    //scroll: true,
                    rowNum: 10,
                    page:1,
                    rowList : [ 10, 20, 30 ],
                    //colNames: ['id','<spring:message code="lbl.encoding"/>','<spring:message code="lbl.content"/>','<spring:message code="lbl.operation"/>'],
                    colNames: ['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
                    colModel: [
                        {name: 'id', index: 'id', hidden:true},
                        {name: 'code', index: 'code', align: "center",editable:true},
                        {name: 'value', index: 'value',align: "center",editable:true},
                        {
                            name: 'operator ',
                            index: 'operator',
                            sortable: false,
                            align: "center",
                            formatter: function (value, dictEntryGrid, rows, state) {
                                return  '<a data-toggle="modal" data-target="#modifyDictEntryRowModal" class="J_modify-btn" data-rowid="' + dictEntryGrid.rowId + '" >'+$.i18n.prop("btn.update")+' / </a>' +
                                    '<a data-toggle="modal" data-target="#deleteRowModal" class="J_delete-btn" data-rowid="' + dictEntryGrid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
                            }
                        }
                    ],
                    sortable:true,
                    jsonReader: {
                        root: "detailModelList",
                        page: "currPage",
                        total: "totalPage",
                        records: "totalCount",
                        repeatitems: true,
                        id: "0"
                    },
                    onSelectRow: function () {
                        rowIds = dictEntryGrid.instance.jqGrid('getGridParam', 'selarrrow');

                    },
                    postData:{dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry}
                }).render();

                $("#dictIdForEntry").val(dictId);
            }
            else{
                dictEntryGrid.instance.jqGrid('setGridParam',
                    {
                        url : Context.path +"/cdadict/searchDictEntryList",
                        postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                        page:1,
                        rows:10
                    }).trigger('reloadGrid');
                $("#dictIdForEntry").val(dictId);
            }
        }

        function pageInit(){
            var cdaVersion = $("#cdaVersion").val();

            initSourceDDL(cdaVersion,$("#stdSource"));
            createDictGrid(searchNm,cdaVersion);
            initBaseDictDDL(cdaVersion,$("#baseDict"),"");

            $("#baseDict").chosen();
        }

        function clearDictModal() {
            $("#dictId").val("");
            $("#dictCode").val("");
            $("#dictName").val("");
            $("#baseDict").find("option[value='-1']").attr("selected",true).trigger("liszt:updated");
            //$("#baseDict").val("");
            $("#stdVersion").val("");
            $("#description").val("");
            //$("#stdSource").empty();
        }

        function clearDictEntryModal() {
            $("#dictEntryId").val("");
            $("#dictEntryCode").val("");
            $("#dictEntryName").val("");
            $("#entryDescription").val("");
        }

        pageInit();

        $("#cdaVersion").change(function(){
            pageInit();
        });

        //定义Dict部分控制器，用于DICT的增删改查。
        var dictController = new ViewController('#left-gmenu', {

            elements: {
                '.J_searchBtn' : 'searchDict',
                '.J_add-btn'   : 'addDict',
                '.J_modify-btn': 'updateDict',
                '.J_delete-btn': 'deleteDict'
            },
            events: {
                'click .J_searchBtn': 'searchDictItem',
                'click .J_add-btn': 'addDictItem',
                'click .J_modify-btn': 'editDictItem',
                'click .J_delete-btn': 'deleteDictItem'
            },
            handlers: {
                'searchDictItem': function(){
                    var cdaVersion = $("#cdaVersion").val();
                    var searchNm = $("#searchNm").val();

                    initSourceDDL(cdaVersion,$("#stdSource"));

                    if(dictGrid==undefined){
                        createDictGrid(searchNm,cdaVersion);
                    }
                    else{
                        dictGrid.instance.jqGrid('setGridParam', {
                            url : Context.path +"/cdadict/getCdaDictList",
                            postData:  {searchNm: searchNm, strVersionCode: cdaVersion},
                            page:1,
                            rows:10
                        }).trigger('reloadGrid');
                    }
                },

                'addDictItem': function(){

                    var cdaVersion = $("#cdaVersion").val();

                    //initSourceDDL(cdaVersion,$("#stdSource"));
                    clearDictModal();
                },

                'editDictItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var dictId = dictGrid.instance.getRowData(rowid).id;
                    var cdaVersion = $("#cdaVersion").val();

                    $.ajax({
                        url:Context.path +"/cdadict/getCdaDictInfo",
                        type:"post",
                        datatype:"json",
                        data:{dictId:dictId,strVersionCode:cdaVersion},
                        success:function(data){

                            clearDictModal();
                            debugger
                            var result =  JSON.parse(data);
                            var dictInfo = result.obj;

                            $("#dictId").val(dictInfo.id);
                            $("#dictCode").val(dictInfo.code);
                            $("#dictName").val(dictInfo.name);
                            $("#baseDict").find("option[value='"+dictInfo.baseDictId+"']").attr("selected",true).trigger("liszt:updated");
                            $("#stdSource").find("option[value='"+dictInfo.stdSource+"']").attr("selected",true);
                            $("#stdVersion").val(dictInfo.stdVersion);
                            $("#description").val(dictInfo.description);
                        }
                    })
                },

                'deleteDictItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var dictId = dictGrid.instance.getRowData(rowid).id;
                    var cdaVersion = $("#cdaVersion").val();

                    $("#deleteDictId").val(dictId);
                }
            }
        });

        $("#updateDictBtn").click(function(){

            searchNm = $("#searchNm").val();
            var dictId = $("#dictId").val();
            var cdaVersion = $("#cdaVersion").val();
            var dictCode = $("#dictCode").val();
            var dictName = $("#dictName").val();
            var baseDict = $("#baseDict").val();
            var stdSource = $("#stdSource").val();
            var stdVersion = $("#stdVersion").val();
            var description = $("#description").val();

            $.ajax({
                url:Context.path +"/cdadict/saveDict",
                type:"post",
                dataType:"json",
                data:{cdaVersion:cdaVersion,dictId:dictId,code:dictCode,name:dictName,baseDict:baseDict,stdSource:stdSource,stdVersion:stdVersion,description:description},
                success:function(data){

                    $(".close").click();
                    dictGrid.instance.jqGrid('setGridParam', {
                        url : Context.path +"/cdadict/getCdaDictList",
                        postData:  {searchNm: searchNm, strVersionCode: cdaVersion},
                        page:1,
                        rows:10
                    }).trigger('reloadGrid');
                }
            })
        });

        $("#deleteDictBtn").click(function() {
            var cdaVersion = $("#cdaVersion").val();
            var searchNm = $("#searchNm").val();
            var dictId = $("#deleteDictId").val();
            $.ajax({
                type: "POST",
                url: Context.path +"/cdadict/deleteDict",
                data: {dictId:dictId,cdaVersion:cdaVersion},
                dataType: "json",
                success: function (data) {
                    dictGrid.instance.jqGrid('setGridParam', {
                        url : Context.path +"/cdadict/getCdaDictList",
                        postData:  {searchNm: searchNm, strVersionCode: cdaVersion},
                        page:1,
                        rows:10
                    }).trigger('reloadGrid');
                }
            });
        });

        //定义右边dictGrid控制器，用于字典明细的增删改查。
        var dictEntryController = new ViewController('#right-gmenu',{
            events: {
                'click .J_searchBtn' : 'searchDictEntryItem',
                'click .J_add-btn' : 'addDictEntryItem',
                'click .J_modify-btn': 'editDictEntryItem',
                'click .J_delete-btn': 'delDictEntryItem'
            },
            handlers: {
                'searchDictEntryItem': function(){
                    var cdaVersion = $("#cdaVersion").val();
                    var searchNmEntry = $("#searchNmEntry").val();
                    var dictId = $("#dictIdForEntry").val();

                    if(dictEntryGrid==undefined){
                        createDictEntryGrid(dictId,cdaVersion,searchNmEntry);
                    }
                    else{
                        dictEntryGrid.instance.jqGrid('setGridParam', {
                            mtype: 'POST',
                            url : Context.path +"/cdadict/searchDictEntryList",
                            postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                            page:1
                        }).trigger('reloadGrid');
                    }
                },

                'addDictEntryItem': function(){
                   clearDictEntryModal();


                },

                'editDictEntryItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var dictId = $("#dictIdForEntry").val();
                    var cdaVersion = $("#cdaVersion").val();

                    clearDictEntryModal();

                    var id = dictEntryGrid.instance.getRowData(rowid).id;

                    $.ajax({
                        type : "POST",
                        url : Context.path +"/cdadict/getDictEntry",
                        data : {id:id,dictId:dictId,strVersionCode:cdaVersion},
                        dataType : "json",
                        async:false,
                        success :function(data){

                            var dictEntry = data.obj;
                            $("#dictEntryId").val(dictEntry.id);
                            $("#dictEntryCode").val(dictEntry.code);
                            $("#dictEntryName").val(dictEntry.value);
                            var code= $("#dictEntryCode").val();
                            var value= $("#dictEntryName").val();
                            if(code==null || code==""){
                                alert("请输入代码");
                                return;
                            }
                            if(value==null || value==""){
                                alert("请输入值");
                                return;
                            }
                            $("#entryDescription").val(dictEntry.desc);
                        }
                    });
                },

                'delDictEntryItem': function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");

                    $("#deleteDictEntryId").val(rowid);
                }
            }
        });

        $("#updateDictEntryBtn").click(function(){

            var cdaVersion = $("#cdaVersion").val();
            var searchNmEntry = $("#searchNmEntry").val();
            var dictId = $("#dictIdForEntry").val();
            var id = $("#dictEntryId").val();
            var code = $("#dictEntryCode").val();
            var value = $("#dictEntryName").val();
            var desc = $("#entryDescription").val();
            if(code==null || code==""){
                alert("请输入代码");
                return;
            }
            if(value==null || value==""){
                alert("请输入值");
                return;
            }
            $.ajax({
                url:Context.path +"/cdadict/saveDictEntry",
                type:"post",
                dataType:"json",
                data:{cdaVersion:cdaVersion,dictId:dictId,id:id,code:code,value:value,desc:desc},
                success:function(data){

                    $(".close").click();
                    dictEntryGrid.instance.jqGrid('setGridParam', {
                        url : Context.path +"/cdadict/searchDictEntryList",
                        postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                        page:1
                    }).trigger('reloadGrid');
                }
            })
        });

        $("#deleteDictEntryBtn").click(function() {

            var cdaVersion = $("#cdaVersion").val();
            var searchNmEntry = $("#searchNmEntry").val();
            var dictId = $("#dictIdForEntry").val();

            var rowid = $("#deleteDictEntryId").val();
            var id = dictEntryGrid.instance.getRowData(rowid).id;

            $.ajax({
                type: "POST",
                url: Context.path +"/cdadict/deleteDictEntry",
                data: {dictId:dictId,cdaVersion:cdaVersion,entryId:id},
                dataType: "json",
                success: function (data) {
                    dictEntryGrid.instance.jqGrid('setGridParam', {
                        url : Context.path +"/cdadict/searchDictEntryList",
                        postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                        page:1
                    }).trigger('reloadGrid');
                }
            });
        });

        $("#delDictEntryListBtn").click(function () {
            var rowIds = dictEntryGrid.instance.jqGrid('getGridParam', 'selarrrow');
            var id=[];
            for(var i= 0;i<rowIds.length;i++){
                id[i]=dictEntryGrid.instance.getRowData(rowIds[i]).id;
            }
            delDictEntryList(id.join(","));
        });

        function delDictEntryList(id){
            var cdaVersion = $("#cdaVersion").val();
            var searchNmEntry = $("#searchNmEntry").val();
            var dictId = $("#dictIdForEntry").val();

            $.ajax({
                type: "POST",
                url: Context.path +"/cdadict/deleteDictEntryList",
                data: {id: id,cdaVersion:cdaVersion},
                dataType: "json",
                success: function (data) {
                    dictEntryGrid.instance.jqGrid('setGridParam', {
                        url : Context.path +"/cdadict/searchDictEntryList",
                        postData: {dictId: dictId,strVersionCode:cdaVersion,searchNmEntry:searchNmEntry},
                        page:1
                    }).trigger('reloadGrid');
                }
            });
        }

        $("#baseDict").chosen({
            no_results_text : $.i18n.prop("message.this.option.was.not.found"),
            width:"70%"
        });
    });
})