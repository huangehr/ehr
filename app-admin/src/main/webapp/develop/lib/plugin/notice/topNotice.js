/**
 * topNotice -v1.0.0 (2015/11/14)
 * @namespace Notice
 * @requires jQuery
 * @requires $.publish
 * @description
 *  页面消息提示
 * @example
 *  $.Notice.success('提示内容');
 *
 * @author    yezehua
 * 
 * @copyright   2015 www.yihu.com
 */
(function ($, win) {

    var Util = $.Util;
    var Type = {
        SUCCESS: 'success',
        ERROR: 'error',
        WARN: 'warn',
        CONFIRM: 'confirm',
        WAITTING: 'waitting'
    };

    function getConfig(arg) {

        if(Util.isStrEquals(Type.WAITTING, arg[0])){
            arg[1] = Util.isString(arg[1])&& arg[1] || '正在处理中...'
        }
        return {
            type: arg[0],
            msg: arg[1],
            callback: Util.isFunction(arg[2])?arg[2]:null,
            options: arg[3]
        };
    }
    function openDialog(type,args) {
        var params = Array.prototype.slice.call(args);
        params.unshift(type);
        var opts = getConfig(params);
        return $.ligerDialog[opts.type](opts.msg,$.ligerDefaults.DialogString.titleMessage,opts.callback, opts.options);
    }
    $.Notice = {
        success: function () {
            return openDialog(Type.SUCCESS, arguments);
        },
        error: function () {
            return openDialog(Type.ERROR, arguments);
        },
        warn: function () {
            return openDialog(Type.WARN, arguments);
        },
        confirm: function () {
            return openDialog(Type.CONFIRM, arguments);
        },
        waitting: function () {
            var time = (arguments.length && arguments[arguments.length-1]) || null;
            var dialog = openDialog(Type.WAITTING, arguments);
            if(typeof time == 'number') {
                setTimeout(function () { dialog.close();}, time);
            } else if(typeof time == "boolean") {
                setTimeout(function () { dialog.close();}, 3000);
            }
            return dialog;
        }
    }
})(jQuery, window);