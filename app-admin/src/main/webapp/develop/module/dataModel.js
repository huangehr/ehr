/**
 * DataModel -v1.1.0 (2015/11/17)
 * @namespace DataModel
 * @requires jQuery
 * @requires $.Util
 * @requires $.AjaxUtil
 * @requires $.BaseObject
 * @description 数据模型对象
 *
 *      该对象（类）继承于BaseObject，是BaseObject的子类。
 *
 * @example
 *      var dataModel = $.DataModel.init(); // 获取普通数据模型实例对象
 *      // 请求查询数据
 *      dataModel.fetchRemote("https://localhost:8443/ha/dict/searchSysDicts",
 *      {data:{searchNm:'',page: 1, rows: 10}, success: function(data) {}});
 *      // 请求创建数据
 *      dataModel.createRemote("https://localhost:8443/ha/dict/createDict", {data: {name:'qwreqw', reference: ''}, success: function(data) {}});
 *      // 请求更新数据（修改、删除）
 *      dataModel.updateRemote("https://localhost:8443/ha/dict/deleteDict", {data: {dictId:'123'}, success: function(data) {}} )
 * @example
 *      var BusinessDataModel = DataModel.create(); // 调用其create方法可以创建，具体的数据模型类。
 *      var businessDataModel = BusinessDataModel.init(); // 获取实例对象
 *      businessDataModel.fetchRemote('http://localhost:8080/health/hello'); // 加载远程数据
 *
 * @author      yezehua
 *
 * @copyright   2015 www.yihu.com
 */

(function ($, win) {

    var Util = $.Util;

    var ajax = $.AjaxUtil.ajax;

    var DataModel = $.DataModel = $.BaseObject.create();

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
        /**
         * 是否缓存远程数据
         * @memberOf DataModel
         * @instance
         */
        cacheData: true,
        /**
         * 缓存远程数据对象
         * @memberOf DataModel
         * @instance
         */
        data: {}, // 缓存远程数据对象
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

        /**
         * 请求查询数据
         * @param {String} url - 请求的路径
         * @param {Object} options - 请求的相关配置，用法参照jQuery.ajax(options)
         * @example
         * var dataModel = $.DataModel.init();
         * dataModel.fetchRemote("https://localhost:8443/ha/dict/searchSysDicts",
         *      {data:{searchNm:'',page: 1, rows: 10}, success: function(data) {}});
         * @memberOf DataModel
         * @instance
         */
        fetchRemote: function (url,options) {

            ajax.call(this,url,options);
        },
        /**
         * 请求查询数据
         * @param {String} url - 请求的路径
         * @param {Object} options - 请求的相关配置，用法参照jQuery.ajax(options)
         * @example
         * var dataModel = $.DataModel.init();
         * dataModel.createRemote("https://localhost:8443/ha/dict/createDict", {data:{name:'qwreqw', reference: ''}, success: function(data) {}});
         * @memberOf DataModel
         * @instance
         */
        createRemote: function (url,options) {
            //options = $.extend({}, {type: 'POST'}, options);
            ajax.call(this,url,options);
        },
        /**
         * 请求查询数据
         * @param {String} url - 请求的路径
         * @param {Object} options - 请求的相关配置，用法参照jQuery.ajax(options)
         * @example
         * var dataModel = $.DataModel.init();
         * dataModel.createRemote("https://localhost:8443/ha/dict/deleteDict", {data:{dictId:'123'}, success: function(data) {}});
         * @memberOf DataModel
         * @instance
         */
        updateRemote: function (url,options) {
            //options = $.extend({}, {type: 'DELETE'}, options);
            ajax.call(this,url,options);
        }

    });
})(jQuery, window);