/*!
 * vmf -v1.0.0 (2015/8/24)
 * 
 * @description:
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */
define(function (require, exports, module) {

    var DOC = window.document,
        $ = require('jquery');

    var rword = /[^, ]+/g //切割字符串为一个个小块，以空格或豆号分开它们，结合replace实现字符串的forEach

    function oneObject(array, val) {
        if (typeof array === "string") {
            array = array.match(rword) || []
        }
        var result = {},
            value = val !== void 0 ? val : 1
        for (var i = 0, n = array.length; i < n; i++) {
            result[array[i]] = value
        }
        return result;
    }


    var readyList = [],
        VMODELS: [];

    var vm = exports.VM = {

        ready: function (fn) {
            readyList.push(fn);
        },
        define: function (id, factory) {
            var $id = id.$id || id
            if (!$id) {
                console.log("warning: vm必须指定$id")
            }
            if (VMODELS[$id]) {
                console.log("warning: " + $id + " 已经存在于avalon.vmodels中")
            }
            if (typeof id === "object") {
                var model = modelFactory(id)
            } else {
                var scope = {
                    $watch: noop
                }
                factory(scope) //得到所有定义

                model = modelFactory(scope) //偷天换日，将scope换为model
                stopRepeatAssign = true
                factory(model)
                stopRepeatAssign = false
            }
            model.$id = $id
            return VMODELS[$id] = model
        },

        scan: function() {}

    };
});