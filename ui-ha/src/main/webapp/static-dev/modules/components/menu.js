/**
 * Created by yezehua on 2015/7/23.
 */

define(function (require, exports, module) {

    var $        = require('jquery'),
        juicer    = require('juicer'),
        Util      = require('utils'),
        Component = require('component'),
        ViewController = require('viewController');

    require('modules/components/menu.css');

    var tpl = '<ul class="u-menu f-toe" style="width: {$$opts.width}; height: {$$opts.height};">' +
                    '{@each opts.data as it,index}' +
                    '<li class="item {@if it.active===true}active{@/if}" style="height: {$$opts.itemHeight}; line-height: {$$opts.itemHeight}; width: {$$opts.itemWidth};">' +
                    '<div class="J-content content" style="display: inline-block;">' +
                        '<span class="J-icon item-icon {$$it.ico}" style="float: left; width: {$$opts.iconWidth}; height: {$$opts.itemHeight};"></span>' +
                        '<a class="J-title {$$opts.color}" href="${{it.href}}"> {$$it.title}</a> </div>' +
                    '</li>' +
                     '{@/each}' +
                '</ul>';

        return Component.create({

            tpl: tpl,
            init: function (options) {

                // 设置初始化参数
                this.options = $.extend({
                    target: document.body,
                    orientation: 'H', // 菜单项排列方向，默认为水平方向
                    height: '40px', // 菜单条高度
                    width: '100%', // 菜单条宽度
                    'padding-top': '0', // 菜单条内边距
                    itemHeight: '40px', // 菜单项高度
                    itemWidth: '100px', // 菜单项宽度
                    iconWidth: '40px', // 菜单项图标宽度
                    activeClass: null, // 菜单项激活样式
                    data: [], // {ico: '',title: ''} // 菜单项内容，包括图标，标题
                    clicked: null
                },options||{});

                // 目标容器
                this.target = $(this.options.target);

                return this;
            },
            getMenu: function () {
                return $('.u-menu',this.target).eq(0);
            },
            getMenuItems: function () {
                return  $('.u-menu .item',this.target);
            },
            activeItem: function (o) {
                this.getMenuItems().removeClass(this.options.activeClass);
                o.addClass(this.options.activeClass);
            },
            handler: function () {
                var opts   = this.options,
                    target = opts.target,
                    self    = this;

                new ViewController(target,{
                    events: {'click .item': 'itemClick'
                    },
                    handlers: {
                        itemClick: function (e) {
                            self.activeItem($(e.currentTarget));
                            Util.isFunction(opts.clicked) && opts.clicked.call(e.currentTarget,arguments);
                        }
                    }
                });

            },
            render: function () {
                var opts = this.options,
                    html = juicer(this.tpl,{opts: opts}),
                    target = this.target;

                // 先设置生成的菜单透明化，因为后面需要根据里面的内容进行计算调整样式
                var menu = $(html).css('opacity',0);
                target.append(menu);

                var height     = Util.parseInteger(opts.height),  // 菜单条高度
                    width      = Util.parseInteger(opts.width),  // 菜单条宽度
                    itemHeight = Util.parseInteger(opts.itemHeight), // 菜单项高度
                    itemWidth  = Util.parseInteger(opts.itemWidth), // 菜单项宽度
                    iconWidth  = Util.parseInteger(opts.iconWidth); // 菜单项图标宽度

                // 计算并设置菜单项左右边距，使其内容水平居中
                var itemPadding = (itemWidth - iconWidth - $('.J-title',target).width()) / 2;
                $('.J-content',target).css({padding: '0 '+ itemPadding+'px'});


                if(Util.isStrEqualsIgnorecase('H',opts.orientation)) {

                    // 设置菜单条上下内边距， 使菜单项竖直居中
                    var padding = (height - itemHeight) / 2;
                    menu.css({'padding-top': padding + 'px'});
                    this.getMenuItems().addClass('f-ib');
                } else {
                    this.getMenuItems().css({width: width+'px'});
                    this.getMenu().addClass('vertical');
                }
                this.handler();

                menu.css('opacity', 1);
            }
        });
})
