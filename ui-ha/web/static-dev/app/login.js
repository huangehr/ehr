/*!
 * Login -v1.0.0 (2015/7/29)
 * 
 * @description: 登录页面模块
 *
 * @author:      yezehua
 * 
 * @copyright:   2015 www.yihu.com
 */

define(function (require, exports, module) {

    /* 加载依赖的模块 */

    var $           = require('base').$,
        context     = require('base').context,
        Util        = require('utils'),
        DModel      = require('dataModel').create(),
        Controller  = require('viewController'),
        jValidation = require('modules/validate/jValidate');
    $.i18n.properties({              //加载资浏览器语言对应的资源文件
        name:'message',    //资源文件名称
        path: context.resourcePath+'/resources/i18n/', //资源文件路径
        mode:'map',                  //用Map的方式使用资源文件中的值
        callback: function() {       //加载成功后设置显示内容
        }
    });

    require('app/login.css');
    require('modules/captcha/idcode');
    require('modules/captcha/idcode.css');


    /* 加载依赖的模块结束 */

    // 暴露给外面的init方法
    exports.init = function () {

        // box-form 在文档加载完成之后再显示，防止在其样式作用前后产生视觉上漂移
        $('.box-form').show();

        // 表单验证码插件的初始化
        $.idcode.setCode({ inputID: 'captcha-code-input', codeTip: '<div>'+$.i18n.prop('lab.canot.see.learly')+'</div><div>'+$.i18n.prop('lab.change.one')+'</div>'});

        // 提交表单的远程服务器地址
        var url =  context.path + '/login/loginValid';

        // 生成表单控制器实例
        var formCtr = new Controller('#login-form',{
            // 存放选择器与本地变量的映射
            elements: {
                '#login-btn': 'loginBtn',
                '#captcha-code-input': 'captcha',
                '#captchaHidden': 'captchaHidden',
                '#user-name': 'uName',
                '#password': 'uPsw'
            },
            // 事件名称、选择器和回调函数的映射
            events: {
                'click #login-btn': 'submit',
                'keydown #user-name': 'submit',
                'keydown #password': 'submit',
                'keydown #captcha-code-input': 'submit'
            },
            // 存放回调函数
            handlers:{
                'submit': function(e){

                    if(Util.isStrEquals('click',e.type) || (Util.isStrEquals('keydown',e.type) && e.which==13)) {
                        var validator = new jValidation.Validation(formCtr.el,{immediate:true,onSubmit:false});
                        if(!validator.validate() ) {
                            $.idcode.setCode();
                            formCtr.captchaHidden.val($.idcode.getCode());
                            return;
                        }
                        formCtr.el.submit();
                    }
                }
            }
        });

        formCtr.captchaHidden.val($.idcode.getCode());

    };



});