/*!
 * timepager -v1.0.0 (2015/12/14)
 * 
 * @description:
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */
(function ($,win) {
    var template = '<div class="m-wrap f-pr">' +
                        '<div class="m-base-page pre-page" > <span></span> </div>' +
                        '<div class="m-frame"> ' +
                            '<ul class="j-years clearfix"> ' +
                            '{@each years as year}' +
                                '<li>{{year}}年</li> ' +
                            '{@/each}' +
                            '</ul>' +
                        '</div>' +
                         '<div class="m-base-page next-page" > <span></span> </div>' +
                    '</div>';

    function getYears (n) {
        var date = new Date();
        var curYear = date.getFullYear();
        var n = n || 10;
        var years = [];
        for(var i=0; i<n; i++) {
            years.unshift(curYear-i);
        }

        return years;
    }
    $.fn.timepager = function () {
        var html = juicer(template, {years: getYears()});
        var $wrap = $(html);
        var $frame = $('.m-frame', $wrap);
        this.append($wrap);

        $frame.sly({
            horizontal: 1,
            itemNav: 'basic',
            smart: 1,
            activateOn: 'click',
            mouseDragging: 1,
            touchDragging: 1,
            releaseSwing: 1,
            startAt: 0,
            activatePageOn: 'click',
            speed: 300,
            easing: 'easeOutExpo',
            prevPage: $wrap.find('.pre-page'),
            nextPage: $wrap.find('.next-page')
        });
    };
})(jQuery, window);