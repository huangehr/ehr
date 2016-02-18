/**
 * Created by zqb on 2015/9/30.
 */

define(function (require, exports, module) {

    require('app/std/standardsource/standardSource.css');

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        ViewController = require('viewController'),
        JValidate = require('jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    $(function () {
        initVersionDDL($('#version-no'));
        var searchNm = $("#search-no").val();
        var version = $("#version-no").val();
        var grid = new Grid('#data-grid', {
            url: context.path + "/standardsource/searchStdSource",
            mtype:'post',
            datatype: 'json',
            shrinkToFit: true,
            scrollOffset: 0,
            width: $(window).width(),
            height: $(window).height() - 194,
            autoFit: true,
            marginHeight:194,
            rowNum: 10,
            //scroll: true, //滚动加载
            multiselect: true,//多选
            viewrecords: true, // 是否显示总记录数
            colNames: ['ID',$.i18n.prop('grid.code'),$.i18n.prop('grid.name'),$.i18n.prop('grid.operation')],
            colModel: [
                {
                    name: 'id',
                    index: 'id',
                    hidden: true,
                    align: "center"
                },
                {
                    name: 'code',
                    index: 'code',
                    align: "center"
                },
                {
                    name: 'name',
                    index: 'name',
                    align: "center"
                },
                {
                    name: 'operator ',
                    index: 'operator',
                    sortable: false,
                    align: "center",
                    formatter: function (value, grid, rows, state) {
                        return '<a data-toggle="modal" data-target="#modify-row-modal" class="J_modify-btn" data-rowid="' + grid.rowId + '" >'+$.i18n.prop("btn.update")+'</a> / <a data-toggle="modal" data-target="#delete-row-modal" class="J_delete-btn" data-rowid="' + grid.rowId + '">'+$.i18n.prop("btn.delete")+'</a>';
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
            postData: {searchNm: searchNm,version: version},
            onSelectRow: function (rowId, status, e) {
                var rowIds = grid.instance.jqGrid('getGridParam', 'selarrrow');

            }
        }).render();

        new ViewController('#data-grid', {
            events: {
                'click .J_modify-btn': 'modifyItem',
                'click .J_delete-btn': 'deleteItem'
            },
            handlers: {
                modifyItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    var version = $("#version-no").val();
                    getModalInfo(id,version);
                },
                deleteItem: function (e) {
                    var cell = $(e.currentTarget);
                    var rowid = cell.attr("data-rowid");
                    var id = grid.instance.getRowData(rowid).id;
                    $("#delete-id").val(id);
                }
            }
        });

        <!--校验器-->
        var validator = new JValidate.Validation($("#update-form"),{immediate:true,onSubmit:false});
        //焦点丢失校验
        $('#update-form').on('blur','.required',function() {
            JValidate.Validation.validateElement(this);
        });
        //模态
        $('#modify-row-modal').on('hidden.bs.modal', function (e) {
            validator.reset();
        });

        $(".J_searchBtn").click(function () {
            reloadGrid();
        });

        $("#add-btn").click(function () {
            clearModal();
        });

        //delete multi row
        $("#delete-rows-btn").click(function () {
            var rowIds = grid.instance.jqGrid('getGridParam', 'selarrrow');
            var id=[];
            for(var i= 0;i<rowIds.length;i++){
                id[i]=grid.instance.getRowData(rowIds[i]).id;
            }
            delStdSource(id.join(","));
        });

        //delete one row
        $("#delete-btn").click(function () {
            var id = $("#delete-id").val();
            delStdSource(id);
        });


        $("#update-btn").click(function () {
            if(!validator.validate() ) {return;}
            var id =  $("#id").val();
            var code =  $("#code").val();
            var name =  $("#name").val();
            var type =  $("#type").val();
            var description =  $("#description").val();
            var version = $("#version-no").val();
            $.ajax({
                type: "POST",
                url: context.path + "/standardsource/updateStdSource",
                data: {id: id,version: version,code:code,name:name,type:type,description:description},
                dataType: 'json',
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                        $(".close",$('#modify-row-modal')).click();
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
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

        function delStdSource(id){
            var version = $("#version-no").val();
            $.ajax({
                type: "POST",
                url: context.path + "/standardsource/delStdSource",
                data: {id: id,version:version},
                dataType: "json",
                success: function (data) {
                    if (data.successFlg) {
                        reloadGrid();
                    } else {
                        alert($.i18n.prop('message.distribution.failure'));
                        return;
                    }
                },
                error: function (data) {
                }
            });
        }

        function clearModal() {
            $('input', $("#update-table")).val("");
            $('select', $("#update-table")).val("");
            $('textarea', $("#update-table")).val("");
        }

        function getModalInfo(id,version) {
            clearModal();
            $.ajax({
                type: "POST",
                url: context.path + "/standardsource/getStdSource",
                data: {id: id,version: version},
                dataType: "json",
                success: function (data) {
                    var model = data.obj.stdSourceModel;
                    $("#id").val(model.id);
                    $("#code").val(model.code);
                    $('#name').val(model.name);
                    $('#description').val(model.description);
                    $('#type').find("option[value='" + model.source_type + "']").prop("selected", true);
                },
                error: function (data) {
                }
            });
        }

        function reloadGrid() {
            var searchNm = $("#search-no").val();
            var version = $("#version-no").val();
            grid.instance.jqGrid('setGridParam', {
                url: context.path + "/standardsource/searchStdSource",
                postData: {searchNm: searchNm,version: version} //发送数据
            }).trigger("reloadGrid");
        }


    })
});