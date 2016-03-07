<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>


<script src="${staticRoot}/common/base.js"></script>
<script src="${staticRoot}/module/cookie.js"></script>
<script src="${staticRoot}/module/util.js"></script>
<script src="${staticRoot}/module/juicer.js"></script>
<script src="${staticRoot}/module/pubsub.js"></script>
<script src="${staticRoot}/module/ajax.js"></script>
<script src="${staticRoot}/module/baseObject.js"></script>
<script src="${staticRoot}/module/dataModel.js"></script>
<script src="${staticRoot}/module/pinyin.js"></script>
<script src="${staticRoot}/lib/plugin/formEx/attrscan.js"></script>
<script src="${staticRoot}/lib/plugin/formEx/readonly.js"></script>
<script src="${staticRoot}/lib/bootstrap/js/bootstrap.js"></script>
<script src="${staticRoot}/lib/ligerui/core/base.js"></script>

<script src="${staticRoot}/lib/plugin/tips/tips.js"></script>
<script src="${staticRoot}/lib/plugin/validate/jValidate.js"></script>
<script src="${staticRoot}/lib/plugin/validation/jquery.validate.min.js"></script>
<script src="${staticRoot}/lib/plugin/validation/jquery.metadata.js"></script>
<script src="${staticRoot}/lib/plugin/validation/messages_cn.js"></script>

<script src="${staticRoot}/lib/ligerui/plugins/ligerLayout.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerDrag.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerResizable.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerDialog.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerGrid.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerTree.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerButton.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerCheckBox.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerCheckBoxList.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerComboBox.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerListBox.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerTextBox.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerDateEditor.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerForm.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/customTree.js"></script>
<script src="${staticRoot}/lib/ligerui/plugins/ligerRadio.js"></script>

<script src="${staticRoot}/lib/plugin/notice/topNotice.js"></script>
<script src="${staticRoot}/lib/plugin/combo/addressDropdown.js"></script>

<script src="${staticRoot}/lib/plugin/scrollbar/jquery.mousewheel.min.js"></script>
<script src="${staticRoot}/lib/plugin/scrollbar/jquery.mCustomScrollbar.js"></script>
<script src="${staticRoot}/lib/plugin/upload/webuploader.js"></script>
<script src="${staticRoot}/lib/plugin/upload/upload.js"></script>

<script src="${staticRoot}/lib/ligerui/custom/ligerGridEx.js"></script>

<script src="${staticRoot}/browser/lib/echarts/echarts-all.js"></script>
<script src="${staticRoot}/lib/plugin/easing/jquery.easing.1.3.js"></script>
<script src="${staticRoot}/lib/plugin/sly/sly.js"></script>
<script src="${staticRoot}/browser/lib/timepager/timepager.js"></script>
<script src="${staticRoot}/browser/lib/timeline/timeline.js"></script>
<%--<script src="${staticRoot}/browser/lib/grid/tableExport.js"></script>--%>
<script src="${staticRoot}/browser/lib/grid/js/bootstrap-table.js"></script>
<%--<script src="${staticRoot}/browser/lib/grid/bootstrap-table-export.js"></script>--%>
<script src='${staticRoot}/browser/lib/fullcalendar/lib/moment.min.js'></script>
<script src='${staticRoot}/browser/lib/fullcalendar/fullcalendar.min.js'></script>
<script src='${staticRoot}/browser/lib/mspagination/ms-pagination.js'></script>
<script src="${staticRoot}/browser/lib/floatMenu/float-menu-tree.js"></script>
<script>
    (function ($, win) {
        $(function () {
            var $title = $('title'),
                    $headTitleTarget = $('[data-head-title="true"]');
            document.title = $headTitleTarget.text();
        });
    })(jQuery, window);
</script>