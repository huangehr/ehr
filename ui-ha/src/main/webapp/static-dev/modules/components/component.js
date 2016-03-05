/*!
 * Component -v1.0.0 (2015/7/27)
 * 
 * @description: 该模块负责UI组件类创建
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    var $ = require('jquery');
    return {
        create: function(includes){

            var result = function(){
                this.init.apply(this, arguments);
            };

            result.fn = result.prototype;
            result.fn.init =  function(){};

            result.proxy = function (func) {
                return $.proxy(func, this);
            };
            result.fn.proxy = result.proxy;

            result.include = function(ob){
                $.extend(this.fn, ob);
            };
            result.extend = function (ob) {
                $.extend(this, ob);
            };

            if(includes){
                result.include(includes);
            }

            return result;
        }

    }
})