/*!
 * grid -v1.0.0 (2015/8/6)
 * 
 * @description:
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    var $ = require('jquery'),
        juicer    = require('juicer'),
        Util      = require('utils'),
        Component = require('component'),
        ViewController = require('viewController');
    require('jqGrid');
    require('jqGridCN');
    require('modules/components/grid.css');

    var tpl = '<div class="u-grid">' +
                    '<table id="{$$id}" class="grid-body"></table>' +
                  '<div id="{$$id}-pager"> </div>' +
               '</div>';

    //replace icons with FontAwesome icons like above
    function updatePagerIcons(table) {
        var replacement =
        {
            'ui-icon-seek-first' : 'icon-double-angle-left bigger-140',
            'ui-icon-seek-prev' : 'icon-angle-left bigger-140',
            'ui-icon-seek-next' : 'icon-angle-right bigger-140',
            'ui-icon-seek-end' : 'icon-double-angle-right bigger-140'
        };
        $('.ui-pg-table:not(.navtable) > tbody > tr > .ui-pg-button > .ui-icon').each(function(){
            var icon = $(this);
            var $class = $.trim(icon.attr('class').replace('ui-icon', ''));

            if($class in replacement) icon.attr('class', 'ui-icon '+replacement[$class]);
        })
    }

    return Component.create({

            init: function (target,options) {

                this.target = $(target);
                this.options = $.extend({
                    datatype: "local",
                    regional : 'cn',
                    data: [],
                    rowList:[5,10,20,30] //每页记录数可选列表
                }, options);
            },
            render: function () {

                var opt = this.options;
                var girdId = Util.guid({strFormat: 'grid-xxxxxxxx-xxxx'}); // 自动生成jqGrid需要的table容器id
                var html = juicer(tpl,{id: girdId});
                this.target.append(html);

                var grid = $("#"+girdId);
                grid.jqGrid(
                    $.extend({}, {
                            pager: girdId+'-pager',
                            pagerpos:'left',
                            loadComplete : function() {
                                var table = this;
                                setTimeout(function(){
                                    if(Util.isFunction(opt.loadComplete)) {
                                        opt.loadComplete.apply(table);
                                    }
                                    updatePagerIcons(table);
                                }, 0);
                            }
                        },opt)
                );
                // grid.jqGrid('filterToolbar',{searchOperators : true});
                if(opt.pager === null) {
                    grid.jqGrid('navGrid',girdId+'-pager',
                        { 	//navbar options
                            edit: true,
                            editicon : 'icon-pencil blue',
                            add: true,
                            addicon : 'icon-plus-sign purple',
                            del: true,
                            delicon : 'icon-trash red',
                            search: true,
                            searchicon : 'icon-search orange',
                            refresh: true,
                            refreshicon : 'icon-refresh green',
                            view: true,
                            viewicon : 'icon-zoom-in grey'
                        });
                }

                grid.trigger("reloadGrid");

                this.instance = grid;

                if(opt.autoFit===true) {
                    $(window).resize(function() {
                        var marginHeight = opt.marginHeight||160;
                        if (Util.isIE(8)) {
                            grid.setGridHeight(document.documentElement.clientHeight - marginHeight);
                        } else {
                            grid.setGridHeight(window.innerHeight - marginHeight);
                        }
                        //必须要再高度设置完再计算宽度，否则会有垂直滚动条18的宽度差
                        var marginWidth = opt.marginWidth || 0;
                        var gridWidth = opt.widthLock||false?grid.width():window.innerWidth;
                        grid.setGridWidth(gridWidth - marginWidth);
                    });
                }

                return this;
            },
            createController: function () {

                var self = this;
                new ViewController(this.target,{
                    events: {'click ul.pagination>li.J_pgnode': 'pagerClick'
                    },
                    handlers: {
                        pagerClick: function (e) {


                        }
                    }
                });
            }
        });
});