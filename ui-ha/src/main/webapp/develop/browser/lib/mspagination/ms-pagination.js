(function ($,win) {
    var template =
        //'<div  class="ms-page-bar">' +
        //    '<a href="javascript:void(0)" class="ms-left ms-page-pre">&nbsp;</a>'+
            '{@each pages as page}' +
            '<a href="javascript:void(0)" data-num="{{page.page}}" class="ms-page-num {@if page.page==page.curPage}ms-page-num-active{@/if}">{{page.page}}</a>'+
            '{@/each}' ;
            //'<a href="javascript:void(0)" class="ms-right ms-page-next ms-right-active">&nbsp;</a>'+
        //'</div>';
    var _el = undefined;
    var options = {
        clickFunc : undefined,
        pageSize: 5,
        total: undefined,
        totalPage:0,
        curPage: 1,
        model: 1, //1: 1,2,3   2:  1,2,3, ... ,4,5,6
        showItems: 5
    }

    function createHtml(){
        if(!options.total)
            return;
        var $div = $('<div  class="ms-page-bar">');
        $div.append('<a href="javascript:void(0)" ' +
                        'class="ms-left ms-page-pre '+(options.curPage!=1?'ms-left-active':'')+'"' +
                        '>&nbsp;</a>');
        var tmp = Math.ceil(options.showItems/2);
        var pageLs = [];
        if(options.model==1){
            if(options.curPage<=tmp){
                for(var i=1; i<=options.showItems&&i<=options.totalPage; i++){
                    pageLs.push({page: i, curPage: options.curPage});
                }
            }
            else if(options.curPage+tmp-1>=options.totalPage){
                for(var i=options.totalPage; i>options.totalPage-options.showItems; i--){
                    pageLs.push({page: i, curPage: options.curPage});
                }
                pageLs = pageLs.reverse();
            }
            else{
                var i=options.curPage-tmp+1;
                var to = i+ options.showItems-1;
                for(; i<=to; i++){
                    pageLs.push({page: i, curPage: options.curPage});
                }
            }
        }
        else if(options.model==2){

        }
        $div.append($(juicer(template, {pages: pageLs})));
        $div.append('<a href="javascript:void(0)" ' +
                        'class="ms-right ms-page-next '+(options.curPage!=options.totalPage?'ms-right-active':'')+'"' +
                        '>&nbsp;</a>');
        return $div;
    }

    function render(){
        _el.empty();
        createHtml().appendTo(_el);
        bindEvent();
    }


    function bindEvent(){
        var p = _el, opts = options;
        $.each($('.ms-page-bar').find('a[class*="ms-page-num"]'), function (i, el) {
            $(el).click(function () {
                var toPage = parseInt($(el).attr('data-num'));
                if(toPage==opts.curPage)
                    return;
                opts.curPage = toPage;
                render();
                if(opts.clickFunc)
                    opts.clickFunc({curPage: opts.curPage, total: opts.total, pageSize: opts.pageSize});
            });
        });
        var left = $('.ms-page-bar').find('a.ms-left');
        var right = $('.ms-page-bar').find('a.ms-right');
        left.click(function () {
            if(opts.curPage==1)
                return;
            --opts.curPage;
            render();
            if(opts.clickFunc)
                opts.clickFunc({curPage: opts.curPage, total: opts.total, pageSize: opts.pageSize});
        })
        right.click(function () {
            if(opts.curPage==opts.totalPage)
                return;
            ++opts.curPage;
            render();
            if(opts.clickFunc)
                opts.clickFunc({curPage: opts.curPage, total: opts.total, pageSize: opts.pageSize});
        })
    }



    $.fn.msPagination = function (opts) {
        _el = this;
        $.extend(options, opts || {});
        options.totalPage = parseInt(options.total/options.pageSize) + (options.total % options.pageSize==0? 0 : 1);
        render();
    };
})(jQuery, window);