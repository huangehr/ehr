<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<c:set var="contextRoot" value="<%=request.getContextPath()%>" scope="page" />
<c:set var="devgMode" value="true" scope="page" />

<c:choose>
    <c:when test="${devgMode == 'true'}">
        <c:set var="staticRoot" value="${contextRoot}/static-dev" scope="page" />
    </c:when>
    <c:otherwise>
        <c:set var="staticRoot" value="${contextRoot}/static" scope="page" />
    </c:otherwise>
</c:choose>

<%--定义页面文档类型以及使用的字符集,浏览器会根据此来调用相应的字符集显示页面内容--%>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8">

<%--IE=edge告诉IE使用最新的引擎渲染网页，chrome=1则可以激活Chrome Frame.--%>
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">

<!--[if lt IE 9]>
<%-- 让IE9以下的IE支持HTML5元素 --%>
<script src="static-dev/extra/html5shiv.js"></script>
<![endif]-->

<%-- 引入Seajs框架 --%>
<script type="text/javascript" src="<%=request.getContextPath()%>/static-dev/sea.js"></script>

<script type="text/javascript">

    (function(){

        //var root = '<%=request.getContextPath()%>'; // 项目访问根目录

        seajs.config({

            base: '${staticRoot}',  // 基目录，require()引入模块将相对此目录进行查找

            alias: // 定义别名
            {
                'base': '${staticRoot}/base/base.js',
                'jquery': '${staticRoot}/base/jquery/jquery-1.11.3.js',
                'bootstrap': '${staticRoot}/base/bootstrap/js/bootstrap.js',
                'jqueryui': '${staticRoot}/base/jqueryui/jquery-ui.js',
                'jqueryuiCss': '${staticRoot}/base/jqueryui/jquery-ui.css',
                'bootstrapCss': '${staticRoot}/base/bootstrap/css/bootstrap.css',
                'fontawesomeCss': '${staticRoot}/base/css/font-awesome.css',
                'jqGrid': '${staticRoot}/modules/jqGrid/js/jquery.jqGrid.js',
                'jqGridCN': '${staticRoot}/modules/jqGrid/js/i18n/grid.locale-cn.js',
                'jqGridCss':'${staticRoot}/modules/jqGrid/css/ui.jqgrid.css',
                'datepicker': '${staticRoot}/modules/datepicker/datepicker',
                'datepickerLocal': '${staticRoot}/modules/datepicker/datepicker.zh-CN.js',
                'datepickerCss': '${staticRoot}/modules/datepicker/datepicker.css',

                'datetimepicker': '${staticRoot}/modules/datepicker/datetimepicker.min',
                'datetimepickerLocal': '${staticRoot}/modules/datepicker/datetimepicker.zh-CN.js',
                'datetimepickerCss': '${staticRoot}/modules/datepicker/datetimepicker.min.css',

                'pubsub': '${staticRoot}/modules/pubsub/pubsub.js',
                'aop': '${staticRoot}/modules/aop/aop.js',
                'utils': '${staticRoot}/modules/utils/util.js',
                'baseObject': '${staticRoot}/modules/models/baseObject.js',
                'baseModel': '${staticRoot}/modules/models/baseModel.js',
                'dataModel': '${staticRoot}/modules/models/dataModel.js',
                'ajax': '${staticRoot}/modules/ajax/ajax.js',
                'controller': '${staticRoot}/modules/controllers/controller.js',
                'viewController': '${staticRoot}/modules/controllers/viewController.js',
                'component': '${staticRoot}/modules/components/component',
                'juicer': '${staticRoot}/modules/juicer/juicer',
                'resetCss': '${staticRoot}/base/css/reset.css',
                'functionCss': '${staticRoot}/base/css/function.css',
                'unitCss': '${staticRoot}/base/css/unit.css',
                'coverCss': '${staticRoot}/base/css/cover.css',
                'jValidate': '${staticRoot}/modules/validate/jValidate.js',
                'i18n': '${staticRoot}/base/i18n/jquery.i18n.properties-1.0.9.js'
            },

            preload: ["jquery",'bootstrap','jqueryui','resetCss','fontawesomeCss','jqueryuiCss','bootstrapCss','datetimepickerCss','functionCss','unitCss','jqGridCss','coverCss','jValidate','i18n'],

            charset:'utf-8',

            timeout: 20000,

            debug:false

        });

        seajs.use('base',function(base){
            base.context.path =  '<%=request.getContextPath()%>'; // 项目访问根目录;
            base.context.resourcePath = '${staticRoot}';
        });

        seajs.use('juicer',function(juicer){

            // 自定义模板语法边界符,借此解决 Juicer 模板语法同某些后端语言模板语法冲突的情况
            juicer.set({
                'tag::operationOpen': '{@',
                'tag::operationClose': '}',
                'tag::interpolateOpen': '{$',
                'tag::interpolateClose': '}',
                'tag::noneencodeOpen': '{$$',
                'tag::noneencodeClose': '}',
                'tag::commentOpen': '{#',
                'tag::commentClose': '}'
            });
        });

    })();

</script>
