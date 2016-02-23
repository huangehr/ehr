<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0">
    <%@include file="/WEB-INF/ehr/commons/jsp/commonHead.jsp"%>
    <style>
        body { font: 12px/1.14 \5b8b\4f53,Sim sun,"宋体"; color: #323232;}
        table tr td { font-size: 12px; height: 34px; }
        .table > thead > tr > th,
        .table > tbody > tr > th,
        .table > tfoot > tr > th,
        .table > thead > tr > td,
        .table > tbody > tr > td,
        .table > tfoot > tr > td { padding: 8px 5px; }
        .u-view-container { margin: 10px; }
        .u-report-title { height: 50px; line-height: 50px; font-size: 24px; font-weight: bold; text-align: center; }
        .u-info-header { padding: 30px 0; border-top: 1px dotted #323232;}
        .u-info-header .u-info-item { display: inline-block; height: 25px; line-height: 25px; margin-right: 10px;}
        .u-info-header span.u-info-field { padding-bottom: 2px; border-bottom: 1px solid #000; }

        .ms-controller,.ms-important,[ms-controller],[ms-important]{
            visibility: hidden;
        }
        div[avalonctrl="viewController"] {
            width: 800px;
            margin: 0 auto!important;
            padding-top: 30px !important;
            padding-right: 5px;
        }

        body { font: 12px/1.14 \5b8b\4f53,Sim sun,"宋体"; color: #323232;}
        .f-pa { position: absolute; }
        .f-pr { position: relative; }
        .f-pos-t0 { top: 0; }
        .f-pos-l0 { left: 0; }

        .f-ib { display: inline-block; }
        .f-p0 { padding: 0 !important; }
        .f-p5 { padding: 5px; }
        .f-pl15 { padding-left: 15px; }
        .f-pl30 { padding-left: 30px; }
        .f-pl35 { padding-left: 35px; }
        .f-pl40 { padding-left: 40px; }
        .f-pl50 { padding-left: 50px; }
        .f-pl55 { padding-left: 55px; }
        .f-pl60 { padding-left: 60px; }
        .f-pl70 { padding-left: 70px; }
        .f-pl80 { padding-left: 80px; }
        .f-pl85 { padding-left: 85px; }
        .f-pl95 { padding-left: 95px; }
        .f-pl120 { padding-left: 110px; }


        .f-pr10 { padding-right: 10px; }
        .f-pr15 { padding-right: 30px; }
        .f-pr30 { padding-right: 30px; }
        .f-pt8 { padding-top: 8px;}
        .f-pt10 { padding-top: 10px;}
        .f-pt15 { padding-top: 15px;}
        .f-pt18 { padding-top: 18px;}
        .f-pt20 { padding-top: 20px;}
        .f-pb2 { padding-bottom: 2px; }

        .f-mb0 { margin-bottom: 0; }
        .f-m5 { margin: 5px; }
        .f-mt10 { margin-top: 10px;}
        .f-mt20{ margin-top:20px;}
        .f-mlr { margin-left: 30px; margin-right:30px;}

        .f-wp16 { width: 16.6666%}
        .f-wp20 { width: 20%}
        .f-wp25 { width: 25%}
        .f-wp30 { width: 30%}
        .f-wp33 { width: 33.3333%}
        .f-wp40 { width: 40%}
        .f-wp50 { width: 50%}
        .f-wp60 { width: 60%}
        .f-wp70 { width: 70%}
        .f-wp80 { width: 80%}
        .f-wp90 { width: 90%}
        .f-wp100 { width: 100%}

        .f-w10 { width: 10px; }
        .f-w50 { width: 50px; }
        .f-w70 { width: 70px; }
        .f-w80 { width: 80px; }
        .f-w100 { width: 100px; }
        .f-w150 { width: 150px; }
        .f-w170 { width: 170px; }
        .f-w200 { width: 200px; }
        .f-w290 { width: 290px; }
        .f-w300 {width: 300px; }

        .f-min-wp70 { min-width: 70%; }
        .f-min-wp60 { min-width: 60%; }
        .f-min-w50 { min-width: 50px; }
        .f-min-w80 { min-width: 80px; }
        .f-min-w100 { min-width: 100px; }
        .f-min-w200 { min-width: 200px; }
        .f-min-w300 { min-width: 300px; }
        .f-min-w400 { min-width: 400px; }
        .f-min-w500 { min-width: 500px; }
        .f-max-w80 { max-width: 80px; }
        .f-h50 { height: 50px; }
        .f-lh25 { line-height: 25px !important; }
        .f-lh50 { line-height: 50px; }

        .f-fs14 { font-size: 14px; }
        .f-fs16 { font-size: 16px; }
        .f-fs18 { font-size: 18px; }
        .f-fs20 { font-size: 20px; }
        .f-fs25 { font-size: 25px; }
        .f-fwb { font-weight: bold; }
        .f-wwb { white-space:normal; word-wrap:break-word; word-break:break-all; }
        .f-ti1 { text-indent:1em; }
        .f-ti2 { text-indent:2em; }
        .f-tal { text-align: left !important; }
        .f-tar { text-align: right !important; }
        .f-tac { text-align: center !important; }
        .f-vac { vertical-align: middle !important; }

        .f-bt0 { border-top: 0 !important; }
        .f-bb1 { border-bottom: 1px solid #232323; }
        .f-bb2 { border-bottom: 2px solid #232323; }
        .f-bd2-0 { border: 2px solid #FFF; }
        .f-bdc0 { border-color: #FFF; }
        .f-bdc1 { border-color: #F5F5F5; }
        .f-bdc2 { border-color: #FF0000 !important; }

        .table-bordered > tbody > tr > td { border-color: #232323; }
        .table-bdtb> tbody > tr > td { border-bottom: 1px solid #232323;border-top:1px solid #232323 }
        .table-bd0> tbody > tr > td { border: 1px solid #232323;}

        .s-c0 { color: #FFF; }
        .s-c1 { color: #F5F5F5; }
        .s-c2 { color: #FF0000; }
        .s-bc0 { background-color: #FFF; }
        .s-bc1 { background-color: #F5F5F5; }

        .f-toe{overflow:hidden;word-wrap:normal;white-space:nowrap;text-overflow:ellipsis;}
        .clear-space { font-size:0; [;font-size:12px;]; *font-size:0; font-family:arial; [;letter-spacing:-3px;]; *letter-spacing:normal; *word-spacing:-1px; }
        .clear-space>* { display:inline-block; *display:inline; *zoom:1; font-size:12px; letter-spacing:normal; word-spacing:normal; }

        .field { min-height: 16px; }
    </style>
    <script type="text/javascript" src="${contextRoot}/static-dev/base/avalon/avalon.js"></script>
    <script src="${staticRoot}/lib/jquery/jquery-1.9.1.js"></script>
</head>
<body>
<div id="div_record_template_data" class="f-dn">
    ${data}
</div>

<div ms-controller="viewController">
    ${html}
</div>

<script src="${staticRoot}/module/util.js"></script>
<script src="${staticRoot}/module/ajax.js"></script>
<script src="${staticRoot}/module/baseObject.js"></script>
<script src="${staticRoot}/module/dataModel.js"></script>
<%--<%@include file="/WEB-INF/ehr/commons/jsp/commonFoot.jsp" %>--%>
<script>
    var templateHtml = $('[ms-controller="viewController"]').html();
    (function ($,win) {
        $(function () {
            var $recordTemplateData = $('#div_record_template_data');
            var json = $recordTemplateData.text();
            if(window.console) {
                console.log(json);
            }

            if(json) {
                try{
                var data = eval('('+json+')')
                } catch(e) {
                    data = {};
                }

                avalon.define({
                    $id: 'viewController',
                    data: data
                });

                avalon.scan();
            }

            win.top.loadTemplate = function(origin, param) {
                $('[avalonctrl="viewController"]').remove();
                delete avalon.vmodels.viewController;
                var dataModel = $.DataModel.init();
                dataModel.fetchRemote('${contextRoot}/browser/personal-profile/data', {
                    data:{
                        cda_version: param.cdaVersion,
                        data_set_list:JSON.stringify(param.dataSets||[]),
                        origin_data: origin
                    },
                    success: function (data) {
                        var $recordTemplateData = $('#div_record_template_data');
                        $recordTemplateData.text(JSON.stringify(data));
                        $('<div ms-controller="viewController">').html(templateHtml).appendTo(document.body);
                        avalon.define({
                            $id: 'viewController',
                            data: JSON.parse($recordTemplateData.text())
                        });

                        avalon.scan();
                }});

            };
//            $('#btn_change_mode').click(function () {
//                $('[avalonctrl="viewController"]').remove();
//                delete avalon.vmodels.viewController;
//                $('<div ms-controller="viewController">').html(templateHtml).appendTo(document.body);
//                require(["avalon"], function(avalon) {
//
//                    avalon.define({
//                        $id: 'viewController',
//                        data: data
//                    });
//
//                    avalon.scan();
//                });
//            });
        })
    })(jQuery, window);

</script>
</body>
</html>