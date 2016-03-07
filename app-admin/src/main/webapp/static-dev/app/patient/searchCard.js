/**
 * Created by zqb on 2015/8/24.
 */
define(function (require, exports, module) {
    // TODO
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

    require('app/patient/searchCard.css');

    var tpl = '<div class="search-card-header">' +
                    '<div class="f-ml10 search-card-title"><strong>'+$.i18n.prop('lab.card.search')+'</strong></div>'+
                    '<span>' +
                        '<input type="text" class="f-ml10 f-mt20" id="searchCardNm" placeholder="'+$.i18n.prop('lab.card.no')+'" > ' +
                        '<select id="searchType" name="searchType" class="inputwidth f-ml20 search-card-type"> ' +
                        '</select>' +
                        '<button type="button" class="btn btn-primary J_search-btn f-ml10" >'+$.i18n.prop('lab.search')+'</button>' +
                    '</span>' +
                '</div>' +
                '<div id="grid-search-card" data-pagerbar-items="10"> </div>';

    var trTpl1 = '{@each data as d,index}' +
        '<tr> <td><div class="f-tar">{$d.name}：</div></td> <td><input  class="inputwidth" value="{$d.value}" disabled/> </td></tr>' +
        '{@/each}';
    var trTpl2 = '{@each data as d,index}' +
        '<tr> <td><div class="f-tar">{$d.name}：</div></td> <td><textarea  class="inputwidth detail-textarea" disabled>{$d.value}</textarea> </td></tr>' +
        '{@/each}';

    var infoTpl =   '<div class="modal fade attach-card-modal msg-modal" id="attachCardModal" tabindex="0" role="dialog" aria-labelledby="confirmModalLabel" data-backdrop="static" > ' +
                        '<div class="modal-dialog msg-dialog" role="document"> ' +
                            '<div class="modal-content"> ' +
                                '<div class="modal-header modal-title"> ' +
                                    '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button> ' +
                                    '<h4 class="modal-title" id="confirmModalLabel">'+$.i18n.prop('lab.binding.information')+'</h4> ' +
                                '</div> ' +
                                '<div class="modal-body f-tac msg-body"> ' +
                                    '<input id="addObjectId" type="hidden"/> ' +
                                    '<input id="addType" type="hidden"/> ' +
                                    '<h5><strong>'+$.i18n.prop('lab.determines.binding.card.information')+'</strong></h5> ' +
                                    '<h5>'+$.i18n.prop('lab.confirm.to.bind.information')+'</h5> ' +
                                '</div> ' +
                                '<div class="modal-footer"> ' +
                                    '<button type="button" class="btn btn-primary f-fl f-ml50 J_attach-btn" data-dismiss="modal">'+$.i18n.prop('btn.confirm')+'</button> ' +
                                    '<button type="button" class="btn btn-default f-fr f-mr50" data-dismiss="modal">'+$.i18n.prop('btn.cancel')+'</button> ' +
                                '</div> ' +
                            '</div> ' +
                        '</div> ' +
                    '</div>';

    var infoBoxTpl = '<div class="modal fade f-pl30 f-pt30" id="searchCardDetail" tabindex="-1" role="dialog" aria-labelledby="searchCardDetailLabel" data-backdrop="static">' +
                        '<div class="modal-dialog f-w400" role="document">' +
                            '<div class="modal-content">' +
                                '<div class="modal-header">' +
                                    '<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>' +
                                    '<h4 class="modal-title" id="searchCardDetailLabel">'+$.i18n.prop('lab.attribute')+'</h4>' +
                                '</div>' +
                                '<div class="modal-body f-tac search-card-table">' +
                                    '<table class="table J_detailTable">' +
                                    '</table>' +
                                '</div>' +
                            '</div>' +
                        '</div>' +
                    '</div>';

    function initSelect(dictId,targetInit){
        /*下拉框的方法*/
        var target = $(targetInit);
        $.ajax({
            url:context.path + "/dict/searchDictEntryList",
            type:"post",
            dataType:"json",
            data:{dictId:dictId,page:"1",rows:"5"},
            success:function(data){
                var option ='<option value="">'+$.i18n.prop('select.choose.all')+'</option>';
                //var option='';
                for(var i=0;i<data.detailModelList.length;i++){
                    option +='<option value='+data.detailModelList[i].code+'>'+data.detailModelList[i].value+'</option>';
                }
                $(target).append(option);
            }
        });
    }

    function getModalInfo(objectId,type) {
        $.ajax({
            type : "POST",
            url : context.path+"/card/getCard",
            data : {objectId:objectId,type:type},
            dataType : "json",
            success :function(data){
                var model = data.obj.cardModel;
                var ds1 = [
                    {   name: $.i18n.prop('grid.type'), value: model.typeValue },
                    {   name: $.i18n.prop('grid.card.no'), value: model.number },
                    {   name: $.i18n.prop('grid.holder.name'), value: model.ownerName },
                    {   name: $.i18n.prop('grid.issue'), value: model.local },
                    {   name: $.i18n.prop('grid.distribution.institution'), value: model.releaseOrg },
                    {   name: $.i18n.prop('grid.add.date'), value: model.createDate },
                    {   name: $.i18n.prop('grid.status'), value: model.statusValue }
                ];
                var ds2 = [
                    {   name: $.i18n.prop('grid.summary'), value: model.description }
                ];
                $('.J_detailTable', '#searchCardDetail').empty();
                var html1 = juicer(trTpl1, {data: ds1});
                $('.J_detailTable', '#searchCardDetail').append(html1);
                var html2 = juicer(trTpl2, {data: ds2});
                $('.J_detailTable', '#searchCardDetail').append(html2);
            },
            error :function(data){
            }
        });
    }

    function reloadGrid() {
        var searchNm = $("#searchCardNm").val();
        var searchType = $('#searchType').find('option:selected').val();
        grid.instance.jqGrid('setGridParam',{
            url: context.path+"/card/searchNewCard",
            datatype: 'json',
            postData:{idCardNo:idCardNo,searchNm:searchNm,searchType:searchType} //发送数据
        }).trigger("reloadGrid");
    }

    var grid,
        idCardNo = '';
    exports.setIdCardNo = function(no){
        idCardNo = no;
    };
    exports.init = function (selector, opts) {

        opts = $.extend({},opts);
        var operator = !opts.operator;
        $(function () {
            var target = $(selector);
            target.append(juicer(tpl,{data:[]}));
            initSelect(10,$('#searchType'));
            grid = new Grid('#grid-search-card',{
                datatype:'json',
                height: 481,
                rowNum : 10,
                mtype:'POST',
                colNames:['ID',$.i18n.prop('grid.order'),'类型code',$.i18n.prop('grid.type'), $.i18n.prop('grid.card.no'), $.i18n.prop('grid.distribution.institution'),$.i18n.prop('grid.add.time'),'状态code',$.i18n.prop('grid.status'),$.i18n.prop('grid.operation')],
                colModel:[
                    {name:'objectId',index:'objectId',hidden:true,width:50,  align: "center"},
                    {name:'order',index:'order',sorttype: 'int', width:40,  align: "center" },
                    {name:'type',index:'type',hidden:true, width:56, align:"center"},
                    {name:'typeValue',index:'typeValue', width:100, align:"center"},
                    {name:'number',index:'number', width:140, align:"center"},
                    {name:'releaseOrg',index:'releaseOrg', width:132, align:"center"},
                    {name:'createDate',index:'createDate', width:94,align:"center"},
                    {name:'status',index:'status',hidden:true, width:40, align:"center"},
                    {name:'statusValue',index:'statusValue', width:40, align:"center"},
                    {
                        name: 'operator ',
                        index: 'operator',
                        width: 40,
                        hidden:operator,
                        sortable: false,
                        align: "center",
                        formatter: function (value, grid) {
                            return '<a data-toggle="modal" data-target="#attachCardModal" class="J_modify-btn" data-rowid="' + grid.rowId + '">'+$.i18n.prop('btn.bind')+'</a>';
                        }
                    }
                ],
                loadonce : true,
                jsonReader : {
                    root:"detailModelList",
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
                    $('#searchCardDetail').modal();
                }
            }).render();

            $(document.body).append(juicer(infoTpl+infoBoxTpl,{data:[]}));

            new ViewController('#attachCardModal', {
                events: {
                    'click .J_attach-btn': 'attach'
                },
                handlers: {
                    attach: function () {
                        var objectId = $("#addObjectId").val();
                        var type = $("#addType").val();
                        $.ajax({
                            type : "POST",
                            url : context.path+"/card/attachCard",
                            data : {idCardNo:idCardNo,objectId:objectId,type:type},
                            dataType : "json",
                            success :function(data){
                                if (data.successFlg) {
                                    reloadGrid();
                                } else {
                                    alert($.i18n.prop('message.toupdate.failure'));
                                    return;
                                }
                            },
                            error :function(data){
                            }
                        });
                    }
                }
            });
            new ViewController(target,{
                events: {'click .J_modify-btn': 'modifyItem',
                    'click .J_search-btn': 'search'
                },
                handlers: {
                    modifyItem: function (e) {
                        var cell = $(e.currentTarget);
                        var rowid = cell.attr("data-rowid");
                        var objectId = grid.instance.getRowData(rowid).objectId;
                        var type = grid.instance.getRowData(rowid).type;
                        $("#addObjectId").val(objectId);
                        $("#addType").val(type);
                    },
                    search: function () {
                        reloadGrid();
                    }
                }
            });

        });
    }
});