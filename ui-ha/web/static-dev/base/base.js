/**
 * Created by yezehua on 2015/7/21.
 *
 * @description:
 *      统一引入常用框架及模块
 */

define(function(require, exports, module) {

    var $     = require('jquery');//引入jQuery框架
    var context = {};

    //require('bootstrap'); //引入boostrap框架Js

    //require('bootstrapCss'); //引入boostrap框架Css

    //var Util = require('utils');

    /*
    // 增加对IE低版本(IE9以下)的支持
    if(Util.isIE(9,'LT')){

        //让IE9以下的IE支持HTML5元素
        require('extra/html5shiv');
        //让IE9以下的IE支持css3媒体查询语句(min-width,max-width)
        require.async('extra/respond');
        //让IE9以下的IE支持css3的伪类以及属性选择器
        require.async('extra/selectivizr');

    }
    */

    //require('pubsub'); // 引入'发布/订阅者'模式模块

    //require('aop');


    exports.$ = $;
    exports.context = context;
});