/**
 * attrscan -v1.0.0 (2015/10/25)
 *
 * @namespace $Form
 * @requires jQuery
 * @requires $.Util
 * @description 为form（jQuery对象）添加对具有特征属性data-attr-scan的节点进行设值与取值的方法
 * @author yezehua
 * @copyright 2015 www.yihu.com
 */
(function ($, win) {

    var Util = $.Util;

    /**
     * 为特征节点添加访问值的getter及setter方法
     * @private
     * @param {jQuery} $el
     * @memberOf $Form
     */
    function extendAccessMethods($el) {
        var tagName = $el[0].tagName,
            type =  $el.attr('data-type') || $el[0].type,
            isRadio = Util.isStrEqualsIgnorecase('radio', type),
            isCheckbox = Util.isStrEqualsIgnorecase('checkbox', type),
            isSelect = Util.isStrEqualsIgnorecase('select',type),
            isComboSelect = Util.isStrEqualsIgnorecase('comboSelect',type)
        if(Util.isStrEqualsIgnorecase('input',tagName) && (isRadio || isCheckbox)) {
            $el.setValue = function (value) {
                var arr = [].concat(value);
                $el.each(function () {
                    $checkedStatus = $(this).closest('.l-'+type+'-wrapper').find('a.l-'+type);
                    if(Util.isContains(arr,$(this).val())) {
                        this.checked = true;
                        if(!$checkedStatus.hasClass('l-'+type+'-checked')) {
                            $(this).closest('.l-'+type+'-wrapper').find('a.l-'+type).trigger('click');
                        }
                    } else {
                        this.checked = false;
                        if($checkedStatus.hasClass('l-'+type+'-checked')) {
                            $(this).closest('.l-'+type+'-wrapper').find('a.l-'+type).trigger('click');
                        }
                    }
                });
            };

            $el.getValue = function () {
                var values = [];
                $el.each(function () {
                    if(this.checked) {
                        values.push(this.value);
                    }
                });
                if(isRadio) return values[0] || null;
                return values;
            };
        } else if(isSelect) {
            $el.setValue = function (value) {
                $el.ligerGetComboBoxManager().setValue(value);
            };
            $el.getValue = function () {
                return $el.ligerGetComboBoxManager().getValue();
            }
        } else if(isComboSelect) {
            $el.setValue = function (value) {
                var $combo = $.data($el[0],'combo');
                return $combo.setValue(value);
            };
            $el.getValue = function () {
                var value = $el.val();
                //return value?value.split(','):[];
                return value?JSON.parse(value):{keys:[],names:[]};
            }
        } else {
            $el.setValue = function (value) {
                $el.val && $el.val(value);
                $el.trigger("change");
            };
            $el.getValue = function () {
                return $el.val && $el.val();
            };
        }
    }

    /**
     * 扫描form中具有特征属性data-attr-scan的节点
     * @private
     * @param {jQuery} $forms
     * @memberOf $Form
     */
    function scan($forms) {
        $forms.each(function () {
            var $form = $(this),
                // 特征节点集
                fields = $('[data-attr-scan]',  $form),
                // 缓存表单内所有特征节点对象
                propMap = $.data(this,'propMap') || {},
                // 缓存当前表单内所有监控属性名
                props = [];

            fields.each(function() {
                var target = $(this),
                    propName = target.attr('data-attr-scan') || target.attr('name'),
                    tagName = this.tagName,
                    type = this.type;
                if(propName && !propMap[propName]) {
                    if(Util.isStrEqualsIgnorecase('input',tagName) &&
                        (Util.isStrEqualsIgnorecase('radio', type) || Util.isStrEqualsIgnorecase('checkbox', type))) {
                        var target = $('input[type="'+type+'"][data-attr-scan="'+propName+'"]');
                        if(!target.length) {
                            target = $('input[type="'+type+'"][name="'+propName+'"]');
                        }
                        // 添加tagName及type属性,以便extendAccessMethods方法判断类型
                        target.tagName = 'input';
                        target.type = type;
                    }
                    propMap[propName] = target;
                    extendAccessMethods(target);
                }
                props.push(propName);
            });

            $.data(this,'propMap', propMap);
            for(var name in propMap) {
                // 如果propMap缓存的特征节点对应的键值不在当前表单监控的特征属性名中，则删除
                if(!Util.isContains(props,name)) {
                    propMap[name] = null;
                    delete propMap[name];
                }
            }
        });
    }

    /**
     * 将对象属性扁平化，例如：{user: {name: '张三'}} => {"user.name": '张三'}
     * @private
     * @param {Object} o - 要转换的对象
     * @param {String} ns - 该对象所属的名字空间
     * @returns {Object} 扁平化的属性对象
     * @memberOf $Form
     */
    function parseObjAttr(o,ns) {
        var propMap = {};
        if(Util.isObject(o)) {
           for(var i in o) {
               var prop = ns?(ns+'.'+i): i;
               if(o[i] && Util.isObject(o[i])) {
                   $.extend(propMap,parseObjAttr(o[i],prop));
               } else {
                   propMap[prop] = o[i];
               }
           }
        }
        return propMap;
    }
    /**
     * 扫描form中具有特征属性data-attr-scan的节点，并使用$.data(form,'propMap')进行缓存
     * @example
     * $form.attrscan()
     * @memberOf $Form
     */
    function attrscan() {
        if(!Util.isStrEqualsIgnorecase('form', this[0].tagName)&&this.attr('data-role-form')===undefined) {
            window.console && window.console.warn('The attrScan method should belong to a jQuery object which must be a form!');
            return;
        }
        // 扫描表单内特征节点
        scan(this);
        // 存储特征节点的映射对象，该对象的数据结构是以特征属性值（或者节点的name属性值）为对象属性，以节点的jQuery对象为值
        this.Fields = {};
        // 将缓存的propMap信息添加到表单的Fields对象中
        $.extend(this.Fields, $.data(this[0],'propMap'));
        // Fields对象添加一次性为表单特征节点赋值的fillValues方法，data参数为json结构的数据对象
        this.Fields.fillValues = function (data) {
            fillValues.call(this,data);
        };
        // Fields对象添加获取所有监控的表单数据
        this.Fields.getValues = function () {
            return getValues.call(this);
        };
        // 为Fields添加序列化为json字符串的方法
        this.Fields.toJsonString = toJsonString;
        this.Fields.toSerializedString = toSerializedString;
        this.Fields.reset = reset;
    }


    /**
     * @namespace $Form.Fields
     * @description 存储特征节点的映射对象。
     */

    /**
     * 根据data(json数据)为表单内指定的特征节点赋值
     * @example
     * $form.Fields.fillValues({
     *      userName: '张三', // userName 为指定的特征节点属性data-attr-scan的值或者name值
     *      age: 25
     * });
     * @param data - 数据对象,该对象的数据结构是以特征属性值（或者节点的name属性值）为对象属性，以节点的jQuery对象为值
     * @memberOf $Form.Fields
     */
    function fillValues(data) {
        var attrMap = parseObjAttr(data);
        for(var prop in attrMap) {
            this[prop] && this[prop].setValue(attrMap[prop]);
        }
    }
    /**
     * 获取$form.Fields中的所有输入值
     * @example
     * $form.Fields.getValues()
     * @returns {Object} 值对象
     * @memberOf $Form.Fields
     */
    function getValues() {
        var data = {};
        for(var key in this) {
            var names = key.split("."),// 用"."分隔出属性字段
                temp = null; // 临时保存循环对象
            // 如果特征属性值中不含有"."，则直接赋值
            if(names.length==1 && !Util.isFunction(this[key])) {
                data[key] = this[key].getValue();
            }
            // 如果特征属性值中含有"."，则根据语法结构生成json对象
            else if(names.length>1 && !Util.isFunction(this[key])) {

                if(!data.hasOwnProperty(names[0])) {
                    temp = data[names[0]] = {};
                } else {
                    temp = data[names[0]];
                }
                // 对于非最后一个属性字段，都应赋值为对象
                for(var i=1; i< names.length-1; i++) {
                    var name = names[i];
                    if(name && !temp.hasOwnProperty(name)) {
                        temp[name] = {}
                    }
                    temp = temp[name];
                }
                // 最后一个属性字段赋值
                temp[names[names.length-1]] = this[key].getValue();
            }
        }

        return data;
    }
    /**
     * 将$form.Fields序列化为json字符串,但不包含其中的方法属性
     * @example
     * $form.Fields.toJsonString()
     * @returns {String} json字符串
     * @memberOf $Form.Fields
     */
    function toJsonString() {
        var data = this.getValues();
        return JSON.stringify(data);
    }

    /**
     * 将$form.Fields值转为表单序列化字符串（形如：key1=value1&key2=value...）,但不包含其中的方法属性
     * @example
     * $form.Fields.toSerializedString()
     * @returns {String} 表单序列化字符串
     * @memberOf $Form.Fields
     */
    function toSerializedString() {
        var data = [];
        for(var key in this) {
            if(!Util.isFunction(this[key])) {
                var values = this[key].getValue();
                if(values===null || values===undefined) continue;
                if(Util.isArray(values)) {
                    for(var i = 0; i < values.length; i++) {
                        data.push(Util.format("{0}={1}",key,values[i]));
                    }
                } else {
                    data.push(Util.format("{0}={1}",key,this[key].getValue()));
                }
            }
        }
        return data.join('&');
    }
    /**
     * 将$form.Fields中所有输入重置
     * @example
     * $form.Fields.reset()
     * @memberOf $Form.Fields
     */
    function reset() {
        for(var key in this) {
            var ele = this[key];
            if(!Util.isFunction(ele) && ele.setValue) {
                ele.setValue(null);
            }
        }
    }
    // 为form添加扫描具有特征属性data-attr-scan的节点方法
    $.fn.attrScan = attrscan;

})(jQuery, window);