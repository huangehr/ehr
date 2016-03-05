<%--
  Created by IntelliJ IDEA.
  User: wq
  Date: 2015/8/11
  Time: 15:07
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html>
<head>

    <title>common</title>
    <%--导入共通头部文件 --%>
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHeader.jsp" %>
    <%--引用js和css--%>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static-dev/base/jquery/jquery-1.9.0.min.js"></script>

</head>
<body>


<div class="common_big" >
    <!--头部图片-->
    <div class="common_end" style="position: absolute;top:0;z-index: 200;">
        <table class="common_table_logmessage">
            <tr>
                <td>
                    <spring:message code="lbl.welcome.login"/>：${currentUser.realName}
                </td>
            </tr>
            <tr>
                <td>
                    <spring:message code="lbl.last.login"/>：${lastLoginTime}&nbsp;
                </td>
                <td>
                    ${address}&nbsp;|&nbsp;
                </td>
                <td>
                    <a href="${contextRoot}/login/login" style="color: #fefafa;"><spring:message code="btn.exit"/></a>
                </td>
            </tr>
        </table>
    </div>
    <div class="common_big ">
        <!--左边导航栏-->
        <div class="common_navigation" style="  position: absolute;top: 0;padding-top: 88px;width: 200px;z-index: 100;">

            <div class="left_top">
                <div class="dhlcd"></div>
                <spring:message code="title.navigation.menu"/>
            </div>

            <div class="level_one" id="leveltwo_0">
                <div class="div2item"></div>
                <spring:message code="title.register.manage.center"/>
            </div>

            <div class="level_three" id="level_three_0">
                <ul>
                    <li>
                        <span class="item item1"></span>
                        <a href="${contextRoot}/user/initial" target="background">
                            <spring:message code="title.user.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item2"></span>
                        <a href="${contextRoot}/organization/initial" target="background">
                            <spring:message code="title.org.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item3"></span>
                        <a href="${contextRoot}/patient/initial" target="background">
                            <spring:message code="title.patient.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item4"></span>
                        <a href="${contextRoot}/app/initial" target="background">
                            <spring:message code="title.app.manage"/>
                        </a>
                    </li>
                </ul>
            </div>

            <div class="level_one" id="leveltwo_1">
                <div class="div2item"></div>
                <spring:message code="title.data.manage.center"/>
            </div>

            <div class="level_three" id="level_three_1">

                <ul>
                    <li>
                        <span class="item item1"></span>
                        <a href="${contextRoot}/standardsource/initial" target="background"><spring:message code="title.std.source"/></a>
                    </li>
                    <li>
                        <span class="item item2"></span>
                        <a href="${contextRoot}/cda/initial" target="background">
                            <spring:message code="title.CDA.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item2"></span>
                        <a href="${contextRoot}/cda/initial" target="background">
                            <spring:message code="title.CDA.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item3"></span>
                        <a href="${contextRoot}/std/dataset/initial" target="background"><spring:message code="title.dataSet"/></a>
                    </li>
                    <li>
                        <span class="item item4"></span>
                        <a href="${contextRoot}/cdadict/initial" target="background">
                            <spring:message code="title.dict.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item5"></span>
                        <a href="${contextRoot}/orgdataset/initial" target="background">
                            <spring:message code="title.org.std.collection.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item6"></span>
                        <a href="${contextRoot}/adapter/initial" target="background">
                            <spring:message code="title.adapter.manager"/>
                        </a>
                    </li>
                </ul>
            </div>

            <div class="level_one" id="leveltwo_2">
                <div class="div2item"></div>
                <spring:message code="title.health.archive.browser"/>
            </div>

            <div class="level_three" id="level_three_2">
                <ul>
                    <li>
                        <span class="item item1"></span>
                        <a href="${contextRoot}/template/initial" target="background"><spring:message code="title.template.manage"/></a>
                    </li>
                </ul>
            </div>

            <div class="level_one" id="leveltwo_3">
                <div class="div2item"></div>
                <spring:message code="title.setting.manage"/>
            </div>

            <div class="level_three" id="level_three_3">
                <ul>
                    <li>
                        <span class="item item1"></span>
                        <a href="${contextRoot}/dict/initial" target="background">
                            <spring:message code="title.sysDict.manage"/>
                        </a>
                    </li>
                    <li>
                        <span class="item item2"></span>
                        <a href="${contextRoot}/monitor/initial" target="background">
                            <spring:message code="title.monitor.manage"/>
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <div style="  position: absolute;top: 88px;width: 100%;height: 40px;padding-left: 200px;  z-index: 90;background-color: #fff;">
            <table class="common_table">
                <tr>
                    <td width="10px"></td>
                    <td>
                        <spring:message code="lbl.location"/>
                    </td>
                    <td>
                        <ul class="list" id="list"></ul>
                    </td>
                </tr>
            </table>

            <div class="common_message" id="common_message">

            </div>
        </div>
        <div id="notice-container" class="f-dn">
            <div class="oni-notice-flow">
                <div class="u-notice success" style="height: 9px;">
                    <div class="msgContent f-dn">
                        <div class="noty_bar">
                            <div class="noty_message f-tac f-pr" style="padding: 8px 10px 9px; width: auto;">
                                <span class="noty_text"></span>
                            </div>
                        </div>
                    </div>
                    <div class="messageControlBar"></div>
                </div>
            </div>
        </div>
        <%--右边iframe部分--%>
        <div class="f-ww f-hh" style="position: absolute;top: 0;  padding-left: 205px;padding-top: 130px;  padding-right: 5px;
  padding-bottom: 5px;">
            <iframe class="f-ww f-hh" name="background" style="border: 1px solid #CCCCCC;">
            </iframe>
        </div>
    </div>
</div>
<script>
    seajs.use(['app/common'], function (common) {
        common.init();
    });
    var $container = $('#notice-container'),
            $msgContent = $('.msgContent', $container),
            $msgControlBar = $('.messageControlBar', $container);
    function showTopNoticeBar(args) {
        if(!args || !args.type || !args.msg) return;

        if(args.type=='success') {
            $('.u-notice',$container).removeClass('error').addClass('success');
        } else if(args.type=='error'){
            $('.u-notice',$container).removeClass('success').addClass('error');
        }

        $('.noty_text', $msgContent).html(args.msg);
        $msgContent.show();
        $container.show();

        setTimeout(function () {
            $msgContent.hide();
        },3000);
    }
    $msgControlBar.on('click',function() {
        $msgContent.show();
        setTimeout(function () {
            $msgContent.hide();
        },3000);
    });
</script>
</body>
</html>