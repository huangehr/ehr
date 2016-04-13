/**
 * Created by AndyCai on 2015/11/25.
 */
var set = {};
set.list = {
    _url: $("#hd_url").val(),
    top: null,
    grid: null,
    elementGrid: null,
    columns: [],
    elementColumns: [],
    setSearch: null,
    elementSearch: null,
    selectObj: null,
    init: function () {
        this.top = $.Util.getTopWindowDOM();
        this.columns = [
            {display: '数据集编码', name: 'code', align: 'left', width: '33%'},
            {display: '数据集名称', name: 'name', align: 'left', width: '34%'},
            {
                display: '操作', isSort: false, width: '33%', render: function (rowdata, rowindex, value) {

                var html = "<div class='grid_edit' style='' title='' onclick='set.list.updateSet(\"" + rowdata.id + "\")'></div> " +
                    "<div class='grid_delete' style=''title='' onclick='set.list.deleteSet(\"" + rowdata.id + "\")'></div>";
                //var html = "<div class='grid_edit' style='margin-left: 30px;cursor:pointer;' title='修改' onclick='set.list.updateSet(\"" + rowdata.id + "\")'></div> " +
                //    "<div class='grid_delete' style='margin-left: 70px;cursor:pointer;'title='删除' onclick='set.list.deleteSet(\"" + rowdata.id + "\")'></div>";
                return html;
            }
            }
        ];
        this.elementColumns = [
            {display: '内部标识符', name: 'innerCode', align: 'left', width: '15%'},
            {display: '数据元编码', name: 'code', align: 'left', width: '15%'},
            {display: '数据元名称', name: 'name', align: 'left', width: '25%'},
            {display: '列类型', name: 'columnType', align: 'left', width: '10%'},
            {display: '检验字典', name: 'dictName', align: 'left', width: '20%'},
            {
                display: '操作', width: '15%', isSort: false, render: function (rowdata, rowindex, value) {
                //var html = "<div class='grid_edit' style='' title='' onclick='set.list.updateElement(\"" + rowdata.id + "\")'></div> " +
                //    "<div class='grid_delete' style='' title='' onclick='set.list.deleteElement(\"" + rowdata.id + "\")'></div>";
                var html = "<a class='grid_edit' href='#' onclick='set.list.updateElement(\"" + rowdata.id + "\")'></a>" +
                    "<a class='grid_delete' href='#' onclick='set.list.deleteElement(\"" + rowdata.id + "\")'></a>";
                return html;
            }
            }
        ];

        this.setSearch = $("#searchNm").ligerTextBox({
            width: 240, isSearch: true, search: function () {
                set.list.selectObj = null;
                var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
                set.list.grid.options.newPage = 1;
                set.list.getSetList(versionCode);
            }
        });
        $("#searchNm").keyup(function (e) {
            if (e.keyCode == 13) {
                set.list.selectObj = null;
                var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
                set.list.grid.options.newPage = 1;
                set.list.getSetList(versionCode);
            }
        });

        this.elementSearch = $("#searchNmEntry").ligerTextBox({
            width: 240, isSearch: true, search: function () {
                var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
                var setid = $("#hdId").val();
                set.list.elementGrid.options.newPage = 1;
                set.list.getElementList(versionCode, setid);
            }
        });
        $("#searchNmEntry").keyup(function (e) {
            if (e.keyCode == 13) {
                var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
                var setid = $("#hdId").val();
                set.list.elementGrid.options.newPage = 1;
                set.list.getElementList(versionCode, setid);
            }
        });

        $("#div_left .l-text").css({
            "margin-left": 50, "margin-top": -20
        });

        $("#div_right .l-text").css({
            "margin-left": 100, "margin-top": -20
        });
        this.setCss();
        this.event();
        this.getVersionList();
        this.bindEvents();
    },
    bindEvents: function () {
        $(window).bind('resize', function () {
            var contentW = $('#grid_content').width();
            var leftW = $('#div_left').width();
            $('#div_right').width(contentW - leftW - 20);
        });
    },
    setCss: function () {
        $("#div_right").css({
            "width": $("#div_wrapper").width() - $("#div_left").width() - 10,
            "margin-left": 10
        });

        $("#div_left .l-text").css({
            "margin-left": 70
        });
    },
    getVersionList: function () {
        var u = set.list;
        $.ajax({
           // url: u._url + "/cdadict/getCdaVersionList",
            //调通测试暂时使用
            url: u._url + "/cdaVersion/getVersionList",
            type: "post",
            dataType: "json",
            data: {page: "1", rows: "100"},
            success: function (data) {
                var envelop = eval(data);
                var result = envelop.detailModelList;
                //var result = eval(data.result);
                var option = [];
                for (var i = 0; i < result.length; i++) {
                    //var version = result[i].version;
                    //var versionArr = version.split(",");
                    option.push({
                        //text: versionArr[1],
                        //id: versionArr[0]
                        text: result[i].versionName,
                        id: result[i].version
                    });
                }
                var select = $("#cdaVersion").ligerComboBox({
                    data: option,
                    onSelected: function (value, text) {
                        set.list.getSetList(value);
                    }
                });
                var manager = $("#cdaVersion").ligerGetComboBoxManager();
                manager.selectItemByIndex(0);
            }
        });
    },
    //获取列表
    getSetList: function (versionCode) {
        var u = set.list;
        $.ajax({
            type: "get",
            url: u._url + "/std/dataset/searchDataSets",
            data: {codename: u.setSearch.getValue(), version: versionCode, page: 1, rows: 100},// 你的formid
            async: true,
            dataType: "json",
            error: function (request) {
                // alert(request);
            },
            success: function (data) {
                //清空数组
                u.enableData = [];
                var resultData = eval(data);
                //if (data != null) {
                if (resultData.successFlg) {

                        var _result = data.detailModelList;
                    for (var i = 0; i < _result.length; i++) {
                        u.enableData.push(_result[i]);
                    }
                    if (u.elementGrid != null && u.enableData.length == 0) {
                        u.getElementList(versionCode, "");
                    }
                }
                u.setUserList();

            },
            complete: function () {
            }
        });
    },
    //获取数据
    setUserList: function () {
        var u = set.list;
        var dataJson = [];
        //根据下拉框加载相应的数据
        dataJson = u.enableData;
        var gridData = {
            Total: dataJson != null ? dataJson.length : null,
            Rows: dataJson
        };
        u.rows = dataJson;
        if (u.grid == null) {
            u.grid = $("#div_set_grid").ligerGrid($.LigerGridEx.config({
                //usePager: false,
                //scrollToPage: true,
                columns: u.columns,
                //pageSizeOptions: [10, 15, 20, 30, 40, 50],
                //pageSize: 20,
                data: [],
                //height: "100%",
                //rownumbers: false,
                //enabledEdit: true,
                validate: true,
                unSetValidateAttr: false,
                allowHideColumn: false,
                root: 'Rows',
                onDblClickRow: function (data, rowindex, rowobj) {
                },
                onSelectRow: function (rowdata, rowid, rowobj) {

                    set.list.selectObj = rowdata;
                    $("#hdId").val(rowdata.id);
                    versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
                    u.getElementList(versionCode, rowdata.id);
                },
                onAfterShowData: function (currentData) {
                    u.grid.select(0);
                    //if (set.list.selectObj != null) {
                    //    u.grid.select(set.list.selectObj);
                    //}
                    //else {
                    //    u.grid.select(0);
                    //}
                },
                onError: function (a, b) {
                }
            }));
            u.grid.options.newPage = 1; //将页数设置为1
            //u.grid.options.pageSize = 20; //每页默认显示的条数
            u.grid.loadData(gridData); //刷新数据
        }
        else {
            u.grid.options.newPage = 1; //将页数设置为1
            u.grid.loadData(gridData); //刷新数据
        }
        window.grid = u.grid;

    },

    getElementList: function (versionCode, setid) {
        var u = set.list;

        var strkey = u.elementSearch.getValue();
        // {cdaId: cdaid, strVersionCode: versionCode, strkey: strkey},
        if (u.elementGrid == null) {
            u.elementGrid = $("#div_element_grid").ligerGrid($.LigerGridEx.config({
                url: u._url + "/std/dataset/searchMetaData",
                // 传给服务器的ajax 参数
                parms: {id: setid, version: versionCode, metaDataCode: strkey},

                columns: u.elementColumns,
                //pageSizeOptions: [10, 15, 20, 30, 40, 50],
                //pageSize: 20,
                //rownumbers: true,
                selectRowButtonOnly: false,
                unSetValidateAttr: false,
                allowHideColumn: false,
                checkbox: true,
                usePager: true
            }));
            // 自适应宽度
            u.elementGrid.adjustToWidth();
        }
        else {
            u.elementGrid.set({
                url: u._url + "/std/dataset/searchMetaData",
                // 传给服务器的ajax 参数
                parms: {id: setid, version: versionCode, metaDataCode: strkey},
                newPage:1
            });
            u.elementGrid.reload();
        }
    },
    showDialog: function (_tital, _url, _height, _width, callback) {
        if (set.list.top == null) {
            set.list.top = $.Util.getTopWindowDOM();
        }
        set.list.top.dialog_set_detail = $.ligerDialog.open({
            title: _tital,
            url: _url,
            height: _height,
            width: _width,
            onClosed: callback
        });
    },
    //添加数据集 窗口
    addSet: function () {
        var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        var _tital = "新增数据集";
        var _url = set.list._url + "/std/dataset/setupdate?id=&versioncode=" + versionCode;
        var callback = function () {
            set.list.getSetList(versionCode);
        };
        set.list.showDialog(_tital, _url, 360, 430, callback);
    },
    //修改数据集 窗口
    updateSet: function (id) {
        var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        var _tital = "修改数据集";
        var _url = set.list._url + "/std/dataset/setupdate?id=" + id + "&versioncode=" + versionCode;
        var callback = function () {
            set.list.getSetList(versionCode);
        };
        set.list.showDialog(_tital, _url, 360, 400, callback);
    },
    //删除数据集 窗口
    deleteSet: function (ids) {
        if (ids == null || ids == "") {
            $.Notice.error("请先选择需要删除的数据集!");
            return;
        }
        var strVersionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        $.Notice.confirm('删除数据集将连同一起删除关联的数据元,是否确定删除该数据集?', function (confirm) {
            if (confirm) {
                $.ajax({
                    url: set.list._url + "/std/dataset/deleteDataSet",
                    type: "get",
                    dataType: "json",
                    data: {dataSetId: ids, version: strVersionCode},
                    success: function (data) {
                        if (data != null) {

                            var _res = eval(data);
                            if (_res.successFlg) {
                                $.Notice.success("删除成功!");

                                set.list.getSetList(strVersionCode);
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
    addElement: function () {
        var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        var setid = $("#hdId").val();
        var _tital = "新增数据元";

        var _url = set.list._url + "/std/dataset/elementupdate?id=&versioncode=" + versionCode + "&setid=" + setid;
        var callback = function () {
            set.list.getElementList(versionCode, setid);
        };
        set.list.showDialog(_tital, _url, 560, 730, callback);
    },
    updateElement: function (id) {
        var versionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        var setid = $("#hdId").val();
        var _tital = "修改数据元";
        var _url = set.list._url + "/std/dataset/elementupdate?id=" + id + "&versioncode=" + versionCode + "&setid=" + setid;
        var callback = function () {
            set.list.getElementList(versionCode, setid);
        };
        set.list.showDialog(_tital, _url, 560, 730, callback);
    },
    deleteElement: function (ids) {
        if (ids == null || ids == "") {
            $.Notice.error("请先选择需要删除的数据集!");
            return;
        }
        var strVersionCode = $("#cdaVersion").ligerGetComboBoxManager().getValue();
        $.Notice.confirm('是否确定删除该数据元?', function (confirm) {
            if (confirm) {
                $.ajax({
                    url: set.list._url + "/std/dataset/deleteMetaData",
                    type: "get",
                    dataType: "json",
                    data: {ids: ids, version: strVersionCode},
                    success: function (data) {
                        if (data != null) {

                            var _res = eval(data);
                            if (_res.successFlg) {
                                $.Notice.success("删除成功!");
                                var setid = $("#hdId").val();
                                set.list.getElementList(strVersionCode, setid);
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
        $("#btn_create").click(function () {
            set.list.addSet();
        });
        $("#btn_add_element").click(function () {
            set.list.addElement();
        });
        $("#btn_Delete_relation").click(function () {

            var rows = set.list.elementGrid.getSelecteds();
            if (rows.length == 0) {
                $.Notice.tip("请选择要删除的内容！");
                return;
            }
            else {
                var ids = "";
                for (var i = 0; i < rows.length; i++) {

                    ids += "," + rows[i].id;
                }
                ids = ids.substr(1);
                set.list.deleteElement(ids);
            }
        });
    }
};

set.attr = {
    set_form: $("#div_set_info_form"),
    init: function () {
        var versionCode = $.Util.getUrlQueryString('versioncode');
        $("#hdversion").val(versionCode);
        var setId = $.Util.getUrlQueryString('id');
        $("#hdId").val(setId);
        this.getStandardSource();
        this.event();
    },
    //加载标准来源下拉框
    getStandardSource: function () {
        var u = set.list;
        if (u._url == "" || u._url == null) {
            u._url = $("#hd_url").val();
        }
        var cdaVersion = $("#hdversion").val();

        $.ajax({
            //url: u._url + "/cdadict/getStdSourceList",
            url: u._url + "/standardsource/getStdSourceList",
            type: "post",
            dataType: "json",
           // data: {strVersionCode: cdaVersion},
            //data: {version: cdaVersion},
            success: function (data) {
                //var result = eval(data.result);
                var envelop = eval(data);
                var result = envelop.detailModelList;
                var option = [];
                if (result != null) {
                    for (var i = 0; i < result.length; i++) {
                        option.push({
                            text: result[i].name,
                            id: result[i].id
                        });
                    }
                }
                else {
                    option.push({
                        text: "请先维护标准来源",
                        id: ""
                    });
                }

                var select = $("#ipt_select").ligerComboBox({
                    data: option
                });
                if (option.length > 0) {
                    select.setValue(option[0].id);
                }

                set.attr.getInfo();
            }
        });
    },
    getInfo: function () {
        var u = set.list;

        var versionCode = $("#hdversion").val();
        var id = $("#hdId").val();

        if (id == "") {
            return;
        }

        $.ajax({
            url: u._url + "/std/dataset/getDataSet",
            type: "get",
            dataType: "json",
            data: {dataSetId: id, versionCode: versionCode},
            success: function (data) {
                //  cda.list.clearCdaDetail();

                var result = eval(data);
                var info = result.obj;
                if (info != null) {
                    set.attr.set_form.attrScan();
                    info.refStandard = info.reference;
                    set.attr.set_form.Fields.fillValues(info);
                    // cda.attr.cda_form.Fields.source_id.setValue("0dae0006561b759649f63220dcacfd00");
                }
                else {
                    $.Notice.error(result.errorMsg);
                }
            }
        })
    },
    save: function () {

        var versionCode = $("#hdversion").val();
        var id = $("#hdId").val();
        if (id == "")
            id = "0";
        set.attr.set_form.attrScan();
        var dataJson = eval("[" + set.attr.set_form.Fields.toJsonString() + "]");
        dataJson[0]["id"] = id;
        dataJson[0]["versionCode"] = versionCode;

        var _url = set.list._url + "/std/dataset/saveDataSet";

        $.ajax({
            url: _url,
            type: "POST",
            dataType: "json",
            data: dataJson[0],
            success: function (data) {
                if (data != null) {

                    var _res = eval(data);
                    if (_res.successFlg) {
                        //alert($.i18n.prop('message.save.success'));
                        $.ligerDialog.alert("保存成功", "提示", "success", function () {
                            parent.set.list.top.dialog_set_detail.close();
                        }, null);
                    }
                    else {
                        $.Notice.error(_res.errorMsg);
                    }
                }
                else {
                    // alert($.i18n.prop('message.save.failure'));
                }
            }
        })
    },
    event: function () {
        // 表单校验工具类
        var jValidation = $.jValidation;
        var validator = new jValidation.Validation(this.set_form, {immediate: true, onSubmit: false});
        $("#btn_save").click(function () {
            if (validator.validate()) {
                set.attr.save();
            }
        });
        $("#btn_close").click(function () {
            parent.set.list.top.dialog_set_detail.close();
        });
    }
};

set.elementAttr = {
    element_form: $("#div_element_info_form"),
    dict_select: null,
    init: function () {
        var versionCode = $.Util.getUrlQueryString('versioncode');
        $("#hdversion").val(versionCode);
        var elementId = $.Util.getUrlQueryString('id');
        $("#hdId").val(elementId);
        var setid = $.Util.getUrlQueryString('setid');
        $("#hdsetid").val(setid);
        //this.getDictList();
        this.getElementInfo();
    },
    getDictList: function (initValue, initText) {
        var version = $("#hdversion").val();
        var url = set.list._url + "/std/dataset/getMetaDataDict?version=" + version;
        set.elementAttr.dict_select = $("#criterionDict").ligerComboBox({
            condition: { inputWidth: 90 ,width:0,labelWidth:0,hideSpace:true,fields: [{ name: 'param', label:''}] },
            url: url,
            grid: getGridOptions(true),
            valueField: 'id',
            textField: 'name',
            width : 240,
            selectBoxHeight : 260,
            //selectBoxWidth: 400,
            autocomplete: true,
            keySupport: true,
            //width: 400,
            onSelected: function(id,name){
                $("#criterionDict").val(name);
            },
            conditionSearchClick: function (g) {
                var param = g.rules.length>0? g.rules[0].value : '';
                param = {param:param }
                g.grid.set({
                    parms: param,
                    newPage: 1
                });
                g.grid.reload();
            },
            onSuccess: function () {
                //set.elementAttr.dict_select.setValue(initValue);
                $("#criterionDict").css({"width": 213, "height": 28});
                $(".l-text-combobox").css({"width": 229});
                $(".l-box-select-absolute").css({"width": 229});
            },
            onAfterSetData: function () {

            }
        });

        if(initValue != ""){
            $("#criterionDict").ligerGetComboBoxManager().setValue(initValue);
            $("#criterionDict").ligerGetComboBoxManager().setText(initText);
        }

        function getGridOptions(checkbox) {
            var options = {
                columns: [
                    {display : '名称', name :'name',width : 210}
                ],
                allowAdjustColWidth : true,
                editorTopDiff : 41,
                headerRowHeight : 0,
                height : '100%',
                heightDiff : 0,
                pageSize: 15,
                pagesizeParmName : 'rows',
                record : "totalCount",
                root : "detailModelList",
                rowHeight : 30,
                rownumbers :false,
                switchPageSizeApplyComboBox: false,
                width :"98%",
                url : url
            };
            return options;
        }


        $("#datatype").ligerComboBox({
            height: 28,
            width: 220
        })

    },
    getElementInfo: function () {

        var dataSetId = $("#hdsetid").val();
        var version = $("#hdversion").val();
        var metaDataId = $("#hdId").val();
        if (metaDataId == null || metaDataId == "") {
            set.elementAttr.getDictList("");
            set.elementAttr.event();
            return;
        }
        $.ajax({
            url: set.list._url + "/std/dataset/getMetaData",
            type: "post",
            dataType: "json",
            data: {dataSetId: dataSetId, metaDataId: metaDataId, version: version},
            async: true,
            success: function (data) {

                if (data != null) {

                    var info = eval(data).obj;
                    set.elementAttr.element_form.attrScan();
                    set.elementAttr.element_form.Fields.fillValues(info);

                    $("#metaDataCodeCopy").val(info.innerCode);
                    $("#fieldNameCopy").val(info.columnName);
                    $("#datatype").val(info.columnType);
                    set.elementAttr.getDictList(info.dictId, info.dictName);
                    $("#primaryKey").attr('checked', false);
                    if (info.primaryKey == "1") {
                        $("#primaryKey").attr('checked', true);
                    }
                    $("#whetherNull").attr('checked', false);
                    if (info.nullable == "1") {
                        $("#whetherNull").attr('checked', true);
                    }
                }
            },
            error: function (data) {
            },
            complete: function () {
                set.elementAttr.event();
            }
        });
    },
    save: function () {

        set.elementAttr.element_form.attrScan();
        var dataJson = eval("[" + set.elementAttr.element_form.Fields.toJsonString() + "]");

        var id = $("#hdId").val();
        if (id == "")
            id = "0";
        dataJson[0]["id"] = id;


        var versionCode = $("#hdversion").val();
        //dataJson[0]["version"] = versionCode;
        var version = versionCode;

        var setId = $("#hdsetid").val();
        dataJson[0]["dataSetId"] = setId;


        var dictId = set.elementAttr.dict_select.getValue();
        dataJson[0]["dictId"] = dictId;

        var columnType = $("#datatype").val();
        dataJson[0]["columnType"] = columnType;

        dataJson[0]["primaryKey"] = 0;
        if ($("#primaryKey").prop("checked")) {
            dataJson[0]["primaryKey"] = 1;
        }
        dataJson[0]["nullable"] = 0;
        if ($("#whetherNull").prop("checked")) {
            dataJson[0]["nullable"] = 1;
        }
        var _url = set.list._url + "/std/dataset/updataMetaSet";

        $.ajax({
            url: _url,
            type: "POST",
            dataType: "json",
            data: {info:JSON.stringify(dataJson[0]),version:version},
            success: function (data) {
                if (data != null) {
                    var _res = eval(data);
                    if (_res.successFlg) {
                        $.ligerDialog.alert("保存成功!", "提示", "success", function () {
                            parent.set.list.top.dialog_set_detail.close();
                        }, null);
                    }
                    else {
                        $.Notice.error(_res.errorMsg);
                    }
                }
            }
        })
    },
    event: function () {
        var Util = $.Util;
        // 表单校验工具类
        var jValidation = $.jValidation;
        var validator = new jValidation.Validation($("#div_element_info_form"),
            {
                immediate: true,
                onSubmit: false,
                onElementValidateForAjax: function (elm) {
                    var hdversion = $("#hdversion").val();
                    var datasetId = $("#hdsetid").val();
                    var ErrorMsg = null;
                    if (Util.isStrEquals($(elm).attr("id"), 'metaDataInnerCode')) {
                        var metaDataCode = $("#metaDataInnerCode").val();
                        var metaDataCodeCopy = $("#metaDataCodeCopy").val();
                        if (metaDataCodeCopy != null && metaDataCodeCopy != '' && metaDataCodeCopy == metaDataCode) {
                            return true;
                        }
                        ErrorMsg = '内部代码不能重复';
                        return set.elementAttr.validatorSearchNm(hdversion, datasetId, metaDataCode, ErrorMsg, 'metaDataCodeMsg');
                    }
                    //没有做验证
                    if (Util.isStrEquals($(elm).attr("id"), 'fieldName')) {
                        var fieldName = $("#fieldName").val();
                        var fieldNameCopy = $("#fieldNameCopy").val();
                        if (fieldNameCopy != null && fieldNameCopy != '' && fieldNameCopy == fieldName) {
                            return true;
                        }
                        ErrorMsg = '字段名不能重复';
                        return set.elementAttr.validatorSearchNm(hdversion, datasetId, fieldName, ErrorMsg, 'fieldNameMsg');
                    }
                }
            });

        $("#btn_save").click(function () {
           if (validator.validate()) {
                set.elementAttr.save();
            }
        });
        $("#btn_close").click(function () {
            parent.set.list.top.dialog_set_detail.close();
        });
        //给数据元主键和是否空值的选择
        $("#primaryKey").click(function () {
            if ($(this).prop('checked')) {
                $('#whetherNull').prop('checked', false);
            }
        });
        $('#whetherNull').click(function () {
            if ($(this).prop('checked') && $('#primaryKey').prop('checked')) {
                $.Notice.error("主键不能为空！");
                $(this).prop('checked', false);
            }
        });
    },
    validatorSearchNm: function (hdversion, datasetId, searchNm, ErrorMsg, metaDataCodeMsg) {
        var jValidation = $.jValidation;
        var result = new jValidation.ajax.Result();
        var dataModel = $.DataModel.init();
        dataModel.fetchRemote(set.list._url + "/std/dataset/validatorMetadata", {
            data: {version: hdversion, datasetId: datasetId, searchNm: searchNm, metaDataCodeMsg: metaDataCodeMsg},
            async: false,
            success: function (data) {
                if (!data.successFlg) {
                    result.setResult(true);
                } else {
                    result.setResult(false);
                    result.setErrorMsg(ErrorMsg);
                }
            }
        });
        return result;
    }
};
