<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@include file="/WEB-INF/ehr/commons/jsp/commonInclude.jsp" %>
<script>
  (function ($, win) {
    $(function () {
      /* ************************** 变量定义 ******************************** */

      // 通用工具类库
      var Util = $.Util;

      var cardInfo = null;

      var addCardInfoGrid = null;
      /* ************************** 变量定义结束 ******************************** */

      /* *************************** 函数定义 ******************************* */
      function pageInit() {
        cardInfo.initForm();
        cardInfo.cardForm();
      }
      /* *************************** 函数定义结束 ******************************* */

      /* *************************** 模块初始化 ***************************** */
      cardInfo = {
        $from:$("#div_addCard_info_form"),
        $selectCardType:$("#inp_select_cardType"),
        $element: $(".m-retrieve-area"),
        $searchCardNo: $("#inp_search"),

        initForm: function () {
          this.$selectCardType.ligerComboBox(
                  {
                    url: '${contextRoot}/dict/searchDictEntryList',
                    valueField: 'code',
                    textField: 'value',
                    dataParmName: 'detailModelList',
                    urlParms: {
                      dictId: 10
                    },
                    width:120,
                    autocomplete: true,
                    onSelected: function (v, t) {
                      cardInfo.searchNewCard(cardInfo.$searchCardNo.val(),v);
                    }
                  });

          this.$element.show();
          this.$searchCardNo.ligerTextBox({
            width: 240, isSearch: true, search: function () {
              cardInfo.searchNewCard(cardInfo.$searchCardNo.val(),cardInfo.$selectCardType.ligerComboBox().getValue());
            }
          });

          this.bindEvents();
          //this.$form.attrScan();
        },
        cardForm:function(){
          addCardInfoGrid = $("#div_addCard_info").ligerGrid($.LigerGridEx.config({
              url: '${contextRoot}/card/searchNewCard',
              parms: {
                idCardNo: '${idCardNo}',
                searchNm: '',
                searchType:''
              },
              columns: [
                { name: 'id',hide: true},
                { name: 'cardType',hide: true},
                {display: '类型', name: 'typeName', width: '10%'},
                {display: '卡号', name: 'number', width: '30%'},
                {display: '发行机构', name: 'releaseOrgName', width: '20%'},
                {display: '就诊时间', name: 'createDate', width: '20%'},
                {display: '状态', name: 'statusName', width: '10%'},
                {
                  display: '操作', name: 'operator', width: '10%', render: function (row) {
                  var html = '<a href="javascript:void(0)" onclick="javascript:' + Util.format("$.publish('{0}',['{1}','{2}'])", "card:cardInfoModifyDialog:open",row.id, row.cardType) + '">关联</a>  ';
                  return html;
                }
                }
              ],
              inWindow: false,
            height:450
            }));
          addCardInfoGrid.adjustToWidth();
        },
        searchNewCard: function (searchNm,searchType) {
          addCardInfoGrid.setOptions({parms: { idCardNo: '${idCardNo}',searchNm: searchNm, searchType:searchType},newPage:1});
          addCardInfoGrid.loadData(true);
        },

        bindEvents: function () {
          //绑定卡信息
          $.subscribe('card:cardInfoModifyDialog:open',function(event,id,cardType){
            $.ligerDialog.confirm('确认关联该卡信息？<br>如果是请点击确认按钮，否则请点击取消。', function (yes) {
              if (yes) {
                var dataModel = $.DataModel.init();
                dataModel.updateRemote('${contextRoot}/card/attachCard', {
                  data: {idCardNo:'${idCardNo}',id: id,cardType:cardType},
                  success: function (data) {
                    if (data.successFlg) {
                      $.Notice.success('关联成功!',function(){
                        cardInfo.searchNewCard();
                      });
                    } else {
                      $.Notice.error('关联失败');
                    }
                  }
                });
              }
            })
          });

        }

      };
      /* *************************** 模块初始化结束 ***************************** */
      /* *************************** 页面初始化 ***************************** */
      pageInit();
      /* *************************** 页面初始化结束 ***************************** */

    });
  })(jQuery,window);
  </script>