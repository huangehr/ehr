/**
 * Created by zqb on 2015/8/26.
 */
define(function (require, exports, module) {
    window.$ = $;

    var $ = require('base').$,
        context = require('base').context,
        Grid = require('modules/components/grid'),
        juicer = require('juicer'),
        ViewController = require('viewController');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    require('app/patient/cardManager.css');

    var tpl =   '<div class="f-ml10 attach-card-header">' +
                    '<div class="f-ml10 attach-card-title"><strong>'+$.i18n.prop('lab.binding.card')+'</strong></div>'+
                    '<span>' +
                        '<input type="text" class="f-ml10 f-mt20" id="attachSearchNm" placeholder="'+$.i18n.prop('lab.card.no')+'" > ' +
                        '<button type="button" class="btn btn-primary J_search-btn f-ml10" >'+$.i18n.prop('lab.search')+'</button>' +
                    '</span>' +
                '</div>' +
                '<div id="grid-attach-card" data-pagerbar-items="10"> </div>';

    var trTpl1 = '{@each data as d,index}' +
                '<tr> <td><div class="f-tar">{$d.name}：</div></td> <td><input  class="inputwidth" value="{$d.value}" disabled/> </td></tr>' +
                '{@/each}';
    var trTpl2 = '{@each data as d,index}' +
                '<tr> <td><div class="f-tar">{$d.name}：</div></td> <td><textarea  class="inputwidth detail-textarea" disabled>{$d.value}</textarea> </td></tr>' +
                '{@/each}';

    var infoTpl = '<div class="modal fade detach-card-modal msg-modal" id="detachCardModal" tabindex="0" role="dialog" aria-labelledby="confirmModalLabel" data-backdrop="static"> ' +
                    '<div class="modal-dialog msg-dialog" role="document"> ' +
                        '<div class="modal-content"> ' +
                            '<div class="modal-header modal-title"> ' +
                                '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> ' +
                                '<h4 class="modal-title" id="confirmModalLabel">'+$.i18n.prop('lab.solution.to.bind.information')+'</h4> ' +
                            '</div> ' +
                            '<div class="modal-body f-tac msg-body"> ' +
                                '<input id="delObjectId" type="hidden"/> ' +
                                '<input id="delType" type="hidden"/> ' +
                                '<h5><strong>'+$.i18n.prop('message.determine.card.information')+'</strong></h5> ' +
                                '<h5>'+$.i18n.prop('lab.confirm.to.bind.information')+'</h5> ' +
                            '</div> ' +
                            '<div class="modal-footer"> ' +
                                '<button type="button" class="btn btn-primary f-fl f-ml50 J_detach-btn" data-dismiss="modal">'+$.i18n.prop('btn.confirm')+'</button> ' +
                                '<button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal">'+$.i18n.prop('btn.cancel')+'</button> ' +
                            '</div> ' +
                        '</div> ' +
                    '</div> ' +
                '</div>';

    var infoBoxTpl =    '<div class="modal fade f-pl30 f-pt30" id="attachCardDetail" tabindex="-1" role="dialog" aria-labelledby="cardDetailModalLabel" data-backdrop="static">' +
                            '<div class="modal-dialog f-w400" role="document">' +
                                '<div class="modal-content">' +
                                    '<div class="modal-header">' +
                                        '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                        '<h4 class="modal-title" id="cardDetailModalLabel">'+$.i18n.prop('lab.attribute')+'</h4>' +
                                    '</div>' +
                                    '<div class="modal-body f-tac attach-card-table">' +
                                        '<table class="table J_attachTable">' +
                                        '</table>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>';

    function getModalInfo(objectId, type) {
        $.ajax({
            type: "POST",
            url: context.path + "/card/getCard",
            data: {objectId: objectId, type: type},
            dataType: "json",
            success: function (data) {
                var model = data.obj.cardModel;
                var ds1 = [
                    {   name: $.i18n.prop('grid.type'), value: model.typeValue},
                    {   name: $.i18n.prop('grid.card.no'), value: model.number},
                    {   name: $.i18n.prop('grid.holder.name'), value: model.ownerName},
                    {   name: $.i18n.prop('grid.issue'), value: model.local},
                    {   name: $.i18n.prop('grid.distribution.institution'), value: model.releaseOrg},
                    {   name: $.i18n.prop('grid.add.date'), value: model.createDate},
                    {   name: $.i18n.prop('grid.status'), value: model.statusValue}
                ];
                var ds2 = [
                    {   name: $.i18n.prop('grid.summary'), value: model.description }
                ];
                $('.J_attachTable', '#attachCardDetail').empty();
                var html1 = juicer(trTpl1, {data: ds1});
                $('.J_attachTable', '#attachCardDetail').append(html1);
                var html2 = juicer(trTpl2, {data: ds2});
                $('.J_attachTable', '#attachCardDetail').append(html2);
            },
            error: function (data) {
            }
        });
    }

    function reloadGrid() {
        var searchNm = $("#attachSearchNm").val();
        grid.instance.jqGrid('setGridParam', {
            url: context.path+"/card/searchCard",
            datatype: 'json',
            postData: {idCardNo: idCardNo, searchNm: searchNm} //发送数据
        }).trigger("reloadGrid");
    }

    var grid,
        idCardNo = '';
    exports.setIdCardNo = function(no){
        idCardNo = no;
        reloadGrid();
    };
    exports.init = function (selector) {
        $(function () {
            var target = $(selector);
            target.append(juicer(tpl, {data: []}));
            grid = new Grid('#grid-attach-card', {
                height: 481,
                mtype:'POST',
                rowNum: 10,
                colNames: ['ID',$.i18n.prop('grid.order'),$.i18n.prop('grid.type.code'),$.i18n.prop('grid.type'), $.i18n.prop('lab.card.no'), $.i18n.prop('grid.distribution.institution'),$.i18n.prop('grid.add.time'),'状态code',$.i18n.prop('grid.status'),$.i18n.prop('grid.operation')],
                colModel: [
                    {
                        name: 'objectId',
                        index: 'objectId',
                        hidden: true,
                        width: 50,
                        align: "center"
                    },
                    {
                        name: 'order',
                        index: 'order',
                        sorttype: 'int',
                        width: 40,
                        align: "center"
                    },
                    {
                        name: 'type',
                        index: 'type',
                        hidden:true,
                        width: 56,
                        align: "center"
                    },
                    {name:'typeValue',index:'typeValue', width:100, align:"center"},
                    {
                        name: 'number',
                        index: 'number',
                        width: 140,
                        align: "center"
                    },
                    {
                        name: 'releaseOrg',
                        index: 'releaseOrg',
                        width: 132,
                        align: "center"
                    },
                    {
                        name: 'createDate',
                        index: 'createDate',
                        width: 94,
                        align: "center"
                    },
                    {
                        name: 'status',
                        index: 'status',
                        hidden:true,
                        width: 40,
                        align: "center"
                    },
                    {
                        name: 'statusValue',
                        index: 'statusValue',
                        width: 40,
                        align: "center"
                    },
                    {
                        name: 'operator ',
                        index: 'operator',
                        width: 40,
                        sortable: false,
                        align: "center",
                        formatter: function (value, grid) {
                            return '<a data-toggle="modal" data-target="#detachCardModal" class="J_delete-btn" data-rowid="' + grid.rowId + '">'+$.i18n.prop('btn.solution.to.bind')+'</a>';
                        }
                    }
                ],
                loadonce: true,
                jsonReader: {
                    root: "detailModelList",
                    page: "currPage",
                    total: "totalPage",
                    records: "totalCount",
                    repeatitems: false,
                    id: "0"
                },
                onSelectRow: function(id){
                    var objectId = grid.instance.getRowData(id).objectId;
                    var type = grid.instance.getRowData(id).type;
                    getModalInfo(objectId, type);
                    $('#attachCardDetail').modal();
                }
            }).render();

            $(document.body).append(juicer(infoTpl+infoBoxTpl, {data: []}));
            new ViewController('#detachCardModal', {
                events: {
                    'click .J_detach-btn': 'detach'
                },
                handlers: {
                    detach: function () {
                        var objectId = $("#delObjectId").val();
                        var type = $("#delType").val();
                        $.ajax({
                            type: "POST",
                            url: context.path+"/card/detachCard",
                            data: {objectId: objectId, type: type},
                            dataType: "json",
                            success: function (data) {
                                if (data.successFlg) {
                                    reloadGrid();
                                } else {
                                    alert($.i18n.prop('message.update.failure'));
                                    return;
                                }
                            },
                            error: function (data) {
                            }
                        });
                    }
                }
            });
            new ViewController(target, {
                events: {
                    'click .J_delete-btn': 'deleteItem',
                    'click .J_search-btn': 'search'
                },
                handlers: {
                    deleteItem: function (e) {
                        var cell = $(e.currentTarget);
                        var rowid = cell.attr("data-rowid");
                        var objectId = grid.instance.getRowData(rowid).objectId;
                        var type = grid.instance.getRowData(rowid).type;
                        $("#delObjectId").val(objectId);
                        $("#delType").val(type);
                    },
                    search: function () {
                        reloadGrid();
                    }
                }
            });

        });
    }
});