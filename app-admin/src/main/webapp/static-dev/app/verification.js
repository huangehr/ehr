/*!
 * verification -v1.0.0 (2015/7/29)
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
        Util        = require('utils'),
        DModel      = require('dataModel').create(),
        ViewController  = require('viewController');

    require('app/verification.css');
    require('modules/validate/validate');

    /* 加载依赖的模块结束 */

    $(function() {

        $('.m-verify').show();

        var step = $('#step');
        var form1 = new ViewController('#step1-form',{
            elements: {
                '.user-valid .currentItem': 'validtype',
                '.message': 'msg',
                '#code-btn': 'codeBtn'
            },
            events: {
                'click .user-valid .dropdown-menu li': 'selecteItem',
                'click #code-btn': 'getCode',
                'click .confirm-btn': 'confirm'
            },
            handlers: {
                selecteItem: function(e){

                    form1.validtype.text($('a',e.currentTarget).text());
                },
                getCode: function () {

                    form1.codeBtn.addClass('disabled');

                    var model = DModel.init();
                    model.fetchRemote('http://localhost:8080/health/HelloWorldController/searchDict',{
                        data: {
                            key: 'phone', // 验证类型
                            value: '15851234567' // 值
                        },
                        success: function (data) {
                            if (Util.isStrEquals(data.flag,"1")) {
                                // .若验证码发送成功，执行如下处理

                                form1.msg.empty();
                                form1.msg.append(
                                                    $('<p>'+$.i18n.prop('lab.check.verification.code1')+'</p>')
                                                    .append('<p>'+$.i18n.prop('lab.check.verification.code2')+'<span id="wait-timer">59</span>'+$.i18n.prop('lab.check.verification.code3')+'</p>')
                                                    .fadeIn()
                                                );
                                var t = 5;
                                var inter = setInterval(function () {
                                    t--;
                                    if(t==0) {
                                        clearInterval(inter);
                                        inter = null;

                                        form1.codeBtn.removeClass('disabled');
                                        form1.msg.empty();
                                    }
                                    $('#wait-timer').text(t);
                                },1000)
                            }
                            else {
                                form1.codeBtn.removeClass('disabled');
                            }

                        }
                    })
                },
                confirm: function (e) {

                    var model = DModel.init();
                    model.fetchRemote('http://localhost:8080/health/HelloWorldController/searchDict',{
                        data: $(form1).serialize(), // 传给服务器的表单数据(手机号|邮箱，验证码)
                        success: function (data) {


                            // 1.根据返回的data数据，判断用户验证是否成功

                            // 2.若用户验证成功，执行如下处理
                            step.removeClass('f-icon-step1').addClass('f-icon-step2');
                            form1.el.hide();
                            form2.el.show();

                            // 3.如果用户验证失败，执行如下处理

                        }
                    })

                }
            }
        });

        var form2 = new ViewController('#step2-form',{
            elements: {
                '.message': 'msg',
                '#code-btn': 'codeBtn'
            },
            events: {

                'click .confirm-btn': 'confirm'
            },
            handlers: {

                confirm: function (e) {

                    var model = DModel.init();
                    model.fetchRemote('http://localhost:8080/health/HelloWorldController/searchDict',{
                        data: $(form2).serialize(), // 传给服务器的表单数据(手机号|邮箱，验证码)
                        success: function (data) {

                            var pwd1 = $("#Create_pwd").val().trim();
                            var pwd2 = $("#Create_newPwd").val().trim();

                            if (pwd1.length > 1) {

                                if (pwd1 == pwd2) {
                                    $("#s_msg").hide();
                                }
                                else {
                                    $("#s_msg").show();
                                }
                            }
                            // 1.根据返回的data数据，判断用户验证是否成功

                            // 2.若用户验证成功，执行如下处理
                            step.removeClass('f-icon-step2').addClass('f-icon-step3');
                            form2.el.hide();
                            form3.el.show();

                            // 3.如果用户验证失败，执行如下处理

                        }
                    })

                    step.removeClass('f-icon-step2').addClass('f-icon-step3');
                }
            }
        });


        var form3 = new ViewController('#step3-form',{
            elements: {
               // '.message': 'msg',
                '#code-btn': 'codeBtn'
            },
            events: {

                'click .confirm-btn': 'confirm'
            },
            handlers: {
                confirm: function (e) {

                }
            }
        });

})
})