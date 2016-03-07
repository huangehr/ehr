(function ($, win) {
    var Util = $.Util;
    var pluginName = "timeline";
    var namespace  = pluginName;

    var template =
        '{@each events as event}' +
        '<li class="event {@if event.open==true }open{@/if} " data-index="{{event.title}}" >' +
        '<div class="event-title"> ' +
        '{@if event.title}' +
        '<div class="title-wrap"><span>{{event.title}}</span> </div>' +
        '<div class="event-title-diamond"></div> ' +
        '<div class="event-title-diamond-covered"></div> ' +
        '{@/if}'+
        '</div>' +
        '<div class="bubble" data-cache="{{event.cache}}"> ' +
        '<span class="switch"></span>' +
        '{@if event.content }' +
        '<div class="event-content"> ' +
        '<div class="boder-diamond"></div>' +
        '<div class="diamond-covered"></div>' +
        '<div class="content f-p10">$${event.content}</div> ' +
        '</div>' +
        '{@/if}' +
        '</div>' +
        '{@if event.children }' +
        '<ul class="detail">'+
        '{@each event.children as item}' +
        '<li>' +
        '<span class="triangle-icon"></span>' +
        '<span class="detail-content" data-cache="{{item.cache}}">{{item.content}}</span>' +
        '</li>' +
        '{@/each}'+
        '</ul>'+
        '{@/if}' +
        '</li>' +
        '{@/each}' ;
    function createHtml() {
        var o = this.option;
        var data = o.data || [];
        var $target = $(this.element).empty();
        var $ul = $('<ul>').appendTo($target);
        if(o.horizontal) {
            $target.addClass('time-line-horizontal');
        }
        $.each(data,function (i, d) {
            var $li = $(juicer(template, {events:[{title: d.title}]})).addClass('parent');
            var $childrenUl = $('<ul class="children">').append(juicer(template, {events: d.children}));
            if(d.open) {
                $li.addClass('tl-open');
            } else {
                $li.addClass('tl-close');
            }
            $li.append($childrenUl);
            $ul.append($li);
        });
        $('.event-title',$target).css({width: o.titleWidth});
        $('.event>.bubble',$target).css({width: o.contentWidth});
        $('.event .children .detail',$target).css({left: o.titleWidth});
        var minWidth = parseInt(o.titleWidth)+parseInt(o.contentWidth);
        var width = this.element.style.width || $(this.element).width();
        if(!width || width < minWidth) {
            $target.css({ width: minWidth });
        }
    }

    function createSly() {
        var o = this.option;
        if(o.horizontal) {
            $(this.element).sly({  horizontal: 1,
                itemNav: false, smart: 1, mouseDragging: 1, touchDragging: 1, releaseSwing: 1, scrollBy: 100,
                activatePageOn: 'click', speed: 300, elasticBounds: 1, easing: 'easeOutExpo', dragHandle: 1,
                dynamicHandle: 1, clickBar: 1,prevPage: o.prevPage, nextPage: o.nextPage
            });
        } else {
            $(this.element).sly({  mouseDragging: 1, touchDragging: 1, releaseSwing: 1,
                startAt: 0, scrollBy: 100, speed: 300, elasticBounds: 1, easing: 'easeOutExpo', dragHandle: 1,within: false,
                // 从最后一次滚轮事件到外部滚动条劫持时间，设置为0，防止滚动条滚动到顶端及底部时发生卡顿现象
                scrollHijack: 0, itemNav: false,prevPage: o.prevPage, nextPage: o.nextPage
            });
        }

        this.slyScroll = Sly.getInstance(this.element);
    }

    function changeExpendStatus(status) {
        var $li = $(this);
        var isOpen = (status && Util.isStrEqualsIgnorecase('open', status)) || !$li.hasClass('tl-open');
        if(isOpen) {
            $li.removeClass('tl-close').addClass('tl-open');
        } else {
            $li.removeClass('tl-open').addClass('tl-close');
        }
    }
    function resizeWidth() {
        var $wrapUl = $('>ul',this.element);
        var ulWidth = 0;
        // 设置足够的宽度，使得li内的内容不会折行，从而让Sly插件能够正确识别li的宽度
        $wrapUl.width('9999999px' );
        // 当IE8时，Sly会对ul设置position:absolute，导致无法显示出内容
        if(Util.isIE(8)) {
            $wrapUl.css('position','relative');
        }
        $('>li',$wrapUl).each(function () {
            var liWidth = 0;
            $(this).children().each(function(){
                liWidth += $(this).width();
            });
            ulWidth += liWidth;
        });
        $wrapUl.width(ulWidth+'px' );
    }
    function bindEvents() {
        var $el = $(this.element);
        var self = this;

        resizeWidth.call(this);
        $el.sly('reload');
        $el.off('click.event').on('click.event', '.parent>.event-title .title-wrap, .parent>.bubble .switch', function() {
            var $li = $(this).closest('.parent');
            changeExpendStatus.call($li);
            resizeWidth.call(self);
            $el.sly('reload');
        }).off('click.content').on('click.content','.children .event-content', function() {
            var $li = $(this).closest('li.event');
            $('.children li.event',$el).removeClass('current');
            $li.addClass('current');
            changeExpendStatus.call($li);
            $('.children .detail>li', $el).not($li.find('.detail>li')).removeClass('current');
            $el.trigger('contentClick');
            resizeWidth.call(self);
            $el.sly('reload');
        }).off('click.detail').on('click.detail','.children .detail>li', function () {
            $('.children .detail>li', $el).removeClass('current');
            $(this).addClass('current');
            $('.children li.event',$el).removeClass('current');
            $(this).closest('li.event').addClass('current');
            var cacheData = $('.detail-content',this).attr('data-cache');
            $el.trigger('detailClick', [this,cacheData]);
        });
    }
    function TimeLine (element, options) {
        // Extend options
        var o = $.extend({}, TimeLine.defaults, options);
        if(o.horizontal) {
            $.extend(o, {
                titleWidth: 60,
                contentWidth: 70
            })
        }
        this.element = element;
        this.option = o;
        this.init();
    }
    TimeLine.prototype.init = function () {
        if(this.initialized) return;
        createHtml.call(this);
        createSly.call(this);
        bindEvents.call(this);
        TimeLine.storeInstance(this.element, this);
        this.initialized = 1;
    };
    TimeLine.prototype.locate = function (item, isExpend) {
        var $element = $(this.element);
        var timeline = this.slyScroll;
        var itemPos = timeline.getPos(item);
        if(itemPos && isExpend) {
            //changeExpendStatus.call(itemPos.el, 'open');
            changeExpendStatus.call(item, 'open');
        }
        if(itemPos)
            timeline.slideTo(itemPos['start'], undefined, true, true);
    };

    TimeLine.defaults = {
        titleWidth: 70,
        contentWidth: 300
    };
    TimeLine.getInstance = function (element) {
        return $.data(element, namespace);
    };
    TimeLine.storeInstance = function (element, timeline) {
        return $.data(element, namespace, timeline);
    };
    TimeLine.removeInstance = function (element) {
        var sly = Sly.getInstance(element);
        if(sly) {
            sly.destroy();
        }
        return $.removeData(element, namespace);
    };
    $.fn[pluginName] = function (options) {
        var element = this[0];
        var instance = TimeLine.getInstance(element);
        if(!instance) {
            new TimeLine(element, options);
        }
    };
    $.TimeLine = TimeLine;
})(jQuery, window);