/**
 * Created by AndyCai on 2015/10/14.
 */
define(function (require, exports, module) {

    //var $ = require('base').$;

     require('app/frame.css');

    function msgBox(msg) {

        $("#common_message",window.top.document).html(msg);
        $("#common_message",window.top.document).fadeIn();
        setTimeout(function () {
            $("#common_message",window.top.document).hide();
        }, 3000);
    }

    function windowHeight() {
        var de = document.documentElement;
        return self.innerHeight || (de && de.clientHeight) || document.body.clientHeight;
    }

    function windowWidth() {
        var de = document.documentElement;
        return self.innerWidth || (de && de.clientWidth) || document.body.clientWidth;
    }

    function init() {
        //自适应设置
        $(function () {
            var _height = windowHeight();
            var _width = windowWidth();
            $(".content").css({
                'height':_height-$(".common_end").height()-2
            });
            $(".content_right").css({
                'height':_height-$(".common_end").height()-2
            });

            $(".content_block").css({
                'height':_height-$(".common_end").height()-$('.right').height()-1
            });
            $(".right").css({
                'width':_width-$(".common_navigation").width()-3
            });


        });

        //导航位置显示信息
        $(function() {

            $(".level_one").click(function(){

                $(this).next("div").slideToggle();

                $(this).children("div").toggleClass("div2ItemToggle");//点击切换图标

            });

            $("li").click(function() {//点击切换三角形

                $(this).children('span').toggleClass('itemToggle');

            });

            $("#leveltwo_one,#level_two,#leveltwo_two").click(function() {
                var txt = $(this).text();
                $("#list").html('<li>' + txt + '></li>');

            });

            $("#level_three ul li,#level_three_one ul li,#level_three_two ul li").click(function() {
                var liTxt = $(this).text();
                if ($("#list").children("li").length<2) {
                    $("#list").append('<li></li>');
                    $("#list li").eq(1).addClass("liItem");
                };
                $(".liItem").html('<li>' + liTxt + '</li>')
            });

        });
    }

    exports.init = init;
    exports.msgBox = msgBox;
});
