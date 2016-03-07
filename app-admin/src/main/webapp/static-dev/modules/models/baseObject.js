/*!
 * BaseObject -v1.0.0 (2015/7/27)
 *
 * @description: 基对象（类）
 *
 *      该对象（类），定义了一些常用的方法，用于创建其子类对象。
 *
 * @author:      yezehua
 *
 * @copyright:   2015 www.yihu.com
 */
define(function (require, exports, module) {

    var $ = require('jquery');
    var util = require('utils');

    // 当浏览器不支持Object.create，则创建
    if(typeof Object.create != "function"){

        Object.create = function (o) {

            function F(){}
            F.prototype = o;

            return new F();
        }
    }

    var BaseObject = {

        // 继承BaseObject对象后回调
        inherited: function(){},
        // 创建对象后回调
        created: function(){},
        // 对象原型
        prototype:{
            init: function(){}
        },
        // 创建对象，这个对象继承自BaseObject（也就是创建了一个继承于BaseObject的子类）
        create: function(){

            var object = Object.create(this);
            object.parent = this;

            // 设置新对象的原型，该原型对象继承自BaseObject对象的原型
            object.prototype = object.fn = Object.create(this.prototype);

            object.created();
            this.inherited(object);

            return object;
        },
        // 初始化创建实例
        init: function () {

            var instance = Object.create(this.prototype);
            instance.parent = this;
            // 调用实例的init方法
            instance.init.apply(instance, arguments);

            return instance;
        },
        // 添加对象属性（子类的属性）
        extend: function(o){

            var extended = o.extended;
            $.extend(this, o);
            if(extended){   extended(this); }   // 添加对象属性后回调
        },
        // 添加实例属性
        include: function(o){

            var included = o.included;
            $.extend(this.prototype, o);
            if(included){   included(this); }    // 添加实例属性后回调
        }
    };

    return BaseObject;
});