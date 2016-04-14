<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="utf-8"%>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script src="${staticRoot}/lib/jquery/jquery-1.9.1.js"></script>
<script>
    $.extend({
        Context: {
            PATH: '${contextRoot}',
            STATIC_PATH: '${staticRoot}'
        }
    })
</script>
<%--<script src="${staticRoot}/common/base.js"></script>--%>
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
<script src="${staticRoot}/lib/ligerui/custom/customCombo.js"></script>
<script src="${staticRoot}/lib/ligerui/custom/cyc-menu.js"></script>

<script>
    (function ($, win) {
        $(function () {
            var $title = $('title'),
                    $headTitleTarget = $('[data-head-title="true"]');
            document.title = $headTitleTarget.text();
        });
    })(jQuery, window);
</script>