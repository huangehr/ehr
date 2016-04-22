/**
 * Created by AndyCai on 2015/12/14.
 */

var cdaType = {};

cdaType.list = {
    _url: $("#hd_url").val(),
    top: null,
    grid: null,
    columns: [],
    TypeSearch: null,
    init: function () {
        this.top = $.Util.getTopWindowDOM();
        //CDA 列名
        this.columns = [

            {display: '名称', name: 'name', align: 'left', id: 'tree_id'},
            {display: '代码', name: 'code', align: 'left'},
            {display: '说明', name: 'description', align: 'left'},
            {
                display: '操作', isSort: false, width: 200, align: 'center', render: function (rowdata, rowindex, value) {

                //var html = "<div class='grid_edit' name='edit_click' style='margin-left: 60px;cursor:pointer;' onclick='cdaType.list.add(\"" + rowdata.id + "\", \"modify\")'></div> " +
                //    "<div class='grid_delete' name='delete_click' style='margin-left: 110px;cursor:pointer;' onclick='cdaType.list.deleted(\"" + rowdata.id + "\")'></div>";
                var html = "<a class='grid_edit' title='编辑' name='edit_click' style='' onclick='cdaType.list.add(\"" + rowdata.id + "\", \"modify\")'></a> " +
                    "<a class='grid_delete' title='删除' name='delete_click' style='' onclick='cdaType.list.deleted(\"" + rowdata.id + "\")'></a>";
                return html;
            }
            }
        ];

        this.TypeSearch = $("#inp_search").ligerTextBox({
            width: 240, isSearch: true, search: function () {
                cdaType.list.getTypeList();
            }
        });

        this.getTypeList();
        this.event();
    },

    getTypeList: function () {
        var u = cdaType.list;
        var codeName = $('#inp_search').val();
        $.ajax({
            url: u._url + "/cdatype/getTreeGridData",
            type: "get",
            dataType: "json",
            data:{codeName:codeName},
            success: function (data) {
                var envelop = eval(data);
                var result = envelop.detailModelList;
                if (result != null) {
                    cdaType.list.setUserList(result);
                }
                //else {
                //    $.Notice.error("数据获取失败！");
                //}
            }
        })
    },
    setUserList: function (data) {
        var u = cdaType.list;
        var dataJson = [];
        //根据下拉框加载相应的数据
        dataJson = data;
        var gridData = {
            Total: dataJson != null ? dataJson.length : null,
            Rows: dataJson
        };
        u.rows = dataJson;
        // window.grid=u.grid=null;
        if (u.grid == null) {
            //$.LigerGridEx.config(
            u.grid = $("#div_cda_type_grid").ligerGrid({
                record: 'totalCount',
                root: 'detailModelList',
                pageSize:15,
                pagesizeParmName: 'rows',
                heightDiff: -10,
                headerRowHeight: 40,
                rowHeight: 40,
                editorTopDiff: 41,
                allowAdjustColWidth: true,

                usePager: false,
                scrollToPage: false,
                columns: u.columns,

                data: gridData,
                height: "100%",
                rownumbers: false,
                checkbox: false,
                root: 'Rows',
                tree: {columnId: 'tree_id',height:'100%'},
                onError: function (a, b) {
                },
                onGroupExtend: function () {
                    alert(1);
                }
            });

        }
        else {
            u.grid.reload(gridData);
        }
        u.grid.collapseAll();

        window.grid = u.grid;

    },
    showDialog: function (_tital, _url, _height, _width, callback) {

        cdaType.list.top.dialog_cdatype_detail = $.ligerDialog.open({
            title: _tital,
            url: _url,
            height: _height,
            width: _width,
            onClosed: callback
        });
    },
    add: function (id, type) {

        var _tital = type=="modify"?"修改CDA分类":"新增CDA分类";
        var _url = cdaType.list._url + "/cdatype/typeupdate?id=" + id;
        var callback = function () {
            cdaType.list.getTypeList();
        };
        cdaType.list.showDialog(_tital, _url, 400, 500, callback);
    },
    deleted: function (ids) {
        if (ids == null || ids == "") {
            $.Notice.error("请先选择需要删除的数据!");
            return;
        }
        //判断该cda类别或其子类别是否有关联的cda文档，---前提 没有批量删除功能
        $.ajax({
            url: cdaType.list._url + "/cdatype/isExitRelativeCDA",
            type: "get",
            dataType: "json",
            data: {ids: ids},
            success: function (data) {
                var _res = eval(data);
                if (_res.successFlg) {
                    $.Notice.error("该cda类别不能删除！当前类别或其子类别存在关联的cda文档！");
                    return;
                }
                //先判断是否存在子集
                $.ajax({
                    url: cdaType.list._url + "/cdatype/getCDATypeListByParentId",
                    type: "get",
                    dataType: "json",
                    data: {ids: ids},
                    success: function (data) {
                        var _res = data;
                        if (_res != null && _res.length > 0) {
                            var _text = "当前类别存在子类别,删除将会同时删除子类别！\n请确认是否删除？";
                            for (var i = 0; i < _res.length; i++) {
                                ids += "," + _res[i].id;
                            }
                            // ids = ids.substr(1);
                            cdaType.list.doDeleted(ids, _text);
                        }
                        else {
                            var _text = "确定删除当前cda类别？";
                            cdaType.list.doDeleted(ids, _text);
                        }
                    }
                });
            }
        })
    },
    doDeleted: function (ids, _text) {
        $.Notice.confirm(_text, function (confirm) {
            if (confirm) {
                $.ajax({
                    url: cdaType.list._url + "/cdatype/delteCdaTypeInfo",
                    type: "get",
                    dataType: "json",
                    data: {ids: ids},
                    success: function (data) {
                        if (data != null) {

                            var _res = eval(data);
                            if (_res.successFlg) {
                                $.Notice.success("删除成功!");
                                cdaType.list.getTypeList();
                            }
                            else {
                                $.Notice.error(_res.errorMsg);
                            }
                        }
                        else {
                            $.Notice.error('删除失败!');
                        }
                    }
                })
            }
        });
    },
    event: function () {
        $(".li_seach").on('keyup', 'input', function (e) {
            var name = $(this).attr('name');
            switch (name) {
                case'inp_search':
                    if (e.keyCode == 13) {
                        cdaType.list.getTypeList();
                    }
                    break;
            }

        }).on('click', 'a', function () {
            var id = $(this).attr("id");
            switch (id) {
                case 'btn_Delete_relation':
                    var rows = cdaType.list.grid.getSelecteds();
                    if (rows.length == 0) {
                        $.Notice.error("请选择要删除的内容！");
                        return;
                    }
                    else {
                        var ids = "";
                        for (var i = 0; i < rows.length; i++) {

                            ids += "," + rows[i].id;
                        }
                        ids = ids.substr(1);
                        cdaType.list.deleted(ids);
                    }
                    break;
                case 'btn_Update_relation':
                    cdaType.list.add("");

                    break;
            }
        })
    }
};

