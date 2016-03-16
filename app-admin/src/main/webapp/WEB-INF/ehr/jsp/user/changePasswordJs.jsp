<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
  (function ($, win) {
    $(function () {
      /* ************************** 变量定义 ******************************** */
      // 通用工具类库
      var Util = $.Util;
      // 页面表格条件部模块
      var retrieve = null;
      // 页面主模块，对应于用户信息表区域
      var master = null;

      /* *************************** 函数定义 ******************************* */
      /**
       * 页面初始化。
       */
      function pageInit() {
        retrieve.init();
        master.init();
      }

      /* *************************** 模块初始化 ***************************** */
      master = {

        reloadGrid: function () {
          <%--var values = retrieve.$element.Fields.getValues();--%>
          <%--reloadGrid.call(this, '${contextRoot}/user/searchUsers', values);--%>
        },

        bindEvents: function () {
          var self = this;

        }
      };

      /* ************************* 模块初始化结束 ************************** */


      /* *************************** 页面初始化 **************************** */

      pageInit();

      /* ************************* 页面初始化结束 ************************** */
    });
  })(jQuery,window);
</script>