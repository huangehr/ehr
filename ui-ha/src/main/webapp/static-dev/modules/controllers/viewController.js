/*！
 * ViewController -v1.0.0 (2015/7/27)
 *
 * @description: 视图控制器
 *
 *
 *      使用它对页面的视图元素进行缓存，事件绑定。
 *
 * @usage:
 * html:
 *      <form id="form">
 *        <input type="text" name="name" value="ye">
 *        <input type="text" name="name" value="ze">
 *      </form>
 * js:
 *      var ViewController = require('viewController');
 *      new ViewController('#form',{
 *           events: {
 *               'click input': 'inputFun', // 事件触发时，回调函数名称，须与下面includes的属性名inputFun一致
 *                'mouseover': 'clickFun'
 *           },
 *           handlers: {
 *              'inputFun': function(){
 *                   alert('input');
 *               }
 *           }
 *       });
 *
 *       new ViewController('#form',{
 *           events: {
 *               'click input': {
 *                   handler: 'inputFun', // 事件触发时，回调函数名称，须与下面includes的属性名inputFun一致
 *                   data: [1,2,3] // 触发事件时，需要通过event.data传递给事件处理函数的任意数据
 *               }
 *           },
 *           handlers: {
 *               'inputFun': function(e){
 *                   alert(e.data[0]);
 *               }
 *           }
 *       });
 *
 * @author:      yezehua
 *
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    var $ = require('jquery');
    var Controller = require('controller');
    var Util = require('utils');

    return Controller.create({
        // 存放选择器与本地变量的映射
        elements: {
            // "选择器": "本地变量名"，如： "form.searchForm": "searchForm"
        },
        // 所有事件名称、选择器和回调的映射
        events: {
            // "事件名称 选择器": 回调函数名
        },
        // 根据第1个空格来分割出事件和选择器
        eventSplitter: /^(\w+)\s*(.*)$/,
        init: function (element,opts) {

            this.el = $(element);

            if(opts) {

                this.elements = opts.elements || {};
                this.events = opts.events || {};

                $.extend(this,opts.handlers || {});
            }

            this.refreshElements();
            this.delegateEvents();
        },
        $: function (selector) {
            // 需要一个‘el’属性，同时传入选择器
            return $(selector,this.el);
        },
        // 设置本地变量
        refreshElements: function(){
            for(var key in this.elements){

                this[this.elements[key]] = this.$(key);
            }
        },
        delegateEvents: function(){
            for(var key in this.events){

                var methodName = this.events[key], methodObj = methodName;
                var match      = key.match(this.eventSplitter);
                var eventName  = match[1], selector = match[2];
                var method     = null, data = null;

                if(Util.isObject(methodObj) && !Util.isObjEmpty(methodObj)) {
                    methodName = methodObj.handler;
                    // 触发事件时，需要通过event.data传递给事件处理函数的任意数据
                    data       = methodObj.data;
                }
                if(Util.isString(methodName) &&$.isFunction(this[methodName])) {
                    method     = this.proxy(this[methodName]);
                    // 如果没有提供事件选择器，则将事件绑定在el上，否则事件将委托给el
                    if(selector === ''){
                        this.el.bind(eventName,data, method);
                    } else {
                        this.el.delegate(selector, eventName,data, method);
                    }
                }

            }
        },
        undelegateEvent: function(event, removeHandler){

            var match      = event.match(this.eventSplitter);
            var eventName  = match[1], selector = match[2];
            if(selector === ''){
                this.el.unbind(eventName);
            } else {
                this.el.undelegate(selector, eventName);
            }
            removeHandler = removeHandler || true;
            var e = this.events[event];
            // 删除this.events保存的事件处理函数
            if(removeHandler)  Util.isObject(e)? delete this[e.handler] : delete this[e];
        }
    });
})