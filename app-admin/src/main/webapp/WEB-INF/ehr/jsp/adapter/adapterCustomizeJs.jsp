<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>

<script type="text/javascript">

  (function ($, win) {

    /* ************************** 变量定义 ******************************** */
    var dialog = frameElement.dialog;
    //标准定制变量
    var adapterCustomize = null;
    var fromTree;
    var toTree;

    /* ************************** 变量定义结束 **************************** */

    /* *************************** 函数定义 ******************************* */
    /**
     * 页面初始化
     */
    function pageInit() {
      adapterCustomize.init();
    }

    //来源树复选框点击事件
    function f_onFromTreeCheck(node, checked) {
      var fromTreeOption = {
        fromTree:fromTree,
        toTree:toTree,
        checkNodeItem:node,
        checked:checked,
        toTarget: "#tree_to",
        fromFlag: "std",
        toFlag: "adapter"
      };
      fromTree.f_onFromTreeCheck(fromTreeOption);
    }

    //目标树复选框点击事件
    function f_onToTreeCheck(node, checked) {
      var toTreeOption = {
        fromTree:fromTree,
        toTree:toTree,
        checkNodeItem:node,
        checked:checked,
        fromFlag: "adapter",
        toFlag: "std"
      };
      toTree.f_onToTreeCheck(toTreeOption);
    }
  /* ************************** 函数定义结束 **************************** */

  /* *************************** 模块初始化 ***************************** */
    adapterCustomize = {
      $treeFrom:$("#tree_from"),
      $treeTo:$("#tree_to"),
      $updateCustomizeBtn: $("#btn_save"),
      $cancelBtn: $("#btn_cancel"),
      init: function () {
        this.initForm();
        this.bindEvents();
      },
      initForm: function () {
        var self = this;
        //设置tree的默认属性
        var defaults = {
          idFieldName: 'id',
          parentIDFieldName: 'pid',
          isExpand: 2,
          parentIcon: null,
          childIcon: null,
          adjustToWidth: true
        };
        var allData = JSON.parse('${allData}');
        var stdDataSet = allData.stdDataSet;
        var adapterDataSet = allData.adapterDataSet;
        //var stdDataSet = JSON.parse('${stdDataSet}');
        //var adapterDataSet = JSON.parse('${adapterDataSet}');
//        debugger;
        //来源树对象（ligerTree对象）
        fromTree = self.$treeFrom.ligerTree($.extend({},defaults,{
          data:stdDataSet,
          onCheck: f_onFromTreeCheck
        }));
        //目标树对象（ligerTree对象）
        toTree = self.$treeTo.ligerTree($.extend({},defaults,{
          data: adapterDataSet,
          onCheck: f_onToTreeCheck
        }));
        $(".l-tree").mCustomScrollbar({theme: "minimal-dark"});//设置滚动条
      },
      bindEvents: function () {
        var self = this;
        //修改用户的点击事件
        self.$updateCustomizeBtn.click(function () {
          var notes = toTree.getChecked();
          var note;
          var adapterDataSet = [];
          for (var i = 0; i < notes.length; i++) {
            note = notes[i].data;
            adapterDataSet.push({id:note.id.replace("adapter",""),pid:(note.pid+'').replace("adapter",""),text:note.text});
          }
          var waittingDialog = $.ligerDialog.waitting("正在处理中，请稍候..");
          var dataModel = $.DataModel.init();
          dataModel.updateRemote("${contextRoot}/adapter/adapterDataSet", {
            data: {planId: '${planId}',customizeData:JSON.stringify(adapterDataSet)},
            success: function (data) {
              waittingDialog.close();
              if (data.successFlg) {
                parent.closeCustomizeDialog('修改成功！');
              } else {
                $.Notice.error("修改失败！");
              }
            },
            error : function () {
              waittingDialog.close();
            }
          })
        });
        self.$cancelBtn.click(function () {
          dialog.close();
        });
      }

    };

    /* ************************* 模块初始化结束 ************************** */

    /* *************************** 页面初始化 **************************** */

    pageInit();
    /* ************************* 页面初始化结束 ************************** */

  })(jQuery, window);
</script>