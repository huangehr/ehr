/**
 * Created by yezehua on 2015/7/23.
 */
define(function (require, exports, module) {

    return {
        isString: function (str){
            return (typeof str=='string')&&str.constructor==String;
        },
        // 格式化字符串，使用方式类似java中的String.format()方法
        format: function(format){
            var args = [];
            for(var i=1; i<arguments.length; i++){
                args.push(arguments[i]);
            }

            return format.replace(/\{(\d+)\}/g, function(m, i){
                return args[i];
            });
        },
        /**
         * 去掉字符串前后的空格
         * @param {String} str 要去掉空格的字符串
         * @return {String} 处理后的字符串
         */
        trimAll: function (str){
            return str.replace(/(^\s*)|(\s*$)/g, '');

        },
        /**
         * 去掉字符串前的空格
         * @param {String} str 要去掉空格的字符串
         * @return {String} 处理后的字符串
         */
        trimLeft: function (str){
            return str.replace(/^\s*/g,'');
        },
        /**
         * 去掉字符串后的空格
         * @param {String} str 要去掉空格的字符串
         * @return {String} 处理后的字符串
         */
        trimRight: function (str){
            return str.replace(/\s*$/,'');
        },
        // 判断字符串是否相等
        isStrEquals: function(str1,str2){
            if(str1==str2){
                return true;
            }
            return false;
        },
        // 忽略大小写判断字符串是否相同
        isStrEqualsIgnorecase: function(str1,str2){
            if(str1.toUpperCase() == str2.toUpperCase())
            {
                return true;
            }
            return false;
        },
        // 判断字符串是否为空
        isStrEmpty: function(str){
            return str == null || !str || typeof str == undefined || str == '';
        },
        // 判断是否是数字
        isNum: function(value){
            if( value != null && value.length>0 && isNaN(value) == false){
                return true;
            }
            else{
                return false;
            }

        },
        // 判断是否是中文
        isChinese: function(str){
            var reg = /^([\u4E00-\u9FA5]|[\uFE30-\uFFA0])*$/;
            if(reg.test(str)){
                return false;
            }
            return true;
        },
        // 判断是否为对象
        isObject: function (o){
            return (typeof o=='object')&&o.constructor==Object;
        },
        // 判断是否为日期类型
        isDate: function (o){
            return (typeof o=='object')&&o.constructor==Date;
        },
        // 判断是否为函数
        isFunction: function (o){
            return (typeof o=='function')&&o.constructor==Function;
        },
        isArray: function (o){
            return (typeof o=='object')&&o.constructor==Array;
        },
        //检测对象是否是空对象(不包含任何可读属性)。方法既检测对象本身的属性，也检测从原型继承的属性。
        isObjEmpty: function (o) {
            for (var name in o)
            {
                return false;
            }
            return true;
        },
        // 检测对象是否是空对象(不包含任何可读属性)。方法只既检测对象本身的属性，不检测从原型继承的属性。
        isObjOwnEmpty: function(o)
        {
            for(var name in o)
            {
                if(o.hasOwnProperty(name))
                {
                    return false;
                }
            }
            return true;
        },
        // 将传入值转换成整数
        parseInteger: function(v){
            if(typeof v == 'number'){
                return v;
            }else if(typeof v == 'string'){
                var ret = parseInt(v);

                if(isNaN(ret) || !isFinite(ret)){
                    return 0;
                }

                return ret;
            }else{
                return 0;
            }
        },
        //将传入值转换成小数
        parseFloat: function(v){
            if(typeof v == 'number'){
                return v;
            }else if(typeof v == 'string'){
                var ret = parseFloat(v);
                if(isNaN(ret) || !isFinite(ret)){
                    return 0;
                }

                return ret;
            }else{
                return 0;
            }
        },
        // 将传入值转换成小数,传入值可以是以逗号(,)分隔的数字，此方法将会过滤掉(,)
        parseDotFloat: function(v){
            if(typeof v == 'number'){
                return v;
            }else if(typeof v == 'string'){
                v = v.replace(/[^\d|.]/g , '');
                v = parseFloat(v);

                if(isNan(v) || !isFinite(v)){
                    return 0
                }
                return ret;
            }else{
                return 0;
            }
        },
        /**
         * 字符串传换成date类型
         * @params: {string} str 字符串格式表示的日期，格式为：yyyy-mm-dd
         * @return {Date}由str转换得到的Date对象
         * */
        str2date: function(str){
            var   re   =   /^(\d{4})\S(\d{1,2})\S(\d{1,2})$/;
            var   dt;
            if(re.test(str)){
                dt   =   new   Date(RegExp.$1,RegExp.$2   -   1,RegExp.$3);
            }
            return dt;
        },
        /**
         * 计算2个日期之间的天数
         *
         * @params {Date} day1 起始日期
         *        {Date} day2 结束日期
         * @return day2 - day1的天数差
         * */
        dayMinus: function(day1, day2){
            var days = Math.floor((day2-day1)/(1000 * 60 * 60 * 24));
            return days;
        },
        // 获取年
        getYear: function(){
            var year = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                year = dateTime.getFullYear();
            }else{
                year = this.curDateTime.getFullYear();
            }
            return year;
        },
        //获取月
        getMonth: function(){
            var month = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                month = dateTime.getMonth() + 1;
            }else{
                month = this.curDateTime.getMonth() + 1;
            }
            return month;
        },
        //获取天
        getDay: function(){
            var day = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                day = dateTime.getDate();
            }else{
                day = this.curDateTime.getDate();
            }
            return day;

        },
        // 获取小时
        getHour: function(){
            var hour = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                hour = dateTime.getHours();
            }else{
                hour = this.curDateTime.getHours();
            }
            return hour;
        },
        // 获取分钟
        getMinute: function(){
            var minute = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                minute = dateTime.getMinutes();
            }else{
                minute = this.curDateTime.getMinutes();
            }
            return minute;
        },
        // 获取秒
        getSecond: function(){
            var second = null;
            var dateTime = this.getDateTime();
            if(dateTime != null){
                second = dateTime.getSeconds();
            }else{
                second = this.curDateTime.getSeconds();
            }
            return second;
        },
        // 判断是否是闰年
        isLeapYear: function(){
            var flag = false;
            if((this.getYear() % 4 == 0 && this.getYear() % 100 !=0)
                || (this.getYear() % 400 == 0)){
                flag = true;
            }
            return flag;
        },
        /**
         * GUID(Globally Unique Identifier 全局统一标识)生成器
         *
         * @param: {Object} options
         *   options.strFormat: {String} 指定生成的字符串格式（例如： ''xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'）
         *   options.len: {Int} 指定生成字符串的长度
         *   option.radix: {Int} 指定生成字符串的字符基数
         * */
        guid: function (options) {

            options = options || {};

            if(!this.isStrEmpty(options.strFormat)){

                if(!this.isString(options.strFormat)) throw Error('options.strFormat is not String type');

                return options.strFormat.replace(/[xy]/g, function(c) {
                    var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
                    return v.toString(16);
                });
            } else {

                var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('');
                var uuid = [], i;
                var radix = options.radix || chars.length;
                var len = options.len;

                if (len) {
                    // Compact form
                    for (i = 0; i < len; i++) uuid[i] = chars[0 | Math.random()*radix];
                } else {
                    // rfc4122, version 4 form
                    var r;

                    // rfc4122 requires these characters
                    uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
                    uuid[14] = '4';

                    // Fill in random data.  At i==19 set the high bits of clock sequence as
                    // per rfc4122, sec. 4.1.5
                    for (i = 0; i < 36; i++) {
                        if (!uuid[i]) {
                            r = 0 | Math.random()*16;
                            uuid[i] = chars[(i == 19) ? (r & 0x3) | 0x8 : r];
                        }
                    }
                }

                return uuid.join('');
            }
        },
        /**
         * 判断IE及版本
         *
         * @param: {Int} v IE版本（缺省时只判断是否为IE，不考虑其版本）
         * @param: {String} m 匹配模式（缺省时为'EQ',即与判断版本是否参数v相等）
         * */
        isIE: function(v,m){

            var ua=navigator.userAgent.toLowerCase();
            var isIE = ua.match(/msie/) != null || ua.match(/trident/) != null;
            if(this.isStrEmpty(v) || !isIE){
                return isIE;
            } else if(isIE){
                var ver= ua.match(/msie ([\d.]+)/) != null ? ua.match(/msie ([\d.]+)/)[1] : ua.match(/rv:([\d.]+)/)[1];
                var self = this;

                return (function(){
                    if(self.isStrEmpty(m) || self.isStrEqualsIgnorecase(m,'EQ')){
                        return self.isStrEquals(v,ver);
                    } else if(self.isStrEqualsIgnorecase(m,'GT')){
                        return ver>v;
                    } else if(self.isStrEqualsIgnorecase(m,'LT')){
                        return ver<v;
                    } else if(self.isStrEqualsIgnorecase(m,'GTE')){
                        return ver>=v;
                    } else if(self.isStrEqualsIgnorecase(m,'LTE')) {
                        return ver<=v;
                    }
                })();
            }
        }

    };
})