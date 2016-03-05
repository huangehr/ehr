/**
 * Created by zqb on 2015/10/8.
 */

define(function (require, exports, module) {
    require('app/dict/systemDict.css');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController');
        //require('i18n');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function()  {
        var dictEntryGrid;
        var dictGrid;
        var searchNm;

        //定义查询条件部控制器，用于查询DICT列表
        var conditionCtr = new ViewController('#conditionArea', {

            elements: {
                '.J_searchBtn': 'searchBtn'
            },
            events: { 'click .J_searchBtn': 'searchItem'},
            handlers: {  //定义回调
                'searchItem': function(){
                    searchNm = $("#searchNm").val();

                    if(dictGrid==undefined){
                        dictGrid = new Grid('#dictGrid', {
                            url: context.path + "/dict/searchSysDicts",
                            datatype: 'json',
                            shrinkToFit: true,
                            scrollOffset: 0,
                            mtype:'POST',
                            widthLock:true,
                            width:400,
                            //width: $(window).width() - 410,
                            height: $(window).height() - 198,
                            autoFit: true,
                            marginHeight:198,
                            rowNum: 10,
                            rowList : [ 10, 20, 30 ],
                            colNames: ['id',$.i18n.prop('grid.codeName'),$.i18n.prop('grid.operation')],
                            colModel: [
                                {name: 'id', index: 'id', hidden:true},
                                {name: 'name', index: 'name', width: 258, align: "center",editable:true},
                                {
                                    name: 'operator ',
                                    index: 'operator',
                                    width: 100,
                                    sortable: false,
                                    align: "center",
                                    formatter: function (value, dictGrid, rows, state) {
                                        return  '<a class="J_modify-btn" data-rowid="' + dictGrid.rowId + '" >'+$.i18n.prop("btn.update")+' / </a>' +
                                            '<a class="J_save-btn" hidden data-rowid="' + dictGrid.rowId + '" >'+$.i18n.prop("btn.save")+' / </a>' +
                                            '<a href="javascript:void(0);" data-toggle="modal" data-target="#deleteDictRowModal" class="J_delete-btn" data-rowid="' + dictGrid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
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
                            onSelectRow : function(ids) {
                                var dictId;
                                if(ids == null) {
                                    ids = 0;
                                    dictId = dictGrid.getRowData(ids).id;
                                    if (dictEntryGrid.instance.jqGrid('getGridParam', 'records') > 0) {
                                        searchDictEntry(dictId);
                                    }
                                }else if(dictGrid.instance.getRowData(ids).id=="New"){
                                    //新增字典未保存的时候
                                    searchDictEntry(ids);
                                }else if(dictGrid.instance.getRowData(ids).name.substr(1, 17)=='input type="text"'){
                                    //新增字典保存，但是校验未通过
                                    dictId = dictGrid.instance.getRowData(ids).id;
                                    searchDictEntry(dictId);
                                }else{
                                    dictId = dictGrid.instance.getRowData(ids).id;
                                    searchDictEntry(dictId);
                                }
                            },
                            postData: {searchNm: searchNm},
                            gridComplete : function() {
                                var ids = dictGrid.instance.jqGrid('getDataIDs');
                                if(ids != 0){
                                    var dictId = dictGrid.instance.getRowData(1).id;
                                    if(dictEntryGrid == undefined){
                                        loadDictEntryGrid(dictId);
                                    }
                                    else{
                                        searchDictEntry(dictId);
                                    }
                                }
                            }
                        }).render();
                    }
                    else{
                        searchSysDicts();
                    }
                }
            }
        });

        //定义左边dictGrid控制器
        var gmenuCtr = new ViewController('#left-gmenu',{

            events: {
                'click .J_add-btn' : 'addDictItem',
                'click .J_modify-btn': 'editDictItem',
                'click .J_save-btn': 'saveDictItem',
                'click .J_delete-btn': 'delDictItem'
            },
            handlers: {
                'addDictItem': function(e){
                    addDict(dictGrid);
                },
                'editDictItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    dictGrid.instance.editRow(rowid);

                    var dictLi = cell.closest(".jqgrow");
                    GridSave(dictLi);
                },
                'delDictItem': function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");

                    //当删除的是第一行的时候，需要重新加载新的第一行的数据。  》》wait for develop
                    dictGrid.instance.saveRow(rowid);

                    $("#deleteDictId").val(rowid);
                },
                'saveDictItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    //saveRow之后，该行的ID值会变成rowid值，所以dictId的取值要放在saveRow之前。
                    var dictId = dictGrid.instance.getRowData(rowid)["id"];

                    dictGrid.instance.saveRow(rowid);
                    var name = dictGrid.instance.getRowData(rowid)["name"].trim();
                    var reference = "";
                    if(name == null || name == "")  {
                        alert($.i18n.prop('message.please.enter.dictionary.name'));
                        dictGrid.instance.editRow(rowid);
                        return;
                    }
                    if(dictId == "New") {
                        $.ajax({
                            type: "POST",
                            url: context.path + "/dict/createDict",
                            data: {name: name,reference:reference},
                            dataType: "json",
                            success: function (data) {
                                searchSysDicts();
                            },
                            error: function (e) {
                            }
                        });
                    } else{
                        $.ajax({
                            type: "POST",
                            url: context.path + "/dict/updateDict",
                            data: {name: name,dictId:dictId},
                            dataType: "json",
                            success: function (data) {
                                searchSysDicts();
                            },
                            error: function (e) {
                            }
                        });
                    }
                }
            }
        });

        //定义右边dictEntryGrid控制器
        var dictEntryCtr = new ViewController('#right-gmenu',{
            events: {
                'click .J_add-btn': 'addDictEntryItem',
                'click .J_modify-btn': 'editDictEntryItem',
                'click .J_save-btn': 'saveDictEntryItem',
                'click .J_delete-btn': 'delDictEntryItem'
            },
            handlers: {
                'addDictEntryItem': function(e){
                    addDict(dictEntryGrid);
                },
                'editDictEntryItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    dictEntryGrid.instance.editRow(rowid);

                    var dictLi = cell.closest(".jqgrow");
                    GridSave(dictLi);
                },
                'saveDictEntryItem': function(e){

                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    dictEntryGrid.instance.saveRow(rowid);

                    var dictId = dictEntryGrid.instance.getRowData(rowid)["id.dictId"];
                    var code = dictEntryGrid.instance.getRowData(rowid)["id.code"];
                    var value = dictEntryGrid.instance.getRowData(rowid)["value"];
                    var sort = dictEntryGrid.instance.getRowData(rowid)["sort"];
                    var catalog = dictEntryGrid.instance.getRowData(rowid)["catalog"];

                    if(code == "" || value == ""){

                        alert($.i18n.prop('message.confirm.details.encoding.contents.of.item'));
                        dictEntryGrid.instance.editRow(rowid);

                    } else{
                        if(dictId == "") {
                            dictId = $("#dictIdForEntry").val();
                            $.ajax({
                                type: "POST",
                                url: context.path + "/dict/createDictEntry",
                                data: {dictId: dictId,code:code,value:value,sort:sort,catalog:catalog},
                                dataType: "json",
                                success: function (data) {

                                    var dictLi = cell.closest(".jqgrow");
                                    GridModify(dictLi);

                                    var dictId = $("#dictIdForEntry").val();
                                    searchDictEntry(dictId);
                                },
                                error: function (e) {
                                }
                            });
                        }
                        else{
                            $.ajax({
                                type: "POST",
                                url: context.path + "/dict/updateDictEntry",
                                data: {dictId: dictId,code:code,value:value,sort:sort,catalog:catalog},
                                dataType: "json",
                                success: function (data) {

                                    var dictLi = cell.closest(".jqgrow");
                                    GridModify(dictLi);
                                },
                                error: function (e) {
                                }
                            });
                        }
                    }
                },
                'delDictEntryItem': function(e){
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");

                    dictEntryGrid.instance.saveRow(rowid);

                    $("#deleteId").val(rowid);
                }
            }
        });

        conditionCtr.searchBtn.trigger('click');

        //删除按钮点击保存事件
        $("#deleteDictBtn").click(function(e) {

            var rowid = $("#deleteDictId").val();
            var dictId = dictGrid.instance.getRowData(rowid)["id"];

            var ids = dictGrid.instance.getDataIDs();
            if(rowid == ids[0]){
                var nextRow = ids[1];
            }

            if(dictId == "")
            {
                dictGrid.instance.jqGrid("delRowData", rowid);
            }
            else{
                $.ajax({
                    type: "POST",
                    url: context.path + "/dict/deleteDict",
                    data: {dictId: dictId},
                    dataType: "json",
                    success: function (data) {
                        dictGrid.instance.jqGrid("delRowData", rowid);
                    },
                    error: function (e) {
                    }
                });

                dictId = dictGrid.instance.getRowData(nextRow).id;

                searchDictEntry(dictId);

            }
        });

        //删除按钮点击保存事件
        $("#deleteBtn").click(function(e) {

            var rowid = $("#deleteId").val();
            var dictId = dictEntryGrid.instance.getRowData(rowid)["id.dictId"];
            var code = dictEntryGrid.instance.getRowData(rowid)["id.code"];

            if(dictId == "")
            {
                dictEntryGrid.instance.jqGrid("delRowData", rowid);
            }
            else{
                $.ajax({
                    type: "POST",
                    url: context.path + "/dict/deleteDictEntry",
                    data: {dictId: dictId,code:code},
                    dataType: "json",
                    success: function (data) {
                        //执行成功删除当前行。
                        dictEntryGrid.instance.jqGrid("delRowData", rowid);
                    },
                    error: function (e) {
                    }
                });
            }
        });

        function loadDictEntryGrid(dictId) {
            dictEntryGrid = new Grid('#dictEntryGrid', {
                url: context.path + "/dict/searchDictEntryListForManage",
                datatype: 'json',
                shrinkToFit: true,
                mtype:'POST',
                scrollOffset: 0,
                width: $(window).width() - 422,
                height: $(window).height() - 198,
                autoFit: true,
                marginHeight:198,
                marginWidth:422,
                rowNum: 10,
                rowList : [ 10, 20, 30 ],
                //viewrecords:true,
                colNames: ['id',$.i18n.prop('grid.code'),$.i18n.prop('grid.value'),$.i18n.prop('grid.catalog'),$.i18n.prop('grid.sort'),$.i18n.prop('grid.operation')],
                colModel: [
                    {name: 'id.dictId', index: 'id.dictId', hidden:true},
                    {name: 'code', index: 'code', align: "center",editable:true},
                    {name: 'value', index: 'value', align: "center",editable:true},
                    {name: 'catalog', index: 'catalog',  align: "center",editable:true},
                    {name: 'sort', index: 'sort', align: "center",editable:true, sortable:true},
                    {
                        name: 'operator ',
                        index: 'operator',
                        sortable: false,
                        align: "center",
                        formatter: function (value, dictEntryGrid, rows, state) {
                            return  '<a class="J_modify-btn" data-rowid="' + dictEntryGrid.rowId + '" >'+$.i18n.prop("btn.update")+' / </a>' +
                                '<a class="J_save-btn" hidden data-rowid="' + dictEntryGrid.rowId + '" >'+$.i18n.prop("btn.save")+' / </a>' +
                                '<a href="javascript:void(0);" data-toggle="modal" data-target="#deleteRowModal" class="J_delete-btn" data-rowid="' + dictEntryGrid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
                        }
                    }
                ],
                sortable:true,
                sortname : 'sort',
                jsonReader: {
                    root: "detailModelList",
                    page: "currPage",
                    total: "totalPage",
                    records: "totalCount",
                    repeatitems: false,
                    id: "0"
                },
                postData: {dictId: dictId}
            }).render();

            $("#dictIdForEntry").val(dictId);
        }

        function searchDictEntry(dictId){
            dictEntryGrid.instance.jqGrid('setGridParam',
                {
                    url : context.path + "/dict/searchDictEntryListForManage",
                    postData: {dictId: dictId},
                    page : 1
                }).trigger('reloadGrid');
            $("#dictIdForEntry").val(dictId);
        }

        function searchSysDicts(){

            searchNm = $("#searchNm").val();
            dictGrid.instance.jqGrid('setGridParam',{
                url :context.path + "/dict/searchSysDicts",
                postData:{searchNm: searchNm},
                page:1
            }).trigger("reloadGrid");
        }

        function addDict(target){
            var ids = target.instance.jqGrid('getDataIDs');
            if(ids.length!=0){
                var targetTableUrl = target.options.url;
                var last_row_id;
                var last_row_dictId;
                if(targetTableUrl=="/ha/dict/searchSysDicts"){
                    //左边表格单行编辑校验
                    last_row_id = target.instance.getRowData()[ids.length-1].id;
                    if(last_row_id=="New" || dictGrid.instance.getRowData()[ids.length-1].name.substr(1, 17)=='input type="text"'){
                        alert($.i18n.prop('message.confirm.dictionary.name'));
                        return;
                    }
                }else if(targetTableUrl=="/ha/dict/searchDictEntryListForManage"){
                    //右边表格单行编辑校验
                    last_row_dictId = target.instance.getRowData()[ids.length-1]["id.dictId"];
                    if(last_row_dictId==""){
                        alert($.i18n.prop('message.confirm.details.encoding.contents.of.item'));
                        return;
                    }
                }


            }
            //获得当前最大行号（数据编号）
            var newrowid = 0;
            var rowid = 0;

            if(ids!=""){
                rowid = Math.max.apply(Math,ids);
                newrowid= rowid+1;
            }
            var dataRow = {
                id:"New",
                name:""
            };
            //将新添加的行插入到最后一列
            target.instance.jqGrid("addRowData", newrowid, dataRow, "last");
            target.instance.jqGrid('editRow', newrowid, true);

            var Dict= $(target.instance.getGridRowById(newrowid));
            GridSave(Dict);
        }

        function GridModify(target){
            $('.J_modify-btn',target).show();
            $('.J_save-btn',target).hide();
        }
        function GridSave(target){
            $('.J_modify-btn',target).hide();
            $('.J_save-btn',target).show();
        }
    });
});