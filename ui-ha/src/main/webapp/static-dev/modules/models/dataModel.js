/*!
 * DataModel -v1.0.0 (2015/7/27)
 *
 * @description: 数据模型对象
 *
 *      该对象（类）继承于BaseModel，是BaseModel的子类。
 *
 * @usage:
 *      var BusinessDataModel = require('dataModel').create(); // 调用其create方法可以创建，具体的数据模型类。
 *      var businessDataModel = BusinessDataModel.init(); // 获取实例对象
 *      businessDataModel.fetchRemote('http://localhost:8080/health/hello'); // 加载远程数据
 *
 * @author:      yezehua
 *
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    var $ = require('jquery');
    var BaseModel = require('baseModel');
    var Util = require('utils');
    var ajax = require('ajax').ajax;

    var DataModel = BaseModel.create();

    DataModel.extend({ // 添加对象属性
        cacheInstance: false,
        created: function () {

            // 用来缓存实例对象
            this.instances = {};

            // 用来保存哪些属性需要序列化
            this.attributes = [];
        },
        find: function (id) {

            var record = this.instances[id];
            if(!record) throw('Unknown instance(id:'+id+')');
            return record.dup();
        }

    });
    DataModel.include({ // 添加实例属性
        newInstance: true,
        cacheData: true, // 是否缓存远程数据
        data: null, // 缓存远程数据对象
        attributes: function() {

            var result = {};
            for(var i in this.parent.attributes){

                var attr = this.parent.attributes[i];
                result[attr] = this[attr];
            }

            return result;
        },
        // 覆盖JSON.stringify所需要的toJSON方法
        toJSON: function () {
            return (this.attributes());
        },
        create: function () {

            this.newInstance = false;
            this.parent.cacheInstance && (this.parent.instances[this.id] = this.dup());
        },
        init: function (id) {

            if(id) {
                this.id = id;
            } else {
                this.id = this.id || Util.guid('xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx');
            }
        },
        update: function () {

            this.parent.cacheInstance && (this.parent.instances[this.id] = this.dup());
        },
        save: function () {

            this.newInstance? this.create() : this.update();
        },
        destroy: function () {

            if(this.parent.cacheInstance){ delete this.parent.instances[this.id]; }
        },
        // 返回当前实例的一份副本
        dup: function () {

            return $.extend(true,{},this);
        },
        // 加载远程数据，options的用法参照jQuery.ajax(options)
        fetchRemote: function (url,options) {

            ajax.call(this,url,options);
        },
        createRemote: function (url,options) {
            options = $.extend({}, {type: 'POST'}, options);
            ajax.call(this,url,options);
        },
        updateRemote: function (url,options) {
            options = $.extend({}, {type: 'DELETE'}, options);
            ajax.call(this,url,options);
        }

    });

    return DataModel;
})