/**
 * jQuery ligerUI 1.3.2
 *
 * http://ligerui.com
 *
 * Author daomi 2015 [ gd_star@163.com ]
 *
 */
(function ($)
{
    $.fn.ligerTextBox = function ()
    {
        return $.ligerui.run.call(this, "ligerTextBox", arguments);
    };

    $.fn.ligerGetTextBoxManager = function ()
    {
        return $.ligerui.run.call(this, "ligerGetTextBoxManager", arguments);
    };

    $.ligerDefaults.TextBox = {
        onChangeValue: null,
        onMouseOver: null,
        onMouseOut: null,
        onBlur: null,
        onFocus: null,
        width: null,
        disabled: false,
        initSelect : false,
        value: null,     //初始化值 
        precision: 2,    //保留小数位(仅currency时有效)
        nullText: null,   //不能为空时的提示
        digits: false,     //是否限定为数字输入框
        number: false,    //是否限定为浮点数格式输入框
        currency: false,     //是否显示为货币形式
        readonly: false              //是否只读
    };


    $.ligerui.controls.TextBox = function (element, options)
    {
        $.ligerui.controls.TextBox.base.constructor.call(this, element, options);
    };

    $.ligerui.controls.TextBox.ligerExtend($.ligerui.controls.Input, {
        __getType: function ()
        {
            return 'TextBox'
        },
        __idPrev: function ()
        {
            return 'TextBox';
        },
        _init: function ()
        {
            $.ligerui.controls.TextBox.base._init.call(this);
            var g = this, p = this.options;
            if (!p.width)
            {
                p.width = $(g.element).width();
            }
            if ($(this.element).attr("readonly"))
            {
                p.readonly = true;
            } else if (p.readonly)
            {
                $(this.element).attr("readonly", true);
            }
        },
        _render: function ()
        {
            var g = this, p = this.options;
            g.inputText = $(this.element);
            //外层
            // TODO [代码片段002][增加‘j-text-wrapper’类用于表单验证失败时样式控制][yezeh]
            //g.wrapper = g.inputText.wrap('<div class="l-text"></div>').parent();
            g.wrapper = g.inputText.wrap('<div class="l-text j-text-wrapper"></div>').parent();
            // TODO [代码片段002][增加‘j-text-wrapper’类用于表单验证失败时样式控制][yezeh] 结束
            g.wrapper.append('<div class="l-text-l"></div><div class="l-text-r"></div>');
            if (!g.inputText.hasClass("l-text-field"))
                g.inputText.addClass("l-text-field");
            this._setEvent();
            if (p.digits || p.number || p.currency)
            {
                g.inputText.addClass("l-text-field-number");
            }
            g.set(p);
            g.formatValue();
        },
        destroy: function ()
        {
            var g = this;
            if (g.wrapper)
            {
                g.wrapper.remove();
            }
            g.options = null;
            liger.remove(this);
        },
        _getValue: function ()
        {
            var g = this, p = this.options;

            if (g.inputText.hasClass("l-text-field-null"))
            {
                return "";
            }
            if (p.digits || p.number || p.currency)
            {
                return g.parseNumber();
            }
            return g.inputText.val();
        },
        _setNullText: function ()
        {
            this.checkNotNull();
        },
        formatValue: function ()
        {
            var g = this, p = this.options;
            var v = g.inputText.val() || "";
            if (v == "") return "";
            if (p.currency)
            {
                g.inputText.val(currencyFormatter(v, p.precision));
            } else if(p.number && p.precision && v)
            {
                var value = parseFloat(g.inputText.val()).toFixed(p.precision);
                g.inputText.val(value);
            }
        },
        checkNotNull: function ()
        {
            var g = this, p = this.options;

            if (p.nullText && p.nullText != "null" && !p.disabled && !p.readonly)
            {
                if (!g.inputText.val())
                {
                    g.inputText.addClass("l-text-field-null").val(p.nullText);
                    return;
                }
            }
            g.inputText.removeClass("l-text-field-null");
        },
        _setEvent: function ()
        {
            var g = this, p = this.options;
            function validate()
            {
                var value = g.inputText.val();
                if (!value || value == "-") return true;

                var r = (p.digits ? /^-?\d+$/ : /^(-?\d+)(\.)?(\d+)?$/).test(value);
                return r;
            }
            function keyCheck()
            {
                if (!validate())
                {
                    g.inputText.val(g.parseNumber());
                }
            }
            if (p.digits || p.number || p.currency)
            {
                g.inputText.keyup(keyCheck).bind("paste", keyCheck);
            }
            g.inputText.bind('blur.textBox', function ()
            {
                g.trigger('blur');
                g.checkNotNull();
                g.formatValue();
                g.wrapper.removeClass("l-text-focus");
            }).bind('focus.textBox', function ()
            {
                if (p.readonly) return;
                g.trigger('focus');
                if (p.nullText)
                {
                    if ($(this).hasClass("l-text-field-null"))
                    {
                        $(this).removeClass("l-text-field-null").val("");
                    }
                }
                g.wrapper.addClass("l-text-focus");

                if (p.digits || p.number || p.currency)
                {
                    $(this).val(g.parseNumber());
                    if (p.initSelect)
                    {
                        setTimeout(function ()
                        {
                            g.inputText.select();
                        }, 150);
                    }
                }
            }).change(function () {
                    g.trigger('changeValue', [this.value]);
            });

            /*****************TODO开始（添加搜索按钮并注册回调事件）*******************/
            g.search = '<div class="l-trigger l-trigger-search"><div class="l-trigger-icon"></div></div>';
            if(p.isSearch){//要显示搜索按钮
                var $searchBtn = $(g.search).click(function(){
                    p.search && p.search.call(g.wrapper);
                });
                g.wrapper.append($searchBtn);
            }
            /*****************TODO结束*******************/

            /*****************TODO开始（添加文本框数据删除按钮代码及注册点击事件，用于快速清空文本框数据）*******************/
            g.unselect = $('<div class="l-trigger l-trigger-cancel l-text-trigger-cancel"><div class="l-trigger-icon"></div></div>').hide();
            g.unselect.click(function () {
                g.inputText.val("");
                g.inputText.focus();
                g.trigger('changeValue', [""]);
            });
            /*****************TODO结束*******************/
            /*****************TODO开始（判断是不是文本域，如果是 关闭按钮缩进10像素）*******************/
            $("body").delegate("textarea.l-text-field","mouseover",function(){
                var tares=$(this).closest("div");
                if(!tares.hasClass("uicyc-tar")){
                    tares.addClass("uicyc-tar");
                }
            });
            /*****************TODO结束*******************/
            g.wrapper.hover(function ()
            {
                g.trigger('mouseOver');
                g.wrapper.addClass("l-text-over");
                /*****************TODO开始（鼠标移入文本框时，显示删除框）*******************/
                if(p.isSearch==null) {//不显示搜索按钮时
                    g.unselect.addClass("l-text-trigger-cancel").show();
                }else{//显示搜索按钮时
                    g.unselect.removeClass("l-text-trigger-cancel").show();
                }
                /*****************TODO结束*******************/
            }, function ()
            {
                g.trigger('mouseOut');
                g.wrapper.removeClass("l-text-over");
                /*****************TODO开始（鼠标移出文本框时，隐藏删除框）*******************/
                if(p.isSearch==null) {//不显示搜索按钮
                    g.unselect.addClass("l-text-trigger-cancel").hide();
                }else{//显示搜索按钮时
                    g.unselect.removeClass("l-text-trigger-cancel").hide();
                }
                /*****************TODO结束*******************/
            });

            /*****************TODO开始（当文本框可用时，将删除框的代码添加到文本框中*******************/
            if (!p.disabled) {
                g.wrapper.append(g.unselect);
            }
            /*****************TODO结束*******************/
        },

        //将value转换为有效的数值
        //1,去除无效字符 2,小数点保留
        parseNumber : function(value)
        {
            var g = this, p = this.options;
            var isInt = p.digits ? true : false;
            value = value || g.inputText.val();
            if (value == null || value == "") return "";
            if (!(p.digits || p.number || p.currency)) return value;
            if (typeof (value) != "string") value = (value || "").toString();
            var sign = /^\-/.test(value);
            if (isInt)
            {
                if (value == "0") return value;
                value = value.replace(/\D+|^[0]+/g, '');
            } else
            {
                value = value.replace(/[^0-9.]/g, '');
                if (/^[0]+[1-9]+/.test(value))
                {
                    value = value.replace(/^[0]+/, '');
                }
            }
            if (!isInt && p.precision)
            {
                value = parseFloat(value).toFixed(p.precision);
                if (value == "NaN") return "0";
            }
            if (sign) value = "-" + value;
            return value;
        },

        _setDisabled: function (value)
        {
            var g = this, p = this.options;
            if (value)
            {
                this.inputText.attr("readonly", "readonly");
                this.wrapper.addClass("l-text-disabled");
            }
            else if (!p.readonly)
            {
                this.inputText.removeAttr("readonly");
                this.wrapper.removeClass('l-text-disabled');
            }
        },
        _setWidth: function (value)
        {
            if (value > 20)
            {
                this.wrapper.css({ width: value });
                // TODO [代码片段001][修正验证失败时，输入框显示宽度达不到外层容器宽度问题][yezehua]
                // this.inputText.css({ width: value - 4 });
                this.inputText.css({ width: value - 2 });
                // TODO [代码片段001][修正验证失败时，输入框显示宽度达不到外层容器宽度问题][yezehua] 结束
            }
        },
        _setHeight: function (value)
        {
            if (value > 10)
            {
                this.wrapper.height(value);
                this.inputText.height(value - 2);
            }
        },
        _setValue: function (value)
        {
            if (value != null)
                this.inputText.val(value);
            this.checkNotNull();
        },
        _setLabel: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper)
            {
                g.labelwrapper = g.wrapper.wrap('<div class="l-labeltext"></div>').parent();
                var lable = $('<div class="l-text-label" style="float:left;">' + value + ':&nbsp</div>');
                g.labelwrapper.prepend(lable);
                g.wrapper.css('float', 'left');
                if (!p.labelWidth)
                {
                    p.labelWidth = lable.width();
                }
                else
                {
                    g._setLabelWidth(p.labelWidth);
                }
                lable.height(g.wrapper.height());
                if (p.labelAlign)
                {
                    g._setLabelAlign(p.labelAlign);
                }
                g.labelwrapper.append('<br style="clear:both;" />');
                g.labelwrapper.width(p.labelWidth + p.width + 2);
            }
            else
            {
                g.labelwrapper.find(".l-text-label").html(value + ':&nbsp');
            }
        },
        _setLabelWidth: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper) return;
            g.labelwrapper.find(".l-text-label").width(value);
        },
        _setLabelAlign: function (value)
        {
            var g = this, p = this.options;
            if (!g.labelwrapper) return;
            g.labelwrapper.find(".l-text-label").css('text-align', value);
        },
        updateStyle: function ()
        {
            var g = this, p = this.options;
            if (g.inputText.attr('readonly'))
            {
                g.wrapper.addClass("l-text-readonly");
                p.disabled = true;
            }
            else
            {
                g.wrapper.removeClass("l-text-readonly");
                p.disabled = false;
            }
            if (g.inputText.attr('disabled'))
            {
                g.wrapper.addClass("l-text-disabled");
                p.disabled = true;
            }
            else
            {
                g.wrapper.removeClass("l-text-disabled");
                p.disabled = false;
            }
            if (g.inputText.hasClass("l-text-field-null") && g.inputText.val() != p.nullText)
            {
                g.inputText.removeClass("l-text-field-null");
            }
            g.formatValue();
        },
        setValue: function (value)
        {
            this._setValue(value);
            this.trigger('changeValue', [value]);
        }
    });

    function currencyFormatter(num, precision)
    {
        var cents, sign;
        if (!num) num = 0;
        num = num.toString().replace(/\$|\,/g, '').replace(/[a-zA-Z]+/g, '');
        if (num.indexOf('.') > -1) num = num.replace(/[0]+$/g, '');
        if (isNaN(num))
            num = 0;
        sign = (num == (num = Math.abs(num)));

        if (precision == null)
        {
            num = num.toString();
            cents = num.indexOf('.') != -1 ? num.substr(num.indexOf('.') + 1) : '';
            if (cents)
            {
                num = Math.floor(num * 1);
                num = num.toString();
            }
        }
        else
        {
            precision = parseInt(precision);
            var r = Math.pow(10, precision);
            num = Math.floor(num * r + 0.50000000001);
            cents = num % 100;
            num = Math.floor(num / r).toString();
            while (cents.toString().length < precision)
            {
                cents = "0" + cents;
            }
        }
        for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3) ; i++)
            num = num.substring(0, num.length - (4 * i + 3)) + ',' +
                num.substring(num.length - (4 * i + 3));
        var numStr = "" + (((sign) ? '' : '-') + '' + num);
        if (cents) numStr += ('.' + cents);
        return numStr;
    }

})(jQuery);