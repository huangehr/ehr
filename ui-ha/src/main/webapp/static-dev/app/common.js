/**
 * Created by wq on 2015/8/11.
 */
define(function (require, exports, module) {
    
    var $ = require('base').$;

    require('app/common.css');

    function msgBox(msg) {

        $("#common_message",window.top.document).html(msg);
        $("#common_message",window.top.document).fadeIn();
        setTimeout(function () {
            $("#common_message",window.top.document).hide();
        }, 3000);
    }
    function init() {
        //导航位置显示信息
        $(function() {

            $(".level_one").click(function(){
                $(this).next("div").slideToggle();
                $(this).children("div").toggleClass("div2ItemToggle");//点击切换图标
            });

            $("li").click(function() {//点击切换三角形
                $(this).children('span').toggleClass('itemToggle');
            });

            $(".level_one").click(function() {
                var txt = $(this).text();
                $("#list").html('<li>' + txt + '</li>');
            });

            $(".level_three ul li").click(function() {
                //将数字改成阿拉伯数子，（在保证菜单一级列表不超过10个的情况下），这样就不用加if条件判断
                var liTxt = $(this).text();
                if ($("#list").children("li").length<2) {
                    $("#list").append('<li></li>');
                    $("#list li").eq(1).addClass("liItem");
                };
                var level_three_id = $(this).parent().parent().attr("id");
                var leveltwo_id = "leveltwo_"+level_three_id.substr(12,1);
                var txt = $("#"+leveltwo_id).text();
                $("#list li").eq(0).html('<li>' + txt + '</li>');
                $(".liItem").html('<li>>' + liTxt + '</li>');
            });

        });
    }

    exports.init = init;
    exports.msgBox = msgBox;
});
