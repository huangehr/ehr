/**
 * Created by AndyCai on 2015/12/21.
 */

(function (exports) {
    var list = {};
    var webRoot=$("#hd_url").val();
    exports.list = list;

    var base = {
        p: {},
        grid: null,
        init: function (p) {
            // 初始化参数
            base.p = p || [];
            // 构建表格
            base.buildGrid();
            // 绑定按钮
            base.bindBtn();
            // 初始获取数据
            p.get("", base.loadData);
            // 绑定检索事件
            base.bindSearch();

            ////数据集选择器 select change  事件
            //base.bindChange();
        },
        buildGrid: function () {
            var jq_pane = $("#pane-list").empty(),
                param = {
                    columns: base.p.columns,
                    width: "98%",
                    height: 250,
                    usePager: false,
                    checkbox: true,
                    isChecked: base.isChecked,
                    onCheckRow: base.onChecked,
                    onCheckAllRow: base.onCheckAllRows,
                    root: 'Rows'
                };
            // init grid
            var grid = base.grid = jq_pane.ligerGrid($.LigerGridEx.config(param));
            //console.log($.LigerGridEx.config(param));
            if (!base.p.multiple) {
                var chkObj = $("#pane-list .l-grid-hd-cell-checkbox");
                chkObj.empty().removeClass("l-grid-hd-cell-checkbox");
            }
        },
        bindBtn: function () {
            $("#btn-submit").click(function () {
                if (typeof (base.p.callback) == "function") {
                    base.p.callback();
                }
                //if (!top.mustCheck || top.mustCheck == "undefined") {
                //    base.p.win.close();
                //}
            });
            $("#btn-cancel").click(function () {
                //if ($("#pane-list-selected").hasClass("changed")) {
                //    window.parent.common.alert.confirm("编辑数据未保存,是否确认关闭窗口?", function (param) {
                //        if (param) {
                //            base.p.win.close();
                //        }
                //    });
                //} else {
                //    base.p.win.close();
                //}
                base.p.win.close();
            });
        },
        bindSearch: function () {
            var jq_search = $("#txb-key");
            // IE only
            try {
                jq_search.bind("input propertychange keyup", function () { base.searchEvent(); });
            } catch (e) {

            }

            //if ("\v" == "v") { jq_search.bind("propertychange", function () { base.searchEvent(); }); }
            //else { jq_search[0].addEventListener("input", base.searchEvent, false); };
        },
        searchEvent: function () {
            var jq_search = $("#txb-key");
            base.grid.reload({ Rows: [] });
            base.p.get(jq_search.val().trim(), base.loadData);
        },
        loadData: function (data) {
            var selectedData = [], newData = [];
            var uniqueField = base.p.uniqueField,
                storage = base.p.storage;
            for (var i = 0; i < storage.length; i++) {
                for (var j = 0; j < data.length; j++) {
                    if (storage[i][uniqueField] == data[j][uniqueField]) {
                        selectedData.push(data[j]);
                        data.splice(j, 1);
                    }
                }
            }
            newData = selectedData.concat(data);
            base.grid.reload({ Rows: newData });
        },
        isChecked: function (rowdata) {
            var uniqueField = base.p.uniqueField,
                storage = base.p.storage;
            for (var i = 0; i < storage.length; i++) {
                if (storage[i][uniqueField] == rowdata[uniqueField]) {
                    storage[i].name = rowdata.name;
                    storage[i].code = rowdata.code;
                    base.addItem(storage[i]);
                    return true;
                }
            }
            return false;
        },
        onChecked: function (checked, data, rowid, rowdata) {
            $("#pane-list-selected").addClass("changed");//取消是否提示
            // 非多选内容
            if (!base.p.multiple) {
                var uniqueField = base.p.uniqueField,
                    rows = base.grid.getSelectedRows();
                for (var i = 0; i < rows.length; i++) {
                    var curObj = rows[i];
                    if (curObj[uniqueField] != data[uniqueField]) {
                        base.deleteItem(curObj);
                    }
                }
            }
            // 判断
            if (checked) {
                base.addItem(data);
                base.pushData(data);
            }
            else {
                base.deleteItem(data);
            }
           //执行Controller方法获取XML信息,并赋值给浏览器
            var strSetId = "";
            for(var i=0;i<base.p.storage.length;i++)
            {
                //添加
                strSetId+=base.p.storage[i].id+",";
            }
            strSetId=strSetId.substring(0,strSetId.length-1);
            if (typeof (base.p.callback) == "function") {
                base.p.get_relation_xml(strSetId,base.p.versionCode);
            }
        },
        onCheckAllRows: function (checked) {
            $("#pane-list-selected").empty();
            $("#pane-list-selected").addClass("changed");//取消是否提示
            base.p.storage.splice(0);
            //如果是全选
            if (checked) {
                var rows = base.grid.getSelectedRows();
                for (var i = 0; i < rows.length; i++) {
                    var curObj = rows[i];
                    base.addItem(curObj);
                    base.pushData(curObj);
                };
            }

            //执行Controller方法获取XML信息,并赋值给浏览器
            var strSetId = "";
            for(var i=0;i<base.p.storage.length;i++)
            {
                strSetId+=base.p.storage[i].id+",";
            }
            strSetId=strSetId.substring(0,strSetId.length-1);
            if (typeof (base.p.callback) == "function") {
                base.p.get_relation_xml(strSetId,base.p.versionCode);
            }
        },
        addItem: function (data) {
            var jq_ul = $("#pane-list-selected");
            // var itemclass = data[base.p.uniqueField].replace("(", "_").replace(")", "_");
            var itemclass = data[base.p.uniqueField].toString().replace("(", "_").replace(")", "_");
            if ($(".item.item-" + itemclass).length > 0) { return; }
            if (!base.p.multiple) { jq_ul.empty(); }

            var jq_li = $("<li/>").appendTo(jq_ul),
                jq_close = $("<a/>").appendTo(jq_li);
            //当class中有括号时换成下划线

            //end
            var _text = data[base.p.displayField];
            if (base.p.titalField != undefined) {
                _text = data[base.p.titalField] + "-" + _text;
            }

            jq_li
                .append(_text)
                .attr("title", _text)
                .addClass("item item-" + itemclass);

            jq_close
                .text("X")
                .addClass("close")
                .data("data", data)
                .click(function () { $("#pane-list-selected").addClass("changed"); base.deleteItem($(this).data("data")); });
        },
        deleteItem: function (data) {
            var uniqueField = base.p.uniqueField,
                storage = base.p.storage;
            // 移除grid中的勾选
            var rows = base.grid.getSelectedRows()
            for (var i = 0; i < rows.length; i++) {
                var curObj = rows[i];
                if (curObj[uniqueField] == data[uniqueField]) {
                    base.grid.unselect(curObj);
                    break;
                }
            }
            //ls去掉class中的括号换为下划线
            var itemclass = data[base.p.uniqueField].toString().replace("(", "_").replace(")", "_");
            //end

            // 去除Item
            $("#pane-list-selected li.item-" + itemclass).remove();
            // 移除存储的对象
            for (var i = 0; i < storage.length; i++) {
                if (storage[i][uniqueField] == data[uniqueField]) {
                    storage.splice(i, 1);
                    break;
                }
            }

            //执行Controller方法获取XML信息,并赋值给浏览器
            var strSetId = "";
            for(var i=0;i<base.p.storage.length;i++)
            {
                strSetId+=base.p.storage[i].id+",";
            }
            strSetId=strSetId.substring(0,strSetId.length-1);
            if (typeof (base.p.callback) == "function") {
                base.p.get_relation_xml(strSetId,base.p.versionCode);
            }
        },
        pushData: function (data) {
            if (!base.p.multiple) { base.p.storage.splice(0); }
            base.p.storage.push(data);
        },
        //#region 通用字典查询用前端查询（xml格式，用前端查询）
        getWhere: function () {
            if (!base.grid) return null;
            var clause = function (rowdata, rowindex) {
                var key = $("#txb-key").val();
                var uniqueField = base.p.uniqueField;
                var displayField = base.p.displayField;
                return (rowdata[uniqueField].indexOf(key) > -1) || (rowdata[displayField].indexOf(key) > -1);
            }
            return clause;
        },
        localSearch: function () {
            var jq_search = $("#txb-key");
            // IE only
            if ("\v" == "v") { jq_search.bind("propertychange", function () { base.getSearch(); }); }
            else { jq_search[0].addEventListener("input", base.getSearch, false); };
        },
        getSearch: function () {
            base.grid.options.data = $.extend(true, {}, { Rows: top.result });
            base.grid.loadData(base.getWhere());
        }
    };
    list.dataset=function(versionCode,cda_id){
        var top = $.Util.getTopWindowDOM();
        if (top.list_dataset_storage == null) { top.list_dataset_storage = []; }
        base.init({
            win: top.dialog_cda_detail,
            storage:top.list_dataset_storage,
            callback: function () {
                top.list_dataset_savedata = top.list_dataset_storage;
                if (top.list_dataSet_callback != undefined) {
                    top.list_dataSet_callback();
                }
            },
            get_relation_xml:function(setId, VersionCode) {
                if (top.get_relation_xml != undefined)
                {
                    top.get_relation_xml(setId, VersionCode);
                }
            },
            versionCode:top.versionCode,
            multiple: $.Util.getUrlQueryString("multiple") != 0 ? true : false,
            uniqueField: "id",
            displayField: "name",
            titalField: "code",
            columns: [
                { display: '数据集代码', name: 'code', align: 'left' },
                { display: '数据集名称', name: 'name',  align: 'left' }
            ],
            get: function (key, callback) {
                var url = webRoot + "/std/dataset/searchDataSets";
                console.log(url);
                $.ajax({
                    url: url,
                    type:"post",
                    dataType:"json",
                    data:{codename:key,version:versionCode,page:1,rows:999},
                    success:function(data) {
                        if (data != null) {
                            var result = eval(data);
                            callback(result.detailModelList);
                        }
                    }
                })
            }
        });
    }

})(window);