cdaType.attr = {
    type_form: $("#div_cdatype_info_form"),
    validator: null,
    parent_select: null,
    init: function () {

        var typeId = $.Util.getUrlQueryString('id');
        $("#hdId").val(typeId);
        this.getCdaTypeInfo();
        this.event();

        this.validator =  new $.jValidation.Validation(this.type_form, {immediate: true, onSubmit: false,
            onElementValidateForAjax:function(elm){

            }
        });
    },
    getParentType: function (initValue, initText) {
        cdaType.attr.parent_select = $("#ipt_select").ligerComboBox({
            url: cdaType.list._url + "/cdatype/getCdaTypeExcludeSelfAndChildren?strId=" + $("#hdId").val(),
            valueField: 'id',
            textField: 'name',
            dataParmName: 'detailModelList',
            selectBoxWidth: 240,
            keySupport: true,
            width: 240,
            initValue: initValue,
            initText: initText,
            onSuccess: function () {

            },
            onAfterSetData: function () {

            }
        });
        cdaType.attr.parent_select.setValue(initValue);
        cdaType.attr.parent_select.setText(initText);
    },
    getCdaTypeInfo: function () {

        var u = cdaType.list;
        var id = $("#hdId").val();

        if (id == "") {
            cdaType.attr.getParentType("", "");
            return;
        }

        $.ajax({
            url: u._url + "/cdatype/getCdaTypeById",
            type: "get",
            dataType: "json",
            data: {strIds: id},
            success: function (data) {

                var envelop = eval(data);
                var info = envelop.obj;
                if (info != null) {
                    cdaType.attr.type_form.attrScan();
                    cdaType.attr.type_form.Fields.fillValues(info);

                    var initValue = info.parentId;
                    var initText = info.parentName;

                    cdaType.attr.getParentType(initValue, initText);
                }
                else {
                    $.Notice.error(result.errorMsg);
                }
            }
        })
    },
    save: function () {
        if(!this.validator.validate()){
            return;
        }
        var id = $("#hdId").val();
        cdaType.attr.type_form.attrScan();
        var dataJson = eval("[" + cdaType.attr.type_form.Fields.toJsonString() + "]");
        dataJson[0]["id"] = id;

        var user_id = $("#hd_user").val();
        dataJson[0]["createUser"] = user_id;

        var parent_id = cdaType.attr.parent_select.getValue();
        dataJson[0]["parentId"] = parent_id;

        var _url = cdaType.list._url + "/cdatype/SaveCdaType";

        $.ajax({
            url: _url,
            type: "POST",
            dataType: "json",
            data: {dataJson:JSON.stringify(dataJson[0])},
            success: function (data) {
                if (data != null) {

                    var _res = eval(data);
                    if (_res.successFlg) {
                        //alert($.i18n.prop('message.save.success'));
                        $.ligerDialog.alert("保存成功", "提示", "success", function () {
                            parent.cdaType.list.top.dialog_cdatype_detail.close();
                        }, null);
                    }
                    else {
                        $.Notice.error(_res.errorMsg);
                    }
                }
                else {
                    $.Notice.error("保存失败！")
                }
            }
        })
    },
    event: function () {
        $("#btn_save").click(function () {
            cdaType.attr.save();
        });
        $("#btn_close").click(function () {
            parent.cdaType.list.top.dialog_cdatype_detail.close();
        });
    }
}


