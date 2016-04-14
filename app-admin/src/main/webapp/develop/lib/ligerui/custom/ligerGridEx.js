/**
 * ligerGridEx -v1.0.0 (2015/10/28)
 *
 * @description LigerGrid的相关扩展
 * @example
 * $grid.ligerGrid($.LigerGridEx.config({
 * checkbox: true},{
 * isCheckedMemory: true
 * })；
 * @author      yezehua
 * @copyright   2015 www.yihu.com
 */
(function ($, win) {

    var Util = $.Util;

    function f_onCheckAllRow(checked)
    {
        for (var rowid in this.records)
        {
            if(checked)
                addCheckedRecord(this.records[rowid]['id']);
            else
                removeCheckedRecord(this.records[rowid]['id']);
        }
    }
    /*
     该例子实现 表单分页多选
     即利用onCheckRow将选中的行记忆下来，并利用isChecked将记忆下来的行初始化选中
     */
    var checkedRecords = [];
    function findCheckedRecord(id)
    {
        for(var i =0;i<checkedRecords.length;i++)
        {
            if(checkedRecords[i] == id) return i;
        }
        return -1;
    }
    function addCheckedRecord(id)
    {
        if(findCheckedRecord(id) == -1)
            checkedRecords.push(id);
    }
    function removeCheckedRecord(id)
    {
        var i = findCheckedRecord(id);
        if(i==-1) return;
        checkedRecords.splice(i,1);
    }
    function f_isChecked(rowdata)
    {
        if (findCheckedRecord(rowdata.id) == -1)
            return false;
        return true;
    }
    function f_onCheckRow(checked, data)
    {
        if (checked) addCheckedRecord(data.id);
        else removeCheckedRecord(data.id);
    }

    function resizeColumns() {
        var self = this;
        self._initColumns();

        $(self.columns).each(function () {
            if(!this.isrownumber) {
                var newwidth = this._width;
                var g = self, p = self.options;
                if (!newwidth) return;
                newwidth = parseInt(newwidth, 10);
                var column = this;

                if (!column) return;

                $('.l-grid2 .l-grid-header-table .l-grid-hd-cell',self.element).each(function(){
                    var idx = $(this).attr('columnindex');
                    $(this).css('width',self.getColumn(idx)._width);
                });
                $(document.getElementById(column['__domid'])).css('width', newwidth);
                var cells = [];
                for (var rowid in g.records)
                {
                    var obj = g.getCellObj(g.records[rowid], column);
                    if (obj) cells.push(obj);

                    if (!g.enabledDetailEdit() && g.editors[rowid] && g.editors[rowid][column['__id']])
                    {
                        var o = g.editors[rowid][column['__id']];
                        if (o.editor.resize) o.editor.resize(o.input, newwidth, o.container.height(), o.editParm);
                    }
                }
                for (var i = 0; i < g.totalNumber; i++)
                {
                    var tobj = document.getElementById(g.id + "|total" + i + "|" + column['__id']);
                    if (tobj) cells.push(tobj);
                }
                $(cells).css('width', newwidth).find("> div.l-grid-row-cell-inner:first").css('width', newwidth - 8);

            }
        });
        self._initBuildGridHeader();
        this.gridheader.width(this.gridtablewidth);
        //$("div:first", this.gridheader).width(this.gridtablewidth);
        // $("div:first", this.gridbody).width(this.gridtablewidth);
        updateScroller.apply(self, arguments);
        checkColumnVisable.call(self);
        resizeMinWidth.call(self);
    }

    function updateScroller () {
        var self = this;
        var gridScroll = $('.m-custom-scroll',self.element);
        var pluginPfx = 'mCS';
        var d=gridScroll.data(pluginPfx);

        var scrollWrapper =  $('.m-custom-scroll',this.element),
            mGridBody = $('.m-grid-body', this.element),
            mContainer =  $('.mCSB_container', this.element);

        if($('.l-grid-body-inner',this.gridview1).height() <= $('.l-grid-body-inner',this.gridview1).parent().height()) {
            scrollWrapper.css({
                width: this.gridview2.width(),
                height: this.gridview.height()-40
            });
            mGridBody.css({
                width: this.gridtablewidth - 1,
                height: $('.l-grid-body-inner',this.gridview1).height()
            });
            $('.mCSB_scrollTools_horizontal',this.gridview2).css({
                bottom: 0
            });
        } else {
            scrollWrapper.css({
                width: this.gridview2.width(),
                height: Math.min(this.gridbody.height(),this.gridview.height()-40) - 10
            });
            mGridBody.css({
                width: this.gridtablewidth - 1,
                height: Math.max(this.gridview.height()-40,$('.l-grid-body-table', this.element).height())
            });
            $('.l-grid-body1', this.gridview1).css({
                height: scrollWrapper.height()+10
            });
            $('.mCSB_scrollTools_horizontal',this.gridview2).css({
                bottom: -13
            });
        }

        mContainer.css({
            width: this.gridtablewidth - 1
        });
        self.gridbody.css({
            overflow: 'hidden',
            height:$('.l-grid-body-table', this.element).height()
        });

        if(!d) {
            self.mScroller = gridScroll.mCustomScrollbar({
                theme:"minimal-dark",
                axis:"yx",
                callbacks:{
                    whileScrolling: function(){
                        self.gridheader[0].scrollLeft = -this.mcs.left;
                        self.f.gridbody[0].scrollTop = -this.mcs.top;
                        //$('.l-grid-editor',self.element).top - self.f.gridbody[0].scrollTop;
                        // 修复滚动条滚动时，gridbody高度不变导致数据无法显示问题
                        self.gridbody.css({
                            height:$('.l-grid-body-table', self.element).height()
                        });
                    },
                    onScroll: function () {
                        //updateScroller.apply(self, arguments);
                    }
                }
            });
        }


        $(".m-custom-scroll",this.gridview2).mCustomScrollbar("update");
    }

    function checkColumnVisable () {
        var self = this;
        $('.l-grid-popup .l-checkbox', self.element).each(function() {
            var $cell = $(this);
            var idx = $cell.attr('columnindex');
            self.toggleCol(parseInt(idx), $cell[0].checked, true);
        });
    }

    function resizeMinWidth() {
        var self = this;
        $(this.columns).each(function () {
            if(this.lWidth && this.lWidth > this._width) {
                self.setColumnWidth(this.columnindex, this.lWidth);
            }
        });
    }

    $.LigerGridEx = {
        config: function (opts,ext) {
            var defaults = {
                // 存储总记录数的字段名
                record: 'totalCount',
                // 数据源字段名
                root: 'detailModelList',
                // 每页记录数
                pageSize:15,
                // 页记录数参数名，(提交给服务器)
                pagesizeParmName: 'rows',
                //width: '98%',
                height:'100%',
                // 高度补差，当设置height:100%时，可能会有高度的误差，可以通过这个属性调整
                heightDiff: -10,
                // 是否显示行序号
                rownumbers:true,
                // 表头行的高度
                headerRowHeight: 40,
                // 行的高度
                rowHeight: 40,
                // 编辑器位置误差调整
                editorTopDiff: 41,
                allowAdjustColWidth: true
            };

            var extOpts = {};
            var ext = ext || {};
            if(ext.adjustToWidth!==false) {

                $.extend(extOpts,{
                    onAfterChangeColumnWidth: function () {

                        updateScroller.apply(this, arguments);
                    },
                    onLoading: function() {

                        this.toggleLoading(true);
                    },
                    onAfterShowData: function () {

                        updateScroller.apply(this, arguments);
                        //var left = parseInt($('.mCSB_scrollTools_horizontal .mCSB_dragger').css('left'));
                        opts.onAfterShowData&&opts.onAfterShowData.apply(this, arguments);
                        $(".m-custom-scroll",this.gridview2).mCustomScrollbar("scrollTo");
                        resizeMinWidth.call(this);

                        var mcs = this.mScroller[0].mcs;
                        if(mcs) {
                            this.gridheader[0].scrollLeft = -mcs.left;
                            this.f.gridbody[0].scrollTop = -mcs.top;
                        }
                    },
                    onAfterAddRow: function () {

                        updateScroller.apply(this, arguments);
                        $(".m-custom-scroll",this.gridview2).mCustomScrollbar("scrollTo","bottom");
                    },
                    onTreeExpanded: function () {
                        this.resizeColumns();
                    },
                    onTreeCollapsed:function () {
                        this.resizeColumns();
                    }
                });
                $.ligerMethos.Grid.adjustToWidth = function () {
                    var self = this;
                    // 修复数据表初始化无数据时，出现横向滚动条问题
                    updateScroller.apply(self, arguments);
                    var timmer = null;
                    $(window).on('resize', function () {
                        if(!timmer) {
                            timmer = setTimeout(function () {
                                self.resizeColumns();
                                win.clearTimeout(timmer);
                                timmer = null;
                            },100);
                        }
                    });

                    $('.l-grid-popup .l-checkbox', self.element).click(function(){
                        updateScroller.apply(self, arguments);
                        $("div:first", self.gridheader).width($('.m-grid-body',self.gridview2).width());
                    });

                };
                $.ligerMethos.Grid.resizeColumns = resizeColumns;

            }

            if(ext.isCheckedMemory) {
                return $.extend({},opts,defaults,{
                    isChecked: function() {
                        var flag = true;
                        if(Util.isFunction(opts.isChecked)){
                            flag = opts.isChecked.apply(this, arguments);
                        }
                        return flag && f_isChecked.apply(this, arguments);
                    },
                    onCheckRow: function() {
                        var flag = true;
                        if(Util.isFunction(opts.onCheckRow)){
                            flag = opts.onCheckRow.apply(this, arguments);
                        }
                        return flag && f_onCheckRow.apply(this, arguments);
                    },
                    onCheckAllRow: function() {
                        var flag = true;
                        if(Util.isFunction(opts.onCheckAllRow)){
                            flag = opts.onCheckAllRow.apply(this, arguments);
                        }
                        return flag && f_onCheckAllRow.apply(this, arguments);
                    }
                });
            } else {
                return $.extend({},defaults,opts,extOpts);
            }
        }
    };
})(jQuery, window);
